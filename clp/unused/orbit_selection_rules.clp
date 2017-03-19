(defrule ORBIT-SELECTION::assert-all-possible-mission-orbits
    (declare (salience 20))
    ?orig <- (MANIFEST::Mission (Name ?name) (select-orbit yes) (in-orbit ?nil) (num-of-planes# ?np) (num-of-sats-per-plane# ?ns) (mission-architecture ?arch) (orbit-altitude# nil) (orbit-type nil) (orbit-RAAN nil) (orbit-inclination nil)  (instruments $?payload))
    =>
    
    (foreach ?typ (create$ SSO LEO) 
        (foreach ?h (create$ 275 400 600 800 1300)
            (foreach ?i (create$ polar SSO near-polar)
                (foreach ?raan (create$ AM PM DD NA)
                    (if (valid-orbit ?typ ?h ?i ?raan) then (duplicate ?orig (in-orbit (str-cat ?typ "-" ?h "-" ?i "-" ?raan)) (num-of-planes# ?np) (num-of-sats-per-plane# ?ns) (mission-architecture ?arch) (orbit-altitude# ?h) (orbit-type ?typ) (orbit-RAAN ?raan) (orbit-inclination ?i) )))
        )))
    (retract ?orig)
    )

(defrule ORBIT-SELECTION::accept-fixed-mission-orbit
    (declare (salience 20))
    ?orig <- (MANIFEST::Mission (Name ?name) (select-orbit no) (in-orbit ?nil) (orbit-altitude# ?h) (orbit-type ?typ) (orbit-RAAN ?raan) (orbit-inclination ?inc))
    =>
    (modify ?orig (in-orbit (str-cat ?typ "-" ?h "-" ?inc "-" ?raan)))
    )


(defrule ORBIT-SELECTION::assert-instrument-orbits "Asserts instrument orbits from mission orbit"
    (declare (salience 10))
    (MANIFEST::Mission (Name ?name) (select-orbit yes) (in-orbit ?miss) (orbit-altitude# ?h&~nil) (orbit-type ?typ&~nil) (orbit-RAAN ?raan) (orbit-inclination ?i&~nil)  (instruments $?payload))
    
    =>
    
    (foreach ?ins $?payload 
        (bind ?var (str-cat "?*" ?miss "-" ?ins "-" ?typ "-alt" ?h "-inc" ?i "-raan-" ?raan "*"))
         (assert (ORBIT-SELECTION::orbit (orb (str-cat ?name "-" ?miss)) (of-instrument ?ins) (in-mission ?name) (penalty-var ?var)  (is-type ?typ) (h ?h) (i ?i) (raan ?raan) (penalty-var ?var)))
         ;;(printout t "assert orbit for instrument " ?ins crlf )
        )    
    )

(defrule ORBIT-SELECTION::assert-0-penalties-for-all-orbits
    (declare (salience 5))
    ?o <- (ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name) (penalty-var ?var)  (is-type ?typ) (h ?h) (i ?i) (raan ?raan))
    =>
    ;;(printout t ?var crlf)
    ;(eval (str-cat  "(bind " ?var " 0)"))
    (eval (str-cat  "(defglobal " ?var " = 0)"))
    )


(defrule ORBIT-SELECTION::no-lidars-beyond-500km-p20 "Because of power considerations"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name) (penalty-var ?var)  (is-type ?typ) (h ?h&:(> ?h 500)))
    (DATABASE::Instrument (Name ?ins) (Intent ?int))
    (or 
        (test (eq ?int "Laser altimeters")) 
        (test (eq ?int "Elastic lidar")) 
        (test (eq ?int "Differential Absorption Lidars")) 
        (test (eq ?int "Doppler Wind Lidars"))
        )
    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (+ " ?var " 20) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " penalized 20 because there is a lidar beyond 500km." crlf)
    )

(defrule ORBIT-SELECTION::oceanography-missions-want-1000km-b20 "To get low drag penalties and better orbit determination"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type LEO) (h 1300) (i near-polar))
    (DATABASE::Instrument (Name ?ins) (Intent ?int) (Illumination ?illum) (average-power# ?p) (Concept ?c))
    (test (neq (str-index "ocean" ?c) FALSE))
     
    (or 
    	(test (eq ?int "Imaging MW radars (SAR)"))
        ;(test (eq ?int "Radar scatterometer")) 
        (test (eq ?int "Radar altimeter"))  
        )

    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (- " ?var " 20) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " has a benefit of 8 because there is a high energy instrument flying in a DD SSO." crlf)
    )

(defrule ORBIT-SELECTION::tropical-missions-want-equatorial-inclination-b10 "To get better coverage of tropical regions"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name) (penalty-var ?var)  (is-type LEO) (i equat))
    (DATABASE::Instrument (Name ?ins) (Concept ?c))
    (test (neq (str-index "tropic" ?c) FALSE))

    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (- " ?var " 10) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " has a benefit of 8 because there is a high energy instrument flying in a DD SSO." crlf)
    )


(defrule ORBIT-SELECTION::no-radars-beyond-600km-except-oceanography-p7 "Because of power considerations"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type ?typ) (h ?h&:(> ?h 600)))
    (DATABASE::Instrument (Name ?ins) (Intent ?int))
    (or 
        (test (eq ?int "Imaging MW radars (SAR)")) 
        (test (eq ?int "Cloud profile and rain radars")) 
        )
    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (+ " ?var " 7) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " penalized 7 because there is a radar not for oceanography beyond 600km." crlf)
    )

(defrule ORBIT-SELECTION::no-grav-or-magn-instruments-beyond-400km-p9 "Because need the Earth to be close to maximize sensitivity"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type ?typ) (h ?h&:(> ?h 600)))
    (DATABASE::Instrument (Name ?ins) (Intent ?int))
    (or 
        (test (eq ?int "Magnetic field")) 
        (test (eq ?int "Gravity instruments")) 
        )
    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (+ " ?var " 9) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " penalized 9 because there is a gravity/magnetic field instrument beyond 400km." crlf)
    )

(defrule ORBIT-SELECTION::grav-or-magn-instruments-want-to-fly-VLEO-b9 "Because need the Earth to be close to maximize sensitivity"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type ?typ) (h ?h&:(< ?h 400)))
    (DATABASE::Instrument (Name ?ins) (Intent ?int))
    (or 
        (test (eq ?int "Magnetic field")) 
        (test (eq ?int "Gravity instruments")) 
        )
    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (- " ?var " 9) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " penalized 9 because there is a gravity/magnetic field instrument beyond 400km." crlf)
    )

(defrule ORBIT-SELECTION::passive-imagers-want-to-fly-high-b6 "Because of coverage considerations"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type ?typ) (h ?h&:(> ?h 600)))
    (DATABASE::Instrument (Name ?ins) (Intent ?int) (Illumination Passive))
    (test (neq ?int "Magnetic field")) 
    (test (neq ?int "Gravity instruments")) 
    ;(or 
    ;    (test (eq ?int "Imaging multi-spectral radiometers -passive optical-")) 
    ;    (test (eq ?int "Imaging multi-spectral radiometers -passive MW-")) 
    ;    (test (eq ?int "High resolution optical imagers"))
    ;    (test (eq ?int "Atmospheric chemistry: IR nadir spectrometers")) 
    ;    (test (eq ?int "Atmospheric chemistry: IR passive limb sounders"))  
    ;    )
    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (- " ?var " 6) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " benefit +6 because there is a passive imaging instrument flying beyond 600km." crlf)
    )

(defrule ORBIT-SELECTION::passive-imagers-want-to-fly-high-b3 "Because of coverage considerations"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type ?typ) (h ?h&:(> ?h 400)))
    (DATABASE::Instrument (Name ?ins) (Intent ?int))
    (or 
        (test (eq ?int "Imaging multi-spectral radiometers -passive optical-")) 
        (test (eq ?int "Imaging multi-spectral radiometers -passive MW-")) 
        (test (eq ?int "High resolution optical imagers")) 
        (test (eq ?int "Imaging MW radars -SAR-"))
        )
    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (- " ?var " 3) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " benefit +6 because there is a passive imaging instrument flying beyond 600km." crlf)
    )

(defrule ORBIT-SELECTION::nothing-below-400-except-grav-magn-p9 "Because of drag issues"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type ?typ) (h ?h&:(< ?h 400)))
    (DATABASE::Instrument (Name ?ins) (Intent ?int))

	(test (neq ?int "Magnetic field")) 
    (test (neq ?int "Gravity instruments"))

    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (+ " ?var " 9) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " is penalized 9 because there is a nonn gravity/magnetic instrument flying below 400km." crlf)
    )

(defrule ORBIT-SELECTION::passive-VNIR-instruments-want-raan-AMorPM-p7 "Because passive VIS/NIR instruments need a source of light"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type SSO) (raan DD))
    (DATABASE::Instrument (Name ?ins) (Intent ?int) (Spectral-region ?sr) (Illumination Passive))
    (test (neq ?int "Magnetic field")) 
    (test (neq ?int "Gravity instruments")) 
    
    (or 
        (test (eq ?sr opt-UV))
        (test (eq ?sr opt-VIS))
        (test (eq ?sr opt-NIR))
        (test (eq ?sr opt-VNIR))
        (test (eq ?sr opt-SWIR))
        (test (eq ?sr opt-UV+VNIR))
        )

    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (+ " ?var " 7) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " is penalized 7 because there is a passive UV/VIS/NIR instrument flying in a DD SSO." crlf)
    )

(defrule ORBIT-SELECTION::high-energy-instruments-want-raan-DD-b8 "Because DD orbits are more favorable in terms of energy"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type SSO) (raan ?raan&:(eq ?raan DD)))
    (DATABASE::Instrument (Name ?ins) (Intent ?int) (Illumination ?illum) (average-power# ?p))
    (or 
        (test (eq ?illum Active))
		(test (eq ?int "Imaging MW radars (SAR)")) 
        (test (eq ?int "Cloud profile and rain radars"))
        (test (eq ?int "Laser altimeters")) 
        (test (eq ?int "Elastic lidar")) 
        (test (eq ?int "Differential Absorption Lidars")) 
        (test (eq ?int "Doppler Wind Lidars"))
        (test (and (neq ?p nil) (> ?p 1000)))
        )

    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (- " ?var " 8) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " has a benefit of 8 because there is a high energy instrument flying in a DD SSO." crlf)
    )

(defrule ORBIT-SELECTION::no-SSO-for-oceanography-instruments-p4 "Because SSO bring tidal aliasing problems"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type SSO))
    (DATABASE::Instrument (Name ?ins) (Intent ?int) )
    (or 
        (test (eq ?int "Radar scatterometer")) 
        (test (eq ?int "Radar altimeter")) 
        )

    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (+ " ?var " 4) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " is penalized 4 because there is an oceanography instrument flying in an SSO." crlf)
    )

(defrule ORBIT-SELECTION::true-polar-preferred-for-cryospheric-instruments-b9 "Because better coverage of the poles"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type LEO) (i ?i&:(eq ?i polar)))
    (DATABASE::Instrument (Name ?ins) (Concept ?c) )
    (or 
        (test (neq (str-index "cryospher" ?c) FALSE))
        (test (neq (str-index "ice" ?c) FALSE))
        (test (neq (str-index "snow" ?c) FALSE))
        )

    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (- " ?var " 9) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " has a benefit of 4 because of better coverage of the polar regions." crlf)
    )

(defrule ORBIT-SELECTION::vegetation-instruments-want-raan-PM-b3 "Because photosynthetic activity is maximal early PM"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type SSO) (raan ?raan&:(eq ?raan PM)))
    (DATABASE::Instrument (Name ?ins) (Intent ?int) (Concept ?c) (Illumination Passive))
 (or 
        (test (neq (str-index "vegetation" ?c) FALSE))
        (test (neq (str-index "photosynthe" ?c) FALSE))
        )
    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (- " ?var " 3) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " has a benefit of 3 because there is a passive vegetation instrument flying in a SSO PM." crlf)
    )

(defrule ORBIT-SELECTION::passive-imagers-want-SSO-not-POL-p3 "To ensure illumination characteristics that are as similar as possible every pass"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type ?type&:(neq ?type SSO)))
    (DATABASE::Instrument (Name ?ins) (Intent ?int) (Concept ?c))
     (test (neq ?int "Magnetic field")) 
    (test (neq ?int "Gravity instruments")) 
    (test (eq (str-index "radiation budget" ?c) FALSE))
    (test (eq (str-index "radio occultation" ?c) FALSE))
    (test (eq (str-index "cryosph" ?c) FALSE))
    (test (eq (str-index "oceanograph" ?c) FALSE))
    (test (eq (str-index "altimet" ?c) FALSE))
    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (+ " ?var " 3) )"))
    ;(printout t ?c crlf)
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " has a penalty of 3 because there is a non oceanography, not cryospheric instrument flying in a non SSO orbit." crlf)
    )

(defrule ORBIT-SELECTION::chemistry-instruments-want-raan-PM-b3 "Because pollution is maximal in early PM"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name)(penalty-var ?var)  (is-type SSO) (raan ?raan&:(eq ?raan PM)))
    (DATABASE::Instrument (Name ?ins) (Intent ?int) (Concept ?c) (Illumination Passive))
 (or 
        (test (neq (str-index "chemistry" ?c) FALSE))
        (test (neq (str-index "pollut" ?c) FALSE))
        )
    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (- " ?var " 3) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " has a benefit of 3 because there is an atmospheric chemistry instrument flying in a SSO PM." crlf)
    )

(defrule ORBIT-SELECTION::true-polar-preferred-for-radiation-budget-instruments-b8 "Because better coverage of the poles"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name) (penalty-var ?var)  (is-type LEO) (i ?i&:(eq ?i polar)))
    (DATABASE::Instrument (Name ?ins) (Intent "Earth radiation budget radiometers") )


    ;?p <- (ORBIT-SELECTION::penalty (of-orbit ?o) (is ?var)) 
         
    =>
    (eval (str-cat  "(bind " ?var " (- " ?var " 8) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " has a benefit of 4 because of better coverage of the polar regions." crlf)
    )

(defrule ORBIT-SELECTION::multi-purpose-passive-optical-imagers-want-raan-AM-b8 "Because there is less cloudiness in the morning than in the afternoon, AND Better lighting conditions in the morning than in the afternoon"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name) (penalty-var ?var)  (is-type SSO) (raan AM))
    (DATABASE::Instrument (Name ?ins) (Intent "High resolution optical imagers") (Concept ?c) (Illumination Passive))
    (or 
        (test (neq (str-index "land" ?c) FALSE))
        (test (neq (str-index "reflectance" ?c) FALSE))
        )

    =>
    (eval (str-cat  "(bind " ?var " (- " ?var " 8) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " has a benefit of 3 because there is an atmospheric chemistry instrument flying in a SSO PM." crlf)
    )

(defrule ORBIT-SELECTION::other-instruments-want-raan-AM-b2 "Because there is less cloudiness in the morning than in the afternoon, AND Better lighting conditions in the morning than in the afternoon"
    ?o <-(ORBIT-SELECTION::orbit (orb ?miss) (of-instrument ?ins) (in-mission ?name) (penalty-var ?var)  (is-type SSO) (raan AM))
    (DATABASE::Instrument (Name ?ins) (Intent ?int) (Concept ?c) (Illumination Passive))
    (test (eq (str-index "radiation budget" ?c) FALSE))
    (test (eq (str-index "vegetation" ?c) FALSE))
    (test (eq (str-index "radio occultation" ?c) FALSE))
    (test (eq (str-index "chemistry" ?c) FALSE))
    =>
    (eval (str-cat  "(bind " ?var " (- " ?var " 2) )"))
    ;(modify ?p (is ?new-pen))
    ;(printout t "Orbit " ?miss" for instrument " ?ins " has a benefit of 3 because there is an atmospheric chemistry instrument flying in a SSO PM." crlf)
    )

(defrule ORBIT-SELECTION::compute-orbital-period-circular-orbits
    "This rule computes the period in seconds for a circular orbit"
    ?m <- (MANIFEST::Mission (orbit-altitude# ?h&~nil) (orbit-period# nil))
    =>
    (bind ?P (* 2 (pi) (sqrt (/ (** (+ 6378000 (* 1000 ?h)) 3) 3.986e14))) )
    (modify ?m (orbit-period# ?P))
    )

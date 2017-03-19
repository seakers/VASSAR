;; **********************
;; SMAP EXAMPLE ENUMERATION RULES
;; ***************************
;(set-reset-globals FALSE)
;(ENUMERATION::SMAP-ARCHITECTURE (payload SMAP_RAD SMAP_MWR CMIS VIIRS BIOMASS) (num-sats 1) (orbit-altitude 800) (orbit-raan DD) (orbit-type SSO) (orbit-inc SSO) (num-planes 1) (doesnt-fly ) (num-sats-per-plane 1) (num-instruments 5) (sat-assignments 1 1 1 1 1))

(deftemplate MANIFEST::ARCHITECTURE (multislot payload) (slot num-sats)
    (slot orbit-altitude) (slot orbit-raan) (slot orbit-type) (slot orbit-inc) (slot num-planes)
    (multislot doesnt-fly) (slot num-sats-per-plane) (slot lifecycle-cost) (slot benefit)  (slot space-segment-cost) (slot ground-segment-cost)
    (slot num-instruments) (multislot sat-assignments) (multislot ground-stations) (multislot constellations))

;(defglobal ?*smap-instruments* = 0)
;(bind ?*smap-instruments* (create$ SMAP_RAD SMAP_MWR CMIS VIIRS BIOMASS))
(deftemplate DATABASE::list-of-instruments (multislot list))

(deffacts DATABASE::list-of-instruments (DATABASE::list-of-instruments 
        (list (create$ SMAP_RAD SMAP_MWR CMIS VIIRS BIOMASS))))
(reset)
(defquery DATABASE::get-instruments 
    ?f <- (DATABASE::list-of-instruments (list $?l))
    )

(deffunction get-instruments ()
    (bind ?res (run-query* DATABASE::get-instruments))
    (?res next)
    (bind ?f (?res getObject f))
    (return ?f.list)
    )

(deffunction create-index-of ()
    (bind ?prog "(deffunction index-of (?elem) ")
    (bind ?i 0)
    (bind ?smap-instruments (get-instruments))
    (foreach ?el ?smap-instruments
        (bind ?prog (str-cat ?prog " (if (eq (str-compare ?elem " ?el ") 0) then (return " (++ ?i) ")) "))
        )
    (bind ?prog (str-cat ?prog "(return -1))"))
    ;(printout t ?prog crlf)
    (build ?prog)
    )

(create-index-of)

(deffunction get-instrument (?ind)
    (return (nth$ ?ind (get-instruments)))
    )

(defrule ENUMERATION::duplicate-arch-add-instrument
    ?a <- (MANIFEST::ARCHITECTURE (payload $?payl) (num-instruments ?N&:(< ?N (length$ (get-instruments))))
        (doesnt-fly $?missing) (orbit-altitude nil))
    
    =>
    (bind ?j (index-of (last$ $?payl))); to avoid duplicates 
    (foreach ?p $?missing
        (if (> (index-of ?p) ?j) then 
            (duplicate ?a (payload (add-element$ $?payl ?p))
         (num-instruments (+ ?N 1))            
        (doesnt-fly (del-element$ $?missing ?p)))) 
        )
    (if (eq ?N 0) then (retract ?a))
    )

(defrule ENUMERATION::duplicate-arch-add-constellation-design
    ?a <- (MANIFEST::ARCHITECTURE (num-planes nil) (num-sats-per-plane nil) (num-instruments ?N&~0))
    
    =>

    (foreach ?ns (create$ 1 2)
        (duplicate ?a (num-sats-per-plane ?ns) (num-planes 1))) 
    )

(defrule ENUMERATION::duplicate-arch-add-orbit-altitude
    ?a <- (MANIFEST::ARCHITECTURE (orbit-altitude nil) (num-instruments ?N&~0))
    
    =>

    (foreach ?h (create$ 400 600 800)
        (duplicate ?a (orbit-altitude ?h))) 
    )

(defrule ENUMERATION::duplicate-arch-add-orbit-RAAN
    ?a <- (MANIFEST::ARCHITECTURE (orbit-raan nil) (orbit-inc SSO) (num-instruments ?N&~0))
    
    =>

    (foreach ?r (create$ DD AM)
        (duplicate ?a (orbit-raan ?r))) 
    )

(defrule ENUMERATION::duplicate-arch-add-orbit-RAAN-NA
    ?a <- (MANIFEST::ARCHITECTURE (orbit-raan nil) (orbit-inc ?inc&~SSO&~nil) (num-instruments ?N&~0))
    
    =>
	(modify ?a (orbit-raan NA))
    )

(defrule ENUMERATION::duplicate-arch-add-orbit-inclination-and-type
    ?a <- (MANIFEST::ARCHITECTURE (orbit-inc nil) (num-instruments ?N&~0))
    
    =>
	(bind ?types (create$ SSO LEO))
    (bind ?incs (create$ SSO polar))
    
    (for (bind ?i 1) (<= ?i (length$ ?types)) (++ ?i)
        (duplicate ?a (orbit-inc (nth$ ?i ?incs)) (orbit-type (nth$ ?i ?types)))
        )  
    )

(defrule ENUMERATION::duplicate-arch-add-nsats
    ?a <- (MANIFEST::ARCHITECTURE (num-instruments ?N&~0)  (sat-assignments $?ass))      
    (test (< (length$ $?ass) ?N))
    
    =>
   
    (bind ?n (length$ ?ass))
    (bind ?mx (max$ $?ass)) ; index of current last element of form
    (for (bind ?i 1) (<= ?i (+ ?mx 1)) (++ ?i)
        (bind ?new-ass (insert$ $?ass (+ ?n 1) ?i))
        (duplicate ?a (sat-assignments ?new-ass) (num-sats (max$ ?new-ass)))
        )
    (retract ?a))

; ****

(defrule ENUMERATION::remove-incomplete-arch-missing-raan
    (declare (salience -10))
    ?a <- (MANIFEST::ARCHITECTURE (orbit-raan nil))
    =>
    (retract ?a)
    )

(defrule ENUMERATION::remove-incomplete-arch-missing-inc
    (declare (salience -10))
    ?a <- (MANIFEST::ARCHITECTURE (orbit-inc nil))
    =>
    (retract ?a)
    )

(defrule ENUMERATION::remove-incomplete-arch-missing-type
    (declare (salience -10))
    ?a <- (MANIFEST::ARCHITECTURE (orbit-type nil))
    =>
    (retract ?a)
    )

(defrule ENUMERATION::remove-incomplete-arch-missing-numsats
    (declare (salience -10))
    ?a <- (MANIFEST::ARCHITECTURE (num-sats-per-plane nil))
    =>
    (retract ?a)
    )

(defrule ENUMERATION::remove-incomplete-arch-missing-numplanes
    (declare (salience -10))
    ?a <- (MANIFEST::ARCHITECTURE (num-planes nil))
    =>
    (retract ?a)
    )

(defrule ENUMERATION::remove-incomplete-arch-missing-altitude
    (declare (salience -10))
    ?a <- (MANIFEST::ARCHITECTURE (orbit-altitude nil))
    =>
    (retract ?a)
    )

(defrule ENUMERATION::remove-incomplete-arch-missing-nsats
    (declare (salience -10))
    ?a <- (MANIFEST::ARCHITECTURE (num-sats nil))
    =>
    (retract ?a)
    )

;; **********************
;; SMAP EXAMPLE MANIFEST RULES
;; ***************************


(deffunction to-indexes (?instrs)
    (bind ?list (create$ ))   
    (for (bind ?i 1) (<= ?i (length$ ?instrs)) (++ ?i)
        (bind ?list (insert$ ?list ?i (index-of (nth$ ?i ?instrs))))
        )
    (return ?list)
    )

(deffunction to-strings (?indexes)

    (return (map get-instrument ?indexes))
    )

(deffunction pack-assignment-to-sats (?ass)
    (bind ?list (create$ )) (bind ?n 1)
    (for (bind ?i 1) (<= ?i (length$ ?ass)) (++ ?i)
        (bind ?indexes (find$ ?i ?ass))
        (if (isempty$ ?indexes) then (continue))
        (bind ?list (insert$ ?list ?n "sat")) (++ ?n)
        (bind ?sat-ins (to-strings ?indexes))
        (bind ?list (insert$ ?list ?n ?sat-ins)) (bind ?n (+ ?n (length$ ?sat-ins)))
        
        ) 
    (return ?list)   
    )



(deffunction pack-sats-to-assignment (?sats ?n)
    (bind ?nsat 0) (bind ?ass (create-list-n$ ?n))
    (for (bind ?i 1) (<= ?i (length$ ?sats)) (++ ?i)
        (bind ?el (nth$ ?i ?sats))
        ;(printout t ?el " eq sat? " (eq "sat" ?el) "  nsat " ?nsat crlf)
        (if (eq "sat" ?el) then (++ ?nsat) else 
            ;(printout t "ass " ?ass " element " ?el " index " (index-of ?el) " nsat " ?nsat crlf) 
            (bind ?ass (replace$ ?ass (index-of ?el) (index-of ?el) ?nsat))
            )
        )
    (return ?ass)
    )


(defrule MANIFEST::assert-missions
    (MANIFEST::ARCHITECTURE (payload $?p) (num-sats ?ns) (orbit-type ?typ)
    (orbit-altitude ?h) (orbit-raan ?raan) (orbit-inc ?inc)
         (num-planes ?np) (num-sats-per-plane ?nsp)
    (doesnt-fly $?miss) (num-instruments ?ni) (sat-assignments $?ass))
    
    =>
    
    
    (if (and (eq ?nsp 1) (eq ?np 1)) then (bind ?march single-sat) else (bind ?march constellation))
    (bind ?sats (pack-assignment-to-sats $?ass)) (bind ?nsat 1)
    (printout t "sats " ?sats crlf)
    (bind ?payl (create$ ))
    (for (bind ?i 2) (<= ?i (length$ ?sats)) (++ ?i)
        (bind ?el (nth$ ?i ?sats))
        
        (if (eq "sat" ?el) then 
            
            (assert (MANIFEST::Mission (Name (str-cat "SMAP-" ?nsat))  (mission-architecture ?march) 
                    (num-of-planes# ?np) (num-of-sats-per-plane# ?nsp) (orbit-type ?typ) (orbit-eccentricity 0)
                    (lifetime 5) (instruments ?payl) (orbit-altitude# ?h) (orbit-inclination ?inc)            
            		(orbit-RAAN ?raan) (select-orbit no)  (in-orbit (str-cat ?typ "-" ?h "-" ?inc "-" ?raan))
                    (ADCS-requirement 0.01) (ADCS-type three-axis) (propellant-ADCS hydrazine) (propellant-injection hydrazine)
                    (deorbiting-strategy drag-based) (slew-angle 2.0)
                    ))
            (bind ?payl (create$ ))
            (++ ?nsat)
             else 
            (bind ?payl (insert$ ?payl (+ 1 (length$ ?payl)) ?el))
            ;(printout t " payload " ?payl crlf)
        )
        )
    
    (assert (MANIFEST::Mission (Name (str-cat "SMAP-" ?nsat)) (lifetime 5) (mission-architecture ?march) 
            (num-of-planes# ?np) (num-of-sats-per-plane# ?nsp)
            (instruments ?payl) (orbit-altitude# ?h) (orbit-eccentricity 0)
            (orbit-RAAN ?raan) (orbit-inclination ?inc) (orbit-type ?typ) (select-orbit no) 
            (in-orbit (str-cat ?typ "-" ?h "-" ?inc "-" ?raan))
            (ADCS-requirement 0.01) (ADCS-type three-axis) (propellant-ADCS hydrazine) (propellant-injection hydrazine)
             (deorbiting-strategy drag-based)   (slew-angle 2.0)       
           ))
    )



;; **********************
;; SMAP EXAMPLE CAPABILITY RULES
;; ***************************
(deffunction contains$ (?list ?elem)
    (if (eq (length$ ?list) 0) then (return FALSE))
    (if (eq (first$ ?list) (create$ ?elem)) then (return TRUE) else
         (return (contains$ (rest$ ?list) ?elem)))    
    )

(defrule MANIFEST::SMAP-add-common-dish-to-MWR
    "If we manifest the SMAP radar, radiometer, or both, then we need to 
    manifest the share dish"
    
    ?miss <- (MANIFEST::Mission (instruments $?list-of-instruments))
    (test (eq (contains$ ?list-of-instruments SMAP_ANT) FALSE))
    (test (eq (contains$ ?list-of-instruments SMAP_MWR) TRUE))
    
       =>
    ;(bind ?new-list (add-element$ ?list-of-instruments SMAP_ANT))
    ;(printout t "contains SMAP_ANT = " (eq (subsetp (create$ SMAP_ANT) ?list-of-instruments) FALSE) " new list = " ?new-list crlf)
    (modify ?miss (instruments (add-element$ ?list-of-instruments SMAP_ANT)))
    
    ) 

(defrule MANIFEST::SMAP-add-common-dish-to-RAD
    "If we manifest the SMAP radar, radiometer, or both, then we need to manifest the share dish"
    ?miss <- (MANIFEST::Mission (instruments $?list-of-instruments))
	(test (eq (contains$ ?list-of-instruments SMAP_ANT) FALSE))
    (test (eq (contains$ ?list-of-instruments SMAP_RAD) TRUE))
    ;(test (eq (subsetp (create$ SMAP_MWR) ?list-of-instruments) TRUE))
       =>
    ;(bind ?new-list (insert$ ?list-of-instruments (+ 1 (length$ ?list-of-instruments)) SMAP_ANT))
    ;(printout t "contains SMAP_ANT = " (eq (subsetp (create$ SMAP_ANT) ?list-of-instruments) FALSE) " new list = " ?new-list crlf)
    (modify ?miss (instruments (add-element$ ?list-of-instruments SMAP_ANT)))
    
    ) 

(deffunction compute-swath-conical-MWR (?h ?half-scan ?off-nadir)
    (return (* 2 (/ ?h (matlabf cos (torad ?off-nadir))) (matlabf tan (torad ?half-scan))))
    )


(defrule MANIFEST::compute-SMAP-MWR-spatial-resolution
    ?MWR <- (CAPABILITIES::Manifested-instrument  (Name SMAP_MWR) (Intent "Imaging multi-spectral radiometers -passive MW-")
         (frequency# ?f&~nil) (orbit-altitude# ?h&~nil) (Horizontal-Spatial-Resolution# nil) (off-axis-angle-plus-minus# ?theta) (scanning-angle-plus-minus# ?alfa) (flies-in ?sat))
    (CAPABILITIES::Manifested-instrument  (Name SMAP_ANT) (dimension-x# ?D) (flies-in ?sat))
    =>
    (bind ?dtheta (/ 3e8 (* ?D ?f))); lambda/D
    (bind ?theta1 (- (torad ?theta) (/ ?dtheta 2)))
    (bind ?theta2 (+ (torad ?theta) (/ ?dtheta 2)))
    (bind ?x1 (* (* 1000 ?h) (matlabf tan ?theta1)))
    (bind ?x2 (* (* 1000 ?h) (matlabf tan ?theta2)))
    (bind ?along (- ?x2 ?x1))
    (bind ?cross (* 2 (* (/ ?h (matlabf cos (torad ?theta))) (matlabf tan (/ ?dtheta 2)))))
    ;(printout t "(compute-swath-conical-MWR ?h ?alfa ?theta) = " (compute-swath-conical-MWR ?h ?alfa ?theta) crlf)
    (bind ?sw (compute-swath-conical-MWR ?h ?alfa ?theta))
    (modify ?MWR (Angular-resolution-elevation# ?dtheta) (Horizontal-Spatial-Resolution# ?along) (Horizontal-Spatial-Resolution-Along-track# ?along) 
        (Horizontal-Spatial-Resolution-Cross-track# ?cross) (Swath# ?sw) (Field-of-view# ?alfa))
    )

(defrule MANIFEST::compute-CMIS-spatial-resolution
    ?MWR <- (CAPABILITIES::Manifested-instrument  (Name CMIS) (Intent "Imaging multi-spectral radiometers -passive MW-")
         (frequency# ?f&~nil) (orbit-altitude# ?h&~nil) (dimension-x# ?D&~nil) (Horizontal-Spatial-Resolution# nil) (off-axis-angle-plus-minus# ?theta) (scanning-angle-plus-minus# ?alfa) (flies-in ?sat))
    =>
    (bind ?dtheta (/ 3e8 (* ?D ?f))); lambda/D
    (bind ?theta1 (- (torad ?theta) (/ ?dtheta 2)))
    (bind ?theta2 (+ (torad ?theta) (/ ?dtheta 2)))
    (bind ?x1 (* (* 1000 ?h) (matlabf tan ?theta1)))
    (bind ?x2 (* (* 1000 ?h) (matlabf tan ?theta2)))
    (bind ?along (- ?x2 ?x1))
    (bind ?cross (* 2 (* (/ ?h (matlabf cos (torad ?theta))) (matlabf tan (/ ?dtheta 2)))))
    (bind ?sw (compute-swath-conical-MWR ?h ?alfa ?theta))
    (modify ?MWR (Angular-resolution-elevation# ?dtheta) (Horizontal-Spatial-Resolution# ?along) (Horizontal-Spatial-Resolution-Along-track# ?along) 
        (Horizontal-Spatial-Resolution-Cross-track# ?cross) (Swath# ?sw) (Field-of-view# ?alfa))
    )

(defrule MANIFEST::compute-SMAP-RAD-spatial-resolution
    ?RAD <- (CAPABILITIES::Manifested-instrument  (Name SMAP_RAD) (bandwidth# ?B) (off-axis-angle-plus-minus# ?theta) (number-of-looks# ?nl&~nil)  (scanning-angle-plus-minus# ?alfa)
         (frequency# ?f&~nil) (orbit-altitude# ?h&~nil) (Horizontal-Spatial-Resolution# nil) (off-axis-angle-plus-minus# ?theta) (flies-in ?sat))
    (CAPABILITIES::Manifested-instrument  (Name SMAP_ANT) (dimension-x# ?D&~nil) (flies-in ?sat))
    =>
    ;(printout t "b = " ?B " theta = " ?theta crlf)
    (bind ?dtheta (/ 3e8 (* ?D ?f))); lambda/D
    (bind ?range-res (/ 3e8 (* 2 ?B (matlabf sin (torad ?theta)))))
    (bind ?sw (* 2 ?h (matlabf tan (/ (torad (+ ?alfa ?theta)) 2))))
    (modify ?RAD (Angular-resolution-elevation# ?dtheta) (Horizontal-Spatial-Resolution# (* ?nl ?range-res)) 
        (Horizontal-Spatial-Resolution-Along-track# (/ ?range-res (matlabf sin (torad ?theta)))) 
        (Horizontal-Spatial-Resolution-Cross-track# ?range-res) (Swath# ?sw) 
        (Field-of-view# ?alfa))
    )

(defrule MANIFEST::compute-BIOMASS-spatial-resolution
    ?RAD <- (CAPABILITIES::Manifested-instrument  (Name BIOMASS) (dimension-x# ?D&~nil) (bandwidth# ?B) (off-axis-angle-plus-minus# ?theta) (number-of-looks# ?nl&~nil)  (scanning-angle-plus-minus# ?alfa)
         (frequency# ?f&~nil) (orbit-altitude# ?h&~nil) (Horizontal-Spatial-Resolution# nil) (off-axis-angle-plus-minus# ?theta) (flies-in ?sat))
    =>
    ;(printout t "b = " ?B " theta = " ?theta crlf)
    (bind ?dtheta (/ 3e8 (* ?D ?f))); lambda/D
    (bind ?range-res (/ 3e8 (* 2 ?B (matlabf sin (torad ?theta)))))
    (bind ?sw (compute-swath-conical-MWR ?h ?alfa ?theta))
    (modify ?RAD (Angular-resolution-elevation# ?dtheta) (Horizontal-Spatial-Resolution# (* ?nl ?range-res)) 
        (Horizontal-Spatial-Resolution-Along-track# (/ ?range-res (matlabf sin (torad ?theta)))) 
        (Horizontal-Spatial-Resolution-Cross-track# ?range-res) (Swath# ?sw) 
        (Field-of-view# ?alfa))
    )

(defrule compute-sensitivity-to-soil-moisture-in-vegetation
    "This rule computes the sensitivity to soil moisture in the presence
    of vegetation as a function of frequency, based on [Jackson et al, 91]:
    sensitivity = 10*lambda - 0.4 in BT/SM%"
    
    ?instr <- (CAPABILITIES::Manifested-instrument (frequency# ?f&~nil)
          (sensitivity# nil))
    =>
    (modify ?instr (sensitivity# (- (* 10 (/ 3e8 ?f)) 0.4)))
    )

(defrule CAPABILITIES::compute-image-distortion-in-side-looking-instruments
    "Computes image distortion for side-looking instruments"
    ?instr <- (CAPABILITIES::Manifested-instrument (orbit-altitude# ?h&~nil) 
        (Geometry slant)  (characteristic-orbit ?orb&~nil) (image-distortion# nil))
    =>
    (bind ?href (get-orbit-altitude ?orb))
    
    (modify ?instr (image-distortion# (/ ?h ?href))) 
        
    )

(deffunction between (?x ?mn ?mx)
    ;(printout t ?x " " ?mn " " ?mx crlf)
    ;(printout t ">= x min " (>= ?x ?mn)  " <= x max = " (<= ?x ?mx) crlf)
    (return 
        (and 
            (>= ?x ?mn) (<= ?x ?mx)))
    )




(deffunction get-soil-penetration (?f)
    (bind ?lambda (/ 3e10 ?f)); lambda in cm
    (if (< ?lambda 1) then (return 0.001))
    (if (between ?lambda 1 2) then (return 0.01))
    (if (between ?lambda 2 5) then (return 0.05))
    (if (between ?lambda 5 10) then (return 0.08))
    (if (between ?lambda 10 25) then (return 0.3))
    (if (between ?lambda 25 50) then (return 0.8))
    (if (> ?lambda 50) then (return 1.0))
    )

(defrule CAPABILITIES::compute-soil-penetration
    ?instr <- (CAPABILITIES::Manifested-instrument (frequency# ?f&~nil) 
        (soil-penetration# nil))
    =>
    (modify ?instr (soil-penetration# (get-soil-penetration ?f)))
    )
;; **********************
;; SMAP EXAMPLE EMERGENCE RULES
;; ***************************

(defrule SYNERGIES::SMAP-spatial-disaggregation 
    "A frequent coarse spatial resolution measurement can be combined
     with a sparse high spatial resolution measurement to produce 
    a frequent high spatial resolution measurement with average accuracy"
    
    ?m1 <- (REQUIREMENTS::Measurement (Parameter "2.3.2 soil moisture") (Illumination Active) 
        (Horizontal-Spatial-Resolution# ?hs1&~nil) (Accuracy# ?a1&~nil)  (Id ?id1) (taken-by ?ins1))
    ?m2 <- (REQUIREMENTS::Measurement (Parameter "2.3.2 soil moisture") (Illumination Passive) 
        (Horizontal-Spatial-Resolution# ?hs2&~nil) (Accuracy# ?a2&~nil) (Id ?id2) (taken-by ?ins2))
    (SYNERGIES::cross-registered (measurements $?m))
    (test (member$ ?id1 $?m))
    (test (member$ ?id2 $?m))
    ;(not (REASONING::stop-improving (Measurement ?p)))
    (test (eq (str-index disaggregated ?ins1) FALSE))
    (test (eq (str-index disaggregated ?ins2) FALSE))
    (test (neq ?id1 ?id2))

	=>
	;(printout t hola crlf)
    (duplicate ?m1 (Horizontal-Spatial-Resolution# (sqrt (* ?hs1 ?hs2))) (Accuracy# ?a2)
            (Id (str-cat ?id1 "-disaggregated" ?id2))
            (taken-by (str-cat ?ins1 "-" ?ins2 "-disaggregated")));; fuzzy-max in accuracy is OK because joint product does provide 4% accuracy
)

(defrule SYNERGIES::carbon-net-ecosystem-exchange 
    "Carbon net ecosystem exchange data products are produced from the combination of soil moisture, land surface temperature, 
    landcover classificatin, and vegetation gross primary productivity [Entekhabi et al, 2010]"
    
    ?SM <- (REQUIREMENTS::Measurement (Parameter "2.3.2 soil moisture")  (Id ?id1) (taken-by ?ins1))
    (REQUIREMENTS::Measurement (Parameter "2.5.1 Surface temperature -land-") (Id ?id2) (taken-by ?ins2))
    (REQUIREMENTS::Measurement (Parameter "2.6.2 landcover status")  (Id ?id3) (taken-by ?ins3))
    (REQUIREMENTS::Measurement (Parameter "2.4.2 vegetation state") (Id ?id4) (taken-by ?ins4))
    ;(SYNERGIES::cross-registered (measurements $?m)) (test (subsetp (create$ ?id1 ?id2 ?id3 ?id4) $?m))
    ;(not (REQUIREMENTS::Measurement (Parameter "2.3.3 Carbon net ecosystem exchange NEE")))
	=>

    (duplicate ?SM (Parameter "2.3.3 Carbon net ecosystem exchange NEE")  
            (Id (str-cat ?id1 "-syn" ?id2 "-syn" ?id3 "-syn" ?id4))
            (taken-by (str-cat ?ins1 "-syn" ?ins2 "-syn-" ?ins3 "-syn-" ?ins4)));; fuzzy-max in accuracy is OK because joint product does provide 4% accuracy
)

(defrule SYNERGIES::snow-cover-3freqs
    "Full accuracy of snow cover product is obtained when IR, X, and L-band measurements
    are combined "
    
    ?IR <- (REQUIREMENTS::Measurement (Parameter "4.2.4 snow cover") (Spectral-region opt-VNIR+TIR)
         (Accuracy Low) (Id ?id1) (taken-by ?ins1))
    
    ?X <- (REQUIREMENTS::Measurement (Parameter "4.2.4 snow cover") (Spectral-region MW-X+Ka+Ku+mm)
         (Accuracy Low) (Id ?id2) (taken-by ?ins2))
    
    ?L <- (REQUIREMENTS::Measurement (Parameter "4.2.4 snow cover") (Spectral-region MW-L)
        (Accuracy Low) (Id ?id3) (taken-by ?ins3))
    
    =>
    
    (duplicate ?X (Accuracy High) (Id (str-cat ?id1 "-syn-" ?id2 "-syn-" ?id3))
            (taken-by (str-cat ?ins1 "-syn-" ?ins2 "-syn-" ?ins3)))
    )

(defrule SYNERGIES::snow-cover-2freqs
    "Medium accuracy of snow cover product is obtained when IR and MW measurements
    are combined "
    
    ?IR <- (REQUIREMENTS::Measurement (Parameter "4.2.4 snow cover") (Spectral-region opt-VNIR+TIR)
         (Accuracy Low) (Id ?id1) (taken-by ?ins1))
    
    ?MW <- (REQUIREMENTS::Measurement (Parameter "4.2.4 snow cover") (Spectral-region ?sr&~nil)
         (Accuracy Low) (Id ?id2) (taken-by ?ins2))

    (test (neq (str-index MW ?sr) FALSE))
    =>
    ;(printout t "snow cover 2 freqs " crlf)
    (duplicate ?MW (Accuracy Medium) (Id (str-cat ?id1 "-syn-" ?id2 ))
            (taken-by (str-cat ?ins1 "-syn-" ?ins2)))
    )

(defrule SYNERGIES::ice-cover-3freqs
    "Full accuracy of ice cover product is obtained when IR, X, and L-band measurements
    are combined "
    
    ?IR <- (REQUIREMENTS::Measurement (Parameter "4.3.2 Sea ice cover") (Spectral-region opt-VNIR+TIR)
         (Accuracy Low) (Id ?id1) (taken-by ?ins1))
    
    ?X <- (REQUIREMENTS::Measurement (Parameter "4.3.2 Sea ice cover") (Spectral-region MW-X+Ka+Ku+mm)
        (Accuracy Low) (Id ?id2) (taken-by ?ins2))
    
    ?L <- (REQUIREMENTS::Measurement (Parameter "4.3.2 Sea ice cover") (Spectral-region MW-L)
         (Accuracy Low) (Id ?id3) (taken-by ?ins3))
    
    =>
    
    (duplicate ?X (Accuracy High) (Id (str-cat ?id1 "-syn-" ?id2 "-syn-" ?id3))
            (taken-by (str-cat ?ins1 "-syn-" ?ins2 "-syn-" ?ins3)))
    )

(defrule SYNERGIES::ice-cover-2freqs
    "Medium accuracy of ice cover product is obtained when IR and MW measurements
    are combined "
    
    ?IR <- (REQUIREMENTS::Measurement (Parameter "4.3.2 Sea ice cover") (Spectral-region opt-VNIR+TIR)
        (Accuracy Low) (Id ?id1) (taken-by ?ins1))
    
    ?MW <- (REQUIREMENTS::Measurement (Parameter "4.3.2 Sea ice cover") (Spectral-region ?sr&~nil)
         (Accuracy Low) (Id ?id2) (taken-by ?ins2))

    (test (neq (str-index MW ?sr) FALSE))
    =>
    
    (duplicate ?MW (Accuracy Medium) (Id (str-cat ?id1 "-syn-" ?id2 ))
            (taken-by (str-cat ?ins1 "-syn-" ?ins2)))
    )

(defrule SYNERGIES::ocean-salinity-space-average
    "L-band passive radiometer can yield 0.2psu data if we average in space
    (from SMAP applications report)"

    ?L <- (REQUIREMENTS::Measurement (Parameter "3.3.1 Ocean salinity") (Accuracy# ?a1&~nil) 
        (Horizontal-Spatial-Resolution# ?hsr1&~nil) (Id ?id1) (taken-by ?ins1&SMAP_MWR))    
    (test (eq (str-index averaged ?ins1) FALSE))
    =>
    (bind ?a2 (/ ?a1 3.0))
    (bind ?hsr2 (* ?hsr1 3.0))
    (duplicate ?L (Accuracy# ?a2) (Horizontal-Spatial-Resolution# ?hsr2) (Id (str-cat ?id1 "-space-averaged")) 
        (taken-by (str-cat ?ins1 "-space-averaged")))
    )

(defrule SYNERGIES::ocean-wind-space-average
    "L-band passive radiometer can yield 1 m/s wind data if we average in space
    (from SMAP applications report)"

    ?L <- (REQUIREMENTS::Measurement (Parameter "3.4.1 Ocean surface wind speed") (Accuracy# ?a1&~nil) 
        (Horizontal-Spatial-Resolution# ?hsr1&~nil) (Id ?id1) (taken-by ?ins1&SMAP_MWR))    
    (test (eq (str-index averaged ?ins1) FALSE))
    =>
    (bind ?a2 (/ ?a1 2.0))
    (bind ?hsr2 (* ?hsr1 2.0))
    (duplicate ?L (Accuracy# ?a2) (Horizontal-Spatial-Resolution# ?hsr2) (Id (str-cat ?id1 "-space-averaged")) 
        (taken-by (str-cat ?ins1 "-space-averaged")))
    )
;; **********************
;; SMAP EXAMPLE REQUIREMENT RULES
;; ***************************




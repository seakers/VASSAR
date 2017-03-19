
(deffunction get-launch-cost (?lv)
    (if (eq ?lv Ariane5-class) then (return 100)
        elif (eq ?lv Soyuz-class) then (return 50)
        elif (eq ?lv Vega-class) then (return 25)
        elif (eq ?lv Pegasus-class) then (return 15)
        elif (eq ?lv Atlas5-class) then (return 110)
        elif (eq ?lv Delta7320-class) then (return 45)
        elif (eq ?lv Delta7420-class) then (return 55)
        elif (eq ?lv Delta7920-class) then (return 65)
        elif (eq ?lv Taurus-class) then (return 20)
        elif (eq ?lv Taurus-XL-class) then (return 30)
        elif (eq ?lv MinotaurIV-class) then (return 35)
        else (return 500)
        
        )
    )

(deffunction get-launch-fairing-dimensions (?lv)
    (if (eq ?lv Ariane5-class) then (return (create$ 4.57 10.0))
        elif (eq ?lv Soyuz-class) then (return (create$ 3.86 5.06))
        elif (eq ?lv Vega-class) then (return (create$ 2.38 3.5))
        elif (eq ?lv Pegasus-class) then (return (create$ 1.18 2.13))
        elif (eq ?lv Atlas5-class) then (return (create$ 4.57 7.63))
        elif (eq ?lv Delta7320-class) then (return (create$ 2.51 6.82))
        elif (eq ?lv Delta7420-class) then (return (create$ 2.69 7.16))
        elif (eq ?lv Delta7920-class) then (return (create$ 2.69 7.53))
        elif (eq ?lv Taurus-class) then (return (create$ 1.4 2.67))
        elif (eq ?lv Taurus-XL-class) then (return (create$ 1.98 5.71))
        elif (eq ?lv MinotaurIV-class) then (return (create$ 2.00 5.44))
        else (return (create$ 100 100))
        
        )
    )


; Create all four possibilities



(defquery LV-SELECTION::search-instrument-by-name
    (declare (variables ?name))
    (DATABASE::Instrument (Name ?name) (mass# ?m) (average-power# ?p) (peak-power# ?pp) 
        (average-data-rate# ?rb) (dimension-x# ?dx) (dimension-y# ?dy) 
        (dimension-z# ?dz) (cost# ?c))
    )

(deffunction get-instrument-cost (?instr)
    (bind ?result (run-query* search-instrument-by-name ?instr))
    (?result next)
    (return (?result getDouble c))
    )

(deffunction get-instrument-mass (?instr)
    (bind ?result (run-query* search-instrument-by-name ?instr))
    (?result next)
    (return (?result getDouble m))
    )


(deffunction get-instrument-peak-power (?instr)
    (bind ?result (run-query* search-instrument-by-name ?instr))
    (?result next)
    (return (?result getDouble pp))
    )



(deffunction get-instrument-power (?instr)
    (bind ?result (run-query* search-instrument-by-name ?instr))
    (?result next)
    (return (?result getDouble p))
    )

(deffunction get-instrument-datarate (?instr)
    (bind ?result (run-query* search-instrument-by-name ?instr))
    (?result next)
    (return (?result getDouble rb))
    )

(deffunction compute-payload-mass ($?payload)
    (bind ?mass 0)
    (foreach ?instr $?payload
        (bind ?mass (+ ?mass (get-instrument-mass ?instr)))
        )
    (return ?mass)
    )

(deffunction compute-payload-power ($?payload)
    (bind ?power 0)
    (foreach ?instr $?payload
        (bind ?power (+ ?power (get-instrument-power ?instr)))
        )
    (return ?power)
    )

(deffunction compute-payload-peak-power ($?payload)
    (bind ?power 0)
    (foreach ?instr $?payload
        (bind ?power (+ ?power (get-instrument-peak-power ?instr)))
        )
    (return ?power)
    )

(deffunction compute-payload-data-rate ($?payload)
    (bind ?rb 0)
    (foreach ?instr $?payload
        (bind ?rb (+ ?rb (get-instrument-datarate ?instr)))
        )
    (return ?rb)
    )

(deffunction compute-payload-dimensions (?payload)
    ;;(printout t "compute-payload-dimensions" ?payload crlf)
    (bind ?X 0) (bind ?Y 0) (bind ?Z 0) (bind ?nadir-area 0)
    (foreach ?instr ?payload
        (bind ?result (run-query* search-instrument-by-name ?instr))
        (?result next)
        (bind ?dx (?result getDouble dx))
        (bind ?dy (?result getDouble dy))
        (bind ?dz (?result getDouble dz))
        (bind ?X (max ?X ?dx))
        (bind ?Y (max ?Y ?dy))
        (bind ?nadir-area  (+ ?nadir-area  (* ?dx ?dy)))
        (bind ?Z (max ?Z ?dz))
        )
    (bind ?max-dim (max ?X ?Y ?Z))
    (return (create$ ?max-dim ?nadir-area ?Z))
    )

(defrule LV-SELECTION::recompute-power-needs-lidar-or-SAR
    "The power consumption of an active lidar/SAR is assumed to be for 
    400km/600km respectively. If the actual orbit altitude is not this one, power consumption
     needs to be corrected using a h^3 pseudo-lidar-radar-equation to be iso-performance"
    (declare (salience 20))
    ?i <- (CAPABILITIES::Manifested-instrument (Name ?name) (Illumination Active) (Intent ?int)
        (characteristic-orbit ?orb&~nil) (orbit-altitude# ?h&~nil) (orbit-type ?typ) 
        (orbit-RAAN ?raan) (orbit-inclination ?inc) (average-power# ?p&~nil))
    ?d <- (DATABASE::Instrument (Name ?name))
    (test (neq (integer ?h) (integer (get-orbit-altitude ?orb))))
    (or 
        (test (eq ?int "Laser altimeters")) 
        (test (eq ?int "Elastic lidar")) 
        (test (eq ?int "Differential Absorption Lidars")) 
        (test (eq ?int "Doppler Wind Lidars"))
        (test (eq ?int "Imaging MW radars (SAR)"))
        )
    =>
    ;(printout t "power at characteristic-orbit = " ?orb " is " ?p " and at new-orbit = " (str-cat ?typ "-" ?h "-" ?inc "-" ?raan) " is " (* ?p (** (/ ?h (get-orbit-altitude ?orb)) 3)) crlf)    
    (modify ?i (characteristic-orbit (str-cat ?typ "-" ?h "-" ?inc "-" ?raan)) 
        (average-power# (* ?p (** (/ ?h (get-orbit-altitude ?orb)) 3))))
    (modify ?d (characteristic-orbit (str-cat ?typ "-" ?h "-" ?inc "-" ?raan)) 
        (average-power# (* ?p (** (/ ?h (get-orbit-altitude ?orb)) 3))))
    )


; populate payload mass
(defrule LV-SELECTION::populate-payload-mass
    (declare (salience 10))
    ?miss <- (MANIFEST::Mission (instruments $?payload) (payload-mass# nil))
    =>
    (bind ?m (compute-payload-mass $?payload))
    (modify ?miss (payload-mass# ?m))
    )
 


; populate payload power
(defrule LV-SELECTION::populate-payload-power
    (declare (salience 10))
    ?miss <- (MANIFEST::Mission (instruments $?payload) (payload-power# nil))
    =>
    (bind ?p (compute-payload-power $?payload))
    (modify ?miss (payload-power# ?p) )
    )

(defrule LV-SELECTION::populate-payload-peak-power
    (declare (salience 10))
    ?miss <- (MANIFEST::Mission (instruments $?payload) (payload-peak-power# nil))
    =>
    (bind ?peak (compute-payload-peak-power $?payload))
    (modify ?miss (payload-peak-power# ?peak)))
    
; populate payload data rate
(defrule LV-SELECTION::populate-payload-data-rate
    (declare (salience 10))
    ?miss <- (MANIFEST::Mission (instruments $?payload) (orbit-period# ?P) (payload-data-rate# nil))
    =>
    (bind ?rb (compute-payload-data-rate $?payload))
    (bind ?rbo (/ (* ?rb 1.2 ?P) (* 1024 8))); (GByte/orbit) 20% overhead
    (modify ?miss (payload-data-rate# ?rb) (sat-data-rate-per-orbit# ?rbo))
    )

; populate payload dimensions
(defrule LV-SELECTION::populate-payload-dimensions
    (declare (salience 10))
    ?miss <- (MANIFEST::Mission (instruments $?payload) (payload-dimensions# $?pd))
    (test (eq (length$ ?pd) 0))
    =>
    (bind ?dim (compute-payload-dimensions ?payload))
    (modify ?miss (payload-dimensions# ?dim))
    )


(deffunction sufficient-performance-Soyuz (?m ?type ?h ?i)
    ;; SSO
    (if (eq ?type SSO) then
        (bind ?perf (- 5260 (* 1.26667 (- ?h 400)))); SSO perf for Soyuz in kg
        (if (> ?perf (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))
        ;; LEO polar
     elif ( eq ?type LEO ) then 
        (bind ?perf (+ 5232 (* 0.0869 ?h) (* ?h ?h -0.00024))); LEO polar perf for Soyuz in kg from user manual page 2-12
        (if (> ?perf (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))
        ;; GTO
     elif (eq ?type GTO) then   
        (bind ?perf 3060); SSO perf for Soyuz in kg
        (if (> ?perf (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))
    else  (return 0))
  )
    
 
(deffunction sufficient-performance-Ariane5 (?m ?type ?h ?i)
    ;; SSO
    ;;(printout t ?m ?type ?h ?i crlf)
    (if (eq ?type SSO) then
        (bind ?perf 10000); SSO perf for Ariane5 in kg
        (if (> ?perf (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))
        
        ;; LEO polar
     elif (eq ?type LEO) then 
        (bind ?perf (- 20000 (* (/ 10000 400) (- ?h 400)))); LEO polar perf for Ariane5 in kg from user manual page 2-12
        ;;(printout t ?perf crlf)
        (if (> ?perf (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))
        
        ;; GTO
     elif (eq ?type GTO) then   
        (bind ?perf 10000); GTO perf for Ariane in kg
        (if (> ?perf (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))
    else (return 0))
  )

(deffunction sufficient-performance-Atlas5 (?m ?type ?h ?i)
    ;; SSO
    ;;(printout t ?m ?type ?h ?i crlf)
    (if (eq ?type SSO) then
        (bind ?perf 10000); SSO perf for Ariane5 in kg
        (if (> ?perf (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))
        
        ;; LEO polar
     elif (eq ?type LEO) then 
        (bind ?perf (- 20000 (* (/ 10000 400) (- ?h 400)))); LEO polar perf for Ariane5 in kg from user manual page 2-12
        ;;(printout t ?perf crlf)
        (if (> ?perf (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))
        
        ;; GTO
     elif (eq ?type GTO) then   
        (bind ?perf 4250); GTO perf for Ariane in kg
        (if (> ?perf (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))
    else (return 0))
  )

(deffunction sufficient-performance-Delta7320 (?m ?type ?h ?i)
(if (eq ?type SSO) then
        (bind ?perf (- 2100 (* 0.6 ?h)));from Delta2 user manual pages 2-10 and beyond
        (if (> ?perf (* ?m 1.05)) then ; 5% margin
            (return 1)
        else (return 0))
        ;; LEO polar
     elif (eq ?type LEO) then 
        (bind ?perf (+ 2187.5 (* -0.53125 ?h) (* ?h ?h 4.0625e-5))); LEO polar perf for Soyuz in kg from user manual page 2-12
        (if (> ?perf (* ?m 1.05)) then ; 5% margin
            (return 1)
        else (return 0))
        ;; GTO
     elif (eq ?type GTO) then   
        (bind ?perf 250); 
        (if (> ?perf (* ?m 1.1)) then ; 10% margin
            (return 1)
        else (return 0))
    else  (return 0))	
  )

(deffunction sufficient-performance-Delta7420 (?m ?type ?h ?i)
(if (eq ?type SSO) then
        (bind ?perf (- 2525 (* 0.67 ?h)));from Delta2 user manual pages 2-10 and beyond
        (if (> ?perf (* ?m 1.05)) then ; 5% margin
            (return 1)
        else (return 0))
        ;; LEO polar
     elif (eq ?type LEO) then 
        (bind ?perf (+ 2492.5 (* -0.57875 ?h) (* ?h ?h 4.6875e-5))); LEO polar perf for Soyuz in kg from user manual page 2-12
        (if (> ?perf (* ?m 1.05)) then ; 5% margin
            (return 1)
        else (return 0))
        ;; GTO
     elif (eq ?type GTO) then   
        (bind ?perf 300); 
        (if (> ?perf (* ?m 1.1)) then ; 10% margin
            (return 1)
        else (return 0))
    else  (return 0))	
  )

(deffunction sufficient-performance-Delta7920 (?m ?type ?h ?i)
(if (eq ?type SSO) then
        (bind ?perf (- 4000 ?h));from Delta2 user manual pages 2-10 and beyond
        (if (> ?perf (* ?m 1.05)) then ; 5% margin
            (return 1)
        else (return 0))
        ;; LEO polar
     elif (eq ?type LEO) then 
        (bind ?perf (+ 3977.5 (* -0.8675 ?h) (* ?h ?h 6.8750e-5))); LEO polar perf for Soyuz in kg from user manual page 2-12
        (if (> ?perf (* ?m 1.05)) then ; 5% margin
            (return 1)
        else (return 0))
        ;; GTO
     elif (eq ?type GTO) then   
        (bind ?perf 500); 
        (if (> ?perf (* ?m 1.1)) then ; 10% margin
            (return 1)
        else (return 0))
    else  (return 0))	
  )
(deffunction sufficient-performance-Vega (?m ?type ?h ?i)
    ;; SSO
    (if (eq ?type SSO)  then
        (bind ?perf (- 1825 (* ?h 0.5371))); SSO perf for Vega in kg from Vega User manual Fig 2.4
        (if (> ?perf (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))
        
        ;; LEO polar
     elif (and (eq ?type LEO) (eq ?i polar)) then 
        (bind ?perf (- 2504 (* 0.6564 ?h ))); LEO polar ?perf for Ariane5 in kg from user manual page 2-12
        (if (> ?perf (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))

      ;; LEO equatorial
     elif (and (eq ?type LEO) (eq ?i 0)) then 
        (bind ?perf (- 1895 (* 0.5487 ?h ))); LEO polar perf for Ariane5 in kg from user manual page 2-12
        (if (> ?perf (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))
                
        ;; GTO
     elif (eq ?type GTO) then   
         (return 0)
    else  (return 0))
  )

(deffunction sufficient-performance-Taurus (?m ?type ?h ?i)
    ;; SSO
    (if (eq ?type SSO)  then
        (bind ?perf (- 1236 (* ?h 0.4575))); SSO perf for Taurus in kg from Taurus User manual Fig 3.3
        (if (> ?perf (* ?m 1.1)) then ; 10% margin
            (return 1)
        else (return 0))
        
        ;; LEO polar
     elif (and (eq ?type LEO) (eq ?i polar)) then 
        (bind ?perf (- 1191 (* 0.44 ?h ))); LEO polar ?perf for Taurus in kg from Taurus user manual fig 3.4
        (if (> ?perf (* ?m 1.1)) then ; 10% margin
            (return 1)
        else (return 0))

      ;; LEO equatorial
     elif (and (eq ?type LEO) (eq ?i near-polar)) then 
        (bind ?perf (- 1716 (* 62 ?h ))); LEO polar perf for Ariane5 in kg from user manual page 2-12
        (if (> ?perf (* ?m 1.1)) then ; 30% margin
            (return 1)
        else (return 0))
                
        ;; GTO
     elif (eq ?type GTO) then   
         (return 0)
    else  (return 0))
  )

(deffunction sufficient-performance-Taurus-XL (?m ?type ?h ?i)
    ;; SSO
    (if (eq ?type SSO)  then
        (bind ?perf (- 1236 (* ?h 0.4575))); SSO perf for Taurus in kg from Taurus User manual Fig 3.3
        (if (> ?perf (* ?m 1.1)) then ; 10% margin
            (return 1)
        else (return 0))
        
        ;; LEO polar
     elif (and (eq ?type LEO) (eq ?i polar)) then 
        (bind ?perf (- 1191 (* 0.44 ?h ))); LEO polar ?perf for Taurus in kg from Taurus user manual fig 3.4
        (if (> ?perf (* ?m 1.1)) then ; 10% margin
            (return 1)
        else (return 0))

      ;; LEO equatorial
     elif (and (eq ?type LEO) (eq ?i near-polar)) then 
        (bind ?perf (- 1716 (* 62 ?h ))); LEO polar perf for Ariane5 in kg from user manual page 2-12
        (if (> ?perf (* ?m 1.1)) then ; 30% margin
            (return 1)
        else (return 0))
                
        ;; GTO
     elif (eq ?type GTO) then   
         (return 0)
    else  (return 0))
  )

(deffunction sufficient-performance-MinotaurIV (?m ?type ?h ?i)
    ;; SSO
    (if (eq ?type SSO)  then
        (bind ?perf (+ 1281.8 (* -0.22125 ?h) (* ?h ?h -9.3750e-5))); SSO perf for Minotaur-IV in kg from Minotaur IV User manual p. 14 fig 3.3
        (if (> ?perf (* ?m 1.05)) then ; 10% margin
            (return 1)
        else (return 0))
        
        ;; LEO polar
     elif (eq ?type LEO) then 
        (bind ?perf (+ 1379.8 (* -0.4237 ?h) (* ?h ?h -9.3750e-5))); LEO polar ?perf for Minotaur-IV in kg from Minotaur IV User manual p. 14 fig 3.3
        (if (> ?perf (* ?m 1.05)) then ; 10% margin
            (return 1)
        else (return 0))
                
        ;; GTO
     elif (eq ?type GTO) then   
         (return 0)
    else  (return 0))
  )

 (deffunction sufficient-performance-Pegasus (?m ?type ?h ?i)
    ;; SSO
    (if (eq ?type SSO)  then
        ;(bind ?perf (- 365 (* ?h 0.219))); SSO perf for Vega in kg from Vega User manual Fig 2.4
        (if (> (- 365 (* ?h 0.219)) (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))
        
        ;; LEO polar
     elif (and (eq ?type LEO) (eq ?i polar)) then 
        ;(bind ?perf (- 394 (* 0.2325 ?h ))); LEO polar perf for Ariane5 in kg from user manual page 2-12
        (if (> (- 394 (* 0.2325 ?h )) (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))

      ;; LEO equatorial
     elif (and (eq ?type LEO) (eq ?i 0)) then 
        ;(bind ?perf (- 506 (* 0.2675 ?h ))); LEO polar perf for Ariane5 in kg from user manual page 2-12
        (if (> (- 506 (* 0.2675 ?h )) (* ?m 1.3)) then ; 30% margin
            (return 1)
        else (return 0))
                 
        ;; GTO
     elif (eq ?type GTO) then   
         (return 0)
    else  (return 0))
  )

; sufficient-performance functions
(deffunction sufficient-performance (?lv ?m ?type ?h ?i)
    (if (eq ?lv Ariane5-class) then (return (sufficient-performance-Ariane5 ?m ?type ?h ?i))
	 elif (eq ?lv Soyuz-class) then (return (sufficient-performance-Soyuz ?m ?type ?h ?i))
     elif (eq ?lv Vega-class) then (return (sufficient-performance-Vega ?m ?type ?h ?i))
     elif (eq ?lv Atlas5-class) then (return (sufficient-performance-Atlas5 ?m ?type ?h ?i))
     elif (eq ?lv Delta7320-class) then (return (sufficient-performance-Delta7320 ?m ?type ?h ?i))
     elif (eq ?lv Delta7420-class) then (return (sufficient-performance-Delta7420 ?m ?type ?h ?i))
     elif (eq ?lv Delta7920-class) then (return (sufficient-performance-Delta7920 ?m ?type ?h ?i))
     elif (eq ?lv Pegasus-class) then (return (sufficient-performance-Pegasus ?m ?type ?h ?i))
     elif (eq ?lv Taurus-class) then (return (sufficient-performance-Taurus ?m ?type ?h ?i))
     elif (eq ?lv Taurus-XL-class) then (return (sufficient-performance-Taurus-XL ?m ?type ?h ?i))
     elif (eq ?lv MinotaurIV-class) then (return (sufficient-performance-MinotaurIV ?m ?type ?h ?i))
        else (return 1)
        )

    )


; performance rule
(defrule LV-SELECTION::insufficient-performance-prelim
    "Eliminate options for which performance is not sufficient with margin"
    ?f <- (MANIFEST::Mission (Name ?miss) (launch-vehicle ?lv&~nil) (satellite-mass# nil) (payload-mass# ?m&~nil) 
        (orbit-type ?typ&~nil) (orbit-altitude# ?h&~nil) (orbit-inclination ?i&~nil))
    (test (neq (sufficient-performance ?lv (* 3 ?m) ?typ ?h ?i) 1)); assume payload-to-bus mass ratio of 3
    =>
    ;(printout t "Insufficient perf of " ?lv " for mission " ?miss crlf)
    (retract ?f)
    )

(defrule LV-SELECTION::insufficient-performance-final
    "Eliminate options for which performance is not sufficient with margin"
    ?f <- (MANIFEST::Mission (Name ?miss) (launch-vehicle ?lv&~nil) (satellite-mass# ?m&~nil) 
        (orbit-type ?typ&~nil) (orbit-altitude# ?h&~nil) (orbit-inclination ?i&~nil))
    (test (neq (sufficient-performance ?lv ?m ?typ ?h ?i) 1)); assume payload-to-bus mass ratio of 3
    =>
    ;(printout t "Insufficient perf of " ?lv " for mission " ?miss crlf)
    (retract ?f)
    )


(deffunction large-enough-height (?lv ?dim); ?dim = (max-diam area height)
    ;(printout t "large-enough-diameter " ?lv ?dim crlf )
    (bind ?fairing-dimensions (get-launch-fairing-dimensions ?lv)); (diam height)
    (bind ?diam (nth$ 1 ?dim))
    (if (eq ?diam nil) then (return 0) else
        (if (> (max$ ?fairing-dimensions) (* 0.8 ?diam)) then 
        (return 1)
        else (return 0)
        )
      )
    ;(bind ?area (nth$ 2 ?dim))
    
    )

(deffunction large-enough-area (?lv ?dim); ?dim = (max-diam area height)
    (bind ?fairing-dimensions (get-launch-fairing-dimensions ?lv)); (diam height)
    ;(bind ?diam (nth$ 1 ?dim))
    (bind ?area (nth$ 2 ?dim))
    (if (eq ?area nil) then (return 0))
    (if (> (* (nth$ 1 ?fairing-dimensions) (nth$ 2 ?fairing-dimensions)) (* 0.65 ?area)) then 
        (return 1)
        else (return 0)
        )
    )

; diameter rule
(defrule LV-SELECTION::insufficient-fairing-height
    "Eliminate options for which fairing height is not sufficient with margin"
    ?f <- (MANIFEST::Mission (Name ?miss) (launch-vehicle ?lv&~nil) (payload-dimensions# $?dim))
    (test (neq (large-enough-height ?lv ?dim) 1))
    =>
    ;(printout t "Insufficient diameter of " ?lv " for mission " ?miss crlf)
    (retract ?f)
   )

; length rule
;(defrule LV-SELECTION::insufficient-fairing-area
;    "Eliminate options for which fairing area is not sufficient with margin"
;    ?f <- (MANIFEST::Mission (Name ?miss ) (launch-vehicle ?lv&~nil) (payload-dimensions# $?dim))
;    (test (neq (large-enough-area ?lv ?dim) 1))
;    =>
;    ;(printout t "Insufficient area of " ?lv " for mission " ?miss crlf)
;    ;(printout t "lv = " ?lv "?dim = " ?dim "(large-enough-height ?lv ?dim) = " (large-enough-height ?lv ?dim) crlf)
;    (retract ?f)
;    )


; fits rule
;(defrule LV-SELECTION::insufficient-fairing-area-or-volume
;    "Eliminate options for which fairing area is not sufficient with margin"
;    ?f <- (MANIFEST::Mission (Name ?miss ) (launch-vehicle ?lv&~nil) (payload-dimensions# $?dim))
;    (test (neq (large-enough-area ?lv ?dim) 1))
;    =>
;    ;(printout t "Insufficient area of " ?lv " for mission " ?miss crlf)
;    ;(printout t "lv = " ?lv "?dim = " ?dim "(large-enough-height ?lv ?dim) = " (large-enough-height ?lv ?dim) crlf)
;    (retract ?f)
;    )

; cost rule
(defrule LV-SELECTION::eliminate-more-expensive-launchers
    "From all feasible options, eliminate the most expensive ones"
    (declare (salience -5))
    ?m1 <- (MANIFEST::Mission (Name ?name) (launch-vehicle ?lv1&~nil) (launch-cost# ?c1&~nil))
    ?m2 <- (MANIFEST::Mission (Name ?name) (launch-vehicle ?lv2&~nil) (launch-cost# ?c2&~nil))
    (test (neq ?lv1 ?lv2))
    =>
    (if (< ?c1 ?c2) then (retract ?m2) 
        else (retract ?m1) )  
    )




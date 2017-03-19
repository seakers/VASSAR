
;; ******************
;; SMAP specific rules
;; *******************

;(defrule SYNERGIES::SMAP-spatial-disaggregation "A frequent coarse spatial resolution measurement can be combined with a sparse high spatial resolution measurement to produce a frequent high spatial resolution measurement with average accuracy"
;    ?m1 <- (REQUIREMENTS::Measurement (Parameter "2.3.2 soil moisture") (Horizontal-Spatial-Resolution ?hsr1&~nil) (Accuracy ?a1&~nil) (Id ?id1) (taken-by ?ins1))
;    ?m2 <- (REQUIREMENTS::Measurement (Parameter "2.3.2 soil moisture") (Horizontal-Spatial-Resolution ?hsr2&~nil) (Accuracy ?a2&~nil) (Id ?id2) (taken-by ?ins2))
;    (SYNERGIES::cross-registered (measurements $?m))
;    (test (member$ ?id1 $?m))
;    (test (member$ ?id2 $?m))
;    (not (REASONING::stop-improving (Measurement ?p)))
;    (test (eq (str-index disaggregated ?ins1) FALSE))
;    (test (eq (str-index disaggregated ?ins2) FALSE))
;    (test (neq ?id1 ?id2))
;
;	=>
;
 ;   (duplicate ?m1 (Horizontal-Spatial-Resolution (eval (fuzzy-max Horizontal-Spatial-Resolution ?hsr1 ?hsr2))) 
  ;          (Accuracy (eval (fuzzy-max Accuracy ?a1 ?a2))) 
   ;         (Id (str-cat ?id1 "-disaggregated" ?id2))
    ;        (taken-by (str-cat ?ins1 "-" ?ins2 "-disaggregated")));; fuzzy-max in accuracy is OK because joint product does provide 4% accuracy
;)

(defrule SYNERGIES::SMAP-spatial-disaggregation 
    "A frequent coarse spatial resolution measurement can be combined
     with a sparse high spatial resolution measurement to produce 
    a frequent high spatial resolution measurement with average accuracy"
    
    ?m1 <- (REQUIREMENTS::Measurement (Parameter "2.3.2 soil moisture") (Illumination Active) 
        (Horizontal-Spatial-Resolution# ?hs1) (Accuracy# ?a1)  (Id ?id1) (taken-by ?ins1))
    ?m2 <- (REQUIREMENTS::Measurement (Parameter "2.3.2 soil moisture") (Illumination Passive) 
        (Horizontal-Spatial-Resolution# ?hs2) (Accuracy# ?a2) (Id ?id2) (taken-by ?ins2))
    (SYNERGIES::cross-registered (measurements $?m))
    (test (member$ ?id1 $?m))
    (test (member$ ?id2 $?m))
    ;(not (REASONING::stop-improving (Measurement ?p)))
    ;(test (eq (str-index disaggregated ?ins1) FALSE))
    ;(test (eq (str-index disaggregated ?ins2) FALSE))
    ;(test (neq ?id1 ?id2))

	=>
	;(printout t hola crlf)
    (duplicate ?m1 (Horizontal-Spatial-Resolution# (sqrt (* ?hs1 ?hs2))) (Accuracy# ?a2)
            (Id (str-cat ?id1 "-disaggregated" ?id2))
            (taken-by (str-cat ?ins1 "-" ?ins2 "-disaggregated")));; fuzzy-max in accuracy is OK because joint product does provide 4% accuracy
)



(defrule SYNERGIES::carbon-net-ecosystem-exchange 
    "Carbon net ecosystem exchange data products are produced from the combination of soil moisture, land surface temperature, 
    landcover classificatin, and vegetation gross primary productivity [Entekhabi et al, 2010]"
    
    ?SM <- (REQUIREMENTS::Measurement (Parameter "2.3.2 soil moisture") (Horizontal-Spatial-Resolution ?hsr1&~nil) (Accuracy ?a1&~nil) (Id ?id1) (taken-by ?ins1))
    ?LST <- (REQUIREMENTS::Measurement (Parameter "2.5.1 Surface temperature -land-") (Horizontal-Spatial-Resolution ?hsr2&~nil) (Accuracy ?a2&~nil) (Id ?id2) (taken-by ?ins2))
    ?LC <- (REQUIREMENTS::Measurement (Parameter "2.6.2 landcover status") (Horizontal-Spatial-Resolution ?hsr3&~nil) (Accuracy ?a3&~nil) (Id ?id3) (taken-by ?ins3))
    ?VEG <- (REQUIREMENTS::Measurement (Parameter "2.4.2 vegetation state") (Horizontal-Spatial-Resolution ?hsr4&~nil) (Accuracy ?a4&~nil) (Id ?id4) (taken-by ?ins4))
    (SYNERGIES::cross-registered (measurements $?m)) (test (subsetp (create$ ?id1 ?id2 ?id3 ?id4) $?m))
    (not (REQUIREMENTS::Measurement (Parameter "2.3.3 Carbon net ecosystem exchange NEE")))
	=>

    (assert (REQUIREMENTS::Measurement (Parameter "2.3.3 Carbon net ecosystem exchange NEE") (Horizontal-Spatial-Resolution (eval (fuzzy-max Horizontal-Spatial-Resolution ?hsr1 ?hsr2))) 
            (Accuracy (eval (fuzzy-max Accuracy ?a1 ?a2))) 
            (Id (str-cat ?id1 "-disaggregated" ?id2))
            (taken-by (str-cat ?ins1 "-" ?ins2 "-disaggregated"))));; fuzzy-max in accuracy is OK because joint product does provide 4% accuracy
)
   

(defrule MANIFEST::SMAP-add-common-dish-to-MWR
    "If we manifest the SMAP radar, radiometer, or both, then we need to manifest the share dish"
    ?miss <- (MANIFEST::Mission (instruments $?list-of-instruments))
    (test (eq (subsetp (create$ SMAP_ANT) ?list-of-instruments) FALSE))
    ;(test (eq (subsetp (create$ SMAP_RAD) ?list-of-instruments) TRUE))
    (test (eq (subsetp (create$ SMAP_MWR) ?list-of-instruments) TRUE))
       =>
    (bind ?new-list (insert$ ?list-of-instruments (+ 1 (length$ ?list-of-instruments)) SMAP_ANT))
    ;(printout t "contains SMAP_ANT = " (eq (subsetp (create$ SMAP_ANT) ?list-of-instruments) FALSE) " new list = " ?new-list crlf)
    (modify ?miss (instruments ?new-list))
    
    ) 

(defrule MANIFEST::SMAP-add-common-dish-to-RAD
    "If we manifest the SMAP radar, radiometer, or both, then we need to manifest the share dish"
    ?miss <- (MANIFEST::Mission (instruments $?list-of-instruments))
    (test (eq (subsetp (create$ SMAP_ANT) ?list-of-instruments) FALSE))
    (test (eq (subsetp (create$ SMAP_RAD) ?list-of-instruments) TRUE))
    ;(test (eq (subsetp (create$ SMAP_MWR) ?list-of-instruments) TRUE))
       =>
    (bind ?new-list (insert$ ?list-of-instruments (+ 1 (length$ ?list-of-instruments)) SMAP_ANT))
    ;(printout t "contains SMAP_ANT = " (eq (subsetp (create$ SMAP_ANT) ?list-of-instruments) FALSE) " new list = " ?new-list crlf)
    (modify ?miss (instruments ?new-list))
    
    ) 

(defrule MANIFEST::compute-MWR-spatial-resolution
    ?MWR <- (CAPABILITIES::Manifested-instrument  (Name SMAP_MWR) (Intent "Imaging multi-spectral radiometers -passive MW-")
         (frequency# ?f&~nil) (orbit-altitude# ?h&~nil) (Horizontal-Spatial-Resolution# nil) (off-axis-angle-plus-minus# ?theta) (scanning-angle-plus-minus# ?alfa) (flies-in ?sat))
    (CAPABILITIES::Manifested-instrument  (Name SMAP_ANT) (dimension-x# ?D) (flies-in ?sat))
    =>
    (bind ?dtheta (/ 3e8 (* ?D ?f))); lambda/D
    (bind ?theta1 (- (torad ?theta) (/ ?dtheta 2)))
    (bind ?theta2 (+ (torad ?theta) (/ ?dtheta 2)))
    (bind ?x1 (* (* 1000 ?h) (tan ?theta1)))
    (bind ?x2 (* (* 1000 ?h) (tan ?theta2)))
    (bind ?along (- ?x2 ?x1))
    (bind ?cross (* 2 (* (/ ?h (cos (torad ?theta))) (tan (/ ?dtheta 2)))))
    (bind ?sw (* 2 (* (/ ?h (cos (torad ?theta))) (tan (/ ?alfa 2)))))
    (modify ?MWR (Horizontal-Spatial-Resolution# ?along) (Horizontal-Spatial-Resolution-Along-track# ?along) 
        (Horizontal-Spatial-Resolution-Cross-track# ?cross) (Swath# ?sw) (Field-of-view# ?alfa))
    )

(defrule MANIFEST::compute-RAD-spatial-resolution
    ?RAD <- (CAPABILITIES::Manifested-instrument  (Name SMAP_RAD) (bandwidth# ?B) (off-axis-angle-plus-minus# ?theta) (number-of-looks# ?nl&~nil)  (scanning-angle-plus-minus# ?alfa)
         (frequency# ?f&~nil) (orbit-altitude# ?h&~nil) (Horizontal-Spatial-Resolution# nil) (off-axis-angle-plus-minus# ?theta) (flies-in ?sat))
    (CAPABILITIES::Manifested-instrument  (Name SMAP_ANT) (dimension-x# ?D) (flies-in ?sat))
    =>
    ;(printout t "b = " ?B " theta = " ?theta crlf)
    (bind ?range-res (/ 3e8 (* 2 ?B (sin (torad ?theta)))))
    (bind ?sw (* 2 (* (/ ?h (cos (torad ?theta))) (tan (/ ?alfa 2)))))
    (modify ?RAD (Horizontal-Spatial-Resolution# (* ?nl ?range-res)) (Horizontal-Spatial-Resolution-Along-track# (/ ?range-res (sin (torad ?theta)))) 
        (Horizontal-Spatial-Resolution-Cross-track# ?range-res) (Swath# ?sw) (Field-of-view# ?alfa))
    )

;; number of looks hsr ==> hsr*sqrt(#looks) in each direction to increase relative error
 
;; **********************
;; DESDYNI
;; **********************

(defrule MANIFEST::compute-SAR-spatial-resolution
    ?RAD <- (CAPABILITIES::Manifested-instrument  (Name DESD_SAR) (bandwidth# ?B) (dimension-x# ?D)  (off-axis-angle-plus-minus# ?theta) (number-of-looks# ?nl&~nil)  (scanning-angle-plus-minus# ?alfa)
         (frequency# ?f&~nil) (orbit-altitude# ?h&~nil) (Horizontal-Spatial-Resolution# nil) (off-axis-angle-plus-minus# ?theta) (flies-in ?sat))
    =>
    ;(printout t "b = " ?B " theta = " ?theta crlf)
    (bind ?range-res (/ 3e8 (* 2 ?B (sin (torad ?theta)))))
    (bind ?sw (* 2 (* (/ ?h (cos (torad ?theta))) (tan (/ ?alfa 2)))))
    (modify ?RAD (Horizontal-Spatial-Resolution# (* ?nl ?range-res)) (Horizontal-Spatial-Resolution-Along-track# (/ ?range-res (sin (torad ?theta)))) 
        (Horizontal-Spatial-Resolution-Cross-track# ?range-res) (Swath# ?sw) (Field-of-view# ?alfa))
    )

 
;; **********************
;; HYSPIRI
;; **********************
(defrule MANIFEST::compute-HYSP-TIR-spatial-resolution 
    "Compute field of view in degrees from angular resolution (IFOV)
    and number of pixels for a square image"
    (declare (salience 5))
    ?instr <- (CAPABILITIES::Manifested-instrument (Name HYSP_TIR) (Field-of-view# nil) 
        (Angular-resolution-azimuth# ?ifovc&~nil) (Angular-resolution-elevation# ?ifova&~nil) (orbit-altitude# ?h&~nil) 
        (num-pixels-along-track# ?npixa&~nil) (num-pixels-cross-track# ?npixc&~nil) 
        (scanning-angle-plus-minus# ?alfa)) 
    =>
	(bind ?fov ?alfa);for orbit calculations
    (bind ?hsra (* 1000 ?h (torad ?ifova))) (bind ?hsrc (* 1000 ?h (torad ?ifovc)))
    (bind ?hsr ?hsrc)
    (bind ?sw (/ (* ?hsr ?npixc) 1000)) 
    (modify ?instr (Field-of-view# ?fov) (Swath# ?sw) 
        (Horizontal-Spatial-Resolution# ?hsra) (Horizontal-Spatial-Resolution# ?hsrc)
        (Horizontal-Spatial-Resolution# ?hsr))
    ;(printout t "HYSP TIR compute hsr ang = " ?ifova " hsra = " ?hsra " hsrc = " ?hsrc " h = " ?h crlf)
    )

(defrule MANIFEST::compute-HYSP-VIS-spatial-resolution 
    "Compute field of view in degrees from angular resolution (IFOV)
    and number of pixels for a square image"
    (declare (salience 5))
    ?instr <- (CAPABILITIES::Manifested-instrument (Name HYSP_VIS) (Field-of-view# nil) 
        (Angular-resolution-azimuth# ?ifovc&~nil) (Angular-resolution-elevation# ?ifova&~nil) (orbit-altitude# ?h&~nil) 
        (num-pixels-along-track# ?npixa&~nil) (num-pixels-cross-track# ?npixc&~nil) 
        (scanning-angle-plus-minus# ?alfa)) 
    =>
	(bind ?fov 5);for orbit calculations
    (bind ?hsra (* 1000 ?h (torad ?ifova))) (bind ?hsrc (* 1000 ?h (torad ?ifovc)))
    (bind ?hsr ?hsrc)
    (bind ?sw (/ (* ?hsr ?npixc) 1000)) 
    (modify ?instr (Field-of-view# ?fov) (Swath# ?sw) 
        (Horizontal-Spatial-Resolution# ?hsra) (Horizontal-Spatial-Resolution# ?hsrc)
        (Horizontal-Spatial-Resolution# ?hsr))
    ;(printout t "HYSP VIS compute hsr ang = " ?ifova " hsra = " ?hsra " hsrc = " ?hsrc " h = " ?h crlf)
    )
;; *********************
;; LIDARS
;; **********************    
(defrule CAPABILITIES::ice-lidar-sensitivity-through-optically-thin-clouds
        ?i <- (CAPABILITIES::Manifested-instrument (sensitivity-in-cirrus nil) (spectral-bands $?sb) (Field-of-view# ?fov&~nil))
        (or 
            (and (test (subsetp (create$ opt-NIR-1064nm) $?sb)) (test (< ?fov 400)))
            (test (subsetp (create$ opt-nir-532nm) $?sb))
            );; less than 400 urad)
        =>
        (modify ?i (sensitivity-in-cirrus High))
        
        )

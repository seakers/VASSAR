

;; SMAP specific rules
(defrule SYNERGIES::SMAP-spatial-disaggregation "A frequent coarse spatial resolution measurement can be combined with a sparse high spatial resolution measurement to produce a frequent high spatial resolution measurement with average accuracy"
    ?m1 <- (REQUIREMENTS::Measurement (Parameter "2.3.2 soil moisture") (Horizontal-Spatial-Resolution ?hsr1&~nil) (Accuracy ?a1&~nil) (Id ?id1) (taken-by ?ins1))
    ?m2 <- (REQUIREMENTS::Measurement (Parameter "2.3.2 soil moisture") (Horizontal-Spatial-Resolution ?hsr2&~nil) (Accuracy ?a2&~nil) (Id ?id2) (taken-by ?ins2))
    (SYNERGIES::cross-registered (measurements $?m))
    (test (member$ ?id1 $?m))
    (test (member$ ?id2 $?m))
    (not (REASONING::stop-improving (Measurement ?p)))
    (test (eq (str-index disaggregated ?ins1) FALSE))
    (test (eq (str-index disaggregated ?ins2) FALSE))
    (test (neq ?id1 ?id2))

	=>

    (duplicate ?m1 (Horizontal-Spatial-Resolution (eval (fuzzy-max Horizontal-Spatial-Resolution ?hsr1 ?hsr2))) 
            (Accuracy (eval (fuzzy-max Accuracy ?a1 ?a2))) 
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
    
    
(defrule CAPABILITIES::ice-lidar-sensitivity-through-optically-thin-clouds
        ?i <- (CAPABILITIES::Manifested-instrument (sensitivity-in-cirrus nil) (spectral-bands $?sb) (Field-of-view# ?fov&~nil))
        (or 
            (and (test (subsetp (create$ opt-NIR-1064nm) $?sb)) (test (< ?fov 400)))
            (test (subsetp (create$ opt-nir-532nm) $?sb))
            );; less than 400 urad)
        =>
        (modify ?i (sensitivity-in-cirrus High))
        
        )

;(require* modules "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\modules.clp")
;(require* templates "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\templates.clp")
;(require* more-templates "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\more_templates.clp")
;(require* functions "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\functions.clp")

; performance rule
(deffunction sufficient-performance (?lv ?m ?typ ?h ?i)
    ;(printout t "sufficient performance " ?lv " m=" ?m " orb=" ?typ " h=" ?a " i=" ?i)
    (bind ?margin 1.1); 10% margin
    (bind ?perf (get-performance ?lv ?typ ?h ?i))
    (return (> ?perf (* ?margin ?m)))
    )


(defrule LV-SELECTION::compute-number-of-launches
    "Compute number of launches needed for a certain constellation on a certain launcher"
    
      ?f <-  (MANIFEST::Mission (id ?sat) (launch-vehicle ?lv&~nil) (num-launches nil)
        (num-of-planes# ?np&~nil) (num-of-sats-per-plane# ?ns&~nil) (satellite-wet-mass ?m&~nil) (satellite-dimensions $?dim) 
        (orbit-type ?orb&~nil) (orbit-semimajor-axis ?a&~nil) (orbit-eccentricity ?e&~nil)
         (orbit-inclination ?i&~nil))
    =>
    (bind ?N (* ?np ?ns))
    (bind ?perf (get-performance ?lv ?orb (to-km (r-to-h ?a)) ?i))
    ;(printout t "the perf of lv " ?lv " for orbit "  ?orb " a = " ?a " i = " ?i " is " ?perf crlf)
    (bind ?NL-mass (ceil (/ (* ?m ?N) ?perf)))
    (bind ?NL-vol (ceil (/ (* (*$ $?dim) ?N) (*$ (get-launch-fairing-dimensions ?lv)))))
    ;(printout t "the dimensoins of lv " ?lv " are diam "  (nth$ 1 (get-launch-fairing-dimensions ?lv)) " h = " (nth$ 2 (get-launch-fairing-dimensions ?lv)) crlf)
    (bind ?NL-diam (ceil (/ (* (max$ ?dim) ?N) (max$ (get-launch-fairing-dimensions ?lv)))))
    (modify ?f (num-launches (max ?NL-mass ?NL-vol ?NL-diam)))
    )

(defrule LV-SELECTION::compute-constellation-launch-cost
    "This rule computes launch cost as the product of number of launches and 
    the cost of a single launch."
    
    ?f <- (MANIFEST::Mission (launch-vehicle ?lv&~nil) (num-launches ?num&~nil)
        (launch-cost# nil))
    =>
    
    (modify ?f (launch-cost# (* ?num (get-launch-cost ?lv))))
    )


(deffunction large-enough-height (?lv ?dim); ?dim = (max-diam area height)
    ;(printout t "large-enough-diameter " ?lv ?dim crlf )
    (bind ?fairing-dimensions (get-launch-fairing-dimensions ?lv)); (diam height)
    (bind ?diam (nth$ 1 ?dim))
    (if (eq ?diam nil) then (return 0) else
        ;(printout t "large-enough-height " ?lv " with dimensions " ?fairing-dimensions " sat-diameter " ?diam " " crlf)
        ;(printout t " max fairing dims " (max$ ?fairing-dimensions) " (* 0.8 ?diam) = " (* 0.8 ?diam) crlf)
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




; cost rule
(defrule LV-SELECTION::eliminate-more-expensive-launch-options
    "From all feasible options, eliminate the most expensive ones"
    
    (declare (salience -5))
    ?m1 <- (MANIFEST::Mission (Name ?name) (launch-vehicle ?lv1&~nil) (launch-cost# ?c1&~nil))
    (MANIFEST::Mission (Name ?name) (launch-vehicle ?lv2&~?lv1&~nil) (launch-cost# ?c2&~nil&:(<= ?c2 ?c1)))

        =>
    
	 (retract ?m1) 
    )




;(require* templates "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\templates.clp")
;(require* more-templates "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\more_templates.clp")
;(require* functions "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\functions.clp")


; ********************
; Ground stations cost (4 rules)
; ********************
(deffunction get-facility-adjustment-factor (?loc) 
    "This rule estimates the adjustment factor for the calculation of the non-recurring cost
    based on the location of the ground station, as suggested in the Air Force Facility Cost Handbook
    Table with data can be found in SMAD new page 307"
    
    (if (eq ?loc "Las Cruces, NM") then (return 1.00))
    (if (eq ?loc "Cape Canaveral, FL") then (return 0.97))
    (if (eq ?loc "JPL, CA") then (return 1.13))
    (if (eq ?loc "Marshall Center, AL") then (return 0.85))
    (if (eq ?loc "Hawaii") then (return 1.70))
    (if (eq ?loc "Germany") then (return 1.22))
    (if (eq ?loc "Japan") then (return 1.62))
    (if (eq ?loc "Australia") then (return 1.30))
    (if (eq ?loc "Guam") then (return 2.64))
    )

(defrule COST-ESTIMATION::compute-adjustment-factor
    "This rule estimates the adjustment factor for the calculation of the non-recurring cost
    based on the location of the ground station, as suggested in the Air Force Facility Cost Handbook
    Table with data can be found in SMAD new page 307"
    
    ?gs <- (MANIFEST::GROUND-STATION (adjustment-factor nil) (location ?loc&~nil))
    =>
    (modify ?gs (adjustment-factor (get-facility-adjustment-factor ?loc)))
    )

(defrule COST-ESTIMATION::compute-ground-non-recurring-cost
    "This rule estimates the non-recurring cost of a facility based on its surface area
    and the adjustment factor as suggested in the Air Force Facility Cost Handbook and 
    new SMAD page 307"
    
    ?gs <- (MANIFEST::GROUND-STATION (non-recurring-cost nil) (adjustment-factor ?fac&~nil) (area-m2 ?A&~nil))
    =>
    (bind ?perm2 6.471e-3); 6.471e-3$M per m2, from Air Force Facility Cost Handbook and new SMAD page 307
    (modify ?gs (non-recurring-cost (* ?fac ?A ?perm2))); cost in 2010$M
    )

(defrule COST-ESTIMATION::compute-ground-recurring-cost
    "This rule estimates the recurring cost of a facility based on its personnel"
    
    ?gs <- (MANIFEST::GROUND-STATION (recurring-cost nil) (personnel ?P&~nil) (lifetime ?yrs&~nil))
    =>
    (bind ?salary 0.08); 0.080M$/yr/man
    (modify ?gs (recurring-cost (* ?salary ?P ?yrs))); in 2010$M
    )

(defrule COST-ESTIMATION::compute-ground-lifecycle-cost
    "This rule estimates the lifecycle cost of a facility"
    
    ?gs <- (MANIFEST::GROUND-STATION (recurring-cost ?rc&~nil) (non-recurring-cost ?nrc&~nil)
        (lifecycle-cost nil) )
    =>
    (modify ?gs (lifecycle-cost  (+ ?rc ?nrc))); in 2010$M
    )

(deffunction get-ground-station-cost# (?gs) 
    (bind ?res (run-query* COST-ESTIMATION::get-gs-cost ?gs))
    (if (?res next) then (return (?res getDouble c)) else (return 0.0))
    )


(deffunction get-ground-station-non-rec-cost# (?gs) 
    (bind ?res (run-query* COST-ESTIMATION::get-gs-cost ?gs))
    (if (?res next) then (return (?res getDouble nrc)) else (return 0.0))
    )

(deffunction get-ground-station-rec-cost# (?gs)
    (bind ?res (run-query* COST-ESTIMATION::get-gs-cost ?gs))
    (if (?res next) then (return (?res getDouble rc)) else (return 0.0))
    )

(deffunction get-bus-cost (?bus)
    (if (eq ?bus T330-class) then (return 140000)
        elif (eq ?bus BCP2000-class) then (return 40000)
        elif (eq ?bus Pegastar-class) then (return 15000)
        else (return 1000000)        
        )
    )



(deffunction enough-mass (?bus ?m)
    (if (eq ?bus Pegastar-class) then (if (< ?m 70) then (return TRUE) else (return FALSE)))
    (if (eq ?bus BCP2000-class) then (if (< ?m 300) then (return TRUE) else (return FALSE)))
    (if (eq ?bus T330-class) then (if (< ?m 1300) then (return TRUE) else (return FALSE)))
    (if (eq ?bus dedicated-class) then (return TRUE)) (return FALSE)
    )

(deffunction enough-power (?bus ?p)
    (if (eq ?bus Pegastar-class) then (if (< ?p 70) then (return TRUE) else (return FALSE)))
    (if (eq ?bus BCP2000-class) then (if (< ?p 300) then (return TRUE) else (return FALSE)))
    (if (eq ?bus T330-class) then (if (< ?p 1300) then (return TRUE) else (return FALSE)))
    (if (eq ?bus dedicated-class) then (return TRUE)) (return FALSE)
    )


(defrule BUS-SELECTION::standard-bus-not-enough-payload-mass
    "Eliminate options for which performance is not sufficient with margin"
    ?f <- (MANIFEST::Mission (Name ?miss) (standard-bus ?bus&~nil) (payload-mass# ?m&~nil) 
        (payload-power# ?p&~nil) (payload-data-rate# ?h&~nil) (payload-dimensions# $?dim))
    
    (test (neq (enough-mass ?bus ?m) TRUE));
    =>
    ;(printout t "Insufficient mass capacity of " ?bus " for mission " ?miss crlf)
    (retract ?f)
    )

(defrule BUS-SELECTION::standard-bus-not-enough-payload-power
    "Eliminate options for which performance is not sufficient with margin"
    ?f <- (MANIFEST::Mission (Name ?miss) (standard-bus ?bus&~nil) (payload-mass# ?m&~nil) 
        (payload-power# ?p&~nil) (payload-data-rate# ?h&~nil) (payload-dimensions# $?dim))
    
    (test (neq (enough-power ?bus ?p) TRUE));
    =>
    ;(printout t (enough-power ?bus ?p) " Insufficient power capacity of " ?bus " for mission " ?miss crlf)
    (retract ?f)
    )

(defrule BUS-SELECTION::eliminate-more-expensive-buses
    "From all feasible options, eliminate the most expensive ones"
    (declare (salience -5))
    ?m1 <- (MANIFEST::Mission (Name ?name) (standard-bus ?lv1&~nil) (bus-cost# ?c1&~nil))
    ?m2 <- (MANIFEST::Mission (Name ?name) (standard-bus ?lv2&~nil) (bus-cost# ?c2&~nil))
    (test (neq ?lv1 ?lv2))
    =>
    (if (< ?c1 ?c2) then (retract ?m2) 
        else (retract ?m1) )  
    )
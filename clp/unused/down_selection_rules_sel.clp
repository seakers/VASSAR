	;(defmodule DOWN-SELECTION)
;(deftemplate DOWN-SELECTION::MAX-COST (slot max-cost))
;(deftemplate DOWN-SELECTION::MIN-SCIENCE (slot min-science))
;(deftemplate DOWN-SELECTION::MIN-PARETO-RANK (slot min-pareto-rank))
;(deftemplate DOWN-SELECTION::MIN-UTILITY (multislot metrics) (multislot weights) (slot min-utility))

(defrule DOWN-SELECTION::delete-archs-too-expensive
    "Delete all archs with a cost that exceeds the max cost cap"
    (declare (salience 10))
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH  (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) 
        (sequence ?seq) (selected-instruments $?str))
    (DOWN-SELECTION::MAX-COST (max-cost ?max-cost&:(< ?max-cost ?c)))
    =>
    (assert (REASONING::architecture-eliminated (arch-id ?seq) (arch-str (implode$ $?str)) (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) 
            (reason-str delete-archs-too-expensive)))
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-too-little-utility
    "Delete all archs with a utility that does not meet min utility requirements"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH  (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk)
         (sequence ?seq) (selected-instruments $?str))
    (DOWN-SELECTION::MIN-UTILITY (min-utility ?min-utility&:(> ?min-utility ?u)))
    =>
    (assert (REASONING::architecture-eliminated (arch-id ?seq) (arch-str (implode$ $?str)) (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) 
            (reason-str delete-archs-too-little-utility)))
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-not-enough-pareto-ranking
    "Delete all archs with a pareto ranking that does not meet min pareto ranking requirements"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk)
        (sequence ?seq) (selected-instruments $?str))
    (DOWN-SELECTION::MIN-PARETO-RANK (min-pareto-rank ?min-pareto-rank&:(< ?min-pareto-rank ?p)))   
    =>
    (assert (REASONING::architecture-eliminated (arch-id ?seq) (arch-str (implode$ $?str)) (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) 
            (reason-str delete-archs-not-enough-pareto-ranking)))
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-with-too-much-programmatic-risk
    "Delete all archs with a fraction of instruments with low TRL higher than 33%"
     
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk)
         (sequence ?seq) (selected-instruments $?str))
    (DOWN-SELECTION::MAX-PROG-RISK (max-programmatic-risk ?max-risk&:(< ?max-risk ?risk)))
    =>
    (assert (REASONING::architecture-eliminated (arch-id ?seq) (arch-str (implode$ $?str)) (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) 
            (reason-str delete-archs-too-much-programmatic-risk)))
    (retract ?arch)
    
    )

(defrule DOWN-SELECTION::delete-archs-too-little-science
    "Delete all archs with a science that does not meet min science requirements"
    (declare (salience 10))
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk)
        (sequence ?seq) (selected-instruments $?str))
    (DOWN-SELECTION::MIN-SCIENCE (min-science ?min-science&:(> ?min-science ?s)))
    =>
    (retract ?arch)
     (assert (REASONING::architecture-eliminated (arch-id ?seq) (arch-str (implode$ $?str)) (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) 
            (reason-str delete-archs-too-little-science)))
    )

(defrule DOWN-SELECTION::delete-archs-that-dont-fit
    "Delete all archs with a science that does not meet min science requirements"
    (declare (salience 10))
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk)
        (sequence ?seq) (selected-instruments $?str) (fit ?fit))
    (DOWN-SELECTION::MAX-FIT (max-fit ?max-fit&:(< ?max-fit ?fit)))
    =>
    (retract ?arch)
     (assert (REASONING::architecture-eliminated (arch-id ?seq) (fit ?fit) (arch-str (implode$ $?str)) (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) 
            (reason-str delete-archs-that-dont-fit)))
    )
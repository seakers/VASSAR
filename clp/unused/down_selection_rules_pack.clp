;(defmodule DOWN-SELECTION)
;(deftemplate DOWN-SELECTION::MAX-COST (slot max-cost))
;(deftemplate DOWN-SELECTION::MIN-SCIENCE (slot min-science))
;(deftemplate DOWN-SELECTION::MIN-PARETO-RANK (slot min-pareto-rank))
;(deftemplate DOWN-SELECTION::MIN-UTILITY (multislot metrics) (multislot weights) (slot min-utility))

(defrule DOWN-SELECTION::delete-archs-too-expensive
    "Delete all archs with a cost that exceeds the max cost cap"
    (declare (salience 10))
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH  (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) 
        (assignments $?seq) (str $?str))
    (DOWN-SELECTION::MAX-COST (max-cost ?max-cost&:(< ?max-cost ?c)))
    =>
    (assert (REASONING::architecture-eliminated  (arch-str $?str) (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) 
            (reason-str delete-archs-too-expensive)))
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-too-little-utility
    "Delete all archs with a utility that does not meet min utility requirements"
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH  (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk)
         (assignments $?seq) (str $?str))
    (DOWN-SELECTION::MIN-UTILITY (min-utility ?min-utility&:(> ?min-utility ?u)))
    =>
    (assert (REASONING::architecture-eliminated  (arch-str $?str) (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) 
            (reason-str delete-archs-too-little-utility)))
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-not-enough-pareto-ranking
    "Delete all archs with a pareto ranking that does not meet min pareto ranking requirements"
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk)
        (assignments $?seq) (str $?str))
    (DOWN-SELECTION::MIN-PARETO-RANK (min-pareto-rank ?min-pareto-rank&:(< ?min-pareto-rank ?p)))   
    =>
    (assert (REASONING::architecture-eliminated  (arch-str $?str) (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) 
            (reason-str delete-archs-not-enough-pareto-ranking)))
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-with-too-much-programmatic-risk
    "Delete all archs with a fraction of instruments with low TRL higher than 33%"
     
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk)
         (assignments $?seq) (str $?str))
    (DOWN-SELECTION::MAX-PROG-RISK (max-programmatic-risk ?max-risk&:(< ?max-risk ?risk)))
    =>
    (assert (REASONING::architecture-eliminated  (arch-str $?str) (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) 
            (reason-str delete-archs-too-much-programmatic-risk)))
    (retract ?arch)
    
    )

(defrule DOWN-SELECTION::delete-archs-too-little-science
    "Delete all archs with a science that does not meet min science requirements"
    (declare (salience 10))
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk)
        (assignments $?seq) (str $?str))
    (DOWN-SELECTION::MIN-SCIENCE (min-science ?min-science&:(> ?min-science ?s)))
    =>
    (retract ?arch)
     (assert (REASONING::architecture-eliminated (arch-str $?str) (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) 
            (reason-str delete-archs-too-little-science)))
    )

(defrule DOWN-SELECTION::delete-archs-too-much-launch-risk
    "Delete all archs with too much launch risk defined as 1 - entropy"
    (declare (salience 10))
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) (launch-risk ?ri)
        (assignments $?seq) (str $?str))
    (DOWN-SELECTION::MAX-LAUNCH-RISK (max-launch-risk ?max-launch-risk&:(< ?max-launch-risk ?ri)))
    =>
    (retract ?arch)
     (assert (REASONING::architecture-eliminated (arch-str $?str) (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) (launch-risk ?ri)
            (reason-str delete-archs-too-much-launch-risk)))
    )



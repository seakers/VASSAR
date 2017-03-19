;(defmodule DOWN-SELECTION)
;(deftemplate DOWN-SELECTION::MAX-COST (slot max-cost))
;(deftemplate DOWN-SELECTION::MIN-SCIENCE (slot min-science))
;(deftemplate DOWN-SELECTION::MIN-PARETO-RANK (slot min-pareto-rank))
;(deftemplate DOWN-SELECTION::MIN-UTILITY (multislot metrics) (multislot weights) (slot min-utility))



(defrule DOWN-SELECTION::delete-archs-too-little-discounted-value
    "Delete all archs with a discounted value that does not meet min requirements"
    (declare (salience 10))
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH  (sequence $?seq) (str ?str) (data-continuity ?dc) (discounted-value ?dv) (utility ?u) (fairness ?f) (pareto-ranking ?p))
    (DOWN-SELECTION::MIN-DISCOUNTED-VALUE (min-discounted-value ?min-dv&:(> ?min-dv ?dv)))
    =>
    (assert (REASONING::architecture-eliminated  (arch-str $?str) (data-continuity ?dc) (discounted-value ?dv) (utility ?u) (fairness ?f)
            (reason-str delete-archs-too-little-discounted-value)))
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-too-little-data-continuity
    "Delete all archs with a data continuity metric that does not meet min requirements"
    (declare (salience 10))
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH  (sequence $?seq) (str ?str) (data-continuity ?dc) (discounted-value ?dv) (utility ?u) (fairness ?f) (pareto-ranking ?p))
    (DOWN-SELECTION::MIN-DATA-CONTINUITY (min-data-continuity ?min-dc&:(> ?min-dc ?dc)))
    =>
    (assert (REASONING::architecture-eliminated  (arch-str $?str) (data-continuity ?dc) (discounted-value ?dv) (utility ?u) (fairness ?f)
            (reason-str delete-archs-too-little-data-continuity)))
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-too-little-utility
    "Delete all archs with a utility that does not meet min utility requirements"
    (declare (salience 5))
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH  (sequence $?seq) (str ?str) (data-continuity ?dc) (discounted-value ?dv) (utility ?u) (fairness ?f) (pareto-ranking ?p))
    (DOWN-SELECTION::MIN-UTILITY (min-utility ?min-utility&:(> ?min-utility ?u)))
    =>
    (assert (REASONING::architecture-eliminated (arch-str $?str) (data-continuity ?dc) (discounted-value ?dv) (utility ?u) (fairness ?f)
            (reason-str delete-archs-too-little-utility)))
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-not-enough-pareto-ranking
    "Delete all archs with a pareto ranking that does not meet min pareto ranking requirements"
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH  (sequence $?seq) (str ?str) (data-continuity ?dc) (discounted-value ?dv) (utility ?u) (fairness ?f) (pareto-ranking ?p))
    (DOWN-SELECTION::MIN-PARETO-RANK (min-pareto-rank ?min-pareto-rank&:(< ?min-pareto-rank ?p)))   
    =>
     (assert (REASONING::architecture-eliminated  (arch-str $?str)  (data-continuity ?dc) (discounted-value ?dv) (utility ?u) (fairness ?f)
            (reason-str delete-archs-not-enough-pareto-ranking)))
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-too-much-unfairness
    "Delete all archs with a pareto ranking that does not meet min pareto ranking requirements"
    (declare (salience 10))
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH  (sequence $?seq) (str ?str) (data-continuity ?dc) (discounted-value ?dv) (utility ?u) (fairness ?f) (pareto-ranking ?p))
    (DOWN-SELECTION::MIN-FAIRNESS (min-fairness ?min-fairness&:(> ?min-fairness ?f)))   
    =>
    (assert (REASONING::architecture-eliminated  (arch-str $?str)  (data-continuity ?dc) (discounted-value ?dv) (utility ?u) (fairness ?f)
            (reason-str delete-archs-too-much-unfairness)))
    (retract ?arch)
    )

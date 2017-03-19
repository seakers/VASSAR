;(defmodule DOWN-SELECTION)
;(deftemplate DOWN-SELECTION::MAX-COST (slot max-cost))
;(deftemplate DOWN-SELECTION::MIN-SCIENCE (slot min-science))
;(deftemplate DOWN-SELECTION::MIN-PARETO-RANK (slot min-pareto-rank))
;(deftemplate DOWN-SELECTION::MIN-UTILITY (multislot metrics) (multislot weights) (slot min-utility))

(defrule DOWN-SELECTION::delete-archs-too-expensive
    "Delete all archs with a cost that exceeds the max cost cap"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH  (cost ?c))
    (DOWN-SELECTION::MAX-COST (max-cost ?max-cost&:(< ?max-cost ?c)))
    =>
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-too-little-science
    "Delete all archs with a science that does not meet min science requirements"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (science ?s))
    (DOWN-SELECTION::MIN-SCIENCE (min-science ?min-science&:(> ?min-science ?s)))
    =>
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-too-little-utility
    "Delete all archs with a utility that does not meet min utility requirements"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH  (utility ?u))
    (DOWN-SELECTION::MIN-UTILITY (min-utility ?min-utility&:(> ?min-utility ?u)))
    =>
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-not-enough-pareto-ranking
    "Delete all archs with a pareto ranking that does not meet min pareto ranking requirements"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (pareto-ranking ?p))
    (DOWN-SELECTION::MIN-PARETO-RANK (min-pareto-rank ?min-pareto-rank&:(< ?min-pareto-rank ?p)))   
    =>
    (retract ?arch)
    )

(defrule DOWN-SELECTION::delete-archs-with-too-much-programmatic-risk
    "Delete all archs with a fraction of instruments with low TRL higher than 33%"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (programmatic-risk ?risk))
    (DOWN-SELECTION::MAX-PROG-RISK (max-programmatic-risk ?max-risk&:(< ?max-risk ?risk)))
    =>
    (retract ?arch)
    
    )


(defrule CRITIQUE-COST::mass-check "limits the dry-mass of a satellite"
	(MANIFEST::Mission (Name ?n)(satellite-dry-mass ?sdm&:(> ?sdm 4000)))
	=>
	(printout t "NEGATIVE>> Satellite at " ?n " is too heavy: dry-mass " ?sdm " kg" crlf))
	
	
(defrule CRITIQUE-COST::satellite-size-comparison
	(CRITIQUE-COST-PARAM::satellite-max-size-ratio (value ?r&:(> ?r 2.5)) (big-name ?bn) (small-name ?sn))
	=>
	(printout t "NEGATIVE>> Satellites do not have similar sizes: satellite flying in " ?bn " is larger than the one in " ?sn " by a factor of " ?r crlf)
)

(defrule CRITIQUE-COST::satellite-cost-comparison
	(CRITIQUE-COST-PARAM::satellite-max-cost-ratio (value ?r&:(> ?r 2.5))(big-name ?bn) (small-name ?sn))
	=>
	(printout t "NEGATIVE>> Satellites do not have similar costs: satellite flying in " ?bn " costs more than the one in " ?sn " by a factor of " ?r crlf)
)
	
(defrule CRITIQUE-COST::launch-packaging-factors
	(CRITIQUE-COST-PARAM::launch-packaging-factors (name ?n)(performance-mass-ratio ?r-pm) (diameter-ratio ?r-dia) (height-ratio ?r-h))
	=>
	(bind ?m (min$ (create$ (bind ?f1 (- 1 ?r-pm)) (bind ?f2 (- 1 ?r-dia)) (bind ?f3 (- 1 ?r-h)))))
	(if (= ?m ?f1) then (bind ?lf "mass"))
	(if (= ?m ?f2) then (bind ?lf "diameter"))
	(if (= ?m ?f3) then (bind ?lf "height"))
	(if (> ?m 0.2)
	then (printout t "NEGATIVE>> The limiting factor among launch-packaging ratios of " ?n " is " ?lf ": "?m  crlf)))
	


(defrule CRITIQUE-PERFORMANCE::active-instr-in-orbit-DD
	(CAPABILITIES::can-measure (in-orbit ?o)(instrument ?instr) (orbit-RAAN ~DD))
	(CRITIQUE-PERFORMANCE-PARAM::list-of-active-instruments (list $?l))
	=>
	(if (numberp (member$ ?instr $?l))
		then(printout t "NEGATIVE>> Active instrument " ?instr " is not in orbit DD (" ?o ")" crlf)))
	
(defrule CRITIQUE-PERFORMANCE::two-lidars-at-same-frequency-cannot-work
    "Two lidars at same frequency can interfere with each other"
    (CAPABILITIES::can-measure (in-orbit ?o)(instrument ?ins1) (can-take-measurements yes))
    (CAPABILITIES::can-measure (in-orbit ?o)(instrument ?ins2&~?ins1) (can-take-measurements yes))
    (DATABASE::Instrument (Name ?ins1) (Intent "Laser altimeters") (spectral-bands $?sr))
    (DATABASE::Instrument (Name ?ins2) (Intent "Laser altimeters") (spectral-bands $?sr))
    =>
    (printout t "NEGATIVE>> two lidars working at same frequency: " ?o " " ?ins1 " " ?ins2 " at "$?sr crlf)
)
	

(defrule CRITIQUE-PERFORMANCE::num-of-instruments
	(CRITIQUE-PERFORMANCE-PARAM::total-num-of-instruments (value ?v&:(> ?v 14)))
	=>
	(printout t "NEGATIVE>> Too many instruments total: "?v crlf))

	
(defrule CRITIQUE-PERFORMANCE::resource-limitations-datarate
    (MANIFEST::Mission  (Name ?miss) (datarate-duty-cycle# ?dc&:(< ?dc 1.0)))
    =>
    (printout t "NEGATIVE>> Cumulative spacecraft data rate cannot be downloaded to ground stations: " ?miss " dc = " ?dc crlf))
	
(defrule CRITIQUE-PERFORMANCE::resource-limitations-power
    (MANIFEST::Mission (Name ?miss) (power-duty-cycle# ?dc&:(< ?dc 1.0)))
    =>
	(printout t "NEGATIVE>> Cumulative spacecraft power exceeds 10kW: " ?miss " dc = " ?dc crlf))
	
(defrule CRITIQUE-PERFORMANCE::fairness-check
	(CRITIQUE-PERFORMANCE-PARAM::fairness (flag 1) (value ?v)(stake-holder1 ?sh1) (stake-holder2 ?sh2))
	=>
	(printout t "NEGATIVE>> Satisfaction value for stakeholder " ?sh1 " is larger than " ?sh2 " (difference: " ?v ")" crlf))
	


	
	
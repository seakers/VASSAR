(defrule SEARCH-HEURISTICS::remove-superfluous-instrument
    "This heuristic finds an existing superfluous instruments and removes it" 
    ?arch0 <- (MANIFEST::ARCHITECTURE (bitString ?orig) (num-sats-per-plane ?ns) (improve removeSuperfluous))
    =>
	;(printout t remove-existing-interference crlf)
    (bind ?N 1)
    (for (bind ?i 0) (< ?i ?N) (++ ?i) 
		(bind ?arch ((new rbsa.eoss.Architecture ?orig ?ns) removeSuperfluous))
    	(assert-string (?arch toFactString)))
	(modify ?arch0 (improve no))
    )
	 
(deffacts DATABASE::add-remove-superfluous-list-of-improve-heuristics
(SEARCH-HEURISTICS::improve-heuristic (id removeSuperfluous))
)


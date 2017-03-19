(defrule SEARCH-HEURISTICS::remove-random-instrument-from-loaded-sat
    "This heuristic removes a random instrument from an existing loaded satellite" 
    ?arch0 <- (MANIFEST::ARCHITECTURE (bitString ?orig) (num-sats-per-plane ?ns) (improve removeRandomFromLoadedSat))
    =>
	;(printout t remove-existing-interference crlf)
    (bind ?N 1)
    (for (bind ?i 0) (< ?i ?N) (++ ?i) 
		(bind ?arch ((new rbsa.eoss.Architecture ?orig ?ns) removeRandomFromLoadedSat))
    	(assert-string (?arch toFactString)))
	(modify ?arch0 (improve no))
    )
	 
(deffacts DATABASE::add-remove-random-instrument-from-loaded-sat-list-of-improve-heuristics
(SEARCH-HEURISTICS::improve-heuristic (id removeRandomFromLoadedSat))
)


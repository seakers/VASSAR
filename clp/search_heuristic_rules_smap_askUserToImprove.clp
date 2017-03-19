(defrule SEARCH-HEURISTICS::improve-ask-user-to-improve
    "This heuristic simply asks the user to improve the current architecture" 
    ?arch0 <- (MANIFEST::ARCHITECTURE (bitString ?orig) (num-sats-per-plane ?ns) (improve askUserToImprove))
    =>
	;(printout t bestNeighbor crlf)
    (bind ?N 1)
    (for (bind ?i 0) (< ?i ?N) (++ ?i) 
		(bind ?arch ((new rbsa.eoss.Architecture ?orig ?ns) askUserToImprove))
    	(assert-string (?arch toFactString)))
	(modify ?arch0 (improve no))
    )
	 
(deffacts DATABASE::add-improve-askUserToImprove-list-of-improve-heuristics
(SEARCH-HEURISTICS::improve-heuristic (id askUserToImprove))
)


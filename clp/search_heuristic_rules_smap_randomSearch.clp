(defrule SEARCH-HEURISTICS::improve-random-search
    "This mutation function swaps the value of a single bit" 
    ?arch0 <- (MANIFEST::ARCHITECTURE (bitString ?orig) (num-sats-per-plane ?ns) (improve randomSearch))
    =>
	;(printout t improve-random-search crlf)
    (bind ?N 1)
    (for (bind ?i 0) (< ?i ?N) (++ ?i)
	    
		(bind ?arch ((new rbsa.eoss.Architecture ?orig ?ns) randomSearch))
    	(assert-string (?arch toFactString)))
	(modify ?arch0 (improve no))
    )
	
(deffacts DATABASE::add-random-search-list-of-improve-heuristics
(SEARCH-HEURISTICS::improve-heuristic (id randomSearch))
)


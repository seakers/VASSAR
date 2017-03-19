(defrule SEARCH-HEURISTICS::remove-existing-interference
    "This heuristic finds an existing interference between instruments and removes the necessary instrument" 
    ?arch0 <- (MANIFEST::ARCHITECTURE (bitString ?orig) (num-sats-per-plane ?ns) (improve removeInterference))
    =>
	;(printout t remove-existing-interference crlf)
    (bind ?N 1)
    (for (bind ?i 0) (< ?i ?N) (++ ?i) 
		(bind ?arch ((new rbsa.eoss.Architecture ?orig ?ns) removeInterference))
    	(assert-string (?arch toFactString)))
	(modify ?arch0 (improve no))
    )
	 
(deffacts DATABASE::add-remove-interf-list-of-improve-heuristics
(SEARCH-HEURISTICS::improve-heuristic (id removeInterference))
)


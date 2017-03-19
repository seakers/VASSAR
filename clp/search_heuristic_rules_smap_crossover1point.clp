	 
(defrule SEARCH-HEURISTICS::crossover-one-point
    "This mutation performs crossover on one point" 
    ?arch1 <- (MANIFEST::ARCHITECTURE (bitString ?orig) (num-sats-per-plane ?ns) (improve crossover1point))
	?arch2 <- (MANIFEST::ARCHITECTURE (bitString ?orig2&~?orig)(num-sats-per-plane ?ns2) (improve crossover1point))
    => 
	;(printout t crossover-one-point crlf)
    (bind ?N 1)
    (for (bind ?i 0) (< ?i ?N) (++ ?i)   
		(bind ?arch3 ((new rbsa.eoss.Architecture ?orig ?ns) crossover1point (new rbsa.eoss.Architecture ?orig2 ?ns2)))
    	(assert-string (?arch3 toFactString))
		(bind ?arch4 ((new rbsa.eoss.Architecture ?orig2 ?ns2) crossover1point (new rbsa.eoss.Architecture ?orig ?ns)))
    	(assert-string (?arch4 toFactString))
		)
	(modify ?arch1 (improve no))
	(modify ?arch2 (improve no))
    ) 
	
(deffacts DATABASE::add-crossover-list-of-improve-heuristics
(SEARCH-HEURISTICS::improve-heuristic (id crossover1point))
)


(defrule DATABASE::create-list-of-improve-heuristics
	?ret <- (SEARCH-HEURISTICS::improve-heuristic (id ?id))
	?f <- (SEARCH-HEURISTICS::list-improve-heuristics (list $?list) (num-heuristics ?n))
	=>
	(bind ?new-list (append$ $?list ?id))
	(modify ?f (list ?new-list) (num-heuristics (+ ?n 1)))
	(retract ?ret)
)
(defrule SEARCH-HEURISTICS::mutation-swap-one-bit 
    "This mutation function swaps the value of a single bit" 
	(declare (salience 100))
    ?arch0 <- (MANIFEST::ARCHITECTURE (bitString ?orig) (num-sats-per-plane ?ns) (mutate yes))
    =>
	;(printout t mutation-swap-one-bit crlf)
    (bind ?N 1)
    (for (bind ?i 0) (< ?i ?N) (++ ?i)
	    
		(bind ?arch ((new rbsa.eoss.Architecture ?orig ?ns) mutate1bit))
    	(assert-string (?arch toFactString)))
	(retract ?arch0)
    )
(deffacts DATABASE::assert-empty-list-of-improve-heuristics
(SEARCH-HEURISTICS::list-improve-heuristics (list (create$ )) (num-heuristics 0))
)


	 
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


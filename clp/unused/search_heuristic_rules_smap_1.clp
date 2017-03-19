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
	    
		(bind ?arch ((new rbsa.eoss.Architecture ?arch0 ?ns) mutate1bit))
    	(assert-string (?arch toFactString)))
	(retract ?arch0)
    )
(deffacts DATABASE::assert-empty-list-of-improve-heuristics
(SEARCH-HEURISTICS::list-improve-heuristics (list (create$ )) (num-heuristics 0))
)


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


(defrule SEARCH-HEURISTICS::add-random-instrument-to-small-sat
    "This heuristic finds an existing small satellite and adds a random instrument" 
    ?arch0 <- (MANIFEST::ARCHITECTURE (bitString ?orig) (num-sats-per-plane ?ns) (heuristics-to-apply $? addRandomToSmallSat $?) (heuristics-applied $?applied&:(not-contains$ addRandomToSmallSat $?applied)))
    =>
	;(printout t add-random-instrument-to-small-sat crlf)
    (bind ?N 1)
    (for (bind ?i 0) (< ?i ?N) (++ ?i) 
		(bind ?arch ((new rbsa.eoss.Architecture ?orig ?ns) addRandomToSmallSat))
    	(assert-string (?arch toFactString)))
	(modify ?arch0 (heuristics-applied (append$ ?applied addRandomToSmallSat)))
    )
	 
(deffacts DATABASE::add-random-instrument-to-small-sat-list-of-improve-heuristics
(SEARCH-HEURISTICS::improve-heuristic (id addRandomToSmallSat))
)


(deftemplate arch (slot bitString) (multislot heuristics-to-apply) (multislot heuristics-applied) (slot id))

(deffunction append$ (?list ?el)
(return (insert$ ?list (+ 1 (length$ ?list)) ?el)))
(deffunction not-contains$ (?el ?list)
    (return (not (numberp (member$ ?el ?list))))
    )
	
(deffacts archs 
	(arch (id 1) (bitString 010101) (heuristics-to-apply h1 h2))
	(arch (id 2) (bitString 000001) (heuristics-to-apply h2 h3))
	(arch (id 3) (bitString 111000) (heuristics-to-apply h3))
	(arch (id 4) (bitString 100001) (heuristics-to-apply h1 h2 h3)))
	
(defrule h1 "Puts all to ones"
	?a<- (arch (id ?id) (bitString ?bit) (heuristics-to-apply $? h1 $?) (heuristics-applied $?applied&:(not-contains$ h1 $?applied)))
	=>
	(duplicate ?a (id (str-cat ?id -h1)) (bitString 111111) (heuristics-applied (append$ ?applied h1))))

(defrule h2 "Puts all to twos"
	?a<- (arch (id ?id) (bitString ?bit) (heuristics-to-apply $? h2 $?) (heuristics-applied $?applied&:(not-contains$ h2 $?applied)))
	=>
	(duplicate ?a (id (str-cat ?id -h2)) (bitString 222222) (heuristics-applied (append$ ?applied h2))))
	
(defrule h3 "Puts all to threes"
	?a <- (arch (id ?id) (bitString ?bit) (heuristics-to-apply $? h3 $?) (heuristics-applied $?applied&:(not-contains$ h3 $?applied)))
	=>
	(duplicate ?a (id (str-cat ?id -h3)) (bitString 333333) (heuristics-applied (append$ ?applied h3))))

(reset)
(watch all)
(run)
(facts)
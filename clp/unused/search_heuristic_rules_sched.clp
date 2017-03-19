; search_heuristic_rules_sched.clp

(deffunction rand-int-1-to-N (?N)
    (bind ?x (+ 1 (round (* (- ?N 1) (/ (random) 65536)))))
    )

(deffunction mutate-two-positions-in-sequence (?seq)
    (bind ?N (length$ ?seq))
    (bind ?pos1 (rand-int-1-to-N ?N))
    (bind ?ok FALSE)
    (while (eq ?ok FALSE) (bind ?pos2 (rand-int-1-to-N ?N)) (bind ?ok (neq ?pos1 ?pos2)))
    
    (bind ?tmp (nth$ ?pos1 ?seq))
    (bind ?seq (replace$ ?seq ?pos1 ?pos1 (nth$ ?pos2 ?seq)))
    (bind ?seq (replace$ ?seq ?pos2 ?pos2 ?tmp))
    (return ?seq)
    )


(defrule SEARCH-HEURISTICS::mutation-swap-two-positions
    "This mutation function swaps the value of a single bit"
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence ?orig) (mutate yes))
    =>
    (bind ?N 10)
    (for (bind ?i 0) (< ?i ?N) (++ ?i)
	    (bind ?new-seq (mutate-two-positions-in-sequence ?orig))
	    (bind ?new-str (explode$ (matlabf SCHED_arch_to_str ?new-seq)))
    	(duplicate ?arch (sequence ?new-seq) (mutate no) (str ?new-str))
        (modify ?arch (mutate no)))
    
    )



(deffunction get-sublist-with-indexes (?list ?indexes)
    (bind ?sub (create$ ))
    (foreach ?i ?indexes 
        (bind ?sub (insert$ ?sub (+ 1 (length$ ?sub)) (nth$ ?i ?list)))
    )
    (return ?sub)
    )

(deffunction EPP-crossover (?dad ?mom); dad = (1 4 5 2 3), mom = (2 4 5 1 3)
    (bind ?n (length$ ?dad))
    (bind ?m (round (/ ?n 2)))
    (bind ?child (subseq$ ?dad 1 ?m)); first half of sequence from dad: (1 4 5)
    (for (bind ?i 1) (<= ?i ?n) (++ ?i); second half from mom: (2 3)
    (bind ?el (nth$ ?i ?mom))
        (if (not (subsetp (create$ ?el) ?child)) then (bind ?child (insert$ ?child (+ (length$ ?child) 1) ?el))))
    (return ?child)
    )

(defrule SEARCH-HEURISTICS::EPP-crossover-operator
    "This rule performs a crossover between two individuals of an EPP population
    "
     ?dad <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence ?seq-dad) (improve yes))
     ?mom <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence ?seq-mom) (improve yes))
    =>
    (bind ?new-seq (EPP-crossover ?seq-dad ?seq-mom)); 
    (assert HARD-CONSTRAINTS::PERMUTING-ARCH (sequence ?new-seq) (improve no) (str (matlabf get_str_from_arch SCHEDULING ?new-seq)))
    (modify ?dad (improve no))
    (modify ?mom (improve no))
    )

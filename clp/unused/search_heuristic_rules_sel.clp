(defrule SEARCH-HEURISTICS::mutation-swap-one-bit
    "This mutation function swaps the value of a single bit"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (sequence ?orig) (mutate yes))
    =>
    (bind ?N 10)
    (for (bind ?i 0) (< ?i ?N) (++ ?i)
	    (bind ?new-seq (matlabf mutate_one_bit ?orig))
	    (bind ?new-instr (explode$ (matlabf get_instr_from_seq ?new-seq)))
    	(duplicate ?arch (sequence ?new-seq) (mutate no) (selected-instruments ?new-instr))
        (modify ?arch (mutate no)))
    
    )

(defrule SEARCH-HEURISTICS::improve-by-completing-architecture
    "This rule identifies the shortcomings of an architecture and 
    modifies it by adding an instrument that covers some of the gaps
    "
     ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (sequence ?orig) (improve yes))
    =>
    (bind ?new-seq (matlabf complete_arch ?orig)); this is an ArrayList of objectives
    (bind ?new-instr (explode$ (matlabf get_instr_from_seq ?new-seq)))
    (if (neq ?new-seq ?orig) then (duplicate ?arch (sequence ?new-seq) (improve no) (selected-instruments ?new-instr)))
    (modify ?arch (improve no))
    )

(defrule SEARCH-HEURISTICS::improve-by-removing-redundancy
    "This rule identifies the potential redundancies in an architecture and eliminates
    one of them"
    
     ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (sequence ?orig) (improve yes))
    =>
    (bind ?new-seq (matlabf remove_redundancy_from_arch ?orig)); this is an ArrayList of objectives
    (bind ?new-instr (explode$ (matlabf get_instr_from_seq ?new-seq)))
    (duplicate ?arch (sequence ?new-seq) (improve no) (selected-instruments ?new-instr))
    (modify ?arch (improve no))
    )



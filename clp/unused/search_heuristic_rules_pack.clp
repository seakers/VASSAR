(defrule SEARCH-HEURISTICS::mutation-swap-one-instrument
    "This mutation function swaps the position of one instrument"
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (assignments $?ass) (mutate yes))
    =>
    (bind ?N 5)
    (for (bind ?i 0) (< ?i ?N) (++ ?i)
	    (bind ?new-arch (matlabf mutate_swap_one_instrument ?ass))
    (bind ?new-str (matlabf get_str_from_arch PACKAGING ?new-arch))
    (duplicate ?arch (assignments ?new-arch) (mutate no) (str ?new-str)))
    
    )

(defrule SEARCH-HEURISTICS::mutation-break-one-big-satellite
    "This mutation function breaks one big random satellite"
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (assignments $?ass) (mutate yes))
    =>
    (bind ?N 5)
    (for (bind ?i 0) (< ?i ?N) (++ ?i)
    (bind ?new-arch (matlabf mutate_break_big_sat ?ass))
    (bind ?new-str (matlabf get_str_from_arch PACKAGING ?new-arch))
    (duplicate ?arch (assignments ?new-arch) (mutate no) (str ?new-str)))
    
    )

(defrule SEARCH-HEURISTICS::mutation-combine-two-small-satellites
    "This mutation function combines two small random satellites"
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (assignments $?ass) (mutate yes))
    =>
    (bind ?N 5)
    (for (bind ?i 0) (< ?i ?N) (++ ?i)
    (bind ?new-arch (matlabf mutate_combine_small_sats ?ass))
    (bind ?new-str (matlabf get_str_from_arch PACKAGING ?new-arch))
    (duplicate ?arch (assignments ?new-arch) (mutate no) (str ?new-str)))
    
    )

(defrule SEARCH-HEURISTICS::mutation-swaps-two-instruments
    "This mutation function swaps the positions of two instruments"
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (assignments $?ass) (mutate yes))
    =>
    (bind ?N 5)
    (for (bind ?i 0) (< ?i ?N) (++ ?i)
    (bind ?new-arch (matlabf mutate_swap_two_instruments ?ass))
    (bind ?new-str (matlabf get_str_from_arch PACKAGING ?new-arch))
    (duplicate ?arch (assignments ?new-arch) (mutate no) (str ?new-str)))
    
    )

(defrule SEARCH-HEURISTICS::improve-by-adding-synergy
    "This heuristic search rule improves a packaging architecture by identifying 
    a synergy that is not captured and rearranging the assignments so that the 
    synergy is captured"
    
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (assignments $?ass) (improve yes))
    =>
    (bind ?N 5)
    (for (bind ?i 0) (< ?i ?N) (++ ?i)
	    (bind ?new-arch (matlabf improve_add_synergy ?ass))
    (bind ?new-str (matlabf get_str_from_arch PACKAGING ?new-arch))
    (duplicate ?arch (assignments ?new-arch) (improve no) (str ?new-str)))
          
    )

(defrule SEARCH-HEURISTICS::improve-by-removing-interference
    "This heuristic search rule improves a packaging architecture by identifying 
    an existing interference and removing it"
    
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (assignments $?ass) (improve yes))
    =>
    (bind ?N 5)
    (for (bind ?i 0) (< ?i ?N) (++ ?i)
    (bind ?new-arch (matlabf improve_remove_interference ?ass))
    (bind ?new-str (matlabf get_str_from_arch PACKAGING ?new-arch))
    (duplicate ?arch (assignments ?new-arch) (improve no) (str ?new-str)))
    
    )

(defrule SEARCH-HEURISTICS::improve-by-filling-low-pack-factors
    "This operator locates a satellite with low packaging factor 
    and tries to add an instrument to improve it"
    
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (assignments $?ass) (improve yes) (launch-pack-factors $?pack))
    =>
    (bind ?N 5)
    (for (bind ?i 0) (< ?i ?N) (++ ?i)
    (bind ?new-arch (matlabf improve_fill_low_pack ?ass $?pack))
    (bind ?new-str (matlabf get_str_from_arch PACKAGING ?new-arch))
    (duplicate ?arch (assignments ?new-arch) (improve no) (str ?new-str)))
    
    )

(defrule SEARCH-HEURISTICS::improve-by-splitting-low-pack-factors
    "This mutation function breaks one big random satellite"
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (assignments $?ass) (improve yes) (launch-pack-factors $?pack))
    =>
    (bind ?N 5)
    (for (bind ?i 0) (< ?i ?N) (++ ?i)
    (bind ?new-arch (matlabf improve_split_low_pack ?ass $?pack))
    (bind ?new-str (matlabf get_str_from_arch PACKAGING ?new-arch))
    (duplicate ?arch (assignments ?new-arch) (improve no) (str ?new-str)))
    
    )


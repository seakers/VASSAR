(defquery HARD-CONSTRAINTS::get-max-sats
    
    (HARD-CONSTRAINTS::MAX-SATS (max-sats# ?max))
    )

(defrule HARD-CONSTRAINTS::no-two-max-sats
    (HARD-CONSTRAINTS::MAX-SATS (max-sats# ?max1))
    ?f <- (HARD-CONSTRAINTS::MAX-SATS (max-sats# ?max2&:(> ?max1 ?max2)))
    =>
    (retract ?f)
    )

(defrule HARD-CONSTRAINTS::delete-archs-with-too-many-sats
    (HARD-CONSTRAINTS::MAX-SATS (max-sats# ?mx&~nil))
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (assignments $?ass))
    (test (> (max$ ?ass) ?mx))
    =>
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::delete-archs-with-too-many-instruments-in-one-sat
    (HARD-CONSTRAINTS::MAX-INSTRS-PER-SAT (max-instruments-per-satellite# ?mx&~nil))
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (assignments $?ass))
    (test (> (matlabf get_max_instr_per_sat $?ass) ?mx))
    =>
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::delete-archs-breaking-together-instrument-reqs
    (HARD-CONSTRAINTS::TOGETHER-INSTRUMENTS (instruments $?ins))
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (assignments $?ass))
    (test (matlabf break_together_req ?ins ?ass))
    =>
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::delete-archs-breaking-apart-instrument-reqs
    (HARD-CONSTRAINTS::APART-INSTRUMENTS (instruments $?ins))
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (assignments $?ass))
    (test (matlabf break_apart_req ?ins ?ass))
    =>
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::delete-archs-breaking-alone-instrument-reqs
    (HARD-CONSTRAINTS::ALONE-INSTRUMENTS (instruments $?ins))
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (assignments $?ass))
    (test (matlabf break_alone_req ?ins ?ass))
    =>
    (retract ?arch)
    )

(deffunction violates-orbit-requirements (?instr ?req-orb $?orbs)
    (if (eq (length$ ?orbs) 0) then (return FALSE))
    (if (eq (matlabf check_orbit_reqs ?instr ?req-orb $?orbs) 1.0) then (return FALSE) else (return TRUE))
    )

(defrule HARD-CONSTRAINTS::delete-archs-breaking-hard-orbit-requirements
    ?arch <- (HARD-CONSTRAINTS::PACK-ARCH (science ?s) (cost ?c) (utility ?u) (pareto-ranking ?p) (programmatic-risk ?risk) (launch-risk ?ri)
        (assignments $?seq) (str $?str) (instrument-orbits $?orbs))
    (HARD-CONSTRAINTS::FORCE-ORBIT (of-instrument ?instr) (required-orbit ?req-orb))
    (test (eq (violates-orbit-requirements ?instr ?req-orb $?orbs) TRUE))
    =>
    (retract ?arch)
    
    )
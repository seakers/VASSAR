;(deftemplate FIX-INSTRUMENTS (multislot instruments) (slot mask))
;(deftemplate OR-INSTRUMENTS (multislot instruments) (slot mask))
;(deftemplate SEL-ARCH (multislot selected-instruments) (slot sequence))
;(defmodule HARD-CONSTRAINTS)

(defrule HARD-CONSTRAINTS::compute-fix-instrument-mask
    "This rule computes the mask of a fix-instrument mask from its instruments"
    (declare (salience 10))
    ?f <- (HARD-CONSTRAINTS::FIX-INSTRUMENTS (instruments $?fix) (mask nil))
    =>
    (bind ?mask (matlabf get_mask_from_instruments (implode$ ?fix) jess)) ;; returns an integer
    (modify ?f (mask ?mask))
    )

(defrule HARD-CONSTRAINTS::compute-not-instrument-mask
    "This rule computes the mask of a not-instrument mask from its instruments"
    (declare (salience 10))
    ?f <- (HARD-CONSTRAINTS::NOT-INSTRUMENTS (instruments $?fix) (mask nil))
    =>
    (bind ?mask (matlabf get_mask_from_instruments (implode$ ?fix) jess)) ;; returns an integer
    (modify ?f (mask ?mask))
    )

(defrule HARD-CONSTRAINTS::compute-or-instrument-mask
    "This rule computes the mask of an or-instrument mask from its instruments"
    (declare (salience 10))
    ?f <- (HARD-CONSTRAINTS::OR-INSTRUMENTS (instruments $?or) (mask nil))
    =>
    (bind ?mask (matlabf get_mask_from_instruments (implode$ ?or) jess)) ;; returns an integer
    (modify ?f (mask ?mask))
    )

(defrule HARD-CONSTRAINTS::compute-xor-instrument-mask
    "This rule computes the mask of a xor-instrument mask from its instruments"
    (declare (salience 10))
    ?f <- (HARD-CONSTRAINTS::XOR-INSTRUMENTS (instruments $?xor) (mask nil))
    =>
    (bind ?mask (matlabf get_mask_from_instruments (implode$ ?xor) jess)) ;; returns an integer
    (modify ?f (mask ?mask))
    )

(defrule HARD-CONSTRAINTS::compute-GROUP-instrument-mask
    "This rule computes the mask of a group-instrument mask from its instruments"
    (declare (salience 10))
    ?f <- (HARD-CONSTRAINTS::GROUP-INSTRUMENTS (instruments $?if) (mask nil))
    =>
    (bind ?mask (matlabf get_mask_from_instruments (implode$ ?if) jess)) ;; returns an integer
    ;(printout t "mask = " ?mask crlf)
    (modify ?f (mask ?mask))
    )

(defrule HARD-CONSTRAINTS::compute-SUPPORT-instrument-mask
    "This rule computes the mask of a support-instrument mask from its instruments"
    (declare (salience 10))
    ?f <- (HARD-CONSTRAINTS::GROUP-INSTRUMENTS (instruments $?if) (mask nil))
    =>
    (bind ?mask (matlabf get_mask_from_instruments (implode$ ?if) jess)) ;; returns an integer
    ;(printout t "mask = " ?mask crlf)
    (modify ?f (mask ?mask))
    )


(defrule HARD-CONSTRAINTS::compute-arch-instrument-mask
    "This rule computes the mask of a fix-instrument mask from its instruments"
    (declare (salience 10))
    ?f <- (HARD-CONSTRAINTS::SEL-ARCH (selected-instruments $?ins) (sequence nil))
    =>
    (bind ?seq (matlabf get_mask_from_instruments (implode$ ?ins) jess)) ;; returns an integer
    (modify ?f (sequence ?seq))
    )


(defrule HARD-CONSTRAINTS::delete-archs-without-fix-instruments
    "If an architecture does not respect a fix instrument constraint then it should be deleted"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (sequence ?seq&~nil))
    (HARD-CONSTRAINTS::FIX-INSTRUMENTS (mask ?mask&~nil))
    (test (neq (matlabf bit_and ?mask ?seq) ?mask))
    
    =>
    ;(printout t "seq = " ?seq "mask = " ?mask crlf)
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::modify-archs-with-not-instruments
    "If an architecture contais a not instrument then this instrument should be deleted"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (sequence ?seq&~nil))
    (HARD-CONSTRAINTS::NOT-INSTRUMENTS (mask ?mask&~nil))
    (test (> (matlabf bit_and ?mask ?seq) 0))
    
    =>
    ;(printout t "seq = " ?seq "mask = " ?mask crlf)
    (bind ?new-mask (matlabf bit_not ?mask))
    (bind ?new-seq (matlabf bit_and ?new-mask ?seq))
    (bind ?new-instr (explode$ (matlabf get_instr_from_seq ?new-seq)))
    (modify ?arch (sequence ?new-seq) (selected-instruments ?new-instr))
    )

(defrule HARD-CONSTRAINTS::delete-archs-without-or-instruments
    "If an architecture does not cointain one of the instruments 
    in an or constraint then it should be deleted"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (sequence ?seq&~nil))
    (HARD-CONSTRAINTS::OR-INSTRUMENTS  (mask ?mask&~nil))
    (test (< (matlabf bit_and ?mask ?seq) 1))
    
    =>
    
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::delete-archs-without-xor-instruments
    "If an architecture does not cointain exactly one of the instruments 
    in an xor constraint then it should be deleted"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (sequence ?seq&~nil))
    (HARD-CONSTRAINTS::XOR-INSTRUMENTS  (mask ?mask&~nil))
    (test (neq (matlabf bit_and2 ?mask ?seq) 1.0))
    
    =>
    
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::complete-archs-without-GROUP-instruments
    "If an architecture does not cointain one of the instruments 
    in an or constraint then it should be deleted"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (sequence ?seq&~nil))
    (HARD-CONSTRAINTS::GROUP-INSTRUMENTS  (mask ?mask&~nil))  
    (test (> (matlabf bit_and2 ?mask ?seq) 0)) ;; arch has at least one instrument in group
    (test (neq (matlabf bit_and ?mask ?seq) ?mask)) ;; but not all of them
    =>
    (bind ?new-seq (matlabf bit_or ?mask ?seq))
    (bind ?new-instr (explode$ (matlabf get_instr_from_seq ?new-seq)))
    (modify ?arch (sequence ?new-seq) (selected-instruments ?new-instr))
    )

(defrule HARD-CONSTRAINTS::complete-archs-with-orphan-support-instrument
    "If an architecture cointains a support instrument but not its primary instrument 
    then its primary instrument will be added"
    ?arch <- (HARD-CONSTRAINTS::SEL-ARCH (sequence ?seq&~nil))
    (HARD-CONSTRAINTS::SUPPORT-INSTRUMENTS  (instruments $?ins) (mask ?mask&~nil))  
    (test (eq (matlabf bit_and ?mask ?seq) 1.0)) ;; arch has at least one instrument in group
    ;(test (neq (matlabf bit_and ?mask ?seq) ?mask)) ;; but not all of them
    =>
    (bind ?new-seq (matlabf bit_or ?mask ?seq))
    (bind ?new-instr (explode$ (matlabf get_instr_from_seq ?new-seq)))
    (modify ?arch (sequence ?new-seq) (selected-instruments ?new-instr))
    )

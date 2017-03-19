

(defrule HARD-CONSTRAINTS::enforce-BEFORE-constraints
    "If an architecture does not does not satisfy a before constraint 
    then it should be deleted"
    
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (HARD-CONSTRAINTS::BEFORE-CONSTRAINT (element ?el) (before $?elems))
    (test (eq (check-precedence-in-sequence ?el ?elems ?seq) FALSE))  
    =>     
    (retract ?arch)
    )

(deffunction check-precedence-in-dates (?el ?date ?seq)
    (if (stringp ?date) then (bind ?date (integer ?date)))
    (if (listp ?date) then (bind ?date (nth$ 1 ?date)))
    (bind ?dates (matlabf get_launch_dates_from_seq2 ?seq))
    (return (< (nth$ ?el ?dates) ?date))
    )

(deffunction check-succession-in-dates (?el ?date ?seq)
    (return (not (check-precedence-in-dates ?el ?date ?seq)))
    )

(defrule HARD-CONSTRAINTS::enforce-BEFORE-DATE-constraints
    "If an architecture does not does not satisfy a before constraint 
    then it should be deleted"
    
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (HARD-CONSTRAINTS::BEFORE-DATE-CONSTRAINT (element ?el) (before ?date))
    (test (eq (check-precedence-in-dates ?el ?date ?seq) FALSE))  
    =>     
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::enforce-AFTER-constraints
    "If an architecture does not does not satisfy an AFTER constraint 
    then it should be deleted"
    
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (HARD-CONSTRAINTS::AFTER-CONSTRAINT (element ?el) (after $?elems))
    (test (eq (check-succession-in-sequence ?el ?elems ?seq) FALSE))  
    =>    
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::enforce-AFTER-DATE-constraints
    "If an architecture does not does not satisfy an AFTER constraint 
    then it should be deleted"
    
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (HARD-CONSTRAINTS::AFTER-DATE-CONSTRAINT (element ?el) (after ?date))
    (test (eq (check-succession-in-dates ?el ?date ?seq) FALSE))  
    =>    
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::enforce-BETWEEN-constraints
    "If an architecture does not does not satisfy a BETWEEN constraint 
    then it should be deleted"
    
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (HARD-CONSTRAINTS::BETWEEN-CONSTRAINT (element ?el) (between $?elems))
    (or (test (eq (check-succession-in-sequence ?el (first$ ?elems) ?seq) FALSE)) 
    (test (eq (check-precedence-in-sequence ?el (rest$ ?elems) ?seq) FALSE)) ) 
    =>    
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::enforce-BETWEEN-DATES-constraints
    "If an architecture does not does not satisfy a BETWEEN constraint 
    then it should be deleted"
    
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (HARD-CONSTRAINTS::BETWEEN-DATES-CONSTRAINT (element ?el) (between $?dates))
    (or (test (eq (check-succession-in-dates ?el (first$ ?dates) ?seq) FALSE)) 
    (test (eq (check-precedence-in-dates ?el (rest$ ?dates) ?seq) FALSE)) ) 
    =>    
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::enforce-NOT-BETWEEN-constraints
    "If an architecture does not does not satisfy a BETWEEN constraint 
    then it should be deleted"
    
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (HARD-CONSTRAINTS::NOT-BETWEEN-CONSTRAINT (element ?el) (not-between $?elems))
    (test (eq (check-precedence-in-sequence ?el (first$ ?elems) ?seq) FALSE)) 
    (test (eq (check-succession-in-sequence ?el (rest$ ?elems) ?seq) FALSE)) 
    =>    
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::enforce-NOT-BETWEEN-DATES-constraints
    "If an architecture does not does not satisfy a BETWEEN constraint 
    then it should be deleted"
    
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (HARD-CONSTRAINTS::NOT-BETWEEN-DATES-CONSTRAINT (element ?el) (not-between $?dates))
    (test (eq (check-precedence-in-dates ?el (first$ ?dates) ?seq) FALSE)) 
    (test (eq (check-succession-in-dates ?el (rest$ ?dates) ?seq) FALSE)) 
    =>    
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::enforce-CONTIGUITY-constraints
    "If an architecture does not does not satisfy a CONTIGUITY constraint 
    then it should be deleted"
    
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (HARD-CONSTRAINTS::CONTIGUITY-CONSTRAINT (elements $?elems) )
    (test (eq (check-contiguity-in-sequence ?elems ?seq) FALSE)) 
    =>    
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::enforce-NON-CONTIGUITY-constraints
    "If an architecture does not does not satisfy a NON CONTIGUITY constraint 
    then it should be deleted"
    
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (HARD-CONSTRAINTS::NON-CONTIGUITY-CONSTRAINT (elements $?elems) )
    (test (eq (check-contiguity-in-sequence ?elems ?seq) TRUE)) 
    =>    
    (retract ?arch)
    )


(defrule HARD-CONSTRAINTS::enforce-SUBSEQUENCE-constraints
    "If an architecture does not does not satisfy a SUBSEQUENCE constraint 
    then it should be deleted"
    
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (HARD-CONSTRAINTS::SUBSEQUENCE-CONSTRAINT (subsequence $?elems) )
    (test (eq (is-subsequence ?elems ?seq) FALSE)) 
    =>    
    (retract ?arch)
    )

(deffunction by-beginning-binary (?elem ?seq)
    (if (<= (nth$ ?elem (sequence-to-ordering ?seq)) (round (/ (length$ ?seq) 3))) 
        then (return TRUE) else (return FALSE))
    )

(deffunction by-middle-binary (?elem ?seq)
    (if (and 
            (> (nth$ ?elem (sequence-to-ordering ?seq)) (round (/ (length$ ?seq) 3)))
            (<= (nth$ ?elem (sequence-to-ordering ?seq)) (round (* 2 (/ (length$ ?seq) 3))))
            ) 
        then (return TRUE) else (return FALSE))
    )

(deffunction by-end-binary (?elem ?seq)
    (if (> (nth$ ?elem (sequence-to-ordering ?seq)) (round (* 2 (/ (length$ ?seq) 3))))           
        then (return TRUE) else (return FALSE))
    )

(deffunction by-beginning (?elems ?seq)
    (if (not (listp ?elems)) then (return (by-beginning-binary ?elems ?seq)))   
    (if (eq (length$ ?elems) 1) then (return (by-beginning-binary (nth$ 1 ?elems) ?seq))
        else 
        (if (eq (by-beginning-binary (nth$ 1 ?elems) ?seq) FALSE) then (return FALSE)
            else (return (by-beginning  (rest$ ?elems) ?seq))))
    )

(deffunction by-middle (?elems ?seq)
    (if (not (listp ?elems)) then (return (by-middle-binary ?elems ?seq)))   
    (if (eq (length$ ?elems) 1) then (return (by-middle-binary (nth$ 1 ?elems) ?seq))
        else 
        (if (eq (by-middle-binary (nth$ 1 ?elems) ?seq) FALSE) then (return FALSE)
            else (return (by-middle  (rest$ ?elems) ?seq))))
    )

(deffunction by-end (?elems ?seq)
    (if (not (listp ?elems)) then (return (by-end-binary ?elems ?seq)))   
    (if (eq (length$ ?elems) 1) then (return (by-end-binary (nth$ 1 ?elems) ?seq))
        else 
        (if (eq (by-end-binary (nth$ 1 ?elems) ?seq) FALSE) then (return FALSE)
            else (return (by-end  (rest$ ?elems) ?seq))))
    )

(defrule HARD-CONSTRAINTS::enforce-by-beginning-constraint
    (HARD-CONSTRAINTS::BY-BEGINNING-CONSTRAINT (elements $?elems))
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (test (eq (by-beginning ?elems ?seq) FALSE))
    =>
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::enforce-by-middle-constraint
    (HARD-CONSTRAINTS::BY-MIDDLE-CONSTRAINT (elements $?elems))
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (test (eq (by-middle ?elems ?seq) FALSE))
    =>
    (retract ?arch)
    )

(defrule HARD-CONSTRAINTS::enforce-by-end-constraint
    (HARD-CONSTRAINTS::BY-END-CONSTRAINT (elements $?elems))
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (test (eq (by-end ?elems ?seq) FALSE))
    =>
    (retract ?arch)
    )

(deffunction is-in-position (?elem ?pos ?seq)
    (return (eq (nth$ ?elem (sequence-to-ordering ?seq)) ?pos))  
    )

(defrule HARD-CONSTRAINTS::enforce-fix-position-constraint
    (HARD-CONSTRAINTS::FIX-POSITION-CONSTRAINT (element ?elem) (position ?pos))
    ?arch <- (HARD-CONSTRAINTS::PERMUTING-ARCH (sequence $?seq))
    (test (eq (is-in-position ?elem ?pos ?seq) FALSE))
    =>
    (retract ?arch)
    )


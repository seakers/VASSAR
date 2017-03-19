(defrule LOOK-AROUND::add-instrument
?a <- (MANIFEST::ARCHITECTURE (payload $?payl) (doesnt-fly $?nots) (sat-assignments $?pack) (source yes))
=>
(foreach ?instr $?nots
    (bind ?new-payl (append$ $?payl ?instr)) 
    (bind ?new-nots (remove$ $?nots ?instr)) 
    (for (bind ?i 1) (<= ?i (+ (max$ $?pack) 1)) (++ ?i)
        (bind ?new-assign (append$ $?pack ?i))
        (duplicate ?a (payload ?new-payl) (doesnt-fly ?new-nots) (source no)
        (sat-assignments ?new-assign) (num-instruments (length$ ?new-payl))))
    ))

(defrule LOOK-AROUND::remove-instrument
?a <- (MANIFEST::ARCHITECTURE (payload $?payl) (doesnt-fly $?nots) (sat-assignments $?pack) (source yes))
=>
(foreach ?instr $?payl
    (bind ?ind (member$ ?instr $?payl))
    (bind ?new-payl (delete$ $?payl ?ind ?ind)) 
    (bind ?new-nots (append$ $?nots ?instr)) 
    (bind ?assign (delete$ $?pack ?ind ?ind))
    
    (bind ?new-assign (fix-pack ?assign))
    ;(printout t ?assign  " is transformed to " ?new-assign crlf)
    (duplicate ?a (payload ?new-payl) (doesnt-fly ?new-nots) (source no)
    (sat-assignments ?new-assign) (num-sats (max$ ?new-assign)) (num-instruments (length$ ?new-payl)))
    ))

(defrule LOOK-AROUND::change-nsats-per-plane
?a <- (MANIFEST::ARCHITECTURE (num-sats-per-plane ?ns) (source yes))
=>
 (if (eq ?ns 1) then (bind ?ns2 2) else (bind ?ns2 1))
 (duplicate ?a (num-sats-per-plane ?ns2) (source no)))

(defrule LOOK-AROUND::change-orbit-type-inc
?a <- (MANIFEST::ARCHITECTURE (orbit-type ?orb) (source yes))
=>
 (if (eq ?orb SSO) then (duplicate ?a (orbit-type LEO) (orbit-inc polar) (orbit-raan NA) (source no))
 else (duplicate ?a (orbit-type SSO) (orbit-inc SSO) (orbit-raan DD) (source no))
      (duplicate ?a (orbit-type SSO) (orbit-inc SSO) (orbit-raan AM) (source no))))


(defrule LOOK-AROUND::change-SSO-raan
?a <- (MANIFEST::ARCHITECTURE (orbit-type SSO) (orbit-raan ?raan) (source yes))
=>
 (if (eq ?raan DD) then (bind ?raan2 AM) else (bind ?raan2 DD))
 (duplicate ?a (orbit-raan ?raan2) (source no)))


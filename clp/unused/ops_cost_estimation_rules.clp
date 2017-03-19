;(require* templates "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\templates.clp")
;(require* more-templates "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\more_templates.clp")
;(require* functions "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\functions.clp")

; ********************
; 4. Operations cost (3 rules)
; ********************

(defrule COST-ESTIMATION::compute-procurement-operations-cost
    "This rule computes ops cost in the procurement mode by 
    setting it to a given amount $$ per year per satellite"
    
    ?c <- (MANIFEST::CONSTELLATION (contract-modality procurement) (operations-cost nil) 
        (satellite ?sat) (lifetime ?yrs&~nil) (num-of-planes# ?np) (num-of-sats-per-plane# ?ns) )
    
    =>
    (modify ?c (operations-cost (* ?np ?ns 0.5 ?yrs)))
    )

(defrule COST-ESTIMATION::compute-hosted-payloads-operations-cost
    "This rule computes ops cost in the hosted payloads mode by 
    setting it to zero"
    
    ?c <- (MANIFEST::CONSTELLATION (contract-modality hosted-payloads) (operations-cost nil))
    
    =>
    (modify ?c (operations-cost 0.0))
    )

(defrule COST-ESTIMATION::compute-commercial-operations-cost
    "This rule computes ops cost in the commercial mode by 
    setting it to zero"
    
    ?c <- (MANIFEST::CONSTELLATION (contract-modality commercial) (operations-cost nil))
    
    =>
    (modify ?c (operations-cost 0.0))
    )

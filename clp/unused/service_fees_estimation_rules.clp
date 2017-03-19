;(require* templates "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\templates.clp")
;(require* more-templates "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\more_templates.clp")
;(require* functions "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\functions.clp")


; ********************
; 5. Service fees (3 rules)
; ********************
(defrule COST-ESTIMATION::compute-procurement-service-fee
    "This rule computes total service fee in the procurement mode by 
    setting it to zero"
    
    ?c <- (MANIFEST::CONSTELLATION (contract-modality procurement) (service-fee nil))
    
    =>
    (modify ?c (service-fee 0.0))
    )

(defrule COST-ESTIMATION::compute-hosted-payloads-service-fee
    "This rule computes total service fee in the hosted payloads mode by 
    setting it to a given amount $$ per satellite"
    
    ?c <- (MANIFEST::CONSTELLATION (contract-modality hosted-payloads) (service-fee nil) 
        (satellite ?sat) (num-planes ?np) (num-sats-per-plane ?ns) )
    
    =>
    (modify ?c (service-fee (* ?np ?ns 10.0)))
    )

(defrule COST-ESTIMATION::compute-commercial-service-fee
    "This rule computes total service fee in the commercial mode by 
    setting it to a given amount $$ per year per satellite"
    
    ?c <- (MANIFEST::CONSTELLATION (contract-modality commercial) (service-fee nil) 
        (satellite ?sat) (lifetime ?yrs&~nil) (num-planes ?np) (num-sats-per-plane ?ns) )
    
    =>
    (modify ?c (service-fee (* ?np ?ns 1.2 ?yrs)))
    )

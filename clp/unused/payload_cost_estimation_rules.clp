;(require* templates "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\templates.clp")
;(require* more-templates "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\more_templates.clp")
;(require* functions "C:\\Users\\dani\\Documents\\My Dropbox\\Marc - Dani\\SCAN\\clp\\functions.clp")
; *****************
; **** 5 rules ****
; *****************

(defrule DATABASE::compute-payload-cost-CER
    "This rule estimates the cost of a communications payload 
    using the CER defined in new SMAD page 298. The total cost is the sum
    of non-recurring (development + qualif unit) and recurring cost (first flight unit)."

    ?s <- (DATABASE::PAYLOAD (cost nil) (mass ?m&~nil) (num-channels ?n&~nil))
    =>
    (bind ?nrec (/ (+ (* 339 ?m) (* 5127 ?n)) 1000))
    (bind ?rec (/ (* 189 ?m) 1000))
    (bind ?cost (+ ?nrec ?rec))
    (modify ?s (cost ?cost) (non-recurring-cost ?nrec) (recurring-cost ?rec))
    )

(defquery COST-ESTIMATION::get-sat-payload-cost
    (declare (variables ?sat))
    (MANIFEST::SATELLITE (payload-cost ?c) (payload-recurring-cost ?r) (payload-non-recurring-cost ?nr) (id ?sat))
    )

(deffunction get-sat-payload-cost# (?sat) 
    (bind ?res (run-query* COST-ESTIMATION::get-sat-payload-cost ?sat))
    (if (?res next) then (return (?res getDouble c)) else (return 0.0))
    )

(deffunction get-sat-payload-rec-cost# (?sat) 
    (bind ?res (run-query* COST-ESTIMATION::get-sat-payload-cost ?sat))
    (if (?res next) then (return (?res getDouble r)) else (return 0.0))
    )

(deffunction get-sat-payload-non-rec-cost# (?sat) 
    (bind ?res (run-query* COST-ESTIMATION::get-sat-payload-cost ?sat))
    (if (?res next) then (return (?res getDouble nr)) else (return 0.0))
    )

(defrule COST-ESTIMATION::calculate-satellite-payload-cost
    "This rule calculates the total payload cost of a satellite"
    ?f <- (MANIFEST::SATELLITE (payloads $?payls) (payload-cost nil))
    =>
    (bind ?p 0.0) (bind ?r 0.0) (bind ?nr 0.0) 
    (foreach ?payl $?payls 
        (bind ?p (+ ?p (get-payload-cost# ?payl)))
        (bind ?r (+ ?r (get-payload-rec-cost# ?payl)))
        (bind ?nr (+ ?nr (get-payload-non-rec-cost# ?payl)))
        )
    (modify ?f (payload-cost ?p) (payload-recurring-cost ?r) (payload-non-recurring-cost ?nr))
)

(defrule COST-ESTIMATION::compute-constellation-payloads-cost-procurement
    "This rule computes total payload cost of constellations in the procurement mode by 
    adding satellite payload costs. A 95% learning curve is applied."
    
    ?c <- (MANIFEST::CONSTELLATION (num-of-planes# ?np&~nil) (num-of-sats-per-plane# ?ns&~nil) 
        (contract-modality procurement) (payloads-cost nil) 
        (satellite ?sat&~nil))
    (MANIFEST::SATELLITE (id ?sat) (payload-non-recurring-cost ?nrec&~nil)
         (payload-recurring-cost ?rec&~nil) (payload-cost ?t&~nil))
    =>
    (bind ?S 0.95); 95% learning curve, means doubling N reduces average cost by 5% (See  SMAD p 809)
    (bind ?N (* ?np ?ns)) 
    (bind ?B (- 1 (/ (log (/ 1 ?S)) (log 2))))
    (bind ?L (** ?N ?B))
    (bind ?r (* ?L ?rec))  
    (bind ?tot (+ ?r ?nrec))
    
    (modify ?c (payloads-cost ?tot) (payloads-non-rec-cost ?nrec) (payloads-rec-cost ?r))
    )

(defrule COST-ESTIMATION::compute-constellation-payloads-cost-hosted-payloads
    "This rule computes total payload cost of constellations in the hosted payloads mode by 
    adding satellite payload costs "
    
    ?c <- (MANIFEST::CONSTELLATION (contract-modality hosted-payloads) (payloads-cost nil) 
        (satellite ?sat) (num-of-planes# ?np) (num-of-sats-per-plane# ?ns) )
    (MANIFEST::SATELLITE (id ?sat) (payload-non-recurring-cost ?nrec&~nil)
         (payload-recurring-cost ?rec&~nil) (payload-cost ?t&~nil))
    =>
    (bind ?S 0.95); 95% learning curve, means doubling N reduces average cost by 5% (See  SMAD p 809)
    (bind ?N (* ?np ?ns)) 
    (bind ?B (- 1 (/ (log (/ 1 ?S)) (log 2))))
    (bind ?L (** ?N ?B))
    (bind ?r (* ?L ?rec))  
    (bind ?tot (+ ?r ?nrec))
    
    (modify ?c (payloads-cost ?tot) (payloads-non-rec-cost ?nrec) (payloads-rec-cost ?r))
    )

(defrule COST-ESTIMATION::compute-constellation-payloads-cost-commercial
    "This rule computes total payload cost of constellations in the commercial mode by 
    setting it to zero"
    
    ?c <- (MANIFEST::CONSTELLATION (contract-modality commercial) (payloads-cost nil))
    
    =>
    (modify ?c (payloads-cost 0.0) (payloads-non-rec-cost 0.0) (payloads-rec-cost 0.0))
    )

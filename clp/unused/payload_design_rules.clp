(deffunction parabolic-mass-from-diameter (?d)
    (return (max (+ (* 25.27 (** ?d 2)) (* -19.623) 11.673) 0))
    )


;; ********************************
;; SUPPORTING QUERIES AND FUNCTIONS
;; ********************************
(defquery DATABASE::get-payload-mass
    (declare (variables ?name))
    (DATABASE::PAYLOAD (id ?name) (mass ?m))
    )

(deffunction get-payload-mass# (?payl)
    (bind ?res (run-query* DATABASE::get-payload-mass ?payl))
    (if (?res next) then (return (?res getDouble m)) else (return 0.0))
    
    )

(defquery DATABASE::get-antenna-mass
    (declare (variables ?name))
    (DATABASE::ANTENNA (id ?name) (mass ?m))
    )

(deffunction get-antenna-mass# (?payl)
    (bind ?res (run-query* DATABASE::get-antenna-mass ?payl))
    (if (?res next) then (return (?res getDouble m)) else (return 0.0))
    
    )


(defquery DATABASE::get-payload-dimensions
    (declare (variables ?name))
    (DATABASE::PAYLOAD (id ?name) (dimensions $?p))
    )

(deffunction get-payload-dimensions# (?payl)
    (bind ?res (run-query* DATABASE::get-payload-dimensions ?payl))
    (if (?res next) then (return (?res get p)) else (return (create$ 0.0 0.0 0.0)))
    )

(defquery DATABASE::get-antenna-dimensions
    (declare (variables ?name))
    (DATABASE::ANTENNA (id ?name) (dimensions $?p))
    )

(deffunction get-antenna-dimensions# (?payl)
    (bind ?res (run-query* DATABASE::get-payload-dimensions ?payl))
    (if (?res next) then (return (?res get p)) else (return (create$ 0.0 0.0 0.0)))
    )

(defquery DATABASE::search-payload-by-name
    (declare (variables ?name))
    (DATABASE::PAYLOAD (id ?name) (mass ?m) (power-lin ?p) (power-dB ?p-dB) 
        (data-rate ?rb) )
    )

(defquery DATABASE::get-payload-power
    (declare (variables ?name))
    (DATABASE::PAYLOAD (id ?name) (power-lin ?p))
    )

(deffunction get-payload-power# (?payl)
    (bind ?res (run-query* DATABASE::get-payload-power ?payl))
    (if (?res next) then (return (?res getDouble p)) else (return 0.0))
    )

(defquery DATABASE::get-payload-cost
    (declare (variables ?name))
    (DATABASE::PAYLOAD (id ?name) (cost ?c) (recurring-cost ?rec) (non-recurring-cost ?nrec))
    )

(deffunction get-payload-cost# (?payl)
    (bind ?res (run-query* DATABASE::get-payload-cost ?payl))
    (if (?res next) then (return (?res getDouble c)) else (return 0.0))
    )

(deffunction get-payload-rec-cost# (?payl)
    (bind ?res (run-query* DATABASE::get-payload-cost ?payl))
    (if (?res next) then (return (?res getDouble rec)) else (return 0.0))
    )

(deffunction get-payload-non-rec-cost# (?payl)
    (bind ?res (run-query* DATABASE::get-payload-cost ?payl))
    (if (?res next) then (return (?res getDouble nrec)) else (return 0.0))
    )

(defquery DATABASE::get-payload-data-rate
    (declare (variables ?name))
    (DATABASE::PAYLOAD (id ?name) (data-rate ?rb))
    )

(deffunction get-payload-data-rate# (?payl)
    (bind ?res (run-query* DATABASE::get-payload-data-rate ?payl))
    (if (?res next) then (return (?res getDouble rb)) else (return 0.0))
    )

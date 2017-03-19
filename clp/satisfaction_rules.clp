(defrule SATISFACTION::compute-architecture-satisfaction
	"This rule computes the benefit of an architecture"
    ?f <- (MANIFEST::ARCHITECTURE (benefit nil)
        						  (stakeholders-weight $?stks-weight)
        						  (stakeholders-satisfaction $?stks-sat))
    (test (eq (member$ -1 $?stks-sat) FALSE))
    =>
    (bind ?benefit (dot-product$ $?stks-sat $?stks-weight))
    (modify ?f (benefit ?benefit))
)

(defrule SATISFACTION::compute-stakeholder-satisfaction
	"This rule computes the satisfaction of an stakeholder"
    ?f <- (SATISFACTION::STAKEHOLDER (satisfaction nil)
        							 (objectives-weight $?objs-weight)
        							 (objectives-satisfaction $?objs-sat))
    (test (eq (member$ -1 $?objs-sat) FALSE))
    =>
    (bind ?obj-sat (dot-product$ $?objs-sat $?objs-weight))
    (modify ?f (satisfaction ?obj-sat))
)

(defrule SATISFACTION::compute-objective-satisfaction
	"This rule computes the satisfaction of an objective"
    ?f <- (SATISFACTION::OBJECTIVE (users-satisfaction $?users-sat)
        						   (users-weight $?users-weight)
        						   (objectives-satisfaction $?objs-sat)
        						   (objectives-weight $?objs-weight)
        						   (satisfaction nil))
    (test (eq (member$ -1 $?users-sat) 	FALSE))
    (test (eq (member$ -1 $?objs-sat) FALSE))
    =>
    (if (neq (length$ $?users-weight) 0) then
    	(bind ?user-sat (dot-product$ $?users-sat $?users-weight))
    else
        (bind ?user-sat 0)
    )
    (if (neq (length$ $?objs-weight) 0) then
    	(bind ?obj-sat (dot-product$ $?objs-sat $?objs-weight))
    else
        (bind ?obj-sat 0)
    )
    (modify ?f (satisfaction (+ ?user-sat ?obj-sat)))
)

(defrule SATISFACTION::compute-user-satisfaction
	"This rule computes the satisfaction of a user"
    ?f <- (SATISFACTION::USER (id ?id)
        					  (services-id $?services-id)
        					  (services-weight $?services-w)
        					  (services-satisfaction $?services-sat)
        					  (satisfaction nil))
    =>
    (foreach ?service $?services-id
    	(bind ?sat (get-requirement-satisfaction ?id ?service))
        (bind ?p (member$ ?service $?services-id))
        (bind $?services-sat (replace$ $?services-sat ?p ?p ?sat))    
    )
    (modify ?f (services-satisfaction $?services-sat) 
        	   (satisfaction (dot-product$ $?services-sat $?services-w))) 
)

(defrule SATISFACTION::add-satisfaction-of-stakeholder-to-architecture
	"This rule adds the satisfaction of an stakeholder to the architecture"
    (SATISFACTION::STAKEHOLDER (id ?id) (satisfaction ?sat&~nil))    
    ?f <- (MANIFEST::ARCHITECTURE (stakeholders-id $?stks-id)
        						     (stakeholders-satisfaction $?stks-sat))
    (test (neq (member$ ?id $?stks-id) FALSE))
    (test (eq (nth$ (member$ ?id $?stks-id) $?stks-sat) -1))
    =>
    (bind ?p (member$ ?id $?stks-id))
    (bind $?stks-sat (replace$ $?stks-sat ?p ?p ?sat))
    (modify ?f (stakeholders-satisfaction $?stks-sat)) 
)

(defrule SATISFACTION::add-satisfaction-of-objective-to-stakeholder
	"This rule adds the satisfaction of an objective to it stakeholder"
    (SATISFACTION::OBJECTIVE (id ?id) (satisfaction ?sat&~nil))    
    ?f <- (SATISFACTION::STAKEHOLDER (objectives-id $?objs-id)
        						     (objectives-satisfaction $?objs-sat))
    (test (neq (member$ ?id $?objs-id) FALSE))
    (test (eq (nth$ (member$ ?id $?objs-id) $?objs-sat) -1))
    =>
    (bind ?p (member$ ?id $?objs-id))
    (bind $?objs-sat (replace$ $?objs-sat ?p ?p ?sat))
    (modify ?f (objectives-satisfaction $?objs-sat)) 
)

(defrule SATISFACTION::add-satisfaction-of-subobjective-to-objective
	"This rule adds the satisfaction of a subobjective to its parent objective"
    (SATISFACTION::OBJECTIVE (id ?id) (parent ?parent&~nil) (satisfaction ?sat&~nil))
    ?f <- (SATISFACTION::OBJECTIVE (id ?parent) (objectives-id $?objs-id)
        						   (objectives-satisfaction $?objs-sat))
    (test (neq (member$ ?id $?objs-id) FALSE))
    (test (eq (nth$ (member$ ?id $?objs-id) $?objs-sat) -1))
    =>
    (bind ?p (member$ ?id $?objs-id))
    (bind $?objs-sat (replace$ $?objs-sat ?p ?p ?sat))
    (modify ?f (objectives-satisfaction $?objs-sat))   
)

(defrule SATISFACTION::add-satisfaction-of-user-to-objective
	"This rule adds the satisfaction of a user to its parent objective"
    ?f <- (SATISFACTION::OBJECTIVE (users-id $?users-id) (users-satisfaction $?users-sat))
    (test (neq (member$ -1 $?users-sat) FALSE))
    =>
    (foreach ?user $?users-id
    	(bind ?user-sat (get-user-satisfaction ?user))
        (bind ?p (member$ ?user $?users-id))
        (bind $?users-sat (replace$ $?users-sat ?p ?p ?user-sat))    
    )
    (modify ?f (users-satisfaction $?users-sat))
)

(defrule SATISFACTION::find-user-parent
    "This rule finds the parent of a user as the objective that possesses it"
	?f <- (SATISFACTION::USER (id ?id) (parent nil))
    (SATISFACTION::OBJECTIVE (id ?obj-id) (users-id $?users-id))
    (test (neq (member$ ?id $?users-id) FALSE))
    =>
    (modify ?f (parent ?obj-id))    
)

(defquery SATISFACTION::search-stakeholder-satisfaction
    (declare (variables ?id))
    (SATISFACTION::STAKEHOLDER (id ?id) (satisfaction ?sat))    
    )

(defquery SATISFACTION::get-stakeholder-satisfaction
    (SATISFACTION::STAKEHOLDER (id ?id) (satisfaction ?sat))    
    )

(defquery SATISFACTION::get-objective-satisfaction
    (declare (variables ?parent))
    (SATISFACTION::OBJECTIVE (id ?id) (description ?desc) (parent ?parent) (satisfaction ?sat))    
    )

(defquery SATISFACTION::search-user-satisfaction
    (declare (variables ?id))
    ?f <- (SATISFACTION::USER (id ?id) (satisfaction ?sat))
)

(defquery SATISFACTION::search-user-satisfaction-by-parent
    (declare (variables ?parent))
    ?f <- (SATISFACTION::USER (id ?id) (parent ?parent) (satisfaction ?sat))
)

(defquery SATISFACTION::search-requirement-satisfaction
	(declare (variables ?user-id ?service-id))
    (CAPABILITIES::REQUIREMENT (user-id ?user-id) (service-id ?service-id) (satisfaction ?sat))    
)

(defquery SATISFACTION::search-requirement-satisfaction-by-user
	(declare (variables ?user-id))
    (CAPABILITIES::REQUIREMENT (service-id ?id) (user-id ?user-id) (satisfaction ?sat))    
)

(defquery SATISFACTION::get-architecture-benefit
    
    (MANIFEST::ARCHITECTURE (benefit ?sat))    
    )
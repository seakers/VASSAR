/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

/**
 *
 * @author dani
 */
import rbsa.eoss.local.Params;
import jess.*;
import jxl.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.*;
import org.apache.commons.lang3.StringUtils;

public class JessInitializer {
        
    private static JessInitializer instance = null;
    
    private JessInitializer() 
    {
        
    }
    
    public static JessInitializer getInstance()
    {
        if( instance == null ) 
        {
            instance = new JessInitializer();
        }
        return instance;
    }
    
    public void initializeJess( Rete r , QueryBuilder qb, MatlabFunctions m)
    {
        try {
            // Create global variable path
            String tmp = Params.path.replaceAll("\\\\", "\\\\\\\\");
            r.eval( "(defglobal ?*app_path* = \"" + tmp + "\")" );
            r.eval("(import rbsa.eoss.*)");
            r.eval("(import java.util.*)");
            r.eval("(import jess.*)");
            r.eval("(defglobal ?*rulesMap* = (new java.util.HashMap))");
            r.eval("(set-reset-globals nil)");
            Params.nof = 1;
            Params.nor = 1;

            // Load modules
            loadModules( r );

            // Load templates
            Workbook templates_xls = Workbook.getWorkbook( new File( Params.template_definition_xls ) );
            loadTemplates( r, templates_xls, Params.template_definition_clp);

            // Load functions
            loadFunctions(r,Params.functions_clp);  
            
            // Load mission analysis database
            Workbook mission_analysis_xls = Workbook.getWorkbook( new File( Params.mission_analysis_database_xls ) );
            loadOrderedDeffacts(r,mission_analysis_xls, "Walker", "Walker-revisit-time-facts","DATABASE::Revisit-time-of");
            loadOrderedDeffacts(r,mission_analysis_xls, "Power", "orbit-information-facts", "DATABASE::Orbit");

            //Load launch vehicle database
            loadOrderedDeffacts(r,mission_analysis_xls, "Launch Vehicles", "DATABASE::launch-vehicle-information-facts", "DATABASE::Launch-vehicle");
            r.reset();
            ArrayList<Fact> facts = qb.makeQuery("DATABASE::Launch-vehicle");
            
            for (int i = 0;i<facts.size();i++) {
                Fact lv = facts.get(i);
                String id = lv.getSlotValue("id").stringValue(r.getGlobalContext());
                double cost = lv.getSlotValue("cost").floatValue(r.getGlobalContext());
                double diam = lv.getSlotValue("diameter").floatValue(r.getGlobalContext());
                double height = lv.getSlotValue("height").floatValue(r.getGlobalContext());
                HashMap<String,ValueVector> payload_coeffs = new HashMap<String,ValueVector>();
                ValueVector payload_LEO_polar = lv.getSlotValue("payload-LEO-polar").listValue(r.getGlobalContext());
                ValueVector payload_SSO = lv.getSlotValue("payload-SSO").listValue(r.getGlobalContext());
                ValueVector payload_LEO_equat = lv.getSlotValue("payload-LEO-equat").listValue(r.getGlobalContext());
                ValueVector payload_MEO = lv.getSlotValue("payload-MEO").listValue(r.getGlobalContext());
                ValueVector payload_GEO = lv.getSlotValue("payload-GEO").listValue(r.getGlobalContext());
                ValueVector payload_HEO = lv.getSlotValue("payload-HEO").listValue(r.getGlobalContext());
                ValueVector payload_ISS = lv.getSlotValue("payload-ISS").listValue(r.getGlobalContext());
                payload_coeffs.put("LEO-polar",payload_LEO_polar);
                payload_coeffs.put("SSO-SSO",payload_SSO);
                payload_coeffs.put("LEO-equat",payload_LEO_equat);
                payload_coeffs.put("MEO-polar",payload_MEO);
                payload_coeffs.put("GEO-equat",payload_GEO);
                payload_coeffs.put("HEO-polar",payload_HEO);
                payload_coeffs.put("LEO-ISS",payload_ISS);
                LaunchVehicle lvh = new LaunchVehicle(id,payload_coeffs,diam,height,cost);
                m.addLaunchVehicletoDB(id, lvh);
            }
             // Load instrument database
            Workbook instrument_xls = Workbook.getWorkbook( new File( Params.capability_rules_xls ) );
            loadUnorderedDeffacts(r,instrument_xls, "CHARACTERISTICS", "instrument-database-facts","DATABASE::Instrument");

            //Load attribute inheritance rules
            loadAttributeInheritanceRules(r, templates_xls, "Attribute Inheritance", Params.attribute_inheritance_clp);
            
            //Load orbit rules;
            loadOrbitRules(r, Params.orbit_rules_clp);

            //Load mass budget rules;
            loadMassBudgetRules(r, Params.mass_budget_rules_clp);
            loadMassBudgetRules(r, Params.subsystem_mass_budget_rules_clp);
            loadMassBudgetRules(r, Params.deltaV_budget_rules_clp);

            //Load eps design rules;
            loadSpacecraftDesignRules(r, Params.eps_design_rules_clp);
            loadSpacecraftDesignRules(r, Params.adcs_design_rules_clp);
            loadSpacecraftDesignRules(r, Params.propulsion_design_rules_clp);
            
            //Load cost estimation rules;
            loadCostEstimationRules(r, new String[]{Params.fuzzy_cost_estimation_rules_clp});

            //Load launch vehicle selection rules
            loadLaunchVehicleSelectionRules(r,Params.launch_vehicle_selection_rules_clp);
            
            //Load fuzzy attribute rules
            loadFuzzyAttributeRules(r, templates_xls, "Fuzzy Attributes", "REQUIREMENTS::Measurement");

            //Load requirement rules
            Workbook requirements_xls = Workbook.getWorkbook( new File( Params.requirement_satisfaction_xls ) );
            if (Params.req_mode.equalsIgnoreCase("FUZZY-CASES")) {
                loadFuzzyRequirementRules(r, requirements_xls, "Requirement rules");
            } else if (Params.req_mode.equalsIgnoreCase("CRISP-CASES")) {
                loadRequirementRules(r, requirements_xls, "Requirement rules");
            } else if (Params.req_mode.equalsIgnoreCase("CRISP-ATTRIBUTES")) {
                loadRequirementRulesAttribs(r, requirements_xls, "Attributes", m);
            } else if (Params.req_mode.equalsIgnoreCase("FUZZY-ATTRIBUTES")) {
                loadFuzzyRequirementRulesAttribs(r, requirements_xls, "Attributes", m);
            }

            //Load capability rules
            loadCapabilityRules(r,instrument_xls,Params.capability_rules_clp);

            //Load synergy rules
            loadSynergyRules(r,Params.synergy_rules_clp);
            
            // Load assimilation rules
            loadAssimilationRules(r,Params.assimilation_rules_clp);
            
            //Ad-hoc rules
            r.eval("(deftemplate DATABASE::list-of-instruments (multislot list) (slot factHistory))");
            r.eval("(deffacts DATABASE::list-of-instruments (DATABASE::list-of-instruments " +
                    "(list (create$ SMAP_RAD SMAP_MWR CMIS VIIRS BIOMASS)) (factHistory "+ Params.nof +")))");
            Params.nof++;
            if(!Params.adhoc_rules_clp.isEmpty()) {
                System.out.println("WARNING: Loading ad-hoc rules");
                r.batch(Params.adhoc_rules_clp);
            }

            //Load down-selection rules
            loadDownSelectionRules(r,Params.down_selection_rules_clp);

            //Load search rules
            r.eval("(deffacts DATABASE::add-improve-orbit-list-of-improve-heuristics " +
                    "(SEARCH-HEURISTICS::improve-heuristic (id improveOrbit) (factHistory " + Params.nof + ")" +
                    "))");
            Params.nof++;

            loadSearchRules(r,Params.search_heuristic_rules_clp);

            
            // Load explanation rules
            loadExplanationRules(r, Params.explanation_rules_clp);

            //Load aggregation rules
            Workbook aggregation_xls = Workbook.getWorkbook( new File( Params.aggregation_xls ) );

            loadAggregationRules(r, aggregation_xls, "Aggregation rules", new String[]{Params.aggregation_rules_clp,Params.fuzzy_aggregation_rules_clp});

            // Load Critiquer rules

     //       loadCritiquerRules(r, Params.critique_cost_clp);
     //       loadCritiquerRules(r, Params.critique_performance_clp);
     //       loadCritiquerRules(r, Params.critique_performance_precalculation_clp);
     //       loadCritiquerRules(r, Params.critique_cost_precalculation_clp);       
            
            ///////////////////////////////////////////////////////////////////////////// 

            Iterator ruleIter = r.listDefrules();
            Iterator ruleIterCheck = r.listDefrules();
            Params.rules_defrule_map = new HashMap<>();
            Params.rules_NametoID_Map = new HashMap<>();
            Params.rules_IDtoName_map = new HashMap<>();

            Defrule targetRule = new Defrule("","",r);
            int cnt = 0;

            while(ruleIter.hasNext()){
                if (ruleIterCheck.next().getClass().getName().equalsIgnoreCase("jess.Defquery")){
                    ruleIter.next();
                    ruleIter.remove();
                } else {
                    cnt++;
                    Defrule currentRule = ((Defrule) ruleIter.next());
                    String ruleName = currentRule.getName();
                    Params.rules_defrule_map.put(ruleName,currentRule);
                    Params.rules_NametoID_Map.put(ruleName, cnt);
                    Params.rules_IDtoName_map.put(cnt,ruleName);
                    String tmpString = "(?*rulesMap* put " + ruleName + " " + cnt + ")";
                    r.eval(tmpString);
                }
            }
            
            //////////////////////////////////////////////////////////////////////////
            
            r.reset();
            
            //Create precomputed queries;
            load_precompute_queries(qb);
        
        } catch (Exception e) {
            System.out.println( "EXC in InitializerJess " +e.getClass() + " : " + e.getMessage() );
            e.printStackTrace();
        }
    }
    private void load_precompute_queries(QueryBuilder qb) {
        HashMap<String,Fact> db_instruments = new HashMap<String,Fact>();
        for (int i = 0;i<Params.ninstr;i++) {
            String instr = Params.instrument_list[i];
            ArrayList<Fact> facts = qb.makeQuery("DATABASE::Instrument (Name " + instr + ")");
            Fact f = facts.get(0);
            db_instruments.put(instr,f);
        }
        qb.addPrecomputed_query("DATABASE::Instrument", db_instruments);
        
        
    }
    private void loadModules( Rete r )
    {
        try {
            r.batch( Params.module_definition_clp );
        } catch (Exception e) {
            System.out.println( "EXC in loadModules " +e.getMessage() );
        }
    }
    private void loadOrbitRules( Rete r, String clp )
    {
        try {
            r.batch( clp );
        } catch (Exception e) {
            System.out.println( "EXC in loadOrbitRules " +e.getMessage() );
        }
    }
    
    private void loadTemplates( Rete r, Workbook xls, String clp)
    {
        loadMeasurementTemplate(r, xls);
        loadInstrumentTemplate(r, xls);
        loadSimpleTemplate(r, xls, "Mission","MANIFEST::Mission");
        loadSimpleTemplate(r, xls, "Orbit","DATABASE::Orbit");
        loadSimpleTemplate(r, xls, "Launch-vehicle","DATABASE::Launch-vehicle");
        loadTemplatesCLP(r, clp);
    }
    
    private void loadTemplatesCLP( Rete r, String clp) {
        try {
            r.batch(clp);
        }catch(Exception e) {System.out.println("EXC in loadTemplatesCLP " +e.getClass() + " : " + e.getMessage());}
    }
    
    private void loadMeasurementTemplate(Rete r, Workbook xls) {
        try {
            HashMap attribs_to_keys = new HashMap();
            HashMap keys_to_attribs = new HashMap();
            HashMap attribs_to_types = new HashMap();
            HashMap attribSet = new HashMap();
            Params.parameter_list = new ArrayList<String>();
            Sheet meas = xls.getSheet("Measurement");    
            String call = "(deftemplate REQUIREMENTS::Measurement ";
            int nslots = meas.getRows();
            for (int i = 1;i<nslots;i++) {
                Cell[] row = meas.getRow(i);
                String slot_type = row[0].getContents();
                String name = row[1].getContents();
                String str_id = row[2].getContents();
                int id = Integer.parseInt(str_id);
                String type = row[3].getContents();
                                
                attribs_to_keys.put(name,id);
                keys_to_attribs.put(id,name);
                attribs_to_types.put(name,type);
                if (type.equalsIgnoreCase("NL") || type.equalsIgnoreCase("OL")) {
                    String str_num_atts = row[4].getContents();
                    int num_vals = Integer.parseInt(str_num_atts);
                    Hashtable<String, Integer> accepted_values = new Hashtable<String, Integer>();
                    for (int j = 0;j<num_vals;j++) {
                        accepted_values.put(row[j+5].getContents(), new Integer(j));
                    }
                    EOAttribute attrib = AttributeBuilder.make(type,name,"N/A");
                    attrib.acceptedValues = accepted_values;
                    attribSet.put(name, attrib);
                    if(name.equalsIgnoreCase("Parameter")) {
                        Params.parameter_list.addAll(accepted_values.keySet());
                    }
                } else{
                    EOAttribute attrib = AttributeBuilder.make(type,name,"N/A");
                    attribSet.put(name, attrib);
                }
                
                
                call = call.concat(" (" + slot_type + " " + name + ") ");
            }
            GlobalVariables.defineMeasurement(attribs_to_keys,keys_to_attribs,attribs_to_types,attribSet);
            
            call = call.concat(")");
            r.eval(call);         
        } catch (Exception e) {
            System.out.println( "EXC in loadMeasurementTemplate " +e.getMessage() );
        }
    }
    
    private void loadInstrumentTemplate(Rete r, Workbook xls) {
        try {

            HashMap attribs_to_keys = new HashMap();
            HashMap keys_to_attribs = new HashMap();
            HashMap attribs_to_types = new HashMap();
            HashMap attribSet = new HashMap();
            
            Sheet meas = xls.getSheet("Instrument");    
            String call = "(deftemplate CAPABILITIES::Manifested-instrument ";
            String call2 = "(deftemplate DATABASE::Instrument ";
            int nslots = meas.getRows();
            for (int i = 1;i<nslots;i++) {
                Cell[] row = meas.getRow(i);
                String slot_type = row[0].getContents();
                String name = row[1].getContents();
                String str_id = row[2].getContents();
                int id = Integer.parseInt(str_id);
                String type = row[3].getContents();
                
                
                
                attribs_to_keys.put(name,id);
                keys_to_attribs.put(id,name);
                attribs_to_types.put(name,type);
                if (type.equalsIgnoreCase("NL") || type.equalsIgnoreCase("OL")) {
                    String str_num_atts = row[4].getContents();
                    int num_vals = Integer.parseInt(str_num_atts);
                    Hashtable accepted_values = new Hashtable();
                    for (int j = 0;j<num_vals;j++) {
                        accepted_values.put(row[j+5], new Integer(j));
                    }
                    EOAttribute attrib = AttributeBuilder.make(type,name,"N/A");
                    attrib.acceptedValues = accepted_values;
                    attribSet.put(name, attrib);
                } else{
                    EOAttribute attrib = AttributeBuilder.make(type,name,"N/A");
                    attribSet.put(name, attrib);
                }
                
                
                call = call.concat(" (" + slot_type + " " + name + ") ");
                call2 = call2.concat(" (" + slot_type + " " + name + ") ");
            }
            GlobalVariables.defineInstrument(attribs_to_keys,keys_to_attribs,attribs_to_types,attribSet);
            
            call = call.concat(")");
            call2 = call2.concat(")");
            r.eval(call);
            r.eval(call2); 
        } catch (Exception e) {
            System.out.println( "EXC in loadInstrumentTemplate " +e.getMessage() );
        }
    }
     private void loadSimpleTemplate(Rete r, Workbook xls, String sheet, String template_name) {
        try {
         
            Sheet meas = xls.getSheet(sheet);    
            String call = "(deftemplate " + template_name + " ";
            int nslots = meas.getRows();
            for (int i = 1;i<nslots;i++) {
                Cell[] row = meas.getRow(i);
                String slot_type = row[0].getContents();
                String name = row[1].getContents();
                call = call.concat(" (" + slot_type + " " + name + ") ");
            }

            
            call = call.concat(")");
            r.eval(call);
        } catch (Exception e) {
            System.out.println( "EXC in loadSimpleTemplate " +e.getMessage() );
        }
    }
     
     private void loadFunctions(Rete r, String[] clps) {
         try {
             r.addUserfunction(new SameOrBetter());
             r.addUserfunction(new Improve());
             r.addUserfunction(new Worsen());
             for (String clp : clps) {
                 r.batch(clp);
             }    
             r.eval("(deffunction update-objective-variable (?obj ?new-value) \"Update the value of the global variable with the new value only if it is better \" (bind ?obj (max ?obj ?new-value)))");
             r.eval("(deffunction ContainsRegion (?observed-region ?desired-region)  \"Returns true if the observed region i.e. 1st param contains the desired region i.e. 2nd param \" (bind ?tmp1 (eq ?observed-region Global)) (bind ?tmp2 (eq ?desired-region ?observed-region)) (if (or ?tmp1 ?tmp2) then (return TRUE) else (return FALSE)))");
             r.eval("(deffunction ContainsBands (?list-bands ?desired-bands)  \"Returns true if the list of bands contains the desired bands \" (if (subsetp ?desired-bands ?list-bands) then (return TRUE) else (return FALSE)))");

             r.eval("(deffunction numerical-to-fuzzy (?num ?values ?mins ?maxs)"  + 
                "(bind ?ind 1)"  + 
                "(bind ?n (length$ ?values))"  + 
                "(while (<= ?ind ?n)"  + 
                "(if (and (< ?num (nth$ ?ind ?maxs)) (>= ?num (nth$ ?ind ?mins))) then (return (nth$ ?ind ?values))"  + 
                    "else (++ ?ind))))");
             r.eval("(deffunction revisit-time-to-temporal-resolution (?region ?values)"  + 
            "(if (eq ?region Global) then "  + 
                "(return (nth$ 1 ?values))"  + 
            " elif (eq ?region Tropical-regions) then"  +  
                "(return (nth$ 2 ?values))"  + 
            " elif (eq ?region Northern-hemisphere) then"  +  
                "(return (nth$ 3 ?values))"  + 
            " elif (eq ?region Southern-hemisphere) then"  +  
                "(return (nth$ 4 ?values))"  + 
            " elif (eq ?region Cold-regions) then"  +  
                "(return (nth$ 5 ?values))"  + 
            " elif (eq ?region US) then"  +  
                "(return (nth$ 6 ?values))"  + 
            " else (throw new JessException \"revisit-time-to-temporal-resolution: The region of interest is invalid\")" + 
                "))");
        
            r.eval("(deffunction fuzzy-max (?att ?v1 ?v2) "  + 
                "(if (>= (SameOrBetter ?att ?v1 ?v2) 0) then "  + 
                "?v1 else ?v2))");

            r.eval("(deffunction fuzzy-min (?att ?v1 ?v2) "  + 
                "(if (<= (SameOrBetter ?att ?v1 ?v2) 0) then "  + 
                "?v1 else ?v2))");


            r.eval("(deffunction fuzzy-avg (?v1 ?v2) "  + 
                "(if (or (and (eq ?v1 High) (eq ?v2 Low)) (and (eq ?v1 Low) (eq ?v2 High))) then "  + 
                " \"Medium\" "  + 
                " else (fuzzy-min Accuracy ?v1 ?v2)))");

            r.eval("(deffunction member (?elem ?list) "  + 
                "(if (listp ?list) then "  + 
                " (neq (member$ ?elem ?list) FALSE) "  + 
                " else (?list contains ?elem)))");

            r.eval("(deffunction valid-orbit (?typ ?h ?i ?raan) "  + 
                "(bind ?valid TRUE)"  + 
                "(if (and (eq ?typ GEO) (or (neq ?h GEO) (neq ?i 0))) then (bind ?valid FALSE))"  + 
                "(if (and (neq ?typ GEO) (eq ?h GEO)) then (bind ?valid FALSE))"  + 
                "(if (and (eq ?typ SSO) (neq ?i SSO)) then (bind ?valid FALSE))"  + 
                "(if (and (neq ?typ SSO) (eq ?i SSO)) then (bind ?valid FALSE))"  + 
                "(if (and (neq ?typ SSO) (neq ?raan NA)) then (bind ?valid FALSE))"  + 
                "(if (and (eq ?typ SSO) (eq ?raan NA)) then (bind ?valid FALSE))"  + 
                "(if (and (or (eq ?h 1000) (eq ?h 1300)) (neq ?i near-polar)) then (bind ?valid FALSE))"  + 
                "(if (and (< ?h 400) (or (neq ?typ LEO) (eq ?i SSO) (eq ?i near-polar))) then (bind ?valid FALSE))"  + 
                " (return ?valid))");

            r.eval("(deffunction worth-improving-measurement (?meas) "  + 
                "(bind ?worth TRUE)"  + 
                "(bind ?arr (matlabf get_related_suboj ?meas))"  + 
                "(if (eq ?arr nil) then (return FALSE))"  + 
                "(bind ?iter (?arr iterator))"  + 
                "(while (?iter hasNext) "  + 
                "(bind ?subobj (?iter next)) "  + 
                "(if (eq (eval ?subobj) 1) then (bind ?worth FALSE))) "  + 
                "(return ?worth))");

            r.eval("(deffunction meas-group (?p ?gr)"  + 
            "(if (eq (str-compare (sub-string 1 1 ?p) A) 0) then (return FALSE))"  + 
            "(bind ?pos (str-index \" \" ?p)) " + 
            "(bind ?str (sub-string 1 (- ?pos 1) ?p)) " + 
            "(bind ?meas-1 (nth$ 1 (get-meas-group ?str))) " +  
            "(bind ?meas-2 (nth$ 2 (get-meas-group ?str)))"  +  
            "(bind ?meas-3 (nth$ 3 (get-meas-group ?str))) " +  
            "(bind ?gr-1 (nth$ 1 (get-meas-group ?gr))) " +  
            "(bind ?gr-2 (nth$ 2 (get-meas-group ?gr))) " +  
            "(bind ?gr-3 (nth$ 3 (get-meas-group ?gr)))"  +   
            "(if (and (neq (str-compare ?gr-1 ?meas-1) 0) (neq (str-compare ?gr-1 0) 0)) then (return FALSE)) " + 
            "(if (and (neq (str-compare ?gr-2 ?meas-2) 0) (neq (str-compare ?gr-2 0) 0)) then (return FALSE))"  + 
            "(if (and (neq (str-compare ?gr-3 ?meas-3) 0) (neq (str-compare ?gr-3 0) 0)) then (return FALSE)) " + 
                   " (return TRUE))");

        r.eval("(deffunction get-meas-group (?str)"  + 
            "(bind ?pos (str-index . ?str)) " + 
            "(bind ?gr1 (sub-string 1 (- ?pos 1) ?str)) " + 
            "(bind ?new-str (sub-string (+ ?pos 1) (str-length ?str) ?str)) " + 
            "(bind ?pos2 (str-index . ?new-str)) " + 
            "(bind ?gr2 (sub-string 1 (- ?pos2 1) ?new-str)) " + 
            "(bind ?gr3 (sub-string (+ ?pos2 1) (str-length ?new-str) ?new-str)) " + 
            "(return (create$ ?gr1 ?gr2 ?gr3)))");
    
            } catch (Exception e) {
            System.out.println( "EXC in loadFunctions " +e.getMessage() );
        }
     }
    
     private void loadOrderedDeffacts(Rete r, Workbook xls, String sheet, String name, String template) {
         try {
             
            Sheet meas = xls.getSheet(sheet);    
            String call = "(deffacts " + name + " ";
            int nfacts = meas.getRows();
            int nslots = meas.getColumns();
            Cell[] slotNames = meas.getRow(0);
            String[] slot_names = new String[nslots]; 
            for (int i = 0;i<nslots;i++) {
                slot_names[i] = slotNames[i].getContents();
            }
            for (int i = 1;i<nfacts;i++) {
                Cell[] row = meas.getRow(i);
                call = call.concat(" (" + template + " ");  
                for (int j = 0;j < nslots; j++) {
                    String slot_value = row[j].getContents();
                    if (slot_value.matches("\\[(.+)(,(.+))+\\]")) {
                        call = call.concat( " (" + slot_names[j] + " " + createJessList(slot_value) + ") ");
                    } else {
                        call = call.concat( " (" + slot_names[j] + " " + slot_value + ") ");
                    }
                }
                call = call.concat("(factHistory F" + Params.nof + ")");
                Params.nof++;
                call = call.concat(") ");
            }
            call = call.concat(")");
            r.eval(call);
        } catch (Exception e) {
            System.out.println( "EXC in loadOrderedDeffacts " +e.getMessage() );
        }
     }
     
     private void loadUnorderedDeffacts(Rete r, Workbook xls, String sheet, String name, String template) {
         try {
         
            Sheet meas = xls.getSheet(sheet);    
            String call = "(deffacts " + name + " ";
            int nfacts = meas.getRows();
            int nslots = meas.getColumns();
           
            for (int i = 1;i<nfacts;i++) {
                Cell[] row = meas.getRow(i);
                
                call = call.concat(" (" + template + " ");  
                for (int j = 0;j < nslots; j++) {
                    String cell_value = row[j].getContents();
                    String[] splitted = cell_value.split(" ");
                    int len = splitted.length;
                    String slot_name = "";
                    String slot_value = ""; 
                    if (len < 2) {
                        System.out.println("EXC in loadUnorderedDeffacts, expected format is slot_name slot_value. Space not found.");
                    }
                    if (len == 2) {
                       slot_name = splitted[0];
                       slot_value = splitted[1]; 
                    } else {
                       slot_name = splitted[0];
                       slot_value = splitted[1]; 
                       for (int kk = 2;kk < len;kk++) {
                           slot_value = slot_value + " " + splitted[kk]; 
                       }
                    }
                    
                    call = call.concat( " (" + slot_name + " " + slot_value + ") ");
                }
                call = call.concat("(factHistory F" + Params.nof + ")");
                Params.nof++;
                call = call.concat(") ");
            }
            call = call.concat(")");
            r.eval(call);
        } catch (Exception e) {
            System.out.println( "EXC in loadUnorderedDeffacts " +e.getMessage() );
        }
     }
     private void loadAttributeInheritanceRules(Rete r, Workbook xls, String sheet, String clp) {
         try {
             r.batch(clp);
             Sheet meas = xls.getSheet(sheet);
             
            int nrules = meas.getRows();
            for (int i = 1;i<nrules;i++) {
                Cell[] row = meas.getRow(i);
                String template1 = row[0].getContents();
                String copy_slot_type1 = row[1].getContents();
                String copy_slot_name1 = row[2].getContents();
                String matching_slot_type1 = row[3].getContents();
                String matching_slot_name1 = row[4].getContents();
                String template2 = row[5].getContents();
                String matching_slot_name2 = row[6].getContents();
                String copy_slot_name2 = row[7].getContents();
                String module = row[8].getContents();
                String call = "(defrule " + module + "::inherit-" + template1.split("::")[1] + "-" + copy_slot_name1 + "-TO-" + template2.split("::")[1].trim() + " ";
                String ruleName = (module + "::inherit-" + template1.split("::")[1] + "-" + copy_slot_name1 + "-TO-" + template2.split("::")[1]).trim();
                call = call + "(declare (no-loop TRUE))";         
                if (copy_slot_type1.equalsIgnoreCase("slot")) {
                    call = call + " ?sub <- (" + template1 + " (" + copy_slot_name1 + " ?x&~nil) ";
                } else {
                    call = call + " ?sub <- (" + template1 + " (" + copy_slot_name1 + " $?x&:(> (length$ $?x) 0)) ";
                }
                if (matching_slot_type1.equalsIgnoreCase("slot")) {
                    call = call + " (" + matching_slot_name1 + " ?id&~nil) )  ";
                } else {
                    call = call + " (" + matching_slot_name1 + " $?id&:(> (length$ $?id) 0)) ) ";
                }
                call = call + " ?old <- (" + template2 + " ";
                if (matching_slot_type1.equalsIgnoreCase("slot")) {
                    call = call + " (" + matching_slot_name2 + " ?id) (factHistory ?fh) ";
                } else {
                    call = call + " (" + matching_slot_name2 + " $?id) (factHistory ?fh)";
                }
                if (copy_slot_type1.equalsIgnoreCase("slot")) {
                    call = call + " (" + copy_slot_name2 + " nil) ";
                } else {
                    call = call + " (" + copy_slot_name2 + " $?x&:(eq (length$ $?x) 0)) ";
                }
                
                String newFactHistory = "(str-cat \"{R\" (?*rulesMap* get " + ruleName + ") \" \" ?fh \" S\" (call ?sub getFactId) \"}\")";
                call = call + ") => (modify ?old (" + copy_slot_name2 + " ?x)"
                        + "(factHistory "+ newFactHistory +")"
                        + "))";
                r.eval(call);     
            }          
         }catch (Exception e) {
            System.out.println( "EXC in loadAttributeInheritanceRules " +e.getMessage() );
        }
     }
     private void loadFuzzyAttributeRules(Rete r, Workbook xls, String sheet, String template) {
         try {
             Sheet meas = xls.getSheet(sheet);
             
            int nrules = meas.getRows();
           
            for (int i = 1;i<nrules;i++) {
                Cell[] row = meas.getRow(i);
                String att = row[0].getContents();
                String param = row[1].getContents();
                //String unit = row[2].getContents();
                int num_values = Integer.parseInt(row[3].getContents());
                String[] fuzzy_values = new String[num_values];
                String[] mins = new String[num_values];
                String[] means = new String[num_values];
                String[] maxs = new String[num_values];
                String call_values = "(create$ ";
                String call_mins = "(create$ ";
                String call_maxs = "(create$ ";
                for (int j = 1;j<=num_values;j++) {
                    fuzzy_values[j-1] = row[4*j].getContents();
                    call_values = call_values + fuzzy_values[j-1] + " ";
                    mins[j-1] = row[1+4*j].getContents();
                    call_mins = call_mins + mins[j-1] +   " ";
                    means[j-1] = row[2+4*j].getContents();
                    maxs[j-1] = row[3+4*j].getContents();
                    call_maxs = call_maxs + maxs[j-1] + " ";
                }
                call_values = call_values + ")";
                call_mins = call_mins + ")";
                call_maxs = call_maxs + ")";
                

                String call = "(defrule FUZZY::numerical-to-fuzzy-" + att + " ";
                String ruleName = "FUZZY::numerical-to-fuzzy-" + att;
                if (param.equalsIgnoreCase("all")) {
                    call = call + "?m <- (" + template + " (" + att + "# ?num&~nil) (" + att + " nil) (factHistory ?fh)) => " ;
                    call = call + "(bind ?value (numerical-to-fuzzy ?num " + call_values + " " + call_mins + " " + call_maxs + " )) (modify ?m (" + att  + " ?value)"
                            + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ruleName+") \" \" ?fh \"}\"))"
                            + ")) ";
                } else {
                    String att2 = att.substring(0, att.length()-1);
                    call = call + "?m <- (" + template + " (Parameter \"" + param + "\") (" + att2 + "# ?num&~nil) (" + att + " nil) (factHistory ?fh)) => " ;
                    call = call + "(bind ?value (numerical-to-fuzzy ?num " + call_values + " " + call_mins + " " + call_maxs + " )) (modify ?m (" + att  + " ?value)"
                            + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ruleName+") \" \" ?fh \"}\"))"
                            + ")) ";
                }

                r.eval(call);     
            }          
         }catch (Exception e) {
            System.out.println( "EXC in loadAttributeInheritanceRules " +e.getMessage() );
        }
     }
     
     private void loadRequirementRules(Rete r, Workbook xls, String sheet) {
         try {
             Sheet meas = xls.getSheet(sheet);
             
            int nrules = meas.getRows();
            int nobj = 0;
            int nsubobj = 0;
            String current_obj = "";
            String current_subobj = "";
            String var_name = "";
            for (int i = 1;i<nrules;i++) {
                Cell[] row = meas.getRow(i);
                String obj = row[0].getContents();
                String explan = row[1].getContents();
                Params.subobj_measurement_params.put(obj, explan);
                if(!obj.equalsIgnoreCase(current_obj)) {
                    nobj++;
                    nsubobj = 0;
                    var_name = "?*obj-" + obj + "*";
                    r.eval("(defglobal " + var_name + " = 0)");
                    current_obj = obj;
                }
                String subobj = row[2].getContents();
                if(!subobj.equalsIgnoreCase(current_subobj)) {
                    nsubobj++;
                    var_name = "?*subobj-" + subobj + "*";
                    r.eval("(defglobal " + var_name + " = 0)");
                    current_subobj = subobj;
                }
                String type = row[5].getContents();
                String value = row[6].getContents();
                String desc = row[7].getContents();
                String param = row[8].getContents();
                
                String tmp = "?*subobj-" + subobj + "*";
                
                if (Params.measurements_to_subobjectives.containsKey(param)) {
                    ArrayList list = (ArrayList) Params.measurements_to_subobjectives.get(param);
                    if(!list.contains(tmp)) {
                        list.add(tmp);
                        Params.measurements_to_subobjectives.put(param,list);
                    }   
                } else {
                    ArrayList list = new ArrayList();
                    list.add(tmp);
                    Params.measurements_to_subobjectives.put(param,list);
                }
                
                if (Params.measurements_to_objectives.containsKey(param)) {
                    ArrayList list = (ArrayList) Params.measurements_to_objectives.get(param);
                    if(!list.contains(obj)) {
                        list.add(obj);
                        Params.measurements_to_objectives.put(param,list);
                    }   
                } else {
                    ArrayList list = new ArrayList();
                    list.add(obj);
                    Params.measurements_to_objectives.put(param,list);
                }
                String pan = obj.substring(0,2);
                if (Params.measurements_to_panels.containsKey(param)) {
                    ArrayList list = (ArrayList) Params.measurements_to_panels.get(param);
                    if(!list.contains(pan)) {
                        list.add(pan);
                        Params.measurements_to_panels.put(param,list);
                    }   
                } else {
                    ArrayList list = new ArrayList();
                    list.add(pan);
                    Params.measurements_to_panels.put(param,list);
                }
                String ruleName = "REQUIREMENTS::subobjective-" + subobj + "-" + type + " " + desc;
                String call = "(defrule REQUIREMENTS::subobjective-" + subobj + "-" + type + " " + desc + " ?mea <- (REQUIREMENTS::Measurement (Parameter " + param + ") "; 
                //boolean more_attributes = true;
                int ntests = 0;
                String calls_for_later = "";
                for (int j = 9;j<row.length;j++) {
                    if (row[j].getType().toString().equalsIgnoreCase("Empty")) {
                        break;
                    }
                    String attrib = row[j].getContents();
                   
                    String[] tokens = attrib.split(" ",2);// limit = 2 so that remain contains RegionofInterest Global
                    String header = tokens[0];
                    String remain = tokens[1];
                    if (attrib.equalsIgnoreCase("")) {
                        call = call + " (taken-by ?who))";
                        //more_attributes = false;
                    } else if (header.startsWith("SameOrBetter")) {
                        ntests++;
                        String[] tokens2 = remain.split(" ");
                        String att = tokens2[0];
                        String val = tokens2[1];
                        String new_var_name = "?x" + ntests;
                        String match = att + " " +  new_var_name + "&~nil"; 
                        call = call + "(" + match + ")";
                        calls_for_later = calls_for_later + " (test (>= (SameOrBetter " + att + " " + new_var_name + " " + val + ") 0))";
                    } else if (header.startsWith("ContainsRegion")) {
                        ntests++;
                        String[] tokens2 = remain.split(" ");
                        String att = tokens2[0];
                        String val = tokens2[1];
                        String new_var_name = "?x" + ntests;
                        String match = att + " " +  new_var_name + "&~nil"; 
                        call = call + "(" + match + ")";
                        calls_for_later = calls_for_later + " (test (ContainsRegion " + new_var_name + " " + val + "))";
                    } else if (header.startsWith("ContainsBands")) {
                        ntests++;
                        String new_var_name = "?x" + ntests;
                        String match = " spectral-bands $" +  new_var_name; 
                        call = call + "(" + match + ")";
                        calls_for_later = calls_for_later + " (test (ContainsBands  (create$ " + remain + ") $" + new_var_name + "))";
                    } else {
                        call = call + "(" + attrib + ")";
                    }
                }
                call = call + "(taken-by ?who)) " + calls_for_later + " => ";
                var_name = "?*subobj-" + subobj + "*";
                
                if (type.startsWith("nominal")) {
                    call = call + "(assert (REASONING::fully-satisfied (subobjective " + subobj + ") (parameter " + param + ") (objective \" " + explan + "\") (taken-by ?who)"
                            + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ruleName+") \" A\" (call ?mea getFactId) \"}\"))"
                            + "))" ;
                } else {
                    call = call + "(assert (REASONING::partially-satisfied (subobjective " + subobj + ") (parameter " + param + ") (objective \" " + explan + "\") (attribute " + desc + ") (taken-by ?who)"
                            + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ ruleName +") \" A\" (call ?mea getFactId) \"}\"))"
                            + "))" ;
                }
                call = call + "(bind " + var_name + " (max " + var_name + " " + value + " )))";
                r.eval(call);     
                r.eval("(defglobal ?*num-soundings-per-day* = 0)");
            }
            Params.subobjectives_to_measurements = getInverseHashMap(Params.measurements_to_subobjectives);
            Params.objectives_to_measurements = getInverseHashMap(Params.measurements_to_objectives);
            Params.panels_to_measurements = getInverseHashMap(Params.measurements_to_panels);
         }catch (Exception e) {
            System.out.println( "EXC in loadRequirementRules " +e.getMessage() );
        }
     }
     private void loadRequirementRulesAttribs(Rete r, Workbook xls, String sheet, MatlabFunctions m) {
         try {
             Sheet meas = xls.getSheet(sheet);
             int nlines = meas.getRows();            
             String call2 = "(deffacts REQUIREMENTS::init-subobjectives ";
             //String rhs0 = ") => (bind ?reason \"\") (bind ?new-reasons (create$ ))";
             String lhs = "";
             String rhs = "";
             String rhs2 = " (bind ?list (create$ ";
             String current_subobj = "";
             int nattrib = 0;
             String req_rule = "";
             String attribs = "";
             String param = "";
             String current_param = "";
             String ruleName = "";
             HashMap<String,ArrayList<String>> subobj_tests = null;
             Params.requirement_rules = new HashMap();
             for (int i =1;i<nlines;i++) {
                 Cell[] row = meas.getRow(i);
                 String subobj = row[0].getContents();
                 param = row[1].getContents();
                 Params.subobj_measurement_params.put(subobj, param);
                 
                 
                 ArrayList<String> attrib_test = new ArrayList();
                if(!subobj.equalsIgnoreCase(current_subobj)) {
                    
                    if (nattrib > 0) {
                        //finish this requirement rule
                        String[] tokens = current_subobj.split("-",2);// limit = 2 so that remain contains RegionofInterest Global
                        String parent = tokens[0];
                        String index = tokens[1];
                        call2 = call2 + " (AGGREGATION::SUBOBJECTIVE (satisfaction 0.0) (id " + current_subobj + ") (index " + index + ") (parent " + parent + ") (reasons (create$ " + StringUtils.repeat("N-A ",nattrib) + " ))"
                                + "(factHistory F" + Params.nof + ")) ";
                        Params.nof++;
                        String rhs0 = ") => (bind ?reason \"\") (bind ?new-reasons (create$ "  + StringUtils.repeat("N-A ",nattrib) + "))";
                        req_rule = lhs + rhs0 + rhs + rhs2 + ")) (assert (AGGREGATION::SUBOBJECTIVE (id " + current_subobj + ") (attributes " + attribs + ") (index " + index + ") (parent " + parent + " ) (attrib-scores ?list) (satisfaction (*$ ?list)) (reasons ?new-reasons) (satisfied-by ?whom) (reason ?reason )"
                                + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ruleName+") \" A\" (call ?m getFactId) \"}\"))"
                                + "))";
                        req_rule = req_rule + ")";
                        Params.requirement_rules.put(current_subobj,subobj_tests);
                        Params.subobjectives_to_measurements.put(current_subobj, current_param);
                        r.eval(req_rule);
                        
                        //start next requirement rule
                        rhs = "";
                        rhs2 = " (bind ?list (create$ ";
                        attribs = "";
                        
                        lhs = "(defrule REQUIREMENTS::"  + subobj + "-attrib ?m <- (REQUIREMENTS::Measurement (taken-by ?whom) (power-duty-cycle# ?pc) (data-rate-duty-cycle# ?dc)  (Parameter " + param + ")";
                        ruleName = "REQUIREMENTS::"  + subobj + "-attrib";
                        current_subobj = subobj;
                        current_param = param;
                        nattrib = 0;
                        subobj_tests = new HashMap();
                    } else {
                        //start next requirement rule
                        rhs = "";
                        rhs2 = " (bind ?list (create$ ";
                        attribs = "";
                        lhs = "(defrule REQUIREMENTS::"  + subobj + "-attrib ?m <- (REQUIREMENTS::Measurement (taken-by ?whom)  (power-duty-cycle# ?pc) (data-rate-duty-cycle# ?dc)  (Parameter " + param + ")";
                        ruleName = "REQUIREMENTS::"  + subobj + "-attrib";
                        current_subobj = subobj;
                        current_param = param;
                        subobj_tests = new HashMap();
                        //nattrib = 0;
                    }
                }
                
                
                String attrib = row[2].getContents();
                attribs = attribs + " " + attrib;
                String type = row[3].getContents();
                String thresholds = row[4].getContents();
                String scores = row[5].getContents();
                String justif = row[6].getContents();
                attrib_test.add(type);
                attrib_test.add(thresholds);
                attrib_test.add(scores);
                subobj_tests.put(attrib, attrib_test);
                nattrib++;
                lhs = lhs + " (" + attrib + " ?val" + nattrib + "&~nil) ";
                rhs = rhs + "(bind ?x" + nattrib + " (nth$ (find-bin-num ?val" + nattrib + " " + m.toJessList(thresholds) + " ) " + m.toJessList(scores) + "))";
                rhs = rhs + "(if (< ?x" + nattrib + " 1.0) then (bind ?new-reasons (replace$  ?new-reasons " + nattrib + " " + nattrib + " " + justif 
                        + " )) (bind ?reason (str-cat ?reason " + " " + justif + "))) ";
                rhs2 = rhs2 + " ?x" + nattrib;
             }
             //Last rule has not been processed
            String[] tokens = current_subobj.split("-",2);// limit = 2 so that remain contains RegionofInterest Global
            String parent = tokens[0];
            String index = tokens[1];
            call2 = call2 + " (AGGREGATION::SUBOBJECTIVE (satisfaction 0.0) (id " + current_subobj + ") (index " + index + ") (parent " + parent + ") (reasons (create$ " + StringUtils.repeat("N-A ",nattrib) + " ))"
                    + "(factHistory F" + Params.nof + ")) ";
            Params.nof++;
            String rhs0 = ") => (bind ?reason \"\") (bind ?new-reasons (create$ "  + StringUtils.repeat("N-A ",nattrib) + "))";
            req_rule = lhs + rhs0 + rhs + rhs2 + ")) (assert (AGGREGATION::SUBOBJECTIVE (id " + current_subobj + ") (attributes " + attribs + ") (index " + index + ") (parent " + parent + " ) (attrib-scores ?list) (satisfaction (*$ ?list)) (reasons ?new-reasons) (satisfied-by ?whom) (reason ?reason )"
                    + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ruleName+") \" A\" (call ?m getFactId) \"}\"))"
                    + "))";
            req_rule = req_rule + ")";

            r.eval(req_rule);
            Params.requirement_rules.put(current_subobj,subobj_tests);
            Params.subobjectives_to_measurements.put(current_subobj, current_param);            
            call2= call2 + ")";
            r.eval(call2);
         }catch (Exception e) {
            System.out.println( "EXC in loadRequirementRulesAttribs " +e.getMessage() );
        }
     }
     
     private void loadFuzzyRequirementRulesAttribs(Rete r, Workbook xls, String sheet, MatlabFunctions m) {
         try {
             Sheet meas = xls.getSheet(sheet);
             int nlines = meas.getRows();            
             String call2 = "(deffacts REQUIREMENTS::init-subobjectives ";
             //String rhs0 = ") => (bind ?reason \"\") (bind ?new-reasons (create$ ))";
             String lhs = "";
             String rhs = "";
             String rhs2 = " (bind ?list (create$ ";
             String current_subobj = "";
             int nattrib = 0;
             String req_rule = "";
             String attribs = "";
             String param = "";
             String current_param = "";
             String ruleName = "";
             HashMap<String,ArrayList<String>> subobj_tests = null;
             Params.requirement_rules = new HashMap();
             for (int i =1;i<nlines;i++) {
                 Cell[] row = meas.getRow(i);
                 String subobj = row[0].getContents();
                 param = row[1].getContents();
                 Params.subobj_measurement_params.put(subobj, param);
                 
                 
                 ArrayList<String> attrib_test = new ArrayList();
                if(!subobj.equalsIgnoreCase(current_subobj)) {
                    
                    if (nattrib > 0) {
                        //finish this requirement rule
                        String[] tokens = current_subobj.split("-",2);// limit = 2 so that remain contains RegionofInterest Global
                        String parent = tokens[0];
                        String index = tokens[1];
                        call2 = call2 + " (AGGREGATION::SUBOBJECTIVE (satisfaction 0.0) (fuzzy-value (new FuzzyValue \"Value\" 0.0 0.0 0.0 \"utils\" (MatlabFunctions getValue_inv_hashmap))) (id " + current_subobj + ") (index " + index + ") (parent " + parent + ") (reasons (create$ " + StringUtils.repeat("N-A ",nattrib) + " ))"
                                + "(factHistory F" + Params.nof + ")) ";
                        Params.nof++;
                        String rhs0 = ") => (bind ?reason \"\") (bind ?new-reasons (create$ "  + StringUtils.repeat("N-A ",nattrib + 2) + "))";
                        req_rule = lhs + rhs0 + rhs + rhs2 + " ?dc ?pc)) (assert (AGGREGATION::SUBOBJECTIVE (id " + current_subobj + ") (attributes " + attribs + " data-rate-duty-cycle# power-duty-cycle#) (index " + index + ") (parent " + parent + " ) "
                                + "(attrib-scores ?list) (satisfaction (*$ ?list)) (fuzzy-value (new FuzzyValue \"Value\" (call "
                                + "(new FuzzyValue \"Value\" (new Interval \"interval\" (*$ ?list) (*$ ?list)) \"utils\" "
                                + "(MatlabFunctions getValue_hashmap)) getFuzzy_val) \"utils\" (MatlabFunctions getValue_inv_hashmap))) "
                                + " (reasons ?new-reasons) (satisfied-by ?whom) (reason ?reason )"
                                + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ruleName+") \" A\" (call ?m getFactId) \"}\"))"
                                + "))";
                        req_rule = req_rule + ")";
                        Params.requirement_rules.put(current_subobj,subobj_tests);
                        Params.subobjectives_to_measurements.put(current_subobj, current_param);
                        r.eval(req_rule);
                        
                        //start next requirement rule
                        rhs = "";
                        rhs2 = " (bind ?list (create$ ";
                        attribs = "";
                        
                        lhs = "(defrule FUZZY-REQUIREMENTS::"  + subobj + "-attrib ?m <- (REQUIREMENTS::Measurement (taken-by ?whom) (data-rate-duty-cycle# ?dc) (power-duty-cycle# ?pc) (Parameter " + param + ") ";
                        ruleName = "FUZZY-REQUIREMENTS::"  + subobj + "-attrib";
                        current_subobj = subobj;
                        current_param = param;
                        nattrib = 0;
                        subobj_tests = new HashMap();
                    } else {
                        //start next requirement rule
                        rhs = "";
                        rhs2 = " (bind ?list (create$ ";
                        attribs = "";
                        lhs = "(defrule FUZZY-REQUIREMENTS::"  + subobj + "-attrib ?m <- (REQUIREMENTS::Measurement (taken-by ?whom) (data-rate-duty-cycle# ?dc) (power-duty-cycle# ?pc) (Parameter " + param + ") ";
                        ruleName = "FUZZY-REQUIREMENTS::"  + subobj + "-attrib";
                        current_subobj = subobj;
                        current_param = param;
                        subobj_tests = new HashMap();
                        //nattrib = 0;
                    }
                }
                
                
                String attrib = row[2].getContents();
                attribs = attribs + " " + attrib;
                String type = row[3].getContents();
                String thresholds = row[4].getContents();
                String scores = row[5].getContents();
                String justif = row[6].getContents();
                attrib_test.add(type);
                attrib_test.add(thresholds);
                attrib_test.add(scores);
                subobj_tests.put(attrib, attrib_test);
                nattrib++;
                lhs = lhs + " (" + attrib + " ?val" + nattrib + "&~nil) ";
                rhs = rhs + "(bind ?x" + nattrib + " (nth$ (find-bin-num ?val" + nattrib + " " + m.toJessList(thresholds) + " ) " + m.toJessList(scores) + "))";
                rhs = rhs + "(if (< ?x" + nattrib + " 1.0) then (bind ?new-reasons (replace$  ?new-reasons " + nattrib + " " + nattrib + " " + justif 
                        + " )) (bind ?reason (str-cat ?reason " + " " + justif + "))) ";
                rhs2 = rhs2 + " ?x" + nattrib;
             }
             //Last rule has not been processed
            String[] tokens = current_subobj.split("-",2);// limit = 2 so that remain contains RegionofInterest Global
            String parent = tokens[0];
            String index = tokens[1];
            call2 = call2 + " (AGGREGATION::SUBOBJECTIVE (satisfaction 0.0) (fuzzy-value "
                    + "(new FuzzyValue \"Value\" 0.0 0.0 0.0 \"utils\" (MatlabFunctions getValue_inv_hashmap)))"
                    + " (id " + current_subobj + ") (index " + index + ") (parent " + parent + ") "
                    + "(reasons (create$ " + StringUtils.repeat("N-A ",nattrib + 2) + " ))"
                    + "(factHistory F" + Params.nof + ")) ";
            Params.nof++;
            String rhs0 = ") => (bind ?reason \"\") (bind ?new-reasons (create$ "  + StringUtils.repeat("N-A ",nattrib) + "))";
            req_rule = lhs + rhs0 + rhs + rhs2 + " ?dc ?pc)) (assert (AGGREGATION::SUBOBJECTIVE (id " + current_subobj + ") (attributes " + attribs + " data-rate-duty-cycle# power-duty-cycle#) (index " + index + ") (parent " + parent + " ) "
                                + "(attrib-scores ?list) (satisfaction (*$ ?list)) (fuzzy-value (new FuzzyValue \"Value\" (call "
                                + "(new FuzzyValue \"Value\" (new Interval \"interval\" (*$ ?list) (*$ ?list)) \"utils\" "
                                + "(MatlabFunctions getValue_hashmap)) getFuzzy_val) \"utils\" (MatlabFunctions getValue_inv_hashmap))) "
                                + " (reasons ?new-reasons) (satisfied-by ?whom) (reason ?reason ) (factHistory (str-cat \"{R\" (?*rulesMap* get "+ruleName+") \" A\" (call ?m getFactId) \"}\"))))";
            req_rule = req_rule + ")";

            r.eval(req_rule);
            Params.requirement_rules.put(current_subobj,subobj_tests);
            Params.subobjectives_to_measurements.put(current_subobj, current_param);            
            call2= call2 + ")";
            r.eval(call2);
         }catch (Exception e) {
            System.out.println( "EXC in loadFuzzyRequirementRulesAttribs " +e.getMessage() );
            e.printStackTrace();
        }
     }
     
     private void loadFuzzyRequirementRules(Rete r, Workbook xls, String sheet) {
         try {
             Sheet meas = xls.getSheet(sheet);
             
            int nrules = meas.getRows();
            int nobj = 0;
            int nsubobj = 0;
            String current_obj = "";
            String current_subobj = "";
            String var_name = "";
            String call2 = "(deffacts REQUIREMENTS::init-subobjectives ";
            
            for (int i = 1;i<nrules;i++) {
                Cell[] row = meas.getRow(i);
                String obj = row[0].getContents();
                String explan = row[1].getContents();
                Params.subobj_measurement_params.put(obj, explan);
                if(!obj.equalsIgnoreCase(current_obj)) {
                    nobj++;
                    nsubobj = 0;
                    var_name = "?*obj-" + obj + "*";
                    r.eval("(defglobal " + var_name + " = 0)");
                    current_obj = obj;
                }
                String subobj = row[2].getContents();
                if(!subobj.equalsIgnoreCase(current_subobj)) {
                    nsubobj++;
                    var_name = "?*subobj-" + subobj + "*";
                    r.eval("(defglobal " + var_name + " = 0)");
                    current_subobj = subobj;
                }
                String type = row[5].getContents();
                String value = row[6].getContents();
                String desc = row[7].getContents();
                String param = row[8].getContents();
                
                String tmp = "?*subobj-" + subobj + "*";
                
                if (Params.measurements_to_subobjectives.containsKey(param)) {
                    ArrayList list = (ArrayList) Params.measurements_to_subobjectives.get(param);
                    if(!list.contains(tmp)) {
                        list.add(tmp);
                        Params.measurements_to_subobjectives.put(param,list);
                    }   
                } else {
                    ArrayList list = new ArrayList();
                    list.add(tmp);
                    Params.measurements_to_subobjectives.put(param,list);
                }
                
                if (Params.measurements_to_objectives.containsKey(param)) {
                    ArrayList list = (ArrayList) Params.measurements_to_objectives.get(param);
                    if(!list.contains(obj)) {
                        list.add(obj);
                        Params.measurements_to_objectives.put(param,list);
                    }   
                } else {
                    ArrayList list = new ArrayList();
                    list.add(obj);
                    Params.measurements_to_objectives.put(param,list);
                }
                String pan = obj.substring(0,2);
                if (Params.measurements_to_panels.containsKey(param)) {
                    ArrayList list = (ArrayList) Params.measurements_to_panels.get(param);
                    if(!list.contains(pan)) {
                        list.add(pan);
                        Params.measurements_to_panels.put(param,list);
                    }   
                } else {
                    ArrayList list = new ArrayList();
                    list.add(pan);
                    Params.measurements_to_panels.put(param,list);
                }
                
                String call = "(defrule FUZZY-REQUIREMENTS::subobjective-" + subobj + "-" + type + " " + desc + " ?mea <- (REQUIREMENTS::Measurement (Parameter " + param + ")  "; 
                String ruleName = "FUZZY-REQUIREMENTS::subobjective-" + subobj + "-" + type + " " + desc;
                //boolean more_attributes = true;
                int ntests = 0;
                String calls_for_later = "";
                for (int j = 9;j<row.length;j++) {
                    if (row[j].getType().toString().equalsIgnoreCase("Empty")) {
                        break;
                    }
                    String attrib = row[j].getContents();
                   
                    String[] tokens = attrib.split(" ",2);// limit = 2 so that remain contains RegionofInterest Global
                    String header = tokens[0];
                    String remain = tokens[1];
                    if (attrib.equalsIgnoreCase("")) {
                        call = call + " (taken-by ?who))";
                        //more_attributes = false;
                    } else if (header.startsWith("SameOrBetter")) {
                        ntests++;
                        String[] tokens2 = remain.split(" ");
                        String att = tokens2[0];
                        String val = tokens2[1];
                        String new_var_name = "?x" + ntests;
                        String match = att + " " +  new_var_name + "&~nil"; 
                        call = call + "(" + match + ")";
                        calls_for_later = calls_for_later + " (test (>= (SameOrBetter " + att + " " + new_var_name + " " + val + ") 0))";
                    } else if (header.startsWith("ContainsRegion")) {
                        ntests++;
                        String[] tokens2 = remain.split(" ");
                        String att = tokens2[0];
                        String val = tokens2[1];
                        String new_var_name = "?x" + ntests;
                        String match = att + " " +  new_var_name + "&~nil"; 
                        call = call + "(" + match + ")";
                        calls_for_later = calls_for_later + " (test (ContainsRegion " + new_var_name + " " + val + "))";
                    } else if (header.startsWith("ContainsBands")) {
                        ntests++;
                        String new_var_name = "?x" + ntests;
                        String match = " spectral-bands $" +  new_var_name; 
                        call = call + "(" + match + ")";
                        calls_for_later = calls_for_later + " (test (ContainsBands  (create$ " + remain + ") $" + new_var_name + "))";
                    } else {
                        call = call + "(" + attrib + ")";
                    }
                }
                call = call + "(taken-by ?who)) " + calls_for_later + " => ";
                var_name = "?*subobj-" + subobj + "*";
                
                if (type.startsWith("nominal")) {
                    call = call + "(assert (REASONING::fully-satisfied (subobjective " + subobj + ") (parameter " + param + ") (objective \" " + explan + "\") (taken-by ?who)"
                            + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ruleName+") \" A\" (call ?mea getFactId) \"}\"))"
                            + "))" ;
                } else {
                    call = call + "(assert (REASONING::partially-satisfied (subobjective " + subobj + ") (parameter " + param + ") (objective \" " + explan + "\") (attribute " + desc + ") (taken-by ?who)"
                            + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ruleName+") \" A\" (call ?mea getFactId) \"}\"))"
                            + "))" ;
                }
                //Addition for fuzzy rules
                //tmpp = regexp(subobj,'(?<parent>.+)-(?<index>.+)','names');
                String the_index = "";
                String the_parent = "";
                
                call2 = call2 + " (AGGREGATION::SUBOBJECTIVE (satisfaction 0.0) (fuzzy-value (new FuzzyValue \"Value\" 0.0 0.0 0.0 \"utils\" (matlabf get_value_inv_hashmap))) (id " + subobj + ") (index " + 
                        the_index + ") (parent " + the_parent + " )"
                        + "(factHistory F" + Params.nof + ")) ";
                Params.nof++;
                call = call + "(assert (AGGREGATION::SUBOBJECTIVE (id " + subobj + ") (index " + the_index + " ) (parent " + the_parent + 
                        " ) (fuzzy-value (new FuzzyValue \"Value\" (call (new FuzzyValue \"Value\" (new Interval \"interval\" " + value + 
                        " " + value + ") \"utils\" (matlabf get_value_hashmap)) getFuzzy_val) \"utils\" (matlabf get_value_inv_hashmap))) (satisfaction " + value + ")  (satisfied-by ?who)"
                        + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ruleName+") \" A\" (call ?mea getFactId) \"}\"))"
                        + " ))";
                        
                //Back to normal rules
                call = call + " (bind " + var_name + " (max " + var_name + " " + value + " )))";
                r.eval(call);     
                r.eval("(defglobal ?*num-soundings-per-day* = 0)");
            }
            Params.subobjectives_to_measurements = getInverseHashMap(Params.measurements_to_subobjectives);
            Params.objectives_to_measurements = getInverseHashMap(Params.measurements_to_objectives);
            Params.panels_to_measurements = getInverseHashMap(Params.measurements_to_panels);
         }catch (Exception e) {
            System.out.println( "EXC in loadRequirementRules " +e.getMessage() );
        }
     }
     private void loadCapabilityRules(Rete r, Workbook xls, String clp) {
         try {
             r.batch(clp);
             for (String instrument:Params.instrument_list) {
                 Sheet sh = xls.getSheet(instrument);
                 int nmeasurements = sh.getRows();
                 ArrayList meas = new ArrayList();
                 ArrayList subobj = new ArrayList();
                 ArrayList obj = new ArrayList();
                 ArrayList pan = new ArrayList();
                 String ruleName = "MANIFEST::" + instrument + "-init-can-measure";
                 String call = "(defrule MANIFEST::" + instrument + "-init-can-measure " + "(declare (salience -20)) ?this <- (CAPABILITIES::Manifested-instrument  (Name ?ins&" + instrument
                         +  ") (Id ?id) (flies-in ?miss) (Intent ?int) (Spectral-region ?sr) (orbit-type ?typ) (orbit-altitude# ?h) (orbit-inclination ?inc) (orbit-RAAN ?raan) (orbit-anomaly# ?ano) (Illumination ?il) (factHistory ?fh)) " 
                         + " (not (CAPABILITIES::can-measure (instrument ?ins) (in-orbit ?miss) (can-take-measurements no))) => " 
                         + "(assert (CAPABILITIES::can-measure (instrument ?ins) (orbit-type ?typ) (orbit-altitude# ?h) (orbit-inclination ?inc) (data-rate-duty-cycle# nil) (power-duty-cycle# nil)(orbit-RAAN ?raan)"
                         + "(in-orbit (eval (str-cat ?typ \"-\" ?h \"-\" ?inc \"-\" ?raan))) (can-take-measurements yes) (reason \"by default\") "
                         + "(copied-to-measurement-fact no)(factHistory (str-cat \"{R\" (?*rulesMap* get "+ ruleName +") \" A\" (call ?this getFactId) \"}\")))))";
                r.eval(call);
                
                ruleName = "CAPABILITIES::" + instrument + "-measurements";
                String call2 = "(defrule CAPABILITIES-GENERATE::" + instrument + "-measurements " + "?this <- (CAPABILITIES::Manifested-instrument  (Name ?ins&" + instrument
                         +  ") (Id ?id) (flies-in ?miss) (Intent ?int) (orbit-string ?orb) (Spectral-region ?sr) (orbit-type ?typ) (orbit-altitude# ?h) (orbit-inclination ?inc) (orbit-RAAN ?raan) (orbit-anomaly# ?ano) (Illumination ?il) (factHistory ?fh1)) " 
                         + " ?this2 <- (CAPABILITIES::can-measure (instrument ?ins) (in-orbit ?orb) (can-take-measurements yes) (data-rate-duty-cycle# ?dc-d) (power-duty-cycle# ?dc-p) (copied-to-measurement-fact no)(factHistory ?fh2)) => " 
                         + " (if (and (numberp ?dc-d) (numberp ?dc-p)) then (bind ?*science-multiplier* (min ?dc-d ?dc-p)) else (bind ?*science-multiplier* 1.0)) " 
                        + "(assert (CAPABILITIES::resource-limitations (data-rate-duty-cycle# ?dc-d) (power-duty-cycle# ?dc-p)"
                        + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ ruleName +") \" A\" (call ?this getFactId) \" A\" (call ?this2 getFactId) \"}\"))"
                        + ")) ";//+ " (if (and (numberp ?dc-d) (numberp ?dc-d)) then (bind ?*science-multiplier* (min ?dc-d ?dc-p)) else (bind ?*science-multiplier* 1.0)) " 
                String list_of_measurements = "";
                for (int i = 0;i<nmeasurements;i++) {
                    Cell[] row = sh.getRow(i);
                    call2 = call2 + "(assert (REQUIREMENTS::Measurement";
                    
                    String capability_type = row[0].getContents();//Measurement
                    if (!capability_type.equalsIgnoreCase("Measurement")) {
                        throw new Exception("loadCapabilityRules: Type of capability not recognized (use Measurement)");
                    }
                    String att_value_pair = row[1].getContents();
                    String[] tokens2 = att_value_pair.split(" ",2);
                    String att = tokens2[0];//Parameter
                    String val = tokens2[1];//"x.x.x Soil moisture"
                    meas.add(val);
                    ArrayList list_subobjs = (ArrayList) Params.measurements_to_subobjectives.get(val);
                    if (list_subobjs != null) {
                        Iterator list_subobjs2 = list_subobjs.iterator();
                        while (list_subobjs2.hasNext()) {
                            String tmp = (String) list_subobjs2.next();
                            String subob = tmp.substring(9,tmp.length()-1);
                            if (!subobj.contains(subob)) {
                                subobj.add(subob);
                            }
                            String[] tokens3 = subob.split("-",2);
                            String ob = tokens3[0];
                            if (!obj.contains(ob)) {
                                obj.add(ob);
                            }
                            java.util.regex.Pattern p = java.util.regex.Pattern.compile("^[A-Z]+");
                            Matcher m = p.matcher(ob);
                            m.find();
                            String pa = m.group();
                            if (!pan.contains(pa)) {
                                pan.add(pa);
                            }
                        }
                    }
                    for (int j = 1;j<row.length;j++) {
                        String att_value_pair2 = row[j].getContents();
                        tokens2 = att_value_pair2.split(" ",2);  
                        if(tokens2[1].equalsIgnoreCase("nil")){
                            continue;
                        }
                        call2 = call2 + " (" + att_value_pair2 + ") ";
                    }
                    call2 = call2 + "(taken-by " + instrument +  ") (flies-in ?miss) (orbit-altitude# ?h) (orbit-RAAN ?raan) (orbit-anomaly# ?ano) (Id " + instrument + i + ") (Instrument " + instrument + ")"
                            + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ ruleName +") \" A\" (call ?this getFactId) \" A\" (call ?this2 getFactId) \"}\"))"
                            + ")) ";
                    list_of_measurements = list_of_measurements + " " + instrument + i + " ";
                }
                call2 = call2 + "(assert (SYNERGIES::cross-registered (measurements " + list_of_measurements + " ) (degree-of-cross-registration instrument) (platform ?id  )"
                        + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ ruleName +") \" A\" (call ?this getFactId) \" A\" (call ?this2 getFactId) \"}\"))"
                        + "))";
                call2 = call2 + "(modify ?this (measurement-ids " + list_of_measurements + ")"
                        + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ ruleName +") \" \" ?fh1 \" S\" (call ?this2 getFactId) \"}\"))"
                        + ")";
                call2 = call2 + "(modify ?this2 (copied-to-measurement-fact yes)"
                        + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ ruleName +") \" \" ?fh1 \" S\" (call ?this2 getFactId) \"}\"))"
                        + "))";                
                
                r.eval(call2);
                Params.instruments_to_measurements.put(instrument,meas);
                Params.instruments_to_subobjectives.put(instrument,subobj);
                Params.instruments_to_objectives.put(instrument,obj);
                Params.instruments_to_panels.put(instrument,pan);
             }       
             Params.measurements_to_instruments = getInverseHashMap(Params.instruments_to_measurements);
             Params.subobjectives_to_instruments = getInverseHashMap(Params.instruments_to_measurements);
             Params.objectives_to_instruments = getInverseHashMap(Params.instruments_to_objectives);
             Params.panels_to_instruments = getInverseHashMap(Params.instruments_to_panels);
         }catch (Exception e) {
            System.out.println( "EXC in loadCapabilityRules " +e.getMessage() );
        }
     }
     private HashMap getInverseHashMap(HashMap hm) {
         HashMap inverse = new HashMap();
         Iterator es = hm.entrySet().iterator();
         while (es.hasNext()) {
             Map.Entry<String,ArrayList> entr = (Map.Entry<String,ArrayList>) es.next();
             String key = (String) entr.getKey();
             ArrayList vals = (ArrayList) entr.getValue();
             Iterator vals2 = vals.iterator();
             while (vals2.hasNext()) {
                 String val = (String) vals2.next();
                 if (inverse.containsKey(val)) {
                     ArrayList list = (ArrayList) inverse.get(val);
                     if (!list.contains(key)) {
                         list.add(key);
                         inverse.put(val,list);
                     } 
                 } else {
                     ArrayList list = new ArrayList();
                     list.add(key);
                     inverse.put(val,list);
                 }
             }
         }
         return inverse;
     }
     private void loadSynergyRules(Rete r, String clp) {
         try {
            r.batch(clp);
            Iterator it = Params.measurements_to_subobjectives.entrySet().iterator();
            while(it.hasNext())  {
                Map.Entry<String,ArrayList> es = (Map.Entry<String,ArrayList>) it.next();
                String meas = (String) es.getKey();
                ArrayList subobjs2 = (ArrayList) es.getValue(); 
                Iterator subobjs = subobjs2.iterator();
                String call = "(defrule SYNERGIES::stop-improving-" + meas.substring(1, meas.indexOf(" ")) + " ";
                String ruleName = "SYNERGIES::stop-improving-" + meas.substring(1, meas.indexOf(" "));
                while (subobjs.hasNext())  {
                    String var = (String) subobjs.next();
                    call = call + "?fsat <- (REASONING::fully-satisfied (subobjective " + var + ") (factHistory ?fh))";
                }
                call = call + " => (assert (REASONING::stop-improving (Measurement " + meas + ")"
                        + "(factHistory (str-cat \"{R\" (?*rulesMap* get "+ruleName+") \" A\" (call ?fsat getFactId) \"}\"))"
                        + ")))";
                r.eval(call);
            }
         } catch (Exception e) {
             System.out.println( "EXC in loadSynergyRules " +e.getMessage() );
         }     
     }
     private void loadAggregationRules (Rete r, Workbook xls, String sheet, String[] clps) {
         try {
             for (String clp:clps)
                r.batch(clp);
             Sheet meas = xls.getSheet(sheet);
             
             //Stakeholders or panels
             Cell[] col = meas.getColumn(1);
             Params.npanels = col.length-3;
             String call = "(deffacts AGGREGATION::init-aggregation-facts ";
             Params.panel_names = new ArrayList(Params.npanels);
             Params.panel_weights = new ArrayList(Params.npanels);
             Params.obj_weights = new ArrayList(Params.npanels);
             Params.subobj_weights = new ArrayList(Params.npanels);
             Params.num_objectives_per_panel = new ArrayList(Params.npanels);
             Params.subobj_weights_map = new HashMap();
             for(int i = 0;i<Params.npanels;i++) {
                 Params.panel_names.add(meas.getCell(1, i+2).getContents());
                 //Params.panel_weights.add(Double.parseDouble(meas.getCell(3, i+2).getContents()));
                 NumberCell nc = (NumberCell)meas.getCell(3, i+2);
                 Params.panel_weights.add(nc.getValue());
             }
             call = call  + " (AGGREGATION::VALUE (sh-scores (repeat$ -1.0 " + Params.npanels + ")) (sh-fuzzy-scores (repeat$ -1.0 " + Params.npanels + ")) (weights " + javaArrayList2JessList(Params.panel_weights) + ")"
                     + "(factHistory F" + Params.nof + "))";
             Params.nof++;
             
             // Objectives
             Cell[] obj_w = meas.getColumn(8);
             Cell[] obj_n = meas.getColumn(6);
             Cell[] obj_d = meas.getColumn(7);
             int i = 3;
             int p = 0;
             //ArrayList<String> obj_descriptions = new ArrayList<>();
             HashMap<String, String> obj_descriptions = new HashMap<>();
             while(p<Params.npanels) {            
                 Boolean new_panel = false;
                 ArrayList<Double> obj_weights_p = new ArrayList<>();
                 while(!new_panel) {             
                     //Double weight = Double.parseDouble(obj_w[i].getContents());
                     NumberCell nc2 = (NumberCell) obj_w[i];
                     obj_weights_p.add(nc2.getValue());
                     String obj = obj_n[i].getContents();
                     obj_descriptions.put(obj,obj_d[i].getContents());
                     new_panel = obj_d[i+1].getContents().equalsIgnoreCase("");
                     i++;
                     
                 }
                 Params.obj_weights.add(obj_weights_p);
                 Params.num_objectives_per_panel.add(obj_weights_p.size());
                 
                 call = call + " (AGGREGATION::STAKEHOLDER (id " + Params.panel_names.get(p) + " ) (index " + (p + 1) + " ) (obj-fuzzy-scores (repeat$ -1.0 " +  obj_weights_p.size() + ")) (obj-scores (repeat$ -1.0 " + obj_weights_p.size() + ")) (weights " +  javaArrayList2JessList(obj_weights_p) + ")"
                         + "(factHistory F" + Params.nof + ")) ";
                 Params.nof++;
                 p++;
                 i = i + 4;
             }
             Params.objective_descriptions = obj_descriptions;
             //Subobjectives
             
            
             p = 0;
             Params.subobjectives = new ArrayList();
             HashMap<String,String> subobjDes = new HashMap<>();
             while(p<Params.npanels) {  
                 Cell[] subobj_w = meas.getColumn(13+p*5);
                 Cell[] subobj_n = meas.getColumn(11+p*5);
                 Cell[] subobj_d = meas.getColumn(12+p*5);
                 //Cell[] subobj_d = meas.getColumn(12*p*5);
                 ArrayList<ArrayList> subobj_weights_p = new ArrayList<ArrayList>();
                 ArrayList subobj_p = new ArrayList(Params.num_objectives_per_panel.get(p));
                 i = 4;
                 int o = 0;
                 while(o<Params.num_objectives_per_panel.get(p)) {
                     Boolean new_obj = false;
                     ArrayList<Double> subobj_weights_o = new ArrayList<Double>();
                     ArrayList subobj_o = new ArrayList();
                     int so = 1;
                     while(!new_obj) {             
                         //Double weight = Double.parseDouble(subobj_w[i].getContents());
                         NumberCell nc3 = (NumberCell) subobj_w[i];
                         double weight = nc3.getValue();
                         subobj_weights_o.add(weight);
                         String subobj_name = Params.panel_names.get(p) + (o+1) + "-" + so;
//                         System.out.println(subobj_name + ": " + subobj_d[i].getContents());
                         subobjDes.put(subobj_name, subobj_d[i].getContents());
                         Params.subobj_weights_map.put(subobj_name,weight);
                         subobj_o.add(subobj_name);
                         i++;so++;
                         if (i>= subobj_n.length) {
                             new_obj = true;
                         } else {
                             String subobj = subobj_n[i].getContents();
                             new_obj = subobj.equalsIgnoreCase("");
                         }
                         
                     }
                     subobj_weights_p.add(subobj_weights_o);
                     subobj_p.add(subobj_o);
                     call = call + " (AGGREGATION::OBJECTIVE (id " + Params.panel_names.get(p) + (o + 1) + " ) (parent " + Params.panel_names.get(p) + ") (index " + (o + 1)+ " ) (subobj-fuzzy-scores (repeat$ -1.0 " +  subobj_weights_o.size() + ")) (subobj-scores (repeat$ -1.0 " + subobj_weights_o.size() + ")) (weights " +  javaArrayList2JessList(subobj_weights_o) + ")"
                             + "(factHistory F" + Params.nof + ")) ";
                     Params.nof++;
                     o++;
                     i = i + 4;
                 }
                 p++;
                 Params.subobj_weights.add(subobj_weights_p);
                 Params.subobjectives.add(subobj_p);
             }
             Params.subobj_descriptions = subobjDes;
             call = call + ")";//close deffacts
             r.eval(call);         
         } catch (Exception e) {
            System.out.println( "EXC in loadAggregationRules " +e.getMessage() );
        }
     }
     private String javaArrayList2JessList(ArrayList list) {
         String call = "(create$";
         for (int i = 0;i<list.size();i++) {
             call = call + " " + list.get(i);
         }
         call = call + ")";
         return call;
     }
     private void loadAssimilationRules (Rete r, String clp) {
         try {
             r.batch(clp);
         } catch(Exception e) {
             System.out.println( "EXC in loadAssimilationRules " +e.getMessage() );
         }  
     }
     private void loadMassBudgetRules (Rete r, String clp) {
         try {
             r.batch(clp);
         } catch(Exception e) {
             System.out.println( "EXC in loadMassBudgetRules " +e.getMessage() );
         }  
     }
     private void loadSpacecraftDesignRules (Rete r, String clp) {
         try {
             r.batch(clp);
         } catch(Exception e) {
             System.out.println( "EXC in loadEpsDesignRules " +e.getMessage() );
         }  
     }
     private void loadCostEstimationRules (Rete r, String[] clps) {
         try {
             for (String clp:clps)
                r.batch(clp);
         } catch(Exception e) {
             System.out.println( "EXC in loadCostEstimationRules " +e.getMessage() );
         }  
     }
     private void loadLaunchVehicleSelectionRules (Rete r, String clp) {
         try {
             r.batch(clp);
         } catch(Exception e) {
             System.out.println( "EXC in loadLaunchVehicleSelectionRules " +e.getMessage() );
         }  
     }
     private void loadSearchRules (Rete r, String clp) {
         try {
             r.batch(clp);
             r.reset();
             r.setFocus("DATABASE");
             r.run();
         } catch(Exception e) {
             System.out.println( "EXC in loadSearchRules " +e.getMessage() );
         }
         
         //get rules in SEARCH
 /*        Iterator it = r.listDefrules();
         ArrayList<String> improve_rules = new ArrayList<String>();
         while(it.hasNext()) {
             HasLHS rule = (HasLHS) it.next();
             String name = rule.getName();
             if(!name.contains("improve"))
                 continue;
             jess.Pattern p = (jess.Pattern)rule.getConditionalElements();
             improve_rules.add(p.toString());
         }*/
     }
     
     private void loadDownSelectionRules (Rete r, String clp) {
         try {
             r.batch(clp);
         } catch(Exception e) {
             System.out.println( "EXC in loadDownSelectionRules " +e.getMessage() );
         }  
     }
     private void loadExplanationRules (Rete r, String clp) {
         try {
             r.batch(clp);
            String call = "(defquery REQUIREMENTS::search-all-measurements-by-parameter \"Finds all measurements of this parameter in the campaign\" " + 
            "(declare (variables ?param)) " +
            "(REQUIREMENTS::Measurement (Parameter ?param) (flies-in ?flies) (launch-date ?ld) (lifetime ?lt) (Instrument ?instr)" +
            " (Temporal-resolution ?tr) (All-weather ?aw) (Horizontal-Spatial-Resolution ?hsr) (Spectral-sampling ?ss)" +
            " (taken-by ?tk) (Vertical-Spatial-Resolution ?vsr) (sensitivity-in-low-troposphere-PBL ?tro) (sensitivity-in-upper-stratosphere ?str)))";
            r.eval(call);
         } catch(Exception e) {
             System.out.println( "EXC in loadExplanationRules " +e.getMessage() );
         }  
     }
     
     
     
     private void loadCritiquerRules (Rete r, String clp){
         try{
            r.batch(clp); 
         } catch(Exception e){
             System.out.println("EXC in loadCritiquerRules" + e.getMessage());
         }
     }
     
     private String createJessList( String str )
    {
        String s = "(create$ ";
        
        str = str.substring(1, str.length()-1);
        String[] list = str.split(",");
        
        for( int i = 0; i < list.length; i++ )
            s = s + list[i] + " ";
        
        return ( s + ")" );
    }
}

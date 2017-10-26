/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss.local;

/**
 *
 * @author dani
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import jess.Defrule;

public class Params {
    //public static String master_xls;
    //public static boolean recompute_scores;
    
    public static String path;// used
    public static String req_mode;//used
    public static String name;//used
    public static String run_mode;//used
    public static String initial_pop;
    public static String template_definition_xls;// used
    public static String element_database_xls;
    public static String instrument_capability_xls;// used
    public static String attribute_inheritance_rules_xls;
    public static String mission_analysis_database_xls;// used
    public static String value_aggregation_xls;
    public static String requirement_satisfaction_xls;
    public static String aggregation_xls;
    public static String capability_rules_xls;//used
    
    public static String module_definition_clp;// used
    public static String template_definition_clp;// used
    public static String[] functions_clp = new String[2];// used
    public static String assimilation_rules_clp;// used
    public static String aggregation_rules_clp;// used
    public static String fuzzy_aggregation_rules_clp;//used
    public static String jess_queries_clp;
    public static String enumeration_rules_clp;
    public static String manifest_rules_clp;
    public static String database_completeness_clp;
    public static String attribute_inheritance_clp;
    public static String orbit_rules_clp;
    public static String eps_design_rules_clp;
    public static String adcs_design_rules_clp;
    public static String propulsion_design_rules_clp;
    public static String capability_rules_clp;
    public static String satisfaction_rules_clp;
    public static String demand_rules_clp;
    public static String synergy_rules_clp;//Used
    public static String explanation_rules_clp;//Used
    public static String fuzzy_attribute_clp;
    public static String value_aggregation_clp;
    public static String requirement_satisfaction_clp;
    public static String cost_estimation_rules_clp;
    public static String fuzzy_cost_estimation_rules_clp;
    public static String mass_budget_rules_clp;
    public static String subsystem_mass_budget_rules_clp;
    public static String deltaV_budget_rules_clp;
    public static String sat_configuration_rules_clp;
    public static String launch_vehicle_selection_rules_clp;
    public static String standard_bus_selection_rules_clp;
    public static String links_rules_clp;
    public static String RF_spectrum_rules_clp;
    public static String scheduler_rules_clp;
    public static String paths_rules_clp;
    public static String search_heuristic_rules_clp;
    public static String down_selection_rules_clp;
    public static String adhoc_rules_clp;
    public static String critique_cost_clp;
    public static String critique_performance_clp;
    public static String critique_performance_precalculation_clp;
    public static String critique_cost_precalculation_clp;
    public static String critique_performance_initialize_facts_clp;
    public static String critique_cost_initialize_facts_clp;
    
    // Metrics for utility and pareto calculations
    public static ArrayList<String> pareto_metrics;
    public static ArrayList<String> pareto_metric_types;
    public static int pareto_ranking_depth;
    public static int pareto_ranking_threshold;
    public static ArrayList<String> util_metrics;
    public static ArrayList<String> util_metric_types;
    public static ArrayList<Double> util_metric_weights;
    public static double min_science;
    public static double max_science;
    public static double min_cost;
    public static double max_cost;
    public static int num_improve_heuristics;
    public static double prob_accept;
    // Instruments
    //public static String[] instrument_list = {"SMAP_RAD","SMAP_MWR","CMIS","VIIRS","BIOMASS"};
    public static String[] instrument_list = {"ACE_ORCA","ACE_POL","ACE_LID","CLAR_ERB","ACE_CPR","DESD_SAR","DESD_LID","GACM_VIS","GACM_SWIR","HYSP_TIR","POSTEPS_IRS","CNES_KaRIN"};
    public static int ninstr;
    public static String[] orbit_list = {"LEO-600-polar-NA","SSO-600-SSO-AM","SSO-600-SSO-DD","SSO-800-SSO-DD","SSO-800-SSO-PM"};
    public static int norb;
    public static HashMap instrument_indexes;
    public static HashMap orbit_indexes;
    public static int[] nsats = {1};
    public static int MAX_TOTAL_INSTR;
    // Results
    public static String path_save_results;
    
    
    // Intermediate results
    public static int nof; //number of facts
    public static int nor; //number of rules
    public static HashMap <String, Defrule> rules_defrule_map;
    public static HashMap <Integer,String> rules_IDtoName_map;
    public static HashMap <String, Integer> rules_NametoID_Map;
    public static HashMap requirement_rules;
    public static HashMap measurements_to_subobjectives;
    public static HashMap measurements_to_objectives;
    public static HashMap measurements_to_panels;
    public static ArrayList parameter_list;
    public static ArrayList objectives;
    public static ArrayList subobjectives;
    public static HashMap instruments_to_measurements;
    public static HashMap instruments_to_subobjectives;
    public static HashMap instruments_to_objectives;
    public static HashMap instruments_to_panels;
    public static HashMap measurements_to_instruments;
    public static HashMap subobjectives_to_instruments;
    public static HashMap objectives_to_instruments;
    public static HashMap panels_to_instruments;
    public static HashMap subobjectives_to_measurements;
    public static HashMap objectives_to_measurements;
    public static HashMap panels_to_measurements;
    public ArrayList<String> lowTRL_instruments;
    public static int npanels;
    public static ArrayList<Double> panel_weights;
    public static ArrayList<String> panel_names;
    //public static ArrayList<String> objective_descriptions;
    public static ArrayList obj_weights;
    public static ArrayList<Integer> num_objectives_per_panel;
    public static ArrayList subobj_weights;
    public static HashMap objective_descriptions;
    public static HashMap subobj_descriptions;
    public static HashMap subobj_weights_map;
    public static HashMap revtimes;
    public static HashMap scores;
    public static HashMap subobj_scores;
    public static HashMap capabilities;
    public static HashMap all_dsms;
    public static HashMap subobj_measurement_params;
    //Cubesat costs model
    public static HashMap instrument_masses;
    public static String capability_dat_file;
    public static String revtimes_dat_file;
    public static String dsm_dat_file;
    public static String scores_dat_file;
    
   
    public Params( String p , String mode, String name, String run_mode, String search_clp)
    {
        //this.master_xls = master_xls;
        //this.recompute_scores = recompute_scores;
        this.path = p;
        this.req_mode = mode;
        this.name = name;
        this.run_mode = run_mode;
        path_save_results = path + "/results";
        capability_dat_file = path + "/dat/capabilities.dat";//capabilities2014-09-09-02-33-13.
        revtimes_dat_file = path + "/dat/climate-centric revtimes.dat";
        dsm_dat_file = path + "/dat/all_dsms2014-09-14-18-56-03.dat";
        scores_dat_file = path  + "/dat/scores2014-09-14-18-13-37.dat";
        initial_pop = "";//path + "/results/2014-09-15_08-59-55_test.rs";//2014-09-08_15-51-32_test
        //initial_pop = "";
        // Paths for common xls files
        /*template_definition_xls = path + "/xls/AttributeSet.xls";//used
        mission_analysis_database_xls = path + "/xls/Mission Analysis Database.xls";//used
        capability_rules_xls = path + "/xls/SMAP Instrument Capability Definition.xls";//used
        requirement_satisfaction_xls = path + "/xls/SMAP Requirement Rules.xls";//used
        aggregation_xls = path + "/xls/SMAP Aggregation Rules.xls";//used*/        
        
        template_definition_xls = path + "/xls/Climate-centric/Climate-centric AttributeSet.xls";//used
        mission_analysis_database_xls = path + "/xls/Climate-centric/Mission Analysis Database.xls";//used
        capability_rules_xls = path + "/xls/Climate-centric/Climate-centric Instrument Capability Definition2.xls";//used
        requirement_satisfaction_xls = path + "/xls/Climate-centric/Climate-centric Requirement Rules.xls";//used
        aggregation_xls = path + "/xls/Climate-centric/Climate-centric Aggregation Rules.xls";//used  
        
        // Paths for common clp files
        module_definition_clp        = path + "/clp/modules.clp";//used
        template_definition_clp      = path + "/clp/templates.clp";//used
        functions_clp[0]           = path + "/clp/jess_functions.clp";//used
        functions_clp[1]           = path + "/clp/functions.clp";//used
        assimilation_rules_clp       = path + "/clp/assimilation_rules.clp";//used
        aggregation_rules_clp       = path + "/clp/aggregation_rules.clp";//used
        fuzzy_aggregation_rules_clp  = path + "/clp/fuzzy_aggregation_rules.clp";//used
        jess_queries_clp             = path + "/clp/queries.clp";//Absent in SMAP
        enumeration_rules_clp        = path + "/clp/enumeration_rules.clp";
        manifest_rules_clp           = path + "/clp/manifest_rules.clp";
        database_completeness_clp    = path + "/clp/database_completeness_rules.clp";
        attribute_inheritance_clp    = path + "/clp/attribute_inheritance_rules.clp";
        orbit_rules_clp              = path + "/clp/orbit_rules.clp";
        capability_rules_clp         = path + "/clp/capability_rules.clp";
        satisfaction_rules_clp       = path + "/clp/satisfaction_rules.clp";
        //demand_rules_clp             = path + "/clp/demand_rules.clp";
        synergy_rules_clp            = path + "/clp/synergy_rules.clp";//Used
        explanation_rules_clp        = path + "/clp/explanation_rules.clp";//Used
        fuzzy_attribute_clp          = path + "/clp/fuzzy_attribute_rules.clp";
        value_aggregation_clp        = path + "/clp/requirement_rules.clp";
        requirement_satisfaction_clp = path + "/clp/aggregation_rules.clp";
        cost_estimation_rules_clp    = path + "/clp/cost_estimation_rules.clp";
        fuzzy_cost_estimation_rules_clp    = path + "/clp/fuzzy_cost_estimation_rules.clp";
        mass_budget_rules_clp        = path + "/clp/mass_budget_rules.clp";
        subsystem_mass_budget_rules_clp = path + "/clp/subsystem_mass_budget_rules.clp";
        deltaV_budget_rules_clp = path + "/clp/deltaV_budget_rules.clp";
        adcs_design_rules_clp        = path + "/clp/adcs_design_rules.clp";
        propulsion_design_rules_clp        = path + "/clp/propulsion_design_rules.clp";
        eps_design_rules_clp        = path + "/clp/eps_design_rules.clp";
        sat_configuration_rules_clp  = path + "/clp/sat_configuration_rules.clp";
        launch_vehicle_selection_rules_clp  = path + "/clp/launch_cost_estimation_rules.clp";
        standard_bus_selection_rules_clp    = path + "/clp/standard_bus_selection_rules.clp";
        if(search_clp.isEmpty())
            search_heuristic_rules_clp   = path + "/clp/search_heuristic_rules_smap_improveOrbit.clp";
        else
            search_heuristic_rules_clp   = path + "/clp/" + search_clp + ".clp";
        
        down_selection_rules_clp     = path + "/clp/down_selection_rules_smap.clp";
        adhoc_rules_clp              = path + "/clp/climate_centric_rules.clp";
        critique_cost_clp   = path + "/clp/critique/critique_cost.clp";
        critique_performance_clp  = path + "/clp/critique/critique_performance.clp";
        critique_performance_precalculation_clp  = path + "/clp/critique/critique_performance_precalculation.clp";
        critique_cost_precalculation_clp = path + "/clp/critique/critique_cost_precalculation.clp";
        critique_performance_initialize_facts_clp = path + "/clp/critique/critique_performance_initialize_facts.clp";
        critique_cost_initialize_facts_clp = path + "/clp/critique/critique_cost_initialize_facts.clp";
        
        // Metrics for utility and pareto calculations
        pareto_metrics = new ArrayList<String>();
        pareto_metrics.add( "lifecycle_cost" );
        pareto_metrics.add( "benefit" );
        pareto_metric_types = new ArrayList<String>();
        pareto_metric_types.add( "SIB" );
        pareto_metric_types.add( "LIB" );
        util_metrics = new ArrayList<String>();
        util_metrics.add( "lifecycle_cost" );
        util_metrics.add( "benefit" );
        util_metric_types = new ArrayList<String>();
        util_metric_types.add( "SIB" );
        util_metric_types.add( "LIB" );
        util_metric_weights = new ArrayList<Double>();
        util_metric_weights.add( 0.5 );
        util_metric_weights.add( 0.5 );
        prob_accept = 0.8;
        // Instruments  & Orbits

        //instrument_list[0] = "SMAP_ANT";
        ninstr = instrument_list.length;
        MAX_TOTAL_INSTR = 5*ninstr;
        norb = orbit_list.length;
        instrument_indexes = new HashMap<String,Integer>();
        orbit_indexes= new HashMap<String,Integer>();
        for (int j = 0;j<ninstr;j++)
            instrument_indexes.put(instrument_list[j], j);
        for (int j = 0;j<norb;j++)
            orbit_indexes.put(orbit_list[j], j);
        // Intermediate results
        measurements_to_subobjectives = new HashMap();
        measurements_to_objectives = new HashMap();
        measurements_to_panels = new HashMap();
        objectives = new ArrayList();
        subobjectives = new ArrayList();
        instruments_to_measurements = new HashMap();
        instruments_to_subobjectives = new HashMap();
        instruments_to_objectives = new HashMap();
        instruments_to_panels = new HashMap();
        
        measurements_to_instruments = new HashMap();
        subobjectives_to_instruments = new HashMap();
        objectives_to_instruments = new HashMap();
        panels_to_instruments = new HashMap();
    
        subobjectives_to_measurements = new HashMap();
        objectives_to_measurements = new HashMap();
        panels_to_measurements = new HashMap();
        
        subobj_measurement_params = new HashMap();
        
        try {
            
            
            if(!run_mode.equalsIgnoreCase("update_revtimes")) {
                FileInputStream fis = new FileInputStream(revtimes_dat_file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                revtimes = (HashMap) ois.readObject();
                ois.close();
            }
            if(!run_mode.equalsIgnoreCase("update_capabilities")) {
                
                FileInputStream fis2 = new FileInputStream(capability_dat_file);
                ObjectInputStream ois2 = new ObjectInputStream(fis2);
                capabilities = (HashMap) ois2.readObject();
                ois2.close();
            }
            if(!run_mode.equalsIgnoreCase("update_dsms")) {
                FileInputStream fis3 = new FileInputStream(dsm_dat_file);
                ObjectInputStream ois3 = new ObjectInputStream(fis3);
                all_dsms = (HashMap) ois3.readObject();
                ois3.close();
            }
            if(!run_mode.equalsIgnoreCase("update_scores")) {
                FileInputStream fis3 = new FileInputStream(scores_dat_file);
                ObjectInputStream ois3 = new ObjectInputStream(fis3);
                scores = (HashMap) ois3.readObject();
                subobj_scores = (HashMap) ois3.readObject();
                ois3.close();
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
       }
    public static String getConfiguration() {
        return "reqs" + requirement_satisfaction_xls + ";capas= " + capability_rules_xls;
    }

    public static String getName() {
        return name;
    }
    
}
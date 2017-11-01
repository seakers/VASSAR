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
    //public String master_xls;
    //public boolean recompute_scores;
    public static Params instance = null;

    public static Params initInstance(String p, String mode, String name, String run_mode, String search_clp) {
        instance = new Params(p, mode, name, run_mode, search_clp);
        return instance;
    }

    public static Params getInstance() {
        return instance;
    }
    
    public String path;// used
    public String req_mode;//used
    public String name;//used
    public String run_mode;//used
    public String initial_pop;
    public String template_definition_xls;// used
    public String element_database_xls;
    public String instrument_capability_xls;// used
    public String attribute_inheritance_rules_xls;
    public String mission_analysis_database_xls;// used
    public String value_aggregation_xls;
    public String requirement_satisfaction_xls;
    public String aggregation_xls;
    public String capability_rules_xls;//used
    
    public String module_definition_clp;// used
    public String template_definition_clp;// used
    public String[] functions_clp = new String[2];// used
    public String assimilation_rules_clp;// used
    public String aggregation_rules_clp;// used
    public String fuzzy_aggregation_rules_clp;//used
    public String jess_queries_clp;
    public String enumeration_rules_clp;
    public String manifest_rules_clp;
    public String database_completeness_clp;
    public String attribute_inheritance_clp;
    public String orbit_rules_clp;
    public String eps_design_rules_clp;
    public String adcs_design_rules_clp;
    public String propulsion_design_rules_clp;
    public String capability_rules_clp;
    public String satisfaction_rules_clp;
    public String demand_rules_clp;
    public String synergy_rules_clp;//Used
    public String explanation_rules_clp;//Used
    public String fuzzy_attribute_clp;
    public String value_aggregation_clp;
    public String requirement_satisfaction_clp;
    public String cost_estimation_rules_clp;
    public String fuzzy_cost_estimation_rules_clp;
    public String mass_budget_rules_clp;
    public String subsystem_mass_budget_rules_clp;
    public String deltaV_budget_rules_clp;
    public String sat_configuration_rules_clp;
    public String launch_vehicle_selection_rules_clp;
    public String standard_bus_selection_rules_clp;
    public String links_rules_clp;
    public String RF_spectrum_rules_clp;
    public String scheduler_rules_clp;
    public String paths_rules_clp;
    public String search_heuristic_rules_clp;
    public String down_selection_rules_clp;
    public String adhoc_rules_clp;
    public String critique_cost_clp;
    public String critique_performance_clp;
    public String critique_performance_precalculation_clp;
    public String critique_cost_precalculation_clp;
    public String critique_performance_initialize_facts_clp;
    public String critique_cost_initialize_facts_clp;
    
    // Metrics for utility and pareto calculations
    public static ArrayList<String> pareto_metrics = new ArrayList<String>();
    public static ArrayList<String> pareto_metric_types = new ArrayList<String>();
    public int pareto_ranking_depth;
    public int pareto_ranking_threshold;
    public static ArrayList<String> util_metrics = new ArrayList<String>();
    public static ArrayList<String> util_metric_types = new ArrayList<String>();
    public static ArrayList<Double> util_metric_weights= new ArrayList<Double>();
    public double min_science;
    public double max_science;
    public double min_cost;
    public double max_cost;
    public int num_improve_heuristics;
    public static double prob_accept = 0.8;
    // Instruments
    //public String[] instrument_list = {"SMAP_RAD","SMAP_MWR","CMIS","VIIRS","BIOMASS"};
    public static String[] instrument_list = {"ACE_ORCA","ACE_POL","ACE_LID","CLAR_ERB","ACE_CPR","DESD_SAR","DESD_LID","GACM_VIS","GACM_SWIR","HYSP_TIR","POSTEPS_IRS","CNES_KaRIN"};
    public static int ninstr;
    public static String[] orbit_list = {"LEO-600-polar-NA","SSO-600-SSO-AM","SSO-600-SSO-DD","SSO-800-SSO-DD","SSO-800-SSO-PM"};
    public static int norb;
    public static HashMap instrument_indexes = new HashMap<String, Integer>();
    public static HashMap orbit_indexes = new HashMap<String, Integer>();
    public static int[] nsats = {1};
    public static int MAX_TOTAL_INSTR;
    // Results
    public String path_save_results;
    
    
    // Intermediate results
    public int nof; //number of facts
    public int nor; //number of rules
    public HashMap <String, Defrule> rules_defrule_map;
    public HashMap <Integer,String> rules_IDtoName_map;
    public HashMap <String, Integer> rules_NametoID_Map;
    public HashMap requirement_rules;
    public HashMap measurements_to_subobjectives;
    public HashMap measurements_to_objectives;
    public HashMap measurements_to_panels;
    public ArrayList parameter_list;
    public ArrayList objectives;
    public ArrayList subobjectives;
    public HashMap instruments_to_measurements;
    public HashMap instruments_to_subobjectives;
    public HashMap instruments_to_objectives;
    public HashMap instruments_to_panels;
    public HashMap measurements_to_instruments;
    public HashMap subobjectives_to_instruments;
    public HashMap objectives_to_instruments;
    public HashMap panels_to_instruments;
    public HashMap subobjectives_to_measurements;
    public HashMap objectives_to_measurements;
    public HashMap panels_to_measurements;
    public ArrayList<String> lowTRL_instruments;
    public int npanels;
    public ArrayList<Double> panel_weights;
    public ArrayList<String> panel_names;
    //public ArrayList<String> objective_descriptions;
    public ArrayList obj_weights;
    public ArrayList<Integer> num_objectives_per_panel;
    public ArrayList subobj_weights;
    public HashMap<String, String> objective_descriptions;
    public HashMap subobj_descriptions;
    public HashMap subobj_weights_map;
    public HashMap revtimes;
    public HashMap scores;
    public HashMap subobj_scores;
    public HashMap capabilities;
    public HashMap all_dsms;
    public HashMap subobj_measurement_params;
    //Cubesat costs model
    public HashMap instrument_masses;
    public String capability_dat_file;
    public String revtimes_dat_file;
    public String dsm_dat_file;
    public String scores_dat_file;

    static {
        // Metrics for utility and pareto calculations
        pareto_metrics.add("lifecycle_cost");
        pareto_metrics.add("benefit");
        pareto_metric_types.add("SIB");
        pareto_metric_types.add("LIB");
        util_metrics.add("lifecycle_cost");
        util_metrics.add("benefit");
        util_metric_types.add("SIB");
        util_metric_types.add("LIB");
        util_metric_weights.add(0.5);
        util_metric_weights.add(0.5);

        // Instruments & Orbits
        //instrument_list[0] = "SMAP_ANT";
        ninstr = instrument_list.length;
        MAX_TOTAL_INSTR = 5*ninstr;
        norb = orbit_list.length;
        for (int j = 0; j < ninstr; j++) {
            instrument_indexes.put(instrument_list[j], j);
        }
        for (int j = 0; j < norb; j++) {
            orbit_indexes.put(orbit_list[j], j);
        }
    }
    
   
    public Params(String p, String mode, String name, String run_mode, String search_clp) {
        //this.master_xls = master_xls;
        //this.recompute_scores = recompute_scores;
        this.path = p;
        this.req_mode = mode;
        this.name = name;
        this.run_mode = run_mode;
        this.path_save_results = path + "/results";
        this.capability_dat_file = path + "/dat/capabilities.dat"; //capabilities2014-09-09-02-33-13.
        this.revtimes_dat_file = path + "/dat/climate-centric revtimes.dat";
        this.dsm_dat_file = path + "/dat/all_dsms2014-09-14-18-56-03.dat";
        this.scores_dat_file = path + "/dat/scores2014-09-14-18-13-37.dat";
        this.initial_pop = ""; //path + "/results/2014-09-15_08-59-55_test.rs";//2014-09-08_15-51-32_test
        //initial_pop = "";
        // Paths for common xls files
        /*template_definition_xls = path + "/xls/AttributeSet.xls";//used
        mission_analysis_database_xls = path + "/xls/Mission Analysis Database.xls";//used
        capability_rules_xls = path + "/xls/SMAP Instrument Capability Definition.xls";//used
        requirement_satisfaction_xls = path + "/xls/SMAP Requirement Rules.xls";//used
        aggregation_xls = path + "/xls/SMAP Aggregation Rules.xls";//used*/
        this.template_definition_xls = path + "/xls/Climate-centric/Climate-centric AttributeSet.xls";//used
        this.mission_analysis_database_xls = path + "/xls/Climate-centric/Mission Analysis Database.xls";//used
        this.capability_rules_xls = path + "/xls/Climate-centric/Climate-centric Instrument Capability Definition2.xls";//used
        this.requirement_satisfaction_xls = path + "/xls/Climate-centric/Climate-centric Requirement Rules.xls";//used
        this.aggregation_xls = path + "/xls/Climate-centric/Climate-centric Aggregation Rules.xls";//used
        
        // Paths for common clp files
        this.module_definition_clp        = path + "/clp/modules.clp";//used
        this.template_definition_clp      = path + "/clp/templates.clp";//used
        this.functions_clp[0]             = path + "/clp/jess_functions.clp";//used
        this.functions_clp[1]             = path + "/clp/functions.clp";//used
        this.assimilation_rules_clp       = path + "/clp/assimilation_rules.clp";//used
        this.aggregation_rules_clp        = path + "/clp/aggregation_rules.clp";//used
        this.fuzzy_aggregation_rules_clp  = path + "/clp/fuzzy_aggregation_rules.clp";//used
        this.jess_queries_clp             = path + "/clp/queries.clp";//Absent in SMAP
        this.enumeration_rules_clp        = path + "/clp/enumeration_rules.clp";
        this.manifest_rules_clp           = path + "/clp/manifest_rules.clp";
        this.database_completeness_clp    = path + "/clp/database_completeness_rules.clp";
        this.attribute_inheritance_clp    = path + "/clp/attribute_inheritance_rules.clp";
        this.orbit_rules_clp              = path + "/clp/orbit_rules.clp";
        this.capability_rules_clp         = path + "/clp/capability_rules.clp";
        this.satisfaction_rules_clp       = path + "/clp/satisfaction_rules.clp";
        //this.demand_rules_clp             = path + "/clp/demand_rules.clp";
        this.synergy_rules_clp            = path + "/clp/synergy_rules.clp";//Used
        this.explanation_rules_clp        = path + "/clp/explanation_rules.clp";//Used
        this.fuzzy_attribute_clp          = path + "/clp/fuzzy_attribute_rules.clp";
        this.value_aggregation_clp        = path + "/clp/requirement_rules.clp";
        this.requirement_satisfaction_clp = path + "/clp/aggregation_rules.clp";
        this.cost_estimation_rules_clp    = path + "/clp/cost_estimation_rules.clp";
        this.fuzzy_cost_estimation_rules_clp = path + "/clp/fuzzy_cost_estimation_rules.clp";
        this.mass_budget_rules_clp        = path + "/clp/mass_budget_rules.clp";
        this.subsystem_mass_budget_rules_clp = path + "/clp/subsystem_mass_budget_rules.clp";
        this.deltaV_budget_rules_clp      = path + "/clp/deltaV_budget_rules.clp";
        this.adcs_design_rules_clp        = path + "/clp/adcs_design_rules.clp";
        this.propulsion_design_rules_clp  = path + "/clp/propulsion_design_rules.clp";
        this.eps_design_rules_clp         = path + "/clp/eps_design_rules.clp";
        this.sat_configuration_rules_clp  = path + "/clp/sat_configuration_rules.clp";
        this.launch_vehicle_selection_rules_clp = path + "/clp/launch_cost_estimation_rules.clp";
        this.standard_bus_selection_rules_clp = path + "/clp/standard_bus_selection_rules.clp";
        if(search_clp.isEmpty()) {
            this.search_heuristic_rules_clp = path + "/clp/search_heuristic_rules_smap_improveOrbit.clp";
        }
        else {
            this.search_heuristic_rules_clp = path + "/clp/" + search_clp + ".clp";
        }
        this.down_selection_rules_clp     = path + "/clp/down_selection_rules_smap.clp";
        this.adhoc_rules_clp              = path + "/clp/climate_centric_rules.clp";
        this.critique_cost_clp            = path + "/clp/critique/critique_cost.clp";
        this.critique_performance_clp     = path + "/clp/critique/critique_performance.clp";
        this.critique_performance_precalculation_clp = path + "/clp/critique/critique_performance_precalculation.clp";
        this.critique_cost_precalculation_clp = path + "/clp/critique/critique_cost_precalculation.clp";
        this.critique_performance_initialize_facts_clp = path + "/clp/critique/critique_performance_initialize_facts.clp";
        this.critique_cost_initialize_facts_clp = path + "/clp/critique/critique_cost_initialize_facts.clp";

        // Intermediate results
        this.measurements_to_subobjectives = new HashMap();
        this.measurements_to_objectives = new HashMap();
        this.measurements_to_panels = new HashMap();
        this.objectives = new ArrayList();
        this.subobjectives = new ArrayList();
        this.instruments_to_measurements = new HashMap();
        this.instruments_to_subobjectives = new HashMap();
        this.instruments_to_objectives = new HashMap();
        this.instruments_to_panels = new HashMap();

        this.measurements_to_instruments = new HashMap();
        this.subobjectives_to_instruments = new HashMap();
        this.objectives_to_instruments = new HashMap();
        this.panels_to_instruments = new HashMap();

        this.subobjectives_to_measurements = new HashMap();
        this.objectives_to_measurements = new HashMap();
        this.panels_to_measurements = new HashMap();

        this.subobj_measurement_params = new HashMap();
        
        try {
            if (!this.run_mode.equalsIgnoreCase("update_revtimes")) {
                FileInputStream fis = new FileInputStream(revtimes_dat_file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                this.revtimes = (HashMap) ois.readObject();
                ois.close();
            }
            if (!this.run_mode.equalsIgnoreCase("update_capabilities")) {
                FileInputStream fis2 = new FileInputStream(capability_dat_file);
                ObjectInputStream ois2 = new ObjectInputStream(fis2);
                this.capabilities = (HashMap) ois2.readObject();
                ois2.close();
            }
            if (!this.run_mode.equalsIgnoreCase("update_dsms")) {
                FileInputStream fis3 = new FileInputStream(dsm_dat_file);
                ObjectInputStream ois3 = new ObjectInputStream(fis3);
                this.all_dsms = (HashMap) ois3.readObject();
                ois3.close();
            }
            if (!this.run_mode.equalsIgnoreCase("update_scores")) {
                FileInputStream fis3 = new FileInputStream(scores_dat_file);
                ObjectInputStream ois3 = new ObjectInputStream(fis3);
                this.scores = (HashMap) ois3.readObject();
                this.subobj_scores = (HashMap) ois3.readObject();
                ois3.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public String getConfiguration() {
        return "reqs" + this.requirement_satisfaction_xls + ";capas= " + this.capability_rules_xls;
    }

    public String getName() {
        return name;
    }
    
}
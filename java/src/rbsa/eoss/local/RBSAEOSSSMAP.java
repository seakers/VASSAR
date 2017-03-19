/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss.local;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import rbsa.eoss.Architecture;
import rbsa.eoss.ArchitectureEvaluator;
import rbsa.eoss.ArchitectureGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import rbsa.eoss.ArchTradespaceExplorer;
import rbsa.eoss.SearchOptions;
import rbsa.eoss.Result;
import rbsa.eoss.ResultCollection;
import rbsa.eoss.ResultManager;
import rbsa.eoss.SearchPerformance;
import rbsa.eoss.SearchPerformanceComparator;
import java.util.HashMap;
import madkit.action.KernelAction;
import madkit.kernel.Madkit;
import rbsa.eoss.NDSM;
import rbsa.eoss.SearchPerformanceManager;
import rbsa.eoss.local.Params;
//import rbsa.eoss.DSM;
/**
 *
 * @author dani
 */
public class RBSAEOSSSMAP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //PATH
        //String path  = "C:\\Users\\DS925\\Documents\\GitHub\\RBES_EOSS";//RBES SMAP for IEEEAero14 code
//        String path  = "C:\\Users\\Ana-Dani\\Documents\\GitHub\\RBES_EOSS";
      //  String path = "C:\\Users\\Nozomi\\Documents\\CSER_2015";
        String path = "C:\\Users\\Bang\\Desktop\\CSER_2015";
//        String path = "C:\\Users\\SEAK1\\Nozomi\\CSER_2015";
        
        int MODE = 1;
        ArchitectureEvaluator AE = ArchitectureEvaluator.getInstance();
        ArchTradespaceExplorer ATE = ArchTradespaceExplorer.getInstance();
        ResultManager RM = ResultManager.getInstance();

        Params params = null;
        Madkit kernel;
        String search_clps = "";
        switch(MODE) {
            case 1: //1 arch
                params = new Params( path, "FUZZY-ATTRIBUTES", "test","normal",search_clps);//FUZZY or CRISP
                AE.init(1);
                //Architecture arch = ArchitectureGenerator.getInstance().getMaxArch2();
                Architecture arch = ArchitectureGenerator.getInstance().getTestArch();
                //Architecture arch = ArchitectureGenerator.getInstance().getUserEnteredArch();
                Result result1 = AE.evaluateArchitecture(arch,"Slow");
                //Architecture arch2 = arch.addSynergy();
                //System.out.println("NOSYN. Arch " + arch.toBitString() + "=> science = " + result1.getScience() + " cost = " + result1.getCost());
                Architecture arch2 = ArchitectureGenerator.getInstance().getTestArch2();
                Result result2 = AE.evaluateArchitecture(arch2,"Slow");
                //System.out.println("SYN. Arch " + arch.toBitString() + "=> science = " + result2.getScience() + " cost = " + result2.getCost());
                RM.saveResultCollection(new ResultCollection(AE.getResults()));
                System.out.println("DONE");
                break;
            case 2://Full factorial 7 CPUS with random population
                params = new Params( path, "FUZZY-ATTRIBUTES", "test","normal",search_clps);//FUZZY or CRISP
                ArrayList<Architecture> population = ArchitectureGenerator.getInstance().generateRandomPopulation(80);
                AE.init(1);
                AE.setPopulation( population );
                //AE.evalMinMax();
                AE.evaluatePopulation();  
                ResultCollection rc = new ResultCollection(AE.getResults());
                RM.saveResultCollection(rc);
                System.out.println("DONE");
                break;
            case 3://Search
                int POP_SIZE = 200;
                int MAX_SEARCH_ITS = 4;
                params = new Params( path, "FUZZY-ATTRIBUTES", "test","normal","search_heuristic_rules_smap_127");
                ResultCollection c = null;
                ArrayList<Architecture> init_pop = ArchitectureGenerator.getInstance().getInitialPopulation(POP_SIZE);
                for (int i = 0;i<20;i++) {
                    if (i>0) {
                        params = new Params( path, "FUZZY-ATTRIBUTES", "test","normal","search_heuristic_rules_smap_127");//FUZZY or CRISP
                        if (init_pop != null)
                            init_pop = c.getPopulation();
                    }
                    AE.clear();
                    AE.init(8);
                    AE.evalMinMax();
                    ATE.clear();
                    ATE.setTerm_crit(new SearchOptions(POP_SIZE,MAX_SEARCH_ITS,0.5,0.1,0.5,init_pop));
                    ATE.search_NSGA2();
                    System.out.println("PERF: " + ATE.getSp().toString());
                    c =  new ResultCollection(AE.getResults());//
                    RM.saveResultCollection(c);
                }
                
                System.out.println("DONE");
                break;
            case 4://Explore different heuristics
                params = new Params( path, "CRISP-ATTRIBUTES", "test","normal","search_heuristic_rules_smap_2");//FUZZY or CRISP
                AE.init(3);
                AE.evalMinMax();
                ArrayList<SearchPerformance> perfs = new ArrayList<SearchPerformance>();
                int NUM_ITS = 3;
                POP_SIZE = 100;
                MAX_SEARCH_ITS = 5;
                SearchPerformance bestPerf = null;
                for(int i = 0;i<NUM_ITS;i++) {
                    ATE.setTerm_crit(new SearchOptions(POP_SIZE,MAX_SEARCH_ITS,0.5,0.1,0.5,null));
                    ATE.search_NSGA2();
                    RM.saveResultCollection(new ResultCollection(AE.getResults()));
                    SearchPerformance spi = ATE.getSp();
                    perfs.add(spi); 
                    int best = spi.compareTo(bestPerf);
                    if (best == 1) {
                        bestPerf = spi;
                    }
                }
                System.out.println("BEST PERF AFTER " + NUM_ITS + " its: " + ATE.getSp().toString());
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd-HH-mm-ss" );
                    String stamp = dateFormat.format( new Date() );
                    FileOutputStream file = new FileOutputStream( Params.path_save_results + "\\perfs_" + stamp + ".dat");
                    ObjectOutputStream os = new ObjectOutputStream( file );
                    os.writeObject( perfs );
                    os.close();
                    file.close();
                } catch (Exception e) {
                    System.out.println( e.getMessage() );
                }
                System.out.println("DONE");
                break;
            case 5://Update DSMs
                params = new Params( path, "FUZZY-ATTRIBUTES", "test","update_dsms",search_clps);//FUZZY or CRISP
                AE.init(8);
                //AE.recomputeAllDSM();
                AE.recomputeNDSM(2);
                AE.clearResults();
                AE.recomputeNDSM(3);
                AE.clearResults();
                AE.recomputeNDSM(Params.ninstr);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd-HH-mm-ss" );
                    String stamp = dateFormat.format( new Date() );
                    FileOutputStream file = new FileOutputStream( Params.path_save_results + "\\all_dsms" + stamp + ".dat");
                    ObjectOutputStream os = new ObjectOutputStream( file );
                    os.writeObject( AE.getDsm_map() );
                    os.close();
                    file.close();
                } catch (Exception e) {
                    System.out.println( e.getMessage() );
                }
                System.out.println("DONE");
                break;
            case 6://Update capabilities file
                params = new Params( path, "FUZZY-ATTRIBUTES", "test","update_capabilities",search_clps);//FUZZY or CRISP
                AE.init(8);
                AE.precomputeCapabilities();                
                try{
                    SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd-HH-mm-ss" );
                    String stamp = dateFormat.format( new Date() );
                    FileOutputStream fos = new FileOutputStream(Params.path_save_results + "\\capabilities" + stamp + ".dat");
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(AE.getCapabilities());
                    oos.close();
                    fos.close();
                } catch(Exception e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("DONE");
                break;
             case 7://Update scores file
                params = new Params( path, "FUZZY-ATTRIBUTES", "test","update_scores",search_clps);//FUZZY or CRISP
                AE.init(8);
                AE.recomputeScores(1);
                AE.clearResults();
                AE.recomputeScores(2);
                AE.clearResults();
                AE.recomputeScores(3);
                AE.clearResults();
                AE.recomputeScores(Params.ninstr);
                AE.clearResults();
                AE.recomputeScores(Params.ninstr-1);
                try{
                    SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd-HH-mm-ss" );
                    String stamp = dateFormat.format( new Date() );
                    FileOutputStream fos = new FileOutputStream(Params.path_save_results + "\\scores" + stamp + ".dat");
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(AE.getScores());
                    oos.writeObject(AE.getSubobj_scores());
                    oos.close();
                    fos.close();
                } catch(Exception e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("DONE");
                break;
            case 8://KISA
                NUM_ITS = 1;//Monte Carlo for a given search heuristics
                POP_SIZE = 100;
                MAX_SEARCH_ITS = 50;//Max its for optimization termination criteria
                ArrayList<Integer> look_at = new ArrayList<Integer>();
                //look_at.add(1);look_at.add(2);look_at.add(28);look_at.add(32);look_at.add(60);
                //look_at.add(1);look_at.add(2);look_at.add(1);//514 510 crossover + all KI with bestNeighbor, crossover only, random (control)
                look_at.add(2);
                //for (int i = 1;i<Math.pow(2,6);i++)
                //    look_at.add(i);
                long t0 = System.currentTimeMillis();
                int nrest = look_at.size()*NUM_ITS;
                SearchPerformanceManager spm = SearchPerformanceManager.getInstance();
                for (int i = 0;i<look_at.size();i++) {
                    int num = look_at.get(i);
                    params = new Params( path, "CRISP-ATTRIBUTES", "test","normal","search_heuristic_rules_smap_" + num );//FUZZY or CRISP
                    AE.init(7);
                    AE.evalMinMax();
                    perfs = new ArrayList<SearchPerformance>(); 
                    bestPerf = new SearchPerformance();
                    long t1 = System.currentTimeMillis();
                    for(int j = 0;j<NUM_ITS;j++) {
                        long t2 = System.currentTimeMillis();
                        ATE.setTerm_crit(new SearchOptions(POP_SIZE,MAX_SEARCH_ITS,0.5,0.1,0.5,null));
                        ATE.search_NSGA2();
                        RM.saveResultCollection(new ResultCollection(AE.getResults()));
                        SearchPerformance spi = new SearchPerformance(ATE.getSp());
                        spm.saveSearchPerformance(spi);
                        perfs.add(spi); 
                        int best = spi.compareTo(bestPerf);
                        if (best == 1) {
                            bestPerf = new SearchPerformance(spi);
                        }
                        long t3 = System.currentTimeMillis();
                        nrest--;
                        System.out.println("Heuristics " + i + " Iter " + j + " it time " + String.valueOf((t3-t2)/60000) + " heur time " + String.valueOf((t3-t1)/60000) + " total time " +  String.valueOf((t3-t0)/60000) + " expect end in " + String.valueOf((t3-t0)/60000*nrest));
                    }
                    long t4 = System.currentTimeMillis();
                    System.out.println("BEST PERF" + String.valueOf(num) + " AFTER " + NUM_ITS + " its: " + bestPerf.toString());
                    System.out.println("Heuristics "  + i + " heur time " + String.valueOf((t4-t1)/60000) + " total time " +  String.valueOf((t4-t0)/60000) + " expect end in " + String.valueOf((t4-t0)/60000*(look_at.size()-i-1)));
                    SearchPerformanceComparator spc = new SearchPerformanceComparator(String.valueOf(num),perfs);
                    
                    spm.saveSearchPerformanceComparator(spc);

                }   
                System.out.println("DONE");
                break;
            case 9://local search
                params = new Params( path, "FUZZY-ATTRIBUTES", "test","normal","search_heuristic_rules_smap_2");
                ArrayList<Architecture> init_popu = ResultManager.getInstance().loadResultCollectionFromFile(Params.initial_pop).getPopulation();
                AE.setPopulation(ArchitectureGenerator.getInstance().localSearch(init_popu));
                AE.init(8);
                AE.evalMinMax();
                AE.evaluatePopulation();
                RM.saveResultCollection(new ResultCollection(AE.getResults()));
                System.out.println("DONE");
                break;
            default:
                System.out.println("Choose a mode between 1 and 12");
        }
        
    }
}
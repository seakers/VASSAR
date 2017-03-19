/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import jess.*;
import java.util.Random;
import java.util.Arrays;
import java.util.Collections;
import rbsa.eoss.local.Params;
/**
 *
 * @author Ana-Dani
 */
public class ArchTradespaceExplorer {
    private static ArchTradespaceExplorer instance = null;
    private ArrayList<Architecture> current_population;
    private ArrayList<Architecture> current_best_archs;
    private Stack<Result> results;
    private int nits;
    private SearchOptions term_crit;
    private Random rnd;
    private SearchPerformance sp;
    
    private ArchTradespaceExplorer () {
        results = new Stack<Result>();
        current_population = null;
        current_best_archs = null;
        nits = 0;
        term_crit = null;
        rnd = new Random();
        sp = null;
    }
    public void clear() {
        results = new Stack<Result>();
        current_population = null;
        current_best_archs = null;
        nits = 0;
        term_crit = null;
        rnd = new Random();
        sp = null;
    }
    public static ArchTradespaceExplorer getInstance()
    {
        if( instance == null ) 
        {
            instance = new ArchTradespaceExplorer();
        }
        return instance;
    }


    public void search_NSGA2() {
        
        Boolean converged = false;
        //Init population
        current_population = term_crit.getInit_population();
        ArchitectureEvaluator ArchEval = ArchitectureEvaluator.getInstance();
        nits = 0;
        sp = new SearchPerformance();
        while (!converged) {

            //extend population using search rules
            System.out.println("Evaluating...");
            long t0 = System.currentTimeMillis();
            //extendPopulation();
            extendPopulationWithCooperation();
            //evaluate extended population
            ArchEval.clearResults();
            ArchEval.setPopulation(current_population);
            ArchEval.evaluatePopulation();
            //results = ArchEval.getResults();
            
            //Down-select population based on pareto ranking and crowding distance
            selection_NSGA2();
            
            //Check termination criteria
            nits++;
            sp.updateSearchPerformance(results,nits);
            converged = term_crit.checkTerminationCriteria(sp);
            System.out.println(sp.toString());
            long t1 = System.currentTimeMillis();
            System.out.println("Generation " + nits + " done in " + String.valueOf((t1-t0)/60000) + ". Approx time remaining " + String.valueOf((t1-t0)/60000*(term_crit.getMAX_ITS()-nits-1)));
        }
    }

    
    
    public void down_select() {
        Resource res = ArchitectureEvaluator.getInstance().getResourcePool().getResource();
        
        Rete r = res.getRete();
        try {
            
            assertArchs(res,current_population);//check that archs contain pareto rankings
            r.eval("(assert (DOWN-SELECTION::MIN-PARETO-RANK (min-pareto-rank 3)))");
            r.eval("(focus DOWN-SELECTION)");
            r.run();
            current_best_archs = retrieveArchs(res);
        } catch (Exception e) {
            System.out.println("EXC in ArchTradespaceExplorer:down_select: " + e.getClass() + " " + e.getMessage());e.printStackTrace();
            ArchitectureEvaluator.getInstance().getResourcePool().freeResource(res);
        }
        
        ArchitectureEvaluator.getInstance().getResourcePool().freeResource(res);
    }
    public void generateNextPopulation() {
        if (nits == 0) {
            current_population = ArchitectureGenerator.getInstance().generateRandomPopulation(term_crit.getPopulation_size());
        } else {
            Resource res = ArchitectureEvaluator.getInstance().getResourcePool().getResource();
            Rete r = res.getRete();
            try {
                selection();
                assertArchs(res,current_population);
                r.eval("(focus SEARCH-HEURISTICS)");
                r.run();
                current_population = retrieveArchs(res);
            } catch (Exception e) {
                System.out.println("EXC in ArchTradespaceExplorer:generateNextPopulation: " + e.getClass() + " " + e.getMessage());
                e.printStackTrace();
                ArchitectureEvaluator.getInstance().getResourcePool().freeResource(res);
            }
            ArchitectureEvaluator.getInstance().getResourcePool().freeResource(res);
        }
        
        
    }
    public void extendPopulation() {
        if(nits>0) {
            //Mark small fraction for random mutation
            for (int i = 0;i<current_population.size();i++) {
                if(rnd.nextDouble() < term_crit.getMutation_rate()) 
                    current_population.get(i).setMutate("yes");
            }

            //Mark everyone for improvement
            Collections.shuffle(current_population);
            /*long max = java.lang.Math.round(term_crit.getImprovement_rate()*current_population.size());
            for (int i = 0;i<max;i++) {
                current_population.get(i).setCrossover("yes");
            }*/
            
            Resource res = ArchitectureEvaluator.getInstance().getSearchResource();
            Rete r = res.getRete();
            ArrayList<String> list = null;
            try {
                r.setFocus("DATABASE");
                r.run();
                ArrayList<Fact> ff = res.getQueryBuilder().makeQuery("SEARCH-HEURISTICS::list-improve-heuristics");
                Fact f = ff.get(0);
                ValueVector vv = f.getSlotValue("list").listValue(r.getGlobalContext());
                list = res.getM().JessList2ArrayList(vv, r);
            } catch (Exception e) {
                System.out.println("EXC in ArchTradespaceExplorer:extendPopulation: " + e.getClass() + " " + e.getMessage());
                e.printStackTrace();
                ArchitectureEvaluator.getInstance().freeSearchResource();
            }
            int n = 0;
            String impr;
            for (long i = 0;i<current_population.size();i++) {
                if (list==null)
                    impr = "yes";
                else
                    impr = list.get(n);
                
                current_population.get((int)i).setImprove(impr);
                if (list!=null) {
                    n++;
                    if(n==list.size())
                        n = 0;
                }

            }

            //Apply heuristics, this needs to keep original architectures and produce new ones.
            
            try {
                assertArchs(res,current_population);
                //r.eval("(watch rules)");r.eval("(watch facts)");
                r.eval("(focus SEARCH-HEURISTICS)");
                r.run();
                //r.eval("(unwatch all)");
                current_population = retrieveArchs(res);
            } catch (Exception e) {
                System.out.println("EXC in ArchTradespaceExplorer:generateNextPopulation: " + e.getClass() + " " + e.getMessage());
                e.printStackTrace();
                ArchitectureEvaluator.getInstance().freeSearchResource();
            }
            ArchitectureEvaluator.getInstance().freeSearchResource();
        } else {
            current_population.addAll(ArchitectureGenerator.getInstance().generateRandomPopulation(term_crit.getPopulation_size()));
        }
    }
    public void extendPopulationWithCooperation() {
        if(nits>0) {
            //Mark small fraction for random mutation
            for (int i = 0;i<current_population.size();i++) {
                if(rnd.nextDouble() < term_crit.getMutation_rate()) 
                    current_population.get(i).setMutate("yes");
            }

            Collections.shuffle(current_population);
            Resource res = ArchitectureEvaluator.getInstance().getSearchResource();
            Rete r = res.getRete();
            String str_list = "";
            try {
                r.setFocus("DATABASE");
                r.run();
                ArrayList<Fact> ff = res.getQueryBuilder().makeQuery("SEARCH-HEURISTICS::list-improve-heuristics");
                Fact f = ff.get(0);
                ValueVector vv = f.getSlotValue("list").listValue(r.getGlobalContext());
                ArrayList<String> list = res.getM().JessList2ArrayList(vv, r);
                for (String heur:list)
                    str_list = str_list + " " + heur;//Initial extra space is irrelevant
            } catch (Exception e) {
                System.out.println("EXC in ArchTradespaceExplorer:extendPopulation: " + e.getClass() + " " + e.getMessage());
                e.printStackTrace();
                ArchitectureEvaluator.getInstance().freeSearchResource();
            }
            String impr;
            for (long i = 0;i<current_population.size();i++) {
                if (str_list.isEmpty())
                    impr = "crossover1point";
                else
                    impr = str_list;
                //current_population.get((int)i).setImprove(impr);
                current_population.get((int)i).setHeuristics_to_apply(impr);
            }

            //Apply heuristics, this needs to keep original architectures and produce new ones.
            
            try {
                assertArchs(res,current_population);
                //r.eval("(watch rules)");r.eval("(watch facts)");
                r.eval("(focus SEARCH-HEURISTICS)");
                r.run();
                //r.eval("(unwatch all)");
                current_population = retrieveArchs(res);
                System.out.println("Population size after expansion is " + current_population.size());
            } catch (Exception e) {
                System.out.println("EXC in ArchTradespaceExplorer:generateNextPopulation: " + e.getClass() + " " + e.getMessage());
                e.printStackTrace();
                ArchitectureEvaluator.getInstance().freeSearchResource();
            }
            ArchitectureEvaluator.getInstance().freeSearchResource();
        } else {
            current_population.addAll(ArchitectureGenerator.getInstance().generateRandomPopulation(term_crit.getPopulation_size()));
        }
    }
    public void selection() {
        //Mutation
        for (int i = 0;i<current_population.size();i++) {
            if(rnd.nextDouble() < term_crit.getMutation_rate()) 
                current_population.get(i).setMutate("yes");
        }
        
        //Crossover
        int[] rankings = nonDominatedSort();
        for (int i = 0;i<current_population.size();i++) {
            current_population.get(i).setParetoRanking(rankings[i]);
            if(rankings[i]<=3) { 
                current_population.get(i).setCrossover("yes");
            }
        }
    }
    public void selection_NSGA2() {
        ArrayList<Architecture> new_population = new ArrayList<Architecture>();
        //non-dominated sorting, returns fronts
        HashMap<Integer,ArrayList<Architecture>> fronts = nonDominatedSorting(true);
        
        //take n first fronts so as to leave some room
        int i = 1;
        while(new_population.size() + fronts.get(i).size() <= term_crit.getPopulation_size() && i < fronts.size()) {
            new_population.addAll(fronts.get(i));
            i++;
        }
        
        //Take remaining archs from sorted next front
        int NA = term_crit.getPopulation_size() - new_population.size();
        if (NA>0) {
            ArrayList<Architecture> sorted_last_front = new ArrayList<Architecture>();
            sorted_last_front.addAll(fronts.get(i));
            computeCrowdingDistance(sorted_last_front);
            Collections.sort(sorted_last_front,Architecture.ArchCrowdDistComparator);
            ArrayList<Architecture> partial_sorted_last_front = new ArrayList<Architecture> ( sorted_last_front.subList(0, NA));
            new_population.addAll(partial_sorted_last_front);
        }
        //Update population and results
        current_population = new_population;
        results.clear();
        for (Architecture arch:current_population) {
            results.push(arch.getResult());
        }
    }
    public void computeCrowdingDistance(ArrayList<Architecture> front) {
        
        int nsol = front.size();

        //Science
        Collections.sort(front,Architecture.ArchScienceComparator);
        front.get(0).getResult().setCrowdingDistance(1000);
        front.get(front.size()-1).getResult().setCrowdingDistance(1000);
        for (int i = 1;i<nsol-1;i++) 
            front.get(i).getResult().setCrowdingDistance(
                    front.get(i).getResult().getCrowdingDistance() + Math.abs(
                    (front.get(i+1).getResult().getScience() - front.get(i-1).getResult().getScience())/(Params.max_science-Params.min_science))) ;
        
        //Cost
        Collections.sort(front,Architecture.ArchCostComparator);
        front.get(0).getResult().setCrowdingDistance(1000);
        front.get(front.size()-1).getResult().setCrowdingDistance(1000);
        for (int i = 1;i<nsol-1;i++) 
            front.get(i).getResult().setCrowdingDistance(
                    front.get(i).getResult().getCrowdingDistance() + Math.abs(
                    (front.get(i+1).getResult().getCost() - front.get(i-1).getResult().getCost())/(Params.max_cost-Params.min_cost))) ;
    }
    public HashMap<Integer,ArrayList<Architecture>> nonDominatedSorting(boolean compute_all_fronts) {

        HashMap<Integer,ArrayList<Architecture>> fronts = new HashMap<Integer,ArrayList<Architecture>>();//archs in front i
        HashMap<Architecture,ArrayList<Integer>> dominates = new HashMap<Architecture,ArrayList<Integer>>();//indexes of archs that arch dominates
        int[] dominationCounters = new int[current_population.size()];//number of archs that dominate arch i
        for (int i = 0;i<dominationCounters.length;i++)
            dominationCounters[i] = 0;
        
        for (int i = 0;i<current_population.size();i++) {
            Architecture a1 = current_population.get(i);
            Result r1 = a1.getResult();
            for (int j = 0;j<current_population.size();j++) {
                Architecture a2 = current_population.get(j);
                Result r2 = a2.getResult();
                int r1domr2 = dominates(r1,r2);
                if(r1domr2==1) {//if a1 dominates a2
                    ArrayList<Integer> existing = dominates.get(a1);
                    if(existing == null)
                        existing = new ArrayList<Integer>();
                    existing.add(j);//add j to indexes of archs that arch a1 dominates
                    dominates.put(a1,existing);
                } else if(r1domr2==-1) {
                    dominationCounters[i]++;//increment counter of archs that dominate a1
                }
            }
            if(dominationCounters[i]==0) {//no one dominates arch i
                ArrayList<Architecture> existing = fronts.get(1);
                if(existing == null)
                    existing = new ArrayList<Architecture>();
                existing.add(a1);
                //System.out.println("Arch " + i + " added to Front 1");
                fronts.put(1,existing);//add a1 to first front
            }
        }
        if(!compute_all_fronts)
            return fronts;
        int i = 1;
        ArrayList<Architecture> nextFront = fronts.get(i);
        while(!nextFront.isEmpty()) {
            nextFront = new ArrayList<Architecture>();
            for (int j = 0;j<fronts.get(i).size();j++) {//iterate over archs of front i
                Architecture a1 = fronts.get(i).get(j);//arch j of front i
                ArrayList<Integer> doms = dominates.get(a1);//set of solutions dominated by a1
                if (doms!=null)  {
                    for (int k = 0;k<doms.size();k++) {
                        Architecture a2 = current_population.get(doms.get(k));
                        dominationCounters[doms.get(k)]--;//decrease domination counter of arch a2 since a1 is removed from tradespace
                        if( dominationCounters[doms.get(k)] <= 0) {
                            nextFront.add(a2);
                            //System.out.println("Arch " + doms.get(k) + " added to Front " + (i+1));
                        }    
                    }
                }
            }
            i++;
            if (!nextFront.isEmpty())
                fronts.put(i,nextFront);
        }
        return fronts;
    }
    
    public int[] nonDominatedSort() {

        HashMap<Integer,ArrayList<Result>> fronts = new HashMap<Integer,ArrayList<Result>>();
        HashMap<Result,ArrayList<Integer>> dominates = new HashMap<Result,ArrayList<Integer>>();
        int[] dominationCounters = new int[current_population.size()];
        int[] rankings = new int[current_population.size()];
        for (int i = 0;i<dominationCounters.length;i++)
            dominationCounters[i] = 0;
        
        for (int i = 0;i<current_population.size();i++) {
            Result r1 = current_population.get(i).getResult();
            for (int j = 0;j<current_population.size();j++) {
                Result r2 = current_population.get(j).getResult();
                int r1domr2 = dominates(r1,r2);
                if(r1domr2==1) {
                    ArrayList<Integer> existing = dominates.get(r1);
                    if(existing == null)
                        existing = new ArrayList<Integer>();
                    existing.add(j);
                    dominates.put(r1,existing);
                } else if(r1domr2==-1) {
                    dominationCounters[i]++;
                }
            }
            if(dominationCounters[i]==0) {
                rankings[i] = 1;
                ArrayList<Result> existing = fronts.get(1);
                if(existing == null)
                    existing = new ArrayList<Result>();
                existing.add(r1);
                fronts.put(1,existing);
            }
        }
        int i = 1;
        while(!fronts.get(i).isEmpty()) {
            ArrayList<Result> nextFront = new ArrayList<Result>();
            for (int j = 0;j<fronts.get(i).size();j++) {//iterate over archs of front i
                Result r1 = fronts.get(i).get(j);//arch j of front i
                ArrayList<Integer> doms = dominates.get(r1);//set of solutions dominated by r1
                if (doms!=null)  {
                    for (int k = 0;k<doms.size();k++) {
                        Result r2 = current_population.get(doms.get(k)).getResult();
                        dominationCounters[doms.get(k)]--;
                        if( dominationCounters[doms.get(k)] <= 0) {
                            rankings[doms.get(k)] = i+1;
                            nextFront.add(r2);
                        }    
                    }
                }
            }
            i++;
            fronts.put(i,nextFront);
        }
        return rankings;
    }
    public int dominates(Result r1,Result r2) {
        // Feasibility before fitness
        if (r1.getArch().isFeasibleAssignment() && !r2.getArch().isFeasibleAssignment())
            return 1;
        if (!r1.getArch().isFeasibleAssignment() && r2.getArch().isFeasibleAssignment())
            return -1;
        if (!r1.getArch().isFeasibleAssignment() && !r2.getArch().isFeasibleAssignment())
            if(r1.getArch().getTotalInstruments() < r2.getArch().getTotalInstruments())
                return 1;
            else if(r1.getArch().getTotalInstruments() > r2.getArch().getTotalInstruments()) 
                return -1;
            else //Both are infeasible, and both to teh same degree (i.e., both have the same number of total instruments)
                return 0;
        
        //Both feasible ==> Sorting by fitness
        double x1 = r1.getScience() - r2.getScience();
        double x2 = r1.getCost() - r2.getCost();
        if((x1>=0 && x2<=0) && !(x1==0 && x2==0)) 
            return 1;
        else if((x1<=0 && x2>=0) && !(x1==0 && x2==0))
            return -1;
        else return 0;
    } 
    public void assertArchs(Resource res, ArrayList<Architecture> archs) {
        try {
            Rete r = res.getRete();
            r.reset();
            for (int i = 0;i<archs.size();i++) 
                r.assertString(archs.get(i).toFactString());
        } catch (Exception e) {
            System.out.println("EXC in ArchTradespaceExplorer:assertArchs: " + e.getClass() + " " + e.getMessage());
            e.printStackTrace();
            ArchitectureEvaluator.getInstance().freeSearchResource();
        }
        
    }
    public ArrayList<Architecture> retrieveArchs(Resource res)
    {
        ArrayList<Architecture> archs = new ArrayList<Architecture>();
        results.clear();
        try {
            Rete r = res.getRete();
            ArrayList<Fact> facts = res.getQueryBuilder().makeQuery("MANIFEST::ARCHITECTURE");
            for (int i = 0;i<facts.size();i++) {
                Fact f = facts.get(i);
                String bs = f.getSlotValue("bitString").stringValue(r.getGlobalContext());
                int nsat = f.getSlotValue("num-sats-per-plane").intValue(r.getGlobalContext());
                Architecture arch = new Architecture(bs,nsat);
                double science = f.getSlotValue("benefit").floatValue(r.getGlobalContext());
                double cost = f.getSlotValue("lifecycle-cost").floatValue(r.getGlobalContext());
                int pr = f.getSlotValue("pareto-ranking").intValue(r.getGlobalContext());
                Result result = new Result(arch,science,cost,pr);
                arch.setResult(result);
                results.push(result);
                archs.add(arch);
            }
        } catch (Exception e) {
            System.out.println("EXC in ArchTradespaceExplorer:retrieveArchs: " + e.getClass() + " " + e.getMessage());
            e.printStackTrace();
            ArchitectureEvaluator.getInstance().freeSearchResource();
        }
        return archs;
    }            
    

    public Boolean check_termination_criteria() {
        Boolean converged = false;
        if (nits>term_crit.getMAX_ITS()) {
            converged = true;
        }
        return converged;
       
    }
    

    public SearchPerformance getSp() {
        return sp;
    }
    public void setTerm_crit(SearchOptions term_crit) {
        this.term_crit = term_crit;
    }

    public ArrayList<Architecture> getCurrent_population() {
        return current_population;
    }

    public ArrayList<Architecture> getCurrent_best_archs() {
        return current_best_archs;
    }

    public Stack<Result> getResults() {
        return results;
    }

    public int getNits() {
        return nits;
    }

    public SearchOptions getTerm_crit() {
        return term_crit;
    }
    
     public void setCurrent_population(ArrayList<Architecture> current_population) {
        this.current_population = current_population;
    }
    
}

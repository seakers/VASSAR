/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

import java.util.Stack;
import rbsa.eoss.local.Params;
import java.util.TreeMap;
import java.util.Map;
import java.util.Iterator;
import java.io.Serializable;
import java.util.ArrayList;
/**
 *
 * @author Ana-Dani
 */
public class SearchPerformance implements Serializable {
    private Result cheapest_max_benefit_arch;
    private Stack<Result> results;
    private ArrayList<Result> current_pareto_front;
    private double avg_pareto_distance;
    private int narch;
    private int narch_front;
    private Float[] benefits;
    private Float[] costs;
    private ArrayList<Result> history_cheapest_max_benefit_archs;
    private ArrayList<Double> history_avg_pareto_distance;
    private ArrayList<ArrayList<Result>> history_fronts;
    int nits;
    
    //Constructors
    public SearchPerformance() {
        results = null;
        narch = 0;
        narch_front = 0;
        cheapest_max_benefit_arch = null;
        avg_pareto_distance = 0.0;
        benefits = null;
        costs = null;
        history_cheapest_max_benefit_archs = new ArrayList<Result>();
        history_avg_pareto_distance = new ArrayList<Double>();
        nits = 0;
        current_pareto_front = new ArrayList<Result>();
        history_fronts = new ArrayList<ArrayList<Result>>();
    }
    public SearchPerformance(Stack<Result> results, int nits)  {
        this.results = results;
        narch = results.size();
        current_pareto_front = compute_pareto_front(results);
        narch_front = current_pareto_front.size();
        getBenefits();
        getCosts();
        cheapest_max_benefit_arch = compute_cheapest_max_benefit_arch();
        history_cheapest_max_benefit_archs.add(cheapest_max_benefit_arch);
        avg_pareto_distance = compute_avg_pareto_distance(); 
        history_avg_pareto_distance.add(avg_pareto_distance);
        this.nits = nits;
        history_fronts.add(current_pareto_front);
    }
    public SearchPerformance(SearchPerformance other) {
        this.results = other.results;
        this.narch = other.narch;
        this.cheapest_max_benefit_arch = other.cheapest_max_benefit_arch;
        this.avg_pareto_distance = other.avg_pareto_distance;
        this.history_avg_pareto_distance = new ArrayList<Double>();
        this.history_avg_pareto_distance.addAll(other.history_avg_pareto_distance);
        this.history_cheapest_max_benefit_archs = new ArrayList<Result>();
        this.history_cheapest_max_benefit_archs.addAll(other.history_cheapest_max_benefit_archs);
        this.nits = other.nits;
        this.benefits = other.benefits;
        this.costs = other.costs;
        this.current_pareto_front = other.current_pareto_front;
        this.history_fronts = new ArrayList<ArrayList<Result>>();
        this.history_fronts.addAll(other.history_fronts);
    }
    
    //Public methods
    public void updateSearchPerformance(Stack<Result> results, int nits) {
        this.results = results;
        narch = results.size();
        current_pareto_front = compute_pareto_front(results);
        narch_front = current_pareto_front.size();
        getBenefits();
        getCosts();
        cheapest_max_benefit_arch = compute_cheapest_max_benefit_arch();
        avg_pareto_distance = compute_avg_pareto_distance();
        history_cheapest_max_benefit_archs.add(cheapest_max_benefit_arch);
        history_avg_pareto_distance.add(avg_pareto_distance);
        history_fronts.add(current_pareto_front);
        this.nits = nits;
    }    
    @Override
    public String toString() {
        String str;
        if (cheapest_max_benefit_arch == null)
            str = "SearchPerformance after= " + nits + " its: cheapest max benefit is null";
        else if (avg_pareto_distance == Double.NaN)
            str = "SearchPerformance after= " + nits + " its: cheapest max benefit is null";
        else
            str = "SearchPerformance after= " + nits + " its: avg_pareto_distance: " + avg_pareto_distance + 
                " cheapest_max_benefit_arch: " + cheapest_max_benefit_arch.getScience() + " " + cheapest_max_benefit_arch.getCost() + " " + cheapest_max_benefit_arch.toString();
        return str;
    }
    public int compareTo(SearchPerformance other) {
        if (other == null || other.getCheapest_max_benefit_arch() == null) return 1;
        if(cheapest_max_benefit_arch==null)
            System.out.println("hi");
        if (cheapest_max_benefit_arch.getCost() < other.getCheapest_max_benefit_arch().getCost())  {
            return 1;
        } else if (cheapest_max_benefit_arch.getCost() > other.getCheapest_max_benefit_arch().getCost())  {
            return -1;
        } else return 0;
        
    }
    public final Float[] getBenefits() {
        benefits = new Float[narch_front];
        for (int i = 0;i<narch_front;i++) {
            benefits[i] = new Float(current_pareto_front.get(i).getScience());
        }
        return benefits;
    }
    public final Float[] getCosts() {
        costs = new Float[narch_front];
        for (int i = 0;i<narch_front;i++) {
            costs[i] = new Float(current_pareto_front.get(i).getCost());
        }
        return costs;
    }
    
    //Private methods
    private Result compute_cheapest_max_benefit_arch() {
        Result res = null;
        double min_cost = 1e10;
        double max_science = 0;
        for (int i = 0;i<narch_front;i++) {
            Result re = current_pareto_front.get(i);
            if (re.getScience() > max_science && re.getCost() < min_cost) {
                min_cost = re.getCost();
                res = re;
            }
        }
        return res;
    }
    private double compute_avg_pareto_distance() {
        //sort sciences
        Map<Float, Integer> map = new TreeMap<Float, Integer>();
        for (int i = 0; i < benefits.length; ++i) {
            map.put(benefits[i], i);
        }
        Iterator indices = map.values().iterator();
        //compute gaps between consecutive archs
        Result old = new Result(null,0.0,0.0);
        int i = 0;
        double average = 0.0;
        while(indices.hasNext()) {
            Integer index = (Integer)indices.next();
            Result res = current_pareto_front.get(index);
            double distance = res.distance(old);
            average+= distance;
            old = res;
            i++;
        }
        //return average
        return average/i;
    }
    private ArrayList<Result> compute_pareto_front(Stack<Result> stack) {
        ArrayList<Result> thefront = new ArrayList<Result>();
        for (int i = 0;i<stack.size();i++) {
            Result r1 = stack.get(i);
            boolean dominated = false;
            for (int j = 0;j<stack.size();j++) {
                if(r1.dominates(stack.get(j))==-1) {
                    dominated = true;
                    break;//dominated
                }
            }
            if(!dominated) {
                thefront.add(r1);
            }
        }
        return thefront;
    }
    
    
    //Getters and setters
    public Result getCheapest_max_benefit_arch() {
        return cheapest_max_benefit_arch;
    }
    public Stack<Result> getResults() {
        return results;
    }
    public double getAvg_pareto_distance() {
        return avg_pareto_distance;
    }
    public int getNarch() {
        return narch;
    }
    public ArrayList<Result> getHistory_cheapest_max_benefit_archs() {
        return history_cheapest_max_benefit_archs;
    }
    public void setHistory_cheapest_max_benefit_archs(ArrayList<Result> history_cheapest_max_benefit_archs) {
        this.history_cheapest_max_benefit_archs = history_cheapest_max_benefit_archs;
    }
    public ArrayList<Double> getHistory_avg_pareto_distance() {
        return history_avg_pareto_distance;
    }
    public void setHistory_avg_pareto_distance(ArrayList<Double> history_avg_pareto_distance) {
        this.history_avg_pareto_distance = history_avg_pareto_distance;
    }
    public ArrayList<Result> getCurrent_pareto_front() {
        return current_pareto_front;
    }
    public void setCurrent_pareto_front(ArrayList<Result> current_pareto_front) {
        this.current_pareto_front = current_pareto_front;
    }
    public ArrayList<ArrayList<Result>> getHistory_fronts() {
        return history_fronts;
    }
    public void setHistory_fronts(ArrayList<ArrayList<Result>> history_fronts) {
        this.history_fronts = history_fronts;
    }
   
}

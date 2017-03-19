/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

/**
 *
 * @author Ana-Dani
 */
import java.util.ArrayList;
import jess.*;
import rbsa.eoss.local.Params;
import java.util.TreeMap;
public class Result implements java.io.Serializable {
    private double science;
    private double cost;
    private double norm_science;
    private double norm_cost;
    private ArrayList subobjective_scores;
    private ArrayList objective_scores;
    private ArrayList panel_scores;
    private FuzzyValue fuzzy_science;
    private FuzzyValue fuzzy_cost;
    private Architecture arch;
    private TreeMap<String,ArrayList<Fact>> explanations;
    private TreeMap<String,Double> subobjective_scores2;
    private ArrayList<Fact> capabilities;
    private ArrayList<Fact> cost_facts;
    private int paretoRanking;
    private double crowdingDistance;
    private double utility;
    private String taskType;
    private Rete r = null;
    private QueryBuilder qb = null;
    private Rete r2 = null;
    private QueryBuilder qb2 = null;

    
    //Constructors
    public Result () {
        
    }
    public Result(Architecture arch, double science, double cost, ArrayList subobjective_scores, ArrayList objective_scores, ArrayList panel_scores, TreeMap<String,Double> subobjective_scores2) {
        this.science = science;
        this.cost = cost;
        this.subobjective_scores = subobjective_scores;
        this.subobjective_scores2 = subobjective_scores2;
        this.objective_scores = objective_scores;
        this.panel_scores = panel_scores;
        this.arch = arch;
        explanations=null;
        capabilities = null;
        cost_facts = null;
        paretoRanking = -1;
        crowdingDistance = 0.0;
        utility = -1.0;
        this.norm_science = (science - Params.min_science)/(Params.max_science-Params.min_science);
        this.norm_cost = (cost - Params.min_cost)/(Params.max_cost-Params.min_cost);
        taskType = "Fast";
        this.fuzzy_science = null;
        this.fuzzy_cost = null;
    }
    public Result(Architecture arch, double science, double cost, FuzzyValue fs, FuzzyValue fc, ArrayList subobjective_scores, ArrayList objective_scores, ArrayList panel_scores, TreeMap<String,Double> subobjective_scores2) {
        this.science = science;
        this.cost = cost;
        this.subobjective_scores = subobjective_scores;
        this.subobjective_scores2 = subobjective_scores2;
        this.objective_scores = objective_scores;
        this.panel_scores = panel_scores;
        this.arch = arch;
        explanations=null;
        capabilities = null;
        cost_facts = null;
        paretoRanking = -1;
        crowdingDistance = 0.0;
        utility = -1.0;
        this.norm_science = (science - Params.min_science)/(Params.max_science-Params.min_science);
        this.norm_cost = (cost - Params.min_cost)/(Params.max_cost-Params.min_cost);
        taskType = "Fast";
        this.fuzzy_science = fs;
        this.fuzzy_cost = fc;
    }
    public Result(Architecture arch, double science, double cost) {
        this.science = science;
        this.cost = cost;
        this.subobjective_scores = null;
        this.subobjective_scores2 = null;
        this.objective_scores = null;
        this.panel_scores = null;
        this.arch = arch;
        explanations=null;
        capabilities = null;
        cost_facts = null;
        paretoRanking = -1;
        crowdingDistance = 0.0;
        utility = -1.0;
        this.norm_science = (science - Params.min_science)/(Params.max_science-Params.min_science);
        this.norm_cost = (cost - Params.min_cost)/(Params.max_cost-Params.min_cost);
        taskType = "Fast";
        this.fuzzy_science = null;
        this.fuzzy_cost = null;
    }
    public Result(Architecture arch, double science, double cost, FuzzyValue fs, FuzzyValue fc) {
        this.science = science;
        this.cost = cost;
        this.subobjective_scores = null;
        this.subobjective_scores2 = null;
        this.objective_scores = null;
        this.panel_scores = null;
        this.arch = arch;
        explanations=null;
        capabilities = null;
        cost_facts = null;
        paretoRanking = -1;
        crowdingDistance = 0.0;
        utility = -1.0;
        this.norm_science = (science - Params.min_science)/(Params.max_science-Params.min_science);
        this.norm_cost = (cost - Params.min_cost)/(Params.max_cost-Params.min_cost);
        taskType = "Fast";
        this.fuzzy_science = fs;
        this.fuzzy_cost = fc;
    }
    public Result(Architecture arch, double science, double cost, int pr) {
        this.science = science;
        this.cost = cost;
        this.subobjective_scores = null;
        this.subobjective_scores2 = null;
        this.objective_scores = null;
        this.panel_scores = null;
        this.arch = arch;
        explanations=null;
        capabilities = null;
        cost_facts = null;
        paretoRanking = pr;
        crowdingDistance = 0.0;
        utility = -1.0;
        this.norm_science = (science - Params.min_science)/(Params.max_science-Params.min_science);
        this.norm_cost = (cost - Params.min_cost)/(Params.max_cost-Params.min_cost);
        taskType = "Fast";
        this.fuzzy_science = null;
        this.fuzzy_cost = null;
    }
    public Result(Architecture arch, double science, double cost, FuzzyValue fs, FuzzyValue fc, int pr) {
        this.science = science;
        this.cost = cost;
        this.subobjective_scores = null;
        this.subobjective_scores2 = null;
        this.objective_scores = null;
        this.panel_scores = null;
        this.arch = arch;
        explanations=null;
        capabilities = null;
        cost_facts = null;
        paretoRanking = pr;
        crowdingDistance = 0.0;
        utility = -1.0;
        this.norm_science = (science - Params.min_science)/(Params.max_science-Params.min_science);
        this.norm_cost = (cost - Params.min_cost)/(Params.max_cost-Params.min_cost);
        taskType = "Fast";
        this.fuzzy_science = fs;
        this.fuzzy_cost = fc;
    }
    
    //Getters and Setters
    public ArrayList<Fact> getCapabilities() {
        return capabilities;
    }
    public void setCapabilities(ArrayList<Fact> capabilities) {
        this.capabilities = capabilities;
    }
    public TreeMap<String,ArrayList<Fact>> getExplanations() {
        return explanations;
    }
    public void setExplanations(TreeMap<String,ArrayList<Fact>> explanations) {
        this.explanations = explanations;
    }
    public String getTaskType() {
        return taskType;
    }
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
    public Architecture getArch() {
        return arch;
    }
    public void setArch(Architecture arch) {
        this.arch = arch;
    }
    public double getCrowdingDistance() {
        return crowdingDistance;
    }
    public void setCrowdingDistance(double crowdingDistance) {
        this.crowdingDistance = crowdingDistance;
    }
    public double getScience() {
        return science;
    }
    public void setScience(double science) {
        this.norm_science = (science - Params.min_science)/(Params.max_science-Params.min_science);
        this.science = science;
    }
    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
        this.norm_cost = (cost - Params.min_cost)/(Params.max_cost-Params.min_cost);
    }
    public void setParetoRanking(int paretoRanking) {
        this.paretoRanking = paretoRanking;
    }
    public void setUtility(double utility) {
        this.utility = utility;
    }
    public int getParetoRanking() {
        return paretoRanking;
    }
    public double getUtility() {
        return utility;
    }
    public ArrayList getSubobjective_scores() {
        return subobjective_scores;
    }
    public void setSubobjective_scores(ArrayList subobjective_scores) {
        this.subobjective_scores = subobjective_scores;
    }
    public ArrayList getObjective_scores() {
        return objective_scores;
    }
    public void setObjective_scores(ArrayList objective_scores) {
        this.objective_scores = objective_scores;
    }
    public TreeMap<String, Double> getSubobjective_scores2() {
        return subobjective_scores2;
    }
    public void setSubobjective_scores2(TreeMap<String, Double> subobjective_scores2) {
        this.subobjective_scores2 = subobjective_scores2;
    }
    public ArrayList getPanel_scores() {
        return panel_scores;
    }
    public void setPanel_scores(ArrayList panel_scores) {
        this.panel_scores = panel_scores;
    }
    public double getNorm_science() {
        return norm_science;
    }
    public double getNorm_cost() {
        return norm_cost;
    }
    public ArrayList<Fact> getCost_facts() {
        return cost_facts;
    }
    public void setCost_facts(ArrayList<Fact> cost_facts) {
        this.cost_facts = cost_facts;
    }
    public FuzzyValue getFuzzy_science() {
        return fuzzy_science;
    }
    public FuzzyValue getFuzzy_cost() {
        return fuzzy_cost;
    }

    public void setFuzzy_science(FuzzyValue fuzzy_science) {
        this.fuzzy_science = fuzzy_science;
    }

    public void setFuzzy_cost(FuzzyValue fuzzy_cost) {
        this.fuzzy_cost = fuzzy_cost;
    }
    
    public void setRete(Rete r){
        this.r = r;
    }
    public void setRete2(Rete r2){
        this.r2 = r2;
    }
    public void setQueryBuilder(QueryBuilder qb){
        this.qb = qb;
    }
    public void setQueryBuilder2(QueryBuilder qb2){
        this.qb2 = qb2;
    }
    
    public Rete getRete(){
        return r;
    }
    public Rete getRete2(){
        return r2;
    }
    public QueryBuilder getQueryBuilder(){
        return qb;
    }
    public QueryBuilder getQuieryBuilder2(){
        return qb2;
    }


    //Public methods
    public int dominates(Result r2) {
        if (this.getArch().isFeasibleAssignment() && !r2.getArch().isFeasibleAssignment())
            return 1;
        if (!this.getArch().isFeasibleAssignment() && r2.getArch().isFeasibleAssignment())
            return -1;
        if (!this.getArch().isFeasibleAssignment() && !r2.getArch().isFeasibleAssignment())
            if(this.getArch().getTotalInstruments() < r2.getArch().getTotalInstruments())
                return 1;
            else if(this.getArch().getTotalInstruments() > r2.getArch().getTotalInstruments()) 
                return -1;
            else //Both are infeasible, and both to teh same degree (i.e., both have the same number of total instruments)
                return 0;
        double x1 = this.getScience() - r2.getScience();
        double x2 = this.getCost() - r2.getCost();
        if((x1>=0 && x2<=0) && !(x1==0 && x2==0)) 
            return 1;
        else if((x1<=0 && x2>=0) && !(x1==0 && x2==0))
            return -1;
        else return 0;
    }
    public static ArrayList dotSum(ArrayList a, ArrayList b) throws Exception {
        int n = a.size();
        int n2 = b.size();
        if (n!=n2) {
            throw new Exception ("dotSum: Arrays of different sizes");
        }
        ArrayList c = new ArrayList(n);
        for (int i = 0;i<n;i++) {
            Double t = (Double) a.get(i) + (Double) b.get(i);
            c.add(t);
        }
        return c;
    }
    public static double SumDollar(ArrayList a) {
        int n = a.size();
        double res = 0.0;
        for (int i = 0;i<n;i++) {
            res = res +  (Double) a.get(i) ;
        }
        return res;
    }
    public static ArrayList dotMult(ArrayList a, ArrayList b) throws Exception {
        int n = a.size();
        int n2 = b.size();
        if (n!=n2) {
            throw new Exception ("dotSum: Arrays of different sizes");
        }
        ArrayList c = new ArrayList(n);
        for (int i = 0;i<n;i++) {
            Double t = (Double) a.get(i) * (Double) b.get(i);
            c.add(t);
        }
        return c;
    }
    public static double sumProduct(ArrayList a, ArrayList b) throws Exception {
        return SumDollar(dotMult(a,b));
    }
    public double distance(Result other) {
        return Math.sqrt(Math.pow(norm_science-other.getNorm_science(),2) + Math.pow(norm_cost-other.getNorm_cost(),2));
    }
    @Override
    public String toString() {
        String fs;
        if (fuzzy_science == null)
            fs = "null";
        else
            fs = fuzzy_science.toString();
        String fc;
        if (fuzzy_cost == null)
            fc = "null";
        else
            fc = fuzzy_cost.toString();
        return "Result{" + "science=" + science + ", cost=" + cost + " fuz_sc=" + fs + " fuz_co=" + fc + ", arch=" + arch.toString() + ", paretoRanking=" + paretoRanking + '}';
    }
   
}

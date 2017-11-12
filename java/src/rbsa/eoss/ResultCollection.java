/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

/**
 *
 * @author Marc
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;
import rbsa.eoss.local.Params;
import java.util.ArrayList;
import java.util.HashMap;
        
public class ResultCollection implements java.io.Serializable {

    private Params params;
    private String stamp;
    private String filePath;
    private String name;
    private Stack<Result> results;
    private HashMap<String,String> conf;
    private ArrayList<Result> front;
    
    public ResultCollection() {
        params = Params.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" );
        stamp = dateFormat.format( new Date() );
        name = params.getName();
        conf = new HashMap<>();
        conf.put("Requirements",params.requirement_satisfaction_xls);
        conf.put("Capabilities",params.capability_rules_xls);
        //int ind1 = inputFile.indexOf("\\");
        //int ind2 = inputFile.indexOf(".");
        //String tmp = inputFile.substring(ind1+1, ind2);
        
        filePath = params.path_save_results + "\\" + stamp + "_" + name + ".rs";
        filePath = filePath.replaceAll("\\\\", "\\\\\\\\");
        results = new Stack<>();
        front = new ArrayList<>();
    }

    public ResultCollection(Stack<Result> results) {
        params = Params.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" );
        stamp = dateFormat.format( new Date() );
        name = params.getName();
        conf = new HashMap<>();
        conf.put("Requirements",params.requirement_satisfaction_xls);
        conf.put("Capabilities",params.capability_rules_xls);
        
        //int ind1 = inputFile.indexOf("\\");
        //int ind2 = inputFile.indexOf(".");
        //String tmp = inputFile.substring(ind1+1, ind2);
        
        filePath = params.path_save_results + "\\" + stamp + "_" + name + ".rs";
        filePath = filePath.replaceAll("\\\\", "\\\\\\\\");
        this.results = results;
        front = compute_pareto_front(results);
    }

    public ResultCollection(String stamp, Stack<Result> results) {
        params = Params.getInstance();
        this.stamp = stamp;
        this.results = results;
        front = compute_pareto_front(results);
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

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }

    public Stack<Result> getResults() {
        return results;
    }

    public void setResults(Stack<Result> results) {
        this.results = results;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String inputFile) {
        this.name = inputFile;
    }

    public ArrayList<Result> getFront() {
        return front;
    }

    public ArrayList<Architecture> getPopulation() {
        ArrayList<Architecture> pop = new ArrayList<Architecture>();
        for (Result res:front)
            pop.add(res.getArch());
        return pop;
    }
    public void setFront(ArrayList<Result> front) {
        this.front = front;
    }


    public void pushResult( Result result ) {
        results.push( result );
    }
    
    public Result popResult() {
        return results.pop();
    }
    
    public Result peekResult() {
        return results.peek();
    }
    
    public void clearResults()
    {
        results.clear();
    }
    
    public boolean isEmpty()
    {
        return results.isEmpty();
    }

    public HashMap<String, String> getConf() {
        return conf;
    }

    public void setConf(HashMap<String, String> conf) {
        this.conf = conf;
    }

    
    
}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

/**
 *
 * @author Ana-Dani
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import rbsa.eoss.local.Params;

public class SearchPerformanceComparator implements Serializable {
    private ArrayList<SearchPerformance> perf_array;
    private ArrayList<Double> avg_pareto_distances;
    private ArrayList<Double> max_sciences;
    private ArrayList<Double> cost_of_max_sciences;
    private ArrayList<Architecture> lowest_cost_max_science_arch;
    private ArrayList<ArrayList<Result>> histories_lowest_cost_max_science_arch;
    private ArrayList<ArrayList<Double>> histories_avg_pareto_distances;
    private String name;
    private String file_path;
    private String stamp;
    
    public SearchPerformanceComparator(ArrayList<SearchPerformance> perf_array) {
        this.perf_array = perf_array;
        computeAvgParetoDistance();
        computeLowesCostMaxScienceArch();
        computeHistories();
        name = "perfs";
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd--HH-mm-ss" );
        stamp = dateFormat.format( new Date() );
        file_path = Params.path_save_results + "\\" + name + "_" + stamp + ".rs";
    }
    public SearchPerformanceComparator(String name, ArrayList<SearchPerformance> perf_array) {
        this.perf_array = perf_array;
        computeAvgParetoDistance();
        computeLowesCostMaxScienceArch();
        computeHistories();
        this.name = "perfs_" + name;
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd--HH-mm-ss" );
        stamp = dateFormat.format( new Date() );
        file_path = Params.path_save_results + "\\" + this.name + "_" + stamp + ".rs";
    }
    public SearchPerformanceComparator(String name, String stamp, ArrayList<SearchPerformance> perf_array) {
        this.perf_array = perf_array;
        computeAvgParetoDistance();
        computeLowesCostMaxScienceArch();
        computeHistories();
        this.name = name;
        this.stamp = stamp;
        file_path = Params.path_save_results + "\\" + name + "_" + stamp + ".rs";
    }

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }
    
    private void computeHistories() {
        histories_lowest_cost_max_science_arch = new ArrayList<ArrayList<Result>>();
        histories_avg_pareto_distances = new ArrayList<ArrayList<Double>>();
        for(SearchPerformance perf:perf_array) {
            histories_lowest_cost_max_science_arch.add(perf.getHistory_cheapest_max_benefit_archs());
            histories_avg_pareto_distances.add(perf.getHistory_avg_pareto_distance());
        }
    }
    private void computeAvgParetoDistance () {
        avg_pareto_distances = new ArrayList<Double>();
        for(SearchPerformance perf:perf_array)
            avg_pareto_distances.add(perf.getAvg_pareto_distance());
    }
    private void computeLowesCostMaxScienceArch () {
        cost_of_max_sciences = new ArrayList<Double>();
        lowest_cost_max_science_arch = new ArrayList<Architecture>();
        max_sciences = new ArrayList<Double>();
        for(SearchPerformance perf:perf_array) {
            lowest_cost_max_science_arch.add(perf.getCheapest_max_benefit_arch().getArch());
            cost_of_max_sciences.add(perf.getCheapest_max_benefit_arch().getCost());
            max_sciences.add(perf.getCheapest_max_benefit_arch().getScience());
        }
    }
   /* private void computeCostofMaxSciences () {
        cost_of_max_sciences = new ArrayList<Double>();
        for(SearchPerformance perf:perf_array)
            cost_of_max_sciences.add(perf.getCheapest_max_benefit_arch().getCost());
    }
    private void computeMaxSciences () {
        max_sciences = new ArrayList<Double>();
        for(SearchPerformance perf:perf_array)
            max_sciences.add(perf.getCheapest_max_benefit_arch().getScience());
    }*/
    public ArrayList<Double> getAvg_pareto_distances() {
        return avg_pareto_distances;
    }

    public ArrayList<Architecture> getLowest_cost_max_science_arch() {
        return lowest_cost_max_science_arch;
    }

    public void setLowest_cost_max_science_arch(ArrayList<Architecture> lowest_cost_max_science_arch) {
        this.lowest_cost_max_science_arch = lowest_cost_max_science_arch;
    }

    public void setAvg_pareto_distances(ArrayList<Double> avg_pareto_distances) {
        this.avg_pareto_distances = avg_pareto_distances;
    }

    public ArrayList<Double> getMax_sciences() {
        return max_sciences;
    }

    public void setMax_sciences(ArrayList<Double> max_sciences) {
        this.max_sciences = max_sciences;
    }

    public ArrayList<Double> getCost_of_max_sciences() {
        return cost_of_max_sciences;
    }

    public void setCost_of_max_sciences(ArrayList<Double> cost_of_max_sciences) {
        this.cost_of_max_sciences = cost_of_max_sciences;
    }

    public ArrayList<ArrayList<Result>> getHistories_lowest_cost_max_science_arch() {
        return histories_lowest_cost_max_science_arch;
    }

    public void setHistories_lowest_cost_max_science_arch(ArrayList<ArrayList<Result>> histories_lowest_cost_max_science_arch) {
        this.histories_lowest_cost_max_science_arch = histories_lowest_cost_max_science_arch;
    }

    public ArrayList<ArrayList<Double>> getHistories_avg_pareto_distances() {
        return histories_avg_pareto_distances;
    }

    public void setHistories_avg_pareto_distances(ArrayList<ArrayList<Double>> histories_avg_pareto_distances) {
        this.histories_avg_pareto_distances = histories_avg_pareto_distances;
    }    
    
    public ArrayList<SearchPerformance> getPerf_array() {
        return perf_array;
    }

    public void setPerf_array(ArrayList<SearchPerformance> perf_array) {
        this.perf_array = perf_array;
    }

    @Override
    public String toString() {
        return "SearchPerformanceComparator{cheapest_max_science_arch = " + lowest_cost_max_science_arch + ", avg_pareto_distances=" + avg_pareto_distances + ", max_sciences=" + max_sciences + ", cost_of_max_sciences=" + cost_of_max_sciences + '}';
    }
    
}

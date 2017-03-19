/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;
import java.util.ArrayList;
/**
 *
 * @author Ana-Dani
 */
public class SearchOptions {
    private int MAX_ITS;
    private double TOL;
    private double mutation_rate;
    private int population_size;
    private double improvement_rate;
    private ArrayList<Architecture> init_population;
    
    public SearchOptions(int n, int MAX_ITS, double TOL,double mut,double improvement_rate, ArrayList<Architecture> init_pop ) {
        this.MAX_ITS = MAX_ITS;
        this.TOL = TOL;
        this.mutation_rate = mut;
        this.improvement_rate = improvement_rate;
        population_size = n;
        if (init_pop == null)
            init_population = ArchitectureGenerator.getInstance().generateRandomPopulation(n);
        else
            init_population = init_pop;
    }

    public ArrayList<Architecture> getInit_population() {
        return init_population;
    }

    public void setInit_population(ArrayList<Architecture> init_population) {
        this.init_population = init_population;
    }

    
    public double getImprovement_rate() {
        return improvement_rate;
    }

    public void setImprovement_rate(double improvement_rate) {
        this.improvement_rate = improvement_rate;
    }

    public int getPopulation_size() {
        return population_size;
    }

    public void setPopulation_size(int population_size) {
        this.population_size = population_size;
    }


    public double getMutation_rate() {
        return mutation_rate;
    }

    public int getMAX_ITS() {
        return MAX_ITS;
    }

    public void setMAX_ITS(int MAX_ITS) {
        this.MAX_ITS = MAX_ITS;
    }

    public double getTOL() {
        return TOL;
    }

    public void setTOL(double TOL) {
        this.TOL = TOL;
    }
    
    public Boolean checkTerminationCriteria(SearchPerformance sp) {
        Boolean converged = false;
        if (sp.nits >= MAX_ITS) {
            converged = true;
        }
        
        return converged;
    }
}

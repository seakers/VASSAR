/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rbsa.eoss;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author SEAK1
 */
public class Statistics {
    
    public String sense; //Positive: something happens    Negative: not happening
    public int feature_i1 = 0;
    public int feature_j1 = 0;
    public int feature_i2 = 0;
    public int feature_j2 = 0;
    
    
    
    
     public int countSomething (ArrayList<int[][]> population, Object architecture) {
        //List <Architecture> populationList = population.asList();
        int count = 0;
        int numInstr = population.get(0)[0].length;
        
        int numOrb = population.get(0).length;
        
        int[][] user = (int[][]) architecture;
        for ( int[][] element: population ) {
            boolean match = true;
            for (int i = 0; i < numOrb; ++i) {
                for (int j = 0; j < numInstr; ++j) {
                    if (user[i][j] == 0 && element[i][j] == 1) match = false;
                    if (user[i][j] == 1 && element[i][j] == 0) match = false;
                }
            }
            if (match) ++count;
        }
        
        return count;
    }
  
    //returns number of architectures with this feature in a population
    public int countFeature (List<Architecture> population) {
        //List <Architecture> populationList = population.asList();
        int count = 0;
        
            boolean[][] element;
            for (Architecture arch : population ) {
                 
                element = arch.getMat();
                if (sense.equals("Positive")) {
                    //x_i@orb_j
                    if  (element[feature_j1][feature_i1]) ++count; 
                } else if (sense.equals("Negative")) {
                    //x_i!@orb_j 
                    if  (!element[feature_j1][feature_i1]) ++count;
                }
            }
        
        return count;
    }
    
    //returns number of architectures with this two features in a population
    private int countFeatureOrder2 (List<Architecture> population) {
        //List <Architecture> populationList = population.asList();
        int count = 0;
        
            boolean[][] element;
            for (Architecture arch : population ) {
                
                element = arch.getMat();
                if (sense.equals("Positive")) {
                    //x_i1@orb_j1 and x_i2@orb_j2
                    if  (element[feature_i1][feature_j1] && element[feature_i2][feature_j2]) ++count; 
                } else if (sense.equals("Negative")) {
                    //x_i1!@orb_j1 and x_i2!@orb_j2
                    if  (!element[feature_i1][feature_j1] && !element[feature_i2][feature_j2]) ++count;
                }
            }
            

        
        return count;
    }
    
    //returns % of architectures with this feature in a population
    double featureInPopulation (List<Architecture> population, int order) {
        int count;
        if (order == 1) count = countFeature(population);
        else if (order == 2) count = countFeatureOrder2(population);
        else {
            System.out.println ("Order out of rang");
            return 0;
        }
        int total = population.size();
        return (double) count/total*100;                             
               
    }
    
    //returns % of architectures with this feature that are on the pareto front
    public double featureInEntirePopAndInParetoFront (List<Architecture> currentPopulation, List<Architecture> paretoArchs, int order) {
        int count_pareto, count_pop;
        if (order == 1) {
            count_pareto = countFeature(paretoArchs);
            count_pop = countFeature(currentPopulation);
        }
        else if (order == 2) {
            count_pareto = countFeatureOrder2(paretoArchs);
            count_pop = countFeatureOrder2(currentPopulation);
        }
        else {
           System.out.println ("Order out of rang");
           return 0; 
        }
        return (double) count_pareto/count_pop*100;
    }
    
      
    
    
   
}

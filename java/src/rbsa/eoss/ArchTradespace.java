/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Ana-Dani
 */
public class ArchTradespace {
    private static ArchTradespace instance = null;
    private ArrayList<Architecture> archs;
    private Stack<Result> results;
    private int narchs;
    
     private ArchTradespace () {
        results = null;
        archs = null;
        narchs = -1;
    }
    
    public static ArchTradespace getInstance()
    {
        if( instance == null ) 
        {
            instance = new ArchTradespace();
        }
        return instance;
    }

    public ArrayList<Architecture> getArchs() {
        return archs;
    }

    public void setArchs(ArrayList<Architecture> archs) {
        this.archs = archs;
        this.narchs = archs.size();
    }

    public Stack<Result> getResults() {
        return results;
    }

    public void setResults(Stack<Result> results) {
        this.results = results;
        this.narchs = results.size();
    }

    public int getNarchs() {
        return narchs;
    }
    
    
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

/**
 *
 * @author Ana-Dani
 */
import java.util.HashMap;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;

public class DSM implements Serializable {
    private double[][] matrix;
    private String[] elements;
    private int numel;
    private HashMap<String[],Double> map;
    private HashMap<String,Integer> indices;
    private String description;
    
    public DSM(String[] el,String desc) {
        elements = el;
        numel = el.length;
        matrix = new double[numel][numel];
        map = new HashMap<String[],Double>();
        indices = new  HashMap<String,Integer>();
        for(int i = 0;i<numel;i++) {
            indices.put(el[i],i);
        }
        description = desc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setInteraction(String el1, String el2, double x) {
        String[] els = new String[2];
        els[0] = el1;
        els[1] = el2;
        map.put(els,new Double(x));
        matrix[indices.get(el1)][indices.get(el2)] = x;
    }
    public Double getInteraction(String el1, String el2) {
        return matrix[indices.get(el1)][indices.get(el2)];
    }
    public String printAllInteractions() {
        String ret = "";
        for (String[] key : map.keySet()) {
            Double val = map.get(key);
            if (val!=0.0) {
                System.out.println(StringUtils.join(key, " ")  + " : " + val);
            }
        }
        return ret;
    }
    public TreeMap<String[],Double> getAllInteractions(String operator) {
        HashMap<String[],Double> unsorted_map = new HashMap<String[],Double>();
        ValueComparator bvc =  new ValueComparator(map);
        TreeMap<String[],Double> sorted_map = new TreeMap<String[],Double>(bvc);
        
        for (String[] key : map.keySet()) {
            Double val = map.get(key);
            if ((val>0.0 && operator.equalsIgnoreCase("+")) || (val<0.0 && operator.equalsIgnoreCase("-"))) {
                unsorted_map.put(key,val);
            }
        }
        sorted_map.putAll(unsorted_map);
        return sorted_map;
    }
    
    public double[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public String[] getElements() {
        return elements;
    }

    public void setElements(String[] elements) {
        this.elements = elements;
    }

    public int getNumel() {
        return numel;
    }

    public void setNumel(int numel) {
        this.numel = numel;
    }

    public HashMap<String[], Double> getMap() {
        return map;
    }

    public void setMap(HashMap<String[], Double> map) {
        this.map = map;
    }

    public HashMap<String, Integer> getIndices() {
        return indices;
    }

    public void setIndices(HashMap<String, Integer> indices) {
        this.indices = indices;
    }
    
}
class ValueComparator implements Comparator<String[]> {

    HashMap<String[], Double> base;
    public ValueComparator(HashMap<String[], Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    @Override
    public int compare(String[] a, String[] b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
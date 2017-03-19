package rbsa.eoss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dani
 */
public class Instrument {
    public String type;
    public String name;
    public Measurement[] measurements;

    public Instrument(){
        this.type = "N/A";
    }

    public Instrument(String name){
        this.type = "N/A";
        this.name = name;
    }

    public String getType(){
        return this.type;
    }
    public void setType(String t){
        this.type = t;
    }
    
}

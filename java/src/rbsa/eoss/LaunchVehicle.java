/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;
import java.util.HashMap;
import jess.ValueVector;
/**
 *
 * @author dani
 */
public class LaunchVehicle {
    private String id;
    private HashMap<String,ValueVector> payload_coeffs;
    private double diameter;
    private double height;
    private double cost;

    public LaunchVehicle(String id, HashMap<String, ValueVector> payload_coeffs, double diameter, double height, double cost) {
        this.id = id;
        this.payload_coeffs = payload_coeffs;
        this.diameter = diameter;
        this.height = height;
        this.cost = cost;
    }
    public ValueVector getPayload_coeffsOrbit(String orb) {
        return payload_coeffs.get(orb);
    }

    
    public double getDiameter() {
        return diameter;
    }

    public double getHeight() {
        return height;
    }


    public double getCost() {
        return cost;
    }

    
}

package rbsa.eoss;

import java.io.*;
import java.util.Arrays;


public class Interval implements Serializable {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 7088030013297057287L;
	/**
	 * 
	 */
	
	private double min;
	private double max;
	
	public Interval (String type, double a, double b)  {
	    if(type.equals("interval")) {
		this.min = a;
		this.max = b;
	    }
	    else if (type.equals("delta")) {
	    	this.min = a * (1 - b/100);
	    	this.max = a * (1 + b/100);	
	    }
	}
	
	// Manipulating fuzzy values
	public Interval add(Interval other) {
		// assume same unit and parameter
		double new_min = this.min + other.min;
		double new_max = this.max + other.max;
		Interval fv = new Interval("interval",new_min,new_max);
		return fv;
	}
	
	public Interval minus(Interval other) {
		double new_min = this.min - other.max;
		double new_max = this.max - other.min;	
		Interval fv = new Interval("interval",new_min,new_max);
		return fv;
	}
	
	public Interval prod(Interval other) {
		double[] arr = {this.min*other.min, this.min*other.max, this.max*other.min, this.max*other.max};
		Arrays.sort(arr);
		double new_max = arr[arr.length-1];
		double new_min = arr[0];
		Interval fv = new Interval("interval",new_min,new_max);
		return fv;
	}
	
	public Interval times(double scal) {
		double new_min = scal*this.min;
		double new_max = scal*this.max ;	
		Interval fv = new Interval("interval",new_min,new_max);;
		return fv;
	}
	
	public Interval exp(double scal) {
		double a = Math.pow(min, scal);
		double b = Math.pow(max, scal);	
		double lo = Math.min(a,b);
		double hi = Math.max(a,b);
		Interval fv = new Interval("interval",lo,hi);
		return fv;
	}
	
	public boolean intersects(Interval b) {
		Interval a = this;
		//System.out.println("a.min = " + a.min + "a.max = " + a.max + "b.min = " + b.min + "b.max = " + b.max);
        if (b.min <= a.max && b.min >= a.min) { return true; }
        if (a.min <= b.max && a.min >= b.min) { return true; }
        return false;
	}
	
	public String toString() {
		return ("[ " + this.getMin() + " , " + this.getMax() + " ]");
	}
	
	// Getters and setters
	public double getMean() {
		return (this.min + this.max)/2;
	}
	
	public double getMin() {
		return this.min;
	}
	
	public double getMax() {
		return this.max;
	}
	
	/*private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
	}
	private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
	}   */ 
}


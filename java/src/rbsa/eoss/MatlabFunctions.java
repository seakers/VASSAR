/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

/**
 *
 * @author dani
 */
import rbsa.eoss.local.Params;
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.reflect.Method;

import jess.*;

public class MatlabFunctions implements Userfunction {
    private HashMap<String,Interval> value_inv_hashmap;
    private HashMap<Interval,String> value_hashmap;
    private Value vvalue_inv_hashmap;
    private Value vvalue_hashmap;
    private HashMap<Integer,Double> infFactors;
    private ValueVector instrument_list;
    private Resource res;
    private Method m;
    private HashMap<String,LaunchVehicle> lv_database;
    public HashMap<Integer, Double> getInfFactors() {
        return infFactors;
    }


   
    
    public MatlabFunctions(Resource res) {
        this.res = res;
        m = null;
        value_inv_hashmap = new HashMap<String,Interval>(5);
        value_inv_hashmap.put("Full",new Interval("interval",1.0,1.0));
        value_inv_hashmap.put("Most",new Interval("interval",0.66,1.0));
        //value_inv_hashmap.put("Half",new Interval("interval",0.4,0.6));
        value_inv_hashmap.put("Some",new Interval("interval",0.33,0.66));
        value_inv_hashmap.put("Marginal",new Interval("interval",0.0,0.33));
        vvalue_inv_hashmap = new Value(value_inv_hashmap);
        
        value_hashmap = new HashMap<Interval,String>(5);
        value_hashmap.put(new Interval("interval",1.0,1.0),"Full");
        value_hashmap.put(new Interval("interval",0.66,1.0),"Most");
        //value_hashmap.put(new Interval("interval",0.4,0.6),"Half");
        value_hashmap.put(new Interval("interval",0.33,0.66),"Some");
        value_hashmap.put(new Interval("interval",0.0,0.33),"Marginal");
        vvalue_hashmap = new Value(value_hashmap);
        
        lv_database = new HashMap<String,LaunchVehicle>();
        
        infFactors = new HashMap<Integer,Double>();
        initializeInflationFactors();
    }
    @Override
    public String getName()
    {
        return "MatlabFunctions";
    }
    
    @Override
    public Value call( ValueVector vv, Context context ) throws JessException
    {
        Class<?>[] partypes = new Class<?>[2];
        try {
            partypes[0] = vv.getClass();
            partypes[1] = context.getClass();
            
            m = this.getClass().getDeclaredMethod( vv.get(1).toString(), partypes );
            
            Value v = (Value)m.invoke( this, vv, context ) ;
            
            return v;
        } catch (Exception e) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
            return null;
        }
    }
    public Method getMethod() {
        return m;
    }

    public void setMethod( Method m ) {
        this.m = m;
    }

    public Resource getResource() {
        return res;
    }

    public void setResource(Resource res) {
        this.res = res;
    }
    
private void initializeInflationFactors()
    {   
        String factors = "0.097,0.088,0.08,0.075,0.078,0.08,0.081,0.084,0.082,"
                + "0.081,0.081,0.085,0.095,0.1,0.102,0.105,0.113,0.13,"
                + "0.14,0.138,0.14,0.151,0.154,0.155,0.156,0.156,0.158,"
                + "0.163,0.168,0.169,0.172,0.174,0.175,0.178,0.18,0.183,"
                + "0.188,0.194,0.202,0.213,0.225,0.235,0.243,0.258,0.286,"
                + "0.312,0.33,0.352,0.379,0.422,0.479,0.528,0.56,0.578,"
                + "0.603,0.625,0.636,0.66,0.687,0.72,0.759,0.791,0.815,"
                + "0.839,0.861,0.885,0.911,0.932,0.947,0.967,1,1.028,"
                + "1.045,1.069,1.097,1.134,1.171,1.171,1.216,1.208,1.226,"
                + "1.244,1.264,1.285,1.307,1.328,1.35,1.372,1.395,1.418";
        
        String[] tmp = factors.split(",");
        for( int i = 1930, j = 0; i<= 2019; i++, j++ )
            infFactors.put(new Integer(i), new Double(tmp[j]));
    }
    public Value getValue_inv_hashmap(Funcall vv, Context c) {
        return vvalue_inv_hashmap;
    }

    public Value getValue_hashmap(Funcall vv, Context c) {
        return vvalue_hashmap;
    }
    
    public String StringArraytoStringWithSpaces(String[] array) {
        String res = array[0];
        for (int i = 1;i<array.length;i++) {
            res = res + " " + array[i];
        }
        return res;
    }
 
    public String StringArraytoStringWith(String[] array, String ss) {
        String res = array[0];
        for (int i = 1;i<array.length;i++) {
            res = res + ss + array[i];
        }
        return res;
    }
    public String toJessList(String str) {//str = [a,b,c]; goal is to return (create$ a b c)
        String str2 = str.substring(1, str.length()-1);//get rid of []
        return " (create$ " + str2.replace(",", " ") + ")";
    }
    public boolean[][] BooleanString2Matrix(String bitString) {
        boolean[][] mat = new boolean[Params.norb][Params.ninstr];
        for (int i= 0;i<Params.norb;i++) {
            for (int j= 0;j<Params.ninstr;j++) { 
                String b = bitString.substring(Params.ninstr*i+j,Params.ninstr*i+j);
                mat[i][j] = Boolean.parseBoolean(b);
            }         
        }
        return mat;
    } 
    public boolean[][] bitString2Mat(boolean[] bitString, int norb, int ninstr) {
        boolean[][] mat = new boolean[norb][ninstr];
        int b = 0;
        for (int i= 0;i<norb;i++) {
            for (int j= 0;j<ninstr;j++) { 
                mat[i][j] = bitString[b++];
            }         
        }
        return mat;
    }
    public boolean[] Mat2bitString (boolean[][] mat) {
        int norb = mat.length;
        int ninstr = mat[0].length;
        boolean[] bitString = new boolean[norb*ninstr];
        int b = 0;
        for (int i= 0;i<norb;i++) {
            for (int j= 0;j<ninstr;j++) { 
               bitString[b++] =  mat[i][j];
            }         
        }
        return bitString;
    }
    public int SumColBool(boolean[][] mat, int col) {
        int x = 0;
        int nrows = mat.length;
        for (int i = 0;i<nrows;i++) {
            if (mat[i][col]) {
                x = x + 1;
            }
        }
        return x;
    }
    public int SumRowBool(boolean[][] mat, int row) {
        int x = 0;
        int ncols = mat[0].length;
        for (int i = 0;i<ncols;i++) {
            if (mat[row][i]) {
                x = x + 1;
            }
        }
        return x;
    }
    public int SumMatBool (boolean[][] mat) {
        int x = 0;
        for (int i = 0;i<mat.length;i++) {
            x = x + SumRowBool(mat,i);
        }
        return x;
    }
    
    public ArrayList<Double> AbsDiffArrayList(ArrayList<Double> x,ArrayList<Double> y) {
        ArrayList<Double> z = new ArrayList<Double>(x.size());
        for (int i = 0;i<x.size();i++) {
            z.add(Math.abs(x.get(i) - y.get(i)));
        }
        return z;
    }
    public double SumelArrayList(ArrayList<Double> x) {
        double z = 0.0;
        for (int i = 0;i<x.size();i++) {
            z = z + x.get(i);
        }
        return z;
    }
    
    private Value computeDataVolume( Funcall vv, Context c )
    {
        ValueVector dr;
        double dt;
        
        try {
            dr = vv.get(2).listValue(c);
            dt = vv.get(3).floatValue(c);
            
            double tmp = 0;
            for( int i = 0; i < dr.size(); i++ )
                tmp += dr.get(i).floatValue(c);
            
            return new Value( tmp*dt, RU.FLOAT );
        } catch (Exception e) {
            System.out.println( e.getMessage() );
            return null;
        }
        
    }
     public ValueVector getInstrument_list(Funcall vv, Context c) {
        return instrument_list;

    }

    public void setInstrument_list( ValueVector instrument_list ){
        this.instrument_list = instrument_list;
    }
    
    public Value designEPS( Funcall vv, Context c )
    {
        double Pavg_payload;
        double Ppeak_payload;
        double frac_sunlight;
        double worst_sun_angle;
        double period;
        double lifetime;
        double dry_mass;
        double DOD;
        
        try {
            
            Pavg_payload = vv.get(2).floatValue(c);
            Ppeak_payload = vv.get(3).floatValue(c);
            frac_sunlight = vv.get(4).floatValue(c);
            worst_sun_angle = vv.get(5).floatValue(c);
            period = vv.get(6).floatValue(c);
            lifetime = vv.get(7).floatValue(c);
            dry_mass = vv.get(8).floatValue(c);
            DOD = vv.get(9).floatValue(c);
            
            double Xe = 0.65;
            double Xd = 0.85;
            double P0 = 202;
            double Id = 0.77;
            double degradation = 2.75/100;
            double Spec_power_SA = 25;
            double n = 0.9;
            double Spec_energy_density_batt = 40;
            
            Pavg_payload = Pavg_payload/0.5;
            Ppeak_payload = Ppeak_payload/0.5;
            double Pd = 0.8*Pavg_payload + 0.2*Ppeak_payload;
            double Pe = Pd;
            double Td = period*frac_sunlight;
            double Te = period - Td;
            
            if( Te == 0 )
                Te = 0.1*Td;
            
            double Psa = (Pe*Te/Xe+Pd*Td/Xd)/Td;
            double theta = worst_sun_angle*Math.PI/180;
            double P_density_BOL = Math.abs(P0*Id*Math.cos(theta));
            double Ld = Math.pow(1-degradation, lifetime);
            double P_density_EOL = P_density_BOL*Ld;
            
            double Asa = Psa/P_density_EOL;
            double P_BOL = P_density_BOL*Asa;
            double mass_SA = P_BOL/Spec_power_SA;
            
            double Cr = Pe*Te/(3600*DOD*n);
            double mass_batt = Cr/Spec_energy_density_batt;
            
            double mass_others = (0.02 + 0.0125)*P_BOL + 0.02*dry_mass;
            
            double mass_EPS = mass_SA + mass_batt + mass_others;
            
            ValueVector vv2 = new ValueVector(5);
            vv2.add(mass_EPS);
            vv2.add(P_BOL);
            vv2.add(Asa);
            vv2.add(mass_SA);
            vv2.add(mass_batt);
            
            return new Value( vv2, RU.LIST );
        } catch (Exception e) {
            System.out.println( e.getMessage() );
            return null;
        }
    }
    
    public Value inflate( Funcall vv, Context c )
    {
        double x1;
        int y1, y2;
        
        try{
            x1 = vv.get(2).floatValue(c);
            y1 = vv.get(3).intValue(c);
            y2 = vv.get(4).intValue(c);
            
            Double f1 = infFactors.get( new Integer(y1) );
            Double f2 = infFactors.get( new Integer(y2) );
            
            Double d = (x1/f1)*f2;
            
            return new Value( d.doubleValue(), RU.FLOAT );
            
        } catch (Exception e) {
            System.out.println( e.getMessage() );
            return null;
        }
    }
    public void addLaunchVehicletoDB(String id, LaunchVehicle lv) {
        lv_database.put(id, lv);
    }
     public Value getLaunchVehicleCost ( Funcall vv, Context c )
    {
        String id;
        try{
            id = vv.get(2).stringValue(c);
            double cost = lv_database.get(id).getCost();
            return new Value( cost, RU.FLOAT );
            
        } catch (Exception e) {
            System.out.println( e.getMessage() );
            return null;
        }
    }
     
     public Value getLaunchVehicleDimensions ( Funcall vv, Context c )
    {
        String id;
        try{
            id = vv.get(2).stringValue(c);
            double h = lv_database.get(id).getHeight();
            double d = lv_database.get(id).getDiameter();
            ValueVector vv2 = new ValueVector(2);
            vv2.add(d);
            vv2.add(h);
            return new Value( vv2, RU.LIST );
            
        } catch (Exception e) {
            System.out.println( e.getMessage() );
            return null;
        }
    }
     
     public Value getLaunchVehiclePerformanceCoeffs ( Funcall vv, Context c )
    {
        String id;
        String orb;
        try{
            id = vv.get(2).stringValue(c);
            orb = vv.get(3).stringValue(c);
            ValueVector coeffs = lv_database.get(id).getPayload_coeffsOrbit(orb);
            return new Value( coeffs, RU.LIST );
            
        } catch (Exception e) {
            System.out.println( e.getMessage() );
            return null;
        }
    }

    public ArrayList<String> JessList2ArrayList(ValueVector vv, Rete r) {
        ArrayList<String> al = new ArrayList<>();
        try {
            for (int i = 0; i < vv.size(); i++) {
                al.add(vv.get(i).stringValue(r.getGlobalContext()));
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            al = null;
        }
        return al;
    }
}

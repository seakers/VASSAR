/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

import rbsa.eoss.local.Params;
import java.util.UUID;
import java.util.Random;
import org.paukov.combinatorics.*;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import jess.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
/**
 *
 * @author dani
 */
//import jess.*;
public class Architecture implements Comparable<Architecture>, java.io.Serializable {
    private Params params;
    private boolean[] bitString;
    private int norb;
    private int ninstr;
    private boolean[][] mat;
    private String eval_mode;
    private String[] payloads;
    private String payload;
    private String orbit;
    private String key;
    private Result result;
    private Random rnd;
    private String mutate;
    private String crossover;
    private Nto1pair nto1pair;
    private String improve;
    private String heuristics_to_apply;
    private String heuristics_applied;
    private String id;
    private int nsats;

    
    //Constructors
    public Architecture(boolean[] bitString, int no, int ni, int nsat) {
        params = Params.getInstance();
        this.bitString = bitString;
        norb = no;
        ninstr = ni;
        this.nsats= nsat;
        eval_mode = "RUN";
        //m = new MatlabFunctions();
        mat = bitString2Mat(bitString, norb, ninstr);
        key = createKey(mat,nsats);
        payloads = null;
        orbit = null;
        result = new Result(this,-1,-1,-1);
        rnd = new Random();
        updateOrbitPayload();
        mutate = "no";
        crossover = "no";
        improve = "no";
        heuristics_to_apply = "";
        heuristics_applied = "";
        id = UUID.randomUUID().toString();
    }
    public Architecture(Fact f) {
        params = Params.getInstance();
        Resource res = ArchitectureEvaluator.getInstance().getResourcePool().getResource();
        Rete r = res.getRete();
        Context c = res.getRete().getGlobalContext();
        String bs = null;
        String _mutate = null;
        String _crossover = null;
        String _improve = null;
        ArrayList<String> _heuristics_to_apply = null;
        ArrayList<String> _heuristics_applied = null;
        try {
            bs = f.getSlotValue("bitString").stringValue(c);
            nsats = f.getSlotValue("num-sats-per-plane").intValue(c);
            _mutate = f.getSlotValue("mutate").stringValue(c);
            _crossover = f.getSlotValue("crossover").stringValue(c);
            _improve = f.getSlotValue("improve").stringValue(c);
            ValueVector vv = (ValueVector)f.getSlotValue("heuristics-to-apply").listValue(c);
            _heuristics_to_apply = res.getM().JessList2ArrayList(vv, r);
            vv = (ValueVector)f.getSlotValue("heuristics-applied").listValue(c);
            _heuristics_applied = res.getM().JessList2ArrayList(vv, r);
            ArchitectureEvaluator.getInstance().getResourcePool().freeResource(res);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ArchitectureEvaluator.getInstance().getResourcePool().freeResource(res);
        }
        mat = BooleanString2Matrix(bs);
        norb = Params.norb;
        ninstr = Params.ninstr;
        bitString = Mat2bitString(mat);
        eval_mode = "RUN";
        //m = new MatlabFunctions();
        key = createKey(mat,nsats);
        payloads = null;
        orbit = null;
        result = new Result(this,-1,-1,-1);
        rnd = new Random();
        updateOrbitPayload();
        mutate = _mutate;
        crossover = _crossover;
        improve = _improve;
        if(_heuristics_to_apply != null)
            heuristics_to_apply = res.getM().StringArraytoStringWithSpaces((String[])(_heuristics_to_apply.toArray()));
        if(_heuristics_applied != null)
            heuristics_applied = res.getM().StringArraytoStringWithSpaces((String[])(_heuristics_applied.toArray()));
        id = UUID.randomUUID().toString();
    }//BooleanString2Matrix

    public Architecture(String bs, int nsat) {
        params = Params.getInstance();
        mat = BooleanString2Matrix(bs);
        norb = Params.norb;
        ninstr = Params.ninstr;
        nsats = nsat;
        bitString = Mat2bitString(mat);
        eval_mode = "RUN";
        //m = new MatlabFunctions();
        key = createKey(mat,nsats);
        payloads = null;
        orbit = null;
        result = new Result(this,-1,-1,-1);
        rnd = new Random();
        updateOrbitPayload();
        mutate = "no";
        crossover = "no";
        improve = "no";
        heuristics_to_apply = "";
        heuristics_applied = "";
        id = UUID.randomUUID().toString();
    }//BooleanString2Matrix

    public Architecture(boolean[][] mat, int nsat) {
        params = Params.getInstance();
        this.mat = mat;
        norb = mat.length;
        ninstr = mat[0].length;
        bitString = Mat2bitString(mat);
        eval_mode = "RUN";
        nsats = nsat;
        //m = new MatlabFunctions();
        key = createKey(mat,nsats);
        payloads = null;
        orbit = null;
        result = new Result(this,-1,-1,-1);
        rnd = new Random();
        updateOrbitPayload();
        mutate = "no";
        crossover = "no";
        improve = "no";
        heuristics_to_apply = "";
        heuristics_applied = "";
        id = UUID.randomUUID().toString();
    }

    public Architecture(String[] payload, String orbit) {
        params = Params.getInstance();
        this.payloads = payload;
        this.orbit = orbit;
        mat = new boolean[Params.norb][Params.ninstr];
        for (int o = 0;o<Params.norb;o++)
            for (int i = 0;i<Params.ninstr;i++)
                this.mat[o][i] = false;
        for (int i = 0;i<payload.length;i++) {
            try {
                mat[(Integer)Params.orbit_indexes.get(orbit)][(Integer)Params.instrument_indexes.get(payload[i])] = true;
            } catch(Exception e) {
                System.out.println("Architecture " + e.getMessage());
            }
        }
        norb = mat.length;
        ninstr = mat[0].length;
        bitString = Mat2bitString(mat);
        eval_mode = "RUN";
        key = createKey(mat,1);
        result = null;
        rnd = new Random();
        updateOrbitPayload();
        mutate = "no";
        crossover = "no";
        improve = "no";
        heuristics_to_apply = "";
        heuristics_applied = "";
        id = UUID.randomUUID().toString();
        nsats = 1;
    }

    public Architecture(Nto1pair nto1pair, String orbit) {
        params = Params.getInstance();
        this.nto1pair = nto1pair;
        String[] base = nto1pair.getBase();
        String add = nto1pair.getAdded();
        int n = base.length + 1;
        payloads = new String[n];
        payload = "";
        System.arraycopy(base, 0, payloads, 0, n-1);
        payloads[n-1] = add;
       
        this.orbit = orbit;
        mat = new boolean[Params.norb][Params.ninstr];
        for (int o = 0;o<Params.norb;o++)
            for (int i = 0;i<Params.ninstr;i++)
                this.mat[o][i] = false;
        for (int i = 0;i<payloads.length;i++) {
            try {
                mat[(Integer) Params.orbit_indexes.get(orbit)][(Integer) Params.instrument_indexes.get(payloads[i])] = true;
            } catch (Exception e) {
                System.out.println("Architecture " + e.getMessage());
            }
        }
        norb = mat.length;
        ninstr = mat[0].length;
        bitString = Mat2bitString(mat);
        eval_mode = "RUN";
        key = createKey(mat,1);
        result = null;
        rnd = new Random();
        updateOrbitPayload();
        mutate = "no";
        crossover = "no";
        improve = "no";
        heuristics_to_apply = "";
        heuristics_applied = "";
        id = UUID.randomUUID().toString();
        nsats = 1;
    }

    public Architecture(ICombinatoricsVector<String> payl, String orbit) {
        params = Params.getInstance();
        int n = payl.getSize();
        payloads = new String[n];
        payload = "";
       for (int i = 0;i<n;i++) {
            payloads[i] = payl.getValue(i);
        }
        this.orbit = orbit;
        mat = new boolean[Params.norb][Params.ninstr];
        for (int o = 0;o<Params.norb;o++)
            for (int i = 0;i<Params.ninstr;i++)
                this.mat[o][i] = false;
        
        for (int i = 0;i<payloads.length;i++) {
            try {
                mat[(Integer)Params.orbit_indexes.get(orbit)][(Integer)Params.instrument_indexes.get(payloads[i])] = true;
            } catch(Exception e) {
                System.out.println("Architecture " + e.getMessage());
            }
        }
        
        norb = mat.length;
        ninstr = mat[0].length;
        bitString = Mat2bitString(mat);
        eval_mode = "RUN";
        key = createKey(mat,1);
        result = null;
        rnd = new Random();
        updateOrbitPayload();
        mutate = "no";
        crossover = "no";
        improve = "no";
        heuristics_to_apply = "";
        heuristics_applied = "";
        id = UUID.randomUUID().toString();
        nsats = 1;
    }

    public Architecture(HashMap<String,String[]> mapping, int nsat) {
        params = Params.getInstance();
        mat = new boolean[Params.norb][Params.ninstr];
         for(int o = 0;o<Params.norb;o++) {
             for(int i = 0;i<Params.ninstr;i++) {
                 mat[o][i] = false;
             }
         }
        for(int o = 0;o<Params.norb;o++) {
            String orb = Params.orbit_list[o];
            String[] payl = mapping.get(orb);
            if (payl == null)
                continue;
            ArrayList<String> thepayl = new ArrayList<String>();
            thepayl.addAll(Arrays.asList(payl));
            for(int i = 0;i<Params.ninstr;i++) {
                String instr = Params.instrument_list[i];
                if(thepayl.contains(instr)) 
                    mat[o][i] = true;
            }
        }
        norb = mat.length;
        ninstr = mat[0].length;
        bitString = Mat2bitString(mat);
        eval_mode = "RUN";
        //m = new MatlabFunctions();
        nsats = nsat;
        key = createKey(mat,nsats);
        payloads = null;
        orbit = null;
        result = new Result(this,-1,-1,-1);
        rnd = new Random();
        updateOrbitPayload();
        mutate = "no";
        crossover = "no";
        improve = "no";
        heuristics_to_apply = "";
        heuristics_applied = "";
        id = UUID.randomUUID().toString();
    }
    
    //Getters
    public int getNsats() {
        return nsats;
    }
    public String getId() {
        return id;
    }
    public String[] getPayloads() {
        return payloads;
    }
    public Nto1pair getNto1pair() {
        return nto1pair;
    }
    public int getParetoRanking() {
        return result.getParetoRanking();
    }
    public double getUtility() {
        return result.getUtility();
    }
    public String getPayload() {
        return payload;
    }
    public String getOrbit() {
        return orbit;
    }
    public String getMutate() {
        return mutate;
    }
    public String getCrossover() {
        return crossover;
    }
    public String getImprove() {
        return improve;
    }
    public String getKey() {
        return key;
    }
    public String getEval_mode() {
        return eval_mode;
    }
    public boolean[] getBitString() {
        return bitString;
    }
    public boolean[][] getMat() {
        return mat;
    }
    public int getNinstr() {
        return ninstr;
    }
    public int getNorb() {
        return norb;
    }
    public Result getResult() {
        return result;
    }
    public int getTotalInstruments() {
        return SumAllInstruments(mat);
    }
    
    //Setters
    public void setParetoRanking(int paretoRanking) {
        result.setParetoRanking(paretoRanking);
    }
    public void setUtility(double utility) {
        result.setUtility(utility);
    }
    public void setMutate(String mutate) {
        this.mutate = mutate;
    }
    public void setCrossover(String crossover) {
        this.crossover = crossover;
    }
    public void setImprove(String improve) {
        this.improve = improve;
    }
    public void setResult(Result result) {
        this.result = result;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public void setEval_mode(String eval_mode) {
        this.eval_mode = eval_mode;
    }
    public void setBitString(boolean[] bitString) {
        this.bitString = bitString;
    }
    public void setMat(boolean[][] mat) {
        this.mat = mat;
    }
    public void setNinstr(int ninstr) {
        this.ninstr = ninstr;
    }
    public void setNorb(int norb) {
        this.norb = norb;
    }

    public String getHeuristics_to_apply() {
        return heuristics_to_apply;
    }

    public void setHeuristics_to_apply(String heuristics_to_apply) {
        this.heuristics_to_apply = heuristics_to_apply;
    }

    public String getHeuristics_applied() {
        return heuristics_applied;
    }

    public void setHeuristics_applied(String heuristics_applied) {
        this.heuristics_applied = heuristics_applied;
    }

    //toString
    @Override
    public String toString() {
        //return "Architecture{ bitString=" + this.toBitString() + ", norb=" + norb + ", ninstr=" + ninstr + "}";
        String ret = "Arch = " + nsats + " x ";
        for (int o = 0;o<norb;o++) {
            String orb = Params.orbit_list[o];
            String[] payls = this.getPayloadInOrbit(orb);
            if(payls!=null) {
                ret = ret + "\n" + orb + ": " + StringUtils.join(payls, " ") ;
            }
        }
        return ret;
    }

    public String toFactString() {
        String ret =  "(MANIFEST::ARCHITECTURE" + " (id " + id + ") (num-sats-per-plane " + nsats + ") (bitString " + toBitString() + ") (payload " + payload + ") (orbit " + orbit + ")"
                + " (mutate " + mutate + " ) (crossover " + crossover + ") (improve " + improve + ") (heuristics-to-apply " + heuristics_to_apply + " ) (heuristics-applied " + heuristics_applied + ") "
                + "(factHistory F" + params.nof + ")";
                params.nof++;
        if(result != null) {
            ret = ret + " (benefit " + result.getScience() + " ) (lifecycle-cost " + result.getCost() + ")" + " (pareto-ranking " + result.getParetoRanking() + " ) (utility " + result.getUtility() + ")";
        }
        ret = ret + ")";        
        return ret;

    }

    public String toBitString() {
        String str = "\"";
        for (int i = 0;i<bitString.length;i++) {
            String c = "0";
            if(bitString[i]) {
                c = "1";
            }
            str = str + c;
        }
        str = str + "\"";
        return str;
    }
    
    //Utils
    public static boolean[][] BooleanString2Matrix(String bitString) {
        boolean[][] mat = new boolean[Params.norb][Params.ninstr];
        for (int i= 0;i<Params.norb;i++) {
            for (int j= 0;j<Params.ninstr;j++) { 
                String b = bitString.substring(Params.ninstr*i+j,Params.ninstr*i+j+1);
                if (b.equalsIgnoreCase("1")) {
                    mat[i][j] = true;
                } else if (b.equalsIgnoreCase("0")) {
                    mat[i][j] = false;
                } else {
                    System.out.println("Architecture:BooleanString2Matrix string b is nor equal to 1 or 0!");
                }
            }         
        }
        return mat;
    }

    public static boolean[][] bitString2Mat(boolean[] bitString, int norb, int ninstr) {
        boolean[][] mat = new boolean[norb][ninstr];
        int b = 0;
        for (int i= 0;i<norb;i++) {
            for (int j= 0;j<ninstr;j++) { 
                mat[i][j] = bitString[b++];
            }         
        }
        return mat;
    }

    public static boolean[] Mat2bitString (boolean[][] mat) {
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

    private String createKey(boolean[][] mat, int nsat) {
        String the_orbit = null;
        String the_payload = null;
        for (int i = 0; i < Params.norb; i++) {
            int n = SumRowBool(mat, i);
            if (n > 0) {
                the_orbit = Params.orbit_list[i];
                the_payload = "";
                for (int j = 0; j < Params.ninstr; j++) {
                    if (mat[i][j]) {
                        if (the_payload.equalsIgnoreCase("")) {
                            the_payload = Params.instrument_list[j];
                        } else {
                            the_payload = the_payload + " " + Params.instrument_list[j];
                        }
                    }
                }
            }
        }
        return nsat + " x " + the_orbit + "@"  + the_payload;
    }

    private void updateOrbitPayload() {

        for (int i = 0; i < Params.norb; i++) {
            int n = SumRowBool(mat, i);
            if (n > 0) {
                orbit = Params.orbit_list[i];
                payload = "";
                payloads = new String[n];
                int k = 0;
                for (int j = 0; j < Params.ninstr; j++) {
                    if (mat[i][j]) {
                        payload = payload + " " + Params.instrument_list[j];
                        payloads[k] = Params.instrument_list[j];
                        k++;
                    }
                }
            }
        }
    }

    private int SumRowBool(boolean[][] mat, int row) {
        int x = 0;
        int ncols = mat[0].length;
        for (int i = 0;i<ncols;i++) {
            if (mat[row][i]) {
                x = x + 1;
            }
        }
        return x;
    }

    private int SumAllInstruments(boolean[][] mat) {
        int x = 0;
        int ncols = mat[0].length;
        for (int row = 0;row<mat.length;row++) {
            for (int i = 0;i<ncols;i++) {
                if (mat[row][i]) {
                    x = x + 1;
                }
            }
        }
        return x;
    }

    public Boolean isEmptyOrbit(String orb) {
        return (getPayloadInOrbit(orb).length==0);
    }

    private Boolean capturesInteraction(ArrayList<String>  thepayload, ArrayList<String> al) {
        //returns true if payl contains all elements in nt IN THE desired ORBIT     
        return (al.containsAll(thepayload));
    }

    private Boolean ContainsAllBut1FromInteraction(ArrayList<String>  thepayload, ArrayList<String> al) {
        //returns true if payl contains all but 1 elements in nt IN THE desired ORBIT
        int count = 0;
        for (int i = 0;i<al.size();i++) {
            if(thepayload.contains(al.get(i))) {
                count++;
            }
        }
        
        //return true if from the N elements in the interaction, we have exactly N-1 elements in the payload (i.e. 1 missing)
        return count == al.size()-1;
    }

    public String StringArraytoStringWithSpaces(String[] array) {
        String res = array[0];
        for (int i = 1;i<array.length;i++) {
            res = res + " " + array[i];
        }
        return res;
    }

    public String[] StringWithSpacestoStringArrayList(String str) {
        String[] res = str.split(" ");
        return res;
    }

    public String[] getPayloadInOrbit(String orb) {
        String[] thepayloads = null;
         for (int i = 0; i < Params.norb; i++) {
            if (orb.equalsIgnoreCase(Params.orbit_list[i])) {
                int n = SumRowBool(mat, i); 
                thepayloads = new String[n];
                int k = 0;
                for (int j = 0; j < Params.ninstr; j++) {
                    if (mat[i][j]) {
                        //payload = payload + " " + Params.instrument_list[j];
                        thepayloads[k++] = Params.instrument_list[j];
                    }
                }
            }
        }
        return thepayloads;
    }

    public boolean hasInstrument(String instr) {
        for (int i = 0; i < Params.norb; i++) {
            String[] payl = getPayloadInOrbit(Params.orbit_list[i]);
            for (int j = 0; j < payl.length; j++) {
                if (payl[j].equalsIgnoreCase(instr)) {
                    return true;
                }
            } 
        }
        return false;
    }

    public int getNInstrLowTRL() {
        int ninstrlowTRL = 0;
        for (String instr:ArchitectureEvaluator.getInstance().getLowTRLinstruments())
            if (hasInstrument(instr))
                ninstrlowTRL++;
        return ninstrlowTRL;
    }
    
    //CompareTo
    @Override
    public int compareTo(Architecture other) {
        if(this.toBitString().compareTo(other.toBitString()) == 0 && this.getNsats() == other.getNsats())
            return 0;
        else return 1;
    }

    public static Comparator<Architecture> ArchCrowdDistComparator = new Comparator<Architecture>() {
        @Override
        public int compare(Architecture a1, Architecture a2) {
            double x = (a1.getResult().getCrowdingDistance() - a2.getResult().getCrowdingDistance());
            if(x<0) {
                return 1;
            } else if (x>0) {
                return - 1;
            } else {
                return 0;
            }
        }
    };

    public static Comparator<Architecture> ArchScienceComparator = new Comparator<Architecture>() {
        @Override
        public int compare(Architecture a1, Architecture a2) {
            double x = (a1.getResult().getScience() - a2.getResult().getScience());
            if(x>0) {
                return 1;
            } else if (x<0) {
                return - 1;
            } else {
                return 0;
            }
        }
    };

    public static Comparator<Architecture> ArchCostComparator = new Comparator<Architecture>() {
        @Override
        public int compare(Architecture a1, Architecture a2) {
            double x = (a1.getResult().getCost() - a2.getResult().getCost());
            if(x<0) {
                return 1;
            } else if (x>0) {
                return - 1;
            } else {
                return 0;
            }
        }
    };

    public static Comparator<Map.Entry<String,Double>> ByValueComparator = new Comparator<Map.Entry<String,Double>>() {
        @Override
        public int compare(Map.Entry<String,Double> a1, Map.Entry<String,Double> a2) {
            return a1.getValue().compareTo(a2.getValue());
            
        }
    };

    //Heuristics
    public Architecture mutate1bit() {
       if (rnd.nextBoolean()) { //mutate matrix but not nsats
            Integer index = rnd.nextInt(norb*ninstr-1);
            boolean[] new_bitString = new boolean[norb*ninstr];
            System.arraycopy(bitString,0,new_bitString,0,norb*ninstr);
            new_bitString[index] = !bitString[index]; 
            Architecture new_one = new Architecture(new_bitString,this.norb,this.ninstr,nsats);
            new_one.setCrossover(crossover);
            new_one.setImprove(improve);
            return new_one;
       } else {///mutate nsats but not matrix
 
            Architecture new_one = new Architecture(bitString,this.norb,this.ninstr,Params.nsats[rnd.nextInt(Params.nsats.length)]);
            new_one.setCrossover(crossover);
            new_one.setImprove(improve);
            return new_one;
       }
    }

    public Architecture randomSearch() {
        //System.out.println("randomSearch");
        return ArchitectureGenerator.getInstance().getRandomArch();
    }

    public Architecture crossover1point(Architecture other) {
        Integer index = rnd.nextInt(norb*ninstr-1);
        boolean[] other_bs = other.getBitString();
        boolean[] new_bitString = new boolean[norb*ninstr];
        System.arraycopy(bitString,0,new_bitString,0,index);
        System.arraycopy(other_bs,index+1,new_bitString,index+1,norb*ninstr-index-1);//norb*ninstr
        if (rnd.nextBoolean())
           return new Architecture(new_bitString,this.norb,this.ninstr,nsats);
        else
           return new Architecture(new_bitString,this.norb,this.ninstr,other.getNsats());
        //System.out.println("crossover1point");
    }

    public Architecture removeRandomFromLoadedSat() {
        //Find a random non-empty orbit and its payload
        String[] payl0 = null;
        int MINSIZE = 3;
        int ntrials = 0;
        String orb = "";
        ArrayList<String> theorbits = new ArrayList<String>();
        Collections.addAll(theorbits,Params.orbit_list);
        Collections.shuffle(theorbits);//this sorts orbits in random order
        while (ntrials<Params.norb) {
            orb = theorbits.get(ntrials);
            payl0 = getPayloadInOrbit(orb);
            if (payl0 == null || payl0.length < MINSIZE) { // is there at least MINSIZE instruments in this orbit?
                ntrials++;
            }
            else {
                break;
            }
        }
        //If all orbits are empty mutate 1 bit = add 1 instrument to 1 orbit
        if(payl0 == null || ntrials == Params.norb) {
            //System.out.println("removeRandomFromLoadedSat > mutate1bit");
            return mutate1bit();
        }
        //Return new architecture with one instrument (random) removed from orb
        ArrayList<String> candidates = new ArrayList<String>();
        Collections.addAll(candidates,payl0);
        Collections.shuffle(candidates);//this sorts candidates in random order
        String instr = candidates.get(0);
        boolean[][] new_mat = removeInstrumentFromOrbit(mat,instr,orb);
        //System.out.println("removeRandomFromLoadedSat");
        return new Architecture(new_mat,nsats);
    }

    public Architecture removeInterference() {
        String[] payl0 = null;
        String orb;        
        ArrayList<String> theorbits = new ArrayList<String>();
        Collections.addAll(theorbits,Params.orbit_list);
        Collections.shuffle(theorbits);//this sorts orbits in random order
        int ntrials = 0;
        while (ntrials < Params.norb)  {
            orb = theorbits.get(ntrials);
            payl0 = getPayloadInOrbit(orb);
            if(payl0 == null) { // is there any instrument in this orbit?
                ntrials++;
                continue;
            } else {
                //get dsm and positive binary inteferences for that orbit
                NDSM edsm = (NDSM) params.all_dsms.get("EDSM2@" + orb);
                TreeMap<Nto1pair,Double> tm = edsm.getAllInteractions("+");

                //Find a current interference
                Iterator it = tm.keySet().iterator();
                int i;
                for (i = 0;i<tm.size();i++) {
                    Nto1pair nt = (Nto1pair)it.next();
                    ArrayList<String> al = new ArrayList<String>();
                    Collections.addAll(al,nt.getBase());
                    al.add(nt.getAdded());
                    ArrayList<String> thepayload = new ArrayList<String>();
                    Collections.addAll(thepayload,payl0);
                    if(capturesInteraction(thepayload,al) && rnd.nextFloat() < Params.prob_accept) {
                        //System.out.println("removeInterference");
                        return new Architecture(breakupPayload(mat,nt,orb),nsats);
                    }
                }

                //try with 3-lateral interferences
                edsm = (NDSM) params.all_dsms.get("EDSM3@" + orb);
                tm = edsm.getAllInteractions("+");

                //Find a missing synergy from intreaction tree
                it = tm.keySet().iterator();
                for (i = 0;i<tm.size();i++) {
                    Nto1pair nt = (Nto1pair)it.next();
                    ArrayList<String> al = new ArrayList<String>();
                    Collections.addAll(al,nt.getBase());
                    al.add(nt.getAdded());
                    ArrayList<String> thepayload = new ArrayList<String>();
                    Collections.addAll(thepayload,payl0);
                    if(capturesInteraction(thepayload,al) && rnd.nextFloat() < Params.prob_accept) {
                        //System.out.println("removeInterference");
                        return new Architecture(breakupPayload(mat,nt,orb),nsats);
                    }
                }
            }
            ntrials++;
        }
        //If all orbits are empty mutate 1 bit = add 1 instrument to 1 orbit
        if(payl0 == null) {
            //System.out.println("removeInterference > mutate");
            return mutate1bit();
        }
       //if there are non-empty orbits, but all 2- and 3-inteferences are already solved, return with no changes
       /*System.out.println("removeInterference > bestNeighbor");
       return bestNeighbor();*/
        
       //System.out.println("removeInterference > No Changes"); 
       return new Architecture(mat,nsats);
    }

    public Architecture removeSuperfluous() {
        String[] payl0 = null;
        String orb;        
        ArrayList<String> theorbits = new ArrayList<String>();
        Collections.addAll(theorbits,Params.orbit_list);
        Collections.shuffle(theorbits);//this sorts orbits in random order
        int ntrials = 0;
        while (ntrials < Params.norb)  {
            orb = theorbits.get(ntrials);
            payl0 = getPayloadInOrbit(orb);
            if (payl0 == null) { // is there any instrument in this orbit?
                ntrials++;
                continue;
            }
            else {
                //get redundancy dsm and zero binary inteferences for that orbit
                NDSM rdsm = (NDSM) params.all_dsms.get("RDSM" + (Params.ninstr) + "@" + orb);
                TreeMap<Nto1pair,Double> tm = rdsm.getAllInteractions("-");

                //Find a current interference
                Iterator it = tm.keySet().iterator();
                int i;
                for (i = 0;i<tm.size();i++) {
                    Nto1pair nt = (Nto1pair)it.next();
                    ArrayList<String> al = new ArrayList<String>();
                    Collections.addAll(al,nt.getBase());
                    al.add(nt.getAdded());
                    ArrayList<String> thepayload = new ArrayList<String>();
                    Collections.addAll(thepayload,payl0);
                    if((!nt.getAdded().equalsIgnoreCase("SMAP_ANT")) && capturesInteraction(thepayload,al) && rnd.nextFloat() < Params.prob_accept) {
                        //System.out.println("removeSuperfluous");
                        return new Architecture(removeInstrumentFromOrbit(mat,nt.getAdded(),orb),nsats);
                    }
                }

                //try with 3-lateral interferences
                rdsm = (NDSM) params.all_dsms.get("RDSM3@" + orb);
                tm = rdsm.getAllInteractions("-");

                //Find a missing synergy from intreaction tree
                it = tm.keySet().iterator();
                for (i = 0;i<tm.size();i++) {
                    Nto1pair nt = (Nto1pair)it.next();
                    ArrayList<String> al = new ArrayList<String>();
                    Collections.addAll(al,nt.getBase());
                    al.add(nt.getAdded());
                    ArrayList<String> thepayload = new ArrayList<String>();
                    Collections.addAll(thepayload,payl0);
                    if((!nt.getAdded().equalsIgnoreCase("SMAP_ANT")) && capturesInteraction(thepayload,al) && rnd.nextFloat() < Params.prob_accept) {
                        //System.out.println("removeSuperfluous");
                        return new Architecture(removeInstrumentFromOrbit(mat,nt.getAdded(),orb),nsats);
                    }
                }
            }
            ntrials++;
        }
        //If all orbits are empty mutate 1 bit = add 1 instrument to 1 orbit
        if(payl0 == null) {
            //System.out.println("removeSuperfluous > mutate");
            return mutate1bit();
        }
        //if there are non-empty orbits, but all 2- and 3-inteferences are already solved, return with no changes
        /*
        System.out.println("removeSuperfluous > bestNeighbor");
        return bestNeighbor();*/
        System.out.println("removeSuperfluous > No changes");
        return new Architecture(mat,nsats);
    }

    public Architecture improveOrbit() {
        //Find a random non-empty orbit and its payload
        String[] payl0 = null;
        int ntrials = 0;
        String orb;
        ArrayList<String> theorbits = new ArrayList<String>();
        Collections.addAll(theorbits,Params.orbit_list);
        Collections.shuffle(theorbits);//this sorts orbits in random order
        while(ntrials < Params.norb)  {
           orb = theorbits.get(ntrials);
           payl0 = getPayloadInOrbit(orb);
           if(payl0 == null) { // is there any instrument in this orbit?
               ntrials++;
               continue;
           } else {
               ArrayList<String> thepayloads = new ArrayList<String>();
               Collections.addAll(thepayloads,payl0);
               Collections.shuffle(thepayloads);//this sorts orbits in random order
               for(String instr:thepayloads) {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(instr);

                    //getallorbit scores
                    ArrayList<Map.Entry<String,Double>> list2 = new ArrayList<Map.Entry<String,Double>>();
                    list2.addAll(ArchitectureEvaluator.getInstance().getAllOrbitScores(list).entrySet());

                    //sort orbits and get best_orbit
                    Collections.sort(list2,Collections.reverseOrder(ByValueComparator));
                    String best_orbit = list2.get(0).getKey();
                    Double new_score = list2.get(0).getValue();
                    Double old_score = ArchitectureEvaluator.getInstance().getScore(list,orb);
                    
                    if(new_score>old_score && rnd.nextFloat() < Params.prob_accept) {
                        //System.out.println("improveOrbit");
                        return new Architecture(moveInstrument(mat,instr,orb,best_orbit),nsats);
                    }
               }
           }
           ntrials++;
       }
       //If all orbits are empty mutate 1 bit = add 1 instrument to 1 orbit
       if(payl0 == null || payl0.length == 0) {
           //System.out.println("improveOrbit > mutate1bit");
           return mutate1bit();
       }
       
       //Otherwise, all instruments are in hthe best possible orbits, so return unchanged
       /*
       System.out.println("improveOrbit > bestNeighbor");
       return bestNeighbor();*/
       System.out.println("improveOrbit > No changes");
       return new Architecture(mat,nsats);
    }

    public Architecture addSynergy() {
        //Find a random non-empty orbit and its payload 
        String[] payl0 = null;
        ArrayList<String> missing;
        String orb;        
        ArrayList<String> theorbits = new ArrayList<String>();
        Collections.addAll(theorbits,Params.orbit_list);
        Collections.shuffle(theorbits);//this sorts orbits in random order
        int ntrials = 0;
        while(ntrials<Params.norb)  {
            orb = theorbits.get(ntrials);
            payl0 = getPayloadInOrbit(orb);
            if(payl0 == null) { // is there any instrument in this orbit?
                ntrials++;
                continue;
            } else {
                //get dsm and positive binary synergies for that orbit
                NDSM sdsm = (NDSM) params.all_dsms.get("SDSM2@" + orb);
                TreeMap<Nto1pair,Double> tm = sdsm.getAllInteractions("+");

                //Find a missing synergy from intreaction tree
                Iterator it = tm.keySet().iterator();
                int i;
                for (i = 0;i<tm.size();i++) {
                    //get next strongest interaction
                    Nto1pair nt = (Nto1pair)it.next();

                    //if architecture already contains that interaction, OR if does not contain N-1 elements from the interaction continue
                    ArrayList<String> al = new ArrayList<String>();
                    Collections.addAll(al,nt.getBase());
                    al.add(nt.getAdded());
                    ArrayList<String> thepayload = new ArrayList<String>();
                    Collections.addAll(thepayload,payl0);
                    if(capturesInteraction(thepayload,al) || !ContainsAllBut1FromInteraction(thepayload,al) || rnd.nextFloat() > Params.prob_accept) {
                        continue;
                    } else {
                         //otherwise find missing element and return;
                         missing = new ArrayList<String>();
                         missing.addAll(al);
                         missing.removeAll(thepayload);
                         //System.out.println("addSynergy");
                         return new Architecture(addInstrumentToOrbit(mat,missing.get(0),orb),nsats);
                    }
                }

                //try with 3-lateral synergies
                sdsm = (NDSM) params.all_dsms.get("SDSM3@" + orb);
                tm = sdsm.getAllInteractions("+");

                //Find a missing synergy from intreaction tree
                it = tm.keySet().iterator();
                for (i = 0;i<tm.size();i++) {
                    //get next strongest interaction
                    Nto1pair nt = (Nto1pair)it.next();

                    //if architecture already contains that interaction, OR if does not contain N-1 elements from the interaction continue
                    ArrayList<String> al = new ArrayList<String>();
                    Collections.addAll(al,nt.getBase());
                    al.add(nt.getAdded());
                    ArrayList<String> thepayload = new ArrayList<String>();
                    Collections.addAll(thepayload,payl0);
                    if(capturesInteraction(thepayload,al) || !ContainsAllBut1FromInteraction(thepayload,al) || rnd.nextFloat() > Params.prob_accept) {
                        continue;
                    } else {
                         //otherwise find missing element and break;
                         missing = new ArrayList<String>();
                         missing.addAll(al);
                         missing.removeAll(thepayload);
                         //System.out.println("addSynergy");
                         return new Architecture(addInstrumentToOrbit(mat,missing.get(0),orb),nsats);
                    }
                }
            }
            ntrials++;
        }
        //If all orbits are empty mutate 1 bit = add 1 instrument to 1 orbit
        if(payl0 == null) {
            System.out.println("addSynergy > mutate1bit");
            return mutate1bit();
        }
      
       //if there are non-empty orbits, but all 2- and 3-synergies are captured, return best neighbor
       /* 
        System.out.println("addSynergy > bestNeighbor");
        return bestNeighbor();*/
        //System.out.println("addSynergy > No changes");
        return new Architecture(mat,nsats);
    }

    public Architecture addRandomToSmallSat() {
       //Find a random non-empty orbit and its payload 
       String[] payl0 = null;
       int MAXSIZE = 3;
       int ntrials = 0;
       String orb = "";
       ArrayList<String> theorbits = new ArrayList<String>();
       Collections.addAll(theorbits,Params.orbit_list);
       Collections.shuffle(theorbits);//this sorts orbits in random order
       while(ntrials<Params.norb)  {
           orb = theorbits.get(ntrials);
           payl0 = getPayloadInOrbit(orb);
           if(payl0 == null || payl0.length > MAXSIZE) { // is there at most MAXSIZE instruments in this orbit?
               ntrials++;
               continue;
           } else {
               break;
           }
       }
       //If all orbits are empty mutate 1 bit = add 1 instrument to 1 orbit
       if(payl0 == null || ntrials == Params.norb) {
           System.out.println("addRandomToSmallSat > mutate1bit");
           return mutate1bit();
       }    
       
       //Return new architecture with one instrument (random) added to orb
       ArrayList<String> candidates = new ArrayList<String>();
       Collections.addAll(candidates,Params.instrument_list);
       ArrayList<String> flown = new ArrayList<String>();
       Collections.addAll(flown,payl0);
       
       candidates.removeAll(flown);
       Collections.shuffle(candidates);//this sorts candidates in random order
       String instr = candidates.get(0);//so we can pick the first one
       boolean[][] new_mat = addInstrumentToOrbit(mat,instr,orb);
       //System.out.println("addRandomToSmallSat");
       return new Architecture(new_mat,nsats); 
    }

    public ArrayList<Architecture> bestNeighbor() {
        ArrayList<Architecture> neighbors = new ArrayList<Architecture>();
        for (int i = 0;i<bitString.length;i++) {
            boolean[] newbitString = new boolean[bitString.length];
            System.arraycopy(bitString, 0, newbitString, 0, bitString.length-1);
            newbitString[i] = !newbitString[i];
            Architecture arch = new Architecture(newbitString,norb,ninstr,nsats);
            neighbors.add(arch);
        }
        return neighbors;
    }

    public Architecture askUserToImprove() {
        //System.out.println("askUserToImprove");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Current arch is " + this.toString() + ".\n"
                + "Has science score: " + this.getResult().getScience() +
                "and has cost: " + this.getResult().getCost() + ".\n How can I improve this architecture?");
        HashMap<String,String[]> mapping= new HashMap<String,String[]>();
        int numInsAdded = 0;
        for (String orb:Params.orbit_list) {    
            try {
                boolean valid = false;
                String input = "";
                while(!valid) {
                    System.out.println("Added "+numInsAdded+" instruments");
                    System.out.println("New payload in " + orb + "? ");
                    input = bufferedReader.readLine();
                    String[] instruments = input.split(" ");
                    ArrayList<String> validInstruments = new ArrayList<String>();
                    validInstruments.addAll(Arrays.asList(Params.instrument_list));
                    valid = true;
                    for (int i = 0;i<instruments.length;i++) {
                        String instr= instruments[i];
                        if(instr.equalsIgnoreCase("")) {
                            valid = true;
                            break;
                        }
                        if(!validInstruments.contains(instr)) {
                            valid = false;
                            break;
                        }
                    }
                }    
                numInsAdded+=input.split(" ").length;
                mapping.put(orb,input.split(" "));
            } catch (Exception e) {
                System.out.println("EXC in askUserToImprove" + e.getMessage() + " " + e.getClass());
            }           
        }
        return new Architecture(mapping,nsats);
    }
    
    //Support functions for heuristics
    public boolean[][] addInstrumentToOrbit(boolean[][] old, String toadd, String where) {
        //create copy of current matrix
        boolean[][] thenew = new boolean[norb][ninstr];
        for (int i = 0; i < old.length; i++) {
            System.arraycopy(old[i], 0, thenew[i], 0, old[0].length);
        }
        //add the missing instrument in the right orbit and return
        thenew[(Integer) Params.orbit_indexes.get(where)][(Integer)Params.instrument_indexes.get(toadd)] = true;
        return thenew;
    }

    public boolean[][] removeInstrumentFromOrbit(boolean[][] old, String instr, String from) {
        //create copy of current matrix
        boolean[][] thenew = new boolean[norb][ninstr];
        for (int i = 0; i < old.length; i++) {
            System.arraycopy(old[i], 0, thenew[i], 0, old[0].length);
        }
        //add the missing instrument in the right orbit and return
        thenew[(Integer) Params.orbit_indexes.get(from)][(Integer)Params.instrument_indexes.get(instr)] = false;
        return thenew;
    }

    public boolean[][] moveInstrument(boolean[][] old, String instr, String from, String to) {
        //create copy of current matrix
        boolean[][] thenew = new boolean[norb][ninstr];
        for (int i = 0; i < old.length; i++) {
            System.arraycopy(old[i], 0, thenew[i], 0, old[0].length);
        }
        //add the missing instrument in the right orbit and return
        thenew[(Integer) Params.orbit_indexes.get(from)][(Integer)Params.instrument_indexes.get(instr)] = false;
        thenew[(Integer) Params.orbit_indexes.get(to)][(Integer)Params.instrument_indexes.get(instr)] = true;
        return thenew;
    }

    public boolean[][] breakupPayload(boolean[][] old, Nto1pair tobreak, String orb) {
        //create copy of current matrix
        boolean[][] thenew = new boolean[norb][ninstr];
        for (int i = 0; i < old.length; i++) {
            System.arraycopy(old[i], 0, thenew[i], 0, old[0].length);
        }
        //get instrument instr from tobreak
        String instr = tobreak.getAdded();
        //remove it from orb
        thenew[(Integer) Params.orbit_indexes.get(orb)][(Integer)Params.instrument_indexes.get(instr)] = false;
        //choose a random orbit differen from orb
        String an_orbit = orb;
        while(an_orbit.equalsIgnoreCase(orb)) {
            an_orbit = Params.orbit_list[rnd.nextInt(norb)];
        }
        //add instr to orb
        thenew[(Integer) Params.orbit_indexes.get(an_orbit)][(Integer)Params.instrument_indexes.get(instr)] = true;
        return thenew;
    }
    
    public boolean isFeasibleAssignment() {
        return (SumAllInstruments(mat) <= Params.MAX_TOTAL_INSTR);
    }
    
    public Architecture copy(){
        Architecture arch =  new Architecture(this.bitString,this.norb,this.ninstr,this.nsats);
        arch.setResult(this.result);
        return arch;
    }
    


}

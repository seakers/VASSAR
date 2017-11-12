/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

/**
 *
 * @author Marc
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import rbsa.eoss.local.Params;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class ArchitectureGenerator {

    public static ArchitectureGenerator getInstance() {
        if (instance == null) {
            instance = new ArchitectureGenerator();
        }
        return instance;
    }

    private static ArchitectureGenerator instance = null;

    private Params params;
    private ArrayList<Architecture> population;
    private Random rnd;
    
    private ArchitectureGenerator() {
        params = Params.getInstance();
        rnd = new Random();
    }

    public ArrayList<Architecture> generatePrecomputedPopulation() {
        long NUM_ARCHS = Params.norb * Math.round(Math.pow(2, Params.ninstr) - 1);
        population = new ArrayList<>((int) NUM_ARCHS);
        try {
            for (int ns=0; ns < Params.nsats.length;ns++){
                for (int o = 0; o < Params.norb; o++) {
                    for (int ii = 1; ii < Math.round(Math.pow(2, Params.ninstr)); ii++) {
                        boolean[][] mat = new boolean[Params.norb][Params.ninstr];
                        boolean[] bin = de2bi(ii,Params.ninstr);
                        for (int i = 0; i < Params.norb; i++) {
                            if (o == i) {
                                for (int j = 0; j < Params.ninstr; j++) {
                                    if (bin[j]) {
                                        mat[i][j] = true;
                                    } else {
                                        mat[i][j] = false;
                                    }
                                }
                            } else {
                                for (int j = 0; j < Params.ninstr; j++) {
                                    mat[i][j] = false;
                                }
                            }   
                        }
                        population.add(new Architecture(mat,Params.nsats[ns]));
                    }
                }
        }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return (ArrayList<Architecture>) population;
    }
    public static boolean[] de2bi(int d, int N) {
        boolean[] b = new boolean[N];
        for (int i = 0;i<N;i++) {
            //double q = (double)d/2;
            int r = d%2;
            if (r == 1) {
                b[i] = true;
            } else {
                b[i] = false;
            }
            d = d/2;
        }
        return b;
    }
    public ArrayList<Architecture> getInitialPopulation (int NUM_ARCHS) {
        population = new ArrayList<Architecture>();
        
        if (params.initial_pop.isEmpty()) {
            population.addAll(getManualArchitectures());
            population.addAll(generateRandomPopulation(NUM_ARCHS - population.size()));
        }
        else {
            population.addAll(getManualArchitectures());
            ArrayList<Architecture> init = ResultManager.getInstance().loadResultCollectionFromFile(params.initial_pop).getPopulation();
            int n = Math.min(NUM_ARCHS - population.size(), init.size());
            population.addAll(init.subList(0, n));
            population.addAll(generateRandomPopulation(NUM_ARCHS - population.size()));
        }
        
        return population;
    }
    
    public ArrayList<Architecture> generateRandomPopulation(int NUM_ARCHS) {
        //int NUM_ARCHS = 100;
        int GENOME_LENGTH = Params.ninstr * Params.norb;
        ArrayList<Architecture> popu = new ArrayList(NUM_ARCHS);
        try {
            for (int i = 0; i < NUM_ARCHS; i++) {
                boolean[] x = new boolean[GENOME_LENGTH];
                for (int j = 0; j < GENOME_LENGTH; j++) {
                    x[j] = rnd.nextBoolean();
                }
                Architecture arch = new Architecture(x, Params.norb, Params.ninstr,Params.nsats[rnd.nextInt(Params.nsats.length)]);
                //arch.setEval_mode("DEBUG");
                popu.add(arch);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return popu;
    }
    
    public ArrayList<Architecture> localSearch(ArrayList<Architecture> pop0) {
        //Remove duplicates
        
        ArrayList<Architecture> pop1 = new ArrayList<Architecture>(); 
        for (int i = 0;i<pop0.size()-1;i++) {
            Architecture arch1 = pop0.get(i);
            boolean UNIQUE = true;
            for (int j = i+1;j<pop0.size();j++) {
                Architecture arch2 = pop0.get(j);
                if (!arch1.getId().equalsIgnoreCase(arch2.getId()) && arch1.compareTo(arch2) == 0) {
                    //System.out.println("Eliminating duplicate a1 = " + arch1.getId() + " " + arch1.toString() + " a2 = " + arch2.getId() + " " + arch2.toString());
                    UNIQUE = false;
                    break;
                }
            }
            if (UNIQUE)
                pop1.add(arch1);
        }
        //ArrayList<Architecture> pop1  = new ArrayList<Architecture>(new HashSet<Architecture>(pop0));
        int n1 = pop1.size();
        ArrayList<Architecture> pop2  = new ArrayList<>();
        int nvars = pop1.get(0).getBitString().length;
        for (Architecture arch:pop1) {
            pop2.add(arch);
            Architecture newarch;
            if (arch.getNsats()>1) {
                newarch = new Architecture(arch.getBitString(),Params.norb,Params.ninstr,arch.getNsats()-1);
                newarch.setEval_mode("DEBUG");
                pop2.add(newarch);
            }
            if (arch.getNsats()<Params.nsats[Params.nsats.length-1]) {
                newarch = new Architecture(arch.getBitString(),Params.norb,Params.ninstr,arch.getNsats()+1);
                newarch.setEval_mode("DEBUG");
                pop2.add(newarch);
            }
        }
        for (int k = 0; k < n1; k++) {
            //System.out.println("Searching around arch " + k);
            for (int j = 0;j<nvars;j++) {
                boolean[] arch = pop1.get(k).getBitString().clone();
                arch[j] = !arch[j];
                Architecture new_one = new Architecture(arch, Params.norb, Params.ninstr, pop1.get(k).getNsats());
                //new_one.setEval_mode("DEBUG");
                pop2.add(new_one);
            }
        }
        
        //Remove duplicates after local search
        ArrayList<Architecture> pop3 = new ArrayList<Architecture>(); 
        for (Architecture arch1:pop2) {
            boolean UNIQUE = true;
            for (Architecture arch2:pop2) {
                if (!arch1.getId().equalsIgnoreCase(arch2.getId()) && arch1.compareTo(arch2) == 0) {
                    UNIQUE = false;
                    break;
                }
            }
            if (UNIQUE)
                pop3.add(arch1);
        }
        System.out.println("New population has a size of " + pop3.size() + " archs");
        return pop3;
    }
    
    public ArrayList<Architecture> getPopulation() {
        return population;
    }

    public ArrayList<Architecture> getManualArchitectures() {
        ArrayList<Architecture> man_archs = new ArrayList<>();
        man_archs.add(new Architecture("000000000000000000000000000000000000000000000000000000000000",1));
        //N = 1 in random orbit (12) 
        for (int i = 0;i<Params.ninstr;i++) {
            StringBuilder str = new StringBuilder("000000000000000000000000000000000000000000000000000000000000");
            int orb = rnd.nextInt(Params.norb);
            str.setCharAt(Params.ninstr*orb + i, '1');
            man_archs.add(new Architecture(str.toString(),1));
        }
        //N = 2 in random orbit (66)
        for (int i = 0;i<Params.ninstr - 1;i++) {
            for (int j = i+1;j<Params.ninstr;j++) {
                StringBuilder str = new StringBuilder("000000000000000000000000000000000000000000000000000000000000");
                int orb = rnd.nextInt(Params.norb);
                str.setCharAt(Params.ninstr*orb + i, '1');
                str.setCharAt(Params.ninstr*orb + j, '1');
                man_archs.add(new Architecture(str.toString(),1));
            }
        }
        
        //One copy of each instrument in the same orbit
        man_archs.add(new Architecture("000000000000111111111111000000000000000000000000000000000000",1));
        
        //Two copies of each instrument in the same orbits
        man_archs.add(new Architecture("111111111111111111111111000000000000000000000000000000000000",1));
        
        //Reference architecture #1
        HashMap<String,String[]> map = new HashMap<>();
        String[] payl_polar = {""};map.put("LEO-600-polar-NA",payl_polar);
        String[] payl_AM = {"HYSP_TIR"};map.put("SSO-600-SSO-AM",payl_AM);
        String[] payl_600DD = {""};map.put("SSO-600-SSO-DD",payl_600DD);
        String[] payl_PM = {"GACM_VIS","GACM_SWIR"};map.put("SSO-800-SSO-PM",payl_PM);
        String[] payl_800DD = {""};map.put("SSO-800-SSO-DD",payl_800DD);
        man_archs.add(new Architecture(map,1));
        
        //Reference architecture #2
        HashMap<String,String[]> map2 = new HashMap<>();
        String[] payl2_polar = {""};map2.put("LEO-600-polar-NA",payl2_polar);
        String[] payl2_AM = {"HYSP_TIR"};map2.put("SSO-600-SSO-AM",payl2_AM);
        String[] payl2_600DD = {""};map2.put("SSO-600-SSO-DD",payl2_600DD);
        String[] payl2_PM = {"GACM_VIS","GACM_SWIR","POSTEPS_IRS"};map2.put("SSO-800-SSO-PM",payl2_PM);
        String[] payl2_800DD = {"DESD_SAR"};map2.put("SSO-800-SSO-DD",payl2_800DD);
        man_archs.add(new Architecture(map2,1));
        
        //Reference architecture #3
        HashMap<String,String[]> map3 = new HashMap<>();
        String[] payl3_polar = {"CLAR_ERB"};map3.put("LEO-600-polar-NA",payl3_polar);
        String[] payl3_AM = {"HYSP_TIR","POSTEPS_IRS"};map3.put("SSO-600-SSO-AM",payl3_AM);
        String[] payl3_600DD = {""};map3.put("SSO-600-SSO-DD",payl3_600DD);
        String[] payl3_PM = {"GACM_VIS","GACM_SWIR","POSTEPS_IRS"};map3.put("SSO-800-SSO-PM",payl3_PM);
        String[] payl3_800DD = {"DESD_SAR"};map3.put("SSO-800-SSO-DD",payl3_800DD);
        man_archs.add(new Architecture(map3,1));
        
        //Reference architecture #4
        HashMap<String,String[]> map4 = new HashMap<>();
        String[] payl4_polar = {"CLAR_ERB","CNES_KaRIN"};map4.put("LEO-600-polar-NA",payl4_polar);
        String[] payl4_AM = {"HYSP_TIR","POSTEPS_IRS"};map4.put("SSO-600-SSO-AM",payl4_AM);
        String[] payl4_600DD = {"DESD_LID"};map4.put("SSO-600-SSO-DD",payl4_600DD);
        String[] payl4_PM = {"GACM_VIS","GACM_SWIR","POSTEPS_IRS"};map4.put("SSO-800-SSO-PM",payl4_PM);
        String[] payl4_800DD = {"DESD_SAR"};map4.put("SSO-800-SSO-DD",payl4_800DD);
        man_archs.add(new Architecture(map4,1));
        
        //Reference architecture #5
        HashMap<String,String[]> map5 = new HashMap<>();
        String[] payl5_polar = {"CLAR_ERB","CNES_KaRIN","ACE_POL","ACE_ORCA"};map5.put("LEO-600-polar-NA",payl5_polar);
        String[] payl5_AM = {"HYSP_TIR","POSTEPS_IRS","ACE_LID"};map5.put("SSO-600-SSO-AM",payl5_AM);
        String[] payl5_600DD = {"DESD_LID"};map5.put("SSO-600-SSO-DD",payl5_600DD);
        String[] payl5_PM = {"GACM_VIS","GACM_SWIR","POSTEPS_IRS"};map5.put("SSO-800-SSO-PM",payl5_PM);
        String[] payl5_800DD = {"DESD_SAR"};map5.put("SSO-800-SSO-DD",payl5_800DD);
        man_archs.add(new Architecture(map5,1));
        return man_archs;
    }
    
    //Single architecture constructors
    public Architecture getRandomArch() {
        boolean[][] mat = new boolean[Params.norb][Params.ninstr];
        for (int i = 0; i < Params.norb; i++) {
            for (int j = 0;j < Params.ninstr;j++) {
                mat[i][j] = rnd.nextBoolean();
            }
        }
        return new Architecture(mat,Params.nsats[rnd.nextInt(Params.nsats.length)]);
    }

    public Architecture getTestArch() { // SMAP 2 SSO orbits, 2 sats per orbit
        //Architecture arch = new Architecture("0011000000111110000000000",1);
        //Architecture arch = new Architecture("000000000000000000000000000000000000000000000000000000010001",1);
        //Architecture arch = new Architecture("00100000000000000000000000000000000000000000000000000000100000000",1);
        //String[] payl = {"GACM_VIS"};
        //Architecture arch = new Architecture(payl,"SSO-600-SSO-DD");//LEO-600-polar-NA
        HashMap<String,String[]> map5 = new HashMap<String,String[]>();
        String[] payl5_polar = {};map5.put("LEO-600-polar-NA",payl5_polar);
        String[] payl5_AM = {"GACM_VIS"};map5.put("SSO-600-SSO-AM",payl5_AM);
        String[] payl5_600DD = {};map5.put("SSO-600-SSO-DD",payl5_600DD);
        String[] payl5_PM = {};map5.put("SSO-800-SSO-PM",payl5_PM);
        String[] payl5_800DD = {};map5.put("SSO-800-SSO-DD",payl5_800DD);
        Architecture arch = new Architecture(map5,1);
        arch.setEval_mode("DEBUG");
        return arch;//{"SMAP_RAD","SMAP_MWR","CMIS","VIIRS","BIOMASS"};{"600polar","600AM","600DD","800AM","800PM"};
    }

    public Architecture getTestArch2() {
        //String[] payl = {"GACM_VIS"};
        //Architecture arch = new Architecture(payl,"LEO-600-polar-NA");//LEO-600-polar-NA
        HashMap<String,String[]> map5 = new HashMap<String,String[]>();
        String[] payl5_polar = {};map5.put("LEO-600-polar-NA",payl5_polar);
        String[] payl5_AM = {"GACM_VIS"};map5.put("SSO-600-SSO-AM",payl5_AM);
        String[] payl5_600DD = {"GACM_VIS"};map5.put("SSO-600-SSO-DD",payl5_600DD);
        String[] payl5_PM = {};map5.put("SSO-800-SSO-PM",payl5_PM);
        String[] payl5_800DD = {};map5.put("SSO-800-SSO-DD",payl5_800DD);
        Architecture arch = new Architecture(map5,1);
        arch.setEval_mode("DEBUG");
        return arch;
    }
    
    public Architecture getTestArch3() {
        //String[] payl = {"GACM_VIS"};
        //Architecture arch = new Architecture(payl,"LEO-600-polar-NA");//LEO-600-polar-NA
        HashMap<String,String[]> map5 = new HashMap<String,String[]>();
        String payl5_polar; 
        payl5_polar = ("GACM_SWIR POSTEPS_IRS");map5.put("LEO-600-polar-NA",payl5_polar.split(" "));
        String payl5_AM = ("ACE_LID");map5.put("SSO-600-SSO-AM",payl5_AM.split(" "));
        String payl5_600DD = ("ACE_ORCA DESD_LID DESD_SAR");map5.put("SSO-600-SSO-DD",payl5_600DD.split(" "));
        String payl5_PM = ("DESD_SAR");map5.put("SSO-800-SSO-PM",payl5_PM.split(" "));
        String payl5_800DD = ("DESD_SAR ACE_LID");map5.put("SSO-800-SSO-DD",payl5_800DD.split(" "));
        Architecture arch = new Architecture(map5,1);
     //   arch.setEval_mode("DEBUG");
        return arch;
    }
    
        public Architecture getTestArch4() {
        //String[] payl = {"GACM_VIS"};
        //Architecture arch = new Architecture(payl,"LEO-600-polar-NA");//LEO-600-polar-NA
        HashMap<String,String[]> map5 = new HashMap<String,String[]>();
        String payl5_polar; 
        payl5_polar = ("ACE_LID");map5.put("LEO-600-polar-NA",payl5_polar.split(" "));
        String payl5_AM = ("CNES_KaRIN ");map5.put("SSO-600-SSO-AM",payl5_AM.split(" "));
        String payl5_600DD = ("ACE_ORCA DESD_LID DESD_SAR");map5.put("SSO-600-SSO-DD",payl5_600DD.split(" "));
        String payl5_PM = ("ACE_POL HYSP_TIR");map5.put("SSO-800-SSO-PM",payl5_PM.split(" "));
        String payl5_800DD = ("GACM_SWIR DESD_LID");map5.put("SSO-800-SSO-DD",payl5_800DD.split(" "));
        Architecture arch = new Architecture(map5,1);
     //   arch.setEval_mode("DEBUG");
        return arch;
    }

    public Architecture defineNewArch(String booleanString){
        return new Architecture(booleanString, 1);        
    }        

    public Architecture defineNewArch(ArrayList<String> input_arch) {
        HashMap<String,String[]> map5 = new HashMap<>();
        String[] instrument_list = Params.instrument_list;
        String[] orbit_list = Params.orbit_list;
        String relabeled_instrument_list = "ABCDEFGHIJKL";
        for (int j = 0; j < input_arch.size(); j++){
            String payloads = input_arch.get(j);
            String[] instruments = new String[payloads.length()];
            for(int i = 0; i < payloads.length(); i++){
                int index;
                if (i == payloads.length() - 1) {
                    index = relabeled_instrument_list.indexOf(payloads.substring(i));
                }
                else {
                    index = relabeled_instrument_list.indexOf(payloads.substring(i,i+1));
                }
                instruments[i] = instrument_list[index];
            }
            map5.put(orbit_list[j], instruments);
        }

        Architecture arch = new Architecture(map5,1);
        return arch;
    }
    
    
    public Architecture getMaxArch() {
        boolean[][] mat = new boolean[Params.norb][Params.ninstr];
        for (int i = 0; i < Params.norb; i++) {
            for (int j = 0;j < Params.ninstr; j++) {
                mat[i][j] = true;
            }
        }
        return new Architecture(mat,1);
    }

    public Architecture getMaxArch2() { // SMAP 2 SSO orbits, 2 sats per orbit
        Architecture arch = new Architecture("111111111111111111111111000000000000000000000000000000000000",1);
        arch.setEval_mode("DEBUG");
        return arch;//{"SMAP_RAD","SMAP_MWR","CMIS","VIIRS","BIOMASS"};{"600polar","600AM","600DD","800AM","800PM"};
    }

    public Architecture getMinArch() {
        boolean[][] mat = new boolean[Params.norb][Params.ninstr];
        for (int i = 0; i < Params.norb; i++) {
            for (int j = 0;j < Params.ninstr;j++) {
                mat[i][j] = false;
            }

        }
        return new Architecture(mat,1);
    }

    public Architecture getUserEnteredArch() { // This architecture has a science score of 0.02
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        
        HashMap<String,String[]> mapping= new HashMap<String,String[]>();
        for (String orb: Params.orbit_list) {
            try {
                boolean valid = false;
                String input = "";
                while (!valid) {
                    System.out.println("New payload in " + orb + "? ");
                    input = bufferedReader.readLine();
                    String[] instruments = input.split(" ");
                    ArrayList<String> validInstruments = new ArrayList<>();
                    validInstruments.addAll(Arrays.asList(Params.instrument_list));
                    valid = true;
                    for (String instr: instruments) {
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
                mapping.put(orb,input.split(" "));
            }
            catch (Exception e) {
                System.out.println("EXC in getUserEnteredArch" + e.getMessage() + " " + e.getClass());
                e.printStackTrace();
                return null;
            }           
        }
        System.out.println("Num sats per orbit? ");
        try {
            String tmp = bufferedReader.readLine();
            return new Architecture(mapping,Integer.parseInt(tmp));
        }
        catch (Exception e) {
            System.out.println("EXC in getUserEnteredArch" + e.getMessage() + " " + e.getClass());
            e.printStackTrace();
            return null;
        }
    }

    public void setPopulation(ArrayList<Architecture> population) {
        this.population = population;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss.local;


import java.io.BufferedReader;
import java.io.FileReader;
import rbsa.eoss.Architecture;
import rbsa.eoss.ArchitectureEvaluator;
import rbsa.eoss.ArchitectureGenerator;
import rbsa.eoss.Result;
import rbsa.eoss.ResultCollection;
import rbsa.eoss.ResultManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import rbsa.eoss.DBManagement;


/**
 *
 * @author Bang
 */
public class RBSAEOSSEval {
    
    public static void main(String[] args){
        
        // Set a path to the project folder
        String path = "/Users/bang/workspace/RBSAEOSS-Eval-netbeans";
        
        // Initialization
        ArchitectureEvaluator AE = ArchitectureEvaluator.getInstance();
        ArchitectureGenerator AG = ArchitectureGenerator.getInstance();
        ResultManager RM = ResultManager.getInstance();
        Params params = null;
        String search_clps = "";
        params = new Params( path, "FUZZY-ATTRIBUTES", "test","normal",search_clps);//FUZZY or CRISP
        AE.init(1);        
        
        // Configure the database
//        DBManagement dbm = new DBManagement();
        // Initialize the database - do it only once
//        dbm.createNewDB();
//        dbm.encodeRules();




//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////// Evaluate single architecture ////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////        
        

        long t0 = System.currentTimeMillis();


        // Input a new architecture design
        // There must be 5 orbits. Instrument name is represented by a capital letter, taken from {A,B,C,D,E,F,G,H,I,J,K,L}
        ArrayList<String> input_arch = new ArrayList<>();
        String orbit_1 = "ABH"; input_arch.add(orbit_1);
        String orbit_2 = "KG"; input_arch.add(orbit_2);
        String orbit_3 = "A"; input_arch.add(orbit_3);
        String orbit_4 = "ALE"; input_arch.add(orbit_4);
        String orbit_5 = "BE"; input_arch.add(orbit_5);
//        String orbit_1 = "ABCDLH"; input_arch.add(orbit_1);
//        String orbit_2 = "AKDFG"; input_arch.add(orbit_2);
//        String orbit_3 = "ALBGH"; input_arch.add(orbit_3);
//        String orbit_4 = "ADCLE"; input_arch.add(orbit_4);
//        String orbit_5 = "BHKELJ"; input_arch.add(orbit_5);
        // Generate a new architecture
        Architecture architecture = AG.defineNewArch(input_arch);
        

//        Architecture architecture = AG.getMaxArch2();

        
        // Evaluate the architecture
        Result result = AE.evaluateArchitecture(architecture,"Slow");
        
        // Save the score and the cost
        double cost = result.getCost();
        double science = result.getScience();
        
        System.out.println("Performance Score: " + science + ", Cost: " + cost);
        
        
        long t1 = System.currentTimeMillis();
        System.out.println( "Evaluation done in: " + String.valueOf(t1-t0) + " msec");
        
        

//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////// Read in csv file and evaluate architectures ////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////


//        long t0 = System.currentTimeMillis();
//
//        // Read in a csv file and evaluate the architectures
//        String line = "";
//        String splitBy = ",";
//
//        ArrayList<String> bitStrings = new ArrayList<>();
//        String resultPath = "/Users/bang/workspace/RBSAEOSS-Eval-netbeans/results/EOSS_data.csv";
//        
//        try (BufferedReader br = new BufferedReader(new FileReader(resultPath))) {
////            skip header
////            line = br.readLine();
//        	
//            while ((line = br.readLine()) != null) {
//                // use comma as separator
//                String[] tmp = line.split(splitBy);
//                String bitString = tmp[0];
//                bitStrings.add(bitString);
//                double science = Double.parseDouble(tmp[1]);
//                double cost = Double.parseDouble(tmp[2]);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
//        
////        int numArchs = (int) dbm.getNArchs();
//        int numArchs = 0;
//        for(int i=0;i<1;i++){
//            int archID = numArchs + i + 1;
//            String bitString = bitStrings.get(archID-1);
//            Architecture architecture = new Architecture(bitString,1);
//
//            // Evaluate the architecture
//            Result result = AE.evaluateArchitecture(architecture,"Slow",archID);
//            // Save the score and the cost
//            double cost = result.getCost();
//            double science = result.getScience();
//
//            System.out.println("ArchID: "+ archID + ", Performance Score: " + science + ", Cost: " + cost);        
//        }
//        
//        
//
//        long t1 = System.currentTimeMillis();
//        System.out.println( "Evaluation done in: " + String.valueOf(t1-t0) + " msec");


//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////// Read in a result file and write in a csv format  //////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////


          // Reading in the result file and writing a csv file.
//        try{
//            PrintWriter writer = new PrintWriter("/Users/bang/workspace/RBSAEOSS-Eval-netbeans/results/EOSS_data.csv", "UTF-8");
//
//            // Load the existing result file
//            String result_path = Params.path + "/results/1.rs";
//            Stack<Result> results;
//            ResultCollection RC = RM.loadResultCollectionFromFile(result_path);
//            Stack<Result> tmpResults = RC.getResults();
//            results = new Stack<Result>();
//            for (Result tmpResult:tmpResults){
//                if(tmpResult.getScience()>=0.001){
//                    results.add(tmpResult);
//                }
//            }
//            int nResults = results.size();
//            for (int i=0;i<nResults;i++){
//                double sci = results.get(i).getScience();
//                double cos = results.get(i).getCost();
//                Architecture arch = results.get(i).getArch();
//                
//                String bitString="";
//                for(boolean bool:arch.getBitString()){
//                    if(bool){bitString=bitString+"1";}
//                    else{bitString=bitString+"0";}
//                }
//                writer.println(bitString+","+sci+","+cos);
//            }
//	        
//            result_path = Params.path + "/results/3.rs";
//            RC = RM.loadResultCollectionFromFile(result_path);
//            tmpResults = RC.getResults();
//            results = new Stack<Result>();
//            for (Result tmpResult:tmpResults){
//                if(tmpResult.getScience()>=0.001){
//                    results.add(tmpResult);
//                }
//            }
//            nResults = results.size();
//            for (int i=0;i<nResults;i++){
//                double sci = results.get(i).getScience();
//                double cos = results.get(i).getCost();
//                Architecture arch = results.get(i).getArch();
//                
//                String bitString="";
//                for(boolean bool:arch.getBitString()){
//                    if(bool){bitString=bitString+"1";}
//                    else{bitString=bitString+"0";}
//                }
//                writer.println(bitString+","+sci+","+cos);
//            }
//	        
//        
//            writer.close();
//        } catch (IOException e) {
//            // do something
//        }
        


//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////// Make Queries ////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////

//    Query Data    

//    ArrayList<String> slots = new ArrayList<>();
//    ArrayList<String> conditions = new ArrayList<>();
//    ArrayList<String> values = new ArrayList<>();
//    ArrayList<String> valueTypes = new ArrayList<>();
    
//    slots.add("average-power#");
//    conditions.add("gt");
//    values.add("100");
//    valueTypes.add("Double");
//    
    
//    dbm.makeQuery("science","CAPABILITIES::Manifested-instrument",slots,conditions,values,valueTypes); 
    
    
    System.out.println("Done");

    }
 
}

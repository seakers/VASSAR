package server;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */



import rbsa.eoss.Architecture;
import rbsa.eoss.Result;
import rbsa.eoss.ResultManager;
import rbsa.eoss.CritiqueGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javaInterface.BinaryInputArchitecture;
import javaInterface.VASSARInterface;


// Generated code
//import shared.*;

import rbsa.eoss.local.Params;

public class VASSARInterfaceHandler implements VASSARInterface.Iface {
    
    private rbsa.eoss.ArchitectureEvaluator AE = rbsa.eoss.ArchitectureEvaluator.getInstance();
    private rbsa.eoss.ArchitectureGenerator AG = rbsa.eoss.ArchitectureGenerator.getInstance();
    private boolean jessInitialized = false;

    public void ping() {
      System.out.println("ping()");
    }
  
    public String initJess(){
        
        if(jessInitialized) return "Jess already initialized";
        
        // Set a path to the project folder
        String path = "/Users/bang/workspace/daphne/VASSAR";
        
        // Initialization
        ResultManager RM = ResultManager.getInstance();
        Params params = null;
        String search_clps = "";
        params = new Params(path, "FUZZY-ATTRIBUTES", "test","normal",search_clps);//FUZZY or CRISP
        AE.init(1);        
        jessInitialized = true;
        return "Jess Initialized";
    }
 
  
    public BinaryInputArchitecture eval(List<Boolean> boolList){
        
        // Input a new architecture design
        // There must be 5 orbits. Instrument name is represented by a capital letter, taken from {A,B,C,D,E,F,G,H,I,J,K,L}

        initJess();
        
        String bitString = "";
        for(Boolean b:boolList){
            if(b) bitString = bitString + "1";
            else bitString = bitString + "0";
        }

        // Generate a new architecture
        Architecture architecture = AG.defineNewArch(bitString);

        // Evaluate the architecture
        Result result = AE.evaluateArchitecture(architecture,"Slow");
        
        // Save the score and the cost
        double cost = result.getCost();
        double science = result.getScience();
        List<Double> outputs = new ArrayList<>();
        outputs.add(science);
        outputs.add(cost);
        
        System.out.println("Performance Score: " + science + ", Cost: " + cost);
        return new BinaryInputArchitecture(0,boolList,outputs);
    }
    
    
    @Override
    public List<String> getCritique(List<Boolean> boolList){
        
        initJess();
        
        String bitString = "";
        for(Boolean b:boolList){
            if(b) bitString = bitString + "1";
            else bitString = bitString + "0";
        }
        
        System.out.println(bitString);

        // Generate a new architecture
        Architecture architecture = AG.defineNewArch(bitString);

        // Initialize Critique Generator
        CritiqueGenerator critiquer = new CritiqueGenerator(architecture);
        
        List<String> critique = critiquer.getCritique();
        
        return critique;
    }
    
    @Override
    public List<BinaryInputArchitecture> runLocalSearch(List<Boolean> boolList){
        
        initJess();
        
        String bitString = "";
        for(Boolean b:boolList){
            if(b) bitString = bitString + "1";
            else bitString = bitString + "0";
        }
        
        ArrayList<String> samples = randomLocalChange(bitString, 4);
        
        List<BinaryInputArchitecture> out = new ArrayList<>();
        
        for(String sample:samples){
            // Generate a new architecture
            Architecture architecture = AG.defineNewArch(sample);

            // Evaluate the architecture
            Result result = AE.evaluateArchitecture(architecture,"Slow");

            // Save the score and the cost
            double cost = result.getCost();
            double science = result.getScience();
            List<Double> outputs = new ArrayList<>();
            outputs.add(science);
            outputs.add(cost);  
            
            System.out.println("bitString: "+ sample +", Science: " + science + ", Cost: " + cost);
            
            BinaryInputArchitecture arch = new BinaryInputArchitecture(0,bitString2BoolArray(sample),outputs);
            out.add(arch);
        }
        
        return out;
    }    
    
    
    public ArrayList<String> randomLocalChange(String bitString, int n){
        
        Random rand = new Random();
        
        int numVars = Params.orbit_list.length * Params.instrument_list.length;
        
        ArrayList<String> out = new ArrayList<>();
        
        for(int i=0;i<n;i++){
            int  k = rand.nextInt(numVars);
            
            StringBuilder tempBitString = new StringBuilder(bitString);
            if(bitString.charAt(k)=='1'){
                tempBitString.setCharAt(k, '0');
            }else{
                tempBitString.setCharAt(k, '1');
            }
            out.add(tempBitString.toString());
        }  
        return out;
    }
    
    
    public List<Boolean> bitString2BoolArray(String bitString){
        List<Boolean> out = new ArrayList<>();
        for(int i=0;i<bitString.length();i++){
            if(bitString.charAt(i)=='1'){
                out.add(true);
            }else{
                out.add(false);
            }
        }
        return out;
    }
    
    
    
    public ArrayList<String> getOrbitList(){
        ArrayList<String> orbitList = new ArrayList<>();
        String[] list = Params.orbit_list;
        for(String o:list){
            orbitList.add(o);
        }
        return orbitList;
    }
    
    
    public ArrayList<String> getInstrumentList(){
        ArrayList<String> instrumentList = new ArrayList<>();
        String[] list = Params.instrument_list;
        for(String i:list){
            instrumentList.add(i);
        }
        return instrumentList;
    }


}


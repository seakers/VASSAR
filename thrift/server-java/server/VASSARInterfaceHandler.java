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
import java.util.ArrayList;



import javaInterface.ArchitectureInfo;
import javaInterface.VASSARInterface;
import org.apache.thrift.TException;

// Generated code
//import shared.*;

import java.util.HashMap;
import rbsa.eoss.local.Params;

public class VASSARInterfaceHandler implements VASSARInterface.Iface {
    
    rbsa.eoss.ArchitectureEvaluator AE = rbsa.eoss.ArchitectureEvaluator.getInstance();
    rbsa.eoss.ArchitectureGenerator AG = rbsa.eoss.ArchitectureGenerator.getInstance();
    

    public void ping() {
      System.out.println("ping()");
    }
  
    public String initJess(){
        // Set a path to the project folder
        String path = "/Users/bang/workspace/RBSAEOSS-Eval-netbeans";
        
        // Initialization
        ResultManager RM = ResultManager.getInstance();
        Params params = null;
        String search_clps = "";
        params = new Params( path, "FUZZY-ATTRIBUTES", "test","normal",search_clps);//FUZZY or CRISP
        AE.init(1);        
        return "Jess Initialized";
    }
 
  
    public ArchitectureInfo eval(String anything){
        // Input a new architecture design
        // There must be 5 orbits. Instrument name is represented by a capital letter, taken from {A,B,C,D,E,F,G,H,I,J,K,L}
        ArrayList<String> input_arch = new ArrayList<>();
        String orbit_1 = "ABH"; input_arch.add(orbit_1);
        String orbit_2 = "KG"; input_arch.add(orbit_2);
        String orbit_3 = "A"; input_arch.add(orbit_3);
        String orbit_4 = "ALE"; input_arch.add(orbit_4);
        String orbit_5 = "BE"; input_arch.add(orbit_5);
        // Generate a new architecture
        Architecture architecture = AG.defineNewArch(input_arch);

        // Evaluate the architecture
        Result result = AE.evaluateArchitecture(architecture,"Slow");
        
        boolean[] bitString = result.getArch().getBitString();
        String booleanString = "";
        for(int i=0;i<bitString.length;i++){
            if(bitString[i]){
                booleanString = booleanString + "1";
            }else{
                booleanString = booleanString + "0";
            }
                    
        }
        
        // Save the score and the cost
        double cost = result.getCost();
        double science = result.getScience();
        
        System.out.println("Performance Score: " + science + ", Cost: " + cost);
        return new ArchitectureInfo(science,cost,booleanString);
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


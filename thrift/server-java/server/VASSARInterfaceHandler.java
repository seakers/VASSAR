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
    boolean jessInitialized = false;

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
        params = new Params(path, "FUZZY-ATTRIBUTES", "test","normal",search_clps);//FUZZY or CRISP
        AE.init(1);        
        jessInitialized = true;
        return "Jess Initialized";
    }
 
  
    public ArchitectureInfo eval(String bitString){
        
        // Input a new architecture design
        // There must be 5 orbits. Instrument name is represented by a capital letter, taken from {A,B,C,D,E,F,G,H,I,J,K,L}

        if(!jessInitialized){
            initJess();
        }

        // Generate a new architecture
        Architecture architecture = AG.defineNewArch(bitString);

        // Evaluate the architecture
        Result result = AE.evaluateArchitecture(architecture,"Slow");
        
        // Save the score and the cost
        double cost = result.getCost();
        double science = result.getScience();
        
        System.out.println("Performance Score: " + science + ", Cost: " + cost);
        return new ArchitectureInfo(science,cost,bitString);
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


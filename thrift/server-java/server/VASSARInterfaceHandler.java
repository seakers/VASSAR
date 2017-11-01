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
import rbsa.eoss.ArchitectureEvaluator;
import rbsa.eoss.ArchitectureGenerator;
import rbsa.eoss.Result;
import rbsa.eoss.ResultManager;
import rbsa.eoss.CritiqueGenerator;

import java.util.ArrayList;
import java.util.List;

import javaInterface.BinaryInputArchitecture;
import javaInterface.VASSARInterface;
import org.apache.thrift.TException;

// Generated code
//import shared.*;

import java.util.HashMap;
import java.util.Map;

import rbsa.eoss.local.Params;

public class VASSARInterfaceHandler implements VASSARInterface.Iface {

    private Params params;
    private ArchitectureEvaluator AE = null;
    private ArchitectureGenerator AG = null;
    private boolean jessInitialized = false;

    public void ping() {
      System.out.println("ping()");
    }
  
    private String initJess() {
        if(jessInitialized) {
            return "Jess already initialized";
        }
        
        // Set a path to the project folder
        String path = System.getProperty("user.dir");
        
        // Initialization
        String search_clps = "";
        params = Params.initInstance(path, "FUZZY-ATTRIBUTES", "test","normal", search_clps);//FUZZY or CRISP
        AE = ArchitectureEvaluator.getInstance();
        AG = ArchitectureGenerator.getInstance();
        AE.init(1);
        ResultManager RM = ResultManager.getInstance();
        jessInitialized = true;
        return "Jess Initialized";
    }

    public BinaryInputArchitecture eval(List<Boolean> boolList) {
        // Input a new architecture design
        // There must be 5 orbits. Instrument name is represented by a capital letter, taken from {A,B,C,D,E,F,G,H,I,J,K,L}
        initJess();
        
        String bitString = "";
        for (Boolean b: boolList) {
            bitString += b ? "1" : "0";
        }

        // Generate a new architecture
        Architecture architecture = AG.defineNewArch(bitString);

        // Evaluate the architecture
        Result result = AE.evaluateArchitecture(architecture,"Slow");
        
        // Save the score and the cost
        double cost = result.getCost();
        double science = result.getScience();
        List<Double> outputs = new ArrayList<>();
        outputs.add(cost);
        outputs.add(science);
        
        System.out.println("Performance Score: " + science + ", Cost: " + cost);
        return new BinaryInputArchitecture(0, boolList, outputs);
    }

    public List<String> getCritique(List<Boolean> boolList) {
        initJess();
        String bitString = "";
        for(Boolean b: boolList){
            bitString += b ? "1" : "0";
        }
        
        System.out.println(bitString);

        // Generate a new architecture
        Architecture architecture = AG.defineNewArch(bitString);

        // Initialize Critique Generator
        CritiqueGenerator critiquer = new CritiqueGenerator(architecture);

        return critiquer.getCritique();
    }

    public ArrayList<String> getOrbitList() {
        ArrayList<String> orbitList = new ArrayList<>();
        for(String o: Params.orbit_list){
            orbitList.add(o);
        }
        return orbitList;
    }

    public ArrayList<String> getInstrumentList() {
        ArrayList<String> instrumentList = new ArrayList<>();
        for (String i: Params.instrument_list) {
            instrumentList.add(i);
        }
        return instrumentList;
    }

    public ArrayList<String> getObjectiveList() {
        initJess();
        ArrayList<String> objectiveList = new ArrayList<>();
        params.objective_descriptions.forEach((k, v) -> {
            objectiveList.add(k);
        });
        return objectiveList;
    }
}


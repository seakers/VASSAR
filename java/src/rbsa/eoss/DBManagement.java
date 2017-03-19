/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBList;
import com.mongodb.client.MongoCursor;
import com.mongodb.DBCursor;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import jess.Defrule;
import jess.Fact;
import jess.Rete;
import rbsa.eoss.local.Params;


/**
 *
 * @author Bang
 */
public class DBManagement {
    
    private MongoClient mongoClient;
//    private String dbName = "EOSS_eval_data";
    private String dbName = "rbsa_eoss";
    private String metaDataCollectionName = "metadata";
    private String ruleCollectionName = "jessRules";
    private ArrayList<String> dataCollectionNames;
    private static DBManagement instance = null;

    
    public DBManagement(){
        try{            
//            mongoClient = new MongoClient( "localhost" , 27017 );
            MongoClientURI uri = new MongoClientURI("mongodb://bang:qkdgustmd@ds145828.mlab.com:45828/rbsa_eoss");
            mongoClient = new MongoClient(uri);
            
            dataCollectionNames = new ArrayList<>();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }    
    
    public DBManagement(String dbName){
        try{            
            this.dbName = dbName;
            mongoClient = new MongoClient( "localhost" , 27017 );
            dataCollectionNames = new ArrayList<>();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static DBManagement getInstance()
    {
        if( instance == null ) 
        {
            instance = new DBManagement();
        }
        return instance;
    }

    

    public void addNewCollection(String colName){
        this.dataCollectionNames.add(colName);
    }
    
    public void createNewDB(){
        boolean dbExists = false;
        MongoCursor<String> iter = mongoClient.listDatabaseNames().iterator();
        while(iter.hasNext()){
            if(iter.next().equals(dbName)){
                dbExists = true;
            }
        }   
        if(dbExists) {
            mongoClient.getDatabase(dbName).drop();
        }
    }
    
    

    public void encodeMetadata(int ArchID, String bitString, double science, double cost){
        MongoDatabase Mdb = mongoClient.getDatabase(dbName);
        MongoCollection col = Mdb.getCollection(metaDataCollectionName);
        col.insertOne(
                new Document()
                    .append("ArchID", (double) ArchID)
                    .append("bitString",bitString)
                    .append("science", science)
                    .append("cost",cost)
        );
    }
    
    public void encodeData(int ArchID, String collectionPrefix, Rete r, QueryBuilder qb){
        MongoDatabase Mdb = mongoClient.getDatabase(dbName);

        ArrayList<Integer> factsToEncode = new ArrayList<>();
        ArrayList<Integer> factsEncoded = new ArrayList<>();
            
        try{
            
            JessFactHandler jfh = null;
            if(collectionPrefix.equalsIgnoreCase("science")){
                jess.Fact value = qb.makeQuery("AGGREGATION::VALUE").get(0);
                jfh = new JessFactHandler(value, r, qb);
                factsToEncode = jfh.getParentFactIDs();
            }else if(collectionPrefix.equalsIgnoreCase("cost")){
                ArrayList<Fact> missions = qb.makeQuery("MANIFEST::Mission");
                for(Fact m:missions){
                    factsToEncode.add(m.getFactId());
                }
                jfh = new JessFactHandler(missions.get(0), r, qb);
            }
            
            int cnt =0;
            while(factsToEncode.size() > 0){
                // Get the first element from the array and remove it from the list
                jess.Fact thisFact = r.findFactByID(factsToEncode.get(0));
                
                // Requested Fact had been retracted
                if(thisFact==null){
                    // Related to the launch vehicle selection during cost calculation
                    System.out.println("Requested Fact had been retracted"); 
                    factsEncoded.add(factsToEncode.get(0));
                    factsToEncode.remove(0);
                    cnt++;
                    continue;
                } else if(thisFact.getName().equals("DATABASE::Instrument")){
                    jess.Value slotVal = thisFact.getSlotValue("Name");
                    String instrumentName = slotVal.stringValue(r.getGlobalContext());                    
                    if(this.QueryExists("science.DATABASE.Instrument","Name",instrumentName)){
                        // Remove the encoded fact from the list
                        factsToEncode.remove(0);
                        factsEncoded.add(thisFact.getFactId());
                        continue;
                    }
                } else if(thisFact.getName().equals("DATABASE::Launch-vehicle")){
                    jess.Value slotVal = thisFact.getSlotValue("id");
                    String lvName = slotVal.stringValue(r.getGlobalContext());                    
                    if(this.QueryExists("cost.DATABASE.Launch_vehicle","id",lvName)){
                        // Remove the encoded fact from the list
                        factsToEncode.remove(0);
                        factsEncoded.add(thisFact.getFactId());
                        continue;
                    }
                }
                
                // Encode the fact
                org.bson.Document doc = encodeFact(ArchID, thisFact,r,qb);
                
                String collectionName = collectionPrefix + "." + thisFact.getName().replace("::",".");
                collectionName = collectionName.replace("-", "_");
                
                MongoCollection col = Mdb.getCollection(collectionName);
                col.insertOne(doc);
                
                // Remove the encoded fact from the list
                factsToEncode.remove(0);
                factsEncoded.add(thisFact.getFactId());
                // Get new list of facts to encode
                jfh.setNewFact(thisFact);
                ArrayList<Integer> newFacts = jfh.getParentFactIDs();
                for(int fid:newFacts){
                    // If it is a new fact, add it to the list
                    if(!factsToEncode.contains(fid) && !factsEncoded.contains(fid)){
                        factsToEncode.add(fid);
                    }
                }           
                cnt++;
                if(cnt>3000){break;}
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    public org.bson.Document encodeFact(int ArchID, jess.Fact f, Rete r, QueryBuilder qb){
        
        org.bson.Document doc = new org.bson.Document();
        
        try{
            doc.append("ArchID", (double) ArchID);
            doc.append("factName", f.getName());
            doc.append("factID", (double) f.getFactId());
            doc.append("module", f.getModule());            
        
            jess.Deftemplate factTemplate = f.getDeftemplate();
            String[] slots = factTemplate.getSlotNames();
            
            for(int i=0;i<slots.length;i++){
                String slot = slots[i];
                jess.Value slotVal = f.getSlotValue(slot);
                String slotVal_string = slotVal.toString();
                
                if(slotVal_string.contains("Java-Object")){
                    // Java Objects are not saved in the database for now
                    continue;
                }else if(slotVal_string.isEmpty() || slotVal_string.equals("\"\"")){
                    // Value is empty
                    continue;
                }
                
                if(factTemplate.isMultislot(i)){
                    // Save as string
                    doc.append(slot, slotVal_string);
                }else{
                    // Not a multi-slot
                    if(!slotVal_string.equals("nil")){
                        // If the value is nil then don't save it in the DB
                        if(slotVal.isNumeric(r.getGlobalContext())){
                            double slotVal_double = slotVal.floatValue(r.getGlobalContext());
                            doc.append(slot,slotVal_double);
                        }else{
                            // Save as string
                            String slotVal_string2 = slotVal.stringValue(r.getGlobalContext());
                            doc.append(slot, slotVal_string2);
                        }
                    }
                }
                
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(f.getFactId());
        }
        return doc;
    }
    
    
    
    public void encodeRules(){
        
        HashMap<Integer,String> rules_id_to_name = Params.rules_IDtoName_map;
        HashMap<String,Integer> rules_name_to_id = Params.rules_NametoID_Map;
        HashMap<String,Defrule> rules = Params.rules_defrule_map;
        
        MongoDatabase Mdb = mongoClient.getDatabase(dbName);
        MongoCollection col = Mdb.getCollection(this.ruleCollectionName);

        Set<Integer> ruleIDs = rules_id_to_name.keySet();
        Iterator<Integer> iter = ruleIDs.iterator();
        
        try{
            while(iter.hasNext()){
                
                int id = iter.next();
                String ruleName = rules_id_to_name.get(id);
                Defrule defrule = rules.get(ruleName);
                
                String module = ruleName.split("::")[0];
                                
                // Encode the fact
                org.bson.Document doc = new org.bson.Document();
                
                doc.append("ruleID",id)
                    .append("ruleName",ruleName)
                    .append("module",module);
                col.insertOne(doc);  
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }    
    
    
    
    
    
    
    
    public void makeQuery(){
        
//        MongoDatabase Mdb = mongoClient.getDatabase(dbName);
//        MongoCollection col = Mdb.getCollection("science.MANIFEST.Mission");
//        FindIterable found = col.find(
//                and(
//                    eq("factName","MANIFEST::Mission"),
//                    gte("fraction-sunlight",0.7)
//                ));
//        
////        Example:
////        col.find(and(gte("stars", 2), lt("stars", 5), eq("categories", "Bakery")));
//
//        found.projection(fields(
//                    include("factName","factID","module","fraction-sunlight"),
//                    exclude("_id")
//                ));
//        MongoCursor iter = found.iterator();
//        while(iter.hasNext()){
//            Document doc = (Document) iter.next();
//            System.out.println(doc.get("fraction-sunlight").getClass().toString());
//            System.out.println(doc.toString());
//        }
    }    
    
    
    public double getNArchs(){
        MongoDatabase Mdb = mongoClient.getDatabase(dbName);
        MongoCollection col = Mdb.getCollection(this.metaDataCollectionName);
        return col.count();
    }
    
    
    public void getClassOfSlot(String collectionName, String slotName){
        MongoDatabase Mdb = mongoClient.getDatabase(dbName);
        
        MongoCollection col = Mdb.getCollection(collectionName);
  
        FindIterable found = col.find();
        MongoCursor iter = found.iterator();
        org.bson.Document doc = (org.bson.Document) iter.next();
        System.out.println(doc.get(slotName).getClass().toString());
        
    }    
    
    
    public boolean findMatchingArch(String booleanString){
        MongoDatabase Mdb = mongoClient.getDatabase(dbName);
        
        MongoCollection col = Mdb.getCollection(this.metaDataCollectionName);
        Document filter = new Document("bitString",booleanString);
  
        FindIterable found = col.find(filter);
        MongoCursor iter = found.iterator();
        if(iter.hasNext()){
            return true;
        }
        return false;
    }
    
    public boolean QueryExists(String collectionName, String slotName, String value){
        MongoDatabase Mdb = mongoClient.getDatabase(dbName);
        MongoCollection col = Mdb.getCollection(collectionName);
        Document filter = new Document(slotName,value);
        FindIterable found = col.find(filter);
        MongoCursor iter = found.iterator();
        return iter.hasNext();    
    }
    
    
    
    
    
    /**
     * Makes a query from the database
     * 
     * @param collectionPrefix: Prefix for the collection name: science or cost
     * @param factName: Name of the fact to be searched
     * @param slots: Names of the slots to be used in the filter
     * @param conditions: Equality and inequality signs. Valid input are: gt, lt, gte, lte, eq
     * @param values: Values to be compared.
     * @param valueTypes: Types of values. Valid inputs are: Double, String
     */
    public void makeQuery(String collectionPrefix, String factName, ArrayList<String> slots, ArrayList<String> conditions, 
                            ArrayList<String> values, ArrayList<String> valueTypes){
        
        MongoDatabase Mdb = mongoClient.getDatabase(dbName);
        
        String collectionName = collectionPrefix + "." + factName.replace("::",".").replace("-", "_");
        MongoCollection col = Mdb.getCollection(collectionName);
        
        Document filter = new Document("factName",factName);
        
        for(int i=0;i<slots.size();i++){
            String slotName = slots.get(i);
            String cond = conditions.get(i);
            String val = values.get(i);
            String valType = valueTypes.get(i);
            if(cond.equals("eq")){
                if(valType.equals("String")){
                    filter.append(slotName,val);
                }
                else if(valType.equals("Double")){
                    filter.append(slotName, Double.parseDouble(val));
                }
            }
            else if(cond.equals("gt") || cond.equals("gte") || cond.equals("lt") || cond.equals("lte")){
                if(valType.equals("String")){
                    filter.append(slotName, new Document("$"+cond,val));
                }
                else if(valType.equals("Double")){
                    filter.append(slotName, new Document("$"+cond,Double.parseDouble(val)));
                }
            }            
        }
        
        FindIterable found = col.find(filter);
        found.projection(fields(
                    exclude("_id","factID","factHistory")
                ));
        MongoCursor iter = found.iterator();
        while(iter.hasNext()){
            Document doc = (Document) iter.next();
            System.out.println(doc.toString());
        }
    }
    

    
    
    public class JessFactHandler{
        
        private jess.Fact f;
        private int factID;
        private String factName;
        private String factHistory;
        private Rete r;
        private QueryBuilder qb;
        
        public JessFactHandler(jess.Fact f, Rete r, QueryBuilder qb){
            try{
                this.factID = f.getFactId();
                this.factName = f.getName();
                String facthis = f.getSlotValue("factHistory").stringValue(r.getGlobalContext());
                
//              "{R110 {R112 {R112 {R112 F672 S675} S674} S673}}"
                this.factHistory = facthis.replace('{', '(').replace('}',')');
                this.r = r;
                this.qb = qb;
                
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        public String getName(){return this.factName;}
        public int getID(){return this.factID;}
        
        public void setNewFact(jess.Fact f){
            try{
                this.factID = f.getFactId();
                this.factName = f.getName();
                String facthis = f.getSlotValue("factHistory").stringValue(r.getGlobalContext());
                this.factHistory = facthis.replace('{', '(').replace('}',')');
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        
        /**
         * Returns a list of ID's of the Facts that are used to generate and modify the current Fact
         * 
         * @return ArrayList of factIDs
         */
        public ArrayList<Integer> getParentFactIDs(){
            ArrayList<Integer> factIDs = new ArrayList<>();

            JessExpressionAnalyzer jea = JessExpressionAnalyzer.getInstance();
            int level = jea.getNestedParenLevel(this.factHistory);
            
            for (int i=0; i < level; i++){
                
                String inside = jea.getInsideParen(factHistory,i+1);
                String[] insideSplit = inside.split(" ", 2);
                if (insideSplit[0].substring(1).equalsIgnoreCase("nil")){
//                    System.out.println("nil found: " + this.factID);
//                    System.out.println(factHistory);
                    continue;
                }
                // First element of a factHistory should be a rule
                int ruleID = Integer.parseInt(insideSplit[0].substring(1));
                String rest = insideSplit[1];
                rest = jea.collapseAllParenIntoSymbol(rest);
                
                String[] restSplit = rest.split(" ");                
                for (String tmp:restSplit){
                    if((tmp.startsWith("A")) || (tmp.startsWith("F")) || (tmp.startsWith("S")) || (tmp.startsWith("D")) || (tmp.startsWith("J"))){
                        // A: Newly asserted
                        // F: Modified
                        // S: Slot values are used
                        // D: Duplicated
                        // J: Asserted from Java
                        
                        
                        if(tmp.startsWith("F")){continue;}
                        
                        int id = Integer.parseInt(tmp.substring(1));
                        if(!factIDs.contains(id)){
                            factIDs.add(id);
                        }
                    }
                }                
            }            
            return factIDs;
        }
        

        
        
    }
        
        
    
   
    
    
    
    
}
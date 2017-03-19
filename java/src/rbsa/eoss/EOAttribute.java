package rbsa.eoss;

// package KBSAofEOSS;
import java.util.Hashtable;
import java.util.Iterator;

//import org.apache.commons.lang.ArrayUtils;
public class EOAttribute {
// types of attributes: 
    // GB good boolean (yes/no, yes includes no), BB bad boolean (no includes yes, NB neutral Boolean (yes and no are simply different)
    // LIB2 (High, Low High incldues low) LIB3 (High,Medium,Low, High includes all), LIB5 (with highest and lowest) 
    // SIB2 (Low, High, Low incldues high) SIB3 (Low,Medium,High, Low includes all), SIB5 (with lowest and highest)
    // NL Neutral List (they are just all different, e.g., bands)
    // OL Ordered List (unspecified list of values, but they have a defined preference order)
    public int id;
    public String characteristic;
    public String value;
    public String type;
    public Hashtable<String, Integer> acceptedValues = new Hashtable<String, Integer>();
    
    public EOAttribute(){
        this.characteristic = "N/A";
        this.value = "N/A";
    }
    
    public EOAttribute(String charact, String val){
//         this.type = typ;
        this.characteristic = charact;
        this.value = val;
        }

    public String getCharacteristic(){
        return this.characteristic;

    }
    //public void clone(EOAttribute other){
      //  this.characteristic = other.characteristic;
        //this.value = other.value;
    //}
    public int SameOrBetter(EOAttribute other) {
         // By default
            int z = 0;
         if(this.value.compareTo(other.value)==0) {
             z = 0;
         }
         else {
             z = -1;
         }
     return z;    
     }

    public Hashtable<Integer, String> getReverseAcceptedValues() {
        Hashtable<Integer, String> reverseAcceptedValues = new Hashtable<Integer, String>();
        Iterator key_set = this.acceptedValues.keySet().iterator();
        int size = this.acceptedValues.size();
        for(int i = 1; i <= size; i++){
                String key = (String) key_set.next();
                reverseAcceptedValues.put(this.acceptedValues.get(key), key);
        }
                return reverseAcceptedValues;
    }
    public String Improve() {
         // By default
         if(this.value.compareTo("Low")==0) {
             this.value = "Medium";
         }
         else {
             this.value = "High";
         }
      return this.value;
     }

    public String Worsen() {
        // By default
        if(this.value.compareTo("High")==0) {
            this.value = "Medium";
        }
        else {
            this.value = "Low";
        }


     return this.value;
    }
    

public boolean CheckValue(String value) {
        boolean result = false;
        if(this.acceptedValues.size()>0){ // NL or OL attribute
            if(this.acceptedValues.containsKey(value)){
                result = true;
            }
            else{
                result = false;
            }
        }
         else{
                result = true;
         }

        return result;
    }
    public  EOAttribute cloneAttribute(EOAttribute other){
        EOAttribute n = new EOAttribute(other.characteristic, other.value);
        return n;
    }
}

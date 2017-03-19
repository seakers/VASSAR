package rbsa.eoss;

// package KBSAofEOSS;
import java.util.Hashtable;
public class NLAttribute extends EOAttribute {
    public NLAttribute(String charact, String val, Hashtable<String, Integer> accepted){
        this.characteristic = charact;
        this.value = val;
        this.type = "NL";
        this.acceptedValues = accepted;
        }
    
    public NLAttribute(String charact, String val){
        this.characteristic = charact;
        this.value = val;
        this.type = "NL";

        //EOAttribute att = (EOAttribute) GlobalVariables.measurementAttributeSet.get(charact);
        //this.acceptedValues = att.acceptedValues;
        }
    @Override
    public int SameOrBetter(EOAttribute other) {
     // Since this is a Neutral List attribute:
        int z = 0;
     int value_this = this.acceptedValues.get(this.value);
     int value_other = other.acceptedValues.get(other.value);
     if(value_this == value_other) {
         z = 0;
     }
     else {
         z = -1;
     }
 return z;    
 }



    @Override
   public  NLAttribute cloneAttribute(EOAttribute other){
        NLAttribute n = new NLAttribute(other.characteristic, other.value);
        return n;
    }
}

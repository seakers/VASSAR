package rbsa.eoss;

// package KBSAofEOSS;
import java.util.HashMap;

public class GlobalVariables {
// This class contains all the hashtables used to define the parameters
// List of measurement parameters
    public static HashMap measurementAttributeList;
    public static HashMap measurementAttributeKeys;
    public static HashMap measurementAttributeTypes;
    public static HashMap measurementAttributeSet;

    // List of instrument parameters
    public static HashMap instrumentAttributeList;
    public static HashMap instrumentAttributeKeys;
    public static HashMap instrumentAttributeTypes;
    public static HashMap instrumentAttributeSet;
    
    public static void defineMeasurement(HashMap attribs, HashMap attribKeys, HashMap attribTypes, HashMap attribSet){
        measurementAttributeList = attribs;
        measurementAttributeKeys = attribKeys;
        measurementAttributeTypes = attribTypes;
        measurementAttributeSet = attribSet;
    }
   public static void defineInstrument(HashMap attribs, HashMap attribKeys, HashMap attribTypes, HashMap attribSet){
        instrumentAttributeList = attribs;
        instrumentAttributeKeys = attribKeys;
        instrumentAttributeTypes = attribTypes;
        instrumentAttributeSet = attribSet;
    }
    
}
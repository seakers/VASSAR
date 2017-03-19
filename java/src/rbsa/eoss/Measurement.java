package rbsa.eoss;

// package KBSAofEOSS;
import java.util.HashMap;
import java.beans.*;

public class Measurement {
    public String parameter;
    public EOAttribute[] attributes = new EOAttribute[100];
    public HashMap attribute_list = new HashMap();
    public int[] assigned_attributes = new int[100];// array containing the ids of the attributes assigned to this measurement, not including the parameter, e.g. : [1, 3, 28]
    public int nattributes = 0;
    public LIB5Attribute spatialResolution = new LIB5Attribute("spatialResolution","Lowest");

    public Measurement(){
        this.parameter = "N/A";
        this.nattributes = 0;
    }

    public Measurement(String param, EOAttribute[] attribs){
        this.parameter = param;
        this.attributes = attribs;
        this.nattributes = this.attributes.length;

        for(int i = 0;i< this.nattributes;i++){
            // fill out assigned_attributes
            String charac = attribs[i].characteristic;
            Integer ind = (Integer) GlobalVariables.measurementAttributeList.get(charac);
            int index = ind.intValue();
            this.assigned_attributes[i] = index;
        }
    }
    public Measurement(String[] attribs){
        this.parameter = attribs[0];// always assigned
        // Find assigned attributes other than parameter
        int n = 0;
        for(int i = 1;i<attribs.length;i++){
            if(attribs[i].compareTo("N/A") != 0){
                this.assigned_attributes[n] = i;
                String charact = GlobalVariables.measurementAttributeKeys.get(new Integer(i)).toString();
                String typ = GlobalVariables.measurementAttributeTypes.get(charact).toString();
                System.out.println("Attrib " + i + "of type " + typ + " and value " + attribs[i] + " assigned.");

                // Create Atribute of the right type
                EOAttribute tmp = AttributeBuilder.makeMeasurementAttribute(charact,attribs[i]);
                this.attributes[n] = tmp.cloneAttribute(tmp);
        
                n = n + 1;
            }
            else{
               //System.out.println("No");
            }
        }
        this.nattributes = n;
     }
    public String getParameter(){
        return this.parameter;
    }
    public LIB5Attribute getSpatialResolution(){
        return this.spatialResolution;
    }
    public void addAttribute(String charact, EOAttribute attrib){
        this.attribute_list.put(charact, attrib);
    }

    public EOAttribute getAttribute(String charact){
        return (EOAttribute) this.attribute_list.get(charact);
    }

    public String getAttributeValue(String charact){
        EOAttribute att  = (EOAttribute) this.attribute_list.get(charact);
        return att.value;
    }

    public EOAttribute GetAttribute(String charact){
        EOAttribute att = new EOAttribute();
        
        String typ = GlobalVariables.measurementAttributeTypes.get(charact).toString();

        Integer ind = (Integer) GlobalVariables.measurementAttributeList.get(charact);
        int index = ind.intValue();
        int position = -1;
        for(int i = 0;i<this.assigned_attributes.length;i++){
            if(this.assigned_attributes[i] == index){
                position = i;
                break;
            }
        }
        
        if(position>=0){
            //att = AttributeBuilder.make(typ,charact,this.attributes[position].value);
            EOAttribute tmp = AttributeBuilder.makeMeasurementAttribute(charact,attributes[position].value);
            att = tmp.cloneAttribute(tmp);
        }
        else {
            //return error attribute
            att.characteristic = "N/A";
            att.value = "N/A";
            att.type = "N/A";
        }
        return att;
    }
    public void SetAttribute(String charact,String value){
        String typ = GlobalVariables.measurementAttributeTypes.get(charact).toString();

        Integer ind = (Integer) GlobalVariables.measurementAttributeList.get(charact);
        int index = ind.intValue();
        int position = -1;
        for(int i = 0;i<this.assigned_attributes.length;i++){
            if(this.assigned_attributes[i] == index){
                position = i;
                break;
            }
        }
        if(position>=0){//Attribute already exists
            System.out.println("Already exists");
            this.attributes[position].value = value;
        }
        else {//Need to add attribute
            System.out.println("Does not Already exist");
            //this.attributes[++this.nattributes] = AttributeBuilder.make(typ,charact,value);
            EOAttribute tmp = AttributeBuilder.makeMeasurementAttribute(charact,value);
            this.attributes[++this.nattributes] = tmp.cloneAttribute(tmp);

        }
    }

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener p){
        pcs.addPropertyChangeListener(p);
    }
    public void removePropertyChangeListener(PropertyChangeListener p){
        pcs.removePropertyChangeListener(p);
    }
    public int SameOrBetter(Measurement other) {
         // Use attribute SameORBetter
        int[] zi = new int[this.nattributes];
        int z = 0;
        for(int i = 0; i < this.nattributes; i++){
            EOAttribute this_att = this.attributes[i];
            EOAttribute other_att = other.GetAttribute(this_att.characteristic);
            if(other_att.type.compareTo("N/A") == 0){
                // The other measurement does not have this attribute defined
                z = -2;// Missing information
                break;
            }
            else{
                zi[i] = this_att.SameOrBetter(other_att);
                if(zi[i] == -1){
                    z = -1;
                }
            }

        }
        if(z > -1){
            int sum = 0;
            for (int i =0;i<zi.length; i++){
                sum+=zi[i];
            }
            System.out.println(sum + " attributes out of " + zi.length + " are better, the rest are equal");
            if(sum > 0){
                z = 1;
            }
            else{
                z = 0;
            }
        }
        return z;
     }
}

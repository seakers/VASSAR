package rbsa.eoss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dani
 */
import jess.*;
import java.io.Serializable;

public class Improve implements Userfunction , Serializable {
    private static final long serialVersionUID = 1L;
    public String getName() { return "Improve"; }
    public Value call(ValueVector vv, Context c) throws JessException {
        Value v1 = vv.get(1).resolveValue(c);
        Value v2 = vv.get(2).resolveValue(c);

        String attribute = v1.toString();
        String value = v2.stringValue(c);
        //System.out.println(attribute);
        //System.out.println(value);
        if(value.equalsIgnoreCase("nil")){
            return new Value(-1,RU.INTEGER);
        }

        EOAttribute tmp = (EOAttribute) GlobalVariables.measurementAttributeSet.get(attribute);
        tmp.value = value;
        //System.out.println("Line 33!");
        EOAttribute att_value = tmp.cloneAttribute(tmp);
        tmp.value = (String) value;
        
        String result = att_value.Improve();
        //System.out.println(result);


        //System.out.println(result);
        Value rv =  new Value(result,RU.STRING);
        return rv;
    }
}


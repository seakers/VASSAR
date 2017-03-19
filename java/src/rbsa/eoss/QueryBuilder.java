/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

/**
 *
 * @author Marc
 */
import jess.*;
import java.util.ArrayList;
import java.util.HashMap;
public class QueryBuilder {
    
    private Rete r;
    private HashMap<String,HashMap> precomputed_queries;
    public QueryBuilder( Rete r ) 
    {
       this.r = r;
       precomputed_queries = new HashMap<String,HashMap>();
    }

    public HashMap getPrecomputed_query(String key) {
        return precomputed_queries.get(key);
    }

    public void addPrecomputed_query(String key, HashMap hm) {
        this.precomputed_queries.put(key, hm);
    }
    
    public ArrayList<Fact> makeQuery( String template )
    {
        ArrayList<Fact> facts = new ArrayList<Fact>();
        
        String call = "(defquery temp-query ?f <- (" + template + "))";
        
        try {
            r.eval( call );
            QueryResult q_result = r.runQueryStar( "temp-query", new ValueVector() );
            
            while( q_result.next() )
                facts.add( (Fact) q_result.getObject("f") );
            
            r.removeDefrule( "temp-query" );
            
        } catch (Exception e) {
            System.out.println( e.getMessage() );
        }
        
        return facts;
    }
    public void printData( String template, String slot )
    {
        ArrayList<Fact> facts = makeQuery( template );
        
        for( int i = 0; i < facts.size(); i++ )
        {
            try{
                System.out.println( facts.get(i).getFactId() + " : " + 
                                    facts.get(i).getSlotValue(slot).stringValue(r.getGlobalContext()));
            } catch (Exception e) {
                System.out.println( e.getMessage() );
            }       
        }
    }
    
}

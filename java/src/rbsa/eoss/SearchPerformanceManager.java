/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

/**
 *
 * @author Ana-Dani
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import rbsa.eoss.local.Params;

public class SearchPerformanceManager {
    private static SearchPerformanceManager instance;
    private HashMap<String,SearchPerformanceComparator> results;
    
    private SearchPerformanceManager()
    {
        results = new HashMap<String,SearchPerformanceComparator>();
    }
    
    public static SearchPerformanceManager getInstance()
    {
        if( instance == null ) 
        {
            instance = new SearchPerformanceManager();
        }
        return instance;
    }
    
    public void saveSearchPerformanceComparator( SearchPerformanceComparator c )
    {
        results.put( c.getName() + "_" + c.getStamp(), c );
        
        try {
            FileOutputStream file = new FileOutputStream( c.getFile_path() );
            ObjectOutputStream os = new ObjectOutputStream( file );
            os.writeObject( c );
            os.close();
            file.close();
        } catch (Exception e) {
            System.out.println( e.getMessage() );
        }
    }
    public void saveSearchPerformance( SearchPerformance sp)
    {        
        try {
            String name = "sp";
            SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd--HH-mm-ss" );
            String stamp = dateFormat.format( new Date() );
            String file_path = Params.path_save_results + "\\" + name + "_" + stamp + ".rs";
            FileOutputStream file = new FileOutputStream( file_path );
            ObjectOutputStream os = new ObjectOutputStream( file );
            os.writeObject( sp );
            os.close();
            file.close();
        } catch (Exception e) {
            System.out.println( e.getMessage() );
        }
    }
    public SearchPerformanceComparator loadSearchPerformanceComparator( String name, String stamp)
    {
        SearchPerformanceComparator res = results.get( name + "_" + stamp );
        if( res != null )
            return res;
        
        if( Params.path_save_results == null )
        {
            System.out.println( "The params class needs to be initialized" );
            return null;
        }
        System.out.println("hello");
        try {
            String filePath = Params.path_save_results + "\\" + name + "_" + stamp + ".rs";
            FileInputStream file = new FileInputStream( filePath );
            ObjectInputStream is = new ObjectInputStream( file );
            res = (SearchPerformanceComparator)is.readObject();
            is.close();
            file.close();
            results.put( res.getName() + "_" + res.getStamp(), res );
            return res;
        } catch (Exception e) {
            System.out.println( "The SearchPerformanceComparator is not found" );
            System.out.println( e.getMessage() );
            return null;
        }
    }
    public SearchPerformanceComparator loadSearchPerformanceComparatorFromFile( String filePath)
    {
        SearchPerformanceComparator res;
//        if( Params.path_save_results == null )
//        {
//            System.out.println( "The params class needs to be initialized" );
//            return null;
//        }
        
        try {
            FileInputStream file = new FileInputStream( filePath );
            ObjectInputStream is = new ObjectInputStream( file );
            res = (SearchPerformanceComparator)is.readObject();
            is.close();
            file.close();
            //results.put( res.getName() + "_" + res.getStamp(), res );
            return res;
        } catch (Exception e) {
            System.out.println( "The SearchPerformanceComparator is not found" );
            System.out.println( e.getMessage() );
            return null;
        }
    }
    public SearchPerformance loadSearchPerformanceFromFile( String filePath)
    {
        SearchPerformance res;
        if( Params.path_save_results == null )
        {
            System.out.println( "The params class needs to be initialized" );
            return null;
        }
        
        try {
            FileInputStream file = new FileInputStream( filePath );
            ObjectInputStream is = new ObjectInputStream( file );
            res = (SearchPerformance)is.readObject();
            is.close();
            file.close();
            //results.put( res.getName() + "_" + res.getStamp(), res );
            return res;
        } catch (Exception e) {
            System.out.println( "The SearchPerformance is not found" );
            System.out.println( e.getMessage() );
            return null;
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rbsa.eoss;

/**
 *
 * @author Marc
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.util.HashMap;
import rbsa.eoss.local.Params;

public class ResultManager {
    
    private static ResultManager instance;

    private Params params;
    private HashMap<String,ResultCollection> results;
    
    private ResultManager()
    {
        params = Params.getInstance();
        results = new HashMap<>();
    }
    
    public static ResultManager getInstance()
    {
        if( instance == null ) 
        {
            instance = new ResultManager();
        }
        return instance;
    }
    
    public void saveResultCollection( ResultCollection c )
    {
        results.put( c.getName(), c );
        
        try {
            FileOutputStream file = new FileOutputStream( c.getFilePath() );
            ObjectOutputStream os = new ObjectOutputStream( file );
            os.writeObject( c );
            os.close();
            file.close();
        } catch (Exception e) {
            System.out.println( e.getMessage() );
        }
    }
    
    public ResultCollection loadResultCollection( String stamp )
    {
        ResultCollection res = results.get( stamp );
        if( res != null )
            return res;
        
        if( params.path_save_results == null )
        {
            System.out.println( "The params class needs to be initialized" );
            return null;
        }
        
        try {
            String filePath = params.path_save_results + "\\" + stamp;
            FileInputStream file = new FileInputStream( filePath );
            ObjectInputStream is = new ObjectInputStream( file );
            res = (ResultCollection)is.readObject();
            is.close();
            file.close();
            results.put( res.getStamp(), res );
            return res;
        } catch (Exception e) {
            System.out.println( "The result collection is not found" );
            System.out.println( e.getMessage() );
            return null;
        }
    }
    
    public ResultCollection loadResultCollectionFromFile( String filePath )
    {
        ResultCollection res;
        
        try {
            FileInputStream file = new FileInputStream( filePath );
            ObjectInputStream is = new ObjectInputStream( file );
            res = (ResultCollection)is.readObject();
            is.close();
            file.close();
            results.put( res.getStamp(), res );
            return res;
        } catch (Exception e) {
            System.out.println( "The result collection is not found" );
            System.out.println( e.getMessage() );
            return null;
        }
    }
    
    public void loadAllCollections( String path )
    {
        if( params.path_save_results == null )
        {
            System.out.println( "The params class needs to be initialized" );
            return;
        }
        
        clear();
        
        path = params.path + "\\" + path;
        
        ResultCollection res;
        File folder = new File( path );
        File[] fileList = folder.listFiles();
        
        for( int i = 0; i < fileList.length; i++ )
            if( fileList[i].isFile() && fileList[i].getName().endsWith( ".rs" ) )
            {
                try {
                    res = loadResultCollection( fileList[i].getName() );
                    results.put( res.getStamp(), res );
                } catch (Exception e) {
                    System.out.println( "The file " + fileList[i].getName() + " could not be opened" );
                    System.out.println( e.getMessage() );
                }
            }
    }
    
    public HashMap<String,ResultCollection> findResultsByInputFile( String fileName )
    {  
        HashMap<String,ResultCollection> tmp = new HashMap<String,ResultCollection>();
        String[] keys = (String[])results.keySet().toArray();
        
        for( int i = 0; i < keys.length; i++ )
            if( results.get(keys[i]).getName().contains( fileName ) )
                tmp.put(keys[i], results.get(keys[i]));
        
        if( tmp.isEmpty() )
        {
            System.out.println( "No result collection has been found matching " + fileName );
            return null;
        }
        
        return tmp;
    }
    
    public String[] getKeys()
    {
        Object[] tmp = results.keySet().toArray();
        String[] keys = new String[ tmp.length ];
        for( int i = 0; i < keys.length; i++ )
            keys[i] = (String)tmp[i];
        
        return keys;
    }
    
    public HashMap<String,ResultCollection> getResultCollections()
    {
        return results;
    }
    
    public void clear()
    {
        results.clear();
    }
    
    public ResultCollection getResultCollection( String key )
    {
        return results.get( key );
    }
    
    public boolean isEmpty()
    {
        return results.isEmpty();
    }
}


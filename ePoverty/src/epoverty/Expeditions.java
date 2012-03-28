/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package epoverty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author bnielson
 */
public class Expeditions
{

    //table columns go here
    public int expeditionId = 0;
    public String name;
    public String expeditionDescription;
    public float cost;
    public float defaultRaiseGoal;
    public Date departDate;
    public Date returnDate;
    public Date cutoffDate;
    private PreparedStatement ps; // To make the inserts and updates easier

    //Loads person data from the database using the personId number
    public void LoadExpeditions( int ID )
    {
        String query = "SELECT * FROM expeditions WHERE expeditionId=" + ID;
        ResultSet rs = MySQLInterface.ExecuteQuery( query );

        try {
            rs.next();
            expeditionId = rs.getInt( "expeditionId" );
            name = rs.getString( "name" );
            expeditionDescription = rs.getString( "expeditionDescription" );
            cost = rs.getFloat( "cost" );
            defaultRaiseGoal = rs.getFloat( "defaultRaiseGoal" );
            departDate = rs.getDate( "departDate" );
            returnDate = rs.getDate( "returnDate" );
            cutoffDate = rs.getDate( "cutoffDate" );

        }
        catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
        }

    }

    public String ToString()
    {
        return name;
    }

    public void SaveExpeditions()
    {
        if ( expeditionId == 0 ) //new  
        {
            try {
                String sql = "insert into expeditions (name, expeditionDescription, cost, defaultRaiseGoal, departDate, returnDate, cutoffDate)"
                        + " values (?,?,?,?,?,?,?)";
                ps = MySQLInterface.dbConn.prepareStatement( sql );//prepared statements use variable places defined by ?s, indexed starting at 1

                ps.setString( 1, name );
                ps.setString( 2, expeditionDescription );
                ps.setFloat( 3, cost );
                ps.setFloat( 4, defaultRaiseGoal );
                ps.setDate( 5, departDate );
                ps.setDate( 6, returnDate );
                ps.setDate( 7, cutoffDate );

                if ( !ps.execute() ) {
                    System.out.println( "Error while adding new expeditions" );
                }
            }
            catch ( SQLException ex ) {
                ex.printStackTrace();
            }

        } else //update
        {
            try {
                String sql = "UPDATE expeditions SET name=?,expeditionDescription=?,cost=?,defaultRaiseGoal=?,departDate=?,returnDate=?,cutoffDate=?"
                        + " WHERE expeditionId='?'";
                ps = MySQLInterface.dbConn.prepareStatement( sql );//prepared statements use variable places defined by ?s, indexed starting at 1

                ps.setString( 1, name );
                ps.setString( 2, expeditionDescription );
                ps.setFloat( 3, cost );
                ps.setFloat( 4, defaultRaiseGoal );
                ps.setDate( 5, departDate );
                ps.setDate( 6, returnDate );
                ps.setDate( 7, cutoffDate );
                ps.setInt( 8, expeditionId );

                if ( !ps.execute() ) {
                    System.out.println( "Error while updating expeditions with id: " + expeditionId );
                }
            }
            catch ( SQLException ex ) {
                System.out.println( "Error while updating or inserting new expeditions: " + ex.getMessage() );
                ex.printStackTrace();
            }

        }

        if ( expeditionId == 0 ) {
            ResultSet rs = MySQLInterface.ExecuteQuery( "SELECT LAST_INSERT_ID() LID;" );
            try {
                rs.next();
                expeditionId = rs.getInt( "LID" );
            }
            catch ( Exception ex ) {
            }
        }
    }

    //Static method useful for retrieving all the data in the table and creating an object for each
    // It then returns an array of objects
    public static Expeditions[] getExpeditions()
    {
        ArrayList<Expeditions> expeditions = new ArrayList<>();
        try {
            String query = "select * from expeditions;";
            ResultSet rs;
            rs = MySQLInterface.dbConn.createStatement().executeQuery( query );

            while ( rs.next() ) {
                Expeditions temp = new Expeditions();
                temp.expeditionId = rs.getInt( "expeditionId" );
                temp.name = rs.getString( "name" );

                temp.expeditionDescription = rs.getString( "expeditionDescription" );
                temp.cost = rs.getFloat( "cost" );
                temp.defaultRaiseGoal = rs.getFloat( "defaultRaiseGoal" );
                temp.departDate = rs.getDate( "departDate" );
                temp.returnDate = rs.getDate( "returnDate" );
                temp.cutoffDate = rs.getDate( "cutoffDate" );

                //add the object into the ArrayList for later use
                expeditions.add( temp );

            }

        }
        catch ( SQLException ex ) {
            System.out.println( "Error while retrieve all the Expeditions data from the table: " + ex.getMessage() );
        }

        //return our ArrayList as an array
        Expeditions[] objects = expeditions.toArray( new Expeditions[ expeditions.size() ] ); //convert the arraylist to an array of objects
        return objects;
    }

    public Object[] jTree()
    {
        SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat( "yyyy-MM-dd" );
        StringBuilder cdate = new StringBuilder( dateformatYYYYMMDD.format( cutoffDate ) );
        StringBuilder ddate = new StringBuilder( dateformatYYYYMMDD.format( departDate ) );

        return new Object[]{ expeditionId, name, cost, defaultRaiseGoal, cdate, ddate };
    }

}

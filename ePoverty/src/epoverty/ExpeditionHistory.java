/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package epoverty;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author bnielson
 */
public class ExpeditionHistory
{

    //table columns go here
    public int expedtionId =0;
    public int fundraiserId =0;
    
    //classes for foreign keys
    Expeditions expedition = new Expeditions();
    Fundraisers fundraiser = new Fundraisers();

    private PreparedStatement ps; // To make the inserts and updates easier    
    

    //Loads person data from the database using the personId number
    public void LoadExpeditionHistory(int ID)
    {
        String query = "SELECT * FROM expeditionHistory WHERE expedtionId=" + ID;
        ResultSet rs = MySQLInterface.ExecuteQuery(query);

        try
        {
            rs.next();
            expedtionId = rs.getInt("expedtionId");            
            
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    public String ToString()
    {
        return "";
    }

    public void SaveExpeditionHistory()
    {
        if (expedtionId == 0) //new  
        {
            try
            {
                String sql = "insert into expeditionHistory ()"
                        + " values ()";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1

                //ps vars
                //ps.set? = ?

                if (!ps.execute())
                {
                    System.out.println("Error while adding new expeditionHistory");
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

        }
        else //update
        {
            try
            {
                String sql = "UPDATE expeditionHistory SET "                        
                        + "WHERE expedtionId='?'";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1
                
                ps.setInt(1, expedtionId);

                if (!ps.execute())
                {
                    System.out.println("Error while updating expeditionHistory with id: " + expedtionId);
                }
            }
            catch (SQLException ex)
            {
                System.out.println("Error while updating or inserting new expeditionHistory: " + ex.getMessage());
                ex.printStackTrace();
            }

        }

        if (expedtionId == 0)
        {
            ResultSet rs = MySQLInterface.ExecuteQuery("SELECT LAST_INSERT_ID() LID;");
            try
            {
                rs.next();
                expedtionId = rs.getInt("LID");
            }
            catch (Exception ex)
            {
            }
        }
    }

    //Static method useful for retrieving all the data in the table and creating an object for each
    // It then returns an array of objects
    public static ExpeditionHistory[] getExpeditionHistory()
    {
        ArrayList<ExpeditionHistory> expeditionHistory = new ArrayList<>();
        try
        {
            String query = "select expedtionId from expeditionHistory;";
            ResultSet rs;
            rs = MySQLInterface.dbConn.createStatement().executeQuery(query);
            
            while (rs.next())
            {
                ExpeditionHistory temp = new ExpeditionHistory();
                temp.expedtionId=rs.getInt("expedtionId");
                
                //add the object into the ArrayList for later use
                expeditionHistory.add(temp);

            }

        }
        catch (SQLException ex)
        {
            System.out.println("Error while retrieve all the ExpeditionHistory data from the table: " + ex.getMessage());
        }

        //return our ArrayList as an array
        ExpeditionHistory[] objects = expeditionHistory.toArray(new ExpeditionHistory[expeditionHistory.size()]); //convert the arraylist to an array of objects
        return objects;
    }
}


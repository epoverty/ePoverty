package epoverty;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Brent Nielson
 */
public class Admins
{

    //table columns go here
    public int adminId =0;
    public int personId=0;
    
    //related
    public Person person;

    private PreparedStatement ps; // To make the inserts and updates easier

    //Loads person data from the database using the personId number
    public void LoadAdmins(int ID)
    {
        String query = "SELECT * FROM admins WHERE adminId=" + ID;
        ResultSet rs = MySQLInterface.ExecuteQuery(query);

        try
        {
            rs.next();
            adminId = rs.getInt("adminId");            
            personId=rs.getInt("personId");
            
            person = new Person();
            person.LoadPerson(personId);
            
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    public String ToString()
    {
        return person.toString();
    }

    public void SaveAdmins()
    {
        if (adminId == 0) //new  
        {
            try
            {
                String sql = "insert into admins (personId)"
                        + " values (?)";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1

                //ps vars
                ps.setInt(1, personId);

                if (!ps.execute())
                {
                    System.out.println("Error while adding new admins");
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
                String sql = "UPDATE admins SET personId=?"                        
                        + "WHERE adminId='?'";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1
                
                ps.setInt(1, personId);
                ps.setInt(2, adminId);

                if (!ps.execute())
                {
                    System.out.println("Error while updating admins with id: " + adminId);
                }
            }
            catch (SQLException ex)
            {
                System.out.println("Error while updating or inserting new admins: " + ex.getMessage());
                ex.printStackTrace();
            }

        }

        if (adminId == 0)
        {
            ResultSet rs = MySQLInterface.ExecuteQuery("SELECT LAST_INSERT_ID() LID;");
            try
            {
                rs.next();
                adminId = rs.getInt("LID");
            }
            catch (Exception ex)
            {
            }
        }
    }

    //Static method useful for retrieving all the data in the table and creating an object for each
    // It then returns an array of objects
    public static Admins[] getAdmins()
    {
        ArrayList<Admins> admins = new ArrayList<>();
        try
        {
            String query = "select adminId from admins;";
            ResultSet rs;
            rs = MySQLInterface.dbConn.createStatement().executeQuery(query);
            
            while (rs.next())
            {
                Admins temp = new Admins();
                temp.adminId=rs.getInt("adminId");
                temp.personId=rs.getInt("personId");
                
                temp.person=new Person();
                temp.person.LoadPerson(temp.personId);
                
                //add the object into the ArrayList for later use
                admins.add(temp);

            }

        }
        catch (SQLException ex)
        {
            System.out.println("Error while retrieve all the Admins data from the table: " + ex.getMessage());
        }

        //return our ArrayList as an array
        Admins[] objects = admins.toArray(new Admins[admins.size()]); //convert the arraylist to an array of objects
        return objects;
    }
}

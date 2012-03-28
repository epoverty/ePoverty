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
public class Donor
{
    //table columns go here
    public int donorId = 0;
    public int personId = 0;
    public Person person = new Person();
    private PreparedStatement ps; // To make the inserts and updates easier

    //Loads person data from the database using the personId number
    public void LoadDonor(int ID)
    {
        String query = "SELECT * FROM donors WHERE donorId=" + ID;
        ResultSet rs = MySQLInterface.ExecuteQuery(query);

        try
        {
            rs.next();
            donorId = rs.getInt("donorId");
            personId = rs.getInt("personId");

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
        return person.ToString();
    }

    public void SaveDonor()
    {
        if (donorId == 0) //new  
        {
            try
            {
                String sql = "insert into donors (personId)"
                        + " values (?)";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1

                //ps vars
                ps.setInt(1, personId);

                if (!MySQLInterface.ExecutePreparedStatement(ps))
                {
                    System.out.println("Error while adding new donors");
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
                String sql = "UPDATE donors SET personId=?"
                        + "WHERE donorId='?'";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1

                ps.setInt(1, personId);
                ps.setInt(2, donorId);

                if (!MySQLInterface.ExecutePreparedStatement(ps))
                {
                    System.out.println("Error while updating donors with id: " + donorId);
                }
            }
            catch (SQLException ex)
            {
                System.out.println("Error while updating or inserting new donors: " + ex.getMessage());
                ex.printStackTrace();
            }

        }

        if (donorId == 0)
        {
            ResultSet rs = MySQLInterface.ExecuteQuery("SELECT LAST_INSERT_ID() LID;");
            try
            {
                rs.next();
                donorId = rs.getInt("LID");
            }
            catch (Exception ex)
            {
            }
        }
    }

    //Static method useful for retrieving all the data in the table and creating an object for each
    // It then returns an array of objects
    public static Donor[] getDonor()
    {
        ArrayList<Donor> donors = new ArrayList<>();
        try
        {
            String query = "select * from donors;";
            ResultSet rs;
            rs = MySQLInterface.dbConn.createStatement().executeQuery(query);

            while (rs.next())
            {
                Donor temp = new Donor();
                temp.donorId = rs.getInt("donorId");
                temp.personId = rs.getInt("personId");
                temp.person = new Person();
                temp.person.LoadPerson(rs.getInt("personId"));

                //add the object into the ArrayList for later use
                donors.add(temp);
            }

        }
        catch (SQLException ex)
        {
            System.out.println("Error while retrieve all the Donor data from the table: " + ex.getMessage());
        }

        //return our ArrayList as an array
        Donor[] objects = donors.toArray(new Donor[donors.size()]); //convert the arraylist to an array of objects
        return objects;
    }
}

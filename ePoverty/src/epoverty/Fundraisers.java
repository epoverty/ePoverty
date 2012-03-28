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
public class Fundraisers
{

    public int fundraiserId = 0;
    public int personId = 0;
    public int expeditionId = 0;
    public float raiseGoal = 0;
    public float raised = 0;
    private PreparedStatement ps; // To make the inserts and updates easier
    public Person person;

    //Loads person data from the database using the personId number
    public void LoadFundraiser(int fID)
    {
        String query = "SELECT * FROM fundraisers WHERE fundraiserId=" + fID;
        ResultSet rs = MySQLInterface.ExecuteQuery(query);

        try
        {
            rs.next();
            fundraiserId = rs.getInt("fundraiserId");
            personId = rs.getInt("personId");
            expeditionId = rs.getInt("expeditionId");
            raiseGoal = rs.getFloat("raiseGoal");
            raised = rs.getFloat("raiseGoal");
            person = new Person();
            person.LoadPerson(personId);

        } catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    public String ToString()
    {
        return person.ToString();
    }

    public void SaveFundraiser()
    {
        if (fundraiserId == 0) //new fundraiser 
        {
            try
            {
                String sql = "insert into fundraisers (personID,expeditionId,raiseGoal,raised)"
                        + " values (?, ?, ?, ?)";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1

                ps.setInt(1, personId);
                ps.setInt(2, expeditionId);
                ps.setFloat(3, raiseGoal);
                ps.setFloat(4, raised);

                MySQLInterface.ExecutePreparedStatement(ps);
            } catch (SQLException ex)
            {
                ex.printStackTrace();
            }

        } else //update a fundraiser
        {
            try
            {
                String sql = "UPDATE fundraisers SET personID='?', expeditionId='?', raiseGoal='?', raised='?'"
                        + " WHERE fundraiserId='?'";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1

                ps.setInt(1, personId);
                ps.setInt(2, expeditionId);
                ps.setFloat(3, raiseGoal);
                ps.setFloat(4, raised);
                ps.setInt(5, fundraiserId);

                MySQLInterface.ExecutePreparedStatement(ps);
            } catch (SQLException ex)
            {
                System.out.println("Error while updating or inserting new fundraiser: " + ex.getMessage());
                ex.printStackTrace();
            }

        }

        if (fundraiserId == 0)
        {
            ResultSet rs = MySQLInterface.ExecuteQuery("SELECT LAST_INSERT_ID() LID;");
            try
            {
                rs.next();
                fundraiserId = rs.getInt("LID");
            } catch (Exception ex)
            {
            }
        }
    }

    //Static method useful for retrieving all the data in the table and creating an object for each
    // It then returns an array of objects
    public static Fundraisers[] getFundraisers()
    {
        ArrayList<Fundraisers> fundraisers = new ArrayList<>();
        try
        {
            String query = "select fundraiserId,personId from persons;";
            ResultSet rs;
            rs = MySQLInterface.ExecuteQuery(query);

            while (rs.next())
            {
                Fundraisers temp = new Fundraisers();
                temp.fundraiserId = rs.getInt("fundraiserId");
                temp.personId = rs.getInt("personId");
                temp.person = new Person();
                temp.person.LoadPerson(temp.personId);

                //add the Person object into the ArrayList for later use
                fundraisers.add(temp);

            }

        } catch (SQLException ex)
        {
            System.out.println("Error while retrieve all the people from the Persons table: " + ex.getMessage());
        }

        //return our ArrayList as an array
        Fundraisers[] objects = fundraisers.toArray(new Fundraisers[fundraisers.size()]); //convert the arraylist to an array of objects
        return objects;
    }
}

package epoverty;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;

/**
 *
 * @author Brent Nielson
 */
public class Donations
{

    //table columns go here
    public int donationId =0;
    public int donorId=0;
    public int fundraiserId=0;
    public Date donationDate;
    public float donationAmount=0;
    
    //linked classes
    Donor donor;
    Fundraisers fundraiser;

    private PreparedStatement ps; // To make the inserts and updates easier    

    //Loads person data from the database using the personId number
    public void LoadDonations(int ID)
    {
        String query = "SELECT * FROM donations WHERE donationId=" + ID;
        ResultSet rs = MySQLInterface.ExecuteQuery(query);

        try
        {
            rs.next();
            donationId = rs.getInt("donationId");
            donorId=rs.getInt("donorId");
            fundraiserId=rs.getInt("fundraiserId");
            donationDate=rs.getDate("donationDate");
            donationAmount=rs.getFloat("donationAmount");
            
            //
            donor = new Donor();
            donor.LoadDonor(donorId);
            fundraiser = new Fundraisers();
            fundraiser.LoadFundraiser(fundraiserId);
            
            
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

    public void SaveDonations()
    {
        if (donationId == 0) //new  
        {
            try
            {
                String sql = "insert into donations ()"
                        + " values ()";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1

                //ps vars
                //ps.set? = ?

                if (!MySQLInterface.ExecutePreparedStatement(ps))
                {
                    System.out.println("Error while adding new donations");
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
                String sql = "UPDATE donations SET "                        
                        + "WHERE donationId='?'";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1
                
                ps.setInt(1, donationId);

                if (!MySQLInterface.ExecutePreparedStatement(ps))
                {
                    System.out.println("Error while updating donations with id: " + donationId);
                }
            }
            catch (SQLException ex)
            {
                System.out.println("Error while updating or inserting new donations: " + ex.getMessage());
                ex.printStackTrace();
            }

        }

        if (donationId == 0)
        {
            ResultSet rs = MySQLInterface.ExecuteQuery("SELECT LAST_INSERT_ID() LID;");
            try
            {
                rs.next();
                donationId = rs.getInt("LID");
            }
            catch (Exception ex)
            {
            }
        }
    }

    //Static method useful for retrieving all the data in the table and creating an object for each
    // It then returns an array of objects
    public static Donations[] getDonations()
    {
        ArrayList<Donations> donations = new ArrayList<>();
        try
        {
            String query = "select donationId from donations;";
            ResultSet rs;
            rs = MySQLInterface.dbConn.createStatement().executeQuery(query);
            
            while (rs.next())
            {
                Donations temp = new Donations();
                temp.donationId=rs.getInt("donationId");
                
                //add the object into the ArrayList for later use
                donations.add(temp);

            }

        }
        catch (SQLException ex)
        {
            System.out.println("Error while retrieve all the Donations data from the table: " + ex.getMessage());
        }

        //return our ArrayList as an array
        Donations[] objects = donations.toArray(new Donations[donations.size()]); //convert the arraylist to an array of objects
        return objects;
    }
}

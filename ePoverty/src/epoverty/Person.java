package epoverty;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author Brent Nielson
 */
public class Person
{

    public int personID = 0;
    public String firstName = "";
    public String middleName = "";
    public String lastName = "";
    public String phoneNumber = "";
    public String emailAddress = "";
    public String addressStreet = "";
    public String addressCity = "";
    public String addressState = "";
    public String addressZip = "";
    public String password = "";
    public BufferedImage photo = null;
    //related vars
    public int donorId = 0;
    public int fundraiserId = 0;
    public int adminId = 0;
    private PreparedStatement ps; // To make the inserts and updates easier

    //Loads person data from the database using the personId number
    public void LoadPerson(int pID)
    {
        String query = " SELECT P.*,COALESCE(A.adminId,0) adminId,COALESCE(F.fundraiserId,0) fundraiserId"
                + ",COALESCE(D.donorId,0) donorId FROM `persons` P left join admins A using (personId)"
                + " left join fundraisers F using (personId) left join donors D using (personId) WHERE personId=" + pID;
        ResultSet rs = MySQLInterface.ExecuteQuery(query);

        try
        {
            rs.next();
            personID = rs.getInt("personId");
            firstName = rs.getString("firstName");
            middleName = rs.getString("middleName");
            lastName = rs.getString("lastName");
            phoneNumber = rs.getString("phoneNumber");
            emailAddress = rs.getString("emailAddress");
            addressStreet = rs.getString("addressStreet");
            addressCity = rs.getString("addressCity");
            addressState = rs.getString("addressState");
            addressZip = rs.getString("addressZip");
            password = rs.getString("password");

            byte[] photoBytes = rs.getBytes("photo");//gets the person's photo as an array of bytes
            if (photoBytes != null)//check to see if there is a photo on file for this person
            {
                ByteArrayInputStream bis = new ByteArrayInputStream(photoBytes);//create a ByteArrayInputStream from our array of bytes
                photo = ImageIO.read(bis);//use Java's native ImageIO class, and static read method to read from our bytes, and create a BufferedImage

            }
            else
            {
                photo = null;
            }

            //related vars
            adminId = rs.getInt("adminId");
            donorId = rs.getInt("donorId");
            fundraiserId = rs.getInt("fundraiserId");
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    public void LoadPersonWhere(String[] fields, String[] values) throws Exception
    {

        //create query
        String query = "Select * FROM persons WHERE ";
        if (fields.length != values.length)
        {
            throw new Exception("fields doesn't match values");
        }

        for (int x = 0; x < fields.length; x++)
        {

            query += fields[x] + "='" + values[x] + "'";

            if (x + 1 != fields.length)
            {
                query += " AND ";
            }

        }
        System.out.println(query);
        try
        {
            ResultSet rs = MySQLInterface.ExecuteQuery(query);

            rs.next();
            personID = rs.getInt("personId");
            firstName = rs.getString("firstName");
            middleName = rs.getString("middleName");
            lastName = rs.getString("lastName");
            phoneNumber = rs.getString("phoneNumber");
            emailAddress = rs.getString("emailAddress");
            addressStreet = rs.getString("addressStreet");
            addressCity = rs.getString("addressCity");
            addressState = rs.getString("addressState");
            addressZip = rs.getString("addressZip");
            password = rs.getString("password");

            byte[] photoBytes = rs.getBytes("photo");//gets the person's photo as an array of bytes
            if (photoBytes != null)//check to see if there is a photo on file for this person
            {
                ByteArrayInputStream bis = new ByteArrayInputStream(photoBytes);//create a ByteArrayInputStream from our array of bytes
                photo = ImageIO.read(bis);//use Java's native ImageIO class, and static read method to read from our bytes, and create a BufferedImage

            }
            else
            {
                photo = null;
            }
        }
        catch (SQLException ex)
        {
            System.out.println("blah " + ex.getMessage());
        }

    }

    public String ToString()
    {
        return firstName + " " + middleName + " " + lastName;
    }

    public void SavePerson()
    {
        String query;
        byte[] photoBytes = null; // Used to upload the photo as bytes

        if (photo != null) // There is a photo to update
        {
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(photo, "jpg", baos); // write the photo to the ByteArrayOutputStream
                baos.flush();
                photoBytes = baos.toByteArray(); // Convert it to a byteArray
            }
            catch (IOException ex)
            {
                System.out.println("Error converting photo to a byte array: " + ex.getMessage());
            }
        }

        if (personID == 0) //new person 
        {
            try
            {
                String sql = "insert into persons (firstName,middleName,lastName,phoneNumber,emailAddress,addressStreet,addressCity,addressState,addressZip,password,photo)"
                        + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1

                ps.setString(1, firstName);
                ps.setString(2, middleName);
                ps.setString(3, lastName);
                ps.setString(4, phoneNumber);
                ps.setString(5, emailAddress);
                ps.setString(6, addressStreet);
                ps.setString(7, addressCity);
                ps.setString(8, addressState);
                ps.setString(9, addressZip);
                ps.setString(10, password);
                ps.setBytes(11, photoBytes);

                MySQLInterface.ExecutePreparedStatement(ps);
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }

        }
        else //update a person
        {
            try
            {
                String sql = "UPDATE persons SET firstName= ? , middleName= ? , lastName= ? , phoneNumber= ? , emailAddress= ? ,"
                        + "addressStreet= ? , addressCity= ? , addressState= ? , addressZip= ? , password= ? , photo= ? "
                        + "WHERE personId= ? ";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1

                ps.setString(1, firstName);
                ps.setString(2, middleName);
                ps.setString(3, lastName);
                ps.setString(4, phoneNumber);
                ps.setString(5, emailAddress);
                ps.setString(6, addressStreet);
                ps.setString(7, addressCity);
                ps.setString(8, addressState);
                ps.setString(9, addressZip);
                ps.setString(10, password);
                ps.setBytes(11, photoBytes);
                ps.setInt(12, personID);

                MySQLInterface.ExecutePreparedStatement(ps);
            }
            catch (SQLException ex)
            {
                System.out.println("Error while updating or inserting new person: " + ex.getMessage());
                ex.printStackTrace();
            }

        }

        if (personID == 0)
        {
            ResultSet rs = MySQLInterface.ExecuteQuery("SELECT LAST_INSERT_ID() LID;");
            try
            {
                rs.next();
                personID = rs.getInt("LID");
            }
            catch (Exception ex)
            {
            }
        }
    }

    //Static method useful for retrieving all the people in the Persons table and creating a Person object for each
    // It then returns an array of Person objects
    public static Person[] getPersons()
    {
        ArrayList<Person> persons = new ArrayList<>();
        try
        {
            String query = "select personId,firstName,middleName,lastName,phoneNumber,emailAddress,addressStreet,addressCity,addressState,addressZip,password,photo from persons;";
            ResultSet rs;
            rs = MySQLInterface.ExecuteQuery(query);
            String name;
            while (rs.next())
            {
                Person tempPerson = new Person();
                tempPerson.personID = rs.getInt("personId");
                tempPerson.firstName = rs.getString("firstName");
                tempPerson.middleName = rs.getString("middleName");
                tempPerson.lastName = rs.getString("lastName");
                tempPerson.phoneNumber = rs.getString("phoneNumber");
                tempPerson.emailAddress = rs.getString("emailAddress");
                tempPerson.addressStreet = rs.getString("addressStreet");
                tempPerson.addressCity = rs.getString("addressCity");
                tempPerson.addressState = rs.getString("addressState");
                tempPerson.addressZip = rs.getString("addressZip");
                tempPerson.password = rs.getString("password");

                byte[] photoBytes = rs.getBytes("photo");
                if (photoBytes != null)//check to see if there is a photo on file for this person
                {
                    ByteArrayInputStream bis = new ByteArrayInputStream(photoBytes);//create a ByteArrayInputStream from our array of bytes
                    try
                    {
                        tempPerson.photo = ImageIO.read(bis);//use Java's native ImageIO class, and static read method to read from our bytes, and create a BufferedImage
                    }
                    catch (IOException ex)
                    {
                        System.out.println("Could not read the photo from the database: " + ex.getMessage());
                    }

                }
                else
                {
                    tempPerson.photo = null;
                }

                //add the Person object into the ArrayList for later use
                persons.add(tempPerson);

            }


        }
        catch (SQLException ex)
        {
            System.out.println("Error while retrieve all the people from the Persons table: " + ex.getMessage());
        }

        //return our ArrayList as an array
        Person[] objects = persons.toArray(new Person[persons.size()]); //convert the arraylist to an array of Person objects
        return objects;
    }

    public Object[] jTree()
    {
        return new Object[]
                {
                    firstName, middleName, lastName, emailAddress
                };
    }

    public boolean isAdmin()
    {
        return (adminId != 0);
    }

    public boolean isDonor()
    {
        return (donorId != 0);
    }

    public boolean isFundraiser()
    {
        return (fundraiserId != 0);
    }

    public void SetAsAdmin()
    {
        if (personID == 0)
            return;
        try
        {
            String query = "INSERT IGNORE INTO admins (personId) VALUES (?);";
            ps = MySQLInterface.dbConn.prepareStatement(query);
            ps.setInt(1,personID);
            MySQLInterface.ExecutePreparedStatement(ps);
        }
        catch (SQLException ex)
        {
            System.out.println("Error while setting person as admin: " + ex.getMessage());
            ex.printStackTrace();
        }

    }
    
    public void RemoveAsAdmin()
    {
        try
        {
            String query = "DELETE FROM admins WHERE personId=?;";
            ps = MySQLInterface.dbConn.prepareStatement(query);
            ps.setInt(1,personID);
            MySQLInterface.ExecutePreparedStatement(ps);
        }
        catch (SQLException ex)
        {
            System.out.println("Error while removing person as admin: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void SetAsDonor()
    {
        if (personID == 0)
            return;
        try
        {
            String query = "INSERT IGNORE INTO donors (personId) VALUES (?);";
            ps = MySQLInterface.dbConn.prepareStatement(query);
            ps.setInt(1,personID);
            MySQLInterface.ExecutePreparedStatement(ps);
        }
        catch (SQLException ex)
        {
            System.out.println("Error while setting person as donor: " + ex.getMessage());
            ex.printStackTrace();
        }

    }
    
    public void RemoveAsDonor()
    {
        try
        {
            String query = "DELETE FROM donors WHERE personId=?;";
            ps = MySQLInterface.dbConn.prepareStatement(query);
            ps.setInt(1,personID);
            MySQLInterface.ExecutePreparedStatement(ps);
        }
        catch (SQLException ex)
        {
            System.out.println("Error while removing person as donor: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void SetAsFundraiser()
    {
        if (personID == 0)
            return;
        try
        {
            String query = "INSERT IGNORE INTO fundraisers (personId) VALUES (?);";
            ps = MySQLInterface.dbConn.prepareStatement(query);
            ps.setInt(1,personID);
            MySQLInterface.ExecutePreparedStatement(ps);
        }
        catch (SQLException ex)
        {
            System.out.println("Error while setting person as fundraiser: " + ex.getMessage());
            ex.printStackTrace();
        }

    }
    
    public void RemoveAsFundraiser()
    {
        try
        {
            String query = "DELETE FROM fundraisers WHERE personId=?;";
            ps = MySQLInterface.dbConn.prepareStatement(query);
            ps.setInt(1,personID);
            MySQLInterface.ExecutePreparedStatement(ps);
        }
        catch (SQLException ex)
        {
            System.out.println("Error while removing person as fundraiser: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

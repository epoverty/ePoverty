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
 * @author Mike Larsen
 */
public class Accounts {
    
    //Create Vars for all fields on the table
    
    //Test Commit

    public int accountID = 0;
    public String accountName = "";
    public String description = "";
    public double balance = 0;
    public double autoRedirectPercent = 0.0;
    //Create a Prepared Statement to help with CrossSiteScripting 
    private PreparedStatement ps; // To make the inserts and updates easier

    //Loads person data from the database using the personId number
    public void LoadAccount(int aID) {
        //Query To pull based on AccountID
        String query = "SELECT * FROM accounts WHERE accountId=" + aID;
        ResultSet rs = MySQLInterface.ExecuteQuery(query);
        //Populate data with returns from query
        try {
            rs.next();
            accountID = rs.getInt("accountID");
            accountName = rs.getString("accountName");
            description = rs.getString("description");
            balance = rs.getDouble("balance");
            autoRedirectPercent = rs.getDouble("autoRedirectPercent");
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    
    public void LoadAccountWhere(String[] fields, String[] values) throws Exception {

        //create select * query for accounts table, using a where clause
        String query = "Select * FROM accounts WHERE ";
         if (fields.length != values.length)
             throw new Exception("fields doesn't match values");
         //Iterate through the where clauses 
        for (int x=0; x<fields.length; x++)
        {
            
                query += fields[x] + "='" + values[x] + "'";
                //Add an "And" clause for each each where item
                if (x+1 != fields.length)
                    query += " AND ";
            
        }
        //Output the query to console, great for debugging
        System.out.println(query);
        //Try to execute the query
        try {
            ResultSet rs = MySQLInterface.ExecuteQuery(query);
            //assign the returned values to local parms
            rs.next();
            accountID = rs.getInt("accountID");
            accountName = rs.getString("accountName");
            description = rs.getString("description");
            balance = rs.getDouble("balance");
            autoRedirectPercent = rs.getDouble("autoRedirectPercent");
            //incase of error output to console.  log4j would be better, 
            //great enhancement for later
        } catch (SQLException ex) {
            System.out.println("Error while pulling account data "+ex.getMessage());
        }

    }
    //to String method
    public String ToString() {
        return accountName + " " + description + " " + balance;
    }
    //save account method
    public void SaveAccount() {
        String query;
        //new account of the account ID = 0
        if (accountID == 0) 
        {
            //Greate prepaired statement to do the insert
            try {
                String sql = "insert into accounts (accountID, accountName, description, balance, autoRedirectPercent)"
                        + " values (?, ?, ?, ?, ?)";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1
                ps.setDouble(1, accountID);
                ps.setString(2, accountName);
                ps.setString(3, description);
                ps.setDouble(4, balance);
                ps.setDouble(5, autoRedirectPercent);
                //Execute the prepaired statement
                //ps.execute();
                MySQLInterface.ExecutePreparedStatement(ps);
                //Output the Stack if error is found
            } catch (SQLException ex) {
                System.out.println("Error during insert of account ID, Stack follows: " + ex.getMessage() + ex.getSQLState());
                ex.printStackTrace();
            }

        } else
            //UPdate the current Account ID record
        {
            try {
                String sql = "UPDATE accounts SET accountName= ? , description= ? , balance= ? , autoRedirectPercent= ? "
                        + "WHERE personId= ? ";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1
                //Populate the Prepared Statement
                ps.setString(1, accountName);
                ps.setString(2, description);
                ps.setDouble(3, balance);
                ps.setDouble(4, autoRedirectPercent);
                //5th parm is the where clause, I used account id
                ps.setInt   (5, accountID);
                //Execute the Prepared statement
                //ps.execute();
                MySQLInterface.ExecutePreparedStatement(ps);
            } catch (SQLException ex) {
                //Catch Error, and out put the error and sql state
                System.out.println("Error while updating account table: " + ex.getMessage() + ex.getSQLState());
                ex.printStackTrace();
            }

        }
        
        if (accountID == 0) {
            ResultSet rs = MySQLInterface.ExecuteQuery("SELECT LAST_INSERT_ID() LID;");
            try {
                rs.next();
                accountID = rs.getInt("LID");
            } catch (Exception ex) {
            }
        }
    }

    //Static method useful for retrieving all the people in the Persons table and creating a Person object for each
    // It then returns an array of Person objects
    public static Accounts[] getAccounts() {
        ArrayList<Accounts> accounts = new ArrayList<>();
        try {
            //Populate look up query values
            String query = "select accountID, accountName, description, balance, autoRedirectPercent from accounts;";
            //create object to hold the results from the above query
            ResultSet rs;
            //Connect to DB and populate rs object
            rs = MySQLInterface.ExecuteQuery(query);
            //Create iterator for rs items
            while (rs.next()) {
                //Create temp account object
                Accounts tempAccount = new Accounts();
                //Assign rs items to temp accounts object
                tempAccount.accountID = rs.getInt("accountID");
                tempAccount.accountName = rs.getString("accountName");
                tempAccount.description = rs.getString("description");
                tempAccount.balance = rs.getDouble("balance");
                tempAccount.autoRedirectPercent = rs.getDouble("autoRedirectPercent");

                //add the accounts object into the ArrayList for later use
                accounts.add(tempAccount);

            }


        } catch (SQLException ex) {
            System.out.println("Error while retrieve all the Accounts from the Accounts table: " + ex.getMessage() + ex.getSQLState());
            ex.printStackTrace();
        }

        //return our ArrayList of accounts as an array
        Accounts[] objects = accounts.toArray(new Accounts[accounts.size()]); //convert the arraylist to an array of Person objects
        return objects;
    }
    //Create jTree object to stay consistant with other objects in project
    public Object[] jTree()
    {
        return new Object[]{accountID, accountName, description, balance};
    }
    
    //Get and set methods
    
}

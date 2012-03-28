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
public class Withdrawals
{

    //table columns go here
    public int withdrawalId = 0;
    public int accountId = 0;
    public float amount = 0;
    public String payee = "";
    public String description = "";
    //Classes for foreign keys
    Accounts account = new Accounts();
    private PreparedStatement ps; // To make the inserts and updates easier

    //Loads person data from the database using the personId number
    public void LoadWithdrawals(int ID)
    {
        String query = "SELECT * FROM withdrawals WHERE withdrawalId=" + ID;
        ResultSet rs = MySQLInterface.ExecuteQuery(query);

        try
        {
            rs.next();
            withdrawalId = rs.getInt("withdrawalId");
            accountId = rs.getInt("accountId");
            amount = rs.getFloat("amount");
            payee = rs.getString("payee");
            description = rs.getString("description");
            
            account.LoadAccount(accountId);

        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    public String ToString()
    {
        //return "";
        return account.ToString();
    }

    public void SaveWithdrawals()
    {
        if (withdrawalId == 0) //new  
        {
            try
            {
                String sql = "insert into withdrawals (accountId, amount, payee, description)"
                        + " values (?,?,?,?)";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1

                //ps vars
                ps.setInt(1,accountId);
                ps.setFloat(2, amount);
                ps.setString(3,payee);
                ps.setString(4, description);

                if (!MySQLInterface.ExecutePreparedStatement(ps))
                {
                    System.out.println("Error while adding new withdrawals");
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
                String sql = "UPDATE withdrawals SET accountId=?, amount=?, payee=?, description=?"
                        + " WHERE withdrawalId='?'";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1

                ps.setInt(1,accountId);
                ps.setFloat(2, amount);
                ps.setString(3,payee);
                ps.setString(4, description);
                ps.setInt(5, withdrawalId);

                if (!MySQLInterface.ExecutePreparedStatement(ps))
                {
                    System.out.println("Error while updating withdrawals with id: " + withdrawalId);
                }
            }
            catch (SQLException ex)
            {
                System.out.println("Error while updating or inserting new withdrawals: " + ex.getMessage());
                ex.printStackTrace();
            }

        }

        if (withdrawalId == 0)
        {
            ResultSet rs = MySQLInterface.ExecuteQuery("SELECT LAST_INSERT_ID() LID;");
            try
            {
                rs.next();
                withdrawalId = rs.getInt("LID");
            }
            catch (Exception ex)
            {
            }
        }
    }

    //Static method useful for retrieving all the data in the table and creating an object for each
    // It then returns an array of objects
    public static Withdrawals[] getWithdrawals()
    {
        ArrayList<Withdrawals> withdrawals = new ArrayList<>();
        try
        {
            String query = "select withdrawalId,accountId from withdrawals;";
            ResultSet rs;
            rs = MySQLInterface.ExecuteQuery(query);

            while (rs.next())
            {
                Withdrawals temp = new Withdrawals();
                temp.withdrawalId = rs.getInt("withdrawalId");
                temp.accountId = rs.getInt("accountId");
                temp.account.LoadAccount(temp.accountId);

                //add the object into the ArrayList for later use
                withdrawals.add(temp);

            }

        }
        catch (SQLException ex)
        {
            System.out.println("Error while retrieve all the Withdrawals data from the table: " + ex.getMessage());
        }

        //return our ArrayList as an array
        Withdrawals[] objects = withdrawals.toArray(new Withdrawals[withdrawals.size()]); //convert the arraylist to an array of objects
        return objects;
    }
}

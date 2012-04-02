/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package epoverty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author z047236
 */
public class Account_Transaction_data {
    String firstName;
    String lastName;
    Double donationAmount;
    String donor;
    String donationDate;
    private PreparedStatement ps;
    
    public Account_Transaction_data[] getTransactions(Double selectedAccount)
    {
        ArrayList<Account_Transaction_data> transactions = new ArrayList<>();
        ResultSet rs2;
  
        try {
                String sql = "Select p.firstName, p.lastName, dn.donationAmount, p2.firstName ||' '|| p2.lastName Donor, dn.donationDate  "
                        + "from accounts ac, deposits dp, donations dn, fundraisers fi, persons p, donors d, persons p2 "
                        + "where ac.accountID = dp.accountID and dp.donationID = dn.donationID and dn.fundraiserID = fi.fundraiserID "
                        + "and fi.personID = p.personID and d.donorId = dn.donorId and d.personId = p2.personId "
                        + " and ac.accountID = ? order by donationdate desc ";
                ps = MySQLInterface.dbConn.prepareStatement(sql);//prepared statements use variable places defined by ?s, indexed starting at 1
                
                ps.setDouble(1, selectedAccount);
                //Execute the prepaired statement
                //ps.execute();
                rs2 =  MySQLInterface.ExecuteQuery(sql);
                        //MySQLInterface.ExecutePreparedStatement(ps);
                //Output the Stack if error is found
                rs2.next();
                //Account_Transaction_data td = new Account_Transaction_data();
                firstName = rs2.getString("firstName");
                lastName = rs2.getString("lastName");
                donationAmount = rs2.getDouble("donationAmount");
                donor = rs2.getString("Donor");
                donationDate = rs2.getString("donationDate");
            } catch (SQLException ex) {
                System.out.println("Error during Pull of account and transaction data, Stack follows: " + ex.getMessage() + ex.getSQLState());
                ex.printStackTrace();
            }
        Account_Transaction_data[] objects = transactions.toArray(new Account_Transaction_data[transactions.size()]); //convert the arraylist to an array of Transactions objects
        return objects;
        
    }

    public Double getDonationAmount() {
        return donationAmount;
    }

    public void setDonationAmount(Double donationAmount) {
        this.donationAmount = donationAmount;
    }

    public String getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(String donationDate) {
        this.donationDate = donationDate;
    }

    public String getDonor() {
        return donor;
    }

    public void setDonor(String donor) {
        this.donor = donor;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    
    
    

}

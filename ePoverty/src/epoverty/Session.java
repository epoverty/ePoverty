/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package epoverty;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Trevor
 */
public class Session {
    private static Session currentSession;
    private int accessType;
    private Person person;
    
    public static final int UNAUTHORIZED = 0;
    public static final int DONOR_TYPE = 1;
    public static final int FUNDRAISER_TYPE = 2;
    public static final int PERSON_TYPE = 3;
    public static final int ADMIN_TYPE = 4;
    
    
    static {
        currentSession = new Session();
    }
    
    public static Session getCurrentSession()
    {
        return currentSession;
    }
    
    private Session()
    {
        accessType = Session.UNAUTHORIZED;
    }
    
    public boolean authenticate(String email, String pass)
    {
        String [] fields = new String[2];
        String [] values = new String [2];
        
        Person person = new Person();
        
        fields[0] = "emailAddress";
        fields[1] = "password";
        
        values[0] = email;
        values[1] = pass;
        try {
            person.LoadPersonWhere(fields, values);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        if (person.personID != 0)
        {
            this.person = person;
            
            this.accessType = Session.findAccessType(person);
            return true;
        }
        System.out.println("Incorrest login");
        
        return false;
    }
    
    public int getAccessType()
    {
        return accessType;
    }
    
    public static int findAccessType(Person person)
    {
        try {
            //first check for admin status
            System.out.println("findAccessType Invoked");
            PreparedStatement ps = MySQLInterface.dbConn.prepareStatement("Select * from admins WHERE personId=?");
            
            ps.setInt(1, person.personID);
            
            if (ps.execute()) //Person is admin
            {
                return Session.ADMIN_TYPE;
            }
            
            ps = MySQLInterface.dbConn.prepareStatement("Select * from donors WHERE personId=?");
            
            ps.setInt(1, person.personID);
            
            if (ps.execute()) //Person is donor
            {
                return Session.DONOR_TYPE;
            }
            
            ps = MySQLInterface.dbConn.prepareStatement("Select * from fundraisers WHERE personId=?");
            
            ps.setInt(1, person.personID);
            
            if (ps.execute()) //Person is fundraiser
            {
                return Session.FUNDRAISER_TYPE;
            }
            
            return Session.PERSON_TYPE;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return Session.PERSON_TYPE;
    }
    
    
    
    
}

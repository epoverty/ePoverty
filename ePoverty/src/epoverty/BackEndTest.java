/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package epoverty;

/**
 *
 * @author duffy
 */
public class BackEndTest
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.println("Connect to database");
        boolean result = MySQLInterface.ConnectToDatabase();
        if (result)
            System.out.println("Connected");
        else
        {
            System.out.println("Failed to connect");
            return;
        }

        Person p = new Person();
        p.LoadPerson(1);
        System.out.println("Pulled user: "+p.ToString()+" from database");
                
        /*
        p = new Person();
        p.firstName = "Test";
        p.lastName = "User";
        p.SavePerson();
        System.out.println("ID of person added to database: "+p.personID);
        */
        
        Session mySession = Session.getCurrentSession();
        mySession.authenticate("trevorhackett@gmail.com", "pass");
        
        if( mySession.getAccessType() == Session.ADMIN_TYPE)
        {
            System.out.println("Test was successful");
        }
                
        MySQLInterface.CloseConnection();
        System.out.println("Closed Connection to Database");
    }
}

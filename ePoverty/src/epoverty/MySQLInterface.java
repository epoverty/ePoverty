package epoverty;

/**
 * Interface for accessing the MySQL database
 *
 * @author Brent Nielson
 */
import com.mysql.jdbc.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLInterface
{

    static Connection dbConn;
    static String url = "jdbc:mysql://50.63.244.60/epoverty";
    static String user = "epoverty";
    static String pass = "Cis2770#";

    public MySQLInterface()
    {
    }

    //attempt to connect to database on error return false
    public static boolean ConnectToDatabase()
    {
        try
        {
            
            dbConn = DriverManager.getConnection(url, user, pass);
            return true;
        }
        catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    //closes connection to the database
    public static void CloseConnection()
    {
        try
        {
            dbConn.close();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(MySQLInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void ExecuteNonQuery(String query)
    {
        ExecuteNonQuery(query, true);

    }

    private static void ExecuteNonQuery(String query, boolean firstTry)
    {
        try
        {
            Statement stmt = dbConn.createStatement();
            stmt.executeUpdate(query);
        }
        catch (SQLException ex)
        {
            if (ConnectToDatabase() && firstTry)
            {
                ExecuteNonQuery(query, false);
            }
            else
            {
                Logger.getLogger(MySQLInterface.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex.getErrorCode());
            }
        }
    }

    public static ResultSet ExecuteQuery(String query)
    {
        return ExecuteQuery(query, true);
    }

    private static ResultSet ExecuteQuery(String query, boolean firstTry)
    {
        try
        {
            ResultSet rs;
            Statement stmt = dbConn.createStatement();
            rs = stmt.executeQuery(query);
            return rs;

        }
        catch (SQLException ex)
        {
            if (ConnectToDatabase() && firstTry)
            {
                return ExecuteQuery(query, false);
            }
            else
            {
                Logger.getLogger(MySQLInterface.class.getName()).log(Level.SEVERE, null, ex);                
                return null;
            }
        }
    }
}

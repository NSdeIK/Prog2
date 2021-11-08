package Server;

import java.sql.*;

import com.lambdaworks.crypto.SCrypt;
import com.lambdaworks.crypto.SCryptUtil;

public class Database
{
    private String url = "";
    private Connection conn;
    private DatabaseMetaData meta;
    private boolean status;

    public boolean getstatus()
    {
        return status;
    }


    public void CreateDatabase(String filename)
    {
        String url = "jdbc:sqlite:" + filename;
        try
        {
            conn = DriverManager.getConnection(url);
            if(conn !=null)
            {
                meta = conn.getMetaData();
                System.out.println("New database has been created...");
            }
            DBCreateTable();
        }
        catch(SQLException sqle)
        {
            System.out.println(sqle);
        }
    }

    public void DBConnect()
    {
            try
            {
                String url = "jdbc:sqlite:demo.db";
                conn = DriverManager.getConnection(url);
                System.out.println("DATABASE CONNECTION TEST - SUCCESS");
            }catch(SQLException sqlerror)
            {
                System.out.println("DATABASE CONNECTION TEST - FAIL");
                sqlerror.getMessage();
            }finally
            {
                try
                {
                    if(conn !=null)
                    {
                        conn.close();
                    }
                }catch(SQLException sqlerror)
                {
                    System.out.println(sqlerror.getMessage());
                }
        }
    }

    public void DBCreateTable()
    {
        String sqlusertable = "CREATE TABLE users(\n"
                        + "user_id INTEGER PRIMARY KEY,\n"
                        + "username VARCHAR(25) NOT NULL,\n"
                        + "password VARCHAR(100) NOT NULL)\n";

        try
        {
            Statement stmt = conn.createStatement();
            stmt.execute(sqlusertable);
        }catch(SQLException sqle)
        {
            System.out.println(sqle.getMessage());
        }

    }

    public void createUser(String name, String pw)
    {
        String url = "jdbc:sqlite:demo.db";
        String sql = "INSERT INTO users(username, password) VALUES(?,?)";
        String sqlcheck = "SELECT * FROM users WHERE username='"+name+"'";
        try
        {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlcheck);
            boolean freename;

            if (rs.next())
            {
                do {
                        status = false;
                        break;
                } while (rs.next());
            }
            else
            {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, pw);
                pstmt.executeUpdate();
                status = true;
            }
            stmt.close();
            conn.close();

        }catch(SQLException e)
        {
            e.getMessage();
        }

    }

    public void loginUser(String name, String pw)
    {
        String url = "jdbc:sqlite:demo.db";
        String sqlcheck = "SELECT * FROM users WHERE username='"+name+"' LIMIT 1";
        try
        {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlcheck);
            if (rs.next())
            {
                do {
                    if(SCryptUtil.check(pw,rs.getString("password")) == true)
                    {
                        status = true;
                        break;
                    }
                    else
                    {
                        status = false;
                        break;
                    }
                } while (rs.next());
            }
            else
            {
                status = false;
            }
            stmt.close();
            conn.close();

        }catch(SQLException e)
        {
            e.getMessage();
        }

    }
}






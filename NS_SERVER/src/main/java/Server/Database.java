package Server;

import java.io.*;
import java.sql.*;

import com.lambdaworks.crypto.SCryptUtil;
import ns_srv.ns_server.Server;

public class Database
{
    private String url = "";
    private String name;
    private Connection conn;
    private DatabaseMetaData meta;
    private boolean status;

    private Server srv;

    public Database(Server srv)
    {
        this.srv = srv;
    }


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

        String sqlwords = "CREATE TABLE words_hu(\n"
                        + "id INTEGER PRIMARY KEY,\n"
                        + "word VARCHAR(255) NOT NULL)\n";

        try
        {
            Statement stmt = conn.createStatement();
            stmt.execute(sqlusertable);
            stmt.execute(sqlwords);
            insertwords_hu(stmt);
        }catch(SQLException sqle)
        {
            System.out.println(sqle.getMessage());
        }

    }

    private void insertwords_hu(Statement stmt)
    {
        try {
            String sql = "INSERT INTO words_hu(word) VALUES(?)";
            File file = new File("./src/main/resources/words_hu.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;

            while ((st = br.readLine()) != null) {

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, st);
                pstmt.executeUpdate();
            }

            stmt.close();
            conn.close();

        }catch(FileNotFoundException e)
        {
            System.out.println("Nincs ilyen fájl [words_hu.txt]");
        }catch(IOException e)
        {
            System.out.println("Hiba történt fájl beolvasásával! [readLine()]");
        }catch(SQLException e)
        {
            System.out.println("Hiba történt adatok feltöltésében! [PreparedStatement...]");
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

    public void WordCheck(String name,String word, int type, String character)
    {
        this.name = name;
        String url = "jdbc:sqlite:demo.db";
        switch(type)
        {
            case 1:
                String sqlcheck = "SELECT word FROM words_hu WHERE word LIKE '%"+character+"' AND word = '"+word+"' LIMIT 1";
                try
                {
                    conn = DriverManager.getConnection(url);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sqlcheck);
                    if (rs.next())
                    {
                        srv.WordSuccess(name, word);
                    }
                    else
                    {
                        System.out.println("Nincs ilyen szó!");
                    }
                    stmt.close();
                    conn.close();

                }catch(SQLException e)
                {
                    e.getMessage();
                }
                break;
            case 2:
                String sqlcheck2 = "SELECT word FROM words_hu WHERE word LIKE '"+character+"%' AND word = '"+word+"' LIMIT 1";
                try
                {
                    conn = DriverManager.getConnection(url);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sqlcheck2);
                    if (rs.next())
                    {
                        srv.WordSuccess(name, word);
                    }
                    else
                    {
                        System.out.println("Nincs ilyen szó!");
                    }
                    stmt.close();
                    conn.close();

                }catch(SQLException e)
                {
                    e.getMessage();
                }
                break;
            case 3:
                String sqlcheck3 = "SELECT word FROM words_hu WHERE word LIKE '%"+character+"%' AND word = '"+word+"' LIMIT 1";
                try
                {
                    conn = DriverManager.getConnection(url);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sqlcheck3);
                    if (rs.next())
                    {
                        srv.WordSuccess(name, word);
                    }
                    else
                    {
                        System.out.println("Nincs ilyen szó!");
                    }
                    stmt.close();
                    conn.close();

                }catch(SQLException e)
                {
                    e.getMessage();
                }
                break;
            default:
                break;
        }
    }

}






//this is the password:
//tpjF6EIf=!Py
//start the server: mysqld --initialize
//connect to the server: mysql -u root -p < password
package main;


import java.sql.Connection;
import com.mysql.jdbc.Driver;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;

import java.util.List;
import java.util.Scanner;


/*
    class - Database.
    explanation - manages the Database.

    main function -
        ExecuteSelectStatement : executes Select statement and returns result.
        ExecuteNonQuery : executes any statement other than Select.

    other functions -
        startServer -static method : starts the server (if needed, server should be started manually).
        serverRunning -static method : checks if the server is running.
        establishConnection - restablish the connection.
        changeUser - attempts to change the curent user. sends true if successful.
*/
class Database{

    Connection conn;
    private String username, password;
    private boolean serviceIsRunning = false;
    private static boolean serverOn = true;

    public Database(){

        System.out.println("the database server must be on!");
        System.out.println("please enter the username and password for the mysql database");
        Scanner scn = new Scanner(System.in);

        this.username = "financial_manager";
        this.password = "password";

        establishConnection();
    }

    public List<Transaction> getTransactions(){

        return null;
    }

    public void establishConnection(){

        conn = Connect(username, password);
    }

    private Connection Connect(String username, String password){

        if(stringHasProblems(username, password)){

            System.out.println("invalid username|password - nope");
            return null;
        }
        try{
            Connection conn =  DriverManager.getConnection("jdbc:mysql://localhost/test?" +
                                        "user="+ username +"&password=" + password);
        }
        catch(SQLException s){

            serverOn = false;
            s.printStackTrace();
            System.out.println("\n" + s.getMessage());
        }
        return conn;
    }

    //unimportant
    public boolean changeUser(String username, String password){

        if(!serviceIsRunning){

            this.username = username;
            this.password = password;

            establishConnection();
            return true;
        }
        return false;
    }

    public boolean stringHasProblems(String... a){

        for(String s:a)
            if(s.contains("\"") || s.contains("&")) return true;

        return false;
    }

    //the idea is that the server could be started and terminated by the app, optionally (not yet implemented).
    //connecting to it will still be done through the java connector.
    //as for right now, the server must be always on.
    //however, if it crushes you can always via loop: check, restart, and then reconnect.

    //do this through a dos script or by hand, it is really simple tbh...
    //just : mysqld --console
    public static Thread startServer(){

        if(true)    //shut up java, I know what I'm doing!
            throw new UnsupportedOperationException("this function is not yet implemented");
        if(serverRunning()) return null;
        else{
            return new Thread(){
                @Override
                public void run(){

                    //establish server
                }
            };
        }
    }

    public static boolean serverRunning(){

        return serverOn; //for now
    }
}

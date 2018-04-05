package org.iiitb.classifieds.database;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class UIDAIDatabaseConnection 
{
	Statement statement;
    ResultSet resultSet;
    Connection connection = null;
    String query = null;

    //Constructor for opening the Database Connection

    public UIDAIDatabaseConnection()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver Found");
        }

        catch (ClassNotFoundException e)
        {
            System.out.println("Driver Not Found: " + e);
        }

        String url = "jdbc:mysql://localhost/UIDAI";
        //String url = "jdbc:mysql://192.168.30.11:3306/sdc";

        String user = "root";
        String password = "password";
        connection = null;

        try
        {
            connection = (Connection)DriverManager.getConnection(url, user, password);
            System.out.println("Successfully Connected to Database");
        }
        catch(SQLException e)
        {
            System.out.println("SQL Exception: " + e); 
        }                   

    }
    public String authenticateAadhar(long aadhaar)
    {
    	String mobile = null;
    	java.sql.PreparedStatement preparedStatement = null;
    	try
    	{
    		query="select * from Aadhaar where AadhaarNumber=?";
    		preparedStatement = connection.prepareStatement(query);
    		preparedStatement.setLong(1,aadhaar);
    		resultSet=preparedStatement.executeQuery();
    		if(resultSet.next()){
    		return resultSet.getString("Mobile");
    	   }
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();	
    	}
    	return mobile;
    	
    }
 
}

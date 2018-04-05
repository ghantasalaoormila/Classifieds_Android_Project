package org.iiitb.classifieds.database;

import java.sql.Blob;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class DatabaseConnection 
{
	Statement statement;
    ResultSet resultSet;
    Connection connection = null;
    String query = null;

    //Constructor for opening the Database Connection

    public DatabaseConnection()
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

        String url = "jdbc:mysql://localhost/classifieds";
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
    public ResultSet authenticateUser(String username,String password)
    {
    	
    	java.sql.PreparedStatement preparedStatement = null;
    	try
    	{
    		query="select * from userDetails where EmailId=? and Password=?";
    		preparedStatement = connection.prepareStatement(query);
    		preparedStatement.setString(1,username);
    		preparedStatement.setString(2,password);
    		resultSet=preparedStatement.executeQuery();
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();	
    	}
    	return resultSet;
    	
    }
    public String userRegistration(String username,int age,String gender,String password,String emailId,long aadhaar,long mobile_number)
    {
    	String result;
    	java.sql.PreparedStatement preparedStatement = null;
    	if(validEmail(emailId).equals("valid")){
    	try
    	{
    	   
    	   query="insert into userDetails values(?,?,?,?,?,?,?,0)";
    	   preparedStatement = connection.prepareStatement(query);
    	   preparedStatement.setString(1,username);
    	   preparedStatement.setInt(2,age);
    	   preparedStatement.setString(3,gender);
    	   preparedStatement.setString(4,password);
    	   preparedStatement.setString(5,emailId);
    	   preparedStatement.setLong(6,aadhaar);
    	   preparedStatement.setLong(7,mobile_number);
    	   preparedStatement.execute();
    	   result="success";
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();
    		result="invalid";
    		
    	}
    }
    	else{
    		result = "in_use";
    	}
    	return result;
    }
    
    public ResultSet getNumber(String Email){
    	
    	java.sql.PreparedStatement preparedStatement = null;
    	try
    	{
    		query="select * from userDetails where EmailId=?";
    		preparedStatement = connection.prepareStatement(query);
    		preparedStatement.setString(1,Email);
    		resultSet=preparedStatement.executeQuery();
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();	
    	}
    	return resultSet;
    }
    
    
    public String validEmail(String mail){
    	
    
    	String result=null;
    	java.sql.PreparedStatement preparedStatement = null;
    	try{
    		query = "select * from userDetails where EmailId=?";
    		preparedStatement=connection.prepareStatement(query);
    		preparedStatement.setString(1,mail);
    		resultSet = preparedStatement.executeQuery();
    		if(resultSet.next()){
    			return "invalid";
    		}
    		else{
    		return "valid";
    		}
    	}
    	catch(SQLException e){
    		e.printStackTrace();
    		result = "invalid";
    	}
    	return result;
    }
    
    public String changeDetails(String name,String email,String gender,int Age,long mobile){
    	
          java.sql.PreparedStatement preparedStatement = null;	
          try{
        	  query = "UPDATE userDetails SET Name=?,Gender=?,MobileNumber=?,Age=? WHERE EmailId=?";
        	  preparedStatement = connection.prepareStatement(query);
        	  preparedStatement.setString(1, name);
        	  preparedStatement.setString(2, gender);
        	  preparedStatement.setLong(3, mobile);
        	  preparedStatement.setInt(4, Age);
        	  preparedStatement.setString(5,email);
              preparedStatement.execute();
              return "success";
          }
          catch(SQLException e){
        	  e.printStackTrace();
          }
          return "fail";
    
    }
    
    
    public String saveToDrafts(String email,String title,String description,long mobile,String category,String location,String image){
   	
    	java.sql.PreparedStatement preparedStatement = null;
 	   
 	   try{
 		   query = "INSERT INTO Drafts(Title,Description,posted_by,Location,Mobile,Category,Image) values(?,?,?,?,?,?,?)";
 		   preparedStatement = connection.prepareStatement(query);
 		   preparedStatement.setString(1,title);
 		   preparedStatement.setString(2,description);
 		   preparedStatement.setLong(5,mobile);    
 	       preparedStatement.setString(3, email);
 		   //preparedStatement.setInt(4,status);
 		   preparedStatement.setString(4,location);
 	       preparedStatement.setString(6,category);
 	       preparedStatement.setString(7, image);
 		   preparedStatement.execute();
 	       query = "SELECT * FROM Drafts WHERE Title=? and posted_by=? and category=?";
 	       preparedStatement = connection.prepareStatement(query);
 	       preparedStatement.setString(1,title);
	       preparedStatement.setString(2,email);
	       preparedStatement.setString(3,category);
	       resultSet = preparedStatement.executeQuery();
 	       if(resultSet.next())
 	            return resultSet.getString("Id");
 	   }
 	   
 	   catch(SQLException e){
 		   e.printStackTrace();
 		   }
 	   return "fail";
    	
    }
    
    public String addToWishlist(String email,String category,int Id){
    	
    	java.sql.PreparedStatement prepareStatement = null;	
        try{
      	  query = "INSERT INTO Wishlist values(?,?,?)";
      	  prepareStatement = connection.prepareStatement(query);
      	  prepareStatement.setString(1,email);
      	  prepareStatement.setString(2,category);
      	  prepareStatement.setInt(3, Id);
          prepareStatement.execute();
          return "success";
        }
        catch(SQLException e){
      	  e.printStackTrace();
      	  //return "fail";
        }
        return "fail";	
    }
    
    
    public String delete_post_wishlist(int id,String email,String category){
    	
    	java.sql.PreparedStatement preparedStatement = null;
 	   try{
 		   if(email.equals("All")){
 			  query = "DELETE FROM Wishlist WHERE Id=? and Category=?";
 	 		   preparedStatement = connection.prepareStatement(query);
 	 		   preparedStatement.setInt(1,id);
 	 		   preparedStatement.setString(2,category);
 	 	       preparedStatement.execute();
 		   }
 		   else{
 		   query = "DELETE FROM Wishlist WHERE Id=? and EmailId=? and Category=?";
 		   preparedStatement = connection.prepareStatement(query);
 		   preparedStatement.setInt(1,id);
 		   preparedStatement.setString(2, email);
 		   preparedStatement.setString(3,category);
 	       preparedStatement.execute();
 		   }
 	       return "success";
 	       }
 	   catch(SQLException e){
 		   e.printStackTrace();
 		   }
 	   return "fail";    	
    }
    
    public String getFromWishList(String Email){
    	java.sql.PreparedStatement prepareStatement = null;	
    	try{
			   
               query = "SELECT * FROM Wishlist WHERE EmailId=?";
			   prepareStatement = connection.prepareStatement(query);
			   prepareStatement.setString(1,Email);
			   ResultSet rS= prepareStatement.executeQuery();
		       JSONObject others = new JSONObject();
		       others.put("posts",0);
		       JSONArray array = new JSONArray();
		       int posts =0;
		       JSONObject jsonObject = new JSONObject();
	       while(rS.next())
		   {
		   int id = rS.getInt("Id");
		   String category = rS.getString("Category");
	       jsonObject = getById(category,id);
		   array.put(posts,jsonObject);
		   posts += 1;
		   //array.put(0,jsonObject);
		   }
	       others.put("Array",array);
	       others.put("posts",posts);
		   return others.toString();
	   }
        catch(SQLException e){
      	  e.printStackTrace();
      	  //return "fail";
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        return "fail";
    }
    
    
    public JSONObject getById(String Category,int Id){
    	
    	if(Category.equals("Lifestyle"))Category="LifeStyle";
    	java.sql.PreparedStatement preparedStatement = null;
 	   try{
 		    query = "SELECT * FROM " + Category + " WHERE Id=?";
 		    preparedStatement = connection.prepareStatement(query);
 		    preparedStatement.setInt(1,Id);
 		    resultSet = preparedStatement.executeQuery();
 		    JSONObject jsonObject = new JSONObject();
 		    //JSONArray array = new JSONArray();
 		    //int posts =0;
 	        while(resultSet.next())
 		    {
 		    //JSONObject jsonObject = new JSONObject();
 		    jsonObject.put("Id",resultSet.getString("Id"));
 		    jsonObject.put("Title",resultSet.getString("Title"));
 		    jsonObject.put("Description",resultSet.getString("Description"));
 		    jsonObject.put("posted_on", resultSet.getString("posted_on"));
 		    jsonObject.put("posted_by", resultSet.getString("posted_by"));
 		    jsonObject.put("Status",resultSet.getInt("status"));
 		    jsonObject.put("Location",resultSet.getString("Location"));
 		    jsonObject.put("Mobile", resultSet.getLong("Mobile"));
 		    jsonObject.put("image", resultSet.getString("Image"));
 		    }
 	        //services.put("Array",array);
 	        jsonObject.put("Category",Category);
 		    return jsonObject;
 	     }
 	   catch(SQLException e){
 		   e.printStackTrace();
 	   }
 	   catch(Exception e){
 		   e.printStackTrace();
 	   }
     return null;
 	   
 	}
    
    public String changePassword(String password,String email){
    	
        java.sql.PreparedStatement preparedStatement = null;	
        try{
      	  query = "UPDATE userDetails SET Password=? WHERE EmailId=?";
      	  preparedStatement = connection.prepareStatement(query);
      	  preparedStatement.setString(1, password);
      	  preparedStatement.setString(2, email);
          preparedStatement.execute();
          return "success";
        }
        catch(SQLException e){
      	  e.printStackTrace();
      	  //return "fail";
        }
        return "fail";
  }
    
   public String delete_account(String EmailId){
	   java.sql.PreparedStatement preparedStatement = null;
	   
	   try{
		   query = "DELETE FROM userDetails where EmailId=?";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setString(1, EmailId);
	       preparedStatement.execute();
		   return "success" ; 
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   return "fail";
   }
   
   public String AddToServices(String title,String description,String posted_by,int status,String location,long mobile,String image){
       
	   java.sql.PreparedStatement preparedStatement = null;
	   
	   try{
		   query = "INSERT INTO Services(Title,Description,posted_by,posted_on,status,Location,Mobile,Image) values(?,?,?,CURDATE(),?,?,?,?)";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setString(1,title);
		   preparedStatement.setLong(6,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setInt(4,status);
		   preparedStatement.setString(5,location);
		   preparedStatement.setString(7, image);
	       preparedStatement.execute();
	       query = "SELECT * FROM Services WHERE Title=? and Description=? and posted_by=? and Location=? and Mobile=?";
 	       preparedStatement = connection.prepareStatement(query);
 	       preparedStatement.setString(1,title);
		   preparedStatement.setLong(5,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setString(4,location);
	       resultSet = preparedStatement.executeQuery();
 	       if(resultSet.next())
 	            return resultSet.getString("Id");
	   
         }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   return "fail";
   }
   
public String AddToEvents(String title,String description,String posted_by,int status,String location,long mobile,String image){
       
	   java.sql.PreparedStatement preparedStatement = null;
	   
	   try{
		   query = "INSERT INTO Events(Title,Description,posted_by,posted_on,status,Location,Mobile,Image) values(?,?,?,CURDATE(),?,?,?,?)";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setString(1,title);
		   preparedStatement.setLong(6,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setInt(4,status);
		   preparedStatement.setString(5,location);
	       preparedStatement.setString(7, image);
		   preparedStatement.execute();
	       query = "SELECT * FROM Events WHERE Title=? and Description=? and posted_by=? and Location=? and Mobile=?";
 	       preparedStatement = connection.prepareStatement(query);
 	       preparedStatement.setString(1,title);
		   preparedStatement.setLong(5,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setString(4,location);
	       resultSet = preparedStatement.executeQuery();
 	       if(resultSet.next())
 	            return resultSet.getString("Id");
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   return "fail";
   }

public String AddToOthers(String title,String description,String posted_by,int status,String location,long mobile,String image){
    
	   java.sql.PreparedStatement preparedStatement = null;
	   
	   try{
		   query = "INSERT INTO Others(Title,Description,posted_by,posted_on,status,Location,Mobile,Image) values(?,?,?,CURDATE(),?,?,?,?)";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setString(1,title);
		   preparedStatement.setLong(6,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setInt(4,status);
		   preparedStatement.setString(5,location);
		   preparedStatement.setString(7, image);
	       preparedStatement.execute();
	       query = "SELECT * FROM Others WHERE Title=? and Description=? and posted_by=? and Location=? and Mobile=?";
 	       preparedStatement = connection.prepareStatement(query);
 	       preparedStatement.setString(1,title);
		   preparedStatement.setLong(5,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setString(4,location);
	       resultSet = preparedStatement.executeQuery();
 	       if(resultSet.next())
 	            return resultSet.getString("Id");
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   return "fail";
}
   
public String AddToElectronics(String title,String description,String posted_by,int status,String location,long mobile,String image){
    
	   java.sql.PreparedStatement preparedStatement = null;
	   
	   try{
		   query = "INSERT INTO Electronics(Title,Description,posted_by,posted_on,status,Location,Mobile,Image) values(?,?,?,CURDATE(),?,?,?,?)";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setString(1,title);
		   preparedStatement.setLong(6,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setInt(4,status);
		   preparedStatement.setString(5,location);
	       preparedStatement.setString(7, image);
		   preparedStatement.execute();
	       query = "SELECT * FROM Electronics WHERE Title=? and Description=? and posted_by=? and Location=? and Mobile=?";
 	       preparedStatement = connection.prepareStatement(query);
 	       preparedStatement.setString(1,title);
		   preparedStatement.setLong(5,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setString(4,location);
	       resultSet = preparedStatement.executeQuery();
 	       if(resultSet.next())
 	            return resultSet.getString("Id");
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   return "fail";
}

public String AddToEducation(String title,String description,String posted_by,int status,String location,long mobile,String image){
    
	   java.sql.PreparedStatement preparedStatement = null;
	   
	   try{
		   query = "INSERT INTO Education(Title,Description,posted_by,posted_on,status,Location,Mobile,Image) values(?,?,?,CURDATE(),?,?,?,?)";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setString(1,title);
		   preparedStatement.setLong(6,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setInt(4,status);
		   preparedStatement.setString(5,location);
		   preparedStatement.setString(7, image);
	       preparedStatement.execute();
	       query = "SELECT * FROM Education WHERE Title=? and Description=? and posted_by=? and Location=? and Mobile=?";
 	       preparedStatement = connection.prepareStatement(query);
 	       preparedStatement.setString(1,title);
		   preparedStatement.setLong(5,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setString(4,location);
		   resultSet = preparedStatement.executeQuery();
 	       if(resultSet.next())
 	            return resultSet.getString("Id");
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   return "fail";
}

public String AddToVehicles(String title,String description,String posted_by,int status,String location,long mobile,String image){
    
	   java.sql.PreparedStatement preparedStatement = null;
	   
	   try{
		   query = "INSERT INTO Vehicles(Title,Description,posted_by,posted_on,status,Location,Mobile,Image) values(?,?,?,CURDATE(),?,?,?,?)";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setString(1,title);
		   preparedStatement.setLong(6,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setInt(4,status);
		   preparedStatement.setString(5,location);
		   preparedStatement.setString(7, image);
	       preparedStatement.execute();
	       query = "SELECT * FROM Vehicles WHERE Title=? and Description=? and posted_by=? and Location=? and Mobile=?";
 	       preparedStatement = connection.prepareStatement(query);
 	       preparedStatement.setString(1,title);
		   preparedStatement.setLong(5,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setString(4,location);
	       resultSet = preparedStatement.executeQuery();
 	       if(resultSet.next())
 	            return resultSet.getString("Id");
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   return "fail";
}

public String AddToLifestyle(String title,String description,String posted_by,int status,String location,long mobile,String image){
    
	   java.sql.PreparedStatement preparedStatement = null;
	   
	   try{
		   query = "INSERT INTO LifeStyle(Title,Description,posted_by,posted_on,status,Location,Mobile,Image) values(?,?,?,CURDATE(),?,?,?,?)";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setString(1,title);
		   preparedStatement.setLong(6,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setInt(4,status);
		   preparedStatement.setString(7, image);
		   preparedStatement.setString(5,location);
	       preparedStatement.execute();
	       query = "SELECT * FROM LifeStyle WHERE Title=? and Description=? and posted_by=? and Location=? and Mobile=?";
 	       preparedStatement = connection.prepareStatement(query);
 	       preparedStatement.setString(1,title);
		   preparedStatement.setLong(5,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setString(4,location);
	       resultSet = preparedStatement.executeQuery();
 	       if(resultSet.next())
 	            return resultSet.getString("Id");
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   return "fail";
}

public String AddToJobs(String title,String description,String posted_by,int status,String location,long mobile,String image){
    
	   java.sql.PreparedStatement preparedStatement = null;
	   
	   try{
		   query = "INSERT INTO Jobs(Title,Description,posted_by,posted_on,status,Location,Mobile,Image) values(?,?,?,CURDATE(),?,?,?,?)";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setString(1,title);
		   preparedStatement.setLong(6,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setInt(4,status);
		   preparedStatement.setString(5,location);
		   preparedStatement.setString(7, image);
	       preparedStatement.execute();
	       query = "SELECT * FROM Jobs WHERE Title=? and Description=? and posted_by=? and Location=? and Mobile=?";
 	       preparedStatement = connection.prepareStatement(query);
 	       preparedStatement.setString(1,title);
		   preparedStatement.setLong(5,mobile);
	       preparedStatement.setString(2,description);
	       preparedStatement.setString(3, posted_by);
		   preparedStatement.setString(4,location);
	       resultSet = preparedStatement.executeQuery();
 	       if(resultSet.next())
 	            return resultSet.getString("Id");
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   return "fail";
   }

public JSONObject postsFromServices(String Email){
	
	   java.sql.PreparedStatement preparedStatement = null;
	   try{
		   if(Email.equals("All")){
			   query = "SELECT * FROM Services";
		       preparedStatement = connection.prepareStatement(query);
		   }
		   else{
		       query = "SELECT * FROM Services WHERE posted_by=?";
		       preparedStatement = connection.prepareStatement(query);
		       preparedStatement.setString(1,Email);
		   }
		   resultSet = preparedStatement.executeQuery();
		   JSONObject services = new JSONObject();
		   services.put("posts",0);
		   JSONArray array = new JSONArray();
		   int posts =0;
		   while(resultSet.next()) 
		   {
		   JSONObject jsonObject = new JSONObject();
		   jsonObject.put("Id",resultSet.getString("Id"));
		   jsonObject.put("Title",resultSet.getString("Title"));
		   jsonObject.put("Description",resultSet.getString("Description"));
		   jsonObject.put("posted_on", resultSet.getString("posted_on"));
		   jsonObject.put("posted_by", resultSet.getString("posted_by"));
		   jsonObject.put("Status",resultSet.getInt("status"));
		   jsonObject.put("Location",resultSet.getString("Location"));
		   jsonObject.put("Mobile", resultSet.getString("Mobile"));
		   jsonObject.put("image", resultSet.getString("Image"));
		   //image = resultSet.getBlob("IMAGE");
		   //jsonObject.put("IMAGE", image);
		   array.put(posts,jsonObject);
		   posts += 1;
		   //array.put(0,jsonObject);
		   }
	       services.put("Array",array);
	       services.put("posts",posts);
		   return services;
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   
	   catch(Exception e){
		   e.printStackTrace();
	   }
	   return null;

  }


public JSONObject postsFromEvents(String Email){
	
	   java.sql.PreparedStatement preparedStatement = null;
	   try{
			   if(Email.equals("All")){
				   query = "SELECT * FROM Events";
			       preparedStatement = connection.prepareStatement(query);
			   }
			   else{
			       query = "SELECT * FROM Events WHERE posted_by=?";
			       preparedStatement = connection.prepareStatement(query);
			       preparedStatement.setString(1,Email);
			   }
			resultSet = preparedStatement.executeQuery();
		   JSONObject events = new JSONObject();
		   events.put("posts",0);
		   JSONArray array = new JSONArray();
		   int posts =0;
	       while(resultSet.next())
		   {
		   JSONObject jsonObject = new JSONObject();
		   jsonObject.put("Id",resultSet.getString("Id"));
		   jsonObject.put("Title",resultSet.getString("Title"));
		   jsonObject.put("Description",resultSet.getString("Description"));
		   jsonObject.put("posted_on", resultSet.getString("posted_on"));
		   jsonObject.put("posted_by",resultSet.getString("posted_by"));
		   jsonObject.put("Status",resultSet.getInt("status"));
		   jsonObject.put("Location",resultSet.getString("Location"));
		   jsonObject.put("Mobile", resultSet.getString("Mobile"));
		   jsonObject.put("image", resultSet.getString("Image"));
		   array.put(posts,jsonObject);
		   posts += 1;
		   //array.put(0,jsonObject);
		   }
	       events.put("Array",array);
	       events.put("posts",posts);
		   return events;
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   
	   catch(Exception e){
		   e.printStackTrace();
	   }
	   return null;
}

public JSONObject postsFromOthers(String Email){
	
	   java.sql.PreparedStatement preparedStatement = null;
	   try{
			   if(Email.equals("All")){
				   query = "SELECT * FROM Others";
			       preparedStatement = connection.prepareStatement(query);
			   }
			   else{
			       query = "SELECT * FROM Others WHERE posted_by=?";
			       preparedStatement = connection.prepareStatement(query);
			       preparedStatement.setString(1,Email);
			   }
			resultSet = preparedStatement.executeQuery();
		   JSONObject others = new JSONObject();
		   //others.put("posts",0);
		   JSONArray array = new JSONArray();
		   int posts =0;
	       while(resultSet.next())
		   {
		   JSONObject jsonObject = new JSONObject();
		   jsonObject.put("Id",resultSet.getString("Id"));
		   jsonObject.put("Title",resultSet.getString("Title"));
		   jsonObject.put("Description",resultSet.getString("Description"));
		   jsonObject.put("posted_on", resultSet.getString("posted_on"));
		   jsonObject.put("posted_by",resultSet.getString("posted_by"));
		   jsonObject.put("Status",resultSet.getInt("status"));
		   jsonObject.put("Location",resultSet.getString("Location"));
		   jsonObject.put("Mobile", resultSet.getString("Mobile"));
		   jsonObject.put("image", resultSet.getString("Image"));
		   array.put(posts,jsonObject);
		   posts += 1;
		   //array.put(0,jsonObject);
		   }
	       others.put("Array",array);
	       others.put("posts",posts);
		   return others;
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   
	   catch(Exception e){
		   e.printStackTrace();
	   }
	   return null;

}

public JSONObject postsFromLifestyle(String Email){
	
	   java.sql.PreparedStatement preparedStatement = null;
	   try{
			   if(Email.equals("All")){
				   query = "SELECT * FROM LifeStyle";
			       preparedStatement = connection.prepareStatement(query);
			   }
			   else{
			       query = "SELECT * FROM LifeStyle WHERE posted_by=?";
			       preparedStatement = connection.prepareStatement(query);
			       preparedStatement.setString(1,Email);
			   }
			resultSet = preparedStatement.executeQuery();
		   JSONObject lifestyle = new JSONObject();
		   lifestyle.put("posts",0);
		   JSONArray array = new JSONArray();
		   int posts =0;
	       while(resultSet.next())
		   {
		   JSONObject jsonObject = new JSONObject();
		   jsonObject.put("Id",resultSet.getString("Id"));
		   jsonObject.put("Title",resultSet.getString("Title"));
		   jsonObject.put("Description",resultSet.getString("Description"));
		   jsonObject.put("posted_on", resultSet.getString("posted_on"));
		   jsonObject.put("posted_by",resultSet.getString("posted_by"));
		   jsonObject.put("Status",resultSet.getInt("status"));
		   jsonObject.put("Location",resultSet.getString("Location"));
		   jsonObject.put("Mobile", resultSet.getString("Mobile"));
		   jsonObject.put("image", resultSet.getString("Image"));
		   array.put(posts,jsonObject);
		   posts += 1;
		   //array.put(0,jsonObject);
		   }
	       lifestyle.put("Array",array);
	       lifestyle.put("posts",posts);
		   return lifestyle;
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   
	   catch(Exception e){
		   e.printStackTrace();
	   }
	   return null;

}


public JSONObject postsFromJobs(String Email){
	
	   java.sql.PreparedStatement preparedStatement = null;
	   try{
		   if(Email.equals("All")){
				   query = "SELECT * FROM Jobs";
			       preparedStatement = connection.prepareStatement(query);
			   }
			   else{
			       query = "SELECT * FROM Jobs WHERE posted_by=?";
			       preparedStatement = connection.prepareStatement(query);
			       preparedStatement.setString(1,Email);
			   }
			resultSet = preparedStatement.executeQuery();
		   JSONObject jobs = new JSONObject();
		   jobs.put("posts",0);
		   JSONArray array = new JSONArray();
		   int posts =0;
	       while(resultSet.next())
		   {
		   JSONObject jsonObject = new JSONObject();
		   jsonObject.put("Id",resultSet.getString("Id"));
		   jsonObject.put("Title",resultSet.getString("Title"));
		   jsonObject.put("Description",resultSet.getString("Description"));
		   jsonObject.put("posted_on", resultSet.getString("posted_on"));
		   jsonObject.put("posted_by",resultSet.getString("posted_by"));
		   jsonObject.put("Status",resultSet.getInt("status"));
		   jsonObject.put("Location",resultSet.getString("Location"));
		   jsonObject.put("Mobile", resultSet.getString("Mobile"));
		   jsonObject.put("image", resultSet.getString("Image"));
		   array.put(posts,jsonObject);
		   posts += 1;
		   //array.put(0,jsonObject);
		   }
	       jobs.put("Array",array);
	       jobs.put("posts",posts);
		   return jobs;
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   
	   catch(Exception e){
		   e.printStackTrace();
	   }
	   return null;

}

public JSONObject postsFromElectronics(String Email){
	
	   java.sql.PreparedStatement preparedStatement = null;
	   try{
			   if(Email.equals("All")){
				   query = "SELECT * FROM Electronics";
			       preparedStatement = connection.prepareStatement(query);
			   }
			   else{
			       query = "SELECT * FROM Electronics WHERE posted_by=?";
			       preparedStatement = connection.prepareStatement(query);
			       preparedStatement.setString(1,Email);
			   }
			resultSet = preparedStatement.executeQuery();
		   JSONObject electronics = new JSONObject();
		   electronics.put("posts",0);
		   JSONArray array = new JSONArray();
		   int posts =0;
	       while(resultSet.next())
		   {
		   JSONObject jsonObject = new JSONObject();
		   jsonObject.put("Id",resultSet.getString("Id"));
		   jsonObject.put("Title",resultSet.getString("Title"));
		   jsonObject.put("Description",resultSet.getString("Description"));
		   jsonObject.put("posted_on", resultSet.getString("posted_on"));
		   jsonObject.put("posted_by",resultSet.getString("posted_by"));
		   jsonObject.put("Status",resultSet.getInt("status"));
		   jsonObject.put("Location",resultSet.getString("Location"));
		   jsonObject.put("Mobile", resultSet.getString("Mobile"));
		   jsonObject.put("image", resultSet.getString("Image"));
		   array.put(posts,jsonObject);
		   posts += 1;
		   //array.put(0,jsonObject);
		   }
	       electronics.put("Array",array);
	       electronics.put("posts",posts);
		   return electronics;
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   
	   catch(Exception e){
		   e.printStackTrace();
	   }
	   return null;

}

public JSONObject postsFromEducation(String Email){
	
	   java.sql.PreparedStatement preparedStatement = null;
	   try{
			   if(Email.equals("All")){
				   query = "SELECT * FROM Education";
			       preparedStatement = connection.prepareStatement(query);
			   }
			   else{
			       query = "SELECT * FROM Education WHERE posted_by=?";
			       preparedStatement = connection.prepareStatement(query);
			       preparedStatement.setString(1,Email);
			   }
		   resultSet = preparedStatement.executeQuery();
		   JSONObject education = new JSONObject();
		   education.put("posts",0);
		   JSONArray array = new JSONArray();
		   int posts =0;
	       while(resultSet.next())
		   {
		   JSONObject jsonObject = new JSONObject();
		   jsonObject.put("Id",resultSet.getString("Id"));
		   jsonObject.put("Title",resultSet.getString("Title"));
		   jsonObject.put("Description",resultSet.getString("Description"));
		   jsonObject.put("posted_on", resultSet.getString("posted_on"));
		   jsonObject.put("posted_by",resultSet.getString("posted_by"));
		   jsonObject.put("Status",resultSet.getInt("status"));
		   jsonObject.put("Location",resultSet.getString("Location"));
		   jsonObject.put("Mobile", resultSet.getString("Mobile"));
		   jsonObject.put("image", resultSet.getString("Image"));
		   array.put(posts,jsonObject);
		   posts += 1;
		   //array.put(0,jsonObject);
		   }
	       education.put("Array",array);
	       education.put("posts",posts);
		   return education;
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   
	   catch(Exception e){
		   e.printStackTrace();
	   }
	   return null;

}


public JSONObject postsFromVehicles(String Email){
	
	   java.sql.PreparedStatement preparedStatement = null;
	   try{
			   if(Email.equals("All")){
				   query = "SELECT * FROM Vehicles";
			       preparedStatement = connection.prepareStatement(query);
			   }
			   else{
			       query = "SELECT * FROM Vehicles WHERE posted_by=?";
			       preparedStatement = connection.prepareStatement(query);
			       preparedStatement.setString(1,Email);
			   }
		   resultSet = preparedStatement.executeQuery();
		   JSONObject education = new JSONObject();
		   education.put("posts",0);
		   JSONArray array = new JSONArray();
		   int posts =0;
	       while(resultSet.next())
		   {
		   JSONObject jsonObject = new JSONObject();
		   jsonObject.put("Id",resultSet.getString("Id"));
		   jsonObject.put("Title",resultSet.getString("Title"));
		   jsonObject.put("Description",resultSet.getString("Description"));
		   jsonObject.put("posted_on", resultSet.getString("posted_on"));
		   jsonObject.put("posted_by",resultSet.getString("posted_by"));
		   jsonObject.put("Status",resultSet.getInt("status"));
		   jsonObject.put("Location",resultSet.getString("Location"));
		   jsonObject.put("Mobile", resultSet.getString("Mobile"));
		   jsonObject.put("image", resultSet.getString("Image"));
		   array.put(posts,jsonObject);
		   posts += 1;
		   //array.put(0,jsonObject);
		   }
	       education.put("Array",array);
	       education.put("posts",posts);
		   return education;
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   
	   catch(Exception e){
		   e.printStackTrace();
	   }
	   return null;

}


  public String getUserDetails(String Email){
	  java.sql.PreparedStatement preparedStatement = null;
	   try{
		   query = "SELECT * FROM userDetails WHERE EmailId=?";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setString(1,Email);
	       resultSet = preparedStatement.executeQuery();
		   JSONObject jsonObject = new JSONObject();
		   while(resultSet.next()){
			   jsonObject.put("Name",resultSet.getString("Name"));
			   jsonObject.put("Age",resultSet.getInt("Age"));
			   jsonObject.put("Gender",resultSet.getString("Gender"));
			   jsonObject.put("Password",resultSet.getString("Password"));
			   jsonObject.put("Email Address",resultSet.getString("EmailId"));
			   jsonObject.put("Aadhaar Number", resultSet.getString("Aadhaar"));
			   jsonObject.put("MobileNumber", resultSet.getString("MobileNumber"));		
		   }
		   return jsonObject.toString();
	   }
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   
	   catch(Exception e){
		   e.printStackTrace();
	   }
	
	return "fail";
  } 
  
  public String registerFeedback(String email,String feedback){
	  java.sql.PreparedStatement preparedStatement = null;
	  try{
		   query = "INSERT INTO Feedback values(?,?)";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setString(1,email);
		   preparedStatement.setString(2, feedback);
	       preparedStatement.execute();
	       return "success";
	  }
	  catch(SQLException e){
		  e.printStackTrace();
	  }
	  return "fail";
  }
  
  public String delete_post(int Id,String category){
	  java.sql.PreparedStatement preparedStatement = null;
	  String cat = category;
	  if(category.equals("Lifestyle"))cat = "LifeStyle"; 
	  try{
		   query = "DELETE FROM "+cat+" WHERE Id=?";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setInt(1,Id);
	       preparedStatement.execute();
	       if(!category.equals("Drafts")){
	    	   delete_post_wishlist(Id,"All",category);
	       }
		   return "success";
	       }
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   return "fail";
  }
  
  
  public String postsFromDrafts(String Email){
	  java.sql.PreparedStatement preparedStatement = null;
	   try{
		   query = "SELECT * FROM Drafts WHERE posted_by=?";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setString(1,Email);
		   resultSet = preparedStatement.executeQuery();
		   JSONObject drafts = new JSONObject();
		   drafts.put("posts",0);
		   JSONArray array = new JSONArray();
		   int posts =0;
	       while(resultSet.next())
		   {
		   JSONObject jsonObject = new JSONObject();
		   jsonObject.put("Id",resultSet.getString("Id"));
		   jsonObject.put("Category",resultSet.getString("Category"));
		   jsonObject.put("Title",resultSet.getString("Title"));
		   jsonObject.put("Description",resultSet.getString("Description"));
		   jsonObject.put("Location",resultSet.getString("Location"));
		   jsonObject.put("image",resultSet.getString("Image"));
		   String mobile = resultSet.getString("Mobile");
		   if(mobile.equals("0")){
			   mobile ="";
		   }
		   jsonObject.put("Mobile",mobile);
		   jsonObject.put("posted_by", resultSet.getString("posted_by"));
		   array.put(posts,jsonObject);
		   posts += 1;
		   //array.put(0,jsonObject);
		   }
	       drafts.put("Array",array);
	       drafts.put("posts",posts);
		   return drafts.toString();
	   }
	   
	   catch(SQLException e){
		   e.printStackTrace();
		   }
	   
	   catch(Exception e){
		   e.printStackTrace();
	   }
	   return null;

  }
  
  public String edit_post(String category,int id,String title,String description,String posted_by,String location,long mobile,String image){
	  java.sql.PreparedStatement preparedStatement = null;
	   
	   try{
		   query = "UPDATE "+category + " SET Title=?,Description=?,Location=?,Image=?,Mobile=? WHERE Id=? AND posted_by=?";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setInt(6, id);
		   preparedStatement.setString(7, posted_by);
		   preparedStatement.setString(1,title);
		   preparedStatement.setLong(5,mobile);
	       preparedStatement.setString(2,description);
		   preparedStatement.setString(3,location);
		   preparedStatement.setString(4, image);
	       preparedStatement.execute();
           return "success";
	   }
	   catch(SQLException e){
		e.printStackTrace();   
	   }
	   return "fail";
	   }

  public String edit_draft(String category,int id,String title,String description,String posted_by,String location,long mobile,String image){
	  java.sql.PreparedStatement preparedStatement = null;
	   
	   try{
		   query = "UPDATE Drafts SET Title=?,Description=?,Location=?,Image=?,Mobile=?,Category=? WHERE Id=? AND posted_by=? ";
		   preparedStatement = connection.prepareStatement(query);
		   preparedStatement.setInt(7, id);
		   preparedStatement.setString(8, posted_by);
		   preparedStatement.setString(1,title);
		   preparedStatement.setLong(5,mobile);
	       preparedStatement.setString(2,description);
		   preparedStatement.setString(3,location);
		   preparedStatement.setString(4, image);
		   preparedStatement.setString(6, category);
	       preparedStatement.execute();
           return "success";
	   }
	   catch(SQLException e){
		e.printStackTrace();   
	   }
	   return "fail";
	   }


}
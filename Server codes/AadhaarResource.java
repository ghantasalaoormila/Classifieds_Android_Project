package org.iiitb.classifieds.resource;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

//import javax.json.Json;
//import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientResponse;
import org.iiitb.classifieds.database.DatabaseConnection;
import org.iiitb.classifieds.EmailValidation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.Blob;

@Path("resource")
public class AadhaarResource {

	/*@Path("login")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String login(@FormParam("username") String username,@FormParam("password") String password)
	{
		DatabaseConnection databaseConnection=new DatabaseConnection();
		return databaseConnection.authenticateUser(username, password);
	
	}*/
	
	@Path("signin")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String signin(String text)
	{
		String emailId = null;
		String password = null;
		
		try
		{
			JSONObject jsonObject=new JSONObject(text);
			emailId=jsonObject.getString("EmailId");
	       password=jsonObject.getString("Password");	
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
			
       DatabaseConnection databaseConnection=new DatabaseConnection();
	   ResultSet resultSet=databaseConnection.authenticateUser(emailId, password);
	   try{
	   	if(resultSet.next())
	   	{
	   		return "success";
	   	}
	   	else
	   	{
	   		return "fail";
	       }
	      }
	   catch(SQLException e)
	   {
	   	e.printStackTrace();
	   }
  	return "fail";
  	
}
	@Path("Register")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String Register(String text)
	{
		String Name = null;
		int age = 0;
		String gender = null;
		String password = null;
		String emailId = null;
		long aadhaar=0;
	    long mobile_number=0;
		
		
		try
		{
			JSONObject jsonObject=new JSONObject(text);
			Name=jsonObject.getString("Name");
	        age=jsonObject.getInt("Age");
	        gender = jsonObject.getString("Gender");
	        password = jsonObject.getString("Password");
	        emailId=jsonObject.getString("Email Address");
	        aadhaar=jsonObject.getLong("Aadhaar Number");
	        mobile_number= jsonObject.getLong("MobileNumber");
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		DatabaseConnection databaseConnection=new DatabaseConnection();
		return databaseConnection.userRegistration(Name,age,gender,password,emailId,aadhaar,mobile_number);		
	}
	
	@Path("forgotPassword")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String sendOtp(String text)
	{  String result=null;
	   JSONObject jsonObject = null;
	   JSONObject jO = null;
	   String Email = null;
	   String mobile = null;
	   String otp = null;
	try {

		jO = new JSONObject(text);
		Email = jO.getString("Email Address");
		otp = jO.getString("otp");
		DatabaseConnection databaseConnection = new DatabaseConnection();
		ResultSet resultSet = databaseConnection.getNumber(Email);
		if(resultSet.next()){
		mobile = resultSet.getString("MobileNumber");
	    jsonObject = new JSONObject();
	    jsonObject.put("api_key", "4ce8e210");
	    jsonObject.put("api_secret", "6f24c9690e405ec7");
	    jsonObject.put("to","91"+mobile);
	    jsonObject.put("from","Classifieds");
	    jsonObject.put("text","OTP : " + otp);
	    String serverURL = "https://rest.nexmo.com/sms/json";
	    URL url = new URL(serverURL);
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	    //connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
	    connection.setDoOutput(true);

	    OutputStream os = connection.getOutputStream();
	    os.write(jsonObject.toString().getBytes("UTF-8"));
	    os.close();
	    int responseCode = connection.getResponseCode();


	    InputStream is = connection.getInputStream();
	    BufferedReader r = new BufferedReader(new InputStreamReader(is));
	    StringBuilder total = new StringBuilder();
	    String line;
	    while ((line = r.readLine()) != null) {
	        total.append(line);
	    }
	    is.close();
	    line = total.toString();
	    line = line.trim();
	    jsonObject = new JSONObject(line);
	    JSONArray innerObject = jsonObject.getJSONArray("messages");
	    jsonObject = innerObject.getJSONObject(0);
	    int returnCode=jsonObject.getInt("status");
	    if(returnCode==0){
	    	return "success";
	    }
	    
	    else{
	    	return "fail";
	     }
		
		}
 	    //return "success";  
	}
	catch (MalformedURLException e)
	{
	    e.printStackTrace();
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}

		
		return "fail";
	}
	
	@Path("resetPassword")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String resetPassword(String text){
		String Email = null;
		String password = null;
		try{
			JSONObject jsonObject = new JSONObject(text);
			Email=jsonObject.getString("Email Address");
			password = jsonObject.getString("Password");
		}
		catch(Exception e){
			e.printStackTrace();
		    return "fail";
		}
		DatabaseConnection dabaseConnection = new DatabaseConnection();
		return dabaseConnection.changePassword(password, Email);
		
	}
	
	
	@Path("EditDetails")
	@POST
	@Consumes(MediaType.TEXT_PLAIN) 
	@Produces(MediaType.TEXT_PLAIN)
	public String EditDetails(String text){
		String result;
		String Name=null;
		String Gender = null;
		String Password = null;
		String EmailId = null;
		long Mobile =0;
        int Age =0;
		try
		  {
			JSONObject jsonObject=new JSONObject(text);
			Name=jsonObject.getString("Name");
			Age = jsonObject.getInt("Age");
	        Gender = jsonObject.getString("Gender");
	        Mobile = jsonObject.getLong("Mobile Number");
	        EmailId =jsonObject.getString("Email Address");
		    //System.out.print(Name+" "+ Age + " " + Gender + " " +Password + " "+EmailId);
		  }
		  catch(JSONException e)
		  {
			e.printStackTrace();
		    return "fail";
		  }
	
		DatabaseConnection databaseConnection=new DatabaseConnection();
		return databaseConnection.changeDetails(Name,EmailId,Gender,Age,Mobile);
		//databaseConnection.changePassword(Password,EmailId);
        //return "Success";
	}
	
	@Path("DeleteAccount")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String DeleteAccount(String EmailId){
	
		String Email = null;
		try
		  {
			JSONObject jsonObject=new JSONObject(EmailId);
	        Email =jsonObject.getString("Email Address");
		  }
		  catch(JSONException e)
		  {
			e.printStackTrace();
		  }
	
		DatabaseConnection databaseConnection=new DatabaseConnection();
		return databaseConnection.delete_account(Email);
      //  return "Success";
	}
	
    @Path("PostAd")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String PostAd(String Text){
    	String title = null;
    	String category = null;
    	String description = null;
    	//Date posted_on = null; 
    	String posted_by = null;
    	String location = null;
    	long mobile = 0;
    	int status =0;
    	String image = null;
    	try
    	{
			JSONObject jsonObject=new JSONObject(Text);
			title=jsonObject.getString("Title");
	        category = jsonObject.getString("Category");
	        description = jsonObject.getString("Description");
	        posted_by =jsonObject.getString("Email Address");
	        location = jsonObject.getString("Location");
		    mobile = jsonObject.getLong("Mobile Number");
		    image = jsonObject.getString("image");
		    //IMAGE = jsonObject.get("IMAGE");
    	}
		  catch(JSONException e)
		  {
			e.printStackTrace();
		  }
	     if(category.equals("Services")){
		  DatabaseConnection databaseConnection=new DatabaseConnection();
		  return databaseConnection.AddToServices(title,description,posted_by,status,location,mobile,image);
	     }
	    
	    else if(category.equals("Jobs")){
			  DatabaseConnection databaseConnection=new DatabaseConnection();
			 return databaseConnection.AddToJobs(title,description,posted_by,status,location,mobile,image);
		    }
	    
	    else if(category.equals("Lifestyle")){
			 DatabaseConnection databaseConnection=new DatabaseConnection();
			 return databaseConnection.AddToLifestyle(title,description,posted_by,status,location,mobile,image);
		    }
	    
	    else if(category.equals("Vehicles")){
			 DatabaseConnection databaseConnection=new DatabaseConnection();
			 return databaseConnection.AddToVehicles(title,description,posted_by,status,location,mobile,image);
		    }
	    
	    else if(category.equals("Education")){
			 DatabaseConnection databaseConnection=new DatabaseConnection();
			 return databaseConnection.AddToEducation(title,description,posted_by,status,location,mobile,image);
		    }
	    
	    else if(category.equals("Others")){
			 DatabaseConnection databaseConnection=new DatabaseConnection();
			 return databaseConnection.AddToOthers(title,description,posted_by,status,location,mobile,image);
		    }
	    
	    else if(category.equals("Events")){
			 DatabaseConnection databaseConnection=new DatabaseConnection();
			 return databaseConnection.AddToEvents(title,description,posted_by,status,location,mobile,image);
		    }
	    
	    else if(category.equals("Electronics")){
			 DatabaseConnection databaseConnection=new DatabaseConnection();
			 return databaseConnection.AddToElectronics(title,description,posted_by,status,location,mobile,image);
		    }
        return "fail";
    }
    
    
    @Path("Posts")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String MyPosts(String text){
    	String Email = null;
		try
		  {
			JSONObject jsonObject=new JSONObject(text);
	        Email =jsonObject.getString("Email Address");
		  }
    	catch(Exception e){
    		e.printStackTrace();
    	}
		DatabaseConnection databaseConnection = new DatabaseConnection();
		JSONObject posts = new JSONObject();
		JSONObject services = databaseConnection.postsFromServices(Email);
	    JSONObject others = databaseConnection.postsFromOthers(Email);
		JSONObject lifestyle = databaseConnection.postsFromLifestyle(Email);
		JSONObject events = databaseConnection.postsFromEvents(Email);
		JSONObject education = databaseConnection.postsFromEducation(Email);
		JSONObject jobs = databaseConnection.postsFromJobs(Email);
		JSONObject electronics = databaseConnection.postsFromElectronics(Email);
		JSONObject vehicles = databaseConnection.postsFromVehicles(Email);
		try{
		   posts.put("Services", services);
		   posts.put("Events",events);
		   posts.put("Others",others);
		   posts.put("Lifestyle", lifestyle);
		   posts.put("Education", education);
		   posts.put("Jobs", jobs);
		   posts.put("Electronics", electronics);
		   posts.put("Vehicles", vehicles);
		   return posts.toString();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return "fail";
    }
    
    @Path("userDetails")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String userDetails(String text){
    	String Email = null;
		try
		  {
			JSONObject jsonObject=new JSONObject(text);
	        Email =jsonObject.getString("Email Address");
		  }
    	catch(Exception e){
    		e.printStackTrace();
    	    return "fail";
    	}
		DatabaseConnection databaseConnection = new DatabaseConnection();
		return databaseConnection.getUserDetails(Email);
    }
    
    @Path("deletePost")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String deletePost(String text){
    	int postId = 0;
    	String category = null;
    	try{
    		
    		JSONObject jsonObject = new JSONObject(text);
    		postId = jsonObject.getInt("Id");
    		category = jsonObject.getString("Category");
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	    return "fail";
    	}
    	DatabaseConnection databaseConnection = new DatabaseConnection();
        return databaseConnection.delete_post(postId,category);   	
    }
    
    @Path("postsFrom")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String postsFrom(String Text){
    	String category = null;
    	try
		  {
			JSONObject jsonObject=new JSONObject(Text);
			category = jsonObject.getString("Category");
		  }
		  catch(JSONException e)
		  {
			e.printStackTrace();
		  }
	     if(category.equals("Services")){
		  DatabaseConnection databaseConnection=new DatabaseConnection();
		  return databaseConnection.postsFromServices("All").toString();
	     }
	    
	    else if(category.equals("Jobs")){
			  DatabaseConnection databaseConnection=new DatabaseConnection();
			  return databaseConnection.postsFromJobs("All").toString();
		    }
	    
	    else if(category.equals("Lifestyle")){
			 DatabaseConnection databaseConnection=new DatabaseConnection();
			 return databaseConnection.postsFromLifestyle("All").toString();
		    }
	    
	    else if(category.equals("Vehicles")){
			 DatabaseConnection databaseConnection=new DatabaseConnection();
			 return databaseConnection.postsFromVehicles("All").toString();
		    }
	    
	    else if(category.equals("Education")){
			 DatabaseConnection databaseConnection=new DatabaseConnection();
			 return databaseConnection.postsFromEducation("All").toString();
		    }
	    
	    else if(category.equals("Others")){
			 DatabaseConnection databaseConnection=new DatabaseConnection();
			 return databaseConnection.postsFromOthers("All").toString();
		    }
	    
	    else if(category.equals("Events")){
			 DatabaseConnection databaseConnection=new DatabaseConnection();
			 return databaseConnection.postsFromEvents("All").toString();
		    }
	    
	    else if(category.equals("Electronics")){
			 DatabaseConnection databaseConnection=new DatabaseConnection();
			 return databaseConnection.postsFromElectronics("All").toString();
		    }
        return "fail";
    }
    
    @Path("SaveToDrafts")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String SaveToDrafts(String text){
    	String category = null;
    	String email = null;
    	String title = null;
    	String Description = null;
    	String Location = null;
    	long mobile = 0;
    	String image = null;
    	
    	try{
    		JSONObject jsonObject = new JSONObject(text);
    		email = jsonObject.getString("Email Address");
    		title = jsonObject.getString("Title");
    		Description = jsonObject.getString("Description");
    		if(Description.equals(null)){
    		    Description = "";	
    		}
    		category = jsonObject.getString("Category");
    		Location = jsonObject.getString("Location");
    		image = jsonObject.getString("image");
    		if(Location.equals(null)){
    			Location = "";
    		}
    		mobile = jsonObject.getLong("Mobile Number");
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	DatabaseConnection databaseConnection = new DatabaseConnection();
        return databaseConnection.saveToDrafts(email,title,Description,mobile,category,Location,image);   	
    }
    
    
    @Path("Drafts")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String getDrafts(String text){
    	
    	String email = null;
    	
    	try{
    		JSONObject jsonObject = new JSONObject(text);
    		email = jsonObject.getString("Email Address");
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	    return "fail";
    	}
    	DatabaseConnection databaseConnection = new DatabaseConnection();
        return databaseConnection.postsFromDrafts(email);
    }
    
    
    @Path("deleteDraft")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteDraft(String text){
    	int id=0;
    	try{
    		JSONObject jsonObject = new JSONObject(text);
    		id = jsonObject.getInt("Id");
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	DatabaseConnection databaseConnection = new DatabaseConnection();
    	return databaseConnection.delete_post(id,"Drafts");
    	
    }
    
    @Path("deleteFromWishList")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String deletewishlist(String text){  	
    	int id=0;
    	String email=null;
    	String category = null;
    	try{
    		JSONObject jsonObject = new JSONObject(text);
    		id = jsonObject.getInt("Id");
    	    email = jsonObject.getString("Email Address");
    	    category = jsonObject.getString("Category"); 
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	DatabaseConnection databaseConnection = new DatabaseConnection();
    	return databaseConnection.delete_post_wishlist(id,email,category);
    }
   
    @Path("EditPost")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String editPost(String text){
    	
    	String title = null;
    	String category = null;
    	String description = null; 
    	String posted_by = null;
    	String location = null;
    	long mobile = 0;
    	int status =0;
    	int id =0 ;
    	String image = null;
    	try
    	{
			JSONObject jsonObject=new JSONObject(text);
			id = jsonObject.getInt("Id");
			title=jsonObject.getString("Title");
	        category = jsonObject.getString("Category");
	        description = jsonObject.getString("Description");
	        posted_by =jsonObject.getString("Email Address");
	        location = jsonObject.getString("Location");
		    mobile = jsonObject.getLong("Mobile Number");
		    image = jsonObject.getString("image");
	        
		    
    	}
		  catch(JSONException e)
		  {
			e.printStackTrace();
		  }
    	 if(category.equals("Lifestyle"))category="LifeStyle";
		  DatabaseConnection databaseConnection=new DatabaseConnection();
		  return databaseConnection.edit_post(category,id,title,description,posted_by,location,mobile,image);
    }
    
    
    @Path("EditDraft")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String editDraft(String text){
    	
    	String title = null;
    	String category = null;
    	String description = null; 
    	String posted_by = null;
    	String location = null;
    	long mobile = 0;
    	int status =0;
    	int id =0 ;
    	String image = null;
    	try
    	{
			JSONObject jsonObject=new JSONObject(text);
			id = jsonObject.getInt("Id");
			title=jsonObject.getString("Title");
	        category = jsonObject.getString("Category");
	        description = jsonObject.getString("Description");
	        posted_by =jsonObject.getString("Email Address");
	        location = jsonObject.getString("Location");
		    image = jsonObject.getString("image");
		    if(description.equals(null))description="";
		    if(location.equals(null))location="";
		    mobile = jsonObject.getLong("Mobile Number");
	        
		    
    	}
		  catch(JSONException e)
		  {
			e.printStackTrace();
		  }
		  DatabaseConnection databaseConnection=new DatabaseConnection();
		  return databaseConnection.edit_draft(category,id,title,description,posted_by,location,mobile,image);
    }
    
    @Path("AddToWishlist")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String AddToWishlist(String text){
    	String Email = null;
    	String Category = null;
    	int Id = 0;
    	try{
    		JSONObject jsonObject = new JSONObject(text);
    		Email = jsonObject.getString("Email Address");
    		Category = jsonObject.getString("Category");
    		Id = jsonObject.getInt("Id");
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	    return "fail";
    	}
    	DatabaseConnection databaseConnection = new DatabaseConnection();
        return databaseConnection.addToWishlist(Email,Category,Id);
    	
    }
    
    @Path("PostAdFromDrafts")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String postAdFromDrafts(String text){
    	String title = null;
    	String category = null;
    	String description = null; 
    	String posted_by = null;
    	String location = null;
    	long mobile = 0;
    	int status =0;
    	String image = null;
    	String result="fail";
    	int id=0;
    	try
    	{
			JSONObject jsonObject=new JSONObject(text);
			id = jsonObject.getInt("Id");
			title=jsonObject.getString("Title");
	        category = jsonObject.getString("Category");
	        description = jsonObject.getString("Description");
	        posted_by =jsonObject.getString("Email Address");
	        location = jsonObject.getString("Location");
		    mobile = jsonObject.getLong("Mobile Number");
		    image = jsonObject.getString("image");
		    //IMAGE = jsonObject.get("IMAGE");
    	}
		  catch(JSONException e)
		  {
			e.printStackTrace();
		  }
    	DatabaseConnection databaseConnection = new DatabaseConnection();
	     if(category.equals("Services")){
		  //DatabaseConnection databaseConnection=new DatabaseConnection();
		  result = databaseConnection.AddToServices(title,description,posted_by,status,location,mobile,image);
	     }
	    
	    else if(category.equals("Jobs")){
			//  DatabaseConnection databaseConnection=new DatabaseConnection();
			 result = databaseConnection.AddToJobs(title,description,posted_by,status,location,mobile,image);
		    }
	    
	    else if(category.equals("Lifestyle")){
			 //DatabaseConnection databaseConnection=new DatabaseConnection();
			 result = databaseConnection.AddToLifestyle(title,description,posted_by,status,location,mobile,image);
		    }
	    
	    else if(category.equals("Vehicles")){
			// DatabaseConnection databaseConnection=new DatabaseConnection();
			 result = databaseConnection.AddToVehicles(title,description,posted_by,status,location,mobile,image);
		    }
	    
	    else if(category.equals("Education")){
			// DatabaseConnection databaseConnection=new DatabaseConnection();
			 result = databaseConnection.AddToEducation(title,description,posted_by,status,location,mobile,image);
		    }
	    
	    else if(category.equals("Others")){
			 //DatabaseConnection databaseConnection=new DatabaseConnection();
			 result = databaseConnection.AddToOthers(title,description,posted_by,status,location,mobile,image);
		    }
	    
	    else if(category.equals("Events")){
			 //DatabaseConnection databaseConnection=new DatabaseConnection();
			 result = databaseConnection.AddToEvents(title,description,posted_by,status,location,mobile,image);
		    }
	    
	    else if(category.equals("Electronics")){
			// DatabaseConnection databaseConnection=new DatabaseConnection();
			 result = databaseConnection.AddToElectronics(title,description,posted_by,status,location,mobile,image);
		    }
        if(!(result.equals("fail"))){	
	  //   DatabaseConnection databaseConnection = new DatabaseConnection();
        	//System.out.println("In drafts");
        	databaseConnection.delete_post(id,"Drafts");
        }
        	return result;
    }
    
    @Path("HelpAndFeedback")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String complaints(String text){
    	String Email = null;
    	String feedback = null;
    	try{
    		JSONObject jsonObject = new JSONObject(text);
    		Email = jsonObject.getString("Email Address");
    		feedback = jsonObject.getString("Text");
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	    return "fail";
    	}
    	DatabaseConnection databaseConnection = new DatabaseConnection();
        return databaseConnection.registerFeedback(Email,feedback);
    }
    
    
    @Path("Wishlist")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String getWishlist(String text){
    	
    	String Email = null;
    	try{
    		JSONObject jsonObject = new JSONObject(text);
    		Email = jsonObject.getString("Email Address");
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	    return "fail";
    	}
    	DatabaseConnection databaseConnection = new DatabaseConnection();
        return databaseConnection.getFromWishList(Email);
    }
    
}


package org.iiitb.classifieds.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

import org.iiitb.classifieds.database.UIDAIDatabaseConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Path("aadhaar")
public class Aadhaar{

	@Path("authentication")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String aadhaarauthentication(String text)
	{  String result=null;
	   JSONObject jsonObject = null;
	   JSONObject jO = null;
	   String otp = null;
	   long aadhaar = 0;
	   String mobile = null;
	try {

		jO = new JSONObject(text);
		otp = jO.getString("otp");
		aadhaar = jO.getLong("aadhaar");
		UIDAIDatabaseConnection databaseConnection=new UIDAIDatabaseConnection();
		mobile = databaseConnection.authenticateAadhar(aadhaar);
	    if(mobile == null)return "fail";
	    jsonObject = new JSONObject();
	    jsonObject.put("api_key", "4ce8e210");
	    jsonObject.put("api_secret", "6f24c9690e405ec7");
	    jsonObject.put("to","91"+mobile);
	    jsonObject.put("from","UIDAI ");
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
	
	
}


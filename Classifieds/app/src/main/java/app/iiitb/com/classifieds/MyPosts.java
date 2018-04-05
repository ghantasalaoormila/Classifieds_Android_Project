package app.iiitb.com.classifieds;

import android.app.Fragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by URMILA on 09-Jul-17.
 */

public class MyPosts extends Fragment {

    private getUserPostsTask mPostsTask;
    private DataBaseAdapter db;
    private ArrayList<PostDetails> pd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_posts, container, false);
        db = new DataBaseAdapter(getActivity());
        displayMyPosts(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Posts");
    }

    public void displayMyPosts(View view){
        SessionManager session = new SessionManager(getActivity());
        String e = session.getEmail();
        db.open();
        Cursor c = db.fetchAllmyPostsdata();
        if (!(c.moveToFirst()) || c.getCount() ==0) {
            mPostsTask = new getUserPostsTask(e);
            mPostsTask.execute();
            db.close();
        }
        else {
            c.moveToFirst();
            try {
                pd = new ArrayList<>();
                while (!c.isAfterLast()) {
                    String title = c.getString(0);
                    String description = c.getString(1);
                    String category = c.getString(2);
                    String postedOn = c.getString(3);
                    String location = c.getString(4);
                    String id = c.getString(5);
                    String mobile = c.getString(6);
                    String img = c.getString(7);
                    pd.add(new PostDetails(title,description,category,location,mobile,id,e,postedOn,true,img));
                    c.moveToNext();
                }
                ArrayAdapter<PostDetails> adapter = new UserPostsArrayAdapter(getActivity(), 0, pd);
                ListView listView = (ListView) view.findViewById(R.id.customListView);
                listView.setAdapter(adapter);
            }
            catch(Exception e1){
                    e1.printStackTrace();
                }
            finally {
                c.close();
            }
        }
    }

    public class getUserPostsTask extends AsyncTask<String, String, String>{
        private String Email;
        getUserPostsTask(String email){
            Email= email;
        }
        @Override
        protected String doInBackground(String...params){
            String result = null;
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject();
                jsonObject.put("Email Address", Email);
                String serverURL = "http://192.168.60.27:8080/classifieds/webapi/resource/Posts";
                URL url = new URL(serverURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                //connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                os.write(jsonObject.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = connection.getResponseCode();

                Log.d("Code", "ResponseCode: " + responseCode);

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
                Log.d("PostActivity", "Data from the Server: " + line);
                result = line;
                return result;
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


            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            if(s == null)
            {
                Log.d("PostActivity", "Some error has occurred at Server");
                Toast.makeText(getActivity(),"No posts to show",Toast.LENGTH_LONG).show();
            }

            else
            {
                Log.d("MyPosts", "Displayed succesfully");
                Log.d("Data", s);
                //Toast.makeText(getActivity(),"Saved to drafts",Toast.LENGTH_LONG).show();
                db.open();
                try {
                    String Title;
                    String Category;
                    String Description;
                    String Location;
                    String Mobile;
                    String postedOn;
                    String iD;
                    String email;
                    String image;

                    pd = new ArrayList<>();
                    JSONObject jObject = new JSONObject(s);
                    JSONObject Services = jObject.getJSONObject("Services");
                    JSONObject Others = jObject.getJSONObject("Others");
                    JSONObject Events = jObject.getJSONObject("Events");
                    JSONObject Education = jObject.getJSONObject("Education");
                    JSONObject Electronics = jObject.getJSONObject("Electronics");
                    JSONObject Lifestyle = jObject.getJSONObject("Lifestyle");
                    JSONObject Jobs = jObject.getJSONObject("Jobs");
                    JSONObject Vehicles = jObject.getJSONObject("Vehicles");

                    JSONArray jArray = Services.getJSONArray("Array");
                    int posts = Services.getInt("posts");
                    for (int i=0;i<posts;i++){
                        JSONObject j = jArray.getJSONObject(i);
                        Category = "Services";
                        Title = j.getString("Title");
                        Description =j.getString("Description");
                        Location = j.getString("Location");
                        Mobile = j.getString("Mobile");
                        postedOn = j.getString("posted_on");
                        iD = j.getString("Id");
                        email = j.getString("posted_by");
                        image = j.getString("image");
                        pd.add(new PostDetails(Title,Description,Category,Location,Mobile,iD,email,postedOn,true,image));
                        db.InsertmyPostsData(Title,Description,Category,Location,postedOn,iD,Mobile,image);
                    }

                    jArray = Others.getJSONArray("Array");
                    posts = Others.getInt("posts");
                    for (int i=0;i<posts;i++){
                        JSONObject j = jArray.getJSONObject(i);
                        Category = "Others";
                        Title = j.getString("Title");
                        Description =j.getString("Description");
                        Location = j.getString("Location");
                        Mobile = j.getString("Mobile");
                        postedOn = j.getString("posted_on");
                        iD = j.getString("Id");
                        email = j.getString("posted_by");
                        image = j.getString("image");
                        pd.add(new PostDetails(Title,Description,Category,Location,Mobile,iD,email,postedOn,true,image));
                        db.InsertmyPostsData(Title,Description,Category,Location,postedOn,iD,Mobile,image);
                    }

                    jArray = Lifestyle.getJSONArray("Array");
                    posts = Lifestyle.getInt("posts");
                    for (int i=0;i<posts;i++){
                        JSONObject j = jArray.getJSONObject(i);
                        Category = "Lifestyle";
                        Title = j.getString("Title");
                        Description =j.getString("Description");
                        Location = j.getString("Location");
                        Mobile = j.getString("Mobile");
                        postedOn = j.getString("posted_on");
                        iD = j.getString("Id");
                        email = j.getString("posted_by");
                        image = j.getString("image");
                        pd.add(new PostDetails(Title,Description,Category,Location,Mobile,iD,email,postedOn,true,image));
                        db.InsertmyPostsData(Title,Description,Category,Location,postedOn,iD,Mobile,image);
                    }

                    jArray = Vehicles.getJSONArray("Array");
                    posts = Vehicles.getInt("posts");
                    for (int i=0;i<posts;i++){
                        JSONObject j = jArray.getJSONObject(i);
                        Category = "Vehicles";
                        Title = j.getString("Title");
                        Description =j.getString("Description");
                        Location = j.getString("Location");
                        Mobile = j.getString("Mobile");
                        postedOn = j.getString("posted_on");
                        iD = j.getString("Id");
                        email = j.getString("posted_by");
                        image = j.getString("image");
                        pd.add(new PostDetails(Title,Description,Category,Location,Mobile,iD,email,postedOn,true,image));
                        db.InsertmyPostsData(Title,Description,Category,Location,postedOn,iD,Mobile,image);
                    }

                    jArray = Education.getJSONArray("Array");
                    posts = Education.getInt("posts");
                    for (int i=0;i<posts;i++){
                        JSONObject j = jArray.getJSONObject(i);
                        Category = "Education";
                        Title = j.getString("Title");
                        Description =j.getString("Description");
                        Location = j.getString("Location");
                        Mobile = j.getString("Mobile");
                        postedOn = j.getString("posted_on");
                        iD = j.getString("Id");
                        email = j.getString("posted_by");
                        image = j.getString("image");
                        pd.add(new PostDetails(Title,Description,Category,Location,Mobile,iD,email,postedOn,true,image));
                        db.InsertmyPostsData(Title,Description,Category,Location,postedOn,iD,Mobile,image);
                    }

                    jArray = Electronics.getJSONArray("Array");
                    posts = Electronics.getInt("posts");
                    for (int i=0;i<posts;i++){
                        JSONObject j = jArray.getJSONObject(i);
                        Category = "Electronics";
                        Title = j.getString("Title");
                        Description =j.getString("Description");
                        Location = j.getString("Location");
                        Mobile = j.getString("Mobile");
                        postedOn = j.getString("posted_on");
                        iD = j.getString("Id");
                        email = j.getString("posted_by");
                        image = j.getString("image");
                        pd.add(new PostDetails(Title,Description,Category,Location,Mobile,iD,email,postedOn,true,image));
                        db.InsertmyPostsData(Title,Description,Category,Location,postedOn,iD,Mobile,image);
                    }

                    jArray = Jobs.getJSONArray("Array");
                    posts = Jobs.getInt("posts");
                    for (int i=0;i<posts;i++){
                        JSONObject j = jArray.getJSONObject(i);
                        Category = "Jobs";
                        Title = j.getString("Title");
                        Description =j.getString("Description");
                        Location = j.getString("Location");
                        Mobile = j.getString("Mobile");
                        postedOn = j.getString("posted_on");
                        iD = j.getString("Id");
                        email = j.getString("posted_by");
                        image = j.getString("image");
                        pd.add(new PostDetails(Title,Description,Category,Location,Mobile,iD,email,postedOn,true,image));
                        db.InsertmyPostsData(Title,Description,Category,Location,postedOn,iD,Mobile,image);
                    }

                    jArray = Events.getJSONArray("Array");
                    posts = Events.getInt("posts");
                    for (int i=0;i<posts;i++){
                        JSONObject j = jArray.getJSONObject(i);
                        Category = "Events";
                        Title = j.getString("Title");
                        Description =j.getString("Description");
                        Location = j.getString("Location");
                        Mobile = j.getString("Mobile");
                        postedOn = j.getString("posted_on");
                        iD = j.getString("Id");
                        email = j.getString("posted_by");
                        image = j.getString("image");
                        pd.add(new PostDetails(Title,Description,Category,Location,Mobile,iD,email,postedOn,true,image));
                        db.InsertmyPostsData(Title,Description,Category,Location,postedOn,iD,Mobile,image);
                    }

                    db.close();
                    ArrayAdapter<PostDetails> adapter = new UserPostsArrayAdapter(getActivity(), 0, pd);
                    ListView listView = (ListView) getActivity().findViewById(R.id.customListView);
                    listView.setAdapter(adapter);
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                    //getActivity().finish();
            }


        }

        @Override
        protected void onCancelled() {
            mPostsTask = null;
        }
    }
}

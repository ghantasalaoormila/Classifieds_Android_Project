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

public class Drafts extends Fragment {

    private getDraftsTask mDraftsTask;
    private DataBaseAdapter db;
    private ArrayList<PostDetails> pd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_posts, container, false);
        db = new DataBaseAdapter(getActivity());
        displayMyDrafts(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Drafts");
    }

    public void displayMyDrafts(View view){
        SessionManager session = new SessionManager(getActivity());
        String e = session.getEmail();
        db.open();
        Cursor c = db.fetchAllmyDraftsdata();
        if (!(c.moveToFirst()) || c.getCount() ==0) {
            mDraftsTask = new getDraftsTask(e);
            mDraftsTask.execute();
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
                    String postedOn = "";
                    String location = c.getString(3);
                    String id = c.getString(4);
                    String mobile = c.getString(5);
                    String image = c.getString(6);
                    pd.add(new PostDetails(title,description,category,location,mobile,id,e,postedOn,false,image));
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

    public class getDraftsTask extends AsyncTask<String, String, String>{
        private String Email;
        getDraftsTask(String email){
            Email= email;
        }
        @Override
        protected String doInBackground(String...params){
            String result = null;
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject();
                jsonObject.put("Email Address", Email);
                String serverURL = "http://192.168.60.27:8080/classifieds/webapi/resource/Drafts";
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
                Log.d("Drafts", "Data from the Server: " + line);
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
                Log.d("Drafts", "Some error has occurred at Server");
                Toast.makeText(getActivity(),"Nothing to display",Toast.LENGTH_LONG).show();
            }

            else
            {
                Log.d("Drafts", "Displayed succesfully");
                //Log.d("Data", s);
                //Toast.makeText(getActivity(),"Saved to drafts",Toast.LENGTH_LONG).show();
                db.open();
                try {
                    String Title;
                    String Category;
                    String Description;
                    String Location;
                    String Mobile;
                    String iD;
                    String email;
                    String img;

                    pd = new ArrayList<>();
                    JSONObject jObject = new JSONObject(s);
                    JSONArray jArray = jObject.getJSONArray("Array");
                    int posts = jObject.getInt("posts");
                    for (int i=0;i<posts;i++){
                        JSONObject j = jArray.getJSONObject(i);
                        Category = j.getString("Category");
                        Title = j.getString("Title");
                        Description =j.getString("Description");
                        Location = j.getString("Location");
                        Mobile = j.getString("Mobile");
                        iD = j.getString("Id");
                        email = j.getString("posted_by");
                        img = j.getString("image");
                        pd.add(new PostDetails(Title,Description,Category,Location,Mobile,iD,email,"Date",false,img));
                        db.InsertmyDraftsData(Title,Description,Category,Location,iD,Mobile,img);
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
            mDraftsTask = null;
        }
    }
}

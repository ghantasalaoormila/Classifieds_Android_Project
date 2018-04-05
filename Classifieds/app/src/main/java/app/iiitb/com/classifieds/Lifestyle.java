package app.iiitb.com.classifieds;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class Lifestyle extends AppCompatActivity {

    private getLifestyleTask mLifestyleTask;
    private DataBaseAdapter db;
    private ArrayList<PostDetails> pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifestyle);
        mLifestyleTask = new getLifestyleTask();
        mLifestyleTask.execute();
    }
    public class getLifestyleTask extends AsyncTask<String, String, String> {
        //private String Email;

        getLifestyleTask(){

        }
        @Override
        protected String doInBackground(String...params){
            String result = null;
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject();
                jsonObject.put("Category", "Lifestyle");
                String serverURL = "http://192.168.60.27:8080/classifieds/webapi/resource/postsFrom";
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
                Log.d("Lifestyle", "Some error has occurred at Server");
                Toast.makeText(Lifestyle.this,"No posts to show",Toast.LENGTH_LONG).show();
            }

            else
            {
                Log.d("Lifestyle", "Displayed succesfully");
                //Log.d("Data", s);
                //Toast.makeText(getActivity(),"Saved to drafts",Toast.LENGTH_LONG).show()
                try {
                    String Title;
                    String Category = "Lifestyle";
                    String Description;
                    String Location;
                    String Mobile;
                    String iD;
                    String email;
                    String image;
                    String postedOn;

                    pd = new ArrayList<>();
                    JSONObject jObject = new JSONObject(s);
                    JSONArray jArray = jObject.getJSONArray("Array");
                    int posts = jObject.getInt("posts");
                    
                    for (int i=0;i<posts;i++){
                        JSONObject j = jArray.getJSONObject(i);
                        //Category = j.getString("Category");
                        Title = j.getString("Title");
                        Description =j.getString("Description");
                        Location = j.getString("Location");
                        Mobile = j.getString("Mobile");
                        iD = j.getString("Id");
                        email = j.getString("posted_by");
                        image = j.getString("image");
                        postedOn = j.getString("posted_on");
                        pd.add(new PostDetails(Title,Description,Category,Location,Mobile,iD,email,postedOn,false,image));
                    }
                    ArrayAdapter<PostDetails> adapter = new AllPostsArrayAdapter(Lifestyle.this, 0, pd);
                    ListView listView = (ListView) Lifestyle.this.findViewById(R.id.customListView);
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
            mLifestyleTask = null;
        }
    }

}

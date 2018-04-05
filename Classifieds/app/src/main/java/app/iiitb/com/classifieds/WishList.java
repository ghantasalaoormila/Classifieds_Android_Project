package app.iiitb.com.classifieds;

import android.app.Fragment;
import android.content.Intent;
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

public class WishList extends Fragment {

    private getWishlist mWishListTask;
    private DataBaseAdapter db;
    private ArrayList<PostDetails> pd;
    private SessionManager session;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wish_list, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Wish-List");
        session = new SessionManager(getActivity());
        mWishListTask = new getWishlist(session.getEmail());
        mWishListTask.execute();
    }

    public class getWishlist extends AsyncTask<String, String, String> {
        private String Email;

        getWishlist(String email){
            Email = email;
        }
        @Override
        protected String doInBackground(String...params){
            String result = null;
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject();
                jsonObject.put("Email Address",Email);
                String serverURL = "http://192.168.60.27:8080/classifieds/webapi/resource/Wishlist";
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
                Log.d("Wish-List", "Some error has occurred at Server");
                Toast.makeText(getActivity(),"No posts to show",Toast.LENGTH_LONG).show();
            }

            else
            {
                Log.d("Services", "Displayed succesfully");
                //Log.d("Data", s);
                //Toast.makeText(getActivity(),"Saved to drafts",Toast.LENGTH_LONG).show()
                try {
                    String Title;
                    String Category;
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
                        Category = j.getString("Category");
                        email = j.getString("posted_by");
                        image = j.getString("image");
                        postedOn = j.getString("posted_on");
                        pd.add(new PostDetails(Title,Description,Category,Location,Mobile,iD,email,postedOn,true,image));
                    }
                    ArrayAdapter<PostDetails> adapter = new AllPostsArrayAdapter(getActivity(), 0, pd);
                    ListView listView = (ListView) getActivity().findViewById(R.id.customListView);
                    listView.setAdapter(adapter);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }


        }

        @Override
        protected void onCancelled() {
            mWishListTask = null;
        }
    }
}

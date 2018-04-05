package app.iiitb.com.classifieds;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.List;


/**
 * Created by URMILA on 16-Jul-17.
 */
//MyPosts,,,,Drafts
public class UserPostsArrayAdapter extends ArrayAdapter<PostDetails> {

    private Context context;
    private List<PostDetails> postDetailsList;
    private DataBaseAdapter  db;
    private deleteUserPost deleteTask;

    //constructor, call on creation
    public UserPostsArrayAdapter(Context context, int resource, ArrayList<PostDetails> objects) {
        super(context, resource, objects);

        this.context = context;
        this.postDetailsList = objects;
    }

    //called when rendering the list
    public View getView(final int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        db = new DataBaseAdapter(context);
        final PostDetails postDetails = postDetailsList.get(position);
        View view;
        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final boolean feature = postDetails.getFeatured();
        if(feature){
            view = inflater.inflate(R.layout.display_post, null);
        }else{
            view = inflater.inflate(R.layout.display_drafts, null);
        }
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView category = (TextView) view.findViewById(R.id.category);
        TextView location = (TextView) view.findViewById(R.id.location);
        TextView mobile = (TextView) view.findViewById(R.id.mobile);
        if(feature) {
            TextView postedOn = (TextView) view.findViewById(R.id.postedOn);
            postedOn.setText(postDetails.getPostedOn());
        }

        ImageView image = (ImageView) view.findViewById(R.id.image);

        //TODO:set onClick for buttons

        title.setText(postDetails.getTitle());
        description.setText(postDetails.getDescription());
        category.setText(postDetails.getCategory());
        location.setText(postDetails.getLocation());
        mobile.setText(postDetails.getMobile());
        String s = postDetails.getImg();
        if(!s.equals("")){
            Bitmap bm = getBitmapFromString(s);
            //get the image associated with this property
            image.setImageBitmap(bm);
        }
        else image.setImageResource(R.drawable.default_image);
        Button callButton = (Button) view.findViewById(R.id.call);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"+ postDetails.getMobile()));
                context.startActivity(i);
            }
        });

        Button shareButton = (Button) view.findViewById(R.id.share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                String text = postDetails.getTitle() + "\n" + postDetails.getDescription()+ "\n" + "Location: " + postDetails.getLocation() + "\n" +"Contact: " + postDetails.getMobile();
                ClipData clip = ClipData.newPlainText("Copied Details", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context,"Details copied to clipboard!",Toast.LENGTH_SHORT);
            }
        });

        Button editButton = (Button) view.findViewById(R.id.edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                if(!feature)  i = new Intent(context,editDraftActivity.class).putExtra("DraftId",postDetails.getId());
                else i = new Intent(context,editPostActivity.class).putExtra("PostId",postDetails.getId());
                context.startActivity(i);

            }
        });

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_post);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.open();
                if(feature){
                    db.deleteRowMyPosts(postDetails.getCategory(),postDetails.getId());
                    postDetailsList.remove(position);
                    notifyDataSetChanged();
                    deleteTask = new deleteUserPost(postDetails.getCategory(),postDetails.getId());
                    deleteTask.execute();
                }
                else{
                    db.deleteRowMyDrafts(postDetails.getId());
                    postDetailsList.remove(position);
                    notifyDataSetChanged();
                    deleteTask = new deleteUserPost("Drafts",postDetails.getId());
                    deleteTask.execute();
                }
            }
        });
        return view;
    }
    private Bitmap getBitmapFromString(String img) {
/*
* This Function converts the String back to Bitmap
* */
        byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }



    public class deleteUserPost extends AsyncTask<String, String,String> {

        private String category;
        private String id;


        deleteUserPost(String cat, String Id) {
            category = cat;
            id = Id;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject();
                jsonObject.put("Category", category);
                jsonObject.put("Id", id);
                String serverURL = "http://192.168.60.27:8080/classifieds/webapi/resource/deletePost";
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
                Log.d("DeletePost", "Data from the Server: " + line);
                result = line;
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                Log.d("DeleteActivity", "Some error has occurred at Server");
                Toast.makeText(context, "Could not delete", Toast.LENGTH_LONG).show();
            } else if (s.equals("fail")) {
                Log.e("DeleteActivity", "Failed Deleting");
                Toast.makeText(context, "Could not delete", Toast.LENGTH_LONG).show();
            } else {
                Log.d("DeleteActivity", "Deleted successfully");
                Toast.makeText(context, "Deleted Successfully!", Toast.LENGTH_LONG).show();
            }


        }

        @Override
        protected void onCancelled() {
            deleteTask = null;
        }
    }
}
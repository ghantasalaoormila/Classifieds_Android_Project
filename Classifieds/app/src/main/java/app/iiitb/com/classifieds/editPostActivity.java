package app.iiitb.com.classifieds;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

public class editPostActivity extends AppCompatActivity {

    private EditText mTitle;
    private EditText mDescription;
    private EditText mLocation;
    private EditText mMobile;
    private editPostTask mEditTask;
    public String mCategory;
    private ImageView viewImage;
    private Button upload;
    private DataBaseAdapter db;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        id = getIntent().getStringExtra("PostId");
        db = new DataBaseAdapter(this);
        db.open();
        Cursor c = db.getPost(id);
        c.moveToFirst();
        mTitle = (EditText) findViewById(R.id.title);
        mDescription = (EditText) findViewById(R.id.description);
        mLocation = (EditText) findViewById(R.id.location);
        mMobile = (EditText) findViewById(R.id.mobile);
        viewImage = (ImageView) findViewById(R.id.imageView);
        mTitle.setText(c.getString(0));
        mDescription.setText(c.getString(1));
        mLocation.setText(c.getString(4));
        mMobile.setText(c.getString(6));
        String s = c.getString(7);
        if (!s.equals("")) {
            Bitmap bm = getBitmapFromString(s);
            //get the image associated with this property
            viewImage.setImageBitmap(bm);
        }
        //else viewImage.setImageResource(R.drawable.default_image);
        upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }

        });


        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner Drop down elements
        final List<String> categories = new ArrayList<String>();
        categories.add("Services");
        categories.add("Lifestyle");
        categories.add("Jobs");
        categories.add("Events");
        categories.add("Electronics");
        categories.add("Education");
        categories.add("Vehicles");
        categories.add("Others");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // On selecting a spinner item
                mCategory = adapterView.getItemAtPosition(position).toString();

                // Showing selected spinner item
                // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(editPostActivity.this, "Please select a category", Toast.LENGTH_SHORT).show();
            }
        });


        Button mPostButton = (Button) findViewById(R.id.post);
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptPost();
            }
        });

    }

    private Bitmap getBitmapFromString(String img) {
/*
* This Function converts the String back to Bitmap
* */
        byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(editPostActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 1);
                    }

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }

        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                viewImage.setImageBitmap(imageBitmap);


            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                viewImage.setImageBitmap(thumbnail);

            }

        }

    }

    @Override
    public void onBackPressed() {
        /*Intent i = new Intent(editDraftActivity.this, HomeActivity.class);
        startActivity(i);
        finish();*/
        super.onBackPressed();
    }


    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

    public void attemptPost() {
        View focusView = null;
        boolean cancel = false;
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String location = mLocation.getText().toString();
        String mobile = mMobile.getText().toString();
        String image;
        if(hasImage(viewImage)) {
            Bitmap bm = ((BitmapDrawable) viewImage.getDrawable()).getBitmap();
            image = getStringFromBitmap(bm);
        }
        else image = "";
        if (TextUtils.isEmpty(title)) {
            mTitle.setError("This field is required");
            focusView = mTitle;
            cancel = true;
        }
        else if(TextUtils.isEmpty(description)) {
            mDescription.setError("This field is required");
            focusView = mDescription;
            cancel = true;
        }
        else if (TextUtils.isEmpty(location)) {
            mLocation.setError("This field is required");
            focusView = mLocation;
            cancel = true;
        }
        else if(TextUtils.isEmpty(mobile)){
            mMobile.setError("This field is required");
            focusView = mMobile;
            cancel = true;
        }
        if(cancel){
            focusView.requestFocus();
        }
        else {
            SessionManager session = new SessionManager(getApplicationContext());
            mEditTask = new editPostTask(session.getEmail(), title, mCategory, description, location, mobile,image);
            mEditTask.execute();
        }
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
 /*
 * This functions converts Bitmap picture to a string which can be
 * JSONified.
 * */
        final int COMPRESSION_QUALITY = 50;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public class editPostTask extends AsyncTask<String, String, String> {

        private String Email;
        private String Title;
        private String Category;
        private String Description;
        private String Location;
        private String Mobile;
        private String image;

        editPostTask(String email, String title, String category, String description, String location, String mobile, String img) {
            Email = email;
            Title = title;
            Category = category;
            Description = description;
            Location = location;
            Mobile = mobile;
            image = img;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject();
                jsonObject.put("Id", id);
                jsonObject.put("Email Address", Email);
                jsonObject.put("Title", Title);
                jsonObject.put("Category", Category);
                jsonObject.put("Description", Description);
                jsonObject.put("Location", Location);
                jsonObject.put("Mobile Number", Mobile);
                jsonObject.put("image", image);
                String serverURL = "http://192.168.60.27:8080/classifieds/webapi/resource/EditPost";
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
                Log.d("PostActivity", "Some error has occurred at Server");
                Toast.makeText(editPostActivity.this, "Could not post!", Toast.LENGTH_LONG).show();
            } else if (s.equals("fail")) {
                Log.e("PostActivity", "Failed Posting");
                Toast.makeText(editPostActivity.this, "Could not post!", Toast.LENGTH_LONG).show();
            } else {
                Log.d("PostActivity", "Posted successfully");
                Toast.makeText(editPostActivity.this, "Posted Successfully!", Toast.LENGTH_LONG).show();
                Date date = new Date();
                db.open();
                db.deleteRowMyPosts(Category, id);
                db.InsertmyPostsData(Title, Description, Category, Location, date.toString(), id, Mobile, image);
                db.close();
                Intent i =new Intent(editPostActivity.this,HomeActivity.class);
                startActivity(i);
                finish();
            }


        }

        @Override
        protected void onCancelled() {
            mEditTask = null;
        }
    }
}

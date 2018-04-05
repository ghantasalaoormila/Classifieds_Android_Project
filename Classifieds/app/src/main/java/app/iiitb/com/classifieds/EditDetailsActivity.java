package app.iiitb.com.classifieds;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;

public class EditDetailsActivity extends AppCompatActivity {

    private EditText mNameView;
    private EditText mAgeView;
    private EditText mGenderView;
    private EditText mEmailView;
    private EditText mMobileView;
    private editUserDetailsTask editDetailsTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager session = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_edit_details);
        HashMap<String, String> details = new HashMap<String, String>();
        details = session.getUserDetails();
        mNameView = (EditText) findViewById(R.id.edit_name);
        mNameView.setText(details.get("name"));
        mAgeView = (EditText) findViewById(R.id.edit_age);
        mAgeView.setText(details.get("age"));
        mGenderView = (EditText) findViewById(R.id.edit_gender);
        mGenderView.setText(details.get("gender"));
        mEmailView = (EditText) findViewById(R.id.edit_email);
        mEmailView.setText(details.get("email"));
        mMobileView = (EditText) findViewById(R.id.edit_mobile);
        mMobileView.setText(details.get("contact_number"));
        Button mSaveButton = (Button) findViewById(R.id.save_changes);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetails();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(EditDetailsActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    public void saveDetails(){
        View focusView = null;
        boolean cancel = false;
        String name = mNameView.getText().toString();
        String age = mAgeView.getText().toString();
        String gender = mGenderView.getText().toString();
        String email = mEmailView.getText().toString();
        String mobile = mMobileView.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mNameView.setError("This field is required");
            focusView = mNameView;
            cancel = true;
        }
        else if(TextUtils.isEmpty(age)) {
            mAgeView.setError("This field is required");
            focusView = mAgeView;
            cancel = true;
        }
        else if (TextUtils.isEmpty(gender)) {
            mGenderView.setError("This field is required");
            focusView = mGenderView;
            cancel = true;
        }
        else if(TextUtils.isEmpty(email)){
            mEmailView.setError("This field is required");
            focusView = mEmailView;
            cancel = true;
        }
        else if(TextUtils.isEmpty(mobile)){
            mMobileView.setError("This field is required");
            focusView = mMobileView;
            cancel = true;
        }
        if(cancel){
            focusView.requestFocus();
        }
        else {
            SessionManager session = new SessionManager(getApplicationContext());
            session.editUserDetails(name,age,gender,email,mobile);
            editDetailsTask = new editUserDetailsTask(name,age,gender,email,mobile);
            editDetailsTask.execute();
        }
    }

    public class editUserDetailsTask extends AsyncTask<String,String,String>{

        private String Name;
        private String age;
        private String gender;
        private String email;
        private String mobile;
        editUserDetailsTask(String n, String a, String g, String e, String m){
            Name = n;
            age = a;
            gender = g;
            email = e;
            mobile = m;
        }
        @Override
        protected String doInBackground(String...params){
            String result = null;
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject();
                jsonObject.put("Name", Name);
                jsonObject.put("Age", age);
                jsonObject.put("Gender", gender);
                jsonObject.put("Email Address", email);
                jsonObject.put("Mobile Number", mobile);
                String serverURL = "http://192.168.60.27:8080/classifieds/webapi/resource/EditDetails";
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
                Log.d("EditDetailsActivity", "Data from the Server: " + line);
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
                Log.d("EditDetailsActivity", "Some error has occurred at Server");
                Toast.makeText(EditDetailsActivity.this,"failed to Edit",Toast.LENGTH_LONG).show();
            }
            else if(s.equals("success"))
            {
                Log.d("EditDetailsActivity", "Registration success");
                Toast.makeText(EditDetailsActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(EditDetailsActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
            else if(s.equals("fail"))
            {
                Log.e("EditDetailsActivity", "Edit Details failed");
                Toast.makeText(EditDetailsActivity.this,"Could not save changes",Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            editDetailsTask = null;
        }
    }
}


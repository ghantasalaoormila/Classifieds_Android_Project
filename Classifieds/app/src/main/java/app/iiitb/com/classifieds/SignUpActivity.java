package app.iiitb.com.classifieds;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

    private UserSignUpTask mSignUptask;

    private EditText mNameView;
    private EditText mAgeView;
    private EditText mGenderView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfPasswordView;
    private EditText mMobileView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //setup the sign up form
        mNameView = (EditText) findViewById(R.id.name);
        mAgeView = (EditText) findViewById(R.id.age);
        mGenderView = (EditText) findViewById(R.id.gender);
        mEmailView = (EditText) findViewById(R.id.email_register);
        mPasswordView = (EditText) findViewById(R.id.password_register);
        mConfPasswordView = (EditText) findViewById(R.id.conf_password);
        mMobileView = (EditText) findViewById(R.id.mobile_no);
        View focusView = mNameView;
        focusView.requestFocus();
        Button mNextButton = (Button) findViewById(R.id.next);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttemptSignUp();
            }
        });
    }

    public void AttemptSignUp(){
        //if(mSignUptask != null)  return;
        mNameView.setError(null);
        mAgeView.setError(null);
        mGenderView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfPasswordView.setError(null);
        mMobileView.setError(null);
        View focusView = null;
        String name = mNameView.getText().toString();
        String age = mAgeView.getText().toString();
        String email = mEmailView.getText().toString();
        String gender=  mGenderView.getText().toString();
        String mobile = mMobileView.getText().toString();
        String pass = mPasswordView.getText().toString();
        String aadhaar = getIntent().getStringExtra("aadhaarNumber");
        String conf_pass = mConfPasswordView.getText().toString();
        if(!pass.equals(conf_pass)){
            Toast.makeText(SignUpActivity.this,"Your Password and Confirmation Password do not match",Toast.LENGTH_LONG).show();
            focusView = mPasswordView;
            focusView.requestFocus();
        }
        else {
            mSignUptask = new UserSignUpTask(name,age,gender,email,pass,aadhaar,mobile);
            mSignUptask.execute();
            finish();
        }
    }

    public class UserSignUpTask extends AsyncTask<String, String, String> {

        private final String mName;
        private final String mAge;
        private final String mGender;
        private final String mEmail;
        private final String mPassword;
        private final String mAadhaar;
        private final String mMobile;
        UserSignUpTask(String name, String age, String gender, String email, String password, String aadhaar, String mobile) {
            mName = name;
            mAge = age;
            mGender = gender;
            mEmail = email;
            mAadhaar = aadhaar;
            mMobile = mobile;
            mPassword = password;
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String result = null;
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject();
                jsonObject.put("Name", mName);
                jsonObject.put("Age", mAge);
                jsonObject.put("Gender", mGender);
                jsonObject.put("Password", mPassword);
                jsonObject.put("Email Address", mEmail);
                jsonObject.put("Aadhaar Number", mAadhaar);
                jsonObject.put("MobileNumber", mMobile);
                String serverURL = "http://192.168.60.27:8080/classifieds/webapi/resource/Register";
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
                Log.d("LoginActivity", "Data from the Server: " + line);
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
            //mAuthTask = null;
            if(s == null)
            {
                Log.d("SignUpActivity", "Some error has occurred at Server");
                Toast.makeText(SignUpActivity.this,"failed to register",Toast.LENGTH_LONG).show();
            }
            else if(s.equals("success"))
            {
                Log.d("SignUpActivity", "Registration success");
                Toast.makeText(SignUpActivity.this,"Successfully registered!",Toast.LENGTH_LONG).show();
                SessionManager session = new SessionManager(getApplicationContext());
                session.createLoginSession(mName,mAge,mGender,mEmail,mPassword,mAadhaar,mMobile);
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
            else if(s.equals("in_use"))
            {
                Log.e("SignUpActivity", "User already exists");
                Toast.makeText(SignUpActivity.this,"Email already in use",Toast.LENGTH_LONG).show();
            }
            else
            {
                Log.e("LoginActivity", "Data from Server: " + s);
                Toast.makeText(SignUpActivity.this,"Failed to register",Toast.LENGTH_LONG).show();
            }


        }

        @Override
        protected void onCancelled() {
            mSignUptask = null;
        }
    }
}



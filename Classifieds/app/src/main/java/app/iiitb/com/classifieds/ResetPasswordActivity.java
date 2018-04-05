package app.iiitb.com.classifieds;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText password;
    private EditText confPassword;
    private updatePasswordTask updateTask;
    private getUserDetailsTask mDetailsTask;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        password = (EditText) findViewById(R.id.new_pass);
        confPassword =(EditText) findViewById(R.id.conf_new);
        Button okButton = (Button) findViewById(R.id.ok_set);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerifyPassword();
            }
        });
    }
    public void VerifyPassword(){
        View focusView = null;
        String pass = password.getText().toString();
        String conf_pass = confPassword.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            password.setError("This field is required");
            focusView = password;
            focusView.requestFocus();
        }
        else if (TextUtils.isEmpty(conf_pass)) {
            confPassword.setError("This field is required");
            focusView = confPassword;
            focusView.requestFocus();
        }
        else if(pass.equals(conf_pass)){
            updatePassword(pass);
        }
        else{
            Toast.makeText(ResetPasswordActivity.this,"Your Password and Confirmation Password do not match",Toast.LENGTH_LONG).show();
        }
    }

    public void updatePassword(String password){
        String e = getIntent().getStringExtra("Email");
        updateTask = new updatePasswordTask(password,e);
        updateTask.execute();
    }

    public class updatePasswordTask extends AsyncTask<String,String,String>{

        private String password;
        private String email;

        updatePasswordTask(String p, String e){
            password = p;
            email = e;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String result = null;
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("Password", password);
                jsonObject.put("Email Address", email);
                String serverURL = "http://192.168.60.27:8080/classifieds/webapi/resource/resetPassword";
                URL url = new URL(serverURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
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
                Log.d("ResetPasswordActivity", "Data from the Server: " + line);
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
                Log.d("LoginActivity", "Some error has occurred at Server");
                Toast.makeText(ResetPasswordActivity.this, "Reset password failed!", Toast.LENGTH_LONG).show();
            } else if (s.equals("success")) {
                Log.d("LoginActivity", "Password changed successfully");
                Toast.makeText(ResetPasswordActivity.this, "Password changed successfully", Toast.LENGTH_LONG).show();
                proceedToNextActivity(email);
            } else if (s.equals("fail")) {
                Toast.makeText(ResetPasswordActivity.this,"Reset Password failed!", Toast.LENGTH_LONG).show();
            }


        }

        @Override
        protected void onCancelled() {
            updateTask = null;
        }
    }

    public void proceedToNextActivity(String email){
        session = new SessionManager(getApplicationContext());
        mDetailsTask = new getUserDetailsTask(email);
        mDetailsTask.execute();

    }

    public class getUserDetailsTask extends AsyncTask<String, String, String>{

        public String Email;

        getUserDetailsTask(String email){
            Email = email;
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String result = null;
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject();
                jsonObject.put("Email Address", Email);
                String serverURL = "http://192.168.60.27:8080/classifieds/webapi/resource/userDetails";
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
                if(result!=null) {
                    try {
                        JSONObject Object = new JSONObject(result);
                        String name = Object.getString("Name");
                        String age = Object.getString("Age");
                        String gender = Object.getString("Gender");
                        String password = Object.getString("Password");
                        String aadhaar = Object.getString("Aadhaar Number");
                        String contact_number = Object.getString("MobileNumber");
                        session.createLoginSession(name,age,gender,Email,password,aadhaar,contact_number);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
                Log.d("LoginActivity", "Some error has occurred at Server failed to retrieve user details");

            }
            else{
                Log.d("LoginActivity","Successfully retrieved user details");
                Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                startActivity(intent);

                finish();
            }

        }

        @Override
        protected void onCancelled() {
            mDetailsTask = null;
        }

    }
}

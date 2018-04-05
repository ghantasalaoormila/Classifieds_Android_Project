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
import java.util.Random;

public class ForgotPassActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mOtp;
    private sendOtpTask mOtpVerifyTask;
    private String otp_sent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        mEmail = (EditText) findViewById(R.id.email);
        mOtp = (EditText) findViewById(R.id.otp);
        View focusView = mEmail;
        focusView.requestFocus();

        Button sendOtpButton = (Button) findViewById(R.id.send_otp);
        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp_sent=sendOtp();
            }
        });
        Button okButton = (Button) findViewById(R.id.ok_otp);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttemptVerify();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(ForgotPassActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
    public String sendOtp(){
        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("This field is required");
            return null;
        }

        Random rand = new Random();
        String generateOtp = String.format("%04d", rand.nextInt(10000));
        mOtpVerifyTask = new sendOtpTask(generateOtp, mEmail.getText().toString());
        mOtpVerifyTask.execute();
        return generateOtp;
    }
    public void AttemptVerify(){
        View focusView = null;
        String enteredOtp = mOtp.getText().toString();
        if (TextUtils.isEmpty(enteredOtp)) {
            mOtp.setError("This field is required");
        }
        else if (otp_sent.equals(enteredOtp)) {
            Toast.makeText(ForgotPassActivity.this, "OTP verified!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ForgotPassActivity.this, ResetPasswordActivity.class).putExtra("Email",mEmail.getText().toString());
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(ForgotPassActivity.this, "OTP incorrect. Please re-enter", Toast.LENGTH_LONG).show();
            focusView = mOtp;
            focusView.requestFocus();
        }
    }

    public class sendOtpTask extends AsyncTask<String,String,String>{

        String mOtp;
        String mEmail;

        sendOtpTask(String otp, String email){
            mOtp = otp;
            mEmail = email;
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String result = null;
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("otp", mOtp);
                jsonObject.put("Email Address", mEmail);

                String serverURL = "http://192.168.60.27:8080/classifieds/webapi/resource/forgotPassword";
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
                Log.d("ForgotPassActivity", "Data from the Server: " + line);
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
                Log.d("ForgotPassActivity", "Some error has occurred at Server");
                Toast.makeText(ForgotPassActivity.this, "OTP verification failed!", Toast.LENGTH_LONG).show();
            } else if (s.equals("success")) {
                Log.d("ForgotPassActivity", "User OTP sent successfully");
                Toast.makeText(ForgotPassActivity.this, "OTP sent", Toast.LENGTH_LONG).show();
            } else if (s.equals("fail")) {
                Toast.makeText(ForgotPassActivity.this, "Invalid number. Re-check and enter", Toast.LENGTH_LONG).show();
            }


        }

        @Override
        protected void onCancelled() {
            mOtpVerifyTask = null;
        }
    }
}

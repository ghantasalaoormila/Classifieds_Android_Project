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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

public class AadhaarAuthActivity extends AppCompatActivity {

    private otpVerifyTask mOtpVerifyTask;
    private EditText mOtpView;
    private EditText aadhaarNumber;
    private String otp_sent;
    private boolean send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aadhaar_auth);
        mOtpView = (EditText) findViewById(R.id.otpInput);
        send = false;
        aadhaarNumber = (EditText) findViewById(R.id.aadhaar_number);
        View focusView = aadhaarNumber;
        focusView.requestFocus();
        Button mSendOtpButton = (Button) findViewById(R.id.Send_otp);
        mSendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp_sent = sendOtp();
                send = true;
            }
        });
        Button mResendButton = (Button) findViewById(R.id.Resend_otp);
        mResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (send) otp_sent = sendOtp();
            }
        });

        Button mOkButton = (Button) findViewById(R.id.otp_ok);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (send) AttemptOtpVerify();
            }
        });
    }

    public void AttemptOtpVerify() {
        View focusView = null;
        String enteredOtp = mOtpView.getText().toString();
        if (otp_sent.equals(enteredOtp)) {
            Toast.makeText(AadhaarAuthActivity.this, "OTP verified!", Toast.LENGTH_LONG).show();
            String aadhaar = aadhaarNumber.getText().toString();
            Intent intent = new Intent(AadhaarAuthActivity.this, SignUpActivity.class).putExtra("aadhaarNumber",aadhaar);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(AadhaarAuthActivity.this, "OTP incorrect. Please re-enter", Toast.LENGTH_LONG).show();
            focusView = mOtpView;
            focusView.requestFocus();
        }
    }

    public String sendOtp() {
        mOtpView.setError(null);
        Random rand = new Random();
        String generateOtp = String.format("%04d", rand.nextInt(10000));
        String aadhaar_number = aadhaarNumber.getText().toString();
        mOtpVerifyTask = new otpVerifyTask(generateOtp, aadhaar_number);
        mOtpVerifyTask.execute();
        return generateOtp;
    }


    private class otpVerifyTask extends AsyncTask<String, String, String> {

        private final String mOtp;
        private final String mAadhaar;

        otpVerifyTask(String otp, String mobile) {
            mOtp = otp;
            mAadhaar = mobile;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String result = null;
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("otp", mOtp);
                jsonObject.put("aadhaar", mAadhaar);
                String serverURL = "http://192.168.60.27:8080/classifieds/webapi/aadhaar/authentication";
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
                Log.d("AadhaarAuthActivity", "Data from the Server: " + line);
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
                Toast.makeText(AadhaarAuthActivity.this, "OTP verification failed!", Toast.LENGTH_LONG).show();
            } else if (s.equals("success")) {
                Log.d("LoginActivity", "User OTP sent successfully");
                Toast.makeText(AadhaarAuthActivity.this, "OTP sent", Toast.LENGTH_LONG).show();
            } else if (s.equals("fail")) {
                Toast.makeText(AadhaarAuthActivity.this, "Invalid number. Re-check and enter", Toast.LENGTH_LONG).show();
            }


        }

        @Override
        protected void onCancelled() {
            mOtpVerifyTask = null;
        }
    }
}
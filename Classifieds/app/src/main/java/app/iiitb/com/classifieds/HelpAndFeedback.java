package app.iiitb.com.classifieds;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Date;


/**
 * Created by URMILA on 09-Jul-17.
 */

public class HelpAndFeedback extends Fragment {
    private EditText text;
    private Button sendButton;
    private sendFeedbackTask mSendTask;
    private SessionManager session;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.help_and_feedback, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Help and Feedback");
        session = new SessionManager(view.getContext());
        text = (EditText) view.findViewById(R.id.complaint);
        sendButton = (Button) view.findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFeedback();
            }
        });
    }

    public void sendFeedback(){
        String s = text.getText().toString();
        View focusView=null;
        boolean c = false;
        if(TextUtils.isEmpty(s)){
            text.setError("This field is required");
            focusView = text;
            focusView.requestFocus();
            c=true;
        }
        if(!c){
            mSendTask = new sendFeedbackTask(session.getEmail(),s);
            mSendTask.execute();
        }
    }

    public class sendFeedbackTask extends AsyncTask<String, String, String> {

        private String Email;
        private String Description;

        sendFeedbackTask(String email, String description) {
            Email = email;
            Description = description;

        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject();
                jsonObject.put("Email Address", Email);
                jsonObject.put("Text", Description);
                String serverURL = "http://192.168.60.27:8080/classifieds/webapi/resource/HelpAndFeedback";
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
                Log.d("HelpandFeedback", "Data from the Server: " + line);
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
                Toast.makeText(getActivity(), "Could not post!", Toast.LENGTH_LONG).show();
            } else if (s.equals("fail")) {
                Log.e("PostActivity", "Failed Posting");
                Toast.makeText(getActivity(), "Could not post!", Toast.LENGTH_LONG).show();
            } else {
                Log.d("Help and Feedback", "Posted successfully");
                Toast.makeText(getActivity(), "Posted Successfully!", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getActivity(),HomeActivity.class);
                startActivity(i);
                getActivity().finish();
            }


        }

        @Override
        protected void onCancelled() {
            mSendTask = null;
        }
    }
}

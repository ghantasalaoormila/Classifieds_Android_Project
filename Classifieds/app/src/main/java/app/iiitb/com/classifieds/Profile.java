package app.iiitb.com.classifieds;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;


/**
 * Created by URMILA on 09-Jul-17.
 */

public class Profile extends Fragment {

    SessionManager session;
    private TextView mName;
    private TextView mEmail;
    private TextView mAge;
    private TextView mGender;
    private TextView mAadhaar;
    private TextView mMobile;
    private Button mEditButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.profile, container, false);
        session = new SessionManager(getActivity());
        HashMap<String,String> userProfile = new HashMap<>();
        userProfile = session.getUserDetails();
        mName = (TextView) view.findViewById(R.id.name_edit);
        mEmail = (TextView) view.findViewById(R.id.email_edit);
        mAge = (TextView) view.findViewById(R.id.age_edit);
        mGender = (TextView) view.findViewById(R.id.gender_edit);
        mAadhaar = (TextView) view.findViewById(R.id.aadhaar_edit);
        mMobile = (TextView) view.findViewById(R.id.mobile_edit);
        mName.setText(userProfile.get("name"));
        mEmail.setText(userProfile.get("email"));
        mAge.setText(userProfile.get("age"));
        mGender.setText(userProfile.get("gender"));
        mAadhaar.setText(userProfile.get("aadhaar"));
        mMobile.setText(userProfile.get("contact_number"));
        mEditButton = (Button) view.findViewById(R.id.edit_details);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDetails();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Profile");
    }

    public void editDetails(){
        Intent i = new Intent(getActivity(),EditDetailsActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}


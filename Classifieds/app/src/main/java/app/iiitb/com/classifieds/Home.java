package app.iiitb.com.classifieds;

import android.app.Fragment;
import android.app.usage.UsageEvents;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Created by URMILA on 09-Jul-17.
 */

public class Home extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home, container, false);

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");
        Button mPostButton = (Button) view.findViewById(R.id.post);
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostAdv();
            }
        });

        Button mServicesButton = (Button) view.findViewById(R.id.Services);
        mServicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Services.class);
                startActivity(i);
            }
        });

        Button mLifeStyleButton = (Button) view.findViewById(R.id.lifestyle);
        mLifeStyleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Lifestyle.class);
                startActivity(i);
            }
        });

        Button mJobsButton = (Button) view.findViewById(R.id.Jobs);
        mJobsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Jobs.class);
                startActivity(i);
            }
        });

        Button mEventsButton = (Button) view.findViewById(R.id.Events);
        mEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Events.class);
                startActivity(i);
            }
        });

        Button mElectronicsButton = (Button) view.findViewById(R.id.electronics);
        mElectronicsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Electronics.class);
                startActivity(i);
            }
        });

        Button mEducationButton = (Button) view.findViewById(R.id.education);
        mEducationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Education.class);
                startActivity(i);
            }
        });

        Button mVehiclesButton = (Button) view.findViewById(R.id.vehicles);
        mVehiclesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Vehicles.class);
                startActivity(i);
            }
        });

        Button mOthersButton = (Button) view.findViewById(R.id.others);
        mOthersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Others.class);
                startActivity(i);
            }
        });

    }

    public void PostAdv(){
        Intent i = new Intent(getActivity(), PostActivity.class);
        startActivity(i);
       // getActivity().finish();
    }
}

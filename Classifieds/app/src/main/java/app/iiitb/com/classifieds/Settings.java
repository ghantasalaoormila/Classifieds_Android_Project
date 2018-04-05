package app.iiitb.com.classifieds;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Created by URMILA on 09-Jul-17.
 */

public class Settings extends Fragment {

    private Button signOut;
    private DataBaseAdapter db;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Settings");
        db = new DataBaseAdapter(view.getContext());
        session= new SessionManager(view.getContext());
        signOut = (Button) view.findViewById(R.id.sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.open();
                db.deleteTables("myPosts","myDrafts");
                db.close();
                session.logoutUser();
                getActivity().finish();
            }
        });
    }
}

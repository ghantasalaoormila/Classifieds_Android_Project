package app.iiitb.com.classifieds;

/**
 * Created by URMILA on 12-Jul-17.
 */

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "UserDetailsPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static  final String KEY_NAME = "name";
    // User name (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public static final String KEY_AGE = "age";

    public static final String KEY_GENDER = "gender";

    public static final String KEY_AADHAAR = "aadhaar";

    public static final String KEY_CONTACT = "contact_number";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */

    public void createLoginSession(String name, String age, String gender, String email, String password, String aadhaar, String contact){

        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref
        editor.putString(KEY_NAME,name);
        editor.putString(KEY_AGE,age);
        editor.putString(KEY_GENDER,gender);
        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_AADHAAR,aadhaar);
        editor.putString(KEY_CONTACT, contact);

        // commit changes
        editor.commit();
    }


    /**
     * Get stored session data
     * */
    public String getEmail(){
        return pref.getString(KEY_EMAIL,null);
    }

    public void editUserDetails(String name, String age, String gender, String email, String mobile){
        editor.putString(KEY_NAME,name);
        editor.putString(KEY_AGE,age);
        editor.putString(KEY_GENDER,gender);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_CONTACT,mobile);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME,pref.getString(KEY_NAME,null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user email id
        user.put(KEY_AGE, pref.getString(KEY_AGE, null));
        user.put(KEY_GENDER,pref.getString(KEY_GENDER,null));
        user.put(KEY_AADHAAR,pref.getString(KEY_AADHAAR,null));
        user.put(KEY_CONTACT,pref.getString(KEY_CONTACT,null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
package app.iiitb.com.classifieds;

/**
 * Created by URMILA on 15-Jul-17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BistroDB";

    private static final int DATABASE_VERSION =1;

    // Database creation sql statement
    public static final String my_posts= "create table myPosts (title text, description text, category text, postedOn text, location text, id text, mobile text, image text);";
    public static final String my_drafts= "create table myDrafts (title text, description text, category text, location text, id text, mobile text, image text);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(my_posts);
        database.execSQL(my_drafts);

    }

    // Method is called during an upgrade of the database, e.g. if you increase
    // the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.d(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        //database.execSQL("DROP TABLE IF EXISTS table1");
        //database.execSQL("DROP TABLE IF EXISTS table2");

        //onCreate(database);
    }


    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }

}
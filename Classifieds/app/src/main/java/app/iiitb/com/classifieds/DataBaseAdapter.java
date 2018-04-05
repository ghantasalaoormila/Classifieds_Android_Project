package app.iiitb.com.classifieds;

/**
 * Created by URMILA on 15-Jul-17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseAdapter {

    // Database fields

    private Context context;
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public DataBaseAdapter(Context context) {
        this.context = context;
    }

    public DataBaseAdapter open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public Cursor fetchAllmyPostsdata() {
        Cursor resultSet = database.rawQuery("Select * from myPosts",null);
        return resultSet;
    }

    public Cursor fetchAllmyDraftsdata() {
        Cursor resultSet = database.rawQuery("Select * from myDrafts",null);
        return resultSet;
    }

    public void deleteTables(String t1,String t2){
        database.execSQL("drop table if exists "+t1+';');
        database.execSQL("drop table if exists "+t2+';');
        dbHelper.deleteDatabase(this.context);
    }
    public void createIndividualTable(String query){
        database.execSQL(query);
    }


    public void InsertmyPostsData(String title, String description, String category, String location, String postedOn, String id, String mobile,String img) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("Category", category);
        values.put("location", location);
        values.put("postedOn", postedOn);
        values.put("id",id );
        values.put("mobile", mobile);
        values.put("image", img);

        database.insert("myPosts", null, values);

    }

    public void InsertmyDraftsData(String title, String description,String category, String location, String id, String mobile, String img) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("Category", category);
        values.put("location", location);
        values.put("id",id );
        values.put("mobile", mobile);
        values.put("image", img);

        database.insert("myDrafts", null, values);

    }



    /*public ContentValues createContentValues(String category, String summary,
                                             String description) {
        ContentValues values = new ContentValues();

        return values;
    }*/

    public Cursor getDraft(String Id){
            return database.rawQuery("Select * from myDrafts where id = "+Id,null);
    }

    public Cursor getPost(String Id){
        return database.rawQuery("Select * from myPosts where id = "+Id,null);
    }

    public void deleteRowMyPosts(String category, String id){
        database.delete("myPosts","category = ? AND id = ?", new String[]{category,id+""});
    }
    public void deleteRowMyDrafts(String id){
        database.delete("myDrafts","id = ?", new String[]{id+""});
    }
}
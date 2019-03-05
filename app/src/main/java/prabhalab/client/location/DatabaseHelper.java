package prabhalab.client.location;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Database helper class holds the database functionality
 * .i.e., Database creation, table creation and database version upgrade are done.
 *
 * @author Jeevanandhan
 */
public class DatabaseHelper extends SQLiteOpenHelper {



    static final String DB_NAME = "my_location.db";

    //DataBase Version..
    public static final int DATABASE_VERSION = 3;


    public static final String TABLE_NAME = "location_detail";

    public static final String location_id = "location_id";
    public static final String latlng = "latlng";
    public static final String address = "address";
    public static final String update_date = "update_date";
    public static final String modified_date = "modified_date";
    public static final String timeMillSec = "timeMillSec";

    public static final String orderId = "order_id";
    public static final String speed = "speed";
    public static final String user_id = "user_id";




    //User Details table create query...
    final private String locationDetails = "create table if not exists "
            + TABLE_NAME
            + " ( location_id integer primary key , "
            + latlng + " text, "
            + address + " text, "
            + update_date + " text, "
            + modified_date+ " text, "
            + orderId+ " text, "
            + speed+ " text, "
            + user_id+ " text, "
            + timeMillSec + " text); ";




    //Single ton object...
    private static DatabaseHelper databaseHelper = null;

    //Single ton method...
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (databaseHelper != null) {
            return databaseHelper;
        } else {
            databaseHelper = new DatabaseHelper(context);
            return databaseHelper;
        }
    }

    //Database helper class constructor...
    public DatabaseHelper(Context context) {
        super(context, context.getExternalFilesDir(DB_NAME)
                .getAbsolutePath() + File.separator + DB_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called Once the data base is created
     * .i.e., when the app is installed.
     *
     * @param db
     */

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            System.out.println("DB:: OnCreate Called before!");

            db.execSQL(locationDetails);


        }
        catch (Exception e)
        {
            System.out.println("DB::Exception in Database onCreate "+e.getMessage());
        }
        //if(db.getVersion() == 0 || db.getVersion()
    }



    /**
     * Called when the app is updated.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(newVersion > oldVersion)
            onCreate(db);

        /*switch (newVersion)
        {
            case 2:
                *//**
                 * Database versioning is being done, for creating the new table (UserDetails).
                 *//*
                onCreate(db);
                break;
            case 3:
                *//**
                 * Database versioning. No new tables are created.
                 *//*
                onCreate(db);
                break;
            case 23://\This to be changed whenever releasing
                try
                {
                    db.execSQL("ALTER TABLE JrTable ADD COLUMN " + ColumnKey.Orderno + " TEXT");
                }
                catch (Exception e)
                {
                    Utility.getInstance().DumpError("Exception in upgrading DB Version "+e.getMessage());
                }
                break;
        }*/

    }
}

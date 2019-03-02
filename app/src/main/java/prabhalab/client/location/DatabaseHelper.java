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

    // Database Name...
    public static final String DATABASE_NAME = "location.db";
    public static final String LocationDetails = "location_details";

    //DataBase Version..
    public static final int DATABASE_VERSION = 23;

    public interface ColumnKey {
        // Table name...




        //String Sno = "Sno";
        //\17Oct16. Adding column call order number. This is to maintain storejourney request order number in db.
        String Orderno = "Orderno";


        String locationId = "location_id";
        String latlng = "lat_lng";
        String address = "address";
        String updateDate = "update_date";
        String modifiedDate = "modified_date";
        String timeMillSec = "timeMillSec";


    }

    //User Details table create query...
    final private String locationDetails = "create table if not exists "
            + LocationDetails
            + " ( id integer primary key , "
            + ColumnKey.latlng + " text, "
            + ColumnKey.address + " text, "
            + ColumnKey.updateDate + " text, "
            + ColumnKey.modifiedDate + " text, "
            + ColumnKey.timeMillSec + " text); ";




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
        super(context, context.getExternalFilesDir(DATABASE_NAME)
                .getAbsolutePath() + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
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




            //getReadableDatabase();

            System.out.println("DB:: OnCreate Called after!");

            //\30Nov16. Checking adding Column name Orderno if not available
            if(!isOrderNoColumnAvailable(db))
                db.execSQL("ALTER TABLE JrTable ADD COLUMN " + ColumnKey.Orderno + " TEXT");
            else
                System.out.println("DB::Order number is available!");
        }
        catch (Exception e)
        {
            System.out.println("DB::Exception in Database onCreate "+e.getMessage());
        }
        //if(db.getVersion() == 0 || db.getVersion()
    }

    private boolean isOrderNoColumnAvailable(SQLiteDatabase db)
    {
        try
        {
            Cursor cursor = db.rawQuery("select * from "+ LocationDetails,null);
            if(cursor != null)
            {
                if(cursor.getColumnIndex(ColumnKey.Orderno) != -1)
                    return true;
            }
            else
            {
                System.out.println("DB::Cursor is null");
            }
            cursor.close();
        }
        catch (Exception e)
        {
            System.out.println("DB::Exception in isOrderNoColumnAvailable "+e.getMessage());
        }
        return  false;
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

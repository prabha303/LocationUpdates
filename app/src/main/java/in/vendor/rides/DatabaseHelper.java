package in.vendor.rides;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import static in.vendor.rides.Utility.AppData.user_id;

/**
 * Database helper class holds the database functionality
 * .i.e., Database creation, table creation and database version upgrade are done.
 * Created by prabha on 05/03/2019.
 */

public class DatabaseHelper extends SQLiteOpenHelper {



    static final String DB_NAME = "my_location.db";

    //DataBase Version..
    public static final int DATABASE_VERSION = 5;


    public static final String TABLE_NAME = "location_detail";
    public static final String TABLE_TODAY_JOBS = "today_jobs";

    public static final String orderId = "order_id";
    public static final String JobrefId = "job_refId";
    public static final String DriverId = "driver_id";
    public static final String jobStatus = "job_status";
    public static final String latlng = "latlng";
    public static final String address = "address";
    public static final String PlaceID = "place_id";
    public static final String ReceivedTime = "received_time";
    public static final String accuracy = "accuracy";
    public static final String modified_date = "modified_date";
    public static final String timeMillSec = "timeMillSec";
    public static final String speed = "speed";



    public static String ID =  "ID";
    public static String BatchID = "BatchID";
    public static String PickupDate = "PickupDate";
    public static String PickupTime = "PickupTime";
    public static String Passenger = "Passenger";
    public static String PickupAddress = "PickupAddress";
    public static String PickupTown = "PickupTown";
    public static String DropAddress = "DropAddress";
    public static String DestinationTown = "DestinationTown";
    public static String Identifier = "Identifier";
    public static String Colour = "Colour";
    public static String Passengers = "Passengers";
    public static String MaskSuppliersPassenger = "MaskSuppliersPassenger";
    public static String FlightNumber = "FlightNumber";
    public static String Mobile = "Mobile";
    public static String IsPointToPoint = "IsPointToPoint";
    public static String DriverName = "DriverName";
    public static String DriverMobile = "DriverMobile";
    public static String VehicleRegistrationNumber = "VehicleRegistrationNumber";
    public static String JobStatus = "JobStatus";



    //User Details table create query...
    final private String locationDetails = "create table if not exists "
            + TABLE_NAME
            + " ( order_id integer primary key autoincrement , "
            + JobrefId + " text, "
            + DriverId + " text, "
            + jobStatus + " text, "
            + latlng + " text, "
            + address + " text, "
            + PlaceID + " text, "
            + ReceivedTime + " text, "
            + accuracy + " text, "
            + modified_date + " text, "
            + speed + " text, "
            + timeMillSec + " text); ";


    final private String todayJobs = "create table if not exists "
            + TABLE_TODAY_JOBS
            + " ( ID text primary key , "
            + PickupDate + " text, "
            + PickupTime + " text, "
            + Passenger + " text, "
            + PickupAddress+ " text, "
            + PickupTown+ " text, "
            + DropAddress+ " text, "
            + DestinationTown+ " text, "
            + Identifier+ " text, "
            + Colour+ " text, "
            + Passengers+ " text, "
            + MaskSuppliersPassenger+ " text, "
            + FlightNumber+ " text, "
            + Mobile+ " text, "
            + IsPointToPoint+ " text, "
            + DriverName+ " text, "
            + DriverMobile+ " text, "
            + JobStatus+ " text, "
            + VehicleRegistrationNumber + " text); ";







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

            db.execSQL(todayJobs);

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

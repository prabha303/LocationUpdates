package prabhalab.client.location;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by prabha on 05/03/2019.
 */

public class JrWayDao {



    public static void insertUserDetails(Context context, Location location, String address) {
        try
        {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
            if (location != null)
            {
                long  timeMillis = System.currentTimeMillis();
                Date curDateTime = new Date(timeMillis);
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                final String dateTime = sdf.format(curDateTime);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseHelper.location_id,timeMillis);
                contentValues.put(DatabaseHelper.latlng,location.getLatitude() +","+location.getLongitude());
                contentValues.put(DatabaseHelper.address,address);
                contentValues.put(DatabaseHelper.modified_date,dateTime);
                contentValues.put(DatabaseHelper.update_date,dateTime);
                contentValues.put(DatabaseHelper.timeMillSec,""+timeMillis);
                contentValues.put(DatabaseHelper.speed,""+location.getSpeed());
                contentValues.put(DatabaseHelper.user_id,"");
                long saved = db.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
                Log.d("Updated_Location save","-"+saved);
            }
            db.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static ArrayList<WayPoint> calculateDistanceAndTime(Context context)
    {
        ArrayList<WayPoint> wayPointList = new ArrayList<>();
        try
        {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();
            String selectQuery = "SELECT * FROM  location_detail ORDER BY cast(location_id as integer)"  +" ASC;";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        String latlag = cursor.getString(cursor.getColumnIndex(DatabaseHelper.latlng));
                        String address = cursor.getString(cursor.getColumnIndex(DatabaseHelper.address));
                        if(Utility.isNotEmpty(latlag))
                        {
                            WayPoint wayPoint = new WayPoint();
                            wayPoint.setAddress(address);
                            wayPoint.setLatLang(latlag);
                            wayPointList.add(wayPoint);
                        }
                    } while (cursor.moveToNext());
                }
            }cursor.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return wayPointList;
    }

    public static boolean deleteRecords(Context context)
    {
         try
        {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();
            db.execSQL("delete from location_detail");
            db.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

}

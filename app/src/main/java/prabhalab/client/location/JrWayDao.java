package prabhalab.client.location;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

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
                long rs = db.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
                Log.d("rs",""+rs);
            }
            db.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

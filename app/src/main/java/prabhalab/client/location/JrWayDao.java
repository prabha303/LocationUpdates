package prabhalab.client.location;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

public class JrWayDao {



    public static void insertUserDetails(Context context, JSONObject jsonObject) {
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.ColumnKey.locationId,"");

        long rs = db.insert(DatabaseHelper.LocationDetails, null, contentValues);
        Log.d("rs",""+rs);
        Log.d("rs",""+rs);

    }
}

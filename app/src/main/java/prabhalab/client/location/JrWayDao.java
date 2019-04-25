package prabhalab.client.location;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import prabhalab.client.location.APIEngine.JsonUtil;
import prabhalab.client.location.driverhome.JobModel;
import prabhalab.client.location.job.StartTrip;

import static prabhalab.client.location.DatabaseHelper.TABLE_TODAY_JOBS;

/**
 * Created by prabha on 05/03/2019.
 */

public class JrWayDao {

    private static JrWayDao jrWayDao = null;

    //Single ton method...
    public static JrWayDao getInstance() {
        if (jrWayDao != null)
        {
            return jrWayDao;
        } else
        {
            jrWayDao = new JrWayDao();
            return jrWayDao;
        }
    }


    public static void insertUserDetails(Context context, Location location, String address) {
        try
        {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
            if (location != null)
            {
                String JobrefId = SharedPref.getStringValue(context, Utility.AppData.job_Id);
                String user_id = SharedPref.getStringValue(context, Utility.AppData.user_id);
                String jobStatus = SharedPref.getStringValue(context, Utility.AppData.job_status);

                long  timeMillis = System.currentTimeMillis();
                Date curDateTime = new Date(timeMillis);
                final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY HH:MM:SS");
                //final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final String dateTime = sdf.format(curDateTime);
                ContentValues contentValues = new ContentValues();
                //contentValues.put(DatabaseHelper.orderId,timeMillis);
                contentValues.put(DatabaseHelper.JobrefId,JobrefId);
                contentValues.put(DatabaseHelper.DriverId,user_id);
                contentValues.put(DatabaseHelper.jobStatus,jobStatus);
                contentValues.put(DatabaseHelper.latlng,location.getLatitude() +","+location.getLongitude());
                contentValues.put(DatabaseHelper.address,address);
                contentValues.put(DatabaseHelper.PlaceID,address);
                contentValues.put(DatabaseHelper.ReceivedTime,dateTime);
                contentValues.put(DatabaseHelper.accuracy,""+location.getAccuracy());
                contentValues.put(DatabaseHelper.modified_date,dateTime);
                contentValues.put(DatabaseHelper.timeMillSec,""+timeMillis);
                contentValues.put(DatabaseHelper.speed,""+location.getSpeed());
                long saved = db.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
                Log.d("Updated_Location_save","-"+saved);
            }
            db.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    public static float getPickupKM(Context context) {

        float t_pickup_km = 0;
        try
        {

            if (context != null) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                String selectQuery = "SELECT * FROM "+ DatabaseHelper.TABLE_NAME +" WHERE "+ DatabaseHelper.jobStatus +" = " + Utility.AppData.job_started  + " ORDER BY cast(order_id as integer)"  +" ASC;";
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {

                        String location_init = "";
                        do {

                            if(Utility.isNotEmpty(location_init))
                            {
                                float distance = Utility.CalCulateDistance(location_init, cursor.getString(cursor.getColumnIndex(DatabaseHelper.latlng)));
                                location_init = cursor.getString(cursor.getColumnIndex(DatabaseHelper.latlng));

                                t_pickup_km = t_pickup_km + distance;


                            }else
                            {
                                location_init = cursor.getString(cursor.getColumnIndex(DatabaseHelper.latlng));
                            }
                        }  while (cursor.moveToNext());

                    }
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return t_pickup_km;
    }



    public static float getDropKM(Context context) {

        float t_pickup_km = 0;
        try
        {

            if (context != null) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                String selectQuery = "SELECT * FROM "+ DatabaseHelper.TABLE_NAME +" WHERE "+ DatabaseHelper.jobStatus +" = " + Utility.AppData.job_pickuped  + " ORDER BY cast(order_id as integer)"  +" ASC;";
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {

                        String location_init = "";
                        do {

                            if(Utility.isNotEmpty(location_init))
                            {
                                float distance = Utility.CalCulateDistance(location_init, cursor.getString(cursor.getColumnIndex(DatabaseHelper.latlng)));
                                location_init = cursor.getString(cursor.getColumnIndex(DatabaseHelper.latlng));

                                t_pickup_km = t_pickup_km + distance;


                            }else
                            {
                                location_init = cursor.getString(cursor.getColumnIndex(DatabaseHelper.latlng));
                            }
                        }  while (cursor.moveToNext());

                    }
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return t_pickup_km;
    }


    public static String  getTotalKM(Context context) {
        float t_km = 0;
        String total_km = "";
        ArrayList<WayPoint> wayPointList = new ArrayList<>();
        try
        {

            if (context != null) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                String selectQuery = "SELECT * FROM "+ DatabaseHelper.TABLE_NAME +" ORDER BY cast(order_id as integer)"  +" ASC;";
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            WayPoint wayPoint = new WayPoint();
                            wayPoint.setLatLang(cursor.getString(cursor.getColumnIndex(DatabaseHelper.latlng)));
                            wayPointList.add(wayPoint);

                        }  while (cursor.moveToNext());

                    }
                }

                String nextLat = "";
                for(int i=0; i<wayPointList.size(); i++){
                    String latLang = wayPointList.get(i).getLatLang();
                    if(Utility.isNotEmpty(latLang))
                    {
                        float distance = MainActivity.CalCulateDistance(nextLat,latLang);
                        t_km = t_km + distance;
                    }
                    nextLat = latLang;
                }

                float totalKM = t_km/1000;


                DecimalFormat dtime = new DecimalFormat("#.##");
                total_km = dtime.format(totalKM);

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return total_km;
    }



    public JobModel getSingleJob(Context context, String RefId) {
        JobModel wayPoint = new JobModel();;
        try
        {
            if (context != null) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                String selectQuery = "SELECT * FROM "+ TABLE_TODAY_JOBS +" WHERE "+ DatabaseHelper.ID +" = " + RefId;
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            wayPoint.setID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID)));
                            wayPoint.setPickupDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PickupDate)));
                            wayPoint.setPickupTime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PickupTime)));
                            wayPoint.setPassenger(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Passenger)));
                            wayPoint.setPickupAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PickupAddress)));
                            wayPoint.setPickupTown(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PickupTown)));
                            wayPoint.setDropAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DropAddress)));
                            wayPoint.setDestinationTown(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DestinationTown)));
                            wayPoint.setIdentifier(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Identifier)));
                            wayPoint.setColour(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Colour)));
                            wayPoint.setPassengers(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Passengers)));
                            wayPoint.setMaskSuppliersPassenger(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MaskSuppliersPassenger)));
                            wayPoint.setFlightNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FlightNumber)));
                            wayPoint.setMobile(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Mobile)));
                            wayPoint.setIsPointToPoint(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IsPointToPoint)));
                            wayPoint.setDriverName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DriverName)));
                            wayPoint.setDriverMobile(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DriverMobile)));
                            wayPoint.setVehicleRegistrationNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.VehicleRegistrationNumber)));
                            wayPoint.setJobStatus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.JobStatus)));
                        }  while (cursor.moveToNext());
                    }
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return wayPoint;
    }

    public ArrayList<JobModel> getWayPoints(Context context) {
        ArrayList<JobModel> wayPointList = new ArrayList<>();
        try
        {
            if (context != null) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                String selectQuery = "SELECT * FROM " + TABLE_TODAY_JOBS;
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            JobModel wayPoint = new JobModel();
                            wayPoint.setID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID)));
                            wayPoint.setPickupDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PickupDate)));
                            wayPoint.setPickupTime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PickupTime)));
                            wayPoint.setPassenger(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Passenger)));
                            wayPoint.setPickupAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PickupAddress)));
                            wayPoint.setPickupTown(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PickupTown)));
                            wayPoint.setDropAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DropAddress)));
                            wayPoint.setDestinationTown(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DestinationTown)));
                            wayPoint.setIdentifier(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Identifier)));
                            wayPoint.setColour(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Colour)));
                            wayPoint.setPassengers(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Passengers)));
                            wayPoint.setMaskSuppliersPassenger(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MaskSuppliersPassenger)));
                            wayPoint.setFlightNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FlightNumber)));
                            wayPoint.setMobile(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Mobile)));
                            wayPoint.setIsPointToPoint(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IsPointToPoint)));
                            wayPoint.setDriverName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DriverName)));
                            wayPoint.setDriverMobile(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DriverMobile)));
                            wayPoint.setVehicleRegistrationNumber(cursor.getString(cursor.getColumnIndex(DatabaseHelper.VehicleRegistrationNumber)));
                            wayPoint.setJobStatus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.JobStatus)));
                            wayPointList.add(wayPoint);
                        } while (cursor.moveToNext());
                    }
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return wayPointList;
    }



    public static void updateUserDetails(Context context, String today_jobs) {
        try
        {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
            db.execSQL("DELETE FROM " + TABLE_TODAY_JOBS);

            if (Utility.isNotEmpty(today_jobs))
            {
                JSONArray jsonArray = new JSONArray(""+today_jobs);
                if(jsonArray.length() != 0){
                for (int i =0;i<jsonArray.length();i++){
                    JobModel objectFromJson = JsonUtil.getObjectFromJson(jsonArray.getJSONObject(i), JobModel.class);
                    long  timeMillis = System.currentTimeMillis();
                    Date curDateTime = new Date(timeMillis);
                    final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY HH:MM");
                    final String dateTime = sdf.format(curDateTime);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DatabaseHelper.ID,objectFromJson.getID());
                    contentValues.put(DatabaseHelper.PickupDate,objectFromJson.getPickupDate());
                    contentValues.put(DatabaseHelper.PickupTime,objectFromJson.getPickupTime());
                    contentValues.put(DatabaseHelper.Passenger,objectFromJson.getPassenger());
                    contentValues.put(DatabaseHelper.PickupAddress,objectFromJson.getPickupAddress());
                    contentValues.put(DatabaseHelper.PickupTown,objectFromJson.getPickupTown());
                    contentValues.put(DatabaseHelper.DropAddress,objectFromJson.getDropAddress());
                    contentValues.put(DatabaseHelper.DestinationTown,objectFromJson.getDestinationTown());
                    contentValues.put(DatabaseHelper.Identifier,objectFromJson.getIdentifier());
                    contentValues.put(DatabaseHelper.Colour,objectFromJson.getColour());
                    contentValues.put(DatabaseHelper.Passengers,objectFromJson.getPassengers());
                    contentValues.put(DatabaseHelper.MaskSuppliersPassenger,objectFromJson.getMaskSuppliersPassenger());
                    contentValues.put(DatabaseHelper.FlightNumber,objectFromJson.getFlightNumber());
                    contentValues.put(DatabaseHelper.Mobile,objectFromJson.getMobile());
                    contentValues.put(DatabaseHelper.IsPointToPoint,objectFromJson.getIsPointToPoint());
                    contentValues.put(DatabaseHelper.DriverName,objectFromJson.getDriverName());
                    contentValues.put(DatabaseHelper.DriverMobile,objectFromJson.getDriverMobile());
                    contentValues.put(DatabaseHelper.VehicleRegistrationNumber,objectFromJson.getVehicleRegistrationNumber());


                    String jobStatus = SharedPref.getStringValue(context, Utility.AppData.job_status);
                    String savedJobId = SharedPref.getStringValue(context, Utility.AppData.job_Id);
                    if(Utility.isNotEmpty(jobStatus) && Utility.isNotEmpty(savedJobId) && savedJobId.equalsIgnoreCase(objectFromJson.getID()))
                    {
                        contentValues.put(DatabaseHelper.JobStatus,SharedPref.getStringValue(context, Utility.AppData.job_status));

                    }else
                    {
                        contentValues.put(DatabaseHelper.JobStatus,"");

                    }



                    long saved = db.insert(TABLE_TODAY_JOBS, null, contentValues);
                    Log.d("updateUserDetails save","-"+saved);
                 }
                }else
                {
                    db.execSQL("DELETE FROM " + TABLE_TODAY_JOBS);
                }
            }else
            {
                db.execSQL("DELETE FROM " + TABLE_TODAY_JOBS);
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


    public static ArrayList<WayPoint> getAllWaypoints(Context context)
    {
        ArrayList<WayPoint> wayPointList = new ArrayList<>();
        try
        {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getReadableDatabase();
            String selectQuery = "SELECT * FROM  location_detail ORDER BY cast(order_id as integer)"  +" ASC;";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        String latlag = cursor.getString(cursor.getColumnIndex(DatabaseHelper.latlng));
                        String address = cursor.getString(cursor.getColumnIndex(DatabaseHelper.address));
                        int order_id = cursor.getInt(cursor.getColumnIndex("order_id"));
                        if(Utility.isNotEmpty(latlag))
                        {
                            WayPoint wayPoint = new WayPoint();
                            wayPoint.setOrder(order_id);
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

package in.vendor.rides;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.vendor.rides.job.DriverLocation;
import in.vendor.rides.job.StartTrip;

import static in.vendor.rides.Utility.AppData.job_started;

/**
 * Created by PrabhagaranR on 01-03-19.
 */

public class LocationService extends Service implements UpdateInterService {
    static Activity context;
    static UpdateInterService updateInterService;
    private static final String TAG = LocationService.class.getSimpleName();
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 20000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 30000;
    public static DriverLocation driverLocation = new DriverLocation();
    // boolean flag to toggle the ui

    public LocationService() {

    }
    public LocationService(Activity context, UpdateInterService mServiceManager) {
        try {
            this.context = context;
            this.updateInterService = mServiceManager;
            intGPSTracker();
            startLocationUpdates();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LocationService(Activity context) {
        try {
            this.context = context;
            intGPSTracker();
            startLocationUpdates();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        reTryData();
    }

    public void reTryData() {
        try {
            final Handler handlerRetry = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    try {
                        if (mCurrentLocation == null) {
                            intGPSTracker();
                        }
                        handlerRetry.postDelayed(this, 20000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            handlerRetry.postDelayed(r, 20000);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void intGPSTracker() {
        try {
            if(context != null)
            {


                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
                mSettingsClient = LocationServices.getSettingsClient(context);
                mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        try {
                            mCurrentLocation = locationResult.getLastLocation();
                            if (mCurrentLocation != null) {
                                doUpdateLocation(mCurrentLocation,"");
                            }
                        } catch (Exception e) {
                            Log.getStackTraceString(e);
                            e.printStackTrace();
                        }
                    }
                };
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(getInterval());
                mLocationRequest.setFastestInterval(getFastInterval());
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(mLocationRequest);
                mLocationSettingsRequest = builder.build();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private long getInterval()
    {
        try {
            if(context != null)
            {
                String location_interval = SharedPref.getStringValue(context, Utility.AppData.location_interval);
                if(Utility.isNotEmpty(location_interval))
                {
                    location_interval = location_interval.trim();
                    long num = Long.parseLong(location_interval);
                    return num * 1000;
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return UPDATE_INTERVAL_IN_MILLISECONDS;
    }

    private long getFastInterval()
    {
        try {
            if(context != null)
            {
                String location_interval = SharedPref.getStringValue(context, Utility.AppData.location_interval);
                if(Utility.isNotEmpty(location_interval))
                {
                    location_interval = location_interval.trim();
                    long num = Long.parseLong(location_interval);
                    num = num + 5;
                    return num * 1000;
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
    }



    private void startLocationUpdates() {
        try {
            if(context != null)
            {
                mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                        .addOnSuccessListener(context, new OnSuccessListener<LocationSettingsResponse>() {
                            @Override
                            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                                //noinspection MissingPermission
                                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                            }
                        })
                        .addOnFailureListener(context, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                int statusCode = ((ApiException) e).getStatusCode();
                                switch (statusCode) {
                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                        Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                                "location settings ");

                                        break;
                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                                "fixed here. Fix in Settings.";
                                        Log.e(TAG, errorMessage);
                                }
                            }
                        });
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Context.bindService() to obtain a persistent connection to a service.  does not call onStartCommand(). The client will receive the IBinder object that the service returns from its onBind(Intent) method,
        //The service will remain running as long as the connection is established
        return  mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {  //onStartCommand() can get called multiple times.
        super.onStartCommand(intent, flags, startId);


        Intent contentIntent = new Intent(LocationService.this,MainActivity.class);
        contentIntent.putExtra("fromForgroundService",true);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(LocationService.this, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(LocationService.this,getResources().getString(R.string.CHANNEL_ID));
        notification.setContentTitle("BTR Service Partner")
                .setContentText("App is running...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        stopForeground(true);

        startForeground(Utility.forgroundservice_notification_id,notification.build());



        return START_STICKY;
    }

    @Override
     public void doUpdateLocation(Location location, String  empty) {
        try{
            if(location != null) {
                // New location has now been determined
                String msg = "Updated_Location : " +location.getLatitude() + "," +location.getLongitude();
                Log.d("Updated_Location - ", ""+location.getAccuracy());
                double mAccuracy = location.getAccuracy(); // Get Accuracy
                if (mAccuracy < 400) {  //Accuracy reached  < 100. stop the location updates
                    Log.d("Updated_Location - acc", ""+location.getAccuracy());
                    String resultMessage = "";
                    SetLocationVariable(location);

                    try {
                        if(updateInterService != null)
                        {
                            updateInterService.doUpdateLocation(location,resultMessage);
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void SetLocationVariable(final Location location)
    {
        try {
            boolean accuracyFilter = Utility.checkAccuracy(location);
            if(accuracyFilter)
            {
                long  timeMillis = System.currentTimeMillis();
                Date curDateTime = new Date(timeMillis);
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                final String dateTime = sdf.format(curDateTime);
                LocationService.driverLocation = new DriverLocation();
                LocationService.driverLocation.setCLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
                LocationService.driverLocation.setLat(""+location.getLatitude());
                LocationService.driverLocation.setLng(""+location.getLongitude());
                LocationService.driverLocation.setTimeStamp(timeMillis);
                LocationService.driverLocation.setDatetimeString(dateTime);
                LocationService.driverLocation.setDateFormat(curDateTime);
                LocationService.driverLocation.setLocation(location);

                String jobStatus = SharedPref.getStringValue(context, Utility.AppData.job_status);
                if(Utility.isNotEmpty(jobStatus))
                {
                    if(jobStatus.equalsIgnoreCase(Utility.AppData.job_started) || jobStatus.equalsIgnoreCase(Utility.AppData.job_pickuped))
                    {

                        new AsyncTask<Void, Void, Boolean>()
                        {
                            protected Boolean doInBackground(Void... params)
                            {

                                String  formatted_address = "";
                                String  place_id = "";


                                String url =  "https://maps.googleapis.com/maps/api/geocode/json?" +
                                        "latlng="+location.getLatitude() +"," + location.getLongitude()
                                        +"&key="+ BuildConfig.APIKEY;

                                Log.d("geolocationData",url);
                                try {
                                   String  data = downloadUrl(url);

                                   if(Utility.isNotEmpty(data))
                                   {
                                       Log.d("geolocationData",data);

                                       JSONObject jsonObject =  new JSONObject(data);
                                       if(jsonObject != null && jsonObject.length() != 0)
                                       {
                                          String result = jsonObject.getString("results");
                                          if(Utility.isNotEmpty(result))
                                          {
                                              JSONArray jsonArray = new JSONArray(result);
                                              if(jsonArray != null && jsonArray.length() != 0)
                                              {
                                                  String address = jsonArray.get(0).toString();

                                                  JSONObject jsonObject_address =  new JSONObject(address);
                                                  if(jsonObject_address != null && jsonObject_address.length() != 0)
                                                  {

                                                      formatted_address = jsonObject_address.getString("formatted_address");
                                                      place_id = jsonObject_address.getString("place_id");

                                                  }
                                              }
                                          }
                                       }
                                   }
                                    Log.d("geolocationData1",place_id +" - "+ formatted_address);

                                    if(Utility.isNotEmpty(formatted_address) && Utility.isNotEmpty(place_id))
                                    {
                                        JrWayDao.insertUserDetails(context,location, formatted_address,place_id);
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                return null;
                            }
                        }.execute();




                    }
                }

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(20000);//20Seconds.
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e)
        {
            e.printStackTrace();
            return "";

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}


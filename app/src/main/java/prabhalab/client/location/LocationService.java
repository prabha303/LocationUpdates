package prabhalab.client.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
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
                        handlerRetry.postDelayed(this, 10000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            handlerRetry.postDelayed(r, 10000);
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
                        // location is received
                        try {
                            mCurrentLocation = locationResult.getLastLocation();
                            Log.d("mCurrentLocation", mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + " acc -" + mCurrentLocation.getAccuracy());
                            Log.d("getAccuracy", "" + mCurrentLocation.getAccuracy());

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
                mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
                mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
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
                if (mAccuracy < 100) {   //Accuracy reached  < 100. stop the location updates
                    Log.d("Updated_Location - acc", ""+location.getAccuracy());
                    String resultMessage = "";
                    try {
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        List<Address> addresses = null;

                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1); // In this sample, get just a single address
                        } catch (IOException ioException) {
                            //resultMessage = MainActivity.this .getString(R.string.service_not_available);
                            Log.e(TAG, resultMessage, ioException);
                        }
                        if (addresses == null || addresses.size() == 0) {
                            if (resultMessage.isEmpty()) {
                                //resultMessage = MainActivity.this.getString(R.string.no_address_found);
                                // Log.e(TAG, resultMessage);
                            }
                        } else {
                            Address address = addresses.get(0);
                            StringBuilder out = new StringBuilder();
                            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                out.append(address.getAddressLine(i));
                            }
                            resultMessage = out.toString();
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    updateInterService.doUpdateLocation(location,resultMessage);
                    JrWayDao.insertUserDetails(context,location,resultMessage);

                }


            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}


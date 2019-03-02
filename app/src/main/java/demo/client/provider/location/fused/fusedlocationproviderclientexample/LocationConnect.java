package demo.client.provider.location.fused.fusedlocationproviderclientexample;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by PrabhagaranR on 01-03-19.
 */
public class LocationConnect implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LocationConnect.class.getSimpleName();
    private Context context1;
    public static GoogleApiClient mGoogleApiClient;
    UpdateInterService mServiceManager;
    private static Location location;
    private static long timestamp;

    public static void disconnect() {
        try {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LocationConnect(Context context, UpdateInterService mServiceManager) {
        try {
            this.context1 = context;
            this.mServiceManager = mServiceManager;
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(context1)
                        .addApi(LocationServices.API)
                        .addApi(ActivityRecognition.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
                mGoogleApiClient.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void connect() {
        try {
            Log.d("driverLoc_", "connecting");
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        try {
            if (loc != null) {
                mServiceManager.doUpdateLocation(location);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            long ONTRIP_MINTime = 1000 * 10;
            long ONTRIP_MIN_INTERVAL_fast1 = 1000 * 10;
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setSmallestDisplacement(0);
            locationRequest.setFastestInterval(ONTRIP_MINTime); //  DEFALT_INTERVAL Receive location update every 10 sec
            locationRequest.setInterval(ONTRIP_MIN_INTERVAL_fast1); // DEFALT_INTERVAL Receive location update every 10 sec
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(context1, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context1, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, locationRequest, this);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }






}


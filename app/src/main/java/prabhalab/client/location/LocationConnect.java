package prabhalab.client.location;

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

    prabhalab.client.location.UpdateInterService mServiceManager;


    @Override
    public void onLocationChanged(Location loc) {
        try {
            if (loc != null) {
                //mServiceManager.doUpdateLocation(loc);
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
            locationRequest.setFastestInterval(0); //  DEFALT_INTERVAL Receive location update every 10 sec
            locationRequest.setInterval(0); // DEFALT_INTERVAL Receive location update every 10 sec
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
          //  LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, locationRequest, this);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onConnectionSuspended(int i)
    {
        Log.d("driverLoc_", "=" + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.d("driverLoc_", "=" + connectionResult.getResolution());
        Log.d("driverLoc_", "=" + connectionResult.getErrorMessage());
    }






}


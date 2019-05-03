package in.vendor.rides;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * Created by PrabhagaranR on 01-03-19.
 */

public class MainActivity extends AppCompatActivity implements UpdateInterService {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    FancyButton end_track_button,start_track_button;
    TextView mLatitude,mLongitude,mTimestamp,status,mAddress,lastTripKM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try
        {
            /*initializeUI();
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
            }else
            {
                startLocationButtonClick();
            }

            Utility.getInstance().stayScreenOn(this);*/
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocationButtonClick();

                } else {
                    Toast.makeText(this,R.string.location_permission_denied,Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    private void initializeUI() {
        try
        {
            mLatitude = findViewById(R.id.latitude_value);
            mLongitude = findViewById(R.id.longitude_value);
            mTimestamp = findViewById(R.id.timestamp_value);
            mAddress =  findViewById(R.id.address_value);
            status =  findViewById(R.id.status);
            lastTripKM =  findViewById(R.id.lastTripKM);

            start_track_button = findViewById(R.id.start_track);
            end_track_button = findViewById(R.id.end_track);
            start_track_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    long  timeMillis = System.currentTimeMillis();
                    SharedPref.getInstance().setSharedValue(MainActivity.this, "start_track", "yes");
                    SharedPref.getInstance().setSharedValue(MainActivity.this, "start_time", ""+timeMillis);
                    //SharedPref.getInstance().setSharedValue(MainActivity.this, "end_time", "");
                    status.setText("Tracking.......");
                    end_track_button.setVisibility(View.VISIBLE);
                    start_track_button.setVisibility(View.GONE);
                    getLocation();
                    Toast.makeText(MainActivity.this, "Started",Toast.LENGTH_LONG).show();
                }
            });


            end_track_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    alert();


                }
            });

            String start_track = SharedPref.getStringValue(MainActivity.this, "start_track");
            if(Utility.isNotEmpty(start_track) && start_track.equalsIgnoreCase("yes"))
            {
                status.setText("Tracking.......");
                end_track_button.setVisibility(View.VISIBLE);
                start_track_button.setVisibility(View.GONE);
            }else
            {
                end_track_button.setVisibility(View.GONE);
                start_track_button.setVisibility(View.VISIBLE);
            }


            String last_trip = SharedPref.getStringValue(MainActivity.this, "last_trip");
            if(Utility.isNotEmpty(last_trip))
            {
                lastTripKM.setVisibility(View.VISIBLE);
                lastTripKM.setText("Last trip : "+ last_trip);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void alert() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Stop Track")
                .setMessage("Are you sure you want to stop trcking?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        SharedPref.getInstance().setSharedValue(MainActivity.this, "start_track", "no");
                        status.setText("Tracking not started.. press to start track your location...");
                        end_track_button.setVisibility(View.GONE);
                        start_track_button.setVisibility(View.VISIBLE);
                        long  timeMillis = System.currentTimeMillis();
                        SharedPref.getInstance().setSharedValue(MainActivity.this, "end_time", ""+timeMillis);

                        CalculateKM();


                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

     }


    private void getLocation() {
        try
        {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
            } else {
                Log.d(TAG, "getLocation: permissions granted");
            }

            Intent service = new Intent(getApplicationContext(), LocationService.class);
            startService(service);
            LocationService locationService = new LocationService(MainActivity.this,this);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void startLocationButtonClick() {
        try
        {
            // Requesting ACCESS_FINE_LOCATION using Dexter library
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            getLocation();
                        }
                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                // open device settings when the permission is
                                // denied permanently
                                openSettings();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
     }
    private void openSettings()
    {
        try
        {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void doUpdateLocation(Location location, String address) {
        try
        {
            long  timeMillis = System.currentTimeMillis();
            Date curDateTime = new Date(timeMillis);
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM");
            final String dateTime = sdf.format(curDateTime);
            mTimestamp.setText(dateTime);
            mAddress.setText(address);
            mLatitude.setText(""+location.getLatitude());
            mLongitude.setText(""+location.getLongitude());
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void CalculateKM()
    {
        try
        {
            float t_km = 0;
            String start_time = SharedPref.getStringValue(MainActivity.this, "start_time");
            String end_time = SharedPref.getStringValue(MainActivity.this, "end_time");
            ArrayList<WayPoint> tripInfo = JrWayDao.calculateDistanceAndTime(MainActivity.this);
            if(tripInfo.size() != 0)
            {
                String nextLat = "";
                for(int i=0; i<tripInfo.size(); i++)
                {
                    String latLang = tripInfo.get(i).getLatLang();
                    if(Utility.isNotEmpty(nextLat))
                    {
                        float distance = CalCulateDistance(nextLat,latLang);
                        t_km = t_km + distance;
                    }
                    nextLat = latLang;
                }
            }
            float totalKM = t_km/1000;

            Log.d("lat_addreess",totalKM +" meters "+t_km);

            long diffInMin = 0;
            DecimalFormat dtime = new DecimalFormat("#.##");

            if(Utility.isNotEmpty(start_time) && Utility.isNotEmpty(end_time))
            {
                long startTime = Long.parseLong(start_time);
                long endTime = Long.parseLong(end_time);
                long diff = startTime - endTime;
                diffInMin = TimeUnit.MILLISECONDS.toMinutes(diff);
            }

            lastTripKM.setVisibility(View.VISIBLE);
            lastTripKM.setText("Last trip - "+ diffInMin +" min - " +dtime.format(totalKM) +" KM");
            SharedPref.getInstance().setSharedValue(MainActivity.this, "last_trip", "Min - "+diffInMin +" - " +dtime.format(totalKM) +" KM");

            Toast.makeText(MainActivity.this, "Done",Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, diffInMin +" min - " +dtime.format(totalKM) +" KM",Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, diffInMin +" min - " +dtime.format(totalKM) +" KM",Toast.LENGTH_LONG).show();


            ClearAll();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void ClearAll()
    {
        try {
            SharedPref.getInstance().setSharedValue(MainActivity.this, "start_time", "");
            SharedPref.getInstance().setSharedValue(MainActivity.this, "end_time", "");
            JrWayDao.deleteRecords(MainActivity.this);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static float CalCulateDistance (String startLocation, String endLocation)
    {
        try
        {
            if(Utility.isNotEmpty(startLocation) && Utility.isNotEmpty(endLocation))
            {
                String[] s_latLng = startLocation.split(",");
                String[] n_latLng = endLocation.split(",");

                double s_latitude = Double.parseDouble(s_latLng[0]);
                double s_longitude = Double.parseDouble(s_latLng[1]);

                double n_latitude = Double.parseDouble(n_latLng[0]);
                double n_longitude = Double.parseDouble(n_latLng[1]);

                Location slocation = new Location("");
                slocation.setLatitude(s_latitude);
                slocation.setLongitude(s_longitude);

                Location nlocation = new Location("");
                nlocation.setLatitude(n_latitude);
                nlocation.setLongitude(n_longitude);

                return slocation.distanceTo(nlocation);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }


}

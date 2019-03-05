package prabhalab.client.location;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;



/**
 * Created by PrabhagaranR on 01-03-19.
 */

public class MainActivity extends AppCompatActivity implements UpdateInterService {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    Button mGetQuoteLocation;
    TextView mLatitude;
    TextView mLongitude;
    TextView mTimestamp;
    TextView mAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try
        {
            initializeUI();
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
            }else
            {
                startLocationButtonClick();
            }

            Utility.getInstance().stayScreenOn(this);
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

                    getLocation();

                } else {
                    Toast.makeText(this,R.string.location_permission_denied,Toast.LENGTH_SHORT).show();

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

            mGetQuoteLocation = findViewById(R.id.get_location);
            mGetQuoteLocation.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getLocation();
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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
            mAddress.setText(address);
            mLatitude.setText(""+location.getLatitude());
            mLongitude.setText(""+location.getLongitude());

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}

package demo.client.provider.location.fused.fusedlocationproviderclientexample;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import java.io.IOException;
import java.util.List;
import java.util.Locale;



/**
 * Created by PrabhagaranR on 01-03-19.
 */

public class MainActivity extends AppCompatActivity implements UpdateInterService {

    // Constants
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";
    private static final String TAG = MainActivity.class.getSimpleName();

    // ui
    Button mGetQuoteLocation;
    TextView mLatitude;
    TextView mLongitude;
    TextView mTimestamp;
    TextView mAddress;

    // Location classes
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
        }else
        {
            getLocation();
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
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d(TAG, "getLocation: permissions granted");
        }

        LocationService locationService = new LocationService(MainActivity.this,this);
        Intent service = new Intent(getApplicationContext(), LocationService.class);
        startService(service);
    }



    @Override
    public void doUpdateLocation(Location location) {
        Geocoder geocoder = new Geocoder(MainActivity.this,Locale.getDefault());
        List<Address> addresses = null;
        String resultMessage = "";
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
        mAddress.setText(resultMessage);
        mLatitude.setText(""+location.getLatitude());
        mLongitude.setText(""+location.getLongitude());
    }
}

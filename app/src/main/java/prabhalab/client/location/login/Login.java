package prabhalab.client.location.login;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import mehdi.sakout.fancybuttons.FancyButton;
import prabhalab.client.location.R;


/**
 * Created by PrabhagaranR on 22-03-19.
 */

public class Login extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = Login.class.getSimpleName();
    FancyButton end_track_button,start_track_button;
    TextView mLatitude,mLongitude,mTimestamp,status,mAddress,lastTripKM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        try
        {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
            }else
            {
                initializeUI();
            }



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

                    initializeUI();

                } else {
                    Toast.makeText(this,R.string.location_permission_denied,Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    private void initializeUI() {
        try
        {


        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




}

package in.vendor.rides.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import mehdi.sakout.fancybuttons.FancyButton;
import in.vendor.rides.R;


/**
 * Created by PrabhagaranR on 22-03-19.
 */

public class SplashActivty extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = SplashActivty.class.getSimpleName();
    FancyButton end_track_button,start_track_button;
    TextView mLatitude,mLongitude,mTimestamp,status,mAddress,lastTripKM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activty);
        try
        {


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivty.this, Login.class);
                    startActivity(i);
                    finish();
                }
            }, 5 * 1000);




        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}

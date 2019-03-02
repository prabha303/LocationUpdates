package prabhalab.client.location;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.location.LocationSettingsRequest;
/**
 * Created by PrabhagaranR on 01-03-19.
 */

public class LocationService extends Service implements UpdateInterService {

    private Activity context;
    private UpdateInterService updateInterService;
    private static final String TAG = LocationService.class.getSimpleName();


    private LocationSettingsRequest mLocationSettingsRequest;
    public static final String GPS_NOT_ENABLED = "GPS_NOT_ENABLED";

    public LocationService()
    {

    }

    public LocationService(Activity context, UpdateInterService mServiceManager)
    {
        try {
            this.context = context;
            this.updateInterService = mServiceManager;
            intGPSTracker();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)

        intGPSTracker();
    }

    private void intGPSTracker()
    {
        try {
            LocationConnect lynkLocationManager = new LocationConnect(context, this);
            lynkLocationManager.connect();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        //Context.bindService() to obtain a persistent connection to a service.  does not call onStartCommand(). The client will receive the IBinder object that the service returns from its onBind(Intent) method,
        //The service will remain running as long as the connection is established
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {  //onStartCommand() can get called multiple times.
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
     public void doUpdateLocation(Location location) {
        try{
            if(location != null) {
                // New location has now been determined
                String msg = "Updated Location: " +Double.toString(location.getLatitude()) + "," +Double.toString(location.getLongitude());
                Log.d("handleNewLocation", msg);
                Log.d("location.getAccuracy()", ""+location.getAccuracy());

                double mAccuracy = location.getAccuracy(); // Get Accuracy
                if (mAccuracy < 30) {   //Accuracy reached  < 5f. stop the location updates
                    Log.d("location.getAccu - 30", ""+location.getAccuracy());
                }
                updateInterService.doUpdateLocation(location);

                JrWayDao.insertUserDetails(context,null);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}


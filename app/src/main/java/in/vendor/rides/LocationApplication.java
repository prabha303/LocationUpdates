package in.vendor.rides;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.multidex.MultiDexApplication;

/**
 * Application class.
 */
public class LocationApplication extends MultiDexApplication {
//    @SuppressLint("StaticFieldLeak")
//    public static MixpanelAPI mixPannel = null;
    @SuppressLint("StaticFieldLeak")
    private static  Context context = null;
    private String regIdGCM = null;
    String DeviceId = null;
    SharedPref sharedPref = null;
    private AsyncTask<Void, Void, Void> mRegisterTask;
    @SuppressLint("StaticFieldLeak")
    private static LocationApplication mInstance;


    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();

    }

    public static Context getAppContext() {
        return LocationApplication.context;
    }
}

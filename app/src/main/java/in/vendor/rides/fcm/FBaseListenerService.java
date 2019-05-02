package in.vendor.rides.fcm;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import in.vendor.rides.SharedPref;
import in.vendor.rides.Utility;

public class FBaseListenerService extends FirebaseInstanceIdService
{

    private static final String TAG = "MyAndroidFCMIIDService";

    @Override
    public void onTokenRefresh()
    {
        try
        {
            //Get hold of the registration token
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            //Log the token
            System.out.println("Refreshed token: "+ refreshedToken);
            SharedPref.getInstance().setSharedValue(getApplicationContext(), Utility.AppData.FCM_ID, refreshedToken);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}

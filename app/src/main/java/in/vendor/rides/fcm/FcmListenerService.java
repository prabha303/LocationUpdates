package in.vendor.rides.fcm;

/**
 * Created by Prabhagaran R 28/03/19
 */

import android.content.Context;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONObject;

import java.util.Map;

public class FcmListenerService extends FirebaseMessagingService
{
     Context context = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        System.out.println("Prabha---- onMessage Received Called ");
        try
        {
            context = FcmListenerService.this;
            Map<String, String> params = remoteMessage.getData();
            JSONObject messageObject =  new JSONObject(params);

            Log.e("----fcm ","trip----"+messageObject.toString());

        }
        catch (Exception e)
        {
            System.out.println("Exception in onMessage Received "+e.getMessage());
        }
    }

}

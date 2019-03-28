package prabhalab.client.location.fcm;

/**
 * Created by harishm on 7/7/2017.
 */

import android.content.Context;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONObject;

import java.util.Map;

public class FcmListenerService extends FirebaseMessagingService
{
    private JSONObject messageObject = null;
    /*public static boolean offerFlag = false;
    public static boolean abortFlag = false;
    public static boolean messageFlag = false;
    public static boolean autoServiceStartFlag = false;
    public static boolean nwAlertFlag = false;

    private Utility utility = null;*/
    private static final String TAG = "MyAndroidFCMService";
    Context context = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        System.out.println("Prabha---- onMessage Received Called ");


        try
        {
            context = FcmListenerService.this;
            Map<String, String> params = remoteMessage.getData();
            messageObject =  new JSONObject(params);

            Log.e("----fcm ","trip----"+messageObject.toString());

        }
        catch (Exception e)
        {
            System.out.println("Exception in onMessage Received "+e.getMessage());
        }
    }

    /*private void insideAppNotification()
    {
        try
        {
            SharedPref sharedPref = SharedPref.getInstance();
            String offer = messageObject.optString("offer");
            if (Utility.isNullCheck(offer))
            {
                sharedPref.setSharedValue(context, OFFER_ID, offer);
                sharedPref.setSharedValue(context, OFFER_FLAG, true);

                //Setting Verify Status
                sharedPref.setSharedValue(context, VerifyStatus, true);


                //\15Oct16. Resetting services. This is to avoid letting existing services running when resending the order.
                utility.stopService(StoreService.class, context);
                utility.stopService(HaversineService.class, context);
                utility.startService(GeoService.class, context);

                //Deleting the values in DB...
                JrWayDao.getInstance().deleteValues(context);

                Intent intent1 = new Intent(context, OfferDialog.class);
                intent1.putExtra("offer", offer);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
            else
            {
                offerFlag = false;
                abortFlag = false;
                messageFlag = false;
                nwAlertFlag = false;
                String abortMessage = messageObject.optString("abort");

                if (Utility.isNullCheck(abortMessage))
                {
                    if(sharedPref.getBooleanValue(context,VerifyStatus))
                    {

                        sharedPref.setSharedValue(context, OFFER_FLAG, false);

                        sharedPref.setSharedValue(context, OFFER_ID, "");

                        //\8Feb17. Clear if auto start timer is scheduled once the abort is done.
                        clearAnyPendingTripTimers();


                        //\Deleting db values.
                        JrWayDao.getInstance().deleteValues(context);

                        Intent intent1 = new Intent(context, AbortDialog.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.putExtra("message", abortMessage);
                        context.startActivity(intent1);
                    }
                    else
                    {
                        Utility.getInstance().DumpError("Verify Status is False. Not processing Abort notification!");
                        return;
                    }
                }
                else
                {
                    String alertMessage = messageObject.optString("message");
                    if (Utility.isNullCheck(alertMessage))
                    {
                        sharedPref.setSharedValue(context, OFFER_FLAG, false);

                        Intent intent1 = new Intent(context, MessageDialog.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.putExtra("message", alertMessage);
                        context.startActivity(intent1);
                    }
                }
            }
        }
        catch (Exception e)
        {
            if(utility != null)
                utility.DumpError("Exception in insideAppNotification "+e.getMessage());
        }

    }

    @Override
    public void successResponse(String successResponse, int flag)
    {


    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag)
    {
        *//** Flag == 0 , represents the PushNotificationACK response.
         *
         *//*

        if (flag == 0) {
            if (messageObject != null && jsonObject != null)
            {
                String response = jsonObject.optString("Response");

                if (Utility.isNullCheck(response) && response.equalsIgnoreCase("success"))
                {
                    insideAppNotification();
                }
            }
        }
    }

    @Override
    public void errorResponse(String errorResponse, int flag) {

    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }



    private void clearAnyPendingTripTimers()
    {
        try
        {
            if(utility != null)
                utility.stopService(BackgroundSoundService.class, getApplicationContext());

            SharedPref.getInstance().setSharedValue(getApplicationContext(), TripAborted, "yes");

        }
        catch (Exception e)
        {
            Utility.getInstance().DumpError("Exception in  clearAnyPendingTripTimers "+e.getMessage());
        }
    }
    */
}

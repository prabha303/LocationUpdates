package prabhalab.client.location;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import prabhalab.client.location.job.StartTrip;

import static android.content.Context.LOCATION_SERVICE;


/**
 * Created by prabha on 05/03/2019.
 */

public class Utility {

    private static Utility utility = null;
    private static ProgressDialog pDialog;
    public static Utility getInstance()
    {
        if (utility != null)
        {
            return utility;
        }
        else
        {
            utility = new Utility();
            return utility;
        }
    }
    public static boolean isNotEmpty(String Value) {
        boolean flag =false;
        try {
            if(!TextUtils.isEmpty(Value)){
                Value = Value.replace("null", "");
            }else{
                Value = "";
            }
            if(!TextUtils.isEmpty(Value)){
                Value = Value.trim();
            }
            if(!TextUtils.isEmpty(Value)){
                flag =true;
            }else{
                flag =false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public void stayScreenOn(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    public void showLoadingDialog(Context context)
    {
        try
        {
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(Html.fromHtml("<b>Please</b><br/>wait..."));
            pDialog.setCancelable(false);
            pDialog.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void closeLoadingDialog() {
        try {
            pDialog.dismiss();
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }


    public static void showCustomDialogWithHeader(Context context, String  tilte, String message, String pasitiveText, String negativeText,
                                                  boolean cancelButton, boolean cancelable, final ConfirmCallBack confirmCallBack){
        final android.app.AlertDialog.Builder alert_dialog= new android.app.AlertDialog.Builder(context);
        alert_dialog.setCancelable(cancelable);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView=inflater.inflate(R.layout.custom_popup_msg_with_button, null);
        alert_dialog.setView(dialogView);
        final TextView popup_msg =(TextView)dialogView.findViewById(R.id.messageText);
        final TextView tilteText =(TextView)dialogView.findViewById(R.id.tilteText);
        final TextView popup_cancel_btn=(TextView)dialogView.findViewById(R.id.popup_cancel_btn);
        final TextView popup_yes_btn = (TextView)dialogView.findViewById(R.id.popup_yes_btn);
        popup_msg.setText(message);
        popup_yes_btn.setText(pasitiveText);
        popup_cancel_btn.setText(negativeText);
        tilteText.setText(tilte);

        // messageText  popup_cancel_btn popup_yes_btn
        if(!cancelButton)
        {
            popup_cancel_btn.setVisibility(View.INVISIBLE);
        }



        final android.app.AlertDialog dialog = alert_dialog.create();
        dialog.show();
        popup_yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    if(confirmCallBack != null)
                    {
                        confirmCallBack.confirmed(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        popup_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    if(confirmCallBack != null)
                    {
                        confirmCallBack.confirmed(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void showSuccessPopup(Context context, String  tilte, String message, String pasitiveText, String negativeText,
                                                  boolean cancelButton, boolean cancelable, final ConfirmCallBack confirmCallBack){
        final android.app.AlertDialog.Builder alert_dialog= new android.app.AlertDialog.Builder(context);
        alert_dialog.setCancelable(cancelable);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView=inflater.inflate(R.layout.success_popup_msg_with_button, null);
        alert_dialog.setView(dialogView);
        //final TextView popup_msg =(TextView)dialogView.findViewById(R.id.messageText);
        //final TextView tilteText =(TextView)dialogView.findViewById(R.id.tilteText);
        //final TextView popup_cancel_btn=(TextView)dialogView.findViewById(R.id.popup_cancel_btn);
        //final TextView popup_yes_btn = (TextView)dialogView.findViewById(R.id.popup_yes_btn);
        //popup_msg.setText(message);
        //popup_yes_btn.setText(pasitiveText);
        //popup_cancel_btn.setText(negativeText);
        //tilteText.setText(tilte);

        // messageText  popup_cancel_btn popup_yes_btn
        /*if(!cancelButton)
        {
            popup_cancel_btn.setVisibility(View.INVISIBLE);
        }*/



        final android.app.AlertDialog dialog = alert_dialog.create();
        dialog.show();
        /*popup_yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    if(confirmCallBack != null)
                    {
                        confirmCallBack.confirmed(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        popup_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    if(confirmCallBack != null)
                    {
                        confirmCallBack.confirmed(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/
    }


    public static boolean canGetLocation(Context context) {
        boolean flag = false;
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnabled && !isNetworkEnabled) {
            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }

    public interface ConfirmCallBack
    {
        void confirmed(boolean success);
    }

    public static boolean checkAccuracy(Location location){
        try {

            if (location.getAccuracy() > 100) // TODO when in trip
            {
                return false;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }


    public static String getLatLng(Context context)
    {
        try {
            if(LocationService.driverLocation !=null && Utility.isNotEmpty(LocationService.driverLocation.getLat()) && Utility.isNotEmpty(LocationService.driverLocation.getLng()))
            {
                return LocationService.driverLocation.getLat() + "," + LocationService.driverLocation.getLng();
            }else
            {
                if(context != null)
                {
                    Intent service = new Intent(context, LocationService.class);
                    context.startService(service);
                }

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isJSONValid1(String jsonInString) {
        try {
             Gson gson = new Gson();
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch(com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }


    public static boolean isJSONValid(String jsonInString) {
        try {
            new JSONObject(jsonInString);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(jsonInString);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }





    public interface AppData {
        String past_jobs = "past_jobs";
        String future_jobs = "future_jobs";
        String today_jobs = "today_jobs";
        String job_status = "job_status";
        String job_Id = "job_id";
        String trip_sheet_ref_number = "trip_sheet_ref_number";


        String job_started = "started";
        String job_pickuped = "pickuped";


        String hasLoggedIn = "has_logged_in";
        String user_id = "user_id";
        String user_name = "user_name";
        String today_job_count = "today_job_count";
        String past_job_count = "past_job_count";
        String future_job_count = "future_job_count";
        String password = "password";
        String FCM_ID = "fcm_id";

        String today_date = "today_date";
        String yesterday_date = "yesterday_date";
        String tomorrow_date = "tomorrow_date";
    }

}

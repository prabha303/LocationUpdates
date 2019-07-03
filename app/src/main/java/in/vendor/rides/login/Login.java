package in.vendor.rides.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
//import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import mehdi.sakout.fancybuttons.FancyButton;
import in.vendor.rides.APIEngine.JsonUtil;
import in.vendor.rides.BuildConfig;
import in.vendor.rides.JrWayDao;
import in.vendor.rides.LocationService;
import in.vendor.rides.R;
import in.vendor.rides.SharedPref;
import in.vendor.rides.Utility;
import in.vendor.rides.driverhome.DriverHome;
import in.vendor.rides.driverhome.JobModel;
import in.vendor.rides.job.RestartServiceBroadCastReceiver;
import in.vendor.rides.job.StartTrip;

import static in.vendor.rides.Utility.AppData.hasLoggedIn;
import static in.vendor.rides.Utility.AppData.job_pickuped;


/**
 * Created by PrabhagaranR on 22-03-19.
 */

public class Login extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = Login.class.getSimpleName();
    FancyButton login;
    TextView forgetPassword;
    EditText password,userId;
    String fcmToken = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //fcmToken = FirebaseInstanceId.getInstance().getToken();

        Log.d("refreshedToken","--"+fcmToken);
        if(Utility.isNotEmpty(fcmToken))
        {
            SharedPref.getInstance().setSharedValue(getApplicationContext(), Utility.AppData.FCM_ID, fcmToken);
        }

        login = findViewById(R.id.login);
        userId = findViewById(R.id.userId);
        password = findViewById(R.id.password);
        forgetPassword = findViewById(R.id.forgetPassword);


        //SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.job_status, "");
        //login.setText(getResources().getString(R.string.fa_arrow_circle_right) + " Login");


        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callForgetPasswordDialog();
            }
        });

        showSplashImage();

    }

    private void showSplashImage() {
        try {
            final LinearLayout splashLayout = findViewById(R.id.splashLayout);
            final LinearLayoutCompat mainLayout = findViewById(R.id.mainLayout);
            splashLayout.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 3 seconds
                    splashLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                    initializeUI();
                }
            }, 2000);

        }catch (Exception e)
        {
            initializeUI();
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        try
        {

            LinearLayout splashLayout = findViewById(R.id.splashLayout);
            LinearLayoutCompat mainLayout = findViewById(R.id.mainLayout);
            splashLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);

            try
            {
                if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
                }else
                {

                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }


            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(validate(userId.getText().toString(),password.getText().toString()))
                    {
                        openDriverPage(userId.getText().toString(),password.getText().toString());
                    }else
                    {
                        Toast.makeText(getApplicationContext(), "UserId, password should not empty", Toast.LENGTH_LONG).show();
                    }
                }
            });
            if(SharedPref.getBooleanValue(this, hasLoggedIn))
            {
                String user_id = SharedPref.getStringValue(this,Utility.AppData.user_id);
                String pwd = SharedPref.getStringValue(this,Utility.AppData.password);

                 if(Utility.isNotEmpty(user_id) && Utility.isNotEmpty(pwd))
                 {
                     userId.setText(user_id);
                     password.setText(pwd);
                     openDriverPage(user_id,pwd);
                 }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private boolean  validate(String usr, String pwd)
    {

        if (Utility.isNotEmpty(usr) && Utility.isNotEmpty(pwd))
        {
            usr = usr.trim();
            pwd = pwd.trim();
        }

        if(Utility.isNotEmpty(usr) && Utility.isNotEmpty(pwd))
        {
            return true;
        }

        return false;
    }

    private void openDriverPage(String user, String pwd)
    {

        new ProcessLogin(user,pwd).execute();

    }


    public  class ForgetPassword extends AsyncTask {
        String  emailId;
        public ForgetPassword(String emailId) {
            this.emailId = emailId;
         }
        protected void onPreExecute() {
            Utility.getInstance().showLoadingDialog(Login.this);
        }
        protected String doInBackground(Object... params) {
            String result = "";
            try
            {
                String SOAP_ACTION = BuildConfig.SOAP_ACTION +"ForgotPassword";
                String URL = BuildConfig.BLT_BASEURL;
                String NAMESPACE = BuildConfig.NAMESPACE;
                String METHOD_NAME = "ForgotPassword";
                SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
                soapObject.addProperty("emailAddress",emailId);
                SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(soapObject);
                HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
                try {
                    httpTransportSE.call(SOAP_ACTION, envelope);
                    SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
                    result = soapPrimitive.toString();
                    Log.d("result","--"+result);
                } catch(SoapFault sf){
                    result = sf.faultstring;
                }
                catch (Exception e) {
                    showCustomDialog(getResources().getString(R.string.checkYourInternetConnection), true);
                    e.printStackTrace();
                }
                Utility.getInstance().closeLoadingDialog();
                return result;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(Object result) {
            Utility.getInstance().closeLoadingDialog();
            Log.d("result","--"+result);
            if(Utility.isNotEmpty(""+result))
            {
                Toast.makeText(getApplicationContext(), ""+result , Toast.LENGTH_LONG).show();

            }else
            {
                showCustomDialog(getResources().getString(R.string.checkYourInternetConnection), true);
            }
        }
    }

    public  class ProcessLogin extends AsyncTask {
        String userId, password;
        public ProcessLogin(String userId, String password) {
            this.userId = userId;
            this.password = password;
        }
        protected void onPreExecute() {
            Utility.getInstance().showLoadingDialog(Login.this);
        }
        protected String doInBackground(Object... params) {
            String result = "";
            try
            {
                if(!Utility.isNotEmpty(fcmToken))
                {
                    //fcmToken = FirebaseInstanceId.getInstance().getToken();
                }
                String SOAP_ACTION = BuildConfig.SOAP_ACTION +"ValidateUser";
                String URL = BuildConfig.BLT_BASEURL;
                String NAMESPACE = BuildConfig.NAMESPACE;
                String METHOD_NAME = "ValidateUser";
                SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
                soapObject.addProperty("userName",userId);
                soapObject.addProperty("password",password);
                soapObject.addProperty("fcmID",""+BuildConfig.VERSION_CODE);

                SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(soapObject);
                HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
                try {
                    httpTransportSE.call(SOAP_ACTION, envelope);
                    SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
                    result = soapPrimitive.toString();
                    Log.d("result","--"+result);
                } catch(SoapFault sf){
                    result = sf.faultstring;
                }
                catch (Exception e) {
                    showCustomDialog(getResources().getString(R.string.checkYourInternetConnection), true);
                    e.printStackTrace();
                }
                Utility.getInstance().closeLoadingDialog();
                return result;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(Object result) {
            Utility.getInstance().closeLoadingDialog();
            Log.d("result","--"+result);
            if(Utility.isNotEmpty(""+result))
            {
                try {
                    JSONObject jsnobject = new JSONObject(""+result);
                    if(Utility.isNotEmpty(jsnobject.optString("output")) && jsnobject.optString("output").contains("Invalid"))
                    {
                        showCustomDialog(""+result, false);

                    }else
                    {

                        SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.past_jobs, jsnobject.optString("past_jobs"));
                        SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.today_jobs, jsnobject.optString("today_jobs"));
                        SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.future_jobs, jsnobject.optString("future_jobs"));


                        SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.user_id, userId);
                        SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.password, password);
                        String username= "",today_date = "",yesterday_date = "",tomorrow_date = "";
                        String jobCount= "";
                        String  today_jobs = jsnobject.optString("today_jobs");
                        if(Utility.isNotEmpty(""+today_jobs))
                        {
                            try {
                                JSONArray jsonArray = new JSONArray(""+today_jobs);
                                if(jsonArray.length() != 0){
                                    for (int i =0;i<jsonArray.length();i++){
                                        JobModel objectFromJson = JsonUtil.getObjectFromJson(jsonArray.getJSONObject(i), JobModel.class);
                                        username = objectFromJson.getDriverName();
                                        today_date = objectFromJson.getPickupDate();
                                        Log.d("username","- "+username);
                                    }
                                    SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.today_job_count, ""+jsonArray.length());
                                }else
                                {
                                    SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.today_job_count, "0");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                        SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.today_date, jsnobject.optString("today_tab"));
                        SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.yesterday_date, jsnobject.optString("past_tab"));
                        SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.tomorrow_date, jsnobject.optString("future_tab"));


                        JrWayDao.updateUserDetails(Login.this,today_jobs);

                        if(Utility.isNotEmpty(username))
                        {
                            SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.user_name, username);
                        }


                        if(Utility.isNotEmpty(jsnobject.optString("panlocations")))
                        {
                            SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.panlocations_data, jsnobject.optString("panlocations"));
                        }else
                        {
                            SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.panlocations_data, "");
                        }



                        /*String  past_jobs = jsnobject.optString("past_jobs");
                        if(Utility.isNotEmpty(""+past_jobs))
                        {
                            try {
                                JSONArray jsonArray = new JSONArray(""+past_jobs);
                                if(jsonArray.length() != 0){
                                    for (int i =0;i<jsonArray.length();i++){
                                        JobModel objectFromJson = JsonUtil.getObjectFromJson(jsonArray.getJSONObject(i), JobModel.class);
                                        yesterday_date = objectFromJson.getPickupDate();
                                    }
                                    SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.yesterday_date, yesterday_date);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }*/

                        /*String future_jobs = jsnobject.optString("future_jobs");
                        if(Utility.isNotEmpty(""+future_jobs))
                        {
                            try {
                                JSONArray jsonArray = new JSONArray(""+future_jobs);
                                if(jsonArray.length() != 0){
                                    for (int i =0;i<jsonArray.length();i++){
                                        JobModel objectFromJson = JsonUtil.getObjectFromJson(jsonArray.getJSONObject(i), JobModel.class);
                                        tomorrow_date = objectFromJson.getPickupDate();
                                    }
                                    //SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.tomorrow_date, tomorrow_date);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }*/



                        SharedPref.getInstance().setSharedValue(Login.this, hasLoggedIn, true);

                        String job_status = SharedPref.getStringValue(Login.this,Utility.AppData.job_status);

                        if(Utility.isNotEmpty(job_status))
                        {
                            if(job_status.equalsIgnoreCase(Utility.AppData.job_started) || job_status.equalsIgnoreCase(Utility.AppData.job_pickuped))
                            {
                                Intent service = new Intent(Login.this, LocationService.class);
                                startService(service);
                                LocationService locationService = new LocationService(Login.this);
                            }
                        }


                        gettimeutc();
                        createNotificationChannel();

                        Intent i = new Intent(Login.this, DriverHome.class);
                        startActivity(i);
                        finish();
                    }


                } catch (JSONException e) {
                    showCustomDialog(""+result, false);
                    e.printStackTrace();
                }

            }else
            {
                showCustomDialog(getResources().getString(R.string.checkYourInternetConnection), true);
            }
        }
    }


    private void gettimeutc(){

        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Login.this, RestartServiceBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarms.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+30*1000, pendingIntent);

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.CHANNEL_ID);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID), name, importance);
            NotificationManager notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }





    private void showCustomDialog(String msg, final boolean retryAPI)
    {
        try {
            boolean cancelButtonFalg = false;
            boolean cancelDialog = true;
            //String message = getResources().getString(R.string.checkYourInternetConnection);;
            Utility.showCustomDialogWithHeaderNew(Login.this, "BTR", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
                @Override                                                              //cancelButton yes r no flag
                public void confirmed(boolean status) {  // true ok butoon
                    try
                    {
                        if(retryAPI)
                        {
                            if(Utility.isNotEmpty(userId.getText().toString()) && Utility.isNotEmpty(password.getText().toString()))
                            {
                                openDriverPage(userId.getText().toString(),password.getText().toString());
                            }
                        }


                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
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
                    Toast.makeText(getApplicationContext(),R.string.location_permission_denied,Toast.LENGTH_LONG).show();

                }
                break;
        }
    }



    private void callForgetPasswordDialog()
    {
        try {

            final android.app.AlertDialog.Builder alert_dialog= new android.app.AlertDialog.Builder(this);
            alert_dialog.setCancelable(true);
            LayoutInflater inflater = LayoutInflater.from(this);
            final View dialogView=inflater.inflate(R.layout.forget_password_popup_msg, null);
            final FancyButton popup_yes_btn =dialogView.findViewById(R.id.popup_yes_btn);
            final EditText emailAddress =dialogView.findViewById(R.id.emailAddress);
            alert_dialog.setView(dialogView);
            final android.app.AlertDialog dialog = alert_dialog.create();
            dialog.show();

         popup_yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String email = emailAddress.getText().toString();
                    if(Utility.isNotEmpty(email))
                    {
                        dialog.dismiss();
                        new ForgetPassword(email).execute();

                    }else
                    {
                        Toast.makeText(getApplicationContext(), "Enter your valid email", Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /*popup_cancel_btn.setOnClickListener(new View.OnClickListener() {
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



        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }



}

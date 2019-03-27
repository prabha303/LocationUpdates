package prabhalab.client.location.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import mehdi.sakout.fancybuttons.FancyButton;
import prabhalab.client.location.APIEngine.APIEngine;
import prabhalab.client.location.MainActivity;
import prabhalab.client.location.R;
import prabhalab.client.location.SharedPref;
import prabhalab.client.location.Utility;
import prabhalab.client.location.driverhome.DriverHome;


/**
 * Created by PrabhagaranR on 22-03-19.
 */

public class Login extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = Login.class.getSimpleName();
    FancyButton login;
    TextView mLatitude,mLongitude,mTimestamp,status,mAddress,lastTripKM;
    EditText password,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        try
        {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
            }

            initializeUI();
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
            login = findViewById(R.id.login);
            userId = findViewById(R.id.userId);
            password = findViewById(R.id.password);


            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openDriverPage();
                }
            });




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

    private void openDriverPage()
    {
        String user = userId.getText().toString();
        String pwd = password.getText().toString();
        if(validate(user,pwd))
        {
            new ProcessLogin(user,pwd).execute();
        }else
        {
            Toast.makeText(Login.this, "UserId, password should not empty", Toast.LENGTH_SHORT).show();
        }




        //String response  = APIEngine.validateUser(this,userId.getText().toString(),password.getText().toString());
        //JSONObject jsnobject = new JSONObject(response);






        /*Intent i = new Intent(Login.this, DriverHome.class);
        startActivity(i);*/
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
                String SOAP_ACTION = "http://btr-ltd.net/Webservice/ValidateUser";
                String URL = "http://btr-ltd.net/Webservice/WebServicePartner.asmx";
                String NAMESPACE = "http://btr-ltd.net/Webservice/";
                String METHOD_NAME = "ValidateUser";
                SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
                soapObject.addProperty("userName",userId);
                soapObject.addProperty("password",password);
                soapObject.addProperty("fcmid","ok");
                SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(soapObject);
                HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
                try {
                    httpTransportSE.call(SOAP_ACTION, envelope);
                    SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
                    result = soapPrimitive.toString();
                    Log.d("result","--"+result);
                } catch (Exception e) {
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
                    if(Utility.isNotEmpty(jsnobject.optString("output")) && jsnobject.optString("output").equalsIgnoreCase("false"))
                    {
                        showCustomDialog(getResources().getString(R.string.login_failed), false);

                    }else
                    {

                        SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.past_jobs, jsnobject.optString("past_jobs"));
                        SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.today_jobs, jsnobject.optString("today_jobs"));
                        SharedPref.getInstance().setSharedValue(Login.this, Utility.AppData.future_jobs, jsnobject.optString("future_jobs"));

                        Intent i = new Intent(Login.this, DriverHome.class);
                        startActivity(i);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else
            {
                showCustomDialog(getResources().getString(R.string.checkYourInternetConnection), true);
            }
        }
    }


    private void showCustomDialog(String msg, final boolean retryAPI)
    {
        try {
            boolean cancelButtonFalg = false;
            boolean cancelDialog = true;
            //String message = getResources().getString(R.string.checkYourInternetConnection);;
            Utility.showCustomDialogWithHeader(Login.this, "BTR", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
                @Override                                                              //cancelButton yes r no flag
                public void confirmed(boolean status) {  // true ok butoon
                    try
                    {
                        if(retryAPI)
                        {
                            if(Utility.isNotEmpty(userId.getText().toString()) && Utility.isNotEmpty(password.getText().toString()))
                            {
                                openDriverPage();
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



}

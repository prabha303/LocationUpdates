package prabhalab.client.location.job;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Response;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;
import prabhalab.client.location.BuildConfig;
import prabhalab.client.location.JrWayDao;
import prabhalab.client.location.MainActivity;
import prabhalab.client.location.R;
import prabhalab.client.location.SharedPref;
import prabhalab.client.location.Utility;
import prabhalab.client.location.WayPoint;
import prabhalab.client.location.login.FontAweSomeTextView;
import prabhalab.client.location.login.Login;

import static android.Manifest.permission_group.CAMERA;
import static prabhalab.client.location.Utility.AppData.job_started;


/**
 * Created by PrabhagaranR on 01-03-19.
 */

public class EndTrip extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = EndTrip.class.getSimpleName();
    String FlightNumber="", job_Id = "",panLocationsId = "";
    LinearLayout signLayout,checklist_layout,extra_amount_layout;
    EditText waiting_amount, parking_amount,toll_cc_amount,amendment_amount,phone_amount,others_amount,service_charge_amt,notes;
    FancyButton go_next,submit,go_back,clear_sign;
    EditText trip_sheet_ref;
    ImageView navigation;
    TextView totaKM_TextView,totalTime;
    LinearLayout toll_layout,parking_layout,dutysheet_layout;
    private DrawingView drawView;
    String pickupAddress = "", dropAddress = "";
    String journeyEndTime_google = "0",finishingKm_google = "0";
    ImageView toll_image;
    FontAweSomeTextView toll_font;
    TextView toll_text;
    boolean signFlag  = false;
    int CAMERA_PERMISSION_CODE = 100;
    String toll_cemara_imgpath = "";
    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_trip);
        try
        {
            Intent intent = getIntent();
            job_Id = intent.getStringExtra("refId");
            FlightNumber = intent.getStringExtra("FlightNumber");
            panLocationsId = intent.getStringExtra("panLocationsId");
            init();

            String  pickupLatlng = getPickipLatLng();
            getpickupAddress(pickupLatlng);

            if(Utility.isNotEmpty(Utility.getLatLng(EndTrip.this)))
            {
                getDropAddress(Utility.getLatLng(EndTrip.this));
            }


            calculateGoogleDistance();

            verifyStoragePermissions(this);

            //JSONArray watPoints = JrWayDao.getAllWaypoints(EndTrip.this);



        }catch (Exception e)
        {
            e.printStackTrace();
        }


        /*

        tab  selected job count
        card view should show if flight number


        calculate km
        calculate time
        google distance , time, canvse image to string
        end journey api finish



      <jobID>string</jobID>
      <hours>string</hours>
      <PaxOnBoardlatlog>pickup location </PaxOnBoardlatlog>
      <PaxOnBoardAddress>pickup location Address</PaxOnBoardAddress>
      <PaxOnBoardDatetime>pickup time </PaxOnBoardDatetime>FlightNumber

      <PaxDropOfflatlog>end location lat</PaxDropOfflatlog>

      <PaxDropOffAddress>end location address</PaxDropOffAddress>

      <PaxDropffDatetime>end location time </PaxDropffDatetime>


      <WayPoints>all location</WayPoints>

      <TotalKm>total km </TotalKm>

      <TravellerSignature>string conversion </TravellerSignature>




      <parkingExtras>string</parkingExtras>

      <tollsExtras>string</tollsExtras>

      <cancellationExtras>0</cancellationExtras>
      <othersExtras>string</othersExtras>
      <journeyStartTime>start time when button clicks </journeyStartTime>
      <passengerDropOffTime>end location time</passengerDropOffTime>

        <journeyEndTime> start location and end location between google duration </journeyEndTime>
        <finishingKm>start location and end location between google km</finishingKm>

      <startingKm>1</startingKm>

      <pickupKm>10.00</pickupKm>  ( this will calculate from start to pickup

      <dropOffKM>string</dropOffKM> (status pick up to end between location km )



      <isCheckedFixedRate>false</isCheckedFixedRate>

      <SelectedFixedPrice>true</SelectedFixedPrice>
      <panLocationID>""</panLocationID>
      <isairportornormal>string</isairportornormal>   - is flight number is there need to send..
*/
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
        }
    }




    private void calculateGoogleDistance()
    {

        String start_loc = SharedPref.getStringValue(EndTrip.this, Utility.AppData.trip_start_loc);
        String end_loc = Utility.getLatLng(this);


        if(Utility.isNotEmpty(start_loc) && Utility.isNotEmpty(end_loc))
        {
            String[] s_latLng = start_loc.split(",");
            String[] n_latLng = end_loc.split(",");
            double s_latitude = Double.parseDouble(s_latLng[0]);
            double s_longitude = Double.parseDouble(s_latLng[1]);
            double n_latitude = Double.parseDouble(n_latLng[0]);
            double n_longitude = Double.parseDouble(n_latLng[1]);
            String key = BuildConfig.APIKEY;
            final String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="+s_latitude+","+s_longitude+"&destinations="+n_latitude+","+n_longitude +"&key="+key;

            new AsyncTask<Void, Void, Boolean>()
            {
                protected Boolean doInBackground(Void... params)
                {
                    try {
                       String  data = downloadUrl(url);
                       Log.d("data", ""+data);
                        Gson gson = new GsonBuilder().create();
                        GoogleResponsePojo distanceAndDuration = gson.fromJson(data, GoogleResponsePojo.class);
                       // String  d_address = distanceAndDuration.getDestination_addresses().get(0);
                        //String  s_address = distanceAndDuration.getOrigin_addresses().get(0);
                        String  duration = distanceAndDuration.getRows().get(0).getElements().get(0).getDuration().getText();
                        String  distance = distanceAndDuration.getRows().get(0).getElements().get(0).getDistance().getText();

                        if(Utility.isNotEmpty(duration))
                        {
                            String[] separated = duration.split(" ");
                            journeyEndTime_google = separated[0];
                        }else
                        {
                            journeyEndTime_google = "0";
                        }

                        if(Utility.isNotEmpty(distance))
                        {
                            String[] separated = distance.split(" ");
                            finishingKm_google = separated[0];
                        }else
                        {
                            finishingKm_google = "0";
                        }
                    } catch (RuntimeException e)
                    {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
       }
     }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);//5Seconds.
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void tripEnd()
    {
        try {
            String  msg = "Are you sure you want end the trip?";
            boolean cancelButtonFalg = false;
            boolean cancelDialog = true;
            //String message = getResources().getString(R.string.checkYourInternetConnection);;
            Utility.showCustomDialogWithHeaderNew(EndTrip.this, "Confirmation", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
                @Override                                                              //cancelButton yes r no flag
                public void confirmed(boolean status) {  // true ok butoon
                    try
                    {

                        callEndTrip();

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





    private void callEndTrip()
    {

        if(!signFlag)
        {
            Toast.makeText(EndTrip.this, "put your signature", Toast.LENGTH_LONG).show();
            return;
        }

        if(!Utility.isNotEmpty(Utility.getLatLng(this)))
        {
            Toast.makeText(EndTrip.this, "Location not detected, move somewhere ", Toast.LENGTH_LONG).show();
            return;
        }


        signLayout.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(signLayout.getDrawingCache());
        //encoded = basePath + Utility.bitmapToBase64(bitmap);
        signLayout.setDrawingCacheEnabled(true);
        //zoomView.setDrawingCacheEnabled(false);
        if(bitmap == null)
        {
            init();
            Toast.makeText(EndTrip.this, "Please Sign to continue!", Toast.LENGTH_LONG).show();
            return;
        }

        String signature = getSignatureFromBitmap(bitmap);
        Log.d("signature",signature);

        JSONArray watPoints = JrWayDao.getAllWaypoints(EndTrip.this);

        EndTripPojo endTripPojo = new EndTripPojo();
        endTripPojo.setJobID(job_Id);
        endTripPojo.setTripTime(getTripTime());
        endTripPojo.setPickupLatlng(getPickipLatLng());
        endTripPojo.setPickupTime(getPickipTime());
        endTripPojo.setPickup_address(pickupAddress);
        endTripPojo.setDrop_location(Utility.getLatLng(this));
        endTripPojo.setDropTime(getDropTime());
        endTripPojo.setDrop_address(dropAddress);
        endTripPojo.setWayPointList(""+watPoints);
        endTripPojo.setStartingKm("1");

        String  pickUpKm = JrWayDao.getPickupKM(EndTrip.this);
        if(!Utility.isNotEmpty(pickUpKm))
        {
            pickUpKm = "0";
        }
        String  dropKm = JrWayDao.getDropKM(EndTrip.this);
        if(!Utility.isNotEmpty(dropKm))
        {
            dropKm = "0";
        }

        String totalKM = JrWayDao.getTotalKM(EndTrip.this);
        if(!Utility.isNotEmpty(totalKM))
        {
            totalKM = "0";
        }

        endTripPojo.setTotalpickupKM(pickUpKm);
        endTripPojo.setTotalDropKM(dropKm);
        endTripPojo.setGetTotalKM(totalKM);


        endTripPojo.setJourneyEndTime(getDropTime());

        endTripPojo.setFinishingKm(finishingKm_google);

        String  waiting  = waiting_amount.getText().toString();
        String  parking  = parking_amount.getText().toString();
        String  tollcc  = toll_cc_amount.getText().toString();
        String  amendment  = amendment_amount.getText().toString();
        String  phone  = phone_amount.getText().toString();
        String  others  = others_amount.getText().toString();
        String  service  = service_charge_amt.getText().toString();
        String  note  = notes.getText().toString();
        if(!Utility.isNotEmpty(waiting))
        {
            waiting = "0";
        }
        if(!Utility.isNotEmpty(parking))
        {
            parking = "0";
        }
        if(!Utility.isNotEmpty(tollcc))
        {
            tollcc = "0";
        }
        if(!Utility.isNotEmpty(amendment))
        {
            amendment = "0";
        }
        if(!Utility.isNotEmpty(phone))
        {
            phone = "0";
        }
        if(!Utility.isNotEmpty(others))
        {
            others = "0";
        }

        if(!Utility.isNotEmpty(service))
        {
            service = "0";
        }
        if(!Utility.isNotEmpty(note))
        {
            note = "no notes";
        }
        endTripPojo.setWaitingExtras(waiting);
        endTripPojo.setParkingExtras(parking);
        endTripPojo.setTollsExtras(tollcc);
        endTripPojo.setAmendmentExtras(amendment);
        endTripPojo.setPhoneExtras(phone);
        endTripPojo.setOthersExtras(others);
        endTripPojo.setServiceChargeExtras(service);
        endTripPojo.setNote(note);
        endTripPojo.setCancellationExtras("0");
        endTripPojo.setTravellerSignature(signature);

        endTripPojo.setPassengerOnBoardTime(getPickipTime());
        endTripPojo.setPassengerDropOffTime(getDropTime());

        endTripPojo.setJourneyStartTime(getStartTime());
        endTripPojo.setNote(notes.getText().toString());

        endTripPojo.setIsCheckedFixedRate("false");
        endTripPojo.setSelectedFixedPrice("true");
        endTripPojo.setPanLocationID(panLocationsId);
        endTripPojo.setIsairportornormal(FlightNumber);
        endTripPojo.setBreaksRating("false");
        endTripPojo.setOverSpeedRating("false");
        endTripPojo.setTrafficviolateRating("false");
        endTripPojo.setPhoneWhileDriveRating("false");
        Log.d("endTripPojo",""+endTripPojo);
        new ProcessEndTrip(endTripPojo).execute();

    }



    public  class ProcessEndTrip extends AsyncTask {
        EndTripPojo endTripPojo;
        public ProcessEndTrip(EndTripPojo endTripPojo) {
            this.endTripPojo = endTripPojo;

        }
        protected void onPreExecute() {
            Utility.getInstance().showLoadingDialog(EndTrip.this);
        }
        protected String doInBackground(Object... params) {
            String result = "";
            try
            {
                String SOAP_ACTION = BuildConfig.SOAP_ACTION +"EndJourney";
                String URL = BuildConfig.BLT_BASEURL;
                String NAMESPACE = BuildConfig.NAMESPACE;
                String METHOD_NAME = "EndJourney";
                SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
                soapObject.addProperty("jobID",endTripPojo.getJobID());
                soapObject.addProperty("hours",endTripPojo.getTripTime());
                soapObject.addProperty("PaxOnBoardlatlog",getPickipLatLng());
                soapObject.addProperty("PaxOnBoardAddress",endTripPojo.getPickup_address());
                soapObject.addProperty("PaxOnBoardDatetime",endTripPojo.getPickupTime());
                soapObject.addProperty("PaxDropOfflatlog",endTripPojo.getDrop_location());
                soapObject.addProperty("PaxDropOfflatlog",endTripPojo.getDrop_location());
                soapObject.addProperty("PaxDropOffAddress",endTripPojo.getDrop_address());
                soapObject.addProperty("PaxDropffDatetime",endTripPojo.getDropTime());
                soapObject.addProperty("WayPoints",endTripPojo.getWayPointList());
                soapObject.addProperty("startingKm",endTripPojo.getStartingKm());
                soapObject.addProperty("pickupKm",endTripPojo.getTotalpickupKM());
                soapObject.addProperty("dropOffKM",endTripPojo.getTotalDropKM());
                soapObject.addProperty("finishingKm",endTripPojo.getFinishingKm());
                soapObject.addProperty("TotalKm",endTripPojo.getGetTotalKM());
                soapObject.addProperty("waitingExtras",endTripPojo.getWaitingExtras());
                soapObject.addProperty("parkingExtras",endTripPojo.getParkingExtras());
                soapObject.addProperty("tollsExtras",endTripPojo.getTollsExtras());
                soapObject.addProperty("amendmentExtras",endTripPojo.getAmendmentExtras());
                soapObject.addProperty("phoneExtras",endTripPojo.getPhoneExtras());
                soapObject.addProperty("cancellationExtras",endTripPojo.getCancellationExtras());
                soapObject.addProperty("othersExtras",endTripPojo.getOthersExtras());
                soapObject.addProperty("serviceChargeExtras",endTripPojo.getServiceChargeExtras());
                soapObject.addProperty("notes",endTripPojo.getNote());
                soapObject.addProperty("TravellerSignature",endTripPojo.getTravellerSignature());
                soapObject.addProperty("passengerOnBoardTime",endTripPojo.getPassengerOnBoardTime());
                soapObject.addProperty("journeyStartTime",endTripPojo.getJourneyStartTime());
                soapObject.addProperty("passengerDropOffTime",endTripPojo.getPassengerDropOffTime());
                soapObject.addProperty("journeyEndTime",endTripPojo.getJourneyEndTime());
                soapObject.addProperty("isCheckedFixedRate",endTripPojo.getIsCheckedFixedRate());
                soapObject.addProperty("SelectedFixedPrice",endTripPojo.getSelectedFixedPrice());
                soapObject.addProperty("panLocationID",endTripPojo.getPanLocationID());
                soapObject.addProperty("isairportornormal",endTripPojo.getIsairportornormal());
                soapObject.addProperty("breaksRating",endTripPojo.getBreaksRating());
                soapObject.addProperty("overSpeedRating",endTripPojo.getOverSpeedRating());
                soapObject.addProperty("trafficviolateRating",endTripPojo.getTrafficviolateRating());
                soapObject.addProperty("phoneWhileDriveRating",endTripPojo.getPhoneWhileDriveRating());
                soapObject.addProperty("username", SharedPref.getStringValue(EndTrip.this, Utility.AppData.user_id));
                soapObject.addProperty("password",SharedPref.getStringValue(EndTrip.this, Utility.AppData.password));

                Log.d("soapObject",""+soapObject.toString());
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
                }catch (Exception e) {
                    result = e.getMessage();
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
            String  res = ""+result;
            Utility.getInstance().closeLoadingDialog();
            Log.d("result_EndJourney","--"+result);
            if(Utility.isNotEmpty(res) && res.contains("completed successfully"))
            {
                JrWayDao.getInstance().deleteTripData(EndTrip.this);
                showSuccessResponse(res);

            }else
            {
                showCustomDialog(res, false);
            }
        }
    }



    private void showCustomDialog(String msg, final boolean retryAPI)
    {
        try {
            boolean cancelButtonFalg = false;
            boolean cancelDialog = true;
            //String message = getResources().getString(R.string.checkYourInternetConnection);;
            Utility.showCustomDialogWithHeaderNew(EndTrip.this, "BTR", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
                @Override                                                              //cancelButton yes r no flag
                public void confirmed(boolean status) {  // true ok butoon
                    try
                    {
                        if(retryAPI)
                        {
                            callEndTrip();
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


    private void showSuccessResponse(String msg)
    {
        try {
            boolean cancelButtonFalg = false;
            boolean cancelDialog = false;
            //String message = getResources().getString(R.string.checkYourInternetConnection);;
            Utility.showCustomDialogWithHeaderNew(EndTrip.this, "BTR", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
                @Override                                                              //cancelButton yes r no flag
                public void confirmed(boolean status) {  // true ok butoon
                    try
                    {

                        Intent intent1 = new Intent(EndTrip.this, Login.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent1);
                        finish();

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






    private String getSignatureFromBitmap(Bitmap sign)
    {
        String img = "";
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            sign.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            img = Base64.encodeToString(b, Base64.DEFAULT);
            img = "data:image/gif;base64," + img;

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return img;
    }

    public Bitmap StringToBitMap(String image){
        try{
            byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }


    private void getDropAddress(String  locationData) {
        String[] latlong =  locationData.split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);

        final LatLng location = new LatLng(latitude,longitude);

        new AsyncTask<Void, Void, Boolean>()
        {
            protected Boolean doInBackground(Void... params)
            {

                String resultAdress = "";
                Geocoder geocoder = new Geocoder(EndTrip.this, Locale.getDefault());
                List<Address> addresses = null;

                try {
                    addresses = geocoder.getFromLocation(location.latitude,location.longitude,1); // In this sample, get just a single address
                } catch (IOException ioException) {
                    //resultMessage = MainActivity.this .getString(R.string.service_not_available);
                    Log.e(TAG, resultAdress, ioException);
                }
                if (addresses == null || addresses.size() == 0) {
                    if (resultAdress.isEmpty()) {
                        //resultMessage = MainActivity.this.getString(R.string.no_address_found);
                        // Log.e(TAG, resultMessage);
                    }
                } else {
                    Address address = addresses.get(0);
                    StringBuilder out = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        out.append(address.getAddressLine(i));
                    }
                    dropAddress = out.toString();
                }
                return null;
            }
        }.execute();
    }


    private void getpickupAddress(String  locationData) {


        String[] latlong =  locationData.split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);

        final LatLng location = new LatLng(latitude,longitude);

        new AsyncTask<Void, Void, Boolean>()
        {
            protected Boolean doInBackground(Void... params)
            {

                String resultAdress = "";
                Geocoder geocoder = new Geocoder(EndTrip.this, Locale.getDefault());
                List<Address> addresses = null;

                try {
                    addresses = geocoder.getFromLocation(location.latitude,location.longitude,1); // In this sample, get just a single address
                } catch (IOException ioException) {
                    //resultMessage = MainActivity.this .getString(R.string.service_not_available);
                    Log.e(TAG, resultAdress, ioException);
                }
                if (addresses == null || addresses.size() == 0) {
                    if (resultAdress.isEmpty()) {
                        //resultMessage = MainActivity.this.getString(R.string.no_address_found);
                        // Log.e(TAG, resultMessage);
                    }
                } else {
                    Address address = addresses.get(0);
                    StringBuilder out = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        out.append(address.getAddressLine(i));
                    }
                    pickupAddress = out.toString();


                }
                return null;
            }
        }.execute();
    }

    private String getTripTime()
    {
        long diffInMin = 0;

        try
        {
            DecimalFormat dtime = new DecimalFormat("#.##");
            String start_time = SharedPref.getStringValue(EndTrip.this, Utility.AppData.start_time);
            if(Utility.isNotEmpty(start_time))
            {
                long endTime = System.currentTimeMillis();
                long startTime = Long.parseLong(start_time);
                long diff = endTime - startTime ;
                diffInMin = TimeUnit.MILLISECONDS.toMinutes(diff);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return ""+diffInMin ;
    }

    private String getTripTimeDisplay()
    {
        try
        {
            DecimalFormat dtime = new DecimalFormat("#.##");
            String start_time = SharedPref.getStringValue(EndTrip.this, Utility.AppData.start_time);
            if(Utility.isNotEmpty(start_time))
            {
                long endTime = System.currentTimeMillis();
                long startTime = Long.parseLong(start_time);
                long different = endTime - startTime ;
                long secondsInMilli = 1000;
                long minutesInMilli = secondsInMilli * 60;
                long hoursInMilli = minutesInMilli * 60;

                long elapsedHours = different / hoursInMilli;
                different = different % hoursInMilli;

                long elapsedMinutes = different / minutesInMilli;
                different = different % minutesInMilli;

                String  time  = "";
                if(elapsedHours >0)
                {
                    time = elapsedHours + " hrs "+ elapsedMinutes + " min";

                }else
                {
                    time = elapsedMinutes + " min";
                }
                return time;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return "" ;
    }



    private String getDropTime()
    {
        long  timeMillis = System.currentTimeMillis();
        Date curDateTime = new Date(timeMillis);
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:MM");
        return  sdf.format(curDateTime);
    }


    private String getPickipTime()
    {
        String pickup_time = SharedPref.getStringValue(EndTrip.this, Utility.AppData.pickup_time);
        if(Utility.isNotEmpty(pickup_time))
        {
           return pickup_time;
        }
        return "";
    }

    private String getPickipLatLng()
    {
        String pickup_latlng = SharedPref.getStringValue(EndTrip.this, Utility.AppData.pickup_location_latlng);
        if(Utility.isNotEmpty(pickup_latlng))
        {
            return pickup_latlng;
        }
        return "";
    }

    private String getStartTime()
    {
        String start_time = SharedPref.getStringValue(EndTrip.this, Utility.AppData.start_time);
        Date curDateTime = new Date(Long.parseLong(start_time));
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:MM");
        final String startTime = sdf.format(curDateTime);

        if(Utility.isNotEmpty(startTime))
        {
            return startTime;
        }
        return "";
    }





    private void init()
    {
        extra_amount_layout = findViewById(R.id.extra_amount_layout);
        checklist_layout = findViewById(R.id.checklist_layout);

        extra_amount_layout.setVisibility(View.VISIBLE);
        checklist_layout.setVisibility(View.GONE);

        waiting_amount = findViewById(R.id.waiting_amount);
        parking_amount = findViewById(R.id.parking_amount);
        toll_cc_amount = findViewById(R.id.toll_cc_amount);
        amendment_amount = findViewById(R.id.amendment_amount);
        phone_amount = findViewById(R.id.phone_amount);
        others_amount = findViewById(R.id.others_amount);
        service_charge_amt = findViewById(R.id.service_charge_amt);
        notes = findViewById(R.id.notes);
        go_next = findViewById(R.id.go_next);
        drawView = findViewById(R.id.drawing);
        drawView.startNew();
        clear_sign = findViewById(R.id.clear_sign);
        go_back = findViewById(R.id.go_back);
        submit = findViewById(R.id.submit);
        signLayout = findViewById(R.id.signLayout);
        totalTime = findViewById(R.id.totalTime);
        totaKM_TextView = findViewById(R.id.totaKM);


        toll_layout = findViewById(R.id.toll_layout);
        toll_image = findViewById(R.id.toll_image);
        toll_font = findViewById(R.id.toll_font);
        toll_text = findViewById(R.id.toll_font);


        parking_layout = findViewById(R.id.parking_layout);
        dutysheet_layout = findViewById(R.id.dutysheet_layout);



        totalTime.setText(getTripTimeDisplay());

        totaKM_TextView.setText(JrWayDao.getTotalKM(EndTrip.this) + " km");


        toll_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    int permissionCheck = ContextCompat.checkSelfPermission(EndTrip.this, Manifest.permission.CAMERA);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(EndTrip.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                    }else
                    {
                        selectImage();
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }



            }
        });

        parking_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        dutysheet_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



        drawView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                signFlag = true;

                return false;
            }
        });



        go_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showNextStep();

                if(Utility.isNotEmpty(finishingKm_google) /*|| Utility.isNotEmpty(journeyEndTime_google)*/)
                {
                    calculateGoogleDistance();
                }
            }
        });

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                backButton();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripEnd();
            }
        });


        clear_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signFlag = false;
                drawView.startNew();
            }
        });

        TextView my_toolbar_title = findViewById(R.id.my_toolbar_title);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        my_toolbar_title.setText("BTR Ref #"+job_Id);

     }




     private void showNextStep()
     {
         extra_amount_layout.setVisibility(View.GONE);
         checklist_layout.setVisibility(View.VISIBLE);
     }


    private void backButton()
    {
        extra_amount_layout.setVisibility(View.VISIBLE);
        checklist_layout.setVisibility(View.GONE);
    }


    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(EndTrip.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    processCameraClick();
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    private void processCameraClick()
    {
        try
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File image = new File(android.os.Environment.getExternalStorageDirectory(), "btr_temp.jpg");
            //\22Feb17.Adding these line bcoz In new test mobiles(Micromax and lenova), this path is not exist. So we explicitly create it
            //image.getParentFile().mkdirs();
            if(!image.exists())
            {
                boolean yes = image.createNewFile();
            }
            // Save a file: path for use with ACTION_VIEW intents
            toll_cemara_imgpath = image.getAbsolutePath();
            Log.d("imagePath",toll_cemara_imgpath);
            Uri mImageCaptureUri = null;

            int version = Build.VERSION.SDK_INT;
            if(version <23 || version ==23)
            {
                mImageCaptureUri = Uri.fromFile(image);
            }else
            {
                mImageCaptureUri = FileProvider.getUriForFile(EndTrip.this, BuildConfig.APPLICATION_ID + ".provider",image);
            }
            if(mImageCaptureUri != null)
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                startActivityForResult(intent, 1);
            }
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {

            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                selectImage();
            }
        }else if (requestCode == REQUEST_EXTERNAL_STORAGE) {

            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                //Toast.makeText(EndTrip.this, "REQUEST_EXTERNAL_STORAGE ", Toast.LENGTH_LONG).show();
            }

        }
    }

    public class UploadToServer extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        Bitmap resized_bitmap;
        String imageName = "";
        int imgProcess=0;
        int maxBufferSize = 1 * 1024 * 1024;
        byte[] buffer;
        int serverResponseCode = 0;

        UploadToServer(Bitmap bitmap,String imageId,int imgProcess) {
            this.resized_bitmap = bitmap;
            this.imageName = imageId;
            this.imgProcess = imgProcess;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(EndTrip.this, "", "Uploading File...", false);
        }

        @Override
        protected String doInBackground(String... args) {
            String serverResponseMessage =null;
            try {
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;

                String serverFileName= imageName+".jpeg";
                String SERVER_URL ="http://btr-ltd.net/webservice/SupportFilesLocation/"+serverFileName;  //http://btr-ltd.net/PMVTest/test.png
                Log.e("SERVER_URL", SERVER_URL.toString());

                //encoding image into a byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resized_bitmap.compress(Bitmap.CompressFormat.JPEG, 55, baos);
                byte[] imageBytes = baos.toByteArray();
                ByteArrayInputStream fileInputStream = new ByteArrayInputStream(imageBytes);
                URL url = new URL(SERVER_URL);
                Log.e("fileInputStream", fileInputStream.toString());
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"files\";filename=\"" + serverFileName+".jpeg" + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                //read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                serverResponseMessage = conn.getResponseMessage();
                Log.i("upload_File", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
                conn.disconnect();
                // image bit map...
                if(imgProcess == 1){
                    resized_bitmap = resized_bitmap;
                }else if(imgProcess ==2){
                    resized_bitmap = resized_bitmap;
                }else if(imgProcess ==3){
                    resized_bitmap = resized_bitmap;
                }
            } catch (Exception e) {
                e.printStackTrace();
                serverResponseMessage = "Unable to connect with server";
            }
            Log.d("serverResponseMessage", "message : " + serverResponseMessage);
            return serverResponseMessage;
        }

        @Override
        protected void onPostExecute(String res) {
            dialog.dismiss();
            try {
                if(Utility.isNotEmpty(res)){
                    if(res.equals("Created")){
                        res = "Photo uploaded successfully";
                    }
                }
                Toast.makeText(EndTrip.this,res,Toast.LENGTH_LONG).show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File filepath = new File(toll_cemara_imgpath);
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(filepath.getAbsolutePath(),bitmapOptions);
                    toll_image.setImageBitmap(bitmap);
                    toll_image.setVisibility(View.VISIBLE);
                    toll_font.setVisibility(View.GONE);
                    toll_text.setVisibility(View.GONE);
                    String path = android.os.Environment.getExternalStorageDirectory() + File.separator+ "Phoenix" + File.separator + "default";
                    filepath.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    UploadToServer task=new UploadToServer(bitmap,"toll_cemara_imgpath",1);
                    task.execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }





            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("path_from gallery", picturePath+"");
                toll_image.setImageBitmap(thumbnail);
                toll_image.setVisibility(View.VISIBLE);
                toll_font.setVisibility(View.GONE);
                toll_text.setVisibility(View.GONE);

                UploadToServer task=new UploadToServer(thumbnail,"toll_cemara_imgpath",1);
                task.execute();

            }
        }
    }

}

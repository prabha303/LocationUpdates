package prabhalab.client.location.job;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
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
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import prabhalab.client.location.JrWayDao;
import prabhalab.client.location.MainActivity;
import prabhalab.client.location.R;
import prabhalab.client.location.SharedPref;
import prabhalab.client.location.Utility;
import prabhalab.client.location.WayPoint;
import prabhalab.client.location.login.Login;

import static prabhalab.client.location.Utility.AppData.job_started;


/**
 * Created by PrabhagaranR on 01-03-19.
 */

public class EndTrip extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = EndTrip.class.getSimpleName();
    String FlightNumber="", job_Id = "";
    LinearLayout signLayout,checklist_layout,extra_amount_layout;
    EditText waiting_amount, parking_amount,toll_cc_amount,amendment_amount,phone_amount,others_amount,service_charge_amt,notes;
    FancyButton go_next,submit,go_back,clear_sign;
    EditText trip_sheet_ref;
    ImageView navigation;
    TextView totaKM_TextView,totalTime;
    private DrawingView drawView;
    String pickupAddress = "", dropAddress = "";
    String journeyEndTime_google = "",finishingKm_google = "";
    boolean signFlag  = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_trip);
        try
        {
            Intent intent = getIntent();
            job_Id = intent.getStringExtra("refId");
            FlightNumber = intent.getStringExtra("FlightNumber");
            init();

            String  pickupLatlng = getPickipLatLng();
            getpickupAddress(pickupLatlng);

            if(Utility.isNotEmpty(Utility.getLatLng(EndTrip.this)))
            {
                getDropAddress(Utility.getLatLng(EndTrip.this));
            }


            calculateGoogleDistance();


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
            String key = "AIzaSyAU9vy6IR3zwNxbWLzWT8iEdMG0NhiXDBQ";
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
                        String  d_address = distanceAndDuration.getDestination_addresses().get(0);
                        String  s_address = distanceAndDuration.getOrigin_addresses().get(0);
                        String  duration = distanceAndDuration.getRows().get(0).getElements().get(0).getDuration().getText();
                        String  distance = distanceAndDuration.getRows().get(0).getElements().get(0).getDistance().getText();

                       // journeyEndTime_google = duration;
                       // finishingKm_google = distance;


                        journeyEndTime_google = "10";
                        finishingKm_google = "10";

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
            Utility.showCustomDialogWithHeader(EndTrip.this, "Confirmation", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
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

        ArrayList<WayPoint> watPoints = JrWayDao.getAllWaypoints(EndTrip.this);

        JSONArray mJSONArray = new JSONArray(Arrays.asList(watPoints));



        EndTripPojo endTripPojo = new EndTripPojo();

        endTripPojo.setJobID(job_Id);
        endTripPojo.setTripTime(getTripTime());
        endTripPojo.setPickupLatlng(getPickipLatLng());
        endTripPojo.setPickupTime(getPickipTime());
        endTripPojo.setPickup_address(pickupAddress);
        endTripPojo.setDrop_location(Utility.getLatLng(this));
        endTripPojo.setDropTime(getDropTime());
        endTripPojo.setDrop_address(dropAddress);
        endTripPojo.setWayPointList("[]");
        endTripPojo.setStartingKm("1");
        endTripPojo.setTotalpickupKM(""+JrWayDao.getPickupKM(EndTrip.this));
        endTripPojo.setTotalDropKM(""+JrWayDao.getDropKM(EndTrip.this));
        endTripPojo.setGetTotalKM(""+JrWayDao.getTotalKM(EndTrip.this));


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
        endTripPojo.setPassengerOnBoardTime(getDropTime());
        endTripPojo.setJourneyStartTime(getStartTime());
        endTripPojo.setNote(notes.getText().toString());
        endTripPojo.setPassengerDropOffTime(getDropTime());
        endTripPojo.setIsCheckedFixedRate("false");
        endTripPojo.setSelectedFixedPrice("true");
        endTripPojo.setPanLocationID("310");
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
                String SOAP_ACTION = "http://btr-ltd.net/Webservice/EndJourney";
                String URL = "http://btr-ltd.net/Webservice/WebServicePartner.asmx";
                String NAMESPACE = "http://btr-ltd.net/Webservice/";
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
                } catch (Exception e) {
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

                Toast.makeText(EndTrip.this, res, Toast.LENGTH_LONG).show();
                Toast.makeText(EndTrip.this, res, Toast.LENGTH_LONG).show();


                Intent i = new Intent(EndTrip.this, Login.class);
                startActivity(i);
                finish();
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
            Utility.showCustomDialogWithHeader(EndTrip.this, "BTR", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
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
        DecimalFormat dtime = new DecimalFormat("#.##");
        String start_time = SharedPref.getStringValue(EndTrip.this, Utility.AppData.start_time);
        if(Utility.isNotEmpty(start_time))
        {
            long  endTime = System.currentTimeMillis();
            long startTime = Long.parseLong(start_time);
            long diff = endTime - startTime ;
            diffInMin = TimeUnit.MILLISECONDS.toMinutes(diff);
        }

        return ""+diffInMin;
    }


    private String getDropTime()
    {
        long  timeMillis = System.currentTimeMillis();
        Date curDateTime = new Date(timeMillis);
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY HH:MM");
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
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY HH:MM");
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



        totalTime.setText(getTripTime() + " min");
        totaKM_TextView.setText(JrWayDao.getTotalKM(EndTrip.this) + " km");






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

                // falback of loc
                if(Utility.isNotEmpty(finishingKm_google) || Utility.isNotEmpty(journeyEndTime_google))
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







}

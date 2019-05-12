package in.vendor.rides.job;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import mehdi.sakout.fancybuttons.FancyButton;
import in.vendor.rides.APIEngine.JsonUtil;
import in.vendor.rides.BuildConfig;
import in.vendor.rides.JrWayDao;
import in.vendor.rides.LocationService;
import in.vendor.rides.MainActivity;
import in.vendor.rides.PanLocationsPojo;
import in.vendor.rides.R;
import in.vendor.rides.SharedPref;
import in.vendor.rides.UpdateInterService;
import in.vendor.rides.Utility;
import in.vendor.rides.WayPoint;
import in.vendor.rides.driverhome.JobModel;
import in.vendor.rides.login.Login;

import static in.vendor.rides.Utility.AppData.job_pickuped;
import static in.vendor.rides.Utility.AppData.job_started;


/**
 * Created by PrabhagaranR on 01-03-19.
 */

public class StartTrip extends AppCompatActivity implements UpdateInterService{
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = StartTrip.class.getSimpleName();
    FancyButton end_track_button,start_track_button;
    TextView mLatitude,mLongitude,mTimestamp,status,mAddress,lastTripKM;
    String vehicleRegistrationNumber="",pickupAddress = "", dropAddress="",job_Id = "", FlightNumber ="", jobStatus = "";
    private GoogleMap googleMap;
    static  LatLng pickip_point = null;
    static  LatLng drop_point = null;
    static  Marker pickip_point_marker = null;
    static  Marker drop_point_marker = null;
    Marker marker = null;
    SupportMapFragment supportMapFragment;
    FancyButton start_trip,end_trip, pick_up;
    String  panLocationsId = "";
    EditText trip_sheet_ref;
    ImageView navigation;
    Spinner panlocation_spinner;
    ArrayList<PanLocationsPojo> panLocationsPojoArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_trip);
        try
        {
            Intent intent = getIntent();
            job_Id = intent.getStringExtra("refId");
            JobModel jobModel = JrWayDao.getInstance().getSingleJob(this,job_Id);
            vehicleRegistrationNumber = jobModel.getVehicleRegistrationNumber();
            pickupAddress = jobModel.getPickupAddress();
            dropAddress = jobModel.getDropAddress();
            jobStatus = jobModel.getJobStatus();
            FlightNumber = jobModel.getFlightNumber();
            init();

            String savedJobId = SharedPref.getStringValue(StartTrip.this, Utility.AppData.job_Id);

            if(Utility.isNotEmpty(savedJobId) && job_Id.equalsIgnoreCase(savedJobId))
            {
                jobStatus = SharedPref.getStringValue(StartTrip.this, Utility.AppData.job_status);

            }

            showButton();


            Utility.getInstance().stayScreenOn(this);
            loadingMapview();

            if(Utility.isNotEmpty(jobStatus))
            {
                if(Utility.isNotEmpty(pickupAddress))
                {
                    getLocationFromAddress(pickupAddress,0);
                }

                if(Utility.isNotEmpty(dropAddress))
                {
                    getLocationFromAddress(dropAddress,1);
                }

            }


        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showButton()
    {

        String savedJobId = SharedPref.getStringValue(StartTrip.this, Utility.AppData.job_Id);
        if(Utility.isNotEmpty(savedJobId) && job_Id.equalsIgnoreCase(savedJobId))
        {
            String trip_sheet_ref_number = SharedPref.getStringValue(StartTrip.this, Utility.AppData.trip_sheet_ref_number);
            if(Utility.isNotEmpty(trip_sheet_ref_number))
            {
                trip_sheet_ref.setText(trip_sheet_ref_number);
            }


            if(jobStatus.equalsIgnoreCase(job_pickuped))
            {
                end_trip.setVisibility(View.VISIBLE);
                pick_up.setVisibility(View.GONE);
                start_trip.setVisibility(View.GONE);
            }else if(jobStatus.equalsIgnoreCase(job_started))
            {
                end_trip.setVisibility(View.GONE);
                pick_up.setVisibility(View.VISIBLE);
                start_trip.setVisibility(View.GONE);
            }
        }else
        {
            start_trip.setVisibility(View.VISIBLE);
            pick_up.setVisibility(View.GONE);
            end_trip.setVisibility(View.GONE);
        }
    }


    private void init()
    {
        start_trip = findViewById(R.id.start_trip);
        end_trip = findViewById(R.id.end_trip);
        pick_up = findViewById(R.id.pick_up);
        trip_sheet_ref = findViewById(R.id.trip_sheet_ref);
        navigation = findViewById(R.id.navigation);

        panlocation_spinner = findViewById(R.id.panlocation_spinner);


        String panlocations_data = SharedPref.getStringValue(StartTrip.this, Utility.AppData.panlocations_data);
        if(Utility.isNotEmpty(panlocations_data))
        {
            try {
                JSONArray jsonArray = new JSONArray(""+panlocations_data);
                if(jsonArray.length() != 0){
                    panLocationsPojoArrayList = new ArrayList<>();

                    PanLocationsPojo panLocation = new PanLocationsPojo();
                    panLocation.setName("Select location");
                    panLocation.setID("");
                    panLocationsPojoArrayList.add(panLocation);

                    for (int i =0;i<jsonArray.length();i++){
                        PanLocationsPojo panLocationsPojo = new PanLocationsPojo();
                        PanLocationsPojo objectFromJson = JsonUtil.getObjectFromJson(jsonArray.getJSONObject(i), PanLocationsPojo.class);
                        panLocationsPojo.setID(objectFromJson.getID());
                        panLocationsPojo.setName(objectFromJson.getName());
                        panLocationsPojoArrayList.add(panLocationsPojo);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.d("panLocationsPojo",""+panLocationsPojoArrayList.size());

            PanLocationListAdapter spinnerAdapter = new PanLocationListAdapter(StartTrip.this, R.layout.spinner_row,panLocationsPojoArrayList);
            panlocation_spinner.setAdapter(spinnerAdapter);

            panlocation_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> adapter, View v, int position, long id)
                {
                    try
                    {
                        String displayName =  panLocationsPojoArrayList.get(position).getName();
                        panLocationsId =  panLocationsPojoArrayList.get(position).getID();
                        Log.d("displayName","-"+displayName);
                        Log.d("selectedId","-"+panLocationsId);
                        hideKeyboard(StartTrip.this);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0)
                {


                }
            });



        }

        start_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tripStart();
            }
        });

        pick_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(Utility.isNotEmpty(Utility.getLatLng(StartTrip.this)))
                {
                    showPicup();
                }else
                {
                    Toast.makeText(getApplicationContext(), "Location not detected, move somewhere ", Toast.LENGTH_LONG).show();
                }
            }
        });


        end_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showEndTripPage();
            }
        });


        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String  address = "";
                if(jobStatus.equalsIgnoreCase(job_started))
                {
                    address = pickupAddress;
                }else
                {
                    address = dropAddress;
                }




                Uri gmmIntentUri = Uri.parse("google.navigation:q="+address);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                //mapIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);//\To remove this app from recent task when moved to driver app.
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null)
                {
                    //startActivity(mapIntent);
                    startActivityForResult(mapIntent, 1003);

                }

            }
        });

        supportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps));
        TextView my_toolbar_title = findViewById(R.id.my_toolbar_title);
        TextView vehicle_reg = findViewById(R.id.vehicle_reg);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        //TextView billing_location = findViewById(R.id.billing_location);
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
        vehicle_reg.setText(vehicleRegistrationNumber);
        //billing_location.setText(pickupAddress);
    }


    private void startTrip () {
        String CurrentLocation = Utility.getLatLng(this);
        String  Jobsheet_ref = trip_sheet_ref.getText().toString();
        new ProcessStartTrip(job_Id,CurrentLocation, pickupAddress, Jobsheet_ref,vehicleRegistrationNumber).execute();

    }

    private void picupCustomer () {
       // String CurrentLocation = Utility.getLatLng(this);
       // String  Jobsheet_ref = trip_sheet_ref.getText().toString();
        //new ProcessStartTrip(job_Id,CurrentLocation, pickupAddress, Jobsheet_ref,vehicleRegistrationNumber).execute();

        SharedPref.getInstance().setSharedValue(StartTrip.this, Utility.AppData.job_status, job_pickuped);
        jobStatus = job_pickuped;
        long  timeMillis = System.currentTimeMillis();
        Date curDateTime = new Date(timeMillis);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final String pickupTime = sdf.format(curDateTime);

        SharedPref.getInstance().setSharedValue(StartTrip.this, Utility.AppData.pickup_location_latlng, Utility.getLatLng(StartTrip.this));
        SharedPref.getInstance().setSharedValue(StartTrip.this, Utility.AppData.pickup_time, pickupTime);


        showButton();

        Toast.makeText(getApplicationContext(), "Successfully pickedup!", Toast.LENGTH_LONG).show();
        hideKeyboard(StartTrip.this);
    }













    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1003:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    //Toast.makeText(Home.this, "External File Permission done!", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }


    private void loadingMapview() {
        try {
            supportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps));
            if (googleMap == null) {
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap gMap) {
                        //loadMap(googleMap);
                        googleMap = gMap;

                        setMap();
                    }
                });
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setMap()
    {

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setCompassEnabled(true);
        //googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
         /*
        //googleMap.getUiSettings().setMyLocationButtonEnabled(true);


        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                // TODO Auto-generated method stub
                setMarker(arg0);

            }
        });
     */
        Intent service = new Intent(getApplicationContext(), LocationService.class);
        startService(service);
        LocationService locationService = new LocationService(StartTrip.this,this);

    }


    /*private void setMarker(Location location)
    {
        if (location != null) {

            if (marker == null) {
                MarkerOptions options = new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title("Marker Title");
                marker = googleMap.addMarker(options);

            }else {
                marker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
            }

            if(pickip_point != null)
            {

                if(pickip_point_marker != null)
                {
                    pickip_point_marker.remove();
                }


                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(marker.getPosition());
                builder.include(pickip_point);

                MarkerOptions options = new MarkerOptions().position(pickip_point).title(pickupAddress);
                pickip_point_marker = googleMap.addMarker(options);

                LatLngBounds bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                googleMap.animateCamera(cu);

            }else
            {
                getLocationFromAddress(pickupAddress);
                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(15)                   // Sets the zoom
                        .bearing(0)// Sets the orientation of the camera to north /\  0 is east.
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
            }
        }
    }*/


    public void getLocationFromAddress(final String strAddress, final int points) {

        try
        {
            new AsyncTask<Void, Void, Boolean>()
            {
                protected Boolean doInBackground(Void... params)
                {

                    Geocoder coder = new Geocoder(StartTrip.this);
                    List<Address> address;
                    LatLng p1 = null;
                    try {
                        // May throw an IOException
                        address = coder.getFromLocationName(strAddress, 5);
                        if (address == null) {
                            return null;
                        }
                        Address location = address.get(0);

                        if(points == 0)
                        {
                            pickip_point = new LatLng(location.getLatitude(), location.getLongitude());
                        }else
                        {
                            drop_point = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    return null;
                }
            }.execute();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }






    @Override
    public void doUpdateLocation(Location location, String address) {

        if(googleMap != null)
        {
            googleMap.clear();
        }

        if(location != null)
        {

            String savedJobId = SharedPref.getStringValue(StartTrip.this, Utility.AppData.job_Id);
            if(Utility.isNotEmpty(savedJobId) && job_Id.equalsIgnoreCase(savedJobId))
            {
                jobStatus = SharedPref.getStringValue(StartTrip.this, Utility.AppData.job_status);
            }


            MarkerOptions options = new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon));
            marker = googleMap.addMarker(options);


            int padding = Utility.convertDpToPixel(40);



            if(Utility.isNotEmpty(jobStatus) && (jobStatus.equalsIgnoreCase(job_started) || jobStatus.equalsIgnoreCase(job_pickuped)))
            {
                if(jobStatus.equalsIgnoreCase(job_started))
                {
                    if(pickip_point != null)
                    {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(marker.getPosition());
                        MarkerOptions options_dest = new MarkerOptions().position(pickip_point).title(pickupAddress);
                        builder.include(pickip_point);
                        pickip_point_marker = googleMap.addMarker(options_dest);
                        LatLngBounds bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,  padding);
                        googleMap.animateCamera(cu);

                    }else
                    {
                        getLocationFromAddress(pickupAddress,0);
                        CameraPosition cameraPosition1 = new CameraPosition.Builder()
                                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                                .zoom(15)                   // Sets the zoom
                                .bearing(0)// Sets the orientation of the camera to north /\  0 is east.
                                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                                .build();

                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                    }


                }else if (jobStatus.equalsIgnoreCase(job_pickuped))
                {
                    if(drop_point != null)
                    {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(marker.getPosition());
                        MarkerOptions options_dest = new MarkerOptions().position(drop_point).title(dropAddress);
                        builder.include(drop_point);
                        drop_point_marker = googleMap.addMarker(options_dest);
                        LatLngBounds bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,  padding);
                        googleMap.animateCamera(cu);
                    }else
                    {
                        getLocationFromAddress(dropAddress,1);
                        CameraPosition cameraPosition1 = new CameraPosition.Builder()
                                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                                .zoom(15)                   // Sets the zoom
                                .bearing(0)// Sets the orientation of the camera to north /\  0 is east.
                                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                                .build();

                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                    }

                }else{
                    CameraPosition cameraPosition1 = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(15)                   // Sets the zoom
                            .bearing(0)// Sets the orientation of the camera to north /\  0 is east.
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                }
            }else
            {
                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(18)                   // Sets the zoom
                        .bearing(0)// Sets the orientation of the camera to north /\  0 is east.
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
            }
        }
    }




    public  class ProcessStartTrip extends AsyncTask {
        String vehicleRegistrationNumber, refId,CurrentLocation,tripSheetRef,pickupAddress;
        public ProcessStartTrip(String refId, String CurrentLocation, String pickupAddress, String Jobsheet_ref, String vehicleRegistrationNumber) {
            this.refId = refId;
            this.CurrentLocation = CurrentLocation;
            this.tripSheetRef = Jobsheet_ref;
            this.vehicleRegistrationNumber = vehicleRegistrationNumber;
            this.pickupAddress = pickupAddress;
        }
        protected void onPreExecute() {
            Utility.getInstance().showLoadingDialog(StartTrip.this);
        }
        protected String doInBackground(Object... params) {
            String result = "";
            try
            {
                String SOAP_ACTION = BuildConfig.SOAP_ACTION +"StartJourney";
                String URL = BuildConfig.BLT_BASEURL;
                String NAMESPACE = BuildConfig.NAMESPACE;
                String METHOD_NAME = "StartJourney";
                SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
                soapObject.addProperty("jobID",refId);
                soapObject.addProperty("latlong",CurrentLocation);
                soapObject.addProperty("address",pickupAddress);
                soapObject.addProperty("tripSheetRef",tripSheetRef);
                soapObject.addProperty("vehicleRegistration",vehicleRegistrationNumber);
                soapObject.addProperty("journeystartdatetime",getStartTime());
                soapObject.addProperty("username", SharedPref.getStringValue(StartTrip.this, Utility.AppData.user_id));
                soapObject.addProperty("password",SharedPref.getStringValue(StartTrip.this, Utility.AppData.password));

                SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(soapObject);
                HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
                try {
                    httpTransportSE.call(SOAP_ACTION, envelope);
                    SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
                    result = soapPrimitive.toString();
                    Log.d("result","--"+result);
                }catch(SoapFault sf){
                    result = sf.faultstring;
                }
                catch (Exception e) {
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
            Utility.getInstance().closeLoadingDialog();
            Log.d("result_startJourney","--"+result);
            if(Utility.isNotEmpty(""+result))
            {
                if (Utility.isJSONValid(result.toString()))
                {

                    JrWayDao.deleteRecords(StartTrip.this);

                    SharedPref.getInstance().setSharedValue(StartTrip.this, Utility.AppData.job_status, job_started);
                    SharedPref.getInstance().setSharedValue(StartTrip.this, Utility.AppData.trip_start_loc, CurrentLocation);
                    jobStatus = job_started;
                    SharedPref.getInstance().setSharedValue(StartTrip.this, Utility.AppData.job_Id, job_Id);
                    SharedPref.getInstance().setSharedValue(StartTrip.this, Utility.AppData.trip_sheet_ref_number, tripSheetRef);
                    long  timeMillis = System.currentTimeMillis();
                    SharedPref.getInstance().setSharedValue(StartTrip.this, Utility.AppData.start_time, ""+timeMillis);

                    showButton();

                    Toast.makeText(getApplicationContext(), "Successfully started!", Toast.LENGTH_LONG).show();


                }else
                {

                    showCustomDialog(""+result, false);
                }

            }else
            {
                result = getResources().getString(R.string.checkYourInternetConnection);
                showCustomDialog(""+result, false);
            }


            hideKeyboard(StartTrip.this);
        }
    }


    public static void hideKeyboard(Activity activity) {

        try
        {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    private String getStartTime()
    {
        try
        {
            long  timeMillis = System.currentTimeMillis();
            Date curDateTime = new Date(timeMillis);
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return  sdf.format(curDateTime);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    private void showCustomDialog(String msg, final boolean retryAPI)
    {
        try {
            boolean cancelButtonFalg = false;
            boolean cancelDialog = true;
            //String message = getResources().getString(R.string.checkYourInternetConnection);;
            Utility.showCustomDialogWithHeaderNew(StartTrip.this, "BTR", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
                @Override                                                              //cancelButton yes r no flag
                public void confirmed(boolean status) {  // true ok butoon
                    try
                    {
                        if(retryAPI)
                        {
                            startTrip();
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


    private void tripStart()
    {
        try {
            String  msg = "Are you sure you want start trip?";
            boolean cancelButtonFalg = false;
            boolean cancelDialog = true;
            //String message = getResources().getString(R.string.checkYourInternetConnection);;
            Utility.showCustomDialogWithHeaderNew(StartTrip.this, "Confirmation", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
                @Override                                                              //cancelButton yes r no flag
                public void confirmed(boolean status) {  // true ok butoon
                    try
                    {
                        startTrip();

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



    private void showPicup()
    {
        try {
            String  msg = "Are you sure in pickup point?";
            boolean cancelButtonFalg = false;
            boolean cancelDialog = true;
            Utility.showCustomDialogWithHeaderNew(StartTrip.this, "Confirmation", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
                @Override                                                              //cancelButton yes r no flag
                public void confirmed(boolean status) {  // true ok butoon
                    try
                    {
                        picupCustomer();

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



    private void showEndTripPage()
    {
        try
        {

            /*if(Utility.isNotEmpty(panLocationsId))
            {
                SharedPref.getInstance().setSharedValue(StartTrip.this, Utility.AppData.job_status_dropped, Utility.AppData.job_dropped);
                Intent i = new Intent(StartTrip.this, EndTrip.class);
                i.putExtra("refId",job_Id);
                i.putExtra("FlightNumber",FlightNumber);
                i.putExtra("panLocationsId",panLocationsId);
                startActivityForResult(i, 100);

                UpdateTable();

            }else
            {
                Toast.makeText(getApplicationContext(), "Please select location" , Toast.LENGTH_LONG).show();
            }*/


            SharedPref.getInstance().setSharedValue(StartTrip.this, Utility.AppData.job_status_dropped, Utility.AppData.job_dropped);
            Intent i = new Intent(StartTrip.this, EndTrip.class);
            i.putExtra("refId",job_Id);
            i.putExtra("FlightNumber",FlightNumber);
            i.putExtra("panLocationsId",panLocationsId);
            startActivityForResult(i, 100);

            UpdateTable();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }




  private void UpdateTable()
  {

      try
      {
          if(LocationService.driverLocation != null && LocationService.driverLocation.getLocation() != null)
          {
              final Location location = LocationService.driverLocation.getLocation();
              if(location != null)
              {
                  String jobStatus = SharedPref.getStringValue(StartTrip.this, Utility.AppData.job_status);
                  if(Utility.isNotEmpty(jobStatus))
                  {
                      if(jobStatus.equalsIgnoreCase(Utility.AppData.job_started) || jobStatus.equalsIgnoreCase(Utility.AppData.job_pickuped))
                      {

                          new AsyncTask<Void, Void, Boolean>()
                          {
                              protected Boolean doInBackground(Void... params)
                              {

                                  String  formatted_address = "";
                                  String  place_id = "";


                                  String url =  "https://maps.googleapis.com/maps/api/geocode/json?" +
                                          "latlng="+location.getLatitude() +"," + location.getLongitude()
                                          +"&key="+ BuildConfig.APIKEY;

                                  Log.d("geolocationData",url);
                                  try {
                                      String  data = downloadUrl(url);

                                      if(Utility.isNotEmpty(data))
                                      {
                                          Log.d("geolocationData",data);

                                          JSONObject jsonObject =  new JSONObject(data);
                                          if(jsonObject != null && jsonObject.length() != 0)
                                          {
                                              String result = jsonObject.getString("results");
                                              if(Utility.isNotEmpty(result))
                                              {
                                                  JSONArray jsonArray = new JSONArray(result);
                                                  if(jsonArray != null && jsonArray.length() != 0)
                                                  {
                                                      String address = jsonArray.get(0).toString();

                                                      JSONObject jsonObject_address =  new JSONObject(address);
                                                      if(jsonObject_address != null && jsonObject_address.length() != 0)
                                                      {

                                                          formatted_address = jsonObject_address.getString("formatted_address");
                                                          place_id = jsonObject_address.getString("place_id");

                                                      }
                                                  }
                                              }
                                          }
                                      }
                                      Log.d("geolocationData1",place_id +" - "+ formatted_address);

                                      if(Utility.isNotEmpty(formatted_address) && Utility.isNotEmpty(place_id))
                                      {
                                          JrWayDao.insertUserDetailsDrop(StartTrip.this,location, formatted_address,place_id);
                                      }
                                  } catch (IOException e) {
                                      e.printStackTrace();
                                  } catch (JSONException e) {
                                      e.printStackTrace();
                                  }


                                  return null;
                              }
                          }.execute();
                      }
                  }
              }
          }
      }catch (Exception e)
      {
          e.printStackTrace();
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
            return "";

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


}

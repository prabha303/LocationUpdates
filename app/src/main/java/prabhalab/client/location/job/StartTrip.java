package prabhalab.client.location.job;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.io.IOException;
import java.util.List;
import mehdi.sakout.fancybuttons.FancyButton;
import prabhalab.client.location.JrWayDao;
import prabhalab.client.location.LocationService;
import prabhalab.client.location.R;
import prabhalab.client.location.SharedPref;
import prabhalab.client.location.UpdateInterService;
import prabhalab.client.location.Utility;
import prabhalab.client.location.driverhome.JobModel;
import static prabhalab.client.location.Utility.AppData.job_pickuped;
import static prabhalab.client.location.Utility.AppData.job_started;


/**
 * Created by PrabhagaranR on 01-03-19.
 */

public class StartTrip extends AppCompatActivity implements UpdateInterService{
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = StartTrip.class.getSimpleName();
    FancyButton end_track_button,start_track_button;
    TextView mLatitude,mLongitude,mTimestamp,status,mAddress,lastTripKM;
    String vehicleRegistrationNumber="",pickupAddress = "", dropAddress="",job_Id = "", jobStatus = "";
    private GoogleMap googleMap;
    static  LatLng pickip_point = null;
    static  Marker pickip_point_marker = null;
    Marker marker = null;
    SupportMapFragment supportMapFragment;
    FancyButton start_trip,end_trip, pick_up;
    EditText trip_sheet_ref;
    ImageView navigation;
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
            init();

            String savedJobId = SharedPref.getStringValue(StartTrip.this, Utility.AppData.job_Id);

            if(Utility.isNotEmpty(jobStatus) && Utility.isNotEmpty(savedJobId) && job_Id.equalsIgnoreCase(savedJobId))
            {
                jobStatus = SharedPref.getStringValue(StartTrip.this, Utility.AppData.job_status);

            }

            showButton();


            Utility.getInstance().stayScreenOn(this);
            loadingMapview();

            getLocationFromAddress(pickupAddress);



        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showButton()
    {

        jobStatus = SharedPref.getStringValue(StartTrip.this, Utility.AppData.job_status);
        String savedJobId = SharedPref.getStringValue(StartTrip.this, Utility.AppData.job_Id);

        if(Utility.isNotEmpty(jobStatus) && Utility.isNotEmpty(savedJobId) && job_Id.equalsIgnoreCase(savedJobId))
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

        start_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tripStart();
            }
        });

        pick_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPicup();
            }
        });


        end_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showEndTrip();
            }
        });


        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+pickupAddress);
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
        TextView billing_location = findViewById(R.id.billing_location);
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
        billing_location.setText(pickupAddress);
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

        showButton();
    }













    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1003:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    //Toast.makeText(Home.this, "External File Permission done!", Toast.LENGTH_SHORT).show();
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


    private void setMarker(Location location)
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
    }


    public void getLocationFromAddress(final String strAddress) {

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
                        pickip_point = new LatLng(location.getLatitude(), location.getLongitude());

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
            MarkerOptions options = new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon));
            marker = googleMap.addMarker(options);
            if(Utility.isNotEmpty(jobStatus) && (jobStatus.equalsIgnoreCase(job_started) || jobStatus.equalsIgnoreCase(job_pickuped)))
            {
                if(pickip_point != null)
                {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(marker.getPosition());
                    builder.include(pickip_point);
                    MarkerOptions options_dest = new MarkerOptions().position(pickip_point).title(pickupAddress);
                    pickip_point_marker = googleMap.addMarker(options_dest);
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
                String SOAP_ACTION = "http://btr-ltd.net/Webservice/StartJourney";
                String URL = "http://btr-ltd.net/Webservice/WebServicePartner.asmx";
                String NAMESPACE = "http://btr-ltd.net/Webservice/";
                String METHOD_NAME = "StartJourney";
                SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
                soapObject.addProperty("jobID",refId);
                soapObject.addProperty("latlong",CurrentLocation);
                soapObject.addProperty("address",pickupAddress);
                soapObject.addProperty("tripSheetRef",tripSheetRef);
                soapObject.addProperty("vehicleRegistration",vehicleRegistrationNumber);
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
                    showCustomDialog(getResources().getString(R.string.checkYourInternetConnection), false);
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

                    SharedPref.getInstance().setSharedValue(StartTrip.this, Utility.AppData.job_status, job_started);
                    SharedPref.getInstance().setSharedValue(StartTrip.this, Utility.AppData.job_Id, job_Id);
                    SharedPref.getInstance().setSharedValue(StartTrip.this, Utility.AppData.trip_sheet_ref_number, tripSheetRef);
                    showButton();

                }else
                {
                    showCustomDialog(result.toString(), false);
                }

            }else
            {
                showCustomDialog(getResources().getString(R.string.checkYourInternetConnection), false);
            }
        }
    }



    private void showCustomDialog(String msg, final boolean retryAPI)
    {
        try {
            boolean cancelButtonFalg = false;
            boolean cancelDialog = true;
            //String message = getResources().getString(R.string.checkYourInternetConnection);;
            Utility.showCustomDialogWithHeader(StartTrip.this, "BTR", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
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
            Utility.showCustomDialogWithHeader(StartTrip.this, "Confirmation", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
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
            //String message = getResources().getString(R.string.checkYourInternetConnection);;
            Utility.showCustomDialogWithHeader(StartTrip.this, "Confirmation", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
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

    private void showEndTrip()
    {
        try {
            String  msg = "Are you sure you want end the trip?";
            boolean cancelButtonFalg = false;
            boolean cancelDialog = true;
            //String message = getResources().getString(R.string.checkYourInternetConnection);;
            Utility.showCustomDialogWithHeader(StartTrip.this, "Confirmation", msg, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
                @Override                                                              //cancelButton yes r no flag
                public void confirmed(boolean status) {  // true ok butoon
                    try
                    {


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

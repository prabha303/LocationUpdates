package prabhalab.client.location.job;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import mehdi.sakout.fancybuttons.FancyButton;
import prabhalab.client.location.R;



/**
 * Created by PrabhagaranR on 01-03-19.
 */

public class EndTrip extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = EndTrip.class.getSimpleName();
    String job_Id = "";
    LinearLayout checklist_layout,extra_amount_layout;
    EditText waiting_amount, parking_amount,toll_cc_amount,amendment_amount,phone_amount,others_amount,service_charge_amt,notes;
    FancyButton go_next,go_back,clear_sign;
    EditText trip_sheet_ref;
    ImageView navigation;
    private DrawingView drawView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_trip);
        try
        {
            Intent intent = getIntent();
            job_Id = intent.getStringExtra("refId");
            init();



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


      <startingKm>1</startingKm>

      <pickupKm>10.00</pickupKm>  ( this will calculate from start to pickup

      <dropOffKM>string</dropOffKM> (status pick up to end between location km )

      <finishingKm>start location and end location between google km</finishingKm>

      <isCheckedFixedRate>false</isCheckedFixedRate>

      <SelectedFixedPrice>true</SelectedFixedPrice>
      <panLocationID>""</panLocationID>
      <isairportornormal>string</isairportornormal>   - is flight number is there need to send..
*/
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
        clear_sign = findViewById(R.id.clear_sign);
        go_back = findViewById(R.id.go_back);


        go_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showNextStep();
            }
        });

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                backButton();
            }
        });


        clear_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

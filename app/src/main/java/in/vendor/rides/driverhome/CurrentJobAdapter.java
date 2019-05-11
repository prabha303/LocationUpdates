package in.vendor.rides.driverhome;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.vendor.rides.R;
import in.vendor.rides.SharedPref;
import in.vendor.rides.Utility;
import in.vendor.rides.job.StartTrip;


/**
 * Destination List adapter class.
 * Act's as the adapter class for the destination list recycler view.
 */
public class CurrentJobAdapter extends RecyclerView.Adapter<CurrentJobAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<JobModel> pastJobModel = null;



    //Construction...
    public CurrentJobAdapter(Context context, ArrayList<JobModel> pastJobModel) {
        this.context = context;
        this.pastJobModel = pastJobModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.past_job_view, parent, false);
        context = view.getContext();
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.order_id.setText(pastJobModel.get(position).getID());
        holder.passangerName.setText(pastJobModel.get(position).getPassenger() +" - "+ pastJobModel.get(position).getPassengers());
        holder.jobDateTime.setText(pastJobModel.get(position).getMobile());
        holder.pickupAddress.setText(pastJobModel.get(position).getPickupAddress1());
        holder.dropAddress.setText(pastJobModel.get(position).getDropAddress());

        holder.order_time.setText(pastJobModel.get(position).getPickupTime());

        holder.job_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, StartTrip.class);
                i.putExtra("refId",pastJobModel.get(position).getID());
                i.putExtra("vehicleRegistrationNumber",pastJobModel.get(position).getVehicleRegistrationNumber());
                i.putExtra("pickupAddress",pastJobModel.get(position).getPickupAddress());
                i.putExtra("dropAddress",pastJobModel.get(position).getDropAddress());
                context.startActivity(i);
            }
        });


        if (Utility.isNotEmpty(pastJobModel.get(position).getFlightNumber()))
        {
            holder.flight_number.setText(pastJobModel.get(position).getFlightNumber());
            holder.flight_layout.setVisibility(View.VISIBLE);
        }else
        {
            holder.flight_layout.setVisibility(View.GONE);
        }


        String savedJobId = SharedPref.getStringValue(context, Utility.AppData.job_Id);

        if(Utility.isNotEmpty(savedJobId) && Utility.isNotEmpty(pastJobModel.get(position).getID()) && pastJobModel.get(position).getID().equalsIgnoreCase(savedJobId))
        {
            holder.status_layout.setVisibility(View.VISIBLE);
            String job_status = SharedPref.getStringValue(context, Utility.AppData.job_status);
            if(Utility.isNotEmpty(job_status))
            {
                holder.status.setText(job_status);
            }else
            {
                holder.status_layout.setVisibility(View.GONE);
            }
        }else
        {
            holder.status_layout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return pastJobModel.size();
    }

    /**
     * Holder class Which holds the views.
     */

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView passangerName,jobDateTime,flight_number,pickupAddress, dropAddress,order_id,order_time,status;

        LinearLayout flight_layout,status_layout;
        CardView job_layout;
        public ViewHolder(View itemView) {
            super(itemView);

            passangerName = (TextView) itemView.findViewById(R.id.passangerName);
            jobDateTime = (TextView) itemView.findViewById(R.id.jobDateTime);
            pickupAddress = (TextView) itemView.findViewById(R.id.pickupAddress);
            dropAddress = (TextView) itemView.findViewById(R.id.dropAddress);
            order_id = (TextView) itemView.findViewById(R.id.order_id);
            job_layout = (CardView) itemView.findViewById(R.id.job_layout);
            order_time =  itemView.findViewById(R.id.order_time);
            flight_number =  itemView.findViewById(R.id.flight_number);
            flight_layout =  itemView.findViewById(R.id.flight_layout);
            status_layout =  itemView.findViewById(R.id.status_layout);
            status =  itemView.findViewById(R.id.status);
        }
    }


    /**
     * Setting the text for the views.
     */

    private void setTextView(ViewHolder holder, int position) {

    }

}
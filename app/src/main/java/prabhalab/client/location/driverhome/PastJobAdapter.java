package prabhalab.client.location.driverhome;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.json.JSONObject;
import java.util.ArrayList;

import prabhalab.client.location.MainActivity;
import prabhalab.client.location.R;
import prabhalab.client.location.login.Login;

/**
 * Destination List adapter class.
 * Act's as the adapter class for the destination list recycler view.
 */
public class PastJobAdapter extends RecyclerView.Adapter<PastJobAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<JobModel> pastJobModel = null;



    //Construction...
    public PastJobAdapter(Context context, ArrayList<JobModel> pastJobModel) {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.order_id.setText(pastJobModel.get(position).getBatchID());
        holder.passangerName.setText(pastJobModel.get(position).getPassenger() +" - "+ pastJobModel.get(position).getPassengers());
        holder.jobDateTime.setText(pastJobModel.get(position).getPickupDate() +":"+pastJobModel.get(position).getPickupTime());
        holder.pickupAddress.setText(pastJobModel.get(position).getPickupAddress1());
        holder.dropAddress.setText(pastJobModel.get(position).getDestinationAddress1());

        holder.job_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, MainActivity.class);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pastJobModel.size();
    }

    /**
     * Holder class Which holds the views.
     */

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView passangerName,jobDateTime,pickupAddress, dropAddress,order_id;
        CardView job_layout;
        public ViewHolder(View itemView) {
            super(itemView);

            passangerName = (TextView) itemView.findViewById(R.id.passangerName);
            jobDateTime = (TextView) itemView.findViewById(R.id.jobDateTime);
            pickupAddress = (TextView) itemView.findViewById(R.id.pickupAddress);
            dropAddress = (TextView) itemView.findViewById(R.id.dropAddress);
            order_id = (TextView) itemView.findViewById(R.id.order_id);
            job_layout = (CardView) itemView.findViewById(R.id.job_layout);

        }
    }


    /**
     * Setting the text for the views.
     */

    private void setTextView(ViewHolder holder, int position) {

    }

}

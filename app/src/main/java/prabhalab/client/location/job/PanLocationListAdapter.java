package prabhalab.client.location.job;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import prabhalab.client.location.PanLocationsPojo;
import prabhalab.client.location.R;

/**
 * Created by sindhya on 02-05-2016.
 * Description: This ArrayAdapter class is used to display the list of clients in the Loads Activity
 * Roles: Customer,Customer Service,CS Managers
 */
public class PanLocationListAdapter extends ArrayAdapter{
    private Context context;
    int textViewResourceId =0;
    private int resource;
    private static LayoutInflater inflater=null;
    ArrayList<PanLocationsPojo> pojoArrayList;

    public PanLocationListAdapter(Context context, int textViewResourceId, ArrayList<PanLocationsPojo> pojoArrayList) {
        super(context,  textViewResourceId,pojoArrayList);
        this.context=context;
        this.pojoArrayList=pojoArrayList;
        this.resource = textViewResourceId;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_row, parent, false);
        // get your views here and set values to them
        TextView textview = row.findViewById(R.id.spinner_item_text);
        textview.setText(pojoArrayList.get(position).getName());

        return row;
    }
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        //LayoutInflater inflater = getLayoutInflater();
        View row = inflater.inflate(R.layout.spinner_row, parent,false);
        TextView textview =row.findViewById(R.id.spinner_item_text);
         textview.setText(pojoArrayList.get(position).getName());
        return row;
    }
}
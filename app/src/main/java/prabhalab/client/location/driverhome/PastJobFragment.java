package prabhalab.client.location.driverhome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import prabhalab.client.location.APIEngine.JsonUtil;
import prabhalab.client.location.R;
import prabhalab.client.location.SharedPref;
import prabhalab.client.location.Utility;


public class PastJobFragment extends Fragment{


    RecyclerView pastJobPointList;

    public PastJobFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.past_job, container, false);
        ArrayList<JobModel> past_jobs_list = new ArrayList<>();
        pastJobPointList = view.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        pastJobPointList.setLayoutManager(manager);
        String past_jobs = SharedPref.getStringValue(getContext(), Utility.AppData.past_jobs);
        try {
            if(Utility.isNotEmpty(past_jobs))
            {
                JSONArray jsonArray = new JSONArray(past_jobs);
                if(jsonArray.length() != 0){
                    for (int i =0;i<jsonArray.length();i++){
                        JobModel objectFromJson = JsonUtil.getObjectFromJson(jsonArray.getJSONObject(i), JobModel.class);
                        past_jobs_list.add(objectFromJson);

                    }
                }
        PastJobAdapter spinnerAdapter = new PastJobAdapter(getContext(),past_jobs_list);
        pastJobPointList.setAdapter(spinnerAdapter);
       }
    } catch (JSONException e) {
       e.printStackTrace();
        }
        // Inflate the layout for this fragment
    return view;
    }


}

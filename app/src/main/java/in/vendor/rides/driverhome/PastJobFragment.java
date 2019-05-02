package in.vendor.rides.driverhome;

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

import in.vendor.rides.APIEngine.JsonUtil;
import in.vendor.rides.R;
import in.vendor.rides.SharedPref;
import in.vendor.rides.Utility;
import in.vendor.rides.login.Login;


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

        SharedPref.getInstance().setSharedValue(getContext(), Utility.AppData.past_job_count, "0");

        try {
            if(Utility.isNotEmpty(past_jobs))
            {
                JSONArray jsonArray = new JSONArray(past_jobs);
                if(jsonArray.length() != 0){
                    for (int i =0;i<jsonArray.length();i++){
                        JobModel objectFromJson = JsonUtil.getObjectFromJson(jsonArray.getJSONObject(i), JobModel.class);
                        past_jobs_list.add(objectFromJson);

                    }
                    SharedPref.getInstance().setSharedValue(getContext(), Utility.AppData.past_job_count, "" + jsonArray.length());
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

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


public class FutureJobFragment extends Fragment{


    RecyclerView futureList;
    public FutureJobFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.future_job, container, false);
        try
        {
            ArrayList<JobModel> future_jobs_list = new ArrayList<>();
            futureList = view.findViewById(R.id.recyclerView);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            futureList.setLayoutManager(manager);
            String future_jobs = SharedPref.getStringValue(getContext(), Utility.AppData.future_jobs);
            SharedPref.getInstance().setSharedValue(getContext(), Utility.AppData.future_job_count, "0");
            if(Utility.isNotEmpty(future_jobs))
            {
                try {
                    JSONArray   jsonArray = new JSONArray(future_jobs);

                    if(jsonArray.length() != 0){
                        for (int i =0;i<jsonArray.length();i++){
                            JobModel objectFromJson = JsonUtil.getObjectFromJson(jsonArray.getJSONObject(i), JobModel.class);
                            future_jobs_list.add(objectFromJson);

                        }
                        SharedPref.getInstance().setSharedValue(getContext(), Utility.AppData.future_job_count, "" + jsonArray.length());
                    }
                    FutureJobAdapter spinnerAdapter = new FutureJobAdapter(getContext(),future_jobs_list);
                    futureList.setAdapter(spinnerAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    return view;
    }


}

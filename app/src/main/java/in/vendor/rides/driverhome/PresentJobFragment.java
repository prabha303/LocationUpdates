package in.vendor.rides.driverhome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import in.vendor.rides.APIEngine.JsonUtil;
import in.vendor.rides.JrWayDao;
import in.vendor.rides.R;
import in.vendor.rides.SharedPref;
import in.vendor.rides.Utility;


public class PresentJobFragment extends Fragment  {
    RecyclerView todayList;
    public PresentJobFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.present_job, container, false);
        //ArrayList<JobModel> present_jobs_list = new ArrayList<>();
        try
        {
            todayList = view.findViewById(R.id.recyclerView);
            TextView no_jobs = view.findViewById(R.id.no_jobs);
            LinearLayoutManager manager = new LinearLayoutManager(todayList.getContext());
            todayList.setLayoutManager(manager);
            String today_jobs = SharedPref.getStringValue(getContext(), Utility.AppData.today_jobs);

            ArrayList<JobModel> present_jobs_list = JrWayDao.getInstance().getWayPoints(todayList.getContext());

            if(present_jobs_list != null && present_jobs_list.size() >0)
            {
                no_jobs.setVisibility(View.GONE);
                CurrentJobAdapter spinnerAdapter = new CurrentJobAdapter(getContext(),present_jobs_list);
                todayList.setAdapter(spinnerAdapter);

            }else
            {
                no_jobs.setVisibility(View.VISIBLE);
                todayList.setVisibility(View.GONE);
                SharedPref.getInstance().setSharedValue(getContext(), Utility.AppData.today_job_count, "0");
            }

            /*if(Utility.isNotEmpty(today_jobs))
            {
                try {
                    JSONArray jsonArray = new JSONArray(today_jobs);
                    if(jsonArray.length() != 0){
                        for (int i =0;i<jsonArray.length();i++){
                            JobModel objectFromJson = JsonUtil.getObjectFromJson(jsonArray.getJSONObject(i), JobModel.class);
                            present_jobs_list.add(objectFromJson);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }*/
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return view;
    }
}

package prabhalab.client.location.driverhome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;

import prabhalab.client.location.JrWayDao;
import prabhalab.client.location.R;
import prabhalab.client.location.SharedPref;
import prabhalab.client.location.Utility;
import prabhalab.client.location.job.EndTrip;
import prabhalab.client.location.login.FontAweSomeTextView;
import prabhalab.client.location.login.Login;


/**
 * Created by PrabhagaranR on 01-03-19.
 */

public class DriverHome extends AppCompatActivity {

    private RelativeLayout toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    FontAweSomeTextView logOut;

    int backPressCount = 0;


    private int[] tabIconsw = {
            R.mipmap.ic_btr_svg_logo,
            R.mipmap.ic_btr_svg_logo,
            R.mipmap.ic_btr_svg_logo
    };

    TextView driverName,jobCount;
    FontAweSomeTextView jobcarsymbol;

    private TabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_tab);
        try
        {
            toolbar = findViewById(R.id.toolbar);
            viewPager = findViewById(R.id.viewpager);

            tabLayout = findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);


            logOut = findViewById(R.id.logOut);

            String today_date   = SharedPref.getStringValue(this,Utility.AppData.today_date);
            String tomorrow_date  = SharedPref.getStringValue(this,Utility.AppData.tomorrow_date);
            String yesterday_date = SharedPref.getStringValue(this,Utility.AppData.yesterday_date);


            adapter = new TabAdapter(getSupportFragmentManager(), this);
            adapter.addFragment(new PastJobFragment(), yesterday_date, 0);
            adapter.addFragment(new PresentJobFragment(), today_date, 0);
            adapter.addFragment(new FutureJobFragment(), tomorrow_date, 0);


            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);

            highLightCurrentTab(1);
            viewPager.setCurrentItem(1);

            try
            {

                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }
                    @Override
                    public void onPageSelected(int position) {
                        try
                        {
                            jobCount.setText("(" + getJobCount(position) +")" );
                            highLightCurrentTab(position);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onPageScrollStateChanged(int state) {


                    }
                });
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            driverName = findViewById(R.id.driverName);
            jobcarsymbol = findViewById(R.id.jobcarsymbol);
            jobCount = findViewById(R.id.jobCount);


            if(Utility.isNotEmpty(SharedPref.getStringValue(this,Utility.AppData.user_name)))
            {
                driverName.setText("Welcome - " + SharedPref.getStringValue(this,Utility.AppData.user_name));
            }else
            {
                driverName.setText("Welcome");
            }


            if(Utility.isNotEmpty(SharedPref.getStringValue(this,Utility.AppData.today_job_count)))
            {
                jobCount.setText("(" + SharedPref.getStringValue(this,Utility.AppData.today_job_count) +")" );
            }



            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showCustomDialog();
                }
            });



        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void highLightCurrentTab(int position) {
        try
        {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                assert tab != null;
                tab.setCustomView(null);
                tab.setCustomView(adapter.getTabView(i));
            }
            TabLayout.Tab tab = tabLayout.getTabAt(position);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(adapter.getSelectedTabView(position));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private class TabAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        //private final List<Integer> mFragmentIconList = new ArrayList<>();
        private Context context;
        TabAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        public void addFragment(Fragment fragment, String title, int tabIcon) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            //mFragmentIconList.add(tabIcon);
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
        //return mFragmentTitleList.get(position);
            return null;
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public View getTabView(int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
            TextView tabTextView = view.findViewById(R.id.tabTextView);
            FontAweSomeTextView tabImageView = view.findViewById(R.id.tabImageView);
            tabTextView.setText(mFragmentTitleList.get(position));
            tabTextView.setTextColor(getResources().getColor(R.color.green));
            tabImageView.setTextColor(getResources().getColor(R.color.green));
            return view;
        }
        public View getSelectedTabView(int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
            TextView tabTextView = view.findViewById(R.id.tabTextView);
            FontAweSomeTextView tabImageView = view.findViewById(R.id.tabImageView);
            tabTextView.setText(mFragmentTitleList.get(position));
            //tabTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            tabImageView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            //tabTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            return view;
        }
    }


    @Override
    public void onBackPressed() {
        /**
         * The default back button click is disabled.
         */
        // super.onBackPressed();
        //super.onBackPressed();
        try
        {
            backPressCount++;
            if(backPressCount >1)
            {
                super.onBackPressed();
            }
        }catch (Exception w)
        {
            w.printStackTrace();
        }
    }

    private String getJobCount (int pos)
    {
        try
        {
            String today = SharedPref.getStringValue(DriverHome.this,Utility.AppData.today_job_count);
            String past = SharedPref.getStringValue(DriverHome.this,Utility.AppData.past_job_count);
            String future = SharedPref.getStringValue(DriverHome.this,Utility.AppData.future_job_count);

            String count = "";

            if(pos == 0)
            {
                return  past;

            }else if (pos == 2)
            {
                return  future;

            }else
            {
                return  today;
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }



    private void showCustomDialog()
    {
        try {
            boolean cancelButtonFalg = false;
            boolean cancelDialog = true;
            String message = "Are you sure want loggout?";
            Utility.showCustomDialogWithHeaderNew(DriverHome.this, "BTR", message, "OK", "Cancel",cancelButtonFalg, cancelDialog, new Utility.ConfirmCallBack() {
                @Override                                                              //cancelButton yes r no flag
                public void confirmed(boolean status) {  // true ok butoon
                    try
                    {
                        SharedPreferences settings = getSharedPreferences(SharedPref.preferenceName, Context.MODE_PRIVATE);
                        settings.edit().clear().apply();
                        JrWayDao.getInstance().deleteTripData(DriverHome.this);
                        Toast.makeText(DriverHome.this, "Successfully logged out", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(DriverHome.this, Login.class);
                        startActivity(i);
                        finish();

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

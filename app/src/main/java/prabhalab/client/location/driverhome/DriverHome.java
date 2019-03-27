package prabhalab.client.location.driverhome;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import prabhalab.client.location.R;
import prabhalab.client.location.SharedPref;
import prabhalab.client.location.Utility;


/**
 * Created by PrabhagaranR on 01-03-19.
 */

public class DriverHome extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_tab);
        try
        {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            viewPager = findViewById(R.id.viewpager);
            setupViewPager(viewPager);
            tabLayout = findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);


            String past_jobs = SharedPref.getStringValue(this,Utility.AppData.past_jobs);
            String today_jobs =  SharedPref.getStringValue(this,Utility.AppData.today_jobs);
            String future_jobs = SharedPref.getStringValue(this,Utility.AppData.future_jobs);
            Log.d("past_jobs",past_jobs);
            Log.d("today_jobs",today_jobs);
            Log.d("future_jobs",future_jobs);


        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PastJobFragment(), "Past JOb");
        adapter.addFragment(new PresentJobFragment(), "Today JOb");
        adapter.addFragment(new FutureJobFragment(), "Future JOb");
        viewPager.setAdapter(adapter);
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}

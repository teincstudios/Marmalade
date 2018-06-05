package com.teincstudios.tickit.activities;

import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.teincstudios.tickit.R;
import com.teincstudios.tickit.adapters.SectionsPagerAdapter;
import com.teincstudios.tickit.fragments.Home_Fragment;
import com.teincstudios.tickit.fragments.Menu_Fragment;

public class HomeActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TextView app_Logo;

    private boolean doubleBackToExitPressedOnce;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Typeface lobster = Typeface.createFromAsset(getAssets(), "fonts/lobster.otf");
        app_Logo = findViewById(R.id.app_logo);
        app_Logo.setText(R.string.app_name);
        app_Logo.setTypeface(lobster);

        mViewPager = findViewById(R.id.home_viewpager);
        setupViewPager();
    }

    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Home_Fragment()); //index 0
        adapter.addFragment(new Menu_Fragment()); //index 1
        mViewPager.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        mViewPager.setCurrentItem(0);

        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

}

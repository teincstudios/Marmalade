package com.teincstudios.tickit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;

import com.teincstudios.tickit.R;
import com.teincstudios.tickit.adapters.SectionsPagerAdapter;
import com.teincstudios.tickit.fragments.RestaurantInfo_Fragment;
import com.teincstudios.tickit.fragments.RestaurantMenu_Fragment;
import com.teincstudios.tickit.models.Joints;
import com.teincstudios.tickit.utils.Config;

import de.hdodenhof.circleimageview.CircleImageView;

public class RestaurantActivity extends AppCompatActivity {

    private ViewPager rViewPager;
    private Joints activeSpot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);


        TextView spotNameTextView = findViewById(R.id.restaurant_page_name);
        TextView spotLocationTextView = findViewById(R.id.restaurant_page_location);
        RatingBar spotRatingBar = findViewById(R.id.restaurant_page_ratingbar);
        TextView ratingTextView = findViewById(R.id.restaurant_page_ratingtext);
        CircleImageView spotImageView = findViewById(R.id.profile_photo);


        activeSpot = getIntent().getParcelableExtra(Config.PARCELABLE_SPOT);
        final String currentCity = getIntent().getExtras().getString("city_name");

        // Updating text and images accordingly
        spotNameTextView.setText(activeSpot.getSpotName());
        spotLocationTextView.setText(activeSpot.getSpotLocation());
        spotRatingBar.setRating(Float.parseFloat(activeSpot.getSpotRating()));
        ratingTextView.setText(activeSpot.getSpotRating().concat("Stars"));

        // TabLayout
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Menu"));
        tabLayout.addTab(tabLayout.newTab().setText("Info"));


        rViewPager = findViewById(R.id.rViewpager);
        setupViewPager();
    }

    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RestaurantInfo_Fragment()); //index 0
        adapter.addFragment(new RestaurantMenu_Fragment()); //index 1
        rViewPager.setAdapter(adapter);

    }
}

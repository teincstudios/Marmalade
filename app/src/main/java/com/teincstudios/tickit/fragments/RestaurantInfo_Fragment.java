package com.teincstudios.tickit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.teincstudios.tickit.R;
import com.teincstudios.tickit.activities.BookingActivity;
import com.teincstudios.tickit.adapters.ImagesAdapter;
import com.teincstudios.tickit.interfaces.RecyclerItemClickListener;
import com.teincstudios.tickit.models.Joints;
import com.teincstudios.tickit.utils.Config;

import java.util.ArrayList;
import java.util.Objects;

public class RestaurantInfo_Fragment extends Fragment {

    private RelativeLayout photoViewSection;
    private ViewPager photoViewPager;
    private int currentSection;
    private Joints activeSpot;
    private PhotoViewPagerAdapter photoViewPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.resfragment_info, container, false);

        activeSpot = Objects.requireNonNull(getActivity()).getIntent().getParcelableExtra(Config.PARCELABLE_SPOT);
        final String currentCity = Objects.requireNonNull(getActivity().getIntent().getExtras()).getString("city_name");

        LinearLayout reserveButton = rootView.findViewById(R.id.call_to_action_button);


        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BookingActivity.class);
                intent.putExtra("BOOKING_SPOT_PARCEL", activeSpot);
                intent.putExtra("city_name_book", currentCity);
                startActivity(intent);
            }
        });

        // PhotoView
        photoViewSection = rootView.findViewById(R.id.photo_view_section);
        photoViewPager = rootView.findViewById(R.id.image_view_pager);
        ImageView photoViewCloseButton = rootView.findViewById(R.id.photo_view_section_close_button);
        photoViewSection.setVisibility(View.GONE);
        photoViewPagerAdapter = new PhotoViewPagerAdapter(getChildFragmentManager(), activeSpot.getPhotosList());
        photoViewCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoViewSection.setVisibility(View.GONE);
                currentSection = 0;
            }
        });

        // Images Section
        Log.i("ResActivity", "imagesList:" + activeSpot.getPhotosList());
        ImagesAdapter imagesAdapter = new ImagesAdapter(activeSpot.getPhotosList());
        RecyclerView imagesRecyclerView = rootView.findViewById(R.id.images_recycler_view);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        imagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        imagesRecyclerView.setAdapter(imagesAdapter);
        imagesRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                photoViewSection.setVisibility(View.VISIBLE);
                //photoViewPager.setAdapter(photoViewPagerAdapter);
                photoViewPager.setCurrentItem(position);
                currentSection = 1;
            }
        }));

        return rootView;
    }

    /* PhotoView Adapter */
    private class PhotoViewPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> imageList;

        public PhotoViewPagerAdapter(FragmentManager fm, ArrayList<String> imageList) {
            super(fm);
            this.imageList = imageList;
        }

        @Override
        public Fragment getItem(int position) {
            //return PhotoViewFragment.newInstance(imageList.get(position));
            return null;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

    }


}

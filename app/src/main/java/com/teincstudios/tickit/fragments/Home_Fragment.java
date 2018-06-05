package com.teincstudios.tickit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.teincstudios.tickit.MyData;
import com.teincstudios.tickit.R;
import com.teincstudios.tickit.adapters.NewsAdapter;
import com.teincstudios.tickit.interfaces.NewsItemClickListener;
import com.teincstudios.tickit.models.News;

import java.util.ArrayList;


/**
 * Created by kwesi_manny on 1/9/2018.
 */

public class Home_Fragment extends Fragment implements NewsItemClickListener {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView news_recyclerView = rootView.findViewById(R.id.feed_news);

        news_recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        news_recyclerView.setLayoutManager(layoutManager);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(1000);
        news_recyclerView.setItemAnimator(animator);
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(news_recyclerView);

        ArrayList<News> data = new ArrayList<>();
        for (int i = 0; i < MyData.nameArray.length; i++) {
            data.add(new News(
                    MyData.nameArray[i]
            ));
        }
        NewsAdapter adapter = new NewsAdapter(data, getContext(), this);
        news_recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onItemClick(int pos, News myData, ImageView shareImageView, Intent intent) {

    }
}




package com.teincstudios.tickit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teincstudios.tickit.R;
import com.teincstudios.tickit.activities.ResHomeActivity;


/*
  Created by kwesi_manny on 1/9/2018.
 */

public class Menu_Fragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        CardView foodBtn = rootView.findViewById(R.id.food_btn);
        foodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), ResHomeActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }


}

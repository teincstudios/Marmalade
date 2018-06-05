package com.teincstudios.tickit.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teincstudios.tickit.R;
import com.teincstudios.tickit.models.Menu;

import java.util.ArrayList;

public class RestaurantMenu_Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.resfragment_menu, container, false);

        final ArrayList<Menu> menuList = new ArrayList<>();
        menuList.add(new Menu(0,true,"Pepper Sandwich Veg", 250, "Pizza with fried pepper barbeque as a demo text chicken as toppings on the crust."));
        menuList.add(new Menu(1,false,"Pepper Barbeque Chicken", 250, "Pizza with fried pepper barbeque as a demo text chicken as toppings on the crust."));
        menuList.add(new Menu(2,false,"Pepper Barbeque Chicken", 250, "Pizza with fried pepper barbeque as a demo text chicken as toppings on the crust."));
        menuList.add(new Menu(3,true,"Pepper Veg Sandwich", 250, "Pizza with fried pepper barbeque as a demo text chicken as toppings on the crust."));
        menuList.add(new Menu(4,true,"Pepper Sandwich Veg", 250, "Pizza with fried pepper barbeque as a demo text chicken as toppings on the crust."));

        //SectionedRecyclerViewAdapter sectionsAdapter = new SectionedRecyclerViewAdapter();
        //sectionsAdapter.addSection(new MenuSection("Bestsellers", menuList));
        //sectionsAdapter.addSection(new MenuSection("Desserts", menuList));
        //sectionsAdapter.notifyDataSetChanged();

        RecyclerView menuRecyclerView = rootView.findViewById(R.id.menu_main_recyclerview);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        menuRecyclerView.setItemAnimator(new DefaultItemAnimator());
       // menuRecyclerView.setAdapter(sectionsAdapter);


        return rootView;
    }
}

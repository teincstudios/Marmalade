package com.teincstudios.tickit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teincstudios.tickit.R;
import com.teincstudios.tickit.models.Joints;
import com.teincstudios.tickit.utils.RestaurantUtils;

import java.util.ArrayList;

import static com.teincstudios.tickit.utils.RestaurantUtils.BOOKINGS_CLOSED;
import static com.teincstudios.tickit.utils.RestaurantUtils.CLOSED_NOW;
import static com.teincstudios.tickit.utils.RestaurantUtils.CLOSING_SOON;

/**
 * Created by kwesi on 07/01/18.
 */

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.MyViewHolder> {

    private ArrayList<Joints> restaurantList;
    private Joints currentRestaurant;
    private RestaurantUtils restaurantUtils;
    private Context context;

    public SpotAdapter(Context context, ArrayList<Joints> restaurantList, RestaurantUtils restaurantUtils) {
        this.restaurantList = restaurantList;
        this.restaurantUtils = restaurantUtils;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.joint_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // Current Restaurant
        currentRestaurant = restaurantList.get(position);

        String openStatus = restaurantUtils.determineOpenStatus(currentRestaurant.getOpenStatus(), currentRestaurant.getOpeningTime(), currentRestaurant.getClosingTime());
        float rating = Float.parseFloat(currentRestaurant.getSpotRating());

        holder.name.setText(currentRestaurant.getSpotName());
        holder.place.setText(currentRestaurant.getSpotLocation());
//        holder.cuisines.setText(currentRestaurant.getCuisines());
        holder.openStatus.setText(openStatus);
        holder.ratingText.setText(String.valueOf(rating));
        //Glide.with(holder.spotImage.getContext()).load(currentRestaurant.getSpotImageUrl()).into(holder.spotImage);
        holder.openStatus.setText(openStatus);
        holder.nearbyImageView.setColorFilter(Color.parseColor("#757575"));
        holder.nearbyDistanceTextView.setText(currentRestaurant.getDistance());

        // OpenStatus color change
        int openStatusColor;
        if (openStatus.matches(CLOSED_NOW) || openStatus.matches(BOOKINGS_CLOSED) || openStatus.toLowerCase().contains("closed")) {
            openStatusColor = context.getResources().getColor(R.color.red);
            holder.openStatus.setTextColor(openStatusColor);
        } else if (openStatus.matches(CLOSING_SOON)) {
            openStatusColor = context.getResources().getColor(android.R.color.holo_orange_dark);
            holder.openStatus.setTextColor(openStatusColor);
        } else {
            openStatusColor = context.getResources().getColor(R.color.green);
            holder.openStatus.setTextColor(openStatusColor);
        }
//        holder.openStatusIcon.setColorFilter(openStatusColor);

        if (rating >= 4.5) {
            holder.ratingCard.setCardBackgroundColor(holder.ratingCard.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else if (rating >= 4) {
            holder.ratingCard.setCardBackgroundColor(holder.ratingCard.getContext().getResources().getColor(android.R.color.holo_green_light));
        } else if (rating >= 3) {
            holder.ratingCard.setCardBackgroundColor(holder.ratingCard.getContext().getResources().getColor(android.R.color.holo_orange_light));
        } else if (rating >= 2) {
            holder.ratingCard.setCardBackgroundColor(holder.ratingCard.getContext().getResources().getColor(android.R.color.holo_orange_dark));
        } else if (rating >= 1) {
            holder.ratingCard.setCardBackgroundColor(holder.ratingCard.getContext().getResources().getColor(android.R.color.holo_red_light));
        } else {
            holder.ratingCard.setCardBackgroundColor(holder.ratingCard.getContext().getResources().getColor(android.R.color.holo_red_light));
        }
    }


    @Override
    public int getItemCount() {
        return restaurantList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, place, openStatus, ratingText, nearbyDistanceTextView;
        ImageView spotImage, openStatusIcon, nearbyImageView;
        CardView ratingCard;
        LinearLayout verifiedSection;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.spot_item_name);
            //cuisines = (TextView) view.findViewById(R.id.spot_item_cuisines);
            openStatus = view.findViewById(R.id.spot_item_openstatus);
            ratingText = view.findViewById(R.id.spot_item_ratingtextview);
            spotImage = view.findViewById(R.id.spot_item_image);
            place = view.findViewById(R.id.spot_item_place);
            ratingCard = view.findViewById(R.id.spot_item_ratingcard);
            //cuisinesTitle = (TextView) view.findViewById(R.id.spot_item_cuisines_title);
            verifiedSection = view.findViewById(R.id.verified_text_section);
            nearbyImageView = view.findViewById(R.id.nearby_imageview_icon);
            nearbyDistanceTextView = view.findViewById(R.id.spot_item_nearby_distance);

        }
    }

}

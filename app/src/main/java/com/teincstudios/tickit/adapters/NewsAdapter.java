package com.teincstudios.tickit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.teincstudios.tickit.R;
import com.teincstudios.tickit.interfaces.NewsItemClickListener;
import com.teincstudios.tickit.models.News;

import java.util.ArrayList;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    int type;
    private NewsItemClickListener ItemClickListener;
    private ArrayList<News> dataSet;
    private Context mCtx = null;


    public NewsAdapter(ArrayList<News> data, Context context, NewsItemClickListener itemClickListener) {
        this.dataSet = data;
        this.mCtx = context;
        this.ItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_feed, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int listPosition) {

        final News dataModel = dataSet.get(listPosition);

        ImageView imageView = holder.imageViewIcon;

        holder.newsTopic.setText(dataSet.get(listPosition).getPosttitle());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView newsTopic;
        ImageView imageViewIcon;
        ImageView read;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.newsTopic = itemView.findViewById(R.id.post_title);
            this.imageViewIcon = itemView.findViewById(R.id.post_image);
            this.read = itemView.findViewById(R.id.read);
        }
    }


}

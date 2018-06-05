package com.teincstudios.tickit.interfaces;

import android.content.Intent;
import android.widget.ImageView;

import com.teincstudios.tickit.models.News;

/**
 * Created by kwesi on 19/02/2017.
 */

public interface NewsItemClickListener {
    void onItemClick(int pos, News myData, ImageView shareImageView, Intent intent);

}
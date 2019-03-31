package com.shalate.red.shalate.Utilities;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Created by Ahmed on 8/27/2018.
 */

public class PicassoClass {

    public static void setImageForStorge(Context context, final String url, final ImageView target) {
        final int radius = 20;
        final int margin = 0;
        final Transformation transformation = new RoundedCornersTransformation(radius, margin);
        final String finalUrl = url;
        Picasso.with(context)
                .load(finalUrl)
                .into(target);
    }
}

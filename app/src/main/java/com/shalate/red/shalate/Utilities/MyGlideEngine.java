package com.shalate.red.shalate.Utilities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
//import com.bumptech.glide.request.RequestOptions;
import com.zhihu.matisse.engine.ImageEngine;

public class MyGlideEngine {

//
//    @Override
//    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
//        Glide.with(context)
//                .asBitmap() // some .jpeg files are actually gif
//                .load(uri)
//                .apply(new RequestOptions()
//                        .override(resize, resize)
//                        .placeholder(placeholder)
//                        .centerCrop())
//                .into(imageView);
//    }
//
//    @Override
//    public void loadAnimatedGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
//
//    }
//
//    @Override
//    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
//        Glide.with(context)
//                .load(uri)
//                .apply(new RequestOptions()
//                        .override(resizeX, resizeY)
//                        .priority(Priority.HIGH)
//                        .fitCenter())
//                .into(imageView);
//    }
//
//    @Override
//    public void loadAnimatedGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
//
//    }
//
//
//    @Override
//    public boolean supportAnimatedGif() {
//        return true;
//    }
}
package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shalate.red.shalate.R;

import java.util.ArrayList;

/**
 * Created by abdelmageed on 08/10/17.
 */

public class MultiPleImageAdapter extends RecyclerView.Adapter<MultiPleImageAdapter.MyHolder> {

    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    Context context;
    public MultiPleImageAdapter(Context context,ArrayList<Bitmap> imgs) {
        this.context=context;
        this.bitmaps=imgs;
    }



    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_multi_image,parent,false);
        MyHolder holder=new MyHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        if (bitmaps.size()>0) {
            holder.civ_offerPhoto.setImageBitmap(bitmaps.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView civ_offerPhoto,close;
        public MyHolder(View itemView) {
            super(itemView);
            civ_offerPhoto=(ImageView) itemView.findViewById(R.id.image_raw);
        }
    }
}

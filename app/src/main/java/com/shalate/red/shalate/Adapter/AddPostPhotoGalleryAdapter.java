package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shalate.red.shalate.R;

import java.util.List;

public class AddPostPhotoGalleryAdapter extends RecyclerView.Adapter<AddPostPhotoGalleryAdapter.MyHolder> {

    Context context;
    List<Bitmap> arrayList;
    int size;
    int oldPosition = -1;

    public AddPostPhotoGalleryAdapter(Context context, List<Bitmap> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.item_add_post_gallery, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.imageStraged.setImageBitmap(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        if (arrayList.size() > 4) {
            size = 4;
        } else {
            size = arrayList.size();
        }
        return size;
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView imageStraged;
//        TextView tvChargeOfList;
//        FrameLayout frame;

        public MyHolder(View itemView) {
            super(itemView);
            imageStraged = itemView.findViewById(R.id.ivItemGridImage);
//            tvChargeOfList = itemView.findViewById(R.id.tvChargeOfList);
//            frame = itemView.findViewById(R.id.frame);
        }
    }
}

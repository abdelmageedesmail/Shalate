package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shalate.red.shalate.Activity.ImageDetails;
import com.shalate.red.shalate.Activity.ImageListActivity;
import com.shalate.red.shalate.Model.ImageModel;
import com.shalate.red.shalate.Model.PhotoGallay;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PicassoClass;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ahmed on 11/18/2017.
 */

public class PhotoGallaryAdapter extends RecyclerView.Adapter<PhotoGallaryAdapter.ViewHolder> {

    OnItemClickListener onItemClickListener;
    public static int num;
    Context context;

    List<ImageModel> faqModels;
    boolean ischecked = false;

    public PhotoGallaryAdapter(Context context, ArrayList<ImageModel> faqModels) {
        this.context = context;
        this.faqModels = faqModels;
    }

    public interface OnItemClickListener {
        void onclick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.photogallayitem, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final String photo = faqModels.get(position).getImage();
//        Picasso.get()
//                .load(photo)
//                .into(holder.imageStraged);
        if (faqModels.size() == 1) {
            android.view.ViewGroup.LayoutParams layoutParams = holder.imageStraged.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.imageStraged.setLayoutParams(layoutParams);
            holder.imageStraged.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        PicassoClass.setImageForStorge(context, photo, holder.imageStraged);

        holder.imageStraged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onclick(holder.getAdapterPosition());
                }
//                Intent intent = new Intent(context, ImageListActivity.class);
//                intent.putExtra("from",2);
//                context.startActivity(intent);
            }
        });
//        holder.imageStraged.
    }

    @Override
    public int getItemCount() {
        return faqModels.size();
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageStraged;

        public ViewHolder(View itemView) {
            super(itemView);

            imageStraged = itemView.findViewById(R.id.ivItemGridImage);

        }
    }
}

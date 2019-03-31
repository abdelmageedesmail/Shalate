package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.shalate.red.shalate.Activity.ImageListActivity;
import com.shalate.red.shalate.Model.ImageModel;
import com.shalate.red.shalate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ahmed on 11/18/2017.
 */

public class ImageDetailsAdapter extends RecyclerView.Adapter<ImageDetailsAdapter.ViewHolder> {

    public static int num;
    Context context;
    int client_id, vendor_id;
    List<ImageModel> commentModels;
    String flage;

    public ImageDetailsAdapter(Context context, ArrayList<ImageModel> commentModels) {
        this.context = context;
        this.commentModels = commentModels;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_image_posts, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Picasso.with(context).load(commentModels.get(position).getImage()).into(holder.ivImage);
        holder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gsonObj = new Gson();
                String jsonStr = gsonObj.toJson(commentModels.get(position));
                Intent intent = new Intent(context, ImageListActivity.class);
                intent.putExtra("Images", jsonStr);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImagePost);
        }
    }
}

package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shalate.red.shalate.Model.CommentModel;
import com.shalate.red.shalate.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ahmed on 11/18/2017.
 */

public class PeronAdapter extends RecyclerView.Adapter<PeronAdapter.ViewHolder> {

    OnItemClickListener onItemClickListener;
    public static   int num;
    Context context;
    int client_id,vendor_id;
    List<CommentModel> commentModels;
    String flage;
    public PeronAdapter(Context context, ArrayList<CommentModel> commentModels) {
        this.context = context;
        this.commentModels = commentModels;
    }
    public interface OnItemClickListener {
        void onclick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_person_fav, parent, false));
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

//        holder.image.setBackgroundResource(filterModel.getImage());
//
////        holder.linerContainer.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                if (onItemClickListener != null) {
////                    onItemClickListener.onclick(position);
////                }
////            }
////        });

    }
    @Override
    public int getItemCount() {
        return 8;
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        ImageView prvileImage;
        public ViewHolder(View itemView) {
            super(itemView);
            prvileImage= itemView.findViewById(R.id.prvileImage);
            name= itemView.findViewById(R.id.name);
        }
        }
}

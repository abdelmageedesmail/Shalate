package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shalate.red.shalate.Model.FilterModel;
import com.shalate.red.shalate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ahmed on 11/18/2017.
 */

public class FilterRecycleAdapter extends RecyclerView.Adapter<FilterRecycleAdapter.ViewHolder> {

    OnItemClickListener onItemClickListener;
    public static int num;
    Context context;
    int client_id, vendor_id;
    List<FilterModel> filterModels;
    String flage;
    int from;

    public FilterRecycleAdapter(Context context, ArrayList<FilterModel> filterModels, int from) {
        this.context = context;
        this.filterModels = filterModels;
        this.from = from;
    }

    public interface OnItemClickListener {
        void onclick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.type_layout_rec, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        FilterModel filterModel = filterModels.get(position);
        holder.name.setText(filterModel.getName());
        Picasso.with(context).load(filterModel.getImage()).into(holder.image);
        Log.e("image", "" + filterModel.getImage());

//        PicassoClass.setImageForStorge(context,filterModel.getImage(),holder.image);
        holder.liContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onclick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return filterModels.size();
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        ImageView image;
        LinearLayout liContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            liContainer = itemView.findViewById(R.id.liContainer);
        }
    }
}

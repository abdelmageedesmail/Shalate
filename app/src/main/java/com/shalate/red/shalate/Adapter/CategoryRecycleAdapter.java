package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shalate.red.shalate.Model.FilterModel;
import com.shalate.red.shalate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ahmed on 11/18/2017.
 */

public class CategoryRecycleAdapter extends RecyclerView.Adapter<CategoryRecycleAdapter.ViewHolder> {

    OnItemClickListener onItemClickListener;
    public static int num;
    Context context;
    int client_id, vendor_id;
    List<FilterModel> filterModels;
    String flage;
    int oldPosition = -1;
    public CategoryRecycleAdapter(Context context, ArrayList<FilterModel> filterModels) {
        this.context = context;
        this.filterModels = filterModels;
    }

    public interface OnItemClickListener {
        void onclick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.type_layout_category, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        FilterModel filterModel = filterModels.get(position);
        holder.name.setText(filterModel.getName());
        Picasso.with(context).load(filterModel.getImage()).into(holder.image);
        Log.e("image", "" + filterModel.getImage());

        holder.liContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
                if (onItemClickListener != null) {
                    onItemClickListener.onclick(position);
                }
            }
        });

        if (oldPosition == holder.getAdapterPosition()) {
            holder.image.setBorderColor(context.getResources().getColor(R.color.greenn));
            holder.image.setBorderWidth(4);
        } else {
            holder.image.setBorderColor(0);
        }
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
        CircleImageView image;
        LinearLayout liContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            liContainer = itemView.findViewById(R.id.liContainer);
        }
    }
}

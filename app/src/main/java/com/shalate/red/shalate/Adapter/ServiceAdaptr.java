package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shalate.red.shalate.Model.GuideModel;
import com.shalate.red.shalate.Model.ServiceModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PicassoClass;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ahmed on 11/18/2017.
 */

public class ServiceAdaptr extends RecyclerView.Adapter<ServiceAdaptr.ViewHolder> {

    OnItemClickListener onItemClickListener;
    OnItemClickListenerr onItemClickListenerr;
    public static   int num;
    Context context;
    int client_id,vendor_id;
    List<ServiceModel> guideModels;
    String flage;
    public ServiceAdaptr(Context context, ArrayList<ServiceModel> guideModels) {
        this.context = context;
        this.guideModels = guideModels;
    }
    public interface OnItemClickListener {
        void onclick(int position);
    }

    public interface OnItemClickListenerr {
        void onclick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.guid_item, parent, false));
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ServiceModel guideModel = guideModels.get(position);
        PicassoClass.setImageForStorge(context,guideModel.getImage(),holder.image);
        holder.title.setText(guideModel.getName());
        holder.rel.setOnClickListener(new View.OnClickListener() {
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
        return guideModels.size();
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }
    public void setOnClickListenerr(OnItemClickListenerr onClickListener) {
        this.onItemClickListenerr = onClickListener;
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        ImageView image;
        LinearLayout makeorder;
        RelativeLayout rel;
        public ViewHolder(View itemView) {
            super(itemView);
            image= itemView.findViewById(R.id.image);
            title= itemView.findViewById(R.id.title);
            rel= itemView.findViewById(R.id.rel);
        }
        }
}

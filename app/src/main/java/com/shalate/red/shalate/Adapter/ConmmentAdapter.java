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
import com.shalate.red.shalate.Utilities.PicassoClass;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ahmed on 11/18/2017.
 */

public class ConmmentAdapter extends RecyclerView.Adapter<ConmmentAdapter.ViewHolder> {

    OnItemClickListener onItemClickListener;
    public static int num;
    Context context;
    int client_id, vendor_id;
    List<CommentModel> commentModels;
    String flage;

    public ConmmentAdapter(Context context, ArrayList<CommentModel> commentModels) {
        this.context = context;
        this.commentModels = commentModels;
    }

    public interface OnItemClickListener {
        void onclick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.best_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PicassoClass.setImageForStorge(context, commentModels.get(position).getImage(), holder.prvileImage);
        holder.name.setText(commentModels.get(position).getComment());
        holder.date.setText(commentModels.get(position).getCreated_at());
    }

    @Override
    public int getItemCount() {
        return commentModels.size();
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date;
        ImageView prvileImage;

        public ViewHolder(View itemView) {
            super(itemView);
            prvileImage = itemView.findViewById(R.id.prvileImage);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
        }
    }
}

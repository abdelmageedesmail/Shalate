package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shalate.red.shalate.Activity.ChatActivity;
import com.shalate.red.shalate.Model.CommentModel;
import com.shalate.red.shalate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ahmed on 11/18/2017.
 */

public class ChatAdapterFRAGMENT extends RecyclerView.Adapter<ChatAdapterFRAGMENT.ViewHolder> {

    OnItemClickListener onItemClickListener;
    public static int num;
    Context context;
    int client_id, vendor_id;
    List<CommentModel> commentModels;
    String flage;

    public ChatAdapterFRAGMENT(Context context, ArrayList<CommentModel> commentModels) {
        this.context = context;
        this.commentModels = commentModels;
    }

    public interface OnItemClickListener {
        void onclick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(commentModels.get(position).getName());
        Picasso.with(context).load(commentModels.get(position).getImage()).placeholder(R.drawable.imagepro).into(holder.prvileImage);
        holder.des.setText(commentModels.get(position).getComment());
        holder.chatelin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("cusId", commentModels.get(position).getUserId());
                intent.putExtra("from", "2");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentModels.size();
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, des;
        ImageView prvileImage;
        LinearLayout chatelin;

        public ViewHolder(View itemView) {
            super(itemView);
            prvileImage = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            chatelin = itemView.findViewById(R.id.chatelin);
            des = itemView.findViewById(R.id.des);
        }
    }
}

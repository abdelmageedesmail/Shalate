package com.shalate.red.shalate.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.shalate.red.shalate.Activity.ChatActivity;
import com.shalate.red.shalate.Activity.ShowFollowers;
import com.shalate.red.shalate.Model.FollowModel;
import com.shalate.red.shalate.Model.ListModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageToFollowerAdapter extends RecyclerView.Adapter<MessageToFollowerAdapter.MyHolder> {
    Context context;
    ArrayList<FollowModel> arrayList;
    PrefrencesStorage storage;
    ProgressLoading loading;
    private Dialog dialog;
    LinearLayout liNoData;
    ListModel model;

    public MessageToFollowerAdapter(Context context, ArrayList<FollowModel> arrayList, ProgressLoading loading, PrefrencesStorage storage, LinearLayout liNoData, ListModel model) {
        this.context = context;
        this.arrayList = arrayList;
        this.loading = loading;
        this.storage = storage;
        this.liNoData = liNoData;
        this.model = model;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.followers_item, parent, false);
        MyHolder holder = new MyHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        holder.btnFollow.setVisibility(View.GONE);
        holder.tvName.setText(arrayList.get(position).getUserName());
        Picasso.with(context).load(arrayList.get(position).getImage()).into(holder.ivProfile);
        holder.btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gsonObj = new Gson();
                String jsonStr = gsonObj.toJson(model);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("cusId", arrayList.get(position).getId());
                intent.putExtra("postText", model.getInformation());
//                intent.putExtra("image", model.getPostImages().get(0).getImage());
                intent.putExtra("postModel", jsonStr);
                intent.putExtra("from", "1");
                context.startActivity(intent);
            }
        });

    }

    private void followUser(final MyHolder holder, final int position) {
        loading.showLoading();
        AndroidNetworking.post(Urls.follow + storage.getId())
                .addBodyParameter("follow_id", arrayList.get(position).getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                if (arrayList.get(position).getType().equals("1")) {
                                    Toast.makeText(context, context.getString(R.string.yoFollowThisUser), Toast.LENGTH_SHORT).show();
                                    holder.btnFollow.setText(context.getString(R.string.unFollow));
                                    ShowFollowers.activity.finish();
                                    ShowFollowers.activity.startActivity(ShowFollowers.activity.getIntent());
                                } else {
                                    ShowFollowers.activity.finish();
                                    ShowFollowers.activity.startActivity(ShowFollowers.activity.getIntent());
                                    arrayList.remove(position);
                                    notifyDataSetChanged();
                                    notifyItemRangeChanged(position, arrayList.size());
                                    Toast.makeText(context, context.getString(R.string.youUnFollowThisUser), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    if (arrayList.size() == 0) {
                                        liNoData.setVisibility(View.VISIBLE);
                                    }

                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loading.cancelLoading();
                    }
                });

    }

    private void showPopUpFollowers(final MyHolder holder, final int position) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_cancel_following);
        ImageView ivProfile = dialog.findViewById(R.id.ivProfile);
        TextView tvName = dialog.findViewById(R.id.tvName);
        Button btnRemove = dialog.findViewById(R.id.btnRemove);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followUser(holder, position);
            }
        });
        tvName.setText(arrayList.get(position).getUserName());
        Picasso.with(context).load(arrayList.get(position).getImage()).into(ivProfile);
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile, btnSendMessage;
        TextView tvName, tvProfession;
        Button btnFollow;

        public MyHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvProfession = itemView.findViewById(R.id.tvProfession);
            btnFollow = itemView.findViewById(R.id.btnFollow);
            btnSendMessage = itemView.findViewById(R.id.btnSendMessage);
        }
    }
}

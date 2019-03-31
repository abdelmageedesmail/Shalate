package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.shalate.red.shalate.Model.CommentModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ahmed on 11/18/2017.
 */

public class NotiAdapter extends RecyclerView.Adapter<NotiAdapter.ViewHolder> {

    OnItemClickListener onItemClickListener;
    public static int num;
    Context context;
    int client_id, vendor_id;
    List<CommentModel> commentModels;
    String flage;
    PrefrencesStorage storage;
    ProgressLoading loading;

    public NotiAdapter(Context context, ArrayList<CommentModel> commentModels) {
        this.context = context;
        this.commentModels = commentModels;
        storage = new PrefrencesStorage(context);
        loading = new ProgressLoading(context);
    }

    public interface OnItemClickListener {
        void onclick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Picasso.with(context).load(commentModels.get(position).getImage()).into(holder.prvileImage);
        holder.name.setText(commentModels.get(position).getName());
        holder.tvNotification.setText(commentModels.get(position).getComment());
        holder.time.setText(commentModels.get(position).getTime());
        holder.buttonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.buttonOptions);

                popup.inflate(R.menu.menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.btnDelete:

                                deleteNotification(position);
                                return true;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });


    }

    private void deleteNotification(final int i) {
        loading.showLoading();
        AndroidNetworking.initialize(context);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.get(Urls.getNotification + storage.getId() + "/" + commentModels.get(i).getUserId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                commentModels.remove(i);
                                notifyItemRemoved(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loading.cancelLoading();
                        Log.e("error", "" + anError.getMessage());
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
        private TextView name, tvNotification, time;
        ImageView prvileImage;
        Button buttonOptions;

        public ViewHolder(View itemView) {
            super(itemView);
            prvileImage = itemView.findViewById(R.id.prvileImage);
            name = itemView.findViewById(R.id.name);
            buttonOptions = itemView.findViewById(R.id.buttonOptions);
            tvNotification = itemView.findViewById(R.id.tvNotification);
            time = itemView.findViewById(R.id.time);
        }
    }
}

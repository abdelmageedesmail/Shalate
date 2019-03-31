package com.shalate.red.shalate.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.shalate.red.shalate.Model.PhotoGallay;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PicassoClass;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ahmed on 11/18/2017.
 */

public class MyPostGallaryAdapter extends RecyclerView.Adapter<MyPostGallaryAdapter.ViewHolder> {

    OnItemClickListener onItemClickListener;
    public static int num;
    Context context;

    List<PhotoGallay> faqModels;
    boolean ischecked = false;
    ProgressLoading loading;

    public MyPostGallaryAdapter(Context context, ArrayList<PhotoGallay> faqModels) {
        this.context = context;
        this.faqModels = faqModels;
        loading = new ProgressLoading(context);
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

        String photo = faqModels.get(position).getPhoto();
        if (faqModels.size() == 1) {
            ViewGroup.LayoutParams layoutParams = holder.imageStraged.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.imageStraged.setLayoutParams(layoutParams);
            holder.imageStraged.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        PicassoClass.setImageForStorge(context, photo, holder.imageStraged);

        if (faqModels.get(position).getFrom() != null) {
            if (faqModels.get(position).getFrom().equals("account")) {
                holder.ivClose.setVisibility(View.VISIBLE);
                holder.ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(context.getString(R.string.doYouWantToDeleteImage))
                                .setPositiveButton(context.getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteImage(position);
                                    }
                                }).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                    }
                });
            } else {
                holder.ivClose.setVisibility(View.GONE);
            }

        } else {
            holder.ivClose.setVisibility(View.GONE);
        }
        holder.imageStraged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onclick(position);
                }
            }
        });

    }

    private void deleteImage(final int position) {
        loading.showLoading();
        AndroidNetworking.initialize(context);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(Urls.deleteImage + faqModels.get(position).getImageId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                faqModels.remove(position);
                                notifyDataSetChanged();
                                notifyItemRangeChanged(position, faqModels.size());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error", "" + anError.getMessage());
                        loading.cancelLoading();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return faqModels.size();
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageStraged, ivClose;

        public ViewHolder(View itemView) {
            super(itemView);

            imageStraged = itemView.findViewById(R.id.ivItemGridImage);
            ivClose = itemView.findViewById(R.id.ivClose);

        }
    }
}

package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.darsh.multipleimageselect.models.Image;
import com.shalate.red.shalate.R;

import java.util.List;

/**
 * Created by waheed_manii on 12/9/2017.
 */

public class UriAdapter extends RecyclerView.Adapter<UriAdapter.UriViewHolder> {

    Context context;
    private List<Image> mUris;
    private List<Uri> mPaths;
    private Bitmap bitmap1;

    public void setData(Context context, List<Image> uris) {
        mUris = uris;
        this.context = context;
//        mPaths = paths;
        notifyDataSetChanged();
    }

    @Override
    public UriViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UriViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_uri, parent, false));
    }

    @Override
    public void onBindViewHolder(UriViewHolder holder, int position) {
//        File sd = Environment.getExternalStorageDirectory();
//        File image = new File(sd + mUris.get(position).path, mUris.get(position).name);
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
//        bitmap = Bitmap.createScaledBitmap(bitmap, 100, 200, true);
        holder.image_raw.setImageBitmap(BitmapFactory.decodeFile(mUris.get(position).path));

    }

    @Override
    public int getItemCount() {
        return mUris == null ? 0 : mUris.size();
    }

    static class UriViewHolder extends RecyclerView.ViewHolder {

        private TextView mUri;
        private TextView mPath;
        ImageView image_raw;

        UriViewHolder(View contentView) {
            super(contentView);
            mUri = contentView.findViewById(R.id.uri);
            mPath = contentView.findViewById(R.id.path);
            image_raw = contentView.findViewById(R.id.image_raw);
        }
    }
}
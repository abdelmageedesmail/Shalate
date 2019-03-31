package com.shalate.red.shalate.Adapter;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shalate.red.shalate.Activity.ImageDetails;
import com.shalate.red.shalate.Activity.MapUserTrack;
import com.shalate.red.shalate.Activity.PostDetails;
import com.shalate.red.shalate.Model.ChatModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.DateTimeFormating;
import com.shalate.red.shalate.Utilities.GPSTracker;
import com.shalate.red.shalate.Utilities.PicassoClass;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
//
//import hb.xvideoplayer.MxVideoPlayer;
//import hb.xvideoplayer.MxVideoPlayerWidget;

/**
 * Created by Ahmed on 8/27/2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.HolderView> {
    Context context;
    ArrayList<ChatModel> list;
    LayoutInflater layoutInflater;
    String myID = "5";
    final int radius = 20;
    final int margin = 0;
    PrefrencesStorage storage;
    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    Runnable runnable;
    private double latitude, longituide;
    GPSTracker mGps;
    LatLng sydney;

    public ChatAdapter(Context context, ArrayList<ChatModel> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
        storage = new PrefrencesStorage(context);
        mGps = new GPSTracker(context);
    }

    @NonNull
    @Override
    public HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.row_chat, parent, false);
        return new HolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HolderView holder, final int position) {
        final ChatModel model = list.get(position);
        holder.tvTime.setText(DateTimeFormating.getFriendlyTime(model.getDate(), context));
        if (model.getFrom().equalsIgnoreCase(storage.getId())) {
            holder.viewContainer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            holder.tvMessage.setBackgroundResource(R.drawable.draw_chat_me);
            //   holder.viewMedia.setBackgroundResource(R.drawable.bubleone);
            holder.tvMessage.setTextColor(Color.WHITE);
        } else {
            holder.viewContainer.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            holder.tvMessage.setBackgroundResource(R.drawable.draw_chat_other);
            //     holder.viewMedia.setBackgroundResource(R.drawable.draw_chat_other);
            holder.tvMessage.setTextColor(Color.BLACK);
        }
        if (list.get(position).getIsLocation() != null) {
            if (list.get(position).getIsLocation().equals("1")) {
                holder.tvMessage.setPaintFlags(holder.tvMessage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
        }

        holder.frMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.get(position).getIsLocation().equals("1")) {
                    Intent intent = new Intent(context, MapUserTrack.class);
                    intent.putExtra("lat", list.get(position).getLat());
                    intent.putExtra("lon", list.get(position).getLon());
                    intent.putExtra("from", list.get(position).getFrom());
                    intent.putExtra("to", list.get(position).getTo());
                    context.startActivity(intent);
                }
            }
        });


        switch (model.getType()) {
            case 1:
                if (list.get(position).getIsLocation()!=null){
                    if (list.get(position).getIsLocation().equals("1")) {
                        holder.frPost.setVisibility(View.GONE);
                        holder.tvMessage.setVisibility(View.GONE);
                        holder.viewMedia.setVisibility(View.GONE);
                        holder.ivImageContent.setVisibility(View.GONE);
                        holder.tvMedia.setVisibility(View.GONE);
                        holder.liSound.setVisibility(View.GONE);
                        holder.frMap.setVisibility(View.VISIBLE);
                        latitude = Double.parseDouble(list.get(position).getLat());
                        longituide = Double.parseDouble(list.get(position).getLon());
                        Log.e("latLang", "" + latitude + ".." + longituide);
                        holder.tvAddress.setText(list.get(position).getMessage());
                    } else {
                        holder.frPost.setVisibility(View.GONE);
                        holder.tvMessage.setVisibility(View.VISIBLE);
                        holder.viewMedia.setVisibility(View.GONE);
                        holder.ivImageContent.setVisibility(View.GONE);
                        holder.tvMedia.setVisibility(View.GONE);
                        holder.liSound.setVisibility(View.GONE);
                        holder.tvMessage.setText(model.getMessage());
                        holder.frMap.setVisibility(View.GONE);
                    }
                }
                break;

            case 2:
                holder.frPost.setVisibility(View.GONE);
                holder.tvMessage.setVisibility(View.GONE);
                holder.viewMedia.setVisibility(View.VISIBLE);
                holder.ivImageContent.setVisibility(View.VISIBLE);
                holder.tvMedia.setVisibility(View.GONE);
                holder.liSound.setVisibility(View.GONE);
                holder.frMap.setVisibility(View.GONE);
                PicassoClass.setImageForStorge(context, model.getMessage(), holder.ivImageContent);
                holder.ivImageContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ImageDetails.class);
                        intent.putExtra("imageName", list.get(holder.getAdapterPosition()).getMessage());
                        context.startActivity(intent);
                    }
                });
                break;
            case 3:
                holder.frPost.setVisibility(View.GONE);
                holder.tvMessage.setVisibility(View.GONE);
                holder.viewMedia.setVisibility(View.VISIBLE);
                holder.ivImageContent.setVisibility(View.GONE);
                holder.tvMedia.setVisibility(View.VISIBLE);
                holder.liSound.setVisibility(View.GONE);
                holder.frMap.setVisibility(View.GONE);
                holder.tvMedia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDownload(model.getMessage());
                    }
                });
                break;
            case 4:
                holder.frPost.setVisibility(View.GONE);
                holder.tvMessage.setVisibility(View.GONE);
                holder.viewMedia.setVisibility(View.GONE);
                holder.ivImageContent.setVisibility(View.GONE);
                holder.tvMedia.setVisibility(View.GONE);
                holder.frMap.setVisibility(View.GONE);
                holder.liSound.setVisibility(View.VISIBLE);
                Log.e("sound", "" + model.getVoiceRecrod());

                holder.ivPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (list.get(holder.getAdapterPosition()).getVoiceRecrod() != null) {
                            mediaPlayer = MediaPlayer.create(context, Uri.parse(list.get(holder.getAdapterPosition()).getVoiceRecrod()));
                            if (mediaPlayer != null) {
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        holder.seekBar.setMax(mediaPlayer.getDuration());
                                        changeSeekBar(holder);
                                    }
                                });

                                holder.ivPlay.setVisibility(View.GONE);
                                holder.ivPause.setVisibility(View.VISIBLE);
                                mediaPlayer.start();
                                changeSeekBar(holder);
                            }


                        }


                    }
                });

                holder.ivPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.ivPlay.setVisibility(View.VISIBLE);
                        holder.ivPause.setVisibility(View.GONE);
                        mediaPlayer.pause();
                    }
                });


                holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (b) {
                            holder.seekBar.setMax(mediaPlayer.getDuration());
                            changeSeekBar(holder);
                            mediaPlayer.seekTo(i);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                break;
            case 5:
                holder.tvMessage.setVisibility(View.GONE);
                holder.viewMedia.setVisibility(View.GONE);
                holder.ivImageContent.setVisibility(View.GONE);
                holder.tvMedia.setVisibility(View.VISIBLE);
                holder.liSound.setVisibility(View.GONE);
                holder.frMap.setVisibility(View.GONE);
                holder.frPost.setVisibility(View.VISIBLE);
                if (list.get(position).getPostImage().endsWith(".jpg") || list.get(position).getPostImage().endsWith(".jpeg")) {
                    holder.ivPost.setVisibility(View.VISIBLE);
//                    holder.videoPlayerWidget.setVisibility(View.GONE);
                    Picasso.with(context).load(list.get(position).getPostImage()).into(holder.ivPost);
                } else {
                    holder.ivPost.setVisibility(View.GONE);
//                    holder.videoPlayerWidget.setVisibility(View.VISIBLE);
//                    holder.videoPlayerWidget.startPlay(list.get(position).getPostImage(), MxVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
                }

                holder.tvPost.setText(list.get(position).getMessage());

                holder.frPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, PostDetails.class);
                        intent.putExtra("postId", list.get(position).getPostId());
                        intent.putExtra("from", "post");
                        context.startActivity(intent);
                    }
                });
        }
    }

    private void changeSeekBar(final HolderView holder) {
        holder.seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    changeSeekBar(holder);
                }
            };
            handler.postDelayed(runnable, 1000);
        } else {
            holder.ivPlay.setVisibility(View.VISIBLE);
            holder.ivPause.setVisibility(View.GONE);
        }
    }

    public void startDownload(String url) {
        DownloadManager mManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request mRqRequest = new DownloadManager.Request(
                Uri.parse(url));
        mRqRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        mRqRequest.setDescription("Download");
//  mRqRequest.setDestinationUri(Uri.parse("give your local path"));
        long idDownLoad = mManager.enqueue(mRqRequest);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HolderView extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        View viewContainer;
        TextView tvMessage, tvTime, tvAddress, tvPost;
        View viewMedia;
        ImageView ivImageContent, ivPlay, ivPause, ivPost;
        TextView tvMedia;
        LinearLayout liSound;
        SeekBar seekBar;
        GoogleMap gmap;
        private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
        MapView mapView;
        CardView frMap;
        FrameLayout frPost;
//        MxVideoPlayerWidget videoPlayerWidget;

        public HolderView(View itemView) {
            super(itemView);
            viewContainer = itemView.findViewById(R.id.viewContainer);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            viewMedia = itemView.findViewById(R.id.viewMedia);
            ivImageContent = itemView.findViewById(R.id.ivImageContent);
            tvMedia = itemView.findViewById(R.id.tvMedia);
            liSound = itemView.findViewById(R.id.liSound);
            ivPlay = itemView.findViewById(R.id.ivPlay);
            ivPause = itemView.findViewById(R.id.ivPause);
            seekBar = itemView.findViewById(R.id.seekBar);
            tvMedia.setPaintFlags(tvMedia.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            mapView = itemView.findViewById(R.id.map_view);
            frMap = itemView.findViewById(R.id.frMap);
            tvPost = itemView.findViewById(R.id.tvPost);
            ivPost = itemView.findViewById(R.id.ivPost);
            frPost = itemView.findViewById(R.id.frPost);
//            videoPlayerWidget = itemView.findViewById(R.id.mpw_video_player);
            if (mapView != null) {
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(this);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (latitude == 0.0) {
                sydney = new LatLng(29.334542, 48.067307);
            } else {
                sydney = new LatLng(latitude, longituide);
            }
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            mapView.setClickable(false);
            googleMap.setMyLocationEnabled(true);
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    sydney, 4);
            googleMap.animateCamera(location);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setAllGesturesEnabled(false);
        }
    }
}

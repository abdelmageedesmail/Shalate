package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.shalate.red.shalate.Model.VideoModel;
import com.shalate.red.shalate.R;

import java.util.ArrayList;

//import hb.xvideoplayer.MxVideoPlayer;
//import hb.xvideoplayer.MxVideoPlayerWidget;


/**
 * Created by ahmed on 11/18/2017.
 */

public class VideoAccountAdapter extends RecyclerView.Adapter<VideoAccountAdapter.ViewHolder> {

    OnItemClickListener onItemClickListener;
    public static int num;
    Context context;
    private HttpProxyCacheServer proxy;

    ArrayList<VideoModel> arrayList;
    boolean ischecked = false;
    private SimpleExoPlayer exoPlayer;

    public VideoAccountAdapter(Context context, ArrayList<VideoModel> faqModels) {
        this.context = context;
        this.arrayList = faqModels;
    }

    public interface OnItemClickListener {
        void onclick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        HttpProxyCacheServer proxy = getProxy();
        String proxyUrl = proxy.getProxyUrl(arrayList.get(position).getVideoPath());
        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            Uri videoURI = Uri.parse(arrayList.get(position).getVideoPath());
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);
            holder.exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(false);
        } catch (Exception e) {
            Log.e("MainAcvtivity", " exoplayer error " + e.toString());
        }

    }


    private HttpProxyCacheServer getProxy() {
        // should return single instance of HttpProxyCacheServer shared for whole app.

        return proxy == null ? (proxy = newProxy()) : proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(context);
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        FrameLayout video_layout;
        //        MxVideoPlayerWidget videoPlayerWidget;
        SimpleExoPlayerView exoPlayerView;

        public ViewHolder(View itemView) {
            super(itemView);
            video_layout = itemView.findViewById(R.id.video_layout);
            exoPlayerView = itemView.findViewById(R.id.exo_player_view);
        }
    }
}

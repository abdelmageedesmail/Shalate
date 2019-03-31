package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
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
import com.google.gson.Gson;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;
import com.shalate.red.shalate.Activity.ImageListActivity;
import com.shalate.red.shalate.Activity.PostDetails;
import com.shalate.red.shalate.Activity.ProfessionActivity;
import com.shalate.red.shalate.Activity.SendMessageToFollowersActivity;
import com.shalate.red.shalate.Model.ImageModel;
import com.shalate.red.shalate.Model.ListModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.internet.Urls;
import com.squareup.picasso.Picasso;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by ahmed on 11/18/2017.
 */

public class RecycleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    OnItemClickListener onItemClickListener;
    OnItemClickListenerr onItemClickListenerr;
    public static int num;
    Context context;
    int client_id, vendor_id;
    List<ListModel> listModels;
    String flage;
    PrefrencesStorage storage;
    private String lang;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private String city, country;
    private HttpProxyCacheServer proxy;
    public static ArrayList<ImageModel> arrayList;
    private boolean isLoadingAdded = false;
    public static SimpleExoPlayer exoPlayer;

    public RecycleListAdapter(Context context, ArrayList<ListModel> listModels, PrefrencesStorage storage) {
        this.context = context;
        this.listModels = listModels;
        this.storage = storage;
        arrayList = new ArrayList<>();
    }

    public interface OnItemClickListener {
        void onclick(int position);
    }

    public interface OnItemClickListenerr {
        void onclick(int position);
    }

    private void addLike(int position) {
        if (Locale.getDefault().getDisplayLanguage().equals("ar")) {
            lang = "ar";
        } else {
            lang = "en";
        }
        AndroidNetworking.initialize(context);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(Urls.like)
                .addBodyParameter("user_id", storage.getId())
                .addBodyParameter("post_id", listModels.get(position).getPostId())
                .addBodyParameter("lang", lang)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", "" + response);
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error", "" + anError.getMessage());
                    }
                });

    }

    @Override
    public int getItemViewType(int position) {
        return listModels.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        //  return position;
    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycle_item, parent, false));
            return viewHolder;
        } else {
            View inflate = LayoutInflater.from(context).inflate(R.layout.loading_item, parent, false);
            ProgressViewHolder holder = new ProgressViewHolder(inflate);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final ListModel recycleVenderModel = listModels.get(position);
            Picasso.with(context).load(recycleVenderModel.getImageProfile()).into(((ViewHolder) holder).prvileImage);
            ((ViewHolder) holder).name.setText(recycleVenderModel.getName());
            ((ViewHolder) holder).des.setText(recycleVenderModel.getProfession());
            ((ViewHolder) holder).tvDate.setText(recycleVenderModel.getCreated_at());
            if (recycleVenderModel.getInformation().equals("null")) {
                ((ViewHolder) holder).tvContent.setText("");
            } else {
                ((ViewHolder) holder).tvContent.setText(recycleVenderModel.getInformation());
            }

            if (recycleVenderModel.getLat().equals("")) {
                ((ViewHolder) holder).tvLocation.setVisibility(View.GONE);
                ((ViewHolder) holder).tvIn.setVisibility(View.GONE);
            } else {
                ((ViewHolder) holder).tvLocation.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).tvIn.setVisibility(View.VISIBLE);
                getAddress(position);
                ((ViewHolder) holder).tvLocation.setText(city + " ," + country);
            }
            if (recycleVenderModel.getLikeCount().equals("0")) {
                ((ViewHolder) holder).ivLiked.setVisibility(View.GONE);
                ((ViewHolder) holder).tvLikeCount.setVisibility(View.GONE);
            } else {
                ((ViewHolder) holder).ivLiked.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).tvLikeCount.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).tvLikeCount.setText(recycleVenderModel.getLikeCount());
            }

            ((ViewHolder) holder).prvileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfessionActivity.class);
                    intent.putExtra("id", recycleVenderModel.getUserID());
                    context.startActivity(intent);
                }
            });
            if (!recycleVenderModel.getCommentCount().equals("")) {
                ((ViewHolder) holder).tvCommentNum.setText("( " + recycleVenderModel.getCommentCount() + " )");
            } else {
                ((ViewHolder) holder).tvCommentNum.setText("( 0 )");
            }
            if (recycleVenderModel.getLike() != null) {
                if (recycleVenderModel.getLike().equals("1")) {
                    Log.e("postID", "" + recycleVenderModel.getArrayLikes());
                    ((ViewHolder) holder).liLike.setImageResource(0);
                    ((ViewHolder) holder).liLike.setImageResource(R.drawable.ic_like);
                } else {
                    ((ViewHolder) holder).liLike.setImageResource(0);
                    ((ViewHolder) holder).liLike.setImageResource(R.drawable.h1);
                }
            }

            ((ViewHolder) holder).tvCommentName1.setText(recycleVenderModel.getCommentName1());
            ((ViewHolder) holder).tvCommentName2.setText(recycleVenderModel.getCommentName2());
            ((ViewHolder) holder).commentContent1.setText(recycleVenderModel.getComment1());
            ((ViewHolder) holder).comment2.setText(recycleVenderModel.getComment2());
            if (recycleVenderModel.getPostImages() != null) {
                if (recycleVenderModel.getPostImages().size() > 0) {
                    ((ViewHolder) holder).ivNoImages.setVisibility(View.GONE);
                    ImagePostAdapter imagePostAdapter = new ImagePostAdapter(context, recycleVenderModel.getPostImages());
                    ((ViewHolder) holder).rvImages.setAdapter(imagePostAdapter);
                    ((ViewHolder) holder).rvImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    ((ViewHolder) holder).indefinitePagerIndicator.attachToRecyclerView(((ViewHolder) holder).rvImages);
                    imagePostAdapter.onImageClick(new ImagePostAdapter.ImageClick() {
                        @Override
                        public void onImageClick(int position) {

                            arrayList = recycleVenderModel.getPostImages();
                            Intent intent = new Intent(context, ImageListActivity.class);
                            intent.putExtra("from", "1");
                            context.startActivity(intent);
                        }
                    });

                } else {
                    ((ViewHolder) holder).ivNoImages.setVisibility(View.VISIBLE);
                }
            }


            ((ViewHolder) holder).liLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ViewHolder) holder).liLike.setImageResource(0);
                    if (listModels.get(position).getLike().equals("1")) {
                        ((ViewHolder) holder).liLike.setImageResource(R.drawable.h1);
                    } else {
                        ((ViewHolder) holder).liLike.setImageResource(R.drawable.ic_like);
                    }

                    addLike(holder.getAdapterPosition());
                }
            });

            ((ViewHolder) holder).ivChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gson gsonObj = new Gson();
                    String jsonStr = gsonObj.toJson(listModels.get(position));
                    Intent intent = new Intent(context, SendMessageToFollowersActivity.class);
                    intent.putExtra("postModel", jsonStr);
                    intent.putExtra("from", "1");
                    context.startActivity(intent);
                }
            });

            ((ViewHolder) holder).liContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gson gsonObj = new Gson();
                    String jsonStr = gsonObj.toJson(listModels.get(position));
                    Intent intent = new Intent(context, PostDetails.class);
                    intent.putExtra("chaletModel", jsonStr);
                    intent.putExtra("postID", listModels.get(position).getPostId());
                    Log.e("postId", "" + listModels.get(position).getPostId());
                    context.startActivity(intent);

                }
            });

            if (!listModels.get(position).getVideoUrl().equals("")) {
                ((ViewHolder) holder).exoPlayerView.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).frImages.setVisibility(View.GONE);
                try {
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                    Uri videoURI = Uri.parse(listModels.get(position).getVideoUrl());
                    DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                    MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);
                    ((ViewHolder) holder).exoPlayerView.setPlayer(exoPlayer);
                    exoPlayer.prepare(mediaSource);
                    exoPlayer.setPlayWhenReady(false);
                } catch (Exception e) {
                    Log.e("MainAcvtivity", " exoplayer error " + e.toString());
                }


                //                ((ViewHolder) holder).jcVideoPlayerStandard.thumbImageView.setImage("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");

            } else {
                ((ViewHolder) holder).ivThumbile.setVisibility(View.GONE);
                ((ViewHolder) holder).exoPlayerView.setVisibility(View.GONE);
                ((ViewHolder) holder).frImages.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).video_layout.setVisibility(View.GONE);
            }
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }


    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }


    private HttpProxyCacheServer getProxy() {
        // should return single instance of HttpProxyCacheServer shared for whole app.

        return proxy == null ? (proxy = newProxy()) : proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(context);
    }

    private void getAddress(int pos) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(listModels.get(pos).getLat()), Double.parseDouble(listModels.get(pos).getLng()), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void add(ListModel mc) {
        listModels.add(mc);
        notifyItemInserted(listModels.size() - 1);
    }

    public void addAll(List<ListModel> mcList) {
        for (ListModel mc : mcList) {
            add(mc);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        listModels.add(null);
        notifyItemInserted(listModels.size() - 1);
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = listModels.size() - 1;
        ListModel item = getItem(position);
        listModels.remove(position);
        notifyItemRemoved(listModels.size());
    }

    private ListModel getItem(int i) {
        return listModels.get(i);
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    public void setOnClickListenerr(OnItemClickListenerr onClickListener) {
        this.onItemClickListenerr = onClickListener;
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImage(ImageView bmImage) {
            this.bmImage = (ImageView) bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            Bitmap myBitmap = null;
            MediaMetadataRetriever mMRetriever = null;
            try {
                mMRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14)
                    mMRetriever.setDataSource(urls[0], new HashMap<String, String>());
                else
                    mMRetriever.setDataSource(urls[0]);
                myBitmap = mMRetriever.getFrameAtTime();
            } catch (Exception e) {
                e.printStackTrace();


            } finally {
                if (mMRetriever != null) {
                    mMRetriever.release();
                }
            }
            return myBitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //        MxVideoPlayerWidget videoPlayerWidget;
        public SimpleExoPlayerView exoPlayerView;

        ImageView prvileImage, liLike, ivComment, ivChat, ivLiked, ivNoImages, ivThumbile, ivPlay;
        View mk_player;
        LinearLayout makeorder, liContainer;
        TextView name, des, tvContent, tvLikeCount, tvCommentNum, tvCommentName1, commentContent1, tvCommentName2, comment2, tvLocation, tvIn, tvDate;
        RecyclerView rvImages;
        IndefinitePagerIndicator indefinitePagerIndicator;
        UniversalVideoView mVideoView;
        UniversalMediaController mMediaController;
        FrameLayout video_layout, frImages, video_layoutView;
        VideoView vid_view;

        public ViewHolder(View itemView) {
            super(itemView);
            prvileImage = itemView.findViewById(R.id.prvileImage);
            makeorder = itemView.findViewById(R.id.makeorder);
            liLike = itemView.findViewById(R.id.liLike);
            ivChat = itemView.findViewById(R.id.ivChat);
            name = itemView.findViewById(R.id.name);
            des = itemView.findViewById(R.id.des);
            tvContent = itemView.findViewById(R.id.tvContent);
            ivComment = itemView.findViewById(R.id.ivComment);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentNum = itemView.findViewById(R.id.tvCommentNum);
            tvCommentName1 = itemView.findViewById(R.id.tvCommentName1);
            commentContent1 = itemView.findViewById(R.id.commentContent1);
            tvCommentName2 = itemView.findViewById(R.id.tvCommentName2);
            comment2 = itemView.findViewById(R.id.comment2);
            rvImages = itemView.findViewById(R.id.rvImages);
            indefinitePagerIndicator = itemView.findViewById(R.id.recyclerview_pager_indicator);
            liContainer = itemView.findViewById(R.id.liContainer);
            mVideoView = itemView.findViewById(R.id.videoView);
            mMediaController = itemView.findViewById(R.id.media_controller);
            video_layout = itemView.findViewById(R.id.video_layout);
            frImages = itemView.findViewById(R.id.frImages);
            ivThumbile = itemView.findViewById(R.id.ivThumbile);
            ivLiked = itemView.findViewById(R.id.ivLiked);
//            videoPlayerWidget = itemView.findViewById(R.id.mpw_video_player);
            exoPlayerView = itemView.findViewById(R.id.exo_player_view);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvIn = itemView.findViewById(R.id.tvIn);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivNoImages = itemView.findViewById(R.id.ivNoImages);
            mVideoView.setMediaController(mMediaController);
        }
    }
}

package com.shalate.red.shalate.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
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
import com.google.gson.reflect.TypeToken;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.r0adkll.slidr.Slidr;
import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;
import com.shalate.red.shalate.Adapter.ConmmentAdapter;
import com.shalate.red.shalate.Adapter.PhotoGallaryAdapter;
import com.shalate.red.shalate.Model.CommentModel;
import com.shalate.red.shalate.Model.ImageModel;
import com.shalate.red.shalate.Model.ListModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;
import com.squareup.picasso.Picasso;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
//
//import hb.xvideoplayer.MxVideoPlayer;
//import hb.xvideoplayer.MxVideoPlayerWidget;

public class PostDetails extends AppCompatActivity {

    ImageView prvileImage, liLike, ivComment, ivChat, ivBack, ivNoImage;
    LinearLayout makeorder;
    TextView name, des, tvContent, tvLikeCount, tvCommentNum, tvCommentName1, commentContent1, tvCommentName2, comment2, tvLocation, tvIn, tvDate;
    RecyclerView rvImages, rvComments;
    EditText etComment;
    Button btnAdd;
    IndefinitePagerIndicator indefinitePagerIndicator;
    private String items;
    private ListModel model;
    PrefrencesStorage storage;
    ProgressLoading loading;
    private String lang;
    public static ArrayList<ImageModel> arrayList;
    FrameLayout video_layout;
    UniversalVideoView mVideoView;
    UniversalMediaController mMediaController;
    private String postId;
    private ArrayList<CommentModel> arrayComments;
    private String id;
    private String postID;
    private int count;
    //    MxVideoPlayerWidget videoPlayerWidget;
    private String city, country;
    private String timeZone;
    private String time;
    private String pId;
    private ListModel listModel;
    private HttpProxyCacheServer proxy;
    SimpleExoPlayerView exoPlayerView;
    private SimpleExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Slidr.attach(this);
        bind();
        init();
        getIntentData();

    }

    private void init() {
        storage = new PrefrencesStorage(this);
        loading = new ProgressLoading(this);
        arrayList = new ArrayList<>();
        arrayComments = new ArrayList<>();
    }

    private void bind() {
        prvileImage = findViewById(R.id.prvileImage);
        makeorder = findViewById(R.id.makeorder);
        liLike = findViewById(R.id.liLike);
        ivChat = findViewById(R.id.ivChat);
        name = findViewById(R.id.name);
        des = findViewById(R.id.des);
        tvContent = findViewById(R.id.tvContent);
        ivComment = findViewById(R.id.ivComment);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        rvImages = findViewById(R.id.rvImages);
        indefinitePagerIndicator = findViewById(R.id.recyclerview_pager_indicator);
        etComment = findViewById(R.id.etComment);
        btnAdd = findViewById(R.id.btnAdd);
        rvComments = findViewById(R.id.rvComments);
        video_layout = findViewById(R.id.video_layout);
        mVideoView = findViewById(R.id.videoView);
        mMediaController = findViewById(R.id.media_controller);
        ivBack = findViewById(R.id.ivBack);
        tvLocation = findViewById(R.id.tvLocation);
        tvIn = findViewById(R.id.tvIn);
        exoPlayerView = findViewById(R.id.exo_player_view);
//        videoPlayerWidget = findViewById(R.id.mpw_video_player);
        tvDate = findViewById(R.id.tvDate);
        ivNoImage = findViewById(R.id.ivNoImage);

        liLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model != null) {
                    addLike(model.getPostId());
                } else {
                    addLike(postID);
                }
            }
        });
        ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model != null) {
                    Gson gsonObj = new Gson();
                    String jsonStr = gsonObj.toJson(model);
                    Intent intent = new Intent(PostDetails.this, SendMessageToFollowersActivity.class);
                    intent.putExtra("postModel", jsonStr);
                    intent.putExtra("from", "1");
                    startActivity(intent);
                } else {
                    Gson gsonObj = new Gson();
                    String jsonStr = gsonObj.toJson(listModel);
                    Intent intent = new Intent(PostDetails.this, SendMessageToFollowersActivity.class);
                    intent.putExtra("postModel", jsonStr);
                    intent.putExtra("from", "1");
                    startActivity(intent);

                }
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });
    }

    private void addLike(String postId) {
        if (Locale.getDefault().getDisplayLanguage().equals("ar")) {
            lang = "ar";
        } else {
            lang = "en";
        }
        int i = Integer.parseInt(tvLikeCount.getText().toString());
        if (liLike.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_like).getConstantState()) {
            liLike.setImageResource(R.drawable.h1);
            count = i - 1;
            tvLikeCount.setText("" + count);
        } else {
            liLike.setImageResource(R.drawable.ic_like);
            count = i + 1;
            tvLikeCount.setText("" + count);
        }
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(Urls.like)
                .addBodyParameter("user_id", storage.getId())
                .addBodyParameter("post_id", postId)
                .addBodyParameter("lang", lang)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", "" + response);
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
//                                int i = Integer.parseInt(tvLikeCount.getText().toString());
//                                if (liLike.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_like).getConstantState()) {
//                                    liLike.setImageResource(R.drawable.h1);
//                                    count = i - 1;
//                                    tvLikeCount.setText("" + count);
//                                } else {
//                                    liLike.setImageResource(R.drawable.ic_like);
//                                    count = i + 1;
//                                    tvLikeCount.setText("" + count);
//                                }
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


    private void fillComments() {
        Log.e("size", "..." + model.getArrayComments());
        if (model.getArrayComments() != null) {
            if (model.getArrayComments().size() > 0) {
                rvComments.setAdapter(new ConmmentAdapter(this, model.getArrayComments()));
                rvComments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            }
        }

    }

    private void getIntentData() {
        if (getIntent().getExtras().getString("from") != null) {
            if (getIntent().getExtras().getString("from").equals("post")) {
                postId = getIntent().getExtras().getString("postId");
                getPostDetails();
                storage.storeKey("postId", postId);
            } else if (getIntent().getExtras().getString("from").equals("notification")) {
                postId = getIntent().getExtras().getString("postId");
                getPostDetails();
                storage.storeKey("postId", postId);
            }
        } else {
            model = new Gson().fromJson(getIntent().getStringExtra("chaletModel"), new TypeToken<ListModel>() {
            }.getType());
            postId = getIntent().getExtras().getString("postID");
            tvDate.setText(model.getCreated_at());
            if (model.getLike().equals("1")) {
                liLike.setImageResource(0);
                liLike.setImageResource(R.drawable.ic_like);
            } else {
                liLike.setImageResource(0);
                liLike.setImageResource(R.drawable.h1);
            }
            if (model.getLat().equals("")) {
                tvIn.setVisibility(View.GONE);
                tvLocation.setVisibility(View.GONE);
            } else {
                tvIn.setVisibility(View.VISIBLE);
                tvLocation.setVisibility(View.VISIBLE);
                getAddress(Double.parseDouble(model.getLat()), Double.parseDouble(model.getLng()));
                tvLocation.setText(city + " ," + country);
            }
            storage.storeKey("postId", postId);
//            postId = model.getPostId();
//            fillViews();
//            fillViewImages();
//            fillComments();
            getPostDetails();
        }
        pId = storage.getKey("postId");

        prvileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetails.this, ProfessionActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

    }

    private void fillViews() {
        Picasso.with(this).load(model.getImageProfile()).into(prvileImage);

        if (model.getVideoUrl().equals("")) {
            video_layout.setVisibility(View.GONE);
            rvImages.setVisibility(View.VISIBLE);
//            videoPlayerWidget.setVisibility(View.GONE);
        } else {
            rvImages.setVisibility(View.GONE);
            ivNoImage.setVisibility(View.GONE);
//            videoPlayerWidget.setVisibility(View.VISIBLE);
//            videoPlayerWidget.startPlay(model.getVideoUrl(), MxVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
        }

        name.setText(model.getName());
        des.setText(model.getProfession());
        Log.e("image", model.getImageProfile() + "...." + model.getProfession());
        if (model.getInformation().equals("null")) {
            tvContent.setText("");
        } else {
            tvContent.setText(model.getInformation());
        }

        tvLikeCount.setText(model.getLike());
//        rvImages.setAdapter(new ImagePostAdapter(this, model.getPostImages()));
//        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        indefinitePagerIndicator.attachToRecyclerView(rvImages);
    }

    public void addComment() {
        if (Locale.getDefault().getDisplayLanguage().equals("ar")) {
            lang = "ar";
        } else {
            lang = "en";
        }

        String addPostComment = Urls.addPostComment;
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        ANRequest.PostRequestBuilder post = AndroidNetworking.post(addPostComment);
        post.addBodyParameter("user_id", storage.getId());
        if (model != null) {
            post.addBodyParameter("post_id", model.getPostId());
        } else {
            post.addBodyParameter("post_id", postId);
        }
        post.addBodyParameter("comment", etComment.getText().toString())
                .addBodyParameter("lang", lang)
                .setPriority(Priority.MEDIUM);

        etComment.setText("");
        post.build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.cancelLoading();
                        Log.e("response", "" + response);
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
//                                Toast.makeText(PostDetails.this, getString(R.string.youAddedCommentSuccessfully), Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(PostDetails.this, HomePage.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                                getPostDetailsAfterComment(pId);
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

    private void getPostDetails() {
        loading.showLoading();
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.get(Urls.getPostDetails + postId)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", "" + response);
                        try {
                            loading.cancelLoading();
                            listModel = new ListModel();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONObject data = response.getJSONObject("data");
                                String lat = data.getString("lat");
                                String lng = data.getString("lng");

                                String created_at = data.getString("created_at");
                                String[] s = created_at.split(" ");
                                String[] split = s[1].split(":");
                                String time1 = split[0] + ":" + split[1];
                                if (Integer.parseInt(split[0]) > 12) {
                                    timeZone = getString(R.string.timeZoneNight);
                                } else {
                                    timeZone = getString(R.string.timeZoneMorning);
                                }
                                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = null;
                                try {
                                    date = format.parse(created_at);
                                    final Date dateObj = sdf.parse(s[1]);
                                    System.out.println(dateObj);
                                    System.out.println(new SimpleDateFormat("K:mm").format(dateObj));

                                    time = new SimpleDateFormat("K:mm").format(dateObj);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String day = (String) DateFormat.format("dd", date); // 20
                                String monthString = (String) DateFormat.format("MMM", date); // Jun
                                String monthNumber = (String) DateFormat.format("MM", date); // 06
                                String year = (String) DateFormat.format("yyyy", date); // 2013
                                tvDate.setText(day + " " + monthString + " " + time);
                                if (lat.equals("")) {
                                    tvIn.setVisibility(View.GONE);
                                    tvLocation.setVisibility(View.GONE);
                                } else {
                                    tvIn.setVisibility(View.VISIBLE);
                                    tvLocation.setVisibility(View.VISIBLE);
                                    getAddress(Double.parseDouble(lat), Double.parseDouble(lng));
                                    tvLocation.setText(city + " ," + country);
                                }
                                postID = data.getString("id");
                                String likes = data.getString("likes");
                                if (likes.equals("1")) {
                                    liLike.setImageResource(0);
                                    liLike.setImageResource(R.drawable.ic_like);
                                } else {
                                    liLike.setImageResource(0);
                                    liLike.setImageResource(R.drawable.h1);
                                }
                                JSONObject user_id = data.getJSONObject("user_id");
                                id = user_id.getString("id");
                                Picasso.with(PostDetails.this).load(user_id.getString("image")).into(prvileImage);
                                String video = data.getString("video");
                                if (data.getString("video").equals("")) {

//                                    videoPlayerWidget.setVisibility(View.GONE);
                                    video_layout.setVisibility(View.GONE);
                                    rvImages.setVisibility(View.VISIBLE);

                                } else {
                                    data.getString("video");
                                    rvImages.setVisibility(View.GONE);
                                    ivNoImage.setVisibility(View.GONE);
                                    HttpProxyCacheServer proxy = getProxy();
                                    String proxyUrl = proxy.getProxyUrl(data.getString("video"));
                                    exoPlayerView.setVisibility(View.VISIBLE);
//                                    videoPlayerWidget.startPlay(proxyUrl, MxVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
                                    try {
                                        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                                        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                                        exoPlayer = ExoPlayerFactory.newSimpleInstance(PostDetails.this, trackSelector);
                                        Uri videoURI = Uri.parse(data.getString("video"));
                                        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                                        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                                        MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);
                                        exoPlayerView.setPlayer(exoPlayer);
                                        exoPlayer.prepare(mediaSource);
                                        exoPlayer.setPlayWhenReady(false);
                                    } catch (Exception e) {
                                        Log.e("MainAcvtivity", " exoplayer error " + e.toString());
                                    }
                                }
                                JSONObject job = user_id.getJSONObject("job");
                                JSONObject name = job.getJSONObject("name");
                                if (Locale.getDefault().getDisplayLanguage().equals("ar")) {
                                    des.setText(name.getString("ar"));
                                } else {
                                    des.setText(name.getString("en"));
                                }
                                PostDetails.this.name.setText(user_id.getString("username"));
                                if (data.getString("info").equals("null")) {
                                    tvContent.setText("");
                                } else {
                                    tvContent.setText(data.getString("info"));
                                }
                                tvLikeCount.setText(data.getString("like_count"));
                                JSONArray images = data.getJSONArray("images");
                                for (int i = 0; i < images.length(); i++) {
                                    JSONObject jsonObject = images.getJSONObject(i);
                                    ImageModel gallay = new ImageModel();
                                    gallay.setImage(jsonObject.getString("image"));
                                    arrayList.add(gallay);
                                }
                                if (arrayList.size() > 0) {
                                    PhotoGallaryAdapter adapter = new PhotoGallaryAdapter(PostDetails.this, arrayList);
                                    rvImages.setAdapter(adapter);
                                    rvImages.setNestedScrollingEnabled(false);
                                    if (arrayList.size() == 1) {
                                        rvImages.setLayoutManager(new GridLayoutManager(PostDetails.this, 1));
                                    } else {
                                        rvImages.setLayoutManager(new GridLayoutManager(PostDetails.this, 2));
                                    }

                                    ivNoImage.setVisibility(View.GONE);
                                    adapter.setOnClickListener(new PhotoGallaryAdapter.OnItemClickListener() {
                                        @Override
                                        public void onclick(int position) {
                                            //  getActivity(). getSupportFragmentManager().beginTransaction().replace(R.id.container, new DetailseOnImageClick()).commit();
                                            Intent intent = new Intent(PostDetails.this, ImageListActivity.class);
                                            intent.putExtra("from", "2");
                                            startActivity(intent);

                                        }
                                    });
                                }
//                                else {
//                                    ivNoImage.setVisibility(View.VISIBLE);
//                                }


                                JSONArray comment = data.getJSONArray("comment");
                                for (int i = 0; i < comment.length(); i++) {
                                    JSONObject obj = comment.getJSONObject(i);
                                    CommentModel model = new CommentModel();
                                    JSONObject user_id1 = obj.getJSONObject("user_id");
                                    model.setName(user_id1.getString("username"));
                                    model.setImage(user_id1.getString("image"));
                                    model.setUserId(user_id1.getString("id"));
                                    model.setComment(obj.getString("comment"));
                                    String created_atComment = obj.getString("created_at");

                                    String[] sComment = created_atComment.split(" ");
                                    String[] splitComment = sComment[1].split(":");
                                    String time2 = splitComment[0] + ":" + splitComment[1];
                                    if (Integer.parseInt(splitComment[0]) > 12) {
                                        timeZone = getString(R.string.timeZoneNight);
                                    } else {
                                        timeZone = getString(R.string.timeZoneMorning);
                                    }
                                    final SimpleDateFormat sdfComment = new SimpleDateFormat("H:mm");
                                    SimpleDateFormat formatComment = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date dateComment = null;
                                    try {
                                        dateComment = format.parse(created_atComment);
                                        final Date dateObj = sdfComment.parse(sComment[1]);
                                        System.out.println(dateObj);
                                        System.out.println(new SimpleDateFormat("K:mm").format(dateObj));

                                        time2 = new SimpleDateFormat("K:mm").format(dateObj);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String dayComment = (String) DateFormat.format("dd", date); // 20
                                    String monthStringComment = (String) DateFormat.format("MMM", date); // Jun
                                    String monthNumberComment = (String) DateFormat.format("MM", date); // 06
                                    String yearComment = (String) DateFormat.format("yyyy", date); // 2013
                                    model.setCreated_at(dayComment + " " + monthStringComment + " " + time2);
                                    arrayComments.add(model);
                                }
                                rvComments.setAdapter(new ConmmentAdapter(PostDetails.this, arrayComments));
                                rvComments.setLayoutManager(new LinearLayoutManager(PostDetails.this, LinearLayoutManager.VERTICAL, false));
                                rvComments.setNestedScrollingEnabled(false);
                                listModel.setCreated_at(created_at);
                                listModel.setLng(lang);
                                listModel.setLat(lat);
                                listModel.setArrayComments(arrayComments);
                                listModel.setArrayImages(images);
                                listModel.setPostId(postId);
                                listModel.setVideoUrl(video);
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


    private HttpProxyCacheServer getProxy() {
        // should return single instance of HttpProxyCacheServer shared for whole app.

        return proxy == null ? (proxy = newProxy()) : proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }

    private void getPostDetailsAfterComment(String postId) {
        arrayComments.clear();
        if (arrayList.size() > 0) {
            arrayList.clear();
        }

        loading.showLoading();
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.get(Urls.getPostDetails + postId)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", "" + response);
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONObject data = response.getJSONObject("data");
                                String lat = data.getString("lat");
                                String lng = data.getString("lng");

                                String created_at = data.getString("created_at");
                                String[] s = created_at.split(" ");
                                String[] split = s[1].split(":");
                                String time1 = split[0] + ":" + split[1];
                                if (Integer.parseInt(split[0]) > 12) {
                                    timeZone = getString(R.string.timeZoneNight);
                                } else {
                                    timeZone = getString(R.string.timeZoneMorning);
                                }
                                final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = null;
                                try {
                                    date = format.parse(created_at);
                                    final Date dateObj = sdf.parse(s[1]);
                                    System.out.println(dateObj);
                                    System.out.println(new SimpleDateFormat("K:mm").format(dateObj));

                                    time = new SimpleDateFormat("K:mm").format(dateObj);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String day = (String) DateFormat.format("dd", date); // 20
                                String monthString = (String) DateFormat.format("MMM", date); // Jun
                                String monthNumber = (String) DateFormat.format("MM", date); // 06
                                String year = (String) DateFormat.format("yyyy", date); // 2013
                                tvDate.setText(day + " " + monthString + " " + time);
                                if (lat.equals("")) {
                                    tvIn.setVisibility(View.GONE);
                                    tvLocation.setVisibility(View.GONE);
                                } else {
                                    tvIn.setVisibility(View.VISIBLE);
                                    tvLocation.setVisibility(View.VISIBLE);
                                    getAddress(Double.parseDouble(lat), Double.parseDouble(lng));
                                    tvLocation.setText(city + " ," + country);
                                }
                                postID = data.getString("id");
                                String likes = data.getString("likes");
                                if (likes.equals("1")) {
                                    liLike.setImageResource(0);
                                    liLike.setImageResource(R.drawable.ic_like);
                                } else {
                                    liLike.setImageResource(0);
                                    liLike.setImageResource(R.drawable.h1);
                                }
                                JSONObject user_id = data.getJSONObject("user_id");
                                id = user_id.getString("id");
                                Picasso.with(PostDetails.this).load(user_id.getString("image")).into(prvileImage);

                                if (data.getString("video").equals("")) {
//                                    videoPlayerWidget.setVisibility(View.GONE);
                                    video_layout.setVisibility(View.GONE);
                                    rvImages.setVisibility(View.VISIBLE);
                                } else {
                                    rvImages.setVisibility(View.GONE);
//                                    videoPlayerWidget.setVisibility(View.VISIBLE);
//                                    videoPlayerWidget.startPlay(data.getString("video"), MxVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
                                }
                                JSONObject job = user_id.getJSONObject("job");
                                JSONObject name = job.getJSONObject("name");
                                if (Locale.getDefault().getDisplayLanguage().equals("ar")) {
                                    des.setText(name.getString("ar"));
                                } else {
                                    des.setText(name.getString("en"));
                                }
                                PostDetails.this.name.setText(user_id.getString("username"));
                                if (data.getString("info").equals("null")) {
                                    tvContent.setText("");
                                } else {
                                    tvContent.setText(data.getString("info"));
                                }
                                tvLikeCount.setText(data.getString("like_count"));
                                JSONArray images = data.getJSONArray("images");
                                for (int i = 0; i < images.length(); i++) {
                                    JSONObject jsonObject = images.getJSONObject(i);
                                    ImageModel gallay = new ImageModel();
                                    gallay.setImage(jsonObject.getString("image"));
                                    arrayList.add(gallay);
                                }
                                if (arrayList.size() > 0) {
                                    PhotoGallaryAdapter adapter = new PhotoGallaryAdapter(PostDetails.this, arrayList);
                                    rvImages.setAdapter(adapter);
                                    if (arrayList.size() == 1) {
                                        rvImages.setLayoutManager(new GridLayoutManager(PostDetails.this, 1));
                                    } else {
                                        rvImages.setLayoutManager(new GridLayoutManager(PostDetails.this, 2));
                                    }

                                    ivNoImage.setVisibility(View.GONE);
                                    adapter.setOnClickListener(new PhotoGallaryAdapter.OnItemClickListener() {
                                        @Override
                                        public void onclick(int position) {
                                            //  getActivity(). getSupportFragmentManager().beginTransaction().replace(R.id.container, new DetailseOnImageClick()).commit();
                                            Intent intent = new Intent(PostDetails.this, ImageListActivity.class);
                                            intent.putExtra("from", "2");
                                            startActivity(intent);

                                        }
                                    });
                                } else {
                                    ivNoImage.setVisibility(View.VISIBLE);
                                }
                                JSONArray comment = data.getJSONArray("comment");
                                for (int i = 0; i < comment.length(); i++) {
                                    JSONObject obj = comment.getJSONObject(i);
                                    CommentModel model = new CommentModel();
                                    JSONObject user_id1 = obj.getJSONObject("user_id");
                                    model.setName(user_id1.getString("username"));
                                    model.setImage(user_id1.getString("image"));
                                    model.setUserId(user_id1.getString("id"));
                                    model.setComment(obj.getString("comment"));
                                    String created_atComment = obj.getString("created_at");

                                    String[] sComment = created_atComment.split(" ");
                                    String[] splitComment = sComment[1].split(":");
                                    String time2 = splitComment[0] + ":" + splitComment[1];
                                    if (Integer.parseInt(splitComment[0]) > 12) {
                                        timeZone = getString(R.string.timeZoneNight);
                                    } else {
                                        timeZone = getString(R.string.timeZoneMorning);
                                    }
                                    final SimpleDateFormat sdfComment = new SimpleDateFormat("H:mm");
                                    SimpleDateFormat formatComment = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date dateComment = null;
                                    try {
                                        dateComment = format.parse(created_atComment);
                                        final Date dateObj = sdfComment.parse(sComment[1]);
                                        System.out.println(dateObj);
                                        System.out.println(new SimpleDateFormat("K:mm").format(dateObj));

                                        time2 = new SimpleDateFormat("K:mm").format(dateObj);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String dayComment = (String) DateFormat.format("dd", date); // 20
                                    String monthStringComment = (String) DateFormat.format("MMM", date); // Jun
                                    String monthNumberComment = (String) DateFormat.format("MM", date); // 06
                                    String yearComment = (String) DateFormat.format("yyyy", date); // 2013
                                    model.setCreated_at(dayComment + " " + monthStringComment + " " + time2);
                                    arrayComments.add(model);
                                }
                                ConmmentAdapter conmmentAdapter = new ConmmentAdapter(PostDetails.this, arrayComments);
                                conmmentAdapter.notifyDataSetChanged();
                                rvComments.setAdapter(conmmentAdapter);
                                rvComments.setLayoutManager(new LinearLayoutManager(PostDetails.this, LinearLayoutManager.VERTICAL, false));
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

    private void getAddress(double lat, double lon) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
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
}


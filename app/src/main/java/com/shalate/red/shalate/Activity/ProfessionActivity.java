package com.shalate.red.shalate.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.Adapter.MyPostGallaryAdapter;
import com.shalate.red.shalate.Adapter.VideoAccountAdapter;
import com.shalate.red.shalate.Fragment.MapFragment;
import com.shalate.red.shalate.Model.CommentModel;
import com.shalate.red.shalate.Model.ImageModel;
import com.shalate.red.shalate.Model.ListModel;
import com.shalate.red.shalate.Model.PhotoGallay;
import com.shalate.red.shalate.Model.VideoModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.GPSTracker;
import com.shalate.red.shalate.Utilities.PermissionsEnabled;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ProfessionActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView prvileImage, ivProfile, ivBack;
    TextView name, tvProfession, tvFollwoers, tvDescription, tvPosts, tvFollowing, tvNoData;
    LinearLayout liLocation, liChat, liCall;
    Button btnFollow, btnAdd;
    RecyclerView photo, rvComments, rvVideos;
    EditText etComment;
    private PrefrencesStorage storage;
    private String id;
    private ProgressLoading loading;
    private String proId;
    private ArrayList<ImageModel> imageModels;
    private ArrayList<CommentModel> arrayComments;
    private ArrayList<ListModel> listModels;
    private StaggeredGridLayoutManager mLayoutManager;
    private ArrayList<PhotoGallay> arrayList;
    private String phone;
    private PermissionsEnabled enabled;
    GPSTracker mGps;
    private String url;
    private String postID;
    private ArrayList<VideoModel> arrayVideos;
    private NestedScrollView nestedScroll;
    LinearLayout liFocus;
    private String image;
    private LinearLayout liFollowes, liFolloweing;
    private String lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profession);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Slidr.attach(this);
        enabled = new PermissionsEnabled(this);
        enabled.enablePermission(3, PermissionsEnabled.CALL_PHONE);
        enabled.enablePermission(1, PermissionsEnabled.LOCATION_REQUEST_CODE);
        mGps = new GPSTracker(this);
        bind();
        init();
        getProfileData();

    }

    private void init() {
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        storage = new PrefrencesStorage(this);
        loading = new ProgressLoading(this);
        arrayVideos = new ArrayList<>();
        listModels = new ArrayList<>();
        imageModels = new ArrayList<>();
        arrayComments = new ArrayList<>();
        arrayList = new ArrayList<>();
        Intent i = getIntent();
        proId = i.getExtras().getString("id");

        Log.e("professionId", "" + proId);
    }

    private void getProfileData() {
        loading.showLoading();
        if (!storage.isFirstTimeLogin()) {
            url = Urls.getProfile + "1&provider=" + proId;
        } else {
            url = Urls.getProfile + storage.getId() + "&provider=" + proId;
        }
        AndroidNetworking.get(url)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONObject data = response.getJSONObject("data");

                                JSONObject information = data.getJSONObject("information");
                                String isFollow1 = data.getString("follow");
                                if (isFollow1.equals("1")) {
                                    btnFollow.setText(getString(R.string.unFollow));
                                } else {
                                    btnFollow.setText(getString(R.string.follow));
                                }
                                id = information.getString("id");
                                name.setText(information.getString("username"));
                                phone = information.getString("phone");
                                image = information.getString("image");
                                Picasso.with(ProfessionActivity.this).load(information.getString("image")).into(prvileImage);
                                JSONObject job = information.getJSONObject("job");
                                JSONObject name = job.getJSONObject("name");
                                String followers = information.getString("followers");
                                String follow = information.getString("follow");
                                lat = information.getString("lat");
                                lng = information.getString("lng");
                                String posts_count = information.getString("posts_count");
                                tvFollowing.setText(follow);
                                tvFollwoers.setText(followers);
                                tvPosts.setText(posts_count);
                                if (Locale.getDefault().getDisplayLanguage().equals("ar")) {
                                    tvProfession.setText(name.getString(name.getString("ar")));
                                } else {
                                    tvProfession.setText(name.getString("en"));
                                }
                                tvDescription.setText(information.getString("information"));

                                JSONArray data1 = data.getJSONArray("post");
                                if (data1.length() > 0) {
                                    for (int x = 0; x < data1.length(); x++) {
                                        VideoModel model = new VideoModel();
                                        JSONObject jsonObject = data1.getJSONObject(x);
                                        JSONArray images = jsonObject.getJSONArray("images");
                                        postID = jsonObject.getString("id");

                                        String video = jsonObject.getString("video");
                                        if (!video.equals("")) {
                                            model.setVideoPath(video);
                                            model.setPostId(postID);
                                            arrayVideos.add(model);
                                        }
                                        for (int i = 0; i < images.length(); i++) {
                                            JSONObject obj = images.getJSONObject(i);
                                            PhotoGallay gallay = new PhotoGallay();
                                            gallay.setPhoto(obj.getString("image"));
                                            gallay.setPostID(postID);
                                            arrayList.add(gallay);
                                        }
                                    }
                                    if (arrayVideos.size() > 0) {
                                        VideoAccountAdapter videoAccountAdapter = new VideoAccountAdapter(ProfessionActivity.this, arrayVideos);
                                        rvVideos.setAdapter(videoAccountAdapter);
                                        rvVideos.setLayoutManager(new LinearLayoutManager(ProfessionActivity.this, LinearLayoutManager.VERTICAL, false));
                                        rvVideos.setNestedScrollingEnabled(false);
                                        videoAccountAdapter.notifyDataSetChanged();
//                                        videoAccountAdapter.setOnClickListener(new VideoAccountAdapter.OnItemClickListener() {
//                                            @Override
//                                            public void onclick(int position) {
//                                                Intent intent = new Intent(ProfessionActivity.this, PostDetails.class);
//                                                intent.putExtra("postId", arrayVideos.get(position).getPostId());
//                                                intent.putExtra("from", "post");
//                                                startActivity(intent);
//                                            }
//                                        });
                                    }
                                    if (arrayList.size() > 0) {
//                                        mLayoutManager = new GridLayoutManager(getActivity(), 2);
                                        photo.setLayoutManager(new GridLayoutManager(ProfessionActivity.this, 2));
                                        MyPostGallaryAdapter adapter = new MyPostGallaryAdapter(ProfessionActivity.this, arrayList);
                                        photo.setAdapter(adapter);
                                        photo.setNestedScrollingEnabled(false);
                                        adapter.notifyDataSetChanged();
                                        adapter.setOnClickListener(new MyPostGallaryAdapter.OnItemClickListener() {
                                            @Override
                                            public void onclick(int position) {
                                                Intent intent = new Intent(ProfessionActivity.this, PostDetails.class);
                                                intent.putExtra("postId", arrayList.get(position).getPostID());
                                                intent.putExtra("from", "post");
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        tvNoData.setVisibility(View.VISIBLE);
                                        tvNoData.setText(getString(R.string.no_result_found));
                                    }

                                }

                            }

                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loading.cancelLoading();
                    }
                });
    }

    private void bind() {
        prvileImage = findViewById(R.id.prvileImage);
        ivProfile = findViewById(R.id.ivProfile);
        name = findViewById(R.id.name);
        tvProfession = findViewById(R.id.tvProfession);
        tvDescription = findViewById(R.id.tvDescription);
        liLocation = findViewById(R.id.liLocation);
        liChat = findViewById(R.id.liChat);
        liCall = findViewById(R.id.liCall);
        btnFollow = findViewById(R.id.btnFollow);
        btnAdd = findViewById(R.id.btnAdd);
        photo = findViewById(R.id.photo);
        rvComments = findViewById(R.id.rvComments);
        etComment = findViewById(R.id.etComment);
        tvPosts = findViewById(R.id.tvPosts);
        tvFollwoers = findViewById(R.id.tvFollowers);
        tvFollowing = findViewById(R.id.tvFollowing);
        tvNoData = findViewById(R.id.tvNoData);
        ivBack = findViewById(R.id.ivBack);
        rvVideos = findViewById(R.id.rvVideos);
        nestedScroll = findViewById(R.id.nestedScroll);
        liFocus = findViewById(R.id.liFocus);
        liFollowes = findViewById(R.id.liFollowes);
        liFolloweing = findViewById(R.id.liFolloweing);
        liFocus.requestFocus();
        nestedScroll.scrollTo(0, 0);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!storage.isFirstTimeLogin()) {
                    Toast.makeText(ProfessionActivity.this, getString(R.string.loginRegisterFirst), Toast.LENGTH_SHORT).show();
                } else {
                    followUser();
                }

            }
        });

        nestedScroll.post(new Runnable() {
            @Override
            public void run() {
                nestedScroll.fling(0);
                nestedScroll.smoothScrollTo(0, 0);
            }
        });

        liChat.setOnClickListener(this);
        liCall.setOnClickListener(this);
        liLocation.setOnClickListener(this);

        liFollowes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfessionActivity.this, ShowFollowers.class);
                intent.putExtra("from", "1");
                intent.putExtra("fromAc", "profession");
                intent.putExtra("proID", proId);
                startActivity(intent);
            }
        });

        liFolloweing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfessionActivity.this, ShowFollowers.class);
                i.putExtra("from", "2");
                i.putExtra("fromAc", "profession");
                i.putExtra("proID", proId);
                startActivity(i);
            }
        });
        prvileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfessionActivity.this, ImageDetails.class);
                intent.putExtra("imageName", image);
                startActivity(intent);
            }
        });
    }

    private void followUser() {
        loading.showLoading();
        AndroidNetworking.post(Urls.follow + storage.getId())
                .addBodyParameter("follow_id", id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
//                                btnFollow.setText(getString(R.string.unFollow));
//                                if (btnFollow.getText().toString().equals(getString(R.string.unFollow))){
//                                    Toast.makeText(ProfessionActivity.this, getString(R.string.youUnFollowThisUser), Toast.LENGTH_SHORT).show();
//                                }else {
//                                    Toast.makeText(ProfessionActivity.this, getString(R.string.yoFollowThisProfession), Toast.LENGTH_SHORT).show();
//                                }
                                startActivity(getIntent());
                                finish();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.liChat:
                if (!storage.isFirstTimeLogin()) {
                    Toast.makeText(ProfessionActivity.this, getString(R.string.loginRegisterFirst), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("cusId", proId);
                    intent.putExtra("from", "2");
                    startActivity(intent);
                }
                break;

            case R.id.liCall:
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:" + phone));

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                startActivity(i);
                break;
            case R.id.liLocation:
                Intent intent = new Intent(ProfessionActivity.this, ProfessionLocation.class);
                intent.putExtra("lat", "" + lat);
                intent.putExtra("lon", "" + lng);
                intent.putExtra("image", "" + image);
                startActivity(intent);
                break;
        }
    }
}

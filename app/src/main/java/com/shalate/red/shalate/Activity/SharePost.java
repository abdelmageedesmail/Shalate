package com.shalate.red.shalate.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.Adapter.AddPostPhotoGalleryAdapter;
import com.shalate.red.shalate.Fragment.CameraFragment;
import com.shalate.red.shalate.Fragment.GalleryFragment;
import com.shalate.red.shalate.Fragment.VideoFragment;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.GPSTracker;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import hb.xvideoplayer.MxVideoPlayer;
//import hb.xvideoplayer.MxVideoPlayerWidget;

public class SharePost extends AppCompatActivity {

    FrameLayout video_layout, frame;
    TextView tvChargeOfList;
    ImageView ivImage, ivBack;
    PrefrencesStorage storage;
    ProgressLoading loading;
    TextView tvShare;
    EditText etDesc;
    RecyclerView rvImages;
    List<Bitmap> arrayList;
    LinearLayout liLocation;
    private int PLACE_PICKER_REQUEST = 1;
    private String latitude, longitude;
    TextView tvLocation;
    GPSTracker mGps;
    VideoView videoViewPlayer;

//    private MxVideoPlayerWidget videoPlayerWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_post);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Slidr.attach(this);
        bind();
        fillViews();
        init();
    }

    private void init() {
        Intent i = getIntent();
        String data = i.getExtras().getString("fromFragment");
        arrayList = new ArrayList<>();
        if (data.equals("video")) {
            frame.setVisibility(View.GONE);
            videoViewPlayer.setVideoPath(VideoFragment.selectedVideoPath);
            videoViewPlayer.seekTo(1);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoViewPlayer);
            videoViewPlayer.setMediaController(mediaController);

//            videoPlayerWidget.startPlay(VideoFragment.selectedVideoPath, MxVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
//            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(VideoFragment.selectedVideoPath, MediaStore.Images.Thumbnails.MINI_KIND);
//            videoPlayerWidget.mThumbImageView.setVisibility(View.VISIBLE);
//            videoPlayerWidget.mThumbImageView.setImageBitmap(thumb);

        } else if (data.equals("camera")) {
            frame.setVisibility(View.GONE);
            video_layout.setVisibility(View.GONE);
            ivImage.setVisibility(View.VISIBLE);
            if (CameraFragment.cameraFile.getAbsolutePath() != null) {
//                Bitmap myBitmap = BitmapFactory.decodeFile(CameraFragment.cameraFile.getAbsolutePath());
                ivImage.setImageBitmap(CameraFragment.thumbnail);
            }


        } else if (data.equals("gallery")) {
            rvImages.setVisibility(View.VISIBLE);
            video_layout.setVisibility(View.GONE);
            ivImage.setVisibility(View.GONE);
            for (int x = 0; x < GalleryFragment.arrayList.size(); x++) {
                Bitmap myBitmap = BitmapFactory.decodeFile(GalleryFragment.arrayList.get(x).getAbsolutePath());
                arrayList.add(myBitmap);
            }
            AddPostPhotoGalleryAdapter addPostPhotoGalleryAdapter = new AddPostPhotoGalleryAdapter(this, arrayList);
            rvImages.setAdapter(addPostPhotoGalleryAdapter);
            rvImages.setLayoutManager(new GridLayoutManager(this, 2));

            if (GalleryFragment.arrayList.size() > 4) {
                frame.setVisibility(View.VISIBLE);
                int i1 = GalleryFragment.arrayList.size() - 4;
                tvChargeOfList.setText("+" + i1);
            } else {
                frame.setVisibility(View.GONE);
            }
        }
        storage = new PrefrencesStorage(this);
        loading = new ProgressLoading(this);
    }

    private void fillViews() {

    }

    private void bind() {
//        videoPlayerWidget = findViewById(R.id.mpw_video_player);
        videoViewPlayer = findViewById(R.id.videoViewPlayer);
        video_layout = findViewById(R.id.video_layout);
        ivImage = findViewById(R.id.ivImage);
        etDesc = findViewById(R.id.etDesc);
        tvShare = findViewById(R.id.tvShare);
        tvLocation = findViewById(R.id.tvLocation);
        rvImages = findViewById(R.id.rvImages);
        frame = findViewById(R.id.frame);
        tvChargeOfList = findViewById(R.id.tvChargeOfList);
        ivBack = findViewById(R.id.ivBack);
        liLocation = findViewById(R.id.liLocation);
        mGps = new GPSTracker(this);
        liLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLngBounds latLngBounds = new LatLngBounds(new LatLng(mGps.getLatitude(), mGps.getLongitude()),
                        new LatLng(mGps.getLatitude(), mGps.getLongitude()));
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                builder.setLatLngBounds(latLngBounds);

                try {
                    startActivityForResult(builder.build(SharePost.this), PLACE_PICKER_REQUEST);
                } catch (Exception e) {
                    Log.e("error", e.getStackTrace().toString());
                }
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePost();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place.getName());
                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                tvLocation.setText(address);
            }
        }
    }

    private void sharePost() {
        Log.e("id", storage.getId());
        loading.showLoading();
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        ANRequest.MultiPartBuilder upload = AndroidNetworking.upload(Urls.sharePost + storage.getId());
        upload.addMultipartParameter("title", "title");
        if (!etDesc.getText().toString().isEmpty()) {
            upload.addMultipartParameter("info", etDesc.getText().toString());
        }
        if (latitude != null) {
            upload.addMultipartParameter("lat", latitude)
                    .addMultipartParameter("lng", longitude);
        }
        if (CameraFragment.thumbnail != null) {
            upload.addMultipartFile("images[0]", CameraFragment.cameraFile);
            Log.e("cameraFile", "" + CameraFragment.cameraFile);
        }
        if (GalleryFragment.arrayList != null) {
            if (GalleryFragment.arrayList.size() > 0) {
                for (int i = 0; i < GalleryFragment.arrayList.size(); i++) {
                    upload.addMultipartFile("images[" + i + "]", GalleryFragment.arrayList.get(i));
                    Log.e("file", "images[" + i + "]," + GalleryFragment.arrayList.get(i));
                }
            }
        }

        if (VideoFragment.file != null) {
            upload.addMultipartFile("video", VideoFragment.file);
        }
        upload.setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.cancelLoading();
                        Log.e("response", "" + response);
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                Toast.makeText(SharePost.this, getString(R.string.postAddedSuccessfully), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SharePost.this, HomePage.class);

                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loading.cancelLoading();
                        Log.e("error", "" + anError.getMessage());
                        Toast.makeText(SharePost.this, getString(R.string.errorTryAgain), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

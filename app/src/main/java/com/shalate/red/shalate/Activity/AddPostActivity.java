package com.shalate.red.shalate.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.Fragment.CameraFragment;
import com.shalate.red.shalate.Fragment.GalleryFragment;
import com.shalate.red.shalate.Fragment.VideoFragment;
import com.shalate.red.shalate.R;
import com.zhihu.matisse.Matisse;

import java.util.ArrayList;
import java.util.List;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener {

    FrameLayout frContainer;
    TextView gallery, tvVideo, tvCamera, tvNext;
    List<Uri> mSelected;
    Uri contentURI;
    Bitmap bm;
    public static Intent dataInntent;
    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Slidr.attach(this);
        bind();
        replaceFragments(new CameraFragment());
    }

    private void bind() {
        frContainer = findViewById(R.id.frContainer);
        gallery = findViewById(R.id.gallery);
        tvVideo = findViewById(R.id.tvVideo);
        tvCamera = findViewById(R.id.tvCamera);
        tvNext = findViewById(R.id.tvNext);
        gallery.setOnClickListener(this);
        tvVideo.setOnClickListener(this);
        tvCamera.setOnClickListener(this);
        tvNext.setOnClickListener(this);
        mSelected = new ArrayList<>();
    }

    public void replaceFragments(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frContainer, fragment).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            mSelected = Matisse.obtainResult(data);
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frContainer);
            fragment.onActivityResult(requestCode, resultCode, data);
            code = resultCode;
            Log.e("dataData", "" + code);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gallery:
                if (CameraFragment.bitmap != null) {
                    CameraFragment.bitmap = null;
                } else if (VideoFragment.file != null) {
                    VideoFragment.file = null;
                }
                replaceFragments(new GalleryFragment());
                break;
            case R.id.tvVideo:
                if (CameraFragment.bitmap != null) {
                    CameraFragment.bitmap = null;
                } else if (GalleryFragment.arrayList != null) {
                    GalleryFragment.arrayList.clear();
                }
                replaceFragments(new VideoFragment());
                break;
            case R.id.tvCamera:
                if (GalleryFragment.arrayList != null) {
                    GalleryFragment.arrayList.clear();
                } else if (VideoFragment.file != null) {
                    VideoFragment.file = null;
                }
                replaceFragments(new CameraFragment());
                break;
            case R.id.tvNext:
                if ((CameraFragment.cameraFile.getAbsolutePath() != null) || (VideoFragment.file != null) || (GalleryFragment.arrayList != null)) {
                    Intent intent = new Intent(AddPostActivity.this, SharePost.class);
                    intent.putExtra("data", "" + code);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, getString(R.string.addData), Toast.LENGTH_SHORT).show();
                }

        }
    }
}

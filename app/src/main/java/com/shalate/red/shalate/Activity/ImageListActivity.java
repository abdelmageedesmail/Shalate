package com.shalate.red.shalate.Activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.r0adkll.slidr.Slidr;
import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;
import com.shalate.red.shalate.Adapter.ImagePostAdapter;
import com.shalate.red.shalate.Adapter.RecycleListAdapter;
import com.shalate.red.shalate.Model.ImageModel;
import com.shalate.red.shalate.R;

public class ImageListActivity extends AppCompatActivity {

    ImageView ivBack;
    RecyclerView rvImages;

    IndefinitePagerIndicator recyclerview_pager_indicator;
    private ImageModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Slidr.attach(this);
        setContentView(R.layout.activity_image_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        rvImages = findViewById(R.id.rvImages);
        recyclerview_pager_indicator = findViewById(R.id.recyclerview_pager_indicator);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        model = new Gson().fromJson(getIntent().getStringExtra("chaletModel"), new TypeToken<ImageModel>() {
        }.getType());
        if (getIntent().getExtras().getString("from").equals("1")){
            rvImages.setAdapter(new ImagePostAdapter(this, RecycleListAdapter.arrayList));
            rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerview_pager_indicator.attachToRecyclerView(rvImages);
        }else {
            rvImages.setAdapter(new ImagePostAdapter(this, PostDetails.arrayList));
            rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerview_pager_indicator.attachToRecyclerView(rvImages);
        }

    }
}

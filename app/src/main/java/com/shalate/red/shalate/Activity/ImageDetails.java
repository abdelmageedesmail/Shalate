package com.shalate.red.shalate.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.R;
import com.squareup.picasso.Picasso;

public class ImageDetails extends AppCompatActivity {

    ImageView ivBack, ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Slidr.attach(this);
        ivBack = findViewById(R.id.ivBack);
        ivImage = findViewById(R.id.ivImage);
        Intent i = getIntent();
        Picasso.with(this).load(i.getExtras().getString("imageName")).into(ivImage);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}

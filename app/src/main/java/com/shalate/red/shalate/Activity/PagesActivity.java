package com.shalate.red.shalate.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class PagesActivity extends AppCompatActivity {
    TextView tvNoData, tvContent, tvTitle;
    private String title;
    ProgressLoading loading;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Slidr.attach(this);
        bind();

        getIntentData();
        init();
        getPages();
    }

    private void init() {
        loading = new ProgressLoading(this);
        if (title.equals("about")) {
            tvTitle.setText(getString(R.string.aboutUS));
        } else {
            tvTitle.setText(getString(R.string.terms));
        }
    }

    private void getIntentData() {
        Intent i = getIntent();
        title = i.getExtras().getString("title");
    }

    private void bind() {
        tvTitle = findViewById(R.id.tvTitle);
        tvContent = findViewById(R.id.tvContent);
        tvNoData = findViewById(R.id.tvNoData);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getPages() {
        String url = Urls.pages + title;
        loading.showLoading();
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.get(url)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.cancelLoading();
                        try {
                            int status = response.getInt("status");
                            if (status == 1) {
                                JSONArray data = response.getJSONArray("data");
                                JSONObject jsonObject = data.getJSONObject(0);
                                if (Locale.getDefault().getDisplayLanguage().equals("ar")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        tvContent.setText(Html.fromHtml(jsonObject.getString("ar"), Html.FROM_HTML_MODE_COMPACT));
                                    } else {
                                        tvContent.setText(Html.fromHtml(jsonObject.getString("ar")));
                                    }
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        tvContent.setText(Html.fromHtml(jsonObject.getString("en"), Html.FROM_HTML_MODE_COMPACT));
                                    } else {
                                        tvContent.setText(Html.fromHtml(jsonObject.getString("en")));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("ERROR", "" + anError.getMessage());
                    }
                });
    }
}

package com.shalate.red.shalate.Activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.Adapter.MessageToFollowerAdapter;
import com.shalate.red.shalate.Model.FollowModel;
import com.shalate.red.shalate.Model.ListModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class SendMessageToFollowersActivity extends AppCompatActivity {

    PrefrencesStorage storage;
    ProgressLoading loading;
    private String lang;
    ArrayList<FollowModel> arrayList;
    RecyclerView rvFollowers;
    ImageView ivBack;
    private String type;
    LinearLayout liWarning;
    TextView tvNoData;
    public static AppCompatActivity activity;
    private ListModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_to_followers);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Slidr.attach(this);
        activity = this;
        bind();
        init();
        model = new Gson().fromJson(getIntent().getStringExtra("postModel"), new TypeToken<ListModel>() {
        }.getType());

        getFollowers();
    }

    private void bind() {
        rvFollowers = findViewById(R.id.rvFollowers);
        ivBack = findViewById(R.id.ivBack);
        liWarning = findViewById(R.id.liWarning);
        tvNoData = findViewById(R.id.tvNoData);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {
        storage = new PrefrencesStorage(this);
        loading = new ProgressLoading(this);
        arrayList = new ArrayList<>();
    }

    private void getFollowers() {
        loading.showLoading();
        if (Locale.getDefault().getDisplayLanguage().equals("ar")) {
            lang = "ar";
        } else {
            lang = "en";
        }
        String url = Urls.getFollowers + storage.getId() + "?lang=" + lang;
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
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
                                JSONArray followes = data.getJSONArray("followes");
                                for (int i = 0; i < followes.length(); i++) {
                                    JSONObject jsonObject = followes.getJSONObject(i);
                                    FollowModel model = new FollowModel();
                                    model.setUserName(jsonObject.getString("username"));
                                    model.setId(jsonObject.getString("id"));
                                    model.setImage(jsonObject.getString("image"));
                                    model.setType(type);
                                    model.setCheck_folow(jsonObject.getString("check_folow"));
                                    arrayList.add(model);
                                }
                                if (arrayList.size() > 0) {
                                    MessageToFollowerAdapter followerAdapter = new MessageToFollowerAdapter(SendMessageToFollowersActivity.this, arrayList, loading, storage, liWarning, model);
                                    rvFollowers.setAdapter(followerAdapter);
                                    rvFollowers.setLayoutManager(new LinearLayoutManager(SendMessageToFollowersActivity.this, LinearLayoutManager.VERTICAL, false));
                                    followerAdapter.notifyDataSetChanged();
                                } else {
                                    liWarning.setVisibility(View.VISIBLE);
                                    tvNoData.setText(getString(R.string.noData));
                                }
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
}

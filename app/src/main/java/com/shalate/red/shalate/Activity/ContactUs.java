package com.shalate.red.shalate.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ContactUs extends AppCompatActivity {

    TextView tvEmail, tvPhone;
    ImageView ivYoutube, ivSnap, ivInsta, ivTwitter, ivBack;
    EditText etName, etEmail, etMessage;
    Button btnSend;
    private String url;
    private JSONObject social;
    private String twitter, snap, inst, youtube;
    ProgressLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Slidr.attach(this);
        bind();
        getContactData();
    }

    private void bind() {
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        ivYoutube = findViewById(R.id.ivYoutube);
        ivSnap = findViewById(R.id.ivSnap);
        ivInsta = findViewById(R.id.ivInsta);
        ivTwitter = findViewById(R.id.ivTwitter);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        ivBack = findViewById(R.id.ivBack);
        loading = new ProgressLoading(this);

        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + tvPhone.getText().toString()));

                if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                view.getContext().startActivity(intent);
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etName.getText().toString().isEmpty()) {
                    etName.setError(getString(R.string.empty));
                } else if (etEmail.getText().toString().isEmpty()) {
                    etEmail.setError(getString(R.string.empty));
                } else if (etMessage.getText().toString().isEmpty()) {
                    etMessage.setError(getString(R.string.empty));
                } else {
                    contactUs();
                }

            }
        });
    }

    private void getContactData() {
        loading.showLoading();
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.get(Urls.pages + "contact")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONArray data = response.getJSONArray("data");
                                JSONObject jsonObject = data.getJSONObject(0);
                                JSONObject value = jsonObject.getJSONObject("value");
                                tvEmail.setText(value.getString("email"));
                                tvPhone.setText(value.getString("phone"));
                                social = value.getJSONObject("social");
                                twitter = social.getString("twitter");
                                inst = social.getString("instgram");
                                snap = social.getString("snapchat");
                                youtube = social.getString("youtube");
                                ivTwitter.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (twitter.startsWith("http") || twitter.startsWith("https")) {
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            url = twitter;
                                            i.setData(Uri.parse(url));
                                            startActivity(i);
                                        } else {
                                            Toast.makeText(ContactUs.this, getString(R.string.errorWebSite), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                ivInsta.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (inst.startsWith("http") || inst.startsWith("https")) {
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            url = twitter;
                                            i.setData(Uri.parse(url));
                                            startActivity(i);
                                        } else {
                                            Toast.makeText(ContactUs.this, getString(R.string.errorWebSite), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                                ivSnap.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (snap.startsWith("http") || snap.startsWith("https")) {
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            url = twitter;
                                            i.setData(Uri.parse(url));
                                            startActivity(i);
                                        } else {
                                            Toast.makeText(ContactUs.this, getString(R.string.errorWebSite), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                                ivYoutube.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (youtube.startsWith("http") || youtube.startsWith("https")) {
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            url = twitter;
                                            i.setData(Uri.parse(url));
                                            startActivity(i);
                                        } else {
                                            Toast.makeText(ContactUs.this, getString(R.string.errorWebSite), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

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

    private void contactUs() {
        loading.showLoading();
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(Urls.pages + "contact")
                .addBodyParameter("username", etName.getText().toString())
                .addBodyParameter("email", etEmail.getText().toString())
                .addBodyParameter("message", etMessage.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                Toast.makeText(ContactUs.this, getString(R.string.messageSend), Toast.LENGTH_SHORT).show();
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
}

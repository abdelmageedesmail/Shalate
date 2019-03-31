package com.shalate.red.shalate.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;
import com.shalate.red.shalate.notification.FCMRegistrationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class UpdatePassword extends AppCompatActivity {

    EditText etPassword, etNewPassword;
    Button btnPassword;
    PrefrencesStorage storage;
    ProgressLoading loading;
    private String macAddress;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slidr.attach(this);
        setContentView(R.layout.activity_update_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        etPassword = findViewById(R.id.etPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnPassword = findViewById(R.id.btnPassword);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        storage = new PrefrencesStorage(this);
        loading = new ProgressLoading(this);
        getMacAddress();
        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etPassword.getText().toString().isEmpty()){
                    etPassword.setError(getString(R.string.empty));
                }else if (etNewPassword.getText().toString().isEmpty()){
                    etNewPassword.setError(getString(R.string.empty));
                }else {
                    updatePassword();
                }

            }
        });
    }

    private void updatePassword() {
        loading.showLoading();
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(Urls.updatePass)
                .addBodyParameter("user_id", storage.getId())
                .addBodyParameter("old_password", etPassword.getText().toString())
                .addBodyParameter("new_password", etNewPassword.getText().toString())
                .addBodyParameter("device_type", "android")
                .addBodyParameter("device_token", FCMRegistrationService.token)
                .addBodyParameter("device_unique_address", macAddress)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.cancelLoading();
                        Log.e("response", "" + response);
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                Toast.makeText(UpdatePassword.this, getString(R.string.passwordUpdatedSuccessfully), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UpdatePassword.this, HomePage.class));
                                finish();
                            } else {
                                Toast.makeText(UpdatePassword.this, getString(R.string.errorPassword), Toast.LENGTH_SHORT).show();
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


    private void getMacAddress() {

        try {
            // get all the interfaces
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            //find network interface wlan0
            for (NetworkInterface networkInterface : all) {
                if (!networkInterface.getName().equalsIgnoreCase("wlan0")) continue;
                //get the hardware address (MAC) of the interface
                byte[] macBytes = networkInterface.getHardwareAddress();
                if (macBytes == null) {
//                    return "";
                }


                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //gets the last byte of b
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                macAddress = res1.toString();
                Log.e("macAddress", macAddress);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

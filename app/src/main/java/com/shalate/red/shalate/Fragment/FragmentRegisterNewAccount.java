package com.shalate.red.shalate.Fragment;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.shalate.red.shalate.Activity.ForgetPassword;
import com.shalate.red.shalate.Activity.HomeActivity;
import com.shalate.red.shalate.Activity.HomePage;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;
import com.shalate.red.shalate.notification.FCMRegistrationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRegisterNewAccount extends AppCompatActivity {

    TextView registernewAccount, passwordforget;
    EditText etEmail, etPass;
    View view;
    private String macAddress;
    private ProgressLoading loading;
    Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fragment_register_new_account);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setVar();
        init();
        setClick();
        getMacAddress();
    }

    private void init() {
        loading = new ProgressLoading(this);
    }

    public void setVar() {
        registernewAccount = findViewById(R.id.registernewAccount);
        passwordforget = findViewById(R.id.passwordforget);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);
    }

    public void setClick() {
        registernewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top);
                fragmentTransaction.replace(R.id.container, new RegistrationType());
                fragmentTransaction.commit();
            }
        });

        passwordforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FragmentRegisterNewAccount.this, ForgetPassword.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etEmail.getText().toString().isEmpty() && etEmail.getText().toString().equals("")) {
                    etEmail.setError(getString(R.string.empty));
                } else if (etPass.getText().toString().isEmpty() && etPass.getText().toString().equals("")) {
                    etPass.setError(getString(R.string.empty));
                } else {
                    login();
                }
            }
        });
    }

    private void login() {
        loading.showLoading();
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(Urls.login)
                .addBodyParameter("phone", etEmail.getText().toString())
                .addBodyParameter("password", etPass.getText().toString())
                .addBodyParameter("device_type", "android")
                .addBodyParameter("device_token", FCMRegistrationService.token)
                .addBodyParameter("device_unique_address", macAddress)
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
                                PrefrencesStorage storage = new PrefrencesStorage(FragmentRegisterNewAccount.this);
                                storage.storeId(jsonObject.getString("id"));
                                storage.storeKey("name", jsonObject.getString("username"));
                                storage.storeKey("email", jsonObject.getString("email"));
                                storage.storeKey("phone", jsonObject.getString("phone"));
                                storage.storeKey("account_type", jsonObject.getString("account_type"));
                                storage.storeKey("oldLat", jsonObject.getString("lat"));
                                storage.storeKey("oldLon", jsonObject.getString("lng"));
                                storage.storeKey("image", jsonObject.getString("image"));
                                if (jsonObject.getString("account_type").equals("provider")) {
                                    String jobId = jsonObject.getJSONObject("job").getString("id");
                                    storage.storeKey("jobId", jobId);
                                }
                                storage.setFirstTimeLogin(true);

                                startActivity(new Intent(FragmentRegisterNewAccount.this, HomeActivity.class));
                            } else {
                                JSONArray errors = response.getJSONArray("errors");
                                for (int i = 0; i < errors.length(); i++) {
                                    JSONObject jsonObject = errors.getJSONObject(i);
                                    String key = jsonObject.getString("key");
                                    if (key.equals("phone")) {
                                        Toast.makeText(FragmentRegisterNewAccount.this, getString(R.string.errorPhone), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(FragmentRegisterNewAccount.this, getString(R.string.errorPassword), Toast.LENGTH_SHORT).show();
                                    }
                                }
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

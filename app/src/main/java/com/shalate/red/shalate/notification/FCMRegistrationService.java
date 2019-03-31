package com.shalate.red.shalate.notification;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;


/**
 * Created by abdelmageed on 30/03/17.
 */

public class FCMRegistrationService extends IntentService {

    Context context;
    SharedPreferences sh;
    String id, userType;

    public static String success, token;
    private String api_token;
    private String user_id, macAddress;
    private String userToken;
    private PrefrencesStorage localeShared;

    public FCMRegistrationService() {
        super("FCM");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        localeShared = new PrefrencesStorage(this);
        user_id = localeShared.getId();
        userToken = localeShared.getToken();
        token = FirebaseInstanceId.getInstance().getToken();
        Log.e("token", "" + token);
        getMacAddress();
        refreshToken();
    }

    private void refreshToken() {
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        ANRequest.PostRequestBuilder post = AndroidNetworking.post(Urls.refreshToken);
        post.addBodyParameter("user_id", localeShared.getId())
                .addBodyParameter("device_type", "android")
                .addBodyParameter("device_token", token)
                .addBodyParameter("device_unique_address", macAddress);
        post.setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        localeShared.storeKey("oldToken", token);
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

package com.shalate.red.shalate.notification;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by abdelmageed on 30/03/17.
 */

public class FCMTokenRefreshListenerService extends FirebaseInstanceIdService {
    public static boolean refreshed;
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, FCMRegistrationService.class);
        refreshed=true;
        startService(intent);
    }
}

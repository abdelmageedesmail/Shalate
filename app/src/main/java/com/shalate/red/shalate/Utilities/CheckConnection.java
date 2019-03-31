package com.shalate.red.shalate.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckConnection {

    public static boolean isOnline(Context context) {

        NetworkInfo connectivity = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return connectivity != null && connectivity.isConnectedOrConnecting() ? true : false;

    }
}

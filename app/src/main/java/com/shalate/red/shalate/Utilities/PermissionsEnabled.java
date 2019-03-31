package com.shalate.red.shalate.Utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;

public class PermissionsEnabled {
    Context context;
    public static final int LOCATION_REQUEST_CODE = 1;
    public static final int READ_AND_WRITE_EXTERNAL_REQUEST_CODE = 2;
    public static final int CALL_PHONE = 3;
    public static final int CAMERA_USAGE = 4;
    public static final int RECORD_AUDIO = 5;
    String per[];

    public PermissionsEnabled(Context context) {
        this.context = context;
    }

    public void enablePermission(int value, int permissionCode) {
        switch (value) {
            case 1:
                per = new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                permissionCode = LOCATION_REQUEST_CODE;
                break;
            case 2:
                per = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                permissionCode = READ_AND_WRITE_EXTERNAL_REQUEST_CODE;
                break;
            case 3:
                per = new String[]{Manifest.permission.CALL_PHONE};
                permissionCode = CALL_PHONE;
                break;
            case 4:
                per = new String[]{Manifest.permission.CAMERA};
                permissionCode = CAMERA_USAGE;
                break;
            case 5:
                per = new String[]{Manifest.permission.RECORD_AUDIO};
                permissionCode = RECORD_AUDIO;
                break;
        }
        ActivityCompat.requestPermissions((Activity) context, per, permissionCode);
    }
}

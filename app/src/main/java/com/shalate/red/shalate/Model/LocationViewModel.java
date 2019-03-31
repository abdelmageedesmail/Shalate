package com.shalate.red.shalate.Model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.shalate.red.shalate.Utilities.GPSTracker;

public class LocationViewModel extends ViewModel {
    MutableLiveData<Boolean> isLocationEnabled;
    GPSTracker mGps;

    public MutableLiveData<Boolean> isLocationEnabled(Context context) {
//        mGps = new GPSTracker(context);
        isLocationEnabled = new MutableLiveData<>();
//        if (mGps.canGetLocation()) {
//            isLocationEnabled.setValue(true);
//        } else {
//            isLocationEnabled.setValue(false);
//        }
        return isLocationEnabled;
    }
}

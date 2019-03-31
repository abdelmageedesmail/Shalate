package com.shalate.red.shalate.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.shalate.red.shalate.Model.TrackModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.GPSTracker;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONObject;

public class UpdateLocationService extends Service {

    GPSTracker mGps;
    private String lat, lon;
    private LatLng latLngA;
    PrefrencesStorage storage;
    private double distanceDifffirence;
    private double distance;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGps = new GPSTracker(getApplicationContext());
        storage = new PrefrencesStorage(getApplicationContext());
        getValue();

    }


    private void getValue() {
//        chatKey = from + "_" + to;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("track");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                TrackModel value = dataSnapshot.getValue(TrackModel.class);
                lat = value.getLat();
                lon = value.getLon();

                distanceDifffirence = getDistanceDifffirence();
                if (distanceDifffirence < 5.0) {
                    pushNotification();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                TrackModel value = dataSnapshot.getValue(TrackModel.class);
                lat = value.getLat();
                lon = value.getLon();
                distanceDifffirence = getDistanceDifffirence();
                if (distanceDifffirence < 5.0) {
                    pushNotification();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("error", "Failed to read value.", error.toException());
            }
        });
    }

    private void pushNotification() {
        Log.e("uId", "" + storage.getId());
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(Urls.sendNotification)
                .addBodyParameter("title", getString(R.string.notification))
                .addBodyParameter("message", getString(R.string.professionNearByYou))
                .addBodyParameter("user_id", storage.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", "" + response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error", "" + anError.getMessage());
                    }
                });
    }

    private double getDistanceDifffirence() {
        if (lat != null) {
            latLngA = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            Location locationA = new Location("point A");
            locationA.setLatitude(latLngA.latitude);
            locationA.setLongitude(latLngA.longitude);
            LatLng latLngB = new LatLng(mGps.getLatitude(), mGps.getLongitude());
            Location locationB = new Location("point B");
            locationB.setLatitude(latLngB.latitude);
            locationB.setLongitude(latLngB.longitude);
            distance = locationA.distanceTo(locationB);
        }

        return distance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}

package com.shalate.red.shalate.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shalate.red.shalate.Fragment.ChatFragment;
import com.shalate.red.shalate.Fragment.FilterationF;
import com.shalate.red.shalate.Fragment.FragmentFavourits;
import com.shalate.red.shalate.Fragment.FragmentNotiFication;
import com.shalate.red.shalate.Fragment.GudidFragment;
import com.shalate.red.shalate.Fragment.MyAccount;
import com.shalate.red.shalate.Model.LocationViewModel;
import com.shalate.red.shalate.Model.TrackModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PermissionsEnabled;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.service.UpdateLocationService;

public class HomePage extends AppCompatActivity {

    TextView lin1txt, lin2txt, lin3txt, lin4txt, lin5txt, title;
    LinearLayout lin1, lin2, lin3, lin4, lin5, bottom;
    ImageView notification, icMenu;
    RelativeLayout frMore;
    FrameLayout frAddPost;
    private LocationViewModel locationViewModel;
    private double latitude, longituide;
    private PrefrencesStorage storage;
    private LatLng latLngA;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        PermissionsEnabled enabled = new PermissionsEnabled(this);
//        enabled.enablePermission(4, PermissionsEnabled.CAMERA_USAGE);
//        enabled.enablePermission(1, PermissionsEnabled.LOCATION_REQUEST_CODE);
//        enabled.enablePermission(2, PermissionsEnabled.READ_AND_WRITE_EXTERNAL_REQUEST_CODE);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FilterationF()).commit();

        lin1txt = (TextView) findViewById(R.id.lin1txt);
        lin2txt = (TextView) findViewById(R.id.lin2txt);
        lin3txt = (TextView) findViewById(R.id.lin3txt);
        lin4txt = (TextView) findViewById(R.id.lin4txt);
        lin5txt = (TextView) findViewById(R.id.lin5txt);
        title = (TextView) findViewById(R.id.title);
        icMenu = findViewById(R.id.icMenu);
        notification = (ImageView) findViewById(R.id.notification);
        bottom = findViewById(R.id.bottom);


        lin1 = (LinearLayout) findViewById(R.id.lin1);
        lin2 = (LinearLayout) findViewById(R.id.lin2);
        lin3 = (LinearLayout) findViewById(R.id.lin3);
        lin4 = (LinearLayout) findViewById(R.id.lin4);
        lin5 = (LinearLayout) findViewById(R.id.lin5);
        frMore = findViewById(R.id.frMore);
        frAddPost = findViewById(R.id.frAddPost);
        storage = new PrefrencesStorage(this);
        image = storage.getKey("image");
        if (storage.getKey("account_type").equals("provider")) {
            frAddPost.setVisibility(View.VISIBLE);
            updateLocation();
        } else {
            startService(new Intent(this, UpdateLocationService.class));
            frAddPost.setVisibility(View.GONE);
            bottom.setWeightSum(4);
        }

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.CAMERA
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        lin1txt.setTextColor(getResources().getColor(R.color.cologreen));
        lin2txt.setTextColor(getResources().getColor(R.color.colorAccent));
        lin3txt.setTextColor(getResources().getColor(R.color.colorAccent));
        lin4txt.setTextColor(getResources().getColor(R.color.colorAccent));
        lin5txt.setTextColor(getResources().getColor(R.color.colorAccent));

        Drawable lin1draw = getResources().getDrawable(R.drawable.p0);
        Drawable lin2dra = getResources().getDrawable(R.drawable.p1);
        Drawable lin3dra = getResources().getDrawable(R.drawable.p2);
        Drawable lin4dra = getResources().getDrawable(R.drawable.p3);
        Drawable lin5dra = getResources().getDrawable(R.drawable.p4);

        lin1txt.setCompoundDrawablesWithIntrinsicBounds(null, lin1draw, null, null);
        lin2txt.setCompoundDrawablesWithIntrinsicBounds(null, lin2dra, null, null);
        lin3txt.setCompoundDrawablesWithIntrinsicBounds(null, lin3dra, null, null);
        lin4txt.setCompoundDrawablesWithIntrinsicBounds(null, lin4dra, null, null);
        lin5txt.setCompoundDrawablesWithIntrinsicBounds(null, lin5dra, null, null);
        setUpFrgemnt();
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("from").equals("notification")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new ChatFragment()).commit();
                lin1txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin2txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin3txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin4txt.setTextColor(getResources().getColor(R.color.cologreen));
                lin5txt.setTextColor(getResources().getColor(R.color.colorAccent));

                lin1draw = getResources().getDrawable(R.drawable.homeicon);
                lin2dra = getResources().getDrawable(R.drawable.p1);
                lin3dra = getResources().getDrawable(R.drawable.p2);
                lin4dra = getResources().getDrawable(R.drawable.chateactive);
                lin5dra = getResources().getDrawable(R.drawable.p4);

                lin1txt.setCompoundDrawablesWithIntrinsicBounds(null, lin1draw, null, null);
                lin2txt.setCompoundDrawablesWithIntrinsicBounds(null, lin2dra, null, null);
                lin3txt.setCompoundDrawablesWithIntrinsicBounds(null, lin3dra, null, null);
                lin4txt.setCompoundDrawablesWithIntrinsicBounds(null, lin4dra, null, null);
                lin5txt.setCompoundDrawablesWithIntrinsicBounds(null, lin5dra, null, null);
            }
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateLocation() {
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                latitude = location.getLatitude();
                longituide = location.getLongitude();
                storage.storeKey("oldLat", "" + latitude);
                storage.storeKey("oldLon", "" + longituide);
                locationManager.removeUpdates(this);
                inint();
                startTrack();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    private void inint() {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

    }

    private void startTrack() {
//        if (getDistanceDifffirence() > 5.0) {
        String chatKey = storage.getId();
        DatabaseReference mRef = database.getReference("ProfessionTrack").child(storage.getKey("jobId")).child(chatKey);
        TrackModel model = new TrackModel();
        model.setFrom(chatKey);
        model.setLat("" + latitude);
        model.setLon("" + longituide);
        model.setUserProfile("" + image);
        model.setStatus(1);
        mRef.setValue(model);
//        }
    }

    private double getDistanceDifffirence() {
        storage = new PrefrencesStorage(this);
        String oldLat = storage.getKey("oldLat");
        String oldLon = storage.getKey("oldLon");
        if (!oldLat.equals("null")) {
            latLngA = new LatLng(Double.parseDouble(oldLat), Double.parseDouble(oldLon));
        }
        LatLng latLngB = new LatLng(latitude, longituide);
        Location locationA = new Location("point A");
        locationA.setLatitude(latLngA.latitude);
        locationA.setLongitude(latLngA.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(latLngB.latitude);
        locationB.setLongitude(latLngB.longitude);
        double distance = locationA.distanceTo(locationB);
        return distance;
    }

    public void setUpFrgemnt() {
        lin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                title.setText(getResources().getString(R.string.home));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new FilterationF()).commit();
                lin1txt.setTextColor(getResources().getColor(R.color.cologreen));
                lin2txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin3txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin4txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin5txt.setTextColor(getResources().getColor(R.color.colorAccent));

                Drawable lin1draw = getResources().getDrawable(R.drawable.p0);
                Drawable lin2dra = getResources().getDrawable(R.drawable.p1);
                Drawable lin3dra = getResources().getDrawable(R.drawable.p2);
                Drawable lin4dra = getResources().getDrawable(R.drawable.p3);
                Drawable lin5dra = getResources().getDrawable(R.drawable.p4);

                lin1txt.setCompoundDrawablesWithIntrinsicBounds(null, lin1draw, null, null);
                lin2txt.setCompoundDrawablesWithIntrinsicBounds(null, lin2dra, null, null);
                lin3txt.setCompoundDrawablesWithIntrinsicBounds(null, lin3dra, null, null);
                lin4txt.setCompoundDrawablesWithIntrinsicBounds(null, lin4dra, null, null);
                lin5txt.setCompoundDrawablesWithIntrinsicBounds(null, lin5dra, null, null);
            }
        });
        lin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                title.setText(getResources().getString(R.string.account));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new MyAccount()).commit();
                lin1txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin2txt.setTextColor(getResources().getColor(R.color.cologreen));
                lin3txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin4txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin5txt.setTextColor(getResources().getColor(R.color.colorAccent));

                Drawable lin1draw = getResources().getDrawable(R.drawable.homeicon);
                Drawable lin2dra = getResources().getDrawable(R.drawable.accountact);
                Drawable lin3dra = getResources().getDrawable(R.drawable.p2);
                Drawable lin4dra = getResources().getDrawable(R.drawable.p3);
                Drawable lin5dra = getResources().getDrawable(R.drawable.p4);

                lin1txt.setCompoundDrawablesWithIntrinsicBounds(null, lin1draw, null, null);
                lin2txt.setCompoundDrawablesWithIntrinsicBounds(null, lin2dra, null, null);
                lin3txt.setCompoundDrawablesWithIntrinsicBounds(null, lin3dra, null, null);
                lin4txt.setCompoundDrawablesWithIntrinsicBounds(null, lin4dra, null, null);
                lin5txt.setCompoundDrawablesWithIntrinsicBounds(null, lin5dra, null, null);
            }
        });
        lin3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                title.setText(getResources().getString(R.string.guid));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new GudidFragment()).commit();
                lin1txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin2txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin3txt.setTextColor(getResources().getColor(R.color.cologreen));
                lin4txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin5txt.setTextColor(getResources().getColor(R.color.colorAccent));

                Drawable lin1draw = getResources().getDrawable(R.drawable.homeicon);
                Drawable lin2dra = getResources().getDrawable(R.drawable.p1);
                Drawable lin3dra = getResources().getDrawable(R.drawable.guidactive);
                Drawable lin4dra = getResources().getDrawable(R.drawable.p3);
                Drawable lin5dra = getResources().getDrawable(R.drawable.p4);

                lin1txt.setCompoundDrawablesWithIntrinsicBounds(null, lin1draw, null, null);
                lin2txt.setCompoundDrawablesWithIntrinsicBounds(null, lin2dra, null, null);
                lin3txt.setCompoundDrawablesWithIntrinsicBounds(null, lin3dra, null, null);
                lin4txt.setCompoundDrawablesWithIntrinsicBounds(null, lin4dra, null, null);
                lin5txt.setCompoundDrawablesWithIntrinsicBounds(null, lin5dra, null, null);
            }
        });
        lin4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                title.setText(getResources().getString(R.string.not));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new ChatFragment()).commit();
                lin1txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin2txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin3txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin4txt.setTextColor(getResources().getColor(R.color.cologreen));
                lin5txt.setTextColor(getResources().getColor(R.color.colorAccent));

                Drawable lin1draw = getResources().getDrawable(R.drawable.homeicon);
                Drawable lin2dra = getResources().getDrawable(R.drawable.p1);
                Drawable lin3dra = getResources().getDrawable(R.drawable.p2);
                Drawable lin4dra = getResources().getDrawable(R.drawable.chateactive);
                Drawable lin5dra = getResources().getDrawable(R.drawable.p4);

                lin1txt.setCompoundDrawablesWithIntrinsicBounds(null, lin1draw, null, null);
                lin2txt.setCompoundDrawablesWithIntrinsicBounds(null, lin2dra, null, null);
                lin3txt.setCompoundDrawablesWithIntrinsicBounds(null, lin3dra, null, null);
                lin4txt.setCompoundDrawablesWithIntrinsicBounds(null, lin4dra, null, null);
                lin5txt.setCompoundDrawablesWithIntrinsicBounds(null, lin5dra, null, null);
            }
        });
        lin5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                title.setText(getResources().getString(R.string.va));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentFavourits()).commit();
                lin1txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin2txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin3txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin4txt.setTextColor(getResources().getColor(R.color.colorAccent));
                lin5txt.setTextColor(getResources().getColor(R.color.cologreen));

                Drawable lin1draw = getResources().getDrawable(R.drawable.homeicon);
                Drawable lin2dra = getResources().getDrawable(R.drawable.p1);
                Drawable lin3dra = getResources().getDrawable(R.drawable.p2);
                Drawable lin4dra = getResources().getDrawable(R.drawable.p3);
                Drawable lin5dra = getResources().getDrawable(R.drawable.varactiv);

                lin1txt.setCompoundDrawablesWithIntrinsicBounds(null, lin1draw, null, null);
                lin2txt.setCompoundDrawablesWithIntrinsicBounds(null, lin2dra, null, null);
                lin3txt.setCompoundDrawablesWithIntrinsicBounds(null, lin3dra, null, null);
                lin4txt.setCompoundDrawablesWithIntrinsicBounds(null, lin4dra, null, null);
                lin5txt.setCompoundDrawablesWithIntrinsicBounds(null, lin5dra, null, null);
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentNotiFication()).addToBackStack(null).commit();
//                title.setText(getResources().getString(R.string.notti));
            }
        });

        frMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, AddPostActivity.class));
            }
        });

        icMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, SettingActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(getString(R.string.doYouWantToLogout));
        dialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }
}

package com.shalate.red.shalate.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shalate.red.shalate.Adapter.ViewPagerAdapter;
import com.shalate.red.shalate.Fragment.FragmentNotiFication;
import com.shalate.red.shalate.Model.TrackModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.service.UpdateLocationService;

public class HomeActivity extends AppCompatActivity {

    private ViewPagerAdapter adapter;
    private int[] defaultIcon;
    private boolean isFirstTime;
    private int[] slelectIcon;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    FrameLayout frAddPost;
    private PrefrencesStorage storage;
    private double latitude, longituide;
    private LatLng latLngA;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private String image;
    private ImageView icMenu;
    private ImageView notification;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        storage = new PrefrencesStorage(this);
        activity = this;
        bind();
        init();
//        if (getIntent().getExtras() != null) {
//            if (getIntent().getExtras().getString("from").equals("notification")) {
//                viewPager.setCurrentItem(3);
//            }
//        }
    }

    private void bind() {

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tablayout);
        frAddPost = findViewById(R.id.frMore);
        viewPager.setOffscreenPageLimit(4);
        icMenu = findViewById(R.id.icMenu);
        notification = findViewById(R.id.notification);
        image = storage.getKey("image");

        if (storage.getKey("account_type").equals("provider")) {
            frAddPost.setVisibility(View.VISIBLE);
            updateLocation();
        } else {
            startService(new Intent(this, UpdateLocationService.class));
            frAddPost.setVisibility(View.GONE);

        }

        frAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AddPostActivity.class));
            }
        });

        icMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new FragmentNotiFication())
                        .addToBackStack(null)
                        .commit();
            }
        });
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

    private void init() {

        this.defaultIcon = new int[]{
                R.drawable.homeicon,
                R.drawable.p1,
                R.drawable.p2,
                R.drawable.p3,
        };

        this.slelectIcon = new int[]{

                R.drawable.p0,
                R.drawable.accountact,
                R.drawable.guidactive,
                R.drawable.chateactive,
        };

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), this, this.isFirstTime);
        viewPager.setAdapter(this.adapter);
        tabLayout.setupWithViewPager(this.viewPager);
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setTextSize(13);

        for (int i = 0; i < defaultIcon.length; i++) {
            tabLayout.getTabAt(i).setIcon(defaultIcon[i]);
        }

        this.tabLayout.getTabAt(0).setIcon(this.slelectIcon[0]);
        this.viewPager.setOffscreenPageLimit(4);
        viewPager.setCurrentItem(0);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                tab.setIcon(slelectIcon[tab.getPosition()]);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                tab.setIcon(defaultIcon[tab.getPosition()]);


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Fragment fragment = adapter.getFragment(state);
                if (fragment != null) {
                    fragment.onResume();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }else {
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

}

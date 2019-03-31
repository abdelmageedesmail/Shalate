package com.shalate.red.shalate.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.Model.SpinnerModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.GPSTracker;
import com.shalate.red.shalate.Utilities.PermissionsEnabled;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;
import com.shalate.red.shalate.notification.FCMRegistrationService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class EditMehniProfileActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    EditText tvName, etEmail, etPhone, etPassword, etLocation, etDescription;

    private MapView mMapView;
    GoogleMap mMap;
    private ImageView ivMarker;
    private Dialog dialog;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private double latitude, longitude;
    GPSTracker mGps;
    ImageView profile_image, ivBack;
    ProgressLoading loading;
    PrefrencesStorage storage;
    ArrayList<SpinnerModel> arrayList;
    private String catId;
    private String macAddress;
    private String lang;
    LinearLayout liCreate, liLocation;
    private String notMove;
    private String id;
    private String JobId;
    int SELECT_FILE = 1;
    private String imagepath;
    private File imageFile;
    private Uri selectedImageUri;
    private String selectedImagePath;
    private Bitmap bm;
    String substring;
    private double lat, lon;
    PermissionsEnabled enabled;
    Button btnEdit, btnEditPass;
    private EditText etCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mehni_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Slidr.attach(this);
        init();
        bind();
        getProfileData();
        updateLocation();
        getMacAddress();
    }

    private void init() {
        loading = new ProgressLoading(this);
        storage = new PrefrencesStorage(this);
        mGps = new GPSTracker(this);
        arrayList = new ArrayList<>();
        enabled = new PermissionsEnabled(this);
        enabled.enablePermission(1, PermissionsEnabled.LOCATION_REQUEST_CODE);
    }

    private void bind() {
        tvName = findViewById(R.id.tvName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        liCreate = findViewById(R.id.liCreate);
        liLocation = findViewById(R.id.liLocation);
        profile_image = findViewById(R.id.profile_image);
        ivBack = findViewById(R.id.ivBack);
        btnEditPass = findViewById(R.id.btnEditPass);
        btnEdit = findViewById(R.id.btnEdit);
        etCategory = findViewById(R.id.etCategory);
        etCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditMehniProfileActivity.this, CategoryActivity.class));
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnEdit.setOnClickListener(this);
        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMap();
            }
        });
        profile_image.setOnClickListener(this);
        btnEditPass.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
    }

    private void showMap() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.map_dialog);
        dialog.show();
        mMapView = dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(this);
        mMapView = dialog.findViewById(R.id.mapView);
        ivMarker = dialog.findViewById(R.id.ivMarker);
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.getMapAsync(this);
        mMapView.onResume();// needed to get the map to display immediately
    }

    private void selectLocation() {
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //get latlng at the center by calling
                final LatLng midLatLng = mMap.getCameraPosition().target;
                final BitmapDescriptor iconLocation = BitmapDescriptorFactory.fromResource(R.drawable.marker);
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mMap.addMarker(new MarkerOptions().position(midLatLng).icon(iconLocation).title("userLocation"));
                        ivMarker.setVisibility(View.GONE);
                        latitude = latLng.latitude;
                        longitude = latLng.longitude;
//                        PrefrencesStorage localeShared = new PrefrencesStorage(getActivity());
                        Log.e("midLatLang", "" + latitude + "," + longitude);
                        etLocation.setText(getAddress(latitude, longitude));
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getThoroughfare()).append(" - ");
                result.append(address.getLocality()).append(" - ");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    private void getCategory() {
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.get(Urls.getCategory)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    SpinnerModel model = new SpinnerModel();
                                    JSONObject name = object.getJSONObject("name");
                                    if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
                                        model.setName(name.getString("ar"));
                                    } else {
                                        model.setName(name.getString("en"));
                                    }
                                    model.setId(object.getString("id"));
                                    model.setCatType(object.getString("cat_type"));
                                    arrayList.add(model);
                                }
//                                spCategory.setAdapter(new SpinnerAdapter(EditMehniProfileActivity.this, arrayList));
                                if (storage.getKey("account_type").equals("provider")) {
                                    for (int i = 0; i < arrayList.size(); i++) {
                                        if (arrayList.get(i).getId().equals(JobId)) {
                                            if (arrayList.get(i).getCatType().equals("not_move")) {
                                                liLocation.setVisibility(View.VISIBLE);
                                            } else {
                                                liLocation.setVisibility(View.GONE);
                                            }
                                            catId = arrayList.get(i).getId();
                                            etCategory.setText(arrayList.get(i).getName());
                                        }
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

    @Override
    protected void onResume() {
        super.onResume();
        if (CategoryActivity.fromCategory) {
            etCategory.setText(CategoryActivity.name);
            if (CategoryActivity.type.equals("not_move")) {
                notMove = "1";
                liLocation.setVisibility(View.VISIBLE);
            } else {
                notMove = "0";
                liLocation.setVisibility(View.GONE);
            }
            CategoryActivity.fromCategory = false;
        }

    }


    private void updateLocation() {
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                lat = location.getLatitude();
                lon = location.getLongitude();
                locationManager.removeUpdates(this);

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

    private void getProfileData() {
        loading.showLoading();
        AndroidNetworking.get(Urls.getProfile + storage.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", "" + response);
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONObject data = response.getJSONObject("data");
                                JSONObject information = data.getJSONObject("information");
                                id = information.getString("id");
                                tvName.setText(information.getString("username"));
                                Picasso.with(EditMehniProfileActivity.this).load(information.getString("image")).into(profile_image);
                                etEmail.setText(information.getString("email"));
                                etPhone.setText(information.getString("phone"));
                                if (storage.getKey("account_type").equals("provider")) {
                                    etLocation.setText(information.getString("address"));
                                    JSONObject job = information.getJSONObject("job");
                                    JobId = job.getString("id");
                                    etDescription.setText(information.getString("information"));
                                }

                                getCategory();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_image:
                selectImage();
                break;
            case R.id.btnEdit:
                update();
                break;

            case R.id.btnEditPass:
                startActivity(new Intent(EditMehniProfileActivity.this, UpdatePassword.class));
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng ny = new LatLng(mGps.getLatitude(), mGps.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
        mMap.clear();
        mMap.getUiSettings().setMapToolbarEnabled(false);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(ny).zoom(9).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        selectLocation();
    }

    private void update() {
        if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
            lang = "ar";
        } else {
            lang = "en";
        }

        loading.showLoading();
        String url = Urls.updateProfile + storage.getId() + "&lang=" + lang;
        AndroidNetworking.initialize(EditMehniProfileActivity.this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        ANRequest.MultiPartBuilder upload = AndroidNetworking.upload(url);
        upload.addMultipartParameter("username", tvName.getText().toString())
                .addMultipartParameter("device_type", "android")
                .addMultipartParameter("device_token", FCMRegistrationService.token)
                .addMultipartParameter("device_unique_address", macAddress)
                .addMultipartParameter("phone", etPhone.getText().toString())
                .addMultipartParameter("account_type", "provider");
        if (CategoryActivity.id != null) {
            Log.e("CatIdFrom", "" + CategoryActivity.id);
            upload.addMultipartParameter("job", CategoryActivity.id);
        } else {
            upload.addMultipartParameter("job", catId);
        }

        if (!etPassword.getText().toString().equals("") || !etPassword.getText().toString().isEmpty()) {
            upload.addMultipartParameter("password", etPassword.getText().toString());
        }

        if (!etDescription.getText().toString().equals("") || !etDescription.getText().toString().isEmpty()) {
            upload.addMultipartParameter("information", etDescription.getText().toString());
        }

        if (notMove != null) {
            if (notMove.equals("1")) {
                upload.addMultipartParameter("lat", "" + latitude)
                        .addMultipartParameter("lng", "" + longitude);
            } else {
                upload.addMultipartParameter("lat", "" + lat)
                        .addMultipartParameter("lng", "" + lon);
            }
        } else {
            upload.addMultipartParameter("lat", "" + lat)
                    .addMultipartParameter("lng", "" + lon);
        }

        if (imageFile != null) {
            upload.addMultipartFile("image", imageFile);
        }
        upload.setPriority(Priority.MEDIUM)
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
                                PrefrencesStorage storage = new PrefrencesStorage(EditMehniProfileActivity.this);
                                storage.storeId(jsonObject.getString("id"));
                                storage.storeKey("name", jsonObject.getString("username"));
                                storage.storeKey("email", jsonObject.getString("email"));
                                storage.storeKey("phone", jsonObject.getString("phone"));
                                storage.storeKey("account_type", jsonObject.getString("account_type"));
                                storage.storeKey("image", jsonObject.getString("image"));

                                startActivity(new Intent(EditMehniProfileActivity.this, HomePage.class));
                            } else {
                                JSONArray errors = response.getJSONArray("errors");
                                for (int i = 0; i < errors.length(); i++) {
                                    JSONObject jsonObject = errors.getJSONObject(i);
                                    String key = jsonObject.getString("key");
                                    if (key.equals("email")) {
                                        Toast.makeText(EditMehniProfileActivity.this, getString(R.string.emailAlreadyTaken), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EditMehniProfileActivity.this, getString(R.string.errorTryAgain), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loading.cancelLoading();
                        Log.e("ERROR", "" + anError.getMessage());
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

    private void selectImage() {

        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                SELECT_FILE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }
        }
    }
    //************************* HERE ***************************************************************

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        selectedImagePath = cursor.getString(column_index);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        substring = selectedImagePath.substring(selectedImagePath.lastIndexOf(".") + 1);

        imagepath = selectedImageUri.getPath();
        imageFile = new File(selectedImagePath);
        Bitmap finalImage = null;
        try {
            ExifInterface ei = new ExifInterface(selectedImagePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Log.e("orientation", orientation + "");
            //finalImage = rotateImage(bm, 270);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    finalImage = rotateImage(bm, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    finalImage = rotateImage(bm, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    finalImage = rotateImage(bm, 270);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (finalImage != null) {
            profile_image.setImageBitmap(finalImage);
        } else {
            profile_image.setImageBitmap(bm);
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }
}

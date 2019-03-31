package com.shalate.red.shalate.Fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.shalate.red.shalate.Activity.ProfessionActivity;
import com.shalate.red.shalate.Adapter.CategoryRecycleAdapter;
import com.shalate.red.shalate.Adapter.ConmmentAdapter;
import com.shalate.red.shalate.Model.CommentModel;
import com.shalate.red.shalate.Model.FilterModel;
import com.shalate.red.shalate.Model.ServiceModel;
import com.shalate.red.shalate.Model.TrackModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.GPSTracker;
import com.shalate.red.shalate.Utilities.PermissionsEnabled;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener {

    HashMap<String, Marker> mMarkers = new HashMap<>();
    Marker markerUpdate;
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String lat;
    String lng;
    SupportMapFragment mapFragment;
    SharedPreferences preferencesid;
    String serviceID;
    BitmapDescriptor icon;
    ArrayList<ServiceModel> services;
    TextView name, des, tvAddress, tvPhone, tvWebSite;
    RecyclerView recyclebestcoment;
    ArrayList<CommentModel> commentModels;
    PrefrencesStorage storage;
    ProgressLoading loading;
    private EditText etComment;
    RelativeLayout rvAddComment, rvPhone;
    private ConmmentAdapter adapter1;
    private String from;
    private Bitmap bmp;
    private JSONObject object;
    private List<Target> targets;
    private List<Marker> vendorMarker;
    private int i;
    private String lat1, lang;
    private View v;
    PermissionsEnabled enabled;
    private double latitude;
    private double longituide;
    FloatingActionMenu menu;
    FloatingActionButton menu_item, menu_item2;
    private String chatKey;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage fireBasestorage;
    StorageReference storageReference;
    private Marker source;
    private LatLng location;
    private String key;
    private TrackModel value;
    private Bitmap smallMarker;
    EditText etSearch;
    private RecyclerView rvCategory;
    private int AUTO_COMP_REQ_CODE = 10;
    private ArrayList<FilterModel> filterModels;
    LinearLayout liSheet;
    private LatLng latLng;
    private Location userCurrentLocation;
    private String photo;
    private String url;
    private double latLocation, lngLocation;
    public static LatLng latngLocation;
    private String id;
    private RelativeLayout rvLocation;
    private double serviceLat, serviceLon;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        enabled = new PermissionsEnabled(MapFragment.this);
        enabled.enablePermission(3, PermissionsEnabled.CALL_PHONE);
        enabled.enablePermission(1, PermissionsEnabled.LOCATION_REQUEST_CODE);
        bind();
        menu = findViewById(R.id.menu);
        menu_item = findViewById(R.id.menu_item);
        menu_item2 = findViewById(R.id.menu_item2);
        rvCategory = (RecyclerView) findViewById(R.id.rvCategory);
        etSearch = (EditText) findViewById(R.id.etSearch);
        liSheet = findViewById(R.id.liSheet);

        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MapFragment.this);
                    startActivityForResult(intent, AUTO_COMP_REQ_CODE);
                } catch (Exception e) {
                    Log.e("error", e.getStackTrace().toString());
                }
            }
        });

        vendorMarker = new ArrayList<>();
        targets = new ArrayList<>();

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_map, container, false);
//
//
//        return view;
//    }

    private void bind() {

//        Bundle bundle = getArguments();
        filterModels = new ArrayList<>();
        serviceID = getIntent().getExtras().getString("id");
        from = getIntent().getExtras().getString("from");


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        services = new ArrayList<>();
        commentModels = new ArrayList<>();
        storage = new PrefrencesStorage(MapFragment.this);
        loading = new ProgressLoading(MapFragment.this);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

        } else {

        }


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(MapFragment.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.connect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        menu_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        menu_item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });
        if (ActivityCompat.checkSelfPermission(MapFragment.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapFragment.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        icon = BitmapDescriptorFactory.fromResource(R.drawable.pinmarker);

        if ((ActivityCompat.checkSelfPermission((Activity) MapFragment.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission((Activity) MapFragment.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions((Activity) MapFragment.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            userCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (userCurrentLocation != null) {
                MarkerOptions currentUserLocation = new MarkerOptions();
                LatLng currentUserLatLang = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
                currentUserLocation.position(currentUserLatLang);
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        currentUserLatLang, 13);
                mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                    }
                });
                if (from.equals("guide")) {
                    liSheet.setVisibility(View.GONE);
                    photo = getIntent().getExtras().getString("photo");
                    getServices();
                } else {
                    String cat = getIntent().getExtras().getString("cat");
                    if (cat.equals("move")) {
                        getCategory();
                        getValue();
                    } else {
                        getCategory();
                        getAllProfesstion(serviceID, userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
                    }
//                    mMap.setOnInfoWindowClickListener(this);
                }


            }
        }
    }


    private void getValue() {

        Log.e("servId", "" + serviceID);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ProfessionTrack").child(serviceID);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
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

    private void setMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once

        int height = 100;
        int width = 100;
        key = dataSnapshot.getKey();
        value = dataSnapshot.getValue(TrackModel.class);
        latLocation = Double.parseDouble(value.getLat());
        lngLocation = Double.parseDouble(value.getLon());
        location = new LatLng(latLocation, lngLocation);
        if (value.getStatus() == 1) {
            if (!mMarkers.containsKey(key)) {
                bmp = getBitmapFromURL(value.getUserProfile());
//            Log.e("bitmap", "" + getBitmapFromURL(value.getUserProfile()).getWidth());
                smallMarker = Bitmap.createScaledBitmap(bmp, width, height, false);
                markerUpdate = mMap.addMarker(new MarkerOptions().title(key).position(location).icon(BitmapDescriptorFactory.fromBitmap(getCroppedBitmap(smallMarker))).snippet(value.getFrom()));
                mMarkers.put(key, markerUpdate);
                mMarkers.get(key).setPosition(location);
            } else {
                bmp = getBitmapFromURL(value.getUserProfile());
                Log.e("bitmap", "" + getBitmapFromURL(value.getUserProfile()).getWidth());
                smallMarker = Bitmap.createScaledBitmap(bmp, width, height, false);
                markerUpdate = mMap.addMarker(new MarkerOptions().title(key).position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(getCroppedBitmap(smallMarker))).snippet(value.getFrom()));
                mMarkers.put(key, markerUpdate);
                mMarkers.get(key).setPosition(location);
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : mMarkers.values()) {
                builder.include(marker.getPosition());
            }
        }

//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longituide)).zoom(12).build();
        mMap.setOnMarkerClickListener(this);
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
            return null;
        }
    }


    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private void getAllProfesstion(String id, double lat, double lon) {
        String url = Urls.providerSearch;
        AndroidNetworking.initialize(MapFragment.this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(url)
                .addBodyParameter("lat", "" + lat)
                .addBodyParameter("lng", "" + lon)
                .addBodyParameter("category", id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", "" + response);
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONArray data = response.getJSONArray("data");
                                for (i = 0; i < data.length(); i++) {
                                    object = data.getJSONObject(i);
                                    lat1 = object.getString("lat");
                                    lang = object.getString("lng");
                                    String id = object.getString("id");
                                    final String imageUrl = object.getString("image");

                                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                        @Override
                                        public View getInfoWindow(Marker marker) {
                                            return v;
                                        }

                                        @Override
                                        public View getInfoContents(Marker marker) {
                                            v = LayoutInflater.from(MapFragment.this).inflate(R.layout.icnon_marker, null);
                                            CircleImageView markerImage = v.findViewById(R.id.user_dp);
//                                            Picasso.with(getActivity()).load(imageUrl).into(markerImage);
                                            RelativeLayout custom_marker_view = v.findViewById(R.id.custom_marker_view);
                                            return v;
                                        }
                                    });

                                    mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(object.getString("lat")), Double.parseDouble(object.getString("lng")))).snippet(id)).showInfoWindow();

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

    private void getServices() {
        String url = Urls.getGuide + "/" + serviceID;
        AndroidNetworking.initialize(MapFragment.this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.get(url)
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
                                    object = data.getJSONObject(i);
                                    ServiceModel model = new ServiceModel();
                                    model.setId(object.getString("id"));
                                    JSONObject name = object.getJSONObject("name");
                                    if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
                                        model.setName(name.getString("ar"));
                                    } else {
                                        model.setName(name.getString("en"));
                                    }
                                    String image = object.getString("image");
                                    model.setImage(image);
                                    JSONObject address = object.getJSONObject("address");
                                    if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
                                        model.setAddress(address.getString("ar"));
                                    } else {
                                        model.setAddress(address.getString("en"));
                                    }
                                    JSONObject information = object.getJSONObject("information");
                                    if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
                                        model.setInformation(information.getString("ar"));
                                    } else {
                                        model.setInformation(information.getString("en"));
                                    }
                                    model.setPhone(object.getString("phone"));
                                    model.setWebsite(object.getString("website"));
                                    JSONObject rate = object.getJSONObject("rate");
                                    model.setRate(rate.getString("rate"));
                                    JSONArray comments = rate.getJSONArray("comments");
                                    model.setComments(comments);
//
                                    int height = 100;
                                    int width = 100;
                                    bmp = getBitmapFromURL(image);
                                    smallMarker = Bitmap.createScaledBitmap(bmp, width, height, false);
                                    Marker marker = createMarker(Double.parseDouble(object.getString("lat")), Double.parseDouble(object.getString("lng")), String.valueOf(i));
                                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(getCroppedBitmap(smallMarker)));
                                    services.add(model);
                                }
                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        serviceLat = marker.getPosition().latitude;
                                        serviceLon = marker.getPosition().longitude;
                                        setUpDialoge();
                                        int s = Integer.parseInt(marker.getSnippet());
                                        name.setText(services.get(s).getName());
                                        des.setText(services.get(s).getInformation());
                                        tvAddress.setText(services.get(s).getAddress());
                                        tvPhone.setText(services.get(s).getPhone());
                                        tvWebSite.setText(services.get(s).getWebsite());
                                        id = services.get(s).getId();
                                        try {
                                            for (int j = 0; j < services.get(s).getComments().length(); j++) {
                                                CommentModel model1 = new CommentModel();
                                                JSONObject obj = services.get(s).getComments().getJSONObject(j);
                                                JSONObject user_id = obj.getJSONObject("user_id");
                                                model1.setImage(user_id.getString("image"));
                                                model1.setComment(obj.getString("comment"));
                                                commentModels.add(model1);
                                            }
                                            adapter1 = new ConmmentAdapter(MapFragment.this, commentModels);
                                            recyclebestcoment.setAdapter(adapter1);
                                            recyclebestcoment.setLayoutManager(new LinearLayoutManager(MapFragment.this, LinearLayoutManager.VERTICAL, true));
                                            adapter1.notifyDataSetChanged();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        return true;
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    protected Marker createMarker(double latitude, double longitude, String snippt) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).snippet(snippt));
        return marker;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private void getCategory() {
        AndroidNetworking.initialize(MapFragment.this);
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
                                    FilterModel filterModel = new FilterModel();
                                    filterModel.setImage("http://sahalaatq8.com/" + object.getString("image"));
                                    JSONObject name = object.getJSONObject("name");
                                    filterModel.setCategory(object.getString("cat_type"));
                                    if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
                                        filterModel.setName(name.getString("ar"));
                                    } else {
                                        filterModel.setName(name.getString("en"));
                                    }
                                    filterModel.setId(object.getString("id"));
                                    filterModels.add(filterModel);
                                }
                                liSheet.setVisibility(View.VISIBLE);
                                CategoryRecycleAdapter adapter1 = new CategoryRecycleAdapter(MapFragment.this, filterModels);
                                rvCategory.setAdapter(adapter1);
                                rvCategory.setLayoutManager(new LinearLayoutManager(MapFragment.this, LinearLayoutManager.HORIZONTAL, false));

                                adapter1.notifyDataSetChanged();
                                adapter1.setOnClickListener(new CategoryRecycleAdapter.OnItemClickListener() {
                                    @Override
                                    public void onclick(int position) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("from", "filter");
                                        bundle.putCharSequence("id", filterModels.get(position).getId());
                                        bundle.putCharSequence("cat", filterModels.get(position).getCategory());
                                        serviceID = filterModels.get(position).getId();
                                        if (latLng != null) {
                                            mMap.clear();
                                            if (filterModels.get(position).getCategory().equals("move")) {
                                                getValue();
                                            } else {
                                                getAllProfesstion(filterModels.get(position).getId(), latLng.latitude, latLng.latitude);
                                            }

                                        } else {
                                            mMap.clear();
                                            if (filterModels.get(position).getCategory().equals("move")) {
                                                getValue();
                                            } else {
                                                getAllProfesstion(filterModels.get(position).getId(), latitude, longituide);
                                            }

                                        }

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTO_COMP_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(MapFragment.this, data);
                etSearch.setText(place.getAddress());
                latLng = place.getLatLng();

            }
        }
    }

    public void setUpDialoge() {
        DialogPlus dialog = DialogPlus.newDialog(MapFragment.this)
                .setExpanded(true, 900)
                .setContentHolder(new ViewHolder(R.layout.dialogepluse))
                .setGravity(Gravity.BOTTOM)
                .setContentHeight(900)
                .setContentBackgroundResource(android.R.color.transparent)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                    }
                })
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        recyclebestcoment = (RecyclerView) dialog.findViewById(R.id.recyclebestcoment);
        name = (TextView) dialog.findViewById(R.id.name);
        des = (TextView) dialog.findViewById(R.id.des);
        tvAddress = (TextView) dialog.findViewById(R.id.tvAddress);
        tvPhone = (TextView) dialog.findViewById(R.id.tvPhone);
        tvWebSite = (TextView) dialog.findViewById(R.id.tvWebSite);
        rvAddComment = (RelativeLayout) dialog.findViewById(R.id.rvAddComment);
        rvPhone = (RelativeLayout) dialog.findViewById(R.id.rvPhone);
        rvLocation = (RelativeLayout) dialog.findViewById(R.id.rvLocation);

        rvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GPSTracker mGps = new GPSTracker(MapFragment.this);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + mGps.getLatitude() + "," + mGps.getLongitude()+ "&daddr=" + serviceLat + "," + serviceLon));
                startActivity(intent);
            }
        });
        rvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + tvPhone.getText().toString()));

                if (ActivityCompat.checkSelfPermission(MapFragment.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                startActivity(intent);
            }
        });

        tvWebSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                if (tvWebSite.getText().toString().startsWith("http") || tvWebSite.getText().toString().startsWith("https")) {
                    url = tvWebSite.getText().toString();
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } else {
                    Toast.makeText(MapFragment.this, getString(R.string.errorWebSite), Toast.LENGTH_SHORT).show();
                }

            }
        });
        etComment = (EditText) dialog.findViewById(R.id.etComment);
//        ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);
        rvAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!storage.isFirstTimeLogin()) {
                    Toast.makeText(MapFragment.this, getString(R.string.loginRegisterFirst), Toast.LENGTH_SHORT).show();
                } else {
                    if (etComment.getText().toString().isEmpty()) {
                        etComment.setError(getString(R.string.empty));
                    } else {
                        addRate(etComment.getText().toString());
                    }
                }
            }
        });
        dialog.show();


    }

    private void addRate(String comment) {
        loading.showLoading();
        AndroidNetworking.initialize(MapFragment.this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(Urls.addComment)
                .addBodyParameter("serv_id", id)
                .addBodyParameter("user_id", storage.getId())
                .addBodyParameter("rate", "2")
                .addBodyParameter("comment", comment)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                Toast.makeText(MapFragment.this, getString(R.string.commentAddedSuccessfully), Toast.LENGTH_SHORT).show();
                                etComment.setText("");
                                adapter1.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MapFragment.this, getString(R.string.youAddedRateBefore), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loading.cancelLoading();
                        Log.e("error", "" + anError.getMessage());
                    }
                });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(MapFragment.this, ProfessionActivity.class);
        Log.e("id", ".." + marker.getSnippet() + "..." + marker.getPosition() + ". .." + key);
        intent.putExtra("id", marker.getSnippet());
        latngLocation = marker.getPosition();
        startActivity(intent);
        return true;
    }
}

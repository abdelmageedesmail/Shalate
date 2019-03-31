package com.shalate.red.shalate.Fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.androidnetworking.AndroidNetworking;
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
import com.shalate.red.shalate.Adapter.FilterRecycleAdapter;
import com.shalate.red.shalate.Adapter.SpinnerAdapter;
import com.shalate.red.shalate.Model.FilterModel;
import com.shalate.red.shalate.Model.SpinnerModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.GPSTracker;
import com.shalate.red.shalate.Utilities.PermissionsEnabled;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MehaniRegistration extends Fragment implements OnMapReadyCallback {

    EditText tvName, etEmail, etPhone, etPassword, etLocation, etDescription;
    Spinner spCategory;
    private MapView mMapView;
    GoogleMap mMap;
    private ImageView ivMarker;
    private Dialog dialog;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private double latitude, longitude;
    GPSTracker mGps;
    ProgressLoading loading;
    PrefrencesStorage storage;
    ArrayList<SpinnerModel> arrayList;
    private String catId;

    public MehaniRegistration() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        PermissionsEnabled enabled = new PermissionsEnabled(getActivity());
        enabled.enablePermission(1, PermissionsEnabled.LOCATION_REQUEST_CODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_mehani_registration, container, false);
        bind(inflate);
        getCategory();
        return inflate;
    }

    private void init() {
        loading = new ProgressLoading(getActivity());
        storage = new PrefrencesStorage(getActivity());
        mGps = new GPSTracker(getActivity());
        arrayList=new ArrayList<>();
    }

    private void bind(View v) {
        tvName = v.findViewById(R.id.tvName);
        etEmail = v.findViewById(R.id.etEmail);
        etPhone = v.findViewById(R.id.etPhone);
        etPassword = v.findViewById(R.id.etPassword);
        etLocation = v.findViewById(R.id.etLocation);
        etDescription = v.findViewById(R.id.etDescription);
        spCategory = v.findViewById(R.id.spCategory);
        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMap();
            }
        });
    }

    private void showMap() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.map_dialog);
        dialog.show();
        mMapView = dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(getActivity());
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
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void getCategory(){
        AndroidNetworking.initialize(getActivity());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.get(Urls.getCategory)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")){
                                JSONArray data = response.getJSONArray("data");
                                for (int i=0;i<data.length();i++){
                                    JSONObject object = data.getJSONObject(i);
                                    SpinnerModel model=new SpinnerModel();
                                    JSONObject name = object.getJSONObject("name");
                                    if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
                                        model.setName(name.getString("ar"));
                                    } else {
                                        model.setName(name.getString("en"));
                                    }
                                    model.setId(object.getString("id"));
                                    arrayList.add(model);
                                }
                                spCategory.setAdapter(new SpinnerAdapter(getActivity(),arrayList));
                                spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        catId = arrayList.get(i).getId();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

}

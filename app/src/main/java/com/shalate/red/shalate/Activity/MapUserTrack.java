package com.shalate.red.shalate.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shalate.red.shalate.Model.TrackModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.GPSTracker;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.helperClasses.DirectionsJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapUserTrack extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double latitude, longituide;
    private BitmapDescriptor icon;
    private BitmapDescriptor icon2;
    String latO, lon;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage;
    StorageReference storageReference;
    private String chatKey, from, to;
    PrefrencesStorage prefrencesStorage;
    DatabaseReference reference;
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private Marker markerUpdate;
    private Timer timer;
    GPSTracker mGps;

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);
    ImageView ivNavigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_user_track);
//        Slidr.attach(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ivNavigator = findViewById(R.id.ivNavigator);

        icon2 = BitmapDescriptorFactory.fromResource(R.drawable.pinmarker);
        mGps = new GPSTracker(this);
        ivNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + mGps.getLatitude() + "," + mGps.getLongitude() + "&daddr=" + latO + "," + lon));
                view.getContext().startActivity(intent);
            }
        });
        inint();
        getIntentData();
        if (prefrencesStorage.getKey("account_type").equals("provider")) {
            if (prefrencesStorage.getId().equals(to)) {
                chatKey = prefrencesStorage.getId();
            } else {
                chatKey = to;
            }
        } else {
            if (prefrencesStorage.getId().equals(to)) {
                chatKey = from;
            } else {
                chatKey = to;
            }
        }
        updateLocation();

    }

    private void inint() {
        prefrencesStorage = new PrefrencesStorage(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }

    private void startTrack() {
        if (prefrencesStorage.getKey("account_type").equals("provider")) {
            if (prefrencesStorage.getId().equals(to)) {
                chatKey = prefrencesStorage.getId();
            } else {
                chatKey = to;
            }
        } else {
            if (prefrencesStorage.getId().equals(to)) {
                chatKey = from;
            } else {
                chatKey = to;
            }
        }

        DatabaseReference mRef = database.getReference("track").child(chatKey);
        TrackModel model = new TrackModel();
        if (prefrencesStorage.getKey("account_type").equals("provider")) {
            model.setProfessionLocation("" + mGps.getLatitude() + "," + mGps.getLongitude());
        }
        model.setFrom(from);
        model.setTo(to);
        mRef.setValue(model);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longituide);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mMap.setMyLocationEnabled(true);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                sydney, 13);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        timer = new Timer();
        if (prefrencesStorage.getKey("account_type").equals("provider")) {
            mMap.animateCamera(location);
            timer.schedule(new StartTracking(), 0, 30000);
            getValue();
        } else {
            getValue();
        }


    }


    private void getIntentData() {
        Intent i = getIntent();
        latO = i.getExtras().getString("lat");
        lon = i.getExtras().getString("lon");
        from = i.getExtras().getString("from");
        to = i.getExtras().getString("to");

        if (prefrencesStorage.getKey("account_type").equals("provider")) {
            chatKey = prefrencesStorage.getId();
        } else {
            chatKey = to;
        }
    }

    private void updateLocation() {
        icon = BitmapDescriptorFactory.fromResource(R.drawable.map_marker);
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
                mMap.animateCamera(cameraUpdate);
                latitude = location.getLatitude();
                longituide = location.getLongitude();

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

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, locationListener);

    }


    private void getValue() {
        Log.e("chatKeyMap", "" + chatKey);
        DatabaseReference myRef = database.getReference("track");

        myRef.child(chatKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("tmz", "Failed to read value.", error.toException());
            }
        });


    }

    private void setMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        double lat = 0.0, lng = 0.0;
        Log.e("dataSnap", "" + dataSnapshot.getValue());
        String key = dataSnapshot.getKey();

        TrackModel value = dataSnapshot.getValue(TrackModel.class);
//        if (prefrencesStorage.getKey("account_type").equals("provider")) {
        if (value != null) {
            if (value.getProfessionLocation() != null) {
                String professionLocation = value.getProfessionLocation();
                String[] split = professionLocation.split(",");
                lat = Double.parseDouble(split[0]);
                lng = Double.parseDouble(split[1]);
            }
        } else {
            lat = 0.0;
            lng = 0.0;
        }

//        }
        LatLng location = new LatLng(lat, lng);
//        if (!mMarkers.containsKey(key)) {
        if (markerUpdate != null) {
            markerUpdate.remove();
        }
        icon = BitmapDescriptorFactory.fromResource(R.drawable.map_marker);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(new LatLng((Double.parseDouble(latO)), Double.parseDouble(lon))).icon(icon).title(getString(R.string.myLocation)));
        markerUpdate = mMap.addMarker(new MarkerOptions().title(key).position(location).icon(icon).title(getString(R.string.professionLocation)));
//            mMarkers.put(key, markerUpdate);

//        } else {
//            mMarkers.get(key).setPosition(location);
//        }
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (Marker marker : mMarkers.values()) {
//            builder.include(marker.getPosition());
//        }
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 13));
        LatLng from = new LatLng(Double.parseDouble(latO), Double.parseDouble(lon));
        LatLng to = new LatLng(lat, lng);
        String url = getDirectionsUrl(to, from);
        Log.e("url", "" + url);
        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList points = null;
            PolylineOptions lineOptions = null;
//            polyline.setEndCap(new RoundCap());
//            polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
//            polyline.setColor(COLOR_BLACK_ARGB);
//            polyline.setJointType(JointType.
//                    ROUND);

            MarkerOptions markerOptions = new MarkerOptions();
            if (result.size() > 0) {


                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }
                    lineOptions.addAll(points);
                    lineOptions.width(30);
                    lineOptions.color(Color.parseColor("#BACC15"));
                    lineOptions.geodesic(true);
//                    Polyline polyline = mMap.addPolyline(lineOptions);

                    List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dot(), new Gap(10f));
                    lineOptions.pattern(pattern);
                }

// Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyCey-yYT55H-LoP8TMVwQGrLnaeXfKVPFk";


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    //timer task
    class StartTracking extends TimerTask {
        public void run() {
            startTrack();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}

package com.shalate.red.shalate.helperClasses;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.shalate.red.shalate.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomeInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;
    String imageURl;

    public CustomeInfoWindowAdapter(Activity context,String imageURl){
        this.context = context;
        this.imageURl=imageURl;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = context.getLayoutInflater().inflate(R.layout.icnon_marker, null);
        CircleImageView markerImage = v.findViewById(R.id.user_dp);
        Picasso.with(context).load(imageURl).into(markerImage);
        return v;
    }
}

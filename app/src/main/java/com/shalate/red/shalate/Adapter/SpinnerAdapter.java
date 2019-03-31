package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shalate.red.shalate.Model.SpinnerModel;
import com.shalate.red.shalate.R;

import java.util.ArrayList;

/**
 * Created by abdelmageed on 20/09/17.
 */

public class SpinnerAdapter extends BaseAdapter {

    Context c;
    ArrayList<SpinnerModel> objects;

    public SpinnerAdapter(Context context, ArrayList<SpinnerModel> objects) {
        super();
        this.c = context;
        this.objects = objects;
    }


    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        SpinnerModel cur_obj = objects.get(position);

      //  LayoutInflater inflater = ((Activity) c).getLayoutInflater();
        View row = LayoutInflater.from(c).inflate(R.layout.custome_spinner, parent, false);
        TextView label = (TextView) row.findViewById(R.id.name);
        label.setText(cur_obj.getName());
        return row;
    }
}
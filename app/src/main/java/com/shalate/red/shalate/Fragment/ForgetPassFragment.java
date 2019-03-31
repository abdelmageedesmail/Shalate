package com.shalate.red.shalate.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shalate.red.shalate.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetPassFragment extends Fragment {


    public ForgetPassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_pass, container, false);
    }

}

package com.shalate.red.shalate.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shalate.red.shalate.Activity.MehniRegisteration;
import com.shalate.red.shalate.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationType extends Fragment {
    LinearLayout mehanyAcount, client;
    View view;

    public RegistrationType() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_registration_type, container, false);
        setView();
        setClick();
        return view;
    }

    public void setView() {
        mehanyAcount = view.findViewById(R.id.mehanyAcount);
        client = view.findViewById(R.id.client);
    }

    public void setClick() {
        mehanyAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), MehniRegisteration.class);
                getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                startActivity(i);
            }
        });

        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top);
                fragmentTransaction.replace(R.id.container, new FragmentClientRegister());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

}

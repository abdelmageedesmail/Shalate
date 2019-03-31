package com.shalate.red.shalate.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shalate.red.shalate.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFavourits extends Fragment {
LinearLayout  Fshop ,Fperson;
View view;
TextView person,shoptxtt;

    public FragmentFavourits() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_fragment_favourits, container, false);

        setView();
        setupClick();
        Fperson.setBackgroundColor(getActivity().getResources().getColor(R.color.greenn));
        Fshop.setBackgroundColor(getActivity().getResources().getColor(R.color.grayy));
        person.setTextColor(getActivity().getResources().getColor(R.color.white));
        shoptxtt.setTextColor(getActivity().getResources().getColor(R.color.black));
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.continer,new FragmentForPerson()).commit();

        return  view;

    }
    public void setView(){
        Fshop=view.findViewById(R.id.Fshop);
        Fperson=view.findViewById(R.id.Fperson);

        person=view.findViewById(R.id.person);
        shoptxtt=view.findViewById(R.id.shoptxtt);
    }
    public void setupClick(){
        Fperson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fperson.setBackgroundColor(getActivity().getResources().getColor(R.color.greenn));
                Fshop.setBackgroundColor(getActivity().getResources().getColor(R.color.grayy));
                person.setTextColor(getActivity().getResources().getColor(R.color.white));
                shoptxtt.setTextColor(getActivity().getResources().getColor(R.color.black));
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.continer,new FragmentForPerson()).commit();

            }
        });
        Fshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fperson.setBackgroundColor(getActivity().getResources().getColor(R.color.grayy));
                Fshop.setBackgroundColor(getActivity().getResources().getColor(R.color.greenn));
                person.setTextColor(getActivity().getResources().getColor(R.color.black));
                shoptxtt.setTextColor(getActivity().getResources().getColor(R.color.white));
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.continer,new FragmentForShops()).commit();

            }
        });
    }
}

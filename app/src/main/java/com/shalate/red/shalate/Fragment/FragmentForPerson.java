package com.shalate.red.shalate.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shalate.red.shalate.Adapter.ConmmentAdapter;
import com.shalate.red.shalate.Adapter.PeronAdapter;
import com.shalate.red.shalate.Model.CommentModel;
import com.shalate.red.shalate.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForPerson extends Fragment {

View view;
RecyclerView FavaForperson;
    private ArrayList<CommentModel> commentModels;

    public FragmentForPerson() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=   inflater.inflate(R.layout.fragment_fragment_for_person, container, false);
        setVar();
        setRecycleview();
        return view;
    }
    public void setVar(){
        FavaForperson=view.findViewById(R.id.FavaForperson);
        commentModels=new ArrayList<>();
    }
    public void setRecycleview(){
        PeronAdapter adapter1 = new PeronAdapter(getActivity(), commentModels);
        FavaForperson.setAdapter(adapter1);
        FavaForperson.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,true));
        adapter1.notifyDataSetChanged();
    }


}

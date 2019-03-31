package com.shalate.red.shalate.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shalate.red.shalate.Adapter.PeronAdapter;
import com.shalate.red.shalate.Adapter.ShopAdapter;
import com.shalate.red.shalate.Model.CommentModel;
import com.shalate.red.shalate.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForShops extends Fragment {

    View view;
    RecyclerView shoprecy;
    private ArrayList<CommentModel> commentModels;

    public FragmentForShops() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_for_shops, container, false);
        setVar();
        setRecycleview();
        return view;
    }

    public void setVar() {
        shoprecy = view.findViewById(R.id.shoprecy);
        commentModels = new ArrayList<>();
    }

    public void setRecycleview() {
        ShopAdapter adapter1 = new ShopAdapter(getActivity(), commentModels);
        shoprecy.setAdapter(adapter1);
        shoprecy.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
        adapter1.notifyDataSetChanged();
    }
}

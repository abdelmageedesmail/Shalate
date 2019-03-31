package com.shalate.red.shalate.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shalate.red.shalate.Activity.OpenGallary;
import com.shalate.red.shalate.Adapter.FilterRecycleAdapter;
import com.shalate.red.shalate.Adapter.PhotoGallaryAdapter;
import com.shalate.red.shalate.Model.FilterModel;
import com.shalate.red.shalate.Model.PhotoGallay;
import com.shalate.red.shalate.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentImageDetailse extends Fragment {
    RecyclerView recycle_filter;
    private String lang;
    private RecyclerView photo;
    private StaggeredGridLayoutManager mLayoutManager;
    private ArrayList<PhotoGallay> images;
    ArrayList<FilterModel> filterModels;
    View view;
    public FragmentImageDetailse() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_fragment_image_detailse, container, false);
//        setVar();
        return view;

    }
//
//    public void setVar() {
//        filterModels=new ArrayList<>();
//        recycle_filter=view.findViewById(R.id.recycle_filter);
//        photo=view.findViewById(R.id.photo);
//        images=new ArrayList<>();
//        String[] IMAGES = new String[]{
//                // Heavy images
//                "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg",
//                "https://lh4.googleusercontent.com/--dq8niRp7W4/URquVgmXvgI/AAAAAAAAAbs/-gnuLQfNnBA/s1024/A%252520Song%252520of%252520Ice%252520and%252520Fire.jpg",
//                "https://lh5.googleusercontent.com/-7qZeDtRKFKc/URquWZT1gOI/AAAAAAAAAbs/hqWgteyNXsg/s1024/Another%252520Rockaway%252520Sunset.jpg",
//                "https://lh3.googleusercontent.com/--L0Km39l5J8/URquXHGcdNI/AAAAAAAAAbs/3ZrSJNrSomQ/s1024/Antelope%252520Butte.jpg",
//                "https://lh6.googleusercontent.com/-8HO-4vIFnlw/URquZnsFgtI/AAAAAAAAAbs/WT8jViTF7vw/s1024/Antelope%252520Hallway.jpg",
//                "https://lh4.googleusercontent.com/-WIuWgVcU3Qw/URqubRVcj4I/AAAAAAAAAbs/YvbwgGjwdIQ/s1024/Antelope%252520Walls.jpg",
//                "https://lh6.googleusercontent.com/-UBmLbPELvoQ/URqucCdv0kI/AAAAAAAAAbs/IdNhr2VQoQs/s1024/Apre%2525CC%252580s%252520la%252520Pluie.jpg",
//                "https://lh3.googleusercontent.com/-s-AFpvgSeew/URquc6dF-JI/AAAAAAAAAbs/Mt3xNGRUd68/s1024/Backlit%252520Cloud.jpg",
//                "https://lh5.googleusercontent.com/-bvmif9a9YOQ/URquea3heHI/AAAAAAAAAbs/rcr6wyeQtAo/s1024/Bee%252520and%252520Flower.jpg",
//                "https://lh5.googleusercontent.com/-n7mdm7I7FGs/URqueT_BT-I/AAAAAAAAAbs/9MYmXlmpSAo/s1024/Bonzai%252520Rock%252520Sunset.jpg",
//                "https://lh6.googleusercontent.com/-4CN4X4t0M1k/URqufPozWzI/AAAAAAAAAbs/8wK41lg1KPs/s1024/Caterpillar.jpg",
//                "https://lh3.googleusercontent.com/-rrFnVC8xQEg/URqufdrLBaI/AAAAAAAAAbs/s69WYy_fl1E/s1024/Chess.jpg",
//                "https://lh5.googleusercontent.com/-WVpRptWH8Yw/URqugh-QmDI/AAAAAAAAAbs/E-MgBgtlUWU/s1024/Chihuly.jpg",
//                "https://lh5.googleusercontent.com/-0BDXkYmckbo/URquhKFW84I/AAAAAAAAAbs/ogQtHCTk2JQ/s1024/Closed%252520Door.jpg",
//                "https://lh3.googleusercontent.com/-PyggXXZRykM/URquh-kVvoI/AAAAAAAAAbs/hFtDwhtrHHQ/s1024/Colorado%252520River%252520Sunset.jpg",
//                "https://lh3.googleusercontent.com/-ZAs4dNZtALc/URquikvOCWI/AAAAAAAAAbs/DXz4h3dll1Y/s1024/Colors%252520of%252520Autumn.jpg",
//                "https://lh4.googleusercontent.com/-GztnWEIiMz8/URqukVCU7bI/AAAAAAAAAbs/jo2Hjv6MZ6M/s1024/Countryside.jpg",
//                "https://lh4.googleusercontent.com/-GztnWEIiMz8/URqukVCU7bI/AAAAAAAAAbs/jo2Hjv6MZ6M/s1024/Countryside.jpg",
//                "https://ei.marketwatch.com/Multimedia/2018/02/06/Photos/ZH/MW-GD155_pain_20180206111428_ZH.jpg?uuid=ce6f6c62-0b58-11e8-b1d9-9c8e992d421e",
//                "https://ei.marketwatch.com/Multimedia/2018/02/06/Photos/ZH/MW-GD155_pain_20180206111428_ZH.jpg?uuid=ce6f6c62-0b58-11e8-b1d9-9c8e992d421e",
//                "https://lh4.googleusercontent.com/-bEg9EZ9QoiM/URquklz3FGI/AAAAAAAAAbs/UUuv8Ac2BaE/s1024/Death%252520Valley%252520-%252520Dunes.jpg"};
//
//        for (int i=0;i<IMAGES.length;i++){
//            PhotoGallay photoGallay=new PhotoGallay();
//            photoGallay.setPhoto(IMAGES[i]);
//            images.add(photoGallay);
//        }
//
//        photo.setHasFixedSize(true);
//        recycle_filter.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,true));
//        mLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
//        photo.setLayoutManager(mLayoutManager);
//        PhotoGallaryAdapter adapter= new PhotoGallaryAdapter(getActivity(), images);
//        photo.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//        adapter.setOnClickListener(new PhotoGallaryAdapter.OnItemClickListener() {
//            @Override
//            public void onclick(int position) {
//             //  getActivity(). getSupportFragmentManager().beginTransaction().replace(R.id.container, new DetailseOnImageClick()).commit();
//
//                Intent intent=new Intent(getActivity(), OpenGallary.class);
//                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
//
//
//            }
//        });
//        String name[]={"جميع الاقسام","سباك صحى","نجار","مبرمج","مهندس ديكور"};
//        int image[]={R.drawable.im3,R.drawable.im2,R.drawable.im1,R.drawable.im4,R.drawable.im5};
//        for (int i=0;i<name.length;i++){
//            FilterModel filterModel=new FilterModel();
//            filterModel.setName(name[i]);
////            filterModel.setImage(image[i]);
//            filterModels.add(filterModel);
//
//        }
//        FilterRecycleAdapter adapter1 = new FilterRecycleAdapter(getActivity(), filterModels,3);
//        recycle_filter.setAdapter(adapter1);
//        recycle_filter.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,true));
//        adapter1.notifyDataSetChanged();
//
//
//    }
}

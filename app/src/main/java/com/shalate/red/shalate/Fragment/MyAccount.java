package com.shalate.red.shalate.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.shalate.red.shalate.Activity.EditMehniProfileActivity;
import com.shalate.red.shalate.Activity.EditProfileActivity;
import com.shalate.red.shalate.Activity.PostDetails;
import com.shalate.red.shalate.Activity.ShowFollowers;
import com.shalate.red.shalate.Adapter.MyPostGallaryAdapter;
import com.shalate.red.shalate.Adapter.VideoAccountAdapter;
import com.shalate.red.shalate.Model.FilterModel;
import com.shalate.red.shalate.Model.PhotoGallay;
import com.shalate.red.shalate.Model.TrackModel;
import com.shalate.red.shalate.Model.VideoModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.CheckConnection;
import com.shalate.red.shalate.Utilities.GPSTracker;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccount extends Fragment implements View.OnClickListener {
    private String lang;
    private RecyclerView photo, rvVideos;
    private StaggeredGridLayoutManager mLayoutManager;
    private ArrayList<PhotoGallay> images;
    ArrayList<FilterModel> filterModels;
    View view;
    Button rvEdit;
    private String id;
    PrefrencesStorage storage;
    ProgressLoading loading;
    public ObservableField<String> name, imageUrl;
    TextView tvName, NumOfPosts, numOfFollowers, des, tvDescription, tvNoData, tvFollowers, numOfFollowering;
    FrameLayout btnEditProfile, btnEditToMehni;
    Button btnLoginRegister;
    ImageView ivProfile, prvileImage;
    ArrayList<JSONArray> arrays;
    private ArrayList<PhotoGallay> arrayList;
    private LinearLayout liFollowes;
    private Button btnFollowes;
    View liContainer, noInternet;
    private String postID;
    private LinearLayout liFolloweing;
    private LinearLayout liFollowing;
    ArrayList<VideoModel> arrayVideos;
    private NestedScrollView nestedScroll;
    Switch switchStatus;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    GPSTracker mGps;
    private String image;

    public MyAccount() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = new PrefrencesStorage(getActivity());
        loading = new ProgressLoading(getActivity());
        id = storage.getId();
        name = new ObservableField<>();
        imageUrl = new ObservableField<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (storage.getKey("account_type").equals("client")) {
            view = inflater.inflate(R.layout.client_account_fragment, container, false);
            setClientVar(view);

            if (CheckConnection.isOnline(getActivity())) {
                liContainer.setVisibility(View.VISIBLE);
                noInternet.setVisibility(View.GONE);
                getProfile();
            } else {
                noInternet.setVisibility(View.VISIBLE);
                liContainer.setVisibility(View.GONE);
            }

        } else if (storage.getKey("account_type").equals("provider")) {
            view = inflater.inflate(R.layout.fragment_fragment_my_account, container, false);
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            mGps = new GPSTracker(getActivity());
            setVar();

            if (CheckConnection.isOnline(getActivity())) {
                liContainer.setVisibility(View.VISIBLE);
                noInternet.setVisibility(View.GONE);
                getProfileData();
            } else {
                noInternet.setVisibility(View.VISIBLE);
                liContainer.setVisibility(View.GONE);
            }
        } else {
            view = inflater.inflate(R.layout.login_register_first, container, false);
            bind(view);
        }
        return view;

    }

    private void bind(View v) {
        btnLoginRegister = v.findViewById(R.id.btnLoginRegister);
        btnLoginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FragmentRegisterNewAccount.class));
            }
        });
    }

    private void setClientVar(View v) {
        tvName = v.findViewById(R.id.tvName);
        btnEditProfile = v.findViewById(R.id.btnEditProfile);
        ivProfile = v.findViewById(R.id.ivProfile);
        tvFollowers = v.findViewById(R.id.tvFollowers);
        liContainer = v.findViewById(R.id.liContainer);
        noInternet = v.findViewById(R.id.noInternet);
        btnEditToMehni = v.findViewById(R.id.btnEditToMehni);
        liFollowing = v.findViewById(R.id.liFollowing);
        btnEditToMehni.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);
        liFollowing.setOnClickListener(this);
    }


    private void getProfileData() {
        loading.showLoading();
        AndroidNetworking.get(Urls.getProfile + storage.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONObject data = response.getJSONObject("data");
                                JSONObject information = data.getJSONObject("information");
                                numOfFollowers.setText(information.getString("followers"));
                                numOfFollowering.setText(information.getString("follow"));
                                id = information.getString("id");
                                tvName.setText(information.getString("username"));
                                if (!information.getString("image").equals("null")) {
                                    image = information.getString("image");
                                    Picasso.with(getActivity()).load(information.getString("image")).into(prvileImage);
                                }

                                JSONObject job = information.getJSONObject("job");
                                JSONObject name = job.getJSONObject("name");
                                if (Locale.getDefault().getDisplayLanguage().equals("ar")) {
                                    des.setText(name.getString(name.getString("ar")));
                                } else {
                                    des.setText(name.getString("en"));
                                }
                                tvDescription.setText(information.getString("information"));
                                NumOfPosts.setText(information.getString("posts_count"));

                                JSONArray data1 = data.getJSONArray("post");
                                if (data1.length() > 0) {
                                    for (int x = 0; x < data1.length(); x++) {
                                        VideoModel model = new VideoModel();
                                        JSONObject jsonObject = data1.getJSONObject(x);
                                        JSONArray images = jsonObject.getJSONArray("images");
                                        postID = jsonObject.getString("id");

                                        String video = jsonObject.getString("video");
                                        if (!video.equals("")) {
                                            model.setVideoPath(video);
                                            model.setPostId(postID);

                                            arrayVideos.add(model);
                                        }
                                        for (int i = 0; i < images.length(); i++) {
                                            JSONObject obj = images.getJSONObject(i);
                                            PhotoGallay gallay = new PhotoGallay();
                                            gallay.setFrom("account");
                                            gallay.setPhoto(obj.getString("image"));
                                            gallay.setPostID(postID);
                                            gallay.setImageId(obj.getString("id"));
                                            arrayList.add(gallay);
                                        }
                                    }
                                    if (arrayVideos.size() > 0) {
                                        VideoAccountAdapter videoAccountAdapter = new VideoAccountAdapter(getActivity(), arrayVideos);
                                        rvVideos.setAdapter(videoAccountAdapter);
                                        rvVideos.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                        rvVideos.setNestedScrollingEnabled(false);
                                        videoAccountAdapter.notifyDataSetChanged();

                                    }
                                    if (arrayList.size() > 0) {
//                                        mLayoutManager = new GridLayoutManager(getActivity(), 2);
                                        photo.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                                        MyPostGallaryAdapter adapter = new MyPostGallaryAdapter(getActivity(), arrayList);
                                        photo.setAdapter(adapter);
                                        photo.setNestedScrollingEnabled(false);
                                        adapter.notifyDataSetChanged();
                                        adapter.setOnClickListener(new MyPostGallaryAdapter.OnItemClickListener() {
                                            @Override
                                            public void onclick(int position) {
                                                Intent intent = new Intent(getActivity(), PostDetails.class);
                                                intent.putExtra("postId", arrayList.get(position).getPostID());
                                                intent.putExtra("from", "post");
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        tvNoData.setVisibility(View.VISIBLE);
                                        tvNoData.setText(getString(R.string.no_result_found));
                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loading.cancelLoading();
                    }
                });
    }

    public void setVar() {
        filterModels = new ArrayList<>();
        arrays = new ArrayList<>();
        arrayList = new ArrayList<>();
        arrayVideos = new ArrayList<>();
        photo = view.findViewById(R.id.photo);
        tvName = view.findViewById(R.id.name);
        tvDescription = view.findViewById(R.id.tvDescription);
        des = view.findViewById(R.id.des);
        NumOfPosts = view.findViewById(R.id.NumOfPosts);
        numOfFollowers = view.findViewById(R.id.numOfFollowers);
        liFollowes = view.findViewById(R.id.liFollowes);
        rvEdit = view.findViewById(R.id.rvEdit);
        prvileImage = view.findViewById(R.id.prvileImage);
        tvNoData = view.findViewById(R.id.tvNoData);
        liContainer = view.findViewById(R.id.liContainer);
        noInternet = view.findViewById(R.id.noInternet);
        numOfFollowering = view.findViewById(R.id.numOfFollowering);
        liFolloweing = view.findViewById(R.id.liFolloweing);
        rvVideos = view.findViewById(R.id.rvVideos);
        nestedScroll = view.findViewById(R.id.nestedScroll);
        switchStatus = view.findViewById(R.id.switchStatus);
        nestedScroll.scrollTo(0, 0);

        switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    startTrack(1);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.onlineProfession))
                            .setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                } else {
                    startTrack(0);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.offlineProfession))
                            .setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
            }
        });
        nestedScroll.post(new Runnable() {
            @Override
            public void run() {
                nestedScroll.fling(0);
                nestedScroll.smoothScrollTo(0, 0);
            }
        });
        rvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditMehniProfileActivity.class));
            }
        });
        liFollowes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Intent(getActivity(), ShowFollowers.class);
                Intent intent = new Intent(getActivity(), ShowFollowers.class);
                intent.putExtra("from", "1");
                intent.putExtra("fromAc", "account");
                startActivity(intent);
            }
        });

        liFolloweing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ShowFollowers.class);
                i.putExtra("from", "2");
                i.putExtra("fromAc", "account");
                startActivity(i);
            }
        });
        images = new ArrayList<>();
        String name[] = {"جميع الاقسام", "سباك صحى", "نجار", "مبرمج", "مهندس ديكور"};
        int image[] = {R.drawable.im3, R.drawable.im2, R.drawable.im1, R.drawable.im4, R.drawable.im5};
        for (int i = 0; i < name.length; i++) {
            FilterModel filterModel = new FilterModel();
            filterModel.setName(name[i]);
            filterModels.add(filterModel);


        }

    }

    private void startTrack(int status) {
        String chatKey = storage.getId();
        DatabaseReference mRef = database.getReference("ProfessionTrack").child(chatKey);
        TrackModel model = new TrackModel();
        model.setFrom(chatKey);
        model.setLat("" + mGps.getLatitude());
        model.setLon("" + mGps.getLongitude());
        model.setStatus(status);
        model.setUserProfile("" + image);
        mRef.setValue(model);
    }

    private void getProfile() {
        loading.showLoading();
        String url = Urls.getProfile + id;
        AndroidNetworking.initialize(getActivity());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.get(url)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONObject data = response.getJSONObject("data");
                                JSONObject information = data.getJSONObject("information");
                                tvName.setText(information.getString("username"));
                                String follow = information.getString("follow");
                                tvFollowers.setText(follow);
//                                imageUrl.set(information.getString("image"));
                                image = information.getString("image");
                                Picasso.with(getActivity()).load(image).into(ivProfile);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loading.cancelLoading();
                    }
                });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEditProfile:
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
                break;
            case R.id.btnEditToMehni:
                startActivity(new Intent(getActivity(), EditMehniProfileActivity.class));
                break;
            case R.id.liFollowing:
                Intent i = new Intent(getActivity(), ShowFollowers.class);
                i.putExtra("from", "2");
                i.putExtra("fromAc", "account");
                startActivity(i);
                break;
        }
    }
}

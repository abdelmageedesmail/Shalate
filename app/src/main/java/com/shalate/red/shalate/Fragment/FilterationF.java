package com.shalate.red.shalate.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.shalate.red.shalate.Adapter.FilterRecycleAdapter;
import com.shalate.red.shalate.Adapter.RecycleListAdapter;
import com.shalate.red.shalate.Model.CommentModel;
import com.shalate.red.shalate.Model.FilterModel;
import com.shalate.red.shalate.Model.ImageModel;
import com.shalate.red.shalate.Model.ListModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.CheckConnection;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterationF extends Fragment {
    RecyclerView recycle_filter, recycle_list;
    ArrayList<ListModel> listModels;
    private View view;
    ArrayList<FilterModel> filterModels;
    RelativeLayout rel;
    ImageView ivWarning;
    private PrefrencesStorage storage;
    ArrayList<ImageModel> imageModels;
    ArrayList<CommentModel> arrayComments;
    private ProgressLoading loading;
    TextView tvNoData;
    ArrayList<String> arrayLikes;
    View noIntenet, liWarning;
    NestedScrollView nestedScroll;
    private RecycleListAdapter adapter;
    private boolean canLoadMore = true;
    private int current_page, last_page;
    private String url;
    private LinearLayout linearFocus;
    private String time;
    private String timeZone = "";
    private boolean isClicked;
    Runnable run;
    Handler handler = new Handler();


    public FilterationF() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_filter, container, false);
        setVar();

        setUpPaginate();
        if (isAdded()) {
            AndroidNetworking.initialize(getActivity());
            AndroidNetworking.setParserFactory(new JacksonParserFactory());
        }
        if (CheckConnection.isOnline(getActivity())) {
            noIntenet.setVisibility(View.GONE);
            url = Urls.homeApp;

            getHomePosts(url);
            run = new Runnable() {
                @Override
                public void run() {
                    getCategory();
                }
            };
            handler.postDelayed(run, 1000);

        } else {
            noIntenet.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isClicked = true;
    }

    public void setVar() {
        filterModels = new ArrayList<>();
        recycle_filter = view.findViewById(R.id.recycle_filter);
        recycle_list = view.findViewById(R.id.recycle_list);
        tvNoData = view.findViewById(R.id.tvNoData);
        noIntenet = view.findViewById(R.id.noIntenet);
        nestedScroll = view.findViewById(R.id.nestedScroll);
        liWarning = view.findViewById(R.id.liWarning);
        ivWarning = view.findViewById(R.id.ivWarning);
        linearFocus = view.findViewById(R.id.linearFocus);
        linearFocus.requestFocus();
        nestedScroll.scrollTo(0, 0);
        listModels = new ArrayList<>();
        storage = new PrefrencesStorage(getActivity());
        loading = new ProgressLoading(getActivity());
        Log.e("id", storage.getId());

        nestedScroll.post(new Runnable() {
            @Override
            public void run() {
                nestedScroll.fling(0);
                nestedScroll.smoothScrollTo(0, 0);
            }
        });
    }

    private void setUpPaginate() {
        adapter = new RecycleListAdapter(getActivity(), listModels, storage);
        recycle_list.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recycle_list, false);
        recycle_list.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycle_list.setLayoutManager(linearLayoutManager);
//        recycle_list.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
//            @Override
//            protected void loadMoreItems() {
//                if (canLoadMore) {
//                    int i = current_page + 1;
//                    url = Urls.homeApp + "?page=" + i;
//                    Log.e("getin", "" + current_page);
//                    adapter.removeLoadingFooter();
//                    canLoadMore = false;
//                    getHomePosts(url);
//
//                }
//            }
//
//            @Override
//            public int getTotalPageCount() {
//                return last_page;
//            }
//
//            @Override
//            public boolean isLastPage() {
//                return canLoadMore;
//            }
//
//            @Override
//            public boolean isLoading() {
//                return canLoadMore;
//            }
//        });

        RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (canLoadMore) {
                    int i = current_page + 1;
                    url = Urls.homeApp + "?page=" + i;
                    Log.e("getin", "" + current_page);
                    adapter.removeLoadingFooter();
                    canLoadMore = false;
                    getHomePosts(url);

                }
            }
        };

        recycle_list.addOnScrollListener(recyclerViewOnScrollListener);
//        recycle_list.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                if (page != 1 && canLoadMore) {
//                    int i = current_page + 1;
//                    url = Urls.homeApp + "?page=" + i;
//                    Log.e("getin", "" + current_page);
//                    adapter.removeLoadingFooter();
////                    canLoadMore = false;
//                    getHomePosts(url);
//                }
//            }
//        });
    }

    private void getHomePosts(String url) {
        loading.showLoading();
        AndroidNetworking.post(url)
                .addBodyParameter("user_id", storage.getId())
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
                                JSONObject posts = data.getJSONObject("posts");
                                JSONArray posts1 = posts.getJSONArray("posts");
                                for (int i = 0; i < posts1.length(); i++) {
                                    imageModels = new ArrayList<>();
                                    arrayComments = new ArrayList<>();
                                    arrayLikes = new ArrayList<>();
                                    JSONObject object = posts1.getJSONObject(i);
                                    ListModel listModel = new ListModel();
                                    listModel.setPostId(object.getString("id"));
                                    listModel.setInformation(object.getString("info"));
                                    String like_count = object.getString("like_count");
                                    listModel.setLat(object.getString("lat"));
                                    String created_at = object.getString("created_at");
                                    String[] s = created_at.split(" ");
                                    String[] split = s[1].split(":");
                                    String time1 = split[0] + ":" + split[1];
                                    Activity activity = getActivity();
                                    if (activity != null && isAdded()) {
                                        if (Integer.parseInt(split[0]) > 12) {
                                            timeZone = getString(R.string.timeZoneNight);
                                        } else {
                                            timeZone = getString(R.string.timeZoneMorning);
                                        }

                                    }
                                    final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date date = null;
                                    try {
                                        date = format.parse(created_at);
                                        final Date dateObj = sdf.parse(s[1]);
                                        System.out.println(dateObj);
                                        System.out.println(new SimpleDateFormat("K:mm").format(dateObj));

                                        time = new SimpleDateFormat("K:mm").format(dateObj);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String day = (String) DateFormat.format("dd", date); // 20
                                    String monthString = (String) DateFormat.format("MMM", date); // Jun
                                    String monthNumber = (String) DateFormat.format("MM", date); // 06
                                    String year = (String) DateFormat.format("yyyy", date); // 2013
                                    listModel.setCreated_at(day + " " + monthString + " " + time);
                                    listModel.setLng(object.getString("lng"));
                                    listModel.setLikeCount(like_count);
                                    listModel.setVideoUrl(object.getString("video"));
                                    JSONObject user_id = object.getJSONObject("user_id");
                                    listModel.setUserID(user_id.getString("id"));
                                    listModel.setName(user_id.getString("username"));
                                    listModel.setImageProfile(user_id.getString("image"));
                                    JSONObject job = user_id.getJSONObject("job");
                                    JSONObject name = job.getJSONObject("name");

                                    if (storage.getKey("language").equals("ar")) {
                                        listModel.setProfession(name.getString("ar"));
                                    } else {
                                        listModel.setProfession(name.getString("en"));
                                    }

                                    JSONArray images = object.getJSONArray("images");
                                    if (images.length() > 0) {
                                        for (int j = 0; j < images.length(); j++) {
                                            JSONObject obj = images.getJSONObject(j);
                                            ImageModel model = new ImageModel();
                                            model.setImage(obj.getString("image"));
                                            imageModels.add(model);
                                        }
                                        if (activity != null && isAdded()) {
                                            listModel.setPostImages(imageModels);
                                        }
                                    }
                                    JSONArray comment1 = object.getJSONArray("comment");
                                    if (comment1.length() > 0) {
                                        for (int x = 0; x < comment1.length(); x++) {
                                            JSONObject obj = comment1.getJSONObject(x);
                                            CommentModel model = new CommentModel();
                                            JSONObject user_id1 = obj.getJSONObject("user_id");
                                            model.setName(user_id1.getString("username"));
                                            model.setImage(user_id1.getString("image"));
                                            model.setUserId(user_id1.getString("id"));
                                            model.setComment(obj.getString("comment"));
                                            String created_atComment = obj.getString("created_at");

                                            String[] sComment = created_atComment.split(" ");
                                            String[] splitComment = sComment[1].split(":");
                                            String time2 = splitComment[0] + ":" + splitComment[1];
                                            activity = getActivity();
                                            if (activity != null && isAdded()) {
                                                if (Integer.parseInt(splitComment[0]) > 12) {
                                                    timeZone = getString(R.string.timeZoneNight);
                                                } else {
                                                    timeZone = getString(R.string.timeZoneMorning);
                                                }
                                            }
                                            final SimpleDateFormat sdfComment = new SimpleDateFormat("H:mm");
                                            SimpleDateFormat formatComment = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            Date dateComment = null;
                                            try {
                                                dateComment = format.parse(created_atComment);
                                                final Date dateObj = sdfComment.parse(sComment[1]);
                                                System.out.println(dateObj);
                                                System.out.println(new SimpleDateFormat("K:mm").format(dateObj));

                                                time2 = new SimpleDateFormat("K:mm").format(dateObj);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            String dayComment = (String) DateFormat.format("dd", date); // 20
                                            String monthStringComment = (String) DateFormat.format("MMM", date); // Jun
                                            String monthNumberComment = (String) DateFormat.format("MM", date); // 06
                                            String yearComment = (String) DateFormat.format("yyyy", date); // 2013
                                            listModel.setCreated_at(dayComment + " " + monthStringComment + " " + time2);
                                            arrayComments.add(model);
                                        }
                                        listModel.setArrayComments(arrayComments);
                                    } else {
                                        listModel.setArrayComments(arrayComments);
                                    }
                                    listModel.setLike(object.getString("likes"));

                                    JSONArray comment = object.getJSONArray("comment");
                                    if (comment.length() > 0) {
                                        listModel.setCommentCount("" + comment.length());
                                        Log.e("count", "" + comment.length());
                                        JSONObject jsonObject1 = comment.getJSONObject(0);
                                        JSONObject user_id1 = jsonObject1.getJSONObject("user_id");
                                        listModel.setCommentName1(user_id1.getString("username"));
                                        listModel.setComment1(jsonObject1.getString("comment"));
                                        if (comment.length() > 2) {
                                            JSONObject jsonObject2 = comment.getJSONObject(1);
                                            JSONObject user_id2 = jsonObject2.getJSONObject("user_id");
                                            listModel.setCommentName2(user_id2.getString("username"));
                                            listModel.setComment2(jsonObject2.getString("comment"));
                                        }
                                    } else {
                                        listModel.setCommentCount("");
                                        listModel.setCommentName1("");
                                        listModel.setComment1("");
                                        listModel.setCommentName2("");
                                        listModel.setComment2("");
                                    }

                                    listModels.add(listModel);
                                }

                                if (listModels.size() > 0) {
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Activity activity = getActivity();
                                    if (activity != null && isAdded()) {
                                        liWarning.setVisibility(View.VISIBLE);
                                        tvNoData.setVisibility(View.VISIBLE);
                                        tvNoData.setText(getString(R.string.followFirst));
                                    }

                                }
                                JSONObject paginate = posts.getJSONObject("paginate");
                                current_page = paginate.getInt("currentPage");
                                last_page = paginate.getInt("lastPage");
                                if (current_page == last_page) {
                                    canLoadMore = false;
                                } else {
                                    canLoadMore = true;
                                    isClicked = true;
//                                    adapter.addAll(listModels);
                                    adapter.addLoadingFooter();

                                }

                            } else {
                                liWarning.setVisibility(View.VISIBLE);
                                tvNoData.setVisibility(View.VISIBLE);
                                tvNoData.setText(getString(R.string.loginRegisterFirst));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loading.cancelLoading();
                        adapter.removeLoadingFooter();
                        Log.e("error", "" + anError.getMessage());
                    }
                });
    }

    private void getCategory() {
        AndroidNetworking.get(Urls.getCategory)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    FilterModel filterModel = new FilterModel();
                                    filterModel.setImage("http://sahalaatq8.com/" + object.getString("image"));
                                    JSONObject name = object.getJSONObject("name");
                                    filterModel.setCategory(object.getString("cat_type"));
                                    if (storage.getKey("language").equals("ar")) {
                                        filterModel.setName(name.getString("ar"));
                                    } else {
                                        filterModel.setName(name.getString("en"));
                                    }
                                    filterModel.setId(object.getString("id"));
                                    filterModels.add(filterModel);
                                }
                                FilterRecycleAdapter adapter1 = new FilterRecycleAdapter(getActivity(), filterModels, 1);
                                recycle_filter.setAdapter(adapter1);
                                recycle_filter.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));

                                recycle_filter.setFocusable(true);
                                adapter1.notifyDataSetChanged();
                                adapter1.setOnClickListener(new FilterRecycleAdapter.OnItemClickListener() {
                                    @Override
                                    public void onclick(int position) {
//                                        Bundle bundle = new Bundle();
//                                        bundle.putString("from", "filter");
//                                        bundle.putCharSequence("id", filterModels.get(position).getId());
//                                        bundle.putCharSequence("cat", filterModels.get(position).getCategory());
                                        Intent intent = new Intent(getActivity(), MapFragment.class);
                                        intent.putExtra("from","filter");
                                        intent.putExtra("id",filterModels.get(position).getId());
                                        intent.putExtra("cat",filterModels.get(position).getCategory());
                                        startActivity(intent);

//                                        MapFragment mapFragment = new MapFragment();
//                                        RecycleListAdapter.exoPlayer.clearVideoSurface();
//                                        mapFragment.setArguments(bundle);
//                                        if (isClicked) {
//                                            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, mapFragment).addToBackStack(null).commit();
//                                        }

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loading.cancelLoading();
                        Log.e("ERROR", "" + anError.getMessage());
                    }
                });
    }

}

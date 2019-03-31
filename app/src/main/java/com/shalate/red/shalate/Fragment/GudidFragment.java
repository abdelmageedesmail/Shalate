package com.shalate.red.shalate.Fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.shalate.red.shalate.Activity.MapsActivity;
import com.shalate.red.shalate.Adapter.ConmmentAdapter;
import com.shalate.red.shalate.Adapter.GuidAdaptr;
import com.shalate.red.shalate.Adapter.ServiceAdaptr;
import com.shalate.red.shalate.Model.CommentModel;
import com.shalate.red.shalate.Model.GuideModel;
import com.shalate.red.shalate.Model.ServiceModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.CheckConnection;
import com.shalate.red.shalate.Utilities.GPSTracker;
import com.shalate.red.shalate.Utilities.PermissionsEnabled;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class GudidFragment extends Fragment {
    RecyclerView guidRec;
    ArrayList<GuideModel> guideModels;

    View view, liContainer, noInternet;
    ProgressLoading loading;
    EditText title;
    private GPSTracker mGps;
    private GuidAdaptr adapter1;
    private EditText etComment;
    RelativeLayout rvAddComment, rvPhone;
    private RecyclerView recyclebestcoment;
    TextView name, des, tvAddress, tvPhone, tvWebSite;
    PrefrencesStorage storage;
    private JSONObject object;
    private ArrayList<ServiceModel> arryFilter;
    private ConmmentAdapter commentAdapter;
    private ArrayList<CommentModel> commentModels;
    private String url;
    private String serviceId;
    private PermissionsEnabled enabled;
    private RelativeLayout rvLocation;
    private String serviceLat, serviceLon;

    public GudidFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        enabled = new PermissionsEnabled(getActivity());
        enabled.enablePermission(1, PermissionsEnabled.LOCATION_REQUEST_CODE);
        view = inflater.inflate(R.layout.fragment_gudid, container, false);
        storage = new PrefrencesStorage(getActivity());
        setVar();
        if (CheckConnection.isOnline(getActivity())) {
            liContainer.setVisibility(View.VISIBLE);
            noInternet.setVisibility(View.GONE);
            getGuideData();
        } else {
            noInternet.setVisibility(View.VISIBLE);
            liContainer.setVisibility(View.GONE);
        }

        return view;
    }

    public void setVar() {
        guidRec = view.findViewById(R.id.guidRec);
        liContainer = view.findViewById(R.id.liContainer);
        noInternet = view.findViewById(R.id.noInternet);
        title = view.findViewById(R.id.title);
        mGps = new GPSTracker(getActivity());
        guideModels = new ArrayList<>();
        arryFilter = new ArrayList<>();
        commentModels = new ArrayList<>();
        loading = new ProgressLoading(getActivity());
        title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    filterGuide(title.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void getGuideData() {
        loading.showLoading();
        AndroidNetworking.initialize(getActivity());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.get(Urls.getGuide)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    GuideModel guideModel = new GuideModel();
                                    JSONObject name = object.getJSONObject("name");
                                    if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
                                        guideModel.setName(name.getString("ar"));
                                    } else {
                                        guideModel.setName(name.getString("en"));
                                    }
                                    guideModel.setId(object.getString("id"));
                                    guideModel.setImage(object.getString("image"));
                                    guideModels.add(guideModel);
                                }
                                adapter1 = new GuidAdaptr(getActivity(), guideModels);
                                guidRec.setAdapter(adapter1);
                                guidRec.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
                                adapter1.notifyDataSetChanged();
                                adapter1.setOnClickListener(new GuidAdaptr.OnItemClickListener() {
                                    @Override
                                    public void onclick(int position) {
//                                        Bundle bundle = new Bundle();
//                                        bundle.putCharSequence("id", guideModels.get(position).getId());
//                                        bundle.putString("from", "guide");
//                                        bundle.putString("photo", guideModels.get(position).getImage());
                                        Intent intent = new Intent(getActivity(), MapFragment.class);
                                        intent.putExtra("id",guideModels.get(position).getId());
                                        intent.putExtra("from", "guide");
                                        intent.putExtra("photo", guideModels.get(position).getImage());
                                        startActivity(intent);
//                                        MapFragment mapFragment = new MapFragment();
//                                        mapFragment.setArguments(bundle);
//                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, mapFragment).commit();

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
                        Log.e("error", "" + anError.getMessage());
                    }
                });
    }

    private void filterGuide(String text) {
        guideModels.clear();
        adapter1.notifyDataSetChanged();
        loading.showLoading();
        AndroidNetworking.initialize(getActivity());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(Urls.getGuide)
                .addBodyParameter("lat", "" + mGps.getLatitude())
                .addBodyParameter("lng", "" + mGps.getLongitude())
                .addBodyParameter("name", text)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.cancelLoading();
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    object = data.getJSONObject(i);
                                    ServiceModel model = new ServiceModel();
                                    model.setId(object.getString("id"));
                                    JSONObject name = object.getJSONObject("name");
                                    if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
                                        model.setName(name.getString("ar"));
                                    } else {
                                        model.setName(name.getString("en"));
                                    }
                                    model.setImage(object.getString("image"));
                                    model.setLat(object.getString("lat"));
                                    model.setLng(object.getString("lng"));
                                    JSONObject address = object.getJSONObject("address");
                                    if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
                                        model.setAddress(address.getString("ar"));
                                    } else {
                                        model.setAddress(address.getString("en"));
                                    }
                                    JSONObject information = object.getJSONObject("information");
                                    if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
                                        model.setInformation(information.getString("ar"));
                                    } else {
                                        model.setInformation(information.getString("en"));
                                    }
                                    model.setPhone(object.getString("phone"));
                                    model.setWebsite(object.getString("website"));
                                    JSONObject rate = object.getJSONObject("rate");
                                    model.setRate(rate.getString("rate"));
                                    JSONArray comments = rate.getJSONArray("comments");
                                    model.setComments(comments);
                                    arryFilter.add(model);
                                }
                                ServiceAdaptr adapter1 = new ServiceAdaptr(getActivity(), arryFilter);
                                guidRec.setAdapter(adapter1);
                                guidRec.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
                                adapter1.notifyDataSetChanged();
                                adapter1.setOnClickListener(new ServiceAdaptr.OnItemClickListener() {
                                    @Override
                                    public void onclick(int position) {
                                        setUpDialoge();
                                        serviceId = arryFilter.get(position).getId();
                                        name.setText(arryFilter.get(position).getName());
                                        des.setText(arryFilter.get(position).getInformation());
                                        tvAddress.setText(arryFilter.get(position).getAddress());
                                        tvPhone.setText(arryFilter.get(position).getPhone());
                                        tvWebSite.setText(arryFilter.get(position).getWebsite());
                                        serviceLat = arryFilter.get(position).getLat();
                                        serviceLon = arryFilter.get(position).getLng();
                                        try {
                                            commentModels.clear();
                                            for (int j = 0; j < arryFilter.get(position).getComments().length(); j++) {
                                                CommentModel model1 = new CommentModel();
                                                JSONObject obj = arryFilter.get(position).getComments().getJSONObject(j);
                                                JSONObject user_id = obj.getJSONObject("user_id");
                                                model1.setImage(user_id.getString("image"));
                                                model1.setComment(obj.getString("comment"));
                                                commentModels.add(model1);
                                            }
                                            commentAdapter = new ConmmentAdapter(getActivity(), commentModels);
                                            recyclebestcoment.setAdapter(commentAdapter);
                                            recyclebestcoment.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
                                            commentAdapter.notifyDataSetChanged();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
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
                        Log.e("error", "" + anError.getMessage());
                    }
                });
    }

    public void setUpDialoge() {
        DialogPlus dialog = DialogPlus.newDialog(getActivity())
                .setExpanded(true, 900)
                .setContentHolder(new ViewHolder(R.layout.dialogepluse))
                .setGravity(Gravity.BOTTOM)
                .setContentHeight(900)
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogPlus dialog) {
                        dialog.dismiss();
                    }
                })
                .setContentBackgroundResource(android.R.color.transparent)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                    }
                })
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        recyclebestcoment = (RecyclerView) dialog.findViewById(R.id.recyclebestcoment);
        name = (TextView) dialog.findViewById(R.id.name);
        des = (TextView) dialog.findViewById(R.id.des);
        tvAddress = (TextView) dialog.findViewById(R.id.tvAddress);
        tvPhone = (TextView) dialog.findViewById(R.id.tvPhone);
        tvWebSite = (TextView) dialog.findViewById(R.id.tvWebSite);
        rvAddComment = (RelativeLayout) dialog.findViewById(R.id.rvAddComment);
        rvPhone = (RelativeLayout) dialog.findViewById(R.id.rvPhone);
        rvLocation = (RelativeLayout) dialog.findViewById(R.id.rvLocation);

        rvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("lat", "" + serviceLat);
                intent.putExtra("lon", "" + serviceLon);
                startActivity(intent);
            }
        });
        rvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + tvPhone.getText().toString()));

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                startActivity(intent);
            }
        });

        tvWebSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                if (tvWebSite.getText().toString().startsWith("http") || tvWebSite.getText().toString().startsWith("https")) {
                    url = tvWebSite.getText().toString();
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.errorWebSite), Toast.LENGTH_SHORT).show();
                }

            }
        });
        etComment = (EditText) dialog.findViewById(R.id.etComment);
//        ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);
        rvAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!storage.isFirstTimeLogin()) {
                    Toast.makeText(getActivity(), getString(R.string.loginRegisterFirst), Toast.LENGTH_SHORT).show();
                } else {
                    if (etComment.getText().toString().isEmpty()) {
                        etComment.setError(getString(R.string.empty));
                    } else {
                        addRate(etComment.getText().toString());
                    }
                }
            }
        });
        dialog.show();


    }

    private void addRate(String comment) {
        loading.showLoading();
        AndroidNetworking.initialize(getActivity());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(Urls.addComment)
                .addBodyParameter("serv_id", serviceId)
                .addBodyParameter("user_id", storage.getId())
                .addBodyParameter("rate", "2")
                .addBodyParameter("comment", comment)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                Toast.makeText(getActivity(), getString(R.string.commentAddedSuccessfully), Toast.LENGTH_SHORT).show();
                                etComment.setText("");
                                adapter1.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.youAddedRateBefore), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loading.cancelLoading();
                        Log.e("error", "" + anError.getMessage());
                    }
                });
    }
}

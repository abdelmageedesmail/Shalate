package com.shalate.red.shalate.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.shalate.red.shalate.Adapter.NotiAdapter;
import com.shalate.red.shalate.Model.CommentModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.helperClasses.DateTimeFormating;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotiFication extends Fragment {

    View view;
    RecyclerView notification;
    private ArrayList<CommentModel> commentModels;
    private PrefrencesStorage storage;
    LinearLayout liNoData;
    private Button btnLoginRegister;

    public FragmentNotiFication() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        storage = new PrefrencesStorage(getActivity());
        if (storage.getId().equals("null")) {
            view = inflater.inflate(R.layout.login_register_first, container, false);
            bind(view);
        } else {
            view = inflater.inflate(R.layout.fragment_fragment_noti_fication, container, false);
            setVar();
        }
        setRecycleview();


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


    public void setVar() {
        notification = view.findViewById(R.id.notification);
        liNoData = view.findViewById(R.id.liNoData);
        commentModels = new ArrayList<>();
    }

    public void setRecycleview() {
        getNotification();
    }


    private void getNotification() {
        AndroidNetworking.initialize(getActivity());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.get(Urls.getNotification + storage.getId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", "" + response);
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject = data.getJSONObject(i);
                                    CommentModel commentModel = new CommentModel();
                                    JSONObject info = jsonObject.getJSONObject("info");
                                    if (Locale.getDefault().getDisplayLanguage().equals("ar")) {
                                        commentModel.setComment(info.getString("ar"));
                                    } else {
                                        commentModel.setComment(info.getString("en"));
                                    }
                                    JSONObject user_info = jsonObject.getJSONObject("user_info");
                                    commentModel.setName(user_info.getString("username"));
                                    commentModel.setImage(user_info.getString("image"));
                                    commentModel.setUserId(jsonObject.getString("id"));
                                    String created_at = jsonObject.getString("created_at");
                                    String friendlyTime = com.shalate.red.shalate.helperClasses.DateTimeFormating.getFriendlyTime(getDate(created_at), getActivity());
                                    commentModel.setTime(friendlyTime);
                                    commentModels.add(commentModel);
                                }
                                if (commentModels.size() > 0) {
                                    NotiAdapter adapter1 = new NotiAdapter(getActivity(), commentModels);
                                    notification.setAdapter(adapter1);
                                    notification.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
                                    adapter1.notifyDataSetChanged();
                                } else {
                                    liNoData.setVisibility(View.VISIBLE);
                                }
                            } else {
                                liNoData.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error", "" + anError.getMessage());
                    }
                });
    }

    private Long getDate(String created_at) {
        return DateTimeFormating.getDateFromString(created_at).getTime();
    }
}

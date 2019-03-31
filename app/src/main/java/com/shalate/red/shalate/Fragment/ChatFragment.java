package com.shalate.red.shalate.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.shalate.red.shalate.Adapter.ChatAdapterFRAGMENT;
import com.shalate.red.shalate.Model.CommentModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.CheckConnection;
import com.shalate.red.shalate.Utilities.PermissionsEnabled;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    View view, noInternet;
    RecyclerView chat;
    PrefrencesStorage storage;
    private ArrayList<CommentModel> commentModels;
    TextView tvNoData;
    ProgressLoading loading;
    LinearLayout liWarning;
    PermissionsEnabled enabled;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        enabled = new PermissionsEnabled(getActivity());
        enabled.enablePermission(2, PermissionsEnabled.READ_AND_WRITE_EXTERNAL_REQUEST_CODE);
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        setVar();
        if (CheckConnection.isOnline(getActivity())) {
            noInternet.setVisibility(View.GONE);
            getChat();
        } else {
            noInternet.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void setVar() {
        chat = view.findViewById(R.id.chat);
        tvNoData = view.findViewById(R.id.tvNoData);
        noInternet = view.findViewById(R.id.noInternet);
        liWarning = view.findViewById(R.id.liWarning);
        storage = new PrefrencesStorage(getActivity());
        commentModels = new ArrayList<>();
        loading = new ProgressLoading(getActivity());
    }


    private void getChat() {
        loading.showLoading();
        String url = Urls.getChatList + storage.getId() + "/chat";
        AndroidNetworking.initialize(getActivity());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.get(url)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("res", "" + response);
                        try {
                            loading.cancelLoading();
                            String status = response.getString("status");
                            if (status.equals("200")) {
                                JSONArray data = response.getJSONArray("data");
                                if (data.length() > 0) {
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject jsonObject = data.getJSONObject(i);
                                        CommentModel model = new CommentModel();
                                        JSONObject sender = jsonObject.getJSONObject("sender");
                                        if (!storage.getId().equals(sender.getString("id"))) {
                                            model.setUserId(sender.getString("id"));
                                            model.setName(sender.getString("username"));
                                            model.setImage(sender.getString("image"));
                                        }
                                        JSONObject receiver = jsonObject.getJSONObject("receiver");
                                        if (!storage.getId().equals(receiver.getString("id"))) {
                                            model.setName(receiver.getString("username"));
                                            model.setImage(receiver.getString("image"));
                                            model.setUserId(receiver.getString("id"));
                                        }
                                        model.setComment(jsonObject.getString("last_mesage"));
                                        commentModels.add(model);
                                    }
                                    ChatAdapterFRAGMENT adapter1 = new ChatAdapterFRAGMENT(getActivity(), commentModels);
                                    chat.setAdapter(adapter1);
                                    chat.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
                                    adapter1.notifyDataSetChanged();
                                } else {
                                    liWarning.setVisibility(View.VISIBLE);
                                    tvNoData.setVisibility(View.VISIBLE);
                                    tvNoData.setText(getString(R.string.noCahtResult));
                                }

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

package com.shalate.red.shalate.Activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.shalate.red.shalate.Adapter.FilterRecycleAdapter;
import com.shalate.red.shalate.Adapter.ItemDecorationAlbumColumns;
import com.shalate.red.shalate.Model.FilterModel;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class CategoryActivity extends AppCompatActivity {
    RecyclerView rvCategories;
    ArrayList<FilterModel> arrayList;
    private DividerItemDecoration mDividerItemDecoration;
    ProgressLoading loading;
    public static boolean fromCategory;
    public static String name, id, type;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        rvCategories = findViewById(R.id.rvCategories);
        arrayList = new ArrayList<>();
        loading = new ProgressLoading(this);
        getCategory();
    }

    private void getCategory() {
        loading.showLoading();
        AndroidNetworking.get(Urls.getCategory)
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
                                    FilterModel filterModel = new FilterModel();
                                    filterModel.setImage("http://sahalaatq8.com/" + object.getString("image"));
                                    JSONObject name = object.getJSONObject("name");
                                    filterModel.setCategory(object.getString("cat_type"));
                                    if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
                                        filterModel.setName(name.getString("ar"));
                                    } else {
                                        filterModel.setName(name.getString("en"));
                                    }
                                    filterModel.setId(object.getString("id"));
                                    arrayList.add(filterModel);
                                }
                                FilterRecycleAdapter adapter1 = new FilterRecycleAdapter(CategoryActivity.this, arrayList, 2);
                                rvCategories.setAdapter(adapter1);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(CategoryActivity.this, 3);
                                rvCategories.setLayoutManager(gridLayoutManager);
                                rvCategories.addItemDecoration(new ItemDecorationAlbumColumns(CategoryActivity.this, 2));
                                rvCategories.setFocusable(true);
                                adapter1.notifyDataSetChanged();
                                adapter1.setOnClickListener(new FilterRecycleAdapter.OnItemClickListener() {
                                    @Override
                                    public void onclick(int position) {
                                        fromCategory = true;
                                        id = arrayList.get(position).getId();
                                        name = arrayList.get(position).getName();
                                        type = arrayList.get(position).getCategory();
                                        onBackPressed();
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

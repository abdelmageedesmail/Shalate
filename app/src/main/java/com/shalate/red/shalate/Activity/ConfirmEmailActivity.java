package com.shalate.red.shalate.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.databinding.ActivityConfirmEmailBinding;
import com.shalate.red.shalate.helperClasses.MyAmimatorClass;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfirmEmailActivity extends AppCompatActivity {

    ActivityConfirmEmailBinding binding;
    ProgressLoading loading;
    private String email;
    EditText et1, et2, et3, et4;
    Button btnConfirm;
    private String phone;
    private String code;
    private String tele_code;
    PrefrencesStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slidr.attach(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_email);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        storage = new PrefrencesStorage(ConfirmEmailActivity.this);
        bind();
        setUpChangeListener();
    }

    private void bind() {
        loading = new ProgressLoading(this);
        Intent i = getIntent();
        if (i.getExtras().getString("activeCode") != null) {
            code = i.getExtras().getString("activeCode");
            phone = i.getExtras().getString("phone");
            tele_code = i.getExtras().getString("tele_code");
            storage.storeKey("phone", phone);
            storage.storeKey("activeCode", code);
            storage.storeKey("teleCode", tele_code);
        }
        Log.e("Acode", storage.getKey("activeCode"));

        et1 = binding.et1;
        et2 = binding.et2;
        et3 = binding.et3;
        et4 = binding.et4;
       ImageView  ivBack=binding.ivBack;
        btnConfirm = binding.btnConfirm;

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

       btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpValidation();
            }
        });


    }


    private void setUpValidation() {
        if (et1.getText().toString().isEmpty() || et1.getText().toString().equals("")) {
            MyAmimatorClass.ShakeAnimator(this, et1);
        } else if (et2.getText().toString().isEmpty() || et2.getText().toString().equals("")) {
            MyAmimatorClass.ShakeAnimator(this, et2);
        } else if (et3.getText().toString().isEmpty() || et3.getText().toString().equals("")) {
            MyAmimatorClass.ShakeAnimator(this, et3);
        } else if (et4.getText().toString().isEmpty() || et4.getText().toString().equals("")) {
            MyAmimatorClass.ShakeAnimator(this, et4);
        } else {

            Log.e("codeFrom", "" + et1.getText().toString() + et2.getText().toString() + et3.getText().toString() + et4.getText().toString() + storage.getKey("activeCode"));
            if ((et1.getText().toString() + et2.getText().toString() + et3.getText().toString() + et4.getText().toString()).equals(storage.getKey("activeCode"))) {
                confirmResendCode(et1.getText().toString() + et2.getText().toString() + et3.getText().toString() + et4.getText().toString());
            } else {
                Toast.makeText(this, getString(R.string.errorCode), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void confirmResendCode(String code) {
        loading.showLoading();
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(Urls.openContact)
                .addBodyParameter("phone", storage.getKey("phone"))
                .addBodyParameter("active_code", code)
                .addBodyParameter("tele_code", storage.getKey("teleCode"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.cancelLoading();
                            String responseCode = response.getString("status");
                            if (responseCode.equals("1")) {
                                JSONArray data = response.getJSONArray("data");
                                JSONObject jsonObject = data.getJSONObject(0);
                                storage.storeId(jsonObject.getString("id"));
                                storage.storeKey("name", jsonObject.getString("username"));
                                storage.storeKey("email", jsonObject.getString("email"));
                                storage.storeKey("phone", jsonObject.getString("phone"));
                                storage.storeKey("account_type", jsonObject.getString("account_type"));
                                storage.storeKey("oldLat", jsonObject.getString("lat"));
                                storage.storeKey("oldLon", jsonObject.getString("lng"));
                                storage.storeKey("image", jsonObject.getString("image"));
                                storage.storeKey("isEnterCode", "0");
                                storage.setFirstTimeLogin(true);
                                Intent intent = new Intent(ConfirmEmailActivity.this, HomePage.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ConfirmEmailActivity.this, getString(R.string.errorCode), Toast.LENGTH_SHORT).show();
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

    private void setUpChangeListener() {
        et1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (et1.getText().toString().length() == 1) {
                    et2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        et2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (et2.getText().toString().length() == 1) {
                    et3.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        et3.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (et3.getText().toString().length() == 1) {
                    et4.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
    }
}

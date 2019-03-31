package com.shalate.red.shalate.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.shalate.red.shalate.Fragment.FragmentRegisterNewAccount;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class NewPasswordActivity extends AppCompatActivity {

    EditText etNewPass, etConfirmNewPass;
    Button btnConfirm;
    String code, phone;
    ProgressLoading loading;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Slidr.attach(this);
        bind();
        Intent i = getIntent();
        code = i.getExtras().getString("code");
        phone = i.getExtras().getString("phone");
        loading = new ProgressLoading(this);
    }

    private void bind() {
        etNewPass = findViewById(R.id.etNewPass);
        etConfirmNewPass = findViewById(R.id.etConfirmNewPass);
        btnConfirm = findViewById(R.id.btnConfirm);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etNewPass.getText().toString().isEmpty()) {
                    etNewPass.setError(getString(R.string.empty));
                } else if (etConfirmNewPass.getText().toString().isEmpty()) {
                    etConfirmNewPass.setError(getString(R.string.empty));
                } else if (!etNewPass.getText().toString().equals(etConfirmNewPass.getText().toString())) {
                    Toast.makeText(NewPasswordActivity.this, getString(R.string.passwordNotMatch), Toast.LENGTH_SHORT).show();
                } else {
                    newPass();
                }
            }
        });
    }

    private void newPass() {
        loading.showLoading();
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(Urls.newPassword)
                .addBodyParameter("phone", phone)
                .addBodyParameter("active_code", code)
                .addBodyParameter("password", etNewPass.getText().toString())

                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.cancelLoading();
                        Log.e("response", "" + response);
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                startActivity(new Intent(NewPasswordActivity.this, FragmentRegisterNewAccount.class));
                                finish();
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

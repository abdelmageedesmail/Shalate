package com.shalate.red.shalate.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.hbb20.CountryCodePicker;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.CountryPickerListener;
import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.ProgressLoading;
import com.shalate.red.shalate.internet.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetPassword extends AppCompatActivity {

    Button btnSendCode;
    CountryCodePicker ccp;
    EditText edtPhoneNumber;
    FrameLayout frCountry;
    TextView tvCode, code;
    ProgressLoading loading;
    private ImageView ivBack;
    private TextView cCode;
    private String ACode, replaceCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Slidr.attach(this);
        btnSendCode = findViewById(R.id.btnSendCode);
        frCountry = findViewById(R.id.frCountry);
        edtPhoneNumber = findViewById(R.id.etEmail);
        tvCode = findViewById(R.id.tvCode);
        cCode = findViewById(R.id.code);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        frCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here
                        tvCode.setText(name);
                        ACode = dialCode;
                        replaceCode = ACode.replace("+", "00");
                        cCode.setText(dialCode);
                        picker.dismiss();

                    }
                });
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        loading = new ProgressLoading(this);
        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cCode.getText().toString().isEmpty()) {
                    Toast.makeText(ForgetPassword.this, getString(R.string.pleaseSelectCountryCode), Toast.LENGTH_SHORT).show();
                } else if (edtPhoneNumber.getText().toString().isEmpty()) {
                    edtPhoneNumber.setError(getString(R.string.empty));
                } else {
                    forgetPassword();
                }

            }
        });
    }

    private void forgetPassword() {
        if (replaceCode == null) {
            replaceCode = cCode.getText().toString().replace("+", "00");
        }
        loading.showLoading();
        AndroidNetworking.initialize(this);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        AndroidNetworking.post(Urls.forgetPassword)
                .addBodyParameter("phone", edtPhoneNumber.getText().toString())
                .addBodyParameter("tele_code", replaceCode)
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
                                String value = data.getString("value");
                                Log.e("code", value);
                                Intent intent = new Intent(ForgetPassword.this, ConfirmationForgetActivity.class);
                                intent.putExtra("code", value);
                                intent.putExtra("phone", edtPhoneNumber.getText().toString());
                                startActivity(intent);
                            } else {
                                Toast.makeText(ForgetPassword.this, getString(R.string.errorPhone), Toast.LENGTH_SHORT).show();
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

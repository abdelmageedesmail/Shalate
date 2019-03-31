package com.shalate.red.shalate.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.R;

public class ConfirmationForgetActivity extends AppCompatActivity {

    EditText etCode;
    Button btnConfirm;
    private String code;
    private String phone;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_forget);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Slidr.attach(this);
        etCode = findViewById(R.id.etCode);
        btnConfirm = findViewById(R.id.btnConfirm);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Intent i = getIntent();
        code = i.getExtras().getString("code");
        phone = i.getExtras().getString("phone");
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCode.getText().toString().equals(code)) {
                    Intent intent = new Intent(ConfirmationForgetActivity.this, NewPasswordActivity.class);
                    intent.putExtra("code", code);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                } else {
                    Toast.makeText(ConfirmationForgetActivity.this, getString(R.string.errorCode), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

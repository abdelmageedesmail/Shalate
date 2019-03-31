package com.shalate.red.shalate.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;
import com.shalate.red.shalate.Fragment.FragmentRegisterNewAccount;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Splash;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.databinding.ActivitySettingBinding;
import com.shalate.red.shalate.interfaces.ClickInterface;
import com.shalate.red.shalate.viewModel.MoreSettingViewModel;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity implements ClickInterface {

    ActivitySettingBinding binding;
    TextView tvLogout;
    PrefrencesStorage storage;
    ImageView ivLogout, ivBack;
    private Dialog d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        Slidr.attach(this);
        tvLogout = binding.tvLogout;
        ivLogout = binding.ivLogout;
        ivBack = binding.ivBack;
        binding.setMore(new MoreSettingViewModel());
        binding.setEventClick((ClickInterface) this);
        storage = new PrefrencesStorage(this);
        if (!storage.isFirstTimeLogin()) {
            ivLogout.setImageResource(0);
            ivLogout.setImageResource(R.drawable.ic_login);
            tvLogout.setText(getString(R.string.login));
        } else {
            ivLogout.setImageResource(0);
            ivLogout.setImageResource(R.drawable.logout);
            tvLogout.setText(getString(R.string.logout));
        }
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMultiViewClick(int val) {
        switch (val) {
            case 1:
                chooseLanguage();
                break;
            case 2:
                Intent intent = new Intent(this, PagesActivity.class);
                intent.putExtra("title", "term");
                startActivity(intent);
                break;
            case 3:
                if (!storage.isFirstTimeLogin()) {
                    startActivity(new Intent(this, FragmentRegisterNewAccount.class));
                } else {
                    storage.setFirstTimeLogin(false);
                    storage.clearSharedPref();
                    startActivity(new Intent(this, FragmentRegisterNewAccount.class));
                }
                break;
            case 4:
                Intent i = new Intent(this, PagesActivity.class);
                i.putExtra("title", "about");
                startActivity(i);
                break;
            case 6:
                startActivity(new Intent(this, ContactUs.class));
                break;

        }
    }


    private void chooseLanguage() {
        d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.language_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = d.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        TextView title = (TextView) d.findViewById(R.id.title);
        RadioButton arabic = (RadioButton) d.findViewById(R.id.arabic);
        RadioButton english = (RadioButton) d.findViewById(R.id.english);
        Button done = (Button) d.findViewById(R.id.done);

        arabic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    storage.storeKey("language", "ar");
                    Locale locale = new Locale("ar");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                }
            }
        });

        english.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                storage.storeKey("language", "en");
                Locale locale = new Locale("en");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, Splash.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                d.dismiss();
            }
        });
        d.show();

    }

    @Override
    public void onSingleViewClick() {

    }
}

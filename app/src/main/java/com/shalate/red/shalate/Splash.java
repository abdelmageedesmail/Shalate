package com.shalate.red.shalate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.shalate.red.shalate.Activity.ConfirmEmailActivity;
import com.shalate.red.shalate.Activity.HomeActivity;
import com.shalate.red.shalate.Activity.HomePage;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;
import com.shalate.red.shalate.notification.FCMRegistrationService;

import java.util.Locale;

public class Splash extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private PrefrencesStorage storage;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startService(new Intent(this, FCMRegistrationService.class));
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                storage = new PrefrencesStorage(Splash.this);
                String language = storage.getKey("language");
                if (language.equals("ar")) {
                    Locale locale = new Locale("ar");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());
                } else if (language.equals("en")) {
                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());
                }
                if (storage.getKey("isEnterCode").equals("1")) {
                    intent = new Intent(Splash.this, ConfirmEmailActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(Splash.this, HomeActivity.class);
                    startActivity(intent);
                }


                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}


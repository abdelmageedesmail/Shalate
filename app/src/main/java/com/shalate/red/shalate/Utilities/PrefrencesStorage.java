package com.shalate.red.shalate.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by abdelmageed on 05/10/17.
 */

public class PrefrencesStorage {

    Context context;
    SharedPreferences sharedPreferences, sharedPreferences1;
    String key = "userData";
    String launchKey = "launchKey ";
    private SharedPreferences.Editor edit, edit1;
    private String id = "id";
    private String token = "apiToken";
    private String userExist = "userExist";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_FIRST_TIME_LAUNCH_LANG = "IsFirstTimeLaunchLang";


    public PrefrencesStorage(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        sharedPreferences1 = context.getSharedPreferences(launchKey, Context.MODE_PRIVATE);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        edit1 = sharedPreferences1.edit();
        edit1.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        edit1.apply();
    }

    public void setFirstTimeLaunchLang(boolean isFirstTime) {
        edit1 = sharedPreferences1.edit();
        edit1.putBoolean(IS_FIRST_TIME_LAUNCH_LANG, isFirstTime);
        edit1.apply();
    }

    public void setFirstTimeLogin(boolean isExist) {
        edit1 = sharedPreferences1.edit();
        edit1.putBoolean("userExist", isExist).apply();
    }

    public boolean isFirstTimeLogin() {
        return sharedPreferences1.getBoolean("userExist", false);
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences1.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean isFirstTimeLaunchLang() {
        return sharedPreferences1.getBoolean(IS_FIRST_TIME_LAUNCH_LANG, true);
    }

    public void storeId(String text) {
        edit = sharedPreferences.edit();
        edit.putString(id, text);
        edit.apply();
    }

    public String getId() {
        sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(id, "null");
        return userId;
    }


    public void storeToken(String text) {
        edit = sharedPreferences.edit();
        edit.putString(token, text);
        edit.apply();
    }

    public String getToken() {
        sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(token, "null");
        return value;
    }


    public void setUserLogOut() {
        edit = sharedPreferences.edit();
        edit.putBoolean(id, false);
        edit.apply();
    }

    public boolean getLogoutState() {
        sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        boolean exist = sharedPreferences.getBoolean(userExist, false);
        return exist;
    }

    public void storeKey(String key, String text) {
        edit = sharedPreferences.edit();
        edit.putString(key, text);
        edit.apply();
    }

    public String getKey(String textKey) {
        sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(textKey, "null");
        return value;
    }

    public void clearSharedPref() {
        edit = sharedPreferences.edit();
        edit.clear().apply();

    }
}

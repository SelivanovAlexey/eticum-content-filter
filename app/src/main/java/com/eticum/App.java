package com.eticum;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class App extends Application {
    private static Application sApplication;
    private static SharedPreferences sharedPreferences;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        sharedPreferences = App.getContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        //TODO: remove before release
        sharedPreferences.edit().clear().apply();
    }

    //TODO: ========== IMPORTANT ==========
    //TODO: initial setup screens
    //TODO: check reboot
    //TODO: logout
    //TODO: internationalization

    //TODO: certificates on google search is not propagated
    //TODO: make preferences store secure
    //TODO: on what depends google music/films services


    //TODO: ========== JUST BUGS ==========
    //TODO: make change of label and topbar when vpn state changed
    //TODO: change error description on different blocking cases - block by categories or block by lists
    //TODO: add scrolling for not only main activity

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static SharedPreferences getPreferences(){
        return sharedPreferences;
    }
}

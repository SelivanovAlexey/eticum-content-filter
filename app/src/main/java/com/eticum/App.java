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
    //TODO: internationalization
    //TODO: launcher icons (mipmap and drawable managment)
    // TODO: explanatory messages with logout, accessibility serach etc.

    //TODO: change error description on different blocking cases - block by categories or block by lists
    //TODO: make preferences store secure

    //TODO: ========== LOW PRIORITY ==========
    //TODO: certificates on google search is not propagated
    //TODO: on what depends google music/films services
    //TODO: add scrolling for not only main activity

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static SharedPreferences getPreferences(){
        return sharedPreferences;
    }
}

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
        // TODO: remove before release
//        sharedPreferences.edit().clear().apply();
    }

    //TODO: add scrolling for not only main activity

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static SharedPreferences getPreferences(){
        return sharedPreferences;
    }
}

package com.eticum.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.eticum.R;

import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class CommonUtils {
    public static void startIntentActivity(Context ctx, String action) {
        startIntentActivity(ctx, action, 0);
    }

    public static void startIntentActivity(Context ctx, String action, Integer flags) {
        val intent = new Intent(action);
        intent.addFlags(flags);
        ctx.startActivity(intent);
    }

    public static Intent prepareIntent( String action) {
        return new Intent(action);
    }

    public static void startIntentActivity(Context ctx, Class<? extends Context> activityClass) {
        startIntentActivity(ctx, activityClass, 0);
    }

    public static void startIntentActivity(Context ctx, Class<? extends Context> activityClass, Integer flags) {
        val intent = new Intent(ctx, activityClass);
        intent.addFlags(flags);
        ctx.startActivity(intent);
    }

    public static void showLoading(View progressBarView, View root, Activity activity) {
        progressBarView.setVisibility(View.VISIBLE);
        root.setForeground(new ColorDrawable(ContextCompat.getColor(activity, R.color.transparent)));
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void hideLoading(View progressBarView, View root, Activity activity) {
        progressBarView.setVisibility(View.GONE);
        root.setForeground(null);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void runAsync(Activity activity, Runnable execute, Runnable postExecute) {
        new Thread(() -> {
            execute.run();
            activity.runOnUiThread(postExecute);
        }).start();
    }

    public static void runAsync(Runnable execute) {
        new Thread(execute).start();
    }
}

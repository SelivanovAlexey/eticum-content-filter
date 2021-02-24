package com.eticum.utils;

import android.content.Context;
import android.content.Intent;

import lombok.val;

public class CommonUtils {
    public static void startIntentActivity(Context ctx, String action){
        startIntentActivity(ctx, action, 0);
    }

    public static void startIntentActivity(Context ctx, String action, Integer flags){
        val intent = new Intent(action);
        intent.addFlags(flags);
        ctx.startActivity(intent);
    }

    public static void startIntentActivity(Context ctx, Class<? extends Context> activityClass){
        startIntentActivity(ctx, activityClass, 0);
    }

    public static void startIntentActivity(Context ctx, Class<? extends Context> activityClass, Integer flags){
        val intent = new Intent(ctx, activityClass);
        intent.addFlags(flags);
        ctx.startActivity(intent);
    }
}

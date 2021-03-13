package com.eticum.services;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.eticum.utils.UserControlUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EticumUserControlService extends AccessibilityService {

    private static EticumUserControlService instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        UserControlUtils.onAccessEvent(this, event.getSource());
    }

    @Override
    public void onInterrupt() {
    }

    public static void stop() {
        if (instance !=null) instance.stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}

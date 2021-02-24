package com.eticum.services;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.eticum.utils.UserControlUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EticumUserControlService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        UserControlUtils.onAccessEvent(this, event.getSource());
    }

    @Override
    public void onInterrupt() { }
}

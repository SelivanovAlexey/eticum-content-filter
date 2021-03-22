package com.eticum.utils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.accessibility.AccessibilityNodeInfo;

import com.eticum.Constants;
import com.eticum.activities.LockActivity;
import com.eticum.receivers.RemoveAppAdminReceiver;
import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

@Slf4j
@UtilityClass
public class UserControlUtils {
    public static Set<Supplier<? extends AccessEvent>> restrictedEvents =
            ImmutableSet.of(
                    VpnAccessEvent::new,
                    AdminAccessEvent::new,
                    AccessibilityAccessEvent::new);

    public static void onAccessEvent(Context ctx, AccessibilityNodeInfo event) {
        restrictedEvents.stream()
                .filter(e -> event != null && e.get().isRestrict(event))
                .findFirst()
                .ifPresent(ev -> {
                    log.debug("Access attempt: {}", ev.get().getClass().getSimpleName());
                    onRestrictEvent(ctx);
                });
    }

    private static void onRestrictEvent(Context ctx) {
        log.error("LOCKING SCREEN");
        val dpm = ((DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE));
        if (dpm.isAdminActive(new ComponentName(ctx, RemoveAppAdminReceiver.class))) {
            dpm.lockNow();
        }

        // Replacing the top activity with root settings activity.
        // It is used to avoid disabling restricted settings from recent apps menu
        CommonUtils.startIntentActivity(ctx, Settings.ACTION_SETTINGS,
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        CommonUtils.startIntentActivity(ctx, LockActivity.class,
                FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}

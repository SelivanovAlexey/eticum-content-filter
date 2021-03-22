package com.eticum.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eticum.api.EticumApiService;
import com.eticum.api.http.model.Profile;
import com.eticum.api.http.utils.KeepAliveCallback;
import com.eticum.services.EticumVpnService;
import com.eticum.utils.SharedPreferencesUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestartOnBootReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (BOOT_COMPLETED_ACTION.equals(intent.getAction()) && SharedPreferencesUtils.isVpnRunning()) {
            log.debug("Restoring Vpn service after reboot");
            EticumApiService.doKeepAlive(new KeepAliveCallback() {
                @Override
                public void onApiConfigChanged(Profile profile) {}

                @Override
                public void onSuccess() {
                    EticumVpnService.start(context);
                }
            });
        }
    }
}

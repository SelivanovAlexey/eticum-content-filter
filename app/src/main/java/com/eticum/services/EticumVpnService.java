package com.eticum.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ProxyInfo;
import android.net.VpnService;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EticumVpnService extends VpnService {

    private static final String ACTION_START = "start";
    private static final String ACTION_STOP = "stop";

    public static void start(Context context) {
        Intent intent = new Intent(context, EticumVpnService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, EticumVpnService.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }

    private EticumVpnService.Builder lastBuilder = null;
    private ParcelFileDescriptor vpn = null;

    private void start() {
        if (vpn == null) {
            lastBuilder = getBuilder();
            vpn = startVPN(lastBuilder);
            if (vpn == null) throw new IllegalStateException("Start failed");
        }
    }

    private void stop() {
        if (vpn != null) {
            stopVPN(vpn);
            vpn = null;
        }
        stopForeground(true);
    }

    @Override
    public void onRevoke() {
        log.debug("Revoke");
        stop();
        vpn = null;
        super.onRevoke();
    }

    private ParcelFileDescriptor startVPN(Builder builder) throws SecurityException {
        try {
            return builder.establish();
        } catch (SecurityException ex) {
            throw ex;
        } catch (Throwable ex) {
            log.error(ex.toString() + "\n" + ex.getStackTrace()[0]);
            return null;
        }
    }

    private Builder getBuilder() {
        // Build VPN service
        Builder builder = new Builder();
        builder.setSession("Eticum");

        // VPN address
        builder.addAddress("10.0.8.2", 32);
        builder.addRoute("0.0.0.0", 0);
        builder.addDnsServer("8.8.4.4");

//        try {
//            switch (filtrationProcessor.getProfile().getMode()){
//                case allow:
//                    for (String app : VPNUtils.getConfirmedApps()) {
//                        builder.addAllowedApplication(app);
//                    }
//                    builder.addAllowedApplication(BuildConfig.APPLICATION_ID);
//                    break;
//                case deny:
//                    for (String app  : VPNUtils.getConfirmedApps()) {
//                        builder.addDisallowedApplication(app);
//                    }
//                    break;
//                case info:
//                default:
//                    break;
//            }
//        } catch (PackageManager.NameNotFoundException ex) {
//            log.error("Package is not exist", ex);
//        }

        builder.setHttpProxy(ProxyInfo.buildDirectProxy("127.0.0.1", 8085));
        return builder;
    }

    private void stopVPN(ParcelFileDescriptor pfd) {
        log.debug("Stopping");
        try {
            pfd.close();
        } catch (IOException ex) {
            log.error(ex.toString() + "\n" + ex.getStackTrace()[0]);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log.debug("Received " + intent);
        // Handle service restart
        if (intent == null) {
            return START_STICKY;
        }

        if (ACTION_START.equals(intent.getAction())) {
            start();
        }
        if (ACTION_STOP.equals(intent.getAction())) {
            stop();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        log.debug("Destroy");
        try {
            if (vpn != null) {
                stopVPN(vpn);
                vpn = null;
            }
        } catch (Throwable ex) {
            log.error(ex.toString() + "\n" + ex.getStackTrace()[0]);
        }
        super.onDestroy();
    }

    public class ServiceBinder extends Binder {
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            // see Implementation of android.net.VpnService.Callback.onTransact()
            if (code == IBinder.LAST_CALL_TRANSACTION) {
                onRevoke();
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        public EticumVpnService getService() {
            return EticumVpnService.this;
        }
    }
}

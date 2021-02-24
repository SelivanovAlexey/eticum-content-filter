package com.eticum.services;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ProxyInfo;
import android.net.VpnService;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.eticum.BuildConfig;
import com.eticum.Constants;
import com.eticum.api.EticumApiService;
import com.eticum.api.http.model.Profile;
import com.eticum.api.http.utils.KeepAliveCallback;
import com.eticum.filter.FilterInfoHolder;
import com.eticum.proxy.ProxyServer;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EticumVpnService extends VpnService implements View.OnTouchListener  {

    private static final String ACTION_START = "start";
    private static final String ACTION_STOP = "stop";

    private ParcelFileDescriptor vpn = null;

    public static boolean isRunning = false;

    private TimerTask keepAliveTask = null;
    private final KeepAliveCallback keepAliveCallback = profile -> {
        stop();
        start(buildVpn(profile));
    };

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

    private void start(VpnService.Builder builder) {
        if (vpn == null) {
            log.debug("Starting new vpn service instance");
            vpn = startVPN(builder);
            ProxyServer.start();
            scheduleAndKeepAliveRequest(keepAliveCallback);
            isRunning = true;
            if (vpn == null) throw new IllegalStateException("Start failed");
        }
    }

    private void stop() {
        if (vpn != null) {
            stopVPN(vpn);
            vpn = null;
            ProxyServer.stop();
            stopKeepAliveRequest();
            isRunning = false;
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

    private ParcelFileDescriptor startVPN(VpnService.Builder builder) throws SecurityException {
        try {
            ParcelFileDescriptor pfd = builder.establish();
            log.debug("Successfully started vpn service");
            return pfd;
        } catch (SecurityException ex) {
            throw ex;
        } catch (Throwable ex) {
            log.error(ex.toString() + "\n" + ex.getStackTrace()[0]);
            return null;
        }
    }

    private VpnService.Builder buildVpn(Profile profile) {
        Builder builder = new Builder();
        switch (profile.getMode()) {
            case allow:
                profile.getApps()
                        .forEach(builder::addAllowedApplication);
                builder.addAllowedApplication(BuildConfig.APPLICATION_ID);
                break;
            case deny:
                profile.getApps()
                        .forEach(builder::addDisallowedApplication);
                break;
            case info:
            default:
                break;
        }

        return builder
                .setSession("Eticum")
                .addAddress("192.0.0.26", 32)
                .addRoute("0.0.0.0", 32)
                .addDnsServer("8.8.8.8")
                .setHttpProxy(ProxyInfo.buildDirectProxy("127.0.0.1", Constants.LOCAL_PROXY_PORT));
    }

    private VpnService.Builder buildVpn() {
        return buildVpn(FilterInfoHolder.get().getProfile());
    }

    private void stopVPN(ParcelFileDescriptor pfd) {
        log.debug("Stopping vpn service");
        try {
            pfd.close();
            log.debug("Stopped");
        } catch (IOException ex) {
            log.error(ex.toString() + "\n" + ex.getStackTrace()[0]);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log.debug("Received " + intent);
        if (intent == null) {
            return START_STICKY;
        }
        if (ACTION_START.equals(intent.getAction())) {
            start(buildVpn());
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        log.debug("Click detected: " + v.getTransitionName());
        return v.performClick();
    }

    public class ServiceBinder extends Binder {
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
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

    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    private void scheduleAndKeepAliveRequest(KeepAliveCallback callback) {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        keepAliveTask = new TimerTask() {
            @Override
            public void run() {
//                if (!SharedPreferencesUtils.isLoggedIn()) doAsynchronousTask.cancel();
                handler.post(() -> EticumApiService.doKeepAlive(callback));
            }
        };
        timer.schedule(keepAliveTask, 0, Constants.KEEP_ALIVE_INTERVAL);
    }

    private void stopKeepAliveRequest() {
        keepAliveTask.cancel();
    }

    private class Builder extends VpnService.Builder {
        @NonNull
        @Override
        public VpnService.Builder addAllowedApplication(@NonNull String packageName) {
            try {
                return super.addAllowedApplication(packageName);
            } catch (PackageManager.NameNotFoundException e) {
                log.error("The package {} is not found on device.", e.getMessage());
                return this;
            }
        }

        @NonNull
        @Override
        public VpnService.Builder addDisallowedApplication(@NonNull String packageName) {
            try {
                return super.addDisallowedApplication(packageName);
            } catch (PackageManager.NameNotFoundException e) {
                log.error("The package {} is not found on device.", e.getMessage());
                return this;
            }
        }
    }

    public boolean isAlwaysOnEnabled() {
        return StringUtils.equals(
                Settings.Secure.getString(getContentResolver(), "always_on_vpn_app"),
                BuildConfig.APPLICATION_ID);
    }

    public boolean isBlockingEnabled() {
        return isAlwaysOnEnabled() && Settings.Secure.getInt(getContentResolver(), "always_on_vpn_lockdown", 0) != 0;
    }
}

package com.eticum.services;

import android.content.Context;
import android.content.Intent;
import android.net.ProxyInfo;
import android.net.VpnService;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

import com.eticum.Constants;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EticumVpnService extends VpnService {

    private static final String ACTION_START = "start";
    private static final String ACTION_STOP = "stop";

    private EticumVpnService.Builder lastBuilder = null;
    private ParcelFileDescriptor vpn = null;

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

    private void start() {
        if (vpn == null) {
            log.debug("Starting new vpn service instance");
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

    private Builder getBuilder() {

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

        return new Builder()
                .setSession("Eticum")
                .addAddress("192.0.0.26", 32)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("8.8.8.8")
                .setHttpProxy(ProxyInfo.buildDirectProxy("127.0.0.1", Constants.LOCAL_PROXY_PORT));
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
}

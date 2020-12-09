package com.eticum.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.security.KeyChain;

import com.eticum.Constants;
import com.eticum.activities.EticumActivity;
import com.eticum.activities.InitialActivity;

import org.littleshoot.proxy.mitm.Authority;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;

public class ActivityControlsUtils {

    public static final int REQUEST_VPN = 1;

    public static void startInitialSetupActivity(Context context) {
        context.startActivity(new Intent(context, InitialActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

//    public static void stopEticumFilterService(Context context) {
//        Intent intent = new Intent(context, EticumFilterService.class);
//        intent.setAction(ACTION_STOP_FOREGROUND_SERVICE);
//        context.startService(intent);
//    }

    public static void showLoginError(Context activityContext) {
//        TextView textView = ((Activity) activityContext).findViewById(R.id.textinput_error);
//        textView.setVisibility(View.VISIBLE);
    }

    public static void startEticumActivity(Context context) {
        Intent intent = new Intent(context, EticumActivity.class);
        context.startActivity(intent);
    }

//    public static void activateDeviceAdmin(FragmentActivity activity, ComponentName devAdminReceiver) {
//        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//        intent.putExtra(EXTRA_DEVICE_ADMIN, devAdminReceiver);
//        activity.startActivityForResult(intent, 0);
//    }

    public static void turnOnAccessibility(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void installEticumCA(Activity context, Authority authority) {
        KeyStore ks;
        Certificate cert;
        try {
            ks = SecurityUtils.loadKeyStore(authority);
            cert = ks.getCertificate(authority.alias());
            Intent intent = KeyChain.createInstallIntent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(KeyChain.EXTRA_CERTIFICATE, cert.getEncoded());
            intent.putExtra(KeyChain.EXTRA_NAME, Constants.ETICUM_CA_ORGANIZATION_NAME);
            context.startActivityForResult(intent, 0xf00);
        } catch (IOException | GeneralSecurityException ex) {
            ex.printStackTrace();
        }
    }
}

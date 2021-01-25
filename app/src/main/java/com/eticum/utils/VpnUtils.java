package com.eticum.utils;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;

import com.eticum.App;
import com.eticum.api.http.model.ApplicationInfo;
import com.eticum.api.http.model.Profile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VpnUtils {

    public static Set<ApplicationInfo> getInstalledApplications() {
        PackageManager pm = App.getContext().getPackageManager();
        @SuppressLint("WrongConstant") List<android.content.pm.ApplicationInfo> apps
                = pm.getInstalledApplications(0);

        Set<android.content.pm.ApplicationInfo> filtered = apps.stream()
                .filter(VpnUtils::isNonSystemPackage)
                .collect(Collectors.toSet());
        return apps.stream()
                .filter(VpnUtils::isNonSystemPackage)
                .map(appInfo -> ApplicationInfo.builder()
                        .appName(pm.getApplicationLabel(appInfo).toString())
                        .appPackage(appInfo.packageName)
                        .build())
                .collect(Collectors.toSet());
    }

    public static boolean isNonSystemPackage(android.content.pm.ApplicationInfo appInfo) {
        return (appInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0;
    }

    // Dependent only on apps processing
    public static boolean isApiConfigChanged(Profile obtainedProfile, Profile currentProfile){
        return !obtainedProfile.getMode().equals(currentProfile.getMode())
                || !obtainedProfile.getApps().equals(currentProfile.getApps());
    }
}

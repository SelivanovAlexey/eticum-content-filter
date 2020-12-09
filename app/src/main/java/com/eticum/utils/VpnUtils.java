package com.eticum.utils;

import android.annotation.SuppressLint;

import com.eticum.App;
import com.eticum.api.http.model.ApplicationInfo;
import com.eticum.api.http.model.Profile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VpnUtils {

    public static Set<ApplicationInfo> getInstalledApplications() {
        @SuppressLint("WrongConstant") List<android.content.pm.ApplicationInfo> apps
                = App.getContext().getPackageManager().getInstalledApplications(0);

        return apps.stream()
                .filter(VpnUtils::isNonSystemPackage)
                .map(appInfo -> ApplicationInfo.builder()
                        .appName(appInfo.name)
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

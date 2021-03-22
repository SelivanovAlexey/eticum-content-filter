package com.eticum.utils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.eticum.App;
import com.eticum.api.http.model.ApplicationInfo;
import com.eticum.api.http.model.Profile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

@UtilityClass
public class VpnUtils {

    public static Set<ApplicationInfo> getInstalledApplications() {
        PackageManager pm = App.getContext().getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);

        return apps.stream()
                .map(ri -> ApplicationInfo.builder()
                        .appName(pm.getApplicationLabel(ri.activityInfo.applicationInfo).toString())
                        .appPackage(ri.activityInfo.packageName)
                        .build())
                .collect(Collectors.toSet());
    }

    // Dependent only on apps processing
    public static boolean isApiConfigChanged(Profile obtainedProfile, Profile currentProfile) {
        return currentProfile != null && // reboot case
                (!obtainedProfile.getMode().equals(currentProfile.getMode())
                || !obtainedProfile.getApps().equals(currentProfile.getApps()));
    }
}

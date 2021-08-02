package com.eticum.utils;

import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

import com.eticum.App;
import com.eticum.R;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public abstract class AccessEvent {
    protected final static String LINEAR_LAYOUT_PACKAGE = "android.widget.LinearLayout";
    protected final static String SETTINGS_PACKAGE = "com.android.settings";
    protected final static String SAMSUNG_ACCESSIBILITY_PACKAGE = "com.samsung.accessibility";
    protected final static String ETICUM = "Eticum";
    protected final static String ETICUM_REMOVAL_CONTROL = "Eticum Removal Control";

    public final boolean isRestrict(AccessibilityNodeInfo source) {
        List<Pair<Function<AccessibilityNodeInfo, Object>, Object>> conditions = getRestrictConditions();
        return conditions
                .stream()
                .allMatch(pair -> Objects.equals(pair.getLeft().apply(source), pair.getRight()) ||
                        Objects.equals(pair.getLeft().apply(source.getParent()), pair.getRight()));
    }

    protected List<Pair<Function<AccessibilityNodeInfo, Object>, Object>> getRestrictConditions() {
        return manufacturerFilter(Collections.emptyList());
    }

    protected List<Pair<Function<AccessibilityNodeInfo, Object>, Object>> manufacturerFilter(List<Pair<Function<AccessibilityNodeInfo, Object>, Object>> map) {
        return map;
    }


}

class VpnAccessEvent extends AccessEvent {

    @Override
    protected List<Pair<Function<AccessibilityNodeInfo, Object>, Object>> getRestrictConditions() {
        return manufacturerFilter(Arrays.asList(
                Pair.of(AccessibilityNodeInfo::getClassName, LINEAR_LAYOUT_PACKAGE),
                Pair.of(AccessibilityNodeInfo::getPackageName, SETTINGS_PACKAGE),
                Pair.of(AccessibilityNodeInfo::getChildCount, 3),
                Pair.of(s -> Optional.ofNullable(s.getChild(0)).map(AccessibilityNodeInfo::getText).orElse(""), ETICUM),
                Pair.of(s -> Optional.ofNullable(s.getChild(1)).map(AccessibilityNodeInfo::getText).orElse(""), App.getContext().getString(R.string.access_event_always_on_default)),
                Pair.of(s -> Optional.ofNullable(s.getChild(2)).map(AccessibilityNodeInfo::getContentDescription).orElse(""), App.getContext().getString(R.string.access_event_settings))));
    }

    @Override
    protected List<Pair<Function<AccessibilityNodeInfo, Object>, Object>> manufacturerFilter(List<Pair<Function<AccessibilityNodeInfo, Object>, Object>> map) {
        if ("samsung".equals(Build.MANUFACTURER) && Locale.getDefault().toString().equals("ru_RU")) {
            map.set(4, Pair.of(s -> Optional.ofNullable(s.getChild(1))
                    .map(AccessibilityNodeInfo::getText)
                    .orElse(""), App.getContext().getString(R.string.access_event_always_on_samsung)));
        }
        return map;
    }
}

class AdminAccessEvent extends AccessEvent {

    @Override
    protected List<Pair<Function<AccessibilityNodeInfo, Object>, Object>> getRestrictConditions() {
        return manufacturerFilter(Arrays.asList(
                Pair.of(AccessibilityNodeInfo::getClassName, LINEAR_LAYOUT_PACKAGE),
                Pair.of(AccessibilityNodeInfo::getPackageName, SETTINGS_PACKAGE),
                Pair.of(AccessibilityNodeInfo::getChildCount, 3),
                Pair.of(s -> Optional.ofNullable(s.getChild(0)).map(AccessibilityNodeInfo::getText).orElse(""), ETICUM_REMOVAL_CONTROL)));
    }
}

class AccessibilityAccessEvent extends AccessEvent {

    @Override
    protected List<Pair<Function<AccessibilityNodeInfo, Object>, Object>> getRestrictConditions() {
        return manufacturerFilter(Arrays.asList(
                Pair.of(AccessibilityNodeInfo::getClassName, LINEAR_LAYOUT_PACKAGE),
                Pair.of(AccessibilityNodeInfo::getPackageName, SETTINGS_PACKAGE),
                Pair.of(AccessibilityNodeInfo::getChildCount, 2),
                Pair.of(s -> Optional.ofNullable(s.getChild(0)).map(AccessibilityNodeInfo::getText).orElse(""), ETICUM),
                Pair.of(s -> Optional.ofNullable(s.getChild(1)).map(AccessibilityNodeInfo::getText).orElse(""), App.getContext().getString(R.string.access_event_on))));
    }

    @Override
    protected List<Pair<Function<AccessibilityNodeInfo, Object>, Object>> manufacturerFilter(List<Pair<Function<AccessibilityNodeInfo, Object>, Object>> map) {
        if ("samsung".equals(Build.MANUFACTURER)) {
            map.set(1, Pair.of(AccessibilityNodeInfo::getPackageName, SAMSUNG_ACCESSIBILITY_PACKAGE));
        }
        return map;
    }
}
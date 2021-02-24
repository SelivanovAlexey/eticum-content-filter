package com.eticum.utils;

import android.view.accessibility.AccessibilityNodeInfo;

import org.apache.commons.lang3.StringUtils;

public abstract class AccessEvent {
    protected final static String LINEAR_LAYOUT_PACKAGE = "android.widget.LinearLayout";
    protected final static String SETTINGS_PACKAGE = "com.android.settings";
    protected final static String ETICUM = "Eticum";
    protected final static String ON = "On";
    protected final static String SETTINGS = "Settings";
    protected final static String ETICUM_REMOVAL_CONTROL = "Eticum Removal Control";

    abstract boolean isRestrict(AccessibilityNodeInfo source);
}

class VpnAccessEvent extends AccessEvent {

    @Override
    public boolean isRestrict(AccessibilityNodeInfo source) {
        return StringUtils.equals(source.getParent().getClassName(), LINEAR_LAYOUT_PACKAGE) &&
                StringUtils.equals(source.getParent().getPackageName(), SETTINGS_PACKAGE) &&
                (source.getParent().getChildCount() == 2 && (
                        StringUtils.equals(source.getParent().getChild(0).getText(), ETICUM) &&
                        StringUtils.equals(source.getParent().getChild(1).getContentDescription(), SETTINGS)));
    }
}

class AdminAccessEvent extends AccessEvent {

    @Override
    public boolean isRestrict(AccessibilityNodeInfo source) {
        return StringUtils.equals(source.getClassName(), LINEAR_LAYOUT_PACKAGE) &&
                StringUtils.equals(source.getPackageName(), SETTINGS_PACKAGE) &&
                (source.getChildCount() == 3 &&
                        StringUtils.equals(source.getChild(0).getText(), ETICUM_REMOVAL_CONTROL));
    }
}

class AccessibilityAccessEvent extends AccessEvent {

    @Override
    public boolean isRestrict(AccessibilityNodeInfo source) {
        return StringUtils.equals(source.getClassName(), LINEAR_LAYOUT_PACKAGE) &&
                StringUtils.equals(source.getPackageName(), SETTINGS_PACKAGE) &&
                (source.getChildCount() == 2 && (
                        StringUtils.equals(source.getChild(0).getText(), ETICUM) &&
                        StringUtils.equals(source.getChild(1).getText(), ON)));
    }
}
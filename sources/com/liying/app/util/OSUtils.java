package com.liying.app.util;

import android.text.TextUtils;

public class OSUtils {
    private static final String KEY_DISPLAY = "ro.build.display.id";
    private static final String KEY_EMUI_VERSION_NAME = "ro.build.version.emui";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";

    public static boolean isMIUI() {
        return !TextUtils.isEmpty(getSystemProperty(KEY_MIUI_VERSION_NAME, ""));
    }

    public static boolean isMIUI6Later() {
        String version = getMIUIVersion();
        if (version.isEmpty()) {
            return false;
        }
        try {
            if (Integer.valueOf(version.substring(1)).intValue() >= 6) {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String getMIUIVersion() {
        return isMIUI() ? getSystemProperty(KEY_MIUI_VERSION_NAME, "") : "";
    }

    public static boolean isEMUI() {
        return !TextUtils.isEmpty(getSystemProperty(KEY_EMUI_VERSION_NAME, ""));
    }

    public static String getEMUIVersion() {
        return isEMUI() ? getSystemProperty(KEY_EMUI_VERSION_NAME, "") : "";
    }

    public static boolean isEMUI3_1() {
        String property = getEMUIVersion();
        if ("EmotionUI 3".equals(property) || property.contains("EmotionUI_3.1")) {
            return true;
        }
        return false;
    }

    public static boolean isEMUI3_0() {
        if (getEMUIVersion().contains("EmotionUI_3.0")) {
            return true;
        }
        return false;
    }

    public static boolean isFlymeOS() {
        return getFlymeOSFlag().toLowerCase().contains("flyme");
    }

    public static boolean isFlymeOS4Later() {
        int num;
        String version = getFlymeOSVersion();
        if (version.isEmpty()) {
            return false;
        }
        try {
            if (version.toLowerCase().contains("os")) {
                num = Integer.valueOf(version.substring(9, 10)).intValue();
            } else {
                num = Integer.valueOf(version.substring(6, 7)).intValue();
            }
            if (num >= 4) {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isFlymeOS5() {
        int num;
        String version = getFlymeOSVersion();
        if (version.isEmpty()) {
            return false;
        }
        try {
            if (version.toLowerCase().contains("os")) {
                num = Integer.valueOf(version.substring(9, 10)).intValue();
            } else {
                num = Integer.valueOf(version.substring(6, 7)).intValue();
            }
            if (num == 5) {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String getFlymeOSVersion() {
        return isFlymeOS() ? getSystemProperty(KEY_DISPLAY, "") : "";
    }

    private static String getFlymeOSFlag() {
        return getSystemProperty(KEY_DISPLAY, "");
    }

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            return (String) clz.getMethod("get", new Class[]{String.class, String.class}).invoke(clz, new Object[]{key, defaultValue});
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}

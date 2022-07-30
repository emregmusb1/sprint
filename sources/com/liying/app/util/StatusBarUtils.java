package com.liying.app.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.ColorInt;
import androidx.core.view.ViewCompat;
import com.ggccc.app.hbsdf23.R;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StatusBarUtils {
    public static final boolean ImmersiveEnable = true;

    public static void setStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().addFlags(Integer.MIN_VALUE);
            activity.getWindow().getDecorView().setSystemUiVisibility(1280);
            activity.getWindow().setStatusBarColor(0);
            activity.getWindow().setNavigationBarColor(ViewCompat.MEASURED_STATE_MASK);
            if (!OSUtils.isEMUI3_1()) {
                activity.getWindow().clearFlags(67108864);
            }
            setRootView(activity);
        } else if (Build.VERSION.SDK_INT == 19) {
            activity.getWindow().addFlags(67108864);
            setRootView(activity);
        }
    }

    private static void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(16908290);
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(false);
            }
        }
    }

    public static int getStatusBarHeight(Context context) {
        return context.getResources().getDimensionPixelSize(context.getResources().getIdentifier("status_bar_height", "dimen", "android"));
    }

    public static int getToolbarHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.toolbar_height) + (Build.VERSION.SDK_INT >= 19 ? getStatusBarHeight(context) : 0);
    }

    public static void setStatusBar(Window window, @ColorInt int v23StatusBarColor, @ColorInt int v21StatusBarColor, boolean lightMode) {
        int ui;
        boolean isMIUI6Later = OSUtils.isMIUI6Later();
        boolean isFlyme4Later = OSUtils.isFlymeOS4Later();
        if (Build.VERSION.SDK_INT >= 23) {
            View decor = window.getDecorView();
            int ui2 = decor.getSystemUiVisibility();
            if (lightMode) {
                ui = ui2 | 8192;
            } else {
                ui = ui2 & -8193;
            }
            decor.setSystemUiVisibility(ui);
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(v23StatusBarColor);
        } else if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(Integer.MIN_VALUE);
            if (isMIUI6Later || isFlyme4Later) {
                window.setStatusBarColor(v23StatusBarColor);
            } else {
                window.setStatusBarColor(v21StatusBarColor);
            }
        }
        if (isMIUI6Later && lightMode) {
            setMIUIStatusBarDarkFont(window, true);
        } else if (isFlyme4Later && lightMode) {
            setMeizuStatusBarDarkIcon(window, true);
        }
    }

    private static void setMIUIStatusBarDarkFont(Window window, boolean darkFont) {
        if (window != null) {
            Class clazz = window.getClass();
            try {
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                int darkModeFlag = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE").getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", new Class[]{Integer.TYPE, Integer.TYPE});
                if (darkFont) {
                    extraFlagField.invoke(window, new Object[]{Integer.valueOf(darkModeFlag), Integer.valueOf(darkModeFlag)});
                    return;
                }
                extraFlagField.invoke(window, new Object[]{0, Integer.valueOf(darkModeFlag)});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean setMeizuStatusBarDarkIcon(Window window, boolean dark) {
        int value;
        if (window == null) {
            return false;
        }
        try {
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt((Object) null);
            int value2 = meizuFlags.getInt(lp);
            if (dark) {
                value = value2 | bit;
            } else {
                value = value2 & (~bit);
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

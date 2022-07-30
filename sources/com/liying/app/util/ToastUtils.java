package com.liying.app.util;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.StringRes;

public class ToastUtils {
    private static Toast toast;

    public static void showShort(Context context, CharSequence message) {
        Context appContext = null;
        if (context != null) {
            appContext = context.getApplicationContext();
        }
        Toast toast2 = toast;
        if (toast2 == null) {
            toast = Toast.makeText(appContext, message, 0);
        } else {
            toast2.setText(message);
        }
        toast.show();
    }

    public static void showShort(Context context, int message) {
        Context appContext = null;
        if (context != null) {
            appContext = context.getApplicationContext();
        }
        showShort(appContext, (CharSequence) context.getResources().getString(message));
    }

    public static void showLong(Context context, CharSequence message) {
        Context appContext = null;
        if (context != null) {
            appContext = context.getApplicationContext();
        }
        Toast toast2 = toast;
        if (toast2 == null) {
            toast = Toast.makeText(appContext, message, 1);
        } else {
            toast2.setText(message);
        }
        toast.show();
    }

    public static void showLong(Context context, @StringRes int message) {
        Context appContext = null;
        if (context != null) {
            appContext = context.getApplicationContext();
        }
        showLong(appContext, (CharSequence) context.getResources().getString(message));
    }

    public static void hideToast() {
        Toast toast2 = toast;
        if (toast2 != null) {
            toast2.cancel();
        }
    }
}

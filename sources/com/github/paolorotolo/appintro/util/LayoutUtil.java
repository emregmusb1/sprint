package com.github.paolorotolo.appintro.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import java.util.Locale;

public class LayoutUtil {
    public static boolean isRtl(@NonNull Context ctx) {
        if (Build.VERSION.SDK_INT < 17 || ctx.getResources().getConfiguration().getLayoutDirection() != 1) {
            return false;
        }
        return true;
    }

    @RequiresApi(api = 17)
    private static boolean defaultIsRtlBehavior() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
    }
}

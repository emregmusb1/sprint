package org.xutils.common.util;

import android.text.TextUtils;
import android.util.Log;
import java.util.Locale;
import org.xutils.x;

public class LogUtil {
    public static String customTagPrefix = "x_log";

    private LogUtil() {
    }

    private static String generateTag() {
        String tag;
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String callerClazzName = caller.getClassName();
        String tag2 = String.format(Locale.getDefault(), "%s.%s(L:%d)", new Object[]{callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1), caller.getMethodName(), Integer.valueOf(caller.getLineNumber())});
        if (TextUtils.isEmpty(customTagPrefix)) {
            tag = tag2;
        } else {
            tag = customTagPrefix + ":" + tag2;
        }
        return tag;
    }

    public static void d(String content) {
        if (x.isDebug() && !TextUtils.isEmpty(content)) {
            Log.d(generateTag(), content);
        }
    }

    public static void d(String content, Throwable tr) {
        if (x.isDebug() && !TextUtils.isEmpty(content)) {
            Log.d(generateTag(), content, tr);
        }
    }

    public static void e(String content) {
        if (x.isDebug() && !TextUtils.isEmpty(content)) {
            Log.e(generateTag(), content);
        }
    }

    public static void e(String content, Throwable tr) {
        if (x.isDebug() && !TextUtils.isEmpty(content)) {
            Log.e(generateTag(), content, tr);
        }
    }

    public static void i(String content) {
        if (x.isDebug() && !TextUtils.isEmpty(content)) {
            Log.i(generateTag(), content);
        }
    }

    public static void i(String content, Throwable tr) {
        if (x.isDebug() && !TextUtils.isEmpty(content)) {
            Log.i(generateTag(), content, tr);
        }
    }

    public static void v(String content) {
        if (x.isDebug() && !TextUtils.isEmpty(content)) {
            Log.v(generateTag(), content);
        }
    }

    public static void v(String content, Throwable tr) {
        if (x.isDebug() && !TextUtils.isEmpty(content)) {
            Log.v(generateTag(), content, tr);
        }
    }

    public static void w(String content) {
        if (x.isDebug() && !TextUtils.isEmpty(content)) {
            Log.w(generateTag(), content);
        }
    }

    public static void w(String content, Throwable tr) {
        if (x.isDebug() && !TextUtils.isEmpty(content)) {
            Log.w(generateTag(), content, tr);
        }
    }

    public static void w(Throwable tr) {
        if (x.isDebug()) {
            Log.w(generateTag(), tr);
        }
    }

    public static void wtf(String content) {
        if (x.isDebug() && !TextUtils.isEmpty(content)) {
            Log.wtf(generateTag(), content);
        }
    }

    public static void wtf(String content, Throwable tr) {
        if (x.isDebug() && !TextUtils.isEmpty(content)) {
            Log.wtf(generateTag(), content, tr);
        }
    }

    public static void wtf(Throwable tr) {
        if (x.isDebug()) {
            Log.wtf(generateTag(), tr);
        }
    }
}

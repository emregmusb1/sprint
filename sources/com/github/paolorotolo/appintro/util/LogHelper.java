package com.github.paolorotolo.appintro.util;

import android.util.Log;

public class LogHelper {
    private static final String LOG_PREFIX = "Log: ";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    private static String makeLogTag(String str) {
        if (str.length() > 23 - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, (23 - LOG_PREFIX_LENGTH) - 1);
        }
        return LOG_PREFIX + str;
    }

    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void v(String tag, Object... messages) {
    }

    public static void d(String tag, Object... messages) {
    }

    public static void i(String tag, Object... messages) {
        log(tag, 4, (Throwable) null, messages);
    }

    public static void w(String tag, Object... messages) {
        log(tag, 5, (Throwable) null, messages);
    }

    public static void w(String tag, Throwable t, Object... messages) {
        log(tag, 5, t, messages);
    }

    public static void e(String tag, Object... messages) {
        log(tag, 6, (Throwable) null, messages);
    }

    public static void e(String tag, Throwable t, Object... messages) {
        log(tag, 6, t, messages);
    }

    private static void log(String tag, int level, Throwable t, Object... messages) {
        String message;
        if (t == null && messages != null && messages.length == 1) {
            message = messages[0].toString();
        } else {
            StringBuilder sb = new StringBuilder();
            if (messages != null) {
                for (Object m : messages) {
                    sb.append(m);
                }
            }
            if (t != null) {
                sb.append("\n");
                sb.append(Log.getStackTraceString(t));
            }
            message = sb.toString();
        }
        Log.println(level, tag, message);
    }
}

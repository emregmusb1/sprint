package com.github.paolorotolo.appintro.util;

import android.content.Context;
import android.graphics.Typeface;
import java.util.Hashtable;

public class CustomFontCache {
    private static final String TAG = LogHelper.makeLogTag(CustomFontCache.class);
    private static final Hashtable<String, Typeface> fCache = new Hashtable<>();

    public static Typeface get(String tfn, Context ctx) {
        Typeface tf = fCache.get(tfn);
        if (tf != null) {
            return tf;
        }
        try {
            Typeface tf2 = Typeface.createFromAsset(ctx.getAssets(), tfn);
            if (tf2 != null) {
                fCache.put(tfn, tf2);
            }
            return tf2;
        } catch (Exception e) {
            if ("".equals(tfn)) {
                LogHelper.w(TAG, e, "Empty path");
                return null;
            }
            LogHelper.w(TAG, e, tfn);
            return null;
        }
    }
}

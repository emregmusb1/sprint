package com.liying.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class Preference {
    public static final HashMap<String, WeakReference<Preference>> hashMap = new HashMap<>();
    private Gson gson = new Gson();
    private SharedPreferences preferences;

    private Preference(Context context, String name) {
        this.preferences = context.getSharedPreferences(name, 0);
    }

    public static Preference obtain(Context context, String name) {
        WeakReference<Preference> weakReference = hashMap.get(name);
        if (weakReference == null || weakReference.get() == null) {
            weakReference = new WeakReference<>(new Preference(context, name));
            hashMap.put(name, weakReference);
        }
        return (Preference) weakReference.get();
    }

    public <T> void save(String key, T value) {
        Class<?> clazz = value.getClass();
        SharedPreferences.Editor edit = this.preferences.edit();
        if (clazz.equals(String.class)) {
            edit.putString(key, (String) value);
        } else if (clazz.equals(Integer.class)) {
            edit.putInt(key, ((Integer) value).intValue());
        } else if (clazz.equals(Boolean.class)) {
            edit.putBoolean(key, ((Boolean) value).booleanValue());
        } else if (clazz.equals(Float.class)) {
            edit.putFloat(key, ((Float) value).floatValue());
        } else if (clazz.equals(Long.class)) {
            edit.putLong(key, ((Long) value).longValue());
        } else {
            edit.putString(key, this.gson.toJson((Object) value, (Type) clazz));
        }
        edit.apply();
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x004e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <T> T get(java.lang.String r6, T r7) {
        /*
            r5 = this;
            java.lang.Class r0 = r7.getClass()
            r1 = 0
            java.lang.Class<java.lang.String> r2 = java.lang.String.class
            boolean r2 = r0.equals(r2)     // Catch:{ Exception -> 0x0050 }
            if (r2 != 0) goto L_0x003e
            java.lang.Class<java.lang.Integer> r2 = java.lang.Integer.class
            boolean r2 = r0.equals(r2)     // Catch:{ Exception -> 0x0050 }
            if (r2 != 0) goto L_0x003e
            java.lang.Class<java.lang.Boolean> r2 = java.lang.Boolean.class
            boolean r2 = r0.equals(r2)     // Catch:{ Exception -> 0x0050 }
            if (r2 != 0) goto L_0x003e
            java.lang.Class<java.lang.Float> r2 = java.lang.Float.class
            boolean r2 = r0.equals(r2)     // Catch:{ Exception -> 0x0050 }
            if (r2 != 0) goto L_0x003e
            java.lang.Class<java.lang.Long> r2 = java.lang.Long.class
            boolean r2 = r0.equals(r2)     // Catch:{ Exception -> 0x0050 }
            if (r2 == 0) goto L_0x002e
            goto L_0x003e
        L_0x002e:
            com.google.gson.Gson r2 = r5.gson     // Catch:{ Exception -> 0x0050 }
            android.content.SharedPreferences r3 = r5.preferences     // Catch:{ Exception -> 0x0050 }
            java.lang.String r4 = ""
            java.lang.String r3 = r3.getString(r6, r4)     // Catch:{ Exception -> 0x0050 }
            java.lang.Object r2 = r2.fromJson((java.lang.String) r3, r0)     // Catch:{ Exception -> 0x0050 }
            r1 = r2
            goto L_0x0049
        L_0x003e:
            android.content.SharedPreferences r2 = r5.preferences     // Catch:{ Exception -> 0x0050 }
            java.util.Map r2 = r2.getAll()     // Catch:{ Exception -> 0x0050 }
            java.lang.Object r2 = r2.get(r6)     // Catch:{ Exception -> 0x0050 }
            r1 = r2
        L_0x0049:
            if (r1 != 0) goto L_0x004e
            r2 = r7
            goto L_0x004f
        L_0x004e:
            r2 = r1
        L_0x004f:
            return r2
        L_0x0050:
            r2 = move-exception
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.liying.app.util.Preference.get(java.lang.String, java.lang.Object):java.lang.Object");
    }

    private SharedPreferences getPreferences() {
        return this.preferences;
    }

    public void clear(String key) {
        this.preferences.edit().remove(key).apply();
    }

    public void clearAll() {
        this.preferences.edit().clear().apply();
    }

    public void clearAllExclude(String... keys) {
        if (keys != null && keys.length >= 1) {
            Set<String> keySets = this.preferences.getAll().keySet();
            keySets.removeAll(Arrays.asList(keys));
            for (String key : keySets) {
                this.preferences.edit().remove(key);
            }
            this.preferences.edit().apply();
        }
    }
}

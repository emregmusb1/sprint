package org.xutils.db.table;

import android.text.TextUtils;
import java.util.Date;
import java.util.HashMap;

public final class DbModel {
    private final HashMap<String, String> dataMap = new HashMap<>();

    public String getString(String columnName) {
        return this.dataMap.get(columnName);
    }

    public int getInt(String columnName, int defaultValue) {
        String value = this.dataMap.get(columnName);
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value).intValue();
        } catch (Throwable th) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String columnName) {
        String value = this.dataMap.get(columnName);
        if (value != null) {
            return value.length() == 1 ? "1".equals(value) : Boolean.valueOf(value).booleanValue();
        }
        return false;
    }

    public double getDouble(String columnName, double defaultValue) {
        String value = this.dataMap.get(columnName);
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Double.valueOf(value).doubleValue();
        } catch (Throwable th) {
            return defaultValue;
        }
    }

    public float getFloat(String columnName, float defaultValue) {
        String value = this.dataMap.get(columnName);
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Float.valueOf(value).floatValue();
        } catch (Throwable th) {
            return defaultValue;
        }
    }

    public long getLong(String columnName, long defaultValue) {
        String value = this.dataMap.get(columnName);
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Long.valueOf(value).longValue();
        } catch (Throwable th) {
            return defaultValue;
        }
    }

    public Date getDate(String columnName, long defaultTime) {
        return new Date(getLong(columnName, defaultTime));
    }

    public java.sql.Date getSqlDate(String columnName, long defaultTime) {
        return new java.sql.Date(getLong(columnName, defaultTime));
    }

    public void add(String columnName, String valueStr) {
        this.dataMap.put(columnName, valueStr);
    }

    public HashMap<String, String> getDataMap() {
        return this.dataMap;
    }

    public boolean isEmpty(String columnName) {
        return TextUtils.isEmpty(this.dataMap.get(columnName));
    }
}

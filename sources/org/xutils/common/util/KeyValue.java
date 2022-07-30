package org.xutils.common.util;

public class KeyValue {
    public final String key;
    public final Object value;

    public KeyValue(String key2, Object value2) {
        this.key = key2;
        this.value = value2;
    }

    public String getValueStrOrEmpty() {
        Object obj = this.value;
        return obj == null ? "" : obj.toString();
    }

    public String getValueStrOrNull() {
        Object obj = this.value;
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KeyValue keyValue = (KeyValue) o;
        String str = this.key;
        if (str != null) {
            return str.equals(keyValue.key);
        }
        if (keyValue.key == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        String str = this.key;
        if (str != null) {
            return str.hashCode();
        }
        return 0;
    }

    public String toString() {
        return "KeyValue{key='" + this.key + '\'' + ", value=" + this.value + '}';
    }
}

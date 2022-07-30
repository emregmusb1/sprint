package com.alibaba.fastjson.serializer;

public class SerialContext {
    public final int features;
    public final Object fieldName;
    public final Object object;
    public final SerialContext parent;

    public SerialContext(SerialContext parent2, Object object2, Object fieldName2, int features2) {
        this.parent = parent2;
        this.object = object2;
        this.fieldName = fieldName2;
        this.features = features2;
    }

    public String toString() {
        if (this.parent == null) {
            return "$";
        }
        if (this.fieldName instanceof Integer) {
            return this.parent.toString() + "[" + this.fieldName + "]";
        }
        return this.parent.toString() + "." + this.fieldName;
    }
}

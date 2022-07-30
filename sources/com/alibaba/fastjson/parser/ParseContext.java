package com.alibaba.fastjson.parser;

import java.lang.reflect.Type;

public class ParseContext {
    public final Object fieldName;
    public Object object;
    public final ParseContext parent;
    public Type type;

    public ParseContext(ParseContext parent2, Object object2, Object fieldName2) {
        this.parent = parent2;
        this.object = object2;
        this.fieldName = fieldName2;
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

package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.util.FieldInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public abstract class FieldDeserializer {
    public final Class<?> clazz;
    public final FieldInfo fieldInfo;

    public abstract void parseField(DefaultJSONParser defaultJSONParser, Object obj, Type type, Map<String, Object> map);

    public FieldDeserializer(Class<?> clazz2, FieldInfo fieldInfo2, int fastMatchToken) {
        this.clazz = clazz2;
        this.fieldInfo = fieldInfo2;
    }

    public void setValue(Object object, int value) throws IllegalAccessException {
        this.fieldInfo.field.setInt(object, value);
    }

    public void setValue(Object object, long value) throws IllegalAccessException {
        this.fieldInfo.field.setLong(object, value);
    }

    public void setValue(Object object, float value) throws IllegalAccessException {
        this.fieldInfo.field.setFloat(object, value);
    }

    public void setValue(Object object, double value) throws IllegalAccessException {
        this.fieldInfo.field.setDouble(object, value);
    }

    public void setValue(Object object, Object value) {
        if (value != null || !this.fieldInfo.fieldClass.isPrimitive()) {
            Field field = this.fieldInfo.field;
            Method method = this.fieldInfo.method;
            try {
                if (this.fieldInfo.fieldAccess) {
                    if (!this.fieldInfo.getOnly) {
                        field.set(object, value);
                    } else if (Map.class.isAssignableFrom(this.fieldInfo.fieldClass)) {
                        Map map = (Map) field.get(object);
                        if (map != null) {
                            map.putAll((Map) value);
                        }
                    } else {
                        Collection collection = (Collection) field.get(object);
                        if (collection != null) {
                            collection.addAll((Collection) value);
                        }
                    }
                } else if (!this.fieldInfo.getOnly) {
                    method.invoke(object, new Object[]{value});
                } else if (Map.class.isAssignableFrom(this.fieldInfo.fieldClass)) {
                    Map map2 = (Map) method.invoke(object, new Object[0]);
                    if (map2 != null) {
                        map2.putAll((Map) value);
                    }
                } else {
                    Collection collection2 = (Collection) method.invoke(object, new Object[0]);
                    if (collection2 != null) {
                        collection2.addAll((Collection) value);
                    }
                }
            } catch (Exception e) {
                throw new JSONException("set property error, " + this.fieldInfo.name, e);
            }
        }
    }
}

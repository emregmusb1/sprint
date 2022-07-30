package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.util.FieldInfo;
import java.io.IOException;
import java.lang.reflect.Member;
import java.util.Collection;

public final class FieldSerializer implements Comparable<FieldSerializer> {
    protected final int features;
    public final FieldInfo fieldInfo;
    protected final String format;
    private RuntimeSerializerInfo runtimeInfo;
    protected final boolean writeNull;

    public FieldSerializer(FieldInfo fieldInfo2) {
        this.fieldInfo = fieldInfo2;
        boolean writeNull2 = false;
        JSONField annotation = fieldInfo2.getAnnotation();
        String format2 = null;
        if (annotation != null) {
            for (SerializerFeature feature : annotation.serialzeFeatures()) {
                if (feature == SerializerFeature.WriteMapNullValue) {
                    writeNull2 = true;
                }
            }
            format2 = annotation.format().trim();
            format2 = format2.length() == 0 ? null : format2;
            this.features = SerializerFeature.of(annotation.serialzeFeatures());
        } else {
            this.features = 0;
        }
        this.writeNull = writeNull2;
        this.format = format2;
    }

    public void writePrefix(JSONSerializer serializer) throws IOException {
        SerializeWriter out = serializer.out;
        int featurs = out.features;
        if ((SerializerFeature.QuoteFieldNames.mask & featurs) == 0) {
            out.writeFieldName(this.fieldInfo.name, true);
        } else if ((SerializerFeature.UseSingleQuotes.mask & featurs) != 0) {
            out.writeFieldName(this.fieldInfo.name, true);
        } else {
            out.write(this.fieldInfo.name_chars, 0, this.fieldInfo.name_chars.length);
        }
    }

    public Object getPropertyValue(Object object) throws Exception {
        try {
            return this.fieldInfo.get(object);
        } catch (Exception ex) {
            Member member = this.fieldInfo.method != null ? this.fieldInfo.method : this.fieldInfo.field;
            throw new JSONException("get property error。 " + (member.getDeclaringClass().getName() + "." + member.getName()), ex);
        }
    }

    public void writeValue(JSONSerializer serializer, Object propertyValue) throws Exception {
        Class<?> runtimeFieldClass;
        String str = this.format;
        if (str != null) {
            serializer.writeWithFormat(propertyValue, str);
            return;
        }
        if (this.runtimeInfo == null) {
            if (propertyValue == null) {
                runtimeFieldClass = this.fieldInfo.fieldClass;
            } else {
                runtimeFieldClass = propertyValue.getClass();
            }
            this.runtimeInfo = new RuntimeSerializerInfo(serializer.config.get(runtimeFieldClass), runtimeFieldClass);
        }
        RuntimeSerializerInfo runtimeInfo2 = this.runtimeInfo;
        if (propertyValue != null) {
            Class<?> valueClass = propertyValue.getClass();
            if (valueClass == runtimeInfo2.runtimeFieldClass) {
                runtimeInfo2.fieldSerializer.write(serializer, propertyValue, this.fieldInfo.name, this.fieldInfo.fieldType);
            } else {
                serializer.config.get(valueClass).write(serializer, propertyValue, this.fieldInfo.name, this.fieldInfo.fieldType);
            }
        } else if ((this.features & SerializerFeature.WriteNullNumberAsZero.mask) != 0 && Number.class.isAssignableFrom(runtimeInfo2.runtimeFieldClass)) {
            serializer.out.write(48);
        } else if ((this.features & SerializerFeature.WriteNullBooleanAsFalse.mask) != 0 && Boolean.class == runtimeInfo2.runtimeFieldClass) {
            serializer.out.write("false");
        } else if ((this.features & SerializerFeature.WriteNullListAsEmpty.mask) == 0 || !Collection.class.isAssignableFrom(runtimeInfo2.runtimeFieldClass)) {
            runtimeInfo2.fieldSerializer.write(serializer, (Object) null, this.fieldInfo.name, runtimeInfo2.runtimeFieldClass);
        } else {
            serializer.out.write("[]");
        }
    }

    static class RuntimeSerializerInfo {
        ObjectSerializer fieldSerializer;
        Class<?> runtimeFieldClass;

        public RuntimeSerializerInfo(ObjectSerializer fieldSerializer2, Class<?> runtimeFieldClass2) {
            this.fieldSerializer = fieldSerializer2;
            this.runtimeFieldClass = runtimeFieldClass2;
        }
    }

    public int compareTo(FieldSerializer o) {
        return this.fieldInfo.compareTo(o.fieldInfo);
    }
}

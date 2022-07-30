package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class JavaObjectDeserializer implements ObjectDeserializer {
    public static final JavaObjectDeserializer instance = new JavaObjectDeserializer();

    JavaObjectDeserializer() {
    }

    /* JADX WARNING: type inference failed for: r3v3, types: [T, java.lang.Object[]] */
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        if (!(type instanceof GenericArrayType)) {
            return parser.parse(fieldName);
        }
        Type componentType = ((GenericArrayType) type).getGenericComponentType();
        if (componentType instanceof TypeVariable) {
            componentType = ((TypeVariable) componentType).getBounds()[0];
        }
        List<Object> list = new ArrayList<>();
        parser.parseArray(componentType, (Collection) list);
        if (!(componentType instanceof Class)) {
            return list.toArray();
        }
        ? r3 = (Object[]) Array.newInstance((Class) componentType, list.size());
        list.toArray(r3);
        return r3;
    }
}

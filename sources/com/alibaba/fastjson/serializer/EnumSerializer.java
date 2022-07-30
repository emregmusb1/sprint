package com.alibaba.fastjson.serializer;

import java.io.IOException;
import java.lang.reflect.Type;

class EnumSerializer implements ObjectSerializer {
    EnumSerializer() {
    }

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.out;
        if ((out.features & SerializerFeature.WriteEnumUsingToString.mask) != 0) {
            String name = ((Enum) object).toString();
            if ((out.features & SerializerFeature.UseSingleQuotes.mask) != 0) {
                out.writeStringWithSingleQuote(name);
            } else {
                out.writeStringWithDoubleQuote(name, 0, false);
            }
        } else {
            out.writeInt(((Enum) object).ordinal());
        }
    }
}

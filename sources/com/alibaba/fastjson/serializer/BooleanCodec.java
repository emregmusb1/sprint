package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.IOException;
import java.lang.reflect.Type;

public final class BooleanCodec implements ObjectSerializer, ObjectDeserializer {
    public static final BooleanCodec instance = new BooleanCodec();

    private BooleanCodec() {
    }

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.out;
        Boolean value = (Boolean) object;
        if (value == null) {
            if ((out.features & SerializerFeature.WriteNullBooleanAsFalse.mask) != 0) {
                out.write("false");
            } else {
                out.writeNull();
            }
        } else if (value.booleanValue()) {
            out.write("true");
        } else {
            out.write("false");
        }
    }

    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        JSONLexer lexer = parser.lexer;
        int token = lexer.token();
        if (token == 6) {
            lexer.nextToken(16);
            return Boolean.TRUE;
        } else if (token == 7) {
            lexer.nextToken(16);
            return Boolean.FALSE;
        } else if (token == 2) {
            int intValue = lexer.intValue();
            lexer.nextToken(16);
            if (intValue == 1) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } else {
            Object value = parser.parse();
            if (value == null) {
                return null;
            }
            return TypeUtils.castToBoolean(value);
        }
    }
}

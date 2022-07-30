package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;

public final class IntegerCodec implements ObjectSerializer, ObjectDeserializer {
    public static IntegerCodec instance = new IntegerCodec();

    private IntegerCodec() {
    }

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.out;
        Number value = (Number) object;
        if (value != null) {
            if (object instanceof Long) {
                out.writeLong(value.longValue());
            } else {
                out.writeInt(value.intValue());
            }
            if ((out.features & SerializerFeature.WriteClassName.mask) != 0) {
                Class<?> clazz = value.getClass();
                if (clazz == Byte.class) {
                    out.write(66);
                } else if (clazz == Short.class) {
                    out.write(83);
                } else if (clazz == Long.class) {
                    long longValue = value.longValue();
                    if (longValue <= 2147483647L && longValue >= -2147483648L && fieldType != Long.class) {
                        out.write(76);
                    }
                }
            }
        } else if ((out.features & SerializerFeature.WriteNullNumberAsZero.mask) != 0) {
            out.write(48);
        } else {
            out.writeNull();
        }
    }

    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        Number intObj;
        JSONLexer lexer = parser.lexer;
        int token = lexer.token();
        if (token == 8) {
            lexer.nextToken(16);
            return null;
        } else if (token == 2) {
            if (clazz == Long.TYPE || clazz == Long.class) {
                intObj = Long.valueOf(lexer.longValue());
            } else {
                try {
                    intObj = Integer.valueOf(lexer.intValue());
                } catch (NumberFormatException ex) {
                    throw new JSONException("int value overflow, field : " + fieldName, ex);
                }
            }
            lexer.nextToken(16);
            return intObj;
        } else if (token == 3) {
            BigDecimal decimalValue = lexer.decimalValue();
            lexer.nextToken(16);
            if (clazz == Long.TYPE || clazz == Long.class) {
                return Long.valueOf(decimalValue.longValue());
            }
            return Integer.valueOf(decimalValue.intValue());
        } else {
            Object value = parser.parse();
            try {
                if (clazz != Long.TYPE) {
                    if (clazz != Long.class) {
                        return TypeUtils.castToInt(value);
                    }
                }
                return TypeUtils.castToLong(value);
            } catch (Exception ex2) {
                throw new JSONException("cast error, field : " + fieldName + ", value " + value, ex2);
            }
        }
    }
}

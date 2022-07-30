package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

public class BigDecimalCodec implements ObjectSerializer, ObjectDeserializer {
    public static final BigDecimalCodec instance = new BigDecimalCodec();

    private BigDecimalCodec() {
    }

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            if ((out.features & SerializerFeature.WriteNullNumberAsZero.mask) != 0) {
                out.write(48);
            } else {
                out.writeNull();
            }
        } else if (object instanceof BigInteger) {
            out.write(((BigInteger) object).toString());
        } else {
            BigDecimal val = (BigDecimal) object;
            out.write(val.toString());
            if ((out.features & SerializerFeature.WriteClassName.mask) != 0 && fieldType != BigDecimal.class && val.scale() == 0) {
                out.write(46);
            }
        }
    }

    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        JSONLexer lexer = parser.lexer;
        int token = lexer.token();
        if (token == 2) {
            String val = lexer.numberString();
            lexer.nextToken(16);
            if (clazz == BigInteger.class) {
                return new BigInteger(val);
            }
            return new BigDecimal(val);
        } else if (token == 3) {
            BigDecimal val2 = lexer.decimalValue();
            lexer.nextToken(16);
            if (clazz == BigInteger.class) {
                return val2.toBigInteger();
            }
            return val2;
        } else {
            Object value = parser.parse();
            if (value == null) {
                return null;
            }
            if (clazz == BigInteger.class) {
                return TypeUtils.castToBigInteger(value);
            }
            return TypeUtils.castToBigDecimal(value);
        }
    }
}

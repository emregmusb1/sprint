package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONStreamAware;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Enumeration;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

public final class MiscCodec implements ObjectSerializer, ObjectDeserializer {
    public static final MiscCodec instance = new MiscCodec();

    private MiscCodec() {
    }

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            if (fieldType == Character.TYPE || fieldType == Character.class) {
                serializer.write("");
                return;
            }
            if ((out.features & SerializerFeature.WriteNullListAsEmpty.mask) != 0) {
                if (Enumeration.class.isAssignableFrom(TypeUtils.getClass(fieldType))) {
                    out.write("[]");
                    return;
                }
            }
            out.writeNull();
        } else if (object instanceof Pattern) {
            serializer.write(((Pattern) object).pattern());
        } else if (object instanceof TimeZone) {
            serializer.write(((TimeZone) object).getID());
        } else if (object instanceof Currency) {
            serializer.write(((Currency) object).getCurrencyCode());
        } else if (object instanceof Class) {
            serializer.write(((Class) object).getName());
        } else if (object instanceof Character) {
            Character value = (Character) object;
            if (value.charValue() == 0) {
                serializer.write("\u0000");
            } else {
                serializer.write(value.toString());
            }
        } else if (object instanceof SimpleDateFormat) {
            String pattern = ((SimpleDateFormat) object).toPattern();
            if ((out.features & SerializerFeature.WriteClassName.mask) == 0 || object.getClass() == fieldType) {
                out.writeString(pattern);
                return;
            }
            out.write(123);
            out.writeFieldName(JSON.DEFAULT_TYPE_KEY, false);
            serializer.write(object.getClass().getName());
            out.write(44);
            out.writeFieldName("val", false);
            out.writeString(pattern);
            out.write(125);
        } else if (object instanceof JSONStreamAware) {
            ((JSONStreamAware) object).writeJSONString(serializer.out);
        } else if (object instanceof JSONAware) {
            out.write(((JSONAware) object).toJSONString());
        } else if (object instanceof JSONSerializable) {
            ((JSONSerializable) object).write(serializer, fieldName, fieldType);
        } else if (object instanceof Enumeration) {
            Type elementType = null;
            if ((out.features & SerializerFeature.WriteClassName.mask) != 0 && (fieldType instanceof ParameterizedType)) {
                elementType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
            }
            Enumeration<?> e = (Enumeration) object;
            SerialContext context = serializer.context;
            serializer.setContext(context, object, fieldName, 0);
            int i = 0;
            try {
                out.write(91);
                while (e.hasMoreElements()) {
                    Object item = e.nextElement();
                    int i2 = i + 1;
                    if (i != 0) {
                        out.write(44);
                    }
                    if (item == null) {
                        out.writeNull();
                    } else {
                        serializer.config.get(item.getClass()).write(serializer, item, Integer.valueOf(i2 - 1), elementType);
                    }
                    i = i2;
                }
                out.write(93);
            } finally {
                serializer.context = context;
            }
        } else {
            serializer.write(object.toString());
        }
    }

    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        Object objVal;
        if (clazz == StackTraceElement.class) {
            return parseStackTraceElement(parser);
        }
        JSONLexer lexer = parser.lexer;
        if (parser.resolveStatus == 2) {
            parser.resolveStatus = 0;
            parser.accept(16);
            if (lexer.token() != 4) {
                throw new JSONException("syntax error");
            } else if ("val".equals(lexer.stringVal())) {
                lexer.nextToken();
                parser.accept(17);
                objVal = parser.parse();
                parser.accept(13);
            } else {
                throw new JSONException("syntax error");
            }
        } else {
            objVal = parser.parse();
        }
        if (objVal == null) {
            return null;
        }
        if (objVal instanceof String) {
            String strVal = (String) objVal;
            if (strVal.length() == 0) {
                return null;
            }
            if (clazz == UUID.class) {
                return UUID.fromString(strVal);
            }
            if (clazz == Class.class) {
                return TypeUtils.loadClass(strVal, parser.config.defaultClassLoader);
            }
            if (clazz == Locale.class) {
                String[] items = strVal.split("_");
                if (items.length == 1) {
                    return new Locale(items[0]);
                }
                if (items.length == 2) {
                    return new Locale(items[0], items[1]);
                }
                return new Locale(items[0], items[1], items[2]);
            } else if (clazz == URI.class) {
                return URI.create(strVal);
            } else {
                if (clazz == URL.class) {
                    try {
                        return new URL(strVal);
                    } catch (MalformedURLException e) {
                        throw new JSONException("create url error", e);
                    }
                } else if (clazz == Pattern.class) {
                    return Pattern.compile(strVal);
                } else {
                    if (clazz == Charset.class) {
                        return Charset.forName(strVal);
                    }
                    if (clazz == Currency.class) {
                        return Currency.getInstance(strVal);
                    }
                    if (clazz == SimpleDateFormat.class) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(strVal, parser.lexer.locale);
                        dateFormat.setTimeZone(parser.lexer.timeZone);
                        return dateFormat;
                    } else if (clazz == Character.TYPE || clazz == Character.class) {
                        return TypeUtils.castToChar(strVal);
                    } else {
                        return TimeZone.getTimeZone(strVal);
                    }
                }
            }
        } else {
            throw new JSONException("except string value");
        }
    }

    /* access modifiers changed from: protected */
    public <T> T parseStackTraceElement(DefaultJSONParser parser) {
        JSONLexer lexer = parser.lexer;
        if (lexer.token() == 8) {
            lexer.nextToken();
            return null;
        } else if (lexer.token() == 12 || lexer.token() == 16) {
            String declaringClass = null;
            String methodName = null;
            String fileName = null;
            int lineNumber = 0;
            while (true) {
                String key = lexer.scanSymbol(parser.symbolTable);
                if (key == null) {
                    if (lexer.token() == 13) {
                        lexer.nextToken(16);
                        break;
                    } else if (lexer.token() == 16 && (lexer.features & Feature.AllowArbitraryCommas.mask) != 0) {
                    }
                }
                lexer.nextTokenWithChar(':');
                if ("className".equals(key)) {
                    if (lexer.token() == 8) {
                        declaringClass = null;
                    } else if (lexer.token() == 4) {
                        declaringClass = lexer.stringVal();
                    } else {
                        throw new JSONException("syntax error");
                    }
                } else if ("methodName".equals(key)) {
                    if (lexer.token() == 8) {
                        methodName = null;
                    } else if (lexer.token() == 4) {
                        methodName = lexer.stringVal();
                    } else {
                        throw new JSONException("syntax error");
                    }
                } else if ("fileName".equals(key)) {
                    if (lexer.token() == 8) {
                        fileName = null;
                    } else if (lexer.token() == 4) {
                        fileName = lexer.stringVal();
                    } else {
                        throw new JSONException("syntax error");
                    }
                } else if ("lineNumber".equals(key)) {
                    if (lexer.token() == 8) {
                        lineNumber = 0;
                    } else if (lexer.token() == 2) {
                        lineNumber = lexer.intValue();
                    } else {
                        throw new JSONException("syntax error");
                    }
                } else if ("nativeMethod".equals(key)) {
                    if (lexer.token() == 8) {
                        lexer.nextToken(16);
                    } else if (lexer.token() == 6) {
                        lexer.nextToken(16);
                    } else if (lexer.token() == 7) {
                        lexer.nextToken(16);
                    } else {
                        throw new JSONException("syntax error");
                    }
                } else if (key != JSON.DEFAULT_TYPE_KEY) {
                    throw new JSONException("syntax error : " + key);
                } else if (lexer.token() == 4) {
                    String elementType = lexer.stringVal();
                    if (!elementType.equals("java.lang.StackTraceElement")) {
                        throw new JSONException("syntax error : " + elementType);
                    }
                } else if (lexer.token() != 8) {
                    throw new JSONException("syntax error");
                }
                if (lexer.token() == 13) {
                    lexer.nextToken(16);
                    break;
                }
            }
            return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
        } else {
            throw new JSONException("syntax error: " + JSONToken.name(lexer.token()));
        }
    }
}

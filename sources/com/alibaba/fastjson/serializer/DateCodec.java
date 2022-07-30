package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateCodec implements ObjectSerializer, ObjectDeserializer {
    public static final DateCodec instance = new DateCodec();

    private DateCodec() {
    }

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        Date date;
        char[] buf;
        JSONSerializer jSONSerializer = serializer;
        Object obj = object;
        SerializeWriter out = jSONSerializer.out;
        if (obj == null) {
            out.writeNull();
            return;
        }
        if ((out.features & SerializerFeature.WriteClassName.mask) == 0) {
            Type type = fieldType;
        } else if (object.getClass() != fieldType) {
            if (object.getClass() == Date.class) {
                out.write("new Date(");
                out.writeLong(((Date) obj).getTime());
                out.write(41);
                return;
            }
            out.write(123);
            out.writeFieldName(JSON.DEFAULT_TYPE_KEY, false);
            jSONSerializer.write(object.getClass().getName());
            out.write(44);
            out.writeFieldName("val", false);
            out.writeLong(((Date) obj).getTime());
            out.write(125);
            return;
        }
        if (obj instanceof Calendar) {
            date = ((Calendar) obj).getTime();
        } else {
            date = (Date) obj;
        }
        if ((out.features & SerializerFeature.WriteDateUseDateFormat.mask) != 0) {
            DateFormat format = serializer.getDateFormat();
            if (format == null) {
                format = new SimpleDateFormat(JSON.DEFFAULT_DATE_FORMAT, jSONSerializer.locale);
                format.setTimeZone(jSONSerializer.timeZone);
            }
            out.writeString(format.format(date));
            return;
        }
        long time = date.getTime();
        if ((out.features & SerializerFeature.UseISO8601DateFormat.mask) != 0) {
            if ((out.features & SerializerFeature.UseSingleQuotes.mask) != 0) {
                out.write(39);
            } else {
                out.write(34);
            }
            Calendar calendar = Calendar.getInstance(jSONSerializer.timeZone, jSONSerializer.locale);
            calendar.setTimeInMillis(time);
            int year = calendar.get(1);
            int month = calendar.get(2) + 1;
            int day = calendar.get(5);
            int hour = calendar.get(11);
            int minute = calendar.get(12);
            int second = calendar.get(13);
            int millis = calendar.get(14);
            if (millis != 0) {
                buf = "0000-00-00T00:00:00.000".toCharArray();
                Date date2 = date;
                SerializeWriter.getChars((long) millis, 23, buf);
                SerializeWriter.getChars((long) second, 19, buf);
                SerializeWriter.getChars((long) minute, 16, buf);
                SerializeWriter.getChars((long) hour, 13, buf);
                SerializeWriter.getChars((long) day, 10, buf);
                SerializeWriter.getChars((long) month, 7, buf);
                SerializeWriter.getChars((long) year, 4, buf);
            } else {
                if (second == 0 && minute == 0 && hour == 0) {
                    buf = "0000-00-00".toCharArray();
                    SerializeWriter.getChars((long) day, 10, buf);
                    SerializeWriter.getChars((long) month, 7, buf);
                    SerializeWriter.getChars((long) year, 4, buf);
                } else {
                    buf = "0000-00-00T00:00:00".toCharArray();
                    SerializeWriter.getChars((long) second, 19, buf);
                    SerializeWriter.getChars((long) minute, 16, buf);
                    SerializeWriter.getChars((long) hour, 13, buf);
                    SerializeWriter.getChars((long) day, 10, buf);
                    SerializeWriter.getChars((long) month, 7, buf);
                    SerializeWriter.getChars((long) year, 4, buf);
                }
            }
            out.write(buf);
            if ((out.features & SerializerFeature.UseSingleQuotes.mask) != 0) {
                out.write(39);
            } else {
                out.write(34);
            }
        } else {
            out.writeLong(time);
        }
    }

    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        return deserialze(parser, clazz, fieldName, (String) null);
    }

    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName, String format) {
        Object val;
        JSONLexer lexer = parser.lexer;
        int token = lexer.token();
        if (token == 2) {
            val = Long.valueOf(lexer.longValue());
            lexer.nextToken(16);
        } else if (token == 4) {
            String strVal = lexer.stringVal();
            Object val2 = strVal;
            lexer.nextToken(16);
            if ((lexer.features & Feature.AllowISO8601DateFormat.mask) != 0) {
                JSONLexer iso8601Lexer = new JSONLexer(strVal);
                if (iso8601Lexer.scanISO8601DateIfMatch(true)) {
                    Calendar calendar = iso8601Lexer.getCalendar();
                    if (clazz == Calendar.class) {
                        iso8601Lexer.close();
                        return calendar;
                    }
                    val2 = calendar.getTime();
                }
                iso8601Lexer.close();
                val = val2;
            } else {
                val = val2;
            }
        } else if (token == 8) {
            lexer.nextToken();
            val = null;
        } else if (token == 12) {
            lexer.nextToken();
            if (lexer.token() == 4) {
                if (JSON.DEFAULT_TYPE_KEY.equals(lexer.stringVal())) {
                    lexer.nextToken();
                    parser.accept(17);
                    Class<?> type = TypeUtils.loadClass(lexer.stringVal(), parser.config.defaultClassLoader);
                    if (type != null) {
                        clazz = type;
                    }
                    parser.accept(4);
                    parser.accept(16);
                }
                lexer.nextTokenWithChar(':');
                if (lexer.token() == 2) {
                    long timeMillis = lexer.longValue();
                    lexer.nextToken();
                    Object val3 = Long.valueOf(timeMillis);
                    parser.accept(13);
                    val = val3;
                } else {
                    throw new JSONException("syntax error : " + lexer.tokenName());
                }
            } else {
                throw new JSONException("syntax error");
            }
        } else if (parser.resolveStatus == 2) {
            parser.resolveStatus = 0;
            parser.accept(16);
            if (lexer.token() != 4) {
                throw new JSONException("syntax error");
            } else if ("val".equals(lexer.stringVal())) {
                lexer.nextToken();
                parser.accept(17);
                val = parser.parse();
                parser.accept(13);
            } else {
                throw new JSONException("syntax error");
            }
        } else {
            val = parser.parse();
        }
        T cast = cast(parser, clazz, fieldName, val, format);
        if (clazz != Calendar.class || (cast instanceof Calendar)) {
            return cast;
        }
        Date date = (Date) cast;
        if (date == null) {
            return null;
        }
        Calendar calendar2 = Calendar.getInstance(lexer.timeZone, lexer.locale);
        calendar2.setTime(date);
        return calendar2;
    }

    /* access modifiers changed from: protected */
    public <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val, String format) {
        DateFormat dateFormat;
        if (val == null) {
            return null;
        }
        if (val instanceof Date) {
            return val;
        }
        if (val instanceof Number) {
            return new Date(((Number) val).longValue());
        }
        if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.length() == 0) {
                return null;
            }
            JSONLexer dateLexer = new JSONLexer(strVal);
            try {
                if (dateLexer.scanISO8601DateIfMatch(false)) {
                    Calendar calendar = dateLexer.getCalendar();
                    if (clazz == Calendar.class) {
                        return calendar;
                    }
                    T time = calendar.getTime();
                    dateLexer.close();
                    return time;
                }
                dateLexer.close();
                if (format != null) {
                    dateFormat = new SimpleDateFormat(format);
                } else {
                    dateFormat = parser.getDateFormat();
                }
                try {
                    return dateFormat.parse(strVal);
                } catch (ParseException e) {
                    return new Date(Long.parseLong(strVal));
                }
            } finally {
                dateLexer.close();
            }
        } else {
            throw new JSONException("parse error");
        }
    }
}

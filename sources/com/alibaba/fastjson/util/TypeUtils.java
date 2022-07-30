package com.alibaba.fastjson.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.ParserConfig;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessControlException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TypeUtils {
    public static boolean compatibleWithJavaBean = false;
    private static ConcurrentMap<String, Class<?>> mappings = new ConcurrentHashMap();
    private static boolean setAccessibleEnable = true;

    static {
        mappings.put("byte", Byte.TYPE);
        mappings.put("short", Short.TYPE);
        mappings.put("int", Integer.TYPE);
        mappings.put("long", Long.TYPE);
        mappings.put("float", Float.TYPE);
        mappings.put("double", Double.TYPE);
        mappings.put("boolean", Boolean.TYPE);
        mappings.put("char", Character.TYPE);
        mappings.put("[byte", byte[].class);
        mappings.put("[short", short[].class);
        mappings.put("[int", int[].class);
        mappings.put("[long", long[].class);
        mappings.put("[float", float[].class);
        mappings.put("[double", double[].class);
        mappings.put("[boolean", boolean[].class);
        mappings.put("[char", char[].class);
        mappings.put(HashMap.class.getName(), HashMap.class);
    }

    public static final String castToString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static final Byte castToByte(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return Byte.valueOf(((Number) value).byteValue());
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 || "null".equals(strVal)) {
                return null;
            }
            return Byte.valueOf(Byte.parseByte(strVal));
        }
        throw new JSONException("can not cast to byte, value : " + value);
    }

    public static final Character castToChar(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Character) {
            return (Character) value;
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            if (strVal.length() == 1) {
                return Character.valueOf(strVal.charAt(0));
            }
            throw new JSONException("can not cast to byte, value : " + value);
        }
        throw new JSONException("can not cast to byte, value : " + value);
    }

    public static final Short castToShort(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return Short.valueOf(((Number) value).shortValue());
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 || "null".equals(strVal)) {
                return null;
            }
            return Short.valueOf(Short.parseShort(strVal));
        }
        throw new JSONException("can not cast to short, value : " + value);
    }

    public static final BigDecimal castToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        String strVal = value.toString();
        if (strVal.length() == 0 || "null".equals(strVal)) {
            return null;
        }
        return new BigDecimal(strVal);
    }

    public static final BigInteger castToBigInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }
        if ((value instanceof Float) || (value instanceof Double)) {
            return BigInteger.valueOf(((Number) value).longValue());
        }
        String strVal = value.toString();
        if (strVal.length() == 0 || "null".equals(strVal)) {
            return null;
        }
        return new BigInteger(strVal);
    }

    public static final Float castToFloat(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return Float.valueOf(((Number) value).floatValue());
        }
        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() == 0 || "null".equals(strVal)) {
                return null;
            }
            return Float.valueOf(Float.parseFloat(strVal));
        }
        throw new JSONException("can not cast to float, value : " + value);
    }

    public static final Double castToDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return Double.valueOf(((Number) value).doubleValue());
        }
        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() == 0 || "null".equals(strVal)) {
                return null;
            }
            return Double.valueOf(Double.parseDouble(strVal));
        }
        throw new JSONException("can not cast to double, value : " + value);
    }

    public static final Date castToDate(Object value) {
        String format;
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            return ((Calendar) value).getTime();
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        long longValue = -1;
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.indexOf(45) != -1) {
                if (strVal.length() == JSON.DEFFAULT_DATE_FORMAT.length()) {
                    format = JSON.DEFFAULT_DATE_FORMAT;
                } else if (strVal.length() == 10) {
                    format = "yyyy-MM-dd";
                } else if (strVal.length() == "yyyy-MM-dd HH:mm:ss".length()) {
                    format = "yyyy-MM-dd HH:mm:ss";
                } else {
                    format = "yyyy-MM-dd HH:mm:ss.SSS";
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat(format, JSON.defaultLocale);
                dateFormat.setTimeZone(JSON.defaultTimeZone);
                try {
                    return dateFormat.parse(strVal);
                } catch (ParseException e) {
                    throw new JSONException("can not cast to Date, value : " + strVal);
                }
            } else if (strVal.length() == 0 || "null".equals(strVal)) {
                return null;
            } else {
                longValue = Long.parseLong(strVal);
            }
        }
        if (longValue >= 0) {
            return new Date(longValue);
        }
        throw new JSONException("can not cast to Date, value : " + value);
    }

    public static final Long castToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return Long.valueOf(((Number) value).longValue());
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 || "null".equals(strVal)) {
                return null;
            }
            try {
                return Long.valueOf(Long.parseLong(strVal));
            } catch (NumberFormatException e) {
                JSONLexer dateParser = new JSONLexer(strVal);
                Calendar calendar = null;
                if (dateParser.scanISO8601DateIfMatch(false)) {
                    calendar = dateParser.getCalendar();
                }
                dateParser.close();
                if (calendar != null) {
                    return Long.valueOf(calendar.getTimeInMillis());
                }
            }
        }
        throw new JSONException("can not cast to long, value : " + value);
    }

    public static final Integer castToInt(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return Integer.valueOf(((Number) value).intValue());
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 || "null".equals(strVal)) {
                return null;
            }
            return Integer.valueOf(Integer.parseInt(strVal));
        }
        throw new JSONException("can not cast to int, value : " + value);
    }

    public static final byte[] castToBytes(Object value) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        if (value instanceof String) {
            return JSONLexer.decodeFast((String) value);
        }
        throw new JSONException("can not cast to int, value : " + value);
    }

    public static final Boolean castToBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            boolean z = true;
            if (((Number) value).intValue() != 1) {
                z = false;
            }
            return Boolean.valueOf(z);
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 || "null".equals(strVal)) {
                return null;
            }
            if ("true".equalsIgnoreCase(strVal) || "1".equals(strVal)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(strVal) || "0".equals(strVal)) {
                return Boolean.FALSE;
            }
        }
        throw new JSONException("can not cast to int, value : " + value);
    }

    public static final <T> T castToJavaBean(Object obj, Class<T> clazz) {
        return cast(obj, clazz, ParserConfig.global);
    }

    public static final <T> T cast(Object obj, Class<T> clazz, ParserConfig mapping) {
        Calendar calendar;
        if (obj == null) {
            return null;
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        } else if (clazz == obj.getClass()) {
            return obj;
        } else {
            if (!(obj instanceof Map)) {
                if (clazz.isArray()) {
                    if (obj instanceof Collection) {
                        Collection<Object> collection = (Collection) obj;
                        int index = 0;
                        Object array = Array.newInstance(clazz.getComponentType(), collection.size());
                        for (Object item : collection) {
                            Array.set(array, index, cast(item, clazz.getComponentType(), mapping));
                            index++;
                        }
                        return array;
                    } else if (clazz == byte[].class) {
                        return castToBytes(obj);
                    }
                }
                if (clazz.isAssignableFrom(obj.getClass())) {
                    return obj;
                }
                if (clazz == Boolean.TYPE || clazz == Boolean.class) {
                    return castToBoolean(obj);
                }
                if (clazz == Byte.TYPE || clazz == Byte.class) {
                    return castToByte(obj);
                }
                if (clazz == Short.TYPE || clazz == Short.class) {
                    return castToShort(obj);
                }
                if (clazz == Integer.TYPE || clazz == Integer.class) {
                    return castToInt(obj);
                }
                if (clazz == Long.TYPE || clazz == Long.class) {
                    return castToLong(obj);
                }
                if (clazz == Float.TYPE || clazz == Float.class) {
                    return castToFloat(obj);
                }
                if (clazz == Double.TYPE || clazz == Double.class) {
                    return castToDouble(obj);
                }
                if (clazz == String.class) {
                    return castToString(obj);
                }
                if (clazz == BigDecimal.class) {
                    return castToBigDecimal(obj);
                }
                if (clazz == BigInteger.class) {
                    return castToBigInteger(obj);
                }
                if (clazz == Date.class) {
                    return castToDate(obj);
                }
                if (clazz.isEnum()) {
                    return castToEnum(obj, clazz, mapping);
                }
                if (Calendar.class.isAssignableFrom(clazz)) {
                    Date date = castToDate(obj);
                    if (clazz == Calendar.class) {
                        calendar = Calendar.getInstance(JSON.defaultTimeZone, JSON.defaultLocale);
                    } else {
                        try {
                            calendar = clazz.newInstance();
                        } catch (Exception e) {
                            throw new JSONException("can not cast to : " + clazz.getName(), e);
                        }
                    }
                    calendar.setTime(date);
                    return calendar;
                }
                if (obj instanceof String) {
                    String strVal = (String) obj;
                    if (strVal.length() == 0 || "null".equals(strVal)) {
                        return null;
                    }
                    if (clazz == Currency.class) {
                        return Currency.getInstance(strVal);
                    }
                }
                throw new JSONException("can not cast to : " + clazz.getName());
            } else if (clazz == Map.class) {
                return obj;
            } else {
                Map map = (Map) obj;
                if (clazz != Object.class || map.containsKey(JSON.DEFAULT_TYPE_KEY)) {
                    return castToJavaBean((Map) obj, clazz, mapping);
                }
                return obj;
            }
        }
    }

    public static final <T> T castToEnum(Object obj, Class<T> clazz, ParserConfig mapping) {
        try {
            if (obj instanceof String) {
                String name = (String) obj;
                if (name.length() == 0) {
                    return null;
                }
                return Enum.valueOf(clazz, name);
            }
            if (obj instanceof Number) {
                int ordinal = ((Number) obj).intValue();
                Object[] values = clazz.getEnumConstants();
                if (ordinal < values.length) {
                    return values[ordinal];
                }
            }
            throw new JSONException("can not cast to : " + clazz.getName());
        } catch (Exception ex) {
            throw new JSONException("can not cast to : " + clazz.getName(), ex);
        }
    }

    public static final <T> T cast(Object obj, Type type, ParserConfig mapping) {
        if (obj == null) {
            return null;
        }
        if (type instanceof Class) {
            return cast(obj, (Class) type, mapping);
        }
        if (type instanceof ParameterizedType) {
            return cast(obj, (ParameterizedType) type, mapping);
        }
        if (obj instanceof String) {
            String strVal = (String) obj;
            if (strVal.length() == 0 || "null".equals(strVal)) {
                return null;
            }
        }
        if (type instanceof TypeVariable) {
            return obj;
        }
        throw new JSONException("can not cast to : " + type);
    }

    public static final <T> T cast(Object obj, ParameterizedType type, ParserConfig mapping) {
        Collection collection;
        Type rawTye = type.getRawType();
        if (rawTye == Set.class || rawTye == HashSet.class || rawTye == TreeSet.class || rawTye == List.class || rawTye == ArrayList.class) {
            Type itemType = type.getActualTypeArguments()[0];
            if (obj instanceof Iterable) {
                if (rawTye == Set.class || rawTye == HashSet.class) {
                    collection = new HashSet();
                } else if (rawTye == TreeSet.class) {
                    collection = new TreeSet();
                } else {
                    collection = new ArrayList();
                }
                for (Object item : (Iterable) obj) {
                    collection.add(cast(item, itemType, mapping));
                }
                return collection;
            }
        }
        if (rawTye == Map.class || rawTye == HashMap.class) {
            Type keyType = type.getActualTypeArguments()[0];
            Type valueType = type.getActualTypeArguments()[1];
            if (obj instanceof Map) {
                Map map = new HashMap();
                for (Map.Entry entry : ((Map) obj).entrySet()) {
                    map.put(cast(entry.getKey(), keyType, mapping), cast(entry.getValue(), valueType, mapping));
                }
                return map;
            }
        }
        if (obj instanceof String) {
            String strVal = (String) obj;
            if (strVal.length() == 0 || "null".equals(strVal)) {
                return null;
            }
        }
        if (type.getActualTypeArguments().length == 1 && (type.getActualTypeArguments()[0] instanceof WildcardType)) {
            return cast(obj, rawTye, mapping);
        }
        throw new JSONException("can not cast to : " + type);
    }

    /* JADX WARNING: type inference failed for: r1v1, types: [com.alibaba.fastjson.parser.deserializer.ObjectDeserializer] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final <T> T castToJavaBean(java.util.Map<java.lang.String, java.lang.Object> r6, java.lang.Class<T> r7, com.alibaba.fastjson.parser.ParserConfig r8) {
        /*
            java.lang.Class<java.lang.StackTraceElement> r0 = java.lang.StackTraceElement.class
            if (r7 != r0) goto L_0x0032
            java.lang.String r0 = "className"
            java.lang.Object r0 = r6.get(r0)     // Catch:{ Exception -> 0x00af }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x00af }
            java.lang.String r1 = "methodName"
            java.lang.Object r1 = r6.get(r1)     // Catch:{ Exception -> 0x00af }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x00af }
            java.lang.String r2 = "fileName"
            java.lang.Object r2 = r6.get(r2)     // Catch:{ Exception -> 0x00af }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x00af }
            java.lang.String r3 = "lineNumber"
            java.lang.Object r3 = r6.get(r3)     // Catch:{ Exception -> 0x00af }
            java.lang.Number r3 = (java.lang.Number) r3     // Catch:{ Exception -> 0x00af }
            if (r3 != 0) goto L_0x0028
            r4 = 0
            goto L_0x002c
        L_0x0028:
            int r4 = r3.intValue()     // Catch:{ Exception -> 0x00af }
        L_0x002c:
            java.lang.StackTraceElement r3 = new java.lang.StackTraceElement     // Catch:{ Exception -> 0x00af }
            r3.<init>(r0, r1, r2, r4)     // Catch:{ Exception -> 0x00af }
            return r3
        L_0x0032:
            java.lang.String r0 = "@type"
            java.lang.Object r0 = r6.get(r0)     // Catch:{ Exception -> 0x00af }
            boolean r1 = r0 instanceof java.lang.String     // Catch:{ Exception -> 0x00af }
            if (r1 == 0) goto L_0x0068
            r1 = r0
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x00af }
            r2 = 0
            java.lang.Class r2 = loadClass(r1, r2)     // Catch:{ Exception -> 0x00af }
            if (r2 == 0) goto L_0x0051
            boolean r3 = r2.equals(r7)     // Catch:{ Exception -> 0x00af }
            if (r3 != 0) goto L_0x0068
            java.lang.Object r3 = castToJavaBean(r6, r2, r8)     // Catch:{ Exception -> 0x00af }
            return r3
        L_0x0051:
            java.lang.ClassNotFoundException r3 = new java.lang.ClassNotFoundException     // Catch:{ Exception -> 0x00af }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00af }
            r4.<init>()     // Catch:{ Exception -> 0x00af }
            r4.append(r1)     // Catch:{ Exception -> 0x00af }
            java.lang.String r5 = " not found"
            r4.append(r5)     // Catch:{ Exception -> 0x00af }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x00af }
            r3.<init>(r4)     // Catch:{ Exception -> 0x00af }
            throw r3     // Catch:{ Exception -> 0x00af }
        L_0x0068:
            boolean r0 = r7.isInterface()     // Catch:{ Exception -> 0x00af }
            if (r0 == 0) goto L_0x008e
            boolean r0 = r6 instanceof com.alibaba.fastjson.JSONObject     // Catch:{ Exception -> 0x00af }
            if (r0 == 0) goto L_0x0076
            r0 = r6
            com.alibaba.fastjson.JSONObject r0 = (com.alibaba.fastjson.JSONObject) r0     // Catch:{ Exception -> 0x00af }
            goto L_0x007b
        L_0x0076:
            com.alibaba.fastjson.JSONObject r0 = new com.alibaba.fastjson.JSONObject     // Catch:{ Exception -> 0x00af }
            r0.<init>((java.util.Map<java.lang.String, java.lang.Object>) r6)     // Catch:{ Exception -> 0x00af }
        L_0x007b:
            java.lang.Thread r1 = java.lang.Thread.currentThread()     // Catch:{ Exception -> 0x00af }
            java.lang.ClassLoader r1 = r1.getContextClassLoader()     // Catch:{ Exception -> 0x00af }
            r2 = 1
            java.lang.Class[] r2 = new java.lang.Class[r2]     // Catch:{ Exception -> 0x00af }
            r3 = 0
            r2[r3] = r7     // Catch:{ Exception -> 0x00af }
            java.lang.Object r1 = java.lang.reflect.Proxy.newProxyInstance(r1, r2, r0)     // Catch:{ Exception -> 0x00af }
            return r1
        L_0x008e:
            if (r8 != 0) goto L_0x0093
            com.alibaba.fastjson.parser.ParserConfig r0 = com.alibaba.fastjson.parser.ParserConfig.global     // Catch:{ Exception -> 0x00af }
            r8 = r0
        L_0x0093:
            r0 = 0
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r1 = r8.getDeserializer(r7)     // Catch:{ Exception -> 0x00af }
            boolean r2 = r1 instanceof com.alibaba.fastjson.parser.JavaBeanDeserializer     // Catch:{ Exception -> 0x00af }
            if (r2 == 0) goto L_0x00a0
            r2 = r1
            com.alibaba.fastjson.parser.JavaBeanDeserializer r2 = (com.alibaba.fastjson.parser.JavaBeanDeserializer) r2     // Catch:{ Exception -> 0x00af }
            r0 = r2
        L_0x00a0:
            if (r0 == 0) goto L_0x00a7
            java.lang.Object r2 = r0.createInstance((java.util.Map<java.lang.String, java.lang.Object>) r6, (com.alibaba.fastjson.parser.ParserConfig) r8)     // Catch:{ Exception -> 0x00af }
            return r2
        L_0x00a7:
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException     // Catch:{ Exception -> 0x00af }
            java.lang.String r3 = "can not get javaBeanDeserializer"
            r2.<init>(r3)     // Catch:{ Exception -> 0x00af }
            throw r2     // Catch:{ Exception -> 0x00af }
        L_0x00af:
            r0 = move-exception
            com.alibaba.fastjson.JSONException r1 = new com.alibaba.fastjson.JSONException
            java.lang.String r2 = r0.getMessage()
            r1.<init>(r2, r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.util.TypeUtils.castToJavaBean(java.util.Map, java.lang.Class, com.alibaba.fastjson.parser.ParserConfig):java.lang.Object");
    }

    public static Class<?> loadClass(String className, ClassLoader classLoader) {
        if (className == null || className.length() == 0) {
            return null;
        }
        Class<?> clazz = (Class) mappings.get(className);
        if (clazz != null) {
            return clazz;
        }
        if (className.charAt(0) == '[') {
            return Array.newInstance(loadClass(className.substring(1), classLoader), 0).getClass();
        }
        if (className.startsWith("L") && className.endsWith(";")) {
            return loadClass(className.substring(1, className.length() - 1), classLoader);
        }
        if (classLoader != null) {
            try {
                clazz = classLoader.loadClass(className);
                mappings.put(className, clazz);
                return clazz;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                Class<?> clazz2 = contextClassLoader.loadClass(className);
                mappings.put(className, clazz2);
                return clazz2;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        try {
            clazz = Class.forName(className);
            mappings.put(className, clazz);
            return clazz;
        } catch (Exception e3) {
            e3.printStackTrace();
            return clazz;
        }
    }

    /* JADX WARNING: type inference failed for: r39v0, types: [java.lang.Class<?>, java.lang.Class] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<com.alibaba.fastjson.util.FieldInfo> computeGetters(java.lang.Class<?> r39, int r40, boolean r41, com.alibaba.fastjson.annotation.JSONType r42, java.util.Map<java.lang.String, java.lang.String> r43, boolean r44, boolean r45, boolean r46, com.alibaba.fastjson.PropertyNamingStrategy r47) {
        /*
            r11 = r39
            r12 = r40
            r13 = r42
            r14 = r43
            r15 = r47
            java.util.LinkedHashMap r0 = new java.util.LinkedHashMap
            r0.<init>()
            r10 = r0
            java.lang.reflect.Field[] r9 = r39.getDeclaredFields()
            r16 = 0
            r17 = 0
            if (r41 != 0) goto L_0x0406
            java.lang.reflect.Method[] r8 = r39.getMethods()
            int r7 = r8.length
            r6 = 0
        L_0x0020:
            if (r6 >= r7) goto L_0x0403
            r5 = r8[r6]
            java.lang.String r4 = r5.getName()
            r0 = 0
            r1 = 0
            int r2 = r5.getModifiers()
            r2 = r2 & 8
            if (r2 != 0) goto L_0x03e7
            java.lang.Class r2 = r5.getReturnType()
            java.lang.Class r3 = java.lang.Void.TYPE
            boolean r2 = r2.equals(r3)
            if (r2 != 0) goto L_0x03da
            java.lang.Class[] r2 = r5.getParameterTypes()
            int r2 = r2.length
            if (r2 != 0) goto L_0x03cd
            java.lang.Class r2 = r5.getReturnType()
            java.lang.Class<java.lang.ClassLoader> r3 = java.lang.ClassLoader.class
            if (r2 == r3) goto L_0x03c0
            java.lang.Class r2 = r5.getDeclaringClass()
            java.lang.Class<java.lang.Object> r3 = java.lang.Object.class
            if (r2 == r3) goto L_0x03b3
            java.lang.String r2 = "getMetaClass"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0077
            java.lang.Class r2 = r5.getReturnType()
            java.lang.String r2 = r2.getName()
            java.lang.String r3 = "groovy.lang.MetaClass"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x0077
            r21 = r6
            r22 = r7
            r29 = r8
            r13 = r9
            r12 = r10
            goto L_0x03f3
        L_0x0077:
            if (r45 == 0) goto L_0x0082
            java.lang.Class<com.alibaba.fastjson.annotation.JSONField> r2 = com.alibaba.fastjson.annotation.JSONField.class
            java.lang.annotation.Annotation r2 = r5.getAnnotation(r2)
            com.alibaba.fastjson.annotation.JSONField r2 = (com.alibaba.fastjson.annotation.JSONField) r2
            goto L_0x0084
        L_0x0082:
            r2 = r16
        L_0x0084:
            if (r2 != 0) goto L_0x008f
            if (r45 == 0) goto L_0x008f
            com.alibaba.fastjson.annotation.JSONField r2 = getSupperMethodAnnotation(r11, r5)
            r18 = r2
            goto L_0x0091
        L_0x008f:
            r18 = r2
        L_0x0091:
            if (r18 == 0) goto L_0x0126
            boolean r2 = r18.serialize()
            if (r2 != 0) goto L_0x00a3
            r21 = r6
            r22 = r7
            r29 = r8
            r13 = r9
            r12 = r10
            goto L_0x03f3
        L_0x00a3:
            int r19 = r18.ordinal()
            com.alibaba.fastjson.serializer.SerializerFeature[] r0 = r18.serialzeFeatures()
            int r20 = com.alibaba.fastjson.serializer.SerializerFeature.of(r0)
            java.lang.String r0 = r18.name()
            int r0 = r0.length()
            if (r0 == 0) goto L_0x0114
            java.lang.String r0 = r18.name()
            if (r14 == 0) goto L_0x00d4
            java.lang.Object r1 = r14.get(r0)
            r0 = r1
            java.lang.String r0 = (java.lang.String) r0
            if (r0 != 0) goto L_0x00d2
            r21 = r6
            r22 = r7
            r29 = r8
            r13 = r9
            r12 = r10
            goto L_0x03f3
        L_0x00d2:
            r3 = r0
            goto L_0x00d5
        L_0x00d4:
            r3 = r0
        L_0x00d5:
            setAccessible(r11, r5, r12)
            com.alibaba.fastjson.util.FieldInfo r2 = new com.alibaba.fastjson.util.FieldInfo
            r21 = 0
            r22 = 0
            r23 = 0
            r24 = 1
            r0 = r2
            r1 = r3
            r25 = r2
            r2 = r5
            r26 = r3
            r3 = r21
            r27 = r4
            r4 = r39
            r28 = r5
            r5 = r22
            r21 = r6
            r6 = r19
            r22 = r7
            r7 = r20
            r29 = r8
            r8 = r18
            r30 = r9
            r9 = r23
            r12 = r10
            r10 = r24
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r1 = r25
            r0 = r26
            r12.put(r0, r1)
            r13 = r30
            goto L_0x03f3
        L_0x0114:
            r27 = r4
            r28 = r5
            r21 = r6
            r22 = r7
            r29 = r8
            r30 = r9
            r12 = r10
            r0 = r19
            r1 = r20
            goto L_0x0133
        L_0x0126:
            r27 = r4
            r28 = r5
            r21 = r6
            r22 = r7
            r29 = r8
            r30 = r9
            r12 = r10
        L_0x0133:
            java.lang.String r2 = "get"
            r10 = r27
            boolean r2 = r10.startsWith(r2)
            r9 = 102(0x66, float:1.43E-43)
            r8 = 95
            r7 = 3
            if (r2 == 0) goto L_0x0289
            int r2 = r10.length()
            r3 = 4
            if (r2 < r3) goto L_0x027f
            java.lang.String r2 = "getClass"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x0155
            r13 = r30
            goto L_0x03f3
        L_0x0155:
            char r6 = r10.charAt(r7)
            boolean r2 = java.lang.Character.isUpperCase(r6)
            if (r2 == 0) goto L_0x0188
            boolean r2 = compatibleWithJavaBean
            if (r2 == 0) goto L_0x016c
            java.lang.String r2 = r10.substring(r7)
            java.lang.String r2 = decapitalize(r2)
            goto L_0x01af
        L_0x016c:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            char r4 = r10.charAt(r7)
            char r4 = java.lang.Character.toLowerCase(r4)
            r2.append(r4)
            java.lang.String r3 = r10.substring(r3)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            goto L_0x01af
        L_0x0188:
            if (r6 != r8) goto L_0x018f
            java.lang.String r2 = r10.substring(r3)
            goto L_0x01af
        L_0x018f:
            if (r6 != r9) goto L_0x0196
            java.lang.String r2 = r10.substring(r7)
            goto L_0x01af
        L_0x0196:
            int r2 = r10.length()
            r4 = 5
            if (r2 < r4) goto L_0x0273
            char r2 = r10.charAt(r3)
            boolean r2 = java.lang.Character.isUpperCase(r2)
            if (r2 == 0) goto L_0x0273
            java.lang.String r2 = r10.substring(r7)
            java.lang.String r2 = decapitalize(r2)
        L_0x01af:
            boolean r3 = isJSONTypeIgnore(r11, r13, r2)
            if (r3 == 0) goto L_0x01b9
            r13 = r30
            goto L_0x03f3
        L_0x01b9:
            r5 = r30
            java.lang.reflect.Field r4 = getField(r11, r2, r5)
            r3 = 0
            if (r4 == 0) goto L_0x021f
            if (r45 == 0) goto L_0x01cd
            java.lang.Class<com.alibaba.fastjson.annotation.JSONField> r7 = com.alibaba.fastjson.annotation.JSONField.class
            java.lang.annotation.Annotation r7 = r4.getAnnotation(r7)
            com.alibaba.fastjson.annotation.JSONField r7 = (com.alibaba.fastjson.annotation.JSONField) r7
            goto L_0x01cf
        L_0x01cd:
            r7 = r16
        L_0x01cf:
            r3 = r7
            if (r3 == 0) goto L_0x0218
            boolean r7 = r3.serialize()
            if (r7 != 0) goto L_0x01db
            r13 = r5
            goto L_0x03f3
        L_0x01db:
            int r0 = r3.ordinal()
            com.alibaba.fastjson.serializer.SerializerFeature[] r7 = r3.serialzeFeatures()
            int r1 = com.alibaba.fastjson.serializer.SerializerFeature.of(r7)
            java.lang.String r7 = r3.name()
            int r7 = r7.length()
            if (r7 == 0) goto L_0x0211
            java.lang.String r2 = r3.name()
            if (r14 == 0) goto L_0x020a
            java.lang.Object r7 = r14.get(r2)
            r2 = r7
            java.lang.String r2 = (java.lang.String) r2
            if (r2 != 0) goto L_0x0203
            r13 = r5
            goto L_0x03f3
        L_0x0203:
            r20 = r0
            r23 = r1
            r24 = r3
            goto L_0x0225
        L_0x020a:
            r20 = r0
            r23 = r1
            r24 = r3
            goto L_0x0225
        L_0x0211:
            r20 = r0
            r23 = r1
            r24 = r3
            goto L_0x0225
        L_0x0218:
            r20 = r0
            r23 = r1
            r24 = r3
            goto L_0x0225
        L_0x021f:
            r20 = r0
            r23 = r1
            r24 = r3
        L_0x0225:
            if (r15 == 0) goto L_0x022b
            java.lang.String r2 = r15.translate(r2)
        L_0x022b:
            if (r14 == 0) goto L_0x023a
            java.lang.Object r0 = r14.get(r2)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 != 0) goto L_0x0238
            r13 = r5
            goto L_0x03f3
        L_0x0238:
            r7 = r0
            goto L_0x023b
        L_0x023a:
            r7 = r2
        L_0x023b:
            r3 = r12
            r2 = r28
            r12 = r40
            setAccessible(r11, r2, r12)
            com.alibaba.fastjson.util.FieldInfo r1 = new com.alibaba.fastjson.util.FieldInfo
            r25 = 0
            r0 = r1
            r31 = r1
            r1 = r7
            r32 = r3
            r3 = r4
            r26 = r4
            r4 = r39
            r13 = r5
            r5 = r25
            r25 = r6
            r6 = r20
            r33 = r7
            r7 = r23
            r8 = r18
            r9 = r24
            r12 = r10
            r10 = r46
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r1 = r31
            r10 = r32
            r0 = r33
            r10.put(r0, r1)
            r0 = r20
            goto L_0x0292
        L_0x0273:
            r25 = r6
            r13 = r30
            r38 = r12
            r12 = r10
            r10 = r38
            r12 = r10
            goto L_0x03f3
        L_0x027f:
            r13 = r30
            r38 = r12
            r12 = r10
            r10 = r38
            r12 = r10
            goto L_0x03f3
        L_0x0289:
            r13 = r30
            r38 = r12
            r12 = r10
            r10 = r38
            r23 = r1
        L_0x0292:
            java.lang.String r1 = "is"
            boolean r1 = r12.startsWith(r1)
            if (r1 == 0) goto L_0x03ad
            int r1 = r12.length()
            r2 = 3
            if (r1 >= r2) goto L_0x02a4
            r12 = r10
            goto L_0x03f3
        L_0x02a4:
            r1 = 2
            char r9 = r12.charAt(r1)
            boolean r3 = java.lang.Character.isUpperCase(r9)
            if (r3 == 0) goto L_0x02d8
            boolean r3 = compatibleWithJavaBean
            if (r3 == 0) goto L_0x02bc
            java.lang.String r1 = r12.substring(r1)
            java.lang.String r1 = decapitalize(r1)
            goto L_0x02e9
        L_0x02bc:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            char r1 = r12.charAt(r1)
            char r1 = java.lang.Character.toLowerCase(r1)
            r3.append(r1)
            java.lang.String r1 = r12.substring(r2)
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            goto L_0x02e9
        L_0x02d8:
            r3 = 95
            if (r9 != r3) goto L_0x02e1
            java.lang.String r1 = r12.substring(r2)
            goto L_0x02e9
        L_0x02e1:
            r2 = 102(0x66, float:1.43E-43)
            if (r9 != r2) goto L_0x03a4
            java.lang.String r1 = r12.substring(r1)
        L_0x02e9:
            java.lang.reflect.Field r2 = getField(r11, r1, r13)
            if (r2 != 0) goto L_0x02f5
            java.lang.reflect.Field r2 = getField(r11, r12, r13)
            r8 = r2
            goto L_0x02f6
        L_0x02f5:
            r8 = r2
        L_0x02f6:
            r2 = 0
            if (r8 == 0) goto L_0x0354
            if (r45 == 0) goto L_0x0304
            java.lang.Class<com.alibaba.fastjson.annotation.JSONField> r3 = com.alibaba.fastjson.annotation.JSONField.class
            java.lang.annotation.Annotation r3 = r8.getAnnotation(r3)
            com.alibaba.fastjson.annotation.JSONField r3 = (com.alibaba.fastjson.annotation.JSONField) r3
            goto L_0x0306
        L_0x0304:
            r3 = r16
        L_0x0306:
            r2 = r3
            if (r2 == 0) goto L_0x034f
            boolean r3 = r2.serialize()
            if (r3 != 0) goto L_0x0312
            r12 = r10
            goto L_0x03f3
        L_0x0312:
            int r0 = r2.ordinal()
            com.alibaba.fastjson.serializer.SerializerFeature[] r3 = r2.serialzeFeatures()
            int r3 = com.alibaba.fastjson.serializer.SerializerFeature.of(r3)
            java.lang.String r4 = r2.name()
            int r4 = r4.length()
            if (r4 == 0) goto L_0x0348
            java.lang.String r1 = r2.name()
            if (r14 == 0) goto L_0x0341
            java.lang.Object r4 = r14.get(r1)
            r1 = r4
            java.lang.String r1 = (java.lang.String) r1
            if (r1 != 0) goto L_0x033a
            r12 = r10
            goto L_0x03f3
        L_0x033a:
            r19 = r0
            r20 = r2
            r23 = r3
            goto L_0x0358
        L_0x0341:
            r19 = r0
            r20 = r2
            r23 = r3
            goto L_0x0358
        L_0x0348:
            r19 = r0
            r20 = r2
            r23 = r3
            goto L_0x0358
        L_0x034f:
            r19 = r0
            r20 = r2
            goto L_0x0358
        L_0x0354:
            r19 = r0
            r20 = r2
        L_0x0358:
            if (r15 == 0) goto L_0x035e
            java.lang.String r1 = r15.translate(r1)
        L_0x035e:
            if (r14 == 0) goto L_0x036d
            java.lang.Object r0 = r14.get(r1)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 != 0) goto L_0x036b
            r12 = r10
            goto L_0x03f3
        L_0x036b:
            r7 = r0
            goto L_0x036e
        L_0x036d:
            r7 = r1
        L_0x036e:
            r24 = r12
            r12 = r40
            setAccessible(r11, r8, r12)
            r6 = r28
            setAccessible(r11, r6, r12)
            com.alibaba.fastjson.util.FieldInfo r5 = new com.alibaba.fastjson.util.FieldInfo
            r25 = 0
            r0 = r5
            r1 = r7
            r2 = r6
            r3 = r8
            r4 = r39
            r11 = r5
            r5 = r25
            r25 = r6
            r6 = r19
            r34 = r7
            r7 = r23
            r26 = r8
            r8 = r18
            r27 = r9
            r9 = r20
            r12 = r10
            r10 = r46
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r0 = r34
            r12.put(r0, r11)
            goto L_0x03f3
        L_0x03a4:
            r27 = r9
            r24 = r12
            r25 = r28
            r12 = r10
            goto L_0x03f3
        L_0x03ad:
            r24 = r12
            r25 = r28
            r12 = r10
            goto L_0x03f3
        L_0x03b3:
            r24 = r4
            r25 = r5
            r21 = r6
            r22 = r7
            r29 = r8
            r13 = r9
            r12 = r10
            goto L_0x03f3
        L_0x03c0:
            r24 = r4
            r25 = r5
            r21 = r6
            r22 = r7
            r29 = r8
            r13 = r9
            r12 = r10
            goto L_0x03f3
        L_0x03cd:
            r24 = r4
            r25 = r5
            r21 = r6
            r22 = r7
            r29 = r8
            r13 = r9
            r12 = r10
            goto L_0x03f3
        L_0x03da:
            r24 = r4
            r25 = r5
            r21 = r6
            r22 = r7
            r29 = r8
            r13 = r9
            r12 = r10
            goto L_0x03f3
        L_0x03e7:
            r24 = r4
            r25 = r5
            r21 = r6
            r22 = r7
            r29 = r8
            r13 = r9
            r12 = r10
        L_0x03f3:
            int r6 = r21 + 1
            r11 = r39
            r10 = r12
            r9 = r13
            r7 = r22
            r8 = r29
            r12 = r40
            r13 = r42
            goto L_0x0020
        L_0x0403:
            r13 = r9
            r12 = r10
            goto L_0x0408
        L_0x0406:
            r13 = r9
            r12 = r10
        L_0x0408:
            java.util.ArrayList r0 = new java.util.ArrayList
            int r1 = r13.length
            r0.<init>(r1)
            r11 = r0
            int r0 = r13.length
            r1 = 0
        L_0x0411:
            if (r1 >= r0) goto L_0x0439
            r2 = r13[r1]
            int r3 = r2.getModifiers()
            r3 = r3 & 8
            if (r3 == 0) goto L_0x041e
            goto L_0x0436
        L_0x041e:
            java.lang.String r3 = r2.getName()
            java.lang.String r4 = "this$0"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x042b
            goto L_0x0436
        L_0x042b:
            int r3 = r2.getModifiers()
            r3 = r3 & 1
            if (r3 == 0) goto L_0x0436
            r11.add(r2)
        L_0x0436:
            int r1 = r1 + 1
            goto L_0x0411
        L_0x0439:
            java.lang.Class r0 = r39.getSuperclass()
        L_0x043d:
            if (r0 == 0) goto L_0x0469
            java.lang.Class<java.lang.Object> r1 = java.lang.Object.class
            if (r0 == r1) goto L_0x0469
            java.lang.reflect.Field[] r1 = r0.getDeclaredFields()
            int r2 = r1.length
            r3 = 0
        L_0x0449:
            if (r3 >= r2) goto L_0x0464
            r4 = r1[r3]
            int r5 = r4.getModifiers()
            r5 = r5 & 8
            if (r5 == 0) goto L_0x0456
            goto L_0x0461
        L_0x0456:
            int r5 = r4.getModifiers()
            r5 = r5 & 1
            if (r5 == 0) goto L_0x0461
            r11.add(r4)
        L_0x0461:
            int r3 = r3 + 1
            goto L_0x0449
        L_0x0464:
            java.lang.Class r0 = r0.getSuperclass()
            goto L_0x043d
        L_0x0469:
            java.util.Iterator r18 = r11.iterator()
        L_0x046d:
            boolean r0 = r18.hasNext()
            if (r0 == 0) goto L_0x0516
            java.lang.Object r0 = r18.next()
            r10 = r0
            java.lang.reflect.Field r10 = (java.lang.reflect.Field) r10
            if (r45 == 0) goto L_0x0485
            java.lang.Class<com.alibaba.fastjson.annotation.JSONField> r0 = com.alibaba.fastjson.annotation.JSONField.class
            java.lang.annotation.Annotation r0 = r10.getAnnotation(r0)
            com.alibaba.fastjson.annotation.JSONField r0 = (com.alibaba.fastjson.annotation.JSONField) r0
            goto L_0x0487
        L_0x0485:
            r0 = r16
        L_0x0487:
            r19 = r0
            r0 = 0
            r1 = 0
            java.lang.String r2 = r10.getName()
            if (r19 == 0) goto L_0x04bc
            boolean r3 = r19.serialize()
            if (r3 != 0) goto L_0x0498
            goto L_0x046d
        L_0x0498:
            int r0 = r19.ordinal()
            com.alibaba.fastjson.serializer.SerializerFeature[] r3 = r19.serialzeFeatures()
            int r1 = com.alibaba.fastjson.serializer.SerializerFeature.of(r3)
            java.lang.String r3 = r19.name()
            int r3 = r3.length()
            if (r3 == 0) goto L_0x04b7
            java.lang.String r2 = r19.name()
            r20 = r0
            r21 = r1
            goto L_0x04c0
        L_0x04b7:
            r20 = r0
            r21 = r1
            goto L_0x04c0
        L_0x04bc:
            r20 = r0
            r21 = r1
        L_0x04c0:
            if (r14 == 0) goto L_0x04cc
            java.lang.Object r0 = r14.get(r2)
            r2 = r0
            java.lang.String r2 = (java.lang.String) r2
            if (r2 != 0) goto L_0x04cc
            goto L_0x046d
        L_0x04cc:
            if (r15 == 0) goto L_0x04d4
            java.lang.String r0 = r15.translate(r2)
            r9 = r0
            goto L_0x04d5
        L_0x04d4:
            r9 = r2
        L_0x04d5:
            boolean r0 = r12.containsKey(r9)
            if (r0 != 0) goto L_0x050d
            r8 = r40
            r7 = r12
            r12 = r39
            setAccessible(r12, r10, r8)
            com.alibaba.fastjson.util.FieldInfo r6 = new com.alibaba.fastjson.util.FieldInfo
            r2 = 0
            r5 = 0
            r22 = 0
            r0 = r6
            r1 = r9
            r3 = r10
            r4 = r39
            r35 = r6
            r6 = r20
            r36 = r7
            r7 = r21
            r8 = r22
            r37 = r9
            r9 = r19
            r22 = r10
            r10 = r46
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r1 = r35
            r0 = r36
            r2 = r37
            r0.put(r2, r1)
            goto L_0x0513
        L_0x050d:
            r2 = r9
            r22 = r10
            r0 = r12
            r12 = r39
        L_0x0513:
            r12 = r0
            goto L_0x046d
        L_0x0516:
            r0 = r12
            r12 = r39
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r2 = 0
            r3 = 0
            r4 = r13
            if (r42 == 0) goto L_0x0544
            java.lang.String[] r3 = r42.orders()
            if (r3 == 0) goto L_0x0543
            int r5 = r3.length
            int r6 = r0.size()
            if (r5 != r6) goto L_0x0543
            r2 = 1
            int r5 = r3.length
            r6 = 0
        L_0x0533:
            if (r6 >= r5) goto L_0x0542
            r7 = r3[r6]
            boolean r8 = r0.containsKey(r7)
            if (r8 != 0) goto L_0x053f
            r2 = 0
            goto L_0x0542
        L_0x053f:
            int r6 = r6 + 1
            goto L_0x0533
        L_0x0542:
            goto L_0x0544
        L_0x0543:
            r2 = 0
        L_0x0544:
            if (r2 == 0) goto L_0x0558
            int r5 = r3.length
            r6 = 0
        L_0x0548:
            if (r6 >= r5) goto L_0x0575
            r7 = r3[r6]
            java.lang.Object r8 = r0.get(r7)
            com.alibaba.fastjson.util.FieldInfo r8 = (com.alibaba.fastjson.util.FieldInfo) r8
            r1.add(r8)
            int r6 = r6 + 1
            goto L_0x0548
        L_0x0558:
            java.util.Collection r5 = r0.values()
            java.util.Iterator r5 = r5.iterator()
        L_0x0560:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L_0x0570
            java.lang.Object r6 = r5.next()
            com.alibaba.fastjson.util.FieldInfo r6 = (com.alibaba.fastjson.util.FieldInfo) r6
            r1.add(r6)
            goto L_0x0560
        L_0x0570:
            if (r44 == 0) goto L_0x0575
            java.util.Collections.sort(r1)
        L_0x0575:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.util.TypeUtils.computeGetters(java.lang.Class, int, boolean, com.alibaba.fastjson.annotation.JSONType, java.util.Map, boolean, boolean, boolean, com.alibaba.fastjson.PropertyNamingStrategy):java.util.List");
    }

    public static JSONField getSupperMethodAnnotation(Class<?> clazz, Method method) {
        JSONField annotation;
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            for (Method interfaceMethod : interfaceClass.getMethods()) {
                if (interfaceMethod.getName().equals(method.getName())) {
                    Class<?>[] interfaceParameterTypes = interfaceMethod.getParameterTypes();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (interfaceParameterTypes.length != parameterTypes.length) {
                        continue;
                    } else {
                        boolean match = true;
                        int i = 0;
                        while (true) {
                            if (i >= interfaceParameterTypes.length) {
                                break;
                            } else if (!interfaceParameterTypes[i].equals(parameterTypes[i])) {
                                match = false;
                                break;
                            } else {
                                i++;
                            }
                        }
                        if (match && (annotation = (JSONField) interfaceMethod.getAnnotation(JSONField.class)) != null) {
                            return annotation;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static boolean isJSONTypeIgnore(Class<?> clazz, JSONType jsonType, String propertyName) {
        if (!(jsonType == null || jsonType.ignores() == null)) {
            for (String item : jsonType.ignores()) {
                if (propertyName.equalsIgnoreCase(item)) {
                    return true;
                }
            }
        }
        Class<? super Object> superclass = clazz.getSuperclass();
        if (superclass == Object.class || superclass == null || !isJSONTypeIgnore(superclass, (JSONType) superclass.getAnnotation(JSONType.class), propertyName)) {
            return false;
        }
        return true;
    }

    public static boolean isGenericParamType(Type type) {
        Type superType;
        if (type instanceof ParameterizedType) {
            return true;
        }
        if (!(type instanceof Class) || (superType = ((Class) type).getGenericSuperclass()) == Object.class || !isGenericParamType(superType)) {
            return false;
        }
        return true;
    }

    public static Type getGenericParamType(Type type) {
        return type instanceof Class ? getGenericParamType(((Class) type).getGenericSuperclass()) : type;
    }

    public static Class<?> getClass(Type type) {
        if (type.getClass() == Class.class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        }
        if (type instanceof TypeVariable) {
            return (Class) ((TypeVariable) type).getBounds()[0];
        }
        return Object.class;
    }

    public static String decapitalize(String name) {
        if (name == null || name.length() == 0 || (name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0)))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static boolean setAccessible(Class<?> clazz, Member member, int classMofifiers) {
        if (member == null || !setAccessibleEnable) {
            return false;
        }
        Class<? super Object> superclass = clazz.getSuperclass();
        if ((superclass == null || superclass == Object.class) && (member.getModifiers() & 1) != 0 && (classMofifiers & 1) != 0) {
            return false;
        }
        try {
            ((AccessibleObject) member).setAccessible(true);
            return true;
        } catch (AccessControlException e) {
            setAccessibleEnable = false;
            return false;
        }
    }

    public static Field getField(Class<?> clazz, String fieldName, Field[] declaredFields) {
        Field field = getField0(clazz, fieldName, declaredFields);
        if (field == null) {
            field = getField0(clazz, "_" + fieldName, declaredFields);
        }
        if (field == null) {
            field = getField0(clazz, "m_" + fieldName, declaredFields);
        }
        if (field != null) {
            return field;
        }
        return getField0(clazz, "m" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), declaredFields);
    }

    private static Field getField0(Class<?> clazz, String fieldName, Field[] declaredFields) {
        for (Field item : declaredFields) {
            if (fieldName.equals(item.getName())) {
                return item;
            }
        }
        Class<? super Object> superclass = clazz.getSuperclass();
        if (superclass == null || superclass == Object.class) {
            return null;
        }
        return getField(superclass, fieldName, superclass.getDeclaredFields());
    }
}

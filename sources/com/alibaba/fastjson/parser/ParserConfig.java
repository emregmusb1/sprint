package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.ArrayCodec;
import com.alibaba.fastjson.serializer.BigDecimalCodec;
import com.alibaba.fastjson.serializer.BooleanCodec;
import com.alibaba.fastjson.serializer.CollectionCodec;
import com.alibaba.fastjson.serializer.DateCodec;
import com.alibaba.fastjson.serializer.IntegerCodec;
import com.alibaba.fastjson.serializer.MiscCodec;
import com.alibaba.fastjson.serializer.NumberCodec;
import com.alibaba.fastjson.serializer.StringCodec;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.IdentityHashMap;
import java.io.Closeable;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public class ParserConfig {
    public static ParserConfig global = new ParserConfig();
    public ClassLoader defaultClassLoader;
    private final IdentityHashMap<ObjectDeserializer> derializers = new IdentityHashMap<>(1024);
    public PropertyNamingStrategy propertyNamingStrategy;
    public final SymbolTable symbolTable = new SymbolTable(16384);

    public static ParserConfig getGlobalInstance() {
        return global;
    }

    public ParserConfig() {
        this.derializers.put(SimpleDateFormat.class, MiscCodec.instance);
        this.derializers.put(Date.class, DateCodec.instance);
        this.derializers.put(Calendar.class, DateCodec.instance);
        this.derializers.put(Map.class, MapDeserializer.instance);
        this.derializers.put(HashMap.class, MapDeserializer.instance);
        this.derializers.put(LinkedHashMap.class, MapDeserializer.instance);
        this.derializers.put(TreeMap.class, MapDeserializer.instance);
        this.derializers.put(ConcurrentMap.class, MapDeserializer.instance);
        this.derializers.put(ConcurrentHashMap.class, MapDeserializer.instance);
        this.derializers.put(Collection.class, CollectionCodec.instance);
        this.derializers.put(List.class, CollectionCodec.instance);
        this.derializers.put(ArrayList.class, CollectionCodec.instance);
        this.derializers.put(Object.class, JavaObjectDeserializer.instance);
        this.derializers.put(String.class, StringCodec.instance);
        this.derializers.put(Character.TYPE, MiscCodec.instance);
        this.derializers.put(Character.class, MiscCodec.instance);
        this.derializers.put(Byte.TYPE, NumberCodec.instance);
        this.derializers.put(Byte.class, NumberCodec.instance);
        this.derializers.put(Short.TYPE, NumberCodec.instance);
        this.derializers.put(Short.class, NumberCodec.instance);
        this.derializers.put(Integer.TYPE, IntegerCodec.instance);
        this.derializers.put(Integer.class, IntegerCodec.instance);
        this.derializers.put(Long.TYPE, IntegerCodec.instance);
        this.derializers.put(Long.class, IntegerCodec.instance);
        this.derializers.put(BigInteger.class, BigDecimalCodec.instance);
        this.derializers.put(BigDecimal.class, BigDecimalCodec.instance);
        this.derializers.put(Float.TYPE, NumberCodec.instance);
        this.derializers.put(Float.class, NumberCodec.instance);
        this.derializers.put(Double.TYPE, NumberCodec.instance);
        this.derializers.put(Double.class, NumberCodec.instance);
        this.derializers.put(Boolean.TYPE, BooleanCodec.instance);
        this.derializers.put(Boolean.class, BooleanCodec.instance);
        this.derializers.put(Class.class, MiscCodec.instance);
        this.derializers.put(char[].class, ArrayCodec.instance);
        this.derializers.put(Object[].class, ArrayCodec.instance);
        this.derializers.put(UUID.class, MiscCodec.instance);
        this.derializers.put(TimeZone.class, MiscCodec.instance);
        this.derializers.put(Locale.class, MiscCodec.instance);
        this.derializers.put(Currency.class, MiscCodec.instance);
        this.derializers.put(URI.class, MiscCodec.instance);
        this.derializers.put(URL.class, MiscCodec.instance);
        this.derializers.put(Pattern.class, MiscCodec.instance);
        this.derializers.put(Charset.class, MiscCodec.instance);
        this.derializers.put(Number.class, NumberCodec.instance);
        this.derializers.put(StackTraceElement.class, MiscCodec.instance);
        this.derializers.put(Serializable.class, JavaObjectDeserializer.instance);
        this.derializers.put(Cloneable.class, JavaObjectDeserializer.instance);
        this.derializers.put(Comparable.class, JavaObjectDeserializer.instance);
        this.derializers.put(Closeable.class, JavaObjectDeserializer.instance);
    }

    public ObjectDeserializer getDeserializer(Type type) {
        ObjectDeserializer derializer = this.derializers.get(type);
        if (derializer != null) {
            return derializer;
        }
        if (type instanceof Class) {
            return getDeserializer((Class) type, type);
        }
        if (!(type instanceof ParameterizedType)) {
            return JavaObjectDeserializer.instance;
        }
        Type rawType = ((ParameterizedType) type).getRawType();
        if (rawType instanceof Class) {
            return getDeserializer((Class) rawType, type);
        }
        return getDeserializer(rawType);
    }

    public ObjectDeserializer getDeserializer(Class<?> clazz, Type type) {
        ObjectDeserializer deserializer;
        JSONType annotation;
        Class<?> mappingTo;
        ObjectDeserializer deserializer2 = this.derializers.get(type);
        if (deserializer2 != null) {
            return deserializer2;
        }
        if (type == null) {
            type = clazz;
        }
        ObjectDeserializer deserializer3 = this.derializers.get(type);
        if (deserializer3 != null) {
            return deserializer3;
        }
        if (!isPrimitive(clazz) && (annotation = (JSONType) clazz.getAnnotation(JSONType.class)) != null && (mappingTo = annotation.mappingTo()) != Void.class) {
            return getDeserializer(mappingTo, mappingTo);
        }
        if ((type instanceof WildcardType) || (type instanceof TypeVariable) || (type instanceof ParameterizedType)) {
            deserializer3 = this.derializers.get(clazz);
        }
        if (deserializer3 != null) {
            return deserializer3;
        }
        ObjectDeserializer deserializer4 = this.derializers.get(type);
        if (deserializer4 != null) {
            return deserializer4;
        }
        if (clazz.isEnum()) {
            deserializer = new EnumDeserializer(clazz);
        } else if (clazz.isArray()) {
            deserializer = ArrayCodec.instance;
        } else if (clazz == Set.class || clazz == HashSet.class || clazz == Collection.class || clazz == List.class || clazz == ArrayList.class) {
            deserializer = CollectionCodec.instance;
        } else if (Collection.class.isAssignableFrom(clazz)) {
            deserializer = CollectionCodec.instance;
        } else if (Map.class.isAssignableFrom(clazz)) {
            deserializer = MapDeserializer.instance;
        } else {
            deserializer = Throwable.class.isAssignableFrom(clazz) ? new ThrowableDeserializer(this, clazz) : new JavaBeanDeserializer(this, clazz, type);
        }
        putDeserializer(type, deserializer);
        return deserializer;
    }

    public ObjectDeserializer registerIfNotExists(Class<?> clazz) {
        return registerIfNotExists(clazz, clazz.getModifiers(), false, true, true, true);
    }

    public ObjectDeserializer registerIfNotExists(Class<?> clazz, int classModifiers, boolean fieldOnly, boolean jsonTypeSupport, boolean jsonFieldSupport, boolean fieldGenericSupport) {
        ObjectDeserializer deserializer = this.derializers.get(clazz);
        if (deserializer != null) {
            return deserializer;
        }
        ObjectDeserializer deserializer2 = new JavaBeanDeserializer(this, clazz, clazz, JavaBeanInfo.build(clazz, classModifiers, clazz, fieldOnly, jsonTypeSupport, jsonFieldSupport, fieldGenericSupport, this.propertyNamingStrategy));
        putDeserializer(clazz, deserializer2);
        return deserializer2;
    }

    public FieldDeserializer createFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        Class<?> fieldClass = fieldInfo.fieldClass;
        if (fieldClass == List.class || fieldClass == ArrayList.class || (fieldClass.isArray() && !fieldClass.getComponentType().isPrimitive())) {
            return new ListTypeFieldDeserializer(mapping, clazz, fieldInfo);
        }
        return new DefaultFieldDeserializer(mapping, clazz, fieldInfo);
    }

    public void putDeserializer(Type type, ObjectDeserializer deserializer) {
        this.derializers.put(type, deserializer);
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == Boolean.class || clazz == Character.class || clazz == Byte.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class || clazz == Float.class || clazz == Double.class || clazz == BigInteger.class || clazz == BigDecimal.class || clazz == String.class || clazz == Date.class || clazz == java.sql.Date.class || clazz == Time.class || clazz == Timestamp.class;
    }
}

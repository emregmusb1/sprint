package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONStreamAware;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.util.IdentityHashMap;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

public class SerializeConfig {
    public static final SerializeConfig globalInstance = new SerializeConfig();
    public PropertyNamingStrategy propertyNamingStrategy;
    private final IdentityHashMap<ObjectSerializer> serializers = new IdentityHashMap<>(1024);
    protected String typeKey = JSON.DEFAULT_TYPE_KEY;

    public static final SerializeConfig getGlobalInstance() {
        return globalInstance;
    }

    public ObjectSerializer registerIfNotExists(Class<?> clazz) {
        return registerIfNotExists(clazz, clazz.getModifiers(), false, true, true, true);
    }

    public ObjectSerializer registerIfNotExists(Class<?> clazz, int classModifers, boolean fieldOnly, boolean jsonTypeSupport, boolean jsonFieldSupport, boolean fieldGenericSupport) {
        Class<?> cls = clazz;
        ObjectSerializer serializer = this.serializers.get(clazz);
        if (serializer != null) {
            return serializer;
        }
        ObjectSerializer serializer2 = new JavaBeanSerializer(clazz, classModifers, (Map<String, String>) null, fieldOnly, jsonTypeSupport, jsonFieldSupport, fieldGenericSupport, this.propertyNamingStrategy);
        this.serializers.put(clazz, serializer2);
        return serializer2;
    }

    public SerializeConfig() {
        this.serializers.put(Boolean.class, BooleanCodec.instance);
        this.serializers.put(Character.class, MiscCodec.instance);
        this.serializers.put(Byte.class, IntegerCodec.instance);
        this.serializers.put(Short.class, IntegerCodec.instance);
        this.serializers.put(Integer.class, IntegerCodec.instance);
        this.serializers.put(Long.class, IntegerCodec.instance);
        this.serializers.put(Float.class, NumberCodec.instance);
        this.serializers.put(Double.class, NumberCodec.instance);
        this.serializers.put(Number.class, NumberCodec.instance);
        this.serializers.put(BigDecimal.class, BigDecimalCodec.instance);
        this.serializers.put(BigInteger.class, BigDecimalCodec.instance);
        this.serializers.put(String.class, StringCodec.instance);
        this.serializers.put(Object[].class, ArrayCodec.instance);
        this.serializers.put(Class.class, MiscCodec.instance);
        this.serializers.put(SimpleDateFormat.class, MiscCodec.instance);
        this.serializers.put(Locale.class, MiscCodec.instance);
        this.serializers.put(Currency.class, MiscCodec.instance);
        this.serializers.put(TimeZone.class, MiscCodec.instance);
        this.serializers.put(UUID.class, MiscCodec.instance);
        this.serializers.put(URI.class, MiscCodec.instance);
        this.serializers.put(URL.class, MiscCodec.instance);
        this.serializers.put(Pattern.class, MiscCodec.instance);
        this.serializers.put(Charset.class, MiscCodec.instance);
    }

    public ObjectSerializer get(Class<?> clazz) {
        ObjectSerializer writer = this.serializers.get(clazz);
        if (writer != null) {
            return writer;
        }
        if (Map.class.isAssignableFrom(clazz)) {
            this.serializers.put(clazz, new MapSerializer());
        } else if (List.class.isAssignableFrom(clazz)) {
            this.serializers.put(clazz, new ListSerializer());
        } else if (Collection.class.isAssignableFrom(clazz)) {
            this.serializers.put(clazz, CollectionCodec.instance);
        } else if (Date.class.isAssignableFrom(clazz)) {
            this.serializers.put(clazz, DateCodec.instance);
        } else if (JSONAware.class.isAssignableFrom(clazz)) {
            this.serializers.put(clazz, MiscCodec.instance);
        } else if (JSONSerializable.class.isAssignableFrom(clazz)) {
            this.serializers.put(clazz, MiscCodec.instance);
        } else if (JSONStreamAware.class.isAssignableFrom(clazz)) {
            this.serializers.put(clazz, MiscCodec.instance);
        } else {
            if (!clazz.isEnum()) {
                Class<? super Object> superclass = clazz.getSuperclass();
                Class<? super Object> cls = superclass;
                if (superclass == null || cls == Object.class || !cls.isEnum()) {
                    if (clazz.isArray()) {
                        Class<?> componentType = clazz.getComponentType();
                        this.serializers.put(clazz, new ArraySerializer(componentType, get(componentType)));
                    } else if (Throwable.class.isAssignableFrom(clazz)) {
                        JavaBeanSerializer serializer = new JavaBeanSerializer(clazz, this.propertyNamingStrategy);
                        serializer.features |= SerializerFeature.WriteClassName.mask;
                        this.serializers.put(clazz, serializer);
                    } else if (TimeZone.class.isAssignableFrom(clazz)) {
                        this.serializers.put(clazz, MiscCodec.instance);
                    } else if (Charset.class.isAssignableFrom(clazz)) {
                        this.serializers.put(clazz, MiscCodec.instance);
                    } else if (Enumeration.class.isAssignableFrom(clazz)) {
                        this.serializers.put(clazz, MiscCodec.instance);
                    } else if (Calendar.class.isAssignableFrom(clazz)) {
                        this.serializers.put(clazz, DateCodec.instance);
                    } else {
                        boolean isCglibProxy = false;
                        boolean isJavassistProxy = false;
                        Class<?>[] interfaces = clazz.getInterfaces();
                        int length = interfaces.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                break;
                            }
                            Class<?> item = interfaces[i];
                            if (item.getName().equals("net.sf.cglib.proxy.Factory") || item.getName().equals("org.springframework.cglib.proxy.Factory")) {
                                isCglibProxy = true;
                            } else if (item.getName().equals("javassist.util.proxy.ProxyObject")) {
                                isJavassistProxy = true;
                                break;
                            } else {
                                i++;
                            }
                        }
                        isCglibProxy = true;
                        if (isCglibProxy || isJavassistProxy) {
                            ObjectSerializer superWriter = get(clazz.getSuperclass());
                            this.serializers.put(clazz, superWriter);
                            return superWriter;
                        }
                        this.serializers.put(clazz, new JavaBeanSerializer(clazz, this.propertyNamingStrategy));
                    }
                }
            }
            this.serializers.put(clazz, new EnumSerializer());
        }
        return this.serializers.get(clazz);
    }

    public boolean put(Type key, ObjectSerializer value) {
        return this.serializers.put(key, value);
    }

    public String getTypeKey() {
        return this.typeKey;
    }

    public void setTypeKey(String typeKey2) {
        this.typeKey = typeKey2;
    }
}

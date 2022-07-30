package com.alibaba.fastjson.util;

import com.alibaba.fastjson.annotation.JSONField;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import kotlin.text.Typography;

public class FieldInfo implements Comparable<FieldInfo> {
    public final Class<?> declaringClass;
    public final Field field;
    public final boolean fieldAccess;
    private final JSONField fieldAnnotation;
    public final Class<?> fieldClass;
    public final boolean fieldTransient;
    public final Type fieldType;
    public final String format;
    public final boolean getOnly;
    public final boolean isEnum;
    public final Method method;
    private final JSONField methodAnnotation;
    public final String name;
    public final char[] name_chars;
    private int ordinal = 0;

    public FieldInfo(String name2, Class<?> declaringClass2, Class<?> fieldClass2, Type fieldType2, Field field2, int ordinal2, int serialzeFeatures) {
        this.name = name2;
        this.declaringClass = declaringClass2;
        this.fieldClass = fieldClass2;
        this.fieldType = fieldType2;
        this.method = null;
        this.field = field2;
        this.ordinal = ordinal2;
        this.isEnum = fieldClass2.isEnum();
        this.fieldAnnotation = null;
        this.methodAnnotation = null;
        if (field2 != null) {
            int modifiers = field2.getModifiers();
            this.fieldAccess = (modifiers & 1) != 0 || this.method == null;
            this.fieldTransient = Modifier.isTransient(modifiers);
        } else {
            this.fieldAccess = false;
            this.fieldTransient = false;
        }
        this.getOnly = false;
        int nameLen = this.name.length();
        this.name_chars = new char[(nameLen + 3)];
        String str = this.name;
        str.getChars(0, str.length(), this.name_chars, 1);
        char[] cArr = this.name_chars;
        cArr[0] = Typography.quote;
        cArr[nameLen + 1] = Typography.quote;
        cArr[nameLen + 2] = ':';
        this.format = null;
    }

    public FieldInfo(String name2, Method method2, Field field2, Class<?> clazz, Type type, int ordinal2, int serialzeFeatures, JSONField methodAnnotation2, JSONField fieldAnnotation2, boolean fieldGenericSupport) {
        Type fieldType2;
        Class<?> fieldClass2;
        Type genericFieldType;
        Type fieldType3;
        Type fieldType4;
        Method method3 = method2;
        Field field3 = field2;
        Class<?> cls = clazz;
        this.name = name2;
        this.method = method3;
        this.field = field3;
        this.ordinal = ordinal2;
        this.methodAnnotation = methodAnnotation2;
        this.fieldAnnotation = fieldAnnotation2;
        JSONField annotation = getAnnotation();
        String format2 = null;
        if (annotation != null) {
            format2 = annotation.format();
            if (format2.trim().length() == 0) {
                format2 = null;
            }
        }
        this.format = format2;
        boolean z = true;
        if (field3 != null) {
            int modifiers = field2.getModifiers();
            this.fieldAccess = method3 == null || ((modifiers & 1) != 0 && method2.getReturnType() == field2.getType());
            this.fieldTransient = (modifiers & 128) != 0;
        } else {
            this.fieldAccess = false;
            this.fieldTransient = false;
        }
        int nameLen = this.name.length();
        this.name_chars = new char[(nameLen + 3)];
        String str = this.name;
        str.getChars(0, str.length(), this.name_chars, 1);
        char[] cArr = this.name_chars;
        cArr[0] = Typography.quote;
        cArr[nameLen + 1] = Typography.quote;
        cArr[nameLen + 2] = ':';
        if (method3 != null) {
            Class<?>[] parameterTypes = method2.getParameterTypes();
            if (parameterTypes.length == 1) {
                fieldClass2 = parameterTypes[0];
                if (fieldClass2 == Class.class || fieldClass2 == String.class || fieldClass2.isPrimitive()) {
                    fieldType2 = fieldClass2;
                } else {
                    fieldType2 = fieldGenericSupport ? method2.getGenericParameterTypes()[0] : fieldClass2;
                }
                this.getOnly = false;
            } else {
                fieldClass2 = method2.getReturnType();
                if (fieldClass2 == Class.class) {
                    fieldType4 = fieldClass2;
                } else {
                    fieldType4 = fieldGenericSupport ? method2.getGenericReturnType() : fieldClass2;
                }
                this.getOnly = true;
            }
            this.declaringClass = method2.getDeclaringClass();
        } else {
            fieldClass2 = field2.getType();
            if (fieldClass2.isPrimitive() || fieldClass2 == String.class || fieldClass2.isEnum()) {
                fieldType3 = fieldClass2;
            } else {
                fieldType3 = fieldGenericSupport ? field2.getGenericType() : fieldClass2;
            }
            this.declaringClass = field2.getDeclaringClass();
            this.getOnly = Modifier.isFinal(field2.getModifiers());
        }
        if (cls == null || fieldClass2 != Object.class || !(fieldType2 instanceof TypeVariable) || (genericFieldType = getInheritGenericType(cls, (TypeVariable) fieldType2)) == null) {
            Type genericFieldType2 = fieldType2;
            if (!(fieldType2 instanceof Class)) {
                genericFieldType2 = getFieldType(cls, type != null ? type : cls, fieldType2);
                if (genericFieldType2 != fieldType2) {
                    if (genericFieldType2 instanceof ParameterizedType) {
                        fieldClass2 = TypeUtils.getClass(genericFieldType2);
                    } else if (genericFieldType2 instanceof Class) {
                        fieldClass2 = TypeUtils.getClass(genericFieldType2);
                    }
                }
            }
            this.fieldType = genericFieldType2;
            this.fieldClass = fieldClass2;
            this.isEnum = (fieldClass2.isArray() || !fieldClass2.isEnum()) ? false : z;
            return;
        }
        this.fieldClass = TypeUtils.getClass(genericFieldType);
        this.fieldType = genericFieldType;
        this.isEnum = fieldClass2.isEnum();
    }

    /* JADX WARNING: type inference failed for: r6v7, types: [java.lang.reflect.Type] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.reflect.Type getFieldType(java.lang.Class<?> r12, java.lang.reflect.Type r13, java.lang.reflect.Type r14) {
        /*
            if (r12 == 0) goto L_0x00dc
            if (r13 != 0) goto L_0x0006
            goto L_0x00dc
        L_0x0006:
            boolean r0 = r14 instanceof java.lang.reflect.GenericArrayType
            if (r0 == 0) goto L_0x0026
            r0 = r14
            java.lang.reflect.GenericArrayType r0 = (java.lang.reflect.GenericArrayType) r0
            java.lang.reflect.Type r1 = r0.getGenericComponentType()
            java.lang.reflect.Type r2 = getFieldType(r12, r13, r1)
            if (r1 == r2) goto L_0x0025
            java.lang.Class r3 = com.alibaba.fastjson.util.TypeUtils.getClass(r2)
            r4 = 0
            java.lang.Object r3 = java.lang.reflect.Array.newInstance(r3, r4)
            java.lang.Class r3 = r3.getClass()
            return r3
        L_0x0025:
            return r14
        L_0x0026:
            boolean r0 = com.alibaba.fastjson.util.TypeUtils.isGenericParamType(r13)
            if (r0 != 0) goto L_0x002d
            return r14
        L_0x002d:
            boolean r0 = r14 instanceof java.lang.reflect.TypeVariable
            if (r0 == 0) goto L_0x0064
            java.lang.reflect.Type r0 = com.alibaba.fastjson.util.TypeUtils.getGenericParamType(r13)
            java.lang.reflect.ParameterizedType r0 = (java.lang.reflect.ParameterizedType) r0
            java.lang.Class r1 = com.alibaba.fastjson.util.TypeUtils.getClass(r0)
            r2 = r14
            java.lang.reflect.TypeVariable r2 = (java.lang.reflect.TypeVariable) r2
            r3 = 0
        L_0x003f:
            java.lang.reflect.TypeVariable[] r4 = r1.getTypeParameters()
            int r4 = r4.length
            if (r3 >= r4) goto L_0x0064
            java.lang.reflect.TypeVariable[] r4 = r1.getTypeParameters()
            r4 = r4[r3]
            java.lang.String r4 = r4.getName()
            java.lang.String r5 = r2.getName()
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0061
            java.lang.reflect.Type[] r4 = r0.getActualTypeArguments()
            r14 = r4[r3]
            return r14
        L_0x0061:
            int r3 = r3 + 1
            goto L_0x003f
        L_0x0064:
            boolean r0 = r14 instanceof java.lang.reflect.ParameterizedType
            if (r0 == 0) goto L_0x00db
            r0 = r14
            java.lang.reflect.ParameterizedType r0 = (java.lang.reflect.ParameterizedType) r0
            java.lang.reflect.Type[] r1 = r0.getActualTypeArguments()
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            boolean r6 = r13 instanceof java.lang.reflect.ParameterizedType
            if (r6 == 0) goto L_0x007f
            r5 = r13
            java.lang.reflect.ParameterizedType r5 = (java.lang.reflect.ParameterizedType) r5
            java.lang.reflect.TypeVariable[] r3 = r12.getTypeParameters()
            goto L_0x0096
        L_0x007f:
            java.lang.reflect.Type r6 = r12.getGenericSuperclass()
            boolean r6 = r6 instanceof java.lang.reflect.ParameterizedType
            if (r6 == 0) goto L_0x0096
            java.lang.reflect.Type r6 = r12.getGenericSuperclass()
            r5 = r6
            java.lang.reflect.ParameterizedType r5 = (java.lang.reflect.ParameterizedType) r5
            java.lang.Class r6 = r12.getSuperclass()
            java.lang.reflect.TypeVariable[] r3 = r6.getTypeParameters()
        L_0x0096:
            r6 = 0
        L_0x0097:
            int r7 = r1.length
            if (r6 >= r7) goto L_0x00ca
            if (r5 == 0) goto L_0x00ca
            r7 = r1[r6]
            boolean r8 = r7 instanceof java.lang.reflect.TypeVariable
            if (r8 == 0) goto L_0x00c7
            r8 = r7
            java.lang.reflect.TypeVariable r8 = (java.lang.reflect.TypeVariable) r8
            r9 = 0
        L_0x00a6:
            int r10 = r3.length
            if (r9 >= r10) goto L_0x00c7
            r10 = r3[r9]
            java.lang.String r10 = r10.getName()
            java.lang.String r11 = r8.getName()
            boolean r10 = r10.equals(r11)
            if (r10 == 0) goto L_0x00c4
            if (r4 != 0) goto L_0x00bf
            java.lang.reflect.Type[] r4 = r5.getActualTypeArguments()
        L_0x00bf:
            r10 = r4[r9]
            r1[r6] = r10
            r2 = 1
        L_0x00c4:
            int r9 = r9 + 1
            goto L_0x00a6
        L_0x00c7:
            int r6 = r6 + 1
            goto L_0x0097
        L_0x00ca:
            if (r2 == 0) goto L_0x00db
            com.alibaba.fastjson.util.ParameterizedTypeImpl r6 = new com.alibaba.fastjson.util.ParameterizedTypeImpl
            java.lang.reflect.Type r7 = r0.getOwnerType()
            java.lang.reflect.Type r8 = r0.getRawType()
            r6.<init>(r1, r7, r8)
            r14 = r6
            return r14
        L_0x00db:
            return r14
        L_0x00dc:
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.util.FieldInfo.getFieldType(java.lang.Class, java.lang.reflect.Type, java.lang.reflect.Type):java.lang.reflect.Type");
    }

    public static Type getInheritGenericType(Class<?> clazz, TypeVariable<?> typeVariable) {
        Type type;
        GenericDeclaration gd = typeVariable.getGenericDeclaration();
        do {
            type = clazz.getGenericSuperclass();
            if (type == null) {
                return null;
            }
            if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                if (ptype.getRawType() == gd) {
                    TypeVariable<?>[] tvs = gd.getTypeParameters();
                    Type[] types = ptype.getActualTypeArguments();
                    for (int i = 0; i < tvs.length; i++) {
                        if (tvs[i] == typeVariable) {
                            return types[i];
                        }
                    }
                    return null;
                }
            }
            clazz = TypeUtils.getClass(type);
        } while (type != null);
        return null;
    }

    public String toString() {
        return this.name;
    }

    public int compareTo(FieldInfo o) {
        int i = this.ordinal;
        int i2 = o.ordinal;
        if (i < i2) {
            return -1;
        }
        if (i > i2) {
            return 1;
        }
        return this.name.compareTo(o.name);
    }

    public boolean equals(FieldInfo o) {
        if (o == this || compareTo(o) == 0) {
            return true;
        }
        return false;
    }

    public JSONField getAnnotation() {
        JSONField jSONField = this.fieldAnnotation;
        if (jSONField != null) {
            return jSONField;
        }
        return this.methodAnnotation;
    }

    public Object get(Object javaObject) throws IllegalAccessException, InvocationTargetException {
        if (this.fieldAccess) {
            return this.field.get(javaObject);
        }
        return this.method.invoke(javaObject, new Object[0]);
    }

    public void set(Object javaObject, Object value) throws IllegalAccessException, InvocationTargetException {
        Method method2 = this.method;
        if (method2 != null) {
            method2.invoke(javaObject, new Object[]{value});
            return;
        }
        this.field.set(javaObject, value);
    }
}

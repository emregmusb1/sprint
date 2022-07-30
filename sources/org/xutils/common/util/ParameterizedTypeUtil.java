package org.xutils.common.util;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ParameterizedTypeUtil {
    private ParameterizedTypeUtil() {
    }

    /* JADX WARNING: type inference failed for: r4v4, types: [java.lang.reflect.Type] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.reflect.Type getParameterizedType(java.lang.reflect.Type r10, java.lang.Class<?> r11, int r12) {
        /*
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            boolean r4 = r10 instanceof java.lang.reflect.ParameterizedType
            if (r4 == 0) goto L_0x001b
            r1 = r10
            java.lang.reflect.ParameterizedType r1 = (java.lang.reflect.ParameterizedType) r1
            java.lang.reflect.Type r4 = r1.getRawType()
            r0 = r4
            java.lang.Class r0 = (java.lang.Class) r0
            java.lang.reflect.Type[] r2 = r1.getActualTypeArguments()
            java.lang.reflect.TypeVariable[] r3 = r0.getTypeParameters()
            goto L_0x001e
        L_0x001b:
            r0 = r10
            java.lang.Class r0 = (java.lang.Class) r0
        L_0x001e:
            if (r11 != r0) goto L_0x0028
            if (r2 == 0) goto L_0x0025
            r4 = r2[r12]
            return r4
        L_0x0025:
            java.lang.Class<java.lang.Object> r4 = java.lang.Object.class
            return r4
        L_0x0028:
            java.lang.reflect.Type[] r4 = r0.getGenericInterfaces()
            if (r4 == 0) goto L_0x005b
            r5 = 0
        L_0x002f:
            int r6 = r4.length
            if (r5 >= r6) goto L_0x005b
            r6 = r4[r5]
            boolean r7 = r6 instanceof java.lang.reflect.ParameterizedType
            if (r7 == 0) goto L_0x0058
            r7 = r6
            java.lang.reflect.ParameterizedType r7 = (java.lang.reflect.ParameterizedType) r7
            java.lang.reflect.Type r7 = r7.getRawType()
            java.lang.Class r7 = (java.lang.Class) r7
            boolean r8 = r11.isAssignableFrom(r7)
            if (r8 == 0) goto L_0x0058
            java.lang.reflect.Type r8 = getParameterizedType(r6, r11, r12)     // Catch:{ Throwable -> 0x0050 }
            java.lang.reflect.Type r8 = getTrueType(r8, r3, r2)     // Catch:{ Throwable -> 0x0050 }
            return r8
        L_0x0050:
            r8 = move-exception
            java.lang.String r9 = r8.getMessage()
            org.xutils.common.util.LogUtil.w(r9, r8)
        L_0x0058:
            int r5 = r5 + 1
            goto L_0x002f
        L_0x005b:
            java.lang.Class r5 = r0.getSuperclass()
            if (r5 == 0) goto L_0x0075
            boolean r6 = r11.isAssignableFrom(r5)
            if (r6 == 0) goto L_0x0075
            java.lang.reflect.Type r6 = r0.getGenericSuperclass()
            java.lang.reflect.Type r6 = getParameterizedType(r6, r11, r12)
            java.lang.reflect.Type r6 = getTrueType(r6, r3, r2)
            return r6
        L_0x0075:
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "FindGenericType:"
            r7.append(r8)
            r7.append(r10)
            java.lang.String r8 = ", declaredClass: "
            r7.append(r8)
            r7.append(r11)
            java.lang.String r8 = ", index: "
            r7.append(r8)
            r7.append(r12)
            java.lang.String r7 = r7.toString()
            r6.<init>(r7)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xutils.common.util.ParameterizedTypeUtil.getParameterizedType(java.lang.reflect.Type, java.lang.Class, int):java.lang.reflect.Type");
    }

    private static Type getTrueType(Type type, TypeVariable<?>[] typeVariables, Type[] actualTypes) {
        if (type instanceof TypeVariable) {
            TypeVariable<?> tv = (TypeVariable) type;
            String name = tv.getName();
            if (actualTypes != null) {
                for (int i = 0; i < typeVariables.length; i++) {
                    if (name.equals(typeVariables[i].getName())) {
                        return actualTypes[i];
                    }
                }
            }
            return tv;
        }
        if (type instanceof GenericArrayType) {
            Type ct = ((GenericArrayType) type).getGenericComponentType();
            if (ct instanceof Class) {
                return Array.newInstance((Class) ct, 0).getClass();
            }
        }
        return type;
    }
}

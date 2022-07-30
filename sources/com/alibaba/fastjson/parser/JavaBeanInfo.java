package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.util.FieldInfo;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

class JavaBeanInfo {
    final Constructor<?> creatorConstructor;
    final Constructor<?> defaultConstructor;
    final int defaultConstructorParameterSize;
    final Method factoryMethod;
    final FieldInfo[] fields;
    final JSONType jsonType;
    boolean ordered = false;
    final FieldInfo[] sortedFields;
    final boolean supportBeanToArray;
    public final String typeName;

    JavaBeanInfo(Class<?> clazz, Constructor<?> defaultConstructor2, Constructor<?> creatorConstructor2, Method factoryMethod2, FieldInfo[] fields2, FieldInfo[] sortedFields2, JSONType jsonType2) {
        int i = 0;
        this.defaultConstructor = defaultConstructor2;
        this.creatorConstructor = creatorConstructor2;
        this.factoryMethod = factoryMethod2;
        this.fields = fields2;
        this.jsonType = jsonType2;
        if (jsonType2 != null) {
            String typeName2 = jsonType2.typeName();
            if (typeName2.length() != 0) {
                this.typeName = typeName2;
            } else {
                this.typeName = clazz.getName();
            }
        } else {
            this.typeName = clazz.getName();
        }
        boolean supportBeanToArray2 = false;
        if (jsonType2 != null) {
            boolean supportBeanToArray3 = false;
            for (Feature feature : jsonType2.parseFeatures()) {
                if (feature == Feature.SupportArrayToBean) {
                    supportBeanToArray3 = true;
                }
            }
            supportBeanToArray2 = supportBeanToArray3;
        }
        this.supportBeanToArray = supportBeanToArray2;
        FieldInfo[] sortedFields3 = computeSortedFields(fields2, sortedFields2);
        this.sortedFields = Arrays.equals(fields2, sortedFields3) ? fields2 : sortedFields3;
        this.defaultConstructorParameterSize = defaultConstructor2 != null ? defaultConstructor2.getParameterTypes().length : i;
    }

    private FieldInfo[] computeSortedFields(FieldInfo[] fields2, FieldInfo[] sortedFields2) {
        String[] orders;
        JSONType jSONType = this.jsonType;
        if (!(jSONType == null || (orders = jSONType.orders()) == null || orders.length == 0)) {
            boolean containsAll = true;
            int i = 0;
            while (true) {
                if (i >= orders.length) {
                    break;
                }
                boolean got = false;
                int j = 0;
                while (true) {
                    if (j >= sortedFields2.length) {
                        break;
                    } else if (sortedFields2[j].name.equals(orders[i])) {
                        got = true;
                        break;
                    } else {
                        j++;
                    }
                }
                if (!got) {
                    containsAll = false;
                    break;
                }
                i++;
            }
            if (!containsAll) {
                return sortedFields2;
            }
            if (orders.length == fields2.length) {
                boolean orderMatch = true;
                int i2 = 0;
                while (true) {
                    if (i2 >= orders.length) {
                        break;
                    } else if (!sortedFields2[i2].name.equals(orders[i2])) {
                        orderMatch = false;
                        break;
                    } else {
                        i2++;
                    }
                }
                if (orderMatch) {
                    return sortedFields2;
                }
                FieldInfo[] newSortedFields = new FieldInfo[sortedFields2.length];
                for (int i3 = 0; i3 < orders.length; i3++) {
                    int j2 = 0;
                    while (true) {
                        if (j2 >= sortedFields2.length) {
                            break;
                        } else if (sortedFields2[j2].name.equals(orders[i3])) {
                            newSortedFields[i3] = sortedFields2[j2];
                            break;
                        } else {
                            j2++;
                        }
                    }
                }
                FieldInfo[] sortedFields3 = newSortedFields;
                this.ordered = true;
                return newSortedFields;
            }
            FieldInfo[] newSortedFields2 = new FieldInfo[sortedFields2.length];
            for (int i4 = 0; i4 < orders.length; i4++) {
                int j3 = 0;
                while (true) {
                    if (j3 >= sortedFields2.length) {
                        break;
                    } else if (sortedFields2[j3].name.equals(orders[i4])) {
                        newSortedFields2[i4] = sortedFields2[j3];
                        break;
                    } else {
                        j3++;
                    }
                }
            }
            int fieldIndex = orders.length;
            for (int i5 = 0; i5 < sortedFields2.length; i5++) {
                boolean contains = false;
                int j4 = 0;
                while (true) {
                    if (j4 >= newSortedFields2.length || j4 >= fieldIndex) {
                        break;
                    } else if (newSortedFields2[i5].equals(sortedFields2[j4])) {
                        contains = true;
                        break;
                    } else {
                        j4++;
                    }
                }
                if (!contains) {
                    newSortedFields2[fieldIndex] = sortedFields2[i5];
                    fieldIndex++;
                }
            }
            this.ordered = true;
        }
        return sortedFields2;
    }

    static boolean addField(List<FieldInfo> fields2, FieldInfo field, boolean fieldOnly) {
        if (!fieldOnly) {
            int size = fields2.size();
            for (int i = 0; i < size; i++) {
                FieldInfo item = fields2.get(i);
                if (item.name.equals(field.name) && (!item.getOnly || field.getOnly)) {
                    return false;
                }
            }
        }
        fields2.add(field);
        return true;
    }

    /* JADX WARNING: type inference failed for: r39v0, types: [java.lang.Class<?>, java.lang.Class] */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0734, code lost:
        if (r1.length() > 0) goto L_0x0753;
     */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x04f2  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x04f9  */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.alibaba.fastjson.parser.JavaBeanInfo build(java.lang.Class<?> r39, int r40, java.lang.reflect.Type r41, boolean r42, boolean r43, boolean r44, boolean r45, com.alibaba.fastjson.PropertyNamingStrategy r46) {
        /*
            r12 = r39
            r13 = r40
            r14 = r42
            r15 = r46
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r11 = r0
            r1 = 0
            r0 = r13 & 1024(0x400, float:1.435E-42)
            r10 = 1
            r9 = 0
            if (r0 != 0) goto L_0x004e
            java.lang.Class[] r0 = new java.lang.Class[r9]     // Catch:{ Exception -> 0x001d }
            java.lang.reflect.Constructor r0 = r12.getDeclaredConstructor(r0)     // Catch:{ Exception -> 0x001d }
            r1 = r0
            goto L_0x001e
        L_0x001d:
            r0 = move-exception
        L_0x001e:
            if (r1 != 0) goto L_0x004c
            boolean r0 = r39.isMemberClass()
            if (r0 == 0) goto L_0x004c
            r0 = r13 & 8
            if (r0 != 0) goto L_0x004c
            java.lang.reflect.Constructor[] r0 = r39.getDeclaredConstructors()
            int r2 = r0.length
            r3 = 0
        L_0x0030:
            if (r3 >= r2) goto L_0x004c
            r4 = r0[r3]
            java.lang.Class[] r5 = r4.getParameterTypes()
            int r6 = r5.length
            if (r6 != r10) goto L_0x0049
            r6 = r5[r9]
            java.lang.Class r7 = r39.getDeclaringClass()
            boolean r6 = r6.equals(r7)
            if (r6 == 0) goto L_0x0049
            r0 = r4
            goto L_0x004f
        L_0x0049:
            int r3 = r3 + 1
            goto L_0x0030
        L_0x004c:
            r0 = r1
            goto L_0x004f
        L_0x004e:
            r0 = r1
        L_0x004f:
            r16 = 0
            r17 = 0
            if (r14 == 0) goto L_0x0058
            r1 = r17
            goto L_0x005c
        L_0x0058:
            java.lang.reflect.Method[] r1 = r39.getMethods()
        L_0x005c:
            r8 = r1
            java.lang.reflect.Field[] r7 = r39.getDeclaredFields()
            if (r0 != 0) goto L_0x0298
            boolean r1 = r39.isInterface()
            if (r1 != 0) goto L_0x0292
            r1 = r13 & 1024(0x400, float:1.435E-42)
            if (r1 != 0) goto L_0x0292
            r1 = 0
            java.lang.reflect.Constructor[] r2 = r39.getDeclaredConstructors()
            int r3 = r2.length
            r4 = 0
        L_0x0074:
            java.lang.String r5 = "multi-json creator"
            if (r4 >= r3) goto L_0x0092
            r6 = r2[r4]
            java.lang.Class<com.alibaba.fastjson.annotation.JSONCreator> r10 = com.alibaba.fastjson.annotation.JSONCreator.class
            java.lang.annotation.Annotation r10 = r6.getAnnotation(r10)
            com.alibaba.fastjson.annotation.JSONCreator r10 = (com.alibaba.fastjson.annotation.JSONCreator) r10
            if (r10 == 0) goto L_0x008f
            if (r1 != 0) goto L_0x0089
            r1 = r6
            r10 = r1
            goto L_0x0093
        L_0x0089:
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException
            r2.<init>(r5)
            throw r2
        L_0x008f:
            int r4 = r4 + 1
            goto L_0x0074
        L_0x0092:
            r10 = r1
        L_0x0093:
            java.lang.String r6 = "illegal json creator"
            if (r10 == 0) goto L_0x0171
            com.alibaba.fastjson.util.TypeUtils.setAccessible(r12, r10, r13)
            java.lang.Class[] r5 = r10.getParameterTypes()
            if (r45 == 0) goto L_0x00a5
            java.lang.reflect.Type[] r1 = r10.getGenericParameterTypes()
            goto L_0x00a6
        L_0x00a5:
            r1 = r5
        L_0x00a6:
            r16 = r1
            r1 = 0
            r4 = r1
        L_0x00aa:
            int r1 = r5.length
            if (r4 >= r1) goto L_0x0137
            java.lang.annotation.Annotation[][] r1 = r10.getParameterAnnotations()
            r3 = r1[r4]
            r1 = 0
            int r2 = r3.length
        L_0x00b5:
            if (r9 >= r2) goto L_0x00ce
            r19 = r1
            r1 = r3[r9]
            r20 = r2
            boolean r2 = r1 instanceof com.alibaba.fastjson.annotation.JSONField
            if (r2 == 0) goto L_0x00c7
            r2 = r1
            com.alibaba.fastjson.annotation.JSONField r2 = (com.alibaba.fastjson.annotation.JSONField) r2
            r19 = r2
            goto L_0x00d0
        L_0x00c7:
            int r9 = r9 + 1
            r1 = r19
            r2 = r20
            goto L_0x00b5
        L_0x00ce:
            r19 = r1
        L_0x00d0:
            if (r19 == 0) goto L_0x0126
            r9 = r5[r4]
            r20 = r16[r4]
            java.lang.String r1 = r19.name()
            java.lang.reflect.Field r2 = com.alibaba.fastjson.util.TypeUtils.getField(r12, r1, r7)
            if (r2 == 0) goto L_0x00e3
            com.alibaba.fastjson.util.TypeUtils.setAccessible(r12, r2, r13)
        L_0x00e3:
            int r21 = r19.ordinal()
            com.alibaba.fastjson.serializer.SerializerFeature[] r1 = r19.serialzeFeatures()
            int r22 = com.alibaba.fastjson.serializer.SerializerFeature.of(r1)
            com.alibaba.fastjson.util.FieldInfo r23 = new com.alibaba.fastjson.util.FieldInfo
            java.lang.String r24 = r19.name()
            r1 = r23
            r25 = r2
            r2 = r24
            r24 = r3
            r3 = r39
            r26 = r4
            r4 = r9
            r27 = r5
            r5 = r20
            r28 = r9
            r9 = r6
            r6 = r25
            r15 = r7
            r7 = r21
            r29 = r0
            r0 = r8
            r8 = r22
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            addField(r11, r1, r14)
            int r4 = r26 + 1
            r8 = r0
            r6 = r9
            r7 = r15
            r5 = r27
            r0 = r29
            r9 = 0
            r15 = r46
            goto L_0x00aa
        L_0x0126:
            r29 = r0
            r24 = r3
            r26 = r4
            r27 = r5
            r9 = r6
            r15 = r7
            r0 = r8
            com.alibaba.fastjson.JSONException r1 = new com.alibaba.fastjson.JSONException
            r1.<init>(r9)
            throw r1
        L_0x0137:
            r29 = r0
            r26 = r4
            r27 = r5
            r15 = r7
            r0 = r8
            int r1 = r11.size()
            com.alibaba.fastjson.util.FieldInfo[] r9 = new com.alibaba.fastjson.util.FieldInfo[r1]
            r11.toArray(r9)
            int r1 = r9.length
            com.alibaba.fastjson.util.FieldInfo[] r7 = new com.alibaba.fastjson.util.FieldInfo[r1]
            int r1 = r9.length
            r2 = 0
            java.lang.System.arraycopy(r9, r2, r7, r2, r1)
            java.util.Arrays.sort(r7)
            if (r43 == 0) goto L_0x015f
            java.lang.Class<com.alibaba.fastjson.annotation.JSONType> r1 = com.alibaba.fastjson.annotation.JSONType.class
            java.lang.annotation.Annotation r1 = r12.getAnnotation(r1)
            com.alibaba.fastjson.annotation.JSONType r1 = (com.alibaba.fastjson.annotation.JSONType) r1
            r8 = r1
            goto L_0x0161
        L_0x015f:
            r8 = r17
        L_0x0161:
            com.alibaba.fastjson.parser.JavaBeanInfo r17 = new com.alibaba.fastjson.parser.JavaBeanInfo
            r3 = 0
            r5 = 0
            r1 = r17
            r2 = r39
            r4 = r10
            r6 = r9
            r18 = r7
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            return r17
        L_0x0171:
            r29 = r0
            r9 = r6
            r15 = r7
            r0 = r8
            r1 = 0
            int r2 = r0.length
            r3 = 0
        L_0x0179:
            if (r3 >= r2) goto L_0x01aa
            r4 = r0[r3]
            int r6 = r4.getModifiers()
            boolean r6 = java.lang.reflect.Modifier.isStatic(r6)
            if (r6 == 0) goto L_0x01a7
            java.lang.Class r6 = r4.getReturnType()
            boolean r6 = r12.isAssignableFrom(r6)
            if (r6 != 0) goto L_0x0192
            goto L_0x01a7
        L_0x0192:
            java.lang.Class<com.alibaba.fastjson.annotation.JSONCreator> r6 = com.alibaba.fastjson.annotation.JSONCreator.class
            java.lang.annotation.Annotation r6 = r4.getAnnotation(r6)
            com.alibaba.fastjson.annotation.JSONCreator r6 = (com.alibaba.fastjson.annotation.JSONCreator) r6
            if (r6 == 0) goto L_0x01a7
            if (r1 != 0) goto L_0x01a1
            r1 = r4
            r8 = r1
            goto L_0x01ab
        L_0x01a1:
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException
            r2.<init>(r5)
            throw r2
        L_0x01a7:
            int r3 = r3 + 1
            goto L_0x0179
        L_0x01aa:
            r8 = r1
        L_0x01ab:
            if (r8 == 0) goto L_0x0279
            com.alibaba.fastjson.util.TypeUtils.setAccessible(r12, r8, r13)
            java.lang.Class[] r7 = r8.getParameterTypes()
            if (r45 == 0) goto L_0x01bb
            java.lang.reflect.Type[] r1 = r8.getGenericParameterTypes()
            goto L_0x01bc
        L_0x01bb:
            r1 = r7
        L_0x01bc:
            r16 = r1
            r1 = 0
            r6 = r1
        L_0x01c0:
            int r1 = r7.length
            if (r6 >= r1) goto L_0x0234
            java.lang.annotation.Annotation[][] r1 = r8.getParameterAnnotations()
            r5 = r1[r6]
            r1 = 0
            int r2 = r5.length
            r3 = 0
        L_0x01cc:
            if (r3 >= r2) goto L_0x01e1
            r4 = r5[r3]
            r19 = r1
            boolean r1 = r4 instanceof com.alibaba.fastjson.annotation.JSONField
            if (r1 == 0) goto L_0x01dc
            r1 = r4
            com.alibaba.fastjson.annotation.JSONField r1 = (com.alibaba.fastjson.annotation.JSONField) r1
            r19 = r1
            goto L_0x01e3
        L_0x01dc:
            int r3 = r3 + 1
            r1 = r19
            goto L_0x01cc
        L_0x01e1:
            r19 = r1
        L_0x01e3:
            if (r19 == 0) goto L_0x0226
            r20 = r7[r6]
            r21 = r16[r6]
            java.lang.String r1 = r19.name()
            java.lang.reflect.Field r22 = com.alibaba.fastjson.util.TypeUtils.getField(r12, r1, r15)
            int r23 = r19.ordinal()
            com.alibaba.fastjson.serializer.SerializerFeature[] r1 = r19.serialzeFeatures()
            int r24 = com.alibaba.fastjson.serializer.SerializerFeature.of(r1)
            com.alibaba.fastjson.util.FieldInfo r25 = new com.alibaba.fastjson.util.FieldInfo
            java.lang.String r2 = r19.name()
            r1 = r25
            r3 = r39
            r4 = r20
            r26 = r5
            r5 = r21
            r27 = r6
            r6 = r22
            r28 = r7
            r7 = r23
            r30 = r8
            r8 = r24
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            addField(r11, r1, r14)
            int r6 = r27 + 1
            r7 = r28
            r8 = r30
            goto L_0x01c0
        L_0x0226:
            r26 = r5
            r27 = r6
            r28 = r7
            r30 = r8
            com.alibaba.fastjson.JSONException r1 = new com.alibaba.fastjson.JSONException
            r1.<init>(r9)
            throw r1
        L_0x0234:
            r27 = r6
            r28 = r7
            r30 = r8
            int r1 = r11.size()
            com.alibaba.fastjson.util.FieldInfo[] r9 = new com.alibaba.fastjson.util.FieldInfo[r1]
            r11.toArray(r9)
            int r1 = r9.length
            com.alibaba.fastjson.util.FieldInfo[] r1 = new com.alibaba.fastjson.util.FieldInfo[r1]
            int r2 = r9.length
            r8 = 0
            java.lang.System.arraycopy(r9, r8, r1, r8, r2)
            java.util.Arrays.sort(r1)
            boolean r2 = java.util.Arrays.equals(r9, r1)
            if (r2 == 0) goto L_0x0258
            r1 = r9
            r18 = r1
            goto L_0x025a
        L_0x0258:
            r18 = r1
        L_0x025a:
            if (r43 == 0) goto L_0x0266
            java.lang.Class<com.alibaba.fastjson.annotation.JSONType> r1 = com.alibaba.fastjson.annotation.JSONType.class
            java.lang.annotation.Annotation r1 = r12.getAnnotation(r1)
            com.alibaba.fastjson.annotation.JSONType r1 = (com.alibaba.fastjson.annotation.JSONType) r1
            r8 = r1
            goto L_0x0268
        L_0x0266:
            r8 = r17
        L_0x0268:
            com.alibaba.fastjson.parser.JavaBeanInfo r17 = new com.alibaba.fastjson.parser.JavaBeanInfo
            r3 = 0
            r4 = 0
            r1 = r17
            r2 = r39
            r5 = r30
            r6 = r9
            r7 = r18
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            return r1
        L_0x0279:
            r30 = r8
            com.alibaba.fastjson.JSONException r1 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "default constructor not found. "
            r2.append(r3)
            r2.append(r12)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x0292:
            r29 = r0
            r15 = r7
            r0 = r8
            r8 = 0
            goto L_0x029d
        L_0x0298:
            r29 = r0
            r15 = r7
            r0 = r8
            r8 = 0
        L_0x029d:
            if (r29 == 0) goto L_0x02a5
            r9 = r29
            com.alibaba.fastjson.util.TypeUtils.setAccessible(r12, r9, r13)
            goto L_0x02a7
        L_0x02a5:
            r9 = r29
        L_0x02a7:
            r7 = 4
            if (r14 != 0) goto L_0x0561
            int r5 = r0.length
            r4 = 0
        L_0x02ac:
            if (r4 >= r5) goto L_0x0558
            r3 = r0[r4]
            r1 = 0
            r2 = 0
            r18 = r4
            java.lang.String r4 = r3.getName()
            int r6 = r4.length()
            if (r6 < r7) goto L_0x053d
            int r6 = r3.getModifiers()
            boolean r6 = java.lang.reflect.Modifier.isStatic(r6)
            if (r6 == 0) goto L_0x02d4
            r26 = r0
            r28 = r5
            r20 = r9
            r0 = r15
            r30 = 1
            r15 = r11
            goto L_0x0549
        L_0x02d4:
            java.lang.Class r6 = r3.getReturnType()
            java.lang.Class r7 = java.lang.Void.TYPE
            if (r6 == r7) goto L_0x02ef
            java.lang.Class r7 = r3.getDeclaringClass()
            if (r6 != r7) goto L_0x02e3
            goto L_0x02ef
        L_0x02e3:
            r26 = r0
            r28 = r5
            r20 = r9
            r0 = r15
            r30 = 1
            r15 = r11
            goto L_0x0549
        L_0x02ef:
            java.lang.Class[] r7 = r3.getParameterTypes()
            int r7 = r7.length
            if (r7 != r10) goto L_0x052e
            java.lang.Class r7 = r3.getDeclaringClass()
            java.lang.Class<java.lang.Object> r8 = java.lang.Object.class
            if (r7 != r8) goto L_0x030a
            r26 = r0
            r28 = r5
            r20 = r9
            r0 = r15
            r30 = 1
            r15 = r11
            goto L_0x0549
        L_0x030a:
            if (r44 == 0) goto L_0x0315
            java.lang.Class<com.alibaba.fastjson.annotation.JSONField> r7 = com.alibaba.fastjson.annotation.JSONField.class
            java.lang.annotation.Annotation r7 = r3.getAnnotation(r7)
            com.alibaba.fastjson.annotation.JSONField r7 = (com.alibaba.fastjson.annotation.JSONField) r7
            goto L_0x0317
        L_0x0315:
            r7 = r17
        L_0x0317:
            if (r7 != 0) goto L_0x0322
            if (r44 == 0) goto L_0x0322
            com.alibaba.fastjson.annotation.JSONField r7 = com.alibaba.fastjson.util.TypeUtils.getSupperMethodAnnotation(r12, r3)
            r22 = r7
            goto L_0x0324
        L_0x0322:
            r22 = r7
        L_0x0324:
            if (r22 == 0) goto L_0x039f
            boolean r7 = r22.deserialize()
            if (r7 != 0) goto L_0x0338
            r26 = r0
            r28 = r5
            r20 = r9
            r0 = r15
            r30 = 1
            r15 = r11
            goto L_0x0549
        L_0x0338:
            int r23 = r22.ordinal()
            com.alibaba.fastjson.serializer.SerializerFeature[] r1 = r22.serialzeFeatures()
            int r24 = com.alibaba.fastjson.serializer.SerializerFeature.of(r1)
            java.lang.String r1 = r22.name()
            int r1 = r1.length()
            if (r1 == 0) goto L_0x0390
            java.lang.String r25 = r22.name()
            com.alibaba.fastjson.util.FieldInfo r8 = new com.alibaba.fastjson.util.FieldInfo
            r7 = 0
            r26 = 0
            r1 = r8
            r2 = r25
            r27 = r3
            r31 = r4
            r4 = r7
            r28 = r5
            r5 = r39
            r19 = r6
            r7 = 3
            r6 = r41
            r7 = r23
            r34 = r8
            r20 = 0
            r8 = r24
            r20 = r9
            r9 = r22
            r10 = r26
            r21 = r15
            r15 = r11
            r11 = r45
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r1 = r34
            addField(r15, r1, r14)
            r11 = r27
            com.alibaba.fastjson.util.TypeUtils.setAccessible(r12, r11, r13)
            r26 = r0
            r0 = r21
            r30 = 1
            goto L_0x0549
        L_0x0390:
            r31 = r4
            r28 = r5
            r19 = r6
            r20 = r9
            r21 = r15
            r15 = r11
            r11 = r3
            r1 = r23
            goto L_0x03ad
        L_0x039f:
            r31 = r4
            r28 = r5
            r19 = r6
            r20 = r9
            r21 = r15
            r15 = r11
            r11 = r3
            r24 = r2
        L_0x03ad:
            java.lang.String r2 = "set"
            r10 = r31
            boolean r2 = r10.startsWith(r2)
            if (r2 != 0) goto L_0x03bf
            r26 = r0
            r0 = r21
            r30 = 1
            goto L_0x0549
        L_0x03bf:
            r9 = 3
            char r8 = r10.charAt(r9)
            boolean r2 = java.lang.Character.isUpperCase(r8)
            if (r2 == 0) goto L_0x03f5
            boolean r2 = com.alibaba.fastjson.util.TypeUtils.compatibleWithJavaBean
            if (r2 == 0) goto L_0x03d8
            java.lang.String r2 = r10.substring(r9)
            java.lang.String r2 = com.alibaba.fastjson.util.TypeUtils.decapitalize(r2)
            r7 = 4
            goto L_0x0421
        L_0x03d8:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            char r3 = r10.charAt(r9)
            char r3 = java.lang.Character.toLowerCase(r3)
            r2.append(r3)
            r7 = 4
            java.lang.String r3 = r10.substring(r7)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            goto L_0x0421
        L_0x03f5:
            r7 = 4
            r2 = 95
            if (r8 != r2) goto L_0x03ff
            java.lang.String r2 = r10.substring(r7)
            goto L_0x0421
        L_0x03ff:
            r2 = 102(0x66, float:1.43E-43)
            if (r8 != r2) goto L_0x0408
            java.lang.String r2 = r10.substring(r9)
            goto L_0x0421
        L_0x0408:
            int r2 = r10.length()
            r3 = 5
            if (r2 < r3) goto L_0x0522
            char r2 = r10.charAt(r7)
            boolean r2 = java.lang.Character.isUpperCase(r2)
            if (r2 == 0) goto L_0x0522
            java.lang.String r2 = r10.substring(r9)
            java.lang.String r2 = com.alibaba.fastjson.util.TypeUtils.decapitalize(r2)
        L_0x0421:
            r6 = r21
            java.lang.reflect.Field r3 = com.alibaba.fastjson.util.TypeUtils.getField(r12, r2, r6)
            if (r3 != 0) goto L_0x045b
            java.lang.Class[] r4 = r11.getParameterTypes()
            r5 = 0
            r4 = r4[r5]
            java.lang.Class r7 = java.lang.Boolean.TYPE
            if (r4 != r7) goto L_0x045b
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r7 = "is"
            r4.append(r7)
            char r7 = r2.charAt(r5)
            char r7 = java.lang.Character.toUpperCase(r7)
            r4.append(r7)
            r7 = 1
            java.lang.String r5 = r2.substring(r7)
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            java.lang.reflect.Field r3 = com.alibaba.fastjson.util.TypeUtils.getField(r12, r4, r6)
            r5 = r3
            goto L_0x045d
        L_0x045b:
            r7 = 1
            r5 = r3
        L_0x045d:
            if (r5 == 0) goto L_0x04dc
            if (r44 == 0) goto L_0x046a
            java.lang.Class<com.alibaba.fastjson.annotation.JSONField> r3 = com.alibaba.fastjson.annotation.JSONField.class
            java.lang.annotation.Annotation r3 = r5.getAnnotation(r3)
            com.alibaba.fastjson.annotation.JSONField r3 = (com.alibaba.fastjson.annotation.JSONField) r3
            goto L_0x046c
        L_0x046a:
            r3 = r17
        L_0x046c:
            r21 = r3
            if (r21 == 0) goto L_0x04cd
            int r23 = r21.ordinal()
            com.alibaba.fastjson.serializer.SerializerFeature[] r1 = r21.serialzeFeatures()
            int r24 = com.alibaba.fastjson.serializer.SerializerFeature.of(r1)
            java.lang.String r1 = r21.name()
            int r1 = r1.length()
            if (r1 == 0) goto L_0x04b8
            java.lang.String r25 = r21.name()
            com.alibaba.fastjson.util.FieldInfo r4 = new com.alibaba.fastjson.util.FieldInfo
            r1 = r4
            r2 = r25
            r3 = r11
            r26 = r0
            r0 = r4
            r4 = r5
            r27 = r5
            r5 = r39
            r29 = r6
            r6 = r41
            r30 = 1
            r7 = r23
            r31 = r8
            r8 = r24
            r9 = r22
            r32 = r10
            r10 = r21
            r33 = r11
            r11 = r45
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            addField(r15, r0, r14)
            r0 = r29
            goto L_0x0549
        L_0x04b8:
            r26 = r0
            r27 = r5
            r29 = r6
            r31 = r8
            r32 = r10
            r33 = r11
            r30 = 1
            if (r22 != 0) goto L_0x04ec
            r0 = r21
            r22 = r0
            goto L_0x04ec
        L_0x04cd:
            r26 = r0
            r27 = r5
            r29 = r6
            r31 = r8
            r32 = r10
            r33 = r11
            r30 = 1
            goto L_0x04ea
        L_0x04dc:
            r26 = r0
            r27 = r5
            r29 = r6
            r31 = r8
            r32 = r10
            r33 = r11
            r30 = 1
        L_0x04ea:
            r23 = r1
        L_0x04ec:
            r11 = r46
            r0 = r29
            if (r11 == 0) goto L_0x04f9
            java.lang.String r1 = r11.translate(r2)
            r21 = r1
            goto L_0x04fb
        L_0x04f9:
            r21 = r2
        L_0x04fb:
            com.alibaba.fastjson.util.FieldInfo r10 = new com.alibaba.fastjson.util.FieldInfo
            r4 = 0
            r25 = 0
            r1 = r10
            r2 = r21
            r3 = r33
            r5 = r39
            r6 = r41
            r7 = r23
            r8 = r24
            r9 = r22
            r35 = r10
            r10 = r25
            r11 = r45
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r1 = r35
            addField(r15, r1, r14)
            com.alibaba.fastjson.util.TypeUtils.setAccessible(r12, r3, r13)
            goto L_0x0549
        L_0x0522:
            r26 = r0
            r31 = r8
            r32 = r10
            r3 = r11
            r0 = r21
            r30 = 1
            goto L_0x0549
        L_0x052e:
            r26 = r0
            r32 = r4
            r28 = r5
            r19 = r6
            r20 = r9
            r0 = r15
            r30 = 1
            r15 = r11
            goto L_0x0549
        L_0x053d:
            r26 = r0
            r32 = r4
            r28 = r5
            r20 = r9
            r0 = r15
            r30 = 1
            r15 = r11
        L_0x0549:
            int r4 = r18 + 1
            r11 = r15
            r9 = r20
            r5 = r28
            r7 = 4
            r8 = 0
            r10 = 1
            r15 = r0
            r0 = r26
            goto L_0x02ac
        L_0x0558:
            r26 = r0
            r20 = r9
            r0 = r15
            r30 = 1
            r15 = r11
            goto L_0x0569
        L_0x0561:
            r26 = r0
            r20 = r9
            r0 = r15
            r30 = 1
            r15 = r11
        L_0x0569:
            java.util.ArrayList r1 = new java.util.ArrayList
            int r2 = r0.length
            r1.<init>(r2)
            r11 = r1
            int r1 = r0.length
            r2 = 0
        L_0x0572:
            if (r2 >= r1) goto L_0x05ac
            r3 = r0[r2]
            int r4 = r3.getModifiers()
            r5 = r4 & 8
            if (r5 == 0) goto L_0x057f
            goto L_0x05a9
        L_0x057f:
            r5 = r4 & 16
            if (r5 == 0) goto L_0x059e
            java.lang.Class r5 = r3.getType()
            java.lang.Class<java.util.Map> r6 = java.util.Map.class
            boolean r6 = r6.isAssignableFrom(r5)
            if (r6 != 0) goto L_0x059a
            java.lang.Class<java.util.Collection> r6 = java.util.Collection.class
            boolean r6 = r6.isAssignableFrom(r5)
            if (r6 == 0) goto L_0x0598
            goto L_0x059a
        L_0x0598:
            r6 = 0
            goto L_0x059b
        L_0x059a:
            r6 = 1
        L_0x059b:
            if (r6 != 0) goto L_0x059e
            goto L_0x05a9
        L_0x059e:
            int r5 = r3.getModifiers()
            r5 = r5 & 1
            if (r5 == 0) goto L_0x05a9
            r11.add(r3)
        L_0x05a9:
            int r2 = r2 + 1
            goto L_0x0572
        L_0x05ac:
            java.lang.Class r1 = r39.getSuperclass()
        L_0x05b0:
            if (r1 == 0) goto L_0x05f7
            java.lang.Class<java.lang.Object> r2 = java.lang.Object.class
            if (r1 == r2) goto L_0x05f7
            java.lang.reflect.Field[] r2 = r1.getDeclaredFields()
            int r3 = r2.length
            r4 = 0
        L_0x05bc:
            if (r4 >= r3) goto L_0x05f2
            r5 = r2[r4]
            int r6 = r5.getModifiers()
            r7 = r6 & 8
            if (r7 == 0) goto L_0x05c9
            goto L_0x05ef
        L_0x05c9:
            r7 = r6 & 16
            if (r7 == 0) goto L_0x05e8
            java.lang.Class r7 = r5.getType()
            java.lang.Class<java.util.Map> r8 = java.util.Map.class
            boolean r8 = r8.isAssignableFrom(r7)
            if (r8 != 0) goto L_0x05e4
            java.lang.Class<java.util.Collection> r8 = java.util.Collection.class
            boolean r8 = r8.isAssignableFrom(r7)
            if (r8 == 0) goto L_0x05e2
            goto L_0x05e4
        L_0x05e2:
            r8 = 0
            goto L_0x05e5
        L_0x05e4:
            r8 = 1
        L_0x05e5:
            if (r8 != 0) goto L_0x05e8
            goto L_0x05ef
        L_0x05e8:
            r7 = r6 & 1
            if (r7 == 0) goto L_0x05ef
            r11.add(r5)
        L_0x05ef:
            int r4 = r4 + 1
            goto L_0x05bc
        L_0x05f2:
            java.lang.Class r1 = r1.getSuperclass()
            goto L_0x05b0
        L_0x05f7:
            java.util.Iterator r18 = r11.iterator()
        L_0x05fb:
            boolean r1 = r18.hasNext()
            if (r1 == 0) goto L_0x06a4
            java.lang.Object r1 = r18.next()
            r10 = r1
            java.lang.reflect.Field r10 = (java.lang.reflect.Field) r10
            java.lang.String r9 = r10.getName()
            r1 = 0
            r2 = 0
            int r3 = r15.size()
            r19 = r1
        L_0x0614:
            if (r2 >= r3) goto L_0x062a
            java.lang.Object r1 = r15.get(r2)
            com.alibaba.fastjson.util.FieldInfo r1 = (com.alibaba.fastjson.util.FieldInfo) r1
            java.lang.String r4 = r1.name
            boolean r4 = r4.equals(r9)
            if (r4 == 0) goto L_0x0627
            r4 = 1
            r19 = r4
        L_0x0627:
            int r2 = r2 + 1
            goto L_0x0614
        L_0x062a:
            if (r19 == 0) goto L_0x062d
            goto L_0x05fb
        L_0x062d:
            r1 = 0
            r2 = 0
            r3 = r9
            if (r44 == 0) goto L_0x063b
            java.lang.Class<com.alibaba.fastjson.annotation.JSONField> r4 = com.alibaba.fastjson.annotation.JSONField.class
            java.lang.annotation.Annotation r4 = r10.getAnnotation(r4)
            com.alibaba.fastjson.annotation.JSONField r4 = (com.alibaba.fastjson.annotation.JSONField) r4
            goto L_0x063d
        L_0x063b:
            r4 = r17
        L_0x063d:
            r21 = r4
            if (r21 == 0) goto L_0x0665
            int r1 = r21.ordinal()
            com.alibaba.fastjson.serializer.SerializerFeature[] r4 = r21.serialzeFeatures()
            int r2 = com.alibaba.fastjson.serializer.SerializerFeature.of(r4)
            java.lang.String r4 = r21.name()
            int r4 = r4.length()
            if (r4 == 0) goto L_0x0660
            java.lang.String r3 = r21.name()
            r22 = r1
            r23 = r2
            goto L_0x0669
        L_0x0660:
            r22 = r1
            r23 = r2
            goto L_0x0669
        L_0x0665:
            r22 = r1
            r23 = r2
        L_0x0669:
            r8 = r46
            if (r8 == 0) goto L_0x0674
            java.lang.String r1 = r8.translate(r3)
            r24 = r1
            goto L_0x0676
        L_0x0674:
            r24 = r3
        L_0x0676:
            com.alibaba.fastjson.util.TypeUtils.setAccessible(r12, r10, r13)
            com.alibaba.fastjson.util.FieldInfo r7 = new com.alibaba.fastjson.util.FieldInfo
            r3 = 0
            r25 = 0
            r1 = r7
            r2 = r24
            r4 = r10
            r5 = r39
            r6 = r41
            r36 = r7
            r7 = r22
            r8 = r23
            r27 = r9
            r9 = r25
            r25 = r10
            r10 = r21
            r28 = r11
            r11 = r45
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r1 = r36
            addField(r15, r1, r14)
            r11 = r28
            goto L_0x05fb
        L_0x06a4:
            r28 = r11
            if (r14 != 0) goto L_0x07b3
            java.lang.reflect.Method[] r11 = r39.getMethods()
            int r10 = r11.length
            r9 = 0
        L_0x06ae:
            if (r9 >= r10) goto L_0x07b3
            r8 = r11[r9]
            java.lang.String r7 = r8.getName()
            int r1 = r7.length()
            r6 = 4
            if (r1 < r6) goto L_0x079e
            int r1 = r8.getModifiers()
            boolean r1 = java.lang.reflect.Modifier.isStatic(r1)
            if (r1 == 0) goto L_0x06d3
            r21 = r9
            r22 = r10
            r23 = r11
            r19 = 3
            r25 = 4
            goto L_0x07ab
        L_0x06d3:
            java.lang.String r1 = "get"
            boolean r1 = r7.startsWith(r1)
            if (r1 == 0) goto L_0x0790
            r5 = 3
            char r1 = r7.charAt(r5)
            boolean r1 = java.lang.Character.isUpperCase(r1)
            if (r1 == 0) goto L_0x0790
            java.lang.Class[] r1 = r8.getParameterTypes()
            int r1 = r1.length
            if (r1 == 0) goto L_0x06f9
            r21 = r9
            r22 = r10
            r23 = r11
            r19 = 3
            r25 = 4
            goto L_0x07ab
        L_0x06f9:
            java.lang.Class r4 = r8.getReturnType()
            java.lang.Class<java.util.Collection> r1 = java.util.Collection.class
            boolean r1 = r1.isAssignableFrom(r4)
            if (r1 != 0) goto L_0x071a
            java.lang.Class<java.util.Map> r1 = java.util.Map.class
            boolean r1 = r1.isAssignableFrom(r4)
            if (r1 == 0) goto L_0x070e
            goto L_0x071a
        L_0x070e:
            r21 = r9
            r22 = r10
            r23 = r11
            r19 = 3
            r25 = 4
            goto L_0x07ab
        L_0x071a:
            if (r44 == 0) goto L_0x0725
            java.lang.Class<com.alibaba.fastjson.annotation.JSONField> r1 = com.alibaba.fastjson.annotation.JSONField.class
            java.lang.annotation.Annotation r1 = r8.getAnnotation(r1)
            com.alibaba.fastjson.annotation.JSONField r1 = (com.alibaba.fastjson.annotation.JSONField) r1
            goto L_0x0727
        L_0x0725:
            r1 = r17
        L_0x0727:
            r18 = r1
            if (r18 == 0) goto L_0x0737
            java.lang.String r1 = r18.name()
            r2 = r1
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0737
            goto L_0x0753
        L_0x0737:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            char r2 = r7.charAt(r5)
            char r2 = java.lang.Character.toLowerCase(r2)
            r1.append(r2)
            java.lang.String r2 = r7.substring(r6)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r2 = r1
        L_0x0753:
            com.alibaba.fastjson.util.FieldInfo r3 = new com.alibaba.fastjson.util.FieldInfo
            r19 = 0
            r21 = 0
            r22 = 0
            r23 = 0
            r1 = r3
            r37 = r3
            r3 = r8
            r24 = r4
            r4 = r19
            r19 = 3
            r5 = r39
            r25 = 4
            r6 = r41
            r27 = r7
            r7 = r21
            r38 = r8
            r8 = r22
            r21 = r9
            r9 = r18
            r22 = r10
            r10 = r23
            r23 = r11
            r11 = r45
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r1 = r37
            addField(r15, r1, r14)
            r1 = r38
            com.alibaba.fastjson.util.TypeUtils.setAccessible(r12, r1, r13)
            goto L_0x07ab
        L_0x0790:
            r27 = r7
            r1 = r8
            r21 = r9
            r22 = r10
            r23 = r11
            r19 = 3
            r25 = 4
            goto L_0x07ab
        L_0x079e:
            r27 = r7
            r1 = r8
            r21 = r9
            r22 = r10
            r23 = r11
            r19 = 3
            r25 = 4
        L_0x07ab:
            int r9 = r21 + 1
            r10 = r22
            r11 = r23
            goto L_0x06ae
        L_0x07b3:
            int r1 = r15.size()
            com.alibaba.fastjson.util.FieldInfo[] r9 = new com.alibaba.fastjson.util.FieldInfo[r1]
            r15.toArray(r9)
            int r1 = r9.length
            com.alibaba.fastjson.util.FieldInfo[] r10 = new com.alibaba.fastjson.util.FieldInfo[r1]
            int r1 = r9.length
            r2 = 0
            java.lang.System.arraycopy(r9, r2, r10, r2, r1)
            java.util.Arrays.sort(r10)
            if (r43 == 0) goto L_0x07d3
            java.lang.Class<com.alibaba.fastjson.annotation.JSONType> r1 = com.alibaba.fastjson.annotation.JSONType.class
            java.lang.annotation.Annotation r1 = r12.getAnnotation(r1)
            com.alibaba.fastjson.annotation.JSONType r1 = (com.alibaba.fastjson.annotation.JSONType) r1
            r8 = r1
            goto L_0x07d5
        L_0x07d3:
            r8 = r17
        L_0x07d5:
            com.alibaba.fastjson.parser.JavaBeanInfo r11 = new com.alibaba.fastjson.parser.JavaBeanInfo
            r4 = 0
            r5 = 0
            r1 = r11
            r2 = r39
            r3 = r20
            r6 = r9
            r7 = r10
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.JavaBeanInfo.build(java.lang.Class, int, java.lang.reflect.Type, boolean, boolean, boolean, boolean, com.alibaba.fastjson.PropertyNamingStrategy):com.alibaba.fastjson.parser.JavaBeanInfo");
    }
}

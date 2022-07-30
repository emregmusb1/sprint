package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JavaBeanSerializer implements ObjectSerializer {
    private static final char[] false_chars = {'f', 'a', 'l', 's', 'e'};
    private static final char[] true_chars = {'t', 'r', 'u', 'e'};
    protected int features;
    private final FieldSerializer[] getters;
    private final FieldSerializer[] sortedGetters;
    protected String typeName;

    public JavaBeanSerializer(Class<?> clazz) {
        this(clazz, (PropertyNamingStrategy) null);
    }

    public JavaBeanSerializer(Class<?> clazz, PropertyNamingStrategy propertyNamingStrategy) {
        this(clazz, clazz.getModifiers(), (Map<String, String>) null, false, true, true, true, propertyNamingStrategy);
    }

    public JavaBeanSerializer(Class<?> clazz, String... aliasList) {
        this(clazz, clazz.getModifiers(), map(aliasList), false, true, true, true, (PropertyNamingStrategy) null);
    }

    private static Map<String, String> map(String... aliasList) {
        Map<String, String> aliasMap = new HashMap<>();
        for (String alias : aliasList) {
            aliasMap.put(alias, alias);
        }
        return aliasMap;
    }

    public JavaBeanSerializer(Class<?> clazz, int classModifiers, Map<String, String> aliasMap, boolean fieldOnly, boolean jsonTypeSupport, boolean jsonFieldSupport, boolean fieldGenericSupport, PropertyNamingStrategy propertyNamingStrategy) {
        JSONType jsonType;
        this.features = 0;
        if (jsonTypeSupport) {
            Class<?> cls = clazz;
            jsonType = (JSONType) clazz.getAnnotation(JSONType.class);
        } else {
            Class<?> cls2 = clazz;
            jsonType = null;
        }
        if (jsonType != null) {
            this.features = SerializerFeature.of(jsonType.serialzeFeatures());
            this.typeName = jsonType.typeName();
            if (this.typeName.length() == 0) {
                this.typeName = null;
            }
        }
        List<FieldInfo> fieldInfoList = TypeUtils.computeGetters(clazz, classModifiers, fieldOnly, jsonType, aliasMap, false, jsonFieldSupport, fieldGenericSupport, propertyNamingStrategy);
        List<FieldSerializer> getterList = new ArrayList<>();
        for (FieldInfo fieldInfo : fieldInfoList) {
            getterList.add(new FieldSerializer(fieldInfo));
        }
        this.getters = (FieldSerializer[]) getterList.toArray(new FieldSerializer[getterList.size()]);
        String[] orders = jsonType != null ? jsonType.orders() : null;
        if (orders == null || orders.length == 0) {
            FieldSerializer[] fieldSerializerArr = this.getters;
            FieldSerializer[] sortedGetters2 = new FieldSerializer[fieldSerializerArr.length];
            System.arraycopy(fieldSerializerArr, 0, sortedGetters2, 0, fieldSerializerArr.length);
            Arrays.sort(sortedGetters2);
            if (Arrays.equals(sortedGetters2, this.getters)) {
                this.sortedGetters = this.getters;
            } else {
                this.sortedGetters = sortedGetters2;
            }
        } else {
            List<FieldInfo> fieldInfoList2 = TypeUtils.computeGetters(clazz, classModifiers, fieldOnly, jsonType, aliasMap, true, jsonFieldSupport, fieldGenericSupport, propertyNamingStrategy);
            List<FieldSerializer> getterList2 = new ArrayList<>();
            for (FieldInfo fieldInfo2 : fieldInfoList2) {
                getterList2.add(new FieldSerializer(fieldInfo2));
            }
            this.sortedGetters = (FieldSerializer[]) getterList2.toArray(new FieldSerializer[getterList2.size()]);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:275:0x03bd, code lost:
        if (((java.lang.Boolean) r1).booleanValue() == false) goto L_0x03c0;
     */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0149 A[SYNTHETIC, Splitter:B:101:0x0149] */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0160 A[Catch:{ Exception -> 0x069e, all -> 0x0697 }] */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0162 A[Catch:{ Exception -> 0x069e, all -> 0x0697 }] */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x016e A[Catch:{ Exception -> 0x069e, all -> 0x0697 }] */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0170 A[Catch:{ Exception -> 0x069e, all -> 0x0697 }] */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x018a A[SYNTHETIC, Splitter:B:122:0x018a] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x028d A[Catch:{ Exception -> 0x01c2, all -> 0x01b7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x02f2 A[Catch:{ Exception -> 0x01c2, all -> 0x01b7 }, LOOP:4: B:214:0x02ec->B:216:0x02f2, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x0349 A[Catch:{ Exception -> 0x01c2, all -> 0x01b7 }, LOOP:5: B:235:0x0343->B:237:0x0349, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0603  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0629  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0630 A[SYNTHETIC, Splitter:B:426:0x0630] */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x0657 A[SYNTHETIC, Splitter:B:437:0x0657] */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x06a8 A[SYNTHETIC, Splitter:B:465:0x06a8] */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x05e8 A[EDGE_INSN: B:474:0x05e8->B:402:0x05e8 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:479:0x02a7 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0102 A[Catch:{ Exception -> 0x00a6, all -> 0x009f }] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x011a A[SYNTHETIC, Splitter:B:87:0x011a] */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x013d  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x013f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void write(com.alibaba.fastjson.serializer.JSONSerializer r46, java.lang.Object r47, java.lang.Object r48, java.lang.reflect.Type r49) throws java.io.IOException {
        /*
            r45 = this;
            r1 = r45
            r2 = r46
            r3 = r47
            r4 = r48
            r5 = r49
            com.alibaba.fastjson.serializer.SerializeWriter r6 = r2.out
            if (r3 != 0) goto L_0x0012
            r6.writeNull()
            return
        L_0x0012:
            com.alibaba.fastjson.serializer.SerialContext r7 = r2.context
            if (r7 == 0) goto L_0x0021
            com.alibaba.fastjson.serializer.SerialContext r7 = r2.context
            int r7 = r7.features
            com.alibaba.fastjson.serializer.SerializerFeature r8 = com.alibaba.fastjson.serializer.SerializerFeature.DisableCircularReferenceDetect
            int r8 = r8.mask
            r7 = r7 & r8
            if (r7 != 0) goto L_0x0031
        L_0x0021:
            java.util.IdentityHashMap<java.lang.Object, com.alibaba.fastjson.serializer.SerialContext> r7 = r2.references
            if (r7 == 0) goto L_0x0031
            java.util.IdentityHashMap<java.lang.Object, com.alibaba.fastjson.serializer.SerialContext> r7 = r2.references
            boolean r7 = r7.containsKey(r3)
            if (r7 == 0) goto L_0x0031
            r46.writeReference(r47)
            return
        L_0x0031:
            int r7 = r6.features
            com.alibaba.fastjson.serializer.SerializerFeature r8 = com.alibaba.fastjson.serializer.SerializerFeature.SortField
            int r8 = r8.mask
            r7 = r7 & r8
            if (r7 == 0) goto L_0x003d
            com.alibaba.fastjson.serializer.FieldSerializer[] r7 = r1.sortedGetters
            goto L_0x003f
        L_0x003d:
            com.alibaba.fastjson.serializer.FieldSerializer[] r7 = r1.getters
        L_0x003f:
            com.alibaba.fastjson.serializer.SerialContext r8 = r2.context
            int r9 = r6.features
            com.alibaba.fastjson.serializer.SerializerFeature r10 = com.alibaba.fastjson.serializer.SerializerFeature.DisableCircularReferenceDetect
            int r10 = r10.mask
            r9 = r9 & r10
            if (r9 != 0) goto L_0x0065
            com.alibaba.fastjson.serializer.SerialContext r9 = new com.alibaba.fastjson.serializer.SerialContext
            int r10 = r1.features
            r9.<init>(r8, r3, r4, r10)
            r2.context = r9
            java.util.IdentityHashMap<java.lang.Object, com.alibaba.fastjson.serializer.SerialContext> r9 = r2.references
            if (r9 != 0) goto L_0x005e
            java.util.IdentityHashMap r9 = new java.util.IdentityHashMap
            r9.<init>()
            r2.references = r9
        L_0x005e:
            java.util.IdentityHashMap<java.lang.Object, com.alibaba.fastjson.serializer.SerialContext> r9 = r2.references
            com.alibaba.fastjson.serializer.SerialContext r10 = r2.context
            r9.put(r3, r10)
        L_0x0065:
            int r9 = r1.features
            com.alibaba.fastjson.serializer.SerializerFeature r10 = com.alibaba.fastjson.serializer.SerializerFeature.BeanToArray
            int r10 = r10.mask
            r9 = r9 & r10
            r10 = 1
            if (r9 != 0) goto L_0x007b
            int r9 = r6.features
            com.alibaba.fastjson.serializer.SerializerFeature r12 = com.alibaba.fastjson.serializer.SerializerFeature.BeanToArray
            int r12 = r12.mask
            r9 = r9 & r12
            if (r9 == 0) goto L_0x0079
            goto L_0x007b
        L_0x0079:
            r9 = 0
            goto L_0x007c
        L_0x007b:
            r9 = 1
        L_0x007c:
            if (r9 == 0) goto L_0x0081
            r12 = 91
            goto L_0x0083
        L_0x0081:
            r12 = 123(0x7b, float:1.72E-43)
        L_0x0083:
            if (r9 == 0) goto L_0x0088
            r13 = 93
            goto L_0x008a
        L_0x0088:
            r13 = 125(0x7d, float:1.75E-43)
        L_0x008a:
            int r14 = r6.count     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            int r14 = r14 + r10
            char[] r15 = r6.buf     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            int r15 = r15.length     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            if (r14 <= r15) goto L_0x00ac
            java.io.Writer r15 = r6.writer     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            if (r15 != 0) goto L_0x009a
            r6.expandCapacity(r14)     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            goto L_0x00ac
        L_0x009a:
            r6.flush()     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            r14 = 1
            goto L_0x00ac
        L_0x009f:
            r0 = move-exception
            r5 = r3
            r1 = r8
            r3 = r0
            r8 = r4
            goto L_0x06ca
        L_0x00a6:
            r0 = move-exception
            r5 = r3
            r1 = r8
            r3 = r0
            goto L_0x06a2
        L_0x00ac:
            char[] r15 = r6.buf     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            int r10 = r6.count     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            r15[r10] = r12     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            r6.count = r14     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            int r10 = r7.length     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            if (r10 <= 0) goto L_0x00c6
            int r10 = r6.features     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            com.alibaba.fastjson.serializer.SerializerFeature r14 = com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            int r14 = r14.mask     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            r10 = r10 & r14
            if (r10 == 0) goto L_0x00c6
            r46.incrementIndent()     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            r46.println()     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
        L_0x00c6:
            r10 = 0
            int r14 = r1.features     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            com.alibaba.fastjson.serializer.SerializerFeature r15 = com.alibaba.fastjson.serializer.SerializerFeature.WriteClassName     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            int r15 = r15.mask     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            r14 = r14 & r15
            if (r14 != 0) goto L_0x00ed
            int r14 = r6.features     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            com.alibaba.fastjson.serializer.SerializerFeature r15 = com.alibaba.fastjson.serializer.SerializerFeature.WriteClassName     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            int r15 = r15.mask     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            r14 = r14 & r15
            if (r14 == 0) goto L_0x00eb
            if (r5 != 0) goto L_0x00ed
            int r14 = r6.features     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            com.alibaba.fastjson.serializer.SerializerFeature r15 = com.alibaba.fastjson.serializer.SerializerFeature.NotWriteRootClassName     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            int r15 = r15.mask     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            r14 = r14 & r15
            if (r14 == 0) goto L_0x00ed
            com.alibaba.fastjson.serializer.SerialContext r14 = r2.context     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            com.alibaba.fastjson.serializer.SerialContext r14 = r14.parent     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            if (r14 == 0) goto L_0x00eb
            goto L_0x00ed
        L_0x00eb:
            r14 = 0
            goto L_0x00ee
        L_0x00ed:
            r14 = 1
        L_0x00ee:
            if (r14 == 0) goto L_0x010f
            java.lang.Class r15 = r47.getClass()     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            if (r15 == r5) goto L_0x010f
            com.alibaba.fastjson.serializer.SerializeConfig r11 = r2.config     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            java.lang.String r11 = r11.typeKey     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            r5 = 0
            r6.writeFieldName(r11, r5)     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            java.lang.String r5 = r1.typeName     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            if (r5 != 0) goto L_0x010b
            java.lang.Class r11 = r47.getClass()     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            java.lang.String r11 = r11.getName()     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            r5 = r11
        L_0x010b:
            r2.write((java.lang.String) r5)     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            r10 = 1
        L_0x010f:
            if (r10 == 0) goto L_0x0114
            r5 = 44
            goto L_0x0115
        L_0x0114:
            r5 = 0
        L_0x0115:
            r15 = r5
            java.util.List<com.alibaba.fastjson.serializer.BeforeFilter> r11 = r2.beforeFilters     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            if (r11 == 0) goto L_0x0139
            java.util.List<com.alibaba.fastjson.serializer.BeforeFilter> r11 = r2.beforeFilters     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            java.util.Iterator r11 = r11.iterator()     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
        L_0x0120:
            boolean r18 = r11.hasNext()     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            if (r18 == 0) goto L_0x0139
            java.lang.Object r18 = r11.next()     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            com.alibaba.fastjson.serializer.BeforeFilter r18 = (com.alibaba.fastjson.serializer.BeforeFilter) r18     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            r19 = r18
            r1 = r19
            char r18 = r1.writeBefore(r2, r3, r15)     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            r15 = r18
            r1 = r45
            goto L_0x0120
        L_0x0139:
            r1 = 44
            if (r15 != r1) goto L_0x013f
            r1 = 1
            goto L_0x0140
        L_0x013f:
            r1 = 0
        L_0x0140:
            int r10 = r6.features     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            com.alibaba.fastjson.serializer.SerializerFeature r11 = com.alibaba.fastjson.serializer.SerializerFeature.QuoteFieldNames     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            int r11 = r11.mask     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            r10 = r10 & r11
            if (r10 == 0) goto L_0x0154
            int r10 = r6.features     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            com.alibaba.fastjson.serializer.SerializerFeature r11 = com.alibaba.fastjson.serializer.SerializerFeature.UseSingleQuotes     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            int r11 = r11.mask     // Catch:{ Exception -> 0x00a6, all -> 0x009f }
            r10 = r10 & r11
            if (r10 != 0) goto L_0x0154
            r10 = 1
            goto L_0x0155
        L_0x0154:
            r10 = 0
        L_0x0155:
            int r11 = r6.features     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            r18 = r1
            com.alibaba.fastjson.serializer.SerializerFeature r1 = com.alibaba.fastjson.serializer.SerializerFeature.UseSingleQuotes     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            int r1 = r1.mask     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            r1 = r1 & r11
            if (r1 == 0) goto L_0x0162
            r1 = 1
            goto L_0x0163
        L_0x0162:
            r1 = 0
        L_0x0163:
            int r11 = r6.features     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            r19 = r5
            com.alibaba.fastjson.serializer.SerializerFeature r5 = com.alibaba.fastjson.serializer.SerializerFeature.NotWriteDefaultValue     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            int r5 = r5.mask     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            r5 = r5 & r11
            if (r5 == 0) goto L_0x0170
            r5 = 1
            goto L_0x0171
        L_0x0170:
            r5 = 0
        L_0x0171:
            java.util.List<com.alibaba.fastjson.serializer.PropertyFilter> r11 = r2.propertyFilters     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            r20 = r12
            java.util.List<com.alibaba.fastjson.serializer.NameFilter> r12 = r2.nameFilters     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            r21 = r14
            java.util.List<com.alibaba.fastjson.serializer.ValueFilter> r14 = r2.valueFilters     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            r22 = r15
            java.util.List<com.alibaba.fastjson.serializer.PropertyPreFilter> r15 = r2.propertyPreFilters     // Catch:{ Exception -> 0x069e, all -> 0x0697 }
            r16 = 0
            r23 = r16
            r4 = r23
        L_0x0185:
            r23 = r8
            int r8 = r7.length     // Catch:{ Exception -> 0x0691, all -> 0x0689 }
            if (r4 >= r8) goto L_0x05e8
            r8 = r7[r4]     // Catch:{ Exception -> 0x05e0, all -> 0x05d6 }
            r24 = r13
            com.alibaba.fastjson.util.FieldInfo r13 = r8.fieldInfo     // Catch:{ Exception -> 0x05e0, all -> 0x05d6 }
            r25 = r7
            java.lang.Class<?> r7 = r13.fieldClass     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r26 = r4
            java.lang.String r4 = r13.name     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r27 = r1
            int r1 = r6.features     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r28 = r10
            com.alibaba.fastjson.serializer.SerializerFeature r10 = com.alibaba.fastjson.serializer.SerializerFeature.SkipTransientField     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            int r10 = r10.mask     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r1 = r1 & r10
            if (r1 == 0) goto L_0x01cb
            java.lang.reflect.Field r1 = r13.field     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r1 == 0) goto L_0x01cb
            boolean r10 = r13.fieldTransient     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r10 == 0) goto L_0x01cb
            r37 = r11
            r38 = r12
            r35 = r14
            r39 = r15
            goto L_0x03c0
        L_0x01b7:
            r0 = move-exception
            r8 = r48
            r5 = r3
            r1 = r23
            r7 = r25
            r3 = r0
            goto L_0x06ca
        L_0x01c2:
            r0 = move-exception
            r5 = r3
            r1 = r23
            r7 = r25
            r3 = r0
            goto L_0x06a2
        L_0x01cb:
            r1 = 1
            if (r15 == 0) goto L_0x01f4
            java.util.Iterator r10 = r15.iterator()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
        L_0x01d2:
            boolean r29 = r10.hasNext()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r29 == 0) goto L_0x01f1
            java.lang.Object r29 = r10.next()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            com.alibaba.fastjson.serializer.PropertyPreFilter r29 = (com.alibaba.fastjson.serializer.PropertyPreFilter) r29     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r30 = r29
            r29 = r1
            r1 = r30
            boolean r30 = r1.apply(r2, r3, r4)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r30 != 0) goto L_0x01ee
            r10 = 0
            r29 = r10
            goto L_0x01f6
        L_0x01ee:
            r1 = r29
            goto L_0x01d2
        L_0x01f1:
            r29 = r1
            goto L_0x01f6
        L_0x01f4:
            r29 = r1
        L_0x01f6:
            if (r29 != 0) goto L_0x0202
            r37 = r11
            r38 = r12
            r35 = r14
            r39 = r15
            goto L_0x03c0
        L_0x0202:
            r1 = 0
            r10 = 0
            r30 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = r1
            boolean r1 = r13.fieldAccess     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            if (r1 == 0) goto L_0x0249
            java.lang.Class r1 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r1) goto L_0x0220
            java.lang.reflect.Field r1 = r13.field     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            int r1 = r1.getInt(r3)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r10 = r1
            r34 = 1
            goto L_0x0251
        L_0x0220:
            java.lang.Class r1 = java.lang.Long.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r1) goto L_0x022f
            java.lang.reflect.Field r1 = r13.field     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            long r36 = r1.getLong(r3)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r30 = r36
            r34 = 1
            goto L_0x0251
        L_0x022f:
            java.lang.Class r1 = java.lang.Boolean.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r1) goto L_0x023e
            java.lang.reflect.Field r1 = r13.field     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            boolean r1 = r1.getBoolean(r3)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r32 = r1
            r34 = 1
            goto L_0x0251
        L_0x023e:
            java.lang.reflect.Field r1 = r13.field     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r33 = 1
            r35 = r1
            goto L_0x0251
        L_0x0249:
            java.lang.Object r1 = r8.getPropertyValue(r3)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r33 = 1
            r35 = r1
        L_0x0251:
            r1 = 1
            if (r11 == 0) goto L_0x02aa
            if (r34 == 0) goto L_0x027f
            r36 = r1
            java.lang.Class r1 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r1) goto L_0x0265
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r35 = r1
            r33 = 1
            goto L_0x0283
        L_0x0265:
            java.lang.Class r1 = java.lang.Long.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r1) goto L_0x0272
            java.lang.Long r1 = java.lang.Long.valueOf(r30)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r35 = r1
            r33 = 1
            goto L_0x0283
        L_0x0272:
            java.lang.Class r1 = java.lang.Boolean.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r1) goto L_0x0281
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r32)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r35 = r1
            r33 = 1
            goto L_0x0283
        L_0x027f:
            r36 = r1
        L_0x0281:
            r1 = r35
        L_0x0283:
            java.util.Iterator r35 = r11.iterator()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
        L_0x0287:
            boolean r37 = r35.hasNext()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r37 == 0) goto L_0x02a7
            java.lang.Object r37 = r35.next()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            com.alibaba.fastjson.serializer.PropertyFilter r37 = (com.alibaba.fastjson.serializer.PropertyFilter) r37     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r38 = r37
            r37 = r11
            r11 = r38
            boolean r38 = r11.apply(r3, r4, r1)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r38 != 0) goto L_0x02a4
            r35 = 0
            r36 = r35
            goto L_0x02b0
        L_0x02a4:
            r11 = r37
            goto L_0x0287
        L_0x02a7:
            r37 = r11
            goto L_0x02b0
        L_0x02aa:
            r36 = r1
            r37 = r11
            r1 = r35
        L_0x02b0:
            if (r36 != 0) goto L_0x02ba
            r38 = r12
            r35 = r14
            r39 = r15
            goto L_0x03c0
        L_0x02ba:
            r11 = r4
            if (r12 == 0) goto L_0x030a
            if (r34 == 0) goto L_0x02e4
            if (r33 != 0) goto L_0x02e4
            r35 = r1
            java.lang.Class r1 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r1) goto L_0x02ce
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r33 = 1
            goto L_0x02e8
        L_0x02ce:
            java.lang.Class r1 = java.lang.Long.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r1) goto L_0x02d9
            java.lang.Long r1 = java.lang.Long.valueOf(r30)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r33 = 1
            goto L_0x02e8
        L_0x02d9:
            java.lang.Class r1 = java.lang.Boolean.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r1) goto L_0x02e6
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r32)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r33 = 1
            goto L_0x02e8
        L_0x02e4:
            r35 = r1
        L_0x02e6:
            r1 = r35
        L_0x02e8:
            java.util.Iterator r35 = r12.iterator()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
        L_0x02ec:
            boolean r38 = r35.hasNext()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r38 == 0) goto L_0x0307
            java.lang.Object r38 = r35.next()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            com.alibaba.fastjson.serializer.NameFilter r38 = (com.alibaba.fastjson.serializer.NameFilter) r38     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r39 = r38
            r38 = r12
            r12 = r39
            java.lang.String r39 = r12.process(r3, r11, r1)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r11 = r39
            r12 = r38
            goto L_0x02ec
        L_0x0307:
            r38 = r12
            goto L_0x030e
        L_0x030a:
            r35 = r1
            r38 = r12
        L_0x030e:
            r12 = r1
            if (r14 == 0) goto L_0x0361
            if (r34 == 0) goto L_0x033b
            if (r33 != 0) goto L_0x033b
            r35 = r1
            java.lang.Class r1 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r1) goto L_0x0323
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r12 = r1
            r33 = 1
            goto L_0x033f
        L_0x0323:
            java.lang.Class r1 = java.lang.Long.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r1) goto L_0x032f
            java.lang.Long r1 = java.lang.Long.valueOf(r30)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r12 = r1
            r33 = 1
            goto L_0x033f
        L_0x032f:
            java.lang.Class r1 = java.lang.Boolean.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r1) goto L_0x033d
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r32)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r12 = r1
            r33 = 1
            goto L_0x033f
        L_0x033b:
            r35 = r1
        L_0x033d:
            r1 = r35
        L_0x033f:
            java.util.Iterator r35 = r14.iterator()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
        L_0x0343:
            boolean r39 = r35.hasNext()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r39 == 0) goto L_0x035e
            java.lang.Object r39 = r35.next()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            com.alibaba.fastjson.serializer.ValueFilter r39 = (com.alibaba.fastjson.serializer.ValueFilter) r39     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r40 = r39
            r39 = r12
            r12 = r40
            java.lang.Object r40 = r12.process(r3, r4, r1)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r1 = r40
            r12 = r39
            goto L_0x0343
        L_0x035e:
            r39 = r12
            goto L_0x0363
        L_0x0361:
            r35 = r1
        L_0x0363:
            if (r33 == 0) goto L_0x037e
            if (r1 != 0) goto L_0x037e
            if (r9 != 0) goto L_0x037e
            r35 = r14
            boolean r14 = r8.writeNull     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r14 != 0) goto L_0x037b
            int r14 = r6.features     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r39 = r15
            com.alibaba.fastjson.serializer.SerializerFeature r15 = com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            int r15 = r15.mask     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r14 = r14 & r15
            if (r14 != 0) goto L_0x0382
            goto L_0x03c0
        L_0x037b:
            r39 = r15
            goto L_0x0382
        L_0x037e:
            r35 = r14
            r39 = r15
        L_0x0382:
            if (r33 == 0) goto L_0x03c7
            if (r1 == 0) goto L_0x03c7
            if (r5 == 0) goto L_0x03c7
            java.lang.Class r14 = java.lang.Byte.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 == r14) goto L_0x03a0
            java.lang.Class r14 = java.lang.Short.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 == r14) goto L_0x03a0
            java.lang.Class r14 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 == r14) goto L_0x03a0
            java.lang.Class r14 = java.lang.Long.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 == r14) goto L_0x03a0
            java.lang.Class r14 = java.lang.Float.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 == r14) goto L_0x03a0
            java.lang.Class r14 = java.lang.Double.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r14) goto L_0x03ae
        L_0x03a0:
            boolean r14 = r1 instanceof java.lang.Number     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r14 == 0) goto L_0x03ae
            r14 = r1
            java.lang.Number r14 = (java.lang.Number) r14     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            byte r14 = r14.byteValue()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r14 != 0) goto L_0x03ae
            goto L_0x03c0
        L_0x03ae:
            java.lang.Class r14 = java.lang.Boolean.TYPE     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r7 != r14) goto L_0x03c7
            boolean r14 = r1 instanceof java.lang.Boolean     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r14 == 0) goto L_0x03c7
            r14 = r1
            java.lang.Boolean r14 = (java.lang.Boolean) r14     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            boolean r14 = r14.booleanValue()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r14 != 0) goto L_0x03c7
        L_0x03c0:
            r40 = r5
            r14 = 0
            r17 = 44
            goto L_0x05a6
        L_0x03c7:
            if (r18 == 0) goto L_0x03f7
            int r14 = r6.count     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r15 = 1
            int r14 = r14 + r15
            char[] r15 = r6.buf     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            int r15 = r15.length     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r14 <= r15) goto L_0x03de
            java.io.Writer r15 = r6.writer     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r15 != 0) goto L_0x03da
            r6.expandCapacity(r14)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            goto L_0x03de
        L_0x03da:
            r6.flush()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r14 = 1
        L_0x03de:
            char[] r15 = r6.buf     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r40 = r5
            int r5 = r6.count     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r17 = 44
            r15[r5] = r17     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r6.count = r14     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            int r5 = r6.features     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            com.alibaba.fastjson.serializer.SerializerFeature r14 = com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            int r14 = r14.mask     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r5 = r5 & r14
            if (r5 == 0) goto L_0x03fb
            r46.println()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            goto L_0x03fb
        L_0x03f7:
            r40 = r5
            r17 = 44
        L_0x03fb:
            if (r11 == r4) goto L_0x0411
            if (r9 != 0) goto L_0x0403
            r5 = 1
            r6.writeFieldName(r11, r5)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
        L_0x0403:
            r2.write((java.lang.Object) r1)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r41 = r4
            r42 = r11
            r44 = r12
            r4 = r30
            r14 = 0
            goto L_0x05a3
        L_0x0411:
            if (r12 == r1) goto L_0x0426
            if (r9 != 0) goto L_0x0418
            r8.writePrefix(r2)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
        L_0x0418:
            r2.write((java.lang.Object) r1)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r41 = r4
            r42 = r11
            r44 = r12
            r4 = r30
            r14 = 0
            goto L_0x05a3
        L_0x0426:
            if (r9 != 0) goto L_0x0487
            if (r28 == 0) goto L_0x047d
            char[] r5 = r13.name_chars     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r14 = 0
            int r15 = r5.length     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r41 = r4
            int r4 = r6.count     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            int r4 = r4 + r15
            r42 = r11
            char[] r11 = r6.buf     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            int r11 = r11.length     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r4 <= r11) goto L_0x046d
            java.io.Writer r11 = r6.writer     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r11 != 0) goto L_0x0446
            r6.expandCapacity(r4)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r43 = r4
            r44 = r12
            goto L_0x0471
        L_0x0446:
            char[] r11 = r6.buf     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            int r11 = r11.length     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r43 = r4
            int r4 = r6.count     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            int r11 = r11 - r4
            char[] r4 = r6.buf     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r44 = r12
            int r12 = r6.count     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            java.lang.System.arraycopy(r5, r14, r4, r12, r11)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            char[] r4 = r6.buf     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            int r4 = r4.length     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r6.count = r4     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r6.flush()     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            int r15 = r15 - r11
            int r14 = r14 + r11
            char[] r4 = r6.buf     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            int r4 = r4.length     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            if (r15 > r4) goto L_0x0468
            r4 = r15
            goto L_0x0473
        L_0x0468:
            r4 = r43
            r12 = r44
            goto L_0x0446
        L_0x046d:
            r43 = r4
            r44 = r12
        L_0x0471:
            r4 = r43
        L_0x0473:
            char[] r11 = r6.buf     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            int r12 = r6.count     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            java.lang.System.arraycopy(r5, r14, r11, r12, r15)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r6.count = r4     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            goto L_0x048d
        L_0x047d:
            r41 = r4
            r42 = r11
            r44 = r12
            r8.writePrefix(r2)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            goto L_0x048d
        L_0x0487:
            r41 = r4
            r42 = r11
            r44 = r12
        L_0x048d:
            if (r34 == 0) goto L_0x0522
            if (r33 != 0) goto L_0x0522
            java.lang.Class r4 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            if (r7 != r4) goto L_0x04ed
            r4 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r10 != r4) goto L_0x04a3
            java.lang.String r4 = "-2147483648"
            r6.write((java.lang.String) r4)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r4 = r30
            r14 = 0
            goto L_0x05a3
        L_0x04a3:
            if (r10 >= 0) goto L_0x04a7
            int r4 = -r10
            goto L_0x04a8
        L_0x04a7:
            r4 = r10
        L_0x04a8:
            r5 = 0
            r11 = r5
        L_0x04aa:
            int[] r5 = com.alibaba.fastjson.serializer.SerializeWriter.sizeTable     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r5 = r5[r11]     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            if (r4 > r5) goto L_0x04e7
            int r5 = r11 + 1
            if (r10 >= 0) goto L_0x04b7
            int r5 = r5 + 1
        L_0x04b7:
            r11 = 0
            int r12 = r6.count     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            int r12 = r12 + r5
            char[] r14 = r6.buf     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            int r14 = r14.length     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            if (r12 <= r14) goto L_0x04d7
            java.io.Writer r14 = r6.writer     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            if (r14 != 0) goto L_0x04c9
            r6.expandCapacity(r12)     // Catch:{ Exception -> 0x01c2, all -> 0x01b7 }
            r15 = r4
            goto L_0x04d8
        L_0x04c9:
            char[] r14 = new char[r5]     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r15 = r4
            long r3 = (long) r10     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            com.alibaba.fastjson.serializer.SerializeWriter.getChars(r3, r5, r14)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            int r3 = r14.length     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r4 = 0
            r6.write((char[]) r14, (int) r4, (int) r3)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r11 = 1
            goto L_0x04d8
        L_0x04d7:
            r15 = r4
        L_0x04d8:
            if (r11 != 0) goto L_0x04e2
            long r3 = (long) r10     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            char[] r14 = r6.buf     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            com.alibaba.fastjson.serializer.SerializeWriter.getChars(r3, r12, r14)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r6.count = r12     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
        L_0x04e2:
            r4 = r30
            r14 = 0
            goto L_0x05a3
        L_0x04e7:
            r15 = r4
            int r11 = r11 + 1
            r3 = r47
            goto L_0x04aa
        L_0x04ed:
            java.lang.Class r3 = java.lang.Long.TYPE     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            if (r7 != r3) goto L_0x04fb
            com.alibaba.fastjson.serializer.SerializeWriter r3 = r2.out     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r4 = r30
            r3.writeLong(r4)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r14 = 0
            goto L_0x05a3
        L_0x04fb:
            r4 = r30
            java.lang.Class r3 = java.lang.Boolean.TYPE     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            if (r7 != r3) goto L_0x051f
            if (r32 == 0) goto L_0x0511
            com.alibaba.fastjson.serializer.SerializeWriter r3 = r2.out     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            char[] r11 = true_chars     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            char[] r12 = true_chars     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            int r12 = r12.length     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r14 = 0
            r3.write((char[]) r11, (int) r14, (int) r12)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r14 = 0
            goto L_0x05a3
        L_0x0511:
            com.alibaba.fastjson.serializer.SerializeWriter r3 = r2.out     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            char[] r11 = false_chars     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            char[] r12 = false_chars     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            int r12 = r12.length     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r14 = 0
            r3.write((char[]) r11, (int) r14, (int) r12)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r14 = 0
            goto L_0x05a3
        L_0x051f:
            r14 = 0
            goto L_0x05a3
        L_0x0522:
            r4 = r30
            if (r9 != 0) goto L_0x059f
            java.lang.Class<java.lang.String> r3 = java.lang.String.class
            if (r7 != r3) goto L_0x055b
            if (r1 != 0) goto L_0x054b
            int r3 = r6.features     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            com.alibaba.fastjson.serializer.SerializerFeature r11 = com.alibaba.fastjson.serializer.SerializerFeature.WriteNullStringAsEmpty     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            int r11 = r11.mask     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r3 = r3 & r11
            if (r3 != 0) goto L_0x0544
            int r3 = r8.features     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            com.alibaba.fastjson.serializer.SerializerFeature r11 = com.alibaba.fastjson.serializer.SerializerFeature.WriteNullStringAsEmpty     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            int r11 = r11.mask     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r3 = r3 & r11
            if (r3 == 0) goto L_0x053f
            goto L_0x0544
        L_0x053f:
            r6.writeNull()     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r14 = 0
            goto L_0x05a3
        L_0x0544:
            java.lang.String r3 = ""
            r6.writeString(r3)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r14 = 0
            goto L_0x05a3
        L_0x054b:
            r3 = r1
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            if (r27 == 0) goto L_0x0554
            r6.writeStringWithSingleQuote(r3)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            goto L_0x0559
        L_0x0554:
            r11 = 1
            r12 = 0
            r6.writeStringWithDoubleQuote(r3, r12, r11)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
        L_0x0559:
            r14 = 0
            goto L_0x05a3
        L_0x055b:
            boolean r3 = r13.isEnum     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            if (r3 == 0) goto L_0x059a
            if (r1 == 0) goto L_0x0595
            int r3 = r6.features     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            com.alibaba.fastjson.serializer.SerializerFeature r11 = com.alibaba.fastjson.serializer.SerializerFeature.WriteEnumUsingToString     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            int r11 = r11.mask     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r3 = r3 & r11
            if (r3 == 0) goto L_0x0589
            r3 = r1
            java.lang.Enum r3 = (java.lang.Enum) r3     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            java.lang.String r11 = r3.toString()     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            int r12 = r6.features     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            com.alibaba.fastjson.serializer.SerializerFeature r14 = com.alibaba.fastjson.serializer.SerializerFeature.UseSingleQuotes     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            int r14 = r14.mask     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r12 = r12 & r14
            if (r12 == 0) goto L_0x057c
            r12 = 1
            goto L_0x057d
        L_0x057c:
            r12 = 0
        L_0x057d:
            if (r12 == 0) goto L_0x0584
            r6.writeStringWithSingleQuote(r11)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r14 = 0
            goto L_0x0588
        L_0x0584:
            r14 = 0
            r6.writeStringWithDoubleQuote(r11, r14, r14)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
        L_0x0588:
            goto L_0x05a3
        L_0x0589:
            r14 = 0
            r3 = r1
            java.lang.Enum r3 = (java.lang.Enum) r3     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            int r11 = r3.ordinal()     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r6.writeInt(r11)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            goto L_0x05a3
        L_0x0595:
            r14 = 0
            r6.writeNull()     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            goto L_0x05a3
        L_0x059a:
            r14 = 0
            r8.writeValue(r2, r1)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            goto L_0x05a3
        L_0x059f:
            r14 = 0
            r8.writeValue(r2, r1)     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
        L_0x05a3:
            r3 = 1
            r18 = r3
        L_0x05a6:
            int r4 = r26 + 1
            r3 = r47
            r8 = r23
            r13 = r24
            r7 = r25
            r1 = r27
            r10 = r28
            r14 = r35
            r11 = r37
            r12 = r38
            r15 = r39
            r5 = r40
            goto L_0x0185
        L_0x05c0:
            r0 = move-exception
            r5 = r47
        L_0x05c3:
            r8 = r48
            r3 = r0
            r1 = r23
            r7 = r25
            goto L_0x06ca
        L_0x05cc:
            r0 = move-exception
            r5 = r47
        L_0x05cf:
            r3 = r0
            r1 = r23
            r7 = r25
            goto L_0x06a2
        L_0x05d6:
            r0 = move-exception
            r5 = r47
            r8 = r48
            r3 = r0
            r1 = r23
            goto L_0x06ca
        L_0x05e0:
            r0 = move-exception
            r5 = r47
            r3 = r0
            r1 = r23
            goto L_0x06a2
        L_0x05e8:
            r27 = r1
            r26 = r4
            r40 = r5
            r25 = r7
            r28 = r10
            r37 = r11
            r38 = r12
            r24 = r13
            r35 = r14
            r39 = r15
            r14 = 0
            r17 = 44
            java.util.List<com.alibaba.fastjson.serializer.AfterFilter> r1 = r2.afterFilters     // Catch:{ Exception -> 0x0680, all -> 0x0675 }
            if (r1 == 0) goto L_0x0629
            if (r18 == 0) goto L_0x0607
            r14 = 44
        L_0x0607:
            r1 = r14
            java.util.List<com.alibaba.fastjson.serializer.AfterFilter> r3 = r2.afterFilters     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
        L_0x060e:
            boolean r4 = r3.hasNext()     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            if (r4 == 0) goto L_0x0626
            java.lang.Object r4 = r3.next()     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            com.alibaba.fastjson.serializer.AfterFilter r4 = (com.alibaba.fastjson.serializer.AfterFilter) r4     // Catch:{ Exception -> 0x05cc, all -> 0x05c0 }
            r5 = r47
            char r7 = r4.writeAfter(r2, r5, r1)     // Catch:{ Exception -> 0x0624, all -> 0x0622 }
            r1 = r7
            goto L_0x060e
        L_0x0622:
            r0 = move-exception
            goto L_0x05c3
        L_0x0624:
            r0 = move-exception
            goto L_0x05cf
        L_0x0626:
            r5 = r47
            goto L_0x062b
        L_0x0629:
            r5 = r47
        L_0x062b:
            r7 = r25
            int r1 = r7.length     // Catch:{ Exception -> 0x0673, all -> 0x0671 }
            if (r1 <= 0) goto L_0x064e
            int r1 = r6.features     // Catch:{ Exception -> 0x0648, all -> 0x0640 }
            com.alibaba.fastjson.serializer.SerializerFeature r3 = com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat     // Catch:{ Exception -> 0x0648, all -> 0x0640 }
            int r3 = r3.mask     // Catch:{ Exception -> 0x0648, all -> 0x0640 }
            r1 = r1 & r3
            if (r1 == 0) goto L_0x064e
            r46.decrementIdent()     // Catch:{ Exception -> 0x0648, all -> 0x0640 }
            r46.println()     // Catch:{ Exception -> 0x0648, all -> 0x0640 }
            goto L_0x064e
        L_0x0640:
            r0 = move-exception
            r8 = r48
            r3 = r0
            r1 = r23
            goto L_0x06ca
        L_0x0648:
            r0 = move-exception
            r3 = r0
            r1 = r23
            goto L_0x06a2
        L_0x064e:
            int r1 = r6.count     // Catch:{ Exception -> 0x0673, all -> 0x0671 }
            r3 = 1
            int r1 = r1 + r3
            char[] r3 = r6.buf     // Catch:{ Exception -> 0x0673, all -> 0x0671 }
            int r3 = r3.length     // Catch:{ Exception -> 0x0673, all -> 0x0671 }
            if (r1 <= r3) goto L_0x0663
            java.io.Writer r3 = r6.writer     // Catch:{ Exception -> 0x0648, all -> 0x0640 }
            if (r3 != 0) goto L_0x065f
            r6.expandCapacity(r1)     // Catch:{ Exception -> 0x0648, all -> 0x0640 }
            goto L_0x0663
        L_0x065f:
            r6.flush()     // Catch:{ Exception -> 0x0648, all -> 0x0640 }
            r1 = 1
        L_0x0663:
            char[] r3 = r6.buf     // Catch:{ Exception -> 0x0673, all -> 0x0671 }
            int r4 = r6.count     // Catch:{ Exception -> 0x0673, all -> 0x0671 }
            r3[r4] = r24     // Catch:{ Exception -> 0x0673, all -> 0x0671 }
            r6.count = r1     // Catch:{ Exception -> 0x0673, all -> 0x0671 }
            r1 = r23
            r2.context = r1
            return
        L_0x0671:
            r0 = move-exception
            goto L_0x068b
        L_0x0673:
            r0 = move-exception
            goto L_0x0693
        L_0x0675:
            r0 = move-exception
            r5 = r47
            r1 = r23
            r7 = r25
            r8 = r48
            r3 = r0
            goto L_0x06ca
        L_0x0680:
            r0 = move-exception
            r5 = r47
            r1 = r23
            r7 = r25
            r3 = r0
            goto L_0x06a2
        L_0x0689:
            r0 = move-exception
            r5 = r3
        L_0x068b:
            r1 = r23
            r8 = r48
            r3 = r0
            goto L_0x06ca
        L_0x0691:
            r0 = move-exception
            r5 = r3
        L_0x0693:
            r1 = r23
            r3 = r0
            goto L_0x06a2
        L_0x0697:
            r0 = move-exception
            r5 = r3
            r1 = r8
            r8 = r48
            r3 = r0
            goto L_0x06ca
        L_0x069e:
            r0 = move-exception
            r5 = r3
            r1 = r8
            r3 = r0
        L_0x06a2:
            java.lang.String r4 = "write javaBean error"
            r8 = r48
            if (r8 == 0) goto L_0x06bd
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x06c3 }
            r10.<init>()     // Catch:{ all -> 0x06c3 }
            r10.append(r4)     // Catch:{ all -> 0x06c3 }
            java.lang.String r11 = ", fieldName : "
            r10.append(r11)     // Catch:{ all -> 0x06c3 }
            r10.append(r8)     // Catch:{ all -> 0x06c3 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x06c3 }
            r4 = r10
        L_0x06bd:
            com.alibaba.fastjson.JSONException r10 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x06c3 }
            r10.<init>(r4, r3)     // Catch:{ all -> 0x06c3 }
            throw r10     // Catch:{ all -> 0x06c3 }
        L_0x06c3:
            r0 = move-exception
            r3 = r0
            goto L_0x06ca
        L_0x06c6:
            r0 = move-exception
            r8 = r48
            r3 = r0
        L_0x06ca:
            r2.context = r1
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.serializer.JavaBeanSerializer.write(com.alibaba.fastjson.serializer.JSONSerializer, java.lang.Object, java.lang.Object, java.lang.reflect.Type):void");
    }

    public Map<String, Object> getFieldValuesMap(Object object) throws Exception {
        Map<String, Object> map = new LinkedHashMap<>(this.sortedGetters.length);
        for (FieldSerializer getter : this.sortedGetters) {
            map.put(getter.fieldInfo.name, getter.getPropertyValue(object));
        }
        return map;
    }
}

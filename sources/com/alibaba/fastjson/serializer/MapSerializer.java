package com.alibaba.fastjson.serializer;

public final class MapSerializer implements ObjectSerializer {
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00ca, code lost:
        if ((com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue.mask & r3.features) == 0) goto L_0x00cd;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void write(com.alibaba.fastjson.serializer.JSONSerializer r19, java.lang.Object r20, java.lang.Object r21, java.lang.reflect.Type r22) throws java.io.IOException {
        /*
            r18 = this;
            r1 = r19
            r2 = r20
            com.alibaba.fastjson.serializer.SerializeWriter r3 = r1.out
            if (r2 != 0) goto L_0x000c
            r3.writeNull()
            return
        L_0x000c:
            r4 = r2
            java.util.Map r4 = (java.util.Map) r4
            java.lang.Class r5 = r4.getClass()
            java.lang.Class<com.alibaba.fastjson.JSONObject> r0 = com.alibaba.fastjson.JSONObject.class
            r7 = 0
            if (r5 == r0) goto L_0x0020
            java.lang.Class<java.util.HashMap> r0 = java.util.HashMap.class
            if (r5 == r0) goto L_0x0020
            java.lang.Class<java.util.LinkedHashMap> r0 = java.util.LinkedHashMap.class
            if (r5 != r0) goto L_0x002a
        L_0x0020:
            java.lang.String r0 = "@type"
            boolean r0 = r4.containsKey(r0)
            if (r0 == 0) goto L_0x002a
            r0 = 1
            goto L_0x002b
        L_0x002a:
            r0 = 0
        L_0x002b:
            r8 = r0
            int r0 = r3.features
            com.alibaba.fastjson.serializer.SerializerFeature r9 = com.alibaba.fastjson.serializer.SerializerFeature.SortField
            int r9 = r9.mask
            r0 = r0 & r9
            if (r0 == 0) goto L_0x0045
            boolean r0 = r4 instanceof java.util.SortedMap
            if (r0 != 0) goto L_0x0045
            boolean r0 = r4 instanceof java.util.LinkedHashMap
            if (r0 != 0) goto L_0x0045
            java.util.TreeMap r0 = new java.util.TreeMap     // Catch:{ Exception -> 0x0044 }
            r0.<init>(r4)     // Catch:{ Exception -> 0x0044 }
            r4 = r0
            goto L_0x0045
        L_0x0044:
            r0 = move-exception
        L_0x0045:
            java.util.IdentityHashMap<java.lang.Object, com.alibaba.fastjson.serializer.SerialContext> r0 = r1.references
            if (r0 == 0) goto L_0x0055
            java.util.IdentityHashMap<java.lang.Object, com.alibaba.fastjson.serializer.SerialContext> r0 = r1.references
            boolean r0 = r0.containsKey(r2)
            if (r0 == 0) goto L_0x0055
            r19.writeReference(r20)
            return
        L_0x0055:
            com.alibaba.fastjson.serializer.SerialContext r9 = r1.context
            r10 = r21
            r1.setContext(r9, r2, r10, r7)
            r0 = 123(0x7b, float:1.72E-43)
            r3.write((int) r0)     // Catch:{ all -> 0x0167 }
            r19.incrementIndent()     // Catch:{ all -> 0x0167 }
            r0 = 0
            r11 = 0
            r12 = 1
            int r13 = r3.features     // Catch:{ all -> 0x0167 }
            com.alibaba.fastjson.serializer.SerializerFeature r14 = com.alibaba.fastjson.serializer.SerializerFeature.WriteClassName     // Catch:{ all -> 0x0167 }
            int r14 = r14.mask     // Catch:{ all -> 0x0167 }
            r13 = r13 & r14
            if (r13 == 0) goto L_0x008b
            if (r8 != 0) goto L_0x008b
            com.alibaba.fastjson.serializer.SerializeConfig r13 = r1.config     // Catch:{ all -> 0x0086 }
            java.lang.String r13 = r13.typeKey     // Catch:{ all -> 0x0086 }
            r3.writeFieldName(r13, r7)     // Catch:{ all -> 0x0086 }
            java.lang.Class r7 = r20.getClass()     // Catch:{ all -> 0x0086 }
            java.lang.String r7 = r7.getName()     // Catch:{ all -> 0x0086 }
            r3.writeString(r7)     // Catch:{ all -> 0x0086 }
            r12 = 0
            goto L_0x008b
        L_0x0086:
            r0 = move-exception
            r17 = r5
            goto L_0x016a
        L_0x008b:
            java.util.Set r7 = r4.entrySet()     // Catch:{ all -> 0x0167 }
            java.util.Iterator r7 = r7.iterator()     // Catch:{ all -> 0x0167 }
        L_0x0093:
            boolean r13 = r7.hasNext()     // Catch:{ all -> 0x0167 }
            if (r13 == 0) goto L_0x0147
            java.lang.Object r13 = r7.next()     // Catch:{ all -> 0x0167 }
            java.util.Map$Entry r13 = (java.util.Map.Entry) r13     // Catch:{ all -> 0x0167 }
            java.lang.Object r14 = r13.getValue()     // Catch:{ all -> 0x0167 }
            java.lang.Object r15 = r13.getKey()     // Catch:{ all -> 0x0167 }
            boolean r16 = r1.applyName(r2, r15)     // Catch:{ all -> 0x0167 }
            if (r16 != 0) goto L_0x00ae
            goto L_0x00cd
        L_0x00ae:
            boolean r16 = r1.apply(r2, r15, r14)     // Catch:{ all -> 0x0167 }
            if (r16 != 0) goto L_0x00b5
            goto L_0x00cd
        L_0x00b5:
            java.lang.Object r16 = r1.processKey(r2, r15, r14)     // Catch:{ all -> 0x0167 }
            r15 = r16
            java.lang.Object r16 = com.alibaba.fastjson.serializer.JSONSerializer.processValue(r1, r2, r15, r14)     // Catch:{ all -> 0x0167 }
            r14 = r16
            if (r14 != 0) goto L_0x00d0
            int r6 = r3.features     // Catch:{ all -> 0x0086 }
            com.alibaba.fastjson.serializer.SerializerFeature r2 = com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue     // Catch:{ all -> 0x0086 }
            int r2 = r2.mask     // Catch:{ all -> 0x0086 }
            r2 = r2 & r6
            if (r2 != 0) goto L_0x00d0
        L_0x00cd:
            r2 = r20
            goto L_0x0093
        L_0x00d0:
            boolean r2 = r15 instanceof java.lang.String     // Catch:{ all -> 0x0167 }
            r6 = 44
            if (r2 == 0) goto L_0x00f1
            r2 = r15
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x0167 }
            if (r12 != 0) goto L_0x00de
            r3.write((int) r6)     // Catch:{ all -> 0x0086 }
        L_0x00de:
            int r6 = r3.features     // Catch:{ all -> 0x0167 }
            r17 = r5
            com.alibaba.fastjson.serializer.SerializerFeature r5 = com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat     // Catch:{ all -> 0x0145 }
            int r5 = r5.mask     // Catch:{ all -> 0x0145 }
            r5 = r5 & r6
            if (r5 == 0) goto L_0x00ec
            r19.println()     // Catch:{ all -> 0x0145 }
        L_0x00ec:
            r5 = 1
            r3.writeFieldName(r2, r5)     // Catch:{ all -> 0x0145 }
            goto L_0x011d
        L_0x00f1:
            r17 = r5
            r5 = 1
            if (r12 != 0) goto L_0x00f9
            r3.write((int) r6)     // Catch:{ all -> 0x0145 }
        L_0x00f9:
            int r2 = r3.features     // Catch:{ all -> 0x0145 }
            com.alibaba.fastjson.serializer.SerializerFeature r6 = com.alibaba.fastjson.serializer.SerializerFeature.BrowserCompatible     // Catch:{ all -> 0x0145 }
            int r6 = r6.mask     // Catch:{ all -> 0x0145 }
            r2 = r2 & r6
            if (r2 != 0) goto L_0x0110
            int r2 = r3.features     // Catch:{ all -> 0x0145 }
            com.alibaba.fastjson.serializer.SerializerFeature r6 = com.alibaba.fastjson.serializer.SerializerFeature.WriteNonStringKeyAsString     // Catch:{ all -> 0x0145 }
            int r6 = r6.mask     // Catch:{ all -> 0x0145 }
            r2 = r2 & r6
            if (r2 == 0) goto L_0x010c
            goto L_0x0110
        L_0x010c:
            r1.write((java.lang.Object) r15)     // Catch:{ all -> 0x0145 }
            goto L_0x0118
        L_0x0110:
            java.lang.String r2 = com.alibaba.fastjson.JSON.toJSONString(r15)     // Catch:{ all -> 0x0145 }
            r1.write((java.lang.String) r2)     // Catch:{ all -> 0x0145 }
        L_0x0118:
            r2 = 58
            r3.write((int) r2)     // Catch:{ all -> 0x0145 }
        L_0x011d:
            r12 = 0
            if (r14 != 0) goto L_0x0129
            r3.writeNull()     // Catch:{ all -> 0x0145 }
            r2 = r20
            r5 = r17
            goto L_0x0093
        L_0x0129:
            java.lang.Class r2 = r14.getClass()     // Catch:{ all -> 0x0145 }
            r6 = 0
            if (r2 != r0) goto L_0x0134
            r11.write(r1, r14, r15, r6)     // Catch:{ all -> 0x0145 }
            goto L_0x013f
        L_0x0134:
            r0 = r2
            com.alibaba.fastjson.serializer.SerializeConfig r5 = r1.config     // Catch:{ all -> 0x0145 }
            com.alibaba.fastjson.serializer.ObjectSerializer r5 = r5.get(r2)     // Catch:{ all -> 0x0145 }
            r5.write(r1, r14, r15, r6)     // Catch:{ all -> 0x0145 }
            r11 = r5
        L_0x013f:
            r2 = r20
            r5 = r17
            goto L_0x0093
        L_0x0145:
            r0 = move-exception
            goto L_0x016a
        L_0x0147:
            r17 = r5
            r1.context = r9
            r19.decrementIdent()
            int r0 = r3.features
            com.alibaba.fastjson.serializer.SerializerFeature r2 = com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat
            int r2 = r2.mask
            r0 = r0 & r2
            if (r0 == 0) goto L_0x0161
            int r0 = r4.size()
            if (r0 <= 0) goto L_0x0161
            r19.println()
        L_0x0161:
            r0 = 125(0x7d, float:1.75E-43)
            r3.write((int) r0)
            return
        L_0x0167:
            r0 = move-exception
            r17 = r5
        L_0x016a:
            r1.context = r9
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.serializer.MapSerializer.write(com.alibaba.fastjson.serializer.JSONSerializer, java.lang.Object, java.lang.Object, java.lang.reflect.Type):void");
    }
}

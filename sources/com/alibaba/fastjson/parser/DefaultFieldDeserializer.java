package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.FieldInfo;

public class DefaultFieldDeserializer extends FieldDeserializer {
    protected ObjectDeserializer fieldValueDeserilizer;

    public DefaultFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo, 2);
    }

    public ObjectDeserializer getFieldValueDeserilizer(ParserConfig config) {
        if (this.fieldValueDeserilizer == null) {
            this.fieldValueDeserilizer = config.getDeserializer(this.fieldInfo.fieldClass, this.fieldInfo.fieldType);
        }
        return this.fieldValueDeserilizer;
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0058  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void parseField(com.alibaba.fastjson.parser.DefaultJSONParser r5, java.lang.Object r6, java.lang.reflect.Type r7, java.util.Map<java.lang.String, java.lang.Object> r8) {
        /*
            r4 = this;
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r0 = r4.fieldValueDeserilizer
            if (r0 != 0) goto L_0x0014
            com.alibaba.fastjson.parser.ParserConfig r0 = r5.config
            com.alibaba.fastjson.util.FieldInfo r1 = r4.fieldInfo
            java.lang.Class<?> r1 = r1.fieldClass
            com.alibaba.fastjson.util.FieldInfo r2 = r4.fieldInfo
            java.lang.reflect.Type r2 = r2.fieldType
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r0 = r0.getDeserializer(r1, r2)
            r4.fieldValueDeserilizer = r0
        L_0x0014:
            boolean r0 = r7 instanceof java.lang.reflect.ParameterizedType
            if (r0 == 0) goto L_0x001c
            com.alibaba.fastjson.parser.ParseContext r0 = r5.contex
            r0.type = r7
        L_0x001c:
            com.alibaba.fastjson.util.FieldInfo r0 = r4.fieldInfo
            java.lang.String r0 = r0.format
            if (r0 == 0) goto L_0x0037
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r1 = r4.fieldValueDeserilizer
            boolean r2 = r1 instanceof com.alibaba.fastjson.serializer.DateCodec
            if (r2 == 0) goto L_0x0037
            com.alibaba.fastjson.serializer.DateCodec r1 = (com.alibaba.fastjson.serializer.DateCodec) r1
            com.alibaba.fastjson.util.FieldInfo r2 = r4.fieldInfo
            java.lang.reflect.Type r2 = r2.fieldType
            com.alibaba.fastjson.util.FieldInfo r3 = r4.fieldInfo
            java.lang.String r3 = r3.name
            java.lang.Object r1 = r1.deserialze(r5, r2, r3, r0)
            goto L_0x0045
        L_0x0037:
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r1 = r4.fieldValueDeserilizer
            com.alibaba.fastjson.util.FieldInfo r2 = r4.fieldInfo
            java.lang.reflect.Type r2 = r2.fieldType
            com.alibaba.fastjson.util.FieldInfo r3 = r4.fieldInfo
            java.lang.String r3 = r3.name
            java.lang.Object r1 = r1.deserialze(r5, r2, r3)
        L_0x0045:
            int r2 = r5.resolveStatus
            r3 = 1
            if (r2 != r3) goto L_0x0058
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r2 = r5.getLastResolveTask()
            r2.fieldDeserializer = r4
            com.alibaba.fastjson.parser.ParseContext r3 = r5.contex
            r2.ownerContext = r3
            r3 = 0
            r5.resolveStatus = r3
            goto L_0x007c
        L_0x0058:
            if (r6 != 0) goto L_0x0062
            com.alibaba.fastjson.util.FieldInfo r2 = r4.fieldInfo
            java.lang.String r2 = r2.name
            r8.put(r2, r1)
            goto L_0x007c
        L_0x0062:
            if (r1 != 0) goto L_0x0079
            com.alibaba.fastjson.util.FieldInfo r2 = r4.fieldInfo
            java.lang.Class<?> r2 = r2.fieldClass
            java.lang.Class r3 = java.lang.Byte.TYPE
            if (r2 == r3) goto L_0x0078
            java.lang.Class r3 = java.lang.Short.TYPE
            if (r2 == r3) goto L_0x0078
            java.lang.Class r3 = java.lang.Float.TYPE
            if (r2 == r3) goto L_0x0078
            java.lang.Class r3 = java.lang.Double.TYPE
            if (r2 != r3) goto L_0x0079
        L_0x0078:
            return
        L_0x0079:
            r4.setValue((java.lang.Object) r6, (java.lang.Object) r1)
        L_0x007c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.DefaultFieldDeserializer.parseField(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.Object, java.lang.reflect.Type, java.util.Map):void");
    }
}

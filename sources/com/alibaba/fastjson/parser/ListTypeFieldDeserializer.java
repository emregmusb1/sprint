package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.FieldInfo;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ListTypeFieldDeserializer extends FieldDeserializer {
    private final boolean array;
    private ObjectDeserializer deserializer;
    private final Type itemType;

    public ListTypeFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo, 14);
        Type fieldType = fieldInfo.fieldType;
        Class<?> fieldClass = fieldInfo.fieldClass;
        if (fieldType instanceof ParameterizedType) {
            this.itemType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
            this.array = false;
        } else if (fieldClass.isArray()) {
            this.itemType = fieldClass.getComponentType();
            this.array = true;
        } else {
            this.itemType = Object.class;
            this.array = false;
        }
    }

    public void parseField(DefaultJSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {
        List list;
        Object fieldValue;
        if (parser.lexer.token == 8) {
            setValue(object, (Object) null);
            parser.lexer.nextToken();
            return;
        }
        JSONArray jsonArray = null;
        if (this.array) {
            List jSONArray = new JSONArray();
            jsonArray = jSONArray;
            jsonArray.setComponentType(this.itemType);
            list = jSONArray;
        } else {
            list = new ArrayList();
        }
        ParseContext context = parser.contex;
        parser.setContext(context, object, this.fieldInfo.name);
        parseArray(parser, objectType, list);
        parser.setContext(context);
        if (this.array) {
            fieldValue = list.toArray((Object[]) Array.newInstance((Class) this.itemType, list.size()));
            jsonArray.setRelatedArray(fieldValue);
        } else {
            fieldValue = list;
        }
        if (object == null) {
            fieldValues.put(this.fieldInfo.name, fieldValue);
        } else {
            setValue(object, fieldValue);
        }
    }

    /* JADX WARNING: type inference failed for: r13v6, types: [java.lang.reflect.Type] */
    /* JADX WARNING: type inference failed for: r11v21, types: [java.lang.reflect.Type] */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void parseArray(com.alibaba.fastjson.parser.DefaultJSONParser r19, java.lang.reflect.Type r20, java.util.Collection r21) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            r3 = r21
            java.lang.reflect.Type r4 = r0.itemType
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r5 = r0.deserializer
            boolean r6 = r2 instanceof java.lang.reflect.ParameterizedType
            if (r6 == 0) goto L_0x00d4
            boolean r6 = r4 instanceof java.lang.reflect.TypeVariable
            r8 = -1
            if (r6 == 0) goto L_0x0067
            r6 = r4
            java.lang.reflect.TypeVariable r6 = (java.lang.reflect.TypeVariable) r6
            r9 = r2
            java.lang.reflect.ParameterizedType r9 = (java.lang.reflect.ParameterizedType) r9
            r10 = 0
            java.lang.reflect.Type r11 = r9.getRawType()
            boolean r11 = r11 instanceof java.lang.Class
            if (r11 == 0) goto L_0x002b
            java.lang.reflect.Type r11 = r9.getRawType()
            r10 = r11
            java.lang.Class r10 = (java.lang.Class) r10
        L_0x002b:
            r11 = -1
            if (r10 == 0) goto L_0x004f
            r12 = 0
            java.lang.reflect.TypeVariable[] r13 = r10.getTypeParameters()
            int r13 = r13.length
        L_0x0034:
            if (r12 >= r13) goto L_0x004f
            java.lang.reflect.TypeVariable[] r14 = r10.getTypeParameters()
            r14 = r14[r12]
            java.lang.String r15 = r14.getName()
            java.lang.String r7 = r6.getName()
            boolean r7 = r15.equals(r7)
            if (r7 == 0) goto L_0x004c
            r11 = r12
            goto L_0x004f
        L_0x004c:
            int r12 = r12 + 1
            goto L_0x0034
        L_0x004f:
            if (r11 == r8) goto L_0x0065
            java.lang.reflect.Type[] r7 = r9.getActualTypeArguments()
            r4 = r7[r11]
            java.lang.reflect.Type r7 = r0.itemType
            boolean r7 = r4.equals(r7)
            if (r7 != 0) goto L_0x0065
            com.alibaba.fastjson.parser.ParserConfig r7 = r1.config
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r5 = r7.getDeserializer(r4)
        L_0x0065:
            goto L_0x00d4
        L_0x0067:
            boolean r6 = r4 instanceof java.lang.reflect.ParameterizedType
            if (r6 == 0) goto L_0x0065
            r6 = r4
            java.lang.reflect.ParameterizedType r6 = (java.lang.reflect.ParameterizedType) r6
            java.lang.reflect.Type[] r7 = r6.getActualTypeArguments()
            int r9 = r7.length
            r10 = 1
            if (r9 != r10) goto L_0x00d4
            r9 = 0
            r10 = r7[r9]
            boolean r10 = r10 instanceof java.lang.reflect.TypeVariable
            if (r10 == 0) goto L_0x00d4
            r10 = r7[r9]
            java.lang.reflect.TypeVariable r10 = (java.lang.reflect.TypeVariable) r10
            r11 = r2
            java.lang.reflect.ParameterizedType r11 = (java.lang.reflect.ParameterizedType) r11
            r12 = 0
            java.lang.reflect.Type r13 = r11.getRawType()
            boolean r13 = r13 instanceof java.lang.Class
            if (r13 == 0) goto L_0x0094
            java.lang.reflect.Type r13 = r11.getRawType()
            r12 = r13
            java.lang.Class r12 = (java.lang.Class) r12
        L_0x0094:
            r13 = -1
            if (r12 == 0) goto L_0x00ba
            r14 = 0
            java.lang.reflect.TypeVariable[] r15 = r12.getTypeParameters()
            int r15 = r15.length
        L_0x009d:
            if (r14 >= r15) goto L_0x00ba
            java.lang.reflect.TypeVariable[] r17 = r12.getTypeParameters()
            r17 = r17[r14]
            java.lang.String r9 = r17.getName()
            java.lang.String r8 = r10.getName()
            boolean r8 = r9.equals(r8)
            if (r8 == 0) goto L_0x00b5
            r13 = r14
            goto L_0x00ba
        L_0x00b5:
            int r14 = r14 + 1
            r8 = -1
            r9 = 0
            goto L_0x009d
        L_0x00ba:
            r8 = -1
            if (r13 == r8) goto L_0x00d4
            java.lang.reflect.Type[] r8 = r11.getActualTypeArguments()
            r8 = r8[r13]
            r9 = 0
            r7[r9] = r8
            com.alibaba.fastjson.util.ParameterizedTypeImpl r8 = new com.alibaba.fastjson.util.ParameterizedTypeImpl
            java.lang.reflect.Type r9 = r6.getOwnerType()
            java.lang.reflect.Type r14 = r6.getRawType()
            r8.<init>(r7, r9, r14)
            r4 = r8
        L_0x00d4:
            com.alibaba.fastjson.parser.JSONLexer r6 = r1.lexer
            int r7 = r6.token
            r8 = 14
            if (r7 == r8) goto L_0x010f
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "exepct '[', but "
            r7.append(r8)
            int r8 = r6.token
            java.lang.String r8 = com.alibaba.fastjson.parser.JSONToken.name(r8)
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            if (r2 == 0) goto L_0x0109
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r7)
            java.lang.String r9 = ", type : "
            r8.append(r9)
            r8.append(r2)
            java.lang.String r7 = r8.toString()
        L_0x0109:
            com.alibaba.fastjson.JSONException r8 = new com.alibaba.fastjson.JSONException
            r8.<init>(r7)
            throw r8
        L_0x010f:
            if (r5 != 0) goto L_0x011a
            com.alibaba.fastjson.parser.ParserConfig r7 = r1.config
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r7 = r7.getDeserializer(r4)
            r0.deserializer = r7
            r5 = r7
        L_0x011a:
            char r7 = r6.ch
            r9 = 15
            r10 = 12
            r11 = 34
            r12 = 123(0x7b, float:1.72E-43)
            r13 = 91
            if (r7 != r13) goto L_0x0142
            int r15 = r6.bp
            r16 = 1
            int r15 = r15 + 1
            r6.bp = r15
            int r14 = r6.len
            if (r15 < r14) goto L_0x0137
            r14 = 26
            goto L_0x013d
        L_0x0137:
            java.lang.String r14 = r6.text
            char r14 = r14.charAt(r15)
        L_0x013d:
            r6.ch = r14
            r6.token = r8
            goto L_0x0181
        L_0x0142:
            if (r7 != r12) goto L_0x015c
            int r14 = r6.bp
            r15 = 1
            int r14 = r14 + r15
            r6.bp = r14
            int r15 = r6.len
            if (r14 < r15) goto L_0x0151
            r15 = 26
            goto L_0x0157
        L_0x0151:
            java.lang.String r15 = r6.text
            char r15 = r15.charAt(r14)
        L_0x0157:
            r6.ch = r15
            r6.token = r10
            goto L_0x0181
        L_0x015c:
            if (r7 != r11) goto L_0x0162
            r6.scanString()
            goto L_0x0181
        L_0x0162:
            r14 = 93
            if (r7 != r14) goto L_0x017e
            int r14 = r6.bp
            r15 = 1
            int r14 = r14 + r15
            r6.bp = r14
            int r15 = r6.len
            if (r14 < r15) goto L_0x0173
            r15 = 26
            goto L_0x0179
        L_0x0173:
            java.lang.String r15 = r6.text
            char r15 = r15.charAt(r14)
        L_0x0179:
            r6.ch = r15
            r6.token = r9
            goto L_0x0181
        L_0x017e:
            r6.nextToken()
        L_0x0181:
            r14 = 0
        L_0x0182:
            int r15 = r6.token
            r11 = 16
            if (r15 != r11) goto L_0x0199
            int r15 = r6.features
            com.alibaba.fastjson.parser.Feature r10 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas
            int r10 = r10.mask
            r10 = r10 & r15
            if (r10 == 0) goto L_0x0199
            r6.nextToken()
            r10 = 12
            r11 = 34
            goto L_0x0182
        L_0x0199:
            int r10 = r6.token
            if (r10 != r9) goto L_0x01c0
            char r8 = r6.ch
            r9 = 44
            if (r8 != r9) goto L_0x01bc
            int r8 = r6.bp
            r9 = 1
            int r8 = r8 + r9
            r6.bp = r8
            int r9 = r6.len
            if (r8 < r9) goto L_0x01b1
            r14 = 26
            goto L_0x01b7
        L_0x01b1:
            java.lang.String r9 = r6.text
            char r14 = r9.charAt(r8)
        L_0x01b7:
            r6.ch = r14
            r6.token = r11
            goto L_0x01bf
        L_0x01bc:
            r6.nextToken()
        L_0x01bf:
            return
        L_0x01c0:
            java.lang.Integer r10 = java.lang.Integer.valueOf(r14)
            java.lang.Object r10 = r5.deserialze(r1, r4, r10)
            r3.add(r10)
            int r15 = r1.resolveStatus
            r9 = 1
            if (r15 != r9) goto L_0x01d3
            r1.checkListResolve(r3)
        L_0x01d3:
            int r9 = r6.token
            if (r9 != r11) goto L_0x0225
            char r7 = r6.ch
            if (r7 != r13) goto L_0x01f8
            int r9 = r6.bp
            r11 = 1
            int r9 = r9 + r11
            r6.bp = r9
            int r11 = r6.len
            if (r9 < r11) goto L_0x01e8
            r11 = 26
            goto L_0x01ee
        L_0x01e8:
            java.lang.String r11 = r6.text
            char r11 = r11.charAt(r9)
        L_0x01ee:
            r6.ch = r11
            r6.token = r8
            r9 = 34
            r11 = 1
            r15 = 12
            goto L_0x022a
        L_0x01f8:
            if (r7 != r12) goto L_0x0216
            int r9 = r6.bp
            r11 = 1
            int r9 = r9 + r11
            r6.bp = r9
            int r15 = r6.len
            if (r9 < r15) goto L_0x0207
            r15 = 26
            goto L_0x020d
        L_0x0207:
            java.lang.String r15 = r6.text
            char r15 = r15.charAt(r9)
        L_0x020d:
            r6.ch = r15
            r15 = 12
            r6.token = r15
            r9 = 34
            goto L_0x022a
        L_0x0216:
            r11 = 1
            r15 = 12
            r9 = 34
            if (r7 != r9) goto L_0x0221
            r6.scanString()
            goto L_0x022a
        L_0x0221:
            r6.nextToken()
            goto L_0x022a
        L_0x0225:
            r9 = 34
            r11 = 1
            r15 = 12
        L_0x022a:
            int r14 = r14 + 1
            r9 = 15
            r10 = 12
            r11 = 34
            goto L_0x0182
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.ListTypeFieldDeserializer.parseArray(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.util.Collection):void");
    }
}

package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

public class CollectionCodec implements ObjectSerializer, ObjectDeserializer {
    public static final CollectionCodec instance = new CollectionCodec();

    private CollectionCodec() {
    }

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.out;
        if (object != null) {
            Type elementType = null;
            if ((out.features & SerializerFeature.WriteClassName.mask) != 0 && (fieldType instanceof ParameterizedType)) {
                elementType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
            }
            Collection<?> collection = (Collection) object;
            SerialContext context = serializer.context;
            serializer.setContext(context, object, fieldName, 0);
            if ((out.features & SerializerFeature.WriteClassName.mask) != 0) {
                if (HashSet.class == collection.getClass()) {
                    out.append((CharSequence) "Set");
                } else if (TreeSet.class == collection.getClass()) {
                    out.append((CharSequence) "TreeSet");
                }
            }
            int i = 0;
            try {
                out.write(91);
                for (Object item : collection) {
                    int i2 = i + 1;
                    if (i != 0) {
                        out.write(44);
                    }
                    if (item == null) {
                        out.writeNull();
                    } else {
                        Class<?> clazz = item.getClass();
                        if (clazz == Integer.class) {
                            out.writeInt(((Integer) item).intValue());
                        } else if (clazz == Long.class) {
                            out.writeLong(((Long) item).longValue());
                            if ((out.features & SerializerFeature.WriteClassName.mask) != 0) {
                                out.write(76);
                            }
                        } else {
                            serializer.config.get(clazz).write(serializer, item, Integer.valueOf(i2 - 1), elementType);
                        }
                    }
                    i = i2;
                }
                out.write(93);
            } finally {
                serializer.context = context;
            }
        } else if ((out.features & SerializerFeature.WriteNullListAsEmpty.mask) != 0) {
            out.write("[]");
        } else {
            out.writeNull();
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v28, resolved type: java.lang.reflect.Type[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <T> T deserialze(com.alibaba.fastjson.parser.DefaultJSONParser r8, java.lang.reflect.Type r9, java.lang.Object r10) {
        /*
            r7 = this;
            com.alibaba.fastjson.parser.JSONLexer r0 = r8.lexer
            int r0 = r0.token()
            r1 = 8
            if (r0 != r1) goto L_0x0013
            com.alibaba.fastjson.parser.JSONLexer r0 = r8.lexer
            r1 = 16
            r0.nextToken(r1)
            r0 = 0
            return r0
        L_0x0013:
            java.lang.Class<com.alibaba.fastjson.JSONArray> r0 = com.alibaba.fastjson.JSONArray.class
            if (r9 != r0) goto L_0x0020
            com.alibaba.fastjson.JSONArray r0 = new com.alibaba.fastjson.JSONArray
            r0.<init>()
            r8.parseArray((java.util.Collection) r0)
            return r0
        L_0x0020:
            r0 = r9
        L_0x0021:
            boolean r1 = r0 instanceof java.lang.Class
            if (r1 == 0) goto L_0x00f7
            r1 = r0
            java.lang.Class r1 = (java.lang.Class) r1
            java.lang.Class<java.util.AbstractCollection> r0 = java.util.AbstractCollection.class
            r2 = 0
            if (r1 == r0) goto L_0x00b0
            java.lang.Class<java.util.Collection> r0 = java.util.Collection.class
            if (r1 != r0) goto L_0x0034
            goto L_0x00b0
        L_0x0034:
            java.lang.Class<java.util.HashSet> r0 = java.util.HashSet.class
            boolean r0 = r1.isAssignableFrom(r0)
            if (r0 == 0) goto L_0x0043
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            goto L_0x00b5
        L_0x0043:
            java.lang.Class<java.util.LinkedHashSet> r0 = java.util.LinkedHashSet.class
            boolean r0 = r1.isAssignableFrom(r0)
            if (r0 == 0) goto L_0x0051
            java.util.LinkedHashSet r0 = new java.util.LinkedHashSet
            r0.<init>()
            goto L_0x00b5
        L_0x0051:
            java.lang.Class<java.util.TreeSet> r0 = java.util.TreeSet.class
            boolean r0 = r1.isAssignableFrom(r0)
            if (r0 == 0) goto L_0x005f
            java.util.TreeSet r0 = new java.util.TreeSet
            r0.<init>()
            goto L_0x00b5
        L_0x005f:
            java.lang.Class<java.util.ArrayList> r0 = java.util.ArrayList.class
            boolean r0 = r1.isAssignableFrom(r0)
            if (r0 == 0) goto L_0x006d
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            goto L_0x00b5
        L_0x006d:
            java.lang.Class<java.util.EnumSet> r0 = java.util.EnumSet.class
            boolean r0 = r1.isAssignableFrom(r0)
            if (r0 == 0) goto L_0x008d
            boolean r0 = r9 instanceof java.lang.reflect.ParameterizedType
            if (r0 == 0) goto L_0x0083
            r0 = r9
            java.lang.reflect.ParameterizedType r0 = (java.lang.reflect.ParameterizedType) r0
            java.lang.reflect.Type[] r0 = r0.getActualTypeArguments()
            r0 = r0[r2]
            goto L_0x0085
        L_0x0083:
            java.lang.Class<java.lang.Object> r0 = java.lang.Object.class
        L_0x0085:
            r3 = r0
            java.lang.Class r3 = (java.lang.Class) r3
            java.util.EnumSet r0 = java.util.EnumSet.noneOf(r3)
            goto L_0x00b5
        L_0x008d:
            java.lang.Object r0 = r1.newInstance()     // Catch:{ Exception -> 0x0094 }
            java.util.Collection r0 = (java.util.Collection) r0     // Catch:{ Exception -> 0x0094 }
            goto L_0x00b5
        L_0x0094:
            r0 = move-exception
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "create instane error, class "
            r3.append(r4)
            java.lang.String r4 = r1.getName()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            throw r2
        L_0x00b0:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
        L_0x00b5:
            r3 = 0
            boolean r4 = r9 instanceof java.lang.reflect.ParameterizedType
            if (r4 == 0) goto L_0x00c4
            r4 = r9
            java.lang.reflect.ParameterizedType r4 = (java.lang.reflect.ParameterizedType) r4
            java.lang.reflect.Type[] r4 = r4.getActualTypeArguments()
            r2 = r4[r2]
            goto L_0x00f3
        L_0x00c4:
            r4 = 0
            boolean r5 = r9 instanceof java.lang.Class
            if (r5 == 0) goto L_0x00ee
            r5 = r9
            java.lang.Class r5 = (java.lang.Class) r5
            r4 = r5
            java.lang.String r5 = r5.getName()
            java.lang.String r6 = "java."
            boolean r5 = r5.startsWith(r6)
            if (r5 != 0) goto L_0x00ec
            java.lang.reflect.Type r5 = r4.getGenericSuperclass()
            boolean r6 = r5 instanceof java.lang.reflect.ParameterizedType
            if (r6 == 0) goto L_0x00ec
            r6 = r5
            java.lang.reflect.ParameterizedType r6 = (java.lang.reflect.ParameterizedType) r6
            java.lang.reflect.Type[] r6 = r6.getActualTypeArguments()
            r3 = r6[r2]
            r2 = r3
            goto L_0x00ef
        L_0x00ec:
            r2 = r3
            goto L_0x00ef
        L_0x00ee:
            r2 = r3
        L_0x00ef:
            if (r2 != 0) goto L_0x00f3
            java.lang.Class<java.lang.Object> r2 = java.lang.Object.class
        L_0x00f3:
            r8.parseArray(r2, r0, r10)
            return r0
        L_0x00f7:
            boolean r1 = r0 instanceof java.lang.reflect.ParameterizedType
            if (r1 == 0) goto L_0x0104
            r1 = r0
            java.lang.reflect.ParameterizedType r1 = (java.lang.reflect.ParameterizedType) r1
            java.lang.reflect.Type r0 = r1.getRawType()
            goto L_0x0021
        L_0x0104:
            com.alibaba.fastjson.JSONException r1 = new com.alibaba.fastjson.JSONException
            java.lang.String r2 = "TODO"
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.serializer.CollectionCodec.deserialze(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.lang.Object):java.lang.Object");
    }
}

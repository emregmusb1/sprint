package com.alibaba.fastjson.serializer;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.List;

public final class ListSerializer implements ObjectSerializer {
    /* JADX INFO: finally extract failed */
    public final void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        Type elementType;
        JSONSerializer jSONSerializer = serializer;
        Object obj = object;
        Object obj2 = fieldName;
        Type type = fieldType;
        SerializeWriter out = jSONSerializer.out;
        boolean writeClassName = (out.features & SerializerFeature.WriteClassName.mask) != 0;
        if (!writeClassName || !(type instanceof ParameterizedType)) {
            elementType = null;
        } else {
            elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        if (obj != null) {
            List<?> list = (List) obj;
            int size = list.size();
            if (size == 0) {
                out.append((CharSequence) "[]");
                return;
            }
            SerialContext context = jSONSerializer.context;
            if ((out.features & SerializerFeature.DisableCircularReferenceDetect.mask) == 0) {
                jSONSerializer.context = new SerialContext(context, obj, obj2, 0);
                if (jSONSerializer.references == null) {
                    jSONSerializer.references = new IdentityHashMap<>();
                }
                jSONSerializer.references.put(obj, context);
            }
            try {
                int i = 44;
                if ((out.features & SerializerFeature.PrettyFormat.mask) != 0) {
                    out.write(91);
                    serializer.incrementIndent();
                    int i2 = 0;
                    while (i2 < size) {
                        Object item = list.get(i2);
                        if (i2 != 0) {
                            out.write(i);
                        }
                        serializer.println();
                        if (item == null) {
                            jSONSerializer.out.writeNull();
                        } else if (jSONSerializer.references == null || !jSONSerializer.references.containsKey(item)) {
                            ObjectSerializer itemSerializer = jSONSerializer.config.get(item.getClass());
                            jSONSerializer.context = new SerialContext(context, obj, obj2, 0);
                            itemSerializer.write(jSONSerializer, item, Integer.valueOf(i2), elementType);
                        } else {
                            jSONSerializer.writeReference(item);
                        }
                        i2++;
                        i = 44;
                    }
                    serializer.decrementIdent();
                    serializer.println();
                    out.write(93);
                    jSONSerializer.context = context;
                    return;
                }
                int newcount = out.count + 1;
                if (newcount > out.buf.length) {
                    if (out.writer == null) {
                        out.expandCapacity(newcount);
                    } else {
                        out.flush();
                        newcount = 1;
                    }
                }
                out.buf[out.count] = '[';
                out.count = newcount;
                int i3 = 0;
                while (i3 < list.size()) {
                    Object item2 = list.get(i3);
                    if (i3 != 0) {
                        int newcount2 = out.count + 1;
                        if (newcount2 > out.buf.length) {
                            if (out.writer == null) {
                                out.expandCapacity(newcount2);
                            } else {
                                out.flush();
                                newcount2 = 1;
                            }
                        }
                        out.buf[out.count] = ',';
                        out.count = newcount2;
                    }
                    if (item2 == null) {
                        out.append((CharSequence) "null");
                    } else {
                        Class<?> clazz = item2.getClass();
                        if (clazz == Integer.class) {
                            out.writeInt(((Integer) item2).intValue());
                        } else if (clazz == Long.class) {
                            long val = ((Long) item2).longValue();
                            if (writeClassName) {
                                out.writeLong(val);
                                out.write(76);
                            } else {
                                out.writeLong(val);
                            }
                        } else if (clazz == String.class) {
                            String itemStr = (String) item2;
                            if ((out.features & SerializerFeature.UseSingleQuotes.mask) != 0) {
                                out.writeStringWithSingleQuote(itemStr);
                            } else {
                                out.writeStringWithDoubleQuote(itemStr, 0, true);
                            }
                        } else {
                            if ((out.features & SerializerFeature.DisableCircularReferenceDetect.mask) == 0) {
                                jSONSerializer.context = new SerialContext(context, obj, obj2, 0);
                            }
                            if (jSONSerializer.references == null || !jSONSerializer.references.containsKey(item2)) {
                                jSONSerializer.config.get(item2.getClass()).write(jSONSerializer, item2, Integer.valueOf(i3), elementType);
                            } else {
                                jSONSerializer.writeReference(item2);
                            }
                        }
                    }
                    i3++;
                    Type type2 = fieldType;
                }
                int newcount3 = out.count + 1;
                if (newcount3 > out.buf.length) {
                    if (out.writer == null) {
                        out.expandCapacity(newcount3);
                    } else {
                        out.flush();
                        newcount3 = 1;
                    }
                }
                out.buf[out.count] = ']';
                out.count = newcount3;
                jSONSerializer.context = context;
            } catch (Throwable th) {
                jSONSerializer.context = context;
                throw th;
            }
        } else if ((out.features & SerializerFeature.WriteNullListAsEmpty.mask) != 0) {
            out.write("[]");
        } else {
            out.writeNull();
        }
    }
}

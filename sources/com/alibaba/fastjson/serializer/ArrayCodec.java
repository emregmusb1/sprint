package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

public final class ArrayCodec implements ObjectSerializer, ObjectDeserializer {
    public static final ArrayCodec instance = new ArrayCodec();

    private ArrayCodec() {
    }

    public final void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        JSONSerializer jSONSerializer = serializer;
        Object obj = object;
        SerializeWriter out = jSONSerializer.out;
        Object[] array = (Object[]) obj;
        if (obj != null) {
            int size = array.length;
            int end = size - 1;
            if (end == -1) {
                out.append((CharSequence) "[]");
                return;
            }
            SerialContext context = jSONSerializer.context;
            jSONSerializer.setContext(context, obj, fieldName, 0);
            Class<?> preClazz = null;
            ObjectSerializer preWriter = null;
            try {
                out.write(91);
                if ((out.features & SerializerFeature.PrettyFormat.mask) != 0) {
                    serializer.incrementIndent();
                    serializer.println();
                    for (int i = 0; i < size; i++) {
                        if (i != 0) {
                            out.write(44);
                            serializer.println();
                        }
                        jSONSerializer.write(array[i]);
                    }
                    serializer.decrementIdent();
                    serializer.println();
                    out.write(93);
                    return;
                }
                for (int i2 = 0; i2 < end; i2++) {
                    Object item = array[i2];
                    if (item == null) {
                        out.append((CharSequence) "null,");
                    } else {
                        if (jSONSerializer.references == null || !jSONSerializer.references.containsKey(item)) {
                            Class<?> clazz = item.getClass();
                            if (clazz == preClazz) {
                                preWriter.write(jSONSerializer, item, (Object) null, (Type) null);
                            } else {
                                preClazz = clazz;
                                preWriter = jSONSerializer.config.get(clazz);
                                preWriter.write(jSONSerializer, item, (Object) null, (Type) null);
                            }
                        } else {
                            jSONSerializer.writeReference(item);
                        }
                        out.write(44);
                    }
                }
                Object item2 = array[end];
                if (item2 == null) {
                    out.append((CharSequence) "null]");
                } else {
                    if (jSONSerializer.references == null || !jSONSerializer.references.containsKey(item2)) {
                        jSONSerializer.writeWithFieldName(item2, Integer.valueOf(end));
                    } else {
                        jSONSerializer.writeReference(item2);
                    }
                    out.write(93);
                }
                jSONSerializer.context = context;
            } finally {
                jSONSerializer.context = context;
            }
        } else if ((out.features & SerializerFeature.WriteNullListAsEmpty.mask) != 0) {
            out.write("[]");
        } else {
            out.writeNull();
        }
    }

    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        JSONLexer lexer = parser.lexer;
        int token = lexer.token();
        if (token == 8) {
            lexer.nextToken(16);
            return null;
        } else if (type == char[].class) {
            if (token == 4) {
                String val = lexer.stringVal();
                lexer.nextToken(16);
                return val.toCharArray();
            } else if (token != 2) {
                return JSON.toJSONString(parser.parse()).toCharArray();
            } else {
                Number val2 = lexer.integerValue();
                lexer.nextToken(16);
                return val2.toString().toCharArray();
            }
        } else if (token == 4) {
            Object bytesValue = lexer.bytesValue();
            lexer.nextToken(16);
            return bytesValue;
        } else {
            Class componentClass = ((Class) type).getComponentType();
            JSONArray array = new JSONArray();
            parser.parseArray(componentClass, array, fieldName);
            return toObjectArray(parser, componentClass, array);
        }
    }

    private <T> T toObjectArray(DefaultJSONParser parser, Class<?> componentType, JSONArray array) {
        Object element;
        if (array == null) {
            return null;
        }
        int size = array.size();
        Object objArray = Array.newInstance(componentType, size);
        for (int i = 0; i < size; i++) {
            Object element2 = array.get(i);
            if (element2 == array) {
                Array.set(objArray, i, objArray);
            } else {
                if (!componentType.isArray()) {
                    element = TypeUtils.cast(element2, componentType, parser.config);
                } else if (componentType.isInstance(element2)) {
                    element = element2;
                } else {
                    element = toObjectArray(parser, componentType, (JSONArray) element2);
                }
                Array.set(objArray, i, element);
            }
        }
        array.setRelatedArray(objArray);
        array.setComponentType(componentType);
        return objArray;
    }
}

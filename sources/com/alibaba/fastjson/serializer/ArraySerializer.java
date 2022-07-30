package com.alibaba.fastjson.serializer;

import java.io.IOException;
import java.lang.reflect.Type;

final class ArraySerializer implements ObjectSerializer {
    private final ObjectSerializer compObjectSerializer;
    private final Class<?> componentType;

    ArraySerializer(Class<?> componentType2, ObjectSerializer compObjectSerializer2) {
        this.componentType = componentType2;
        this.compObjectSerializer = compObjectSerializer2;
    }

    public final void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            if ((out.features & SerializerFeature.WriteNullListAsEmpty.mask) != 0) {
                out.write("[]");
            } else {
                out.writeNull();
            }
        } else if (object instanceof boolean[]) {
            boolean[] array = (boolean[]) object;
            out.write(91);
            for (int i = 0; i < array.length; i++) {
                if (i != 0) {
                    out.write(44);
                }
                out.write(array[i]);
            }
            out.write(93);
        } else if (object instanceof byte[]) {
            out.writeByteArray((byte[]) object);
        } else if (object instanceof char[]) {
            out.writeString(new String((char[]) object));
        } else if (object instanceof double[]) {
            double[] array2 = (double[]) object;
            int end = array2.length - 1;
            if (end == -1) {
                out.append((CharSequence) "[]");
                return;
            }
            out.write(91);
            for (int i2 = 0; i2 < end; i2++) {
                double item = array2[i2];
                if (Double.isNaN(item)) {
                    out.writeNull();
                } else {
                    out.append((CharSequence) Double.toString(item));
                }
                out.write(44);
            }
            double item2 = array2[end];
            if (Double.isNaN(item2)) {
                out.writeNull();
            } else {
                out.append((CharSequence) Double.toString(item2));
            }
            out.write(93);
        } else if (object instanceof float[]) {
            float[] array3 = (float[]) object;
            int end2 = array3.length - 1;
            if (end2 == -1) {
                out.append((CharSequence) "[]");
                return;
            }
            out.write(91);
            for (int i3 = 0; i3 < end2; i3++) {
                float item3 = array3[i3];
                if (Float.isNaN(item3)) {
                    out.writeNull();
                } else {
                    out.append((CharSequence) Float.toString(item3));
                }
                out.write(44);
            }
            float item4 = array3[end2];
            if (Float.isNaN(item4)) {
                out.writeNull();
            } else {
                out.append((CharSequence) Float.toString(item4));
            }
            out.write(93);
        } else if (object instanceof int[]) {
            int[] array4 = (int[]) object;
            out.write(91);
            for (int i4 = 0; i4 < array4.length; i4++) {
                if (i4 != 0) {
                    out.write(44);
                }
                out.writeInt(array4[i4]);
            }
            out.write(93);
        } else if (object instanceof long[]) {
            long[] array5 = (long[]) object;
            out.write(91);
            for (int i5 = 0; i5 < array5.length; i5++) {
                if (i5 != 0) {
                    out.write(44);
                }
                out.writeLong(array5[i5]);
            }
            out.write(93);
        } else if (object instanceof short[]) {
            short[] array6 = (short[]) object;
            out.write(91);
            for (int i6 = 0; i6 < array6.length; i6++) {
                if (i6 != 0) {
                    out.write(44);
                }
                out.writeInt(array6[i6]);
            }
            out.write(93);
        } else {
            Object[] array7 = (Object[]) object;
            int size = array7.length;
            SerialContext context = serializer.context;
            serializer.setContext(context, object, fieldName, 0);
            try {
                out.write(91);
                for (int i7 = 0; i7 < size; i7++) {
                    if (i7 != 0) {
                        out.write(44);
                    }
                    Object item5 = array7[i7];
                    if (item5 == null) {
                        out.append((CharSequence) "null");
                    } else if (item5.getClass() == this.componentType) {
                        this.compObjectSerializer.write(serializer, item5, Integer.valueOf(i7), (Type) null);
                    } else {
                        serializer.config.get(item5.getClass()).write(serializer, item5, Integer.valueOf(i7), (Type) null);
                    }
                }
                out.write(93);
            } finally {
                serializer.context = context;
            }
        }
    }
}

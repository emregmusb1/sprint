package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.ToNumberPolicy;
import com.google.gson.ToNumberStrategy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ObjectTypeAdapter extends TypeAdapter<Object> {
    private static final TypeAdapterFactory DOUBLE_FACTORY = newFactory(ToNumberPolicy.DOUBLE);
    private final Gson gson;
    private final ToNumberStrategy toNumberStrategy;

    private ObjectTypeAdapter(Gson gson2, ToNumberStrategy toNumberStrategy2) {
        this.gson = gson2;
        this.toNumberStrategy = toNumberStrategy2;
    }

    private static TypeAdapterFactory newFactory(final ToNumberStrategy toNumberStrategy2) {
        return new TypeAdapterFactory() {
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                if (type.getRawType() == Object.class) {
                    return new ObjectTypeAdapter(gson, ToNumberStrategy.this);
                }
                return null;
            }
        };
    }

    public static TypeAdapterFactory getFactory(ToNumberStrategy toNumberStrategy2) {
        if (toNumberStrategy2 == ToNumberPolicy.DOUBLE) {
            return DOUBLE_FACTORY;
        }
        return newFactory(toNumberStrategy2);
    }

    public Object read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<>();
                in.beginArray();
                while (in.hasNext()) {
                    list.add(read(in));
                }
                in.endArray();
                return list;
            case BEGIN_OBJECT:
                Map<String, Object> map = new LinkedTreeMap<>();
                in.beginObject();
                while (in.hasNext()) {
                    map.put(in.nextName(), read(in));
                }
                in.endObject();
                return map;
            case STRING:
                return in.nextString();
            case NUMBER:
                return this.toNumberStrategy.readNumber(in);
            case BOOLEAN:
                return Boolean.valueOf(in.nextBoolean());
            case NULL:
                in.nextNull();
                return null;
            default:
                throw new IllegalStateException();
        }
    }

    public void write(JsonWriter out, Object value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        TypeAdapter<Object> typeAdapter = this.gson.getAdapter(value.getClass());
        if (typeAdapter instanceof ObjectTypeAdapter) {
            out.beginObject();
            out.endObject();
            return;
        }
        typeAdapter.write(out, value);
    }
}

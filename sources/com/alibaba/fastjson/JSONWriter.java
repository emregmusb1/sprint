package com.alibaba.fastjson;

import androidx.core.view.PointerIconCompat;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

public class JSONWriter implements Closeable, Flushable {
    private JSONStreamContext context;
    private JSONSerializer serializer = new JSONSerializer(this.writer);
    private SerializeWriter writer;

    public JSONWriter(Writer out) {
        this.writer = new SerializeWriter(out);
    }

    public void config(SerializerFeature feature, boolean state) {
        this.writer.config(feature, state);
    }

    public void startObject() {
        if (this.context != null) {
            beginStructure();
        }
        this.context = new JSONStreamContext(this.context, PointerIconCompat.TYPE_CONTEXT_MENU);
        this.writer.write(123);
    }

    public void endObject() {
        this.writer.write(125);
        endStructure();
    }

    public void writeKey(String key) {
        writeObject(key);
    }

    public void writeValue(Object object) {
        writeObject(object);
    }

    public void writeObject(String object) {
        beforeWrite();
        this.serializer.write(object);
        afterWriter();
    }

    public void writeObject(Object object) {
        beforeWrite();
        this.serializer.write(object);
        afterWriter();
    }

    public void startArray() {
        if (this.context != null) {
            beginStructure();
        }
        this.context = new JSONStreamContext(this.context, PointerIconCompat.TYPE_WAIT);
        this.writer.write(91);
    }

    private void beginStructure() {
        int state = this.context.state;
        switch (this.context.state) {
            case PointerIconCompat.TYPE_CONTEXT_MENU:
            case PointerIconCompat.TYPE_WAIT:
                return;
            case PointerIconCompat.TYPE_HAND:
                this.writer.write(58);
                return;
            case 1005:
                this.writer.write(44);
                return;
            default:
                throw new JSONException("illegal state : " + state);
        }
    }

    public void endArray() {
        this.writer.write(93);
        endStructure();
    }

    private void endStructure() {
        this.context = this.context.parent;
        JSONStreamContext jSONStreamContext = this.context;
        if (jSONStreamContext != null) {
            int newState = -1;
            switch (jSONStreamContext.state) {
                case PointerIconCompat.TYPE_CONTEXT_MENU:
                    newState = PointerIconCompat.TYPE_HAND;
                    break;
                case PointerIconCompat.TYPE_HAND:
                    newState = PointerIconCompat.TYPE_HELP;
                    break;
                case PointerIconCompat.TYPE_WAIT:
                    newState = 1005;
                    break;
            }
            if (newState != -1) {
                this.context.state = newState;
            }
        }
    }

    private void beforeWrite() {
        JSONStreamContext jSONStreamContext = this.context;
        if (jSONStreamContext != null) {
            switch (jSONStreamContext.state) {
                case PointerIconCompat.TYPE_HAND:
                    this.writer.write(58);
                    return;
                case PointerIconCompat.TYPE_HELP:
                    this.writer.write(44);
                    return;
                case 1005:
                    this.writer.write(44);
                    return;
                default:
                    return;
            }
        }
    }

    private void afterWriter() {
        JSONStreamContext jSONStreamContext = this.context;
        if (jSONStreamContext != null) {
            int newState = -1;
            switch (jSONStreamContext.state) {
                case PointerIconCompat.TYPE_CONTEXT_MENU:
                case PointerIconCompat.TYPE_HELP:
                    newState = PointerIconCompat.TYPE_HAND;
                    break;
                case PointerIconCompat.TYPE_HAND:
                    newState = PointerIconCompat.TYPE_HELP;
                    break;
                case PointerIconCompat.TYPE_WAIT:
                    newState = 1005;
                    break;
            }
            if (newState != -1) {
                this.context.state = newState;
            }
        }
    }

    public void flush() throws IOException {
        this.writer.flush();
    }

    public void close() throws IOException {
        this.writer.close();
    }
}

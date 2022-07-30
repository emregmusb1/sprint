package com.alibaba.fastjson;

import androidx.core.view.PointerIconCompat;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

public class JSONReader implements Closeable {
    private JSONStreamContext context;
    private final DefaultJSONParser parser;
    private Reader reader;

    public JSONReader(Reader reader2) {
        this(new JSONLexer(readAll(reader2)));
        this.reader = reader2;
    }

    static String readAll(Reader reader2) {
        StringBuilder buf = new StringBuilder();
        try {
            char[] chars = new char[2048];
            while (true) {
                int len = reader2.read(chars, 0, chars.length);
                if (len < 0) {
                    return buf.toString();
                }
                buf.append(chars, 0, len);
            }
        } catch (Exception ex) {
            throw new JSONException("read string from reader error", ex);
        }
    }

    public JSONReader(JSONLexer lexer) {
        this(new DefaultJSONParser(lexer));
    }

    public JSONReader(DefaultJSONParser parser2) {
        this.parser = parser2;
    }

    public void config(Feature feature, boolean state) {
        this.parser.config(feature, state);
    }

    public void startObject() {
        if (this.context == null) {
            this.context = new JSONStreamContext((JSONStreamContext) null, PointerIconCompat.TYPE_CONTEXT_MENU);
        } else {
            startStructure();
            this.context = new JSONStreamContext(this.context, PointerIconCompat.TYPE_CONTEXT_MENU);
        }
        this.parser.accept(12);
    }

    public void endObject() {
        this.parser.accept(13);
        endStructure();
    }

    public void startArray() {
        if (this.context == null) {
            this.context = new JSONStreamContext((JSONStreamContext) null, PointerIconCompat.TYPE_WAIT);
        } else {
            startStructure();
            this.context = new JSONStreamContext(this.context, PointerIconCompat.TYPE_WAIT);
        }
        this.parser.accept(14);
    }

    public void endArray() {
        this.parser.accept(15);
        endStructure();
    }

    private void startStructure() {
        switch (this.context.state) {
            case PointerIconCompat.TYPE_CONTEXT_MENU:
            case PointerIconCompat.TYPE_WAIT:
                return;
            case PointerIconCompat.TYPE_HAND:
                this.parser.accept(17);
                return;
            case PointerIconCompat.TYPE_HELP:
            case 1005:
                this.parser.accept(16);
                return;
            default:
                throw new JSONException("illegal state : " + this.context.state);
        }
    }

    private void endStructure() {
        this.context = this.context.parent;
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

    public boolean hasNext() {
        if (this.context != null) {
            int token = this.parser.lexer.token();
            int state = this.context.state;
            switch (state) {
                case PointerIconCompat.TYPE_CONTEXT_MENU:
                case PointerIconCompat.TYPE_HELP:
                    if (token != 13) {
                        return true;
                    }
                    return false;
                case PointerIconCompat.TYPE_WAIT:
                case 1005:
                    if (token != 15) {
                        return true;
                    }
                    return false;
                default:
                    throw new JSONException("illegal state : " + state);
            }
        } else {
            throw new JSONException("context is null");
        }
    }

    public int peek() {
        return this.parser.lexer.token();
    }

    public void close() {
        this.parser.lexer.close();
        Reader reader2 = this.reader;
        if (reader2 != null) {
            try {
                reader2.close();
            } catch (IOException ex) {
                throw new JSONException("closed reader error", ex);
            }
        }
    }

    public Integer readInteger() {
        Object object;
        if (this.context == null) {
            object = this.parser.parse();
        } else {
            readBefore();
            object = this.parser.parse();
            readAfter();
        }
        return TypeUtils.castToInt(object);
    }

    public Long readLong() {
        Object object;
        if (this.context == null) {
            object = this.parser.parse();
        } else {
            readBefore();
            object = this.parser.parse();
            readAfter();
        }
        return TypeUtils.castToLong(object);
    }

    public String readString() {
        Object object;
        if (this.context == null) {
            object = this.parser.parse();
        } else {
            readBefore();
            object = this.parser.parse();
            readAfter();
        }
        return TypeUtils.castToString(object);
    }

    public <T> T readObject(TypeReference<T> typeRef) {
        return readObject(typeRef.type);
    }

    public <T> T readObject(Type type) {
        if (this.context == null) {
            return this.parser.parseObject(type);
        }
        readBefore();
        T object = this.parser.parseObject(type);
        readAfter();
        return object;
    }

    public <T> T readObject(Class<T> type) {
        if (this.context == null) {
            return this.parser.parseObject(type);
        }
        readBefore();
        T object = this.parser.parseObject(type);
        readAfter();
        return object;
    }

    public void readObject(Object object) {
        if (this.context == null) {
            this.parser.parseObject(object);
            return;
        }
        readBefore();
        this.parser.parseObject(object);
        readAfter();
    }

    public Object readObject() {
        if (this.context == null) {
            return this.parser.parse();
        }
        readBefore();
        Object object = this.parser.parse();
        readAfter();
        return object;
    }

    public Object readObject(Map object) {
        if (this.context == null) {
            return this.parser.parseObject(object);
        }
        readBefore();
        Object value = this.parser.parseObject(object);
        readAfter();
        return value;
    }

    private void readBefore() {
        int state = this.context.state;
        switch (state) {
            case PointerIconCompat.TYPE_CONTEXT_MENU:
            case PointerIconCompat.TYPE_WAIT:
                return;
            case PointerIconCompat.TYPE_HAND:
                this.parser.accept(17);
                return;
            case PointerIconCompat.TYPE_HELP:
            case 1005:
                this.parser.accept(16);
                return;
            default:
                throw new JSONException("illegal state : " + state);
        }
    }

    private void readAfter() {
        int state = this.context.state;
        int newStat = -1;
        switch (state) {
            case PointerIconCompat.TYPE_CONTEXT_MENU:
                newStat = PointerIconCompat.TYPE_HAND;
                break;
            case PointerIconCompat.TYPE_HAND:
                newStat = PointerIconCompat.TYPE_HELP;
                break;
            case PointerIconCompat.TYPE_HELP:
                newStat = PointerIconCompat.TYPE_HAND;
                break;
            case PointerIconCompat.TYPE_WAIT:
                newStat = 1005;
                break;
            case 1005:
                break;
            default:
                throw new JSONException("illegal state : " + state);
        }
        if (newStat != -1) {
            this.context.state = newStat;
        }
    }
}

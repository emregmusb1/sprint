package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class JSONSerializer {
    protected List<AfterFilter> afterFilters;
    protected List<BeforeFilter> beforeFilters;
    public final SerializeConfig config;
    protected SerialContext context;
    private DateFormat dateFormat;
    private String dateFormatPattern;
    private int indentCount;
    public Locale locale;
    protected List<NameFilter> nameFilters;
    public final SerializeWriter out;
    protected List<PropertyFilter> propertyFilters;
    protected List<PropertyPreFilter> propertyPreFilters;
    protected IdentityHashMap<Object, SerialContext> references;
    public TimeZone timeZone;
    protected List<ValueFilter> valueFilters;

    public JSONSerializer() {
        this(new SerializeWriter((Writer) null, JSON.DEFAULT_GENERATE_FEATURE, SerializerFeature.EMPTY), SerializeConfig.globalInstance);
    }

    public JSONSerializer(SerializeWriter out2) {
        this(out2, SerializeConfig.globalInstance);
    }

    public JSONSerializer(SerializeConfig config2) {
        this(new SerializeWriter((Writer) null, JSON.DEFAULT_GENERATE_FEATURE, SerializerFeature.EMPTY), config2);
    }

    public JSONSerializer(SerializeWriter out2, SerializeConfig config2) {
        this.beforeFilters = null;
        this.afterFilters = null;
        this.propertyFilters = null;
        this.valueFilters = null;
        this.nameFilters = null;
        this.propertyPreFilters = null;
        this.indentCount = 0;
        this.references = null;
        this.timeZone = JSON.defaultTimeZone;
        this.locale = JSON.defaultLocale;
        this.out = out2;
        this.config = config2;
        this.timeZone = JSON.defaultTimeZone;
    }

    public String getDateFormatPattern() {
        DateFormat dateFormat2 = this.dateFormat;
        if (dateFormat2 instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) dateFormat2).toPattern();
        }
        return this.dateFormatPattern;
    }

    public DateFormat getDateFormat() {
        String str;
        if (this.dateFormat == null && (str = this.dateFormatPattern) != null) {
            this.dateFormat = new SimpleDateFormat(str, this.locale);
            this.dateFormat.setTimeZone(this.timeZone);
        }
        return this.dateFormat;
    }

    public void setDateFormat(DateFormat dateFormat2) {
        this.dateFormat = dateFormat2;
        if (this.dateFormatPattern != null) {
            this.dateFormatPattern = null;
        }
    }

    public void setDateFormat(String dateFormat2) {
        this.dateFormatPattern = dateFormat2;
        if (this.dateFormat != null) {
            this.dateFormat = null;
        }
    }

    public void setContext(SerialContext parent, Object object, Object fieldName, int features) {
        if ((this.out.features & SerializerFeature.DisableCircularReferenceDetect.mask) == 0) {
            this.context = new SerialContext(parent, object, fieldName, features);
            if (this.references == null) {
                this.references = new IdentityHashMap<>();
            }
            this.references.put(object, this.context);
        }
    }

    public void writeReference(Object object) {
        SerialContext context2 = this.context;
        if (object == context2.object) {
            this.out.write("{\"$ref\":\"@\"}");
            return;
        }
        SerialContext parentContext = context2.parent;
        if (parentContext == null || object != parentContext.object) {
            SerialContext rootContext = context2;
            while (rootContext.parent != null) {
                rootContext = rootContext.parent;
            }
            if (object == rootContext.object) {
                this.out.write("{\"$ref\":\"$\"}");
                return;
            }
            String path = this.references.get(object).toString();
            this.out.write("{\"$ref\":\"");
            this.out.write(path);
            this.out.write("\"}");
            return;
        }
        this.out.write("{\"$ref\":\"..\"}");
    }

    public List<ValueFilter> getValueFilters() {
        if (this.valueFilters == null) {
            this.valueFilters = new ArrayList();
        }
        return this.valueFilters;
    }

    public void incrementIndent() {
        this.indentCount++;
    }

    public void decrementIdent() {
        this.indentCount--;
    }

    public void println() {
        this.out.write(10);
        for (int i = 0; i < this.indentCount; i++) {
            this.out.write(9);
        }
    }

    public List<BeforeFilter> getBeforeFilters() {
        if (this.beforeFilters == null) {
            this.beforeFilters = new ArrayList();
        }
        return this.beforeFilters;
    }

    public List<AfterFilter> getAfterFilters() {
        if (this.afterFilters == null) {
            this.afterFilters = new ArrayList();
        }
        return this.afterFilters;
    }

    public List<NameFilter> getNameFilters() {
        if (this.nameFilters == null) {
            this.nameFilters = new ArrayList();
        }
        return this.nameFilters;
    }

    public List<PropertyPreFilter> getPropertyPreFilters() {
        if (this.propertyPreFilters == null) {
            this.propertyPreFilters = new ArrayList();
        }
        return this.propertyPreFilters;
    }

    public List<PropertyFilter> getPropertyFilters() {
        if (this.propertyFilters == null) {
            this.propertyFilters = new ArrayList();
        }
        return this.propertyFilters;
    }

    public String toString() {
        return this.out.toString();
    }

    public void config(SerializerFeature feature, boolean state) {
        this.out.config(feature, state);
    }

    public static final void write(Writer out2, Object object) {
        SerializeWriter writer = new SerializeWriter((Writer) null, JSON.DEFAULT_GENERATE_FEATURE, SerializerFeature.EMPTY);
        try {
            new JSONSerializer(writer, SerializeConfig.globalInstance).write(object);
            writer.writeTo(out2);
            writer.close();
        } catch (IOException ex) {
            throw new JSONException(ex.getMessage(), ex);
        } catch (Throwable th) {
            writer.close();
            throw th;
        }
    }

    public static final void write(SerializeWriter out2, Object object) {
        new JSONSerializer(out2, SerializeConfig.globalInstance).write(object);
    }

    public final void write(Object object) {
        if (object == null) {
            this.out.writeNull();
            return;
        }
        try {
            this.config.get(object.getClass()).write(this, object, (Object) null, (Type) null);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    public final void writeWithFieldName(Object object, Object fieldName) {
        writeWithFieldName(object, fieldName, (Type) null, 0);
    }

    /* access modifiers changed from: protected */
    public final void writeKeyValue(char seperator, String key, Object value) {
        if (seperator != 0) {
            this.out.write((int) seperator);
        }
        this.out.writeFieldName(key, true);
        write(value);
    }

    public final void writeWithFieldName(Object object, Object fieldName, Type fieldType, int features) {
        if (object == null) {
            try {
                this.out.writeNull();
            } catch (IOException e) {
                throw new JSONException(e.getMessage(), e);
            }
        } else {
            this.config.get(object.getClass()).write(this, object, fieldName, fieldType);
        }
    }

    public final void writeWithFormat(Object object, String format) {
        if (object instanceof Date) {
            DateFormat dateFormat2 = getDateFormat();
            if (dateFormat2 == null) {
                dateFormat2 = new SimpleDateFormat(format, this.locale);
                dateFormat2.setTimeZone(this.timeZone);
            }
            this.out.writeString(dateFormat2.format((Date) object));
            return;
        }
        write(object);
    }

    public final void write(String value) {
        if (value == null) {
            if ((this.out.features & SerializerFeature.WriteNullStringAsEmpty.mask) != 0) {
                this.out.writeString("");
            } else {
                this.out.writeNull();
            }
        } else if ((this.out.features & SerializerFeature.UseSingleQuotes.mask) != 0) {
            this.out.writeStringWithSingleQuote(value);
        } else {
            this.out.writeStringWithDoubleQuote(value, 0, true);
        }
    }

    public void close() {
        this.out.close();
    }

    public static Object processValue(JSONSerializer serializer, Object object, Object key, Object propertyValue) {
        List<ValueFilter> valueFilters2 = serializer.valueFilters;
        if (valueFilters2 != null) {
            if (key != null && !(key instanceof String)) {
                key = JSON.toJSONString(key);
            }
            for (ValueFilter valueFilter : valueFilters2) {
                propertyValue = valueFilter.process(object, (String) key, propertyValue);
            }
        }
        return propertyValue;
    }

    public Object processKey(Object object, Object key, Object propertyValue) {
        List<NameFilter> nameFilters2 = this.nameFilters;
        if (nameFilters2 != null) {
            if (key != null && !(key instanceof String)) {
                key = JSON.toJSONString(key);
            }
            for (NameFilter nameFilter : nameFilters2) {
                key = nameFilter.process(object, (String) key, propertyValue);
            }
        }
        return key;
    }

    public boolean applyName(Object object, Object key) {
        List<PropertyPreFilter> filters = this.propertyPreFilters;
        if (filters == null) {
            return true;
        }
        for (PropertyPreFilter filter : filters) {
            if (key != null && !(key instanceof String)) {
                key = JSON.toJSONString(key);
            }
            if (!filter.apply(this, object, (String) key)) {
                return false;
            }
        }
        return true;
    }

    public boolean apply(Object object, Object key, Object propertyValue) {
        List<PropertyFilter> propertyFilters2 = this.propertyFilters;
        if (propertyFilters2 == null) {
            return true;
        }
        if (key != null && !(key instanceof String)) {
            key = JSON.toJSONString(key);
        }
        for (PropertyFilter propertyFilter : propertyFilters2) {
            if (!propertyFilter.apply(object, (String) key, propertyValue)) {
                return false;
            }
        }
        return true;
    }

    public SerializeWriter getWriter() {
        return this.out;
    }

    public SerialContext getContext() {
        return this.context;
    }
}

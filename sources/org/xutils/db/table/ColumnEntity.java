package org.xutils.db.table;

import android.database.Cursor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.xutils.common.util.LogUtil;
import org.xutils.db.annotation.Column;
import org.xutils.db.converter.ColumnConverter;
import org.xutils.db.converter.ColumnConverterFactory;
import org.xutils.db.sqlite.ColumnDbType;

public final class ColumnEntity {
    protected final ColumnConverter columnConverter;
    protected final Field columnField;
    protected final Method getMethod;
    private final boolean isAutoId;
    private final boolean isId;
    protected final String name;
    private final String property;
    protected final Method setMethod;

    ColumnEntity(Class<?> entityType, Field field, Column column) {
        field.setAccessible(true);
        this.columnField = field;
        this.name = column.name();
        this.property = column.property();
        this.isId = column.isId();
        Class<?> fieldType = field.getType();
        this.isAutoId = this.isId && column.autoGen() && ColumnUtils.isAutoIdType(fieldType);
        this.columnConverter = ColumnConverterFactory.getColumnConverter(fieldType);
        this.getMethod = ColumnUtils.findGetMethod(entityType, field);
        Method method = this.getMethod;
        if (method != null && !method.isAccessible()) {
            this.getMethod.setAccessible(true);
        }
        this.setMethod = ColumnUtils.findSetMethod(entityType, field);
        Method method2 = this.setMethod;
        if (method2 != null && !method2.isAccessible()) {
            this.setMethod.setAccessible(true);
        }
    }

    public void setValueFromCursor(Object entity, Cursor cursor, int index) {
        Object value = this.columnConverter.getFieldValue(cursor, index);
        if (value != null) {
            Method method = this.setMethod;
            if (method != null) {
                try {
                    method.invoke(entity, new Object[]{value});
                } catch (Throwable e) {
                    LogUtil.e(e.getMessage(), e);
                }
            } else {
                try {
                    this.columnField.set(entity, value);
                } catch (Throwable e2) {
                    LogUtil.e(e2.getMessage(), e2);
                }
            }
        }
    }

    public Object getColumnValue(Object entity) {
        Object fieldValue = getFieldValue(entity);
        if (!this.isAutoId || (!fieldValue.equals(0L) && !fieldValue.equals(0))) {
            return this.columnConverter.fieldValue2DbValue(fieldValue);
        }
        return null;
    }

    public void setAutoIdValue(Object entity, long value) {
        Object idValue = Long.valueOf(value);
        if (ColumnUtils.isInteger(this.columnField.getType())) {
            idValue = Integer.valueOf((int) value);
        }
        Method method = this.setMethod;
        if (method != null) {
            try {
                method.invoke(entity, new Object[]{idValue});
            } catch (Throwable e) {
                LogUtil.e(e.getMessage(), e);
            }
        } else {
            try {
                this.columnField.set(entity, idValue);
            } catch (Throwable e2) {
                LogUtil.e(e2.getMessage(), e2);
            }
        }
    }

    public Object getFieldValue(Object entity) {
        if (entity == null) {
            return null;
        }
        Method method = this.getMethod;
        if (method != null) {
            try {
                return method.invoke(entity, new Object[0]);
            } catch (Throwable e) {
                LogUtil.e(e.getMessage(), e);
                return null;
            }
        } else {
            try {
                return this.columnField.get(entity);
            } catch (Throwable e2) {
                LogUtil.e(e2.getMessage(), e2);
                return null;
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public String getProperty() {
        return this.property;
    }

    public boolean isId() {
        return this.isId;
    }

    public boolean isAutoId() {
        return this.isAutoId;
    }

    public Field getColumnField() {
        return this.columnField;
    }

    public ColumnConverter getColumnConverter() {
        return this.columnConverter;
    }

    public ColumnDbType getColumnDbType() {
        return this.columnConverter.getColumnDbType();
    }

    public String toString() {
        return this.name;
    }
}

package org.xutils.db.converter;

import android.database.Cursor;
import org.xutils.db.sqlite.ColumnDbType;

public class FloatColumnConverter implements ColumnConverter<Float> {
    public Float getFieldValue(Cursor cursor, int index) {
        if (cursor.isNull(index)) {
            return null;
        }
        return Float.valueOf(cursor.getFloat(index));
    }

    public Object fieldValue2DbValue(Float fieldValue) {
        return fieldValue;
    }

    public ColumnDbType getColumnDbType() {
        return ColumnDbType.REAL;
    }
}

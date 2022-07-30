package org.xutils.db.converter;

import android.database.Cursor;
import org.xutils.db.sqlite.ColumnDbType;

public class DoubleColumnConverter implements ColumnConverter<Double> {
    public Double getFieldValue(Cursor cursor, int index) {
        if (cursor.isNull(index)) {
            return null;
        }
        return Double.valueOf(cursor.getDouble(index));
    }

    public Object fieldValue2DbValue(Double fieldValue) {
        return fieldValue;
    }

    public ColumnDbType getColumnDbType() {
        return ColumnDbType.REAL;
    }
}

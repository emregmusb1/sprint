package org.xutils.db.converter;

import android.database.Cursor;
import org.xutils.db.sqlite.ColumnDbType;

public class IntegerColumnConverter implements ColumnConverter<Integer> {
    public Integer getFieldValue(Cursor cursor, int index) {
        if (cursor.isNull(index)) {
            return null;
        }
        return Integer.valueOf(cursor.getInt(index));
    }

    public Object fieldValue2DbValue(Integer fieldValue) {
        return fieldValue;
    }

    public ColumnDbType getColumnDbType() {
        return ColumnDbType.INTEGER;
    }
}

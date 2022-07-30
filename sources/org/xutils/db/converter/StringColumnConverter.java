package org.xutils.db.converter;

import android.database.Cursor;
import org.xutils.db.sqlite.ColumnDbType;

public class StringColumnConverter implements ColumnConverter<String> {
    public String getFieldValue(Cursor cursor, int index) {
        if (cursor.isNull(index)) {
            return null;
        }
        return cursor.getString(index);
    }

    public Object fieldValue2DbValue(String fieldValue) {
        return fieldValue;
    }

    public ColumnDbType getColumnDbType() {
        return ColumnDbType.TEXT;
    }
}

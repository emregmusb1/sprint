package org.xutils.db.converter;

import android.database.Cursor;
import java.util.Date;
import org.xutils.db.sqlite.ColumnDbType;

public class DateColumnConverter implements ColumnConverter<Date> {
    public Date getFieldValue(Cursor cursor, int index) {
        if (cursor.isNull(index)) {
            return null;
        }
        return new Date(cursor.getLong(index));
    }

    public Object fieldValue2DbValue(Date fieldValue) {
        if (fieldValue == null) {
            return null;
        }
        return Long.valueOf(fieldValue.getTime());
    }

    public ColumnDbType getColumnDbType() {
        return ColumnDbType.INTEGER;
    }
}

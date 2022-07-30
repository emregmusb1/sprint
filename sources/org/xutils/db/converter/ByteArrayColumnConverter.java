package org.xutils.db.converter;

import android.database.Cursor;
import org.xutils.db.sqlite.ColumnDbType;

public class ByteArrayColumnConverter implements ColumnConverter<byte[]> {
    public byte[] getFieldValue(Cursor cursor, int index) {
        if (cursor.isNull(index)) {
            return null;
        }
        return cursor.getBlob(index);
    }

    public Object fieldValue2DbValue(byte[] fieldValue) {
        return fieldValue;
    }

    public ColumnDbType getColumnDbType() {
        return ColumnDbType.BLOB;
    }
}

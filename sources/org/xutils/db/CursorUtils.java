package org.xutils.db;

import android.database.Cursor;
import java.util.HashMap;
import org.xutils.db.table.ColumnEntity;
import org.xutils.db.table.DbModel;
import org.xutils.db.table.TableEntity;

final class CursorUtils {
    CursorUtils() {
    }

    public static <T> T getEntity(TableEntity<T> table, Cursor cursor) throws Throwable {
        T entity = table.createEntity();
        HashMap<String, ColumnEntity> columnMap = table.getColumnMap();
        int columnCount = cursor.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            ColumnEntity column = columnMap.get(cursor.getColumnName(i));
            if (column != null) {
                column.setValueFromCursor(entity, cursor, i);
            }
        }
        return entity;
    }

    public static DbModel getDbModel(Cursor cursor) {
        DbModel result = new DbModel();
        int columnCount = cursor.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            result.add(cursor.getColumnName(i), cursor.getString(i));
        }
        return result;
    }
}

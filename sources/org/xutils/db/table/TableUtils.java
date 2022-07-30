package org.xutils.db.table;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.xutils.common.util.LogUtil;
import org.xutils.db.annotation.Column;
import org.xutils.db.converter.ColumnConverterFactory;

final class TableUtils {
    private TableUtils() {
    }

    static LinkedHashMap<String, ColumnEntity> findColumnMap(Class<?> entityType) {
        LinkedHashMap<String, ColumnEntity> columnMap = new LinkedHashMap<>();
        addColumns2Map(entityType, columnMap);
        return columnMap;
    }

    private static void addColumns2Map(Class<?> entityType, HashMap<String, ColumnEntity> columnMap) {
        if (!Object.class.equals(entityType)) {
            try {
                for (Field field : entityType.getDeclaredFields()) {
                    int modify = field.getModifiers();
                    if (!Modifier.isStatic(modify)) {
                        if (!Modifier.isTransient(modify)) {
                            Column columnAnn = (Column) field.getAnnotation(Column.class);
                            if (columnAnn != null && ColumnConverterFactory.isSupportColumnConverter(field.getType())) {
                                ColumnEntity column = new ColumnEntity(entityType, field, columnAnn);
                                if (!columnMap.containsKey(column.getName())) {
                                    columnMap.put(column.getName(), column);
                                }
                            }
                        }
                    }
                }
                addColumns2Map(entityType.getSuperclass(), columnMap);
            } catch (Throwable e) {
                LogUtil.e(e.getMessage(), e);
            }
        }
    }
}

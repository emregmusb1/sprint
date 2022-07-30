package org.xutils.db.sqlite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.xutils.common.util.KeyValue;
import org.xutils.db.table.ColumnEntity;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;

public final class SqlInfoBuilder {
    private static final ConcurrentHashMap<TableEntity<?>, String> INSERT_SQL_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<TableEntity<?>, String> REPLACE_SQL_CACHE = new ConcurrentHashMap<>();

    private SqlInfoBuilder() {
    }

    public static SqlInfo buildInsertSqlInfo(TableEntity<?> table, Object entity) throws DbException {
        List<KeyValue> keyValueList = entity2KeyValueList(table, entity);
        if (keyValueList.size() == 0) {
            return null;
        }
        SqlInfo result = new SqlInfo();
        String sql = INSERT_SQL_CACHE.get(table);
        if (sql == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO ");
            builder.append("\"");
            builder.append(table.getName());
            builder.append("\"");
            builder.append(" (");
            for (KeyValue kv : keyValueList) {
                builder.append("\"");
                builder.append(kv.key);
                builder.append("\"");
                builder.append(',');
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(") VALUES (");
            int length = keyValueList.size();
            for (int i = 0; i < length; i++) {
                builder.append("?,");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(")");
            String sql2 = builder.toString();
            result.setSql(sql2);
            result.addBindArgs(keyValueList);
            INSERT_SQL_CACHE.put(table, sql2);
        } else {
            result.setSql(sql);
            result.addBindArgs(keyValueList);
        }
        return result;
    }

    public static SqlInfo buildReplaceSqlInfo(TableEntity<?> table, Object entity) throws DbException {
        List<KeyValue> keyValueList = entity2KeyValueList(table, entity);
        if (keyValueList.size() == 0) {
            return null;
        }
        SqlInfo result = new SqlInfo();
        String sql = REPLACE_SQL_CACHE.get(table);
        if (sql == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("REPLACE INTO ");
            builder.append("\"");
            builder.append(table.getName());
            builder.append("\"");
            builder.append(" (");
            for (KeyValue kv : keyValueList) {
                builder.append("\"");
                builder.append(kv.key);
                builder.append("\"");
                builder.append(',');
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(") VALUES (");
            int length = keyValueList.size();
            for (int i = 0; i < length; i++) {
                builder.append("?,");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(")");
            String sql2 = builder.toString();
            result.setSql(sql2);
            result.addBindArgs(keyValueList);
            REPLACE_SQL_CACHE.put(table, sql2);
        } else {
            result.setSql(sql);
            result.addBindArgs(keyValueList);
        }
        return result;
    }

    public static SqlInfo buildDeleteSqlInfo(TableEntity<?> table, Object entity) throws DbException {
        SqlInfo result = new SqlInfo();
        ColumnEntity id = table.getId();
        Object idValue = id.getColumnValue(entity);
        if (idValue != null) {
            result.setSql("DELETE FROM " + "\"" + table.getName() + "\"" + " WHERE " + WhereBuilder.b(id.getName(), "=", idValue));
            return result;
        }
        throw new DbException("this entity[" + table.getEntityType() + "]'s id value is null");
    }

    public static SqlInfo buildDeleteSqlInfoById(TableEntity<?> table, Object idValue) throws DbException {
        SqlInfo result = new SqlInfo();
        ColumnEntity id = table.getId();
        if (idValue != null) {
            result.setSql("DELETE FROM " + "\"" + table.getName() + "\"" + " WHERE " + WhereBuilder.b(id.getName(), "=", idValue));
            return result;
        }
        throw new DbException("this entity[" + table.getEntityType() + "]'s id value is null");
    }

    public static SqlInfo buildDeleteSqlInfo(TableEntity<?> table, WhereBuilder whereBuilder) throws DbException {
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append("\"");
        builder.append(table.getName());
        builder.append("\"");
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            builder.append(" WHERE ");
            builder.append(whereBuilder.toString());
        }
        return new SqlInfo(builder.toString());
    }

    public static SqlInfo buildUpdateSqlInfo(TableEntity<?> table, Object entity, String... updateColumnNames) throws DbException {
        List<KeyValue> keyValueList = entity2KeyValueList(table, entity);
        if (keyValueList.size() == 0) {
            return null;
        }
        HashSet<String> updateColumnNameSet = null;
        if (updateColumnNames != null && updateColumnNames.length > 0) {
            updateColumnNameSet = new HashSet<>(updateColumnNames.length);
            Collections.addAll(updateColumnNameSet, updateColumnNames);
        }
        ColumnEntity id = table.getId();
        Object idValue = id.getColumnValue(entity);
        if (idValue != null) {
            SqlInfo result = new SqlInfo();
            StringBuilder builder = new StringBuilder("UPDATE ");
            builder.append("\"");
            builder.append(table.getName());
            builder.append("\"");
            builder.append(" SET ");
            for (KeyValue kv : keyValueList) {
                if (updateColumnNameSet == null || updateColumnNameSet.contains(kv.key)) {
                    builder.append("\"");
                    builder.append(kv.key);
                    builder.append("\"");
                    builder.append("=?,");
                    result.addBindArg(kv);
                }
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(" WHERE ");
            builder.append(WhereBuilder.b(id.getName(), "=", idValue));
            result.setSql(builder.toString());
            return result;
        }
        throw new DbException("this entity[" + table.getEntityType() + "]'s id value is null");
    }

    public static SqlInfo buildUpdateSqlInfo(TableEntity<?> table, WhereBuilder whereBuilder, KeyValue... nameValuePairs) throws DbException {
        if (nameValuePairs == null || nameValuePairs.length == 0) {
            return null;
        }
        SqlInfo result = new SqlInfo();
        StringBuilder builder = new StringBuilder("UPDATE ");
        builder.append("\"");
        builder.append(table.getName());
        builder.append("\"");
        builder.append(" SET ");
        for (KeyValue kv : nameValuePairs) {
            builder.append("\"");
            builder.append(kv.key);
            builder.append("\"");
            builder.append("=?,");
            result.addBindArg(kv);
        }
        builder.deleteCharAt(builder.length() - 1);
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            builder.append(" WHERE ");
            builder.append(whereBuilder.toString());
        }
        result.setSql(builder.toString());
        return result;
    }

    public static SqlInfo buildCreateTableSqlInfo(TableEntity<?> table) throws DbException {
        ColumnEntity id = table.getId();
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append("\"");
        builder.append(table.getName());
        builder.append("\"");
        builder.append(" ( ");
        if (id.isAutoId()) {
            builder.append("\"");
            builder.append(id.getName());
            builder.append("\"");
            builder.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        } else {
            builder.append("\"");
            builder.append(id.getName());
            builder.append("\"");
            builder.append(id.getColumnDbType());
            builder.append(" PRIMARY KEY, ");
        }
        for (ColumnEntity column : table.getColumnMap().values()) {
            if (!column.isId()) {
                builder.append("\"");
                builder.append(column.getName());
                builder.append("\"");
                builder.append(' ');
                builder.append(column.getColumnDbType());
                builder.append(' ');
                builder.append(column.getProperty());
                builder.append(',');
            }
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(" )");
        return new SqlInfo(builder.toString());
    }

    public static List<KeyValue> entity2KeyValueList(TableEntity<?> table, Object entity) {
        Collection<ColumnEntity> columns = table.getColumnMap().values();
        List<KeyValue> keyValueList = new ArrayList<>(columns.size());
        for (ColumnEntity column : columns) {
            KeyValue kv = column2KeyValue(entity, column);
            if (kv != null) {
                keyValueList.add(kv);
            }
        }
        return keyValueList;
    }

    private static KeyValue column2KeyValue(Object entity, ColumnEntity column) {
        if (column.isAutoId()) {
            return null;
        }
        return new KeyValue(column.getName(), column.getFieldValue(entity));
    }
}

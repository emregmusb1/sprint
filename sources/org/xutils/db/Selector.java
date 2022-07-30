package org.xutils.db;

import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import org.xutils.common.util.IOUtil;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;

public final class Selector<T> {
    private int limit = 0;
    private int offset = 0;
    private List<OrderBy> orderByList;
    private final TableEntity<T> table;
    private WhereBuilder whereBuilder;

    private Selector(TableEntity<T> table2) {
        this.table = table2;
    }

    static <T> Selector<T> from(TableEntity<T> table2) {
        return new Selector<>(table2);
    }

    public Selector<T> where(WhereBuilder whereBuilder2) {
        this.whereBuilder = whereBuilder2;
        return this;
    }

    public Selector<T> where(String columnName, String op, Object value) {
        this.whereBuilder = WhereBuilder.b(columnName, op, value);
        return this;
    }

    public Selector<T> and(String columnName, String op, Object value) {
        this.whereBuilder.and(columnName, op, value);
        return this;
    }

    public Selector<T> and(WhereBuilder where) {
        this.whereBuilder.and(where);
        return this;
    }

    public Selector<T> or(String columnName, String op, Object value) {
        this.whereBuilder.or(columnName, op, value);
        return this;
    }

    public Selector<T> or(WhereBuilder where) {
        this.whereBuilder.or(where);
        return this;
    }

    public Selector<T> expr(String expr) {
        if (this.whereBuilder == null) {
            this.whereBuilder = WhereBuilder.b();
        }
        this.whereBuilder.expr(expr);
        return this;
    }

    public DbModelSelector groupBy(String columnName) {
        return new DbModelSelector((Selector<?>) this, columnName);
    }

    public DbModelSelector select(String... columnExpressions) {
        return new DbModelSelector((Selector<?>) this, columnExpressions);
    }

    public Selector<T> orderBy(String columnName) {
        if (this.orderByList == null) {
            this.orderByList = new ArrayList(5);
        }
        this.orderByList.add(new OrderBy(columnName));
        return this;
    }

    public Selector<T> orderBy(String columnName, boolean desc) {
        if (this.orderByList == null) {
            this.orderByList = new ArrayList(5);
        }
        this.orderByList.add(new OrderBy(columnName, desc));
        return this;
    }

    public Selector<T> limit(int limit2) {
        this.limit = limit2;
        return this;
    }

    public Selector<T> offset(int offset2) {
        this.offset = offset2;
        return this;
    }

    public TableEntity<T> getTable() {
        return this.table;
    }

    public WhereBuilder getWhereBuilder() {
        return this.whereBuilder;
    }

    public List<OrderBy> getOrderByList() {
        return this.orderByList;
    }

    public int getLimit() {
        return this.limit;
    }

    public int getOffset() {
        return this.offset;
    }

    public T findFirst() throws DbException {
        if (!this.table.tableIsExists()) {
            return null;
        }
        limit(1);
        Cursor cursor = this.table.getDb().execQuery(toString());
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    T entity = CursorUtils.getEntity(this.table, cursor);
                    IOUtil.closeQuietly(cursor);
                    return entity;
                }
                IOUtil.closeQuietly(cursor);
            } catch (Throwable th) {
                IOUtil.closeQuietly(cursor);
                throw th;
            }
        }
        return null;
    }

    public List<T> findAll() throws DbException {
        if (!this.table.tableIsExists()) {
            return null;
        }
        List<T> result = null;
        Cursor cursor = this.table.getDb().execQuery(toString());
        if (cursor != null) {
            try {
                result = new ArrayList<>();
                while (cursor.moveToNext()) {
                    result.add(CursorUtils.getEntity(this.table, cursor));
                }
                IOUtil.closeQuietly(cursor);
            } catch (Throwable th) {
                IOUtil.closeQuietly(cursor);
                throw th;
            }
        }
        return result;
    }

    public long count() throws DbException {
        if (!this.table.tableIsExists()) {
            return 0;
        }
        DbModel firstModel = select("count(\"" + this.table.getId().getName() + "\") as count").findFirst();
        if (firstModel != null) {
            return firstModel.getLong("count", 0);
        }
        return 0;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("SELECT ");
        result.append("*");
        result.append(" FROM ");
        result.append("\"");
        result.append(this.table.getName());
        result.append("\"");
        WhereBuilder whereBuilder2 = this.whereBuilder;
        if (whereBuilder2 != null && whereBuilder2.getWhereItemSize() > 0) {
            result.append(" WHERE ");
            result.append(this.whereBuilder.toString());
        }
        List<OrderBy> list = this.orderByList;
        if (list != null && list.size() > 0) {
            result.append(" ORDER BY ");
            for (OrderBy orderBy : this.orderByList) {
                result.append(orderBy.toString());
                result.append(',');
            }
            result.deleteCharAt(result.length() - 1);
        }
        if (this.limit > 0) {
            result.append(" LIMIT ");
            result.append(this.limit);
            result.append(" OFFSET ");
            result.append(this.offset);
        }
        return result.toString();
    }

    public static class OrderBy {
        private String columnName;
        private boolean desc;

        public OrderBy(String columnName2) {
            this.columnName = columnName2;
        }

        public OrderBy(String columnName2, boolean desc2) {
            this.columnName = columnName2;
            this.desc = desc2;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            sb.append(this.columnName);
            sb.append("\"");
            sb.append(this.desc ? " DESC" : " ASC");
            return sb.toString();
        }
    }
}

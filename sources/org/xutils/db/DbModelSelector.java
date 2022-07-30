package org.xutils.db;

import android.database.Cursor;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import org.xutils.common.util.IOUtil;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;

public final class DbModelSelector {
    private String[] columnExpressions;
    private String groupByColumnName;
    private WhereBuilder having;
    private Selector<?> selector;

    private DbModelSelector(TableEntity<?> table) {
        this.selector = Selector.from(table);
    }

    protected DbModelSelector(Selector<?> selector2, String groupByColumnName2) {
        this.selector = selector2;
        this.groupByColumnName = groupByColumnName2;
    }

    protected DbModelSelector(Selector<?> selector2, String[] columnExpressions2) {
        this.selector = selector2;
        this.columnExpressions = columnExpressions2;
    }

    static DbModelSelector from(TableEntity<?> table) {
        return new DbModelSelector(table);
    }

    public DbModelSelector where(WhereBuilder whereBuilder) {
        this.selector.where(whereBuilder);
        return this;
    }

    public DbModelSelector where(String columnName, String op, Object value) {
        this.selector.where(columnName, op, value);
        return this;
    }

    public DbModelSelector and(String columnName, String op, Object value) {
        this.selector.and(columnName, op, value);
        return this;
    }

    public DbModelSelector and(WhereBuilder where) {
        this.selector.and(where);
        return this;
    }

    public DbModelSelector or(String columnName, String op, Object value) {
        this.selector.or(columnName, op, value);
        return this;
    }

    public DbModelSelector or(WhereBuilder where) {
        this.selector.or(where);
        return this;
    }

    public DbModelSelector expr(String expr) {
        this.selector.expr(expr);
        return this;
    }

    public DbModelSelector groupBy(String columnName) {
        this.groupByColumnName = columnName;
        return this;
    }

    public DbModelSelector having(WhereBuilder whereBuilder) {
        this.having = whereBuilder;
        return this;
    }

    public DbModelSelector select(String... columnExpressions2) {
        this.columnExpressions = columnExpressions2;
        return this;
    }

    public DbModelSelector orderBy(String columnName) {
        this.selector.orderBy(columnName);
        return this;
    }

    public DbModelSelector orderBy(String columnName, boolean desc) {
        this.selector.orderBy(columnName, desc);
        return this;
    }

    public DbModelSelector limit(int limit) {
        this.selector.limit(limit);
        return this;
    }

    public DbModelSelector offset(int offset) {
        this.selector.offset(offset);
        return this;
    }

    public TableEntity<?> getTable() {
        return this.selector.getTable();
    }

    public DbModel findFirst() throws DbException {
        TableEntity<?> table = this.selector.getTable();
        if (!table.tableIsExists()) {
            return null;
        }
        limit(1);
        Cursor cursor = table.getDb().execQuery(toString());
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    DbModel dbModel = CursorUtils.getDbModel(cursor);
                    IOUtil.closeQuietly(cursor);
                    return dbModel;
                }
                IOUtil.closeQuietly(cursor);
            } catch (Throwable th) {
                IOUtil.closeQuietly(cursor);
                throw th;
            }
        }
        return null;
    }

    public List<DbModel> findAll() throws DbException {
        TableEntity<?> table = this.selector.getTable();
        if (!table.tableIsExists()) {
            return null;
        }
        List<DbModel> result = null;
        Cursor cursor = table.getDb().execQuery(toString());
        if (cursor != null) {
            try {
                result = new ArrayList<>();
                while (cursor.moveToNext()) {
                    result.add(CursorUtils.getDbModel(cursor));
                }
                IOUtil.closeQuietly(cursor);
            } catch (Throwable th) {
                IOUtil.closeQuietly(cursor);
                throw th;
            }
        }
        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("SELECT ");
        String[] strArr = this.columnExpressions;
        if (strArr != null && strArr.length > 0) {
            for (String columnExpression : strArr) {
                result.append(columnExpression);
                result.append(",");
            }
            result.deleteCharAt(result.length() - 1);
        } else if (!TextUtils.isEmpty(this.groupByColumnName)) {
            result.append(this.groupByColumnName);
        } else {
            result.append("*");
        }
        result.append(" FROM ");
        result.append("\"");
        result.append(this.selector.getTable().getName());
        result.append("\"");
        WhereBuilder whereBuilder = this.selector.getWhereBuilder();
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            result.append(" WHERE ");
            result.append(whereBuilder.toString());
        }
        if (!TextUtils.isEmpty(this.groupByColumnName)) {
            result.append(" GROUP BY ");
            result.append("\"");
            result.append(this.groupByColumnName);
            result.append("\"");
            WhereBuilder whereBuilder2 = this.having;
            if (whereBuilder2 != null && whereBuilder2.getWhereItemSize() > 0) {
                result.append(" HAVING ");
                result.append(this.having.toString());
            }
        }
        List<Selector.OrderBy> orderByList = this.selector.getOrderByList();
        if (orderByList != null && orderByList.size() > 0) {
            for (int i = 0; i < orderByList.size(); i++) {
                result.append(" ORDER BY ");
                result.append(orderByList.get(i).toString());
                result.append(',');
            }
            result.deleteCharAt(result.length() - 1);
        }
        if (this.selector.getLimit() > 0) {
            result.append(" LIMIT ");
            result.append(this.selector.getLimit());
            result.append(" OFFSET ");
            result.append(this.selector.getOffset());
        }
        return result.toString();
    }
}

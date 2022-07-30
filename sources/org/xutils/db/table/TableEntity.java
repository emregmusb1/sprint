package org.xutils.db.table;

import android.database.Cursor;
import android.text.TextUtils;
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import org.xutils.DbManager;
import org.xutils.common.util.IOUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.db.annotation.Table;
import org.xutils.db.sqlite.SqlInfoBuilder;
import org.xutils.ex.DbException;

public final class TableEntity<T> {
    private final LinkedHashMap<String, ColumnEntity> columnMap;
    private final Constructor<T> constructor;
    private final DbManager db;
    private final Class<T> entityType;
    private ColumnEntity id;
    private final String name;
    private final String onCreated;
    private volatile Boolean tableCheckedStatus;

    TableEntity(DbManager db2, Class<T> entityType2) throws Throwable {
        this.db = db2;
        this.entityType = entityType2;
        Table table = (Table) entityType2.getAnnotation(Table.class);
        if (table != null) {
            this.name = table.name();
            this.onCreated = table.onCreated();
            try {
                this.constructor = entityType2.getConstructor(new Class[0]);
                this.constructor.setAccessible(true);
                this.columnMap = TableUtils.findColumnMap(entityType2);
                for (ColumnEntity column : this.columnMap.values()) {
                    if (column.isId()) {
                        this.id = column;
                        return;
                    }
                }
            } catch (Throwable th) {
                throw new DbException("missing no-argument constructor for the table: " + this.name);
            }
        } else {
            throw new DbException("missing @Table on " + entityType2.getName());
        }
    }

    public T createEntity() throws Throwable {
        return this.constructor.newInstance(new Object[0]);
    }

    public boolean tableIsExists() throws DbException {
        return tableIsExists(false);
    }

    public boolean tableIsExists(boolean forceCheckFromDb) throws DbException {
        if (this.tableCheckedStatus != null && (this.tableCheckedStatus.booleanValue() || !forceCheckFromDb)) {
            return this.tableCheckedStatus.booleanValue();
        }
        DbManager dbManager = this.db;
        Cursor cursor = dbManager.execQuery("SELECT COUNT(*) AS c FROM sqlite_master WHERE type='table' AND name='" + this.name + "'");
        if (cursor != null) {
            try {
                if (!cursor.moveToNext() || cursor.getInt(0) <= 0) {
                    IOUtil.closeQuietly(cursor);
                } else {
                    this.tableCheckedStatus = true;
                    boolean booleanValue = this.tableCheckedStatus.booleanValue();
                    IOUtil.closeQuietly(cursor);
                    return booleanValue;
                }
            } catch (Throwable th) {
                IOUtil.closeQuietly(cursor);
                throw th;
            }
        }
        this.tableCheckedStatus = false;
        return this.tableCheckedStatus.booleanValue();
    }

    public void createTableIfNotExists() throws DbException {
        if (this.tableCheckedStatus == null || !this.tableCheckedStatus.booleanValue()) {
            synchronized (this.entityType) {
                if (!tableIsExists(true)) {
                    this.db.execNonQuery(SqlInfoBuilder.buildCreateTableSqlInfo(this));
                    this.tableCheckedStatus = true;
                    if (!TextUtils.isEmpty(this.onCreated)) {
                        this.db.execNonQuery(this.onCreated);
                    }
                    DbManager.TableCreateListener listener = this.db.getDaoConfig().getTableCreateListener();
                    if (listener != null) {
                        try {
                            listener.onTableCreated(this.db, this);
                        } catch (Throwable ex) {
                            LogUtil.e(ex.getMessage(), ex);
                        }
                    }
                }
            }
        }
    }

    public DbManager getDb() {
        return this.db;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getEntityType() {
        return this.entityType;
    }

    public String getOnCreated() {
        return this.onCreated;
    }

    public ColumnEntity getId() {
        return this.id;
    }

    public LinkedHashMap<String, ColumnEntity> getColumnMap() {
        return this.columnMap;
    }

    /* access modifiers changed from: package-private */
    public void setTableCheckedStatus(boolean tableCheckedStatus2) {
        this.tableCheckedStatus = Boolean.valueOf(tableCheckedStatus2);
    }

    public String toString() {
        return this.name;
    }
}

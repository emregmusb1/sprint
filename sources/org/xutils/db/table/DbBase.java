package org.xutils.db.table;

import android.database.Cursor;
import java.util.HashMap;
import org.xutils.DbManager;
import org.xutils.common.util.IOUtil;
import org.xutils.ex.DbException;

public abstract class DbBase implements DbManager {
    private final HashMap<Class<?>, TableEntity<?>> tableMap = new HashMap<>();

    public <T> TableEntity<T> getTable(Class<T> entityType) throws DbException {
        TableEntity<T> table;
        synchronized (this.tableMap) {
            table = this.tableMap.get(entityType);
            if (table == null) {
                try {
                    table = new TableEntity<>(this, entityType);
                    this.tableMap.put(entityType, table);
                } catch (DbException ex) {
                    throw ex;
                } catch (Throwable ex2) {
                    throw new DbException(ex2);
                }
            }
        }
        return table;
    }

    public void dropTable(Class<?> entityType) throws DbException {
        TableEntity<?> table = getTable(entityType);
        if (table.tableIsExists()) {
            execNonQuery("DROP TABLE \"" + table.getName() + "\"");
            table.setTableCheckedStatus(false);
            removeTable(entityType);
        }
    }

    public void dropDb() throws DbException {
        Cursor cursor = execQuery("SELECT name FROM sqlite_master WHERE type='table' AND name<>'sqlite_sequence'");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String tableName = cursor.getString(0);
                    execNonQuery("DROP TABLE " + tableName);
                } catch (Throwable e) {
                    try {
                        throw new DbException(e);
                    } catch (Throwable th) {
                        IOUtil.closeQuietly(cursor);
                        throw th;
                    }
                }
            }
            synchronized (this.tableMap) {
                for (TableEntity<?> table : this.tableMap.values()) {
                    table.setTableCheckedStatus(false);
                }
                this.tableMap.clear();
            }
            IOUtil.closeQuietly(cursor);
        }
    }

    public void addColumn(Class<?> entityType, String column) throws DbException {
        TableEntity<?> table = getTable(entityType);
        ColumnEntity col = table.getColumnMap().get(column);
        if (col == null) {
            throw new DbException("the column(" + column + ") is not defined in table: " + table.getName());
        } else if (table.tableIsExists()) {
            execNonQuery("ALTER TABLE " + "\"" + table.getName() + "\"" + " ADD COLUMN " + "\"" + col.getName() + "\"" + " " + col.getColumnDbType() + " " + col.getProperty());
        }
    }

    /* access modifiers changed from: protected */
    public void removeTable(Class<?> entityType) {
        synchronized (this.tableMap) {
            this.tableMap.remove(entityType);
        }
    }
}

package org.xutils.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.xutils.DbManager;
import org.xutils.common.util.IOUtil;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.SqlInfoBuilder;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.ColumnEntity;
import org.xutils.db.table.DbBase;
import org.xutils.db.table.DbModel;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

public final class DbManagerImpl extends DbBase {
    private static final HashMap<DbManager.DaoConfig, DbManagerImpl> DAO_MAP = new HashMap<>();
    private boolean allowTransaction;
    private DbManager.DaoConfig daoConfig;
    private SQLiteDatabase database;

    private DbManagerImpl(DbManager.DaoConfig config) throws DbException {
        if (config != null) {
            this.daoConfig = config;
            this.allowTransaction = config.isAllowTransaction();
            try {
                this.database = openOrCreateDatabase(config);
                DbManager.DbOpenListener dbOpenListener = config.getDbOpenListener();
                if (dbOpenListener != null) {
                    dbOpenListener.onDbOpened(this);
                }
            } catch (DbException ex) {
                IOUtil.closeQuietly((Closeable) this.database);
                throw ex;
            } catch (Throwable ex2) {
                IOUtil.closeQuietly((Closeable) this.database);
                throw new DbException(ex2.getMessage(), ex2);
            }
        } else {
            throw new IllegalArgumentException("daoConfig may not be null");
        }
    }

    public static synchronized DbManager getInstance(DbManager.DaoConfig daoConfig2) throws DbException {
        DbManagerImpl dao;
        synchronized (DbManagerImpl.class) {
            if (daoConfig2 == null) {
                daoConfig2 = new DbManager.DaoConfig();
            }
            dao = DAO_MAP.get(daoConfig2);
            if (dao == null) {
                dao = new DbManagerImpl(daoConfig2);
                DAO_MAP.put(daoConfig2, dao);
            } else {
                dao.daoConfig = daoConfig2;
            }
            SQLiteDatabase database2 = dao.database;
            int oldVersion = database2.getVersion();
            int newVersion = daoConfig2.getDbVersion();
            if (oldVersion != newVersion) {
                if (oldVersion != 0) {
                    DbManager.DbUpgradeListener upgradeListener = daoConfig2.getDbUpgradeListener();
                    if (upgradeListener != null) {
                        upgradeListener.onUpgrade(dao, oldVersion, newVersion);
                    } else {
                        dao.dropDb();
                    }
                }
                database2.setVersion(newVersion);
            }
        }
        return dao;
    }

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    public DbManager.DaoConfig getDaoConfig() {
        return this.daoConfig;
    }

    public void saveOrUpdate(Object entity) throws DbException {
        try {
            beginTransaction();
            if (entity instanceof List) {
                List<?> entities = (List) entity;
                if (!entities.isEmpty()) {
                    TableEntity<?> table = getTable(entities.get(0).getClass());
                    table.createTableIfNotExists();
                    for (Object item : entities) {
                        saveOrUpdateWithoutTransaction(table, item);
                    }
                } else {
                    return;
                }
            } else {
                TableEntity<?> table2 = getTable(entity.getClass());
                table2.createTableIfNotExists();
                saveOrUpdateWithoutTransaction(table2, entity);
            }
            setTransactionSuccessful();
            endTransaction();
        } finally {
            endTransaction();
        }
    }

    public void replace(Object entity) throws DbException {
        try {
            beginTransaction();
            if (entity instanceof List) {
                List<?> entities = (List) entity;
                if (!entities.isEmpty()) {
                    TableEntity<?> table = getTable(entities.get(0).getClass());
                    table.createTableIfNotExists();
                    for (Object item : entities) {
                        execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(table, item));
                    }
                } else {
                    return;
                }
            } else {
                TableEntity<?> table2 = getTable(entity.getClass());
                table2.createTableIfNotExists();
                execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(table2, entity));
            }
            setTransactionSuccessful();
            endTransaction();
        } finally {
            endTransaction();
        }
    }

    public void save(Object entity) throws DbException {
        try {
            beginTransaction();
            if (entity instanceof List) {
                List<?> entities = (List) entity;
                if (!entities.isEmpty()) {
                    TableEntity<?> table = getTable(entities.get(0).getClass());
                    table.createTableIfNotExists();
                    for (Object item : entities) {
                        execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(table, item));
                    }
                } else {
                    return;
                }
            } else {
                TableEntity<?> table2 = getTable(entity.getClass());
                table2.createTableIfNotExists();
                execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(table2, entity));
            }
            setTransactionSuccessful();
            endTransaction();
        } finally {
            endTransaction();
        }
    }

    public boolean saveBindingId(Object entity) throws DbException {
        boolean result = false;
        try {
            beginTransaction();
            if (entity instanceof List) {
                List<?> entities = (List) entity;
                if (entities.isEmpty()) {
                    return false;
                }
                TableEntity<?> table = getTable(entities.get(0).getClass());
                table.createTableIfNotExists();
                for (Object item : entities) {
                    if (!saveBindingIdWithoutTransaction(table, item)) {
                        throw new DbException("saveBindingId error, transaction will not commit!");
                    }
                }
            } else {
                TableEntity<?> table2 = getTable(entity.getClass());
                table2.createTableIfNotExists();
                result = saveBindingIdWithoutTransaction(table2, entity);
            }
            setTransactionSuccessful();
            endTransaction();
            return result;
        } finally {
            endTransaction();
        }
    }

    public void deleteById(Class<?> entityType, Object idValue) throws DbException {
        TableEntity<?> table = getTable(entityType);
        if (table.tableIsExists()) {
            try {
                beginTransaction();
                execNonQuery(SqlInfoBuilder.buildDeleteSqlInfoById(table, idValue));
                setTransactionSuccessful();
            } finally {
                endTransaction();
            }
        }
    }

    public void delete(Object entity) throws DbException {
        try {
            beginTransaction();
            if (entity instanceof List) {
                List<?> entities = (List) entity;
                if (!entities.isEmpty()) {
                    TableEntity<?> table = getTable(entities.get(0).getClass());
                    if (!table.tableIsExists()) {
                        endTransaction();
                        return;
                    }
                    for (Object item : entities) {
                        execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(table, item));
                    }
                } else {
                    return;
                }
            } else {
                TableEntity<?> table2 = getTable(entity.getClass());
                if (!table2.tableIsExists()) {
                    endTransaction();
                    return;
                }
                execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(table2, entity));
            }
            setTransactionSuccessful();
            endTransaction();
        } finally {
            endTransaction();
        }
    }

    public void delete(Class<?> entityType) throws DbException {
        delete(entityType, (WhereBuilder) null);
    }

    public int delete(Class<?> entityType, WhereBuilder whereBuilder) throws DbException {
        TableEntity<?> table = getTable(entityType);
        if (!table.tableIsExists()) {
            return 0;
        }
        try {
            beginTransaction();
            int result = executeUpdateDelete(SqlInfoBuilder.buildDeleteSqlInfo(table, whereBuilder));
            setTransactionSuccessful();
            return result;
        } finally {
            endTransaction();
        }
    }

    public void update(Object entity, String... updateColumnNames) throws DbException {
        try {
            beginTransaction();
            if (entity instanceof List) {
                List<?> entities = (List) entity;
                if (!entities.isEmpty()) {
                    TableEntity<?> table = getTable(entities.get(0).getClass());
                    if (!table.tableIsExists()) {
                        endTransaction();
                        return;
                    }
                    for (Object item : entities) {
                        execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(table, item, updateColumnNames));
                    }
                } else {
                    return;
                }
            } else {
                TableEntity<?> table2 = getTable(entity.getClass());
                if (!table2.tableIsExists()) {
                    endTransaction();
                    return;
                }
                execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(table2, entity, updateColumnNames));
            }
            setTransactionSuccessful();
            endTransaction();
        } finally {
            endTransaction();
        }
    }

    public int update(Class<?> entityType, WhereBuilder whereBuilder, KeyValue... nameValuePairs) throws DbException {
        TableEntity<?> table = getTable(entityType);
        if (!table.tableIsExists()) {
            return 0;
        }
        try {
            beginTransaction();
            int result = executeUpdateDelete(SqlInfoBuilder.buildUpdateSqlInfo(table, whereBuilder, nameValuePairs));
            setTransactionSuccessful();
            return result;
        } finally {
            endTransaction();
        }
    }

    public <T> T findById(Class<T> entityType, Object idValue) throws DbException {
        Cursor cursor;
        TableEntity<T> table = getTable(entityType);
        if (table.tableIsExists() && (cursor = execQuery(Selector.from(table).where(table.getId().getName(), "=", idValue).limit(1).toString())) != null) {
            try {
                if (cursor.moveToNext()) {
                    T entity = CursorUtils.getEntity(table, cursor);
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

    public <T> T findFirst(Class<T> entityType) throws DbException {
        return selector(entityType).findFirst();
    }

    public <T> List<T> findAll(Class<T> entityType) throws DbException {
        return selector(entityType).findAll();
    }

    public <T> Selector<T> selector(Class<T> entityType) throws DbException {
        return Selector.from(getTable(entityType));
    }

    public DbModel findDbModelFirst(SqlInfo sqlInfo) throws DbException {
        Cursor cursor = execQuery(sqlInfo);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToNext()) {
                DbModel dbModel = CursorUtils.getDbModel(cursor);
                IOUtil.closeQuietly(cursor);
                return dbModel;
            }
            IOUtil.closeQuietly(cursor);
            return null;
        } catch (Throwable th) {
            IOUtil.closeQuietly(cursor);
            throw th;
        }
    }

    public List<DbModel> findDbModelAll(SqlInfo sqlInfo) throws DbException {
        List<DbModel> dbModelList = new ArrayList<>();
        Cursor cursor = execQuery(sqlInfo);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    dbModelList.add(CursorUtils.getDbModel(cursor));
                } catch (Throwable th) {
                    IOUtil.closeQuietly(cursor);
                    throw th;
                }
            }
            IOUtil.closeQuietly(cursor);
        }
        return dbModelList;
    }

    private SQLiteDatabase openOrCreateDatabase(DbManager.DaoConfig config) {
        File dbDir = config.getDbDir();
        if (dbDir == null || (!dbDir.exists() && !dbDir.mkdirs())) {
            return x.app().openOrCreateDatabase(config.getDbName(), 0, (SQLiteDatabase.CursorFactory) null);
        }
        return SQLiteDatabase.openOrCreateDatabase(new File(dbDir, config.getDbName()), (SQLiteDatabase.CursorFactory) null);
    }

    private void saveOrUpdateWithoutTransaction(TableEntity<?> table, Object entity) throws DbException {
        ColumnEntity id = table.getId();
        if (!id.isAutoId()) {
            execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(table, entity));
        } else if (id.getColumnValue(entity) != null) {
            execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(table, entity, new String[0]));
        } else {
            saveBindingIdWithoutTransaction(table, entity);
        }
    }

    private boolean saveBindingIdWithoutTransaction(TableEntity<?> table, Object entity) throws DbException {
        ColumnEntity id = table.getId();
        if (id.isAutoId()) {
            execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(table, entity));
            long idValue = getLastAutoIncrementId(table.getName());
            if (idValue == -1) {
                return false;
            }
            id.setAutoIdValue(entity, idValue);
            return true;
        }
        execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(table, entity));
        return true;
    }

    private long getLastAutoIncrementId(String tableName) throws DbException {
        long id = -1;
        Cursor cursor = execQuery("SELECT seq FROM sqlite_sequence WHERE name='" + tableName + "' LIMIT 1");
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    id = cursor.getLong(0);
                }
                IOUtil.closeQuietly(cursor);
            } catch (Throwable th) {
                IOUtil.closeQuietly(cursor);
                throw th;
            }
        }
        return id;
    }

    public void close() throws IOException {
        if (DAO_MAP.containsKey(this.daoConfig)) {
            DAO_MAP.remove(this.daoConfig);
            this.database.close();
        }
    }

    private void beginTransaction() {
        if (!this.allowTransaction) {
            return;
        }
        if (Build.VERSION.SDK_INT < 16 || !this.database.isWriteAheadLoggingEnabled()) {
            this.database.beginTransaction();
        } else {
            this.database.beginTransactionNonExclusive();
        }
    }

    private void setTransactionSuccessful() {
        if (this.allowTransaction) {
            this.database.setTransactionSuccessful();
        }
    }

    private void endTransaction() {
        if (this.allowTransaction) {
            this.database.endTransaction();
        }
    }

    public int executeUpdateDelete(SqlInfo sqlInfo) throws DbException {
        SQLiteStatement statement = null;
        try {
            SQLiteStatement statement2 = sqlInfo.buildStatement(this.database);
            int executeUpdateDelete = statement2.executeUpdateDelete();
            if (statement2 != null) {
                try {
                    statement2.releaseReference();
                } catch (Throwable ex) {
                    LogUtil.e(ex.getMessage(), ex);
                }
            }
            return executeUpdateDelete;
        } catch (Throwable ex2) {
            LogUtil.e(ex2.getMessage(), ex2);
        }
        throw th;
    }

    public int executeUpdateDelete(String sql) throws DbException {
        SQLiteStatement statement = null;
        try {
            SQLiteStatement statement2 = this.database.compileStatement(sql);
            int executeUpdateDelete = statement2.executeUpdateDelete();
            if (statement2 != null) {
                try {
                    statement2.releaseReference();
                } catch (Throwable ex) {
                    LogUtil.e(ex.getMessage(), ex);
                }
            }
            return executeUpdateDelete;
        } catch (Throwable ex2) {
            LogUtil.e(ex2.getMessage(), ex2);
        }
        throw th;
    }

    public void execNonQuery(SqlInfo sqlInfo) throws DbException {
        SQLiteStatement statement = null;
        try {
            statement = sqlInfo.buildStatement(this.database);
            statement.execute();
            if (statement != null) {
                try {
                    statement.releaseReference();
                    return;
                } catch (Throwable ex) {
                    LogUtil.e(ex.getMessage(), ex);
                    return;
                }
            } else {
                return;
            }
        } catch (Throwable ex2) {
            LogUtil.e(ex2.getMessage(), ex2);
        }
        throw th;
    }

    public void execNonQuery(String sql) throws DbException {
        try {
            this.database.execSQL(sql);
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

    public Cursor execQuery(SqlInfo sqlInfo) throws DbException {
        try {
            return this.database.rawQuery(sqlInfo.getSql(), sqlInfo.getBindArgsAsStrArray());
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

    public Cursor execQuery(String sql) throws DbException {
        try {
            return this.database.rawQuery(sql, (String[]) null);
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }
}

package org.xutils.config;

import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;

public enum DbConfigs {
    HTTP(new DbManager.DaoConfig().setDbName("xUtils_http_cache.db").setDbVersion(2).setDbOpenListener(new DbManager.DbOpenListener() {
        public void onDbOpened(DbManager db) {
            db.getDatabase().enableWriteAheadLogging();
        }
    }).setDbUpgradeListener(new DbManager.DbUpgradeListener() {
        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
            try {
                db.dropDb();
            } catch (DbException ex) {
                LogUtil.e(ex.getMessage(), ex);
            }
        }
    })),
    COOKIE(new DbManager.DaoConfig().setDbName("xUtils_http_cookie.db").setDbVersion(1).setDbOpenListener(new DbManager.DbOpenListener() {
        public void onDbOpened(DbManager db) {
            db.getDatabase().enableWriteAheadLogging();
        }
    }).setDbUpgradeListener(new DbManager.DbUpgradeListener() {
        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
            try {
                db.dropDb();
            } catch (DbException ex) {
                LogUtil.e(ex.getMessage(), ex);
            }
        }
    }));
    
    private DbManager.DaoConfig config;

    private DbConfigs(DbManager.DaoConfig config2) {
        this.config = config2;
    }

    public DbManager.DaoConfig getConfig() {
        return this.config;
    }
}

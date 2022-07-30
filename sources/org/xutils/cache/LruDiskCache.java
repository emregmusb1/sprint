package org.xutils.cache;

import android.text.TextUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import org.xutils.DbManager;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.common.util.FileUtil;
import org.xutils.common.util.IOUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.common.util.MD5;
import org.xutils.common.util.ProcessLock;
import org.xutils.config.DbConfigs;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.FileLockedException;
import org.xutils.x;

public final class LruDiskCache {
    private static final String CACHE_DIR_NAME = "xUtils_cache";
    private static final HashMap<String, LruDiskCache> DISK_CACHE_MAP = new HashMap<>(5);
    private static final int LIMIT_COUNT = 5000;
    private static final long LIMIT_SIZE = 104857600;
    private static final int LOCK_WAIT = 3000;
    private static final String TEMP_FILE_SUFFIX = ".tmp";
    private static final long TRIM_TIME_SPAN = 1000;
    /* access modifiers changed from: private */
    public boolean available = false;
    /* access modifiers changed from: private */
    public DbManager cacheDb;
    /* access modifiers changed from: private */
    public File cacheDir;
    /* access modifiers changed from: private */
    public long diskCacheSize = LIMIT_SIZE;
    /* access modifiers changed from: private */
    public long lastTrimTime = 0;
    private final Executor trimExecutor = new PriorityExecutor(1, true);

    public static synchronized LruDiskCache getDiskCache(String dirName) {
        LruDiskCache cache;
        synchronized (LruDiskCache.class) {
            if (TextUtils.isEmpty(dirName)) {
                dirName = CACHE_DIR_NAME;
            }
            cache = DISK_CACHE_MAP.get(dirName);
            if (cache == null) {
                cache = new LruDiskCache(dirName);
                DISK_CACHE_MAP.put(dirName, cache);
            }
        }
        return cache;
    }

    private LruDiskCache(String dirName) {
        try {
            this.cacheDir = FileUtil.getCacheDir(dirName);
            if (this.cacheDir != null && (this.cacheDir.exists() || this.cacheDir.mkdirs())) {
                this.available = true;
            }
            this.cacheDb = x.getDb(DbConfigs.HTTP.getConfig());
        } catch (Throwable ex) {
            this.available = false;
            LogUtil.e(ex.getMessage(), ex);
        }
        deleteNoIndexFiles();
    }

    public LruDiskCache setMaxSize(long maxSize) {
        if (maxSize > 0) {
            long diskFreeSize = FileUtil.getDiskAvailableSize();
            if (diskFreeSize > maxSize) {
                this.diskCacheSize = maxSize;
            } else {
                this.diskCacheSize = diskFreeSize;
            }
        }
        return this;
    }

    public DiskCacheEntity get(String key) {
        if (!this.available || TextUtils.isEmpty(key)) {
            return null;
        }
        DiskCacheEntity result = null;
        try {
            result = this.cacheDb.selector(DiskCacheEntity.class).where("key", "=", key).findFirst();
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
        if (result != null) {
            if (result.getExpires() < System.currentTimeMillis()) {
                return null;
            }
            final DiskCacheEntity finalResult = result;
            this.trimExecutor.execute(new Runnable() {
                public void run() {
                    DiskCacheEntity diskCacheEntity = finalResult;
                    diskCacheEntity.setHits(diskCacheEntity.getHits() + 1);
                    finalResult.setLastAccess(System.currentTimeMillis());
                    try {
                        LruDiskCache.this.cacheDb.update(finalResult, "hits", "lastAccess");
                    } catch (Throwable ex) {
                        LogUtil.e(ex.getMessage(), ex);
                    }
                }
            });
        }
        return result;
    }

    public void put(DiskCacheEntity entity) {
        if (this.available && entity != null && !TextUtils.isEmpty(entity.getTextContent()) && entity.getExpires() >= System.currentTimeMillis()) {
            try {
                this.cacheDb.replace(entity);
            } catch (Throwable ex) {
                LogUtil.e(ex.getMessage(), ex);
            }
            trimSize();
        }
    }

    public DiskCacheFile getDiskCacheFile(String key) throws InterruptedException {
        DiskCacheEntity entity;
        ProcessLock processLock;
        if (!this.available || TextUtils.isEmpty(key) || (entity = get(key)) == null || !new File(entity.getPath()).exists() || (processLock = ProcessLock.tryLock(entity.getPath(), false, 3000)) == null || !processLock.isValid()) {
            return null;
        }
        DiskCacheFile result = new DiskCacheFile(entity.getPath(), entity, processLock);
        if (result.exists()) {
            return result;
        }
        try {
            this.cacheDb.delete((Object) entity);
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
        return null;
    }

    public DiskCacheFile createDiskCacheFile(DiskCacheEntity entity) throws IOException {
        if (!this.available || entity == null) {
            return null;
        }
        entity.setPath(new File(this.cacheDir, MD5.md5(entity.getKey())).getAbsolutePath());
        String tempFilePath = entity.getPath() + TEMP_FILE_SUFFIX;
        ProcessLock processLock = ProcessLock.tryLock(tempFilePath, true);
        if (processLock == null || !processLock.isValid()) {
            throw new FileLockedException(entity.getPath());
        }
        DiskCacheFile result = new DiskCacheFile(tempFilePath, entity, processLock);
        if (!result.getParentFile().exists()) {
            result.mkdirs();
        }
        return result;
    }

    public void clearCacheFiles() {
        IOUtil.deleteFileOrDir(this.cacheDir);
    }

    /* access modifiers changed from: package-private */
    public DiskCacheFile commitDiskCacheFile(DiskCacheFile cacheFile) throws IOException {
        DiskCacheFile result;
        if (!this.available || cacheFile == null) {
            return cacheFile;
        }
        DiskCacheEntity cacheEntity = cacheFile.getCacheEntity();
        if (!cacheFile.getName().endsWith(TEMP_FILE_SUFFIX)) {
            return cacheFile;
        }
        ProcessLock processLock = null;
        DiskCacheFile destFile = null;
        try {
            String destPath = cacheEntity.getPath();
            processLock = ProcessLock.tryLock(destPath, true, 3000);
            if (processLock == null || !processLock.isValid()) {
                throw new FileLockedException(destPath);
            }
            destFile = new DiskCacheFile(destPath, cacheEntity, processLock);
            if (cacheFile.renameTo(destFile)) {
                result = destFile;
                try {
                    this.cacheDb.replace(cacheEntity);
                } catch (Throwable ex) {
                    LogUtil.e(ex.getMessage(), ex);
                }
                trimSize();
                IOUtil.closeQuietly((Closeable) cacheFile);
                IOUtil.deleteFileOrDir(cacheFile);
                return result;
            }
            throw new IOException("rename:" + cacheFile.getAbsolutePath());
        } catch (InterruptedException ex2) {
            result = cacheFile;
            LogUtil.e(ex2.getMessage(), ex2);
            if (result == null) {
                DiskCacheFile result2 = cacheFile;
                IOUtil.closeQuietly((Closeable) destFile);
                IOUtil.closeQuietly((Closeable) processLock);
                IOUtil.deleteFileOrDir(destFile);
                return result2;
            }
        } catch (Throwable th) {
            if (result == null) {
                DiskCacheFile result3 = cacheFile;
                IOUtil.closeQuietly((Closeable) destFile);
                IOUtil.closeQuietly((Closeable) processLock);
                IOUtil.deleteFileOrDir(destFile);
            } else {
                IOUtil.closeQuietly((Closeable) cacheFile);
                IOUtil.deleteFileOrDir(cacheFile);
            }
            throw th;
        }
    }

    private void trimSize() {
        this.trimExecutor.execute(new Runnable() {
            public void run() {
                List<DiskCacheEntity> rmList;
                if (LruDiskCache.this.available) {
                    long current = System.currentTimeMillis();
                    if (current - LruDiskCache.this.lastTrimTime >= LruDiskCache.TRIM_TIME_SPAN) {
                        long unused = LruDiskCache.this.lastTrimTime = current;
                        LruDiskCache.this.deleteExpiry();
                        try {
                            int count = (int) LruDiskCache.this.cacheDb.selector(DiskCacheEntity.class).count();
                            if (count > 5010 && (rmList = LruDiskCache.this.cacheDb.selector(DiskCacheEntity.class).orderBy("lastAccess").orderBy("hits").limit(count - 5000).offset(0).findAll()) != null && rmList.size() > 0) {
                                for (DiskCacheEntity entity : rmList) {
                                    LruDiskCache.this.cacheDb.delete((Object) entity);
                                    String path = entity.getPath();
                                    if (!TextUtils.isEmpty(path)) {
                                        boolean unused2 = LruDiskCache.this.deleteFileWithLock(path);
                                        LruDiskCache lruDiskCache = LruDiskCache.this;
                                        boolean unused3 = lruDiskCache.deleteFileWithLock(path + LruDiskCache.TEMP_FILE_SUFFIX);
                                    }
                                }
                            }
                        } catch (Throwable ex) {
                            LogUtil.e(ex.getMessage(), ex);
                        }
                        while (FileUtil.getFileOrDirSize(LruDiskCache.this.cacheDir) > LruDiskCache.this.diskCacheSize) {
                            try {
                                List<DiskCacheEntity> rmList2 = LruDiskCache.this.cacheDb.selector(DiskCacheEntity.class).orderBy("lastAccess").orderBy("hits").limit(10).offset(0).findAll();
                                if (rmList2 != null && rmList2.size() > 0) {
                                    for (DiskCacheEntity entity2 : rmList2) {
                                        LruDiskCache.this.cacheDb.delete((Object) entity2);
                                        String path2 = entity2.getPath();
                                        if (!TextUtils.isEmpty(path2)) {
                                            boolean unused4 = LruDiskCache.this.deleteFileWithLock(path2);
                                            LruDiskCache lruDiskCache2 = LruDiskCache.this;
                                            boolean unused5 = lruDiskCache2.deleteFileWithLock(path2 + LruDiskCache.TEMP_FILE_SUFFIX);
                                        }
                                    }
                                }
                            } catch (Throwable ex2) {
                                LogUtil.e(ex2.getMessage(), ex2);
                                return;
                            }
                        }
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void deleteExpiry() {
        if (this.available) {
            try {
                WhereBuilder whereBuilder = WhereBuilder.b("expires", "<", Long.valueOf(System.currentTimeMillis()));
                List<DiskCacheEntity> rmList = this.cacheDb.selector(DiskCacheEntity.class).where(whereBuilder).findAll();
                this.cacheDb.delete(DiskCacheEntity.class, whereBuilder);
                if (rmList != null && rmList.size() > 0) {
                    for (DiskCacheEntity entity : rmList) {
                        String path = entity.getPath();
                        if (!TextUtils.isEmpty(path)) {
                            deleteFileWithLock(path);
                        }
                    }
                }
            } catch (Throwable ex) {
                LogUtil.e(ex.getMessage(), ex);
            }
        }
    }

    private void deleteNoIndexFiles() {
        this.trimExecutor.execute(new Runnable() {
            public void run() {
                if (LruDiskCache.this.available) {
                    try {
                        File[] fileList = LruDiskCache.this.cacheDir.listFiles();
                        if (fileList != null) {
                            for (File file : fileList) {
                                if (LruDiskCache.this.cacheDb.selector(DiskCacheEntity.class).where("path", "=", file.getAbsolutePath()).count() < 1) {
                                    IOUtil.deleteFileOrDir(file);
                                }
                            }
                        }
                    } catch (Throwable ex) {
                        LogUtil.e(ex.getMessage(), ex);
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public boolean deleteFileWithLock(String path) {
        ProcessLock processLock = null;
        try {
            processLock = ProcessLock.tryLock(path, true);
            if (processLock != null && processLock.isValid()) {
                return IOUtil.deleteFileOrDir(new File(path));
            }
            IOUtil.closeQuietly((Closeable) processLock);
            return false;
        } finally {
            IOUtil.closeQuietly((Closeable) processLock);
        }
    }
}

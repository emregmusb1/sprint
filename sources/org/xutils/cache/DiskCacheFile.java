package org.xutils.cache;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import org.xutils.common.util.IOUtil;
import org.xutils.common.util.ProcessLock;

public final class DiskCacheFile extends File implements Closeable {
    private final DiskCacheEntity cacheEntity;
    private final ProcessLock lock;

    DiskCacheFile(String path, DiskCacheEntity cacheEntity2, ProcessLock lock2) {
        super(path);
        this.cacheEntity = cacheEntity2;
        this.lock = lock2;
    }

    public void close() throws IOException {
        IOUtil.closeQuietly((Closeable) this.lock);
    }

    public DiskCacheFile commit() throws IOException {
        return getDiskCache().commitDiskCacheFile(this);
    }

    public LruDiskCache getDiskCache() {
        return LruDiskCache.getDiskCache(getParentFile().getName());
    }

    public DiskCacheEntity getCacheEntity() {
        return this.cacheEntity;
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
        close();
    }
}

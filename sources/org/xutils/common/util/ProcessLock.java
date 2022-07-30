package org.xutils.common.util;

import android.text.TextUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;
import org.xutils.x;

public final class ProcessLock implements Closeable {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.##################");
    private static final String LOCK_FILE_DIR = "process_lock";
    private static final DoubleKeyValueMap<String, Integer, ProcessLock> LOCK_MAP = new DoubleKeyValueMap<>();
    private final File mFile;
    private final FileLock mFileLock;
    private final String mLockName;
    private final Closeable mStream;
    private final boolean mWriteMode;

    static {
        IOUtil.deleteFileOrDir(x.app().getDir(LOCK_FILE_DIR, 0));
    }

    private ProcessLock(String lockName, File file, FileLock fileLock, Closeable stream, boolean writeMode) {
        this.mLockName = lockName;
        this.mFileLock = fileLock;
        this.mFile = file;
        this.mStream = stream;
        this.mWriteMode = writeMode;
    }

    public static ProcessLock tryLock(String lockName, boolean writeMode) {
        return tryLockInternal(lockName, customHash(lockName), writeMode);
    }

    public static ProcessLock tryLock(String lockName, boolean writeMode, long maxWaitTimeMillis) throws InterruptedException {
        ProcessLock lock = null;
        long expiryTime = System.currentTimeMillis() + maxWaitTimeMillis;
        String hash = customHash(lockName);
        while (System.currentTimeMillis() < expiryTime && (lock = tryLockInternal(lockName, hash, writeMode)) == null) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException iex) {
                throw iex;
            } catch (Throwable th) {
            }
        }
        return lock;
    }

    public boolean isValid() {
        return isValid(this.mFileLock);
    }

    public void release() {
        release(this.mLockName, this.mFileLock, this.mFile, this.mStream);
    }

    public void close() throws IOException {
        release();
    }

    private static boolean isValid(FileLock fileLock) {
        return fileLock != null && fileLock.isValid();
    }

    private static void release(String lockName, FileLock fileLock, File file, Closeable stream) {
        FileChannel fileChannel;
        synchronized (LOCK_MAP) {
            if (fileLock != null) {
                try {
                    LOCK_MAP.remove(lockName, Integer.valueOf(fileLock.hashCode()));
                    ConcurrentHashMap<Integer, ProcessLock> locks = LOCK_MAP.get(lockName);
                    if (locks == null || locks.isEmpty()) {
                        IOUtil.deleteFileOrDir(file);
                    }
                    if (fileLock.channel().isOpen()) {
                        fileLock.release();
                    }
                    fileChannel = fileLock.channel();
                } catch (Throwable ex) {
                    try {
                        LogUtil.e(ex.getMessage(), ex);
                        fileChannel = fileLock.channel();
                    } catch (Throwable th) {
                        IOUtil.closeQuietly((Closeable) fileLock.channel());
                        throw th;
                    }
                }
                IOUtil.closeQuietly((Closeable) fileChannel);
            }
            IOUtil.closeQuietly(stream);
        }
    }

    private static String customHash(String str) {
        if (TextUtils.isEmpty(str)) {
            return "0";
        }
        double hash = 0.0d;
        byte[] bytes = str.getBytes();
        for (int i = 0; i < str.length(); i++) {
            hash = ((255.0d * hash) + ((double) bytes[i])) * 0.005d;
        }
        return FORMAT.format(hash);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: java.io.FileOutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: java.io.FileInputStream} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.xutils.common.util.ProcessLock tryLockInternal(java.lang.String r19, java.lang.String r20, boolean r21) {
        /*
            r7 = r19
            org.xutils.common.util.DoubleKeyValueMap<java.lang.String, java.lang.Integer, org.xutils.common.util.ProcessLock> r8 = LOCK_MAP
            monitor-enter(r8)
            org.xutils.common.util.DoubleKeyValueMap<java.lang.String, java.lang.Integer, org.xutils.common.util.ProcessLock> r0 = LOCK_MAP     // Catch:{ all -> 0x0120 }
            java.util.concurrent.ConcurrentHashMap r0 = r0.get(r7)     // Catch:{ all -> 0x0120 }
            r9 = r0
            r10 = 0
            if (r9 == 0) goto L_0x0049
            boolean r0 = r9.isEmpty()     // Catch:{ all -> 0x0120 }
            if (r0 != 0) goto L_0x0049
            java.util.Set r0 = r9.entrySet()     // Catch:{ all -> 0x0120 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x0120 }
        L_0x001d:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x0120 }
            if (r1 == 0) goto L_0x0049
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x0120 }
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1     // Catch:{ all -> 0x0120 }
            java.lang.Object r2 = r1.getValue()     // Catch:{ all -> 0x0120 }
            org.xutils.common.util.ProcessLock r2 = (org.xutils.common.util.ProcessLock) r2     // Catch:{ all -> 0x0120 }
            if (r2 == 0) goto L_0x0045
            boolean r3 = r2.isValid()     // Catch:{ all -> 0x0120 }
            if (r3 != 0) goto L_0x003b
            r0.remove()     // Catch:{ all -> 0x0120 }
            goto L_0x0048
        L_0x003b:
            if (r21 == 0) goto L_0x003f
            monitor-exit(r8)     // Catch:{ all -> 0x0120 }
            return r10
        L_0x003f:
            boolean r3 = r2.mWriteMode     // Catch:{ all -> 0x0120 }
            if (r3 == 0) goto L_0x0048
            monitor-exit(r8)     // Catch:{ all -> 0x0120 }
            return r10
        L_0x0045:
            r0.remove()     // Catch:{ all -> 0x0120 }
        L_0x0048:
            goto L_0x001d
        L_0x0049:
            r1 = 0
            r2 = 0
            java.io.File r0 = new java.io.File     // Catch:{ Throwable -> 0x00f2 }
            android.app.Application r3 = org.xutils.x.app()     // Catch:{ Throwable -> 0x00f2 }
            java.lang.String r4 = "process_lock"
            r5 = 0
            java.io.File r3 = r3.getDir(r4, r5)     // Catch:{ Throwable -> 0x00f2 }
            r11 = r20
            r0.<init>(r3, r11)     // Catch:{ Throwable -> 0x00f0 }
            boolean r3 = r0.exists()     // Catch:{ Throwable -> 0x00f0 }
            if (r3 != 0) goto L_0x006d
            boolean r3 = r0.createNewFile()     // Catch:{ Throwable -> 0x00f0 }
            if (r3 == 0) goto L_0x006a
            goto L_0x006d
        L_0x006a:
            r14 = r2
            goto L_0x00ce
        L_0x006d:
            if (r21 == 0) goto L_0x007e
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ Throwable -> 0x00f0 }
            r3.<init>(r0, r5)     // Catch:{ Throwable -> 0x00f0 }
            java.nio.channels.FileChannel r4 = r3.getChannel()     // Catch:{ Throwable -> 0x00f0 }
            r1 = r4
            r2 = r3
            r18 = r1
            r6 = r2
            goto L_0x008c
        L_0x007e:
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ Throwable -> 0x00f0 }
            r3.<init>(r0)     // Catch:{ Throwable -> 0x00f0 }
            java.nio.channels.FileChannel r4 = r3.getChannel()     // Catch:{ Throwable -> 0x00f0 }
            r1 = r4
            r2 = r3
            r18 = r1
            r6 = r2
        L_0x008c:
            if (r18 == 0) goto L_0x00d2
            r13 = 0
            r15 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            if (r21 != 0) goto L_0x009b
            r5 = 1
            r17 = 1
            goto L_0x009d
        L_0x009b:
            r17 = 0
        L_0x009d:
            r12 = r18
            java.nio.channels.FileLock r1 = r12.tryLock(r13, r15, r17)     // Catch:{ Throwable -> 0x00cf }
            r12 = r1
            boolean r1 = isValid(r12)     // Catch:{ Throwable -> 0x00cf }
            if (r1 == 0) goto L_0x00c8
            org.xutils.common.util.ProcessLock r13 = new org.xutils.common.util.ProcessLock     // Catch:{ Throwable -> 0x00cf }
            r1 = r13
            r2 = r19
            r3 = r0
            r4 = r12
            r5 = r6
            r14 = r6
            r6 = r21
            r1.<init>(r2, r3, r4, r5, r6)     // Catch:{ Throwable -> 0x00ee }
            r1 = r13
            org.xutils.common.util.DoubleKeyValueMap<java.lang.String, java.lang.Integer, org.xutils.common.util.ProcessLock> r2 = LOCK_MAP     // Catch:{ Throwable -> 0x00ee }
            int r3 = r12.hashCode()     // Catch:{ Throwable -> 0x00ee }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Throwable -> 0x00ee }
            r2.put(r7, r3, r1)     // Catch:{ Throwable -> 0x00ee }
            monitor-exit(r8)     // Catch:{ all -> 0x0125 }
            return r1
        L_0x00c8:
            r14 = r6
            release(r7, r12, r0, r14)     // Catch:{ Throwable -> 0x00ee }
            r1 = r18
        L_0x00ce:
            goto L_0x011e
        L_0x00cf:
            r0 = move-exception
            r14 = r6
            goto L_0x00f8
        L_0x00d2:
            r14 = r6
            java.io.IOException r1 = new java.io.IOException     // Catch:{ Throwable -> 0x00ee }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x00ee }
            r2.<init>()     // Catch:{ Throwable -> 0x00ee }
            java.lang.String r3 = "can not get file channel:"
            r2.append(r3)     // Catch:{ Throwable -> 0x00ee }
            java.lang.String r3 = r0.getAbsolutePath()     // Catch:{ Throwable -> 0x00ee }
            r2.append(r3)     // Catch:{ Throwable -> 0x00ee }
            java.lang.String r2 = r2.toString()     // Catch:{ Throwable -> 0x00ee }
            r1.<init>(r2)     // Catch:{ Throwable -> 0x00ee }
            throw r1     // Catch:{ Throwable -> 0x00ee }
        L_0x00ee:
            r0 = move-exception
            goto L_0x00f8
        L_0x00f0:
            r0 = move-exception
            goto L_0x00f5
        L_0x00f2:
            r0 = move-exception
            r11 = r20
        L_0x00f5:
            r18 = r1
            r14 = r2
        L_0x00f8:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0125 }
            r1.<init>()     // Catch:{ all -> 0x0125 }
            java.lang.String r2 = "tryLock: "
            r1.append(r2)     // Catch:{ all -> 0x0125 }
            r1.append(r7)     // Catch:{ all -> 0x0125 }
            java.lang.String r2 = ", "
            r1.append(r2)     // Catch:{ all -> 0x0125 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x0125 }
            r1.append(r2)     // Catch:{ all -> 0x0125 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0125 }
            org.xutils.common.util.LogUtil.d(r1)     // Catch:{ all -> 0x0125 }
            org.xutils.common.util.IOUtil.closeQuietly((java.io.Closeable) r14)     // Catch:{ all -> 0x0125 }
            org.xutils.common.util.IOUtil.closeQuietly((java.io.Closeable) r18)     // Catch:{ all -> 0x0125 }
        L_0x011e:
            monitor-exit(r8)     // Catch:{ all -> 0x0125 }
            return r10
        L_0x0120:
            r0 = move-exception
            r11 = r20
        L_0x0123:
            monitor-exit(r8)     // Catch:{ all -> 0x0125 }
            throw r0
        L_0x0125:
            r0 = move-exception
            goto L_0x0123
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xutils.common.util.ProcessLock.tryLockInternal(java.lang.String, java.lang.String, boolean):org.xutils.common.util.ProcessLock");
    }

    public String toString() {
        return this.mLockName + ": " + this.mFile.getName();
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
        release();
    }
}

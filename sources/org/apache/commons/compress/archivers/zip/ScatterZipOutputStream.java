package org.apache.commons.compress.archivers.zip;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
import org.apache.commons.compress.parallel.ScatterGatherBackingStore;

public class ScatterZipOutputStream implements Closeable {
    /* access modifiers changed from: private */
    public final ScatterGatherBackingStore backingStore;
    private AtomicBoolean isClosed = new AtomicBoolean();
    /* access modifiers changed from: private */
    public final Queue<CompressedEntry> items = new ConcurrentLinkedQueue();
    private final StreamCompressor streamCompressor;
    private ZipEntryWriter zipEntryWriter = null;

    private static class CompressedEntry {
        final long compressedSize;
        final long crc;
        final long size;
        final ZipArchiveEntryRequest zipArchiveEntryRequest;

        public CompressedEntry(ZipArchiveEntryRequest zipArchiveEntryRequest2, long crc2, long compressedSize2, long size2) {
            this.zipArchiveEntryRequest = zipArchiveEntryRequest2;
            this.crc = crc2;
            this.compressedSize = compressedSize2;
            this.size = size2;
        }

        public ZipArchiveEntry transferToArchiveEntry() {
            ZipArchiveEntry entry = this.zipArchiveEntryRequest.getZipArchiveEntry();
            entry.setCompressedSize(this.compressedSize);
            entry.setSize(this.size);
            entry.setCrc(this.crc);
            entry.setMethod(this.zipArchiveEntryRequest.getMethod());
            return entry;
        }
    }

    public ScatterZipOutputStream(ScatterGatherBackingStore backingStore2, StreamCompressor streamCompressor2) {
        this.backingStore = backingStore2;
        this.streamCompressor = streamCompressor2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0036, code lost:
        if (r0 != null) goto L_0x0038;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0038, code lost:
        if (r1 != null) goto L_0x003a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003e, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003f, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0043, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0032, code lost:
        r2 = move-exception;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addArchiveEntry(org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest r11) throws java.io.IOException {
        /*
            r10 = this;
            java.io.InputStream r0 = r11.getPayloadStream()
            r1 = 0
            org.apache.commons.compress.archivers.zip.StreamCompressor r2 = r10.streamCompressor     // Catch:{ Throwable -> 0x0034 }
            int r3 = r11.getMethod()     // Catch:{ Throwable -> 0x0034 }
            r2.deflate(r0, r3)     // Catch:{ Throwable -> 0x0034 }
            if (r0 == 0) goto L_0x0013
            r0.close()
        L_0x0013:
            java.util.Queue<org.apache.commons.compress.archivers.zip.ScatterZipOutputStream$CompressedEntry> r0 = r10.items
            org.apache.commons.compress.archivers.zip.ScatterZipOutputStream$CompressedEntry r9 = new org.apache.commons.compress.archivers.zip.ScatterZipOutputStream$CompressedEntry
            org.apache.commons.compress.archivers.zip.StreamCompressor r1 = r10.streamCompressor
            long r3 = r1.getCrc32()
            org.apache.commons.compress.archivers.zip.StreamCompressor r1 = r10.streamCompressor
            long r5 = r1.getBytesWrittenForLastEntry()
            org.apache.commons.compress.archivers.zip.StreamCompressor r1 = r10.streamCompressor
            long r7 = r1.getBytesRead()
            r1 = r9
            r2 = r11
            r1.<init>(r2, r3, r5, r7)
            r0.add(r9)
            return
        L_0x0032:
            r2 = move-exception
            goto L_0x0036
        L_0x0034:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0032 }
        L_0x0036:
            if (r0 == 0) goto L_0x0046
            if (r1 == 0) goto L_0x0043
            r0.close()     // Catch:{ Throwable -> 0x003e }
            goto L_0x0046
        L_0x003e:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x0046
        L_0x0043:
            r0.close()
        L_0x0046:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.zip.ScatterZipOutputStream.addArchiveEntry(org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0030, code lost:
        r2 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0031, code lost:
        r5 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0035, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0036, code lost:
        r7 = r5;
        r5 = r2;
        r2 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x004e, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0052, code lost:
        if (r0 != null) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0054, code lost:
        if (r1 != null) goto L_0x0056;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x005a, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x005b, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x005f, code lost:
        r0.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeTo(org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream r9) throws java.io.IOException {
        /*
            r8 = this;
            org.apache.commons.compress.parallel.ScatterGatherBackingStore r0 = r8.backingStore
            r0.closeForWriting()
            org.apache.commons.compress.parallel.ScatterGatherBackingStore r0 = r8.backingStore
            java.io.InputStream r0 = r0.getInputStream()
            r1 = 0
            java.util.Queue<org.apache.commons.compress.archivers.zip.ScatterZipOutputStream$CompressedEntry> r2 = r8.items     // Catch:{ Throwable -> 0x0050 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ Throwable -> 0x0050 }
        L_0x0012:
            boolean r3 = r2.hasNext()     // Catch:{ Throwable -> 0x0050 }
            if (r3 == 0) goto L_0x0048
            java.lang.Object r3 = r2.next()     // Catch:{ Throwable -> 0x0050 }
            org.apache.commons.compress.archivers.zip.ScatterZipOutputStream$CompressedEntry r3 = (org.apache.commons.compress.archivers.zip.ScatterZipOutputStream.CompressedEntry) r3     // Catch:{ Throwable -> 0x0050 }
            org.apache.commons.compress.utils.BoundedInputStream r4 = new org.apache.commons.compress.utils.BoundedInputStream     // Catch:{ Throwable -> 0x0050 }
            long r5 = r3.compressedSize     // Catch:{ Throwable -> 0x0050 }
            r4.<init>(r0, r5)     // Catch:{ Throwable -> 0x0050 }
            org.apache.commons.compress.archivers.zip.ZipArchiveEntry r5 = r3.transferToArchiveEntry()     // Catch:{ Throwable -> 0x0033, all -> 0x0030 }
            r9.addRawArchiveEntry(r5, r4)     // Catch:{ Throwable -> 0x0033, all -> 0x0030 }
            r4.close()     // Catch:{ Throwable -> 0x0050 }
            goto L_0x0012
        L_0x0030:
            r2 = move-exception
            r5 = r1
            goto L_0x0039
        L_0x0033:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0035 }
        L_0x0035:
            r5 = move-exception
            r7 = r5
            r5 = r2
            r2 = r7
        L_0x0039:
            if (r5 == 0) goto L_0x0044
            r4.close()     // Catch:{ Throwable -> 0x003f }
            goto L_0x0047
        L_0x003f:
            r6 = move-exception
            r5.addSuppressed(r6)     // Catch:{ Throwable -> 0x0050 }
            goto L_0x0047
        L_0x0044:
            r4.close()     // Catch:{ Throwable -> 0x0050 }
        L_0x0047:
            throw r2     // Catch:{ Throwable -> 0x0050 }
        L_0x0048:
            if (r0 == 0) goto L_0x004d
            r0.close()
        L_0x004d:
            return
        L_0x004e:
            r2 = move-exception
            goto L_0x0052
        L_0x0050:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x004e }
        L_0x0052:
            if (r0 == 0) goto L_0x0062
            if (r1 == 0) goto L_0x005f
            r0.close()     // Catch:{ Throwable -> 0x005a }
            goto L_0x0062
        L_0x005a:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x0062
        L_0x005f:
            r0.close()
        L_0x0062:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.zip.ScatterZipOutputStream.writeTo(org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream):void");
    }

    public static class ZipEntryWriter implements Closeable {
        private final Iterator<CompressedEntry> itemsIterator;
        private final InputStream itemsIteratorData;

        public ZipEntryWriter(ScatterZipOutputStream scatter) throws IOException {
            scatter.backingStore.closeForWriting();
            this.itemsIterator = scatter.items.iterator();
            this.itemsIteratorData = scatter.backingStore.getInputStream();
        }

        public void close() throws IOException {
            InputStream inputStream = this.itemsIteratorData;
            if (inputStream != null) {
                inputStream.close();
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
            r1.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0027, code lost:
            r4 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0028, code lost:
            r2.addSuppressed(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x002c, code lost:
            r1.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:5:0x001d, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0021, code lost:
            if (r2 != null) goto L_0x0023;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void writeNextZipEntry(org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream r6) throws java.io.IOException {
            /*
                r5 = this;
                java.util.Iterator<org.apache.commons.compress.archivers.zip.ScatterZipOutputStream$CompressedEntry> r0 = r5.itemsIterator
                java.lang.Object r0 = r0.next()
                org.apache.commons.compress.archivers.zip.ScatterZipOutputStream$CompressedEntry r0 = (org.apache.commons.compress.archivers.zip.ScatterZipOutputStream.CompressedEntry) r0
                org.apache.commons.compress.utils.BoundedInputStream r1 = new org.apache.commons.compress.utils.BoundedInputStream
                java.io.InputStream r2 = r5.itemsIteratorData
                long r3 = r0.compressedSize
                r1.<init>(r2, r3)
                r2 = 0
                org.apache.commons.compress.archivers.zip.ZipArchiveEntry r3 = r0.transferToArchiveEntry()     // Catch:{ Throwable -> 0x001f }
                r6.addRawArchiveEntry(r3, r1)     // Catch:{ Throwable -> 0x001f }
                r1.close()
                return
            L_0x001d:
                r3 = move-exception
                goto L_0x0021
            L_0x001f:
                r2 = move-exception
                throw r2     // Catch:{ all -> 0x001d }
            L_0x0021:
                if (r2 == 0) goto L_0x002c
                r1.close()     // Catch:{ Throwable -> 0x0027 }
                goto L_0x002f
            L_0x0027:
                r4 = move-exception
                r2.addSuppressed(r4)
                goto L_0x002f
            L_0x002c:
                r1.close()
            L_0x002f:
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.zip.ScatterZipOutputStream.ZipEntryWriter.writeNextZipEntry(org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream):void");
        }
    }

    public ZipEntryWriter zipEntryWriter() throws IOException {
        if (this.zipEntryWriter == null) {
            this.zipEntryWriter = new ZipEntryWriter(this);
        }
        return this.zipEntryWriter;
    }

    public void close() throws IOException {
        if (this.isClosed.compareAndSet(false, true)) {
            try {
                if (this.zipEntryWriter != null) {
                    this.zipEntryWriter.close();
                }
                this.backingStore.close();
            } finally {
                this.streamCompressor.close();
            }
        }
    }

    public static ScatterZipOutputStream fileBased(File file) throws FileNotFoundException {
        return fileBased(file, -1);
    }

    public static ScatterZipOutputStream fileBased(File file, int compressionLevel) throws FileNotFoundException {
        ScatterGatherBackingStore bs = new FileBasedScatterGatherBackingStore(file);
        return new ScatterZipOutputStream(bs, StreamCompressor.create(compressionLevel, bs));
    }
}

package org.apache.commons.compress.archivers.examples;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.util.Enumeration;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

public class Expander {

    private interface ArchiveEntrySupplier {
        ArchiveEntry getNextReadableEntry() throws IOException;
    }

    private interface EntryWriter {
        void writeEntryDataTo(ArchiveEntry archiveEntry, OutputStream outputStream) throws IOException;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002d, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002e, code lost:
        r2.addSuppressed(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0032, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0023, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0027, code lost:
        if (r2 != null) goto L_0x0029;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void expand(java.io.File r6, java.io.File r7) throws java.io.IOException, org.apache.commons.compress.archivers.ArchiveException {
        /*
            r5 = this;
            r0 = 0
            java.io.BufferedInputStream r1 = new java.io.BufferedInputStream
            java.nio.file.Path r2 = r6.toPath()
            r3 = 0
            java.nio.file.OpenOption[] r3 = new java.nio.file.OpenOption[r3]
            java.io.InputStream r2 = java.nio.file.Files.newInputStream(r2, r3)
            r1.<init>(r2)
            r2 = 0
            org.apache.commons.compress.archivers.ArchiveStreamFactory r3 = new org.apache.commons.compress.archivers.ArchiveStreamFactory     // Catch:{ Throwable -> 0x0025 }
            r3.<init>()     // Catch:{ Throwable -> 0x0025 }
            java.lang.String r2 = org.apache.commons.compress.archivers.ArchiveStreamFactory.detect(r1)     // Catch:{ Throwable -> 0x0025 }
            r0 = r2
            r1.close()
            r5.expand((java.lang.String) r0, (java.io.File) r6, (java.io.File) r7)
            return
        L_0x0023:
            r3 = move-exception
            goto L_0x0027
        L_0x0025:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0023 }
        L_0x0027:
            if (r2 == 0) goto L_0x0032
            r1.close()     // Catch:{ Throwable -> 0x002d }
            goto L_0x0035
        L_0x002d:
            r4 = move-exception
            r2.addSuppressed(r4)
            goto L_0x0035
        L_0x0032:
            r1.close()
        L_0x0035:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.examples.Expander.expand(java.io.File, java.io.File):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0026, code lost:
        if (r0 != null) goto L_0x0028;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0028, code lost:
        if (r1 != null) goto L_0x002a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002e, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002f, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0033, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004f, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0053, code lost:
        if (r1 != null) goto L_0x0055;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0059, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x005a, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x005e, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0022, code lost:
        r2 = move-exception;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void expand(java.lang.String r6, java.io.File r7, java.io.File r8) throws java.io.IOException, org.apache.commons.compress.archivers.ArchiveException {
        /*
            r5 = this;
            boolean r0 = r5.prefersSeekableByteChannel(r6)
            r1 = 0
            r2 = 0
            if (r0 == 0) goto L_0x0037
            java.nio.file.Path r0 = r7.toPath()
            r3 = 1
            java.nio.file.OpenOption[] r3 = new java.nio.file.OpenOption[r3]
            java.nio.file.StandardOpenOption r4 = java.nio.file.StandardOpenOption.READ
            r3[r2] = r4
            java.nio.channels.FileChannel r0 = java.nio.channels.FileChannel.open(r0, r3)
            org.apache.commons.compress.archivers.examples.CloseableConsumer r2 = org.apache.commons.compress.archivers.examples.CloseableConsumer.CLOSING_CONSUMER     // Catch:{ Throwable -> 0x0024 }
            r5.expand((java.lang.String) r6, (java.nio.channels.SeekableByteChannel) r0, (java.io.File) r8, (org.apache.commons.compress.archivers.examples.CloseableConsumer) r2)     // Catch:{ Throwable -> 0x0024 }
            if (r0 == 0) goto L_0x0021
            r0.close()
        L_0x0021:
            return
        L_0x0022:
            r2 = move-exception
            goto L_0x0026
        L_0x0024:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0022 }
        L_0x0026:
            if (r0 == 0) goto L_0x0036
            if (r1 == 0) goto L_0x0033
            r0.close()     // Catch:{ Throwable -> 0x002e }
            goto L_0x0036
        L_0x002e:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x0036
        L_0x0033:
            r0.close()
        L_0x0036:
            throw r2
        L_0x0037:
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream
            java.nio.file.Path r3 = r7.toPath()
            java.nio.file.OpenOption[] r2 = new java.nio.file.OpenOption[r2]
            java.io.InputStream r2 = java.nio.file.Files.newInputStream(r3, r2)
            r0.<init>(r2)
            org.apache.commons.compress.archivers.examples.CloseableConsumer r2 = org.apache.commons.compress.archivers.examples.CloseableConsumer.CLOSING_CONSUMER     // Catch:{ Throwable -> 0x0051 }
            r5.expand((java.lang.String) r6, (java.io.InputStream) r0, (java.io.File) r8, (org.apache.commons.compress.archivers.examples.CloseableConsumer) r2)     // Catch:{ Throwable -> 0x0051 }
            r0.close()
            return
        L_0x004f:
            r2 = move-exception
            goto L_0x0053
        L_0x0051:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x004f }
        L_0x0053:
            if (r1 == 0) goto L_0x005e
            r0.close()     // Catch:{ Throwable -> 0x0059 }
            goto L_0x0061
        L_0x0059:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x0061
        L_0x005e:
            r0.close()
        L_0x0061:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.examples.Expander.expand(java.lang.String, java.io.File, java.io.File):void");
    }

    @Deprecated
    public void expand(InputStream archive, File targetDirectory) throws IOException, ArchiveException {
        expand(archive, targetDirectory, CloseableConsumer.NULL_CONSUMER);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0026, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0027, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002b, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x001c, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0020, code lost:
        if (r1 != null) goto L_0x0022;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void expand(java.io.InputStream r5, java.io.File r6, org.apache.commons.compress.archivers.examples.CloseableConsumer r7) throws java.io.IOException, org.apache.commons.compress.archivers.ArchiveException {
        /*
            r4 = this;
            org.apache.commons.compress.archivers.examples.CloseableConsumerAdapter r0 = new org.apache.commons.compress.archivers.examples.CloseableConsumerAdapter
            r0.<init>(r7)
            r1 = 0
            org.apache.commons.compress.archivers.ArchiveStreamFactory r2 = new org.apache.commons.compress.archivers.ArchiveStreamFactory     // Catch:{ Throwable -> 0x001e }
            r2.<init>()     // Catch:{ Throwable -> 0x001e }
            org.apache.commons.compress.archivers.ArchiveInputStream r2 = r2.createArchiveInputStream(r5)     // Catch:{ Throwable -> 0x001e }
            java.io.Closeable r2 = r0.track(r2)     // Catch:{ Throwable -> 0x001e }
            org.apache.commons.compress.archivers.ArchiveInputStream r2 = (org.apache.commons.compress.archivers.ArchiveInputStream) r2     // Catch:{ Throwable -> 0x001e }
            r4.expand((org.apache.commons.compress.archivers.ArchiveInputStream) r2, (java.io.File) r6)     // Catch:{ Throwable -> 0x001e }
            r0.close()
            return
        L_0x001c:
            r2 = move-exception
            goto L_0x0020
        L_0x001e:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001c }
        L_0x0020:
            if (r1 == 0) goto L_0x002b
            r0.close()     // Catch:{ Throwable -> 0x0026 }
            goto L_0x002e
        L_0x0026:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x002e
        L_0x002b:
            r0.close()
        L_0x002e:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.examples.Expander.expand(java.io.InputStream, java.io.File, org.apache.commons.compress.archivers.examples.CloseableConsumer):void");
    }

    @Deprecated
    public void expand(String format, InputStream archive, File targetDirectory) throws IOException, ArchiveException {
        expand(format, archive, targetDirectory, CloseableConsumer.NULL_CONSUMER);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0026, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0027, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002b, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x001c, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0020, code lost:
        if (r1 != null) goto L_0x0022;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void expand(java.lang.String r5, java.io.InputStream r6, java.io.File r7, org.apache.commons.compress.archivers.examples.CloseableConsumer r8) throws java.io.IOException, org.apache.commons.compress.archivers.ArchiveException {
        /*
            r4 = this;
            org.apache.commons.compress.archivers.examples.CloseableConsumerAdapter r0 = new org.apache.commons.compress.archivers.examples.CloseableConsumerAdapter
            r0.<init>(r8)
            r1 = 0
            org.apache.commons.compress.archivers.ArchiveStreamFactory r2 = new org.apache.commons.compress.archivers.ArchiveStreamFactory     // Catch:{ Throwable -> 0x001e }
            r2.<init>()     // Catch:{ Throwable -> 0x001e }
            org.apache.commons.compress.archivers.ArchiveInputStream r2 = r2.createArchiveInputStream(r5, r6)     // Catch:{ Throwable -> 0x001e }
            java.io.Closeable r2 = r0.track(r2)     // Catch:{ Throwable -> 0x001e }
            org.apache.commons.compress.archivers.ArchiveInputStream r2 = (org.apache.commons.compress.archivers.ArchiveInputStream) r2     // Catch:{ Throwable -> 0x001e }
            r4.expand((org.apache.commons.compress.archivers.ArchiveInputStream) r2, (java.io.File) r7)     // Catch:{ Throwable -> 0x001e }
            r0.close()
            return
        L_0x001c:
            r2 = move-exception
            goto L_0x0020
        L_0x001e:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001c }
        L_0x0020:
            if (r1 == 0) goto L_0x002b
            r0.close()     // Catch:{ Throwable -> 0x0026 }
            goto L_0x002e
        L_0x0026:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x002e
        L_0x002b:
            r0.close()
        L_0x002e:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.examples.Expander.expand(java.lang.String, java.io.InputStream, java.io.File, org.apache.commons.compress.archivers.examples.CloseableConsumer):void");
    }

    @Deprecated
    public void expand(String format, SeekableByteChannel archive, File targetDirectory) throws IOException, ArchiveException {
        expand(format, archive, targetDirectory, CloseableConsumer.NULL_CONSUMER);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0062, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0066, code lost:
        if (r1 != null) goto L_0x0068;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x006c, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x006d, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0071, code lost:
        r0.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void expand(java.lang.String r6, java.nio.channels.SeekableByteChannel r7, java.io.File r8, org.apache.commons.compress.archivers.examples.CloseableConsumer r9) throws java.io.IOException, org.apache.commons.compress.archivers.ArchiveException {
        /*
            r5 = this;
            org.apache.commons.compress.archivers.examples.CloseableConsumerAdapter r0 = new org.apache.commons.compress.archivers.examples.CloseableConsumerAdapter
            r0.<init>(r9)
            r1 = 0
            boolean r2 = r5.prefersSeekableByteChannel(r6)     // Catch:{ Throwable -> 0x0064 }
            if (r2 != 0) goto L_0x001a
            java.io.InputStream r2 = java.nio.channels.Channels.newInputStream(r7)     // Catch:{ Throwable -> 0x0064 }
            java.io.Closeable r2 = r0.track(r2)     // Catch:{ Throwable -> 0x0064 }
            java.io.InputStream r2 = (java.io.InputStream) r2     // Catch:{ Throwable -> 0x0064 }
            r5.expand((java.lang.String) r6, (java.io.InputStream) r2, (java.io.File) r8)     // Catch:{ Throwable -> 0x0064 }
            goto L_0x0047
        L_0x001a:
            java.lang.String r2 = "zip"
            boolean r2 = r2.equalsIgnoreCase(r6)     // Catch:{ Throwable -> 0x0064 }
            if (r2 == 0) goto L_0x0031
            org.apache.commons.compress.archivers.zip.ZipFile r2 = new org.apache.commons.compress.archivers.zip.ZipFile     // Catch:{ Throwable -> 0x0064 }
            r2.<init>((java.nio.channels.SeekableByteChannel) r7)     // Catch:{ Throwable -> 0x0064 }
            java.io.Closeable r2 = r0.track(r2)     // Catch:{ Throwable -> 0x0064 }
            org.apache.commons.compress.archivers.zip.ZipFile r2 = (org.apache.commons.compress.archivers.zip.ZipFile) r2     // Catch:{ Throwable -> 0x0064 }
            r5.expand((org.apache.commons.compress.archivers.zip.ZipFile) r2, (java.io.File) r8)     // Catch:{ Throwable -> 0x0064 }
            goto L_0x0047
        L_0x0031:
            java.lang.String r2 = "7z"
            boolean r2 = r2.equalsIgnoreCase(r6)     // Catch:{ Throwable -> 0x0064 }
            if (r2 == 0) goto L_0x004b
            org.apache.commons.compress.archivers.sevenz.SevenZFile r2 = new org.apache.commons.compress.archivers.sevenz.SevenZFile     // Catch:{ Throwable -> 0x0064 }
            r2.<init>((java.nio.channels.SeekableByteChannel) r7)     // Catch:{ Throwable -> 0x0064 }
            java.io.Closeable r2 = r0.track(r2)     // Catch:{ Throwable -> 0x0064 }
            org.apache.commons.compress.archivers.sevenz.SevenZFile r2 = (org.apache.commons.compress.archivers.sevenz.SevenZFile) r2     // Catch:{ Throwable -> 0x0064 }
            r5.expand((org.apache.commons.compress.archivers.sevenz.SevenZFile) r2, (java.io.File) r8)     // Catch:{ Throwable -> 0x0064 }
        L_0x0047:
            r0.close()
            return
        L_0x004b:
            org.apache.commons.compress.archivers.ArchiveException r2 = new org.apache.commons.compress.archivers.ArchiveException     // Catch:{ Throwable -> 0x0064 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0064 }
            r3.<init>()     // Catch:{ Throwable -> 0x0064 }
            java.lang.String r4 = "Don't know how to handle format "
            r3.append(r4)     // Catch:{ Throwable -> 0x0064 }
            r3.append(r6)     // Catch:{ Throwable -> 0x0064 }
            java.lang.String r3 = r3.toString()     // Catch:{ Throwable -> 0x0064 }
            r2.<init>(r3)     // Catch:{ Throwable -> 0x0064 }
            throw r2     // Catch:{ Throwable -> 0x0064 }
        L_0x0062:
            r2 = move-exception
            goto L_0x0066
        L_0x0064:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0062 }
        L_0x0066:
            if (r1 == 0) goto L_0x0071
            r0.close()     // Catch:{ Throwable -> 0x006c }
            goto L_0x0074
        L_0x006c:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x0074
        L_0x0071:
            r0.close()
        L_0x0074:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.examples.Expander.expand(java.lang.String, java.nio.channels.SeekableByteChannel, java.io.File, org.apache.commons.compress.archivers.examples.CloseableConsumer):void");
    }

    public void expand(final ArchiveInputStream archive, File targetDirectory) throws IOException, ArchiveException {
        expand((ArchiveEntrySupplier) new ArchiveEntrySupplier() {
            public ArchiveEntry getNextReadableEntry() throws IOException {
                ArchiveEntry next = archive.getNextEntry();
                while (next != null && !archive.canReadEntryData(next)) {
                    next = archive.getNextEntry();
                }
                return next;
            }
        }, (EntryWriter) new EntryWriter() {
            public void writeEntryDataTo(ArchiveEntry entry, OutputStream out) throws IOException {
                IOUtils.copy(archive, out);
            }
        }, targetDirectory);
    }

    public void expand(final ZipFile archive, File targetDirectory) throws IOException, ArchiveException {
        final Enumeration<ZipArchiveEntry> entries = archive.getEntries();
        expand((ArchiveEntrySupplier) new ArchiveEntrySupplier() {
            public ArchiveEntry getNextReadableEntry() throws IOException {
                ZipArchiveEntry next = entries.hasMoreElements() ? (ZipArchiveEntry) entries.nextElement() : null;
                while (next != null && !archive.canReadEntryData(next)) {
                    next = entries.hasMoreElements() ? (ZipArchiveEntry) entries.nextElement() : null;
                }
                return next;
            }
        }, (EntryWriter) new EntryWriter() {
            /* JADX WARNING: Code restructure failed: missing block: B:10:0x0017, code lost:
                r1 = th;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:5:0x0012, code lost:
                r1 = th;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:6:0x0013, code lost:
                r2 = null;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void writeEntryDataTo(org.apache.commons.compress.archivers.ArchiveEntry r5, java.io.OutputStream r6) throws java.io.IOException {
                /*
                    r4 = this;
                    org.apache.commons.compress.archivers.zip.ZipFile r0 = r4
                    r1 = r5
                    org.apache.commons.compress.archivers.zip.ZipArchiveEntry r1 = (org.apache.commons.compress.archivers.zip.ZipArchiveEntry) r1
                    java.io.InputStream r0 = r0.getInputStream(r1)
                    org.apache.commons.compress.utils.IOUtils.copy(r0, r6)     // Catch:{ Throwable -> 0x0015, all -> 0x0012 }
                    if (r0 == 0) goto L_0x0011
                    r0.close()
                L_0x0011:
                    return
                L_0x0012:
                    r1 = move-exception
                    r2 = 0
                    goto L_0x0018
                L_0x0015:
                    r2 = move-exception
                    throw r2     // Catch:{ all -> 0x0017 }
                L_0x0017:
                    r1 = move-exception
                L_0x0018:
                    if (r0 == 0) goto L_0x0028
                    if (r2 == 0) goto L_0x0025
                    r0.close()     // Catch:{ Throwable -> 0x0020 }
                    goto L_0x0028
                L_0x0020:
                    r3 = move-exception
                    r2.addSuppressed(r3)
                    goto L_0x0028
                L_0x0025:
                    r0.close()
                L_0x0028:
                    throw r1
                */
                throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.examples.Expander.AnonymousClass4.writeEntryDataTo(org.apache.commons.compress.archivers.ArchiveEntry, java.io.OutputStream):void");
            }
        }, targetDirectory);
    }

    public void expand(final SevenZFile archive, File targetDirectory) throws IOException, ArchiveException {
        expand((ArchiveEntrySupplier) new ArchiveEntrySupplier() {
            public ArchiveEntry getNextReadableEntry() throws IOException {
                return archive.getNextEntry();
            }
        }, (EntryWriter) new EntryWriter() {
            public void writeEntryDataTo(ArchiveEntry entry, OutputStream out) throws IOException {
                byte[] buffer = new byte[8024];
                while (true) {
                    int read = archive.read(buffer);
                    int n = read;
                    if (-1 != read) {
                        out.write(buffer, 0, n);
                    } else {
                        return;
                    }
                }
            }
        }, targetDirectory);
    }

    private boolean prefersSeekableByteChannel(String format) {
        return ArchiveStreamFactory.ZIP.equalsIgnoreCase(format) || ArchiveStreamFactory.SEVEN_Z.equalsIgnoreCase(format);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x009f, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00a3, code lost:
        if (r4 != null) goto L_0x00a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00a5, code lost:
        if (r5 != null) goto L_0x00a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00ab, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00ac, code lost:
        r5.addSuppressed(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00b0, code lost:
        r4.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void expand(org.apache.commons.compress.archivers.examples.Expander.ArchiveEntrySupplier r9, org.apache.commons.compress.archivers.examples.Expander.EntryWriter r10, java.io.File r11) throws java.io.IOException {
        /*
            r8 = this;
            java.lang.String r0 = r11.getCanonicalPath()
            java.lang.String r1 = java.io.File.separator
            boolean r1 = r0.endsWith(r1)
            if (r1 != 0) goto L_0x001d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r2 = java.io.File.separator
            r1.append(r2)
            java.lang.String r0 = r1.toString()
        L_0x001d:
            org.apache.commons.compress.archivers.ArchiveEntry r1 = r9.getNextReadableEntry()
        L_0x0021:
            if (r1 == 0) goto L_0x00d7
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r1.getName()
            r2.<init>(r11, r3)
            java.lang.String r3 = r2.getCanonicalPath()
            boolean r3 = r3.startsWith(r0)
            if (r3 == 0) goto L_0x00b4
            boolean r3 = r1.isDirectory()
            java.lang.String r4 = "Failed to create directory "
            if (r3 == 0) goto L_0x0060
            boolean r3 = r2.isDirectory()
            if (r3 != 0) goto L_0x009a
            boolean r3 = r2.mkdirs()
            if (r3 == 0) goto L_0x004b
            goto L_0x009a
        L_0x004b:
            java.io.IOException r3 = new java.io.IOException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            r5.append(r2)
            java.lang.String r4 = r5.toString()
            r3.<init>(r4)
            throw r3
        L_0x0060:
            java.io.File r3 = r2.getParentFile()
            boolean r5 = r3.isDirectory()
            if (r5 != 0) goto L_0x0086
            boolean r5 = r3.mkdirs()
            if (r5 == 0) goto L_0x0071
            goto L_0x0086
        L_0x0071:
            java.io.IOException r5 = new java.io.IOException
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r4)
            r6.append(r3)
            java.lang.String r4 = r6.toString()
            r5.<init>(r4)
            throw r5
        L_0x0086:
            java.nio.file.Path r4 = r2.toPath()
            r5 = 0
            java.nio.file.OpenOption[] r5 = new java.nio.file.OpenOption[r5]
            java.io.OutputStream r4 = java.nio.file.Files.newOutputStream(r4, r5)
            r5 = 0
            r10.writeEntryDataTo(r1, r4)     // Catch:{ Throwable -> 0x00a1 }
            if (r4 == 0) goto L_0x009a
            r4.close()
        L_0x009a:
            org.apache.commons.compress.archivers.ArchiveEntry r1 = r9.getNextReadableEntry()
            goto L_0x0021
        L_0x009f:
            r6 = move-exception
            goto L_0x00a3
        L_0x00a1:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x009f }
        L_0x00a3:
            if (r4 == 0) goto L_0x00b3
            if (r5 == 0) goto L_0x00b0
            r4.close()     // Catch:{ Throwable -> 0x00ab }
            goto L_0x00b3
        L_0x00ab:
            r7 = move-exception
            r5.addSuppressed(r7)
            goto L_0x00b3
        L_0x00b0:
            r4.close()
        L_0x00b3:
            throw r6
        L_0x00b4:
            java.io.IOException r3 = new java.io.IOException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Expanding "
            r4.append(r5)
            java.lang.String r5 = r1.getName()
            r4.append(r5)
            java.lang.String r5 = " would create file outside of "
            r4.append(r5)
            r4.append(r11)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        L_0x00d7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.examples.Expander.expand(org.apache.commons.compress.archivers.examples.Expander$ArchiveEntrySupplier, org.apache.commons.compress.archivers.examples.Expander$EntryWriter, java.io.File):void");
    }
}

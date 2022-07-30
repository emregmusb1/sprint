package org.apache.commons.compress.archivers.examples;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

public class Archiver {

    private interface ArchiveEntryConsumer {
        void accept(File file, ArchiveEntry archiveEntry) throws IOException;
    }

    private interface ArchiveEntryCreator {
        ArchiveEntry create(File file, String str) throws IOException;
    }

    private interface Finisher {
        void finish() throws IOException;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0030, code lost:
        if (r0 != null) goto L_0x0032;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0032, code lost:
        if (r1 != null) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0038, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0039, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003d, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0056, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x005a, code lost:
        if (r0 != null) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x005c, code lost:
        if (r1 != null) goto L_0x005e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0062, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0063, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0067, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x002c, code lost:
        r2 = move-exception;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void create(java.lang.String r6, java.io.File r7, java.io.File r8) throws java.io.IOException, org.apache.commons.compress.archivers.ArchiveException {
        /*
            r5 = this;
            boolean r0 = r5.prefersSeekableByteChannel(r6)
            r1 = 0
            r2 = 0
            if (r0 == 0) goto L_0x0041
            java.nio.file.Path r0 = r7.toPath()
            r3 = 3
            java.nio.file.OpenOption[] r3 = new java.nio.file.OpenOption[r3]
            java.nio.file.StandardOpenOption r4 = java.nio.file.StandardOpenOption.WRITE
            r3[r2] = r4
            r2 = 1
            java.nio.file.StandardOpenOption r4 = java.nio.file.StandardOpenOption.CREATE
            r3[r2] = r4
            r2 = 2
            java.nio.file.StandardOpenOption r4 = java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
            r3[r2] = r4
            java.nio.channels.FileChannel r0 = java.nio.channels.FileChannel.open(r0, r3)
            org.apache.commons.compress.archivers.examples.CloseableConsumer r2 = org.apache.commons.compress.archivers.examples.CloseableConsumer.CLOSING_CONSUMER     // Catch:{ Throwable -> 0x002e }
            r5.create((java.lang.String) r6, (java.nio.channels.SeekableByteChannel) r0, (java.io.File) r8, (org.apache.commons.compress.archivers.examples.CloseableConsumer) r2)     // Catch:{ Throwable -> 0x002e }
            if (r0 == 0) goto L_0x002b
            r0.close()
        L_0x002b:
            return
        L_0x002c:
            r2 = move-exception
            goto L_0x0030
        L_0x002e:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x002c }
        L_0x0030:
            if (r0 == 0) goto L_0x0040
            if (r1 == 0) goto L_0x003d
            r0.close()     // Catch:{ Throwable -> 0x0038 }
            goto L_0x0040
        L_0x0038:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x0040
        L_0x003d:
            r0.close()
        L_0x0040:
            throw r2
        L_0x0041:
            java.nio.file.Path r0 = r7.toPath()
            java.nio.file.OpenOption[] r2 = new java.nio.file.OpenOption[r2]
            java.io.OutputStream r0 = java.nio.file.Files.newOutputStream(r0, r2)
            org.apache.commons.compress.archivers.examples.CloseableConsumer r2 = org.apache.commons.compress.archivers.examples.CloseableConsumer.CLOSING_CONSUMER     // Catch:{ Throwable -> 0x0058 }
            r5.create((java.lang.String) r6, (java.io.OutputStream) r0, (java.io.File) r8, (org.apache.commons.compress.archivers.examples.CloseableConsumer) r2)     // Catch:{ Throwable -> 0x0058 }
            if (r0 == 0) goto L_0x0055
            r0.close()
        L_0x0055:
            return
        L_0x0056:
            r2 = move-exception
            goto L_0x005a
        L_0x0058:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0056 }
        L_0x005a:
            if (r0 == 0) goto L_0x006a
            if (r1 == 0) goto L_0x0067
            r0.close()     // Catch:{ Throwable -> 0x0062 }
            goto L_0x006a
        L_0x0062:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x006a
        L_0x0067:
            r0.close()
        L_0x006a:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.examples.Archiver.create(java.lang.String, java.io.File, java.io.File):void");
    }

    @Deprecated
    public void create(String format, OutputStream target, File directory) throws IOException, ArchiveException {
        create(format, target, directory, CloseableConsumer.NULL_CONSUMER);
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
    public void create(java.lang.String r5, java.io.OutputStream r6, java.io.File r7, org.apache.commons.compress.archivers.examples.CloseableConsumer r8) throws java.io.IOException, org.apache.commons.compress.archivers.ArchiveException {
        /*
            r4 = this;
            org.apache.commons.compress.archivers.examples.CloseableConsumerAdapter r0 = new org.apache.commons.compress.archivers.examples.CloseableConsumerAdapter
            r0.<init>(r8)
            r1 = 0
            org.apache.commons.compress.archivers.ArchiveStreamFactory r2 = new org.apache.commons.compress.archivers.ArchiveStreamFactory     // Catch:{ Throwable -> 0x001e }
            r2.<init>()     // Catch:{ Throwable -> 0x001e }
            org.apache.commons.compress.archivers.ArchiveOutputStream r2 = r2.createArchiveOutputStream(r5, r6)     // Catch:{ Throwable -> 0x001e }
            java.io.Closeable r2 = r0.track(r2)     // Catch:{ Throwable -> 0x001e }
            org.apache.commons.compress.archivers.ArchiveOutputStream r2 = (org.apache.commons.compress.archivers.ArchiveOutputStream) r2     // Catch:{ Throwable -> 0x001e }
            r4.create((org.apache.commons.compress.archivers.ArchiveOutputStream) r2, (java.io.File) r7)     // Catch:{ Throwable -> 0x001e }
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.examples.Archiver.create(java.lang.String, java.io.OutputStream, java.io.File, org.apache.commons.compress.archivers.examples.CloseableConsumer):void");
    }

    @Deprecated
    public void create(String format, SeekableByteChannel target, File directory) throws IOException, ArchiveException {
        create(format, target, directory, CloseableConsumer.NULL_CONSUMER);
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
    public void create(java.lang.String r6, java.nio.channels.SeekableByteChannel r7, java.io.File r8, org.apache.commons.compress.archivers.examples.CloseableConsumer r9) throws java.io.IOException, org.apache.commons.compress.archivers.ArchiveException {
        /*
            r5 = this;
            org.apache.commons.compress.archivers.examples.CloseableConsumerAdapter r0 = new org.apache.commons.compress.archivers.examples.CloseableConsumerAdapter
            r0.<init>(r9)
            r1 = 0
            boolean r2 = r5.prefersSeekableByteChannel(r6)     // Catch:{ Throwable -> 0x0064 }
            if (r2 != 0) goto L_0x001a
            java.io.OutputStream r2 = java.nio.channels.Channels.newOutputStream(r7)     // Catch:{ Throwable -> 0x0064 }
            java.io.Closeable r2 = r0.track(r2)     // Catch:{ Throwable -> 0x0064 }
            java.io.OutputStream r2 = (java.io.OutputStream) r2     // Catch:{ Throwable -> 0x0064 }
            r5.create((java.lang.String) r6, (java.io.OutputStream) r2, (java.io.File) r8)     // Catch:{ Throwable -> 0x0064 }
            goto L_0x0047
        L_0x001a:
            java.lang.String r2 = "zip"
            boolean r2 = r2.equalsIgnoreCase(r6)     // Catch:{ Throwable -> 0x0064 }
            if (r2 == 0) goto L_0x0031
            org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream r2 = new org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream     // Catch:{ Throwable -> 0x0064 }
            r2.<init>((java.nio.channels.SeekableByteChannel) r7)     // Catch:{ Throwable -> 0x0064 }
            java.io.Closeable r2 = r0.track(r2)     // Catch:{ Throwable -> 0x0064 }
            org.apache.commons.compress.archivers.ArchiveOutputStream r2 = (org.apache.commons.compress.archivers.ArchiveOutputStream) r2     // Catch:{ Throwable -> 0x0064 }
            r5.create((org.apache.commons.compress.archivers.ArchiveOutputStream) r2, (java.io.File) r8)     // Catch:{ Throwable -> 0x0064 }
            goto L_0x0047
        L_0x0031:
            java.lang.String r2 = "7z"
            boolean r2 = r2.equalsIgnoreCase(r6)     // Catch:{ Throwable -> 0x0064 }
            if (r2 == 0) goto L_0x004b
            org.apache.commons.compress.archivers.sevenz.SevenZOutputFile r2 = new org.apache.commons.compress.archivers.sevenz.SevenZOutputFile     // Catch:{ Throwable -> 0x0064 }
            r2.<init>((java.nio.channels.SeekableByteChannel) r7)     // Catch:{ Throwable -> 0x0064 }
            java.io.Closeable r2 = r0.track(r2)     // Catch:{ Throwable -> 0x0064 }
            org.apache.commons.compress.archivers.sevenz.SevenZOutputFile r2 = (org.apache.commons.compress.archivers.sevenz.SevenZOutputFile) r2     // Catch:{ Throwable -> 0x0064 }
            r5.create((org.apache.commons.compress.archivers.sevenz.SevenZOutputFile) r2, (java.io.File) r8)     // Catch:{ Throwable -> 0x0064 }
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.examples.Archiver.create(java.lang.String, java.nio.channels.SeekableByteChannel, java.io.File, org.apache.commons.compress.archivers.examples.CloseableConsumer):void");
    }

    public void create(final ArchiveOutputStream target, File directory) throws IOException, ArchiveException {
        create(directory, (ArchiveEntryCreator) new ArchiveEntryCreator() {
            public ArchiveEntry create(File f, String entryName) throws IOException {
                return target.createArchiveEntry(f, entryName);
            }
        }, (ArchiveEntryConsumer) new ArchiveEntryConsumer() {
            /* JADX WARNING: Code restructure failed: missing block: B:10:0x0029, code lost:
                if (r1 != null) goto L_0x002b;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
                r0.close();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:13:0x002f, code lost:
                r3 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:14:0x0030, code lost:
                r1.addSuppressed(r3);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:15:0x0034, code lost:
                r0.close();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:6:0x0025, code lost:
                r2 = move-exception;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void accept(java.io.File r5, org.apache.commons.compress.archivers.ArchiveEntry r6) throws java.io.IOException {
                /*
                    r4 = this;
                    org.apache.commons.compress.archivers.ArchiveOutputStream r0 = r4
                    r0.putArchiveEntry(r6)
                    boolean r0 = r6.isDirectory()
                    if (r0 != 0) goto L_0x0038
                    java.io.BufferedInputStream r0 = new java.io.BufferedInputStream
                    java.nio.file.Path r1 = r5.toPath()
                    r2 = 0
                    java.nio.file.OpenOption[] r2 = new java.nio.file.OpenOption[r2]
                    java.io.InputStream r1 = java.nio.file.Files.newInputStream(r1, r2)
                    r0.<init>(r1)
                    r1 = 0
                    org.apache.commons.compress.archivers.ArchiveOutputStream r2 = r4     // Catch:{ Throwable -> 0x0027 }
                    org.apache.commons.compress.utils.IOUtils.copy(r0, r2)     // Catch:{ Throwable -> 0x0027 }
                    r0.close()
                    goto L_0x0038
                L_0x0025:
                    r2 = move-exception
                    goto L_0x0029
                L_0x0027:
                    r1 = move-exception
                    throw r1     // Catch:{ all -> 0x0025 }
                L_0x0029:
                    if (r1 == 0) goto L_0x0034
                    r0.close()     // Catch:{ Throwable -> 0x002f }
                    goto L_0x0037
                L_0x002f:
                    r3 = move-exception
                    r1.addSuppressed(r3)
                    goto L_0x0037
                L_0x0034:
                    r0.close()
                L_0x0037:
                    throw r2
                L_0x0038:
                    org.apache.commons.compress.archivers.ArchiveOutputStream r0 = r4
                    r0.closeArchiveEntry()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.examples.Archiver.AnonymousClass2.accept(java.io.File, org.apache.commons.compress.archivers.ArchiveEntry):void");
            }
        }, (Finisher) new Finisher() {
            public void finish() throws IOException {
                target.finish();
            }
        });
    }

    public void create(final SevenZOutputFile target, File directory) throws IOException {
        create(directory, (ArchiveEntryCreator) new ArchiveEntryCreator() {
            public ArchiveEntry create(File f, String entryName) throws IOException {
                return target.createArchiveEntry(f, entryName);
            }
        }, (ArchiveEntryConsumer) new ArchiveEntryConsumer() {
            /* JADX WARNING: Code restructure failed: missing block: B:10:0x0037, code lost:
                r6 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:14:0x003b, code lost:
                if (r5 != null) goto L_0x003d;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
                r4.close();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:17:0x0041, code lost:
                r7 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:18:0x0042, code lost:
                r5.addSuppressed(r7);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:19:0x0046, code lost:
                r4.close();
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void accept(java.io.File r10, org.apache.commons.compress.archivers.ArchiveEntry r11) throws java.io.IOException {
                /*
                    r9 = this;
                    org.apache.commons.compress.archivers.sevenz.SevenZOutputFile r0 = r4
                    r0.putArchiveEntry(r11)
                    boolean r0 = r11.isDirectory()
                    if (r0 != 0) goto L_0x004a
                    r0 = 8024(0x1f58, float:1.1244E-41)
                    byte[] r0 = new byte[r0]
                    r1 = 0
                    r2 = 0
                    java.io.BufferedInputStream r4 = new java.io.BufferedInputStream
                    java.nio.file.Path r5 = r10.toPath()
                    r6 = 0
                    java.nio.file.OpenOption[] r7 = new java.nio.file.OpenOption[r6]
                    java.io.InputStream r5 = java.nio.file.Files.newInputStream(r5, r7)
                    r4.<init>(r5)
                    r5 = 0
                L_0x0023:
                    r7 = -1
                    int r8 = r4.read(r0)     // Catch:{ Throwable -> 0x0039 }
                    r1 = r8
                    if (r7 == r8) goto L_0x0033
                    org.apache.commons.compress.archivers.sevenz.SevenZOutputFile r7 = r4     // Catch:{ Throwable -> 0x0039 }
                    r7.write(r0, r6, r1)     // Catch:{ Throwable -> 0x0039 }
                    long r7 = (long) r1
                    long r2 = r2 + r7
                    goto L_0x0023
                L_0x0033:
                    r4.close()
                    goto L_0x004a
                L_0x0037:
                    r6 = move-exception
                    goto L_0x003b
                L_0x0039:
                    r5 = move-exception
                    throw r5     // Catch:{ all -> 0x0037 }
                L_0x003b:
                    if (r5 == 0) goto L_0x0046
                    r4.close()     // Catch:{ Throwable -> 0x0041 }
                    goto L_0x0049
                L_0x0041:
                    r7 = move-exception
                    r5.addSuppressed(r7)
                    goto L_0x0049
                L_0x0046:
                    r4.close()
                L_0x0049:
                    throw r6
                L_0x004a:
                    org.apache.commons.compress.archivers.sevenz.SevenZOutputFile r0 = r4
                    r0.closeArchiveEntry()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.examples.Archiver.AnonymousClass5.accept(java.io.File, org.apache.commons.compress.archivers.ArchiveEntry):void");
            }
        }, (Finisher) new Finisher() {
            public void finish() throws IOException {
                target.finish();
            }
        });
    }

    private boolean prefersSeekableByteChannel(String format) {
        return ArchiveStreamFactory.ZIP.equalsIgnoreCase(format) || ArchiveStreamFactory.SEVEN_Z.equalsIgnoreCase(format);
    }

    private void create(File directory, ArchiveEntryCreator creator, ArchiveEntryConsumer consumer, Finisher finisher) throws IOException {
        create("", directory, creator, consumer);
        finisher.finish();
    }

    private void create(String prefix, File directory, ArchiveEntryCreator creator, ArchiveEntryConsumer consumer) throws IOException {
        File[] children = directory.listFiles();
        if (children != null) {
            for (File f : children) {
                StringBuilder sb = new StringBuilder();
                sb.append(prefix);
                sb.append(f.getName());
                sb.append(f.isDirectory() ? "/" : "");
                String entryName = sb.toString();
                consumer.accept(f, creator.create(f, entryName));
                if (f.isDirectory()) {
                    create(entryName, f, creator, consumer);
                }
            }
        }
    }
}

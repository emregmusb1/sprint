package org.apache.commons.compress.archivers;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

public final class Lister {
    private static final ArchiveStreamFactory factory = new ArchiveStreamFactory();

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            usage();
            return;
        }
        PrintStream printStream = System.out;
        printStream.println("Analysing " + args[0]);
        File f = new File(args[0]);
        if (!f.isFile()) {
            PrintStream printStream2 = System.err;
            printStream2.println(f + " doesn't exist or is a directory");
        }
        String format = args.length > 1 ? args[1] : detectFormat(f);
        if (ArchiveStreamFactory.SEVEN_Z.equalsIgnoreCase(format)) {
            list7z(f);
        } else if ("zipfile".equals(format)) {
            listZipUsingZipFile(f);
        } else {
            listStream(f, args);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x004a, code lost:
        r3 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x004b, code lost:
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004f, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0050, code lost:
        r6 = r4;
        r4 = r3;
        r3 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0064, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0068, code lost:
        if (r1 != null) goto L_0x006a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x006e, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x006f, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0073, code lost:
        r0.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void listStream(java.io.File r7, java.lang.String[] r8) throws org.apache.commons.compress.archivers.ArchiveException, java.io.IOException {
        /*
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream
            java.nio.file.Path r1 = r7.toPath()
            r2 = 0
            java.nio.file.OpenOption[] r2 = new java.nio.file.OpenOption[r2]
            java.io.InputStream r1 = java.nio.file.Files.newInputStream(r1, r2)
            r0.<init>(r1)
            r1 = 0
            org.apache.commons.compress.archivers.ArchiveInputStream r2 = createArchiveInputStream(r8, r0)     // Catch:{ Throwable -> 0x0066 }
            java.io.PrintStream r3 = java.lang.System.out     // Catch:{ Throwable -> 0x004d, all -> 0x004a }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x004d, all -> 0x004a }
            r4.<init>()     // Catch:{ Throwable -> 0x004d, all -> 0x004a }
            java.lang.String r5 = "Created "
            r4.append(r5)     // Catch:{ Throwable -> 0x004d, all -> 0x004a }
            java.lang.String r5 = r2.toString()     // Catch:{ Throwable -> 0x004d, all -> 0x004a }
            r4.append(r5)     // Catch:{ Throwable -> 0x004d, all -> 0x004a }
            java.lang.String r4 = r4.toString()     // Catch:{ Throwable -> 0x004d, all -> 0x004a }
            r3.println(r4)     // Catch:{ Throwable -> 0x004d, all -> 0x004a }
        L_0x0030:
            org.apache.commons.compress.archivers.ArchiveEntry r3 = r2.getNextEntry()     // Catch:{ Throwable -> 0x004d, all -> 0x004a }
            r4 = r3
            if (r3 == 0) goto L_0x0041
            java.io.PrintStream r3 = java.lang.System.out     // Catch:{ Throwable -> 0x004d, all -> 0x004a }
            java.lang.String r5 = r4.getName()     // Catch:{ Throwable -> 0x004d, all -> 0x004a }
            r3.println(r5)     // Catch:{ Throwable -> 0x004d, all -> 0x004a }
            goto L_0x0030
        L_0x0041:
            if (r2 == 0) goto L_0x0046
            r2.close()     // Catch:{ Throwable -> 0x0066 }
        L_0x0046:
            r0.close()
            return
        L_0x004a:
            r3 = move-exception
            r4 = r1
            goto L_0x0053
        L_0x004d:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x004f }
        L_0x004f:
            r4 = move-exception
            r6 = r4
            r4 = r3
            r3 = r6
        L_0x0053:
            if (r2 == 0) goto L_0x0063
            if (r4 == 0) goto L_0x0060
            r2.close()     // Catch:{ Throwable -> 0x005b }
            goto L_0x0063
        L_0x005b:
            r5 = move-exception
            r4.addSuppressed(r5)     // Catch:{ Throwable -> 0x0066 }
            goto L_0x0063
        L_0x0060:
            r2.close()     // Catch:{ Throwable -> 0x0066 }
        L_0x0063:
            throw r3     // Catch:{ Throwable -> 0x0066 }
        L_0x0064:
            r2 = move-exception
            goto L_0x0068
        L_0x0066:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0064 }
        L_0x0068:
            if (r1 == 0) goto L_0x0073
            r0.close()     // Catch:{ Throwable -> 0x006e }
            goto L_0x0076
        L_0x006e:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x0076
        L_0x0073:
            r0.close()
        L_0x0076:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.Lister.listStream(java.io.File, java.lang.String[]):void");
    }

    private static ArchiveInputStream createArchiveInputStream(String[] args, InputStream fis) throws ArchiveException {
        if (args.length > 1) {
            return factory.createArchiveInputStream(args[1], fis);
        }
        return factory.createArchiveInputStream(fis);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0025, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0026, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002a, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x001b, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001f, code lost:
        if (r1 != null) goto L_0x0021;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String detectFormat(java.io.File r4) throws org.apache.commons.compress.archivers.ArchiveException, java.io.IOException {
        /*
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream
            java.nio.file.Path r1 = r4.toPath()
            r2 = 0
            java.nio.file.OpenOption[] r2 = new java.nio.file.OpenOption[r2]
            java.io.InputStream r1 = java.nio.file.Files.newInputStream(r1, r2)
            r0.<init>(r1)
            r1 = 0
            org.apache.commons.compress.archivers.ArchiveStreamFactory r2 = factory     // Catch:{ Throwable -> 0x001d }
            java.lang.String r1 = org.apache.commons.compress.archivers.ArchiveStreamFactory.detect(r0)     // Catch:{ Throwable -> 0x001d }
            r0.close()
            return r1
        L_0x001b:
            r2 = move-exception
            goto L_0x001f
        L_0x001d:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001b }
        L_0x001f:
            if (r1 == 0) goto L_0x002a
            r0.close()     // Catch:{ Throwable -> 0x0025 }
            goto L_0x002d
        L_0x0025:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x002d
        L_0x002a:
            r0.close()
        L_0x002d:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.Lister.detectFormat(java.io.File):java.lang.String");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0052, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0056, code lost:
        if (r1 != null) goto L_0x0058;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x005c, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005d, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0061, code lost:
        r0.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void list7z(java.io.File r5) throws org.apache.commons.compress.archivers.ArchiveException, java.io.IOException {
        /*
            org.apache.commons.compress.archivers.sevenz.SevenZFile r0 = new org.apache.commons.compress.archivers.sevenz.SevenZFile
            r0.<init>((java.io.File) r5)
            r1 = 0
            java.io.PrintStream r2 = java.lang.System.out     // Catch:{ Throwable -> 0x0054 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0054 }
            r3.<init>()     // Catch:{ Throwable -> 0x0054 }
            java.lang.String r4 = "Created "
            r3.append(r4)     // Catch:{ Throwable -> 0x0054 }
            java.lang.String r4 = r0.toString()     // Catch:{ Throwable -> 0x0054 }
            r3.append(r4)     // Catch:{ Throwable -> 0x0054 }
            java.lang.String r3 = r3.toString()     // Catch:{ Throwable -> 0x0054 }
            r2.println(r3)     // Catch:{ Throwable -> 0x0054 }
        L_0x0020:
            org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry r2 = r0.getNextEntry()     // Catch:{ Throwable -> 0x0054 }
            r3 = r2
            if (r2 == 0) goto L_0x004e
            java.lang.String r2 = r3.getName()     // Catch:{ Throwable -> 0x0054 }
            if (r2 != 0) goto L_0x0043
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0054 }
            r2.<init>()     // Catch:{ Throwable -> 0x0054 }
            java.lang.String r4 = r0.getDefaultName()     // Catch:{ Throwable -> 0x0054 }
            r2.append(r4)     // Catch:{ Throwable -> 0x0054 }
            java.lang.String r4 = " (entry name was null)"
            r2.append(r4)     // Catch:{ Throwable -> 0x0054 }
            java.lang.String r2 = r2.toString()     // Catch:{ Throwable -> 0x0054 }
            goto L_0x0047
        L_0x0043:
            java.lang.String r2 = r3.getName()     // Catch:{ Throwable -> 0x0054 }
        L_0x0047:
            java.io.PrintStream r4 = java.lang.System.out     // Catch:{ Throwable -> 0x0054 }
            r4.println(r2)     // Catch:{ Throwable -> 0x0054 }
            goto L_0x0020
        L_0x004e:
            r0.close()
            return
        L_0x0052:
            r2 = move-exception
            goto L_0x0056
        L_0x0054:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0052 }
        L_0x0056:
            if (r1 == 0) goto L_0x0061
            r0.close()     // Catch:{ Throwable -> 0x005c }
            goto L_0x0064
        L_0x005c:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x0064
        L_0x0061:
            r0.close()
        L_0x0064:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.Lister.list7z(java.io.File):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0042, code lost:
        if (r1 != null) goto L_0x0044;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0048, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0049, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004d, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x003e, code lost:
        r2 = move-exception;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void listZipUsingZipFile(java.io.File r5) throws org.apache.commons.compress.archivers.ArchiveException, java.io.IOException {
        /*
            org.apache.commons.compress.archivers.zip.ZipFile r0 = new org.apache.commons.compress.archivers.zip.ZipFile
            r0.<init>((java.io.File) r5)
            r1 = 0
            java.io.PrintStream r2 = java.lang.System.out     // Catch:{ Throwable -> 0x0040 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0040 }
            r3.<init>()     // Catch:{ Throwable -> 0x0040 }
            java.lang.String r4 = "Created "
            r3.append(r4)     // Catch:{ Throwable -> 0x0040 }
            java.lang.String r4 = r0.toString()     // Catch:{ Throwable -> 0x0040 }
            r3.append(r4)     // Catch:{ Throwable -> 0x0040 }
            java.lang.String r3 = r3.toString()     // Catch:{ Throwable -> 0x0040 }
            r2.println(r3)     // Catch:{ Throwable -> 0x0040 }
            java.util.Enumeration r2 = r0.getEntries()     // Catch:{ Throwable -> 0x0040 }
        L_0x0024:
            boolean r3 = r2.hasMoreElements()     // Catch:{ Throwable -> 0x0040 }
            if (r3 == 0) goto L_0x003a
            java.io.PrintStream r3 = java.lang.System.out     // Catch:{ Throwable -> 0x0040 }
            java.lang.Object r4 = r2.nextElement()     // Catch:{ Throwable -> 0x0040 }
            org.apache.commons.compress.archivers.zip.ZipArchiveEntry r4 = (org.apache.commons.compress.archivers.zip.ZipArchiveEntry) r4     // Catch:{ Throwable -> 0x0040 }
            java.lang.String r4 = r4.getName()     // Catch:{ Throwable -> 0x0040 }
            r3.println(r4)     // Catch:{ Throwable -> 0x0040 }
            goto L_0x0024
        L_0x003a:
            r0.close()
            return
        L_0x003e:
            r2 = move-exception
            goto L_0x0042
        L_0x0040:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x003e }
        L_0x0042:
            if (r1 == 0) goto L_0x004d
            r0.close()     // Catch:{ Throwable -> 0x0048 }
            goto L_0x0050
        L_0x0048:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x0050
        L_0x004d:
            r0.close()
        L_0x0050:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.Lister.listZipUsingZipFile(java.io.File):void");
    }

    private static void usage() {
        System.out.println("Parameters: archive-name [archive-type]\n");
        System.out.println("the magic archive-type 'zipfile' prefers ZipFile over ZipArchiveInputStream");
    }
}

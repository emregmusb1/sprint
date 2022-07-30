package org.apache.commons.compress.compressors.pack200;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Pack200Utils {
    private Pack200Utils() {
    }

    public static void normalize(File jar) throws IOException {
        normalize(jar, jar, (Map<String, String>) null);
    }

    public static void normalize(File jar, Map<String, String> props) throws IOException {
        normalize(jar, jar, props);
    }

    public static void normalize(File from, File to) throws IOException {
        normalize(from, to, (Map<String, String>) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0068, code lost:
        r4 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0069, code lost:
        r5 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x006d, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x006e, code lost:
        r7 = r5;
        r5 = r4;
        r4 = r7;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void normalize(java.io.File r8, java.io.File r9, java.util.Map<java.lang.String, java.lang.String> r10) throws java.io.IOException {
        /*
            if (r10 != 0) goto L_0x0008
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r10 = r0
        L_0x0008:
            java.lang.String r0 = "pack.segment.limit"
            java.lang.String r1 = "-1"
            r10.put(r0, r1)
            java.lang.String r0 = "commons-compress"
            java.lang.String r1 = "pack200normalize"
            java.io.File r0 = java.io.File.createTempFile(r0, r1)
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ all -> 0x0093 }
            r1.<init>(r0)     // Catch:{ all -> 0x0093 }
            r2 = 0
            java.util.jar.JarFile r3 = new java.util.jar.JarFile     // Catch:{ Throwable -> 0x0082 }
            r3.<init>(r8)     // Catch:{ Throwable -> 0x0082 }
            java.util.jar.Pack200$Packer r4 = java.util.jar.Pack200.newPacker()     // Catch:{ Throwable -> 0x006b, all -> 0x0068 }
            java.util.SortedMap r5 = r4.properties()     // Catch:{ Throwable -> 0x006b, all -> 0x0068 }
            r5.putAll(r10)     // Catch:{ Throwable -> 0x006b, all -> 0x0068 }
            r4.pack(r3, r1)     // Catch:{ Throwable -> 0x006b, all -> 0x0068 }
            r3.close()     // Catch:{ Throwable -> 0x0082 }
            r1.close()     // Catch:{ all -> 0x0093 }
            java.util.jar.Pack200$Unpacker r1 = java.util.jar.Pack200.newUnpacker()     // Catch:{ all -> 0x0093 }
            java.util.jar.JarOutputStream r3 = new java.util.jar.JarOutputStream     // Catch:{ all -> 0x0093 }
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ all -> 0x0093 }
            r4.<init>(r9)     // Catch:{ all -> 0x0093 }
            r3.<init>(r4)     // Catch:{ all -> 0x0093 }
            r1.unpack(r0, r3)     // Catch:{ Throwable -> 0x0057 }
            r3.close()     // Catch:{ all -> 0x0093 }
            boolean r1 = r0.delete()
            if (r1 != 0) goto L_0x0054
            r0.deleteOnExit()
        L_0x0054:
            return
        L_0x0055:
            r4 = move-exception
            goto L_0x0059
        L_0x0057:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0055 }
        L_0x0059:
            if (r2 == 0) goto L_0x0064
            r3.close()     // Catch:{ Throwable -> 0x005f }
            goto L_0x0067
        L_0x005f:
            r5 = move-exception
            r2.addSuppressed(r5)     // Catch:{ all -> 0x0093 }
            goto L_0x0067
        L_0x0064:
            r3.close()     // Catch:{ all -> 0x0093 }
        L_0x0067:
            throw r4     // Catch:{ all -> 0x0093 }
        L_0x0068:
            r4 = move-exception
            r5 = r2
            goto L_0x0071
        L_0x006b:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x006d }
        L_0x006d:
            r5 = move-exception
            r7 = r5
            r5 = r4
            r4 = r7
        L_0x0071:
            if (r5 == 0) goto L_0x007c
            r3.close()     // Catch:{ Throwable -> 0x0077 }
            goto L_0x007f
        L_0x0077:
            r6 = move-exception
            r5.addSuppressed(r6)     // Catch:{ Throwable -> 0x0082 }
            goto L_0x007f
        L_0x007c:
            r3.close()     // Catch:{ Throwable -> 0x0082 }
        L_0x007f:
            throw r4     // Catch:{ Throwable -> 0x0082 }
        L_0x0080:
            r3 = move-exception
            goto L_0x0084
        L_0x0082:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0080 }
        L_0x0084:
            if (r2 == 0) goto L_0x008f
            r1.close()     // Catch:{ Throwable -> 0x008a }
            goto L_0x0092
        L_0x008a:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ all -> 0x0093 }
            goto L_0x0092
        L_0x008f:
            r1.close()     // Catch:{ all -> 0x0093 }
        L_0x0092:
            throw r3     // Catch:{ all -> 0x0093 }
        L_0x0093:
            r1 = move-exception
            boolean r2 = r0.delete()
            if (r2 != 0) goto L_0x009d
            r0.deleteOnExit()
        L_0x009d:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.compressors.pack200.Pack200Utils.normalize(java.io.File, java.io.File, java.util.Map):void");
    }
}

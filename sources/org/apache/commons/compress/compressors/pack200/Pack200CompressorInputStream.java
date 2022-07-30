package org.apache.commons.compress.compressors.pack200;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class Pack200CompressorInputStream extends CompressorInputStream {
    private static final byte[] CAFE_DOOD = {-54, -2, -48, 13};
    private static final int SIG_LENGTH = CAFE_DOOD.length;
    private final InputStream originalInput;
    private final StreamBridge streamBridge;

    public Pack200CompressorInputStream(InputStream in) throws IOException {
        this(in, Pack200Strategy.IN_MEMORY);
    }

    public Pack200CompressorInputStream(InputStream in, Pack200Strategy mode) throws IOException {
        this(in, (File) null, mode, (Map<String, String>) null);
    }

    public Pack200CompressorInputStream(InputStream in, Map<String, String> props) throws IOException {
        this(in, Pack200Strategy.IN_MEMORY, props);
    }

    public Pack200CompressorInputStream(InputStream in, Pack200Strategy mode, Map<String, String> props) throws IOException {
        this(in, (File) null, mode, props);
    }

    public Pack200CompressorInputStream(File f) throws IOException {
        this(f, Pack200Strategy.IN_MEMORY);
    }

    public Pack200CompressorInputStream(File f, Pack200Strategy mode) throws IOException {
        this((InputStream) null, f, mode, (Map<String, String>) null);
    }

    public Pack200CompressorInputStream(File f, Map<String, String> props) throws IOException {
        this(f, Pack200Strategy.IN_MEMORY, props);
    }

    public Pack200CompressorInputStream(File f, Pack200Strategy mode, Map<String, String> props) throws IOException {
        this((InputStream) null, f, mode, props);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0032, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0036, code lost:
        if (r1 != null) goto L_0x0038;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003c, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003d, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0041, code lost:
        r0.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private Pack200CompressorInputStream(java.io.InputStream r5, java.io.File r6, org.apache.commons.compress.compressors.pack200.Pack200Strategy r7, java.util.Map<java.lang.String, java.lang.String> r8) throws java.io.IOException {
        /*
            r4 = this;
            r4.<init>()
            r4.originalInput = r5
            org.apache.commons.compress.compressors.pack200.StreamBridge r0 = r7.newStreamBridge()
            r4.streamBridge = r0
            java.util.jar.JarOutputStream r0 = new java.util.jar.JarOutputStream
            org.apache.commons.compress.compressors.pack200.StreamBridge r1 = r4.streamBridge
            r0.<init>(r1)
            r1 = 0
            java.util.jar.Pack200$Unpacker r2 = java.util.jar.Pack200.newUnpacker()     // Catch:{ Throwable -> 0x0034 }
            if (r8 == 0) goto L_0x0020
            java.util.SortedMap r3 = r2.properties()     // Catch:{ Throwable -> 0x0034 }
            r3.putAll(r8)     // Catch:{ Throwable -> 0x0034 }
        L_0x0020:
            if (r6 != 0) goto L_0x002b
            org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream$1 r3 = new org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream$1     // Catch:{ Throwable -> 0x0034 }
            r3.<init>(r5)     // Catch:{ Throwable -> 0x0034 }
            r2.unpack(r3, r0)     // Catch:{ Throwable -> 0x0034 }
            goto L_0x002e
        L_0x002b:
            r2.unpack(r6, r0)     // Catch:{ Throwable -> 0x0034 }
        L_0x002e:
            r0.close()
            return
        L_0x0032:
            r2 = move-exception
            goto L_0x0036
        L_0x0034:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0032 }
        L_0x0036:
            if (r1 == 0) goto L_0x0041
            r0.close()     // Catch:{ Throwable -> 0x003c }
            goto L_0x0044
        L_0x003c:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x0044
        L_0x0041:
            r0.close()
        L_0x0044:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream.<init>(java.io.InputStream, java.io.File, org.apache.commons.compress.compressors.pack200.Pack200Strategy, java.util.Map):void");
    }

    public int read() throws IOException {
        return this.streamBridge.getInput().read();
    }

    public int read(byte[] b) throws IOException {
        return this.streamBridge.getInput().read(b);
    }

    public int read(byte[] b, int off, int count) throws IOException {
        return this.streamBridge.getInput().read(b, off, count);
    }

    public int available() throws IOException {
        return this.streamBridge.getInput().available();
    }

    public boolean markSupported() {
        try {
            return this.streamBridge.getInput().markSupported();
        } catch (IOException e) {
            return false;
        }
    }

    public void mark(int limit) {
        try {
            this.streamBridge.getInput().mark(limit);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void reset() throws IOException {
        this.streamBridge.getInput().reset();
    }

    public long skip(long count) throws IOException {
        return IOUtils.skip(this.streamBridge.getInput(), count);
    }

    public void close() throws IOException {
        try {
            this.streamBridge.stop();
        } finally {
            InputStream inputStream = this.originalInput;
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < SIG_LENGTH) {
            return false;
        }
        for (int i = 0; i < SIG_LENGTH; i++) {
            if (signature[i] != CAFE_DOOD[i]) {
                return false;
            }
        }
        return true;
    }
}

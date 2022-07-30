package org.apache.commons.compress.compressors.pack200;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.apache.commons.compress.compressors.CompressorOutputStream;

public class Pack200CompressorOutputStream extends CompressorOutputStream {
    private boolean finished;
    private final OutputStream originalOutput;
    private final Map<String, String> properties;
    private final StreamBridge streamBridge;

    public Pack200CompressorOutputStream(OutputStream out) throws IOException {
        this(out, Pack200Strategy.IN_MEMORY);
    }

    public Pack200CompressorOutputStream(OutputStream out, Pack200Strategy mode) throws IOException {
        this(out, mode, (Map<String, String>) null);
    }

    public Pack200CompressorOutputStream(OutputStream out, Map<String, String> props) throws IOException {
        this(out, Pack200Strategy.IN_MEMORY, props);
    }

    public Pack200CompressorOutputStream(OutputStream out, Pack200Strategy mode, Map<String, String> props) throws IOException {
        this.finished = false;
        this.originalOutput = out;
        this.streamBridge = mode.newStreamBridge();
        this.properties = props;
    }

    public void write(int b) throws IOException {
        this.streamBridge.write(b);
    }

    public void write(byte[] b) throws IOException {
        this.streamBridge.write(b);
    }

    public void write(byte[] b, int from, int length) throws IOException {
        this.streamBridge.write(b, from, length);
    }

    public void close() throws IOException {
        try {
            finish();
            try {
                this.streamBridge.stop();
            } finally {
                this.originalOutput.close();
            }
        } catch (Throwable th) {
            this.streamBridge.stop();
            throw th;
        } finally {
            this.originalOutput.close();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0031, code lost:
        if (r2 != null) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0037, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0038, code lost:
        r2.addSuppressed(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003c, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x002d, code lost:
        r3 = move-exception;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void finish() throws java.io.IOException {
        /*
            r5 = this;
            boolean r0 = r5.finished
            if (r0 != 0) goto L_0x0040
            r0 = 1
            r5.finished = r0
            java.util.jar.Pack200$Packer r0 = java.util.jar.Pack200.newPacker()
            java.util.Map<java.lang.String, java.lang.String> r1 = r5.properties
            if (r1 == 0) goto L_0x0018
            java.util.SortedMap r1 = r0.properties()
            java.util.Map<java.lang.String, java.lang.String> r2 = r5.properties
            r1.putAll(r2)
        L_0x0018:
            java.util.jar.JarInputStream r1 = new java.util.jar.JarInputStream
            org.apache.commons.compress.compressors.pack200.StreamBridge r2 = r5.streamBridge
            java.io.InputStream r2 = r2.getInput()
            r1.<init>(r2)
            r2 = 0
            java.io.OutputStream r3 = r5.originalOutput     // Catch:{ Throwable -> 0x002f }
            r0.pack(r1, r3)     // Catch:{ Throwable -> 0x002f }
            r1.close()
            goto L_0x0040
        L_0x002d:
            r3 = move-exception
            goto L_0x0031
        L_0x002f:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x002d }
        L_0x0031:
            if (r2 == 0) goto L_0x003c
            r1.close()     // Catch:{ Throwable -> 0x0037 }
            goto L_0x003f
        L_0x0037:
            r4 = move-exception
            r2.addSuppressed(r4)
            goto L_0x003f
        L_0x003c:
            r1.close()
        L_0x003f:
            throw r3
        L_0x0040:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream.finish():void");
    }
}

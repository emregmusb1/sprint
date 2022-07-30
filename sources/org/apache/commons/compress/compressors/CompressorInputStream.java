package org.apache.commons.compress.compressors;

import java.io.InputStream;

public abstract class CompressorInputStream extends InputStream {
    private long bytesRead = 0;

    /* access modifiers changed from: protected */
    public void count(int read) {
        count((long) read);
    }

    /* access modifiers changed from: protected */
    public void count(long read) {
        if (read != -1) {
            this.bytesRead += read;
        }
    }

    /* access modifiers changed from: protected */
    public void pushedBackBytes(long pushedBack) {
        this.bytesRead -= pushedBack;
    }

    @Deprecated
    public int getCount() {
        return (int) this.bytesRead;
    }

    public long getBytesRead() {
        return this.bytesRead;
    }

    public long getUncompressedCount() {
        return getBytesRead();
    }
}

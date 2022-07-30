package org.apache.commons.compress.utils;

import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStream extends InputStream {
    private long bytesRemaining;
    private final InputStream in;

    public BoundedInputStream(InputStream in2, long size) {
        this.in = in2;
        this.bytesRemaining = size;
    }

    public int read() throws IOException {
        long j = this.bytesRemaining;
        if (j <= 0) {
            return -1;
        }
        this.bytesRemaining = j - 1;
        return this.in.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        long j = this.bytesRemaining;
        if (j == 0) {
            return -1;
        }
        int bytesToRead = len;
        if (((long) bytesToRead) > j) {
            bytesToRead = (int) j;
        }
        int bytesRead = this.in.read(b, off, bytesToRead);
        if (bytesRead >= 0) {
            this.bytesRemaining -= (long) bytesRead;
        }
        return bytesRead;
    }

    public void close() {
    }
}

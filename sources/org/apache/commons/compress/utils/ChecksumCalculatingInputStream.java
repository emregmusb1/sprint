package org.apache.commons.compress.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Checksum;

public class ChecksumCalculatingInputStream extends InputStream {
    private final Checksum checksum;
    private final InputStream in;

    public ChecksumCalculatingInputStream(Checksum checksum2, InputStream in2) {
        if (checksum2 == null) {
            throw new NullPointerException("Parameter checksum must not be null");
        } else if (in2 != null) {
            this.checksum = checksum2;
            this.in = in2;
        } else {
            throw new NullPointerException("Parameter in must not be null");
        }
    }

    public int read() throws IOException {
        int ret = this.in.read();
        if (ret >= 0) {
            this.checksum.update(ret);
        }
        return ret;
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int ret = this.in.read(b, off, len);
        if (ret >= 0) {
            this.checksum.update(b, off, ret);
        }
        return ret;
    }

    public long skip(long n) throws IOException {
        if (read() >= 0) {
            return 1;
        }
        return 0;
    }

    public long getValue() {
        return this.checksum.getValue();
    }
}

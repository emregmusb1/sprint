package org.apache.commons.compress.compressors.xz;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;
import org.tukaani.xz.MemoryLimitException;
import org.tukaani.xz.SingleXZInputStream;
import org.tukaani.xz.XZ;
import org.tukaani.xz.XZInputStream;

public class XZCompressorInputStream extends CompressorInputStream implements InputStreamStatistics {
    private final CountingInputStream countingStream;
    private final InputStream in;

    public static boolean matches(byte[] signature, int length) {
        if (length < XZ.HEADER_MAGIC.length) {
            return false;
        }
        for (int i = 0; i < XZ.HEADER_MAGIC.length; i++) {
            if (signature[i] != XZ.HEADER_MAGIC[i]) {
                return false;
            }
        }
        return true;
    }

    public XZCompressorInputStream(InputStream inputStream) throws IOException {
        this(inputStream, false);
    }

    public XZCompressorInputStream(InputStream inputStream, boolean decompressConcatenated) throws IOException {
        this(inputStream, decompressConcatenated, -1);
    }

    public XZCompressorInputStream(InputStream inputStream, boolean decompressConcatenated, int memoryLimitInKb) throws IOException {
        this.countingStream = new CountingInputStream(inputStream);
        if (decompressConcatenated) {
            this.in = new XZInputStream(this.countingStream, memoryLimitInKb);
        } else {
            this.in = new SingleXZInputStream(this.countingStream, memoryLimitInKb);
        }
    }

    public int read() throws IOException {
        try {
            int ret = this.in.read();
            int i = -1;
            if (ret != -1) {
                i = 1;
            }
            count(i);
            return ret;
        } catch (MemoryLimitException e) {
            throw new org.apache.commons.compress.MemoryLimitException((long) e.getMemoryNeeded(), e.getMemoryLimit(), e);
        }
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        try {
            int ret = this.in.read(buf, off, len);
            count(ret);
            return ret;
        } catch (MemoryLimitException e) {
            throw new org.apache.commons.compress.MemoryLimitException((long) e.getMemoryNeeded(), e.getMemoryLimit(), e);
        }
    }

    public long skip(long n) throws IOException {
        try {
            return IOUtils.skip(this.in, n);
        } catch (MemoryLimitException e) {
            throw new org.apache.commons.compress.MemoryLimitException((long) e.getMemoryNeeded(), e.getMemoryLimit(), e);
        }
    }

    public int available() throws IOException {
        return this.in.available();
    }

    public void close() throws IOException {
        this.in.close();
    }

    public long getCompressedCount() {
        return this.countingStream.getBytesRead();
    }
}

package org.apache.commons.compress.compressors.lzma;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;
import org.tukaani.xz.LZMAInputStream;
import org.tukaani.xz.MemoryLimitException;

public class LZMACompressorInputStream extends CompressorInputStream implements InputStreamStatistics {
    private final CountingInputStream countingStream;
    private final InputStream in;

    public LZMACompressorInputStream(InputStream inputStream) throws IOException {
        CountingInputStream countingInputStream = new CountingInputStream(inputStream);
        this.countingStream = countingInputStream;
        this.in = new LZMAInputStream(countingInputStream, -1);
    }

    public LZMACompressorInputStream(InputStream inputStream, int memoryLimitInKb) throws IOException {
        try {
            CountingInputStream countingInputStream = new CountingInputStream(inputStream);
            this.countingStream = countingInputStream;
            this.in = new LZMAInputStream(countingInputStream, memoryLimitInKb);
        } catch (MemoryLimitException e) {
            throw new org.apache.commons.compress.MemoryLimitException((long) e.getMemoryNeeded(), e.getMemoryLimit(), e);
        }
    }

    public int read() throws IOException {
        int ret = this.in.read();
        count(ret == -1 ? 0 : 1);
        return ret;
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        int ret = this.in.read(buf, off, len);
        count(ret);
        return ret;
    }

    public long skip(long n) throws IOException {
        return IOUtils.skip(this.in, n);
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

    public static boolean matches(byte[] signature, int length) {
        return signature != null && length >= 3 && signature[0] == 93 && signature[1] == 0 && signature[2] == 0;
    }
}

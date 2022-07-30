package org.apache.commons.compress.compressors.zstandard;

import com.github.luben.zstd.ZstdInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;

public class ZstdCompressorInputStream extends CompressorInputStream implements InputStreamStatistics {
    private final CountingInputStream countingStream;
    private final ZstdInputStream decIS;

    public ZstdCompressorInputStream(InputStream in) throws IOException {
        CountingInputStream countingInputStream = new CountingInputStream(in);
        this.countingStream = countingInputStream;
        this.decIS = new ZstdInputStream(countingInputStream);
    }

    public int available() throws IOException {
        return this.decIS.available();
    }

    public void close() throws IOException {
        this.decIS.close();
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public long skip(long n) throws IOException {
        return IOUtils.skip(this.decIS, n);
    }

    public void mark(int readlimit) {
        this.decIS.mark(readlimit);
    }

    public boolean markSupported() {
        return this.decIS.markSupported();
    }

    public int read() throws IOException {
        int ret = this.decIS.read();
        count(ret == -1 ? 0 : 1);
        return ret;
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        int ret = this.decIS.read(buf, off, len);
        count(ret);
        return ret;
    }

    public String toString() {
        return this.decIS.toString();
    }

    public void reset() throws IOException {
        this.decIS.reset();
    }

    public long getCompressedCount() {
        return this.countingStream.getBytesRead();
    }
}

package org.apache.commons.compress.compressors.deflate64;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;

public class Deflate64CompressorInputStream extends CompressorInputStream implements InputStreamStatistics {
    private long compressedBytesRead;
    private HuffmanDecoder decoder;
    private final byte[] oneByte;
    private InputStream originalStream;

    public Deflate64CompressorInputStream(InputStream in) {
        this(new HuffmanDecoder(in));
        this.originalStream = in;
    }

    Deflate64CompressorInputStream(HuffmanDecoder decoder2) {
        this.oneByte = new byte[1];
        this.decoder = decoder2;
    }

    public int read() throws IOException {
        int r;
        do {
            r = read(this.oneByte);
            if (r == -1) {
                return -1;
            }
        } while (r == 0);
        if (r == 1) {
            return this.oneByte[0] & 255;
        }
        throw new IllegalStateException("Invalid return value from read: " + r);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int read = -1;
        HuffmanDecoder huffmanDecoder = this.decoder;
        if (huffmanDecoder != null) {
            read = huffmanDecoder.decode(b, off, len);
            this.compressedBytesRead = this.decoder.getBytesRead();
            count(read);
            if (read == -1) {
                closeDecoder();
            }
        }
        return read;
    }

    public int available() throws IOException {
        HuffmanDecoder huffmanDecoder = this.decoder;
        if (huffmanDecoder != null) {
            return huffmanDecoder.available();
        }
        return 0;
    }

    public void close() throws IOException {
        try {
            closeDecoder();
        } finally {
            InputStream inputStream = this.originalStream;
            if (inputStream != null) {
                inputStream.close();
                this.originalStream = null;
            }
        }
    }

    public long getCompressedCount() {
        return this.compressedBytesRead;
    }

    private void closeDecoder() {
        IOUtils.closeQuietly(this.decoder);
        this.decoder = null;
    }
}

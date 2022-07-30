package org.apache.commons.compress.compressors.snappy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.lz77support.Parameters;
import org.apache.commons.compress.utils.ByteUtils;

public class FramedSnappyCompressorOutputStream extends CompressorOutputStream {
    private static final int MAX_COMPRESSED_BUFFER_SIZE = 65536;
    private final byte[] buffer;
    private final PureJavaCrc32C checksum;
    private final ByteUtils.ByteConsumer consumer;
    private int currentIndex;
    private final byte[] oneByte;
    private final OutputStream out;
    private final Parameters params;

    public FramedSnappyCompressorOutputStream(OutputStream out2) throws IOException {
        this(out2, SnappyCompressorOutputStream.createParameterBuilder(32768).build());
    }

    public FramedSnappyCompressorOutputStream(OutputStream out2, Parameters params2) throws IOException {
        this.checksum = new PureJavaCrc32C();
        this.oneByte = new byte[1];
        this.buffer = new byte[65536];
        this.currentIndex = 0;
        this.out = out2;
        this.params = params2;
        this.consumer = new ByteUtils.OutputStreamByteConsumer(out2);
        out2.write(FramedSnappyCompressorInputStream.SZ_SIGNATURE);
    }

    public void write(int b) throws IOException {
        byte[] bArr = this.oneByte;
        bArr[0] = (byte) (b & 255);
        write(bArr);
    }

    public void write(byte[] data, int off, int len) throws IOException {
        if (this.currentIndex + len > 65536) {
            flushBuffer();
            while (len > 65536) {
                System.arraycopy(data, off, this.buffer, 0, 65536);
                off += 65536;
                len -= 65536;
                this.currentIndex = 65536;
                flushBuffer();
            }
        }
        System.arraycopy(data, off, this.buffer, this.currentIndex, len);
        this.currentIndex += len;
    }

    public void close() throws IOException {
        try {
            finish();
        } finally {
            this.out.close();
        }
    }

    public void finish() throws IOException {
        if (this.currentIndex > 0) {
            flushBuffer();
        }
    }

    private void flushBuffer() throws IOException {
        Throwable th;
        this.out.write(0);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream o = new SnappyCompressorOutputStream((OutputStream) baos, (long) this.currentIndex, this.params);
        try {
            o.write(this.buffer, 0, this.currentIndex);
            o.close();
            byte[] b = baos.toByteArray();
            writeLittleEndian(3, ((long) b.length) + 4);
            writeCrc();
            this.out.write(b);
            this.currentIndex = 0;
            return;
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
        throw th;
    }

    private void writeLittleEndian(int numBytes, long num) throws IOException {
        ByteUtils.toLittleEndian(this.consumer, num, numBytes);
    }

    private void writeCrc() throws IOException {
        this.checksum.update(this.buffer, 0, this.currentIndex);
        writeLittleEndian(4, mask(this.checksum.getValue()));
        this.checksum.reset();
    }

    static long mask(long x) {
        return (((x >> 15) | (x << 17)) + 2726488792L) & 4294967295L;
    }
}

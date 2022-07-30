package org.apache.commons.compress.compressors.lz77support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;

public abstract class AbstractLZ77CompressorInputStream extends CompressorInputStream implements InputStreamStatistics {
    private int backReferenceOffset;
    private final byte[] buf;
    private long bytesRemaining;
    private final CountingInputStream in;
    private final byte[] oneByte = new byte[1];
    private int readIndex;
    private int size = 0;
    protected final ByteUtils.ByteSupplier supplier = new ByteUtils.ByteSupplier() {
        public int getAsByte() throws IOException {
            return AbstractLZ77CompressorInputStream.this.readOneByte();
        }
    };
    private final int windowSize;
    private int writeIndex;

    public AbstractLZ77CompressorInputStream(InputStream is, int windowSize2) throws IOException {
        this.in = new CountingInputStream(is);
        if (windowSize2 > 0) {
            this.windowSize = windowSize2;
            this.buf = new byte[(windowSize2 * 3)];
            this.readIndex = 0;
            this.writeIndex = 0;
            this.bytesRemaining = 0;
            return;
        }
        throw new IllegalArgumentException("windowSize must be bigger than 0");
    }

    public int read() throws IOException {
        if (read(this.oneByte, 0, 1) == -1) {
            return -1;
        }
        return this.oneByte[0] & 255;
    }

    public void close() throws IOException {
        this.in.close();
    }

    public int available() {
        return this.writeIndex - this.readIndex;
    }

    public int getSize() {
        return this.size;
    }

    public void prefill(byte[] data) {
        if (this.writeIndex == 0) {
            int len = Math.min(this.windowSize, data.length);
            System.arraycopy(data, data.length - len, this.buf, 0, len);
            this.writeIndex += len;
            this.readIndex += len;
            return;
        }
        throw new IllegalStateException("The stream has already been read from, can't prefill anymore");
    }

    public long getCompressedCount() {
        return this.in.getBytesRead();
    }

    /* access modifiers changed from: protected */
    public final void startLiteral(long length) {
        if (length >= 0) {
            this.bytesRemaining = length;
            return;
        }
        throw new IllegalArgumentException("length must not be negative");
    }

    /* access modifiers changed from: protected */
    public final boolean hasMoreDataInBlock() {
        return this.bytesRemaining > 0;
    }

    /* access modifiers changed from: protected */
    public final int readLiteral(byte[] b, int off, int len) throws IOException {
        int avail = available();
        if (len > avail) {
            tryToReadLiteral(len - avail);
        }
        return readFromBuffer(b, off, len);
    }

    private void tryToReadLiteral(int bytesToRead) throws IOException {
        int reallyTryToRead = Math.min((int) Math.min((long) bytesToRead, this.bytesRemaining), this.buf.length - this.writeIndex);
        int bytesRead = reallyTryToRead > 0 ? IOUtils.readFully(this.in, this.buf, this.writeIndex, reallyTryToRead) : 0;
        count(bytesRead);
        if (reallyTryToRead == bytesRead) {
            this.writeIndex += reallyTryToRead;
            this.bytesRemaining -= (long) reallyTryToRead;
            return;
        }
        throw new IOException("Premature end of stream reading literal");
    }

    private int readFromBuffer(byte[] b, int off, int len) {
        int readable = Math.min(len, available());
        if (readable > 0) {
            System.arraycopy(this.buf, this.readIndex, b, off, readable);
            this.readIndex += readable;
            if (this.readIndex > this.windowSize * 2) {
                slideBuffer();
            }
        }
        this.size += readable;
        return readable;
    }

    private void slideBuffer() {
        byte[] bArr = this.buf;
        int i = this.windowSize;
        System.arraycopy(bArr, i, bArr, 0, i * 2);
        int i2 = this.writeIndex;
        int i3 = this.windowSize;
        this.writeIndex = i2 - i3;
        this.readIndex -= i3;
    }

    /* access modifiers changed from: protected */
    public final void startBackReference(int offset, long length) {
        if (offset <= 0 || offset > this.writeIndex) {
            throw new IllegalArgumentException("offset must be bigger than 0 but not bigger than the number of bytes available for back-references");
        } else if (length >= 0) {
            this.backReferenceOffset = offset;
            this.bytesRemaining = length;
        } else {
            throw new IllegalArgumentException("length must not be negative");
        }
    }

    /* access modifiers changed from: protected */
    public final int readBackReference(byte[] b, int off, int len) {
        int avail = available();
        if (len > avail) {
            tryToCopy(len - avail);
        }
        return readFromBuffer(b, off, len);
    }

    private void tryToCopy(int bytesToCopy) {
        int copy = Math.min((int) Math.min((long) bytesToCopy, this.bytesRemaining), this.buf.length - this.writeIndex);
        if (copy != 0) {
            int i = this.backReferenceOffset;
            if (i == 1) {
                byte[] bArr = this.buf;
                int i2 = this.writeIndex;
                Arrays.fill(bArr, i2, i2 + copy, bArr[i2 - 1]);
                this.writeIndex += copy;
            } else if (copy < i) {
                byte[] bArr2 = this.buf;
                int i3 = this.writeIndex;
                System.arraycopy(bArr2, i3 - i, bArr2, i3, copy);
                this.writeIndex += copy;
            } else {
                int fullRots = copy / i;
                for (int i4 = 0; i4 < fullRots; i4++) {
                    byte[] bArr3 = this.buf;
                    int i5 = this.writeIndex;
                    int i6 = this.backReferenceOffset;
                    System.arraycopy(bArr3, i5 - i6, bArr3, i5, i6);
                    this.writeIndex += this.backReferenceOffset;
                }
                int i7 = this.backReferenceOffset;
                int pad = copy - (i7 * fullRots);
                if (pad > 0) {
                    byte[] bArr4 = this.buf;
                    int i8 = this.writeIndex;
                    System.arraycopy(bArr4, i8 - i7, bArr4, i8, pad);
                    this.writeIndex += pad;
                }
            }
        }
        this.bytesRemaining -= (long) copy;
    }

    /* access modifiers changed from: protected */
    public final int readOneByte() throws IOException {
        int b = this.in.read();
        if (b == -1) {
            return -1;
        }
        count(1);
        return b & 255;
    }
}

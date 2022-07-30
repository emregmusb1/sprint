package org.apache.commons.compress.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class BitInputStream implements Closeable {
    private static final long[] MASKS = new long[64];
    private static final int MAXIMUM_CACHE_SIZE = 63;
    private long bitsCached = 0;
    private int bitsCachedSize = 0;
    private final ByteOrder byteOrder;
    private final CountingInputStream in;

    static {
        for (int i = 1; i <= 63; i++) {
            long[] jArr = MASKS;
            jArr[i] = (jArr[i - 1] << 1) + 1;
        }
    }

    public BitInputStream(InputStream in2, ByteOrder byteOrder2) {
        this.in = new CountingInputStream(in2);
        this.byteOrder = byteOrder2;
    }

    public void close() throws IOException {
        this.in.close();
    }

    public void clearBitCache() {
        this.bitsCached = 0;
        this.bitsCachedSize = 0;
    }

    public long readBits(int count) throws IOException {
        if (count < 0 || count > 63) {
            throw new IllegalArgumentException("count must not be negative or greater than 63");
        } else if (ensureCache(count)) {
            return -1;
        } else {
            if (this.bitsCachedSize < count) {
                return processBitsGreater57(count);
            }
            return readCachedBits(count);
        }
    }

    public int bitsCached() {
        return this.bitsCachedSize;
    }

    public long bitsAvailable() throws IOException {
        return ((long) this.bitsCachedSize) + (((long) this.in.available()) * 8);
    }

    public void alignWithByteBoundary() {
        int toSkip = this.bitsCachedSize % 8;
        if (toSkip > 0) {
            readCachedBits(toSkip);
        }
    }

    public long getBytesRead() {
        return this.in.getBytesRead();
    }

    private long processBitsGreater57(int count) throws IOException {
        long overflow;
        int bitsToAddCount = count - this.bitsCachedSize;
        int overflowBits = 8 - bitsToAddCount;
        long nextByte = (long) this.in.read();
        if (nextByte < 0) {
            return nextByte;
        }
        if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
            long[] jArr = MASKS;
            this.bitsCached |= (jArr[bitsToAddCount] & nextByte) << this.bitsCachedSize;
            overflow = (nextByte >>> bitsToAddCount) & jArr[overflowBits];
        } else {
            this.bitsCached <<= bitsToAddCount;
            long[] jArr2 = MASKS;
            this.bitsCached |= (nextByte >>> overflowBits) & jArr2[bitsToAddCount];
            overflow = nextByte & jArr2[overflowBits];
        }
        long bitsOut = this.bitsCached & MASKS[count];
        this.bitsCached = overflow;
        this.bitsCachedSize = overflowBits;
        return bitsOut;
    }

    private long readCachedBits(int count) {
        long bitsOut;
        if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
            long j = this.bitsCached;
            bitsOut = j & MASKS[count];
            this.bitsCached = j >>> count;
        } else {
            bitsOut = (this.bitsCached >> (this.bitsCachedSize - count)) & MASKS[count];
        }
        this.bitsCachedSize -= count;
        return bitsOut;
    }

    private boolean ensureCache(int count) throws IOException {
        while (true) {
            int i = this.bitsCachedSize;
            if (i >= count || i >= 57) {
                return false;
            }
            long nextByte = (long) this.in.read();
            if (nextByte < 0) {
                return true;
            }
            if (this.byteOrder == ByteOrder.LITTLE_ENDIAN) {
                this.bitsCached |= nextByte << this.bitsCachedSize;
            } else {
                this.bitsCached <<= 8;
                this.bitsCached |= nextByte;
            }
            this.bitsCachedSize += 8;
        }
    }
}

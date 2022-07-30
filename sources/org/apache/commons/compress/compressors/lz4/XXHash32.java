package org.apache.commons.compress.compressors.lz4;

import java.util.zip.Checksum;
import org.apache.commons.compress.utils.ByteUtils;

public class XXHash32 implements Checksum {
    private static final int BUF_SIZE = 16;
    private static final int PRIME1 = -1640531535;
    private static final int PRIME2 = -2048144777;
    private static final int PRIME3 = -1028477379;
    private static final int PRIME4 = 668265263;
    private static final int PRIME5 = 374761393;
    private static final int ROTATE_BITS = 13;
    private final byte[] buffer;
    private final byte[] oneByte;
    private int pos;
    private final int seed;
    private final int[] state;
    private int totalLen;

    public XXHash32() {
        this(0);
    }

    public XXHash32(int seed2) {
        this.oneByte = new byte[1];
        this.state = new int[4];
        this.buffer = new byte[16];
        this.seed = seed2;
        initializeState();
    }

    public void reset() {
        initializeState();
        this.totalLen = 0;
        this.pos = 0;
    }

    public void update(int b) {
        byte[] bArr = this.oneByte;
        bArr[0] = (byte) (b & 255);
        update(bArr, 0, 1);
    }

    public void update(byte[] b, int off, int len) {
        if (len > 0) {
            this.totalLen += len;
            int end = off + len;
            int i = this.pos;
            if (i + len < 16) {
                System.arraycopy(b, off, this.buffer, i, len);
                this.pos += len;
                return;
            }
            if (i > 0) {
                int size = 16 - i;
                System.arraycopy(b, off, this.buffer, i, size);
                process(this.buffer, 0);
                off += size;
            }
            int limit = end - 16;
            while (off <= limit) {
                process(b, off);
                off += 16;
            }
            if (off < end) {
                this.pos = end - off;
                System.arraycopy(b, off, this.buffer, 0, this.pos);
            }
        }
    }

    public long getValue() {
        int hash;
        if (this.totalLen > 16) {
            hash = Integer.rotateLeft(this.state[0], 1) + Integer.rotateLeft(this.state[1], 7) + Integer.rotateLeft(this.state[2], 12) + Integer.rotateLeft(this.state[3], 18);
        } else {
            hash = this.state[2] + PRIME5;
        }
        int hash2 = hash + this.totalLen;
        int idx = 0;
        int limit = this.pos - 4;
        while (idx <= limit) {
            hash2 = Integer.rotateLeft((getInt(this.buffer, idx) * PRIME3) + hash2, 17) * PRIME4;
            idx += 4;
        }
        while (idx < this.pos) {
            hash2 = Integer.rotateLeft(((this.buffer[idx] & 255) * PRIME5) + hash2, 11) * PRIME1;
            idx++;
        }
        int hash3 = (hash2 ^ (hash2 >>> 15)) * PRIME2;
        int hash4 = (hash3 ^ (hash3 >>> 13)) * PRIME3;
        return ((long) (hash4 ^ (hash4 >>> 16))) & 4294967295L;
    }

    private static int getInt(byte[] buffer2, int idx) {
        return (int) (ByteUtils.fromLittleEndian(buffer2, idx, 4) & 4294967295L);
    }

    private void initializeState() {
        int[] iArr = this.state;
        int i = this.seed;
        iArr[0] = i + PRIME1 + PRIME2;
        iArr[1] = PRIME2 + i;
        iArr[2] = i;
        iArr[3] = i - PRIME1;
    }

    private void process(byte[] b, int offset) {
        int[] iArr = this.state;
        int s0 = iArr[0];
        int s1 = iArr[1];
        int s2 = iArr[2];
        int s3 = iArr[3];
        int s02 = Integer.rotateLeft((getInt(b, offset) * PRIME2) + s0, 13) * PRIME1;
        int s12 = Integer.rotateLeft((getInt(b, offset + 4) * PRIME2) + s1, 13) * PRIME1;
        int s22 = Integer.rotateLeft((getInt(b, offset + 8) * PRIME2) + s2, 13) * PRIME1;
        int s32 = Integer.rotateLeft((getInt(b, offset + 12) * PRIME2) + s3, 13) * PRIME1;
        int[] iArr2 = this.state;
        iArr2[0] = s02;
        iArr2[1] = s12;
        iArr2[2] = s22;
        iArr2[3] = s32;
        this.pos = 0;
    }
}

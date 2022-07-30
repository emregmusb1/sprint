package org.apache.commons.compress.compressors.lz4;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.utils.ByteUtils;

public class FramedLZ4CompressorOutputStream extends CompressorOutputStream {
    private static final byte[] END_MARK = new byte[4];
    private final byte[] blockData;
    private byte[] blockDependencyBuffer;
    private final XXHash32 blockHash;
    private int collectedBlockDependencyBytes;
    private final XXHash32 contentHash;
    private int currentIndex;
    private boolean finished;
    private final byte[] oneByte;
    private final OutputStream out;
    private final Parameters params;

    public enum BlockSize {
        K64(65536, 4),
        K256(262144, 5),
        M1(1048576, 6),
        M4(4194304, 7);
        
        private final int index;
        private final int size;

        private BlockSize(int size2, int index2) {
            this.size = size2;
            this.index = index2;
        }

        /* access modifiers changed from: package-private */
        public int getSize() {
            return this.size;
        }

        /* access modifiers changed from: package-private */
        public int getIndex() {
            return this.index;
        }
    }

    public static class Parameters {
        public static final Parameters DEFAULT = new Parameters(BlockSize.M4, true, false, false);
        /* access modifiers changed from: private */
        public final BlockSize blockSize;
        /* access modifiers changed from: private */
        public final org.apache.commons.compress.compressors.lz77support.Parameters lz77params;
        /* access modifiers changed from: private */
        public final boolean withBlockChecksum;
        /* access modifiers changed from: private */
        public final boolean withBlockDependency;
        /* access modifiers changed from: private */
        public final boolean withContentChecksum;

        public Parameters(BlockSize blockSize2) {
            this(blockSize2, true, false, false);
        }

        public Parameters(BlockSize blockSize2, org.apache.commons.compress.compressors.lz77support.Parameters lz77params2) {
            this(blockSize2, true, false, false, lz77params2);
        }

        public Parameters(BlockSize blockSize2, boolean withContentChecksum2, boolean withBlockChecksum2, boolean withBlockDependency2) {
            this(blockSize2, withContentChecksum2, withBlockChecksum2, withBlockDependency2, BlockLZ4CompressorOutputStream.createParameterBuilder().build());
        }

        public Parameters(BlockSize blockSize2, boolean withContentChecksum2, boolean withBlockChecksum2, boolean withBlockDependency2, org.apache.commons.compress.compressors.lz77support.Parameters lz77params2) {
            this.blockSize = blockSize2;
            this.withContentChecksum = withContentChecksum2;
            this.withBlockChecksum = withBlockChecksum2;
            this.withBlockDependency = withBlockDependency2;
            this.lz77params = lz77params2;
        }

        public String toString() {
            return "LZ4 Parameters with BlockSize " + this.blockSize + ", withContentChecksum " + this.withContentChecksum + ", withBlockChecksum " + this.withBlockChecksum + ", withBlockDependency " + this.withBlockDependency;
        }
    }

    public FramedLZ4CompressorOutputStream(OutputStream out2) throws IOException {
        this(out2, Parameters.DEFAULT);
    }

    public FramedLZ4CompressorOutputStream(OutputStream out2, Parameters params2) throws IOException {
        this.oneByte = new byte[1];
        this.finished = false;
        this.currentIndex = 0;
        this.contentHash = new XXHash32();
        this.params = params2;
        this.blockData = new byte[params2.blockSize.getSize()];
        this.out = out2;
        byte[] bArr = null;
        this.blockHash = params2.withBlockChecksum ? new XXHash32() : null;
        out2.write(FramedLZ4CompressorInputStream.LZ4_SIGNATURE);
        writeFrameDescriptor();
        this.blockDependencyBuffer = params2.withBlockDependency ? new byte[65536] : bArr;
    }

    public void write(int b) throws IOException {
        byte[] bArr = this.oneByte;
        bArr[0] = (byte) (b & 255);
        write(bArr);
    }

    public void write(byte[] data, int off, int len) throws IOException {
        if (this.params.withContentChecksum) {
            this.contentHash.update(data, off, len);
        }
        if (this.currentIndex + len > this.blockData.length) {
            flushBlock();
            while (true) {
                byte[] bArr = this.blockData;
                if (len <= bArr.length) {
                    break;
                }
                System.arraycopy(data, off, bArr, 0, bArr.length);
                byte[] bArr2 = this.blockData;
                off += bArr2.length;
                len -= bArr2.length;
                this.currentIndex = bArr2.length;
                flushBlock();
            }
        }
        System.arraycopy(data, off, this.blockData, this.currentIndex, len);
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
        if (!this.finished) {
            if (this.currentIndex > 0) {
                flushBlock();
            }
            writeTrailer();
            this.finished = true;
        }
    }

    private void writeFrameDescriptor() throws IOException {
        int flags = 64;
        if (!this.params.withBlockDependency) {
            flags = 64 | 32;
        }
        if (this.params.withContentChecksum) {
            flags |= 4;
        }
        if (this.params.withBlockChecksum) {
            flags |= 16;
        }
        this.out.write(flags);
        this.contentHash.update(flags);
        int bd = (this.params.blockSize.getIndex() << 4) & 112;
        this.out.write(bd);
        this.contentHash.update(bd);
        this.out.write((int) ((this.contentHash.getValue() >> 8) & 255));
        this.contentHash.reset();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x009d, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00a1, code lost:
        if (r3 != null) goto L_0x00a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a7, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00a8, code lost:
        r3.addSuppressed(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00ac, code lost:
        r2.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void flushBlock() throws java.io.IOException {
        /*
            r9 = this;
            org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream$Parameters r0 = r9.params
            boolean r0 = r0.withBlockDependency
            java.io.ByteArrayOutputStream r1 = new java.io.ByteArrayOutputStream
            r1.<init>()
            org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream r2 = new org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream
            org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream$Parameters r3 = r9.params
            org.apache.commons.compress.compressors.lz77support.Parameters r3 = r3.lz77params
            r2.<init>(r1, r3)
            r3 = 0
            if (r0 == 0) goto L_0x0026
            byte[] r4 = r9.blockDependencyBuffer     // Catch:{ Throwable -> 0x009f }
            byte[] r5 = r9.blockDependencyBuffer     // Catch:{ Throwable -> 0x009f }
            int r5 = r5.length     // Catch:{ Throwable -> 0x009f }
            int r6 = r9.collectedBlockDependencyBytes     // Catch:{ Throwable -> 0x009f }
            int r5 = r5 - r6
            int r6 = r9.collectedBlockDependencyBytes     // Catch:{ Throwable -> 0x009f }
            r2.prefill(r4, r5, r6)     // Catch:{ Throwable -> 0x009f }
        L_0x0026:
            byte[] r4 = r9.blockData     // Catch:{ Throwable -> 0x009f }
            int r5 = r9.currentIndex     // Catch:{ Throwable -> 0x009f }
            r6 = 0
            r2.write(r4, r6, r5)     // Catch:{ Throwable -> 0x009f }
            r2.close()
            if (r0 == 0) goto L_0x003a
            byte[] r2 = r9.blockData
            int r3 = r9.currentIndex
            r9.appendToBlockDependencyBuffer(r2, r6, r3)
        L_0x003a:
            byte[] r2 = r1.toByteArray()
            int r3 = r2.length
            int r4 = r9.currentIndex
            r5 = 4
            if (r3 <= r4) goto L_0x0068
            java.io.OutputStream r3 = r9.out
            r7 = -2147483648(0xffffffff80000000, float:-0.0)
            r4 = r4 | r7
            long r7 = (long) r4
            org.apache.commons.compress.utils.ByteUtils.toLittleEndian((java.io.OutputStream) r3, (long) r7, (int) r5)
            java.io.OutputStream r3 = r9.out
            byte[] r4 = r9.blockData
            int r7 = r9.currentIndex
            r3.write(r4, r6, r7)
            org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream$Parameters r3 = r9.params
            boolean r3 = r3.withBlockChecksum
            if (r3 == 0) goto L_0x0082
            org.apache.commons.compress.compressors.lz4.XXHash32 r3 = r9.blockHash
            byte[] r4 = r9.blockData
            int r7 = r9.currentIndex
            r3.update(r4, r6, r7)
            goto L_0x0082
        L_0x0068:
            java.io.OutputStream r3 = r9.out
            int r4 = r2.length
            long r7 = (long) r4
            org.apache.commons.compress.utils.ByteUtils.toLittleEndian((java.io.OutputStream) r3, (long) r7, (int) r5)
            java.io.OutputStream r3 = r9.out
            r3.write(r2)
            org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream$Parameters r3 = r9.params
            boolean r3 = r3.withBlockChecksum
            if (r3 == 0) goto L_0x0082
            org.apache.commons.compress.compressors.lz4.XXHash32 r3 = r9.blockHash
            int r4 = r2.length
            r3.update(r2, r6, r4)
        L_0x0082:
            org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream$Parameters r3 = r9.params
            boolean r3 = r3.withBlockChecksum
            if (r3 == 0) goto L_0x009a
            java.io.OutputStream r3 = r9.out
            org.apache.commons.compress.compressors.lz4.XXHash32 r4 = r9.blockHash
            long r7 = r4.getValue()
            org.apache.commons.compress.utils.ByteUtils.toLittleEndian((java.io.OutputStream) r3, (long) r7, (int) r5)
            org.apache.commons.compress.compressors.lz4.XXHash32 r3 = r9.blockHash
            r3.reset()
        L_0x009a:
            r9.currentIndex = r6
            return
        L_0x009d:
            r4 = move-exception
            goto L_0x00a1
        L_0x009f:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x009d }
        L_0x00a1:
            if (r3 == 0) goto L_0x00ac
            r2.close()     // Catch:{ Throwable -> 0x00a7 }
            goto L_0x00af
        L_0x00a7:
            r5 = move-exception
            r3.addSuppressed(r5)
            goto L_0x00af
        L_0x00ac:
            r2.close()
        L_0x00af:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream.flushBlock():void");
    }

    private void writeTrailer() throws IOException {
        this.out.write(END_MARK);
        if (this.params.withContentChecksum) {
            ByteUtils.toLittleEndian(this.out, this.contentHash.getValue(), 4);
        }
    }

    private void appendToBlockDependencyBuffer(byte[] b, int off, int len) {
        int len2 = Math.min(len, this.blockDependencyBuffer.length);
        if (len2 > 0) {
            byte[] bArr = this.blockDependencyBuffer;
            int keep = bArr.length - len2;
            if (keep > 0) {
                System.arraycopy(bArr, len2, bArr, 0, keep);
            }
            System.arraycopy(b, off, this.blockDependencyBuffer, keep, len2);
            this.collectedBlockDependencyBytes = Math.min(this.collectedBlockDependencyBytes + len2, this.blockDependencyBuffer.length);
        }
    }
}

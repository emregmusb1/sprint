package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.utils.InputStreamStatistics;

class ExplodingInputStream extends InputStream implements InputStreamStatistics {
    private BitStream bits;
    private final CircularBuffer buffer = new CircularBuffer(32768);
    private final int dictionarySize;
    private BinaryTree distanceTree;
    private final InputStream in;
    private BinaryTree lengthTree;
    private BinaryTree literalTree;
    private final int minimumMatchLength;
    private final int numberOfTrees;
    private long treeSizes = 0;
    private long uncompressedCount = 0;

    public ExplodingInputStream(int dictionarySize2, int numberOfTrees2, InputStream in2) {
        if (dictionarySize2 != 4096 && dictionarySize2 != 8192) {
            throw new IllegalArgumentException("The dictionary size must be 4096 or 8192");
        } else if (numberOfTrees2 == 2 || numberOfTrees2 == 3) {
            this.dictionarySize = dictionarySize2;
            this.numberOfTrees = numberOfTrees2;
            this.minimumMatchLength = numberOfTrees2;
            this.in = in2;
        } else {
            throw new IllegalArgumentException("The number of trees must be 2 or 3");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0041, code lost:
        if (r1 != null) goto L_0x0043;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0047, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0048, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004c, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x003d, code lost:
        r2 = move-exception;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void init() throws java.io.IOException {
        /*
            r6 = this;
            org.apache.commons.compress.archivers.zip.BitStream r0 = r6.bits
            if (r0 != 0) goto L_0x0050
            org.apache.commons.compress.archivers.zip.ExplodingInputStream$1 r0 = new org.apache.commons.compress.archivers.zip.ExplodingInputStream$1
            java.io.InputStream r1 = r6.in
            r0.<init>(r1)
            r1 = 0
            int r2 = r6.numberOfTrees     // Catch:{ Throwable -> 0x003f }
            r3 = 3
            if (r2 != r3) goto L_0x0019
            r2 = 256(0x100, float:3.59E-43)
            org.apache.commons.compress.archivers.zip.BinaryTree r2 = org.apache.commons.compress.archivers.zip.BinaryTree.decode(r0, r2)     // Catch:{ Throwable -> 0x003f }
            r6.literalTree = r2     // Catch:{ Throwable -> 0x003f }
        L_0x0019:
            r2 = 64
            org.apache.commons.compress.archivers.zip.BinaryTree r3 = org.apache.commons.compress.archivers.zip.BinaryTree.decode(r0, r2)     // Catch:{ Throwable -> 0x003f }
            r6.lengthTree = r3     // Catch:{ Throwable -> 0x003f }
            org.apache.commons.compress.archivers.zip.BinaryTree r2 = org.apache.commons.compress.archivers.zip.BinaryTree.decode(r0, r2)     // Catch:{ Throwable -> 0x003f }
            r6.distanceTree = r2     // Catch:{ Throwable -> 0x003f }
            long r2 = r6.treeSizes     // Catch:{ Throwable -> 0x003f }
            long r4 = r0.getBytesRead()     // Catch:{ Throwable -> 0x003f }
            long r2 = r2 + r4
            r6.treeSizes = r2     // Catch:{ Throwable -> 0x003f }
            r0.close()
            org.apache.commons.compress.archivers.zip.BitStream r0 = new org.apache.commons.compress.archivers.zip.BitStream
            java.io.InputStream r1 = r6.in
            r0.<init>(r1)
            r6.bits = r0
            goto L_0x0050
        L_0x003d:
            r2 = move-exception
            goto L_0x0041
        L_0x003f:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x003d }
        L_0x0041:
            if (r1 == 0) goto L_0x004c
            r0.close()     // Catch:{ Throwable -> 0x0047 }
            goto L_0x004f
        L_0x0047:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x004f
        L_0x004c:
            r0.close()
        L_0x004f:
            throw r2
        L_0x0050:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.zip.ExplodingInputStream.init():void");
    }

    public int read() throws IOException {
        if (!this.buffer.available()) {
            fillBuffer();
        }
        int ret = this.buffer.get();
        if (ret > -1) {
            this.uncompressedCount++;
        }
        return ret;
    }

    public long getCompressedCount() {
        return this.bits.getBytesRead() + this.treeSizes;
    }

    public long getUncompressedCount() {
        return this.uncompressedCount;
    }

    public void close() throws IOException {
        this.in.close();
    }

    private void fillBuffer() throws IOException {
        int literal;
        init();
        int bit = this.bits.nextBit();
        if (bit != -1) {
            if (bit == 1) {
                BinaryTree binaryTree = this.literalTree;
                if (binaryTree != null) {
                    literal = binaryTree.read(this.bits);
                } else {
                    literal = this.bits.nextByte();
                }
                if (literal != -1) {
                    this.buffer.put(literal);
                    return;
                }
                return;
            }
            int distanceLowSize = this.dictionarySize == 4096 ? 6 : 7;
            int distanceLow = (int) this.bits.nextBits(distanceLowSize);
            int distanceHigh = this.distanceTree.read(this.bits);
            if (distanceHigh != -1 || distanceLow > 0) {
                int distance = (distanceHigh << distanceLowSize) | distanceLow;
                int length = this.lengthTree.read(this.bits);
                if (length == 63) {
                    long nextByte = this.bits.nextBits(8);
                    if (nextByte != -1) {
                        length = (int) (((long) length) + nextByte);
                    } else {
                        return;
                    }
                }
                this.buffer.copy(distance + 1, length + this.minimumMatchLength);
            }
        }
    }
}

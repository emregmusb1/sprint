package org.apache.commons.compress.compressors.lz4;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.ChecksumCalculatingInputStream;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;

public class FramedLZ4CompressorInputStream extends CompressorInputStream implements InputStreamStatistics {
    static final int BLOCK_CHECKSUM_MASK = 16;
    static final int BLOCK_INDEPENDENCE_MASK = 32;
    static final int BLOCK_MAX_SIZE_MASK = 112;
    static final int CONTENT_CHECKSUM_MASK = 4;
    static final int CONTENT_SIZE_MASK = 8;
    static final byte[] LZ4_SIGNATURE = {4, 34, 77, 24};
    private static final byte SKIPPABLE_FRAME_PREFIX_BYTE_MASK = 80;
    private static final byte[] SKIPPABLE_FRAME_TRAILER = {42, 77, 24};
    static final int SUPPORTED_VERSION = 64;
    static final int UNCOMPRESSED_FLAG_MASK = Integer.MIN_VALUE;
    static final int VERSION_MASK = 192;
    private byte[] blockDependencyBuffer;
    private final XXHash32 blockHash;
    private final XXHash32 contentHash;
    private InputStream currentBlock;
    private final boolean decompressConcatenated;
    private boolean endReached;
    private boolean expectBlockChecksum;
    private boolean expectBlockDependency;
    private boolean expectContentChecksum;
    private boolean expectContentSize;
    private final CountingInputStream in;
    private boolean inUncompressed;
    private final byte[] oneByte;
    private final ByteUtils.ByteSupplier supplier;

    public FramedLZ4CompressorInputStream(InputStream in2) throws IOException {
        this(in2, false);
    }

    public FramedLZ4CompressorInputStream(InputStream in2, boolean decompressConcatenated2) throws IOException {
        this.oneByte = new byte[1];
        this.supplier = new ByteUtils.ByteSupplier() {
            public int getAsByte() throws IOException {
                return FramedLZ4CompressorInputStream.this.readOneByte();
            }
        };
        this.contentHash = new XXHash32();
        this.blockHash = new XXHash32();
        this.in = new CountingInputStream(in2);
        this.decompressConcatenated = decompressConcatenated2;
        init(true);
    }

    public int read() throws IOException {
        if (read(this.oneByte, 0, 1) == -1) {
            return -1;
        }
        return this.oneByte[0] & 255;
    }

    public void close() throws IOException {
        try {
            if (this.currentBlock != null) {
                this.currentBlock.close();
                this.currentBlock = null;
            }
        } finally {
            this.in.close();
        }
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.endReached) {
            return -1;
        }
        int r = readOnce(b, off, len);
        if (r == -1) {
            nextBlock();
            if (!this.endReached) {
                r = readOnce(b, off, len);
            }
        }
        if (r != -1) {
            if (this.expectBlockDependency) {
                appendToBlockDependencyBuffer(b, off, r);
            }
            if (this.expectContentChecksum) {
                this.contentHash.update(b, off, r);
            }
        }
        return r;
    }

    public long getCompressedCount() {
        return this.in.getBytesRead();
    }

    private void init(boolean firstFrame) throws IOException {
        if (readSignature(firstFrame)) {
            readFrameDescriptor();
            nextBlock();
        }
    }

    private boolean readSignature(boolean firstFrame) throws IOException {
        String garbageMessage = firstFrame ? "Not a LZ4 frame stream" : "LZ4 frame stream followed by garbage";
        byte[] b = new byte[4];
        int read = IOUtils.readFully((InputStream) this.in, b);
        count(read);
        if (read == 0 && !firstFrame) {
            this.endReached = true;
            return false;
        } else if (4 == read) {
            int read2 = skipSkippableFrame(b);
            if (read2 == 0 && !firstFrame) {
                this.endReached = true;
                return false;
            } else if (4 == read2 && matches(b, 4)) {
                return true;
            } else {
                throw new IOException(garbageMessage);
            }
        } else {
            throw new IOException(garbageMessage);
        }
    }

    private void readFrameDescriptor() throws IOException {
        int flags = readOneByte();
        if (flags != -1) {
            this.contentHash.update(flags);
            if ((flags & VERSION_MASK) == 64) {
                boolean z = true;
                this.expectBlockDependency = (flags & 32) == 0;
                if (!this.expectBlockDependency) {
                    this.blockDependencyBuffer = null;
                } else if (this.blockDependencyBuffer == null) {
                    this.blockDependencyBuffer = new byte[65536];
                }
                this.expectBlockChecksum = (flags & 16) != 0;
                this.expectContentSize = (flags & 8) != 0;
                if ((flags & 4) == 0) {
                    z = false;
                }
                this.expectContentChecksum = z;
                int bdByte = readOneByte();
                if (bdByte != -1) {
                    this.contentHash.update(bdByte);
                    if (this.expectContentSize) {
                        byte[] contentSize = new byte[8];
                        int skipped = IOUtils.readFully((InputStream) this.in, contentSize);
                        count(skipped);
                        if (8 == skipped) {
                            this.contentHash.update(contentSize, 0, contentSize.length);
                        } else {
                            throw new IOException("Premature end of stream while reading content size");
                        }
                    }
                    int headerHash = readOneByte();
                    if (headerHash != -1) {
                        int expectedHash = (int) ((this.contentHash.getValue() >> 8) & 255);
                        this.contentHash.reset();
                        if (headerHash != expectedHash) {
                            throw new IOException("Frame header checksum mismatch");
                        }
                        return;
                    }
                    throw new IOException("Premature end of stream while reading frame header checksum");
                }
                throw new IOException("Premature end of stream while reading frame BD byte");
            }
            throw new IOException("Unsupported version " + (flags >> 6));
        }
        throw new IOException("Premature end of stream while reading frame flags");
    }

    private void nextBlock() throws IOException {
        maybeFinishCurrentBlock();
        long len = ByteUtils.fromLittleEndian(this.supplier, 4);
        boolean uncompressed = (-2147483648L & len) != 0;
        int realLen = (int) (2147483647L & len);
        if (realLen < 0) {
            throw new IOException("Found illegal block with negative size");
        } else if (realLen == 0) {
            verifyContentChecksum();
            if (!this.decompressConcatenated) {
                this.endReached = true;
            } else {
                init(false);
            }
        } else {
            InputStream capped = new BoundedInputStream(this.in, (long) realLen);
            if (this.expectBlockChecksum) {
                capped = new ChecksumCalculatingInputStream(this.blockHash, capped);
            }
            if (uncompressed) {
                this.inUncompressed = true;
                this.currentBlock = capped;
                return;
            }
            this.inUncompressed = false;
            BlockLZ4CompressorInputStream s = new BlockLZ4CompressorInputStream(capped);
            if (this.expectBlockDependency) {
                s.prefill(this.blockDependencyBuffer);
            }
            this.currentBlock = s;
        }
    }

    private void maybeFinishCurrentBlock() throws IOException {
        InputStream inputStream = this.currentBlock;
        if (inputStream != null) {
            inputStream.close();
            this.currentBlock = null;
            if (this.expectBlockChecksum) {
                verifyChecksum(this.blockHash, "block");
                this.blockHash.reset();
            }
        }
    }

    private void verifyContentChecksum() throws IOException {
        if (this.expectContentChecksum) {
            verifyChecksum(this.contentHash, "content");
        }
        this.contentHash.reset();
    }

    private void verifyChecksum(XXHash32 hash, String kind) throws IOException {
        byte[] checksum = new byte[4];
        int read = IOUtils.readFully((InputStream) this.in, checksum);
        count(read);
        if (4 != read) {
            throw new IOException("Premature end of stream while reading " + kind + " checksum");
        } else if (hash.getValue() != ByteUtils.fromLittleEndian(checksum)) {
            throw new IOException(kind + " checksum mismatch.");
        }
    }

    /* access modifiers changed from: private */
    public int readOneByte() throws IOException {
        int b = this.in.read();
        if (b == -1) {
            return -1;
        }
        count(1);
        return b & 255;
    }

    private int readOnce(byte[] b, int off, int len) throws IOException {
        if (this.inUncompressed) {
            int cnt = this.currentBlock.read(b, off, len);
            count(cnt);
            return cnt;
        }
        BlockLZ4CompressorInputStream l = (BlockLZ4CompressorInputStream) this.currentBlock;
        long before = l.getBytesRead();
        int cnt2 = this.currentBlock.read(b, off, len);
        count(l.getBytesRead() - before);
        return cnt2;
    }

    private static boolean isSkippableFrameSignature(byte[] b) {
        if ((b[0] & SKIPPABLE_FRAME_PREFIX_BYTE_MASK) != 80) {
            return false;
        }
        for (int i = 1; i < 4; i++) {
            if (b[i] != SKIPPABLE_FRAME_TRAILER[i - 1]) {
                return false;
            }
        }
        return true;
    }

    private int skipSkippableFrame(byte[] b) throws IOException {
        int read = 4;
        while (read == 4 && isSkippableFrameSignature(b)) {
            long len = ByteUtils.fromLittleEndian(this.supplier, 4);
            if (len >= 0) {
                long skipped = IOUtils.skip(this.in, len);
                count(skipped);
                if (len == skipped) {
                    read = IOUtils.readFully((InputStream) this.in, b);
                    count(read);
                } else {
                    throw new IOException("Premature end of stream while skipping frame");
                }
            } else {
                throw new IOException("Found illegal skippable frame with negative size");
            }
        }
        return read;
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
        }
    }

    public static boolean matches(byte[] signature, int length) {
        byte[] bArr = LZ4_SIGNATURE;
        if (length < bArr.length) {
            return false;
        }
        byte[] shortenedSig = signature;
        if (signature.length > bArr.length) {
            shortenedSig = new byte[bArr.length];
            System.arraycopy(signature, 0, shortenedSig, 0, bArr.length);
        }
        return Arrays.equals(shortenedSig, LZ4_SIGNATURE);
    }
}

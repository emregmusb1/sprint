package org.apache.commons.compress.compressors.snappy;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;

public class FramedSnappyCompressorInputStream extends CompressorInputStream implements InputStreamStatistics {
    static final int COMPRESSED_CHUNK_TYPE = 0;
    static final long MASK_OFFSET = 2726488792L;
    private static final int MAX_SKIPPABLE_TYPE = 253;
    private static final int MAX_UNSKIPPABLE_TYPE = 127;
    private static final int MIN_UNSKIPPABLE_TYPE = 2;
    private static final int PADDING_CHUNK_TYPE = 254;
    private static final int STREAM_IDENTIFIER_TYPE = 255;
    static final byte[] SZ_SIGNATURE = {-1, 6, 0, 0, 115, 78, 97, 80, 112, 89};
    private static final int UNCOMPRESSED_CHUNK_TYPE = 1;
    private final int blockSize;
    private final PureJavaCrc32C checksum;
    private final CountingInputStream countingStream;
    private SnappyCompressorInputStream currentCompressedChunk;
    private final FramedSnappyDialect dialect;
    private boolean endReached;
    private long expectedChecksum;
    private final PushbackInputStream in;
    private boolean inUncompressedChunk;
    private final byte[] oneByte;
    private final ByteUtils.ByteSupplier supplier;
    private int uncompressedBytesRemaining;
    private long unreadBytes;

    public FramedSnappyCompressorInputStream(InputStream in2) throws IOException {
        this(in2, FramedSnappyDialect.STANDARD);
    }

    public FramedSnappyCompressorInputStream(InputStream in2, FramedSnappyDialect dialect2) throws IOException {
        this(in2, 32768, dialect2);
    }

    public FramedSnappyCompressorInputStream(InputStream in2, int blockSize2, FramedSnappyDialect dialect2) throws IOException {
        this.oneByte = new byte[1];
        this.expectedChecksum = -1;
        this.checksum = new PureJavaCrc32C();
        this.supplier = new ByteUtils.ByteSupplier() {
            public int getAsByte() throws IOException {
                return FramedSnappyCompressorInputStream.this.readOneByte();
            }
        };
        if (blockSize2 > 0) {
            this.countingStream = new CountingInputStream(in2);
            this.in = new PushbackInputStream(this.countingStream, 1);
            this.blockSize = blockSize2;
            this.dialect = dialect2;
            if (dialect2.hasStreamIdentifier()) {
                readStreamIdentifier();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("blockSize must be bigger than 0");
    }

    public int read() throws IOException {
        if (read(this.oneByte, 0, 1) == -1) {
            return -1;
        }
        return this.oneByte[0] & 255;
    }

    public void close() throws IOException {
        try {
            if (this.currentCompressedChunk != null) {
                this.currentCompressedChunk.close();
                this.currentCompressedChunk = null;
            }
        } finally {
            this.in.close();
        }
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int read = readOnce(b, off, len);
        if (read != -1) {
            return read;
        }
        readNextBlock();
        if (this.endReached) {
            return -1;
        }
        return readOnce(b, off, len);
    }

    public int available() throws IOException {
        if (this.inUncompressedChunk) {
            return Math.min(this.uncompressedBytesRemaining, this.in.available());
        }
        SnappyCompressorInputStream snappyCompressorInputStream = this.currentCompressedChunk;
        if (snappyCompressorInputStream != null) {
            return snappyCompressorInputStream.available();
        }
        return 0;
    }

    public long getCompressedCount() {
        return this.countingStream.getBytesRead() - this.unreadBytes;
    }

    private int readOnce(byte[] b, int off, int len) throws IOException {
        int read = -1;
        if (this.inUncompressedChunk) {
            int amount = Math.min(this.uncompressedBytesRemaining, len);
            if (amount == 0) {
                return -1;
            }
            read = this.in.read(b, off, amount);
            if (read != -1) {
                this.uncompressedBytesRemaining -= read;
                count(read);
            }
        } else {
            SnappyCompressorInputStream snappyCompressorInputStream = this.currentCompressedChunk;
            if (snappyCompressorInputStream != null) {
                long before = snappyCompressorInputStream.getBytesRead();
                read = this.currentCompressedChunk.read(b, off, len);
                if (read == -1) {
                    this.currentCompressedChunk.close();
                    this.currentCompressedChunk = null;
                } else {
                    count(this.currentCompressedChunk.getBytesRead() - before);
                }
            }
        }
        if (read > 0) {
            this.checksum.update(b, off, read);
        }
        return read;
    }

    private void readNextBlock() throws IOException {
        verifyLastChecksumAndReset();
        this.inUncompressedChunk = false;
        int type = readOneByte();
        if (type == -1) {
            this.endReached = true;
        } else if (type == 255) {
            this.in.unread(type);
            this.unreadBytes++;
            pushedBackBytes(1);
            readStreamIdentifier();
            readNextBlock();
        } else if (type == PADDING_CHUNK_TYPE || (type > MAX_UNSKIPPABLE_TYPE && type <= MAX_SKIPPABLE_TYPE)) {
            skipBlock();
            readNextBlock();
        } else if (type >= 2 && type <= MAX_UNSKIPPABLE_TYPE) {
            throw new IOException("Unskippable chunk with type " + type + " (hex " + Integer.toHexString(type) + ") detected.");
        } else if (type == 1) {
            this.inUncompressedChunk = true;
            this.uncompressedBytesRemaining = readSize() - 4;
            if (this.uncompressedBytesRemaining >= 0) {
                this.expectedChecksum = unmask(readCrc());
                return;
            }
            throw new IOException("Found illegal chunk with negative size");
        } else if (type == 0) {
            boolean expectChecksum = this.dialect.usesChecksumWithCompressedChunks();
            long size = ((long) readSize()) - (expectChecksum ? 4 : 0);
            if (size >= 0) {
                if (expectChecksum) {
                    this.expectedChecksum = unmask(readCrc());
                } else {
                    this.expectedChecksum = -1;
                }
                this.currentCompressedChunk = new SnappyCompressorInputStream(new BoundedInputStream(this.in, size), this.blockSize);
                count(this.currentCompressedChunk.getBytesRead());
                return;
            }
            throw new IOException("Found illegal chunk with negative size");
        } else {
            throw new IOException("Unknown chunk type " + type + " detected.");
        }
    }

    private long readCrc() throws IOException {
        byte[] b = new byte[4];
        int read = IOUtils.readFully((InputStream) this.in, b);
        count(read);
        if (read == 4) {
            return ByteUtils.fromLittleEndian(b);
        }
        throw new IOException("Premature end of stream");
    }

    static long unmask(long x) {
        long x2 = (x - MASK_OFFSET) & 4294967295L;
        return 4294967295L & ((x2 >> 17) | (x2 << 15));
    }

    private int readSize() throws IOException {
        return (int) ByteUtils.fromLittleEndian(this.supplier, 3);
    }

    private void skipBlock() throws IOException {
        int size = readSize();
        if (size >= 0) {
            long read = IOUtils.skip(this.in, (long) size);
            count(read);
            if (read != ((long) size)) {
                throw new IOException("Premature end of stream");
            }
            return;
        }
        throw new IOException("Found illegal chunk with negative size");
    }

    private void readStreamIdentifier() throws IOException {
        byte[] b = new byte[10];
        int read = IOUtils.readFully((InputStream) this.in, b);
        count(read);
        if (10 != read || !matches(b, 10)) {
            throw new IOException("Not a framed Snappy stream");
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

    private void verifyLastChecksumAndReset() throws IOException {
        long j = this.expectedChecksum;
        if (j < 0 || j == this.checksum.getValue()) {
            this.expectedChecksum = -1;
            this.checksum.reset();
            return;
        }
        throw new IOException("Checksum verification failed");
    }

    public static boolean matches(byte[] signature, int length) {
        byte[] bArr = SZ_SIGNATURE;
        if (length < bArr.length) {
            return false;
        }
        byte[] shortenedSig = signature;
        if (signature.length > bArr.length) {
            shortenedSig = new byte[bArr.length];
            System.arraycopy(signature, 0, shortenedSig, 0, bArr.length);
        }
        return Arrays.equals(shortenedSig, SZ_SIGNATURE);
    }
}

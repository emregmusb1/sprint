package org.apache.commons.compress.compressors.snappy;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;
import org.apache.commons.compress.compressors.lz77support.Parameters;
import org.apache.commons.compress.utils.ByteUtils;

public class SnappyCompressorOutputStream extends CompressorOutputStream {
    private static final int FOUR_BYTE_COPY_TAG = 3;
    private static final int FOUR_SIZE_BYTE_MARKER = 252;
    private static final int MAX_LITERAL_SIZE_WITHOUT_SIZE_BYTES = 60;
    private static final int MAX_LITERAL_SIZE_WITH_ONE_SIZE_BYTE = 256;
    private static final int MAX_LITERAL_SIZE_WITH_THREE_SIZE_BYTES = 16777216;
    private static final int MAX_LITERAL_SIZE_WITH_TWO_SIZE_BYTES = 65536;
    private static final int MAX_MATCH_LENGTH = 64;
    private static final int MAX_MATCH_LENGTH_WITH_ONE_OFFSET_BYTE = 11;
    private static final int MAX_OFFSET_WITH_ONE_OFFSET_BYTE = 1024;
    private static final int MAX_OFFSET_WITH_TWO_OFFSET_BYTES = 32768;
    private static final int MIN_MATCH_LENGTH = 4;
    private static final int MIN_MATCH_LENGTH_WITH_ONE_OFFSET_BYTE = 4;
    private static final int ONE_BYTE_COPY_TAG = 1;
    private static final int ONE_SIZE_BYTE_MARKER = 240;
    private static final int THREE_SIZE_BYTE_MARKER = 248;
    private static final int TWO_BYTE_COPY_TAG = 2;
    private static final int TWO_SIZE_BYTE_MARKER = 244;
    private final LZ77Compressor compressor;
    private final ByteUtils.ByteConsumer consumer;
    private boolean finished;
    private final byte[] oneByte;
    private final OutputStream os;

    public SnappyCompressorOutputStream(OutputStream os2, long uncompressedSize) throws IOException {
        this(os2, uncompressedSize, 32768);
    }

    public SnappyCompressorOutputStream(OutputStream os2, long uncompressedSize, int blockSize) throws IOException {
        this(os2, uncompressedSize, createParameterBuilder(blockSize).build());
    }

    public SnappyCompressorOutputStream(OutputStream os2, long uncompressedSize, Parameters params) throws IOException {
        this.oneByte = new byte[1];
        this.finished = false;
        this.os = os2;
        this.consumer = new ByteUtils.OutputStreamByteConsumer(os2);
        this.compressor = new LZ77Compressor(params, new LZ77Compressor.Callback() {
            public void accept(LZ77Compressor.Block block) throws IOException {
                int i = AnonymousClass2.$SwitchMap$org$apache$commons$compress$compressors$lz77support$LZ77Compressor$Block$BlockType[block.getType().ordinal()];
                if (i == 1) {
                    SnappyCompressorOutputStream.this.writeLiteralBlock((LZ77Compressor.LiteralBlock) block);
                } else if (i == 2) {
                    SnappyCompressorOutputStream.this.writeBackReference((LZ77Compressor.BackReference) block);
                }
            }
        });
        writeUncompressedSize(uncompressedSize);
    }

    /* renamed from: org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$commons$compress$compressors$lz77support$LZ77Compressor$Block$BlockType = new int[LZ77Compressor.Block.BlockType.values().length];

        static {
            try {
                $SwitchMap$org$apache$commons$compress$compressors$lz77support$LZ77Compressor$Block$BlockType[LZ77Compressor.Block.BlockType.LITERAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$commons$compress$compressors$lz77support$LZ77Compressor$Block$BlockType[LZ77Compressor.Block.BlockType.BACK_REFERENCE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$commons$compress$compressors$lz77support$LZ77Compressor$Block$BlockType[LZ77Compressor.Block.BlockType.EOD.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public void write(int b) throws IOException {
        byte[] bArr = this.oneByte;
        bArr[0] = (byte) (b & 255);
        write(bArr);
    }

    public void write(byte[] data, int off, int len) throws IOException {
        this.compressor.compress(data, off, len);
    }

    public void close() throws IOException {
        try {
            finish();
        } finally {
            this.os.close();
        }
    }

    public void finish() throws IOException {
        if (!this.finished) {
            this.compressor.finish();
            this.finished = true;
        }
    }

    private void writeUncompressedSize(long uncompressedSize) throws IOException {
        boolean more;
        do {
            int currentByte = (int) (127 & uncompressedSize);
            more = uncompressedSize > ((long) currentByte);
            if (more) {
                currentByte |= 128;
            }
            this.os.write(currentByte);
            uncompressedSize >>= 7;
        } while (more);
    }

    /* access modifiers changed from: private */
    public void writeLiteralBlock(LZ77Compressor.LiteralBlock block) throws IOException {
        int len = block.getLength();
        if (len <= 60) {
            writeLiteralBlockNoSizeBytes(block, len);
        } else if (len <= 256) {
            writeLiteralBlockOneSizeByte(block, len);
        } else if (len <= 65536) {
            writeLiteralBlockTwoSizeBytes(block, len);
        } else if (len <= 16777216) {
            writeLiteralBlockThreeSizeBytes(block, len);
        } else {
            writeLiteralBlockFourSizeBytes(block, len);
        }
    }

    private void writeLiteralBlockNoSizeBytes(LZ77Compressor.LiteralBlock block, int len) throws IOException {
        writeLiteralBlockWithSize((len - 1) << 2, 0, len, block);
    }

    private void writeLiteralBlockOneSizeByte(LZ77Compressor.LiteralBlock block, int len) throws IOException {
        writeLiteralBlockWithSize(ONE_SIZE_BYTE_MARKER, 1, len, block);
    }

    private void writeLiteralBlockTwoSizeBytes(LZ77Compressor.LiteralBlock block, int len) throws IOException {
        writeLiteralBlockWithSize(TWO_SIZE_BYTE_MARKER, 2, len, block);
    }

    private void writeLiteralBlockThreeSizeBytes(LZ77Compressor.LiteralBlock block, int len) throws IOException {
        writeLiteralBlockWithSize(THREE_SIZE_BYTE_MARKER, 3, len, block);
    }

    private void writeLiteralBlockFourSizeBytes(LZ77Compressor.LiteralBlock block, int len) throws IOException {
        writeLiteralBlockWithSize(FOUR_SIZE_BYTE_MARKER, 4, len, block);
    }

    private void writeLiteralBlockWithSize(int tagByte, int sizeBytes, int len, LZ77Compressor.LiteralBlock block) throws IOException {
        this.os.write(tagByte);
        writeLittleEndian(sizeBytes, len - 1);
        this.os.write(block.getData(), block.getOffset(), len);
    }

    private void writeLittleEndian(int numBytes, int num) throws IOException {
        ByteUtils.toLittleEndian(this.consumer, (long) num, numBytes);
    }

    /* access modifiers changed from: private */
    public void writeBackReference(LZ77Compressor.BackReference block) throws IOException {
        int len = block.getLength();
        int offset = block.getOffset();
        if (len >= 4 && len <= 11 && offset <= 1024) {
            writeBackReferenceWithOneOffsetByte(len, offset);
        } else if (offset < 32768) {
            writeBackReferenceWithTwoOffsetBytes(len, offset);
        } else {
            writeBackReferenceWithFourOffsetBytes(len, offset);
        }
    }

    private void writeBackReferenceWithOneOffsetByte(int len, int offset) throws IOException {
        this.os.write(((len - 4) << 2) | 1 | ((offset & 1792) >> 3));
        this.os.write(offset & 255);
    }

    private void writeBackReferenceWithTwoOffsetBytes(int len, int offset) throws IOException {
        writeBackReferenceWithLittleEndianOffset(2, 2, len, offset);
    }

    private void writeBackReferenceWithFourOffsetBytes(int len, int offset) throws IOException {
        writeBackReferenceWithLittleEndianOffset(3, 4, len, offset);
    }

    private void writeBackReferenceWithLittleEndianOffset(int tag, int offsetBytes, int len, int offset) throws IOException {
        this.os.write(((len - 1) << 2) | tag);
        writeLittleEndian(offsetBytes, offset);
    }

    public static Parameters.Builder createParameterBuilder(int blockSize) {
        return Parameters.builder(blockSize).withMinBackReferenceLength(4).withMaxBackReferenceLength(64).withMaxOffset(blockSize).withMaxLiteralLength(blockSize);
    }
}

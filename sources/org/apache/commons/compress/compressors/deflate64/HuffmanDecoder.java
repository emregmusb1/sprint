package org.apache.commons.compress.compressors.deflate64;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.apache.commons.compress.utils.BitInputStream;

class HuffmanDecoder implements Closeable {
    private static final int[] CODE_LENGTHS_ORDER = {16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15};
    /* access modifiers changed from: private */
    public static final int[] DISTANCE_TABLE = {16, 32, 48, 64, 81, 113, 146, 210, 275, 403, 532, 788, 1045, 1557, 2070, 3094, 4119, 6167, 8216, 12312, 16409, 24601, 32794, 49178, 65563, 98331, 131100, 196636, 262173, 393245, 524318, 786462};
    private static final int[] FIXED_DISTANCE = new int[32];
    private static final int[] FIXED_LITERALS = new int[288];
    /* access modifiers changed from: private */
    public static final short[] RUN_LENGTH_TABLE = {96, 128, 160, 192, 224, 256, 288, 320, 353, 417, 481, 545, 610, 738, 866, 994, 1123, 1379, 1635, 1891, 2148, 2660, 3172, 3684, 4197, 5221, 6245, 7269, 112};
    private boolean finalBlock = false;
    /* access modifiers changed from: private */
    public final InputStream in;
    /* access modifiers changed from: private */
    public final DecodingMemory memory = new DecodingMemory();
    /* access modifiers changed from: private */
    public BitInputStream reader;
    private DecoderState state;

    static {
        Arrays.fill(FIXED_LITERALS, 0, 144, 8);
        Arrays.fill(FIXED_LITERALS, 144, 256, 9);
        Arrays.fill(FIXED_LITERALS, 256, 280, 7);
        Arrays.fill(FIXED_LITERALS, 280, 288, 8);
        Arrays.fill(FIXED_DISTANCE, 5);
    }

    HuffmanDecoder(InputStream in2) {
        this.reader = new BitInputStream(in2, ByteOrder.LITTLE_ENDIAN);
        this.in = in2;
        this.state = new InitialState();
    }

    public void close() {
        this.state = new InitialState();
        this.reader = null;
    }

    public int decode(byte[] b) throws IOException {
        return decode(b, 0, b.length);
    }

    public int decode(byte[] b, int off, int len) throws IOException {
        while (true) {
            if (this.finalBlock && !this.state.hasData()) {
                return -1;
            }
            if (this.state.state() != HuffmanState.INITIAL) {
                return this.state.read(b, off, len);
            }
            this.finalBlock = readBits(1) == 1;
            int mode = (int) readBits(2);
            if (mode == 0) {
                switchToUncompressedState();
            } else if (mode == 1) {
                this.state = new HuffmanCodes(HuffmanState.FIXED_CODES, FIXED_LITERALS, FIXED_DISTANCE);
            } else if (mode == 2) {
                int[][] tables = readDynamicTables();
                this.state = new HuffmanCodes(HuffmanState.DYNAMIC_CODES, tables[0], tables[1]);
            } else {
                throw new IllegalStateException("Unsupported compression: " + mode);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public long getBytesRead() {
        return this.reader.getBytesRead();
    }

    private void switchToUncompressedState() throws IOException {
        this.reader.alignWithByteBoundary();
        long bLen = readBits(16);
        if ((65535 & (bLen ^ 65535)) == readBits(16)) {
            this.state = new UncompressedState(bLen);
            return;
        }
        throw new IllegalStateException("Illegal LEN / NLEN values");
    }

    private int[][] readDynamicTables() throws IOException {
        int[][] result = {new int[((int) (readBits(5) + 257))], new int[((int) (readBits(5) + 1))]};
        populateDynamicTables(this.reader, result[0], result[1]);
        return result;
    }

    /* access modifiers changed from: package-private */
    public int available() throws IOException {
        return this.state.available();
    }

    private static abstract class DecoderState {
        /* access modifiers changed from: package-private */
        public abstract int available() throws IOException;

        /* access modifiers changed from: package-private */
        public abstract boolean hasData();

        /* access modifiers changed from: package-private */
        public abstract int read(byte[] bArr, int i, int i2) throws IOException;

        /* access modifiers changed from: package-private */
        public abstract HuffmanState state();

        private DecoderState() {
        }
    }

    private class UncompressedState extends DecoderState {
        private final long blockLength;
        private long read;

        private UncompressedState(long blockLength2) {
            super();
            this.blockLength = blockLength2;
        }

        /* access modifiers changed from: package-private */
        public HuffmanState state() {
            return this.read < this.blockLength ? HuffmanState.STORED : HuffmanState.INITIAL;
        }

        /* access modifiers changed from: package-private */
        public int read(byte[] b, int off, int len) throws IOException {
            int readNow;
            int max = (int) Math.min(this.blockLength - this.read, (long) len);
            int readSoFar = 0;
            while (readSoFar < max) {
                if (HuffmanDecoder.this.reader.bitsCached() > 0) {
                    b[off + readSoFar] = HuffmanDecoder.this.memory.add((byte) ((int) HuffmanDecoder.this.readBits(8)));
                    readNow = 1;
                } else {
                    readNow = HuffmanDecoder.this.in.read(b, off + readSoFar, max - readSoFar);
                    if (readNow != -1) {
                        HuffmanDecoder.this.memory.add(b, off + readSoFar, readNow);
                    } else {
                        throw new EOFException("Truncated Deflate64 Stream");
                    }
                }
                this.read += (long) readNow;
                readSoFar += readNow;
            }
            return max;
        }

        /* access modifiers changed from: package-private */
        public boolean hasData() {
            return this.read < this.blockLength;
        }

        /* access modifiers changed from: package-private */
        public int available() throws IOException {
            return (int) Math.min(this.blockLength - this.read, HuffmanDecoder.this.reader.bitsAvailable() / 8);
        }
    }

    private class InitialState extends DecoderState {
        private InitialState() {
            super();
        }

        /* access modifiers changed from: package-private */
        public HuffmanState state() {
            return HuffmanState.INITIAL;
        }

        /* access modifiers changed from: package-private */
        public int read(byte[] b, int off, int len) throws IOException {
            throw new IllegalStateException("Cannot read in this state");
        }

        /* access modifiers changed from: package-private */
        public boolean hasData() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public int available() {
            return 0;
        }
    }

    private class HuffmanCodes extends DecoderState {
        private final BinaryTreeNode distanceTree;
        private boolean endOfBlock = false;
        private final BinaryTreeNode lengthTree;
        private byte[] runBuffer = new byte[0];
        private int runBufferLength = 0;
        private int runBufferPos = 0;
        private final HuffmanState state;

        HuffmanCodes(HuffmanState state2, int[] lengths, int[] distance) {
            super();
            this.state = state2;
            this.lengthTree = HuffmanDecoder.buildTree(lengths);
            this.distanceTree = HuffmanDecoder.buildTree(distance);
        }

        /* access modifiers changed from: package-private */
        public HuffmanState state() {
            return this.endOfBlock ? HuffmanState.INITIAL : this.state;
        }

        /* access modifiers changed from: package-private */
        public int read(byte[] b, int off, int len) throws IOException {
            return decodeNext(b, off, len);
        }

        private int decodeNext(byte[] b, int off, int len) throws IOException {
            byte[] bArr = b;
            int i = len;
            if (this.endOfBlock) {
                return -1;
            }
            int result = copyFromRunBuffer(b, off, len);
            while (result < i) {
                int symbol = HuffmanDecoder.nextSymbol(HuffmanDecoder.this.reader, this.lengthTree);
                if (symbol < 256) {
                    bArr[off + result] = HuffmanDecoder.this.memory.add((byte) symbol);
                    result++;
                } else if (symbol > 256) {
                    short runMask = HuffmanDecoder.RUN_LENGTH_TABLE[symbol - 257];
                    int run = (int) (((long) (runMask >>> 5)) + HuffmanDecoder.this.readBits(runMask & 31));
                    int distMask = HuffmanDecoder.DISTANCE_TABLE[HuffmanDecoder.nextSymbol(HuffmanDecoder.this.reader, this.distanceTree)];
                    int dist = (int) (((long) (distMask >>> 4)) + HuffmanDecoder.this.readBits(distMask & 15));
                    if (this.runBuffer.length < run) {
                        this.runBuffer = new byte[run];
                    }
                    this.runBufferLength = run;
                    this.runBufferPos = 0;
                    HuffmanDecoder.this.memory.recordToBuffer(dist, run, this.runBuffer);
                    result += copyFromRunBuffer(bArr, off + result, i - result);
                } else {
                    this.endOfBlock = true;
                    return result;
                }
            }
            return result;
        }

        private int copyFromRunBuffer(byte[] b, int off, int len) {
            int bytesInBuffer = this.runBufferLength - this.runBufferPos;
            if (bytesInBuffer <= 0) {
                return 0;
            }
            int copiedBytes = Math.min(len, bytesInBuffer);
            System.arraycopy(this.runBuffer, this.runBufferPos, b, off, copiedBytes);
            this.runBufferPos += copiedBytes;
            return copiedBytes;
        }

        /* access modifiers changed from: package-private */
        public boolean hasData() {
            return !this.endOfBlock;
        }

        /* access modifiers changed from: package-private */
        public int available() {
            return this.runBufferLength - this.runBufferPos;
        }
    }

    /* access modifiers changed from: private */
    public static int nextSymbol(BitInputStream reader2, BinaryTreeNode tree) throws IOException {
        BinaryTreeNode node = tree;
        while (node != null && node.literal == -1) {
            node = readBits(reader2, 1) == 0 ? node.leftNode : node.rightNode;
        }
        if (node != null) {
            return node.literal;
        }
        return -1;
    }

    private static void populateDynamicTables(BitInputStream reader2, int[] literals, int[] distances) throws IOException {
        BitInputStream bitInputStream = reader2;
        int[] iArr = literals;
        int[] iArr2 = distances;
        int codeLengths = (int) (readBits(bitInputStream, 4) + 4);
        int[] codeLengthValues = new int[19];
        for (int cLen = 0; cLen < codeLengths; cLen++) {
            codeLengthValues[CODE_LENGTHS_ORDER[cLen]] = (int) readBits(bitInputStream, 3);
        }
        BinaryTreeNode codeLengthTree = buildTree(codeLengthValues);
        int[] auxBuffer = new int[(iArr.length + iArr2.length)];
        int value = -1;
        int length = 0;
        int off = 0;
        while (off < auxBuffer.length) {
            if (length > 0) {
                auxBuffer[off] = value;
                length--;
                off++;
            } else {
                int off2 = nextSymbol(bitInputStream, codeLengthTree);
                if (off2 < 16) {
                    value = off2;
                    auxBuffer[off] = value;
                    off++;
                } else if (off2 == 16) {
                    length = (int) (readBits(bitInputStream, 2) + 3);
                } else if (off2 == 17) {
                    value = 0;
                    length = (int) (readBits(bitInputStream, 3) + 3);
                } else if (off2 == 18) {
                    value = 0;
                    length = (int) (readBits(bitInputStream, 7) + 11);
                }
            }
        }
        System.arraycopy(auxBuffer, 0, iArr, 0, iArr.length);
        System.arraycopy(auxBuffer, iArr.length, iArr2, 0, iArr2.length);
    }

    private static class BinaryTreeNode {
        private final int bits;
        BinaryTreeNode leftNode;
        int literal;
        BinaryTreeNode rightNode;

        private BinaryTreeNode(int bits2) {
            this.literal = -1;
            this.bits = bits2;
        }

        /* access modifiers changed from: package-private */
        public void leaf(int symbol) {
            this.literal = symbol;
            this.leftNode = null;
            this.rightNode = null;
        }

        /* access modifiers changed from: package-private */
        public BinaryTreeNode left() {
            if (this.leftNode == null && this.literal == -1) {
                this.leftNode = new BinaryTreeNode(this.bits + 1);
            }
            return this.leftNode;
        }

        /* access modifiers changed from: package-private */
        public BinaryTreeNode right() {
            if (this.rightNode == null && this.literal == -1) {
                this.rightNode = new BinaryTreeNode(this.bits + 1);
            }
            return this.rightNode;
        }
    }

    /* access modifiers changed from: private */
    public static BinaryTreeNode buildTree(int[] litTable) {
        int[] literalCodes = getCodes(litTable);
        BinaryTreeNode root = new BinaryTreeNode(0);
        for (int i = 0; i < litTable.length; i++) {
            int len = litTable[i];
            if (len != 0) {
                BinaryTreeNode node = root;
                int lit = literalCodes[len - 1];
                for (int p = len - 1; p >= 0; p--) {
                    node = ((1 << p) & lit) == 0 ? node.left() : node.right();
                }
                node.leaf(i);
                int i2 = len - 1;
                literalCodes[i2] = literalCodes[i2] + 1;
            }
        }
        return root;
    }

    private static int[] getCodes(int[] litTable) {
        int max = 0;
        int[] blCount = new int[65];
        for (int aLitTable : litTable) {
            max = Math.max(max, aLitTable);
            blCount[aLitTable] = blCount[aLitTable] + 1;
        }
        int[] blCount2 = Arrays.copyOf(blCount, max + 1);
        int code = 0;
        int[] nextCode = new int[(max + 1)];
        for (int i = 0; i <= max; i++) {
            code = (blCount2[i] + code) << 1;
            nextCode[i] = code;
        }
        return nextCode;
    }

    private static class DecodingMemory {
        private final int mask;
        private final byte[] memory;
        private int wHead;
        private boolean wrappedAround;

        private DecodingMemory() {
            this(16);
        }

        private DecodingMemory(int bits) {
            this.memory = new byte[(1 << bits)];
            this.mask = this.memory.length - 1;
        }

        /* access modifiers changed from: package-private */
        public byte add(byte b) {
            byte[] bArr = this.memory;
            int i = this.wHead;
            bArr[i] = b;
            this.wHead = incCounter(i);
            return b;
        }

        /* access modifiers changed from: package-private */
        public void add(byte[] b, int off, int len) {
            for (int i = off; i < off + len; i++) {
                add(b[i]);
            }
        }

        /* access modifiers changed from: package-private */
        public void recordToBuffer(int distance, int length, byte[] buff) {
            if (distance <= this.memory.length) {
                int i = this.wHead;
                int start = (i - distance) & this.mask;
                if (this.wrappedAround || start < i) {
                    int i2 = 0;
                    int pos = start;
                    while (i2 < length) {
                        buff[i2] = add(this.memory[pos]);
                        i2++;
                        pos = incCounter(pos);
                    }
                    return;
                }
                throw new IllegalStateException("Attempt to read beyond memory: dist=" + distance);
            }
            throw new IllegalStateException("Illegal distance parameter: " + distance);
        }

        private int incCounter(int counter) {
            int newCounter = (counter + 1) & this.mask;
            if (!this.wrappedAround && newCounter < counter) {
                this.wrappedAround = true;
            }
            return newCounter;
        }
    }

    /* access modifiers changed from: private */
    public long readBits(int numBits) throws IOException {
        return readBits(this.reader, numBits);
    }

    private static long readBits(BitInputStream reader2, int numBits) throws IOException {
        long r = reader2.readBits(numBits);
        if (r != -1) {
            return r;
        }
        throw new EOFException("Truncated Deflate64 Stream");
    }
}

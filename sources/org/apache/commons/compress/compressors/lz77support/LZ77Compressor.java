package org.apache.commons.compress.compressors.lz77support;

import java.io.IOException;
import java.util.Arrays;

public class LZ77Compressor {
    private static final int HASH_MASK = 32767;
    private static final int HASH_SIZE = 32768;
    private static final int H_SHIFT = 5;
    private static final int NO_MATCH = -1;
    static final int NUMBER_OF_BYTES_IN_HASH = 3;
    private static final Block THE_EOD = new EOD();
    private int blockStart = 0;
    private final Callback callback;
    private int currentPosition;
    private final int[] head;
    private boolean initialized = false;
    private int insertHash = 0;
    private int lookahead = 0;
    private int matchStart = -1;
    private int missedInserts = 0;
    private final Parameters params;
    private final int[] prev;
    private final int wMask;
    private final byte[] window;

    public static abstract class Block {

        public enum BlockType {
            LITERAL,
            BACK_REFERENCE,
            EOD
        }

        public abstract BlockType getType();
    }

    public interface Callback {
        void accept(Block block) throws IOException;
    }

    public static final class LiteralBlock extends Block {
        private final byte[] data;
        private final int length;
        private final int offset;

        public LiteralBlock(byte[] data2, int offset2, int length2) {
            this.data = data2;
            this.offset = offset2;
            this.length = length2;
        }

        public byte[] getData() {
            return this.data;
        }

        public int getOffset() {
            return this.offset;
        }

        public int getLength() {
            return this.length;
        }

        public Block.BlockType getType() {
            return Block.BlockType.LITERAL;
        }

        public String toString() {
            return "LiteralBlock starting at " + this.offset + " with length " + this.length;
        }
    }

    public static final class BackReference extends Block {
        private final int length;
        private final int offset;

        public BackReference(int offset2, int length2) {
            this.offset = offset2;
            this.length = length2;
        }

        public int getOffset() {
            return this.offset;
        }

        public int getLength() {
            return this.length;
        }

        public Block.BlockType getType() {
            return Block.BlockType.BACK_REFERENCE;
        }

        public String toString() {
            return "BackReference with offset " + this.offset + " and length " + this.length;
        }
    }

    public static final class EOD extends Block {
        public Block.BlockType getType() {
            return Block.BlockType.EOD;
        }
    }

    public LZ77Compressor(Parameters params2, Callback callback2) {
        if (params2 == null) {
            throw new NullPointerException("params must not be null");
        } else if (callback2 != null) {
            this.params = params2;
            this.callback = callback2;
            int wSize = params2.getWindowSize();
            this.window = new byte[(wSize * 2)];
            this.wMask = wSize - 1;
            this.head = new int[32768];
            Arrays.fill(this.head, -1);
            this.prev = new int[wSize];
        } else {
            throw new NullPointerException("callback must not be null");
        }
    }

    public void compress(byte[] data) throws IOException {
        compress(data, 0, data.length);
    }

    public void compress(byte[] data, int off, int len) throws IOException {
        int wSize = this.params.getWindowSize();
        while (len > wSize) {
            doCompress(data, off, wSize);
            off += wSize;
            len -= wSize;
        }
        if (len > 0) {
            doCompress(data, off, len);
        }
    }

    public void finish() throws IOException {
        if (this.blockStart != this.currentPosition || this.lookahead > 0) {
            this.currentPosition += this.lookahead;
            flushLiteralBlock();
        }
        this.callback.accept(THE_EOD);
    }

    public void prefill(byte[] data) {
        if (this.currentPosition == 0 && this.lookahead == 0) {
            int len = Math.min(this.params.getWindowSize(), data.length);
            System.arraycopy(data, data.length - len, this.window, 0, len);
            if (len >= 3) {
                initialize();
                int stop = (len - 3) + 1;
                for (int i = 0; i < stop; i++) {
                    insertString(i);
                }
                this.missedInserts = 2;
            } else {
                this.missedInserts = len;
            }
            this.currentPosition = len;
            this.blockStart = len;
            return;
        }
        throw new IllegalStateException("The compressor has already started to accept data, can't prefill anymore");
    }

    private int nextHash(int oldHash, byte nextByte) {
        return ((oldHash << 5) ^ (nextByte & 255)) & HASH_MASK;
    }

    private void doCompress(byte[] data, int off, int len) throws IOException {
        if (len > (this.window.length - this.currentPosition) - this.lookahead) {
            slide();
        }
        System.arraycopy(data, off, this.window, this.currentPosition + this.lookahead, len);
        this.lookahead += len;
        if (!this.initialized && this.lookahead >= this.params.getMinBackReferenceLength()) {
            initialize();
        }
        if (this.initialized) {
            compress();
        }
    }

    private void slide() throws IOException {
        int wSize = this.params.getWindowSize();
        int i = this.blockStart;
        if (i != this.currentPosition && i < wSize) {
            flushLiteralBlock();
            this.blockStart = this.currentPosition;
        }
        byte[] bArr = this.window;
        System.arraycopy(bArr, wSize, bArr, 0, wSize);
        this.currentPosition -= wSize;
        this.matchStart -= wSize;
        this.blockStart -= wSize;
        int i2 = 0;
        while (true) {
            int i3 = -1;
            if (i2 >= 32768) {
                break;
            }
            int[] iArr = this.head;
            int h = iArr[i2];
            if (h >= wSize) {
                i3 = h - wSize;
            }
            iArr[i2] = i3;
            i2++;
        }
        for (int i4 = 0; i4 < wSize; i4++) {
            int[] iArr2 = this.prev;
            int p = iArr2[i4];
            iArr2[i4] = p >= wSize ? p - wSize : -1;
        }
    }

    private void initialize() {
        for (int i = 0; i < 2; i++) {
            this.insertHash = nextHash(this.insertHash, this.window[i]);
        }
        this.initialized = true;
    }

    private void compress() throws IOException {
        int minMatch = this.params.getMinBackReferenceLength();
        boolean lazy = this.params.getLazyMatching();
        int lazyThreshold = this.params.getLazyMatchingThreshold();
        while (this.lookahead >= minMatch) {
            catchUpMissedInserts();
            int matchLength = 0;
            int hashHead = insertString(this.currentPosition);
            if (hashHead != -1 && hashHead - this.currentPosition <= this.params.getMaxOffset()) {
                matchLength = longestMatch(hashHead);
                if (lazy && matchLength <= lazyThreshold && this.lookahead > minMatch) {
                    matchLength = longestMatchForNextPosition(matchLength);
                }
            }
            if (matchLength >= minMatch) {
                if (this.blockStart != this.currentPosition) {
                    flushLiteralBlock();
                    this.blockStart = -1;
                }
                flushBackReference(matchLength);
                insertStringsInMatch(matchLength);
                this.lookahead -= matchLength;
                this.currentPosition += matchLength;
                this.blockStart = this.currentPosition;
            } else {
                this.lookahead--;
                this.currentPosition++;
                if (this.currentPosition - this.blockStart >= this.params.getMaxLiteralLength()) {
                    flushLiteralBlock();
                    this.blockStart = this.currentPosition;
                }
            }
        }
    }

    private int insertString(int pos) {
        this.insertHash = nextHash(this.insertHash, this.window[(pos - 1) + 3]);
        int[] iArr = this.head;
        int i = this.insertHash;
        int hashHead = iArr[i];
        this.prev[this.wMask & pos] = hashHead;
        iArr[i] = pos;
        return hashHead;
    }

    private int longestMatchForNextPosition(int prevMatchLength) {
        int prevMatchStart = this.matchStart;
        int prevInsertHash = this.insertHash;
        this.lookahead--;
        this.currentPosition++;
        int hashHead = insertString(this.currentPosition);
        int prevHashHead = this.prev[this.currentPosition & this.wMask];
        int matchLength = longestMatch(hashHead);
        if (matchLength > prevMatchLength) {
            return matchLength;
        }
        int matchLength2 = prevMatchLength;
        this.matchStart = prevMatchStart;
        this.head[this.insertHash] = prevHashHead;
        this.insertHash = prevInsertHash;
        this.currentPosition--;
        this.lookahead++;
        return matchLength2;
    }

    private void insertStringsInMatch(int matchLength) {
        int stop = Math.min(matchLength - 1, this.lookahead - 3);
        for (int i = 1; i <= stop; i++) {
            insertString(this.currentPosition + i);
        }
        this.missedInserts = (matchLength - stop) - 1;
    }

    private void catchUpMissedInserts() {
        while (true) {
            int i = this.missedInserts;
            if (i > 0) {
                int i2 = this.currentPosition;
                this.missedInserts = i - 1;
                insertString(i2 - i);
            } else {
                return;
            }
        }
    }

    private void flushBackReference(int matchLength) throws IOException {
        this.callback.accept(new BackReference(this.currentPosition - this.matchStart, matchLength));
    }

    private void flushLiteralBlock() throws IOException {
        Callback callback2 = this.callback;
        byte[] bArr = this.window;
        int i = this.blockStart;
        callback2.accept(new LiteralBlock(bArr, i, this.currentPosition - i));
    }

    private int longestMatch(int matchHead) {
        int longestMatchLength = this.params.getMinBackReferenceLength() - 1;
        int maxPossibleLength = Math.min(this.params.getMaxBackReferenceLength(), this.lookahead);
        int minIndex = Math.max(0, this.currentPosition - this.params.getMaxOffset());
        int niceBackReferenceLength = Math.min(maxPossibleLength, this.params.getNiceBackReferenceLength());
        int maxCandidates = this.params.getMaxCandidates();
        for (int candidates = 0; candidates < maxCandidates && matchHead >= minIndex; candidates++) {
            int currentLength = 0;
            for (int i = 0; i < maxPossibleLength; i++) {
                byte[] bArr = this.window;
                if (bArr[matchHead + i] != bArr[this.currentPosition + i]) {
                    break;
                }
                currentLength++;
            }
            if (currentLength > longestMatchLength) {
                longestMatchLength = currentLength;
                this.matchStart = matchHead;
                if (currentLength >= niceBackReferenceLength) {
                    break;
                }
            }
            matchHead = this.prev[this.wMask & matchHead];
        }
        return longestMatchLength;
    }
}

package org.apache.commons.compress.compressors.lz4;

import androidx.core.internal.view.SupportMenu;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;
import org.apache.commons.compress.compressors.lz77support.Parameters;
import org.apache.commons.compress.utils.ByteUtils;

public class BlockLZ4CompressorOutputStream extends CompressorOutputStream {
    private static final int MIN_BACK_REFERENCE_LENGTH = 4;
    private static final int MIN_OFFSET_OF_LAST_BACK_REFERENCE = 12;
    private final LZ77Compressor compressor;
    private Deque<byte[]> expandedBlocks;
    private boolean finished;
    private final byte[] oneByte;
    private final OutputStream os;
    private Deque<Pair> pairs;

    public BlockLZ4CompressorOutputStream(OutputStream os2) throws IOException {
        this(os2, createParameterBuilder().build());
    }

    public BlockLZ4CompressorOutputStream(OutputStream os2, Parameters params) throws IOException {
        this.oneByte = new byte[1];
        this.finished = false;
        this.pairs = new LinkedList();
        this.expandedBlocks = new LinkedList();
        this.os = os2;
        this.compressor = new LZ77Compressor(params, new LZ77Compressor.Callback() {
            public void accept(LZ77Compressor.Block block) throws IOException {
                int i = AnonymousClass2.$SwitchMap$org$apache$commons$compress$compressors$lz77support$LZ77Compressor$Block$BlockType[block.getType().ordinal()];
                if (i == 1) {
                    BlockLZ4CompressorOutputStream.this.addLiteralBlock((LZ77Compressor.LiteralBlock) block);
                } else if (i == 2) {
                    BlockLZ4CompressorOutputStream.this.addBackReference((LZ77Compressor.BackReference) block);
                } else if (i == 3) {
                    BlockLZ4CompressorOutputStream.this.writeFinalLiteralBlock();
                }
            }
        });
    }

    /* renamed from: org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream$2  reason: invalid class name */
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

    public void prefill(byte[] data, int off, int len) {
        if (len > 0) {
            byte[] b = Arrays.copyOfRange(data, off, off + len);
            this.compressor.prefill(b);
            recordLiteral(b);
        }
    }

    /* access modifiers changed from: private */
    public void addLiteralBlock(LZ77Compressor.LiteralBlock block) throws IOException {
        recordLiteral(writeBlocksAndReturnUnfinishedPair(block.getLength()).addLiteral(block));
        clearUnusedBlocksAndPairs();
    }

    /* access modifiers changed from: private */
    public void addBackReference(LZ77Compressor.BackReference block) throws IOException {
        writeBlocksAndReturnUnfinishedPair(block.getLength()).setBackReference(block);
        recordBackReference(block);
        clearUnusedBlocksAndPairs();
    }

    private Pair writeBlocksAndReturnUnfinishedPair(int length) throws IOException {
        writeWritablePairs(length);
        Pair last = this.pairs.peekLast();
        if (last != null && !last.hasBackReference()) {
            return last;
        }
        Pair last2 = new Pair();
        this.pairs.addLast(last2);
        return last2;
    }

    private void recordLiteral(byte[] b) {
        this.expandedBlocks.addFirst(b);
    }

    private void clearUnusedBlocksAndPairs() {
        clearUnusedBlocks();
        clearUnusedPairs();
    }

    private void clearUnusedBlocks() {
        int blockLengths = 0;
        int blocksToKeep = 0;
        for (byte[] b : this.expandedBlocks) {
            blocksToKeep++;
            blockLengths += b.length;
            if (blockLengths >= 65536) {
                break;
            }
        }
        int size = this.expandedBlocks.size();
        for (int i = blocksToKeep; i < size; i++) {
            this.expandedBlocks.removeLast();
        }
    }

    private void recordBackReference(LZ77Compressor.BackReference block) {
        this.expandedBlocks.addFirst(expand(block.getOffset(), block.getLength()));
    }

    private byte[] expand(int offset, int length) {
        byte[] expanded = new byte[length];
        if (offset == 1) {
            byte[] block = this.expandedBlocks.peekFirst();
            byte b = block[block.length - 1];
            if (b != 0) {
                Arrays.fill(expanded, b);
            }
        } else {
            expandFromList(expanded, offset, length);
        }
        return expanded;
    }

    private void expandFromList(byte[] expanded, int offset, int length) {
        int copyOffset;
        int blockOffset;
        int offsetRemaining = offset;
        int lengthRemaining = length;
        int writeOffset = 0;
        while (lengthRemaining > 0) {
            byte[] block = null;
            if (offsetRemaining > 0) {
                int blockOffset2 = 0;
                Iterator<byte[]> it = this.expandedBlocks.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    byte[] b = it.next();
                    if (b.length + blockOffset2 >= offsetRemaining) {
                        block = b;
                        break;
                    }
                    blockOffset2 += b.length;
                }
                if (block != null) {
                    copyOffset = (block.length + blockOffset2) - offsetRemaining;
                    blockOffset = Math.min(lengthRemaining, block.length - copyOffset);
                } else {
                    throw new IllegalStateException("Failed to find a block containing offset " + offset);
                }
            } else {
                block = expanded;
                copyOffset = -offsetRemaining;
                blockOffset = Math.min(lengthRemaining, writeOffset + offsetRemaining);
            }
            System.arraycopy(block, copyOffset, expanded, writeOffset, blockOffset);
            offsetRemaining -= blockOffset;
            lengthRemaining -= blockOffset;
            writeOffset += blockOffset;
        }
    }

    private void clearUnusedPairs() {
        int pairLengths = 0;
        int pairsToKeep = 0;
        Iterator<Pair> it = this.pairs.descendingIterator();
        while (it.hasNext()) {
            pairsToKeep++;
            pairLengths += it.next().length();
            if (pairLengths >= 65536) {
                break;
            }
        }
        int size = this.pairs.size();
        for (int i = pairsToKeep; i < size && this.pairs.peekFirst().hasBeenWritten(); i++) {
            this.pairs.removeFirst();
        }
    }

    /* access modifiers changed from: private */
    public void writeFinalLiteralBlock() throws IOException {
        rewriteLastPairs();
        for (Pair p : this.pairs) {
            if (!p.hasBeenWritten()) {
                p.writeTo(this.os);
            }
        }
        this.pairs.clear();
    }

    private void writeWritablePairs(int lengthOfBlocksAfterLastPair) throws IOException {
        int unwrittenLength = lengthOfBlocksAfterLastPair;
        Iterator<Pair> it = this.pairs.descendingIterator();
        while (it.hasNext()) {
            Pair p = it.next();
            if (p.hasBeenWritten()) {
                break;
            }
            unwrittenLength += p.length();
        }
        for (Pair p2 : this.pairs) {
            if (!p2.hasBeenWritten()) {
                unwrittenLength -= p2.length();
                if (p2.canBeWritten(unwrittenLength)) {
                    p2.writeTo(this.os);
                } else {
                    return;
                }
            }
        }
    }

    private void rewriteLastPairs() {
        LinkedList<Pair> lastPairs = new LinkedList<>();
        LinkedList<Integer> pairLength = new LinkedList<>();
        int offset = 0;
        Iterator<Pair> it = this.pairs.descendingIterator();
        while (it.hasNext()) {
            Pair p = it.next();
            if (!p.hasBeenWritten()) {
                int len = p.length();
                pairLength.addFirst(Integer.valueOf(len));
                lastPairs.addFirst(p);
                offset += len;
                if (offset >= 12) {
                    break;
                }
            } else {
                break;
            }
        }
        Iterator<Pair> it2 = lastPairs.iterator();
        while (it2.hasNext()) {
            this.pairs.remove(it2.next());
        }
        int lastPairsSize = lastPairs.size();
        int toExpand = 0;
        for (int i = 1; i < lastPairsSize; i++) {
            toExpand += pairLength.get(i).intValue();
        }
        Pair replacement = new Pair();
        if (toExpand > 0) {
            replacement.prependLiteral(expand(toExpand, toExpand));
        }
        int brLen = 0;
        Pair splitCandidate = lastPairs.get(0);
        int stillNeeded = 12 - toExpand;
        if (splitCandidate.hasBackReference()) {
            brLen = splitCandidate.backReferenceLength();
        }
        if (!splitCandidate.hasBackReference() || brLen < stillNeeded + 4) {
            if (splitCandidate.hasBackReference()) {
                replacement.prependLiteral(expand(toExpand + brLen, brLen));
            }
            splitCandidate.prependTo(replacement);
        } else {
            replacement.prependLiteral(expand(toExpand + stillNeeded, stillNeeded));
            this.pairs.add(splitCandidate.splitWithNewBackReferenceLengthOf(brLen - stillNeeded));
        }
        this.pairs.add(replacement);
    }

    public static Parameters.Builder createParameterBuilder() {
        return Parameters.builder(65536).withMinBackReferenceLength(4).withMaxBackReferenceLength(SupportMenu.USER_MASK).withMaxOffset(SupportMenu.USER_MASK).withMaxLiteralLength(SupportMenu.USER_MASK);
    }

    static final class Pair {
        private int brLength;
        private int brOffset;
        private final Deque<byte[]> literals = new LinkedList();
        private boolean written;

        Pair() {
        }

        /* access modifiers changed from: private */
        public void prependLiteral(byte[] data) {
            this.literals.addFirst(data);
        }

        /* access modifiers changed from: package-private */
        public byte[] addLiteral(LZ77Compressor.LiteralBlock block) {
            byte[] copy = Arrays.copyOfRange(block.getData(), block.getOffset(), block.getOffset() + block.getLength());
            this.literals.add(copy);
            return copy;
        }

        /* access modifiers changed from: package-private */
        public void setBackReference(LZ77Compressor.BackReference block) {
            if (!hasBackReference()) {
                this.brOffset = block.getOffset();
                this.brLength = block.getLength();
                return;
            }
            throw new IllegalStateException();
        }

        /* access modifiers changed from: package-private */
        public boolean hasBackReference() {
            return this.brOffset > 0;
        }

        /* access modifiers changed from: package-private */
        public boolean canBeWritten(int lengthOfBlocksAfterThisPair) {
            return hasBackReference() && lengthOfBlocksAfterThisPair >= 16;
        }

        /* access modifiers changed from: package-private */
        public int length() {
            return literalLength() + this.brLength;
        }

        /* access modifiers changed from: private */
        public boolean hasBeenWritten() {
            return this.written;
        }

        /* access modifiers changed from: package-private */
        public void writeTo(OutputStream out) throws IOException {
            int litLength = literalLength();
            out.write(lengths(litLength, this.brLength));
            if (litLength >= 15) {
                writeLength(litLength - 15, out);
            }
            for (byte[] b : this.literals) {
                out.write(b);
            }
            if (hasBackReference()) {
                ByteUtils.toLittleEndian(out, (long) this.brOffset, 2);
                int i = this.brLength;
                if (i - 4 >= 15) {
                    writeLength((i - 4) - 15, out);
                }
            }
            this.written = true;
        }

        private int literalLength() {
            int length = 0;
            for (byte[] b : this.literals) {
                length += b.length;
            }
            return length;
        }

        private static int lengths(int litLength, int brLength2) {
            int br = 15;
            int l = litLength < 15 ? litLength : 15;
            if (brLength2 < 4) {
                br = 0;
            } else if (brLength2 < 19) {
                br = brLength2 - 4;
            }
            return (l << 4) | br;
        }

        private static void writeLength(int length, OutputStream out) throws IOException {
            while (length >= 255) {
                out.write(255);
                length -= 255;
            }
            out.write(length);
        }

        /* access modifiers changed from: private */
        public int backReferenceLength() {
            return this.brLength;
        }

        /* access modifiers changed from: private */
        public void prependTo(Pair other) {
            Iterator<byte[]> listBackwards = this.literals.descendingIterator();
            while (listBackwards.hasNext()) {
                other.prependLiteral(listBackwards.next());
            }
        }

        /* access modifiers changed from: private */
        public Pair splitWithNewBackReferenceLengthOf(int newBackReferenceLength) {
            Pair p = new Pair();
            p.literals.addAll(this.literals);
            p.brOffset = this.brOffset;
            p.brLength = newBackReferenceLength;
            return p;
        }
    }
}

package org.apache.commons.compress.compressors.lz4;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.lz77support.AbstractLZ77CompressorInputStream;
import org.apache.commons.compress.utils.ByteUtils;

public class BlockLZ4CompressorInputStream extends AbstractLZ77CompressorInputStream {
    static final int BACK_REFERENCE_SIZE_MASK = 15;
    static final int LITERAL_SIZE_MASK = 240;
    static final int SIZE_BITS = 4;
    static final int WINDOW_SIZE = 65536;
    private int nextBackReferenceSize;
    private State state = State.NO_BLOCK;

    private enum State {
        NO_BLOCK,
        IN_LITERAL,
        LOOKING_FOR_BACK_REFERENCE,
        IN_BACK_REFERENCE,
        EOF
    }

    public BlockLZ4CompressorInputStream(InputStream is) throws IOException {
        super(is, 65536);
    }

    /* renamed from: org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$commons$compress$compressors$lz4$BlockLZ4CompressorInputStream$State = new int[State.values().length];

        static {
            try {
                $SwitchMap$org$apache$commons$compress$compressors$lz4$BlockLZ4CompressorInputStream$State[State.EOF.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$commons$compress$compressors$lz4$BlockLZ4CompressorInputStream$State[State.NO_BLOCK.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$commons$compress$compressors$lz4$BlockLZ4CompressorInputStream$State[State.IN_LITERAL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$commons$compress$compressors$lz4$BlockLZ4CompressorInputStream$State[State.LOOKING_FOR_BACK_REFERENCE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$commons$compress$compressors$lz4$BlockLZ4CompressorInputStream$State[State.IN_BACK_REFERENCE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int i = AnonymousClass1.$SwitchMap$org$apache$commons$compress$compressors$lz4$BlockLZ4CompressorInputStream$State[this.state.ordinal()];
        if (i == 1) {
            return -1;
        }
        if (i == 2) {
            readSizes();
        } else if (i != 3) {
            if (i != 4) {
                if (i != 5) {
                    throw new IOException("Unknown stream state " + this.state);
                }
            } else if (!initializeBackReference()) {
                this.state = State.EOF;
                return -1;
            }
            int backReferenceLen = readBackReference(b, off, len);
            if (!hasMoreDataInBlock()) {
                this.state = State.NO_BLOCK;
            }
            return backReferenceLen > 0 ? backReferenceLen : read(b, off, len);
        }
        int litLen = readLiteral(b, off, len);
        if (!hasMoreDataInBlock()) {
            this.state = State.LOOKING_FOR_BACK_REFERENCE;
        }
        return litLen > 0 ? litLen : read(b, off, len);
    }

    private void readSizes() throws IOException {
        int nextBlock = readOneByte();
        if (nextBlock != -1) {
            this.nextBackReferenceSize = nextBlock & 15;
            long literalSizePart = (long) ((nextBlock & LITERAL_SIZE_MASK) >> 4);
            if (literalSizePart == 15) {
                literalSizePart += readSizeBytes();
            }
            if (literalSizePart >= 0) {
                startLiteral(literalSizePart);
                this.state = State.IN_LITERAL;
                return;
            }
            throw new IOException("Illegal block with a negative literal size found");
        }
        throw new IOException("Premature end of stream while looking for next block");
    }

    private long readSizeBytes() throws IOException {
        int nextByte;
        long accum = 0;
        do {
            nextByte = readOneByte();
            if (nextByte != -1) {
                accum += (long) nextByte;
            } else {
                throw new IOException("Premature end of stream while parsing length");
            }
        } while (nextByte == 255);
        return accum;
    }

    private boolean initializeBackReference() throws IOException {
        try {
            int backReferenceOffset = (int) ByteUtils.fromLittleEndian(this.supplier, 2);
            int i = this.nextBackReferenceSize;
            long backReferenceSize = (long) i;
            if (i == 15) {
                backReferenceSize += readSizeBytes();
            }
            if (backReferenceSize >= 0) {
                try {
                    startBackReference(backReferenceOffset, 4 + backReferenceSize);
                    this.state = State.IN_BACK_REFERENCE;
                    return true;
                } catch (IllegalArgumentException ex) {
                    throw new IOException("Illegal block with bad offset found", ex);
                }
            } else {
                throw new IOException("Illegal block with a negative match length found");
            }
        } catch (IOException ex2) {
            if (this.nextBackReferenceSize == 0) {
                return false;
            }
            throw ex2;
        }
    }
}

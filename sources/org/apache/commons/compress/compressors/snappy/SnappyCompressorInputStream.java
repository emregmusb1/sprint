package org.apache.commons.compress.compressors.snappy;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.lz77support.AbstractLZ77CompressorInputStream;
import org.apache.commons.compress.utils.ByteUtils;

public class SnappyCompressorInputStream extends AbstractLZ77CompressorInputStream {
    public static final int DEFAULT_BLOCK_SIZE = 32768;
    private static final int TAG_MASK = 3;
    private boolean endReached;
    private final int size;
    private State state;
    private int uncompressedBytesRemaining;

    private enum State {
        NO_BLOCK,
        IN_LITERAL,
        IN_BACK_REFERENCE
    }

    public SnappyCompressorInputStream(InputStream is) throws IOException {
        this(is, 32768);
    }

    public SnappyCompressorInputStream(InputStream is, int blockSize) throws IOException {
        super(is, blockSize);
        this.state = State.NO_BLOCK;
        this.endReached = false;
        int readSize = (int) readSize();
        this.size = readSize;
        this.uncompressedBytesRemaining = readSize;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.endReached) {
            return -1;
        }
        int i = AnonymousClass1.$SwitchMap$org$apache$commons$compress$compressors$snappy$SnappyCompressorInputStream$State[this.state.ordinal()];
        if (i == 1) {
            fill();
            return read(b, off, len);
        } else if (i == 2) {
            int litLen = readLiteral(b, off, len);
            if (!hasMoreDataInBlock()) {
                this.state = State.NO_BLOCK;
            }
            return litLen > 0 ? litLen : read(b, off, len);
        } else if (i == 3) {
            int backReferenceLen = readBackReference(b, off, len);
            if (!hasMoreDataInBlock()) {
                this.state = State.NO_BLOCK;
            }
            return backReferenceLen > 0 ? backReferenceLen : read(b, off, len);
        } else {
            throw new IOException("Unknown stream state " + this.state);
        }
    }

    /* renamed from: org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$commons$compress$compressors$snappy$SnappyCompressorInputStream$State = new int[State.values().length];

        static {
            try {
                $SwitchMap$org$apache$commons$compress$compressors$snappy$SnappyCompressorInputStream$State[State.NO_BLOCK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$commons$compress$compressors$snappy$SnappyCompressorInputStream$State[State.IN_LITERAL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$commons$compress$compressors$snappy$SnappyCompressorInputStream$State[State.IN_BACK_REFERENCE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private void fill() throws IOException {
        if (this.uncompressedBytesRemaining == 0) {
            this.endReached = true;
            return;
        }
        int b = readOneByte();
        if (b != -1) {
            int i = b & 3;
            if (i == 0) {
                int length = readLiteralLength(b);
                if (length >= 0) {
                    this.uncompressedBytesRemaining -= length;
                    startLiteral((long) length);
                    this.state = State.IN_LITERAL;
                    return;
                }
                throw new IOException("Illegal block with a negative literal size found");
            } else if (i == 1) {
                int length2 = ((b >> 2) & 7) + 4;
                if (length2 >= 0) {
                    this.uncompressedBytesRemaining -= length2;
                    int offset = (b & 224) << 3;
                    int b2 = readOneByte();
                    if (b2 != -1) {
                        try {
                            startBackReference(offset | b2, (long) length2);
                            this.state = State.IN_BACK_REFERENCE;
                        } catch (IllegalArgumentException ex) {
                            throw new IOException("Illegal block with bad offset found", ex);
                        }
                    } else {
                        throw new IOException("Premature end of stream reading back-reference length");
                    }
                } else {
                    throw new IOException("Illegal block with a negative match length found");
                }
            } else if (i == 2) {
                int length3 = (b >> 2) + 1;
                if (length3 >= 0) {
                    this.uncompressedBytesRemaining -= length3;
                    try {
                        startBackReference((int) ByteUtils.fromLittleEndian(this.supplier, 2), (long) length3);
                        this.state = State.IN_BACK_REFERENCE;
                    } catch (IllegalArgumentException ex2) {
                        throw new IOException("Illegal block with bad offset found", ex2);
                    }
                } else {
                    throw new IOException("Illegal block with a negative match length found");
                }
            } else if (i == 3) {
                int length4 = (b >> 2) + 1;
                if (length4 >= 0) {
                    this.uncompressedBytesRemaining -= length4;
                    try {
                        startBackReference(((int) ByteUtils.fromLittleEndian(this.supplier, 4)) & Integer.MAX_VALUE, (long) length4);
                        this.state = State.IN_BACK_REFERENCE;
                    } catch (IllegalArgumentException ex3) {
                        throw new IOException("Illegal block with bad offset found", ex3);
                    }
                } else {
                    throw new IOException("Illegal block with a negative match length found");
                }
            }
        } else {
            throw new IOException("Premature end of stream reading block start");
        }
    }

    private int readLiteralLength(int b) throws IOException {
        int length;
        switch (b >> 2) {
            case 60:
                length = readOneByte();
                if (length == -1) {
                    throw new IOException("Premature end of stream reading literal length");
                }
                break;
            case 61:
                length = (int) ByteUtils.fromLittleEndian(this.supplier, 2);
                break;
            case 62:
                length = (int) ByteUtils.fromLittleEndian(this.supplier, 3);
                break;
            case 63:
                length = (int) ByteUtils.fromLittleEndian(this.supplier, 4);
                break;
            default:
                length = b >> 2;
                break;
        }
        return length + 1;
    }

    private long readSize() throws IOException {
        int index = 0;
        long sz = 0;
        while (true) {
            int b = readOneByte();
            if (b != -1) {
                int index2 = index + 1;
                sz |= (long) ((b & 127) << (index * 7));
                if ((b & 128) == 0) {
                    return sz;
                }
                index = index2;
            } else {
                throw new IOException("Premature end of stream reading size");
            }
        }
    }

    public int getSize() {
        return this.size;
    }
}

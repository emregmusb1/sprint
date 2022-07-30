package org.apache.commons.compress.archivers.dump;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.apache.commons.compress.archivers.dump.DumpArchiveConstants;
import org.apache.commons.compress.utils.IOUtils;

class TapeInputStream extends FilterInputStream {
    private static final int RECORD_SIZE = 1024;
    private byte[] blockBuffer = new byte[1024];
    private int blockSize = 1024;
    private long bytesRead = 0;
    private int currBlkIdx = -1;
    private boolean isCompressed = false;
    private int readOffset = 1024;

    public TapeInputStream(InputStream in) {
        super(in);
    }

    public void resetBlockSize(int recsPerBlock, boolean isCompressed2) throws IOException {
        this.isCompressed = isCompressed2;
        if (recsPerBlock >= 1) {
            this.blockSize = recsPerBlock * 1024;
            byte[] oldBuffer = this.blockBuffer;
            this.blockBuffer = new byte[this.blockSize];
            System.arraycopy(oldBuffer, 0, this.blockBuffer, 0, 1024);
            readFully(this.blockBuffer, 1024, this.blockSize - 1024);
            this.currBlkIdx = 0;
            this.readOffset = 1024;
            return;
        }
        throw new IOException("Block with " + recsPerBlock + " records found, must be at least 1");
    }

    public int available() throws IOException {
        int i = this.readOffset;
        int i2 = this.blockSize;
        if (i < i2) {
            return i2 - i;
        }
        return this.in.available();
    }

    public int read() throws IOException {
        throw new IllegalArgumentException("All reads must be multiple of record size (1024 bytes.");
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int n;
        if (len % 1024 == 0) {
            int bytes = 0;
            while (bytes < len) {
                if (this.readOffset == this.blockSize) {
                    try {
                        readBlock(true);
                    } catch (ShortFileException e) {
                        return -1;
                    }
                }
                int i = this.readOffset;
                int i2 = (len - bytes) + i;
                int i3 = this.blockSize;
                if (i2 <= i3) {
                    n = len - bytes;
                } else {
                    n = i3 - i;
                }
                System.arraycopy(this.blockBuffer, this.readOffset, b, off, n);
                this.readOffset += n;
                bytes += n;
                off += n;
            }
            return bytes;
        }
        throw new IllegalArgumentException("All reads must be multiple of record size (1024 bytes.");
    }

    public long skip(long len) throws IOException {
        long n;
        if (len % PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID == 0) {
            long bytes = 0;
            while (bytes < len) {
                int i = this.readOffset;
                int i2 = this.blockSize;
                if (i == i2) {
                    try {
                        readBlock(len - bytes < ((long) i2));
                    } catch (ShortFileException e) {
                        return -1;
                    }
                }
                int i3 = this.readOffset;
                long j = ((long) i3) + (len - bytes);
                int i4 = this.blockSize;
                if (j <= ((long) i4)) {
                    n = len - bytes;
                } else {
                    n = ((long) i4) - ((long) i3);
                }
                this.readOffset = (int) (((long) this.readOffset) + n);
                bytes += n;
            }
            return bytes;
        }
        throw new IllegalArgumentException("All reads must be multiple of record size (1024 bytes.");
    }

    public void close() throws IOException {
        if (this.in != null && this.in != System.in) {
            this.in.close();
        }
    }

    public byte[] peek() throws IOException {
        if (this.readOffset == this.blockSize) {
            try {
                readBlock(true);
            } catch (ShortFileException e) {
                return null;
            }
        }
        byte[] b = new byte[1024];
        System.arraycopy(this.blockBuffer, this.readOffset, b, 0, b.length);
        return b;
    }

    public byte[] readRecord() throws IOException {
        byte[] result = new byte[1024];
        if (-1 != read(result, 0, result.length)) {
            return result;
        }
        throw new ShortFileException();
    }

    private void readBlock(boolean decompress) throws IOException {
        if (this.in != null) {
            if (!this.isCompressed || this.currBlkIdx == -1) {
                readFully(this.blockBuffer, 0, this.blockSize);
                this.bytesRead += (long) this.blockSize;
            } else {
                readFully(this.blockBuffer, 0, 4);
                this.bytesRead += 4;
                int h = DumpArchiveUtil.convert32(this.blockBuffer, 0);
                if (!((h & 1) == 1)) {
                    readFully(this.blockBuffer, 0, this.blockSize);
                    this.bytesRead += (long) this.blockSize;
                } else {
                    int flags = (h >> 1) & 7;
                    int length = (h >> 4) & 268435455;
                    byte[] compBuffer = new byte[length];
                    readFully(compBuffer, 0, length);
                    this.bytesRead += (long) length;
                    if (!decompress) {
                        Arrays.fill(this.blockBuffer, (byte) 0);
                    } else {
                        int i = AnonymousClass1.$SwitchMap$org$apache$commons$compress$archivers$dump$DumpArchiveConstants$COMPRESSION_TYPE[DumpArchiveConstants.COMPRESSION_TYPE.find(flags & 3).ordinal()];
                        if (i == 1) {
                            Inflater inflator = new Inflater();
                            try {
                                inflator.setInput(compBuffer, 0, compBuffer.length);
                                if (inflator.inflate(this.blockBuffer) == this.blockSize) {
                                    inflator.end();
                                } else {
                                    throw new ShortFileException();
                                }
                            } catch (DataFormatException e) {
                                throw new DumpArchiveException("Bad data", e);
                            } catch (Throwable th) {
                                inflator.end();
                                throw th;
                            }
                        } else if (i == 2) {
                            throw new UnsupportedCompressionAlgorithmException("BZLIB2");
                        } else if (i != 3) {
                            throw new UnsupportedCompressionAlgorithmException();
                        } else {
                            throw new UnsupportedCompressionAlgorithmException("LZO");
                        }
                    }
                }
            }
            this.currBlkIdx++;
            this.readOffset = 0;
            return;
        }
        throw new IOException("Input buffer is closed");
    }

    /* renamed from: org.apache.commons.compress.archivers.dump.TapeInputStream$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$commons$compress$archivers$dump$DumpArchiveConstants$COMPRESSION_TYPE = new int[DumpArchiveConstants.COMPRESSION_TYPE.values().length];

        static {
            try {
                $SwitchMap$org$apache$commons$compress$archivers$dump$DumpArchiveConstants$COMPRESSION_TYPE[DumpArchiveConstants.COMPRESSION_TYPE.ZLIB.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$commons$compress$archivers$dump$DumpArchiveConstants$COMPRESSION_TYPE[DumpArchiveConstants.COMPRESSION_TYPE.BZLIB.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$commons$compress$archivers$dump$DumpArchiveConstants$COMPRESSION_TYPE[DumpArchiveConstants.COMPRESSION_TYPE.LZO.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private void readFully(byte[] b, int off, int len) throws IOException {
        if (IOUtils.readFully(this.in, b, off, len) < len) {
            throw new ShortFileException();
        }
    }

    public long getBytesRead() {
        return this.bytesRead;
    }
}

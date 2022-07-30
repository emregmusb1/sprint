package org.apache.commons.compress.compressors.gzip;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;

public class GzipCompressorInputStream extends CompressorInputStream implements InputStreamStatistics {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int FCOMMENT = 16;
    private static final int FEXTRA = 4;
    private static final int FHCRC = 2;
    private static final int FNAME = 8;
    private static final int FRESERVED = 224;
    private final byte[] buf;
    private int bufUsed;
    private final CountingInputStream countingStream;
    private final CRC32 crc;
    private final boolean decompressConcatenated;
    private boolean endReached;
    private final InputStream in;
    private Inflater inf;
    private final byte[] oneByte;
    private final GzipParameters parameters;

    public GzipCompressorInputStream(InputStream inputStream) throws IOException {
        this(inputStream, false);
    }

    public GzipCompressorInputStream(InputStream inputStream, boolean decompressConcatenated2) throws IOException {
        this.buf = new byte[8192];
        this.inf = new Inflater(true);
        this.crc = new CRC32();
        this.endReached = false;
        this.oneByte = new byte[1];
        this.parameters = new GzipParameters();
        this.countingStream = new CountingInputStream(inputStream);
        if (this.countingStream.markSupported()) {
            this.in = this.countingStream;
        } else {
            this.in = new BufferedInputStream(this.countingStream);
        }
        this.decompressConcatenated = decompressConcatenated2;
        init(true);
    }

    public GzipParameters getMetaData() {
        return this.parameters;
    }

    private boolean init(boolean isFirstMember) throws IOException {
        int magic0 = this.in.read();
        if (magic0 == -1 && !isFirstMember) {
            return false;
        }
        if (magic0 == 31 && this.in.read() == 139) {
            DataInput inData = new DataInputStream(this.in);
            int method = inData.readUnsignedByte();
            if (method == 8) {
                int flg = inData.readUnsignedByte();
                if ((flg & FRESERVED) == 0) {
                    this.parameters.setModificationTime(ByteUtils.fromLittleEndian(inData, 4) * 1000);
                    int readUnsignedByte = inData.readUnsignedByte();
                    if (readUnsignedByte == 2) {
                        this.parameters.setCompressionLevel(9);
                    } else if (readUnsignedByte == 4) {
                        this.parameters.setCompressionLevel(1);
                    }
                    this.parameters.setOperatingSystem(inData.readUnsignedByte());
                    if ((flg & 4) != 0) {
                        int xlen = (inData.readUnsignedByte() << 8) | inData.readUnsignedByte();
                        while (true) {
                            int xlen2 = xlen - 1;
                            if (xlen <= 0) {
                                break;
                            }
                            inData.readUnsignedByte();
                            xlen = xlen2;
                        }
                    }
                    if ((flg & 8) != 0) {
                        this.parameters.setFilename(new String(readToNull(inData), CharsetNames.ISO_8859_1));
                    }
                    if ((flg & 16) != 0) {
                        this.parameters.setComment(new String(readToNull(inData), CharsetNames.ISO_8859_1));
                    }
                    if ((flg & 2) != 0) {
                        inData.readShort();
                    }
                    this.inf.reset();
                    this.crc.reset();
                    return true;
                }
                throw new IOException("Reserved flags are set in the .gz header");
            }
            throw new IOException("Unsupported compression method " + method + " in the .gz header");
        }
        throw new IOException(isFirstMember ? "Input is not in the .gz format" : "Garbage after a valid .gz stream");
    }

    private static byte[] readToNull(DataInput inData) throws IOException {
        Throwable th;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (true) {
            try {
                int readUnsignedByte = inData.readUnsignedByte();
                int b = readUnsignedByte;
                if (readUnsignedByte != 0) {
                    bos.write(b);
                } else {
                    byte[] byteArray = bos.toByteArray();
                    bos.close();
                    return byteArray;
                }
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
        }
        throw th;
    }

    public int read() throws IOException {
        if (read(this.oneByte, 0, 1) == -1) {
            return -1;
        }
        return this.oneByte[0] & 255;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        byte[] bArr = b;
        if (this.endReached) {
            return -1;
        }
        int off2 = off;
        int len2 = len;
        int size = 0;
        while (len2 > 0) {
            if (this.inf.needsInput()) {
                this.in.mark(this.buf.length);
                this.bufUsed = this.in.read(this.buf);
                int i = this.bufUsed;
                if (i != -1) {
                    this.inf.setInput(this.buf, 0, i);
                } else {
                    throw new EOFException();
                }
            }
            try {
                int ret = this.inf.inflate(bArr, off2, len2);
                this.crc.update(bArr, off2, ret);
                off2 += ret;
                len2 -= ret;
                size += ret;
                count(ret);
                if (this.inf.finished()) {
                    this.in.reset();
                    int skipAmount = this.bufUsed - this.inf.getRemaining();
                    if (IOUtils.skip(this.in, (long) skipAmount) == ((long) skipAmount)) {
                        this.bufUsed = 0;
                        DataInput inData = new DataInputStream(this.in);
                        if (ByteUtils.fromLittleEndian(inData, 4) != this.crc.getValue()) {
                            throw new IOException("Gzip-compressed data is corrupt (CRC32 error)");
                        } else if (ByteUtils.fromLittleEndian(inData, 4) != (this.inf.getBytesWritten() & 4294967295L)) {
                            throw new IOException("Gzip-compressed data is corrupt(uncompressed size mismatch)");
                        } else if (!this.decompressConcatenated || !init(false)) {
                            this.inf.end();
                            this.inf = null;
                            this.endReached = true;
                            if (size == 0) {
                                return -1;
                            }
                            return size;
                        }
                    } else {
                        throw new IOException();
                    }
                }
            } catch (DataFormatException e) {
                throw new IOException("Gzip-compressed data is corrupt");
            }
        }
        return size;
    }

    public static boolean matches(byte[] signature, int length) {
        return length >= 2 && signature[0] == 31 && signature[1] == -117;
    }

    public void close() throws IOException {
        Inflater inflater = this.inf;
        if (inflater != null) {
            inflater.end();
            this.inf = null;
        }
        if (this.in != System.in) {
            this.in.close();
        }
    }

    public long getCompressedCount() {
        return this.countingStream.getBytesRead();
    }
}

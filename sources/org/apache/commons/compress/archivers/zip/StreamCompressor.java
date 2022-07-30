package org.apache.commons.compress.archivers.zip;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import org.apache.commons.compress.parallel.ScatterGatherBackingStore;

public abstract class StreamCompressor implements Closeable {
    private static final int BUFFER_SIZE = 4096;
    private static final int DEFLATER_BLOCK_SIZE = 8192;
    private final CRC32 crc = new CRC32();
    private final Deflater def;
    private final byte[] outputBuffer = new byte[4096];
    private final byte[] readerBuf = new byte[4096];
    private long sourcePayloadLength = 0;
    private long totalWrittenToOutputStream = 0;
    private long writtenToOutputStreamForLastEntry = 0;

    /* access modifiers changed from: protected */
    public abstract void writeOut(byte[] bArr, int i, int i2) throws IOException;

    StreamCompressor(Deflater deflater) {
        this.def = deflater;
    }

    static StreamCompressor create(OutputStream os, Deflater deflater) {
        return new OutputStreamCompressor(deflater, os);
    }

    static StreamCompressor create(OutputStream os) {
        return create(os, new Deflater(-1, true));
    }

    static StreamCompressor create(DataOutput os, Deflater deflater) {
        return new DataOutputCompressor(deflater, os);
    }

    static StreamCompressor create(SeekableByteChannel os, Deflater deflater) {
        return new SeekableByteChannelCompressor(deflater, os);
    }

    public static StreamCompressor create(int compressionLevel, ScatterGatherBackingStore bs) {
        return new ScatterGatherBackingStoreCompressor(new Deflater(compressionLevel, true), bs);
    }

    public static StreamCompressor create(ScatterGatherBackingStore bs) {
        return create(-1, bs);
    }

    public long getCrc32() {
        return this.crc.getValue();
    }

    public long getBytesRead() {
        return this.sourcePayloadLength;
    }

    public long getBytesWrittenForLastEntry() {
        return this.writtenToOutputStreamForLastEntry;
    }

    public long getTotalBytesWritten() {
        return this.totalWrittenToOutputStream;
    }

    public void deflate(InputStream source, int method) throws IOException {
        reset();
        while (true) {
            byte[] bArr = this.readerBuf;
            int read = source.read(bArr, 0, bArr.length);
            int length = read;
            if (read < 0) {
                break;
            }
            write(this.readerBuf, 0, length, method);
        }
        if (method == 8) {
            flushDeflater();
        }
    }

    /* access modifiers changed from: package-private */
    public long write(byte[] b, int offset, int length, int method) throws IOException {
        long current = this.writtenToOutputStreamForLastEntry;
        this.crc.update(b, offset, length);
        if (method == 8) {
            writeDeflated(b, offset, length);
        } else {
            writeCounted(b, offset, length);
        }
        this.sourcePayloadLength += (long) length;
        return this.writtenToOutputStreamForLastEntry - current;
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        this.crc.reset();
        this.def.reset();
        this.sourcePayloadLength = 0;
        this.writtenToOutputStreamForLastEntry = 0;
    }

    public void close() throws IOException {
        this.def.end();
    }

    /* access modifiers changed from: package-private */
    public void flushDeflater() throws IOException {
        this.def.finish();
        while (!this.def.finished()) {
            deflate();
        }
    }

    private void writeDeflated(byte[] b, int offset, int length) throws IOException {
        if (length > 0 && !this.def.finished()) {
            if (length <= 8192) {
                this.def.setInput(b, offset, length);
                deflateUntilInputIsNeeded();
                return;
            }
            int fullblocks = length / 8192;
            for (int i = 0; i < fullblocks; i++) {
                this.def.setInput(b, (i * 8192) + offset, 8192);
                deflateUntilInputIsNeeded();
            }
            int done = fullblocks * 8192;
            if (done < length) {
                this.def.setInput(b, offset + done, length - done);
                deflateUntilInputIsNeeded();
            }
        }
    }

    private void deflateUntilInputIsNeeded() throws IOException {
        while (!this.def.needsInput()) {
            deflate();
        }
    }

    /* access modifiers changed from: package-private */
    public void deflate() throws IOException {
        Deflater deflater = this.def;
        byte[] bArr = this.outputBuffer;
        int len = deflater.deflate(bArr, 0, bArr.length);
        if (len > 0) {
            writeCounted(this.outputBuffer, 0, len);
        }
    }

    public void writeCounted(byte[] data) throws IOException {
        writeCounted(data, 0, data.length);
    }

    public void writeCounted(byte[] data, int offset, int length) throws IOException {
        writeOut(data, offset, length);
        this.writtenToOutputStreamForLastEntry += (long) length;
        this.totalWrittenToOutputStream += (long) length;
    }

    private static final class ScatterGatherBackingStoreCompressor extends StreamCompressor {
        private final ScatterGatherBackingStore bs;

        public ScatterGatherBackingStoreCompressor(Deflater deflater, ScatterGatherBackingStore bs2) {
            super(deflater);
            this.bs = bs2;
        }

        /* access modifiers changed from: protected */
        public final void writeOut(byte[] data, int offset, int length) throws IOException {
            this.bs.writeOut(data, offset, length);
        }
    }

    private static final class OutputStreamCompressor extends StreamCompressor {
        private final OutputStream os;

        public OutputStreamCompressor(Deflater deflater, OutputStream os2) {
            super(deflater);
            this.os = os2;
        }

        /* access modifiers changed from: protected */
        public final void writeOut(byte[] data, int offset, int length) throws IOException {
            this.os.write(data, offset, length);
        }
    }

    private static final class DataOutputCompressor extends StreamCompressor {
        private final DataOutput raf;

        public DataOutputCompressor(Deflater deflater, DataOutput raf2) {
            super(deflater);
            this.raf = raf2;
        }

        /* access modifiers changed from: protected */
        public final void writeOut(byte[] data, int offset, int length) throws IOException {
            this.raf.write(data, offset, length);
        }
    }

    private static final class SeekableByteChannelCompressor extends StreamCompressor {
        private final SeekableByteChannel channel;

        public SeekableByteChannelCompressor(Deflater deflater, SeekableByteChannel channel2) {
            super(deflater);
            this.channel = channel2;
        }

        /* access modifiers changed from: protected */
        public final void writeOut(byte[] data, int offset, int length) throws IOException {
            this.channel.write(ByteBuffer.wrap(data, offset, length));
        }
    }
}

package org.apache.commons.compress.archivers.sevenz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import org.apache.commons.compress.utils.FlushShieldFilterOutputStream;
import org.tukaani.xz.ARMOptions;
import org.tukaani.xz.ARMThumbOptions;
import org.tukaani.xz.FilterOptions;
import org.tukaani.xz.FinishableWrapperOutputStream;
import org.tukaani.xz.IA64Options;
import org.tukaani.xz.PowerPCOptions;
import org.tukaani.xz.SPARCOptions;
import org.tukaani.xz.X86Options;

class Coders {
    private static final Map<SevenZMethod, CoderBase> CODER_MAP = new HashMap<SevenZMethod, CoderBase>() {
        private static final long serialVersionUID = 1664829131806520867L;

        {
            put(SevenZMethod.COPY, new CopyDecoder());
            put(SevenZMethod.LZMA, new LZMADecoder());
            put(SevenZMethod.LZMA2, new LZMA2Decoder());
            put(SevenZMethod.DEFLATE, new DeflateDecoder());
            put(SevenZMethod.DEFLATE64, new Deflate64Decoder());
            put(SevenZMethod.BZIP2, new BZIP2Decoder());
            put(SevenZMethod.AES256SHA256, new AES256SHA256Decoder());
            put(SevenZMethod.BCJ_X86_FILTER, new BCJDecoder(new X86Options()));
            put(SevenZMethod.BCJ_PPC_FILTER, new BCJDecoder(new PowerPCOptions()));
            put(SevenZMethod.BCJ_IA64_FILTER, new BCJDecoder(new IA64Options()));
            put(SevenZMethod.BCJ_ARM_FILTER, new BCJDecoder(new ARMOptions()));
            put(SevenZMethod.BCJ_ARM_THUMB_FILTER, new BCJDecoder(new ARMThumbOptions()));
            put(SevenZMethod.BCJ_SPARC_FILTER, new BCJDecoder(new SPARCOptions()));
            put(SevenZMethod.DELTA_FILTER, new DeltaDecoder());
        }
    };

    Coders() {
    }

    static CoderBase findByMethod(SevenZMethod method) {
        return CODER_MAP.get(method);
    }

    static InputStream addDecoder(String archiveName, InputStream is, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
        CoderBase cb = findByMethod(SevenZMethod.byId(coder.decompressionMethodId));
        if (cb != null) {
            return cb.decode(archiveName, is, uncompressedLength, coder, password, maxMemoryLimitInKb);
        }
        throw new IOException("Unsupported compression method " + Arrays.toString(coder.decompressionMethodId) + " used in " + archiveName);
    }

    static OutputStream addEncoder(OutputStream out, SevenZMethod method, Object options) throws IOException {
        CoderBase cb = findByMethod(method);
        if (cb != null) {
            return cb.encode(out, options);
        }
        throw new IOException("Unsupported compression method " + method);
    }

    static class CopyDecoder extends CoderBase {
        CopyDecoder() {
            super(new Class[0]);
        }

        /* access modifiers changed from: package-private */
        public InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
            return in;
        }

        /* access modifiers changed from: package-private */
        public OutputStream encode(OutputStream out, Object options) {
            return out;
        }
    }

    static class BCJDecoder extends CoderBase {
        private final FilterOptions opts;

        BCJDecoder(FilterOptions opts2) {
            super(new Class[0]);
            this.opts = opts2;
        }

        /* access modifiers changed from: package-private */
        public InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
            try {
                return this.opts.getInputStream(in);
            } catch (AssertionError e) {
                throw new IOException("BCJ filter used in " + archiveName + " needs XZ for Java > 1.4 - see https://commons.apache.org/proper/commons-compress/limitations.html#7Z", e);
            }
        }

        /* access modifiers changed from: package-private */
        public OutputStream encode(OutputStream out, Object options) {
            return new FlushShieldFilterOutputStream(this.opts.getOutputStream(new FinishableWrapperOutputStream(out)));
        }
    }

    static class DeflateDecoder extends CoderBase {
        private static final byte[] ONE_ZERO_BYTE = new byte[1];

        DeflateDecoder() {
            super(Number.class);
        }

        /* access modifiers changed from: package-private */
        public InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
            Inflater inflater = new Inflater(true);
            return new DeflateDecoderInputStream(new InflaterInputStream(new SequenceInputStream(in, new ByteArrayInputStream(ONE_ZERO_BYTE)), inflater), inflater);
        }

        /* access modifiers changed from: package-private */
        public OutputStream encode(OutputStream out, Object options) {
            Deflater deflater = new Deflater(numberOptionOrDefault(options, 9), true);
            return new DeflateDecoderOutputStream(new DeflaterOutputStream(out, deflater), deflater);
        }

        static class DeflateDecoderInputStream extends InputStream {
            Inflater inflater;
            InflaterInputStream inflaterInputStream;

            public DeflateDecoderInputStream(InflaterInputStream inflaterInputStream2, Inflater inflater2) {
                this.inflaterInputStream = inflaterInputStream2;
                this.inflater = inflater2;
            }

            public int read() throws IOException {
                return this.inflaterInputStream.read();
            }

            public int read(byte[] b, int off, int len) throws IOException {
                return this.inflaterInputStream.read(b, off, len);
            }

            public int read(byte[] b) throws IOException {
                return this.inflaterInputStream.read(b);
            }

            public void close() throws IOException {
                try {
                    this.inflaterInputStream.close();
                } finally {
                    this.inflater.end();
                }
            }
        }

        static class DeflateDecoderOutputStream extends OutputStream {
            Deflater deflater;
            DeflaterOutputStream deflaterOutputStream;

            public DeflateDecoderOutputStream(DeflaterOutputStream deflaterOutputStream2, Deflater deflater2) {
                this.deflaterOutputStream = deflaterOutputStream2;
                this.deflater = deflater2;
            }

            public void write(int b) throws IOException {
                this.deflaterOutputStream.write(b);
            }

            public void write(byte[] b) throws IOException {
                this.deflaterOutputStream.write(b);
            }

            public void write(byte[] b, int off, int len) throws IOException {
                this.deflaterOutputStream.write(b, off, len);
            }

            public void close() throws IOException {
                try {
                    this.deflaterOutputStream.close();
                } finally {
                    this.deflater.end();
                }
            }
        }
    }

    static class Deflate64Decoder extends CoderBase {
        Deflate64Decoder() {
            super(Number.class);
        }

        /* access modifiers changed from: package-private */
        public InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
            return new Deflate64CompressorInputStream(in);
        }
    }

    static class BZIP2Decoder extends CoderBase {
        BZIP2Decoder() {
            super(Number.class);
        }

        /* access modifiers changed from: package-private */
        public InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
            return new BZip2CompressorInputStream(in);
        }

        /* access modifiers changed from: package-private */
        public OutputStream encode(OutputStream out, Object options) throws IOException {
            return new BZip2CompressorOutputStream(out, numberOptionOrDefault(options, 9));
        }
    }
}

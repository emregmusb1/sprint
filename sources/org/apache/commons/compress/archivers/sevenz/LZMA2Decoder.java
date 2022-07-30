package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.MemoryLimitException;
import org.tukaani.xz.FinishableWrapperOutputStream;
import org.tukaani.xz.LZMA2InputStream;
import org.tukaani.xz.LZMA2Options;

class LZMA2Decoder extends CoderBase {
    LZMA2Decoder() {
        super(LZMA2Options.class, Number.class);
    }

    /* access modifiers changed from: package-private */
    public InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
        try {
            int dictionarySize = getDictionarySize(coder);
            int memoryUsageInKb = LZMA2InputStream.getMemoryUsage(dictionarySize);
            if (memoryUsageInKb <= maxMemoryLimitInKb) {
                return new LZMA2InputStream(in, dictionarySize);
            }
            throw new MemoryLimitException((long) memoryUsageInKb, maxMemoryLimitInKb);
        } catch (IllegalArgumentException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    /* access modifiers changed from: package-private */
    public OutputStream encode(OutputStream out, Object opts) throws IOException {
        return getOptions(opts).getOutputStream(new FinishableWrapperOutputStream(out));
    }

    /* access modifiers changed from: package-private */
    public byte[] getOptionsAsProperties(Object opts) {
        int dictSize = getDictSize(opts);
        int lead = Integer.numberOfLeadingZeros(dictSize);
        return new byte[]{(byte) (((19 - lead) * 2) + ((dictSize >>> (30 - lead)) - 2))};
    }

    /* access modifiers changed from: package-private */
    public Object getOptionsFromCoder(Coder coder, InputStream in) throws IOException {
        return Integer.valueOf(getDictionarySize(coder));
    }

    private int getDictSize(Object opts) {
        if (opts instanceof LZMA2Options) {
            return ((LZMA2Options) opts).getDictSize();
        }
        return numberOptionOrDefault(opts);
    }

    private int getDictionarySize(Coder coder) throws IOException {
        if (coder.properties == null) {
            throw new IOException("Missing LZMA2 properties");
        } else if (coder.properties.length >= 1) {
            int dictionarySizeBits = coder.properties[0] & 255;
            if ((dictionarySizeBits & -64) != 0) {
                throw new IOException("Unsupported LZMA2 property bits");
            } else if (dictionarySizeBits > 40) {
                throw new IOException("Dictionary larger than 4GiB maximum size");
            } else if (dictionarySizeBits == 40) {
                return -1;
            } else {
                return ((dictionarySizeBits & 1) | 2) << ((dictionarySizeBits / 2) + 11);
            }
        } else {
            throw new IOException("LZMA2 properties too short");
        }
    }

    private LZMA2Options getOptions(Object opts) throws IOException {
        if (opts instanceof LZMA2Options) {
            return (LZMA2Options) opts;
        }
        LZMA2Options options = new LZMA2Options();
        options.setDictSize(numberOptionOrDefault(opts));
        return options;
    }

    private int numberOptionOrDefault(Object opts) {
        return numberOptionOrDefault(opts, 8388608);
    }
}

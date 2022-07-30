package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.tukaani.xz.DeltaOptions;
import org.tukaani.xz.FinishableWrapperOutputStream;
import org.tukaani.xz.UnsupportedOptionsException;

class DeltaDecoder extends CoderBase {
    DeltaDecoder() {
        super(Number.class);
    }

    /* access modifiers changed from: package-private */
    public InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
        return new DeltaOptions(getOptionsFromCoder(coder)).getInputStream(in);
    }

    /* access modifiers changed from: package-private */
    public OutputStream encode(OutputStream out, Object options) throws IOException {
        try {
            return new DeltaOptions(numberOptionOrDefault(options, 1)).getOutputStream(new FinishableWrapperOutputStream(out));
        } catch (UnsupportedOptionsException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    /* access modifiers changed from: package-private */
    public byte[] getOptionsAsProperties(Object options) {
        return new byte[]{(byte) (numberOptionOrDefault(options, 1) - 1)};
    }

    /* access modifiers changed from: package-private */
    public Object getOptionsFromCoder(Coder coder, InputStream in) {
        return Integer.valueOf(getOptionsFromCoder(coder));
    }

    private int getOptionsFromCoder(Coder coder) {
        if (coder.properties == null || coder.properties.length == 0) {
            return 1;
        }
        return (coder.properties[0] & 255) + 1;
    }
}

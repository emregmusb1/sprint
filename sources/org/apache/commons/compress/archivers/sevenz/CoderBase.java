package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class CoderBase {
    private static final byte[] NONE = new byte[0];
    private final Class<?>[] acceptableOptions;

    /* access modifiers changed from: package-private */
    public abstract InputStream decode(String str, InputStream inputStream, long j, Coder coder, byte[] bArr, int i) throws IOException;

    protected CoderBase(Class<?>... acceptableOptions2) {
        this.acceptableOptions = acceptableOptions2;
    }

    /* access modifiers changed from: package-private */
    public boolean canAcceptOptions(Object opts) {
        for (Class<?> c : this.acceptableOptions) {
            if (c.isInstance(opts)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public byte[] getOptionsAsProperties(Object options) throws IOException {
        return NONE;
    }

    /* access modifiers changed from: package-private */
    public Object getOptionsFromCoder(Coder coder, InputStream in) throws IOException {
        return null;
    }

    /* access modifiers changed from: package-private */
    public OutputStream encode(OutputStream out, Object options) throws IOException {
        throw new UnsupportedOperationException("Method doesn't support writing");
    }

    protected static int numberOptionOrDefault(Object options, int defaultValue) {
        return options instanceof Number ? ((Number) options).intValue() : defaultValue;
    }
}

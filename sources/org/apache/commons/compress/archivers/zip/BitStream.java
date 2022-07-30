package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import org.apache.commons.compress.utils.BitInputStream;

class BitStream extends BitInputStream {
    BitStream(InputStream in) {
        super(in, ByteOrder.LITTLE_ENDIAN);
    }

    /* access modifiers changed from: package-private */
    public int nextBit() throws IOException {
        return (int) readBits(1);
    }

    /* access modifiers changed from: package-private */
    public long nextBits(int n) throws IOException {
        if (n >= 0 && n <= 8) {
            return readBits(n);
        }
        throw new IOException("Trying to read " + n + " bits, at most 8 are allowed");
    }

    /* access modifiers changed from: package-private */
    public int nextByte() throws IOException {
        return (int) readBits(8);
    }
}

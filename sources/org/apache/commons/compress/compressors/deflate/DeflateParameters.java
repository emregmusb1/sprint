package org.apache.commons.compress.compressors.deflate;

public class DeflateParameters {
    private int compressionLevel = -1;
    private boolean zlibHeader = true;

    public boolean withZlibHeader() {
        return this.zlibHeader;
    }

    public void setWithZlibHeader(boolean zlibHeader2) {
        this.zlibHeader = zlibHeader2;
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public void setCompressionLevel(int compressionLevel2) {
        if (compressionLevel2 < -1 || compressionLevel2 > 9) {
            throw new IllegalArgumentException("Invalid Deflate compression level: " + compressionLevel2);
        }
        this.compressionLevel = compressionLevel2;
    }
}

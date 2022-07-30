package org.apache.commons.compress.archivers.zip;

import java.util.Arrays;

public final class UnparseableExtraFieldData implements ZipExtraField {
    private static final ZipShort HEADER_ID = new ZipShort(44225);
    private byte[] centralDirectoryData;
    private byte[] localFileData;

    public ZipShort getHeaderId() {
        return HEADER_ID;
    }

    public ZipShort getLocalFileDataLength() {
        byte[] bArr = this.localFileData;
        return new ZipShort(bArr == null ? 0 : bArr.length);
    }

    public ZipShort getCentralDirectoryLength() {
        byte[] bArr = this.centralDirectoryData;
        return bArr == null ? getLocalFileDataLength() : new ZipShort(bArr.length);
    }

    public byte[] getLocalFileDataData() {
        return ZipUtil.copy(this.localFileData);
    }

    public byte[] getCentralDirectoryData() {
        byte[] bArr = this.centralDirectoryData;
        return bArr == null ? getLocalFileDataData() : ZipUtil.copy(bArr);
    }

    public void parseFromLocalFileData(byte[] buffer, int offset, int length) {
        this.localFileData = Arrays.copyOfRange(buffer, offset, offset + length);
    }

    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) {
        this.centralDirectoryData = Arrays.copyOfRange(buffer, offset, offset + length);
        if (this.localFileData == null) {
            parseFromLocalFileData(buffer, offset, length);
        }
    }
}

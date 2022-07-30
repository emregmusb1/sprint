package org.apache.commons.compress.archivers.zip;

import java.util.Arrays;

public class UnrecognizedExtraField implements ZipExtraField {
    private byte[] centralData;
    private ZipShort headerId;
    private byte[] localData;

    public void setHeaderId(ZipShort headerId2) {
        this.headerId = headerId2;
    }

    public ZipShort getHeaderId() {
        return this.headerId;
    }

    public void setLocalFileDataData(byte[] data) {
        this.localData = ZipUtil.copy(data);
    }

    public ZipShort getLocalFileDataLength() {
        byte[] bArr = this.localData;
        return new ZipShort(bArr != null ? bArr.length : 0);
    }

    public byte[] getLocalFileDataData() {
        return ZipUtil.copy(this.localData);
    }

    public void setCentralDirectoryData(byte[] data) {
        this.centralData = ZipUtil.copy(data);
    }

    public ZipShort getCentralDirectoryLength() {
        byte[] bArr = this.centralData;
        if (bArr != null) {
            return new ZipShort(bArr.length);
        }
        return getLocalFileDataLength();
    }

    public byte[] getCentralDirectoryData() {
        byte[] bArr = this.centralData;
        if (bArr != null) {
            return ZipUtil.copy(bArr);
        }
        return getLocalFileDataData();
    }

    public void parseFromLocalFileData(byte[] data, int offset, int length) {
        setLocalFileDataData(Arrays.copyOfRange(data, offset, offset + length));
    }

    public void parseFromCentralDirectoryData(byte[] data, int offset, int length) {
        byte[] tmp = Arrays.copyOfRange(data, offset, offset + length);
        setCentralDirectoryData(tmp);
        if (this.localData == null) {
            setLocalFileDataData(tmp);
        }
    }
}

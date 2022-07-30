package org.apache.commons.compress.archivers.zip;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;
import java.util.zip.ZipException;
import org.apache.commons.compress.utils.CharsetNames;

public abstract class AbstractUnicodeExtraField implements ZipExtraField {
    private byte[] data;
    private long nameCRC32;
    private byte[] unicodeName;

    protected AbstractUnicodeExtraField() {
    }

    protected AbstractUnicodeExtraField(String text, byte[] bytes, int off, int len) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes, off, len);
        this.nameCRC32 = crc32.getValue();
        try {
            this.unicodeName = text.getBytes(CharsetNames.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("FATAL: UTF-8 encoding not supported.", e);
        }
    }

    protected AbstractUnicodeExtraField(String text, byte[] bytes) {
        this(text, bytes, 0, bytes.length);
    }

    private void assembleData() {
        byte[] bArr = this.unicodeName;
        if (bArr != null) {
            this.data = new byte[(bArr.length + 5)];
            this.data[0] = 1;
            System.arraycopy(ZipLong.getBytes(this.nameCRC32), 0, this.data, 1, 4);
            byte[] bArr2 = this.unicodeName;
            System.arraycopy(bArr2, 0, this.data, 5, bArr2.length);
        }
    }

    public long getNameCRC32() {
        return this.nameCRC32;
    }

    public void setNameCRC32(long nameCRC322) {
        this.nameCRC32 = nameCRC322;
        this.data = null;
    }

    public byte[] getUnicodeName() {
        byte[] bArr = this.unicodeName;
        if (bArr == null) {
            return null;
        }
        byte[] b = new byte[bArr.length];
        System.arraycopy(bArr, 0, b, 0, b.length);
        return b;
    }

    public void setUnicodeName(byte[] unicodeName2) {
        if (unicodeName2 != null) {
            this.unicodeName = new byte[unicodeName2.length];
            System.arraycopy(unicodeName2, 0, this.unicodeName, 0, unicodeName2.length);
        } else {
            this.unicodeName = null;
        }
        this.data = null;
    }

    public byte[] getCentralDirectoryData() {
        if (this.data == null) {
            assembleData();
        }
        byte[] bArr = this.data;
        if (bArr == null) {
            return null;
        }
        byte[] b = new byte[bArr.length];
        System.arraycopy(bArr, 0, b, 0, b.length);
        return b;
    }

    public ZipShort getCentralDirectoryLength() {
        if (this.data == null) {
            assembleData();
        }
        byte[] bArr = this.data;
        return new ZipShort(bArr != null ? bArr.length : 0);
    }

    public byte[] getLocalFileDataData() {
        return getCentralDirectoryData();
    }

    public ZipShort getLocalFileDataLength() {
        return getCentralDirectoryLength();
    }

    public void parseFromLocalFileData(byte[] buffer, int offset, int length) throws ZipException {
        if (length >= 5) {
            byte version = buffer[offset];
            if (version == 1) {
                this.nameCRC32 = ZipLong.getValue(buffer, offset + 1);
                this.unicodeName = new byte[(length - 5)];
                System.arraycopy(buffer, offset + 5, this.unicodeName, 0, length - 5);
                this.data = null;
                return;
            }
            throw new ZipException("Unsupported version [" + version + "] for UniCode path extra data.");
        }
        throw new ZipException("UniCode path extra data must have at least 5 bytes.");
    }

    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
        parseFromLocalFileData(buffer, offset, length);
    }
}

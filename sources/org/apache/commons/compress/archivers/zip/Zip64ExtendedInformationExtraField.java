package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;

public class Zip64ExtendedInformationExtraField implements ZipExtraField {
    private static final byte[] EMPTY = new byte[0];
    static final ZipShort HEADER_ID = new ZipShort(1);
    private static final String LFH_MUST_HAVE_BOTH_SIZES_MSG = "Zip64 extended information must contain both size values in the local file header.";
    private ZipEightByteInteger compressedSize;
    private ZipLong diskStart;
    private byte[] rawCentralDirectoryData;
    private ZipEightByteInteger relativeHeaderOffset;
    private ZipEightByteInteger size;

    public Zip64ExtendedInformationExtraField() {
    }

    public Zip64ExtendedInformationExtraField(ZipEightByteInteger size2, ZipEightByteInteger compressedSize2) {
        this(size2, compressedSize2, (ZipEightByteInteger) null, (ZipLong) null);
    }

    public Zip64ExtendedInformationExtraField(ZipEightByteInteger size2, ZipEightByteInteger compressedSize2, ZipEightByteInteger relativeHeaderOffset2, ZipLong diskStart2) {
        this.size = size2;
        this.compressedSize = compressedSize2;
        this.relativeHeaderOffset = relativeHeaderOffset2;
        this.diskStart = diskStart2;
    }

    public ZipShort getHeaderId() {
        return HEADER_ID;
    }

    public ZipShort getLocalFileDataLength() {
        return new ZipShort(this.size != null ? 16 : 0);
    }

    public ZipShort getCentralDirectoryLength() {
        int i = 8;
        int i2 = 0;
        int i3 = (this.size != null ? 8 : 0) + (this.compressedSize != null ? 8 : 0);
        if (this.relativeHeaderOffset == null) {
            i = 0;
        }
        int i4 = i3 + i;
        if (this.diskStart != null) {
            i2 = 4;
        }
        return new ZipShort(i4 + i2);
    }

    public byte[] getLocalFileDataData() {
        if (this.size == null && this.compressedSize == null) {
            return EMPTY;
        }
        if (this.size == null || this.compressedSize == null) {
            throw new IllegalArgumentException(LFH_MUST_HAVE_BOTH_SIZES_MSG);
        }
        byte[] data = new byte[16];
        addSizes(data);
        return data;
    }

    public byte[] getCentralDirectoryData() {
        byte[] data = new byte[getCentralDirectoryLength().getValue()];
        int off = addSizes(data);
        ZipEightByteInteger zipEightByteInteger = this.relativeHeaderOffset;
        if (zipEightByteInteger != null) {
            System.arraycopy(zipEightByteInteger.getBytes(), 0, data, off, 8);
            off += 8;
        }
        ZipLong zipLong = this.diskStart;
        if (zipLong != null) {
            System.arraycopy(zipLong.getBytes(), 0, data, off, 4);
            int off2 = off + 4;
        }
        return data;
    }

    public void parseFromLocalFileData(byte[] buffer, int offset, int length) throws ZipException {
        if (length != 0) {
            if (length >= 16) {
                this.size = new ZipEightByteInteger(buffer, offset);
                int offset2 = offset + 8;
                this.compressedSize = new ZipEightByteInteger(buffer, offset2);
                int offset3 = offset2 + 8;
                int remaining = length - 16;
                if (remaining >= 8) {
                    this.relativeHeaderOffset = new ZipEightByteInteger(buffer, offset3);
                    offset3 += 8;
                    remaining -= 8;
                }
                if (remaining >= 4) {
                    this.diskStart = new ZipLong(buffer, offset3);
                    int offset4 = offset3 + 4;
                    int remaining2 = remaining - 4;
                    return;
                }
                return;
            }
            throw new ZipException(LFH_MUST_HAVE_BOTH_SIZES_MSG);
        }
    }

    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
        this.rawCentralDirectoryData = new byte[length];
        System.arraycopy(buffer, offset, this.rawCentralDirectoryData, 0, length);
        if (length >= 28) {
            parseFromLocalFileData(buffer, offset, length);
        } else if (length == 24) {
            this.size = new ZipEightByteInteger(buffer, offset);
            int offset2 = offset + 8;
            this.compressedSize = new ZipEightByteInteger(buffer, offset2);
            this.relativeHeaderOffset = new ZipEightByteInteger(buffer, offset2 + 8);
        } else if (length % 8 == 4) {
            this.diskStart = new ZipLong(buffer, (offset + length) - 4);
        }
    }

    public void reparseCentralDirectoryData(boolean hasUncompressedSize, boolean hasCompressedSize, boolean hasRelativeHeaderOffset, boolean hasDiskStart) throws ZipException {
        if (this.rawCentralDirectoryData != null) {
            int i = 0;
            int i2 = 8;
            int i3 = (hasUncompressedSize ? 8 : 0) + (hasCompressedSize ? 8 : 0);
            if (!hasRelativeHeaderOffset) {
                i2 = 0;
            }
            int i4 = i3 + i2;
            if (hasDiskStart) {
                i = 4;
            }
            int expectedLength = i4 + i;
            byte[] bArr = this.rawCentralDirectoryData;
            if (bArr.length >= expectedLength) {
                int offset = 0;
                if (hasUncompressedSize) {
                    this.size = new ZipEightByteInteger(bArr, 0);
                    offset = 0 + 8;
                }
                if (hasCompressedSize) {
                    this.compressedSize = new ZipEightByteInteger(this.rawCentralDirectoryData, offset);
                    offset += 8;
                }
                if (hasRelativeHeaderOffset) {
                    this.relativeHeaderOffset = new ZipEightByteInteger(this.rawCentralDirectoryData, offset);
                    offset += 8;
                }
                if (hasDiskStart) {
                    this.diskStart = new ZipLong(this.rawCentralDirectoryData, offset);
                    return;
                }
                return;
            }
            throw new ZipException("Central directory zip64 extended information extra field's length doesn't match central directory data.  Expected length " + expectedLength + " but is " + this.rawCentralDirectoryData.length);
        }
    }

    public ZipEightByteInteger getSize() {
        return this.size;
    }

    public void setSize(ZipEightByteInteger size2) {
        this.size = size2;
    }

    public ZipEightByteInteger getCompressedSize() {
        return this.compressedSize;
    }

    public void setCompressedSize(ZipEightByteInteger compressedSize2) {
        this.compressedSize = compressedSize2;
    }

    public ZipEightByteInteger getRelativeHeaderOffset() {
        return this.relativeHeaderOffset;
    }

    public void setRelativeHeaderOffset(ZipEightByteInteger rho) {
        this.relativeHeaderOffset = rho;
    }

    public ZipLong getDiskStartNumber() {
        return this.diskStart;
    }

    public void setDiskStartNumber(ZipLong ds) {
        this.diskStart = ds;
    }

    private int addSizes(byte[] data) {
        int off = 0;
        ZipEightByteInteger zipEightByteInteger = this.size;
        if (zipEightByteInteger != null) {
            System.arraycopy(zipEightByteInteger.getBytes(), 0, data, 0, 8);
            off = 0 + 8;
        }
        ZipEightByteInteger zipEightByteInteger2 = this.compressedSize;
        if (zipEightByteInteger2 == null) {
            return off;
        }
        System.arraycopy(zipEightByteInteger2.getBytes(), 0, data, off, 8);
        return off + 8;
    }
}

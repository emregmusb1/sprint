package org.apache.commons.compress.archivers.zip;

import java.util.Date;
import java.util.zip.ZipException;

public class X000A_NTFS implements ZipExtraField {
    private static final long EPOCH_OFFSET = -116444736000000000L;
    private static final ZipShort HEADER_ID = new ZipShort(10);
    private static final ZipShort TIME_ATTR_SIZE = new ZipShort(24);
    private static final ZipShort TIME_ATTR_TAG = new ZipShort(1);
    private ZipEightByteInteger accessTime = ZipEightByteInteger.ZERO;
    private ZipEightByteInteger createTime = ZipEightByteInteger.ZERO;
    private ZipEightByteInteger modifyTime = ZipEightByteInteger.ZERO;

    public ZipShort getHeaderId() {
        return HEADER_ID;
    }

    public ZipShort getLocalFileDataLength() {
        return new ZipShort(32);
    }

    public ZipShort getCentralDirectoryLength() {
        return getLocalFileDataLength();
    }

    public byte[] getLocalFileDataData() {
        byte[] data = new byte[getLocalFileDataLength().getValue()];
        System.arraycopy(TIME_ATTR_TAG.getBytes(), 0, data, 4, 2);
        int pos = 4 + 2;
        System.arraycopy(TIME_ATTR_SIZE.getBytes(), 0, data, pos, 2);
        int pos2 = pos + 2;
        System.arraycopy(this.modifyTime.getBytes(), 0, data, pos2, 8);
        int pos3 = pos2 + 8;
        System.arraycopy(this.accessTime.getBytes(), 0, data, pos3, 8);
        System.arraycopy(this.createTime.getBytes(), 0, data, pos3 + 8, 8);
        return data;
    }

    public byte[] getCentralDirectoryData() {
        return getLocalFileDataData();
    }

    public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
        int len = offset + length;
        int offset2 = offset + 4;
        while (offset2 + 4 <= len) {
            ZipShort tag = new ZipShort(data, offset2);
            int offset3 = offset2 + 2;
            if (tag.equals(TIME_ATTR_TAG)) {
                readTimeAttr(data, offset3, len - offset3);
                return;
            }
            offset2 = offset3 + new ZipShort(data, offset3).getValue() + 2;
        }
    }

    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
        reset();
        parseFromLocalFileData(buffer, offset, length);
    }

    public ZipEightByteInteger getModifyTime() {
        return this.modifyTime;
    }

    public ZipEightByteInteger getAccessTime() {
        return this.accessTime;
    }

    public ZipEightByteInteger getCreateTime() {
        return this.createTime;
    }

    public Date getModifyJavaTime() {
        return zipToDate(this.modifyTime);
    }

    public Date getAccessJavaTime() {
        return zipToDate(this.accessTime);
    }

    public Date getCreateJavaTime() {
        return zipToDate(this.createTime);
    }

    public void setModifyTime(ZipEightByteInteger t) {
        this.modifyTime = t == null ? ZipEightByteInteger.ZERO : t;
    }

    public void setAccessTime(ZipEightByteInteger t) {
        this.accessTime = t == null ? ZipEightByteInteger.ZERO : t;
    }

    public void setCreateTime(ZipEightByteInteger t) {
        this.createTime = t == null ? ZipEightByteInteger.ZERO : t;
    }

    public void setModifyJavaTime(Date d) {
        setModifyTime(dateToZip(d));
    }

    public void setAccessJavaTime(Date d) {
        setAccessTime(dateToZip(d));
    }

    public void setCreateJavaTime(Date d) {
        setCreateTime(dateToZip(d));
    }

    public String toString() {
        return "0x000A Zip Extra Field:" + " Modify:[" + getModifyJavaTime() + "] " + " Access:[" + getAccessJavaTime() + "] " + " Create:[" + getCreateJavaTime() + "] ";
    }

    public boolean equals(Object o) {
        if (!(o instanceof X000A_NTFS)) {
            return false;
        }
        X000A_NTFS xf = (X000A_NTFS) o;
        ZipEightByteInteger zipEightByteInteger = this.modifyTime;
        ZipEightByteInteger zipEightByteInteger2 = xf.modifyTime;
        if (zipEightByteInteger != zipEightByteInteger2 && (zipEightByteInteger == null || !zipEightByteInteger.equals(zipEightByteInteger2))) {
            return false;
        }
        ZipEightByteInteger zipEightByteInteger3 = this.accessTime;
        ZipEightByteInteger zipEightByteInteger4 = xf.accessTime;
        if (zipEightByteInteger3 != zipEightByteInteger4 && (zipEightByteInteger3 == null || !zipEightByteInteger3.equals(zipEightByteInteger4))) {
            return false;
        }
        ZipEightByteInteger zipEightByteInteger5 = this.createTime;
        ZipEightByteInteger zipEightByteInteger6 = xf.createTime;
        if (zipEightByteInteger5 == zipEightByteInteger6 || (zipEightByteInteger5 != null && zipEightByteInteger5.equals(zipEightByteInteger6))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hc = -123;
        ZipEightByteInteger zipEightByteInteger = this.modifyTime;
        if (zipEightByteInteger != null) {
            hc = -123 ^ zipEightByteInteger.hashCode();
        }
        ZipEightByteInteger zipEightByteInteger2 = this.accessTime;
        if (zipEightByteInteger2 != null) {
            hc ^= Integer.rotateLeft(zipEightByteInteger2.hashCode(), 11);
        }
        ZipEightByteInteger zipEightByteInteger3 = this.createTime;
        if (zipEightByteInteger3 != null) {
            return hc ^ Integer.rotateLeft(zipEightByteInteger3.hashCode(), 22);
        }
        return hc;
    }

    private void reset() {
        this.modifyTime = ZipEightByteInteger.ZERO;
        this.accessTime = ZipEightByteInteger.ZERO;
        this.createTime = ZipEightByteInteger.ZERO;
    }

    private void readTimeAttr(byte[] data, int offset, int length) {
        if (length >= 26) {
            if (TIME_ATTR_SIZE.equals(new ZipShort(data, offset))) {
                int offset2 = offset + 2;
                this.modifyTime = new ZipEightByteInteger(data, offset2);
                int offset3 = offset2 + 8;
                this.accessTime = new ZipEightByteInteger(data, offset3);
                this.createTime = new ZipEightByteInteger(data, offset3 + 8);
            }
        }
    }

    private static ZipEightByteInteger dateToZip(Date d) {
        if (d == null) {
            return null;
        }
        return new ZipEightByteInteger((d.getTime() * 10000) - EPOCH_OFFSET);
    }

    private static Date zipToDate(ZipEightByteInteger z) {
        if (z == null || ZipEightByteInteger.ZERO.equals(z)) {
            return null;
        }
        return new Date((z.getLongValue() + EPOCH_OFFSET) / 10000);
    }
}

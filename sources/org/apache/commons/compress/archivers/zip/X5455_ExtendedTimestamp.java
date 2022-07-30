package org.apache.commons.compress.archivers.zip;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipException;

public class X5455_ExtendedTimestamp implements ZipExtraField, Cloneable, Serializable {
    public static final byte ACCESS_TIME_BIT = 2;
    public static final byte CREATE_TIME_BIT = 4;
    private static final ZipShort HEADER_ID = new ZipShort(21589);
    public static final byte MODIFY_TIME_BIT = 1;
    private static final long serialVersionUID = 1;
    private ZipLong accessTime;
    private boolean bit0_modifyTimePresent;
    private boolean bit1_accessTimePresent;
    private boolean bit2_createTimePresent;
    private ZipLong createTime;
    private byte flags;
    private ZipLong modifyTime;

    public ZipShort getHeaderId() {
        return HEADER_ID;
    }

    public ZipShort getLocalFileDataLength() {
        int i = 4;
        int i2 = (this.bit0_modifyTimePresent ? 4 : 0) + 1 + ((!this.bit1_accessTimePresent || this.accessTime == null) ? 0 : 4);
        if (!this.bit2_createTimePresent || this.createTime == null) {
            i = 0;
        }
        return new ZipShort(i2 + i);
    }

    public ZipShort getCentralDirectoryLength() {
        return new ZipShort((this.bit0_modifyTimePresent ? 4 : 0) + 1);
    }

    public byte[] getLocalFileDataData() {
        ZipLong zipLong;
        ZipLong zipLong2;
        byte[] data = new byte[getLocalFileDataLength().getValue()];
        int pos = 0 + 1;
        data[0] = 0;
        if (this.bit0_modifyTimePresent) {
            data[0] = (byte) (data[0] | 1);
            System.arraycopy(this.modifyTime.getBytes(), 0, data, pos, 4);
            pos += 4;
        }
        if (this.bit1_accessTimePresent && (zipLong2 = this.accessTime) != null) {
            data[0] = (byte) (data[0] | 2);
            System.arraycopy(zipLong2.getBytes(), 0, data, pos, 4);
            pos += 4;
        }
        if (this.bit2_createTimePresent && (zipLong = this.createTime) != null) {
            data[0] = (byte) (data[0] | 4);
            System.arraycopy(zipLong.getBytes(), 0, data, pos, 4);
            int pos2 = pos + 4;
        }
        return data;
    }

    public byte[] getCentralDirectoryData() {
        return Arrays.copyOf(getLocalFileDataData(), getCentralDirectoryLength().getValue());
    }

    public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
        reset();
        if (length >= 1) {
            int len = offset + length;
            int offset2 = offset + 1;
            setFlags(data[offset]);
            if (this.bit0_modifyTimePresent && offset2 + 4 <= len) {
                this.modifyTime = new ZipLong(data, offset2);
                offset2 += 4;
            }
            if (this.bit1_accessTimePresent && offset2 + 4 <= len) {
                this.accessTime = new ZipLong(data, offset2);
                offset2 += 4;
            }
            if (this.bit2_createTimePresent && offset2 + 4 <= len) {
                this.createTime = new ZipLong(data, offset2);
                int offset3 = offset2 + 4;
                return;
            }
            return;
        }
        throw new ZipException("X5455_ExtendedTimestamp too short, only " + length + " bytes");
    }

    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
        reset();
        parseFromLocalFileData(buffer, offset, length);
    }

    private void reset() {
        setFlags((byte) 0);
        this.modifyTime = null;
        this.accessTime = null;
        this.createTime = null;
    }

    public void setFlags(byte flags2) {
        this.flags = flags2;
        boolean z = false;
        this.bit0_modifyTimePresent = (flags2 & 1) == 1;
        this.bit1_accessTimePresent = (flags2 & 2) == 2;
        if ((flags2 & 4) == 4) {
            z = true;
        }
        this.bit2_createTimePresent = z;
    }

    public byte getFlags() {
        return this.flags;
    }

    public boolean isBit0_modifyTimePresent() {
        return this.bit0_modifyTimePresent;
    }

    public boolean isBit1_accessTimePresent() {
        return this.bit1_accessTimePresent;
    }

    public boolean isBit2_createTimePresent() {
        return this.bit2_createTimePresent;
    }

    public ZipLong getModifyTime() {
        return this.modifyTime;
    }

    public ZipLong getAccessTime() {
        return this.accessTime;
    }

    public ZipLong getCreateTime() {
        return this.createTime;
    }

    public Date getModifyJavaTime() {
        return zipLongToDate(this.modifyTime);
    }

    public Date getAccessJavaTime() {
        return zipLongToDate(this.accessTime);
    }

    public Date getCreateJavaTime() {
        return zipLongToDate(this.createTime);
    }

    public void setModifyTime(ZipLong l) {
        this.bit0_modifyTimePresent = l != null;
        this.flags = (byte) (l != null ? 1 | this.flags : this.flags & -2);
        this.modifyTime = l;
    }

    public void setAccessTime(ZipLong l) {
        this.bit1_accessTimePresent = l != null;
        this.flags = (byte) (l != null ? this.flags | 2 : this.flags & -3);
        this.accessTime = l;
    }

    public void setCreateTime(ZipLong l) {
        this.bit2_createTimePresent = l != null;
        this.flags = (byte) (l != null ? this.flags | 4 : this.flags & -5);
        this.createTime = l;
    }

    public void setModifyJavaTime(Date d) {
        setModifyTime(dateToZipLong(d));
    }

    public void setAccessJavaTime(Date d) {
        setAccessTime(dateToZipLong(d));
    }

    public void setCreateJavaTime(Date d) {
        setCreateTime(dateToZipLong(d));
    }

    private static ZipLong dateToZipLong(Date d) {
        if (d == null) {
            return null;
        }
        return unixTimeToZipLong(d.getTime() / 1000);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("0x5455 Zip Extra Field: Flags=");
        buf.append(Integer.toBinaryString(ZipUtil.unsignedIntToSignedByte(this.flags)));
        buf.append(" ");
        if (this.bit0_modifyTimePresent && this.modifyTime != null) {
            Date m = getModifyJavaTime();
            buf.append(" Modify:[");
            buf.append(m);
            buf.append("] ");
        }
        if (this.bit1_accessTimePresent && this.accessTime != null) {
            Date a = getAccessJavaTime();
            buf.append(" Access:[");
            buf.append(a);
            buf.append("] ");
        }
        if (this.bit2_createTimePresent && this.createTime != null) {
            Date c = getCreateJavaTime();
            buf.append(" Create:[");
            buf.append(c);
            buf.append("] ");
        }
        return buf.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object o) {
        if (!(o instanceof X5455_ExtendedTimestamp)) {
            return false;
        }
        X5455_ExtendedTimestamp xf = (X5455_ExtendedTimestamp) o;
        if ((this.flags & 7) != (xf.flags & 7)) {
            return false;
        }
        ZipLong zipLong = this.modifyTime;
        ZipLong zipLong2 = xf.modifyTime;
        if (zipLong != zipLong2 && (zipLong == null || !zipLong.equals(zipLong2))) {
            return false;
        }
        ZipLong zipLong3 = this.accessTime;
        ZipLong zipLong4 = xf.accessTime;
        if (zipLong3 != zipLong4 && (zipLong3 == null || !zipLong3.equals(zipLong4))) {
            return false;
        }
        ZipLong zipLong5 = this.createTime;
        ZipLong zipLong6 = xf.createTime;
        if (zipLong5 == zipLong6 || (zipLong5 != null && zipLong5.equals(zipLong6))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hc = (this.flags & 7) * -123;
        ZipLong zipLong = this.modifyTime;
        if (zipLong != null) {
            hc ^= zipLong.hashCode();
        }
        ZipLong zipLong2 = this.accessTime;
        if (zipLong2 != null) {
            hc ^= Integer.rotateLeft(zipLong2.hashCode(), 11);
        }
        ZipLong zipLong3 = this.createTime;
        if (zipLong3 != null) {
            return hc ^ Integer.rotateLeft(zipLong3.hashCode(), 22);
        }
        return hc;
    }

    private static Date zipLongToDate(ZipLong unixTime) {
        if (unixTime != null) {
            return new Date(((long) unixTime.getIntValue()) * 1000);
        }
        return null;
    }

    private static ZipLong unixTimeToZipLong(long l) {
        if (l >= -2147483648L && l <= 2147483647L) {
            return new ZipLong(l);
        }
        throw new IllegalArgumentException("X5455 timestamps must fit in a signed 32 bit integer: " + l);
    }
}

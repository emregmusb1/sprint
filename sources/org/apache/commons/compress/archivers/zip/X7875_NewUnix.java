package org.apache.commons.compress.archivers.zip;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.zip.ZipException;

public class X7875_NewUnix implements ZipExtraField, Cloneable, Serializable {
    private static final ZipShort HEADER_ID = new ZipShort(30837);
    private static final BigInteger ONE_THOUSAND = BigInteger.valueOf(1000);
    private static final ZipShort ZERO = new ZipShort(0);
    private static final long serialVersionUID = 1;
    private BigInteger gid;
    private BigInteger uid;
    private int version = 1;

    public X7875_NewUnix() {
        reset();
    }

    public ZipShort getHeaderId() {
        return HEADER_ID;
    }

    public long getUID() {
        return ZipUtil.bigToLong(this.uid);
    }

    public long getGID() {
        return ZipUtil.bigToLong(this.gid);
    }

    public void setUID(long l) {
        this.uid = ZipUtil.longToBig(l);
    }

    public void setGID(long l) {
        this.gid = ZipUtil.longToBig(l);
    }

    public ZipShort getLocalFileDataLength() {
        byte[] b = trimLeadingZeroesForceMinLength(this.uid.toByteArray());
        int gidSize = 0;
        int uidSize = b == null ? 0 : b.length;
        byte[] b2 = trimLeadingZeroesForceMinLength(this.gid.toByteArray());
        if (b2 != null) {
            gidSize = b2.length;
        }
        return new ZipShort(uidSize + 3 + gidSize);
    }

    public ZipShort getCentralDirectoryLength() {
        return ZERO;
    }

    public byte[] getLocalFileDataData() {
        byte[] uidBytes = this.uid.toByteArray();
        byte[] gidBytes = this.gid.toByteArray();
        byte[] uidBytes2 = trimLeadingZeroesForceMinLength(uidBytes);
        int uidBytesLen = uidBytes2 != null ? uidBytes2.length : 0;
        byte[] gidBytes2 = trimLeadingZeroesForceMinLength(gidBytes);
        int gidBytesLen = gidBytes2 != null ? gidBytes2.length : 0;
        byte[] data = new byte[(uidBytesLen + 3 + gidBytesLen)];
        if (uidBytes2 != null) {
            ZipUtil.reverse(uidBytes2);
        }
        if (gidBytes2 != null) {
            ZipUtil.reverse(gidBytes2);
        }
        int pos = 0 + 1;
        data[0] = ZipUtil.unsignedIntToSignedByte(this.version);
        int pos2 = pos + 1;
        data[pos] = ZipUtil.unsignedIntToSignedByte(uidBytesLen);
        if (uidBytes2 != null) {
            System.arraycopy(uidBytes2, 0, data, pos2, uidBytesLen);
        }
        int pos3 = pos2 + uidBytesLen;
        int pos4 = pos3 + 1;
        data[pos3] = ZipUtil.unsignedIntToSignedByte(gidBytesLen);
        if (gidBytes2 != null) {
            System.arraycopy(gidBytes2, 0, data, pos4, gidBytesLen);
        }
        return data;
    }

    public byte[] getCentralDirectoryData() {
        return new byte[0];
    }

    public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
        reset();
        if (length >= 3) {
            int offset2 = offset + 1;
            this.version = ZipUtil.signedByteToUnsignedInt(data[offset]);
            int offset3 = offset2 + 1;
            int uidSize = ZipUtil.signedByteToUnsignedInt(data[offset2]);
            if (uidSize + 3 <= length) {
                byte[] uidBytes = Arrays.copyOfRange(data, offset3, offset3 + uidSize);
                int offset4 = offset3 + uidSize;
                this.uid = new BigInteger(1, ZipUtil.reverse(uidBytes));
                int offset5 = offset4 + 1;
                int gidSize = ZipUtil.signedByteToUnsignedInt(data[offset4]);
                if (uidSize + 3 + gidSize <= length) {
                    this.gid = new BigInteger(1, ZipUtil.reverse(Arrays.copyOfRange(data, offset5, offset5 + gidSize)));
                    return;
                }
                throw new ZipException("X7875_NewUnix invalid: gidSize " + gidSize + " doesn't fit into " + length + " bytes");
            }
            throw new ZipException("X7875_NewUnix invalid: uidSize " + uidSize + " doesn't fit into " + length + " bytes");
        }
        throw new ZipException("X7875_NewUnix length is too short, only " + length + " bytes");
    }

    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
    }

    private void reset() {
        BigInteger bigInteger = ONE_THOUSAND;
        this.uid = bigInteger;
        this.gid = bigInteger;
    }

    public String toString() {
        return "0x7875 Zip Extra Field: UID=" + this.uid + " GID=" + this.gid;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object o) {
        if (!(o instanceof X7875_NewUnix)) {
            return false;
        }
        X7875_NewUnix xf = (X7875_NewUnix) o;
        if (this.version != xf.version || !this.uid.equals(xf.uid) || !this.gid.equals(xf.gid)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((this.version * -1234567) ^ Integer.rotateLeft(this.uid.hashCode(), 16)) ^ this.gid.hashCode();
    }

    static byte[] trimLeadingZeroesForceMinLength(byte[] array) {
        if (array == null) {
            return array;
        }
        int pos = 0;
        int length = array.length;
        int i = 0;
        while (i < length && array[i] == 0) {
            pos++;
            i++;
        }
        byte[] trimmedArray = new byte[Math.max(1, array.length - pos)];
        int startPos = trimmedArray.length - (array.length - pos);
        System.arraycopy(array, pos, trimmedArray, startPos, trimmedArray.length - startPos);
        return trimmedArray;
    }
}

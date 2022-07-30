package org.apache.commons.compress.archivers.zip;

import java.util.zip.CRC32;
import java.util.zip.ZipException;

public class AsiExtraField implements ZipExtraField, UnixStat, Cloneable {
    private static final ZipShort HEADER_ID = new ZipShort(30062);
    private static final int WORD = 4;
    private CRC32 crc = new CRC32();
    private boolean dirFlag = false;
    private int gid = 0;
    private String link = "";
    private int mode = 0;
    private int uid = 0;

    public ZipShort getHeaderId() {
        return HEADER_ID;
    }

    public ZipShort getLocalFileDataLength() {
        return new ZipShort(getLinkedFile().getBytes().length + 14);
    }

    public ZipShort getCentralDirectoryLength() {
        return getLocalFileDataLength();
    }

    public byte[] getLocalFileDataData() {
        byte[] data = new byte[(getLocalFileDataLength().getValue() - 4)];
        System.arraycopy(ZipShort.getBytes(getMode()), 0, data, 0, 2);
        byte[] linkArray = getLinkedFile().getBytes();
        System.arraycopy(ZipLong.getBytes((long) linkArray.length), 0, data, 2, 4);
        System.arraycopy(ZipShort.getBytes(getUserId()), 0, data, 6, 2);
        System.arraycopy(ZipShort.getBytes(getGroupId()), 0, data, 8, 2);
        System.arraycopy(linkArray, 0, data, 10, linkArray.length);
        this.crc.reset();
        this.crc.update(data);
        long checksum = this.crc.getValue();
        byte[] result = new byte[(data.length + 4)];
        System.arraycopy(ZipLong.getBytes(checksum), 0, result, 0, 4);
        System.arraycopy(data, 0, result, 4, data.length);
        return result;
    }

    public byte[] getCentralDirectoryData() {
        return getLocalFileDataData();
    }

    public void setUserId(int uid2) {
        this.uid = uid2;
    }

    public int getUserId() {
        return this.uid;
    }

    public void setGroupId(int gid2) {
        this.gid = gid2;
    }

    public int getGroupId() {
        return this.gid;
    }

    public void setLinkedFile(String name) {
        this.link = name;
        this.mode = getMode(this.mode);
    }

    public String getLinkedFile() {
        return this.link;
    }

    public boolean isLink() {
        return getLinkedFile().length() != 0;
    }

    public void setMode(int mode2) {
        this.mode = getMode(mode2);
    }

    public int getMode() {
        return this.mode;
    }

    public void setDirectory(boolean dirFlag2) {
        this.dirFlag = dirFlag2;
        this.mode = getMode(this.mode);
    }

    public boolean isDirectory() {
        return this.dirFlag && !isLink();
    }

    public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
        long givenChecksum = ZipLong.getValue(data, offset);
        byte[] tmp = new byte[(length - 4)];
        boolean z = false;
        System.arraycopy(data, offset + 4, tmp, 0, length - 4);
        this.crc.reset();
        this.crc.update(tmp);
        long realChecksum = this.crc.getValue();
        if (givenChecksum == realChecksum) {
            int newMode = ZipShort.getValue(tmp, 0);
            byte[] linkArray = new byte[((int) ZipLong.getValue(tmp, 2))];
            this.uid = ZipShort.getValue(tmp, 6);
            this.gid = ZipShort.getValue(tmp, 8);
            if (linkArray.length == 0) {
                this.link = "";
            } else if (linkArray.length <= tmp.length - 10) {
                System.arraycopy(tmp, 10, linkArray, 0, linkArray.length);
                this.link = new String(linkArray);
            } else {
                throw new ZipException("Bad symbolic link name length " + linkArray.length + " in ASI extra field");
            }
            if ((newMode & 16384) != 0) {
                z = true;
            }
            setDirectory(z);
            setMode(newMode);
            return;
        }
        throw new ZipException("Bad CRC checksum, expected " + Long.toHexString(givenChecksum) + " instead of " + Long.toHexString(realChecksum));
    }

    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
        parseFromLocalFileData(buffer, offset, length);
    }

    /* access modifiers changed from: protected */
    public int getMode(int mode2) {
        int type = 32768;
        if (isLink()) {
            type = 40960;
        } else if (isDirectory()) {
            type = 16384;
        }
        return (mode2 & UnixStat.PERM_MASK) | type;
    }

    public Object clone() {
        try {
            AsiExtraField cloned = (AsiExtraField) super.clone();
            cloned.crc = new CRC32();
            return cloned;
        } catch (CloneNotSupportedException cnfe) {
            throw new RuntimeException(cnfe);
        }
    }
}

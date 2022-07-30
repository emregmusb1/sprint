package org.apache.commons.compress.archivers.cpio;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Date;
import org.apache.commons.compress.archivers.ArchiveEntry;

public class CpioArchiveEntry implements CpioConstants, ArchiveEntry {
    private final int alignmentBoundary;
    private long chksum;
    private final short fileFormat;
    private long filesize;
    private long gid;
    private final int headerSize;
    private long inode;
    private long maj;
    private long min;
    private long mode;
    private long mtime;
    private String name;
    private long nlink;
    private long rmaj;
    private long rmin;
    private long uid;

    public CpioArchiveEntry(short format) {
        this.chksum = 0;
        this.filesize = 0;
        this.gid = 0;
        this.inode = 0;
        this.maj = 0;
        this.min = 0;
        this.mode = 0;
        this.mtime = 0;
        this.nlink = 0;
        this.rmaj = 0;
        this.rmin = 0;
        this.uid = 0;
        if (format == 1) {
            this.headerSize = 110;
            this.alignmentBoundary = 4;
        } else if (format == 2) {
            this.headerSize = 110;
            this.alignmentBoundary = 4;
        } else if (format == 4) {
            this.headerSize = 76;
            this.alignmentBoundary = 0;
        } else if (format == 8) {
            this.headerSize = 26;
            this.alignmentBoundary = 2;
        } else {
            throw new IllegalArgumentException("Unknown header type");
        }
        this.fileFormat = format;
    }

    public CpioArchiveEntry(String name2) {
        this(1, name2);
    }

    public CpioArchiveEntry(short format, String name2) {
        this(format);
        this.name = name2;
    }

    public CpioArchiveEntry(String name2, long size) {
        this(name2);
        setSize(size);
    }

    public CpioArchiveEntry(short format, String name2, long size) {
        this(format, name2);
        setSize(size);
    }

    public CpioArchiveEntry(File inputFile, String entryName) {
        this(1, inputFile, entryName);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public CpioArchiveEntry(short format, File inputFile, String entryName) {
        this(format, entryName, inputFile.isFile() ? inputFile.length() : 0);
        if (inputFile.isDirectory()) {
            setMode(PlaybackStateCompat.ACTION_PREPARE);
        } else if (inputFile.isFile()) {
            setMode(PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID);
        } else {
            throw new IllegalArgumentException("Cannot determine type of file " + inputFile.getName());
        }
        setTime(inputFile.lastModified() / 1000);
    }

    private void checkNewFormat() {
        if ((this.fileFormat & 3) == 0) {
            throw new UnsupportedOperationException();
        }
    }

    private void checkOldFormat() {
        if ((this.fileFormat & 12) == 0) {
            throw new UnsupportedOperationException();
        }
    }

    public long getChksum() {
        checkNewFormat();
        return this.chksum & 4294967295L;
    }

    public long getDevice() {
        checkOldFormat();
        return this.min;
    }

    public long getDeviceMaj() {
        checkNewFormat();
        return this.maj;
    }

    public long getDeviceMin() {
        checkNewFormat();
        return this.min;
    }

    public long getSize() {
        return this.filesize;
    }

    public short getFormat() {
        return this.fileFormat;
    }

    public long getGID() {
        return this.gid;
    }

    public int getHeaderSize() {
        return this.headerSize;
    }

    public int getAlignmentBoundary() {
        return this.alignmentBoundary;
    }

    @Deprecated
    public int getHeaderPadCount() {
        return getHeaderPadCount((Charset) null);
    }

    public int getHeaderPadCount(Charset charset) {
        String str = this.name;
        if (str == null) {
            return 0;
        }
        if (charset == null) {
            return getHeaderPadCount((long) str.length());
        }
        return getHeaderPadCount((long) str.getBytes(charset).length);
    }

    public int getHeaderPadCount(long namesize) {
        if (this.alignmentBoundary == 0) {
            return 0;
        }
        int size = this.headerSize + 1;
        if (this.name != null) {
            size = (int) (((long) size) + namesize);
        }
        int i = this.alignmentBoundary;
        int remain = size % i;
        if (remain > 0) {
            return i - remain;
        }
        return 0;
    }

    public int getDataPadCount() {
        int remain;
        int i = this.alignmentBoundary;
        if (i != 0 && (remain = (int) (this.filesize % ((long) i))) > 0) {
            return i - remain;
        }
        return 0;
    }

    public long getInode() {
        return this.inode;
    }

    public long getMode() {
        return (this.mode != 0 || CpioConstants.CPIO_TRAILER.equals(this.name)) ? this.mode : PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID;
    }

    public String getName() {
        return this.name;
    }

    public long getNumberOfLinks() {
        long j = this.nlink;
        if (j == 0) {
            return isDirectory() ? 2 : 1;
        }
        return j;
    }

    public long getRemoteDevice() {
        checkOldFormat();
        return this.rmin;
    }

    public long getRemoteDeviceMaj() {
        checkNewFormat();
        return this.rmaj;
    }

    public long getRemoteDeviceMin() {
        checkNewFormat();
        return this.rmin;
    }

    public long getTime() {
        return this.mtime;
    }

    public Date getLastModifiedDate() {
        return new Date(getTime() * 1000);
    }

    public long getUID() {
        return this.uid;
    }

    public boolean isBlockDevice() {
        return CpioUtil.fileType(this.mode) == 24576;
    }

    public boolean isCharacterDevice() {
        return CpioUtil.fileType(this.mode) == PlaybackStateCompat.ACTION_PLAY_FROM_URI;
    }

    public boolean isDirectory() {
        return CpioUtil.fileType(this.mode) == PlaybackStateCompat.ACTION_PREPARE;
    }

    public boolean isNetwork() {
        return CpioUtil.fileType(this.mode) == 36864;
    }

    public boolean isPipe() {
        return CpioUtil.fileType(this.mode) == PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM;
    }

    public boolean isRegularFile() {
        return CpioUtil.fileType(this.mode) == PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID;
    }

    public boolean isSocket() {
        return CpioUtil.fileType(this.mode) == 49152;
    }

    public boolean isSymbolicLink() {
        return CpioUtil.fileType(this.mode) == 40960;
    }

    public void setChksum(long chksum2) {
        checkNewFormat();
        this.chksum = 4294967295L & chksum2;
    }

    public void setDevice(long device) {
        checkOldFormat();
        this.min = device;
    }

    public void setDeviceMaj(long maj2) {
        checkNewFormat();
        this.maj = maj2;
    }

    public void setDeviceMin(long min2) {
        checkNewFormat();
        this.min = min2;
    }

    public void setSize(long size) {
        if (size < 0 || size > 4294967295L) {
            throw new IllegalArgumentException("Invalid entry size <" + size + ">");
        }
        this.filesize = size;
    }

    public void setGID(long gid2) {
        this.gid = gid2;
    }

    public void setInode(long inode2) {
        this.inode = inode2;
    }

    public void setMode(long mode2) {
        long maskedMode = 61440 & mode2;
        switch ((int) maskedMode) {
            case 4096:
            case 8192:
            case 16384:
            case CpioConstants.C_ISBLK /*24576*/:
            case 32768:
            case CpioConstants.C_ISNWK /*36864*/:
            case 40960:
            case CpioConstants.C_ISSOCK /*49152*/:
                this.mode = mode2;
                return;
            default:
                throw new IllegalArgumentException("Unknown mode. Full: " + Long.toHexString(mode2) + " Masked: " + Long.toHexString(maskedMode));
        }
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public void setNumberOfLinks(long nlink2) {
        this.nlink = nlink2;
    }

    public void setRemoteDevice(long device) {
        checkOldFormat();
        this.rmin = device;
    }

    public void setRemoteDeviceMaj(long rmaj2) {
        checkNewFormat();
        this.rmaj = rmaj2;
    }

    public void setRemoteDeviceMin(long rmin2) {
        checkNewFormat();
        this.rmin = rmin2;
    }

    public void setTime(long time) {
        this.mtime = time;
    }

    public void setUID(long uid2) {
        this.uid = uid2;
    }

    public int hashCode() {
        int i = 1 * 31;
        String str = this.name;
        return i + (str == null ? 0 : str.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CpioArchiveEntry other = (CpioArchiveEntry) obj;
        String str = this.name;
        if (str != null) {
            return str.equals(other.name);
        }
        if (other.name == null) {
            return true;
        }
        return false;
    }
}

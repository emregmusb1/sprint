package org.apache.commons.compress.archivers.dump;

import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.dump.DumpArchiveConstants;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.compress.archivers.zip.UnixStat;

public class DumpArchiveEntry implements ArchiveEntry {
    private long atime;
    private long ctime;
    private int generation;
    private int gid;
    private final TapeSegmentHeader header = new TapeSegmentHeader();
    private int ino;
    private boolean isDeleted;
    private int mode;
    private long mtime;
    private String name;
    private int nlink;
    private long offset;
    private String originalName;
    private Set<PERMISSION> permissions = Collections.emptySet();
    private String simpleName;
    private long size;
    private final DumpArchiveSummary summary = null;
    private TYPE type = TYPE.UNKNOWN;
    private int uid;
    private int volume;

    public DumpArchiveEntry() {
    }

    public DumpArchiveEntry(String name2, String simpleName2) {
        setName(name2);
        this.simpleName = simpleName2;
    }

    protected DumpArchiveEntry(String name2, String simpleName2, int ino2, TYPE type2) {
        setType(type2);
        setName(name2);
        this.simpleName = simpleName2;
        this.ino = ino2;
        this.offset = 0;
    }

    public String getSimpleName() {
        return this.simpleName;
    }

    /* access modifiers changed from: protected */
    public void setSimpleName(String simpleName2) {
        this.simpleName = simpleName2;
    }

    public int getIno() {
        return this.header.getIno();
    }

    public int getNlink() {
        return this.nlink;
    }

    public void setNlink(int nlink2) {
        this.nlink = nlink2;
    }

    public Date getCreationTime() {
        return new Date(this.ctime);
    }

    public void setCreationTime(Date ctime2) {
        this.ctime = ctime2.getTime();
    }

    public int getGeneration() {
        return this.generation;
    }

    public void setGeneration(int generation2) {
        this.generation = generation2;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public void setDeleted(boolean isDeleted2) {
        this.isDeleted = isDeleted2;
    }

    public long getOffset() {
        return this.offset;
    }

    public void setOffset(long offset2) {
        this.offset = offset2;
    }

    public int getVolume() {
        return this.volume;
    }

    public void setVolume(int volume2) {
        this.volume = volume2;
    }

    public DumpArchiveConstants.SEGMENT_TYPE getHeaderType() {
        return this.header.getType();
    }

    public int getHeaderCount() {
        return this.header.getCount();
    }

    public int getHeaderHoles() {
        return this.header.getHoles();
    }

    public boolean isSparseRecord(int idx) {
        return (this.header.getCdata(idx) & 1) == 0;
    }

    public int hashCode() {
        return this.ino;
    }

    public boolean equals(Object o) {
        DumpArchiveSummary dumpArchiveSummary;
        if (o == this) {
            return true;
        }
        if (o == null || !o.getClass().equals(getClass())) {
            return false;
        }
        DumpArchiveEntry rhs = (DumpArchiveEntry) o;
        if (rhs.header == null || this.ino != rhs.ino) {
            return false;
        }
        if ((this.summary != null || rhs.summary == null) && ((dumpArchiveSummary = this.summary) == null || dumpArchiveSummary.equals(rhs.summary))) {
            return true;
        }
        return false;
    }

    public String toString() {
        return getName();
    }

    static DumpArchiveEntry parse(byte[] buffer) {
        DumpArchiveEntry entry = new DumpArchiveEntry();
        TapeSegmentHeader header2 = entry.header;
        DumpArchiveConstants.SEGMENT_TYPE unused = header2.type = DumpArchiveConstants.SEGMENT_TYPE.find(DumpArchiveUtil.convert32(buffer, 0));
        int unused2 = header2.volume = DumpArchiveUtil.convert32(buffer, 12);
        entry.ino = header2.ino = DumpArchiveUtil.convert32(buffer, 20);
        int m = DumpArchiveUtil.convert16(buffer, 32);
        entry.setType(TYPE.find((m >> 12) & 15));
        entry.setMode(m);
        entry.nlink = DumpArchiveUtil.convert16(buffer, 34);
        entry.setSize(DumpArchiveUtil.convert64(buffer, 40));
        entry.setAccessTime(new Date((((long) DumpArchiveUtil.convert32(buffer, 48)) * 1000) + ((long) (DumpArchiveUtil.convert32(buffer, 52) / 1000))));
        entry.setLastModifiedDate(new Date((((long) DumpArchiveUtil.convert32(buffer, 56)) * 1000) + ((long) (DumpArchiveUtil.convert32(buffer, 60) / 1000))));
        entry.ctime = (((long) DumpArchiveUtil.convert32(buffer, 64)) * 1000) + ((long) (DumpArchiveUtil.convert32(buffer, 68) / 1000));
        entry.generation = DumpArchiveUtil.convert32(buffer, 140);
        entry.setUserId(DumpArchiveUtil.convert32(buffer, 144));
        entry.setGroupId(DumpArchiveUtil.convert32(buffer, TarConstants.CHKSUM_OFFSET));
        int unused3 = header2.count = DumpArchiveUtil.convert32(buffer, 160);
        int unused4 = header2.holes = 0;
        int i = 0;
        while (i < 512 && i < header2.count) {
            if (buffer[i + 164] == 0) {
                TapeSegmentHeader.access$408(header2);
            }
            i++;
        }
        System.arraycopy(buffer, 164, header2.cdata, 0, 512);
        entry.volume = header2.getVolume();
        return entry;
    }

    /* access modifiers changed from: package-private */
    public void update(byte[] buffer) {
        int unused = this.header.volume = DumpArchiveUtil.convert32(buffer, 16);
        int unused2 = this.header.count = DumpArchiveUtil.convert32(buffer, 160);
        int unused3 = this.header.holes = 0;
        int i = 0;
        while (i < 512 && i < this.header.count) {
            if (buffer[i + 164] == 0) {
                TapeSegmentHeader.access$408(this.header);
            }
            i++;
        }
        System.arraycopy(buffer, 164, this.header.cdata, 0, 512);
    }

    static class TapeSegmentHeader {
        /* access modifiers changed from: private */
        public final byte[] cdata = new byte[512];
        /* access modifiers changed from: private */
        public int count;
        /* access modifiers changed from: private */
        public int holes;
        /* access modifiers changed from: private */
        public int ino;
        /* access modifiers changed from: private */
        public DumpArchiveConstants.SEGMENT_TYPE type;
        /* access modifiers changed from: private */
        public int volume;

        TapeSegmentHeader() {
        }

        static /* synthetic */ int access$408(TapeSegmentHeader x0) {
            int i = x0.holes;
            x0.holes = i + 1;
            return i;
        }

        public DumpArchiveConstants.SEGMENT_TYPE getType() {
            return this.type;
        }

        public int getVolume() {
            return this.volume;
        }

        public int getIno() {
            return this.ino;
        }

        /* access modifiers changed from: package-private */
        public void setIno(int ino2) {
            this.ino = ino2;
        }

        public int getCount() {
            return this.count;
        }

        public int getHoles() {
            return this.holes;
        }

        public int getCdata(int idx) {
            return this.cdata[idx];
        }
    }

    public String getName() {
        return this.name;
    }

    /* access modifiers changed from: package-private */
    public String getOriginalName() {
        return this.originalName;
    }

    public final void setName(String name2) {
        this.originalName = name2;
        if (name2 != null) {
            if (isDirectory() && !name2.endsWith("/")) {
                name2 = name2 + "/";
            }
            if (name2.startsWith("./")) {
                name2 = name2.substring(2);
            }
        }
        this.name = name2;
    }

    public Date getLastModifiedDate() {
        return new Date(this.mtime);
    }

    public boolean isDirectory() {
        return this.type == TYPE.DIRECTORY;
    }

    public boolean isFile() {
        return this.type == TYPE.FILE;
    }

    public boolean isSocket() {
        return this.type == TYPE.SOCKET;
    }

    public boolean isChrDev() {
        return this.type == TYPE.CHRDEV;
    }

    public boolean isBlkDev() {
        return this.type == TYPE.BLKDEV;
    }

    public boolean isFifo() {
        return this.type == TYPE.FIFO;
    }

    public TYPE getType() {
        return this.type;
    }

    public void setType(TYPE type2) {
        this.type = type2;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode2) {
        this.mode = mode2 & UnixStat.PERM_MASK;
        this.permissions = PERMISSION.find(mode2);
    }

    public Set<PERMISSION> getPermissions() {
        return this.permissions;
    }

    public long getSize() {
        if (isDirectory()) {
            return -1;
        }
        return this.size;
    }

    /* access modifiers changed from: package-private */
    public long getEntrySize() {
        return this.size;
    }

    public void setSize(long size2) {
        this.size = size2;
    }

    public void setLastModifiedDate(Date mtime2) {
        this.mtime = mtime2.getTime();
    }

    public Date getAccessTime() {
        return new Date(this.atime);
    }

    public void setAccessTime(Date atime2) {
        this.atime = atime2.getTime();
    }

    public int getUserId() {
        return this.uid;
    }

    public void setUserId(int uid2) {
        this.uid = uid2;
    }

    public int getGroupId() {
        return this.gid;
    }

    public void setGroupId(int gid2) {
        this.gid = gid2;
    }

    public enum TYPE {
        WHITEOUT(14),
        SOCKET(12),
        LINK(10),
        FILE(8),
        BLKDEV(6),
        DIRECTORY(4),
        CHRDEV(2),
        FIFO(1),
        UNKNOWN(15);
        
        private int code;

        private TYPE(int code2) {
            this.code = code2;
        }

        public static TYPE find(int code2) {
            TYPE type = UNKNOWN;
            for (TYPE t : values()) {
                if (code2 == t.code) {
                    type = t;
                }
            }
            return type;
        }
    }

    public enum PERMISSION {
        SETUID(2048),
        SETGUI(1024),
        STICKY(512),
        USER_READ(256),
        USER_WRITE(128),
        USER_EXEC(64),
        GROUP_READ(32),
        GROUP_WRITE(16),
        GROUP_EXEC(8),
        WORLD_READ(4),
        WORLD_WRITE(2),
        WORLD_EXEC(1);
        
        private int code;

        private PERMISSION(int code2) {
            this.code = code2;
        }

        public static Set<PERMISSION> find(int code2) {
            Set<PERMISSION> set = new HashSet<>();
            for (PERMISSION p : values()) {
                int i = p.code;
                if ((code2 & i) == i) {
                    set.add(p);
                }
            }
            if (set.isEmpty()) {
                return Collections.emptySet();
            }
            return EnumSet.copyOf(set);
        }
    }
}

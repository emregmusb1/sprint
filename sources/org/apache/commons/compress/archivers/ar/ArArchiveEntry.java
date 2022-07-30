package org.apache.commons.compress.archivers.ar;

import java.io.File;
import java.util.Date;
import org.apache.commons.compress.archivers.ArchiveEntry;

public class ArArchiveEntry implements ArchiveEntry {
    private static final int DEFAULT_MODE = 33188;
    public static final String HEADER = "!<arch>\n";
    public static final String TRAILER = "`\n";
    private final int groupId;
    private final long lastModified;
    private final long length;
    private final int mode;
    private final String name;
    private final int userId;

    public ArArchiveEntry(String name2, long length2) {
        this(name2, length2, 0, 0, 33188, System.currentTimeMillis() / 1000);
    }

    public ArArchiveEntry(String name2, long length2, int userId2, int groupId2, int mode2, long lastModified2) {
        this.name = name2;
        this.length = length2;
        this.userId = userId2;
        this.groupId = groupId2;
        this.mode = mode2;
        this.lastModified = lastModified2;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public ArArchiveEntry(File inputFile, String entryName) {
        this(entryName, inputFile.isFile() ? inputFile.length() : 0, 0, 0, 33188, inputFile.lastModified() / 1000);
    }

    public long getSize() {
        return getLength();
    }

    public String getName() {
        return this.name;
    }

    public int getUserId() {
        return this.userId;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public int getMode() {
        return this.mode;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public Date getLastModifiedDate() {
        return new Date(getLastModified() * 1000);
    }

    public long getLength() {
        return this.length;
    }

    public boolean isDirectory() {
        return false;
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
        ArArchiveEntry other = (ArArchiveEntry) obj;
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

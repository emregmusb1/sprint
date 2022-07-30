package org.apache.commons.compress.archivers.sevenz;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.TimeZone;
import org.apache.commons.compress.archivers.ArchiveEntry;

public class SevenZArchiveEntry implements ArchiveEntry {
    private long accessDate;
    private long compressedCrc;
    private long compressedSize;
    private Iterable<? extends SevenZMethodConfiguration> contentMethods;
    private long crc;
    private long creationDate;
    private boolean hasAccessDate;
    private boolean hasCrc;
    private boolean hasCreationDate;
    private boolean hasLastModifiedDate;
    private boolean hasStream;
    private boolean hasWindowsAttributes;
    private boolean isAntiItem;
    private boolean isDirectory;
    private long lastModifiedDate;
    private String name;
    private long size;
    private int windowsAttributes;

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public boolean hasStream() {
        return this.hasStream;
    }

    public void setHasStream(boolean hasStream2) {
        this.hasStream = hasStream2;
    }

    public boolean isDirectory() {
        return this.isDirectory;
    }

    public void setDirectory(boolean isDirectory2) {
        this.isDirectory = isDirectory2;
    }

    public boolean isAntiItem() {
        return this.isAntiItem;
    }

    public void setAntiItem(boolean isAntiItem2) {
        this.isAntiItem = isAntiItem2;
    }

    public boolean getHasCreationDate() {
        return this.hasCreationDate;
    }

    public void setHasCreationDate(boolean hasCreationDate2) {
        this.hasCreationDate = hasCreationDate2;
    }

    public Date getCreationDate() {
        if (this.hasCreationDate) {
            return ntfsTimeToJavaTime(this.creationDate);
        }
        throw new UnsupportedOperationException("The entry doesn't have this timestamp");
    }

    public void setCreationDate(long ntfsCreationDate) {
        this.creationDate = ntfsCreationDate;
    }

    public void setCreationDate(Date creationDate2) {
        this.hasCreationDate = creationDate2 != null;
        if (this.hasCreationDate) {
            this.creationDate = javaTimeToNtfsTime(creationDate2);
        }
    }

    public boolean getHasLastModifiedDate() {
        return this.hasLastModifiedDate;
    }

    public void setHasLastModifiedDate(boolean hasLastModifiedDate2) {
        this.hasLastModifiedDate = hasLastModifiedDate2;
    }

    public Date getLastModifiedDate() {
        if (this.hasLastModifiedDate) {
            return ntfsTimeToJavaTime(this.lastModifiedDate);
        }
        throw new UnsupportedOperationException("The entry doesn't have this timestamp");
    }

    public void setLastModifiedDate(long ntfsLastModifiedDate) {
        this.lastModifiedDate = ntfsLastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate2) {
        this.hasLastModifiedDate = lastModifiedDate2 != null;
        if (this.hasLastModifiedDate) {
            this.lastModifiedDate = javaTimeToNtfsTime(lastModifiedDate2);
        }
    }

    public boolean getHasAccessDate() {
        return this.hasAccessDate;
    }

    public void setHasAccessDate(boolean hasAcessDate) {
        this.hasAccessDate = hasAcessDate;
    }

    public Date getAccessDate() {
        if (this.hasAccessDate) {
            return ntfsTimeToJavaTime(this.accessDate);
        }
        throw new UnsupportedOperationException("The entry doesn't have this timestamp");
    }

    public void setAccessDate(long ntfsAccessDate) {
        this.accessDate = ntfsAccessDate;
    }

    public void setAccessDate(Date accessDate2) {
        this.hasAccessDate = accessDate2 != null;
        if (this.hasAccessDate) {
            this.accessDate = javaTimeToNtfsTime(accessDate2);
        }
    }

    public boolean getHasWindowsAttributes() {
        return this.hasWindowsAttributes;
    }

    public void setHasWindowsAttributes(boolean hasWindowsAttributes2) {
        this.hasWindowsAttributes = hasWindowsAttributes2;
    }

    public int getWindowsAttributes() {
        return this.windowsAttributes;
    }

    public void setWindowsAttributes(int windowsAttributes2) {
        this.windowsAttributes = windowsAttributes2;
    }

    public boolean getHasCrc() {
        return this.hasCrc;
    }

    public void setHasCrc(boolean hasCrc2) {
        this.hasCrc = hasCrc2;
    }

    @Deprecated
    public int getCrc() {
        return (int) this.crc;
    }

    @Deprecated
    public void setCrc(int crc2) {
        this.crc = (long) crc2;
    }

    public long getCrcValue() {
        return this.crc;
    }

    public void setCrcValue(long crc2) {
        this.crc = crc2;
    }

    /* access modifiers changed from: package-private */
    @Deprecated
    public int getCompressedCrc() {
        return (int) this.compressedCrc;
    }

    /* access modifiers changed from: package-private */
    @Deprecated
    public void setCompressedCrc(int crc2) {
        this.compressedCrc = (long) crc2;
    }

    /* access modifiers changed from: package-private */
    public long getCompressedCrcValue() {
        return this.compressedCrc;
    }

    /* access modifiers changed from: package-private */
    public void setCompressedCrcValue(long crc2) {
        this.compressedCrc = crc2;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size2) {
        this.size = size2;
    }

    /* access modifiers changed from: package-private */
    public long getCompressedSize() {
        return this.compressedSize;
    }

    /* access modifiers changed from: package-private */
    public void setCompressedSize(long size2) {
        this.compressedSize = size2;
    }

    public void setContentMethods(Iterable<? extends SevenZMethodConfiguration> methods) {
        if (methods != null) {
            LinkedList<SevenZMethodConfiguration> l = new LinkedList<>();
            for (SevenZMethodConfiguration m : methods) {
                l.addLast(m);
            }
            this.contentMethods = Collections.unmodifiableList(l);
            return;
        }
        this.contentMethods = null;
    }

    public Iterable<? extends SevenZMethodConfiguration> getContentMethods() {
        return this.contentMethods;
    }

    public int hashCode() {
        String n = getName();
        if (n == null) {
            return 0;
        }
        return n.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SevenZArchiveEntry other = (SevenZArchiveEntry) obj;
        if (Objects.equals(this.name, other.name) && this.hasStream == other.hasStream && this.isDirectory == other.isDirectory && this.isAntiItem == other.isAntiItem && this.hasCreationDate == other.hasCreationDate && this.hasLastModifiedDate == other.hasLastModifiedDate && this.hasAccessDate == other.hasAccessDate && this.creationDate == other.creationDate && this.lastModifiedDate == other.lastModifiedDate && this.accessDate == other.accessDate && this.hasWindowsAttributes == other.hasWindowsAttributes && this.windowsAttributes == other.windowsAttributes && this.hasCrc == other.hasCrc && this.crc == other.crc && this.compressedCrc == other.compressedCrc && this.size == other.size && this.compressedSize == other.compressedSize && equalSevenZMethods(this.contentMethods, other.contentMethods)) {
            return true;
        }
        return false;
    }

    public static Date ntfsTimeToJavaTime(long ntfsTime) {
        Calendar ntfsEpoch = Calendar.getInstance();
        ntfsEpoch.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        ntfsEpoch.set(1601, 0, 1, 0, 0, 0);
        ntfsEpoch.set(14, 0);
        return new Date(ntfsEpoch.getTimeInMillis() + (ntfsTime / 10000));
    }

    public static long javaTimeToNtfsTime(Date date) {
        Calendar ntfsEpoch = Calendar.getInstance();
        ntfsEpoch.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        ntfsEpoch.set(1601, 0, 1, 0, 0, 0);
        ntfsEpoch.set(14, 0);
        return (date.getTime() - ntfsEpoch.getTimeInMillis()) * 1000 * 10;
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:0x001a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean equalSevenZMethods(java.lang.Iterable<? extends org.apache.commons.compress.archivers.sevenz.SevenZMethodConfiguration> r7, java.lang.Iterable<? extends org.apache.commons.compress.archivers.sevenz.SevenZMethodConfiguration> r8) {
        /*
            r6 = this;
            r0 = 1
            r1 = 0
            if (r7 != 0) goto L_0x0009
            if (r8 != 0) goto L_0x0007
            goto L_0x0008
        L_0x0007:
            r0 = 0
        L_0x0008:
            return r0
        L_0x0009:
            if (r8 != 0) goto L_0x000c
            return r1
        L_0x000c:
            java.util.Iterator r2 = r7.iterator()
            java.util.Iterator r3 = r8.iterator()
        L_0x0014:
            boolean r4 = r2.hasNext()
            if (r4 == 0) goto L_0x0032
            boolean r4 = r3.hasNext()
            if (r4 != 0) goto L_0x0021
            return r1
        L_0x0021:
            java.lang.Object r4 = r2.next()
            org.apache.commons.compress.archivers.sevenz.SevenZMethodConfiguration r4 = (org.apache.commons.compress.archivers.sevenz.SevenZMethodConfiguration) r4
            java.lang.Object r5 = r3.next()
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto L_0x0014
            return r1
        L_0x0032:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x0039
            return r1
        L_0x0039:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry.equalSevenZMethods(java.lang.Iterable, java.lang.Iterable):boolean");
    }
}

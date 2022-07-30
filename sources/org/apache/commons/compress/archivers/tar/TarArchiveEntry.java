package org.apache.commons.compress.archivers.tar;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.utils.ArchiveUtils;

public class TarArchiveEntry implements ArchiveEntry, TarConstants {
    public static final int DEFAULT_DIR_MODE = 16877;
    public static final int DEFAULT_FILE_MODE = 33188;
    private static final TarArchiveEntry[] EMPTY_TAR_ARCHIVE_ENTRIES = new TarArchiveEntry[0];
    public static final int MAX_NAMELEN = 31;
    public static final int MILLIS_PER_SECOND = 1000;
    public static final long UNKNOWN = -1;
    private boolean checkSumOK;
    private int devMajor;
    private int devMinor;
    private final Map<String, String> extraPaxHeaders;
    private final File file;
    private long groupId;
    private String groupName;
    private boolean isExtended;
    private byte linkFlag;
    private String linkName;
    private String magic;
    private long modTime;
    private int mode;
    private String name;
    private boolean paxGNUSparse;
    private final boolean preserveAbsolutePath;
    private long realSize;
    private long size;
    private boolean starSparse;
    private long userId;
    private String userName;
    private String version;

    private TarArchiveEntry(boolean preserveAbsolutePath2) {
        this.name = "";
        this.userId = 0;
        this.groupId = 0;
        this.size = 0;
        this.linkName = "";
        this.magic = "ustar\u0000";
        this.version = TarConstants.VERSION_POSIX;
        this.groupName = "";
        this.devMajor = 0;
        this.devMinor = 0;
        this.extraPaxHeaders = new HashMap();
        String user = System.getProperty("user.name", "");
        this.userName = user.length() > 31 ? user.substring(0, 31) : user;
        this.file = null;
        this.preserveAbsolutePath = preserveAbsolutePath2;
    }

    public TarArchiveEntry(String name2) {
        this(name2, false);
    }

    public TarArchiveEntry(String name2, boolean preserveAbsolutePath2) {
        this(preserveAbsolutePath2);
        String name3 = normalizeFileName(name2, preserveAbsolutePath2);
        boolean isDir = name3.endsWith("/");
        this.name = name3;
        this.mode = isDir ? DEFAULT_DIR_MODE : DEFAULT_FILE_MODE;
        this.linkFlag = isDir ? TarConstants.LF_DIR : TarConstants.LF_NORMAL;
        this.modTime = new Date().getTime() / 1000;
        this.userName = "";
    }

    public TarArchiveEntry(String name2, byte linkFlag2) {
        this(name2, linkFlag2, false);
    }

    public TarArchiveEntry(String name2, byte linkFlag2, boolean preserveAbsolutePath2) {
        this(name2, preserveAbsolutePath2);
        this.linkFlag = linkFlag2;
        if (linkFlag2 == 76) {
            this.magic = TarConstants.MAGIC_GNU;
            this.version = TarConstants.VERSION_GNU_SPACE;
        }
    }

    public TarArchiveEntry(File file2) {
        this(file2, file2.getPath());
    }

    public TarArchiveEntry(File file2, String fileName) {
        this.name = "";
        this.userId = 0;
        this.groupId = 0;
        this.size = 0;
        this.linkName = "";
        this.magic = "ustar\u0000";
        this.version = TarConstants.VERSION_POSIX;
        this.groupName = "";
        this.devMajor = 0;
        this.devMinor = 0;
        this.extraPaxHeaders = new HashMap();
        String normalizedName = normalizeFileName(fileName, false);
        this.file = file2;
        if (file2.isDirectory()) {
            this.mode = DEFAULT_DIR_MODE;
            this.linkFlag = TarConstants.LF_DIR;
            int nameLength = normalizedName.length();
            if (nameLength == 0 || normalizedName.charAt(nameLength - 1) != '/') {
                this.name = normalizedName + "/";
            } else {
                this.name = normalizedName;
            }
        } else {
            this.mode = DEFAULT_FILE_MODE;
            this.linkFlag = TarConstants.LF_NORMAL;
            this.size = file2.length();
            this.name = normalizedName;
        }
        this.modTime = file2.lastModified() / 1000;
        this.userName = "";
        this.preserveAbsolutePath = false;
    }

    public TarArchiveEntry(byte[] headerBuf) {
        this(false);
        parseTarHeader(headerBuf);
    }

    public TarArchiveEntry(byte[] headerBuf, ZipEncoding encoding) throws IOException {
        this(headerBuf, encoding, false);
    }

    public TarArchiveEntry(byte[] headerBuf, ZipEncoding encoding, boolean lenient) throws IOException {
        this(false);
        parseTarHeader(headerBuf, encoding, false, lenient);
    }

    public boolean equals(TarArchiveEntry it) {
        return it != null && getName().equals(it.getName());
    }

    public boolean equals(Object it) {
        if (it == null || getClass() != it.getClass()) {
            return false;
        }
        return equals((TarArchiveEntry) it);
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public boolean isDescendent(TarArchiveEntry desc) {
        return desc.getName().startsWith(getName());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = normalizeFileName(name2, this.preserveAbsolutePath);
    }

    public void setMode(int mode2) {
        this.mode = mode2;
    }

    public String getLinkName() {
        return this.linkName;
    }

    public void setLinkName(String link) {
        this.linkName = link;
    }

    @Deprecated
    public int getUserId() {
        return (int) (this.userId & -1);
    }

    public void setUserId(int userId2) {
        setUserId((long) userId2);
    }

    public long getLongUserId() {
        return this.userId;
    }

    public void setUserId(long userId2) {
        this.userId = userId2;
    }

    @Deprecated
    public int getGroupId() {
        return (int) (this.groupId & -1);
    }

    public void setGroupId(int groupId2) {
        setGroupId((long) groupId2);
    }

    public long getLongGroupId() {
        return this.groupId;
    }

    public void setGroupId(long groupId2) {
        this.groupId = groupId2;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName2) {
        this.userName = userName2;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName2) {
        this.groupName = groupName2;
    }

    public void setIds(int userId2, int groupId2) {
        setUserId(userId2);
        setGroupId(groupId2);
    }

    public void setNames(String userName2, String groupName2) {
        setUserName(userName2);
        setGroupName(groupName2);
    }

    public void setModTime(long time) {
        this.modTime = time / 1000;
    }

    public void setModTime(Date time) {
        this.modTime = time.getTime() / 1000;
    }

    public Date getModTime() {
        return new Date(this.modTime * 1000);
    }

    public Date getLastModifiedDate() {
        return getModTime();
    }

    public boolean isCheckSumOK() {
        return this.checkSumOK;
    }

    public File getFile() {
        return this.file;
    }

    public int getMode() {
        return this.mode;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size2) {
        if (size2 >= 0) {
            this.size = size2;
            return;
        }
        throw new IllegalArgumentException("Size is out of range: " + size2);
    }

    public int getDevMajor() {
        return this.devMajor;
    }

    public void setDevMajor(int devNo) {
        if (devNo >= 0) {
            this.devMajor = devNo;
            return;
        }
        throw new IllegalArgumentException("Major device number is out of range: " + devNo);
    }

    public int getDevMinor() {
        return this.devMinor;
    }

    public void setDevMinor(int devNo) {
        if (devNo >= 0) {
            this.devMinor = devNo;
            return;
        }
        throw new IllegalArgumentException("Minor device number is out of range: " + devNo);
    }

    public boolean isExtended() {
        return this.isExtended;
    }

    public long getRealSize() {
        return this.realSize;
    }

    public boolean isGNUSparse() {
        return isOldGNUSparse() || isPaxGNUSparse();
    }

    public boolean isOldGNUSparse() {
        return this.linkFlag == 83;
    }

    public boolean isPaxGNUSparse() {
        return this.paxGNUSparse;
    }

    public boolean isStarSparse() {
        return this.starSparse;
    }

    public boolean isGNULongLinkEntry() {
        return this.linkFlag == 75;
    }

    public boolean isGNULongNameEntry() {
        return this.linkFlag == 76;
    }

    public boolean isPaxHeader() {
        byte b = this.linkFlag;
        return b == 120 || b == 88;
    }

    public boolean isGlobalPaxHeader() {
        return this.linkFlag == 103;
    }

    public boolean isDirectory() {
        File file2 = this.file;
        if (file2 != null) {
            return file2.isDirectory();
        }
        if (this.linkFlag == 53) {
            return true;
        }
        if (isPaxHeader() || isGlobalPaxHeader() || !getName().endsWith("/")) {
            return false;
        }
        return true;
    }

    public boolean isFile() {
        File file2 = this.file;
        if (file2 != null) {
            return file2.isFile();
        }
        byte b = this.linkFlag;
        if (b == 0 || b == 48) {
            return true;
        }
        return !getName().endsWith("/");
    }

    public boolean isSymbolicLink() {
        return this.linkFlag == 50;
    }

    public boolean isLink() {
        return this.linkFlag == 49;
    }

    public boolean isCharacterDevice() {
        return this.linkFlag == 51;
    }

    public boolean isBlockDevice() {
        return this.linkFlag == 52;
    }

    public boolean isFIFO() {
        return this.linkFlag == 54;
    }

    public boolean isSparse() {
        return isGNUSparse() || isStarSparse();
    }

    public Map<String, String> getExtraPaxHeaders() {
        return Collections.unmodifiableMap(this.extraPaxHeaders);
    }

    public void clearExtraPaxHeaders() {
        this.extraPaxHeaders.clear();
    }

    public void addPaxHeader(String name2, String value) {
        processPaxHeader(name2, value);
    }

    public String getExtraPaxHeader(String name2) {
        return this.extraPaxHeaders.get(name2);
    }

    /* access modifiers changed from: package-private */
    public void updateEntryFromPaxHeaders(Map<String, String> headers) {
        for (Map.Entry<String, String> ent : headers.entrySet()) {
            processPaxHeader(ent.getKey(), ent.getValue(), headers);
        }
    }

    private void processPaxHeader(String key, String val) {
        processPaxHeader(key, val, this.extraPaxHeaders);
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void processPaxHeader(java.lang.String r5, java.lang.String r6, java.util.Map<java.lang.String, java.lang.String> r7) {
        /*
            r4 = this;
            int r0 = r5.hashCode()
            switch(r0) {
                case -1916861932: goto L_0x0088;
                case -1916619760: goto L_0x007d;
                case -277496563: goto L_0x0072;
                case -160380561: goto L_0x0067;
                case 102338: goto L_0x005d;
                case 115792: goto L_0x0053;
                case 3433509: goto L_0x0049;
                case 3530753: goto L_0x003f;
                case 98496370: goto L_0x0035;
                case 104223930: goto L_0x002b;
                case 111425664: goto L_0x0020;
                case 530706950: goto L_0x0014;
                case 1195018015: goto L_0x0009;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x0093
        L_0x0009:
            java.lang.String r0 = "linkpath"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 1
            goto L_0x0094
        L_0x0014:
            java.lang.String r0 = "SCHILY.filetype"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 12
            goto L_0x0094
        L_0x0020:
            java.lang.String r0 = "uname"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 5
            goto L_0x0094
        L_0x002b:
            java.lang.String r0 = "mtime"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 7
            goto L_0x0094
        L_0x0035:
            java.lang.String r0 = "gname"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 3
            goto L_0x0094
        L_0x003f:
            java.lang.String r0 = "size"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 6
            goto L_0x0094
        L_0x0049:
            java.lang.String r0 = "path"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 0
            goto L_0x0094
        L_0x0053:
            java.lang.String r0 = "uid"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 4
            goto L_0x0094
        L_0x005d:
            java.lang.String r0 = "gid"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 2
            goto L_0x0094
        L_0x0067:
            java.lang.String r0 = "GNU.sparse.size"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 10
            goto L_0x0094
        L_0x0072:
            java.lang.String r0 = "GNU.sparse.realsize"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 11
            goto L_0x0094
        L_0x007d:
            java.lang.String r0 = "SCHILY.devminor"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 8
            goto L_0x0094
        L_0x0088:
            java.lang.String r0 = "SCHILY.devmajor"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 9
            goto L_0x0094
        L_0x0093:
            r0 = -1
        L_0x0094:
            switch(r0) {
                case 0: goto L_0x00f5;
                case 1: goto L_0x00f1;
                case 2: goto L_0x00e9;
                case 3: goto L_0x00e5;
                case 4: goto L_0x00dd;
                case 5: goto L_0x00d9;
                case 6: goto L_0x00d1;
                case 7: goto L_0x00c1;
                case 8: goto L_0x00b9;
                case 9: goto L_0x00b1;
                case 10: goto L_0x00ad;
                case 11: goto L_0x00a9;
                case 12: goto L_0x009d;
                default: goto L_0x0097;
            }
        L_0x0097:
            java.util.Map<java.lang.String, java.lang.String> r0 = r4.extraPaxHeaders
            r0.put(r5, r6)
            goto L_0x00f9
        L_0x009d:
            java.lang.String r0 = "sparse"
            boolean r0 = r0.equals(r6)
            if (r0 == 0) goto L_0x00f9
            r4.fillStarSparseData(r7)
            goto L_0x00f9
        L_0x00a9:
            r4.fillGNUSparse1xData(r7)
            goto L_0x00f9
        L_0x00ad:
            r4.fillGNUSparse0xData(r7)
            goto L_0x00f9
        L_0x00b1:
            int r0 = java.lang.Integer.parseInt(r6)
            r4.setDevMajor(r0)
            goto L_0x00f9
        L_0x00b9:
            int r0 = java.lang.Integer.parseInt(r6)
            r4.setDevMinor(r0)
            goto L_0x00f9
        L_0x00c1:
            double r0 = java.lang.Double.parseDouble(r6)
            r2 = 4652007308841189376(0x408f400000000000, double:1000.0)
            double r0 = r0 * r2
            long r0 = (long) r0
            r4.setModTime((long) r0)
            goto L_0x00f9
        L_0x00d1:
            long r0 = java.lang.Long.parseLong(r6)
            r4.setSize(r0)
            goto L_0x00f9
        L_0x00d9:
            r4.setUserName(r6)
            goto L_0x00f9
        L_0x00dd:
            long r0 = java.lang.Long.parseLong(r6)
            r4.setUserId((long) r0)
            goto L_0x00f9
        L_0x00e5:
            r4.setGroupName(r6)
            goto L_0x00f9
        L_0x00e9:
            long r0 = java.lang.Long.parseLong(r6)
            r4.setGroupId((long) r0)
            goto L_0x00f9
        L_0x00f1:
            r4.setLinkName(r6)
            goto L_0x00f9
        L_0x00f5:
            r4.setName(r6)
        L_0x00f9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.tar.TarArchiveEntry.processPaxHeader(java.lang.String, java.lang.String, java.util.Map):void");
    }

    public TarArchiveEntry[] getDirectoryEntries() {
        File file2 = this.file;
        if (file2 == null || !file2.isDirectory()) {
            return EMPTY_TAR_ARCHIVE_ENTRIES;
        }
        String[] list = this.file.list();
        if (list == null) {
            return EMPTY_TAR_ARCHIVE_ENTRIES;
        }
        TarArchiveEntry[] result = new TarArchiveEntry[list.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new TarArchiveEntry(new File(this.file, list[i]));
        }
        return result;
    }

    public void writeEntryHeader(byte[] outbuf) {
        try {
            writeEntryHeader(outbuf, TarUtils.DEFAULT_ENCODING, false);
        } catch (IOException e) {
            try {
                writeEntryHeader(outbuf, TarUtils.FALLBACK_ENCODING, false);
            } catch (IOException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }

    public void writeEntryHeader(byte[] outbuf, ZipEncoding encoding, boolean starMode) throws IOException {
        byte[] bArr = outbuf;
        ZipEncoding zipEncoding = encoding;
        int offset = TarUtils.formatNameBytes(this.name, outbuf, 0, 100, encoding);
        byte[] bArr2 = outbuf;
        boolean z = starMode;
        int offset2 = writeEntryHeaderField(this.modTime, bArr2, writeEntryHeaderField(this.size, bArr2, writeEntryHeaderField(this.groupId, bArr2, writeEntryHeaderField(this.userId, bArr2, writeEntryHeaderField((long) this.mode, bArr2, offset, 8, z), 8, z), 8, z), 12, z), 12, z);
        int csOffset = offset2;
        int c = 0;
        while (c < 8) {
            bArr[offset2] = 32;
            c++;
            offset2++;
        }
        bArr[offset2] = this.linkFlag;
        int offset3 = TarUtils.formatNameBytes(this.groupName, outbuf, TarUtils.formatNameBytes(this.userName, outbuf, TarUtils.formatNameBytes(this.version, outbuf, TarUtils.formatNameBytes(this.magic, outbuf, TarUtils.formatNameBytes(this.linkName, outbuf, offset2 + 1, 100, encoding), 6), 2), 32, encoding), 32, encoding);
        byte[] bArr3 = outbuf;
        boolean z2 = starMode;
        int offset4 = writeEntryHeaderField((long) this.devMajor, bArr3, offset3, 8, z2);
        for (int offset5 = writeEntryHeaderField((long) this.devMinor, bArr3, offset4, 8, z2); offset5 < bArr.length; offset5++) {
            bArr[offset5] = 0;
        }
        TarUtils.formatCheckSumOctalBytes(TarUtils.computeCheckSum(outbuf), outbuf, csOffset, 8);
    }

    private int writeEntryHeaderField(long value, byte[] outbuf, int offset, int length, boolean starMode) {
        if (starMode || (value >= 0 && value < (1 << ((length - 1) * 3)))) {
            return TarUtils.formatLongOctalOrBinaryBytes(value, outbuf, offset, length);
        }
        return TarUtils.formatLongOctalBytes(0, outbuf, offset, length);
    }

    public void parseTarHeader(byte[] header) {
        try {
            parseTarHeader(header, TarUtils.DEFAULT_ENCODING);
        } catch (IOException e) {
            try {
                parseTarHeader(header, TarUtils.DEFAULT_ENCODING, true, false);
            } catch (IOException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }

    public void parseTarHeader(byte[] header, ZipEncoding encoding) throws IOException {
        parseTarHeader(header, encoding, false, false);
    }

    private void parseTarHeader(byte[] header, ZipEncoding encoding, boolean oldStyle, boolean lenient) throws IOException {
        String str;
        String str2;
        String str3;
        String str4;
        int offset;
        String xstarPrefix;
        String prefix;
        if (oldStyle) {
            str = TarUtils.parseName(header, 0, 100);
        } else {
            str = TarUtils.parseName(header, 0, 100, encoding);
        }
        this.name = str;
        int offset2 = 0 + 100;
        this.mode = (int) parseOctalOrBinary(header, offset2, 8, lenient);
        int offset3 = offset2 + 8;
        this.userId = (long) ((int) parseOctalOrBinary(header, offset3, 8, lenient));
        int offset4 = offset3 + 8;
        this.groupId = (long) ((int) parseOctalOrBinary(header, offset4, 8, lenient));
        int offset5 = offset4 + 8;
        this.size = TarUtils.parseOctalOrBinary(header, offset5, 12);
        int offset6 = offset5 + 12;
        this.modTime = parseOctalOrBinary(header, offset6, 12, lenient);
        this.checkSumOK = TarUtils.verifyCheckSum(header);
        int offset7 = offset6 + 12 + 8;
        int offset8 = offset7 + 1;
        this.linkFlag = header[offset7];
        if (oldStyle) {
            str2 = TarUtils.parseName(header, offset8, 100);
        } else {
            str2 = TarUtils.parseName(header, offset8, 100, encoding);
        }
        this.linkName = str2;
        int offset9 = offset8 + 100;
        this.magic = TarUtils.parseName(header, offset9, 6);
        int offset10 = offset9 + 6;
        this.version = TarUtils.parseName(header, offset10, 2);
        int offset11 = offset10 + 2;
        if (oldStyle) {
            str3 = TarUtils.parseName(header, offset11, 32);
        } else {
            str3 = TarUtils.parseName(header, offset11, 32, encoding);
        }
        this.userName = str3;
        int offset12 = offset11 + 32;
        if (oldStyle) {
            str4 = TarUtils.parseName(header, offset12, 32);
        } else {
            str4 = TarUtils.parseName(header, offset12, 32, encoding);
        }
        this.groupName = str4;
        int offset13 = offset12 + 32;
        byte b = this.linkFlag;
        if (b == 51 || b == 52) {
            this.devMajor = (int) parseOctalOrBinary(header, offset13, 8, lenient);
            int offset14 = offset13 + 8;
            this.devMinor = (int) parseOctalOrBinary(header, offset14, 8, lenient);
            offset = offset14 + 8;
        } else {
            offset = offset13 + 16;
        }
        int type = evaluateType(header);
        if (type == 2) {
            int offset15 = offset + 12 + 12 + 12 + 4 + 1 + 96;
            this.isExtended = TarUtils.parseBoolean(header, offset15);
            int offset16 = offset15 + 1;
            this.realSize = TarUtils.parseOctal(header, offset16, 12);
            int offset17 = offset16 + 12;
        } else if (type != 4) {
            if (oldStyle) {
                prefix = TarUtils.parseName(header, offset, TarConstants.PREFIXLEN);
            } else {
                prefix = TarUtils.parseName(header, offset, TarConstants.PREFIXLEN, encoding);
            }
            if (isDirectory() && !this.name.endsWith("/")) {
                this.name += "/";
            }
            if (prefix.length() > 0) {
                this.name = prefix + "/" + this.name;
            }
        } else {
            if (oldStyle) {
                xstarPrefix = TarUtils.parseName(header, offset, TarConstants.PREFIXLEN_XSTAR);
            } else {
                xstarPrefix = TarUtils.parseName(header, offset, TarConstants.PREFIXLEN_XSTAR, encoding);
            }
            if (xstarPrefix.length() > 0) {
                this.name = xstarPrefix + "/" + this.name;
            }
        }
    }

    private long parseOctalOrBinary(byte[] header, int offset, int length, boolean lenient) {
        if (!lenient) {
            return TarUtils.parseOctalOrBinary(header, offset, length);
        }
        try {
            return TarUtils.parseOctalOrBinary(header, offset, length);
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

    private static String normalizeFileName(String fileName, boolean preserveAbsolutePath2) {
        String osname;
        int colon;
        if (!preserveAbsolutePath2 && (osname = System.getProperty("os.name").toLowerCase(Locale.ENGLISH)) != null) {
            if (osname.startsWith("windows")) {
                if (fileName.length() > 2) {
                    char ch1 = fileName.charAt(0);
                    if (fileName.charAt(1) == ':' && ((ch1 >= 'a' && ch1 <= 'z') || (ch1 >= 'A' && ch1 <= 'Z'))) {
                        fileName = fileName.substring(2);
                    }
                }
            } else if (osname.contains("netware") && (colon = fileName.indexOf(58)) != -1) {
                fileName = fileName.substring(colon + 1);
            }
        }
        String fileName2 = fileName.replace(File.separatorChar, '/');
        while (!preserveAbsolutePath2 && fileName2.startsWith("/")) {
            fileName2 = fileName2.substring(1);
        }
        return fileName2;
    }

    private int evaluateType(byte[] header) {
        if (ArchiveUtils.matchAsciiBuffer(TarConstants.MAGIC_GNU, header, 257, 6)) {
            return 2;
        }
        if (!ArchiveUtils.matchAsciiBuffer("ustar\u0000", header, 257, 6)) {
            return 0;
        }
        if (ArchiveUtils.matchAsciiBuffer(TarConstants.MAGIC_XSTAR, header, TarConstants.XSTAR_MAGIC_OFFSET, 4)) {
            return 4;
        }
        return 3;
    }

    /* access modifiers changed from: package-private */
    public void fillGNUSparse0xData(Map<String, String> headers) {
        this.paxGNUSparse = true;
        this.realSize = (long) Integer.parseInt(headers.get("GNU.sparse.size"));
        if (headers.containsKey("GNU.sparse.name")) {
            this.name = headers.get("GNU.sparse.name");
        }
    }

    /* access modifiers changed from: package-private */
    public void fillGNUSparse1xData(Map<String, String> headers) {
        this.paxGNUSparse = true;
        this.realSize = (long) Integer.parseInt(headers.get("GNU.sparse.realsize"));
        this.name = headers.get("GNU.sparse.name");
    }

    /* access modifiers changed from: package-private */
    public void fillStarSparseData(Map<String, String> headers) {
        this.starSparse = true;
        if (headers.containsKey("SCHILY.realsize")) {
            this.realSize = Long.parseLong(headers.get("SCHILY.realsize"));
        }
    }
}

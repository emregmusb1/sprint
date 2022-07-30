package org.apache.commons.compress.archivers.zip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.EntryStreamOffsets;
import org.apache.commons.compress.archivers.zip.ExtraFieldUtils;

public class ZipArchiveEntry extends ZipEntry implements ArchiveEntry, EntryStreamOffsets {
    public static final int CRC_UNKNOWN = -1;
    private static final byte[] EMPTY = new byte[0];
    public static final int PLATFORM_FAT = 0;
    public static final int PLATFORM_UNIX = 3;
    private static final int SHORT_MASK = 65535;
    private static final int SHORT_SHIFT = 16;
    private static final ZipExtraField[] noExtraFields = new ZipExtraField[0];
    private int alignment;
    private CommentSource commentSource;
    private long dataOffset;
    private long externalAttributes;
    private ZipExtraField[] extraFields;
    private GeneralPurposeBit gpb;
    private int internalAttributes;
    private boolean isStreamContiguous;
    private long localHeaderOffset;
    private int method;
    private String name;
    private NameSource nameSource;
    private int platform;
    private int rawFlag;
    private byte[] rawName;
    private long size;
    private UnparseableExtraFieldData unparseableExtra;
    private int versionMadeBy;
    private int versionRequired;

    public enum CommentSource {
        COMMENT,
        UNICODE_EXTRA_FIELD
    }

    public enum NameSource {
        NAME,
        NAME_WITH_EFS_FLAG,
        UNICODE_EXTRA_FIELD
    }

    public ZipArchiveEntry(String name2) {
        super(name2);
        this.method = -1;
        this.size = -1;
        this.internalAttributes = 0;
        this.platform = 0;
        this.externalAttributes = 0;
        this.alignment = 0;
        this.unparseableExtra = null;
        this.name = null;
        this.rawName = null;
        this.gpb = new GeneralPurposeBit();
        this.localHeaderOffset = -1;
        this.dataOffset = -1;
        this.isStreamContiguous = false;
        this.nameSource = NameSource.NAME;
        this.commentSource = CommentSource.COMMENT;
        setName(name2);
    }

    public ZipArchiveEntry(ZipEntry entry) throws ZipException {
        super(entry);
        this.method = -1;
        this.size = -1;
        this.internalAttributes = 0;
        this.platform = 0;
        this.externalAttributes = 0;
        this.alignment = 0;
        this.unparseableExtra = null;
        this.name = null;
        this.rawName = null;
        this.gpb = new GeneralPurposeBit();
        this.localHeaderOffset = -1;
        this.dataOffset = -1;
        this.isStreamContiguous = false;
        this.nameSource = NameSource.NAME;
        this.commentSource = CommentSource.COMMENT;
        setName(entry.getName());
        byte[] extra = entry.getExtra();
        if (extra != null) {
            setExtraFields(ExtraFieldUtils.parse(extra, true, (ExtraFieldParsingBehavior) ExtraFieldParsingMode.BEST_EFFORT));
        } else {
            setExtra();
        }
        setMethod(entry.getMethod());
        this.size = entry.getSize();
    }

    public ZipArchiveEntry(ZipArchiveEntry entry) throws ZipException {
        this((ZipEntry) entry);
        GeneralPurposeBit generalPurposeBit;
        setInternalAttributes(entry.getInternalAttributes());
        setExternalAttributes(entry.getExternalAttributes());
        setExtraFields(getAllExtraFieldsNoCopy());
        setPlatform(entry.getPlatform());
        GeneralPurposeBit other = entry.getGeneralPurposeBit();
        if (other == null) {
            generalPurposeBit = null;
        } else {
            generalPurposeBit = (GeneralPurposeBit) other.clone();
        }
        setGeneralPurposeBit(generalPurposeBit);
    }

    protected ZipArchiveEntry() {
        this("");
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ZipArchiveEntry(java.io.File r3, java.lang.String r4) {
        /*
            r2 = this;
            boolean r0 = r3.isDirectory()
            if (r0 == 0) goto L_0x001e
            java.lang.String r0 = "/"
            boolean r1 = r4.endsWith(r0)
            if (r1 != 0) goto L_0x001e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x001f
        L_0x001e:
            r0 = r4
        L_0x001f:
            r2.<init>((java.lang.String) r0)
            boolean r0 = r3.isFile()
            if (r0 == 0) goto L_0x002f
            long r0 = r3.length()
            r2.setSize(r0)
        L_0x002f:
            long r0 = r3.lastModified()
            r2.setTime(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.zip.ZipArchiveEntry.<init>(java.io.File, java.lang.String):void");
    }

    public Object clone() {
        ZipArchiveEntry e = (ZipArchiveEntry) super.clone();
        e.setInternalAttributes(getInternalAttributes());
        e.setExternalAttributes(getExternalAttributes());
        e.setExtraFields(getAllExtraFieldsNoCopy());
        return e;
    }

    public int getMethod() {
        return this.method;
    }

    public void setMethod(int method2) {
        if (method2 >= 0) {
            this.method = method2;
            return;
        }
        throw new IllegalArgumentException("ZIP compression method can not be negative: " + method2);
    }

    public int getInternalAttributes() {
        return this.internalAttributes;
    }

    public void setInternalAttributes(int value) {
        this.internalAttributes = value;
    }

    public long getExternalAttributes() {
        return this.externalAttributes;
    }

    public void setExternalAttributes(long value) {
        this.externalAttributes = value;
    }

    public void setUnixMode(int mode) {
        int i = 0;
        int i2 = (mode << 16) | ((mode & 128) == 0 ? 1 : 0);
        if (isDirectory()) {
            i = 16;
        }
        setExternalAttributes((long) (i2 | i));
        this.platform = 3;
    }

    public int getUnixMode() {
        if (this.platform != 3) {
            return 0;
        }
        return (int) ((getExternalAttributes() >> 16) & 65535);
    }

    public boolean isUnixSymlink() {
        return (getUnixMode() & 61440) == 40960;
    }

    public int getPlatform() {
        return this.platform;
    }

    /* access modifiers changed from: protected */
    public void setPlatform(int platform2) {
        this.platform = platform2;
    }

    /* access modifiers changed from: protected */
    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int alignment2) {
        if (((alignment2 - 1) & alignment2) != 0 || alignment2 > 65535) {
            throw new IllegalArgumentException("Invalid value for alignment, must be power of two and no bigger than 65535 but is " + alignment2);
        }
        this.alignment = alignment2;
    }

    public void setExtraFields(ZipExtraField[] fields) {
        this.unparseableExtra = null;
        List<ZipExtraField> newFields = new ArrayList<>();
        if (fields != null) {
            for (UnparseableExtraFieldData unparseableExtraFieldData : fields) {
                if (unparseableExtraFieldData instanceof UnparseableExtraFieldData) {
                    this.unparseableExtra = unparseableExtraFieldData;
                } else {
                    newFields.add(unparseableExtraFieldData);
                }
            }
        }
        this.extraFields = (ZipExtraField[]) newFields.toArray(noExtraFields);
        setExtra();
    }

    public ZipExtraField[] getExtraFields() {
        return getParseableExtraFields();
    }

    public ZipExtraField[] getExtraFields(boolean includeUnparseable) {
        if (includeUnparseable) {
            return getAllExtraFields();
        }
        return getParseableExtraFields();
    }

    public ZipExtraField[] getExtraFields(ExtraFieldParsingBehavior parsingBehavior) throws ZipException {
        ZipExtraField c;
        if (parsingBehavior == ExtraFieldParsingMode.BEST_EFFORT) {
            return getExtraFields(true);
        }
        if (parsingBehavior == ExtraFieldParsingMode.ONLY_PARSEABLE_LENIENT) {
            return getExtraFields(false);
        }
        List<ZipExtraField> localFields = new ArrayList<>(Arrays.asList(ExtraFieldUtils.parse(getExtra(), true, parsingBehavior)));
        List<ZipExtraField> centralFields = new ArrayList<>(Arrays.asList(ExtraFieldUtils.parse(getCentralDirectoryExtra(), false, parsingBehavior)));
        List<ZipExtraField> merged = new ArrayList<>();
        for (ZipExtraField l : localFields) {
            if (l instanceof UnparseableExtraFieldData) {
                c = findUnparseable(centralFields);
            } else {
                c = findMatching(l.getHeaderId(), centralFields);
            }
            if (c != null) {
                byte[] cd = c.getCentralDirectoryData();
                if (cd != null && cd.length > 0) {
                    l.parseFromCentralDirectoryData(cd, 0, cd.length);
                }
                centralFields.remove(c);
            }
            merged.add(l);
        }
        merged.addAll(centralFields);
        return (ZipExtraField[]) merged.toArray(noExtraFields);
    }

    private ZipExtraField[] getParseableExtraFieldsNoCopy() {
        ZipExtraField[] zipExtraFieldArr = this.extraFields;
        if (zipExtraFieldArr == null) {
            return noExtraFields;
        }
        return zipExtraFieldArr;
    }

    private ZipExtraField[] getParseableExtraFields() {
        ZipExtraField[] parseableExtraFields = getParseableExtraFieldsNoCopy();
        return parseableExtraFields == this.extraFields ? (ZipExtraField[]) Arrays.copyOf(parseableExtraFields, parseableExtraFields.length) : parseableExtraFields;
    }

    private ZipExtraField[] getAllExtraFieldsNoCopy() {
        ZipExtraField[] zipExtraFieldArr = this.extraFields;
        if (zipExtraFieldArr == null) {
            return getUnparseableOnly();
        }
        return this.unparseableExtra != null ? getMergedFields() : zipExtraFieldArr;
    }

    private ZipExtraField[] getMergedFields() {
        ZipExtraField[] zipExtraFieldArr = this.extraFields;
        ZipExtraField[] zipExtraFields = (ZipExtraField[]) Arrays.copyOf(zipExtraFieldArr, zipExtraFieldArr.length + 1);
        zipExtraFields[this.extraFields.length] = this.unparseableExtra;
        return zipExtraFields;
    }

    private ZipExtraField[] getUnparseableOnly() {
        UnparseableExtraFieldData unparseableExtraFieldData = this.unparseableExtra;
        if (unparseableExtraFieldData == null) {
            return noExtraFields;
        }
        return new ZipExtraField[]{unparseableExtraFieldData};
    }

    private ZipExtraField[] getAllExtraFields() {
        ZipExtraField[] allExtraFieldsNoCopy = getAllExtraFieldsNoCopy();
        return allExtraFieldsNoCopy == this.extraFields ? (ZipExtraField[]) Arrays.copyOf(allExtraFieldsNoCopy, allExtraFieldsNoCopy.length) : allExtraFieldsNoCopy;
    }

    private ZipExtraField findUnparseable(List<ZipExtraField> fs) {
        for (ZipExtraField f : fs) {
            if (f instanceof UnparseableExtraFieldData) {
                return f;
            }
        }
        return null;
    }

    private ZipExtraField findMatching(ZipShort headerId, List<ZipExtraField> fs) {
        for (ZipExtraField f : fs) {
            if (headerId.equals(f.getHeaderId())) {
                return f;
            }
        }
        return null;
    }

    public void addExtraField(ZipExtraField ze) {
        if (ze instanceof UnparseableExtraFieldData) {
            this.unparseableExtra = (UnparseableExtraFieldData) ze;
        } else if (this.extraFields == null) {
            this.extraFields = new ZipExtraField[]{ze};
        } else {
            if (getExtraField(ze.getHeaderId()) != null) {
                removeExtraField(ze.getHeaderId());
            }
            ZipExtraField[] zipExtraFieldArr = this.extraFields;
            ZipExtraField[] zipExtraFields = (ZipExtraField[]) Arrays.copyOf(zipExtraFieldArr, zipExtraFieldArr.length + 1);
            zipExtraFields[zipExtraFields.length - 1] = ze;
            this.extraFields = zipExtraFields;
        }
        setExtra();
    }

    public void addAsFirstExtraField(ZipExtraField ze) {
        if (ze instanceof UnparseableExtraFieldData) {
            this.unparseableExtra = (UnparseableExtraFieldData) ze;
        } else {
            if (getExtraField(ze.getHeaderId()) != null) {
                removeExtraField(ze.getHeaderId());
            }
            ZipExtraField[] copy = this.extraFields;
            ZipExtraField[] zipExtraFieldArr = this.extraFields;
            this.extraFields = new ZipExtraField[(zipExtraFieldArr != null ? zipExtraFieldArr.length + 1 : 1)];
            ZipExtraField[] zipExtraFieldArr2 = this.extraFields;
            zipExtraFieldArr2[0] = ze;
            if (copy != null) {
                System.arraycopy(copy, 0, zipExtraFieldArr2, 1, zipExtraFieldArr2.length - 1);
            }
        }
        setExtra();
    }

    public void removeExtraField(ZipShort type) {
        if (this.extraFields != null) {
            List<ZipExtraField> newResult = new ArrayList<>();
            for (ZipExtraField extraField : this.extraFields) {
                if (!type.equals(extraField.getHeaderId())) {
                    newResult.add(extraField);
                }
            }
            if (this.extraFields.length != newResult.size()) {
                this.extraFields = (ZipExtraField[]) newResult.toArray(noExtraFields);
                setExtra();
                return;
            }
            throw new NoSuchElementException();
        }
        throw new NoSuchElementException();
    }

    public void removeUnparseableExtraFieldData() {
        if (this.unparseableExtra != null) {
            this.unparseableExtra = null;
            setExtra();
            return;
        }
        throw new NoSuchElementException();
    }

    public ZipExtraField getExtraField(ZipShort type) {
        ZipExtraField[] zipExtraFieldArr = this.extraFields;
        if (zipExtraFieldArr == null) {
            return null;
        }
        for (ZipExtraField extraField : zipExtraFieldArr) {
            if (type.equals(extraField.getHeaderId())) {
                return extraField;
            }
        }
        return null;
    }

    public UnparseableExtraFieldData getUnparseableExtraFieldData() {
        return this.unparseableExtra;
    }

    public void setExtra(byte[] extra) throws RuntimeException {
        try {
            mergeExtraFields(ExtraFieldUtils.parse(extra, true, (ExtraFieldParsingBehavior) ExtraFieldParsingMode.BEST_EFFORT), true);
        } catch (ZipException e) {
            throw new RuntimeException("Error parsing extra fields for entry: " + getName() + " - " + e.getMessage(), e);
        }
    }

    /* access modifiers changed from: protected */
    public void setExtra() {
        super.setExtra(ExtraFieldUtils.mergeLocalFileDataData(getAllExtraFieldsNoCopy()));
    }

    public void setCentralDirectoryExtra(byte[] b) {
        try {
            mergeExtraFields(ExtraFieldUtils.parse(b, false, (ExtraFieldParsingBehavior) ExtraFieldParsingMode.BEST_EFFORT), false);
        } catch (ZipException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public byte[] getLocalFileDataExtra() {
        byte[] extra = getExtra();
        return extra != null ? extra : EMPTY;
    }

    public byte[] getCentralDirectoryExtra() {
        return ExtraFieldUtils.mergeCentralDirectoryData(getAllExtraFieldsNoCopy());
    }

    public String getName() {
        String str = this.name;
        return str == null ? super.getName() : str;
    }

    public boolean isDirectory() {
        String n = getName();
        return n != null && n.endsWith("/");
    }

    /* access modifiers changed from: protected */
    public void setName(String name2) {
        if (name2 != null && getPlatform() == 0 && !name2.contains("/")) {
            name2 = name2.replace('\\', '/');
        }
        this.name = name2;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size2) {
        if (size2 >= 0) {
            this.size = size2;
            return;
        }
        throw new IllegalArgumentException("Invalid entry size");
    }

    /* access modifiers changed from: protected */
    public void setName(String name2, byte[] rawName2) {
        setName(name2);
        this.rawName = rawName2;
    }

    public byte[] getRawName() {
        byte[] bArr = this.rawName;
        if (bArr != null) {
            return Arrays.copyOf(bArr, bArr.length);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public long getLocalHeaderOffset() {
        return this.localHeaderOffset;
    }

    /* access modifiers changed from: protected */
    public void setLocalHeaderOffset(long localHeaderOffset2) {
        this.localHeaderOffset = localHeaderOffset2;
    }

    public long getDataOffset() {
        return this.dataOffset;
    }

    /* access modifiers changed from: protected */
    public void setDataOffset(long dataOffset2) {
        this.dataOffset = dataOffset2;
    }

    public boolean isStreamContiguous() {
        return this.isStreamContiguous;
    }

    /* access modifiers changed from: protected */
    public void setStreamContiguous(boolean isStreamContiguous2) {
        this.isStreamContiguous = isStreamContiguous2;
    }

    public int hashCode() {
        String n = getName();
        return (n == null ? "" : n).hashCode();
    }

    public GeneralPurposeBit getGeneralPurposeBit() {
        return this.gpb;
    }

    public void setGeneralPurposeBit(GeneralPurposeBit b) {
        this.gpb = b;
    }

    private void mergeExtraFields(ZipExtraField[] f, boolean local) {
        ZipExtraField existing;
        byte[] b;
        if (this.extraFields == null) {
            setExtraFields(f);
            return;
        }
        for (ZipExtraField element : f) {
            if (element instanceof UnparseableExtraFieldData) {
                existing = this.unparseableExtra;
            } else {
                existing = getExtraField(element.getHeaderId());
            }
            if (existing == null) {
                addExtraField(element);
            } else {
                if (local) {
                    b = element.getLocalFileDataData();
                } else {
                    b = element.getCentralDirectoryData();
                }
                if (local) {
                    try {
                        existing.parseFromLocalFileData(b, 0, b.length);
                    } catch (ZipException e) {
                        UnrecognizedExtraField u = new UnrecognizedExtraField();
                        u.setHeaderId(existing.getHeaderId());
                        if (local) {
                            u.setLocalFileDataData(b);
                            u.setCentralDirectoryData(existing.getCentralDirectoryData());
                        } else {
                            u.setLocalFileDataData(existing.getLocalFileDataData());
                            u.setCentralDirectoryData(b);
                        }
                        removeExtraField(existing.getHeaderId());
                        addExtraField(u);
                    }
                } else {
                    existing.parseFromCentralDirectoryData(b, 0, b.length);
                }
            }
        }
        setExtra();
    }

    public Date getLastModifiedDate() {
        return new Date(getTime());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ZipArchiveEntry other = (ZipArchiveEntry) obj;
        String myName = getName();
        String otherName = other.getName();
        if (myName == null) {
            if (otherName != null) {
                return false;
            }
        } else if (!myName.equals(otherName)) {
            return false;
        }
        String myComment = getComment();
        String otherComment = other.getComment();
        if (myComment == null) {
            myComment = "";
        }
        if (otherComment == null) {
            otherComment = "";
        }
        if (getTime() == other.getTime() && myComment.equals(otherComment) && getInternalAttributes() == other.getInternalAttributes() && getPlatform() == other.getPlatform() && getExternalAttributes() == other.getExternalAttributes() && getMethod() == other.getMethod() && getSize() == other.getSize() && getCrc() == other.getCrc() && getCompressedSize() == other.getCompressedSize() && Arrays.equals(getCentralDirectoryExtra(), other.getCentralDirectoryExtra()) && Arrays.equals(getLocalFileDataExtra(), other.getLocalFileDataExtra()) && this.localHeaderOffset == other.localHeaderOffset && this.dataOffset == other.dataOffset && this.gpb.equals(other.gpb)) {
            return true;
        }
        return false;
    }

    public void setVersionMadeBy(int versionMadeBy2) {
        this.versionMadeBy = versionMadeBy2;
    }

    public void setVersionRequired(int versionRequired2) {
        this.versionRequired = versionRequired2;
    }

    public int getVersionRequired() {
        return this.versionRequired;
    }

    public int getVersionMadeBy() {
        return this.versionMadeBy;
    }

    public int getRawFlag() {
        return this.rawFlag;
    }

    public void setRawFlag(int rawFlag2) {
        this.rawFlag = rawFlag2;
    }

    public NameSource getNameSource() {
        return this.nameSource;
    }

    public void setNameSource(NameSource nameSource2) {
        this.nameSource = nameSource2;
    }

    public CommentSource getCommentSource() {
        return this.commentSource;
    }

    public void setCommentSource(CommentSource commentSource2) {
        this.commentSource = commentSource2;
    }

    public enum ExtraFieldParsingMode implements ExtraFieldParsingBehavior {
        BEST_EFFORT(ExtraFieldUtils.UnparseableExtraField.READ) {
            public ZipExtraField fill(ZipExtraField field, byte[] data, int off, int len, boolean local) {
                return ExtraFieldParsingMode.fillAndMakeUnrecognizedOnError(field, data, off, len, local);
            }
        },
        STRICT_FOR_KNOW_EXTRA_FIELDS(ExtraFieldUtils.UnparseableExtraField.READ),
        ONLY_PARSEABLE_LENIENT(ExtraFieldUtils.UnparseableExtraField.SKIP) {
            public ZipExtraField fill(ZipExtraField field, byte[] data, int off, int len, boolean local) {
                return ExtraFieldParsingMode.fillAndMakeUnrecognizedOnError(field, data, off, len, local);
            }
        },
        ONLY_PARSEABLE_STRICT(ExtraFieldUtils.UnparseableExtraField.SKIP),
        DRACONIC(ExtraFieldUtils.UnparseableExtraField.THROW);
        
        private final ExtraFieldUtils.UnparseableExtraField onUnparseableData;

        private ExtraFieldParsingMode(ExtraFieldUtils.UnparseableExtraField onUnparseableData2) {
            this.onUnparseableData = onUnparseableData2;
        }

        public ZipExtraField onUnparseableExtraField(byte[] data, int off, int len, boolean local, int claimedLength) throws ZipException {
            return this.onUnparseableData.onUnparseableExtraField(data, off, len, local, claimedLength);
        }

        public ZipExtraField createExtraField(ZipShort headerId) throws ZipException, InstantiationException, IllegalAccessException {
            return ExtraFieldUtils.createExtraField(headerId);
        }

        public ZipExtraField fill(ZipExtraField field, byte[] data, int off, int len, boolean local) throws ZipException {
            return ExtraFieldUtils.fillExtraField(field, data, off, len, local);
        }

        /* access modifiers changed from: private */
        public static ZipExtraField fillAndMakeUnrecognizedOnError(ZipExtraField field, byte[] data, int off, int len, boolean local) {
            try {
                return ExtraFieldUtils.fillExtraField(field, data, off, len, local);
            } catch (ZipException e) {
                UnrecognizedExtraField u = new UnrecognizedExtraField();
                u.setHeaderId(field.getHeaderId());
                if (local) {
                    u.setLocalFileDataData(Arrays.copyOfRange(data, off, off + len));
                } else {
                    u.setCentralDirectoryData(Arrays.copyOfRange(data, off, off + len));
                }
                return u;
            }
        }
    }
}

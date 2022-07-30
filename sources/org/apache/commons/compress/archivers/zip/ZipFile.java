package org.apache.commons.compress.archivers.zip;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;

public class ZipFile implements Closeable {
    static final int BYTE_SHIFT = 8;
    private static final int CFD_LOCATOR_OFFSET = 16;
    private static final int CFH_LEN = 42;
    private static final long CFH_SIG = ZipLong.getValue(ZipArchiveOutputStream.CFH_SIG);
    private static final int HASH_SIZE = 509;
    private static final long LFH_OFFSET_FOR_FILENAME_LENGTH = 26;
    private static final int MAX_EOCD_SIZE = 65557;
    static final int MIN_EOCD_SIZE = 22;
    static final int NIBLET_MASK = 15;
    private static final byte[] ONE_ZERO_BYTE = new byte[1];
    private static final int POS_0 = 0;
    private static final int POS_1 = 1;
    private static final int POS_2 = 2;
    private static final int POS_3 = 3;
    private static final int ZIP64_EOCDL_LENGTH = 20;
    private static final int ZIP64_EOCDL_LOCATOR_OFFSET = 8;
    private static final int ZIP64_EOCD_CFD_LOCATOR_OFFSET = 48;
    /* access modifiers changed from: private */
    public final SeekableByteChannel archive;
    private final String archiveName;
    private final ByteBuffer cfhBbuf;
    private final byte[] cfhBuf;
    private volatile boolean closed;
    private final ByteBuffer dwordBbuf;
    private final byte[] dwordBuf;
    private final String encoding;
    private final List<ZipArchiveEntry> entries;
    private final Map<String, LinkedList<ZipArchiveEntry>> nameMap;
    private final Comparator<ZipArchiveEntry> offsetComparator;
    private final byte[] shortBuf;
    private final boolean useUnicodeExtraFields;
    private final ByteBuffer wordBbuf;
    private final byte[] wordBuf;
    private final ZipEncoding zipEncoding;

    public ZipFile(File f) throws IOException {
        this(f, "UTF8");
    }

    public ZipFile(String name) throws IOException {
        this(new File(name), "UTF8");
    }

    public ZipFile(String name, String encoding2) throws IOException {
        this(new File(name), encoding2, true);
    }

    public ZipFile(File f, String encoding2) throws IOException {
        this(f, encoding2, true);
    }

    public ZipFile(File f, String encoding2, boolean useUnicodeExtraFields2) throws IOException {
        this(f, encoding2, useUnicodeExtraFields2, false);
    }

    public ZipFile(File f, String encoding2, boolean useUnicodeExtraFields2, boolean ignoreLocalFileHeader) throws IOException {
        this(Files.newByteChannel(f.toPath(), EnumSet.of(StandardOpenOption.READ), new FileAttribute[0]), f.getAbsolutePath(), encoding2, useUnicodeExtraFields2, true, ignoreLocalFileHeader);
    }

    public ZipFile(SeekableByteChannel channel) throws IOException {
        this(channel, "unknown archive", "UTF8", true);
    }

    public ZipFile(SeekableByteChannel channel, String encoding2) throws IOException {
        this(channel, "unknown archive", encoding2, true);
    }

    public ZipFile(SeekableByteChannel channel, String archiveName2, String encoding2, boolean useUnicodeExtraFields2) throws IOException {
        this(channel, archiveName2, encoding2, useUnicodeExtraFields2, false, false);
    }

    public ZipFile(SeekableByteChannel channel, String archiveName2, String encoding2, boolean useUnicodeExtraFields2, boolean ignoreLocalFileHeader) throws IOException {
        this(channel, archiveName2, encoding2, useUnicodeExtraFields2, false, ignoreLocalFileHeader);
    }

    private ZipFile(SeekableByteChannel channel, String archiveName2, String encoding2, boolean useUnicodeExtraFields2, boolean closeOnError, boolean ignoreLocalFileHeader) throws IOException {
        this.entries = new LinkedList();
        this.nameMap = new HashMap(HASH_SIZE);
        boolean z = true;
        this.closed = z;
        this.dwordBuf = new byte[8];
        this.wordBuf = new byte[4];
        this.cfhBuf = new byte[42];
        this.shortBuf = new byte[2];
        this.dwordBbuf = ByteBuffer.wrap(this.dwordBuf);
        this.wordBbuf = ByteBuffer.wrap(this.wordBuf);
        this.cfhBbuf = ByteBuffer.wrap(this.cfhBuf);
        this.offsetComparator = new Comparator<ZipArchiveEntry>() {
            public int compare(ZipArchiveEntry e1, ZipArchiveEntry e2) {
                if (e1 == e2) {
                    return 0;
                }
                Entry ent2 = null;
                Entry ent1 = e1 instanceof Entry ? (Entry) e1 : null;
                if (e2 instanceof Entry) {
                    ent2 = (Entry) e2;
                }
                if (ent1 == null) {
                    return 1;
                }
                if (ent2 == null) {
                    return -1;
                }
                long val = ent1.getLocalHeaderOffset() - ent2.getLocalHeaderOffset();
                if (val == 0) {
                    return 0;
                }
                return val < 0 ? -1 : 1;
            }
        };
        this.archiveName = archiveName2;
        this.encoding = encoding2;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding2);
        this.useUnicodeExtraFields = useUnicodeExtraFields2;
        this.archive = channel;
        boolean success = false;
        try {
            Map<ZipArchiveEntry, NameAndComment> entriesWithoutUTF8Flag = populateFromCentralDirectory();
            if (!ignoreLocalFileHeader) {
                resolveLocalFileHeaderData(entriesWithoutUTF8Flag);
            }
            fillNameMap();
            success = true;
        } finally {
            this.closed = success ? false : z;
            if (!success && closeOnError) {
                IOUtils.closeQuietly(this.archive);
            }
        }
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void close() throws IOException {
        this.closed = true;
        this.archive.close();
    }

    public static void closeQuietly(ZipFile zipfile) {
        IOUtils.closeQuietly(zipfile);
    }

    public Enumeration<ZipArchiveEntry> getEntries() {
        return Collections.enumeration(this.entries);
    }

    public Enumeration<ZipArchiveEntry> getEntriesInPhysicalOrder() {
        List<ZipArchiveEntry> list = this.entries;
        ZipArchiveEntry[] allEntries = (ZipArchiveEntry[]) list.toArray(new ZipArchiveEntry[list.size()]);
        Arrays.sort(allEntries, this.offsetComparator);
        return Collections.enumeration(Arrays.asList(allEntries));
    }

    public ZipArchiveEntry getEntry(String name) {
        LinkedList<ZipArchiveEntry> entriesOfThatName = this.nameMap.get(name);
        if (entriesOfThatName != null) {
            return entriesOfThatName.getFirst();
        }
        return null;
    }

    public Iterable<ZipArchiveEntry> getEntries(String name) {
        List<ZipArchiveEntry> entriesOfThatName = this.nameMap.get(name);
        if (entriesOfThatName != null) {
            return entriesOfThatName;
        }
        return Collections.emptyList();
    }

    /* JADX WARNING: type inference failed for: r1v6, types: [java.lang.Object[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Iterable<org.apache.commons.compress.archivers.zip.ZipArchiveEntry> getEntriesInPhysicalOrder(java.lang.String r3) {
        /*
            r2 = this;
            r0 = 0
            org.apache.commons.compress.archivers.zip.ZipArchiveEntry[] r0 = new org.apache.commons.compress.archivers.zip.ZipArchiveEntry[r0]
            java.util.Map<java.lang.String, java.util.LinkedList<org.apache.commons.compress.archivers.zip.ZipArchiveEntry>> r1 = r2.nameMap
            boolean r1 = r1.containsKey(r3)
            if (r1 == 0) goto L_0x001f
            java.util.Map<java.lang.String, java.util.LinkedList<org.apache.commons.compress.archivers.zip.ZipArchiveEntry>> r1 = r2.nameMap
            java.lang.Object r1 = r1.get(r3)
            java.util.LinkedList r1 = (java.util.LinkedList) r1
            java.lang.Object[] r1 = r1.toArray(r0)
            r0 = r1
            org.apache.commons.compress.archivers.zip.ZipArchiveEntry[] r0 = (org.apache.commons.compress.archivers.zip.ZipArchiveEntry[]) r0
            java.util.Comparator<org.apache.commons.compress.archivers.zip.ZipArchiveEntry> r1 = r2.offsetComparator
            java.util.Arrays.sort(r0, r1)
        L_0x001f:
            java.util.List r1 = java.util.Arrays.asList(r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.zip.ZipFile.getEntriesInPhysicalOrder(java.lang.String):java.lang.Iterable");
    }

    public boolean canReadEntryData(ZipArchiveEntry ze) {
        return ZipUtil.canHandleEntryData(ze);
    }

    public InputStream getRawInputStream(ZipArchiveEntry ze) {
        if (!(ze instanceof Entry)) {
            return null;
        }
        long start = ze.getDataOffset();
        if (start == -1) {
            return null;
        }
        return createBoundedInputStream(start, ze.getCompressedSize());
    }

    public void copyRawEntries(ZipArchiveOutputStream target, ZipArchiveEntryPredicate predicate) throws IOException {
        Enumeration<ZipArchiveEntry> src = getEntriesInPhysicalOrder();
        while (src.hasMoreElements()) {
            ZipArchiveEntry entry = src.nextElement();
            if (predicate.test(entry)) {
                target.addRawArchiveEntry(entry, getRawInputStream(entry));
            }
        }
    }

    public InputStream getInputStream(ZipArchiveEntry ze) throws IOException {
        if (!(ze instanceof Entry)) {
            return null;
        }
        ZipUtil.checkRequestedFeatures(ze);
        InputStream is = new BufferedInputStream(createBoundedInputStream(getDataOffset(ze), ze.getCompressedSize()));
        switch (ZipMethod.getMethodByCode(ze.getMethod())) {
            case STORED:
                return new StoredStatisticsStream(is);
            case UNSHRINKING:
                return new UnshrinkingInputStream(is);
            case IMPLODING:
                return new ExplodingInputStream(ze.getGeneralPurposeBit().getSlidingDictionarySize(), ze.getGeneralPurposeBit().getNumberOfShannonFanoTrees(), is);
            case DEFLATED:
                final Inflater inflater = new Inflater(true);
                return new InflaterInputStreamWithStatistics(new SequenceInputStream(is, new ByteArrayInputStream(ONE_ZERO_BYTE)), inflater) {
                    public void close() throws IOException {
                        try {
                            super.close();
                        } finally {
                            inflater.end();
                        }
                    }
                };
            case BZIP2:
                return new BZip2CompressorInputStream(is);
            case ENHANCED_DEFLATED:
                return new Deflate64CompressorInputStream(is);
            default:
                throw new UnsupportedZipFeatureException(ZipMethod.getMethodByCode(ze.getMethod()), ze);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001d, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0021, code lost:
        if (r1 != null) goto L_0x0023;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0023, code lost:
        if (r0 != null) goto L_0x0025;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0029, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002a, code lost:
        r0.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x002e, code lost:
        r1.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getUnixSymlink(org.apache.commons.compress.archivers.zip.ZipArchiveEntry r5) throws java.io.IOException {
        /*
            r4 = this;
            r0 = 0
            if (r5 == 0) goto L_0x0032
            boolean r1 = r5.isUnixSymlink()
            if (r1 == 0) goto L_0x0032
            java.io.InputStream r1 = r4.getInputStream(r5)
            org.apache.commons.compress.archivers.zip.ZipEncoding r2 = r4.zipEncoding     // Catch:{ Throwable -> 0x001f }
            byte[] r3 = org.apache.commons.compress.utils.IOUtils.toByteArray(r1)     // Catch:{ Throwable -> 0x001f }
            java.lang.String r0 = r2.decode(r3)     // Catch:{ Throwable -> 0x001f }
            if (r1 == 0) goto L_0x001c
            r1.close()
        L_0x001c:
            return r0
        L_0x001d:
            r2 = move-exception
            goto L_0x0021
        L_0x001f:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x001d }
        L_0x0021:
            if (r1 == 0) goto L_0x0031
            if (r0 == 0) goto L_0x002e
            r1.close()     // Catch:{ Throwable -> 0x0029 }
            goto L_0x0031
        L_0x0029:
            r3 = move-exception
            r0.addSuppressed(r3)
            goto L_0x0031
        L_0x002e:
            r1.close()
        L_0x0031:
            throw r2
        L_0x0032:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.zip.ZipFile.getUnixSymlink(org.apache.commons.compress.archivers.zip.ZipArchiveEntry):java.lang.String");
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            if (!this.closed) {
                PrintStream printStream = System.err;
                printStream.println("Cleaning up unclosed ZipFile for archive " + this.archiveName);
                close();
            }
        } finally {
            super.finalize();
        }
    }

    private Map<ZipArchiveEntry, NameAndComment> populateFromCentralDirectory() throws IOException {
        HashMap<ZipArchiveEntry, NameAndComment> noUTF8Flag = new HashMap<>();
        positionAtCentralDirectory();
        this.wordBbuf.rewind();
        IOUtils.readFully((ReadableByteChannel) this.archive, this.wordBbuf);
        long sig = ZipLong.getValue(this.wordBuf);
        if (sig == CFH_SIG || !startsWithLocalFileHeader()) {
            while (sig == CFH_SIG) {
                readCentralDirectoryEntry(noUTF8Flag);
                this.wordBbuf.rewind();
                IOUtils.readFully((ReadableByteChannel) this.archive, this.wordBbuf);
                sig = ZipLong.getValue(this.wordBuf);
            }
            return noUTF8Flag;
        }
        throw new IOException("Central directory is empty, can't expand corrupt archive.");
    }

    private void readCentralDirectoryEntry(Map<ZipArchiveEntry, NameAndComment> noUTF8Flag) throws IOException {
        this.cfhBbuf.rewind();
        IOUtils.readFully((ReadableByteChannel) this.archive, this.cfhBbuf);
        Entry ze = new Entry();
        int versionMadeBy = ZipShort.getValue(this.cfhBuf, 0);
        int off = 0 + 2;
        ze.setVersionMadeBy(versionMadeBy);
        ze.setPlatform((versionMadeBy >> 8) & 15);
        ze.setVersionRequired(ZipShort.getValue(this.cfhBuf, off));
        int off2 = off + 2;
        GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.cfhBuf, off2);
        boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
        ZipEncoding entryEncoding = hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
        if (hasUTF8Flag) {
            ze.setNameSource(ZipArchiveEntry.NameSource.NAME_WITH_EFS_FLAG);
        }
        ze.setGeneralPurposeBit(gpFlag);
        ze.setRawFlag(ZipShort.getValue(this.cfhBuf, off2));
        int off3 = off2 + 2;
        ze.setMethod(ZipShort.getValue(this.cfhBuf, off3));
        int off4 = off3 + 2;
        ze.setTime(ZipUtil.dosToJavaTime(ZipLong.getValue(this.cfhBuf, off4)));
        int off5 = off4 + 4;
        ze.setCrc(ZipLong.getValue(this.cfhBuf, off5));
        int off6 = off5 + 4;
        ze.setCompressedSize(ZipLong.getValue(this.cfhBuf, off6));
        int off7 = off6 + 4;
        ze.setSize(ZipLong.getValue(this.cfhBuf, off7));
        int off8 = off7 + 4;
        int fileNameLen = ZipShort.getValue(this.cfhBuf, off8);
        int off9 = off8 + 2;
        int extraLen = ZipShort.getValue(this.cfhBuf, off9);
        int off10 = off9 + 2;
        int commentLen = ZipShort.getValue(this.cfhBuf, off10);
        int off11 = off10 + 2;
        int diskStart = ZipShort.getValue(this.cfhBuf, off11);
        int off12 = off11 + 2;
        ze.setInternalAttributes(ZipShort.getValue(this.cfhBuf, off12));
        int off13 = off12 + 2;
        ze.setExternalAttributes(ZipLong.getValue(this.cfhBuf, off13));
        int off14 = off13 + 4;
        byte[] fileName = new byte[fileNameLen];
        IOUtils.readFully((ReadableByteChannel) this.archive, ByteBuffer.wrap(fileName));
        ze.setName(entryEncoding.decode(fileName), fileName);
        ze.setLocalHeaderOffset(ZipLong.getValue(this.cfhBuf, off14));
        this.entries.add(ze);
        byte[] cdExtraData = new byte[extraLen];
        int i = off14;
        IOUtils.readFully((ReadableByteChannel) this.archive, ByteBuffer.wrap(cdExtraData));
        ze.setCentralDirectoryExtra(cdExtraData);
        setSizesAndOffsetFromZip64Extra(ze, diskStart);
        byte[] comment = new byte[commentLen];
        int i2 = versionMadeBy;
        IOUtils.readFully((ReadableByteChannel) this.archive, ByteBuffer.wrap(comment));
        ze.setComment(entryEncoding.decode(comment));
        if (hasUTF8Flag || !this.useUnicodeExtraFields) {
            Map<ZipArchiveEntry, NameAndComment> map = noUTF8Flag;
        } else {
            noUTF8Flag.put(ze, new NameAndComment(fileName, comment));
        }
        ze.setStreamContiguous(true);
    }

    private void setSizesAndOffsetFromZip64Extra(ZipArchiveEntry ze, int diskStart) throws IOException {
        Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField) ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        if (z64 != null) {
            boolean z = true;
            boolean hasUncompressedSize = ze.getSize() == 4294967295L;
            boolean hasCompressedSize = ze.getCompressedSize() == 4294967295L;
            boolean hasRelativeHeaderOffset = ze.getLocalHeaderOffset() == 4294967295L;
            if (diskStart != 65535) {
                z = false;
            }
            z64.reparseCentralDirectoryData(hasUncompressedSize, hasCompressedSize, hasRelativeHeaderOffset, z);
            if (hasUncompressedSize) {
                ze.setSize(z64.getSize().getLongValue());
            } else if (hasCompressedSize) {
                z64.setSize(new ZipEightByteInteger(ze.getSize()));
            }
            if (hasCompressedSize) {
                ze.setCompressedSize(z64.getCompressedSize().getLongValue());
            } else if (hasUncompressedSize) {
                z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
            }
            if (hasRelativeHeaderOffset) {
                ze.setLocalHeaderOffset(z64.getRelativeHeaderOffset().getLongValue());
            }
        }
    }

    private void positionAtCentralDirectory() throws IOException {
        positionAtEndOfCentralDirectoryRecord();
        boolean found = false;
        boolean searchedForZip64EOCD = this.archive.position() > 20;
        if (searchedForZip64EOCD) {
            SeekableByteChannel seekableByteChannel = this.archive;
            seekableByteChannel.position(seekableByteChannel.position() - 20);
            this.wordBbuf.rewind();
            IOUtils.readFully((ReadableByteChannel) this.archive, this.wordBbuf);
            found = Arrays.equals(ZipArchiveOutputStream.ZIP64_EOCD_LOC_SIG, this.wordBuf);
        }
        if (!found) {
            if (searchedForZip64EOCD) {
                skipBytes(16);
            }
            positionAtCentralDirectory32();
            return;
        }
        positionAtCentralDirectory64();
    }

    private void positionAtCentralDirectory64() throws IOException {
        skipBytes(4);
        this.dwordBbuf.rewind();
        IOUtils.readFully((ReadableByteChannel) this.archive, this.dwordBbuf);
        this.archive.position(ZipEightByteInteger.getLongValue(this.dwordBuf));
        this.wordBbuf.rewind();
        IOUtils.readFully((ReadableByteChannel) this.archive, this.wordBbuf);
        if (Arrays.equals(this.wordBuf, ZipArchiveOutputStream.ZIP64_EOCD_SIG)) {
            skipBytes(44);
            this.dwordBbuf.rewind();
            IOUtils.readFully((ReadableByteChannel) this.archive, this.dwordBbuf);
            this.archive.position(ZipEightByteInteger.getLongValue(this.dwordBuf));
            return;
        }
        throw new ZipException("Archive's ZIP64 end of central directory locator is corrupt.");
    }

    private void positionAtCentralDirectory32() throws IOException {
        skipBytes(16);
        this.wordBbuf.rewind();
        IOUtils.readFully((ReadableByteChannel) this.archive, this.wordBbuf);
        this.archive.position(ZipLong.getValue(this.wordBuf));
    }

    private void positionAtEndOfCentralDirectoryRecord() throws IOException {
        if (!tryToLocateSignature(22, 65557, ZipArchiveOutputStream.EOCD_SIG)) {
            throw new ZipException("Archive is not a ZIP archive");
        }
    }

    private boolean tryToLocateSignature(long minDistanceFromEnd, long maxDistanceFromEnd, byte[] sig) throws IOException {
        boolean found = false;
        long off = this.archive.size() - minDistanceFromEnd;
        long stopSearching = Math.max(0, this.archive.size() - maxDistanceFromEnd);
        if (off >= 0) {
            while (true) {
                if (off < stopSearching) {
                    break;
                }
                this.archive.position(off);
                try {
                    this.wordBbuf.rewind();
                    IOUtils.readFully((ReadableByteChannel) this.archive, this.wordBbuf);
                    this.wordBbuf.flip();
                    if (this.wordBbuf.get() == sig[0] && this.wordBbuf.get() == sig[1] && this.wordBbuf.get() == sig[2] && this.wordBbuf.get() == sig[3]) {
                        found = true;
                        break;
                    }
                    off--;
                } catch (EOFException e) {
                }
            }
        }
        if (found) {
            this.archive.position(off);
        }
        return found;
    }

    private void skipBytes(int count) throws IOException {
        long newPosition = ((long) count) + this.archive.position();
        if (newPosition <= this.archive.size()) {
            this.archive.position(newPosition);
            return;
        }
        throw new EOFException();
    }

    private void resolveLocalFileHeaderData(Map<ZipArchiveEntry, NameAndComment> entriesWithoutUTF8Flag) throws IOException {
        Iterator<ZipArchiveEntry> it = this.entries.iterator();
        while (it.hasNext()) {
            Entry ze = (Entry) it.next();
            int[] lens = setDataOffset(ze);
            int fileNameLen = lens[0];
            int extraFieldLen = lens[1];
            skipBytes(fileNameLen);
            byte[] localExtraData = new byte[extraFieldLen];
            IOUtils.readFully((ReadableByteChannel) this.archive, ByteBuffer.wrap(localExtraData));
            ze.setExtra(localExtraData);
            if (entriesWithoutUTF8Flag.containsKey(ze)) {
                NameAndComment nc = entriesWithoutUTF8Flag.get(ze);
                ZipUtil.setNameAndCommentFromExtraFields(ze, nc.name, nc.comment);
            }
        }
    }

    private void fillNameMap() {
        for (ZipArchiveEntry ze : this.entries) {
            String name = ze.getName();
            LinkedList<ZipArchiveEntry> entriesOfThatName = this.nameMap.get(name);
            if (entriesOfThatName == null) {
                entriesOfThatName = new LinkedList<>();
                this.nameMap.put(name, entriesOfThatName);
            }
            entriesOfThatName.addLast(ze);
        }
    }

    private int[] setDataOffset(ZipArchiveEntry ze) throws IOException {
        long offset = ze.getLocalHeaderOffset();
        this.archive.position(offset + LFH_OFFSET_FOR_FILENAME_LENGTH);
        this.wordBbuf.rewind();
        IOUtils.readFully((ReadableByteChannel) this.archive, this.wordBbuf);
        this.wordBbuf.flip();
        this.wordBbuf.get(this.shortBuf);
        int fileNameLen = ZipShort.getValue(this.shortBuf);
        this.wordBbuf.get(this.shortBuf);
        int extraFieldLen = ZipShort.getValue(this.shortBuf);
        ze.setDataOffset(LFH_OFFSET_FOR_FILENAME_LENGTH + offset + 2 + 2 + ((long) fileNameLen) + ((long) extraFieldLen));
        return new int[]{fileNameLen, extraFieldLen};
    }

    private long getDataOffset(ZipArchiveEntry ze) throws IOException {
        long s = ze.getDataOffset();
        if (s != -1) {
            return s;
        }
        setDataOffset(ze);
        return ze.getDataOffset();
    }

    private boolean startsWithLocalFileHeader() throws IOException {
        this.archive.position(0);
        this.wordBbuf.rewind();
        IOUtils.readFully((ReadableByteChannel) this.archive, this.wordBbuf);
        return Arrays.equals(this.wordBuf, ZipArchiveOutputStream.LFH_SIG);
    }

    private BoundedInputStream createBoundedInputStream(long start, long remaining) {
        return this.archive instanceof FileChannel ? new BoundedFileChannelInputStream(start, remaining) : new BoundedInputStream(start, remaining);
    }

    private class BoundedInputStream extends InputStream {
        private final long end;
        private long loc;
        private ByteBuffer singleByteBuffer;

        BoundedInputStream(long start, long remaining) {
            this.end = start + remaining;
            if (this.end >= start) {
                this.loc = start;
                return;
            }
            throw new IllegalArgumentException("Invalid length of stream at offset=" + start + ", length=" + remaining);
        }

        public synchronized int read() throws IOException {
            if (this.loc >= this.end) {
                return -1;
            }
            if (this.singleByteBuffer == null) {
                this.singleByteBuffer = ByteBuffer.allocate(1);
            } else {
                this.singleByteBuffer.rewind();
            }
            int read = read(this.loc, this.singleByteBuffer);
            if (read < 0) {
                return read;
            }
            this.loc++;
            return this.singleByteBuffer.get() & 255;
        }

        public synchronized int read(byte[] b, int off, int len) throws IOException {
            if (len <= 0) {
                return 0;
            }
            if (((long) len) > this.end - this.loc) {
                if (this.loc >= this.end) {
                    return -1;
                }
                len = (int) (this.end - this.loc);
            }
            int ret = read(this.loc, ByteBuffer.wrap(b, off, len));
            if (ret <= 0) {
                return ret;
            }
            this.loc += (long) ret;
            return ret;
        }

        /* access modifiers changed from: protected */
        public int read(long pos, ByteBuffer buf) throws IOException {
            int read;
            synchronized (ZipFile.this.archive) {
                ZipFile.this.archive.position(pos);
                read = ZipFile.this.archive.read(buf);
            }
            buf.flip();
            return read;
        }
    }

    private class BoundedFileChannelInputStream extends BoundedInputStream {
        private final FileChannel archive;

        BoundedFileChannelInputStream(long start, long remaining) {
            super(start, remaining);
            this.archive = (FileChannel) ZipFile.this.archive;
        }

        /* access modifiers changed from: protected */
        public int read(long pos, ByteBuffer buf) throws IOException {
            int read = this.archive.read(buf, pos);
            buf.flip();
            return read;
        }
    }

    private static final class NameAndComment {
        /* access modifiers changed from: private */
        public final byte[] comment;
        /* access modifiers changed from: private */
        public final byte[] name;

        private NameAndComment(byte[] name2, byte[] comment2) {
            this.name = name2;
            this.comment = comment2;
        }
    }

    private static class Entry extends ZipArchiveEntry {
        Entry() {
        }

        public int hashCode() {
            return (super.hashCode() * 3) + ((int) getLocalHeaderOffset()) + ((int) (getLocalHeaderOffset() >> 32));
        }

        public boolean equals(Object other) {
            if (!super.equals(other)) {
                return false;
            }
            Entry otherEntry = (Entry) other;
            if (getLocalHeaderOffset() == otherEntry.getLocalHeaderOffset() && super.getDataOffset() == otherEntry.getDataOffset()) {
                return true;
            }
            return false;
        }
    }

    private static class StoredStatisticsStream extends CountingInputStream implements InputStreamStatistics {
        StoredStatisticsStream(InputStream in) {
            super(in);
        }

        public long getCompressedCount() {
            return super.getBytesRead();
        }

        public long getUncompressedCount() {
            return getCompressedCount();
        }
    }
}

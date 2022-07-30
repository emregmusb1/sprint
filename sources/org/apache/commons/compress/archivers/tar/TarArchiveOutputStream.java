package org.apache.commons.compress.archivers.tar;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.commons.compress.utils.CountingOutputStream;
import org.apache.commons.compress.utils.FixedLengthBlockOutputStream;

public class TarArchiveOutputStream extends ArchiveOutputStream {
    private static final ZipEncoding ASCII = ZipEncodingHelper.getZipEncoding("ASCII");
    public static final int BIGNUMBER_ERROR = 0;
    public static final int BIGNUMBER_POSIX = 2;
    public static final int BIGNUMBER_STAR = 1;
    private static final int BLOCK_SIZE_UNSPECIFIED = -511;
    public static final int LONGFILE_ERROR = 0;
    public static final int LONGFILE_GNU = 2;
    public static final int LONGFILE_POSIX = 3;
    public static final int LONGFILE_TRUNCATE = 1;
    private static final int RECORD_SIZE = 512;
    private boolean addPaxHeadersForNonAsciiNames;
    private int bigNumberMode;
    private boolean closed;
    private final CountingOutputStream countingOut;
    private long currBytes;
    private String currName;
    private long currSize;
    final String encoding;
    private boolean finished;
    private boolean haveUnclosedEntry;
    private int longFileMode;
    private final FixedLengthBlockOutputStream out;
    private final byte[] recordBuf;
    private final int recordsPerBlock;
    private int recordsWritten;
    private final ZipEncoding zipEncoding;

    public TarArchiveOutputStream(OutputStream os) {
        this(os, (int) BLOCK_SIZE_UNSPECIFIED);
    }

    public TarArchiveOutputStream(OutputStream os, String encoding2) {
        this(os, (int) BLOCK_SIZE_UNSPECIFIED, encoding2);
    }

    public TarArchiveOutputStream(OutputStream os, int blockSize) {
        this(os, blockSize, (String) null);
    }

    @Deprecated
    public TarArchiveOutputStream(OutputStream os, int blockSize, int recordSize) {
        this(os, blockSize, recordSize, (String) null);
    }

    @Deprecated
    public TarArchiveOutputStream(OutputStream os, int blockSize, int recordSize, String encoding2) {
        this(os, blockSize, encoding2);
        if (recordSize != 512) {
            throw new IllegalArgumentException("Tar record size must always be 512 bytes. Attempt to set size of " + recordSize);
        }
    }

    public TarArchiveOutputStream(OutputStream os, int blockSize, String encoding2) {
        int realBlockSize;
        this.longFileMode = 0;
        this.bigNumberMode = 0;
        this.closed = false;
        this.haveUnclosedEntry = false;
        this.finished = false;
        this.addPaxHeadersForNonAsciiNames = false;
        if (BLOCK_SIZE_UNSPECIFIED == blockSize) {
            realBlockSize = 512;
        } else {
            realBlockSize = blockSize;
        }
        if (realBlockSize <= 0 || realBlockSize % 512 != 0) {
            throw new IllegalArgumentException("Block size must be a multiple of 512 bytes. Attempt to use set size of " + blockSize);
        }
        CountingOutputStream countingOutputStream = new CountingOutputStream(os);
        this.countingOut = countingOutputStream;
        this.out = new FixedLengthBlockOutputStream((OutputStream) countingOutputStream, 512);
        this.encoding = encoding2;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding2);
        this.recordBuf = new byte[512];
        this.recordsPerBlock = realBlockSize / 512;
    }

    public void setLongFileMode(int longFileMode2) {
        this.longFileMode = longFileMode2;
    }

    public void setBigNumberMode(int bigNumberMode2) {
        this.bigNumberMode = bigNumberMode2;
    }

    public void setAddPaxHeadersForNonAsciiNames(boolean b) {
        this.addPaxHeadersForNonAsciiNames = b;
    }

    @Deprecated
    public int getCount() {
        return (int) getBytesWritten();
    }

    public long getBytesWritten() {
        return this.countingOut.getBytesWritten();
    }

    public void finish() throws IOException {
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        } else if (!this.haveUnclosedEntry) {
            writeEOFRecord();
            writeEOFRecord();
            padAsNeeded();
            this.out.flush();
            this.finished = true;
        } else {
            throw new IOException("This archive contains unclosed entries.");
        }
    }

    public void close() throws IOException {
        try {
            if (!this.finished) {
                finish();
            }
        } finally {
            if (!this.closed) {
                this.out.close();
                this.closed = true;
            }
        }
    }

    @Deprecated
    public int getRecordSize() {
        return 512;
    }

    /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
        java.lang.NullPointerException
        */
    public void putArchiveEntry(org.apache.commons.compress.archivers.ArchiveEntry r18) throws java.io.IOException {
        /*
            r17 = this;
            r7 = r17
            boolean r0 = r7.finished
            if (r0 != 0) goto L_0x00fb
            r8 = r18
            org.apache.commons.compress.archivers.tar.TarArchiveEntry r8 = (org.apache.commons.compress.archivers.tar.TarArchiveEntry) r8
            boolean r0 = r8.isGlobalPaxHeader()
            r9 = 0
            r12 = 1
            if (r0 == 0) goto L_0x0045
            java.util.Map r0 = r8.getExtraPaxHeaders()
            byte[] r0 = r7.encodeExtendedPaxHeadersContents(r0)
            int r1 = r0.length
            long r1 = (long) r1
            r8.setSize(r1)
            byte[] r1 = r7.recordBuf
            org.apache.commons.compress.archivers.zip.ZipEncoding r2 = r7.zipEncoding
            int r3 = r7.bigNumberMode
            if (r3 != r12) goto L_0x002a
            r11 = 1
            goto L_0x002b
        L_0x002a:
            r11 = 0
        L_0x002b:
            r8.writeEntryHeader(r1, r2, r11)
            byte[] r1 = r7.recordBuf
            r7.writeRecord(r1)
            long r1 = r8.getSize()
            r7.currSize = r1
            r7.currBytes = r9
            r7.haveUnclosedEntry = r12
            r7.write(r0)
            r17.closeArchiveEntry()
            goto L_0x00fa
        L_0x0045:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r13 = r0
            java.lang.String r14 = r8.getName()
            r5 = 76
            java.lang.String r4 = "path"
            java.lang.String r6 = "file name"
            r0 = r17
            r1 = r8
            r2 = r14
            r3 = r13
            boolean r15 = r0.handleLongName(r1, r2, r3, r4, r5, r6)
            java.lang.String r6 = r8.getLinkName()
            if (r6 == 0) goto L_0x0080
            int r0 = r6.length()
            if (r0 <= 0) goto L_0x0080
            r5 = 75
            java.lang.String r4 = "linkpath"
            java.lang.String r16 = "link name"
            r0 = r17
            r1 = r8
            r2 = r6
            r3 = r13
            r11 = r6
            r6 = r16
            boolean r0 = r0.handleLongName(r1, r2, r3, r4, r5, r6)
            if (r0 == 0) goto L_0x0081
            r0 = 1
            goto L_0x0082
        L_0x0080:
            r11 = r6
        L_0x0081:
            r0 = 0
        L_0x0082:
            int r1 = r7.bigNumberMode
            r2 = 2
            if (r1 != r2) goto L_0x008b
            r7.addPaxHeadersForBigNumbers(r13, r8)
            goto L_0x0090
        L_0x008b:
            if (r1 == r12) goto L_0x0090
            r7.failForBigNumbers(r8)
        L_0x0090:
            boolean r1 = r7.addPaxHeadersForNonAsciiNames
            if (r1 == 0) goto L_0x00a3
            if (r15 != 0) goto L_0x00a3
            org.apache.commons.compress.archivers.zip.ZipEncoding r1 = ASCII
            boolean r1 = r1.canEncode(r14)
            if (r1 != 0) goto L_0x00a3
            java.lang.String r1 = "path"
            r13.put(r1, r14)
        L_0x00a3:
            boolean r1 = r7.addPaxHeadersForNonAsciiNames
            if (r1 == 0) goto L_0x00c2
            if (r0 != 0) goto L_0x00c2
            boolean r1 = r8.isLink()
            if (r1 != 0) goto L_0x00b5
            boolean r1 = r8.isSymbolicLink()
            if (r1 == 0) goto L_0x00c2
        L_0x00b5:
            org.apache.commons.compress.archivers.zip.ZipEncoding r1 = ASCII
            boolean r1 = r1.canEncode(r11)
            if (r1 != 0) goto L_0x00c2
            java.lang.String r1 = "linkpath"
            r13.put(r1, r11)
        L_0x00c2:
            java.util.Map r1 = r8.getExtraPaxHeaders()
            r13.putAll(r1)
            int r1 = r13.size()
            if (r1 <= 0) goto L_0x00d2
            r7.writePaxHeaders(r8, r14, r13)
        L_0x00d2:
            byte[] r1 = r7.recordBuf
            org.apache.commons.compress.archivers.zip.ZipEncoding r2 = r7.zipEncoding
            int r3 = r7.bigNumberMode
            if (r3 != r12) goto L_0x00dc
            r3 = 1
            goto L_0x00dd
        L_0x00dc:
            r3 = 0
        L_0x00dd:
            r8.writeEntryHeader(r1, r2, r3)
            byte[] r1 = r7.recordBuf
            r7.writeRecord(r1)
            r7.currBytes = r9
            boolean r1 = r8.isDirectory()
            if (r1 == 0) goto L_0x00f0
            r7.currSize = r9
            goto L_0x00f6
        L_0x00f0:
            long r1 = r8.getSize()
            r7.currSize = r1
        L_0x00f6:
            r7.currName = r14
            r7.haveUnclosedEntry = r12
        L_0x00fa:
            return
        L_0x00fb:
            java.io.IOException r0 = new java.io.IOException
            java.lang.String r1 = "Stream has already been finished"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.putArchiveEntry(org.apache.commons.compress.archivers.ArchiveEntry):void");
    }

    public void closeArchiveEntry() throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        } else if (this.haveUnclosedEntry) {
            this.out.flushBlock();
            long j = this.currBytes;
            long j2 = this.currSize;
            if (j >= j2) {
                this.recordsWritten = (int) (((long) this.recordsWritten) + (j2 / 512));
                if (0 != j2 % 512) {
                    this.recordsWritten++;
                }
                this.haveUnclosedEntry = false;
                return;
            }
            throw new IOException("Entry '" + this.currName + "' closed at '" + this.currBytes + "' before the '" + this.currSize + "' bytes specified in the header were written");
        } else {
            throw new IOException("No current entry to close");
        }
    }

    public void write(byte[] wBuf, int wOffset, int numToWrite) throws IOException {
        if (!this.haveUnclosedEntry) {
            throw new IllegalStateException("No current tar entry");
        } else if (this.currBytes + ((long) numToWrite) <= this.currSize) {
            this.out.write(wBuf, wOffset, numToWrite);
            this.currBytes += (long) numToWrite;
        } else {
            throw new IOException("Request to write '" + numToWrite + "' bytes exceeds size in header of '" + this.currSize + "' bytes for entry '" + this.currName + "'");
        }
    }

    /* access modifiers changed from: package-private */
    public void writePaxHeaders(TarArchiveEntry entry, String entryName, Map<String, String> headers) throws IOException {
        String name = "./PaxHeaders.X/" + stripTo7Bits(entryName);
        if (name.length() >= 100) {
            name = name.substring(0, 99);
        }
        TarArchiveEntry pex = new TarArchiveEntry(name, (byte) TarConstants.LF_PAX_EXTENDED_HEADER_LC);
        transferModTime(entry, pex);
        byte[] data = encodeExtendedPaxHeadersContents(headers);
        pex.setSize((long) data.length);
        putArchiveEntry(pex);
        write(data);
        closeArchiveEntry();
    }

    private byte[] encodeExtendedPaxHeadersContents(Map<String, String> headers) throws UnsupportedEncodingException {
        StringWriter w = new StringWriter();
        for (Map.Entry<String, String> h : headers.entrySet()) {
            String key = h.getKey();
            String value = h.getValue();
            int len = key.length() + value.length() + 3 + 2;
            String line = len + " " + key + "=" + value + "\n";
            int actualLength = line.getBytes(CharsetNames.UTF_8).length;
            while (len != actualLength) {
                len = actualLength;
                line = len + " " + key + "=" + value + "\n";
                actualLength = line.getBytes(CharsetNames.UTF_8).length;
            }
            w.write(line);
        }
        return w.toString().getBytes(CharsetNames.UTF_8);
    }

    private String stripTo7Bits(String name) {
        int length = name.length();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char stripped = (char) (name.charAt(i) & 127);
            if (shouldBeReplaced(stripped)) {
                result.append("_");
            } else {
                result.append(stripped);
            }
        }
        return result.toString();
    }

    private boolean shouldBeReplaced(char c) {
        return c == 0 || c == '/' || c == '\\';
    }

    private void writeEOFRecord() throws IOException {
        Arrays.fill(this.recordBuf, (byte) 0);
        writeRecord(this.recordBuf);
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
        if (!this.finished) {
            return new TarArchiveEntry(inputFile, entryName);
        }
        throw new IOException("Stream has already been finished");
    }

    private void writeRecord(byte[] record) throws IOException {
        if (record.length == 512) {
            this.out.write(record);
            this.recordsWritten++;
            return;
        }
        throw new IOException("Record to write has length '" + record.length + "' which is not the record size of '" + 512 + "'");
    }

    private void padAsNeeded() throws IOException {
        int start = this.recordsWritten % this.recordsPerBlock;
        if (start != 0) {
            for (int i = start; i < this.recordsPerBlock; i++) {
                writeEOFRecord();
            }
        }
    }

    private void addPaxHeadersForBigNumbers(Map<String, String> paxHeaders, TarArchiveEntry entry) {
        addPaxHeaderForBigNumber(paxHeaders, "size", entry.getSize(), TarConstants.MAXSIZE);
        addPaxHeaderForBigNumber(paxHeaders, "gid", entry.getLongGroupId(), TarConstants.MAXID);
        addPaxHeaderForBigNumber(paxHeaders, "mtime", entry.getModTime().getTime() / 1000, TarConstants.MAXSIZE);
        addPaxHeaderForBigNumber(paxHeaders, "uid", entry.getLongUserId(), TarConstants.MAXID);
        Map<String, String> map = paxHeaders;
        addPaxHeaderForBigNumber(map, "SCHILY.devmajor", (long) entry.getDevMajor(), TarConstants.MAXID);
        addPaxHeaderForBigNumber(map, "SCHILY.devminor", (long) entry.getDevMinor(), TarConstants.MAXID);
        failForBigNumber("mode", (long) entry.getMode(), TarConstants.MAXID);
    }

    private void addPaxHeaderForBigNumber(Map<String, String> paxHeaders, String header, long value, long maxValue) {
        if (value < 0 || value > maxValue) {
            paxHeaders.put(header, String.valueOf(value));
        }
    }

    private void failForBigNumbers(TarArchiveEntry entry) {
        failForBigNumber("entry size", entry.getSize(), TarConstants.MAXSIZE);
        failForBigNumberWithPosixMessage("group id", entry.getLongGroupId(), TarConstants.MAXID);
        failForBigNumber("last modification time", entry.getModTime().getTime() / 1000, TarConstants.MAXSIZE);
        failForBigNumber("user id", entry.getLongUserId(), TarConstants.MAXID);
        failForBigNumber("mode", (long) entry.getMode(), TarConstants.MAXID);
        failForBigNumber("major device number", (long) entry.getDevMajor(), TarConstants.MAXID);
        failForBigNumber("minor device number", (long) entry.getDevMinor(), TarConstants.MAXID);
    }

    private void failForBigNumber(String field, long value, long maxValue) {
        failForBigNumber(field, value, maxValue, "");
    }

    private void failForBigNumberWithPosixMessage(String field, long value, long maxValue) {
        failForBigNumber(field, value, maxValue, " Use STAR or POSIX extensions to overcome this limit");
    }

    private void failForBigNumber(String field, long value, long maxValue, String additionalMsg) {
        if (value < 0 || value > maxValue) {
            throw new RuntimeException(field + " '" + value + "' is too big ( > " + maxValue + " )." + additionalMsg);
        }
    }

    private boolean handleLongName(TarArchiveEntry entry, String name, Map<String, String> paxHeaders, String paxHeaderName, byte linkType, String fieldName) throws IOException {
        ByteBuffer encodedName = this.zipEncoding.encode(name);
        int len = encodedName.limit() - encodedName.position();
        if (len >= 100) {
            int i = this.longFileMode;
            if (i == 3) {
                paxHeaders.put(paxHeaderName, name);
                return true;
            } else if (i == 2) {
                TarArchiveEntry longLinkEntry = new TarArchiveEntry(TarConstants.GNU_LONGLINK, linkType);
                longLinkEntry.setSize(((long) len) + 1);
                transferModTime(entry, longLinkEntry);
                putArchiveEntry(longLinkEntry);
                write(encodedName.array(), encodedName.arrayOffset(), len);
                write(0);
                closeArchiveEntry();
            } else if (i != 1) {
                throw new RuntimeException(fieldName + " '" + name + "' is too long ( > " + 100 + " bytes)");
            }
        }
        return false;
    }

    private void transferModTime(TarArchiveEntry from, TarArchiveEntry to) {
        Date fromModTime = from.getModTime();
        long fromModTimeSeconds = fromModTime.getTime() / 1000;
        if (fromModTimeSeconds < 0 || fromModTimeSeconds > TarConstants.MAXSIZE) {
            fromModTime = new Date(0);
        }
        to.setModTime(fromModTime);
    }
}

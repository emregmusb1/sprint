package org.apache.commons.compress.archivers.tar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import kotlin.jvm.internal.LongCompanionObject;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.commons.compress.utils.IOUtils;

public class TarArchiveInputStream extends ArchiveInputStream {
    private static final int SMALL_BUFFER_SIZE = 256;
    private final int blockSize;
    private TarArchiveEntry currEntry;
    final String encoding;
    private long entryOffset;
    private long entrySize;
    private Map<String, String> globalPaxHeaders;
    private boolean hasHitEOF;
    private final InputStream is;
    private final boolean lenient;
    private final int recordSize;
    private final byte[] smallBuf;
    private final ZipEncoding zipEncoding;

    public TarArchiveInputStream(InputStream is2) {
        this(is2, (int) TarConstants.DEFAULT_BLKSIZE, 512);
    }

    public TarArchiveInputStream(InputStream is2, boolean lenient2) {
        this(is2, TarConstants.DEFAULT_BLKSIZE, 512, (String) null, lenient2);
    }

    public TarArchiveInputStream(InputStream is2, String encoding2) {
        this(is2, TarConstants.DEFAULT_BLKSIZE, 512, encoding2);
    }

    public TarArchiveInputStream(InputStream is2, int blockSize2) {
        this(is2, blockSize2, 512);
    }

    public TarArchiveInputStream(InputStream is2, int blockSize2, String encoding2) {
        this(is2, blockSize2, 512, encoding2);
    }

    public TarArchiveInputStream(InputStream is2, int blockSize2, int recordSize2) {
        this(is2, blockSize2, recordSize2, (String) null);
    }

    public TarArchiveInputStream(InputStream is2, int blockSize2, int recordSize2, String encoding2) {
        this(is2, blockSize2, recordSize2, encoding2, false);
    }

    public TarArchiveInputStream(InputStream is2, int blockSize2, int recordSize2, String encoding2, boolean lenient2) {
        this.smallBuf = new byte[256];
        this.globalPaxHeaders = new HashMap();
        this.is = is2;
        this.hasHitEOF = false;
        this.encoding = encoding2;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding2);
        this.recordSize = recordSize2;
        this.blockSize = blockSize2;
        this.lenient = lenient2;
    }

    public void close() throws IOException {
        this.is.close();
    }

    public int getRecordSize() {
        return this.recordSize;
    }

    public int available() throws IOException {
        if (isDirectory()) {
            return 0;
        }
        long j = this.entrySize;
        long j2 = this.entryOffset;
        if (j - j2 > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) (j - j2);
    }

    public long skip(long n) throws IOException {
        if (n <= 0 || isDirectory()) {
            return 0;
        }
        long skipped = IOUtils.skip(this.is, Math.min(n, this.entrySize - this.entryOffset));
        count(skipped);
        this.entryOffset += skipped;
        return skipped;
    }

    public boolean markSupported() {
        return false;
    }

    public void mark(int markLimit) {
    }

    public synchronized void reset() {
    }

    public TarArchiveEntry getNextTarEntry() throws IOException {
        if (isAtEOF()) {
            return null;
        }
        if (this.currEntry != null) {
            IOUtils.skip(this, LongCompanionObject.MAX_VALUE);
            skipRecordPadding();
        }
        byte[] headerBuf = getRecord();
        if (headerBuf == null) {
            this.currEntry = null;
            return null;
        }
        try {
            this.currEntry = new TarArchiveEntry(headerBuf, this.zipEncoding, this.lenient);
            this.entryOffset = 0;
            this.entrySize = this.currEntry.getSize();
            if (this.currEntry.isGNULongLinkEntry()) {
                byte[] longLinkData = getLongNameData();
                if (longLinkData == null) {
                    return null;
                }
                this.currEntry.setLinkName(this.zipEncoding.decode(longLinkData));
            }
            if (this.currEntry.isGNULongNameEntry()) {
                byte[] longNameData = getLongNameData();
                if (longNameData == null) {
                    return null;
                }
                this.currEntry.setName(this.zipEncoding.decode(longNameData));
            }
            if (this.currEntry.isGlobalPaxHeader()) {
                readGlobalPaxHeaders();
            }
            if (this.currEntry.isPaxHeader()) {
                paxHeaders();
            } else if (!this.globalPaxHeaders.isEmpty()) {
                applyPaxHeadersToCurrentEntry(this.globalPaxHeaders);
            }
            if (this.currEntry.isOldGNUSparse()) {
                readOldGNUSparse();
            }
            this.entrySize = this.currEntry.getSize();
            return this.currEntry;
        } catch (IllegalArgumentException e) {
            throw new IOException("Error detected parsing the header", e);
        }
    }

    private void skipRecordPadding() throws IOException {
        if (!isDirectory()) {
            long j = this.entrySize;
            if (j > 0) {
                int i = this.recordSize;
                if (j % ((long) i) != 0) {
                    count(IOUtils.skip(this.is, (((long) i) * ((j / ((long) i)) + 1)) - j));
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public byte[] getLongNameData() throws IOException {
        ByteArrayOutputStream longName = new ByteArrayOutputStream();
        while (true) {
            int read = read(this.smallBuf);
            int length = read;
            if (read < 0) {
                break;
            }
            longName.write(this.smallBuf, 0, length);
        }
        getNextEntry();
        if (this.currEntry == null) {
            return null;
        }
        byte[] longNameData = longName.toByteArray();
        int length2 = longNameData.length;
        while (length2 > 0 && longNameData[length2 - 1] == 0) {
            length2--;
        }
        if (length2 == longNameData.length) {
            return longNameData;
        }
        byte[] l = new byte[length2];
        System.arraycopy(longNameData, 0, l, 0, length2);
        return l;
    }

    private byte[] getRecord() throws IOException {
        byte[] headerBuf = readRecord();
        setAtEOF(isEOFRecord(headerBuf));
        if (!isAtEOF() || headerBuf == null) {
            return headerBuf;
        }
        tryToConsumeSecondEOFRecord();
        consumeRemainderOfLastBlock();
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEOFRecord(byte[] record) {
        return record == null || ArchiveUtils.isArrayZero(record, this.recordSize);
    }

    /* access modifiers changed from: protected */
    public byte[] readRecord() throws IOException {
        byte[] record = new byte[this.recordSize];
        int readNow = IOUtils.readFully(this.is, record);
        count(readNow);
        if (readNow != this.recordSize) {
            return null;
        }
        return record;
    }

    private void readGlobalPaxHeaders() throws IOException {
        this.globalPaxHeaders = parsePaxHeaders(this);
        getNextEntry();
    }

    private void paxHeaders() throws IOException {
        Map<String, String> headers = parsePaxHeaders(this);
        getNextEntry();
        applyPaxHeadersToCurrentEntry(headers);
    }

    /* access modifiers changed from: package-private */
    public Map<String, String> parsePaxHeaders(InputStream i) throws IOException {
        int ch;
        Map<String, String> headers = new HashMap<>(this.globalPaxHeaders);
        do {
            int len = 0;
            int read = 0;
            while (true) {
                int read2 = i.read();
                ch = read2;
                if (read2 == -1) {
                    break;
                }
                read++;
                if (ch == 10) {
                    continue;
                    break;
                } else if (ch == 32) {
                    ByteArrayOutputStream coll = new ByteArrayOutputStream();
                    while (true) {
                        int read3 = i.read();
                        ch = read3;
                        if (read3 == -1) {
                            continue;
                            break;
                        }
                        read++;
                        if (ch == 61) {
                            String keyword = coll.toString(CharsetNames.UTF_8);
                            int restLen = len - read;
                            if (restLen == 1) {
                                headers.remove(keyword);
                                continue;
                            } else {
                                byte[] rest = new byte[restLen];
                                int got = IOUtils.readFully(i, rest);
                                if (got == restLen) {
                                    headers.put(keyword, new String(rest, 0, restLen - 1, CharsetNames.UTF_8));
                                    continue;
                                } else {
                                    throw new IOException("Failed to read Paxheader. Expected " + restLen + " bytes, read " + got);
                                }
                            }
                        } else {
                            coll.write((byte) ch);
                        }
                    }
                } else {
                    len = (len * 10) + (ch - 48);
                }
            }
        } while (ch != -1);
        return headers;
    }

    private void applyPaxHeadersToCurrentEntry(Map<String, String> headers) {
        this.currEntry.updateEntryFromPaxHeaders(headers);
    }

    private void readOldGNUSparse() throws IOException {
        byte[] headerBuf;
        if (this.currEntry.isExtended()) {
            do {
                headerBuf = getRecord();
                if (headerBuf == null) {
                    this.currEntry = null;
                    return;
                }
            } while (new TarArchiveSparseEntry(headerBuf).isExtended());
        }
    }

    private boolean isDirectory() {
        TarArchiveEntry tarArchiveEntry = this.currEntry;
        return tarArchiveEntry != null && tarArchiveEntry.isDirectory();
    }

    public ArchiveEntry getNextEntry() throws IOException {
        return getNextTarEntry();
    }

    private void tryToConsumeSecondEOFRecord() throws IOException {
        boolean shouldReset = true;
        boolean marked = this.is.markSupported();
        if (marked) {
            this.is.mark(this.recordSize);
        }
        try {
            shouldReset = !isEOFRecord(readRecord());
        } finally {
            if (shouldReset && marked) {
                pushedBackBytes((long) this.recordSize);
                this.is.reset();
            }
        }
    }

    public int read(byte[] buf, int offset, int numToRead) throws IOException {
        if (isAtEOF() || isDirectory() || this.entryOffset >= this.entrySize) {
            return -1;
        }
        if (this.currEntry != null) {
            int numToRead2 = Math.min(numToRead, available());
            int totalRead = this.is.read(buf, offset, numToRead2);
            if (totalRead != -1) {
                count(totalRead);
                this.entryOffset += (long) totalRead;
            } else if (numToRead2 <= 0) {
                setAtEOF(true);
            } else {
                throw new IOException("Truncated TAR archive");
            }
            return totalRead;
        }
        throw new IllegalStateException("No current tar entry");
    }

    public boolean canReadEntryData(ArchiveEntry ae) {
        if (ae instanceof TarArchiveEntry) {
            return !((TarArchiveEntry) ae).isSparse();
        }
        return false;
    }

    public TarArchiveEntry getCurrentEntry() {
        return this.currEntry;
    }

    /* access modifiers changed from: protected */
    public final void setCurrentEntry(TarArchiveEntry e) {
        this.currEntry = e;
    }

    /* access modifiers changed from: protected */
    public final boolean isAtEOF() {
        return this.hasHitEOF;
    }

    /* access modifiers changed from: protected */
    public final void setAtEOF(boolean b) {
        this.hasHitEOF = b;
    }

    private void consumeRemainderOfLastBlock() throws IOException {
        long bytesRead = getBytesRead();
        int i = this.blockSize;
        long bytesReadOfLastBlock = bytesRead % ((long) i);
        if (bytesReadOfLastBlock > 0) {
            count(IOUtils.skip(this.is, ((long) i) - bytesReadOfLastBlock));
        }
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < 265) {
            return false;
        }
        if (ArchiveUtils.matchAsciiBuffer("ustar\u0000", signature, 257, 6) && ArchiveUtils.matchAsciiBuffer(TarConstants.VERSION_POSIX, signature, TarConstants.VERSION_OFFSET, 2)) {
            return true;
        }
        if (ArchiveUtils.matchAsciiBuffer(TarConstants.MAGIC_GNU, signature, 257, 6) && (ArchiveUtils.matchAsciiBuffer(TarConstants.VERSION_GNU_SPACE, signature, TarConstants.VERSION_OFFSET, 2) || ArchiveUtils.matchAsciiBuffer(TarConstants.VERSION_GNU_ZERO, signature, TarConstants.VERSION_OFFSET, 2))) {
            return true;
        }
        if (!ArchiveUtils.matchAsciiBuffer("ustar\u0000", signature, 257, 6) || !ArchiveUtils.matchAsciiBuffer(TarConstants.VERSION_ANT, signature, TarConstants.VERSION_OFFSET, 2)) {
            return false;
        }
        return true;
    }
}

package org.apache.commons.compress.archivers.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;
import kotlin.jvm.internal.LongCompanionObject;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;

public class ZipArchiveInputStream extends ArchiveInputStream implements InputStreamStatistics {
    private static final byte[] APK_SIGNING_BLOCK_MAGIC = {65, 80, TarConstants.LF_GNUTYPE_LONGLINK, 32, TarConstants.LF_GNUTYPE_SPARSE, 105, TarConstants.LF_PAX_GLOBAL_EXTENDED_HEADER, 32, 66, 108, 111, 99, 107, 32, TarConstants.LF_BLK, TarConstants.LF_SYMLINK};
    private static final byte[] CFH = ZipLong.CFH_SIG.getBytes();
    private static final int CFH_LEN = 46;
    private static final byte[] DD = ZipLong.DD_SIG.getBytes();
    private static final byte[] LFH = ZipLong.LFH_SIG.getBytes();
    private static final int LFH_LEN = 30;
    private static final BigInteger LONG_MAX = BigInteger.valueOf(LongCompanionObject.MAX_VALUE);
    private static final long TWO_EXP_32 = 4294967296L;
    private static final String USE_ZIPFILE_INSTEAD_OF_STREAM_DISCLAIMER = " while reading a stored entry using data descriptor. Either the archive is broken or it can not be read using ZipArchiveInputStream and you must use ZipFile. A common cause for this is a ZIP archive containing a ZIP archive. See http://commons.apache.org/proper/commons-compress/zip.html#ZipArchiveInputStream_vs_ZipFile";
    private boolean allowStoredEntriesWithDataDescriptor;
    private final ByteBuffer buf;
    private boolean closed;
    /* access modifiers changed from: private */
    public CurrentEntry current;
    final String encoding;
    private int entriesRead;
    private boolean hitCentralDirectory;
    private final InputStream in;
    private final Inflater inf;
    private ByteArrayInputStream lastStoredEntry;
    private final byte[] lfhBuf;
    private final byte[] shortBuf;
    private final byte[] skipBuf;
    private final byte[] twoDwordBuf;
    private long uncompressedCount;
    private final boolean useUnicodeExtraFields;
    private final byte[] wordBuf;
    private final ZipEncoding zipEncoding;

    public ZipArchiveInputStream(InputStream inputStream) {
        this(inputStream, "UTF8");
    }

    public ZipArchiveInputStream(InputStream inputStream, String encoding2) {
        this(inputStream, encoding2, true);
    }

    public ZipArchiveInputStream(InputStream inputStream, String encoding2, boolean useUnicodeExtraFields2) {
        this(inputStream, encoding2, useUnicodeExtraFields2, false);
    }

    public ZipArchiveInputStream(InputStream inputStream, String encoding2, boolean useUnicodeExtraFields2, boolean allowStoredEntriesWithDataDescriptor2) {
        this.inf = new Inflater(true);
        this.buf = ByteBuffer.allocate(512);
        this.current = null;
        this.closed = false;
        this.hitCentralDirectory = false;
        this.lastStoredEntry = null;
        this.allowStoredEntriesWithDataDescriptor = false;
        this.uncompressedCount = 0;
        this.lfhBuf = new byte[30];
        this.skipBuf = new byte[1024];
        this.shortBuf = new byte[2];
        this.wordBuf = new byte[4];
        this.twoDwordBuf = new byte[16];
        this.entriesRead = 0;
        this.encoding = encoding2;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding2);
        this.useUnicodeExtraFields = useUnicodeExtraFields2;
        this.in = new PushbackInputStream(inputStream, this.buf.capacity());
        this.allowStoredEntriesWithDataDescriptor = allowStoredEntriesWithDataDescriptor2;
        this.buf.limit(0);
    }

    public ZipArchiveEntry getNextZipEntry() throws IOException {
        boolean firstEntry;
        ZipLong cSize;
        int off;
        ZipLong size;
        this.uncompressedCount = 0;
        if (this.closed) {
            return null;
        }
        if (this.hitCentralDirectory) {
            return null;
        }
        if (this.current != null) {
            closeEntry();
            firstEntry = false;
        } else {
            firstEntry = true;
        }
        long currentHeaderOffset = getBytesRead();
        if (firstEntry) {
            try {
                readFirstLocalFileHeader(this.lfhBuf);
            } catch (EOFException e) {
                boolean z = firstEntry;
                long j = currentHeaderOffset;
                return null;
            }
        } else {
            try {
                readFully(this.lfhBuf);
            } catch (EOFException e2) {
                boolean z2 = firstEntry;
                long j2 = currentHeaderOffset;
                return null;
            }
        }
        ZipLong sig = new ZipLong(this.lfhBuf);
        if (sig.equals(ZipLong.LFH_SIG)) {
            this.current = new CurrentEntry((AnonymousClass1) null);
            int off2 = 4 + 2;
            this.current.entry.setPlatform((ZipShort.getValue(this.lfhBuf, 4) >> 8) & 15);
            GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.lfhBuf, off2);
            boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
            ZipEncoding entryEncoding = hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
            boolean unused = this.current.hasDataDescriptor = gpFlag.usesDataDescriptor();
            this.current.entry.setGeneralPurposeBit(gpFlag);
            int off3 = off2 + 2;
            this.current.entry.setMethod(ZipShort.getValue(this.lfhBuf, off3));
            int off4 = off3 + 2;
            this.current.entry.setTime(ZipUtil.dosToJavaTime(ZipLong.getValue(this.lfhBuf, off4)));
            int off5 = off4 + 4;
            if (!this.current.hasDataDescriptor) {
                GeneralPurposeBit generalPurposeBit = gpFlag;
                this.current.entry.setCrc(ZipLong.getValue(this.lfhBuf, off5));
                int off6 = off5 + 4;
                ZipLong cSize2 = new ZipLong(this.lfhBuf, off6);
                int off7 = off6 + 4;
                size = new ZipLong(this.lfhBuf, off7);
                off = off7 + 4;
                cSize = cSize2;
            } else {
                off = off5 + 12;
                size = null;
                cSize = null;
            }
            int fileNameLen = ZipShort.getValue(this.lfhBuf, off);
            int off8 = off + 2;
            int extraLen = ZipShort.getValue(this.lfhBuf, off8);
            int off9 = off8 + 2;
            byte[] fileName = new byte[fileNameLen];
            readFully(fileName);
            ZipLong zipLong = sig;
            boolean z3 = firstEntry;
            this.current.entry.setName(entryEncoding.decode(fileName), fileName);
            if (hasUTF8Flag) {
                this.current.entry.setNameSource(ZipArchiveEntry.NameSource.NAME_WITH_EFS_FLAG);
            }
            byte[] extraData = new byte[extraLen];
            readFully(extraData);
            this.current.entry.setExtra(extraData);
            if (hasUTF8Flag || !this.useUnicodeExtraFields) {
            } else {
                int i = off9;
                ZipUtil.setNameAndCommentFromExtraFields(this.current.entry, fileName, (byte[]) null);
            }
            processZip64Extra(size, cSize);
            this.current.entry.setLocalHeaderOffset(currentHeaderOffset);
            long j3 = currentHeaderOffset;
            ZipLong size2 = size;
            this.current.entry.setDataOffset(getBytesRead());
            this.current.entry.setStreamContiguous(true);
            ZipMethod m = ZipMethod.getMethodByCode(this.current.entry.getMethod());
            if (this.current.entry.getCompressedSize() == -1) {
                ZipLong zipLong2 = size2;
                if (m == ZipMethod.ENHANCED_DEFLATED) {
                    InputStream unused2 = this.current.in = new Deflate64CompressorInputStream(this.in);
                }
            } else if (!ZipUtil.canHandleEntryData(this.current.entry) || m == ZipMethod.STORED || m == ZipMethod.DEFLATED) {
                ZipLong zipLong3 = size2;
            } else {
                ZipLong zipLong4 = size2;
                InputStream bis = new BoundedInputStream(this.in, this.current.entry.getCompressedSize());
                int i2 = AnonymousClass1.$SwitchMap$org$apache$commons$compress$archivers$zip$ZipMethod[m.ordinal()];
                if (i2 == 1) {
                    InputStream unused3 = this.current.in = new UnshrinkingInputStream(bis);
                } else if (i2 == 2) {
                    CurrentEntry currentEntry = this.current;
                    byte[] bArr = extraData;
                    InputStream unused4 = currentEntry.in = new ExplodingInputStream(currentEntry.entry.getGeneralPurposeBit().getSlidingDictionarySize(), this.current.entry.getGeneralPurposeBit().getNumberOfShannonFanoTrees(), bis);
                } else if (i2 == 3) {
                    InputStream unused5 = this.current.in = new BZip2CompressorInputStream(bis);
                    byte[] bArr2 = extraData;
                } else if (i2 != 4) {
                    byte[] bArr3 = extraData;
                } else {
                    InputStream unused6 = this.current.in = new Deflate64CompressorInputStream(bis);
                    byte[] bArr4 = extraData;
                }
            }
            this.entriesRead++;
            return this.current.entry;
        } else if (sig.equals(ZipLong.CFH_SIG) || sig.equals(ZipLong.AED_SIG) || isApkSigningBlock(this.lfhBuf)) {
            this.hitCentralDirectory = true;
            skipRemainderOfArchive();
            return null;
        } else {
            throw new ZipException(String.format("Unexpected record signature: 0X%X", new Object[]{Long.valueOf(sig.getValue())}));
        }
    }

    /* renamed from: org.apache.commons.compress.archivers.zip.ZipArchiveInputStream$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$commons$compress$archivers$zip$ZipMethod = new int[ZipMethod.values().length];

        static {
            try {
                $SwitchMap$org$apache$commons$compress$archivers$zip$ZipMethod[ZipMethod.UNSHRINKING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$commons$compress$archivers$zip$ZipMethod[ZipMethod.IMPLODING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$commons$compress$archivers$zip$ZipMethod[ZipMethod.BZIP2.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$commons$compress$archivers$zip$ZipMethod[ZipMethod.ENHANCED_DEFLATED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private void readFirstLocalFileHeader(byte[] lfh) throws IOException {
        readFully(lfh);
        ZipLong sig = new ZipLong(lfh);
        if (sig.equals(ZipLong.DD_SIG)) {
            throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.SPLITTING);
        } else if (sig.equals(ZipLong.SINGLE_SEGMENT_SPLIT_MARKER)) {
            byte[] missedLfhBytes = new byte[4];
            readFully(missedLfhBytes);
            System.arraycopy(lfh, 4, lfh, 0, 26);
            System.arraycopy(missedLfhBytes, 0, lfh, 26, 4);
        }
    }

    private void processZip64Extra(ZipLong size, ZipLong cSize) {
        Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField) this.current.entry.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        boolean unused = this.current.usesZip64 = z64 != null;
        if (this.current.hasDataDescriptor) {
            return;
        }
        if (z64 != null && (ZipLong.ZIP64_MAGIC.equals(cSize) || ZipLong.ZIP64_MAGIC.equals(size))) {
            this.current.entry.setCompressedSize(z64.getCompressedSize().getLongValue());
            this.current.entry.setSize(z64.getSize().getLongValue());
        } else if (cSize != null && size != null) {
            this.current.entry.setCompressedSize(cSize.getValue());
            this.current.entry.setSize(size.getValue());
        }
    }

    public ArchiveEntry getNextEntry() throws IOException {
        return getNextZipEntry();
    }

    public boolean canReadEntryData(ArchiveEntry ae) {
        if (!(ae instanceof ZipArchiveEntry)) {
            return false;
        }
        ZipArchiveEntry ze = (ZipArchiveEntry) ae;
        if (!ZipUtil.canHandleEntryData(ze) || !supportsDataDescriptorFor(ze) || !supportsCompressedSizeFor(ze)) {
            return false;
        }
        return true;
    }

    public int read(byte[] buffer, int offset, int length) throws IOException {
        int read;
        if (!this.closed) {
            CurrentEntry currentEntry = this.current;
            if (currentEntry == null) {
                return -1;
            }
            if (offset > buffer.length || length < 0 || offset < 0 || buffer.length - offset < length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            ZipUtil.checkRequestedFeatures(currentEntry.entry);
            if (!supportsDataDescriptorFor(this.current.entry)) {
                throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.DATA_DESCRIPTOR, this.current.entry);
            } else if (supportsCompressedSizeFor(this.current.entry)) {
                if (this.current.entry.getMethod() == 0) {
                    read = readStored(buffer, offset, length);
                } else if (this.current.entry.getMethod() == 8) {
                    read = readDeflated(buffer, offset, length);
                } else if (this.current.entry.getMethod() == ZipMethod.UNSHRINKING.getCode() || this.current.entry.getMethod() == ZipMethod.IMPLODING.getCode() || this.current.entry.getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode() || this.current.entry.getMethod() == ZipMethod.BZIP2.getCode()) {
                    read = this.current.in.read(buffer, offset, length);
                } else {
                    throw new UnsupportedZipFeatureException(ZipMethod.getMethodByCode(this.current.entry.getMethod()), this.current.entry);
                }
                if (read >= 0) {
                    this.current.crc.update(buffer, offset, read);
                    this.uncompressedCount += (long) read;
                }
                return read;
            } else {
                throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.UNKNOWN_COMPRESSED_SIZE, this.current.entry);
            }
        } else {
            throw new IOException("The stream is closed");
        }
    }

    public long getCompressedCount() {
        if (this.current.entry.getMethod() == 0) {
            return this.current.bytesRead;
        }
        if (this.current.entry.getMethod() == 8) {
            return getBytesInflated();
        }
        if (this.current.entry.getMethod() == ZipMethod.UNSHRINKING.getCode()) {
            return ((UnshrinkingInputStream) this.current.in).getCompressedCount();
        }
        if (this.current.entry.getMethod() == ZipMethod.IMPLODING.getCode()) {
            return ((ExplodingInputStream) this.current.in).getCompressedCount();
        }
        if (this.current.entry.getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode()) {
            return ((Deflate64CompressorInputStream) this.current.in).getCompressedCount();
        }
        if (this.current.entry.getMethod() == ZipMethod.BZIP2.getCode()) {
            return ((BZip2CompressorInputStream) this.current.in).getCompressedCount();
        }
        return -1;
    }

    public long getUncompressedCount() {
        return this.uncompressedCount;
    }

    private int readStored(byte[] buffer, int offset, int length) throws IOException {
        if (this.current.hasDataDescriptor) {
            if (this.lastStoredEntry == null) {
                readStoredEntry();
            }
            return this.lastStoredEntry.read(buffer, offset, length);
        }
        long csize = this.current.entry.getSize();
        if (this.current.bytesRead >= csize) {
            return -1;
        }
        if (this.buf.position() >= this.buf.limit()) {
            this.buf.position(0);
            int l = this.in.read(this.buf.array());
            if (l != -1) {
                this.buf.limit(l);
                count(l);
                CurrentEntry currentEntry = this.current;
                long unused = currentEntry.bytesReadFromStream = currentEntry.bytesReadFromStream + ((long) l);
            } else {
                this.buf.limit(0);
                throw new IOException("Truncated ZIP file");
            }
        }
        int toRead = Math.min(this.buf.remaining(), length);
        if (csize - this.current.bytesRead < ((long) toRead)) {
            toRead = (int) (csize - this.current.bytesRead);
        }
        this.buf.get(buffer, offset, toRead);
        CurrentEntry currentEntry2 = this.current;
        long unused2 = currentEntry2.bytesRead = currentEntry2.bytesRead + ((long) toRead);
        return toRead;
    }

    private int readDeflated(byte[] buffer, int offset, int length) throws IOException {
        int read = readFromInflater(buffer, offset, length);
        if (read <= 0) {
            if (this.inf.finished()) {
                return -1;
            }
            if (this.inf.needsDictionary()) {
                throw new ZipException("This archive needs a preset dictionary which is not supported by Commons Compress.");
            } else if (read == -1) {
                throw new IOException("Truncated ZIP file");
            }
        }
        return read;
    }

    private int readFromInflater(byte[] buffer, int offset, int length) throws IOException {
        int read = 0;
        while (true) {
            if (this.inf.needsInput()) {
                int l = fill();
                if (l > 0) {
                    CurrentEntry currentEntry = this.current;
                    long unused = currentEntry.bytesReadFromStream = currentEntry.bytesReadFromStream + ((long) this.buf.limit());
                } else if (l == -1) {
                    return -1;
                }
            }
            try {
                read = this.inf.inflate(buffer, offset, length);
                if (read == 0) {
                    if (!this.inf.needsInput()) {
                        break;
                    }
                } else {
                    break;
                }
            } catch (DataFormatException e) {
                throw ((IOException) new ZipException(e.getMessage()).initCause(e));
            }
        }
        return read;
    }

    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            try {
                this.in.close();
            } finally {
                this.inf.end();
            }
        }
    }

    public long skip(long value) throws IOException {
        if (value >= 0) {
            long skipped = 0;
            while (skipped < value) {
                long rem = value - skipped;
                byte[] bArr = this.skipBuf;
                int x = read(bArr, 0, (int) (((long) bArr.length) > rem ? rem : (long) bArr.length));
                if (x == -1) {
                    return skipped;
                }
                skipped += (long) x;
            }
            return skipped;
        }
        throw new IllegalArgumentException();
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < ZipArchiveOutputStream.LFH_SIG.length) {
            return false;
        }
        if (checksig(signature, ZipArchiveOutputStream.LFH_SIG) || checksig(signature, ZipArchiveOutputStream.EOCD_SIG) || checksig(signature, ZipArchiveOutputStream.DD_SIG) || checksig(signature, ZipLong.SINGLE_SEGMENT_SPLIT_MARKER.getBytes())) {
            return true;
        }
        return false;
    }

    private static boolean checksig(byte[] signature, byte[] expected) {
        for (int i = 0; i < expected.length; i++) {
            if (signature[i] != expected[i]) {
                return false;
            }
        }
        return true;
    }

    private void closeEntry() throws IOException {
        if (this.closed) {
            throw new IOException("The stream is closed");
        } else if (this.current != null) {
            if (currentEntryHasOutstandingBytes()) {
                drainCurrentEntryData();
            } else {
                skip(LongCompanionObject.MAX_VALUE);
                int diff = (int) (this.current.bytesReadFromStream - (this.current.entry.getMethod() == 8 ? getBytesInflated() : this.current.bytesRead));
                if (diff > 0) {
                    pushback(this.buf.array(), this.buf.limit() - diff, diff);
                    CurrentEntry currentEntry = this.current;
                    long unused = currentEntry.bytesReadFromStream = currentEntry.bytesReadFromStream - ((long) diff);
                }
                if (currentEntryHasOutstandingBytes()) {
                    drainCurrentEntryData();
                }
            }
            if (this.lastStoredEntry == null && this.current.hasDataDescriptor) {
                readDataDescriptor();
            }
            this.inf.reset();
            this.buf.clear().flip();
            this.current = null;
            this.lastStoredEntry = null;
        }
    }

    private boolean currentEntryHasOutstandingBytes() {
        return this.current.bytesReadFromStream <= this.current.entry.getCompressedSize() && !this.current.hasDataDescriptor;
    }

    private void drainCurrentEntryData() throws IOException {
        long remaining = this.current.entry.getCompressedSize() - this.current.bytesReadFromStream;
        while (remaining > 0) {
            long n = (long) this.in.read(this.buf.array(), 0, (int) Math.min((long) this.buf.capacity(), remaining));
            if (n >= 0) {
                count(n);
                remaining -= n;
            } else {
                throw new EOFException("Truncated ZIP entry: " + ArchiveUtils.sanitize(this.current.entry.getName()));
            }
        }
    }

    private long getBytesInflated() {
        long inB = this.inf.getBytesRead();
        if (this.current.bytesReadFromStream >= TWO_EXP_32) {
            while (inB + TWO_EXP_32 <= this.current.bytesReadFromStream) {
                inB += TWO_EXP_32;
            }
        }
        return inB;
    }

    private int fill() throws IOException {
        if (!this.closed) {
            int length = this.in.read(this.buf.array());
            if (length > 0) {
                this.buf.limit(length);
                count(this.buf.limit());
                this.inf.setInput(this.buf.array(), 0, this.buf.limit());
            }
            return length;
        }
        throw new IOException("The stream is closed");
    }

    private void readFully(byte[] b) throws IOException {
        readFully(b, 0);
    }

    private void readFully(byte[] b, int off) throws IOException {
        int len = b.length - off;
        int count = IOUtils.readFully(this.in, b, off, len);
        count(count);
        if (count < len) {
            throw new EOFException();
        }
    }

    private void readDataDescriptor() throws IOException {
        readFully(this.wordBuf);
        ZipLong val = new ZipLong(this.wordBuf);
        if (ZipLong.DD_SIG.equals(val)) {
            readFully(this.wordBuf);
            val = new ZipLong(this.wordBuf);
        }
        this.current.entry.setCrc(val.getValue());
        readFully(this.twoDwordBuf);
        ZipLong potentialSig = new ZipLong(this.twoDwordBuf, 8);
        if (potentialSig.equals(ZipLong.CFH_SIG) || potentialSig.equals(ZipLong.LFH_SIG)) {
            pushback(this.twoDwordBuf, 8, 8);
            this.current.entry.setCompressedSize(ZipLong.getValue(this.twoDwordBuf));
            this.current.entry.setSize(ZipLong.getValue(this.twoDwordBuf, 4));
            return;
        }
        this.current.entry.setCompressedSize(ZipEightByteInteger.getLongValue(this.twoDwordBuf));
        this.current.entry.setSize(ZipEightByteInteger.getLongValue(this.twoDwordBuf, 8));
    }

    private boolean supportsDataDescriptorFor(ZipArchiveEntry entry) {
        return !entry.getGeneralPurposeBit().usesDataDescriptor() || (this.allowStoredEntriesWithDataDescriptor && entry.getMethod() == 0) || entry.getMethod() == 8 || entry.getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode();
    }

    private boolean supportsCompressedSizeFor(ZipArchiveEntry entry) {
        return entry.getCompressedSize() != -1 || entry.getMethod() == 8 || entry.getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode() || (entry.getGeneralPurposeBit().usesDataDescriptor() && this.allowStoredEntriesWithDataDescriptor && entry.getMethod() == 0);
    }

    private void readStoredEntry() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int off = 0;
        boolean done = false;
        int ddLen = this.current.usesZip64 ? 20 : 12;
        while (!done) {
            int r = this.in.read(this.buf.array(), off, 512 - off);
            if (r <= 0) {
                throw new IOException("Truncated ZIP file");
            } else if (r + off < 4) {
                off += r;
            } else {
                done = bufferContainsSignature(bos, off, r, ddLen);
                if (!done) {
                    off = cacheBytesRead(bos, off, r, ddLen);
                }
            }
        }
        if (this.current.entry.getCompressedSize() == this.current.entry.getSize()) {
            byte[] b = bos.toByteArray();
            if (((long) b.length) == this.current.entry.getSize()) {
                this.lastStoredEntry = new ByteArrayInputStream(b);
                return;
            }
            throw new ZipException("actual and claimed size don't match while reading a stored entry using data descriptor. Either the archive is broken or it can not be read using ZipArchiveInputStream and you must use ZipFile. A common cause for this is a ZIP archive containing a ZIP archive. See http://commons.apache.org/proper/commons-compress/zip.html#ZipArchiveInputStream_vs_ZipFile");
        }
        throw new ZipException("compressed and uncompressed size don't match while reading a stored entry using data descriptor. Either the archive is broken or it can not be read using ZipArchiveInputStream and you must use ZipFile. A common cause for this is a ZIP archive containing a ZIP archive. See http://commons.apache.org/proper/commons-compress/zip.html#ZipArchiveInputStream_vs_ZipFile");
    }

    private boolean bufferContainsSignature(ByteArrayOutputStream bos, int offset, int lastRead, int expectedDDLen) throws IOException {
        boolean done = false;
        int i = 0;
        while (!done && i < (offset + lastRead) - 4) {
            if (this.buf.array()[i] == LFH[0] && this.buf.array()[i + 1] == LFH[1]) {
                int expectDDPos = i;
                if ((i >= expectedDDLen && this.buf.array()[i + 2] == LFH[2] && this.buf.array()[i + 3] == LFH[3]) || (this.buf.array()[i] == CFH[2] && this.buf.array()[i + 3] == CFH[3])) {
                    expectDDPos = i - expectedDDLen;
                    done = true;
                } else if (this.buf.array()[i + 2] == DD[2] && this.buf.array()[i + 3] == DD[3]) {
                    done = true;
                }
                if (done) {
                    pushback(this.buf.array(), expectDDPos, (offset + lastRead) - expectDDPos);
                    bos.write(this.buf.array(), 0, expectDDPos);
                    readDataDescriptor();
                }
            }
            i++;
        }
        return done;
    }

    private int cacheBytesRead(ByteArrayOutputStream bos, int offset, int lastRead, int expecteDDLen) {
        int cacheable = ((offset + lastRead) - expecteDDLen) - 3;
        if (cacheable <= 0) {
            return offset + lastRead;
        }
        bos.write(this.buf.array(), 0, cacheable);
        System.arraycopy(this.buf.array(), cacheable, this.buf.array(), 0, expecteDDLen + 3);
        return expecteDDLen + 3;
    }

    private void pushback(byte[] buf2, int offset, int length) throws IOException {
        ((PushbackInputStream) this.in).unread(buf2, offset, length);
        pushedBackBytes((long) length);
    }

    private void skipRemainderOfArchive() throws IOException {
        realSkip((((long) this.entriesRead) * 46) - 30);
        findEocdRecord();
        realSkip(16);
        readFully(this.shortBuf);
        realSkip((long) ZipShort.getValue(this.shortBuf));
    }

    private void findEocdRecord() throws IOException {
        int currentByte = -1;
        boolean skipReadCall = false;
        while (true) {
            if (!skipReadCall) {
                int readOneByte = readOneByte();
                currentByte = readOneByte;
                if (readOneByte <= -1) {
                    return;
                }
            }
            skipReadCall = false;
            if (isFirstByteOfEocdSig(currentByte)) {
                currentByte = readOneByte();
                if (currentByte == ZipArchiveOutputStream.EOCD_SIG[1]) {
                    currentByte = readOneByte();
                    if (currentByte == ZipArchiveOutputStream.EOCD_SIG[2]) {
                        currentByte = readOneByte();
                        if (currentByte != -1 && currentByte != ZipArchiveOutputStream.EOCD_SIG[3]) {
                            skipReadCall = isFirstByteOfEocdSig(currentByte);
                        } else {
                            return;
                        }
                    } else if (currentByte != -1) {
                        skipReadCall = isFirstByteOfEocdSig(currentByte);
                    } else {
                        return;
                    }
                } else if (currentByte != -1) {
                    skipReadCall = isFirstByteOfEocdSig(currentByte);
                } else {
                    return;
                }
            }
        }
    }

    private void realSkip(long value) throws IOException {
        if (value >= 0) {
            long skipped = 0;
            while (skipped < value) {
                long rem = value - skipped;
                InputStream inputStream = this.in;
                byte[] bArr = this.skipBuf;
                int x = inputStream.read(bArr, 0, (int) (((long) bArr.length) > rem ? rem : (long) bArr.length));
                if (x != -1) {
                    count(x);
                    skipped += (long) x;
                } else {
                    return;
                }
            }
            return;
        }
        throw new IllegalArgumentException();
    }

    private int readOneByte() throws IOException {
        int b = this.in.read();
        if (b != -1) {
            count(1);
        }
        return b;
    }

    private boolean isFirstByteOfEocdSig(int b) {
        return b == ZipArchiveOutputStream.EOCD_SIG[0];
    }

    private boolean isApkSigningBlock(byte[] suspectLocalFileHeader) throws IOException {
        BigInteger toSkip = ZipEightByteInteger.getValue(suspectLocalFileHeader).add(BigInteger.valueOf(((long) (8 - suspectLocalFileHeader.length)) - ((long) APK_SIGNING_BLOCK_MAGIC.length)));
        byte[] magic = new byte[APK_SIGNING_BLOCK_MAGIC.length];
        try {
            if (toSkip.signum() < 0) {
                int off = suspectLocalFileHeader.length + toSkip.intValue();
                if (off < 8) {
                    return false;
                }
                int bytesInBuffer = Math.abs(toSkip.intValue());
                System.arraycopy(suspectLocalFileHeader, off, magic, 0, Math.min(bytesInBuffer, magic.length));
                if (bytesInBuffer < magic.length) {
                    readFully(magic, bytesInBuffer);
                }
            } else {
                while (toSkip.compareTo(LONG_MAX) > 0) {
                    realSkip(LongCompanionObject.MAX_VALUE);
                    toSkip = toSkip.add(LONG_MAX.negate());
                }
                realSkip(toSkip.longValue());
                readFully(magic);
            }
            return Arrays.equals(magic, APK_SIGNING_BLOCK_MAGIC);
        } catch (EOFException e) {
            return false;
        }
    }

    private static final class CurrentEntry {
        /* access modifiers changed from: private */
        public long bytesRead;
        /* access modifiers changed from: private */
        public long bytesReadFromStream;
        /* access modifiers changed from: private */
        public final CRC32 crc;
        /* access modifiers changed from: private */
        public final ZipArchiveEntry entry;
        /* access modifiers changed from: private */
        public boolean hasDataDescriptor;
        /* access modifiers changed from: private */
        public InputStream in;
        /* access modifiers changed from: private */
        public boolean usesZip64;

        private CurrentEntry() {
            this.entry = new ZipArchiveEntry();
            this.crc = new CRC32();
        }

        /* synthetic */ CurrentEntry(AnonymousClass1 x0) {
            this();
        }

        static /* synthetic */ long access$708(CurrentEntry x0) {
            long j = x0.bytesReadFromStream;
            x0.bytesReadFromStream = 1 + j;
            return j;
        }
    }

    private class BoundedInputStream extends InputStream {
        private final InputStream in;
        private final long max;
        private long pos = 0;

        public BoundedInputStream(InputStream in2, long size) {
            this.max = size;
            this.in = in2;
        }

        public int read() throws IOException {
            long j = this.max;
            if (j >= 0 && this.pos >= j) {
                return -1;
            }
            int result = this.in.read();
            this.pos++;
            ZipArchiveInputStream.this.count(1);
            CurrentEntry.access$708(ZipArchiveInputStream.this.current);
            return result;
        }

        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        public int read(byte[] b, int off, int len) throws IOException {
            long j = this.max;
            if (j >= 0 && this.pos >= j) {
                return -1;
            }
            long j2 = this.max;
            int bytesRead = this.in.read(b, off, (int) (j2 >= 0 ? Math.min((long) len, j2 - this.pos) : (long) len));
            if (bytesRead == -1) {
                return -1;
            }
            this.pos += (long) bytesRead;
            ZipArchiveInputStream.this.count(bytesRead);
            CurrentEntry access$900 = ZipArchiveInputStream.this.current;
            long unused = access$900.bytesReadFromStream = access$900.bytesReadFromStream + ((long) bytesRead);
            return bytesRead;
        }

        public long skip(long n) throws IOException {
            long j = this.max;
            long skippedBytes = IOUtils.skip(this.in, j >= 0 ? Math.min(n, j - this.pos) : n);
            this.pos += skippedBytes;
            return skippedBytes;
        }

        public int available() throws IOException {
            long j = this.max;
            if (j < 0 || this.pos < j) {
                return this.in.available();
            }
            return 0;
        }
    }
}

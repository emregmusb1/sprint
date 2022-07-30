package org.apache.commons.compress.archivers.zip;

import androidx.core.internal.view.SupportMenu;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class ZipArchiveOutputStream extends ArchiveOutputStream {
    static final int BUFFER_SIZE = 512;
    private static final int CFH_COMMENT_LENGTH_OFFSET = 32;
    private static final int CFH_COMPRESSED_SIZE_OFFSET = 20;
    private static final int CFH_CRC_OFFSET = 16;
    private static final int CFH_DISK_NUMBER_OFFSET = 34;
    private static final int CFH_EXTERNAL_ATTRIBUTES_OFFSET = 38;
    private static final int CFH_EXTRA_LENGTH_OFFSET = 30;
    private static final int CFH_FILENAME_LENGTH_OFFSET = 28;
    private static final int CFH_FILENAME_OFFSET = 46;
    private static final int CFH_GPB_OFFSET = 8;
    private static final int CFH_INTERNAL_ATTRIBUTES_OFFSET = 36;
    private static final int CFH_LFH_OFFSET = 42;
    private static final int CFH_METHOD_OFFSET = 10;
    private static final int CFH_ORIGINAL_SIZE_OFFSET = 24;
    static final byte[] CFH_SIG = ZipLong.CFH_SIG.getBytes();
    private static final int CFH_SIG_OFFSET = 0;
    private static final int CFH_TIME_OFFSET = 12;
    private static final int CFH_VERSION_MADE_BY_OFFSET = 4;
    private static final int CFH_VERSION_NEEDED_OFFSET = 6;
    static final byte[] DD_SIG = ZipLong.DD_SIG.getBytes();
    public static final int DEFAULT_COMPRESSION = -1;
    static final String DEFAULT_ENCODING = "UTF8";
    public static final int DEFLATED = 8;
    @Deprecated
    public static final int EFS_FLAG = 2048;
    private static final byte[] EMPTY = new byte[0];
    static final byte[] EOCD_SIG = ZipLong.getBytes(101010256);
    private static final int LFH_COMPRESSED_SIZE_OFFSET = 18;
    private static final int LFH_CRC_OFFSET = 14;
    private static final int LFH_EXTRA_LENGTH_OFFSET = 28;
    private static final int LFH_FILENAME_LENGTH_OFFSET = 26;
    private static final int LFH_FILENAME_OFFSET = 30;
    private static final int LFH_GPB_OFFSET = 6;
    private static final int LFH_METHOD_OFFSET = 8;
    private static final int LFH_ORIGINAL_SIZE_OFFSET = 22;
    static final byte[] LFH_SIG = ZipLong.LFH_SIG.getBytes();
    private static final int LFH_SIG_OFFSET = 0;
    private static final int LFH_TIME_OFFSET = 10;
    private static final int LFH_VERSION_NEEDED_OFFSET = 4;
    private static final byte[] LZERO = {0, 0, 0, 0};
    private static final byte[] ONE = ZipLong.getBytes(1);
    public static final int STORED = 0;
    private static final byte[] ZERO = {0, 0};
    static final byte[] ZIP64_EOCD_LOC_SIG = ZipLong.getBytes(117853008);
    static final byte[] ZIP64_EOCD_SIG = ZipLong.getBytes(101075792);
    private final Calendar calendarInstance;
    private long cdLength;
    private long cdOffset;
    private final SeekableByteChannel channel;
    private String comment;
    private final byte[] copyBuffer;
    private UnicodeExtraFieldPolicy createUnicodeExtraFields;
    protected final Deflater def;
    private String encoding;
    private final List<ZipArchiveEntry> entries;
    private CurrentEntry entry;
    private boolean fallbackToUTF8;
    protected boolean finished;
    private boolean hasCompressionLevelChanged;
    private boolean hasUsedZip64;
    private int level;
    private final Map<ZipArchiveEntry, EntryMetaData> metaData;
    private int method;
    private final OutputStream out;
    private final StreamCompressor streamCompressor;
    private boolean useUTF8Flag;
    private Zip64Mode zip64Mode;
    private ZipEncoding zipEncoding;

    public ZipArchiveOutputStream(OutputStream out2) {
        this.finished = false;
        this.comment = "";
        this.level = -1;
        this.hasCompressionLevelChanged = false;
        this.method = 8;
        this.entries = new LinkedList();
        this.cdOffset = 0;
        this.cdLength = 0;
        this.metaData = new HashMap();
        this.encoding = DEFAULT_ENCODING;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(DEFAULT_ENCODING);
        this.useUTF8Flag = true;
        this.fallbackToUTF8 = false;
        this.createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
        this.hasUsedZip64 = false;
        this.zip64Mode = Zip64Mode.AsNeeded;
        this.copyBuffer = new byte[32768];
        this.calendarInstance = Calendar.getInstance();
        this.out = out2;
        this.channel = null;
        this.def = new Deflater(this.level, true);
        this.streamCompressor = StreamCompressor.create(out2, this.def);
    }

    public ZipArchiveOutputStream(File file) throws IOException {
        SeekableByteChannel _channel;
        StreamCompressor _streamCompressor;
        this.finished = false;
        this.comment = "";
        this.level = -1;
        this.hasCompressionLevelChanged = false;
        this.method = 8;
        this.entries = new LinkedList();
        this.cdOffset = 0;
        this.cdLength = 0;
        this.metaData = new HashMap();
        this.encoding = DEFAULT_ENCODING;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(DEFAULT_ENCODING);
        this.useUTF8Flag = true;
        this.fallbackToUTF8 = false;
        this.createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
        this.hasUsedZip64 = false;
        this.zip64Mode = Zip64Mode.AsNeeded;
        this.copyBuffer = new byte[32768];
        this.calendarInstance = Calendar.getInstance();
        this.def = new Deflater(this.level, true);
        OutputStream o = null;
        try {
            _channel = Files.newByteChannel(file.toPath(), EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.TRUNCATE_EXISTING), new FileAttribute[0]);
            _streamCompressor = StreamCompressor.create(_channel, this.def);
        } catch (IOException e) {
            IOUtils.closeQuietly((Closeable) null);
            _channel = null;
            o = new FileOutputStream(file);
            _streamCompressor = StreamCompressor.create(o, this.def);
        }
        this.out = o;
        this.channel = _channel;
        this.streamCompressor = _streamCompressor;
    }

    public ZipArchiveOutputStream(SeekableByteChannel channel2) throws IOException {
        this.finished = false;
        this.comment = "";
        this.level = -1;
        this.hasCompressionLevelChanged = false;
        this.method = 8;
        this.entries = new LinkedList();
        this.cdOffset = 0;
        this.cdLength = 0;
        this.metaData = new HashMap();
        this.encoding = DEFAULT_ENCODING;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(DEFAULT_ENCODING);
        this.useUTF8Flag = true;
        this.fallbackToUTF8 = false;
        this.createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
        this.hasUsedZip64 = false;
        this.zip64Mode = Zip64Mode.AsNeeded;
        this.copyBuffer = new byte[32768];
        this.calendarInstance = Calendar.getInstance();
        this.channel = channel2;
        this.def = new Deflater(this.level, true);
        this.streamCompressor = StreamCompressor.create(channel2, this.def);
        this.out = null;
    }

    public boolean isSeekable() {
        return this.channel != null;
    }

    public void setEncoding(String encoding2) {
        this.encoding = encoding2;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding2);
        if (this.useUTF8Flag && !ZipEncodingHelper.isUTF8(encoding2)) {
            this.useUTF8Flag = false;
        }
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setUseLanguageEncodingFlag(boolean b) {
        this.useUTF8Flag = b && ZipEncodingHelper.isUTF8(this.encoding);
    }

    public void setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy b) {
        this.createUnicodeExtraFields = b;
    }

    public void setFallbackToUTF8(boolean b) {
        this.fallbackToUTF8 = b;
    }

    public void setUseZip64(Zip64Mode mode) {
        this.zip64Mode = mode;
    }

    public void finish() throws IOException {
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        } else if (this.entry == null) {
            this.cdOffset = this.streamCompressor.getTotalBytesWritten();
            writeCentralDirectoryInChunks();
            this.cdLength = this.streamCompressor.getTotalBytesWritten() - this.cdOffset;
            writeZip64CentralDirectory();
            writeCentralDirectoryEnd();
            this.metaData.clear();
            this.entries.clear();
            this.streamCompressor.close();
            this.finished = true;
        } else {
            throw new IOException("This archive contains unclosed entries.");
        }
    }

    private void writeCentralDirectoryInChunks() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(70000);
        int count = 0;
        for (ZipArchiveEntry ze : this.entries) {
            byteArrayOutputStream.write(createCentralFileHeader(ze));
            count++;
            if (count > 1000) {
                writeCounted(byteArrayOutputStream.toByteArray());
                byteArrayOutputStream.reset();
                count = 0;
            }
        }
        writeCounted(byteArrayOutputStream.toByteArray());
    }

    public void closeArchiveEntry() throws IOException {
        preClose();
        flushDeflater();
        long bytesWritten = this.streamCompressor.getTotalBytesWritten() - this.entry.dataStart;
        long realCrc = this.streamCompressor.getCrc32();
        long unused = this.entry.bytesRead = this.streamCompressor.getBytesRead();
        closeEntry(handleSizesAndCrc(bytesWritten, realCrc, getEffectiveZip64Mode(this.entry.entry)), false);
        this.streamCompressor.reset();
    }

    private void closeCopiedEntry(boolean phased) throws IOException {
        preClose();
        CurrentEntry currentEntry = this.entry;
        long unused = currentEntry.bytesRead = currentEntry.entry.getSize();
        closeEntry(checkIfNeedsZip64(getEffectiveZip64Mode(this.entry.entry)), phased);
    }

    private void closeEntry(boolean actuallyNeedsZip64, boolean phased) throws IOException {
        if (!phased && this.channel != null) {
            rewriteSizesAndCrc(actuallyNeedsZip64);
        }
        if (!phased) {
            writeDataDescriptor(this.entry.entry);
        }
        this.entry = null;
    }

    private void preClose() throws IOException {
        if (!this.finished) {
            CurrentEntry currentEntry = this.entry;
            if (currentEntry == null) {
                throw new IOException("No current entry to close");
            } else if (!currentEntry.hasWritten) {
                write(EMPTY, 0, 0);
            }
        } else {
            throw new IOException("Stream has already been finished");
        }
    }

    public void addRawArchiveEntry(ZipArchiveEntry entry2, InputStream rawStream) throws IOException {
        ZipArchiveEntry ae = new ZipArchiveEntry(entry2);
        if (hasZip64Extra(ae)) {
            ae.removeExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        }
        boolean is2PhaseSource = (ae.getCrc() == -1 || ae.getSize() == -1 || ae.getCompressedSize() == -1) ? false : true;
        putArchiveEntry(ae, is2PhaseSource);
        copyFromZipInputStream(rawStream);
        closeCopiedEntry(is2PhaseSource);
    }

    private void flushDeflater() throws IOException {
        if (this.entry.entry.getMethod() == 8) {
            this.streamCompressor.flushDeflater();
        }
    }

    private boolean handleSizesAndCrc(long bytesWritten, long crc, Zip64Mode effectiveMode) throws ZipException {
        if (this.entry.entry.getMethod() == 8) {
            this.entry.entry.setSize(this.entry.bytesRead);
            this.entry.entry.setCompressedSize(bytesWritten);
            this.entry.entry.setCrc(crc);
        } else if (this.channel != null) {
            this.entry.entry.setSize(bytesWritten);
            this.entry.entry.setCompressedSize(bytesWritten);
            this.entry.entry.setCrc(crc);
        } else if (this.entry.entry.getCrc() != crc) {
            throw new ZipException("Bad CRC checksum for entry " + this.entry.entry.getName() + ": " + Long.toHexString(this.entry.entry.getCrc()) + " instead of " + Long.toHexString(crc));
        } else if (this.entry.entry.getSize() != bytesWritten) {
            throw new ZipException("Bad size for entry " + this.entry.entry.getName() + ": " + this.entry.entry.getSize() + " instead of " + bytesWritten);
        }
        return checkIfNeedsZip64(effectiveMode);
    }

    private boolean checkIfNeedsZip64(Zip64Mode effectiveMode) throws ZipException {
        boolean actuallyNeedsZip64 = isZip64Required(this.entry.entry, effectiveMode);
        if (!actuallyNeedsZip64 || effectiveMode != Zip64Mode.Never) {
            return actuallyNeedsZip64;
        }
        throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
    }

    private boolean isZip64Required(ZipArchiveEntry entry1, Zip64Mode requestedMode) {
        return requestedMode == Zip64Mode.Always || isTooLageForZip32(entry1);
    }

    private boolean isTooLageForZip32(ZipArchiveEntry zipArchiveEntry) {
        return zipArchiveEntry.getSize() >= 4294967295L || zipArchiveEntry.getCompressedSize() >= 4294967295L;
    }

    private void rewriteSizesAndCrc(boolean actuallyNeedsZip64) throws IOException {
        long save = this.channel.position();
        this.channel.position(this.entry.localDataStart);
        writeOut(ZipLong.getBytes(this.entry.entry.getCrc()));
        if (!hasZip64Extra(this.entry.entry) || !actuallyNeedsZip64) {
            writeOut(ZipLong.getBytes(this.entry.entry.getCompressedSize()));
            writeOut(ZipLong.getBytes(this.entry.entry.getSize()));
        } else {
            writeOut(ZipLong.ZIP64_MAGIC.getBytes());
            writeOut(ZipLong.ZIP64_MAGIC.getBytes());
        }
        if (hasZip64Extra(this.entry.entry)) {
            ByteBuffer name = getName(this.entry.entry);
            this.channel.position(this.entry.localDataStart + 12 + 4 + ((long) (name.limit() - name.position())) + 4);
            writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getSize()));
            writeOut(ZipEightByteInteger.getBytes(this.entry.entry.getCompressedSize()));
            if (!actuallyNeedsZip64) {
                this.channel.position(this.entry.localDataStart - 10);
                writeOut(ZipShort.getBytes(versionNeededToExtract(this.entry.entry.getMethod(), false, false)));
                this.entry.entry.removeExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
                this.entry.entry.setExtra();
                if (this.entry.causedUseOfZip64) {
                    this.hasUsedZip64 = false;
                }
            }
        }
        this.channel.position(save);
    }

    public void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException {
        putArchiveEntry(archiveEntry, false);
    }

    private void putArchiveEntry(ArchiveEntry archiveEntry, boolean phased) throws IOException {
        ZipEightByteInteger size;
        ZipEightByteInteger size2;
        if (!this.finished) {
            if (this.entry != null) {
                closeArchiveEntry();
            }
            this.entry = new CurrentEntry((ZipArchiveEntry) archiveEntry);
            this.entries.add(this.entry.entry);
            setDefaults(this.entry.entry);
            Zip64Mode effectiveMode = getEffectiveZip64Mode(this.entry.entry);
            validateSizeInformation(effectiveMode);
            if (shouldAddZip64Extra(this.entry.entry, effectiveMode)) {
                Zip64ExtendedInformationExtraField z64 = getZip64Extra(this.entry.entry);
                if (phased) {
                    ZipEightByteInteger size3 = new ZipEightByteInteger(this.entry.entry.getSize());
                    size = size3;
                    size2 = new ZipEightByteInteger(this.entry.entry.getCompressedSize());
                } else if (this.entry.entry.getMethod() != 0 || this.entry.entry.getSize() == -1) {
                    size2 = ZipEightByteInteger.ZERO;
                    size = size2;
                } else {
                    size2 = new ZipEightByteInteger(this.entry.entry.getSize());
                    size = size2;
                }
                z64.setSize(size);
                z64.setCompressedSize(size2);
                this.entry.entry.setExtra();
            }
            if (this.entry.entry.getMethod() == 8 && this.hasCompressionLevelChanged) {
                this.def.setLevel(this.level);
                this.hasCompressionLevelChanged = false;
            }
            writeLocalFileHeader((ZipArchiveEntry) archiveEntry, phased);
            return;
        }
        throw new IOException("Stream has already been finished");
    }

    private void setDefaults(ZipArchiveEntry entry2) {
        if (entry2.getMethod() == -1) {
            entry2.setMethod(this.method);
        }
        if (entry2.getTime() == -1) {
            entry2.setTime(System.currentTimeMillis());
        }
    }

    private void validateSizeInformation(Zip64Mode effectiveMode) throws ZipException {
        if (this.entry.entry.getMethod() == 0 && this.channel == null) {
            if (this.entry.entry.getSize() == -1) {
                throw new ZipException("Uncompressed size is required for STORED method when not writing to a file");
            } else if (this.entry.entry.getCrc() != -1) {
                this.entry.entry.setCompressedSize(this.entry.entry.getSize());
            } else {
                throw new ZipException("CRC checksum is required for STORED method when not writing to a file");
            }
        }
        if ((this.entry.entry.getSize() >= 4294967295L || this.entry.entry.getCompressedSize() >= 4294967295L) && effectiveMode == Zip64Mode.Never) {
            throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(this.entry.entry));
        }
    }

    private boolean shouldAddZip64Extra(ZipArchiveEntry entry2, Zip64Mode mode) {
        return mode == Zip64Mode.Always || entry2.getSize() >= 4294967295L || entry2.getCompressedSize() >= 4294967295L || !(entry2.getSize() != -1 || this.channel == null || mode == Zip64Mode.Never);
    }

    public void setComment(String comment2) {
        this.comment = comment2;
    }

    public void setLevel(int level2) {
        if (level2 < -1 || level2 > 9) {
            throw new IllegalArgumentException("Invalid compression level: " + level2);
        } else if (this.level != level2) {
            this.hasCompressionLevelChanged = true;
            this.level = level2;
        }
    }

    public void setMethod(int method2) {
        this.method = method2;
    }

    public boolean canWriteEntryData(ArchiveEntry ae) {
        if (!(ae instanceof ZipArchiveEntry)) {
            return false;
        }
        ZipArchiveEntry zae = (ZipArchiveEntry) ae;
        if (zae.getMethod() == ZipMethod.IMPLODING.getCode() || zae.getMethod() == ZipMethod.UNSHRINKING.getCode() || !ZipUtil.canHandleEntryData(zae)) {
            return false;
        }
        return true;
    }

    public void write(byte[] b, int offset, int length) throws IOException {
        CurrentEntry currentEntry = this.entry;
        if (currentEntry != null) {
            ZipUtil.checkRequestedFeatures(currentEntry.entry);
            count(this.streamCompressor.write(b, offset, length, this.entry.entry.getMethod()));
            return;
        }
        throw new IllegalStateException("No current entry");
    }

    private void writeCounted(byte[] data) throws IOException {
        this.streamCompressor.writeCounted(data);
    }

    private void copyFromZipInputStream(InputStream src) throws IOException {
        CurrentEntry currentEntry = this.entry;
        if (currentEntry != null) {
            ZipUtil.checkRequestedFeatures(currentEntry.entry);
            boolean unused = this.entry.hasWritten = true;
            while (true) {
                int read = src.read(this.copyBuffer);
                int length = read;
                if (read >= 0) {
                    this.streamCompressor.writeCounted(this.copyBuffer, 0, length);
                    count(length);
                } else {
                    return;
                }
            }
        } else {
            throw new IllegalStateException("No current entry");
        }
    }

    public void close() throws IOException {
        try {
            if (!this.finished) {
                finish();
            }
        } finally {
            destroy();
        }
    }

    public void flush() throws IOException {
        OutputStream outputStream = this.out;
        if (outputStream != null) {
            outputStream.flush();
        }
    }

    /* access modifiers changed from: protected */
    public final void deflate() throws IOException {
        this.streamCompressor.deflate();
    }

    /* access modifiers changed from: protected */
    public void writeLocalFileHeader(ZipArchiveEntry ze) throws IOException {
        writeLocalFileHeader(ze, false);
    }

    private void writeLocalFileHeader(ZipArchiveEntry ze, boolean phased) throws IOException {
        boolean encodable = this.zipEncoding.canEncode(ze.getName());
        ByteBuffer name = getName(ze);
        if (this.createUnicodeExtraFields != UnicodeExtraFieldPolicy.NEVER) {
            addUnicodeExtraFields(ze, encodable, name);
        }
        long localHeaderStart = this.streamCompressor.getTotalBytesWritten();
        byte[] localHeader = createLocalFileHeader(ze, name, encodable, phased, localHeaderStart);
        this.metaData.put(ze, new EntryMetaData(localHeaderStart, usesDataDescriptor(ze.getMethod(), phased)));
        long unused = this.entry.localDataStart = 14 + localHeaderStart;
        writeCounted(localHeader);
        long unused2 = this.entry.dataStart = this.streamCompressor.getTotalBytesWritten();
    }

    private byte[] createLocalFileHeader(ZipArchiveEntry ze, ByteBuffer name, boolean encodable, boolean phased, long archiveOffset) {
        ZipArchiveEntry zipArchiveEntry = ze;
        boolean z = phased;
        ResourceAlignmentExtraField oldAlignmentEx = (ResourceAlignmentExtraField) zipArchiveEntry.getExtraField(ResourceAlignmentExtraField.ID);
        if (oldAlignmentEx != null) {
            zipArchiveEntry.removeExtraField(ResourceAlignmentExtraField.ID);
        }
        int alignment = ze.getAlignment();
        if (alignment <= 0 && oldAlignmentEx != null) {
            alignment = oldAlignmentEx.getAlignment();
        }
        if (alignment > 1 || (oldAlignmentEx != null && !oldAlignmentEx.allowMethodChange())) {
            zipArchiveEntry.addExtraField(new ResourceAlignmentExtraField(alignment, oldAlignmentEx != null && oldAlignmentEx.allowMethodChange(), (int) (((((-archiveOffset) - ((long) (((name.limit() + 30) - name.position()) + ze.getLocalFileDataExtra().length))) - 4) - 2) & ((long) (alignment - 1)))));
        } else {
            long j = archiveOffset;
        }
        byte[] extra = ze.getLocalFileDataExtra();
        int nameLen = name.limit() - name.position();
        byte[] buf = new byte[(nameLen + 30 + extra.length)];
        System.arraycopy(LFH_SIG, 0, buf, 0, 4);
        int zipMethod = ze.getMethod();
        boolean dataDescriptor = usesDataDescriptor(zipMethod, z);
        ZipShort.putShort(versionNeededToExtract(zipMethod, hasZip64Extra(ze), dataDescriptor), buf, 4);
        getGeneralPurposeBits(!encodable && this.fallbackToUTF8, dataDescriptor).encode(buf, 6);
        ZipShort.putShort(zipMethod, buf, 8);
        byte[] extra2 = extra;
        ZipUtil.toDosTime(this.calendarInstance, ze.getTime(), buf, 10);
        if (z) {
            ZipLong.putLong(ze.getCrc(), buf, 14);
        } else if (zipMethod == 8 || this.channel != null) {
            System.arraycopy(LZERO, 0, buf, 14, 4);
        } else {
            ZipLong.putLong(ze.getCrc(), buf, 14);
        }
        if (hasZip64Extra(this.entry.entry)) {
            ZipLong.ZIP64_MAGIC.putLong(buf, 18);
            ZipLong.ZIP64_MAGIC.putLong(buf, 22);
        } else if (z) {
            ZipLong.putLong(ze.getCompressedSize(), buf, 18);
            ZipLong.putLong(ze.getSize(), buf, 22);
        } else if (zipMethod == 8 || this.channel != null) {
            System.arraycopy(LZERO, 0, buf, 18, 4);
            System.arraycopy(LZERO, 0, buf, 22, 4);
        } else {
            ZipLong.putLong(ze.getSize(), buf, 18);
            ZipLong.putLong(ze.getSize(), buf, 22);
        }
        ZipShort.putShort(nameLen, buf, 26);
        byte[] extra3 = extra2;
        ZipShort.putShort(extra3.length, buf, 28);
        System.arraycopy(name.array(), name.arrayOffset(), buf, 30, nameLen);
        System.arraycopy(extra3, 0, buf, nameLen + 30, extra3.length);
        return buf;
    }

    private void addUnicodeExtraFields(ZipArchiveEntry ze, boolean encodable, ByteBuffer name) throws IOException {
        if (this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS || !encodable) {
            ze.addExtraField(new UnicodePathExtraField(ze.getName(), name.array(), name.arrayOffset(), name.limit() - name.position()));
        }
        String comm = ze.getComment();
        if (comm != null && !"".equals(comm)) {
            boolean commentEncodable = this.zipEncoding.canEncode(comm);
            if (this.createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS || !commentEncodable) {
                ByteBuffer commentB = getEntryEncoding(ze).encode(comm);
                ze.addExtraField(new UnicodeCommentExtraField(comm, commentB.array(), commentB.arrayOffset(), commentB.limit() - commentB.position()));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void writeDataDescriptor(ZipArchiveEntry ze) throws IOException {
        if (usesDataDescriptor(ze.getMethod(), false)) {
            writeCounted(DD_SIG);
            writeCounted(ZipLong.getBytes(ze.getCrc()));
            if (!hasZip64Extra(ze)) {
                writeCounted(ZipLong.getBytes(ze.getCompressedSize()));
                writeCounted(ZipLong.getBytes(ze.getSize()));
                return;
            }
            writeCounted(ZipEightByteInteger.getBytes(ze.getCompressedSize()));
            writeCounted(ZipEightByteInteger.getBytes(ze.getSize()));
        }
    }

    /* access modifiers changed from: protected */
    public void writeCentralFileHeader(ZipArchiveEntry ze) throws IOException {
        writeCounted(createCentralFileHeader(ze));
    }

    private byte[] createCentralFileHeader(ZipArchiveEntry ze) throws IOException {
        EntryMetaData entryMetaData = this.metaData.get(ze);
        boolean needsZip64Extra = hasZip64Extra(ze) || ze.getCompressedSize() >= 4294967295L || ze.getSize() >= 4294967295L || entryMetaData.offset >= 4294967295L || this.zip64Mode == Zip64Mode.Always;
        if (!needsZip64Extra || this.zip64Mode != Zip64Mode.Never) {
            handleZip64Extra(ze, entryMetaData.offset, needsZip64Extra);
            return createCentralFileHeader(ze, getName(ze), entryMetaData, needsZip64Extra);
        }
        throw new Zip64RequiredException("Archive's size exceeds the limit of 4GByte.");
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0121  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private byte[] createCentralFileHeader(org.apache.commons.compress.archivers.zip.ZipArchiveEntry r19, java.nio.ByteBuffer r20, org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream.EntryMetaData r21, boolean r22) throws java.io.IOException {
        /*
            r18 = this;
            r0 = r18
            byte[] r1 = r19.getCentralDirectoryExtra()
            java.lang.String r2 = r19.getComment()
            if (r2 != 0) goto L_0x000e
            java.lang.String r2 = ""
        L_0x000e:
            org.apache.commons.compress.archivers.zip.ZipEncoding r3 = r18.getEntryEncoding(r19)
            java.nio.ByteBuffer r3 = r3.encode(r2)
            int r4 = r20.limit()
            int r5 = r20.position()
            int r4 = r4 - r5
            int r5 = r3.limit()
            int r6 = r3.position()
            int r5 = r5 - r6
            int r6 = r4 + 46
            int r7 = r1.length
            int r6 = r6 + r7
            int r6 = r6 + r5
            byte[] r7 = new byte[r6]
            byte[] r8 = CFH_SIG
            r9 = 4
            r10 = 0
            java.lang.System.arraycopy(r8, r10, r7, r10, r9)
            int r8 = r19.getPlatform()
            r11 = 8
            int r8 = r8 << r11
            boolean r12 = r0.hasUsedZip64
            if (r12 != 0) goto L_0x0044
            r12 = 20
            goto L_0x0046
        L_0x0044:
            r12 = 45
        L_0x0046:
            r8 = r8 | r12
            org.apache.commons.compress.archivers.zip.ZipShort.putShort(r8, r7, r9)
            int r8 = r19.getMethod()
            org.apache.commons.compress.archivers.zip.ZipEncoding r9 = r0.zipEncoding
            java.lang.String r12 = r19.getName()
            boolean r9 = r9.canEncode(r12)
            boolean r12 = r21.usesDataDescriptor
            r14 = r22
            int r12 = r0.versionNeededToExtract(r8, r14, r12)
            r15 = 6
            org.apache.commons.compress.archivers.zip.ZipShort.putShort(r12, r7, r15)
            if (r9 != 0) goto L_0x006e
            boolean r12 = r0.fallbackToUTF8
            if (r12 == 0) goto L_0x006e
            r12 = 1
            goto L_0x006f
        L_0x006e:
            r12 = 0
        L_0x006f:
            boolean r15 = r21.usesDataDescriptor
            org.apache.commons.compress.archivers.zip.GeneralPurposeBit r12 = r0.getGeneralPurposeBits(r12, r15)
            r12.encode(r7, r11)
            r11 = 10
            org.apache.commons.compress.archivers.zip.ZipShort.putShort(r8, r7, r11)
            java.util.Calendar r11 = r0.calendarInstance
            long r13 = r19.getTime()
            r15 = 12
            org.apache.commons.compress.archivers.zip.ZipUtil.toDosTime(r11, r13, r7, r15)
            long r13 = r19.getCrc()
            r11 = 16
            org.apache.commons.compress.archivers.zip.ZipLong.putLong(r13, r7, r11)
            long r13 = r19.getCompressedSize()
            r10 = 4294967295(0xffffffff, double:2.1219957905E-314)
            int r17 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r17 >= 0) goto L_0x00cb
            long r13 = r19.getSize()
            int r17 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r17 >= 0) goto L_0x00c6
            org.apache.commons.compress.archivers.zip.Zip64Mode r13 = r0.zip64Mode
            org.apache.commons.compress.archivers.zip.Zip64Mode r14 = org.apache.commons.compress.archivers.zip.Zip64Mode.Always
            if (r13 != r14) goto L_0x00b3
            r12 = 20
            r14 = 24
            goto L_0x00cf
        L_0x00b3:
            long r13 = r19.getCompressedSize()
            r12 = 20
            org.apache.commons.compress.archivers.zip.ZipLong.putLong(r13, r7, r12)
            long r12 = r19.getSize()
            r14 = 24
            org.apache.commons.compress.archivers.zip.ZipLong.putLong(r12, r7, r14)
            goto L_0x00d9
        L_0x00c6:
            r12 = 20
            r14 = 24
            goto L_0x00cf
        L_0x00cb:
            r12 = 20
            r14 = 24
        L_0x00cf:
            org.apache.commons.compress.archivers.zip.ZipLong r13 = org.apache.commons.compress.archivers.zip.ZipLong.ZIP64_MAGIC
            r13.putLong(r7, r12)
            org.apache.commons.compress.archivers.zip.ZipLong r12 = org.apache.commons.compress.archivers.zip.ZipLong.ZIP64_MAGIC
            r12.putLong(r7, r14)
        L_0x00d9:
            r12 = 28
            org.apache.commons.compress.archivers.zip.ZipShort.putShort(r4, r7, r12)
            int r12 = r1.length
            r13 = 30
            org.apache.commons.compress.archivers.zip.ZipShort.putShort(r12, r7, r13)
            r12 = 32
            org.apache.commons.compress.archivers.zip.ZipShort.putShort(r5, r7, r12)
            byte[] r12 = ZERO
            r13 = 34
            r14 = 2
            r15 = 0
            java.lang.System.arraycopy(r12, r15, r7, r13, r14)
            int r12 = r19.getInternalAttributes()
            r13 = 36
            org.apache.commons.compress.archivers.zip.ZipShort.putShort(r12, r7, r13)
            long r12 = r19.getExternalAttributes()
            r14 = 38
            org.apache.commons.compress.archivers.zip.ZipLong.putLong(r12, r7, r14)
            long r12 = r21.offset
            r14 = 42
            int r16 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r16 >= 0) goto L_0x0121
            org.apache.commons.compress.archivers.zip.Zip64Mode r12 = r0.zip64Mode
            org.apache.commons.compress.archivers.zip.Zip64Mode r13 = org.apache.commons.compress.archivers.zip.Zip64Mode.Always
            if (r12 != r13) goto L_0x0115
            goto L_0x0121
        L_0x0115:
            long r12 = r21.offset
            long r10 = java.lang.Math.min(r12, r10)
            org.apache.commons.compress.archivers.zip.ZipLong.putLong(r10, r7, r14)
            goto L_0x0124
        L_0x0121:
            org.apache.commons.compress.archivers.zip.ZipLong.putLong(r10, r7, r14)
        L_0x0124:
            byte[] r10 = r20.array()
            int r11 = r20.arrayOffset()
            r12 = 46
            java.lang.System.arraycopy(r10, r11, r7, r12, r4)
            int r10 = r4 + 46
            int r11 = r1.length
            r12 = 0
            java.lang.System.arraycopy(r1, r12, r7, r10, r11)
            int r11 = r1.length
            int r11 = r11 + r10
            byte[] r12 = r3.array()
            int r13 = r3.arrayOffset()
            java.lang.System.arraycopy(r12, r13, r7, r11, r5)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream.createCentralFileHeader(org.apache.commons.compress.archivers.zip.ZipArchiveEntry, java.nio.ByteBuffer, org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream$EntryMetaData, boolean):byte[]");
    }

    private void handleZip64Extra(ZipArchiveEntry ze, long lfhOffset, boolean needsZip64Extra) {
        if (needsZip64Extra) {
            Zip64ExtendedInformationExtraField z64 = getZip64Extra(ze);
            if (ze.getCompressedSize() >= 4294967295L || ze.getSize() >= 4294967295L || this.zip64Mode == Zip64Mode.Always) {
                z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
                z64.setSize(new ZipEightByteInteger(ze.getSize()));
            } else {
                z64.setCompressedSize((ZipEightByteInteger) null);
                z64.setSize((ZipEightByteInteger) null);
            }
            if (lfhOffset >= 4294967295L || this.zip64Mode == Zip64Mode.Always) {
                z64.setRelativeHeaderOffset(new ZipEightByteInteger(lfhOffset));
            }
            ze.setExtra();
        }
    }

    /* access modifiers changed from: protected */
    public void writeCentralDirectoryEnd() throws IOException {
        writeCounted(EOCD_SIG);
        writeCounted(ZERO);
        writeCounted(ZERO);
        int numberOfEntries = this.entries.size();
        if (numberOfEntries > 65535 && this.zip64Mode == Zip64Mode.Never) {
            throw new Zip64RequiredException("Archive contains more than 65535 entries.");
        } else if (this.cdOffset <= 4294967295L || this.zip64Mode != Zip64Mode.Never) {
            byte[] num = ZipShort.getBytes(Math.min(numberOfEntries, SupportMenu.USER_MASK));
            writeCounted(num);
            writeCounted(num);
            writeCounted(ZipLong.getBytes(Math.min(this.cdLength, 4294967295L)));
            writeCounted(ZipLong.getBytes(Math.min(this.cdOffset, 4294967295L)));
            ByteBuffer data = this.zipEncoding.encode(this.comment);
            int dataLen = data.limit() - data.position();
            writeCounted(ZipShort.getBytes(dataLen));
            this.streamCompressor.writeCounted(data.array(), data.arrayOffset(), dataLen);
        } else {
            throw new Zip64RequiredException("Archive's size exceeds the limit of 4GByte.");
        }
    }

    /* access modifiers changed from: protected */
    public void writeZip64CentralDirectory() throws IOException {
        if (this.zip64Mode != Zip64Mode.Never) {
            if (!this.hasUsedZip64 && (this.cdOffset >= 4294967295L || this.cdLength >= 4294967295L || this.entries.size() >= 65535)) {
                this.hasUsedZip64 = true;
            }
            if (this.hasUsedZip64) {
                long offset = this.streamCompressor.getTotalBytesWritten();
                writeOut(ZIP64_EOCD_SIG);
                writeOut(ZipEightByteInteger.getBytes(44));
                writeOut(ZipShort.getBytes(45));
                writeOut(ZipShort.getBytes(45));
                writeOut(LZERO);
                writeOut(LZERO);
                byte[] num = ZipEightByteInteger.getBytes((long) this.entries.size());
                writeOut(num);
                writeOut(num);
                writeOut(ZipEightByteInteger.getBytes(this.cdLength));
                writeOut(ZipEightByteInteger.getBytes(this.cdOffset));
                writeOut(ZIP64_EOCD_LOC_SIG);
                writeOut(LZERO);
                writeOut(ZipEightByteInteger.getBytes(offset));
                writeOut(ONE);
            }
        }
    }

    /* access modifiers changed from: protected */
    public final void writeOut(byte[] data) throws IOException {
        this.streamCompressor.writeOut(data, 0, data.length);
    }

    /* access modifiers changed from: protected */
    public final void writeOut(byte[] data, int offset, int length) throws IOException {
        this.streamCompressor.writeOut(data, offset, length);
    }

    private GeneralPurposeBit getGeneralPurposeBits(boolean utfFallback, boolean usesDataDescriptor) {
        GeneralPurposeBit b = new GeneralPurposeBit();
        b.useUTF8ForNames(this.useUTF8Flag || utfFallback);
        if (usesDataDescriptor) {
            b.useDataDescriptor(true);
        }
        return b;
    }

    private int versionNeededToExtract(int zipMethod, boolean zip64, boolean usedDataDescriptor) {
        if (zip64) {
            return 45;
        }
        if (usedDataDescriptor) {
            return 20;
        }
        return versionNeededToExtractMethod(zipMethod);
    }

    private boolean usesDataDescriptor(int zipMethod, boolean phased) {
        return !phased && zipMethod == 8 && this.channel == null;
    }

    private int versionNeededToExtractMethod(int zipMethod) {
        return zipMethod == 8 ? 20 : 10;
    }

    public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
        if (!this.finished) {
            return new ZipArchiveEntry(inputFile, entryName);
        }
        throw new IOException("Stream has already been finished");
    }

    private Zip64ExtendedInformationExtraField getZip64Extra(ZipArchiveEntry ze) {
        CurrentEntry currentEntry = this.entry;
        if (currentEntry != null) {
            boolean unused = currentEntry.causedUseOfZip64 = !this.hasUsedZip64;
        }
        this.hasUsedZip64 = true;
        Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField) ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        if (z64 == null) {
            z64 = new Zip64ExtendedInformationExtraField();
        }
        ze.addAsFirstExtraField(z64);
        return z64;
    }

    private boolean hasZip64Extra(ZipArchiveEntry ze) {
        return ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID) != null;
    }

    private Zip64Mode getEffectiveZip64Mode(ZipArchiveEntry ze) {
        if (this.zip64Mode == Zip64Mode.AsNeeded && this.channel == null && ze.getMethod() == 8 && ze.getSize() == -1) {
            return Zip64Mode.Never;
        }
        return this.zip64Mode;
    }

    private ZipEncoding getEntryEncoding(ZipArchiveEntry ze) {
        return (this.zipEncoding.canEncode(ze.getName()) || !this.fallbackToUTF8) ? this.zipEncoding : ZipEncodingHelper.UTF8_ZIP_ENCODING;
    }

    private ByteBuffer getName(ZipArchiveEntry ze) throws IOException {
        return getEntryEncoding(ze).encode(ze.getName());
    }

    /* access modifiers changed from: package-private */
    public void destroy() throws IOException {
        try {
            if (this.channel != null) {
                this.channel.close();
            }
        } finally {
            OutputStream outputStream = this.out;
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public static final class UnicodeExtraFieldPolicy {
        public static final UnicodeExtraFieldPolicy ALWAYS = new UnicodeExtraFieldPolicy("always");
        public static final UnicodeExtraFieldPolicy NEVER = new UnicodeExtraFieldPolicy("never");
        public static final UnicodeExtraFieldPolicy NOT_ENCODEABLE = new UnicodeExtraFieldPolicy("not encodeable");
        private final String name;

        private UnicodeExtraFieldPolicy(String n) {
            this.name = n;
        }

        public String toString() {
            return this.name;
        }
    }

    private static final class CurrentEntry {
        /* access modifiers changed from: private */
        public long bytesRead;
        /* access modifiers changed from: private */
        public boolean causedUseOfZip64;
        /* access modifiers changed from: private */
        public long dataStart;
        /* access modifiers changed from: private */
        public final ZipArchiveEntry entry;
        /* access modifiers changed from: private */
        public boolean hasWritten;
        /* access modifiers changed from: private */
        public long localDataStart;

        private CurrentEntry(ZipArchiveEntry entry2) {
            this.localDataStart = 0;
            this.dataStart = 0;
            this.bytesRead = 0;
            this.causedUseOfZip64 = false;
            this.entry = entry2;
        }
    }

    private static final class EntryMetaData {
        /* access modifiers changed from: private */
        public final long offset;
        /* access modifiers changed from: private */
        public final boolean usesDataDescriptor;

        private EntryMetaData(long offset2, boolean usesDataDescriptor2) {
            this.offset = offset2;
            this.usesDataDescriptor = usesDataDescriptor2;
        }
    }
}

package org.apache.commons.compress.archivers.sevenz;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.zip.CRC32;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;

public class SevenZFile implements Closeable {
    private static final String DEFAULT_FILE_NAME = "unknown archive";
    private static final CharsetEncoder PASSWORD_ENCODER = StandardCharsets.UTF_16LE.newEncoder();
    static final int SIGNATURE_HEADER_SIZE = 32;
    static final byte[] sevenZSignature = {TarConstants.LF_CONTIG, 122, -68, -81, 39, 28};
    private final Archive archive;
    private SeekableByteChannel channel;
    /* access modifiers changed from: private */
    public long compressedBytesReadFromCurrentEntry;
    private int currentEntryIndex;
    private int currentFolderIndex;
    private InputStream currentFolderInputStream;
    private final ArrayList<InputStream> deferredBlockStreams;
    private final String fileName;
    private final SevenZFileOptions options;
    private byte[] password;
    /* access modifiers changed from: private */
    public long uncompressedBytesReadFromCurrentEntry;

    public SevenZFile(File fileName2, char[] password2) throws IOException {
        this(fileName2, password2, SevenZFileOptions.DEFAULT);
    }

    public SevenZFile(File fileName2, char[] password2, SevenZFileOptions options2) throws IOException {
        this(Files.newByteChannel(fileName2.toPath(), EnumSet.of(StandardOpenOption.READ), new FileAttribute[0]), fileName2.getAbsolutePath(), utf16Decode(password2), true, options2);
    }

    public SevenZFile(File fileName2, byte[] password2) throws IOException {
        this(Files.newByteChannel(fileName2.toPath(), EnumSet.of(StandardOpenOption.READ), new FileAttribute[0]), fileName2.getAbsolutePath(), password2, true, SevenZFileOptions.DEFAULT);
    }

    public SevenZFile(SeekableByteChannel channel2) throws IOException {
        this(channel2, SevenZFileOptions.DEFAULT);
    }

    public SevenZFile(SeekableByteChannel channel2, SevenZFileOptions options2) throws IOException {
        this(channel2, DEFAULT_FILE_NAME, (char[]) null, options2);
    }

    public SevenZFile(SeekableByteChannel channel2, char[] password2) throws IOException {
        this(channel2, password2, SevenZFileOptions.DEFAULT);
    }

    public SevenZFile(SeekableByteChannel channel2, char[] password2, SevenZFileOptions options2) throws IOException {
        this(channel2, DEFAULT_FILE_NAME, password2, options2);
    }

    public SevenZFile(SeekableByteChannel channel2, String fileName2, char[] password2) throws IOException {
        this(channel2, fileName2, password2, SevenZFileOptions.DEFAULT);
    }

    public SevenZFile(SeekableByteChannel channel2, String fileName2, char[] password2, SevenZFileOptions options2) throws IOException {
        this(channel2, fileName2, utf16Decode(password2), false, options2);
    }

    public SevenZFile(SeekableByteChannel channel2, String fileName2) throws IOException {
        this(channel2, fileName2, SevenZFileOptions.DEFAULT);
    }

    public SevenZFile(SeekableByteChannel channel2, String fileName2, SevenZFileOptions options2) throws IOException {
        this(channel2, fileName2, (byte[]) null, false, options2);
    }

    public SevenZFile(SeekableByteChannel channel2, byte[] password2) throws IOException {
        this(channel2, DEFAULT_FILE_NAME, password2);
    }

    public SevenZFile(SeekableByteChannel channel2, String fileName2, byte[] password2) throws IOException {
        this(channel2, fileName2, password2, false, SevenZFileOptions.DEFAULT);
    }

    private SevenZFile(SeekableByteChannel channel2, String filename, byte[] password2, boolean closeOnError, SevenZFileOptions options2) throws IOException {
        this.currentEntryIndex = -1;
        this.currentFolderIndex = -1;
        this.currentFolderInputStream = null;
        this.deferredBlockStreams = new ArrayList<>();
        boolean succeeded = false;
        this.channel = channel2;
        this.fileName = filename;
        this.options = options2;
        try {
            this.archive = readHeaders(password2);
            if (password2 != null) {
                this.password = Arrays.copyOf(password2, password2.length);
            } else {
                this.password = null;
            }
            succeeded = true;
        } finally {
            if (!succeeded && closeOnError) {
                this.channel.close();
            }
        }
    }

    public SevenZFile(File fileName2) throws IOException {
        this(fileName2, SevenZFileOptions.DEFAULT);
    }

    public SevenZFile(File fileName2, SevenZFileOptions options2) throws IOException {
        this(fileName2, (char[]) null, options2);
    }

    public void close() throws IOException {
        SeekableByteChannel seekableByteChannel = this.channel;
        if (seekableByteChannel != null) {
            try {
                seekableByteChannel.close();
            } finally {
                this.channel = null;
                byte[] bArr = this.password;
                if (bArr != null) {
                    Arrays.fill(bArr, (byte) 0);
                }
                this.password = null;
            }
        }
    }

    public SevenZArchiveEntry getNextEntry() throws IOException {
        if (this.currentEntryIndex >= this.archive.files.length - 1) {
            return null;
        }
        this.currentEntryIndex++;
        SevenZArchiveEntry entry = this.archive.files[this.currentEntryIndex];
        if (entry.getName() == null && this.options.getUseDefaultNameForUnnamedEntries()) {
            entry.setName(getDefaultName());
        }
        buildDecodingStream();
        this.compressedBytesReadFromCurrentEntry = 0;
        this.uncompressedBytesReadFromCurrentEntry = 0;
        return entry;
    }

    public Iterable<SevenZArchiveEntry> getEntries() {
        return Arrays.asList(this.archive.files);
    }

    private Archive readHeaders(byte[] password2) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
        readFully(buf);
        byte[] signature = new byte[6];
        buf.get(signature);
        if (Arrays.equals(signature, sevenZSignature)) {
            byte archiveVersionMajor = buf.get();
            byte archiveVersionMinor = buf.get();
            if (archiveVersionMajor == 0) {
                StartHeader startHeader = readStartHeader(4294967295L & ((long) buf.getInt()));
                assertFitsIntoInt("nextHeaderSize", startHeader.nextHeaderSize);
                int nextHeaderSizeInt = (int) startHeader.nextHeaderSize;
                this.channel.position(startHeader.nextHeaderOffset + 32);
                ByteBuffer buf2 = ByteBuffer.allocate(nextHeaderSizeInt).order(ByteOrder.LITTLE_ENDIAN);
                readFully(buf2);
                CRC32 crc = new CRC32();
                crc.update(buf2.array());
                if (startHeader.nextHeaderCrc == crc.getValue()) {
                    Archive archive2 = new Archive();
                    int nid = getUnsignedByte(buf2);
                    if (nid == 23) {
                        buf2 = readEncodedHeader(buf2, archive2, password2);
                        archive2 = new Archive();
                        nid = getUnsignedByte(buf2);
                    } else {
                        byte[] bArr = password2;
                    }
                    if (nid == 1) {
                        readHeader(buf2, archive2);
                        return archive2;
                    }
                    throw new IOException("Broken or unsupported archive: no Header");
                }
                byte[] bArr2 = password2;
                throw new IOException("NextHeader CRC mismatch");
            }
            byte[] bArr3 = password2;
            throw new IOException(String.format("Unsupported 7z version (%d,%d)", new Object[]{Byte.valueOf(archiveVersionMajor), Byte.valueOf(archiveVersionMinor)}));
        }
        byte[] bArr4 = password2;
        throw new IOException("Bad 7z signature");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x004f, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0050, code lost:
        r2.addSuppressed(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0054, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0045, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0049, code lost:
        if (r2 != null) goto L_0x004b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.apache.commons.compress.archivers.sevenz.StartHeader readStartHeader(long r10) throws java.io.IOException {
        /*
            r9 = this;
            org.apache.commons.compress.archivers.sevenz.StartHeader r0 = new org.apache.commons.compress.archivers.sevenz.StartHeader
            r0.<init>()
            java.io.DataInputStream r1 = new java.io.DataInputStream
            org.apache.commons.compress.utils.CRC32VerifyingInputStream r8 = new org.apache.commons.compress.utils.CRC32VerifyingInputStream
            org.apache.commons.compress.archivers.sevenz.BoundedSeekableByteChannelInputStream r3 = new org.apache.commons.compress.archivers.sevenz.BoundedSeekableByteChannelInputStream
            java.nio.channels.SeekableByteChannel r2 = r9.channel
            r4 = 20
            r3.<init>(r2, r4)
            r2 = r8
            r6 = r10
            r2.<init>((java.io.InputStream) r3, (long) r4, (long) r6)
            r1.<init>(r8)
            r2 = 0
            long r3 = r1.readLong()     // Catch:{ Throwable -> 0x0047 }
            long r3 = java.lang.Long.reverseBytes(r3)     // Catch:{ Throwable -> 0x0047 }
            r0.nextHeaderOffset = r3     // Catch:{ Throwable -> 0x0047 }
            long r3 = r1.readLong()     // Catch:{ Throwable -> 0x0047 }
            long r3 = java.lang.Long.reverseBytes(r3)     // Catch:{ Throwable -> 0x0047 }
            r0.nextHeaderSize = r3     // Catch:{ Throwable -> 0x0047 }
            r3 = 4294967295(0xffffffff, double:2.1219957905E-314)
            int r5 = r1.readInt()     // Catch:{ Throwable -> 0x0047 }
            int r5 = java.lang.Integer.reverseBytes(r5)     // Catch:{ Throwable -> 0x0047 }
            long r5 = (long) r5     // Catch:{ Throwable -> 0x0047 }
            long r3 = r3 & r5
            r0.nextHeaderCrc = r3     // Catch:{ Throwable -> 0x0047 }
            r1.close()
            return r0
        L_0x0045:
            r3 = move-exception
            goto L_0x0049
        L_0x0047:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0045 }
        L_0x0049:
            if (r2 == 0) goto L_0x0054
            r1.close()     // Catch:{ Throwable -> 0x004f }
            goto L_0x0057
        L_0x004f:
            r4 = move-exception
            r2.addSuppressed(r4)
            goto L_0x0057
        L_0x0054:
            r1.close()
        L_0x0057:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.sevenz.SevenZFile.readStartHeader(long):org.apache.commons.compress.archivers.sevenz.StartHeader");
    }

    private void readHeader(ByteBuffer header, Archive archive2) throws IOException {
        int nid = getUnsignedByte(header);
        if (nid == 2) {
            readArchiveProperties(header);
            nid = getUnsignedByte(header);
        }
        if (nid != 3) {
            if (nid == 4) {
                readStreamsInfo(header, archive2);
                nid = getUnsignedByte(header);
            }
            if (nid == 5) {
                readFilesInfo(header, archive2);
                nid = getUnsignedByte(header);
            }
            if (nid != 0) {
                throw new IOException("Badly terminated header, found " + nid);
            }
            return;
        }
        throw new IOException("Additional streams unsupported");
    }

    private void readArchiveProperties(ByteBuffer input) throws IOException {
        int nid = getUnsignedByte(input);
        while (nid != 0) {
            long propertySize = readUint64(input);
            assertFitsIntoInt("propertySize", propertySize);
            input.get(new byte[((int) propertySize)]);
            nid = getUnsignedByte(input);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: java.io.InputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: java.io.InputStream} */
    /* JADX WARNING: type inference failed for: r0v3 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.nio.ByteBuffer readEncodedHeader(java.nio.ByteBuffer r17, org.apache.commons.compress.archivers.sevenz.Archive r18, byte[] r19) throws java.io.IOException {
        /*
            r16 = this;
            r1 = r16
            r2 = r18
            r16.readStreamsInfo(r17, r18)
            org.apache.commons.compress.archivers.sevenz.Folder[] r0 = r2.folders
            r3 = 0
            r4 = r0[r3]
            r5 = 0
            long r6 = r2.packPos
            r8 = 32
            long r6 = r6 + r8
            r8 = 0
            long r6 = r6 + r8
            java.nio.channels.SeekableByteChannel r0 = r1.channel
            r0.position(r6)
            org.apache.commons.compress.archivers.sevenz.BoundedSeekableByteChannelInputStream r0 = new org.apache.commons.compress.archivers.sevenz.BoundedSeekableByteChannelInputStream
            java.nio.channels.SeekableByteChannel r8 = r1.channel
            long[] r9 = r2.packSizes
            r10 = r9[r3]
            r0.<init>(r8, r10)
            java.lang.Iterable r3 = r4.getOrderedCoders()
            java.util.Iterator r3 = r3.iterator()
        L_0x002d:
            boolean r8 = r3.hasNext()
            if (r8 == 0) goto L_0x0065
            java.lang.Object r8 = r3.next()
            r15 = r8
            org.apache.commons.compress.archivers.sevenz.Coder r15 = (org.apache.commons.compress.archivers.sevenz.Coder) r15
            long r8 = r15.numInStreams
            r10 = 1
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x005d
            long r8 = r15.numOutStreams
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x005d
            java.lang.String r8 = r1.fileName
            long r10 = r4.getUnpackSizeForCoder(r15)
            org.apache.commons.compress.archivers.sevenz.SevenZFileOptions r9 = r1.options
            int r14 = r9.getMaxMemoryLimitInKb()
            r9 = r0
            r12 = r15
            r13 = r19
            java.io.InputStream r0 = org.apache.commons.compress.archivers.sevenz.Coders.addDecoder(r8, r9, r10, r12, r13, r14)
            goto L_0x002d
        L_0x005d:
            java.io.IOException r3 = new java.io.IOException
            java.lang.String r8 = "Multi input/output stream coders are not yet supported"
            r3.<init>(r8)
            throw r3
        L_0x0065:
            boolean r3 = r4.hasCrc
            if (r3 == 0) goto L_0x0078
            org.apache.commons.compress.utils.CRC32VerifyingInputStream r3 = new org.apache.commons.compress.utils.CRC32VerifyingInputStream
            long r10 = r4.getUnpackSize()
            long r12 = r4.crc
            r8 = r3
            r9 = r0
            r8.<init>((java.io.InputStream) r9, (long) r10, (long) r12)
            r0 = r3
            goto L_0x0079
        L_0x0078:
            r3 = r0
        L_0x0079:
            long r8 = r4.getUnpackSize()
            java.lang.String r0 = "unpackSize"
            assertFitsIntoInt(r0, r8)
            long r8 = r4.getUnpackSize()
            int r0 = (int) r8
            byte[] r8 = new byte[r0]
            java.io.DataInputStream r0 = new java.io.DataInputStream
            r0.<init>(r3)
            r9 = r0
            r10 = 0
            r9.readFully(r8)     // Catch:{ Throwable -> 0x00a5 }
            r9.close()
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.wrap(r8)
            java.nio.ByteOrder r9 = java.nio.ByteOrder.LITTLE_ENDIAN
            java.nio.ByteBuffer r0 = r0.order(r9)
            return r0
        L_0x00a1:
            r0 = move-exception
            r11 = r10
            r10 = r0
            goto L_0x00a8
        L_0x00a5:
            r0 = move-exception
            r10 = r0
            throw r10     // Catch:{ all -> 0x00a1 }
        L_0x00a8:
            if (r11 == 0) goto L_0x00b4
            r9.close()     // Catch:{ Throwable -> 0x00ae }
            goto L_0x00b7
        L_0x00ae:
            r0 = move-exception
            r12 = r0
            r11.addSuppressed(r12)
            goto L_0x00b7
        L_0x00b4:
            r9.close()
        L_0x00b7:
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.sevenz.SevenZFile.readEncodedHeader(java.nio.ByteBuffer, org.apache.commons.compress.archivers.sevenz.Archive, byte[]):java.nio.ByteBuffer");
    }

    private void readStreamsInfo(ByteBuffer header, Archive archive2) throws IOException {
        int nid = getUnsignedByte(header);
        if (nid == 6) {
            readPackInfo(header, archive2);
            nid = getUnsignedByte(header);
        }
        if (nid == 7) {
            readUnpackInfo(header, archive2);
            nid = getUnsignedByte(header);
        } else {
            archive2.folders = new Folder[0];
        }
        if (nid == 8) {
            readSubStreamsInfo(header, archive2);
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated StreamsInfo");
        }
    }

    private void readPackInfo(ByteBuffer header, Archive archive2) throws IOException {
        archive2.packPos = readUint64(header);
        long numPackStreams = readUint64(header);
        assertFitsIntoInt("numPackStreams", numPackStreams);
        int numPackStreamsInt = (int) numPackStreams;
        int nid = getUnsignedByte(header);
        if (nid == 9) {
            archive2.packSizes = new long[numPackStreamsInt];
            for (int i = 0; i < archive2.packSizes.length; i++) {
                archive2.packSizes[i] = readUint64(header);
            }
            nid = getUnsignedByte(header);
        }
        if (nid == 10) {
            archive2.packCrcsDefined = readAllOrBits(header, numPackStreamsInt);
            archive2.packCrcs = new long[numPackStreamsInt];
            for (int i2 = 0; i2 < numPackStreamsInt; i2++) {
                if (archive2.packCrcsDefined.get(i2)) {
                    archive2.packCrcs[i2] = 4294967295L & ((long) header.getInt());
                }
            }
            nid = getUnsignedByte(header);
        }
        if (nid != 0) {
            throw new IOException("Badly terminated PackInfo (" + nid + ")");
        }
    }

    private void readUnpackInfo(ByteBuffer header, Archive archive2) throws IOException {
        int nid = getUnsignedByte(header);
        if (nid == 11) {
            long numFolders = readUint64(header);
            assertFitsIntoInt("numFolders", numFolders);
            int numFoldersInt = (int) numFolders;
            Folder[] folders = new Folder[numFoldersInt];
            archive2.folders = folders;
            if (getUnsignedByte(header) == 0) {
                for (int i = 0; i < numFoldersInt; i++) {
                    folders[i] = readFolder(header);
                }
                int nid2 = getUnsignedByte(header);
                if (nid2 == 12) {
                    for (Folder folder : folders) {
                        assertFitsIntoInt("totalOutputStreams", folder.totalOutputStreams);
                        folder.unpackSizes = new long[((int) folder.totalOutputStreams)];
                        for (int i2 = 0; ((long) i2) < folder.totalOutputStreams; i2++) {
                            folder.unpackSizes[i2] = readUint64(header);
                        }
                    }
                    int nid3 = getUnsignedByte(header);
                    if (nid3 == 10) {
                        BitSet crcsDefined = readAllOrBits(header, numFoldersInt);
                        int i3 = 0;
                        while (i3 < numFoldersInt) {
                            if (crcsDefined.get(i3)) {
                                folders[i3].hasCrc = true;
                                folders[i3].crc = ((long) header.getInt()) & 4294967295L;
                            } else {
                                folders[i3].hasCrc = false;
                            }
                            i3++;
                            ByteBuffer byteBuffer = header;
                        }
                        nid3 = getUnsignedByte(header);
                    }
                    if (nid3 != 0) {
                        throw new IOException("Badly terminated UnpackInfo");
                    }
                    return;
                }
                throw new IOException("Expected kCodersUnpackSize, got " + nid2);
            }
            throw new IOException("External unsupported");
        }
        Archive archive3 = archive2;
        throw new IOException("Expected kFolder, got " + nid);
    }

    private void readSubStreamsInfo(ByteBuffer header, Archive archive2) throws IOException {
        int nid;
        Archive archive3 = archive2;
        for (Folder folder : archive3.folders) {
            folder.numUnpackSubStreams = 1;
        }
        int totalUnpackStreams = archive3.folders.length;
        int nid2 = getUnsignedByte(header);
        if (nid2 == 13) {
            int totalUnpackStreams2 = 0;
            for (Folder folder2 : archive3.folders) {
                long numStreams = readUint64(header);
                assertFitsIntoInt("numStreams", numStreams);
                folder2.numUnpackSubStreams = (int) numStreams;
                totalUnpackStreams2 = (int) (((long) totalUnpackStreams2) + numStreams);
            }
            nid2 = getUnsignedByte(header);
            totalUnpackStreams = totalUnpackStreams2;
        }
        SubStreamsInfo subStreamsInfo = new SubStreamsInfo();
        subStreamsInfo.unpackSizes = new long[totalUnpackStreams];
        subStreamsInfo.hasCrc = new BitSet(totalUnpackStreams);
        subStreamsInfo.crcs = new long[totalUnpackStreams];
        int nextUnpackStream = 0;
        for (Folder folder3 : archive3.folders) {
            if (folder3.numUnpackSubStreams != 0) {
                long sum = 0;
                if (nid2 == 9) {
                    int i = 0;
                    while (i < folder3.numUnpackSubStreams - 1) {
                        long size = readUint64(header);
                        subStreamsInfo.unpackSizes[nextUnpackStream] = size;
                        sum += size;
                        i++;
                        nextUnpackStream++;
                    }
                }
                subStreamsInfo.unpackSizes[nextUnpackStream] = folder3.getUnpackSize() - sum;
                nextUnpackStream++;
            }
        }
        if (nid2 == 9) {
            nid2 = getUnsignedByte(header);
        }
        int numDigests = 0;
        for (Folder folder4 : archive3.folders) {
            if (folder4.numUnpackSubStreams != 1 || !folder4.hasCrc) {
                numDigests += folder4.numUnpackSubStreams;
            }
        }
        if (nid2 == 10) {
            BitSet hasMissingCrc = readAllOrBits(header, numDigests);
            long[] missingCrcs = new long[numDigests];
            for (int i2 = 0; i2 < numDigests; i2++) {
                if (hasMissingCrc.get(i2)) {
                    missingCrcs[i2] = 4294967295L & ((long) header.getInt());
                }
            }
            Folder[] folderArr = archive3.folders;
            int length = folderArr.length;
            int nextMissingCrc = 0;
            int nextCrc = 0;
            int nextCrc2 = 0;
            while (nextCrc2 < length) {
                Folder folder5 = folderArr[nextCrc2];
                int totalUnpackStreams3 = totalUnpackStreams;
                int nid3 = nid2;
                if (folder5.numUnpackSubStreams != 1 || !folder5.hasCrc) {
                    for (int i3 = 0; i3 < folder5.numUnpackSubStreams; i3++) {
                        subStreamsInfo.hasCrc.set(nextCrc, hasMissingCrc.get(nextMissingCrc));
                        subStreamsInfo.crcs[nextCrc] = missingCrcs[nextMissingCrc];
                        nextCrc++;
                        nextMissingCrc++;
                    }
                } else {
                    subStreamsInfo.hasCrc.set(nextCrc, true);
                    subStreamsInfo.crcs[nextCrc] = folder5.crc;
                    nextCrc++;
                }
                nextCrc2++;
                totalUnpackStreams = totalUnpackStreams3;
                nid2 = nid3;
            }
            int i4 = nid2;
            nid = getUnsignedByte(header);
        } else {
            ByteBuffer byteBuffer = header;
            int i5 = totalUnpackStreams;
            nid = nid2;
        }
        if (nid == 0) {
            archive3.subStreamsInfo = subStreamsInfo;
            return;
        }
        throw new IOException("Badly terminated SubStreamsInfo");
    }

    private Folder readFolder(ByteBuffer header) throws IOException {
        long numCoders;
        ByteBuffer byteBuffer = header;
        Folder folder = new Folder();
        long numCoders2 = readUint64(header);
        assertFitsIntoInt("numCoders", numCoders2);
        Coder[] coders = new Coder[((int) numCoders2)];
        long totalInStreams = 0;
        long totalOutStreams = 0;
        int i = 0;
        while (i < coders.length) {
            coders[i] = new Coder();
            int bits = getUnsignedByte(header);
            int idSize = bits & 15;
            boolean z = true;
            boolean isSimple = (bits & 16) == 0;
            boolean hasAttributes = (bits & 32) != 0;
            if ((bits & 128) == 0) {
                z = false;
            }
            boolean moreAlternativeMethods = z;
            coders[i].decompressionMethodId = new byte[idSize];
            byteBuffer.get(coders[i].decompressionMethodId);
            if (isSimple) {
                numCoders = numCoders2;
                coders[i].numInStreams = 1;
                coders[i].numOutStreams = 1;
            } else {
                numCoders = numCoders2;
                coders[i].numInStreams = readUint64(header);
                coders[i].numOutStreams = readUint64(header);
            }
            totalInStreams += coders[i].numInStreams;
            totalOutStreams += coders[i].numOutStreams;
            if (hasAttributes) {
                long propertiesSize = readUint64(header);
                assertFitsIntoInt("propertiesSize", propertiesSize);
                coders[i].properties = new byte[((int) propertiesSize)];
                byteBuffer.get(coders[i].properties);
            }
            if (!moreAlternativeMethods) {
                i++;
                numCoders2 = numCoders;
            } else {
                throw new IOException("Alternative methods are unsupported, please report. The reference implementation doesn't support them either.");
            }
        }
        folder.coders = coders;
        assertFitsIntoInt("totalInStreams", totalInStreams);
        folder.totalInputStreams = totalInStreams;
        assertFitsIntoInt("totalOutStreams", totalOutStreams);
        folder.totalOutputStreams = totalOutStreams;
        if (totalOutStreams != 0) {
            long numBindPairs = totalOutStreams - 1;
            assertFitsIntoInt("numBindPairs", numBindPairs);
            BindPair[] bindPairs = new BindPair[((int) numBindPairs)];
            for (int i2 = 0; i2 < bindPairs.length; i2++) {
                bindPairs[i2] = new BindPair();
                bindPairs[i2].inIndex = readUint64(header);
                bindPairs[i2].outIndex = readUint64(header);
            }
            folder.bindPairs = bindPairs;
            if (totalInStreams >= numBindPairs) {
                long numPackedStreams = totalInStreams - numBindPairs;
                assertFitsIntoInt("numPackedStreams", numPackedStreams);
                long[] packedStreams = new long[((int) numPackedStreams)];
                if (numPackedStreams == 1) {
                    int i3 = 0;
                    while (i3 < ((int) totalInStreams) && folder.findBindPairForInStream(i3) >= 0) {
                        i3++;
                    }
                    if (i3 != ((int) totalInStreams)) {
                        packedStreams[0] = (long) i3;
                    } else {
                        throw new IOException("Couldn't find stream's bind pair index");
                    }
                } else {
                    for (int i4 = 0; i4 < ((int) numPackedStreams); i4++) {
                        packedStreams[i4] = readUint64(header);
                    }
                }
                folder.packedStreams = packedStreams;
                return folder;
            }
            throw new IOException("Total input streams can't be less than the number of bind pairs");
        }
        throw new IOException("Total output streams can't be 0");
    }

    private BitSet readAllOrBits(ByteBuffer header, int size) throws IOException {
        if (getUnsignedByte(header) == 0) {
            return readBits(header, size);
        }
        BitSet bits = new BitSet(size);
        for (int i = 0; i < size; i++) {
            bits.set(i, true);
        }
        return bits;
    }

    private BitSet readBits(ByteBuffer header, int size) throws IOException {
        BitSet bits = new BitSet(size);
        int mask = 0;
        int cache = 0;
        for (int i = 0; i < size; i++) {
            if (mask == 0) {
                mask = 128;
                cache = getUnsignedByte(header);
            }
            bits.set(i, (cache & mask) != 0);
            mask >>>= 1;
        }
        return bits;
    }

    private void readFilesInfo(ByteBuffer header, Archive archive2) throws IOException {
        long numFiles;
        BitSet isAnti;
        BitSet isEmptyFile;
        BitSet isAnti2;
        BitSet isEmptyFile2;
        int external;
        long numFiles2;
        BitSet timesDefined;
        ByteBuffer byteBuffer = header;
        Archive archive3 = archive2;
        long numFiles3 = readUint64(header);
        assertFitsIntoInt("numFiles", numFiles3);
        SevenZArchiveEntry[] files = new SevenZArchiveEntry[((int) numFiles3)];
        for (int i = 0; i < files.length; i++) {
            files[i] = new SevenZArchiveEntry();
        }
        BitSet isEmptyStream = null;
        BitSet isEmptyFile3 = null;
        BitSet isAnti3 = null;
        while (true) {
            int propertyType = getUnsignedByte(header);
            if (propertyType == 0) {
                int nonEmptyFileCounter = 0;
                int emptyFileCounter = 0;
                for (int i2 = 0; i2 < files.length; i2++) {
                    files[i2].setHasStream(isEmptyStream == null || !isEmptyStream.get(i2));
                    if (files[i2].hasStream()) {
                        files[i2].setDirectory(false);
                        files[i2].setAntiItem(false);
                        files[i2].setHasCrc(archive3.subStreamsInfo.hasCrc.get(nonEmptyFileCounter));
                        files[i2].setCrcValue(archive3.subStreamsInfo.crcs[nonEmptyFileCounter]);
                        files[i2].setSize(archive3.subStreamsInfo.unpackSizes[nonEmptyFileCounter]);
                        nonEmptyFileCounter++;
                    } else {
                        files[i2].setDirectory(isEmptyFile3 == null || !isEmptyFile3.get(emptyFileCounter));
                        files[i2].setAntiItem(isAnti3 != null && isAnti3.get(emptyFileCounter));
                        files[i2].setHasCrc(false);
                        files[i2].setSize(0);
                        emptyFileCounter++;
                    }
                }
                archive3.files = files;
                calculateStreamMap(archive3);
                return;
            }
            long size = readUint64(header);
            switch (propertyType) {
                case 14:
                    numFiles = numFiles3;
                    BitSet bitSet = isEmptyFile3;
                    BitSet bitSet2 = isAnti3;
                    isEmptyStream = readBits(byteBuffer, files.length);
                    continue;
                case 15:
                    numFiles = numFiles3;
                    BitSet bitSet3 = isEmptyFile3;
                    BitSet isAnti4 = isAnti3;
                    if (isEmptyStream != null) {
                        isEmptyFile3 = readBits(byteBuffer, isEmptyStream.cardinality());
                        isAnti3 = isAnti4;
                        continue;
                    } else {
                        throw new IOException("Header format error: kEmptyStream must appear before kEmptyFile");
                    }
                case 16:
                    numFiles = numFiles3;
                    BitSet isEmptyFile4 = isEmptyFile3;
                    BitSet bitSet4 = isAnti3;
                    if (isEmptyStream != null) {
                        isAnti3 = readBits(byteBuffer, isEmptyStream.cardinality());
                        isEmptyFile3 = isEmptyFile4;
                        continue;
                    } else {
                        throw new IOException("Header format error: kEmptyStream must appear before kAnti");
                    }
                case 17:
                    numFiles = numFiles3;
                    int external2 = getUnsignedByte(header);
                    if (external2 != 0) {
                        BitSet bitSet5 = isEmptyFile3;
                        BitSet bitSet6 = isAnti3;
                        throw new IOException("Not implemented");
                    } else if (((size - 1) & 1) == 0) {
                        assertFitsIntoInt("file names length", size - 1);
                        byte[] names = new byte[((int) (size - 1))];
                        byteBuffer.get(names);
                        int nextFile = 0;
                        int nextName = 0;
                        int i3 = 0;
                        while (i3 < names.length) {
                            if (names[i3] == 0 && names[i3 + 1] == 0) {
                                external = external2;
                                isEmptyFile2 = isEmptyFile3;
                                isAnti2 = isAnti3;
                                files[nextFile].setName(new String(names, nextName, i3 - nextName, CharsetNames.UTF_16LE));
                                nextName = i3 + 2;
                                nextFile++;
                            } else {
                                external = external2;
                                isEmptyFile2 = isEmptyFile3;
                                isAnti2 = isAnti3;
                            }
                            i3 += 2;
                            external2 = external;
                            isEmptyFile3 = isEmptyFile2;
                            isAnti3 = isAnti2;
                        }
                        isEmptyFile = isEmptyFile3;
                        isAnti = isAnti3;
                        if (!(nextName == names.length && nextFile == files.length)) {
                            break;
                        }
                    } else {
                        BitSet bitSet7 = isEmptyFile3;
                        BitSet bitSet8 = isAnti3;
                        throw new IOException("File names length invalid");
                    }
                    break;
                case 18:
                    numFiles = numFiles3;
                    BitSet timesDefined2 = readAllOrBits(byteBuffer, files.length);
                    if (getUnsignedByte(header) == 0) {
                        for (int i4 = 0; i4 < files.length; i4++) {
                            files[i4].setHasCreationDate(timesDefined2.get(i4));
                            if (files[i4].getHasCreationDate()) {
                                files[i4].setCreationDate(header.getLong());
                            }
                        }
                        isEmptyFile = isEmptyFile3;
                        isAnti = isAnti3;
                        break;
                    } else {
                        throw new IOException("Unimplemented");
                    }
                case 19:
                    numFiles = numFiles3;
                    BitSet timesDefined3 = readAllOrBits(byteBuffer, files.length);
                    if (getUnsignedByte(header) == 0) {
                        for (int i5 = 0; i5 < files.length; i5++) {
                            files[i5].setHasAccessDate(timesDefined3.get(i5));
                            if (files[i5].getHasAccessDate()) {
                                files[i5].setAccessDate(header.getLong());
                            }
                        }
                        isEmptyFile = isEmptyFile3;
                        isAnti = isAnti3;
                        break;
                    } else {
                        throw new IOException("Unimplemented");
                    }
                case 20:
                    BitSet timesDefined4 = readAllOrBits(byteBuffer, files.length);
                    if (getUnsignedByte(header) == 0) {
                        int i6 = 0;
                        while (i6 < files.length) {
                            files[i6].setHasLastModifiedDate(timesDefined4.get(i6));
                            if (files[i6].getHasLastModifiedDate()) {
                                numFiles2 = numFiles3;
                                timesDefined = timesDefined4;
                                files[i6].setLastModifiedDate(header.getLong());
                            } else {
                                numFiles2 = numFiles3;
                                timesDefined = timesDefined4;
                            }
                            i6++;
                            timesDefined4 = timesDefined;
                            numFiles3 = numFiles2;
                        }
                        numFiles = numFiles3;
                        BitSet bitSet9 = timesDefined4;
                        isEmptyFile = isEmptyFile3;
                        isAnti = isAnti3;
                        break;
                    } else {
                        BitSet bitSet10 = timesDefined4;
                        throw new IOException("Unimplemented");
                    }
                case 21:
                    BitSet attributesDefined = readAllOrBits(byteBuffer, files.length);
                    if (getUnsignedByte(header) == 0) {
                        int i7 = 0;
                        while (i7 < files.length) {
                            files[i7].setHasWindowsAttributes(attributesDefined.get(i7));
                            if (files[i7].getHasWindowsAttributes()) {
                                files[i7].setWindowsAttributes(header.getInt());
                            }
                            i7++;
                            Archive archive4 = archive2;
                        }
                        numFiles = numFiles3;
                        isEmptyFile = isEmptyFile3;
                        isAnti = isAnti3;
                        break;
                    } else {
                        throw new IOException("Unimplemented");
                    }
                case 24:
                    throw new IOException("kStartPos is unsupported, please report");
                case 25:
                    if (skipBytesFully(byteBuffer, size) >= size) {
                        numFiles = numFiles3;
                        isEmptyFile = isEmptyFile3;
                        isAnti = isAnti3;
                        break;
                    } else {
                        throw new IOException("Incomplete kDummy property");
                    }
                default:
                    numFiles = numFiles3;
                    isEmptyFile = isEmptyFile3;
                    isAnti = isAnti3;
                    if (skipBytesFully(byteBuffer, size) < size) {
                        throw new IOException("Incomplete property of type " + propertyType);
                    }
                    break;
            }
            isEmptyFile3 = isEmptyFile;
            isAnti3 = isAnti;
            archive3 = archive2;
            numFiles3 = numFiles;
        }
        throw new IOException("Error parsing file names");
    }

    private void calculateStreamMap(Archive archive2) throws IOException {
        StreamMap streamMap = new StreamMap();
        int nextFolderPackStreamIndex = 0;
        int numPackSizes = 0;
        int numFolders = archive2.folders != null ? archive2.folders.length : 0;
        streamMap.folderFirstPackStreamIndex = new int[numFolders];
        for (int i = 0; i < numFolders; i++) {
            streamMap.folderFirstPackStreamIndex[i] = nextFolderPackStreamIndex;
            nextFolderPackStreamIndex += archive2.folders[i].packedStreams.length;
        }
        long nextPackStreamOffset = 0;
        if (archive2.packSizes != null) {
            numPackSizes = archive2.packSizes.length;
        }
        streamMap.packStreamOffsets = new long[numPackSizes];
        for (int i2 = 0; i2 < numPackSizes; i2++) {
            streamMap.packStreamOffsets[i2] = nextPackStreamOffset;
            nextPackStreamOffset += archive2.packSizes[i2];
        }
        streamMap.folderFirstFileIndex = new int[numFolders];
        streamMap.fileFolderIndex = new int[archive2.files.length];
        int nextFolderIndex = 0;
        int nextFolderUnpackStreamIndex = 0;
        for (int i3 = 0; i3 < archive2.files.length; i3++) {
            if (archive2.files[i3].hasStream() || nextFolderUnpackStreamIndex != 0) {
                if (nextFolderUnpackStreamIndex == 0) {
                    while (nextFolderIndex < archive2.folders.length) {
                        streamMap.folderFirstFileIndex[nextFolderIndex] = i3;
                        if (archive2.folders[nextFolderIndex].numUnpackSubStreams > 0) {
                            break;
                        }
                        nextFolderIndex++;
                    }
                    if (nextFolderIndex >= archive2.folders.length) {
                        throw new IOException("Too few folders in archive");
                    }
                }
                streamMap.fileFolderIndex[i3] = nextFolderIndex;
                if (archive2.files[i3].hasStream() && (nextFolderUnpackStreamIndex = nextFolderUnpackStreamIndex + 1) >= archive2.folders[nextFolderIndex].numUnpackSubStreams) {
                    nextFolderIndex++;
                    nextFolderUnpackStreamIndex = 0;
                }
            } else {
                streamMap.fileFolderIndex[i3] = -1;
            }
        }
        archive2.streamMap = streamMap;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v0, resolved type: org.apache.commons.compress.utils.BoundedInputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: org.apache.commons.compress.utils.CRC32VerifyingInputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v1, resolved type: org.apache.commons.compress.utils.CRC32VerifyingInputStream} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void buildDecodingStream() throws java.io.IOException {
        /*
            r19 = this;
            r6 = r19
            org.apache.commons.compress.archivers.sevenz.Archive r0 = r6.archive
            org.apache.commons.compress.archivers.sevenz.StreamMap r0 = r0.streamMap
            int[] r0 = r0.fileFolderIndex
            int r1 = r6.currentEntryIndex
            r7 = r0[r1]
            if (r7 >= 0) goto L_0x0014
            java.util.ArrayList<java.io.InputStream> r0 = r6.deferredBlockStreams
            r0.clear()
            return
        L_0x0014:
            org.apache.commons.compress.archivers.sevenz.Archive r0 = r6.archive
            org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry[] r0 = r0.files
            int r1 = r6.currentEntryIndex
            r8 = r0[r1]
            int r0 = r6.currentFolderIndex
            if (r0 != r7) goto L_0x0032
            org.apache.commons.compress.archivers.sevenz.Archive r0 = r6.archive
            org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry[] r0 = r0.files
            int r1 = r6.currentEntryIndex
            int r1 = r1 + -1
            r0 = r0[r1]
            java.lang.Iterable r0 = r0.getContentMethods()
            r8.setContentMethods(r0)
            goto L_0x006e
        L_0x0032:
            r6.currentFolderIndex = r7
            java.util.ArrayList<java.io.InputStream> r0 = r6.deferredBlockStreams
            r0.clear()
            java.io.InputStream r0 = r6.currentFolderInputStream
            if (r0 == 0) goto L_0x0043
            r0.close()
            r0 = 0
            r6.currentFolderInputStream = r0
        L_0x0043:
            org.apache.commons.compress.archivers.sevenz.Archive r0 = r6.archive
            org.apache.commons.compress.archivers.sevenz.Folder[] r0 = r0.folders
            r9 = r0[r7]
            org.apache.commons.compress.archivers.sevenz.Archive r0 = r6.archive
            org.apache.commons.compress.archivers.sevenz.StreamMap r0 = r0.streamMap
            int[] r0 = r0.folderFirstPackStreamIndex
            r10 = r0[r7]
            r0 = 32
            org.apache.commons.compress.archivers.sevenz.Archive r2 = r6.archive
            long r2 = r2.packPos
            long r2 = r2 + r0
            org.apache.commons.compress.archivers.sevenz.Archive r0 = r6.archive
            org.apache.commons.compress.archivers.sevenz.StreamMap r0 = r0.streamMap
            long[] r0 = r0.packStreamOffsets
            r4 = r0[r10]
            long r11 = r2 + r4
            r0 = r19
            r1 = r9
            r2 = r11
            r4 = r10
            r5 = r8
            java.io.InputStream r0 = r0.buildDecoderStack(r1, r2, r4, r5)
            r6.currentFolderInputStream = r0
        L_0x006e:
            org.apache.commons.compress.utils.BoundedInputStream r14 = new org.apache.commons.compress.utils.BoundedInputStream
            java.io.InputStream r0 = r6.currentFolderInputStream
            long r1 = r8.getSize()
            r14.<init>(r0, r1)
            boolean r0 = r8.getHasCrc()
            if (r0 == 0) goto L_0x008e
            org.apache.commons.compress.utils.CRC32VerifyingInputStream r0 = new org.apache.commons.compress.utils.CRC32VerifyingInputStream
            long r15 = r8.getSize()
            long r17 = r8.getCrcValue()
            r13 = r0
            r13.<init>((java.io.InputStream) r14, (long) r15, (long) r17)
            goto L_0x008f
        L_0x008e:
            r0 = r14
        L_0x008f:
            java.util.ArrayList<java.io.InputStream> r1 = r6.deferredBlockStreams
            r1.add(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.sevenz.SevenZFile.buildDecodingStream():void");
    }

    private InputStream buildDecoderStack(Folder folder, long folderOffset, int firstPackStreamIndex, SevenZArchiveEntry entry) throws IOException {
        Folder folder2 = folder;
        this.channel.position(folderOffset);
        InputStream inputStreamStack = new FilterInputStream(new BufferedInputStream(new BoundedSeekableByteChannelInputStream(this.channel, this.archive.packSizes[firstPackStreamIndex]))) {
            public int read() throws IOException {
                int r = this.in.read();
                if (r >= 0) {
                    count(1);
                }
                return r;
            }

            public int read(byte[] b) throws IOException {
                return read(b, 0, b.length);
            }

            public int read(byte[] b, int off, int len) throws IOException {
                int r = this.in.read(b, off, len);
                if (r >= 0) {
                    count(r);
                }
                return r;
            }

            private void count(int c) {
                SevenZFile sevenZFile = SevenZFile.this;
                long unused = sevenZFile.compressedBytesReadFromCurrentEntry = sevenZFile.compressedBytesReadFromCurrentEntry + ((long) c);
            }
        };
        LinkedList<SevenZMethodConfiguration> methods = new LinkedList<>();
        for (Coder coder : folder.getOrderedCoders()) {
            if (coder.numInStreams == 1 && coder.numOutStreams == 1) {
                SevenZMethod method = SevenZMethod.byId(coder.decompressionMethodId);
                inputStreamStack = Coders.addDecoder(this.fileName, inputStreamStack, folder2.getUnpackSizeForCoder(coder), coder, this.password, this.options.getMaxMemoryLimitInKb());
                methods.addFirst(new SevenZMethodConfiguration(method, Coders.findByMethod(method).getOptionsFromCoder(coder, inputStreamStack)));
            } else {
                throw new IOException("Multi input/output stream coders are not yet supported");
            }
        }
        entry.setContentMethods(methods);
        if (!folder2.hasCrc) {
            return inputStreamStack;
        }
        return new CRC32VerifyingInputStream(inputStreamStack, folder.getUnpackSize(), folder2.crc);
    }

    public int read() throws IOException {
        int b = getCurrentStream().read();
        if (b >= 0) {
            this.uncompressedBytesReadFromCurrentEntry++;
        }
        return b;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0045, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0049, code lost:
        if (r0 != null) goto L_0x004b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004b, code lost:
        if (r1 != null) goto L_0x004d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0051, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0052, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0056, code lost:
        r0.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.io.InputStream getCurrentStream() throws java.io.IOException {
        /*
            r7 = this;
            org.apache.commons.compress.archivers.sevenz.Archive r0 = r7.archive
            org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry[] r0 = r0.files
            int r1 = r7.currentEntryIndex
            r0 = r0[r1]
            long r0 = r0.getSize()
            r2 = 0
            r4 = 0
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 != 0) goto L_0x001b
            java.io.ByteArrayInputStream r0 = new java.io.ByteArrayInputStream
            byte[] r1 = new byte[r4]
            r0.<init>(r1)
            return r0
        L_0x001b:
            java.util.ArrayList<java.io.InputStream> r0 = r7.deferredBlockStreams
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0063
        L_0x0023:
            java.util.ArrayList<java.io.InputStream> r0 = r7.deferredBlockStreams
            int r0 = r0.size()
            r1 = 1
            if (r0 <= r1) goto L_0x005a
            java.util.ArrayList<java.io.InputStream> r0 = r7.deferredBlockStreams
            java.lang.Object r0 = r0.remove(r4)
            java.io.InputStream r0 = (java.io.InputStream) r0
            r1 = 0
            r5 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            org.apache.commons.compress.utils.IOUtils.skip(r0, r5)     // Catch:{ Throwable -> 0x0047 }
            if (r0 == 0) goto L_0x0042
            r0.close()
        L_0x0042:
            r7.compressedBytesReadFromCurrentEntry = r2
            goto L_0x0023
        L_0x0045:
            r2 = move-exception
            goto L_0x0049
        L_0x0047:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0045 }
        L_0x0049:
            if (r0 == 0) goto L_0x0059
            if (r1 == 0) goto L_0x0056
            r0.close()     // Catch:{ Throwable -> 0x0051 }
            goto L_0x0059
        L_0x0051:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x0059
        L_0x0056:
            r0.close()
        L_0x0059:
            throw r2
        L_0x005a:
            java.util.ArrayList<java.io.InputStream> r0 = r7.deferredBlockStreams
            java.lang.Object r0 = r0.get(r4)
            java.io.InputStream r0 = (java.io.InputStream) r0
            return r0
        L_0x0063:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "No current 7z entry (call getNextEntry() first)."
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.sevenz.SevenZFile.getCurrentStream():java.io.InputStream");
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int cnt = getCurrentStream().read(b, off, len);
        if (cnt > 0) {
            this.uncompressedBytesReadFromCurrentEntry += (long) cnt;
        }
        return cnt;
    }

    public InputStreamStatistics getStatisticsForCurrentEntry() {
        return new InputStreamStatistics() {
            public long getCompressedCount() {
                return SevenZFile.this.compressedBytesReadFromCurrentEntry;
            }

            public long getUncompressedCount() {
                return SevenZFile.this.uncompressedBytesReadFromCurrentEntry;
            }
        };
    }

    private static long readUint64(ByteBuffer in) throws IOException {
        long firstByte = (long) getUnsignedByte(in);
        int mask = 128;
        long value = 0;
        for (int i = 0; i < 8; i++) {
            if ((((long) mask) & firstByte) == 0) {
                return ((((long) (mask - 1)) & firstByte) << (i * 8)) | value;
            }
            value |= ((long) getUnsignedByte(in)) << (i * 8);
            mask >>>= 1;
        }
        return value;
    }

    private static int getUnsignedByte(ByteBuffer buf) {
        return buf.get() & 255;
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < sevenZSignature.length) {
            return false;
        }
        int i = 0;
        while (true) {
            byte[] bArr = sevenZSignature;
            if (i >= bArr.length) {
                return true;
            }
            if (signature[i] != bArr[i]) {
                return false;
            }
            i++;
        }
    }

    private static long skipBytesFully(ByteBuffer input, long bytesToSkip) throws IOException {
        if (bytesToSkip < 1) {
            return 0;
        }
        int current = input.position();
        int maxSkip = input.remaining();
        if (((long) maxSkip) < bytesToSkip) {
            bytesToSkip = (long) maxSkip;
        }
        input.position(((int) bytesToSkip) + current);
        return bytesToSkip;
    }

    private void readFully(ByteBuffer buf) throws IOException {
        buf.rewind();
        IOUtils.readFully((ReadableByteChannel) this.channel, buf);
        buf.flip();
    }

    public String toString() {
        return this.archive.toString();
    }

    public String getDefaultName() {
        String str;
        if (DEFAULT_FILE_NAME.equals(this.fileName) || (str = this.fileName) == null) {
            return null;
        }
        String lastSegment = new File(str).getName();
        int dotPos = lastSegment.lastIndexOf(".");
        if (dotPos > 0) {
            return lastSegment.substring(0, dotPos);
        }
        return lastSegment + "~";
    }

    private static byte[] utf16Decode(char[] chars) throws IOException {
        if (chars == null) {
            return null;
        }
        ByteBuffer encoded = PASSWORD_ENCODER.encode(CharBuffer.wrap(chars));
        if (encoded.hasArray()) {
            return encoded.array();
        }
        byte[] e = new byte[encoded.remaining()];
        encoded.get(e);
        return e;
    }

    private static void assertFitsIntoInt(String what, long value) throws IOException {
        if (value > 2147483647L) {
            throw new IOException("Cannot handle " + what + value);
        }
    }
}

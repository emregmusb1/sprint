package org.apache.commons.compress.archivers.arj;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.CRC32;
import kotlin.jvm.internal.LongCompanionObject;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class ArjArchiveInputStream extends ArchiveInputStream {
    private static final int ARJ_MAGIC_1 = 96;
    private static final int ARJ_MAGIC_2 = 234;
    private final String charsetName;
    private InputStream currentInputStream;
    private LocalFileHeader currentLocalFileHeader;
    private final DataInputStream in;
    private final MainHeader mainHeader;

    public ArjArchiveInputStream(InputStream inputStream, String charsetName2) throws ArchiveException {
        this.currentLocalFileHeader = null;
        this.currentInputStream = null;
        this.in = new DataInputStream(inputStream);
        this.charsetName = charsetName2;
        try {
            this.mainHeader = readMainHeader();
            if ((this.mainHeader.arjFlags & 1) != 0) {
                throw new ArchiveException("Encrypted ARJ files are unsupported");
            } else if ((this.mainHeader.arjFlags & 4) != 0) {
                throw new ArchiveException("Multi-volume ARJ files are unsupported");
            }
        } catch (IOException ioException) {
            throw new ArchiveException(ioException.getMessage(), ioException);
        }
    }

    public ArjArchiveInputStream(InputStream inputStream) throws ArchiveException {
        this(inputStream, "CP437");
    }

    public void close() throws IOException {
        this.in.close();
    }

    private int read8(DataInputStream dataIn) throws IOException {
        int value = dataIn.readUnsignedByte();
        count(1);
        return value;
    }

    private int read16(DataInputStream dataIn) throws IOException {
        int value = dataIn.readUnsignedShort();
        count(2);
        return Integer.reverseBytes(value) >>> 16;
    }

    private int read32(DataInputStream dataIn) throws IOException {
        int value = dataIn.readInt();
        count(4);
        return Integer.reverseBytes(value);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0023, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r2 = new java.lang.String(r0.toByteArray());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002d, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0030, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0031, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0035, code lost:
        if (r1 != null) goto L_0x0037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003b, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003c, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0040, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0013, code lost:
        if (r6.charsetName == null) goto L_0x0024;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0015, code lost:
        r2 = new java.lang.String(r0.toByteArray(), r6.charsetName);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0020, code lost:
        r0.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String readString(java.io.DataInputStream r7) throws java.io.IOException {
        /*
            r6 = this;
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream
            r0.<init>()
        L_0x0005:
            r1 = 0
            int r2 = r7.readUnsignedByte()     // Catch:{ Throwable -> 0x0033 }
            r3 = r2
            if (r2 == 0) goto L_0x0011
            r0.write(r3)     // Catch:{ Throwable -> 0x0033 }
            goto L_0x0005
        L_0x0011:
            java.lang.String r2 = r6.charsetName     // Catch:{ Throwable -> 0x0033 }
            if (r2 == 0) goto L_0x0024
            java.lang.String r2 = new java.lang.String     // Catch:{ Throwable -> 0x0033 }
            byte[] r4 = r0.toByteArray()     // Catch:{ Throwable -> 0x0033 }
            java.lang.String r5 = r6.charsetName     // Catch:{ Throwable -> 0x0033 }
            r2.<init>(r4, r5)     // Catch:{ Throwable -> 0x0033 }
            r0.close()
            return r2
        L_0x0024:
            java.lang.String r2 = new java.lang.String     // Catch:{ Throwable -> 0x0033 }
            byte[] r4 = r0.toByteArray()     // Catch:{ Throwable -> 0x0033 }
            r2.<init>(r4)     // Catch:{ Throwable -> 0x0033 }
            r0.close()
            return r2
        L_0x0031:
            r2 = move-exception
            goto L_0x0035
        L_0x0033:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0031 }
        L_0x0035:
            if (r1 == 0) goto L_0x0040
            r0.close()     // Catch:{ Throwable -> 0x003b }
            goto L_0x0043
        L_0x003b:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x0043
        L_0x0040:
            r0.close()
        L_0x0043:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.arj.ArjArchiveInputStream.readString(java.io.DataInputStream):java.lang.String");
    }

    private void readFully(DataInputStream dataIn, byte[] b) throws IOException {
        dataIn.readFully(b);
        count(b.length);
    }

    private byte[] readHeader() throws IOException {
        boolean found = false;
        byte[] basicHeaderBytes = null;
        do {
            int second = read8(this.in);
            do {
                int first = second;
                second = read8(this.in);
                if (first == 96 || second == ARJ_MAGIC_2) {
                    int basicHeaderSize = read16(this.in);
                }
                int first2 = second;
                second = read8(this.in);
                break;
            } while (second == ARJ_MAGIC_2);
            int basicHeaderSize2 = read16(this.in);
            if (basicHeaderSize2 == 0) {
                return null;
            }
            if (basicHeaderSize2 <= 2600) {
                basicHeaderBytes = new byte[basicHeaderSize2];
                readFully(this.in, basicHeaderBytes);
                CRC32 crc32 = new CRC32();
                crc32.update(basicHeaderBytes);
                if ((((long) read32(this.in)) & 4294967295L) == crc32.getValue()) {
                    found = true;
                    continue;
                } else {
                    continue;
                }
            }
        } while (!found);
        return basicHeaderBytes;
    }

    private MainHeader readMainHeader() throws IOException {
        byte[] basicHeaderBytes = readHeader();
        if (basicHeaderBytes != null) {
            DataInputStream basicHeader = new DataInputStream(new ByteArrayInputStream(basicHeaderBytes));
            int firstHeaderSize = basicHeader.readUnsignedByte();
            byte[] firstHeaderBytes = new byte[(firstHeaderSize - 1)];
            basicHeader.readFully(firstHeaderBytes);
            DataInputStream firstHeader = new DataInputStream(new ByteArrayInputStream(firstHeaderBytes));
            MainHeader hdr = new MainHeader();
            hdr.archiverVersionNumber = firstHeader.readUnsignedByte();
            hdr.minVersionToExtract = firstHeader.readUnsignedByte();
            hdr.hostOS = firstHeader.readUnsignedByte();
            hdr.arjFlags = firstHeader.readUnsignedByte();
            hdr.securityVersion = firstHeader.readUnsignedByte();
            hdr.fileType = firstHeader.readUnsignedByte();
            hdr.reserved = firstHeader.readUnsignedByte();
            hdr.dateTimeCreated = read32(firstHeader);
            hdr.dateTimeModified = read32(firstHeader);
            hdr.archiveSize = ((long) read32(firstHeader)) & 4294967295L;
            hdr.securityEnvelopeFilePosition = read32(firstHeader);
            hdr.fileSpecPosition = read16(firstHeader);
            hdr.securityEnvelopeLength = read16(firstHeader);
            pushedBackBytes(20);
            hdr.encryptionVersion = firstHeader.readUnsignedByte();
            hdr.lastChapter = firstHeader.readUnsignedByte();
            if (firstHeaderSize >= 33) {
                hdr.arjProtectionFactor = firstHeader.readUnsignedByte();
                hdr.arjFlags2 = firstHeader.readUnsignedByte();
                firstHeader.readUnsignedByte();
                firstHeader.readUnsignedByte();
            }
            hdr.name = readString(basicHeader);
            hdr.comment = readString(basicHeader);
            int extendedHeaderSize = read16(this.in);
            if (extendedHeaderSize > 0) {
                hdr.extendedHeaderBytes = new byte[extendedHeaderSize];
                readFully(this.in, hdr.extendedHeaderBytes);
                long extendedHeaderCrc32 = ((long) read32(this.in)) & 4294967295L;
                CRC32 crc32 = new CRC32();
                crc32.update(hdr.extendedHeaderBytes);
                if (extendedHeaderCrc32 != crc32.getValue()) {
                    throw new IOException("Extended header CRC32 verification failure");
                }
            }
            return hdr;
        }
        throw new IOException("Archive ends without any headers");
    }

    private LocalFileHeader readLocalFileHeader() throws IOException {
        Throwable firstHeader;
        Throwable th;
        Throwable th2;
        Throwable th3;
        Throwable th4;
        Throwable th5;
        byte[] basicHeaderBytes = readHeader();
        if (basicHeaderBytes == null) {
            return null;
        }
        DataInputStream basicHeader = new DataInputStream(new ByteArrayInputStream(basicHeaderBytes));
        try {
            int firstHeaderSize = basicHeader.readUnsignedByte();
            byte[] firstHeaderBytes = new byte[(firstHeaderSize - 1)];
            basicHeader.readFully(firstHeaderBytes);
            th = new DataInputStream(new ByteArrayInputStream(firstHeaderBytes));
            try {
                LocalFileHeader localFileHeader = new LocalFileHeader();
                localFileHeader.archiverVersionNumber = firstHeader.readUnsignedByte();
                localFileHeader.minVersionToExtract = firstHeader.readUnsignedByte();
                localFileHeader.hostOS = firstHeader.readUnsignedByte();
                localFileHeader.arjFlags = firstHeader.readUnsignedByte();
                localFileHeader.method = firstHeader.readUnsignedByte();
                localFileHeader.fileType = firstHeader.readUnsignedByte();
                localFileHeader.reserved = firstHeader.readUnsignedByte();
                localFileHeader.dateTimeModified = read32(firstHeader);
                localFileHeader.compressedSize = ((long) read32(firstHeader)) & 4294967295L;
                localFileHeader.originalSize = ((long) read32(firstHeader)) & 4294967295L;
                localFileHeader.originalCrc32 = ((long) read32(firstHeader)) & 4294967295L;
                localFileHeader.fileSpecPosition = read16(firstHeader);
                localFileHeader.fileAccessMode = read16(firstHeader);
                pushedBackBytes(20);
                localFileHeader.firstChapter = firstHeader.readUnsignedByte();
                localFileHeader.lastChapter = firstHeader.readUnsignedByte();
                readExtraData(firstHeaderSize, firstHeader, localFileHeader);
                localFileHeader.name = readString(basicHeader);
                localFileHeader.comment = readString(basicHeader);
                ArrayList<byte[]> extendedHeaders = new ArrayList<>();
                while (true) {
                    int read16 = read16(this.in);
                    int extendedHeaderSize = read16;
                    if (read16 > 0) {
                        byte[] extendedHeaderBytes = new byte[extendedHeaderSize];
                        readFully(this.in, extendedHeaderBytes);
                        CRC32 crc32 = new CRC32();
                        crc32.update(extendedHeaderBytes);
                        if ((((long) read32(this.in)) & 4294967295L) == crc32.getValue()) {
                            extendedHeaders.add(extendedHeaderBytes);
                        } else {
                            throw new IOException("Extended header CRC32 verification failure");
                        }
                    } else {
                        localFileHeader.extendedHeaders = (byte[][]) extendedHeaders.toArray(new byte[0][]);
                        firstHeader.close();
                        basicHeader.close();
                        return localFileHeader;
                    }
                }
            } catch (Throwable th6) {
                th3 = th5;
                th4 = th6;
            }
        } catch (Throwable th7) {
            th3.addSuppressed(th7);
        } finally {
            firstHeader = th7;
            try {
            } catch (Throwable th8) {
                th.addSuppressed(th8);
            }
        }
        throw th4;
        if (th3 != null) {
            firstHeader.close();
        } else {
            firstHeader.close();
        }
        throw th4;
        throw th2;
    }

    private void readExtraData(int firstHeaderSize, DataInputStream firstHeader, LocalFileHeader localFileHeader) throws IOException {
        if (firstHeaderSize >= 33) {
            localFileHeader.extendedFilePosition = read32(firstHeader);
            if (firstHeaderSize >= 45) {
                localFileHeader.dateTimeAccessed = read32(firstHeader);
                localFileHeader.dateTimeCreated = read32(firstHeader);
                localFileHeader.originalSizeEvenForVolumes = read32(firstHeader);
                pushedBackBytes(12);
            }
            pushedBackBytes(4);
        }
    }

    public static boolean matches(byte[] signature, int length) {
        return length >= 2 && (signature[0] & 255) == 96 && (signature[1] & 255) == ARJ_MAGIC_2;
    }

    public String getArchiveName() {
        return this.mainHeader.name;
    }

    public String getArchiveComment() {
        return this.mainHeader.comment;
    }

    public ArjArchiveEntry getNextEntry() throws IOException {
        InputStream inputStream = this.currentInputStream;
        if (inputStream != null) {
            IOUtils.skip(inputStream, LongCompanionObject.MAX_VALUE);
            this.currentInputStream.close();
            this.currentLocalFileHeader = null;
            this.currentInputStream = null;
        }
        this.currentLocalFileHeader = readLocalFileHeader();
        LocalFileHeader localFileHeader = this.currentLocalFileHeader;
        if (localFileHeader != null) {
            this.currentInputStream = new BoundedInputStream(this.in, localFileHeader.compressedSize);
            if (this.currentLocalFileHeader.method == 0) {
                this.currentInputStream = new CRC32VerifyingInputStream(this.currentInputStream, this.currentLocalFileHeader.originalSize, this.currentLocalFileHeader.originalCrc32);
            }
            return new ArjArchiveEntry(this.currentLocalFileHeader);
        }
        this.currentInputStream = null;
        return null;
    }

    public boolean canReadEntryData(ArchiveEntry ae) {
        return (ae instanceof ArjArchiveEntry) && ((ArjArchiveEntry) ae).getMethod() == 0;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        LocalFileHeader localFileHeader = this.currentLocalFileHeader;
        if (localFileHeader == null) {
            throw new IllegalStateException("No current arj entry");
        } else if (localFileHeader.method == 0) {
            return this.currentInputStream.read(b, off, len);
        } else {
            throw new IOException("Unsupported compression method " + this.currentLocalFileHeader.method);
        }
    }
}

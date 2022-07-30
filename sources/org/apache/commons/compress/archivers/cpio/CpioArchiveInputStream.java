package org.apache.commons.compress.archivers.cpio;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.commons.compress.utils.IOUtils;

public class CpioArchiveInputStream extends ArchiveInputStream implements CpioConstants {
    private final int blockSize;
    private boolean closed;
    private long crc;
    final String encoding;
    private CpioArchiveEntry entry;
    private long entryBytesRead;
    private boolean entryEOF;
    private final byte[] fourBytesBuf;
    private final InputStream in;
    private final byte[] sixBytesBuf;
    private final byte[] tmpbuf;
    private final byte[] twoBytesBuf;
    private final ZipEncoding zipEncoding;

    public CpioArchiveInputStream(InputStream in2) {
        this(in2, 512, CharsetNames.US_ASCII);
    }

    public CpioArchiveInputStream(InputStream in2, String encoding2) {
        this(in2, 512, encoding2);
    }

    public CpioArchiveInputStream(InputStream in2, int blockSize2) {
        this(in2, blockSize2, CharsetNames.US_ASCII);
    }

    public CpioArchiveInputStream(InputStream in2, int blockSize2, String encoding2) {
        this.closed = false;
        this.entryBytesRead = 0;
        this.entryEOF = false;
        this.tmpbuf = new byte[4096];
        this.crc = 0;
        this.twoBytesBuf = new byte[2];
        this.fourBytesBuf = new byte[4];
        this.sixBytesBuf = new byte[6];
        this.in = in2;
        if (blockSize2 > 0) {
            this.blockSize = blockSize2;
            this.encoding = encoding2;
            this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding2);
            return;
        }
        throw new IllegalArgumentException("blockSize must be bigger than 0");
    }

    public int available() throws IOException {
        ensureOpen();
        if (this.entryEOF) {
            return 0;
        }
        return 1;
    }

    public void close() throws IOException {
        if (!this.closed) {
            this.in.close();
            this.closed = true;
        }
    }

    private void closeEntry() throws IOException {
        do {
        } while (skip(2147483647L) == 2147483647L);
    }

    private void ensureOpen() throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
    }

    public CpioArchiveEntry getNextCPIOEntry() throws IOException {
        ensureOpen();
        if (this.entry != null) {
            closeEntry();
        }
        byte[] bArr = this.twoBytesBuf;
        readFully(bArr, 0, bArr.length);
        if (CpioUtil.byteArray2long(this.twoBytesBuf, false) == 29127) {
            this.entry = readOldBinaryEntry(false);
        } else if (CpioUtil.byteArray2long(this.twoBytesBuf, true) == 29127) {
            this.entry = readOldBinaryEntry(true);
        } else {
            byte[] bArr2 = this.twoBytesBuf;
            System.arraycopy(bArr2, 0, this.sixBytesBuf, 0, bArr2.length);
            readFully(this.sixBytesBuf, this.twoBytesBuf.length, this.fourBytesBuf.length);
            String magicString = ArchiveUtils.toAsciiString(this.sixBytesBuf);
            char c = 65535;
            switch (magicString.hashCode()) {
                case 1426477263:
                    if (magicString.equals(CpioConstants.MAGIC_NEW)) {
                        c = 0;
                        break;
                    }
                    break;
                case 1426477264:
                    if (magicString.equals(CpioConstants.MAGIC_NEW_CRC)) {
                        c = 1;
                        break;
                    }
                    break;
                case 1426477269:
                    if (magicString.equals(CpioConstants.MAGIC_OLD_ASCII)) {
                        c = 2;
                        break;
                    }
                    break;
            }
            if (c == 0) {
                this.entry = readNewEntry(false);
            } else if (c == 1) {
                this.entry = readNewEntry(true);
            } else if (c == 2) {
                this.entry = readOldAsciiEntry();
            } else {
                throw new IOException("Unknown magic [" + magicString + "]. Occured at byte: " + getBytesRead());
            }
        }
        this.entryBytesRead = 0;
        this.entryEOF = false;
        this.crc = 0;
        if (!this.entry.getName().equals(CpioConstants.CPIO_TRAILER)) {
            return this.entry;
        }
        this.entryEOF = true;
        skipRemainderOfLastBlock();
        return null;
    }

    private void skip(int bytes) throws IOException {
        if (bytes > 0) {
            readFully(this.fourBytesBuf, 0, bytes);
        }
    }

    public int read(byte[] b, int off, int len) throws IOException {
        ensureOpen();
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        } else {
            CpioArchiveEntry cpioArchiveEntry = this.entry;
            if (cpioArchiveEntry == null || this.entryEOF) {
                return -1;
            }
            if (this.entryBytesRead == cpioArchiveEntry.getSize()) {
                skip(this.entry.getDataPadCount());
                this.entryEOF = true;
                if (this.entry.getFormat() != 2 || this.crc == this.entry.getChksum()) {
                    return -1;
                }
                throw new IOException("CRC Error. Occured at byte: " + getBytesRead());
            }
            int tmplength = (int) Math.min((long) len, this.entry.getSize() - this.entryBytesRead);
            if (tmplength < 0) {
                return -1;
            }
            int tmpread = readFully(b, off, tmplength);
            if (this.entry.getFormat() == 2) {
                for (int pos = 0; pos < tmpread; pos++) {
                    this.crc += (long) (b[pos] & 255);
                    this.crc &= 4294967295L;
                }
            }
            if (tmpread > 0) {
                this.entryBytesRead += (long) tmpread;
            }
            return tmpread;
        }
    }

    private final int readFully(byte[] b, int off, int len) throws IOException {
        int count = IOUtils.readFully(this.in, b, off, len);
        count(count);
        if (count >= len) {
            return count;
        }
        throw new EOFException();
    }

    private long readBinaryLong(int length, boolean swapHalfWord) throws IOException {
        byte[] tmp = new byte[length];
        readFully(tmp, 0, tmp.length);
        return CpioUtil.byteArray2long(tmp, swapHalfWord);
    }

    private long readAsciiLong(int length, int radix) throws IOException {
        byte[] tmpBuffer = new byte[length];
        readFully(tmpBuffer, 0, tmpBuffer.length);
        return Long.parseLong(ArchiveUtils.toAsciiString(tmpBuffer), radix);
    }

    private CpioArchiveEntry readNewEntry(boolean hasCrc) throws IOException {
        CpioArchiveEntry ret;
        if (hasCrc) {
            ret = new CpioArchiveEntry(2);
        } else {
            ret = new CpioArchiveEntry(1);
        }
        ret.setInode(readAsciiLong(8, 16));
        long mode = readAsciiLong(8, 16);
        if (CpioUtil.fileType(mode) != 0) {
            ret.setMode(mode);
        }
        ret.setUID(readAsciiLong(8, 16));
        ret.setGID(readAsciiLong(8, 16));
        ret.setNumberOfLinks(readAsciiLong(8, 16));
        ret.setTime(readAsciiLong(8, 16));
        ret.setSize(readAsciiLong(8, 16));
        if (ret.getSize() >= 0) {
            ret.setDeviceMaj(readAsciiLong(8, 16));
            ret.setDeviceMin(readAsciiLong(8, 16));
            ret.setRemoteDeviceMaj(readAsciiLong(8, 16));
            ret.setRemoteDeviceMin(readAsciiLong(8, 16));
            long namesize = readAsciiLong(8, 16);
            if (namesize >= 0) {
                ret.setChksum(readAsciiLong(8, 16));
                String name = readCString((int) namesize);
                ret.setName(name);
                if (CpioUtil.fileType(mode) != 0 || name.equals(CpioConstants.CPIO_TRAILER)) {
                    skip(ret.getHeaderPadCount(namesize - 1));
                    return ret;
                }
                throw new IOException("Mode 0 only allowed in the trailer. Found entry name: " + ArchiveUtils.sanitize(name) + " Occured at byte: " + getBytesRead());
            }
            throw new IOException("Found illegal entry with negative name length");
        }
        throw new IOException("Found illegal entry with negative length");
    }

    private CpioArchiveEntry readOldAsciiEntry() throws IOException {
        CpioArchiveEntry ret = new CpioArchiveEntry(4);
        ret.setDevice(readAsciiLong(6, 8));
        ret.setInode(readAsciiLong(6, 8));
        long mode = readAsciiLong(6, 8);
        if (CpioUtil.fileType(mode) != 0) {
            ret.setMode(mode);
        }
        ret.setUID(readAsciiLong(6, 8));
        ret.setGID(readAsciiLong(6, 8));
        ret.setNumberOfLinks(readAsciiLong(6, 8));
        ret.setRemoteDevice(readAsciiLong(6, 8));
        ret.setTime(readAsciiLong(11, 8));
        long namesize = readAsciiLong(6, 8);
        if (namesize >= 0) {
            ret.setSize(readAsciiLong(11, 8));
            if (ret.getSize() >= 0) {
                String name = readCString((int) namesize);
                ret.setName(name);
                if (CpioUtil.fileType(mode) != 0 || name.equals(CpioConstants.CPIO_TRAILER)) {
                    return ret;
                }
                throw new IOException("Mode 0 only allowed in the trailer. Found entry: " + ArchiveUtils.sanitize(name) + " Occured at byte: " + getBytesRead());
            }
            throw new IOException("Found illegal entry with negative length");
        }
        throw new IOException("Found illegal entry with negative name length");
    }

    private CpioArchiveEntry readOldBinaryEntry(boolean swapHalfWord) throws IOException {
        CpioArchiveEntry ret = new CpioArchiveEntry(8);
        ret.setDevice(readBinaryLong(2, swapHalfWord));
        ret.setInode(readBinaryLong(2, swapHalfWord));
        long mode = readBinaryLong(2, swapHalfWord);
        if (CpioUtil.fileType(mode) != 0) {
            ret.setMode(mode);
        }
        ret.setUID(readBinaryLong(2, swapHalfWord));
        ret.setGID(readBinaryLong(2, swapHalfWord));
        ret.setNumberOfLinks(readBinaryLong(2, swapHalfWord));
        ret.setRemoteDevice(readBinaryLong(2, swapHalfWord));
        ret.setTime(readBinaryLong(4, swapHalfWord));
        long namesize = readBinaryLong(2, swapHalfWord);
        if (namesize >= 0) {
            ret.setSize(readBinaryLong(4, swapHalfWord));
            if (ret.getSize() >= 0) {
                String name = readCString((int) namesize);
                ret.setName(name);
                if (CpioUtil.fileType(mode) != 0 || name.equals(CpioConstants.CPIO_TRAILER)) {
                    skip(ret.getHeaderPadCount(namesize - 1));
                    return ret;
                }
                throw new IOException("Mode 0 only allowed in the trailer. Found entry: " + ArchiveUtils.sanitize(name) + "Occured at byte: " + getBytesRead());
            }
            throw new IOException("Found illegal entry with negative length");
        }
        throw new IOException("Found illegal entry with negative name length");
    }

    private String readCString(int length) throws IOException {
        byte[] tmpBuffer = new byte[(length - 1)];
        readFully(tmpBuffer, 0, tmpBuffer.length);
        if (this.in.read() != -1) {
            return this.zipEncoding.decode(tmpBuffer);
        }
        throw new EOFException();
    }

    public long skip(long n) throws IOException {
        if (n >= 0) {
            ensureOpen();
            int max = (int) Math.min(n, 2147483647L);
            int total = 0;
            while (true) {
                if (total >= max) {
                    break;
                }
                int len = max - total;
                byte[] bArr = this.tmpbuf;
                if (len > bArr.length) {
                    len = bArr.length;
                }
                int len2 = read(this.tmpbuf, 0, len);
                if (len2 == -1) {
                    this.entryEOF = true;
                    break;
                }
                total += len2;
            }
            return (long) total;
        }
        throw new IllegalArgumentException("Negative skip length");
    }

    public ArchiveEntry getNextEntry() throws IOException {
        return getNextCPIOEntry();
    }

    private void skipRemainderOfLastBlock() throws IOException {
        long bytesRead = getBytesRead();
        int i = this.blockSize;
        long readFromLastBlock = bytesRead % ((long) i);
        long remainingBytes = readFromLastBlock == 0 ? 0 : ((long) i) - readFromLastBlock;
        while (remainingBytes > 0) {
            long skipped = skip(((long) this.blockSize) - readFromLastBlock);
            if (skipped > 0) {
                remainingBytes -= skipped;
            } else {
                return;
            }
        }
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < 6) {
            return false;
        }
        if (signature[0] == 113 && (signature[1] & 255) == 199) {
            return true;
        }
        if (signature[1] == 113 && (signature[0] & 255) == 199) {
            return true;
        }
        if (signature[0] != 48 || signature[1] != 55 || signature[2] != 48 || signature[3] != 55 || signature[4] != 48) {
            return false;
        }
        if (signature[5] == 49 || signature[5] == 50 || signature[5] == 55) {
            return true;
        }
        return false;
    }
}

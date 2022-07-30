package org.apache.commons.compress.archivers.cpio;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.utils.CharsetNames;

public class CpioArchiveOutputStream extends ArchiveOutputStream implements CpioConstants {
    private final int blockSize;
    private boolean closed;
    private long crc;
    final String encoding;
    private CpioArchiveEntry entry;
    private final short entryFormat;
    private boolean finished;
    private final HashMap<String, CpioArchiveEntry> names;
    private long nextArtificalDeviceAndInode;
    private final OutputStream out;
    private long written;
    private final ZipEncoding zipEncoding;

    public CpioArchiveOutputStream(OutputStream out2, short format) {
        this(out2, format, 512, CharsetNames.US_ASCII);
    }

    public CpioArchiveOutputStream(OutputStream out2, short format, int blockSize2) {
        this(out2, format, blockSize2, CharsetNames.US_ASCII);
    }

    public CpioArchiveOutputStream(OutputStream out2, short format, int blockSize2, String encoding2) {
        this.closed = false;
        this.names = new HashMap<>();
        this.crc = 0;
        this.nextArtificalDeviceAndInode = 1;
        this.out = out2;
        if (format == 1 || format == 2 || format == 4 || format == 8) {
            this.entryFormat = format;
            this.blockSize = blockSize2;
            this.encoding = encoding2;
            this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding2);
            return;
        }
        throw new IllegalArgumentException("Unknown format: " + format);
    }

    public CpioArchiveOutputStream(OutputStream out2) {
        this(out2, 1);
    }

    public CpioArchiveOutputStream(OutputStream out2, String encoding2) {
        this(out2, 1, 512, encoding2);
    }

    private void ensureOpen() throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
    }

    public void putArchiveEntry(ArchiveEntry entry2) throws IOException {
        if (!this.finished) {
            CpioArchiveEntry e = (CpioArchiveEntry) entry2;
            ensureOpen();
            if (this.entry != null) {
                closeArchiveEntry();
            }
            if (e.getTime() == -1) {
                e.setTime(System.currentTimeMillis() / 1000);
            }
            short format = e.getFormat();
            if (format != this.entryFormat) {
                throw new IOException("Header format: " + format + " does not match existing format: " + this.entryFormat);
            } else if (this.names.put(e.getName(), e) == null) {
                writeHeader(e);
                this.entry = e;
                this.written = 0;
            } else {
                throw new IOException("Duplicate entry: " + e.getName());
            }
        } else {
            throw new IOException("Stream has already been finished");
        }
    }

    private void writeHeader(CpioArchiveEntry e) throws IOException {
        short format = e.getFormat();
        if (format == 1) {
            this.out.write(ArchiveUtils.toAsciiBytes(CpioConstants.MAGIC_NEW));
            count(6);
            writeNewEntry(e);
        } else if (format == 2) {
            this.out.write(ArchiveUtils.toAsciiBytes(CpioConstants.MAGIC_NEW_CRC));
            count(6);
            writeNewEntry(e);
        } else if (format == 4) {
            this.out.write(ArchiveUtils.toAsciiBytes(CpioConstants.MAGIC_OLD_ASCII));
            count(6);
            writeOldAsciiEntry(e);
        } else if (format == 8) {
            writeBinaryLong(29127, 2, true);
            writeOldBinaryEntry(e, true);
        } else {
            throw new IOException("Unknown format " + e.getFormat());
        }
    }

    private void writeNewEntry(CpioArchiveEntry entry2) throws IOException {
        long inode = entry2.getInode();
        long devMin = entry2.getDeviceMin();
        if (CpioConstants.CPIO_TRAILER.equals(entry2.getName())) {
            devMin = 0;
            inode = 0;
        } else if (inode == 0 && devMin == 0) {
            long j = this.nextArtificalDeviceAndInode;
            inode = j & -1;
            this.nextArtificalDeviceAndInode = j + 1;
            devMin = (j >> 32) & -1;
        } else {
            this.nextArtificalDeviceAndInode = Math.max(this.nextArtificalDeviceAndInode, (4294967296L * devMin) + inode) + 1;
        }
        writeAsciiLong(inode, 8, 16);
        writeAsciiLong(entry2.getMode(), 8, 16);
        writeAsciiLong(entry2.getUID(), 8, 16);
        writeAsciiLong(entry2.getGID(), 8, 16);
        writeAsciiLong(entry2.getNumberOfLinks(), 8, 16);
        writeAsciiLong(entry2.getTime(), 8, 16);
        writeAsciiLong(entry2.getSize(), 8, 16);
        writeAsciiLong(entry2.getDeviceMaj(), 8, 16);
        writeAsciiLong(devMin, 8, 16);
        writeAsciiLong(entry2.getRemoteDeviceMaj(), 8, 16);
        writeAsciiLong(entry2.getRemoteDeviceMin(), 8, 16);
        byte[] name = encode(entry2.getName());
        writeAsciiLong(((long) name.length) + 1, 8, 16);
        writeAsciiLong(entry2.getChksum(), 8, 16);
        writeCString(name);
        pad(entry2.getHeaderPadCount((long) name.length));
    }

    private void writeOldAsciiEntry(CpioArchiveEntry entry2) throws IOException {
        long inode = entry2.getInode();
        long device = entry2.getDevice();
        if (CpioConstants.CPIO_TRAILER.equals(entry2.getName())) {
            device = 0;
            inode = 0;
        } else if (inode == 0 && device == 0) {
            long j = this.nextArtificalDeviceAndInode;
            inode = j & 262143;
            this.nextArtificalDeviceAndInode = j + 1;
            device = (j >> 18) & 262143;
        } else {
            this.nextArtificalDeviceAndInode = Math.max(this.nextArtificalDeviceAndInode, (PlaybackStateCompat.ACTION_SET_REPEAT_MODE * device) + inode) + 1;
        }
        writeAsciiLong(device, 6, 8);
        writeAsciiLong(inode, 6, 8);
        writeAsciiLong(entry2.getMode(), 6, 8);
        writeAsciiLong(entry2.getUID(), 6, 8);
        writeAsciiLong(entry2.getGID(), 6, 8);
        writeAsciiLong(entry2.getNumberOfLinks(), 6, 8);
        writeAsciiLong(entry2.getRemoteDevice(), 6, 8);
        writeAsciiLong(entry2.getTime(), 11, 8);
        byte[] name = encode(entry2.getName());
        writeAsciiLong(((long) name.length) + 1, 6, 8);
        writeAsciiLong(entry2.getSize(), 11, 8);
        writeCString(name);
    }

    private void writeOldBinaryEntry(CpioArchiveEntry entry2, boolean swapHalfWord) throws IOException {
        long inode = entry2.getInode();
        long device = entry2.getDevice();
        if (CpioConstants.CPIO_TRAILER.equals(entry2.getName())) {
            device = 0;
            inode = 0;
        } else if (inode == 0 && device == 0) {
            long j = this.nextArtificalDeviceAndInode;
            inode = j & 65535;
            this.nextArtificalDeviceAndInode = j + 1;
            device = (j >> 16) & 65535;
        } else {
            this.nextArtificalDeviceAndInode = Math.max(this.nextArtificalDeviceAndInode, (PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH * device) + inode) + 1;
        }
        writeBinaryLong(device, 2, swapHalfWord);
        writeBinaryLong(inode, 2, swapHalfWord);
        writeBinaryLong(entry2.getMode(), 2, swapHalfWord);
        writeBinaryLong(entry2.getUID(), 2, swapHalfWord);
        writeBinaryLong(entry2.getGID(), 2, swapHalfWord);
        writeBinaryLong(entry2.getNumberOfLinks(), 2, swapHalfWord);
        writeBinaryLong(entry2.getRemoteDevice(), 2, swapHalfWord);
        writeBinaryLong(entry2.getTime(), 4, swapHalfWord);
        byte[] name = encode(entry2.getName());
        writeBinaryLong(((long) name.length) + 1, 2, swapHalfWord);
        writeBinaryLong(entry2.getSize(), 4, swapHalfWord);
        writeCString(name);
        pad(entry2.getHeaderPadCount((long) name.length));
    }

    public void closeArchiveEntry() throws IOException {
        if (!this.finished) {
            ensureOpen();
            CpioArchiveEntry cpioArchiveEntry = this.entry;
            if (cpioArchiveEntry == null) {
                throw new IOException("Trying to close non-existent entry");
            } else if (cpioArchiveEntry.getSize() == this.written) {
                pad(this.entry.getDataPadCount());
                if (this.entry.getFormat() != 2 || this.crc == this.entry.getChksum()) {
                    this.entry = null;
                    this.crc = 0;
                    this.written = 0;
                    return;
                }
                throw new IOException("CRC Error");
            } else {
                throw new IOException("Invalid entry size (expected " + this.entry.getSize() + " but got " + this.written + " bytes)");
            }
        } else {
            throw new IOException("Stream has already been finished");
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        ensureOpen();
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new IndexOutOfBoundsException();
        } else if (len != 0) {
            CpioArchiveEntry cpioArchiveEntry = this.entry;
            if (cpioArchiveEntry == null) {
                throw new IOException("No current CPIO entry");
            } else if (this.written + ((long) len) <= cpioArchiveEntry.getSize()) {
                this.out.write(b, off, len);
                this.written += (long) len;
                if (this.entry.getFormat() == 2) {
                    for (int pos = 0; pos < len; pos++) {
                        this.crc += (long) (b[pos] & 255);
                        this.crc &= 4294967295L;
                    }
                }
                count(len);
            } else {
                throw new IOException("Attempt to write past end of STORED entry");
            }
        }
    }

    public void finish() throws IOException {
        ensureOpen();
        if (this.finished) {
            throw new IOException("This archive has already been finished");
        } else if (this.entry == null) {
            this.entry = new CpioArchiveEntry(this.entryFormat);
            this.entry.setName(CpioConstants.CPIO_TRAILER);
            this.entry.setNumberOfLinks(1);
            writeHeader(this.entry);
            closeArchiveEntry();
            long bytesWritten = getBytesWritten();
            int i = this.blockSize;
            int lengthOfLastBlock = (int) (bytesWritten % ((long) i));
            if (lengthOfLastBlock != 0) {
                pad(i - lengthOfLastBlock);
            }
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

    private void pad(int count) throws IOException {
        if (count > 0) {
            this.out.write(new byte[count]);
            count(count);
        }
    }

    private void writeBinaryLong(long number, int length, boolean swapHalfWord) throws IOException {
        byte[] tmp = CpioUtil.long2byteArray(number, length, swapHalfWord);
        this.out.write(tmp);
        count(tmp.length);
    }

    private void writeAsciiLong(long number, int length, int radix) throws IOException {
        String tmpStr;
        StringBuilder tmp = new StringBuilder();
        if (radix == 16) {
            tmp.append(Long.toHexString(number));
        } else if (radix == 8) {
            tmp.append(Long.toOctalString(number));
        } else {
            tmp.append(Long.toString(number));
        }
        if (tmp.length() <= length) {
            int insertLength = length - tmp.length();
            for (int pos = 0; pos < insertLength; pos++) {
                tmp.insert(0, "0");
            }
            tmpStr = tmp.toString();
        } else {
            tmpStr = tmp.substring(tmp.length() - length);
        }
        byte[] b = ArchiveUtils.toAsciiBytes(tmpStr);
        this.out.write(b);
        count(b.length);
    }

    private byte[] encode(String str) throws IOException {
        ByteBuffer buf = this.zipEncoding.encode(str);
        return Arrays.copyOfRange(buf.array(), buf.arrayOffset(), buf.arrayOffset() + (buf.limit() - buf.position()));
    }

    private void writeCString(byte[] str) throws IOException {
        this.out.write(str);
        this.out.write(0);
        count(str.length + 1);
    }

    public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
        if (!this.finished) {
            return new CpioArchiveEntry(inputFile, entryName);
        }
        throw new IOException("Stream has already been finished");
    }
}

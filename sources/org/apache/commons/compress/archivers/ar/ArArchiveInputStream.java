package org.apache.commons.compress.archivers.ar;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.utils.IOUtils;

public class ArArchiveInputStream extends ArchiveInputStream {
    private static final String BSD_LONGNAME_PATTERN = "^#1/\\d+";
    static final String BSD_LONGNAME_PREFIX = "#1/";
    private static final int BSD_LONGNAME_PREFIX_LEN = BSD_LONGNAME_PREFIX.length();
    private static final int FILE_MODE_LEN = 8;
    private static final int FILE_MODE_OFFSET = 40;
    private static final String GNU_LONGNAME_PATTERN = "^/\\d+";
    private static final String GNU_STRING_TABLE_NAME = "//";
    private static final int GROUP_ID_LEN = 6;
    private static final int GROUP_ID_OFFSET = 34;
    private static final int LAST_MODIFIED_LEN = 12;
    private static final int LAST_MODIFIED_OFFSET = 16;
    private static final int LENGTH_LEN = 10;
    private static final int LENGTH_OFFSET = 48;
    private static final int NAME_LEN = 16;
    private static final int NAME_OFFSET = 0;
    private static final int USER_ID_LEN = 6;
    private static final int USER_ID_OFFSET = 28;
    private boolean closed;
    private ArArchiveEntry currentEntry = null;
    private long entryOffset = -1;
    private final InputStream input;
    private final byte[] metaData = new byte[58];
    private byte[] namebuffer = null;
    private long offset = 0;

    public ArArchiveInputStream(InputStream pInput) {
        this.input = pInput;
        this.closed = false;
    }

    public ArArchiveEntry getNextArEntry() throws IOException {
        ArArchiveEntry arArchiveEntry = this.currentEntry;
        if (arArchiveEntry != null) {
            trackReadBytes(IOUtils.skip(this.input, (this.entryOffset + arArchiveEntry.getLength()) - this.offset));
            this.currentEntry = null;
        }
        if (this.offset == 0) {
            byte[] expected = ArchiveUtils.toAsciiBytes(ArArchiveEntry.HEADER);
            byte[] realized = new byte[expected.length];
            int read = IOUtils.readFully(this.input, realized);
            trackReadBytes((long) read);
            if (read != expected.length) {
                throw new IOException("Failed to read header. Occured at byte: " + getBytesRead());
            } else if (!Arrays.equals(expected, realized)) {
                throw new IOException("Invalid header " + ArchiveUtils.toAsciiString(realized));
            }
        }
        if (this.offset % 2 != 0) {
            if (this.input.read() < 0) {
                return null;
            }
            trackReadBytes(1);
        }
        int read2 = IOUtils.readFully(this.input, this.metaData);
        trackReadBytes((long) read2);
        if (read2 == 0) {
            return null;
        }
        if (read2 >= this.metaData.length) {
            byte[] expected2 = ArchiveUtils.toAsciiBytes(ArArchiveEntry.TRAILER);
            byte[] realized2 = new byte[expected2.length];
            int read3 = IOUtils.readFully(this.input, realized2);
            trackReadBytes((long) read3);
            if (read3 != expected2.length) {
                throw new IOException("Failed to read entry trailer. Occured at byte: " + getBytesRead());
            } else if (Arrays.equals(expected2, realized2)) {
                this.entryOffset = this.offset;
                String temp = ArchiveUtils.toAsciiString(this.metaData, 0, 16).trim();
                if (isGNUStringTable(temp)) {
                    this.currentEntry = readGNUStringTable(this.metaData, 48, 10);
                    return getNextArEntry();
                }
                long len = asLong(this.metaData, 48, 10);
                if (temp.endsWith("/")) {
                    temp = temp.substring(0, temp.length() - 1);
                } else if (isGNULongName(temp)) {
                    temp = getExtendedName(Integer.parseInt(temp.substring(1)));
                } else if (isBSDLongName(temp)) {
                    temp = getBSDLongName(temp);
                    int nameLen = temp.length();
                    len -= (long) nameLen;
                    this.entryOffset += (long) nameLen;
                }
                this.currentEntry = new ArArchiveEntry(temp, len, asInt(this.metaData, 28, 6, true), asInt(this.metaData, 34, 6, true), asInt(this.metaData, 40, 8, 8), asLong(this.metaData, 16, 12));
                return this.currentEntry;
            } else {
                throw new IOException("Invalid entry trailer. not read the content? Occured at byte: " + getBytesRead());
            }
        } else {
            throw new IOException("Truncated ar archive");
        }
    }

    private String getExtendedName(int offset2) throws IOException {
        if (this.namebuffer != null) {
            int i = offset2;
            while (true) {
                byte[] bArr = this.namebuffer;
                if (i >= bArr.length) {
                    throw new IOException("Failed to read entry: " + offset2);
                } else if (bArr[i] != 10 && bArr[i] != 0) {
                    i++;
                }
            }
            if (this.namebuffer[i - 1] == 47) {
                i--;
            }
            return ArchiveUtils.toAsciiString(this.namebuffer, offset2, i - offset2);
        }
        throw new IOException("Cannot process GNU long filename as no // record was found");
    }

    private long asLong(byte[] byteArray, int offset2, int len) {
        return Long.parseLong(ArchiveUtils.toAsciiString(byteArray, offset2, len).trim());
    }

    private int asInt(byte[] byteArray, int offset2, int len) {
        return asInt(byteArray, offset2, len, 10, false);
    }

    private int asInt(byte[] byteArray, int offset2, int len, boolean treatBlankAsZero) {
        return asInt(byteArray, offset2, len, 10, treatBlankAsZero);
    }

    private int asInt(byte[] byteArray, int offset2, int len, int base) {
        return asInt(byteArray, offset2, len, base, false);
    }

    private int asInt(byte[] byteArray, int offset2, int len, int base, boolean treatBlankAsZero) {
        String string = ArchiveUtils.toAsciiString(byteArray, offset2, len).trim();
        if (string.length() != 0 || !treatBlankAsZero) {
            return Integer.parseInt(string, base);
        }
        return 0;
    }

    public ArchiveEntry getNextEntry() throws IOException {
        return getNextArEntry();
    }

    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.input.close();
        }
        this.currentEntry = null;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        ArArchiveEntry arArchiveEntry = this.currentEntry;
        if (arArchiveEntry != null) {
            long entryEnd = this.entryOffset + arArchiveEntry.getLength();
            if (len < 0) {
                return -1;
            }
            long j = this.offset;
            if (j >= entryEnd) {
                return -1;
            }
            int ret = this.input.read(b, off, (int) Math.min((long) len, entryEnd - j));
            trackReadBytes((long) ret);
            return ret;
        }
        throw new IllegalStateException("No current ar entry");
    }

    public static boolean matches(byte[] signature, int length) {
        return length >= 8 && signature[0] == 33 && signature[1] == 60 && signature[2] == 97 && signature[3] == 114 && signature[4] == 99 && signature[5] == 104 && signature[6] == 62 && signature[7] == 10;
    }

    private static boolean isBSDLongName(String name) {
        return name != null && name.matches(BSD_LONGNAME_PATTERN);
    }

    private String getBSDLongName(String bsdLongName) throws IOException {
        int nameLen = Integer.parseInt(bsdLongName.substring(BSD_LONGNAME_PREFIX_LEN));
        byte[] name = new byte[nameLen];
        int read = IOUtils.readFully(this.input, name);
        trackReadBytes((long) read);
        if (read == nameLen) {
            return ArchiveUtils.toAsciiString(name);
        }
        throw new EOFException();
    }

    private static boolean isGNUStringTable(String name) {
        return GNU_STRING_TABLE_NAME.equals(name);
    }

    private void trackReadBytes(long read) {
        count(read);
        if (read > 0) {
            this.offset += read;
        }
    }

    private ArArchiveEntry readGNUStringTable(byte[] length, int offset2, int len) throws IOException {
        int bufflen = asInt(length, offset2, len);
        this.namebuffer = new byte[bufflen];
        int read = IOUtils.readFully(this.input, this.namebuffer, 0, bufflen);
        trackReadBytes((long) read);
        if (read == bufflen) {
            return new ArArchiveEntry(GNU_STRING_TABLE_NAME, (long) bufflen);
        }
        throw new IOException("Failed to read complete // record: expected=" + bufflen + " read=" + read);
    }

    private boolean isGNULongName(String name) {
        return name != null && name.matches(GNU_LONGNAME_PATTERN);
    }
}

package org.apache.commons.compress.archivers.ar;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.utils.ArchiveUtils;

public class ArArchiveOutputStream extends ArchiveOutputStream {
    public static final int LONGFILE_BSD = 1;
    public static final int LONGFILE_ERROR = 0;
    private long entryOffset = 0;
    private boolean finished = false;
    private boolean haveUnclosedEntry = false;
    private int longFileMode = 0;
    private final OutputStream out;
    private ArArchiveEntry prevEntry;

    public ArArchiveOutputStream(OutputStream pOut) {
        this.out = pOut;
    }

    public void setLongFileMode(int longFileMode2) {
        this.longFileMode = longFileMode2;
    }

    private long writeArchiveHeader() throws IOException {
        byte[] header = ArchiveUtils.toAsciiBytes(ArArchiveEntry.HEADER);
        this.out.write(header);
        return (long) header.length;
    }

    public void closeArchiveEntry() throws IOException {
        if (this.finished) {
            throw new IOException("Stream has already been finished");
        } else if (this.prevEntry == null || !this.haveUnclosedEntry) {
            throw new IOException("No current entry to close");
        } else {
            if (this.entryOffset % 2 != 0) {
                this.out.write(10);
            }
            this.haveUnclosedEntry = false;
        }
    }

    public void putArchiveEntry(ArchiveEntry pEntry) throws IOException {
        if (!this.finished) {
            ArArchiveEntry pArEntry = (ArArchiveEntry) pEntry;
            ArArchiveEntry arArchiveEntry = this.prevEntry;
            if (arArchiveEntry == null) {
                writeArchiveHeader();
            } else if (arArchiveEntry.getLength() != this.entryOffset) {
                throw new IOException("Length does not match entry (" + this.prevEntry.getLength() + " != " + this.entryOffset);
            } else if (this.haveUnclosedEntry) {
                closeArchiveEntry();
            }
            this.prevEntry = pArEntry;
            writeEntryHeader(pArEntry);
            this.entryOffset = 0;
            this.haveUnclosedEntry = true;
            return;
        }
        throw new IOException("Stream has already been finished");
    }

    private long fill(long pOffset, long pNewOffset, char pFill) throws IOException {
        long diff = pNewOffset - pOffset;
        if (diff > 0) {
            for (int i = 0; ((long) i) < diff; i++) {
                write(pFill);
            }
        }
        return pNewOffset;
    }

    private long write(String data) throws IOException {
        byte[] bytes = data.getBytes("ascii");
        write(bytes);
        return (long) bytes.length;
    }

    private long writeEntryHeader(ArArchiveEntry pEntry) throws IOException {
        boolean mustAppendName;
        long offset;
        String n = pEntry.getName();
        if (this.longFileMode != 0 || n.length() <= 16) {
            if (1 != this.longFileMode || (n.length() <= 16 && !n.contains(" "))) {
                offset = 0 + write(n);
                mustAppendName = false;
            } else {
                offset = 0 + write("#1/" + String.valueOf(n.length()));
                mustAppendName = true;
            }
            long offset2 = fill(offset, 16, ' ');
            String m = "" + pEntry.getLastModified();
            if (m.length() <= 12) {
                long offset3 = fill(offset2 + write(m), 28, ' ');
                String u = "" + pEntry.getUserId();
                if (u.length() <= 6) {
                    long offset4 = fill(offset3 + write(u), 34, ' ');
                    String g = "" + pEntry.getGroupId();
                    if (g.length() <= 6) {
                        long offset5 = fill(offset4 + write(g), 40, ' ');
                        String fm = "" + Integer.toString(pEntry.getMode(), 8);
                        if (fm.length() <= 8) {
                            long offset6 = fill(offset5 + write(fm), 48, ' ');
                            String s = String.valueOf(pEntry.getLength() + ((long) (mustAppendName ? n.length() : 0)));
                            if (s.length() <= 10) {
                                long offset7 = fill(offset6 + write(s), 58, ' ') + write(ArArchiveEntry.TRAILER);
                                if (mustAppendName) {
                                    return offset7 + write(n);
                                }
                                return offset7;
                            }
                            throw new IOException("Size too long");
                        }
                        throw new IOException("Filemode too long");
                    }
                    throw new IOException("Group id too long");
                }
                throw new IOException("User id too long");
            }
            throw new IOException("Last modified too long");
        }
        throw new IOException("File name too long, > 16 chars: " + n);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write(b, off, len);
        count(len);
        this.entryOffset += (long) len;
    }

    public void close() throws IOException {
        try {
            if (!this.finished) {
                finish();
            }
        } finally {
            this.out.close();
            this.prevEntry = null;
        }
    }

    public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
        if (!this.finished) {
            return new ArArchiveEntry(inputFile, entryName);
        }
        throw new IOException("Stream has already been finished");
    }

    public void finish() throws IOException {
        if (this.haveUnclosedEntry) {
            throw new IOException("This archive contains unclosed entries.");
        } else if (!this.finished) {
            this.finished = true;
        } else {
            throw new IOException("This archive has already been finished");
        }
    }
}

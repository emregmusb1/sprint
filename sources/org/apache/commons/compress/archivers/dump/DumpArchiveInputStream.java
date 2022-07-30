package org.apache.commons.compress.archivers.dump;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.dump.DumpArchiveConstants;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;

public class DumpArchiveInputStream extends ArchiveInputStream {
    private DumpArchiveEntry active;
    private byte[] blockBuffer;
    final String encoding;
    private long entryOffset;
    private long entrySize;
    private long filepos;
    private boolean hasHitEOF;
    private boolean isClosed;
    private final Map<Integer, Dirent> names;
    private final Map<Integer, DumpArchiveEntry> pending;
    private Queue<DumpArchiveEntry> queue;
    protected TapeInputStream raw;
    private final byte[] readBuf;
    private int readIdx;
    private int recordOffset;
    private DumpArchiveSummary summary;
    private final ZipEncoding zipEncoding;

    public DumpArchiveInputStream(InputStream is) throws ArchiveException {
        this(is, (String) null);
    }

    public DumpArchiveInputStream(InputStream is, String encoding2) throws ArchiveException {
        this.readBuf = new byte[1024];
        this.names = new HashMap();
        this.pending = new HashMap();
        this.raw = new TapeInputStream(is);
        this.hasHitEOF = false;
        this.encoding = encoding2;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding2);
        try {
            byte[] headerBytes = this.raw.readRecord();
            if (DumpArchiveUtil.verify(headerBytes)) {
                this.summary = new DumpArchiveSummary(headerBytes, this.zipEncoding);
                this.raw.resetBlockSize(this.summary.getNTRec(), this.summary.isCompressed());
                this.blockBuffer = new byte[4096];
                readCLRI();
                readBITS();
                this.names.put(2, new Dirent(2, 2, 4, "."));
                this.queue = new PriorityQueue(10, new Comparator<DumpArchiveEntry>() {
                    public int compare(DumpArchiveEntry p, DumpArchiveEntry q) {
                        if (p.getOriginalName() == null || q.getOriginalName() == null) {
                            return Integer.MAX_VALUE;
                        }
                        return p.getOriginalName().compareTo(q.getOriginalName());
                    }
                });
                return;
            }
            throw new UnrecognizedFormatException();
        } catch (IOException ex) {
            throw new ArchiveException(ex.getMessage(), ex);
        }
    }

    @Deprecated
    public int getCount() {
        return (int) getBytesRead();
    }

    public long getBytesRead() {
        return this.raw.getBytesRead();
    }

    public DumpArchiveSummary getSummary() {
        return this.summary;
    }

    private void readCLRI() throws IOException {
        byte[] buffer = this.raw.readRecord();
        if (DumpArchiveUtil.verify(buffer)) {
            this.active = DumpArchiveEntry.parse(buffer);
            if (DumpArchiveConstants.SEGMENT_TYPE.CLRI != this.active.getHeaderType()) {
                throw new InvalidFormatException();
            } else if (this.raw.skip(((long) this.active.getHeaderCount()) * PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) != -1) {
                this.readIdx = this.active.getHeaderCount();
            } else {
                throw new EOFException();
            }
        } else {
            throw new InvalidFormatException();
        }
    }

    private void readBITS() throws IOException {
        byte[] buffer = this.raw.readRecord();
        if (DumpArchiveUtil.verify(buffer)) {
            this.active = DumpArchiveEntry.parse(buffer);
            if (DumpArchiveConstants.SEGMENT_TYPE.BITS != this.active.getHeaderType()) {
                throw new InvalidFormatException();
            } else if (this.raw.skip(((long) this.active.getHeaderCount()) * PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) != -1) {
                this.readIdx = this.active.getHeaderCount();
            } else {
                throw new EOFException();
            }
        } else {
            throw new InvalidFormatException();
        }
    }

    public DumpArchiveEntry getNextDumpEntry() throws IOException {
        return getNextEntry();
    }

    public DumpArchiveEntry getNextEntry() throws IOException {
        DumpArchiveEntry entry = null;
        String path = null;
        if (!this.queue.isEmpty()) {
            return this.queue.remove();
        }
        while (entry == null) {
            if (this.hasHitEOF) {
                return null;
            }
            while (this.readIdx < this.active.getHeaderCount()) {
                DumpArchiveEntry dumpArchiveEntry = this.active;
                int i = this.readIdx;
                this.readIdx = i + 1;
                if (!dumpArchiveEntry.isSparseRecord(i) && this.raw.skip(PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) == -1) {
                    throw new EOFException();
                }
            }
            this.readIdx = 0;
            this.filepos = this.raw.getBytesRead();
            byte[] headerBytes = this.raw.readRecord();
            if (DumpArchiveUtil.verify(headerBytes)) {
                this.active = DumpArchiveEntry.parse(headerBytes);
                while (DumpArchiveConstants.SEGMENT_TYPE.ADDR == this.active.getHeaderType()) {
                    if (this.raw.skip(((long) (this.active.getHeaderCount() - this.active.getHeaderHoles())) * PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) != -1) {
                        this.filepos = this.raw.getBytesRead();
                        byte[] headerBytes2 = this.raw.readRecord();
                        if (DumpArchiveUtil.verify(headerBytes2)) {
                            this.active = DumpArchiveEntry.parse(headerBytes2);
                        } else {
                            throw new InvalidFormatException();
                        }
                    } else {
                        throw new EOFException();
                    }
                }
                if (DumpArchiveConstants.SEGMENT_TYPE.END == this.active.getHeaderType()) {
                    this.hasHitEOF = true;
                    return null;
                }
                entry = this.active;
                if (entry.isDirectory()) {
                    readDirectoryEntry(this.active);
                    this.entryOffset = 0;
                    this.entrySize = 0;
                    this.readIdx = this.active.getHeaderCount();
                } else {
                    this.entryOffset = 0;
                    this.entrySize = this.active.getEntrySize();
                    this.readIdx = 0;
                }
                this.recordOffset = this.readBuf.length;
                path = getPath(entry);
                if (path == null) {
                    entry = null;
                }
            } else {
                throw new InvalidFormatException();
            }
        }
        entry.setName(path);
        entry.setSimpleName(this.names.get(Integer.valueOf(entry.getIno())).getName());
        entry.setOffset(this.filepos);
        return entry;
    }

    private void readDirectoryEntry(DumpArchiveEntry entry) throws IOException {
        DumpArchiveEntry entry2;
        int datalen;
        DumpArchiveEntry entry3;
        int datalen2;
        boolean first = true;
        long size = entry.getEntrySize();
        DumpArchiveEntry entry4 = entry;
        while (true) {
            if (first || DumpArchiveConstants.SEGMENT_TYPE.ADDR == entry4.getHeaderType()) {
                if (!first) {
                    this.raw.readRecord();
                }
                if (!this.names.containsKey(Integer.valueOf(entry4.getIno())) && DumpArchiveConstants.SEGMENT_TYPE.INODE == entry4.getHeaderType()) {
                    this.pending.put(Integer.valueOf(entry4.getIno()), entry4);
                }
                int datalen3 = entry4.getHeaderCount() * 1024;
                if (this.blockBuffer.length < datalen3) {
                    this.blockBuffer = new byte[datalen3];
                }
                if (this.raw.read(this.blockBuffer, 0, datalen3) == datalen3) {
                    int i = 0;
                    while (i < datalen3 - 8 && ((long) i) < size - 8) {
                        int ino = DumpArchiveUtil.convert32(this.blockBuffer, i);
                        int reclen = DumpArchiveUtil.convert16(this.blockBuffer, i + 4);
                        byte[] bArr = this.blockBuffer;
                        byte type = bArr[i + 6];
                        String name = DumpArchiveUtil.decode(this.zipEncoding, bArr, i + 8, bArr[i + 7]);
                        if (".".equals(name)) {
                            entry2 = entry4;
                            datalen = datalen3;
                        } else if ("..".equals(name)) {
                            entry2 = entry4;
                            datalen = datalen3;
                        } else {
                            this.names.put(Integer.valueOf(ino), new Dirent(ino, entry4.getIno(), type, name));
                            for (Map.Entry<Integer, DumpArchiveEntry> e : this.pending.entrySet()) {
                                String path = getPath(e.getValue());
                                if (path != null) {
                                    e.getValue().setName(path);
                                    entry3 = entry4;
                                    datalen2 = datalen3;
                                    e.getValue().setSimpleName(this.names.get(e.getKey()).getName());
                                    this.queue.add(e.getValue());
                                } else {
                                    entry3 = entry4;
                                    datalen2 = datalen3;
                                }
                                entry4 = entry3;
                                datalen3 = datalen2;
                            }
                            entry2 = entry4;
                            datalen = datalen3;
                            for (DumpArchiveEntry e2 : this.queue) {
                                this.pending.remove(Integer.valueOf(e2.getIno()));
                            }
                        }
                        i += reclen;
                        entry4 = entry2;
                        datalen3 = datalen;
                    }
                    int i2 = datalen3;
                    byte[] peekBytes = this.raw.peek();
                    if (DumpArchiveUtil.verify(peekBytes)) {
                        DumpArchiveEntry entry5 = DumpArchiveEntry.parse(peekBytes);
                        first = false;
                        size -= PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                        entry4 = entry5;
                    } else {
                        throw new InvalidFormatException();
                    }
                } else {
                    int i3 = datalen3;
                    throw new EOFException();
                }
            } else {
                return;
            }
        }
    }

    private String getPath(DumpArchiveEntry entry) {
        Stack<String> elements = new Stack<>();
        int i = entry.getIno();
        while (true) {
            if (!this.names.containsKey(Integer.valueOf(i))) {
                elements.clear();
                break;
            }
            Dirent dirent = this.names.get(Integer.valueOf(i));
            elements.push(dirent.getName());
            if (dirent.getIno() == dirent.getParentIno()) {
                break;
            }
            i = dirent.getParentIno();
        }
        if (elements.isEmpty() != 0) {
            this.pending.put(Integer.valueOf(entry.getIno()), entry);
            return null;
        }
        StringBuilder sb = new StringBuilder(elements.pop());
        while (!elements.isEmpty()) {
            sb.append('/');
            sb.append(elements.pop());
        }
        return sb.toString();
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        int totalRead = 0;
        if (this.hasHitEOF || this.isClosed) {
            return -1;
        }
        long j = this.entryOffset;
        long j2 = this.entrySize;
        if (j >= j2) {
            return -1;
        }
        if (this.active != null) {
            if (((long) len) + j > j2) {
                len = (int) (j2 - j);
            }
            while (len > 0) {
                byte[] bArr = this.readBuf;
                int length = bArr.length;
                int i = this.recordOffset;
                int sz = len > length - i ? bArr.length - i : len;
                int i2 = this.recordOffset;
                int i3 = i2 + sz;
                byte[] bArr2 = this.readBuf;
                if (i3 <= bArr2.length) {
                    System.arraycopy(bArr2, i2, buf, off, sz);
                    totalRead += sz;
                    this.recordOffset += sz;
                    len -= sz;
                    off += sz;
                }
                if (len > 0) {
                    if (this.readIdx >= 512) {
                        byte[] headerBytes = this.raw.readRecord();
                        if (DumpArchiveUtil.verify(headerBytes)) {
                            this.active = DumpArchiveEntry.parse(headerBytes);
                            this.readIdx = 0;
                        } else {
                            throw new InvalidFormatException();
                        }
                    }
                    DumpArchiveEntry dumpArchiveEntry = this.active;
                    int i4 = this.readIdx;
                    this.readIdx = i4 + 1;
                    if (!dumpArchiveEntry.isSparseRecord(i4)) {
                        TapeInputStream tapeInputStream = this.raw;
                        byte[] bArr3 = this.readBuf;
                        if (tapeInputStream.read(bArr3, 0, bArr3.length) != this.readBuf.length) {
                            throw new EOFException();
                        }
                    } else {
                        Arrays.fill(this.readBuf, (byte) 0);
                    }
                    this.recordOffset = 0;
                }
            }
            this.entryOffset += (long) totalRead;
            return totalRead;
        }
        throw new IllegalStateException("No current dump entry");
    }

    public void close() throws IOException {
        if (!this.isClosed) {
            this.isClosed = true;
            this.raw.close();
        }
    }

    public static boolean matches(byte[] buffer, int length) {
        if (length < 32) {
            return false;
        }
        if (length >= 1024) {
            return DumpArchiveUtil.verify(buffer);
        }
        if (60012 == DumpArchiveUtil.convert32(buffer, 24)) {
            return true;
        }
        return false;
    }
}

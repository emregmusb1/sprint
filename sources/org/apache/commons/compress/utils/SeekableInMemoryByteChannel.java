package org.apache.commons.compress.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class SeekableInMemoryByteChannel implements SeekableByteChannel {
    private static final int NAIVE_RESIZE_LIMIT = 1073741823;
    private final AtomicBoolean closed;
    private byte[] data;
    private int position;
    private int size;

    public SeekableInMemoryByteChannel(byte[] data2) {
        this.closed = new AtomicBoolean();
        this.data = data2;
        this.size = data2.length;
    }

    public SeekableInMemoryByteChannel() {
        this(new byte[0]);
    }

    public SeekableInMemoryByteChannel(int size2) {
        this(new byte[size2]);
    }

    public long position() {
        return (long) this.position;
    }

    public SeekableByteChannel position(long newPosition) throws IOException {
        ensureOpen();
        if (newPosition < 0 || newPosition > 2147483647L) {
            throw new IllegalArgumentException("Position has to be in range 0.. 2147483647");
        }
        this.position = (int) newPosition;
        return this;
    }

    public long size() {
        return (long) this.size;
    }

    public SeekableByteChannel truncate(long newSize) {
        if (((long) this.size) > newSize) {
            this.size = (int) newSize;
        }
        repositionIfNecessary();
        return this;
    }

    public int read(ByteBuffer buf) throws IOException {
        ensureOpen();
        repositionIfNecessary();
        int wanted = buf.remaining();
        int possible = this.size - this.position;
        if (possible <= 0) {
            return -1;
        }
        if (wanted > possible) {
            wanted = possible;
        }
        buf.put(this.data, this.position, wanted);
        this.position += wanted;
        return wanted;
    }

    public void close() {
        this.closed.set(true);
    }

    public boolean isOpen() {
        return !this.closed.get();
    }

    public int write(ByteBuffer b) throws IOException {
        ensureOpen();
        int wanted = b.remaining();
        int i = this.size;
        int i2 = this.position;
        if (wanted > i - i2) {
            int newSize = i2 + wanted;
            if (newSize < 0) {
                resize(Integer.MAX_VALUE);
                wanted = Integer.MAX_VALUE - this.position;
            } else {
                resize(newSize);
            }
        }
        b.get(this.data, this.position, wanted);
        this.position += wanted;
        int i3 = this.size;
        int i4 = this.position;
        if (i3 < i4) {
            this.size = i4;
        }
        return wanted;
    }

    public byte[] array() {
        return this.data;
    }

    private void resize(int newLength) {
        int len = this.data.length;
        if (len <= 0) {
            len = 1;
        }
        if (newLength < NAIVE_RESIZE_LIMIT) {
            while (len < newLength) {
                len <<= 1;
            }
        } else {
            len = newLength;
        }
        this.data = Arrays.copyOf(this.data, len);
    }

    private void ensureOpen() throws ClosedChannelException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }
    }

    private void repositionIfNecessary() {
        int i = this.position;
        int i2 = this.size;
        if (i > i2) {
            this.position = i2;
        }
    }
}

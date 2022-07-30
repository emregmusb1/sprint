package org.apache.commons.compress.utils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MultiReadOnlySeekableByteChannel implements SeekableByteChannel {
    private final List<SeekableByteChannel> channels;
    private int currentChannelIdx;
    private long globalPosition;

    public MultiReadOnlySeekableByteChannel(List<SeekableByteChannel> channels2) {
        this.channels = Collections.unmodifiableList(new ArrayList((Collection) Objects.requireNonNull(channels2, "channels must not be null")));
    }

    public synchronized int read(ByteBuffer dst) throws IOException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        } else if (!dst.hasRemaining()) {
            return 0;
        } else {
            int totalBytesRead = 0;
            while (dst.hasRemaining() && this.currentChannelIdx < this.channels.size()) {
                SeekableByteChannel currentChannel = this.channels.get(this.currentChannelIdx);
                int newBytesRead = currentChannel.read(dst);
                if (newBytesRead == -1) {
                    this.currentChannelIdx++;
                } else {
                    if (currentChannel.position() >= currentChannel.size()) {
                        this.currentChannelIdx++;
                    }
                    totalBytesRead += newBytesRead;
                }
            }
            if (totalBytesRead <= 0) {
                return -1;
            }
            this.globalPosition += (long) totalBytesRead;
            return totalBytesRead;
        }
    }

    public void close() throws IOException {
        IOException first = null;
        for (SeekableByteChannel ch : this.channels) {
            try {
                ch.close();
            } catch (IOException ex) {
                if (first == null) {
                    first = ex;
                }
            }
        }
        if (first != null) {
            throw new IOException("failed to close wrapped channel", first);
        }
    }

    public boolean isOpen() {
        for (SeekableByteChannel ch : this.channels) {
            if (!ch.isOpen()) {
                return false;
            }
        }
        return true;
    }

    public long position() {
        return this.globalPosition;
    }

    public long size() throws IOException {
        long acc = 0;
        for (SeekableByteChannel ch : this.channels) {
            acc += ch.size();
        }
        return acc;
    }

    public SeekableByteChannel truncate(long size) {
        throw new NonWritableChannelException();
    }

    public int write(ByteBuffer src) {
        throw new NonWritableChannelException();
    }

    public synchronized SeekableByteChannel position(long newPosition) throws IOException {
        long newChannelPos;
        if (newPosition >= 0) {
            try {
                if (isOpen()) {
                    this.globalPosition = newPosition;
                    long pos = newPosition;
                    for (int i = 0; i < this.channels.size(); i++) {
                        SeekableByteChannel currentChannel = this.channels.get(i);
                        long size = currentChannel.size();
                        if (pos == -1) {
                            newChannelPos = 0;
                        } else if (pos <= size) {
                            this.currentChannelIdx = i;
                            newChannelPos = pos;
                            pos = -1;
                        } else {
                            pos -= size;
                            newChannelPos = size;
                        }
                        currentChannel.position(newChannelPos);
                    }
                } else {
                    throw new ClosedChannelException();
                }
            } catch (Throwable th) {
                throw th;
            }
        } else {
            throw new IllegalArgumentException("Negative position: " + newPosition);
        }
        return this;
    }

    public static SeekableByteChannel forSeekableByteChannels(SeekableByteChannel... channels2) {
        if (((SeekableByteChannel[]) Objects.requireNonNull(channels2, "channels must not be null")).length == 1) {
            return channels2[0];
        }
        return new MultiReadOnlySeekableByteChannel(Arrays.asList(channels2));
    }

    public static SeekableByteChannel forFiles(File... files) throws IOException {
        List<SeekableByteChannel> channels2 = new ArrayList<>();
        for (File f : (File[]) Objects.requireNonNull(files, "files must not be null")) {
            channels2.add(Files.newByteChannel(f.toPath(), new OpenOption[]{StandardOpenOption.READ}));
        }
        if (channels2.size() == 1) {
            return channels2.get(0);
        }
        return new MultiReadOnlySeekableByteChannel(channels2);
    }
}

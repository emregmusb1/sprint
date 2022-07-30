package org.apache.commons.compress.archivers.zip;

class CircularBuffer {
    private final byte[] buffer;
    private int readIndex;
    private final int size;
    private int writeIndex;

    CircularBuffer(int size2) {
        this.size = size2;
        this.buffer = new byte[size2];
    }

    public boolean available() {
        return this.readIndex != this.writeIndex;
    }

    public void put(int value) {
        byte[] bArr = this.buffer;
        int i = this.writeIndex;
        bArr[i] = (byte) value;
        this.writeIndex = (i + 1) % this.size;
    }

    public int get() {
        if (!available()) {
            return -1;
        }
        byte[] bArr = this.buffer;
        int i = this.readIndex;
        byte value = bArr[i];
        this.readIndex = (i + 1) % this.size;
        return value & 255;
    }

    public void copy(int distance, int length) {
        int pos1 = this.writeIndex - distance;
        int pos2 = pos1 + length;
        for (int i = pos1; i < pos2; i++) {
            byte[] bArr = this.buffer;
            int i2 = this.writeIndex;
            int i3 = this.size;
            bArr[i2] = bArr[(i + i3) % i3];
            this.writeIndex = (i2 + 1) % i3;
        }
    }
}

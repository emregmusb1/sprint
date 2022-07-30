package org.apache.commons.compress.compressors.lzw;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import org.apache.commons.compress.MemoryLimitException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.BitInputStream;
import org.apache.commons.compress.utils.InputStreamStatistics;

public abstract class LZWInputStream extends CompressorInputStream implements InputStreamStatistics {
    protected static final int DEFAULT_CODE_SIZE = 9;
    protected static final int UNUSED_PREFIX = -1;
    private byte[] characters;
    private int clearCode = -1;
    private int codeSize = 9;
    protected final BitInputStream in;
    private final byte[] oneByte = new byte[1];
    private byte[] outputStack;
    private int outputStackLocation;
    private int[] prefixes;
    private int previousCode = -1;
    private byte previousCodeFirstChar;
    private int tableSize;

    /* access modifiers changed from: protected */
    public abstract int addEntry(int i, byte b) throws IOException;

    /* access modifiers changed from: protected */
    public abstract int decompressNextSymbol() throws IOException;

    protected LZWInputStream(InputStream inputStream, ByteOrder byteOrder) {
        this.in = new BitInputStream(inputStream, byteOrder);
    }

    public void close() throws IOException {
        this.in.close();
    }

    public int read() throws IOException {
        int ret = read(this.oneByte);
        if (ret < 0) {
            return ret;
        }
        return this.oneByte[0] & 255;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = readFromStack(b, off, len);
        while (len - bytesRead > 0) {
            int result = decompressNextSymbol();
            if (result >= 0) {
                bytesRead += readFromStack(b, off + bytesRead, len - bytesRead);
            } else if (bytesRead <= 0) {
                return result;
            } else {
                count(bytesRead);
                return bytesRead;
            }
        }
        count(bytesRead);
        return bytesRead;
    }

    public long getCompressedCount() {
        return this.in.getBytesRead();
    }

    /* access modifiers changed from: protected */
    public void setClearCode(int codeSize2) {
        this.clearCode = 1 << (codeSize2 - 1);
    }

    /* access modifiers changed from: protected */
    public void initializeTables(int maxCodeSize, int memoryLimitInKb) throws MemoryLimitException {
        if (maxCodeSize > 0) {
            if (memoryLimitInKb > -1) {
                long memoryUsageInKb = (((long) (1 << maxCodeSize)) * 6) >> 10;
                if (memoryUsageInKb > ((long) memoryLimitInKb)) {
                    throw new MemoryLimitException(memoryUsageInKb, memoryLimitInKb);
                }
            }
            initializeTables(maxCodeSize);
            return;
        }
        throw new IllegalArgumentException("maxCodeSize is " + maxCodeSize + ", must be bigger than 0");
    }

    /* access modifiers changed from: protected */
    public void initializeTables(int maxCodeSize) {
        if (maxCodeSize > 0) {
            int maxTableSize = 1 << maxCodeSize;
            this.prefixes = new int[maxTableSize];
            this.characters = new byte[maxTableSize];
            this.outputStack = new byte[maxTableSize];
            this.outputStackLocation = maxTableSize;
            for (int i = 0; i < 256; i++) {
                this.prefixes[i] = -1;
                this.characters[i] = (byte) i;
            }
            return;
        }
        throw new IllegalArgumentException("maxCodeSize is " + maxCodeSize + ", must be bigger than 0");
    }

    /* access modifiers changed from: protected */
    public int readNextCode() throws IOException {
        int i = this.codeSize;
        if (i <= 31) {
            return (int) this.in.readBits(i);
        }
        throw new IllegalArgumentException("Code size must not be bigger than 31");
    }

    /* access modifiers changed from: protected */
    public int addEntry(int previousCode2, byte character, int maxTableSize) {
        int i = this.tableSize;
        if (i >= maxTableSize) {
            return -1;
        }
        this.prefixes[i] = previousCode2;
        this.characters[i] = character;
        this.tableSize = i + 1;
        return i;
    }

    /* access modifiers changed from: protected */
    public int addRepeatOfPreviousCode() throws IOException {
        int i = this.previousCode;
        if (i != -1) {
            return addEntry(i, this.previousCodeFirstChar);
        }
        throw new IOException("The first code can't be a reference to its preceding code");
    }

    /* access modifiers changed from: protected */
    public int expandCodeToOutputStack(int code, boolean addedUnfinishedEntry) throws IOException {
        int entry = code;
        while (entry >= 0) {
            byte[] bArr = this.outputStack;
            int i = this.outputStackLocation - 1;
            this.outputStackLocation = i;
            bArr[i] = this.characters[entry];
            entry = this.prefixes[entry];
        }
        int entry2 = this.previousCode;
        if (entry2 != -1 && !addedUnfinishedEntry) {
            addEntry(entry2, this.outputStack[this.outputStackLocation]);
        }
        this.previousCode = code;
        byte[] bArr2 = this.outputStack;
        int i2 = this.outputStackLocation;
        this.previousCodeFirstChar = bArr2[i2];
        return i2;
    }

    private int readFromStack(byte[] b, int off, int len) {
        int remainingInStack = this.outputStack.length - this.outputStackLocation;
        if (remainingInStack <= 0) {
            return 0;
        }
        int maxLength = Math.min(remainingInStack, len);
        System.arraycopy(this.outputStack, this.outputStackLocation, b, off, maxLength);
        this.outputStackLocation += maxLength;
        return maxLength;
    }

    /* access modifiers changed from: protected */
    public int getCodeSize() {
        return this.codeSize;
    }

    /* access modifiers changed from: protected */
    public void resetCodeSize() {
        setCodeSize(9);
    }

    /* access modifiers changed from: protected */
    public void setCodeSize(int cs) {
        this.codeSize = cs;
    }

    /* access modifiers changed from: protected */
    public void incrementCodeSize() {
        this.codeSize++;
    }

    /* access modifiers changed from: protected */
    public void resetPreviousCode() {
        this.previousCode = -1;
    }

    /* access modifiers changed from: protected */
    public int getPrefix(int offset) {
        return this.prefixes[offset];
    }

    /* access modifiers changed from: protected */
    public void setPrefix(int offset, int value) {
        this.prefixes[offset] = value;
    }

    /* access modifiers changed from: protected */
    public int getPrefixesLength() {
        return this.prefixes.length;
    }

    /* access modifiers changed from: protected */
    public int getClearCode() {
        return this.clearCode;
    }

    /* access modifiers changed from: protected */
    public int getTableSize() {
        return this.tableSize;
    }

    /* access modifiers changed from: protected */
    public void setTableSize(int newSize) {
        this.tableSize = newSize;
    }
}

package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import org.apache.commons.compress.compressors.lzw.LZWInputStream;

class UnshrinkingInputStream extends LZWInputStream {
    private static final int MAX_CODE_SIZE = 13;
    private static final int MAX_TABLE_SIZE = 8192;
    private final boolean[] isUsed = new boolean[getPrefixesLength()];

    public UnshrinkingInputStream(InputStream inputStream) throws IOException {
        super(inputStream, ByteOrder.LITTLE_ENDIAN);
        setClearCode(9);
        initializeTables(13);
        for (int i = 0; i < 256; i++) {
            this.isUsed[i] = true;
        }
        setTableSize(getClearCode() + 1);
    }

    /* access modifiers changed from: protected */
    public int addEntry(int previousCode, byte character) throws IOException {
        int tableSize = getTableSize();
        while (tableSize < 8192 && this.isUsed[tableSize]) {
            tableSize++;
        }
        setTableSize(tableSize);
        int idx = addEntry(previousCode, character, 8192);
        if (idx >= 0) {
            this.isUsed[idx] = true;
        }
        return idx;
    }

    private void partialClear() {
        boolean[] isParent = new boolean[8192];
        int i = 0;
        while (true) {
            boolean[] zArr = this.isUsed;
            if (i >= zArr.length) {
                break;
            }
            if (zArr[i] && getPrefix(i) != -1) {
                isParent[getPrefix(i)] = true;
            }
            i++;
        }
        for (int i2 = getClearCode() + 1; i2 < isParent.length; i2++) {
            if (!isParent[i2]) {
                this.isUsed[i2] = false;
                setPrefix(i2, -1);
            }
        }
    }

    /* access modifiers changed from: protected */
    public int decompressNextSymbol() throws IOException {
        int code = readNextCode();
        if (code < 0) {
            return -1;
        }
        if (code == getClearCode()) {
            int subCode = readNextCode();
            if (subCode < 0) {
                throw new IOException("Unexpected EOF;");
            } else if (subCode == 1) {
                if (getCodeSize() < 13) {
                    incrementCodeSize();
                    return 0;
                }
                throw new IOException("Attempt to increase code size beyond maximum");
            } else if (subCode == 2) {
                partialClear();
                setTableSize(getClearCode() + 1);
                return 0;
            } else {
                throw new IOException("Invalid clear code subcode " + subCode);
            }
        } else {
            boolean addedUnfinishedEntry = false;
            int effectiveCode = code;
            if (!this.isUsed[code]) {
                effectiveCode = addRepeatOfPreviousCode();
                addedUnfinishedEntry = true;
            }
            return expandCodeToOutputStack(effectiveCode, addedUnfinishedEntry);
        }
    }
}

package org.apache.commons.compress.compressors.bzip2;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.utils.BitInputStream;
import org.apache.commons.compress.utils.CloseShieldFilterInputStream;
import org.apache.commons.compress.utils.InputStreamStatistics;

public class BZip2CompressorInputStream extends CompressorInputStream implements BZip2Constants, InputStreamStatistics {
    private static final int EOF = 0;
    private static final int NO_RAND_PART_A_STATE = 5;
    private static final int NO_RAND_PART_B_STATE = 6;
    private static final int NO_RAND_PART_C_STATE = 7;
    private static final int RAND_PART_A_STATE = 2;
    private static final int RAND_PART_B_STATE = 3;
    private static final int RAND_PART_C_STATE = 4;
    private static final int START_BLOCK_STATE = 1;
    private BitInputStream bin;
    private boolean blockRandomised;
    private int blockSize100k;
    private int computedBlockCRC;
    private int computedCombinedCRC;
    private final CRC crc;
    private int currentState;
    private Data data;
    private final boolean decompressConcatenated;
    private int last;
    private int nInUse;
    private int origPtr;
    private int storedBlockCRC;
    private int storedCombinedCRC;
    private int su_ch2;
    private int su_chPrev;
    private int su_count;
    private int su_i2;
    private int su_j2;
    private int su_rNToGo;
    private int su_rTPos;
    private int su_tPos;
    private char su_z;

    public BZip2CompressorInputStream(InputStream in) throws IOException {
        this(in, false);
    }

    public BZip2CompressorInputStream(InputStream in, boolean decompressConcatenated2) throws IOException {
        this.crc = new CRC();
        this.currentState = 1;
        this.bin = new BitInputStream(in == System.in ? new CloseShieldFilterInputStream(in) : in, ByteOrder.BIG_ENDIAN);
        this.decompressConcatenated = decompressConcatenated2;
        init(true);
        initBlock();
    }

    public int read() throws IOException {
        if (this.bin != null) {
            int r = read0();
            count(r < 0 ? -1 : 1);
            return r;
        }
        throw new IOException("Stream closed");
    }

    public int read(byte[] dest, int offs, int len) throws IOException {
        if (offs < 0) {
            throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
        } else if (len < 0) {
            throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
        } else if (offs + len > dest.length) {
            throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" + len + ") > dest.length(" + dest.length + ").");
        } else if (this.bin == null) {
            throw new IOException("Stream closed");
        } else if (len == 0) {
            return 0;
        } else {
            int hi = offs + len;
            int destOffs = offs;
            while (destOffs < hi) {
                int read0 = read0();
                int b = read0;
                if (read0 < 0) {
                    break;
                }
                dest[destOffs] = (byte) b;
                count(1);
                destOffs++;
            }
            if (destOffs == offs) {
                return -1;
            }
            return destOffs - offs;
        }
    }

    public long getCompressedCount() {
        return this.bin.getBytesRead();
    }

    private void makeMaps() {
        boolean[] inUse = this.data.inUse;
        byte[] seqToUnseq = this.data.seqToUnseq;
        int nInUseShadow = 0;
        for (int i = 0; i < 256; i++) {
            if (inUse[i]) {
                seqToUnseq[nInUseShadow] = (byte) i;
                nInUseShadow++;
            }
        }
        this.nInUse = nInUseShadow;
    }

    private int read0() throws IOException {
        switch (this.currentState) {
            case 0:
                return -1;
            case 1:
                return setupBlock();
            case 2:
                throw new IllegalStateException();
            case 3:
                return setupRandPartB();
            case 4:
                return setupRandPartC();
            case 5:
                throw new IllegalStateException();
            case 6:
                return setupNoRandPartB();
            case 7:
                return setupNoRandPartC();
            default:
                throw new IllegalStateException();
        }
    }

    private int readNextByte(BitInputStream in) throws IOException {
        return (int) in.readBits(8);
    }

    private boolean init(boolean isFirstStream) throws IOException {
        BitInputStream bitInputStream = this.bin;
        if (bitInputStream != null) {
            if (!isFirstStream) {
                bitInputStream.clearBitCache();
            }
            int magic0 = readNextByte(this.bin);
            if (magic0 == -1 && !isFirstStream) {
                return false;
            }
            int magic1 = readNextByte(this.bin);
            int magic2 = readNextByte(this.bin);
            if (magic0 == 66 && magic1 == 90 && magic2 == 104) {
                int blockSize = readNextByte(this.bin);
                if (blockSize < 49 || blockSize > 57) {
                    throw new IOException("BZip2 block size is invalid");
                }
                this.blockSize100k = blockSize - 48;
                this.computedCombinedCRC = 0;
                return true;
            }
            throw new IOException(isFirstStream ? "Stream is not in the BZip2 format" : "Garbage after a valid BZip2 stream");
        }
        throw new IOException("No InputStream");
    }

    private void initBlock() throws IOException {
        BitInputStream bin2 = this.bin;
        do {
            char magic0 = bsGetUByte(bin2);
            char magic1 = bsGetUByte(bin2);
            char magic2 = bsGetUByte(bin2);
            char magic3 = bsGetUByte(bin2);
            char magic4 = bsGetUByte(bin2);
            char magic5 = bsGetUByte(bin2);
            if (magic0 != 23 || magic1 != 'r' || magic2 != 'E' || magic3 != '8' || magic4 != 'P' || magic5 != 144) {
                boolean z = false;
                if (magic0 == '1' && magic1 == 'A' && magic2 == 'Y' && magic3 == '&' && magic4 == 'S' && magic5 == 'Y') {
                    this.storedBlockCRC = bsGetInt(bin2);
                    if (bsR(bin2, 1) == 1) {
                        z = true;
                    }
                    this.blockRandomised = z;
                    if (this.data == null) {
                        this.data = new Data(this.blockSize100k);
                    }
                    getAndMoveToFrontDecode();
                    this.crc.initialiseCRC();
                    this.currentState = 1;
                    return;
                }
                this.currentState = 0;
                throw new IOException("Bad block header");
            }
        } while (!complete());
    }

    private void endBlock() throws IOException {
        this.computedBlockCRC = this.crc.getFinalCRC();
        int i = this.storedBlockCRC;
        int i2 = this.computedBlockCRC;
        if (i == i2) {
            int i3 = this.computedCombinedCRC;
            this.computedCombinedCRC = (i3 >>> 31) | (i3 << 1);
            this.computedCombinedCRC ^= i2;
            return;
        }
        int i4 = this.storedCombinedCRC;
        this.computedCombinedCRC = (i4 >>> 31) | (i4 << 1);
        this.computedCombinedCRC = i ^ this.computedCombinedCRC;
        throw new IOException("BZip2 CRC error");
    }

    private boolean complete() throws IOException {
        this.storedCombinedCRC = bsGetInt(this.bin);
        this.currentState = 0;
        this.data = null;
        if (this.storedCombinedCRC != this.computedCombinedCRC) {
            throw new IOException("BZip2 CRC error");
        } else if (!this.decompressConcatenated || !init(false)) {
            return true;
        } else {
            return false;
        }
    }

    public void close() throws IOException {
        BitInputStream inShadow = this.bin;
        if (inShadow != null) {
            try {
                inShadow.close();
            } finally {
                this.data = null;
                this.bin = null;
            }
        }
    }

    private static int bsR(BitInputStream bin2, int n) throws IOException {
        long thech = bin2.readBits(n);
        if (thech >= 0) {
            return (int) thech;
        }
        throw new IOException("Unexpected end of stream");
    }

    private static boolean bsGetBit(BitInputStream bin2) throws IOException {
        return bsR(bin2, 1) != 0;
    }

    private static char bsGetUByte(BitInputStream bin2) throws IOException {
        return (char) bsR(bin2, 8);
    }

    private static int bsGetInt(BitInputStream bin2) throws IOException {
        return bsR(bin2, 32);
    }

    private static void checkBounds(int checkVal, int limitExclusive, String name) throws IOException {
        if (checkVal < 0) {
            throw new IOException("Corrupted input, " + name + " value negative");
        } else if (checkVal >= limitExclusive) {
            throw new IOException("Corrupted input, " + name + " value too big");
        }
    }

    private static void hbCreateDecodeTables(int[] limit, int[] base, int[] perm, char[] length, int minLen, int maxLen, int alphaSize) throws IOException {
        int pp = 0;
        for (int i = minLen; i <= maxLen; i++) {
            for (int j = 0; j < alphaSize; j++) {
                if (length[j] == i) {
                    perm[pp] = j;
                    pp++;
                }
            }
        }
        int i2 = 23;
        while (true) {
            i2--;
            if (i2 <= 0) {
                break;
            }
            base[i2] = 0;
            limit[i2] = 0;
        }
        for (int i3 = 0; i3 < alphaSize; i3++) {
            char l = length[i3];
            checkBounds(l, BZip2Constants.MAX_ALPHA_SIZE, "length");
            int i4 = l + 1;
            base[i4] = base[i4] + 1;
        }
        int b = base[0];
        for (int i5 = 1; i5 < 23; i5++) {
            b += base[i5];
            base[i5] = b;
        }
        int i6 = minLen;
        int vec = 0;
        int b2 = base[i6];
        while (i6 <= maxLen) {
            int nb = base[i6 + 1];
            int vec2 = vec + (nb - b2);
            b2 = nb;
            limit[i6] = vec2 - 1;
            vec = vec2 << 1;
            i6++;
        }
        for (int i7 = minLen + 1; i7 <= maxLen; i7++) {
            base[i7] = ((limit[i7 - 1] + 1) << 1) - base[i7];
        }
    }

    private void recvDecodingTables() throws IOException {
        BitInputStream bin2 = this.bin;
        Data dataShadow = this.data;
        boolean[] inUse = dataShadow.inUse;
        byte[] pos = dataShadow.recvDecodingTables_pos;
        byte[] selector = dataShadow.selector;
        byte[] selectorMtf = dataShadow.selectorMtf;
        int inUse16 = 0;
        for (int i = 0; i < 16; i++) {
            if (bsGetBit(bin2)) {
                inUse16 |= 1 << i;
            }
        }
        Arrays.fill(inUse, false);
        for (int i2 = 0; i2 < 16; i2++) {
            if (((1 << i2) & inUse16) != 0) {
                int i16 = i2 << 4;
                for (int j = 0; j < 16; j++) {
                    if (bsGetBit(bin2)) {
                        inUse[i16 + j] = true;
                    }
                }
            }
        }
        makeMaps();
        int alphaSize = this.nInUse + 2;
        int nGroups = bsR(bin2, 3);
        int nSelectors = bsR(bin2, 15);
        checkBounds(alphaSize, 259, "alphaSize");
        checkBounds(nGroups, 7, "nGroups");
        checkBounds(nSelectors, 18003, "nSelectors");
        for (int i3 = 0; i3 < nSelectors; i3++) {
            int j2 = 0;
            while (bsGetBit(bin2)) {
                j2++;
            }
            selectorMtf[i3] = (byte) j2;
        }
        int v = nGroups;
        while (true) {
            v--;
            if (v < 0) {
                break;
            }
            pos[v] = (byte) v;
        }
        for (int i4 = 0; i4 < nSelectors; i4++) {
            int v2 = selectorMtf[i4] & 255;
            checkBounds(v2, 6, "selectorMtf");
            byte tmp = pos[v2];
            while (v2 > 0) {
                pos[v2] = pos[v2 - 1];
                v2--;
            }
            pos[0] = tmp;
            selector[i4] = tmp;
        }
        char[][] len = dataShadow.temp_charArray2d;
        for (int t = 0; t < nGroups; t++) {
            int curr = bsR(bin2, 5);
            char[] len_t = len[t];
            int i5 = 0;
            while (i5 < alphaSize) {
                while (bsGetBit(bin2)) {
                    curr += bsGetBit(bin2) ? -1 : 1;
                }
                len_t[i5] = (char) curr;
                i5++;
                bin2 = bin2;
            }
        }
        createHuffmanDecodingTables(alphaSize, nGroups);
    }

    private void createHuffmanDecodingTables(int alphaSize, int nGroups) throws IOException {
        Data dataShadow = this.data;
        char[][] len = dataShadow.temp_charArray2d;
        int[] minLens = dataShadow.minLens;
        int[][] limit = dataShadow.limit;
        int[][] base = dataShadow.base;
        int[][] perm = dataShadow.perm;
        for (int t = 0; t < nGroups; t++) {
            int minLen = 32;
            char c = 0;
            char[] len_t = len[t];
            int i = alphaSize;
            while (true) {
                i--;
                if (i < 0) {
                    break;
                }
                char lent = len_t[i];
                if (lent > c) {
                    c = lent;
                }
                if (lent < minLen) {
                    minLen = lent;
                }
            }
            hbCreateDecodeTables(limit[t], base[t], perm[t], len[t], minLen, c, alphaSize);
            minLens[t] = minLen;
        }
    }

    private void getAndMoveToFrontDecode() throws IOException {
        String str;
        int limitLast;
        int nextSym;
        String str2;
        String str3;
        int lastShadow;
        String str4;
        int groupPos;
        char c;
        BitInputStream bin2 = this.bin;
        this.origPtr = bsR(bin2, 24);
        recvDecodingTables();
        Data dataShadow = this.data;
        byte[] ll8 = dataShadow.ll8;
        int[] unzftab = dataShadow.unzftab;
        byte[] selector = dataShadow.selector;
        byte[] seqToUnseq = dataShadow.seqToUnseq;
        char[] yy = dataShadow.getAndMoveToFrontDecode_yy;
        int[] minLens = dataShadow.minLens;
        int[][] limit = dataShadow.limit;
        int[][] base = dataShadow.base;
        int[][] perm = dataShadow.perm;
        int limitLast2 = this.blockSize100k * BZip2Constants.BASEBLOCKSIZE;
        int i = 256;
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            yy[i] = (char) i;
            unzftab[i] = 0;
        }
        Data data2 = dataShadow;
        int limitLast3 = this.nInUse + 1;
        int nextSym2 = getAndMoveToFrontDecode0();
        int i2 = selector[0] & 255;
        int groupNo = 0;
        int groupPos2 = 49;
        checkBounds(i2, 6, "zt");
        int[] base_zt = base[i2];
        int[] limit_zt = limit[i2];
        int[] perm_zt = perm[i2];
        int minLens_zt = minLens[i2];
        int lastShadow2 = -1;
        int lastShadow3 = i2;
        int zt = nextSym2;
        while (zt != limitLast3) {
            int eob = limitLast3;
            String str5 = "groupNo";
            String str6 = "zvec";
            String str7 = " exceeds ";
            BitInputStream bin3 = bin2;
            String str8 = "zn";
            if (zt == 0) {
                nextSym = zt;
                limitLast = limitLast2;
                str2 = str6;
                str = str7;
                bin2 = bin3;
                str3 = str8;
            } else if (zt == 1) {
                nextSym = zt;
                limitLast = limitLast2;
                str2 = str6;
                str = str7;
                bin2 = bin3;
                str3 = str8;
            } else {
                lastShadow2++;
                if (lastShadow2 < limitLast2) {
                    int limitLast4 = limitLast2;
                    checkBounds(zt, 257, "nextSym");
                    char tmp = yy[zt - 1];
                    checkBounds(tmp, 256, "yy");
                    byte b = seqToUnseq[tmp] & 255;
                    unzftab[b] = unzftab[b] + 1;
                    ll8[lastShadow2] = seqToUnseq[tmp];
                    if (zt <= 16) {
                        int j = zt - 1;
                        while (j > 0) {
                            int j2 = j - 1;
                            yy[j] = yy[j2];
                            j = j2;
                        }
                        int i3 = zt;
                        c = 0;
                    } else {
                        int i4 = zt;
                        c = 0;
                        System.arraycopy(yy, 0, yy, 1, zt - 1);
                    }
                    yy[c] = tmp;
                    if (groupPos2 == 0) {
                        int groupNo2 = groupNo + 1;
                        checkBounds(groupNo2, BZip2Constants.MAX_SELECTORS, str5);
                        byte b2 = selector[groupNo2] & 255;
                        checkBounds(b2, 6, "zt");
                        int[] base_zt2 = base[b2];
                        base_zt = base_zt2;
                        limit_zt = limit[b2];
                        perm_zt = perm[b2];
                        minLens_zt = minLens[b2];
                        byte b3 = b2;
                        groupPos2 = 49;
                        groupNo = groupNo2;
                    } else {
                        groupPos2--;
                    }
                    int zn = minLens_zt;
                    String str9 = str8;
                    checkBounds(zn, BZip2Constants.MAX_ALPHA_SIZE, str9);
                    BitInputStream bin4 = bin3;
                    int zn2 = zn;
                    int zvec = bsR(bin4, zn);
                    while (zvec > limit_zt[zn2]) {
                        int zn3 = zn2 + 1;
                        checkBounds(zn3, BZip2Constants.MAX_ALPHA_SIZE, str9);
                        zvec = (zvec << 1) | bsR(bin4, 1);
                        zn2 = zn3;
                        tmp = tmp;
                    }
                    int idx = zvec - base_zt[zn2];
                    checkBounds(idx, BZip2Constants.MAX_ALPHA_SIZE, str6);
                    zt = perm_zt[idx];
                    bin2 = bin4;
                    limitLast3 = eob;
                    limitLast2 = limitLast4;
                } else {
                    int i5 = zt;
                    BitInputStream bitInputStream = bin3;
                    throw new IOException("Block overrun in MTF, " + lastShadow2 + str7 + limitLast2);
                }
            }
            int s = -1;
            int n = 1;
            byte[] ll82 = ll8;
            int zvec2 = nextSym;
            while (true) {
                if (zvec2 != 0) {
                    lastShadow = lastShadow2;
                    if (zvec2 != 1) {
                        break;
                    }
                    s += n << 1;
                } else {
                    s += n;
                    lastShadow = lastShadow2;
                }
                if (groupPos2 == 0) {
                    int i6 = zvec2;
                    int nextSym3 = groupNo + 1;
                    groupPos = 49;
                    checkBounds(nextSym3, BZip2Constants.MAX_SELECTORS, str5);
                    byte b4 = selector[nextSym3] & 255;
                    str4 = str5;
                    checkBounds(b4, 6, "zt");
                    base_zt = base[b4];
                    limit_zt = limit[b4];
                    perm_zt = perm[b4];
                    minLens_zt = minLens[b4];
                    groupNo = nextSym3;
                    byte b5 = b4;
                } else {
                    str4 = str5;
                    int i7 = zvec2;
                    groupPos = groupPos2 - 1;
                }
                int zn4 = minLens_zt;
                int i8 = BZip2Constants.MAX_ALPHA_SIZE;
                checkBounds(zn4, BZip2Constants.MAX_ALPHA_SIZE, str3);
                int zn5 = zn4;
                int zvec3 = bsR(bin2, zn4);
                while (zvec3 > limit_zt[zn5]) {
                    int zn6 = zn5 + 1;
                    checkBounds(zn6, i8, str3);
                    zvec3 = (zvec3 << 1) | bsR(bin2, 1);
                    zn5 = zn6;
                    i8 = BZip2Constants.MAX_ALPHA_SIZE;
                }
                int tmp2 = zvec3 - base_zt[zn5];
                checkBounds(tmp2, BZip2Constants.MAX_ALPHA_SIZE, str2);
                zvec2 = perm_zt[tmp2];
                n <<= 1;
                lastShadow2 = lastShadow;
                str5 = str4;
            }
            int nextSym4 = zvec2;
            char yy0 = yy[0];
            checkBounds(yy0, 256, "yy");
            byte ch = seqToUnseq[yy0];
            byte b6 = ch & 255;
            unzftab[b6] = unzftab[b6] + s + 1;
            int lastShadow4 = lastShadow + 1;
            int from = lastShadow4;
            lastShadow2 = lastShadow4 + s;
            byte[] ll83 = ll82;
            Arrays.fill(ll83, from, lastShadow2 + 1, ch);
            int limitLast5 = limitLast;
            if (lastShadow2 < limitLast5) {
                ll8 = ll83;
                limitLast2 = limitLast5;
                limitLast3 = eob;
                zt = nextSym4;
            } else {
                byte[] bArr = ll83;
                StringBuilder sb = new StringBuilder();
                BitInputStream bitInputStream2 = bin2;
                sb.append("Block overrun while expanding RLE in MTF, ");
                sb.append(lastShadow2);
                sb.append(str);
                sb.append(limitLast5);
                throw new IOException(sb.toString());
            }
        }
        int i9 = zt;
        byte[] bArr2 = ll8;
        int i10 = limitLast3;
        int i11 = lastShadow2;
        int eob2 = limitLast2;
        this.last = lastShadow2;
    }

    private int getAndMoveToFrontDecode0() throws IOException {
        Data dataShadow = this.data;
        int zt = dataShadow.selector[0] & 255;
        checkBounds(zt, 6, "zt");
        int[] limit_zt = dataShadow.limit[zt];
        int zn = dataShadow.minLens[zt];
        checkBounds(zn, BZip2Constants.MAX_ALPHA_SIZE, "zn");
        int zvec = bsR(this.bin, zn);
        while (zvec > limit_zt[zn]) {
            zn++;
            checkBounds(zn, BZip2Constants.MAX_ALPHA_SIZE, "zn");
            zvec = (zvec << 1) | bsR(this.bin, 1);
        }
        int tmp = zvec - dataShadow.base[zt][zn];
        checkBounds(tmp, BZip2Constants.MAX_ALPHA_SIZE, "zvec");
        return dataShadow.perm[zt][tmp];
    }

    private int setupBlock() throws IOException {
        Data data2;
        if (this.currentState == 0 || (data2 = this.data) == null) {
            return -1;
        }
        int[] cftab = data2.cftab;
        int ttLen = this.last + 1;
        int[] tt = this.data.initTT(ttLen);
        byte[] ll8 = this.data.ll8;
        cftab[0] = 0;
        System.arraycopy(this.data.unzftab, 0, cftab, 1, 256);
        int c = cftab[0];
        for (int i = 1; i <= 256; i++) {
            c += cftab[i];
            cftab[i] = c;
        }
        int lastShadow = this.last;
        for (int i2 = 0; i2 <= lastShadow; i2++) {
            byte b = ll8[i2] & 255;
            int i3 = cftab[b];
            cftab[b] = i3 + 1;
            int tmp = i3;
            checkBounds(tmp, ttLen, "tt index");
            tt[tmp] = i2;
        }
        int i4 = this.origPtr;
        if (i4 < 0 || i4 >= tt.length) {
            throw new IOException("Stream corrupted");
        }
        this.su_tPos = tt[i4];
        this.su_count = 0;
        this.su_i2 = 0;
        this.su_ch2 = 256;
        if (!this.blockRandomised) {
            return setupNoRandPartA();
        }
        this.su_rNToGo = 0;
        this.su_rTPos = 0;
        return setupRandPartA();
    }

    private int setupRandPartA() throws IOException {
        if (this.su_i2 <= this.last) {
            this.su_chPrev = this.su_ch2;
            byte[] bArr = this.data.ll8;
            int i = this.su_tPos;
            int su_ch2Shadow = bArr[i] & 255;
            checkBounds(i, this.data.tt.length, "su_tPos");
            this.su_tPos = this.data.tt[this.su_tPos];
            int i2 = this.su_rNToGo;
            int i3 = 0;
            if (i2 == 0) {
                this.su_rNToGo = Rand.rNums(this.su_rTPos) - 1;
                int i4 = this.su_rTPos + 1;
                this.su_rTPos = i4;
                if (i4 == 512) {
                    this.su_rTPos = 0;
                }
            } else {
                this.su_rNToGo = i2 - 1;
            }
            if (this.su_rNToGo == 1) {
                i3 = 1;
            }
            int i5 = su_ch2Shadow ^ i3;
            int su_ch2Shadow2 = i5;
            this.su_ch2 = i5;
            this.su_i2++;
            this.currentState = 3;
            this.crc.updateCRC(su_ch2Shadow2);
            return su_ch2Shadow2;
        }
        endBlock();
        initBlock();
        return setupBlock();
    }

    private int setupNoRandPartA() throws IOException {
        if (this.su_i2 <= this.last) {
            this.su_chPrev = this.su_ch2;
            byte[] bArr = this.data.ll8;
            int i = this.su_tPos;
            int su_ch2Shadow = bArr[i] & 255;
            this.su_ch2 = su_ch2Shadow;
            checkBounds(i, this.data.tt.length, "su_tPos");
            this.su_tPos = this.data.tt[this.su_tPos];
            this.su_i2++;
            this.currentState = 6;
            this.crc.updateCRC(su_ch2Shadow);
            return su_ch2Shadow;
        }
        this.currentState = 5;
        endBlock();
        initBlock();
        return setupBlock();
    }

    private int setupRandPartB() throws IOException {
        if (this.su_ch2 != this.su_chPrev) {
            this.currentState = 2;
            this.su_count = 1;
            return setupRandPartA();
        }
        int i = this.su_count + 1;
        this.su_count = i;
        if (i >= 4) {
            byte[] bArr = this.data.ll8;
            int i2 = this.su_tPos;
            this.su_z = (char) (bArr[i2] & 255);
            checkBounds(i2, this.data.tt.length, "su_tPos");
            this.su_tPos = this.data.tt[this.su_tPos];
            int i3 = this.su_rNToGo;
            if (i3 == 0) {
                this.su_rNToGo = Rand.rNums(this.su_rTPos) - 1;
                int i4 = this.su_rTPos + 1;
                this.su_rTPos = i4;
                if (i4 == 512) {
                    this.su_rTPos = 0;
                }
            } else {
                this.su_rNToGo = i3 - 1;
            }
            this.su_j2 = 0;
            this.currentState = 4;
            if (this.su_rNToGo == 1) {
                this.su_z = (char) (this.su_z ^ 1);
            }
            return setupRandPartC();
        }
        this.currentState = 2;
        return setupRandPartA();
    }

    private int setupRandPartC() throws IOException {
        if (this.su_j2 < this.su_z) {
            this.crc.updateCRC(this.su_ch2);
            this.su_j2++;
            return this.su_ch2;
        }
        this.currentState = 2;
        this.su_i2++;
        this.su_count = 0;
        return setupRandPartA();
    }

    private int setupNoRandPartB() throws IOException {
        if (this.su_ch2 != this.su_chPrev) {
            this.su_count = 1;
            return setupNoRandPartA();
        }
        int i = this.su_count + 1;
        this.su_count = i;
        if (i < 4) {
            return setupNoRandPartA();
        }
        checkBounds(this.su_tPos, this.data.ll8.length, "su_tPos");
        this.su_z = (char) (this.data.ll8[this.su_tPos] & 255);
        this.su_tPos = this.data.tt[this.su_tPos];
        this.su_j2 = 0;
        return setupNoRandPartC();
    }

    private int setupNoRandPartC() throws IOException {
        if (this.su_j2 < this.su_z) {
            int su_ch2Shadow = this.su_ch2;
            this.crc.updateCRC(su_ch2Shadow);
            this.su_j2++;
            this.currentState = 7;
            return su_ch2Shadow;
        }
        this.su_i2++;
        this.su_count = 0;
        return setupNoRandPartA();
    }

    private static final class Data {
        final int[][] base = ((int[][]) Array.newInstance(int.class, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        final int[] cftab = new int[257];
        final char[] getAndMoveToFrontDecode_yy = new char[256];
        final boolean[] inUse = new boolean[256];
        final int[][] limit = ((int[][]) Array.newInstance(int.class, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        byte[] ll8;
        final int[] minLens = new int[6];
        final int[][] perm = ((int[][]) Array.newInstance(int.class, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        final byte[] recvDecodingTables_pos = new byte[6];
        final byte[] selector = new byte[BZip2Constants.MAX_SELECTORS];
        final byte[] selectorMtf = new byte[BZip2Constants.MAX_SELECTORS];
        final byte[] seqToUnseq = new byte[256];
        final char[][] temp_charArray2d = ((char[][]) Array.newInstance(char.class, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        int[] tt;
        final int[] unzftab = new int[256];

        Data(int blockSize100k) {
            this.ll8 = new byte[(BZip2Constants.BASEBLOCKSIZE * blockSize100k)];
        }

        /* access modifiers changed from: package-private */
        public int[] initTT(int length) {
            int[] ttShadow = this.tt;
            if (ttShadow != null && ttShadow.length >= length) {
                return ttShadow;
            }
            int[] iArr = new int[length];
            int[] ttShadow2 = iArr;
            this.tt = iArr;
            return ttShadow2;
        }
    }

    public static boolean matches(byte[] signature, int length) {
        return length >= 3 && signature[0] == 66 && signature[1] == 90 && signature[2] == 104;
    }
}

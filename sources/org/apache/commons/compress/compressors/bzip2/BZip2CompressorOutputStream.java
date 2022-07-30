package org.apache.commons.compress.compressors.bzip2;

import androidx.core.view.InputDeviceCompat;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import org.apache.commons.compress.compressors.CompressorOutputStream;

public class BZip2CompressorOutputStream extends CompressorOutputStream implements BZip2Constants {
    private static final int GREATER_ICOST = 15;
    private static final int LESSER_ICOST = 0;
    public static final int MAX_BLOCKSIZE = 9;
    public static final int MIN_BLOCKSIZE = 1;
    private final int allowableBlockSize;
    private int blockCRC;
    private final int blockSize100k;
    private BlockSort blockSorter;
    private int bsBuff;
    private int bsLive;
    private volatile boolean closed;
    private int combinedCRC;
    private final CRC crc;
    private int currentChar;
    private Data data;
    private int last;
    private int nInUse;
    private int nMTF;
    private OutputStream out;
    private int runLength;

    private static void hbMakeCodeLengths(byte[] len, int[] freq, Data dat, int alphaSize, int maxLen) {
        int i;
        int i2;
        Data data2 = dat;
        int i3 = alphaSize;
        int[] heap = data2.heap;
        int[] weight = data2.weight;
        int[] parent = data2.parent;
        int i4 = alphaSize;
        while (true) {
            i = -1;
            i4--;
            i2 = 1;
            if (i4 < 0) {
                break;
            }
            int i5 = i4 + 1;
            if (freq[i4] != 0) {
                i2 = freq[i4];
            }
            weight[i5] = i2 << 8;
        }
        boolean tooLong = true;
        while (tooLong) {
            tooLong = false;
            int nNodes = alphaSize;
            int nHeap = 0;
            heap[0] = 0;
            weight[0] = 0;
            parent[0] = -2;
            for (int i6 = 1; i6 <= i3; i6++) {
                parent[i6] = i;
                nHeap++;
                heap[nHeap] = i6;
                int zz = nHeap;
                int tmp = heap[zz];
                while (weight[tmp] < weight[heap[zz >> 1]]) {
                    heap[zz] = heap[zz >> 1];
                    zz >>= 1;
                }
                heap[zz] = tmp;
            }
            while (nHeap > i2) {
                int n1 = heap[i2];
                heap[i2] = heap[nHeap];
                int nHeap2 = nHeap - 1;
                int zz2 = 1;
                int tmp2 = heap[i2];
                while (true) {
                    int yy = zz2 << 1;
                    if (yy > nHeap2) {
                        break;
                    }
                    if (yy < nHeap2 && weight[heap[yy + 1]] < weight[heap[yy]]) {
                        yy++;
                    }
                    if (weight[tmp2] < weight[heap[yy]]) {
                        break;
                    }
                    heap[zz2] = heap[yy];
                    zz2 = yy;
                    Data data3 = dat;
                    i2 = 1;
                }
                heap[zz2] = tmp2;
                int n2 = heap[i2];
                heap[i2] = heap[nHeap2];
                int nHeap3 = nHeap2 - 1;
                int zz3 = 1;
                int tmp3 = heap[i2];
                while (true) {
                    int yy2 = zz3 << 1;
                    if (yy2 > nHeap3) {
                        break;
                    }
                    if (yy2 < nHeap3 && weight[heap[yy2 + 1]] < weight[heap[yy2]]) {
                        yy2++;
                    }
                    if (weight[tmp3] < weight[heap[yy2]]) {
                        break;
                    }
                    heap[zz3] = heap[yy2];
                    zz3 = yy2;
                    Data data4 = dat;
                }
                heap[zz3] = tmp3;
                nNodes++;
                parent[n2] = nNodes;
                parent[n1] = nNodes;
                int weight_n1 = weight[n1];
                int weight_n2 = weight[n2];
                weight[nNodes] = (((weight_n1 & 255) > (weight_n2 & 255) ? weight_n1 & 255 : weight_n2 & 255) + 1) | ((weight_n1 & InputDeviceCompat.SOURCE_ANY) + (weight_n2 & InputDeviceCompat.SOURCE_ANY));
                parent[nNodes] = -1;
                int nHeap4 = nHeap3 + 1;
                heap[nHeap4] = nNodes;
                int zz4 = nHeap4;
                int tmp4 = heap[zz4];
                int weight_tmp = weight[tmp4];
                while (weight_tmp < weight[heap[zz4 >> 1]]) {
                    heap[zz4] = heap[zz4 >> 1];
                    zz4 >>= 1;
                }
                heap[zz4] = tmp4;
                Data data5 = dat;
                nHeap = nHeap4;
                i2 = 1;
            }
            for (int i7 = 1; i7 <= i3; i7++) {
                int j = 0;
                int k = i7;
                while (true) {
                    int i8 = parent[k];
                    int parent_k = i8;
                    if (i8 < 0) {
                        break;
                    }
                    k = parent_k;
                    j++;
                }
                len[i7 - 1] = (byte) j;
                if (j > maxLen) {
                    tooLong = true;
                }
            }
            int i9 = maxLen;
            if (tooLong) {
                for (int i10 = 1; i10 < i3; i10++) {
                    weight[i10] = (((weight[i10] >> 8) >> 1) + 1) << 8;
                }
            }
            Data data6 = dat;
            i = -1;
            i2 = 1;
        }
        int i11 = maxLen;
    }

    public static int chooseBlockSize(long inputLength) {
        if (inputLength > 0) {
            return (int) Math.min((inputLength / 132000) + 1, 9);
        }
        return 9;
    }

    public BZip2CompressorOutputStream(OutputStream out2) throws IOException {
        this(out2, 9);
    }

    public BZip2CompressorOutputStream(OutputStream out2, int blockSize) throws IOException {
        this.crc = new CRC();
        this.currentChar = -1;
        this.runLength = 0;
        if (blockSize < 1) {
            throw new IllegalArgumentException("blockSize(" + blockSize + ") < 1");
        } else if (blockSize <= 9) {
            this.blockSize100k = blockSize;
            this.out = out2;
            this.allowableBlockSize = (this.blockSize100k * BZip2Constants.BASEBLOCKSIZE) - 20;
            init();
        } else {
            throw new IllegalArgumentException("blockSize(" + blockSize + ") > 9");
        }
    }

    public void write(int b) throws IOException {
        if (!this.closed) {
            write0(b);
            return;
        }
        throw new IOException("Closed");
    }

    private void writeRun() throws IOException {
        int lastShadow = this.last;
        if (lastShadow < this.allowableBlockSize) {
            int currentCharShadow = this.currentChar;
            Data dataShadow = this.data;
            dataShadow.inUse[currentCharShadow] = true;
            byte ch = (byte) currentCharShadow;
            int runLengthShadow = this.runLength;
            this.crc.updateCRC(currentCharShadow, runLengthShadow);
            if (runLengthShadow == 1) {
                dataShadow.block[lastShadow + 2] = ch;
                this.last = lastShadow + 1;
            } else if (runLengthShadow == 2) {
                dataShadow.block[lastShadow + 2] = ch;
                dataShadow.block[lastShadow + 3] = ch;
                this.last = lastShadow + 2;
            } else if (runLengthShadow != 3) {
                int runLengthShadow2 = runLengthShadow - 4;
                dataShadow.inUse[runLengthShadow2] = true;
                byte[] block = dataShadow.block;
                block[lastShadow + 2] = ch;
                block[lastShadow + 3] = ch;
                block[lastShadow + 4] = ch;
                block[lastShadow + 5] = ch;
                block[lastShadow + 6] = (byte) runLengthShadow2;
                this.last = lastShadow + 5;
            } else {
                byte[] block2 = dataShadow.block;
                block2[lastShadow + 2] = ch;
                block2[lastShadow + 3] = ch;
                block2[lastShadow + 4] = ch;
                this.last = lastShadow + 3;
            }
        } else {
            endBlock();
            initBlock();
            writeRun();
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        if (!this.closed) {
            System.err.println("Unclosed BZip2CompressorOutputStream detected, will *not* close it");
        }
        super.finalize();
    }

    public void finish() throws IOException {
        if (!this.closed) {
            this.closed = true;
            try {
                if (this.runLength > 0) {
                    writeRun();
                }
                this.currentChar = -1;
                endBlock();
                endCompression();
            } finally {
                this.out = null;
                this.blockSorter = null;
                this.data = null;
            }
        }
    }

    public void close() throws IOException {
        if (!this.closed) {
            OutputStream outShadow = this.out;
            try {
                finish();
            } finally {
                outShadow.close();
            }
        }
    }

    public void flush() throws IOException {
        OutputStream outShadow = this.out;
        if (outShadow != null) {
            outShadow.flush();
        }
    }

    private void init() throws IOException {
        bsPutUByte(66);
        bsPutUByte(90);
        this.data = new Data(this.blockSize100k);
        this.blockSorter = new BlockSort(this.data);
        bsPutUByte(104);
        bsPutUByte(this.blockSize100k + 48);
        this.combinedCRC = 0;
        initBlock();
    }

    private void initBlock() {
        this.crc.initialiseCRC();
        this.last = -1;
        boolean[] inUse = this.data.inUse;
        int i = 256;
        while (true) {
            i--;
            if (i >= 0) {
                inUse[i] = false;
            } else {
                return;
            }
        }
    }

    private void endBlock() throws IOException {
        this.blockCRC = this.crc.getFinalCRC();
        int i = this.combinedCRC;
        this.combinedCRC = (i >>> 31) | (i << 1);
        this.combinedCRC ^= this.blockCRC;
        if (this.last != -1) {
            blockSort();
            bsPutUByte(49);
            bsPutUByte(65);
            bsPutUByte(89);
            bsPutUByte(38);
            bsPutUByte(83);
            bsPutUByte(89);
            bsPutInt(this.blockCRC);
            bsW(1, 0);
            moveToFrontCodeAndSend();
        }
    }

    private void endCompression() throws IOException {
        bsPutUByte(23);
        bsPutUByte(114);
        bsPutUByte(69);
        bsPutUByte(56);
        bsPutUByte(80);
        bsPutUByte(144);
        bsPutInt(this.combinedCRC);
        bsFinishedWithStream();
    }

    public final int getBlockSize() {
        return this.blockSize100k;
    }

    public void write(byte[] buf, int offs, int len) throws IOException {
        if (offs < 0) {
            throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
        } else if (len < 0) {
            throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
        } else if (offs + len > buf.length) {
            throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" + len + ") > buf.length(" + buf.length + ").");
        } else if (!this.closed) {
            int hi = offs + len;
            while (offs < hi) {
                write0(buf[offs]);
                offs++;
            }
        } else {
            throw new IOException("Stream closed");
        }
    }

    private void write0(int b) throws IOException {
        int i = this.currentChar;
        if (i != -1) {
            int b2 = b & 255;
            if (i == b2) {
                int i2 = this.runLength + 1;
                this.runLength = i2;
                if (i2 > 254) {
                    writeRun();
                    this.currentChar = -1;
                    this.runLength = 0;
                    return;
                }
                return;
            }
            writeRun();
            this.runLength = 1;
            this.currentChar = b2;
            return;
        }
        this.currentChar = b & 255;
        this.runLength++;
    }

    private static void hbAssignCodes(int[] code, byte[] length, int minLen, int maxLen, int alphaSize) {
        int vec = 0;
        for (int n = minLen; n <= maxLen; n++) {
            for (int i = 0; i < alphaSize; i++) {
                if ((length[i] & 255) == n) {
                    code[i] = vec;
                    vec++;
                }
            }
            vec <<= 1;
        }
    }

    private void bsFinishedWithStream() throws IOException {
        while (this.bsLive > 0) {
            this.out.write(this.bsBuff >> 24);
            this.bsBuff <<= 8;
            this.bsLive -= 8;
        }
    }

    private void bsW(int n, int v) throws IOException {
        OutputStream outShadow = this.out;
        int bsLiveShadow = this.bsLive;
        int bsBuffShadow = this.bsBuff;
        while (bsLiveShadow >= 8) {
            outShadow.write(bsBuffShadow >> 24);
            bsBuffShadow <<= 8;
            bsLiveShadow -= 8;
        }
        this.bsBuff = (v << ((32 - bsLiveShadow) - n)) | bsBuffShadow;
        this.bsLive = bsLiveShadow + n;
    }

    private void bsPutUByte(int c) throws IOException {
        bsW(8, c);
    }

    private void bsPutInt(int u) throws IOException {
        bsW(8, (u >> 24) & 255);
        bsW(8, (u >> 16) & 255);
        bsW(8, (u >> 8) & 255);
        bsW(8, u & 255);
    }

    private void sendMTFValues() throws IOException {
        byte[][] len = this.data.sendMTFValues_len;
        int nGroups = 2;
        int alphaSize = this.nInUse + 2;
        int t = 6;
        while (true) {
            t--;
            if (t < 0) {
                break;
            }
            byte[] len_t = len[t];
            int v = alphaSize;
            while (true) {
                v--;
                if (v >= 0) {
                    len_t[v] = 15;
                }
            }
        }
        int t2 = this.nMTF;
        if (t2 >= 200) {
            nGroups = t2 < 600 ? 3 : t2 < 1200 ? 4 : t2 < 2400 ? 5 : 6;
        }
        sendMTFValues0(nGroups, alphaSize);
        int nSelectors = sendMTFValues1(nGroups, alphaSize);
        sendMTFValues2(nGroups, nSelectors);
        sendMTFValues3(nGroups, alphaSize);
        sendMTFValues4();
        sendMTFValues5(nGroups, nSelectors);
        sendMTFValues6(nGroups, alphaSize);
        sendMTFValues7();
    }

    private void sendMTFValues0(int nGroups, int alphaSize) {
        byte[][] len = this.data.sendMTFValues_len;
        int[] mtfFreq = this.data.mtfFreq;
        int remF = this.nMTF;
        int gs = 0;
        for (int nPart = nGroups; nPart > 0; nPart--) {
            int tFreq = remF / nPart;
            int ge = gs - 1;
            int aFreq = 0;
            int a = alphaSize - 1;
            while (aFreq < tFreq && ge < a) {
                ge++;
                aFreq += mtfFreq[ge];
            }
            if (!(ge <= gs || nPart == nGroups || nPart == 1 || (1 & (nGroups - nPart)) == 0)) {
                aFreq -= mtfFreq[ge];
                ge--;
            }
            byte[] len_np = len[nPart - 1];
            int v = alphaSize;
            while (true) {
                v--;
                if (v < 0) {
                    break;
                } else if (v < gs || v > ge) {
                    len_np[v] = 15;
                } else {
                    len_np[v] = 0;
                }
            }
            gs = ge + 1;
            remF -= aFreq;
        }
    }

    private int sendMTFValues1(int nGroups, int alphaSize) {
        byte[] len_0;
        int nMTFShadow;
        int i = nGroups;
        Data dataShadow = this.data;
        int[][] rfreq = dataShadow.sendMTFValues_rfreq;
        int[] fave = dataShadow.sendMTFValues_fave;
        short[] cost = dataShadow.sendMTFValues_cost;
        char[] sfmap = dataShadow.sfmap;
        byte[] selector = dataShadow.selector;
        byte[][] len = dataShadow.sendMTFValues_len;
        int i2 = 0;
        byte[] len_02 = len[0];
        byte[] len_1 = len[1];
        byte[] len_2 = len[2];
        byte[] len_3 = len[3];
        byte[] len_4 = len[4];
        byte[] len_5 = len[5];
        int nMTFShadow2 = this.nMTF;
        int nSelectors = 0;
        int iter = 0;
        for (int i3 = 4; iter < i3; i3 = 4) {
            int t = nGroups;
            while (true) {
                t--;
                if (t < 0) {
                    break;
                }
                fave[t] = i2;
                int[] rfreqt = rfreq[t];
                int i4 = alphaSize;
                while (true) {
                    i4--;
                    if (i4 >= 0) {
                        rfreqt[i4] = i2;
                    }
                }
            }
            nSelectors = 0;
            int gs = 0;
            while (gs < this.nMTF) {
                Data dataShadow2 = dataShadow;
                int ge = Math.min((gs + 50) - 1, nMTFShadow2 - 1);
                if (i == 6) {
                    short cost1 = 0;
                    int i5 = gs;
                    short cost5 = 0;
                    short cost52 = 0;
                    short cost4 = 0;
                    short cost3 = 0;
                    short cost2 = 0;
                    while (i5 <= ge) {
                        char icv = sfmap[i5];
                        short cost12 = (short) (cost2 + (len_1[icv] & 255));
                        short cost22 = (short) (cost3 + (len_2[icv] & 255));
                        short cost32 = (short) (cost4 + (len_3[icv] & 255));
                        short cost42 = (short) (cost52 + (len_4[icv] & 255));
                        i5++;
                        cost5 = (short) (cost5 + (len_5[icv] & 255));
                        cost52 = cost42;
                        len_02 = len_02;
                        cost4 = cost32;
                        cost3 = cost22;
                        cost2 = cost12;
                        cost1 = (short) (cost1 + (len_02[icv] & 255));
                        nMTFShadow2 = nMTFShadow2;
                    }
                    len_0 = len_02;
                    nMTFShadow = nMTFShadow2;
                    cost[0] = cost1;
                    cost[1] = cost2;
                    cost[2] = cost3;
                    cost[3] = cost4;
                    cost[4] = cost52;
                    cost[5] = cost5;
                } else {
                    len_0 = len_02;
                    nMTFShadow = nMTFShadow2;
                    int t2 = nGroups;
                    while (true) {
                        t2--;
                        if (t2 < 0) {
                            break;
                        }
                        cost[t2] = 0;
                    }
                    for (int i6 = gs; i6 <= ge; i6++) {
                        char icv2 = sfmap[i6];
                        int t3 = nGroups;
                        while (true) {
                            t3--;
                            if (t3 < 0) {
                                break;
                            }
                            cost[t3] = (short) (cost[t3] + (len[t3][icv2] & 255));
                        }
                    }
                }
                int t4 = nGroups;
                int bt = -1;
                int bc = 999999999;
                while (true) {
                    t4--;
                    if (t4 < 0) {
                        break;
                    }
                    byte[] len_12 = len_1;
                    short cost_t = cost[t4];
                    if (cost_t < bc) {
                        bc = cost_t;
                        bt = t4;
                    }
                    len_1 = len_12;
                }
                byte[] len_13 = len_1;
                fave[bt] = fave[bt] + 1;
                selector[nSelectors] = (byte) bt;
                nSelectors++;
                int[] rfreq_bt = rfreq[bt];
                for (int i7 = gs; i7 <= ge; i7++) {
                    char c = sfmap[i7];
                    rfreq_bt[c] = rfreq_bt[c] + 1;
                }
                gs = ge + 1;
                len_1 = len_13;
                dataShadow = dataShadow2;
                nMTFShadow2 = nMTFShadow;
                len_02 = len_0;
            }
            Data dataShadow3 = dataShadow;
            byte[] len_03 = len_02;
            byte[] len_14 = len_1;
            int nMTFShadow3 = nMTFShadow2;
            for (int t5 = 0; t5 < i; t5++) {
                hbMakeCodeLengths(len[t5], rfreq[t5], this.data, alphaSize, 20);
            }
            int i8 = alphaSize;
            iter++;
            len_1 = len_14;
            dataShadow = dataShadow3;
            nMTFShadow2 = nMTFShadow3;
            len_02 = len_03;
            i2 = 0;
        }
        int i9 = alphaSize;
        Data data2 = dataShadow;
        byte[] bArr = len_02;
        byte[] bArr2 = len_1;
        int i10 = nMTFShadow2;
        return nSelectors;
    }

    private void sendMTFValues2(int nGroups, int nSelectors) {
        Data dataShadow = this.data;
        byte[] pos = dataShadow.sendMTFValues2_pos;
        int i = nGroups;
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            pos[i] = (byte) i;
        }
        for (int i2 = 0; i2 < nSelectors; i2++) {
            byte ll_i = dataShadow.selector[i2];
            byte tmp = pos[0];
            int j = 0;
            while (ll_i != tmp) {
                j++;
                byte tmp2 = tmp;
                tmp = pos[j];
                pos[j] = tmp2;
            }
            pos[0] = tmp;
            dataShadow.selectorMtf[i2] = (byte) j;
        }
    }

    private void sendMTFValues3(int nGroups, int alphaSize) {
        int[][] code = this.data.sendMTFValues_code;
        byte[][] len = this.data.sendMTFValues_len;
        for (int t = 0; t < nGroups; t++) {
            int minLen = 32;
            int maxLen = 0;
            byte[] len_t = len[t];
            int i = alphaSize;
            while (true) {
                i--;
                if (i < 0) {
                    break;
                }
                int l = len_t[i] & 255;
                if (l > maxLen) {
                    maxLen = l;
                }
                if (l < minLen) {
                    minLen = l;
                }
            }
            hbAssignCodes(code[t], len[t], minLen, maxLen, alphaSize);
        }
    }

    private void sendMTFValues4() throws IOException {
        boolean[] inUse = this.data.inUse;
        boolean[] inUse16 = this.data.sentMTFValues4_inUse16;
        int i = 16;
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            inUse16[i] = false;
            int i16 = i * 16;
            int j = 16;
            while (true) {
                j--;
                if (j >= 0) {
                    if (inUse[i16 + j]) {
                        inUse16[i] = true;
                    }
                }
            }
        }
        for (int i2 = 0; i2 < 16; i2++) {
            bsW(1, inUse16[i2] ? 1 : 0);
        }
        OutputStream outShadow = this.out;
        int bsLiveShadow = this.bsLive;
        int bsBuffShadow = this.bsBuff;
        for (int i3 = 0; i3 < 16; i3++) {
            if (inUse16[i3]) {
                int i162 = i3 * 16;
                for (int j2 = 0; j2 < 16; j2++) {
                    while (bsLiveShadow >= 8) {
                        outShadow.write(bsBuffShadow >> 24);
                        bsBuffShadow <<= 8;
                        bsLiveShadow -= 8;
                    }
                    if (inUse[i162 + j2]) {
                        bsBuffShadow |= 1 << ((32 - bsLiveShadow) - 1);
                    }
                    bsLiveShadow++;
                }
            }
        }
        this.bsBuff = bsBuffShadow;
        this.bsLive = bsLiveShadow;
    }

    private void sendMTFValues5(int nGroups, int nSelectors) throws IOException {
        bsW(3, nGroups);
        bsW(15, nSelectors);
        OutputStream outShadow = this.out;
        byte[] selectorMtf = this.data.selectorMtf;
        int bsLiveShadow = this.bsLive;
        int bsBuffShadow = this.bsBuff;
        for (int i = 0; i < nSelectors; i++) {
            int hj = selectorMtf[i] & 255;
            for (int j = 0; j < hj; j++) {
                while (bsLiveShadow >= 8) {
                    outShadow.write(bsBuffShadow >> 24);
                    bsBuffShadow <<= 8;
                    bsLiveShadow -= 8;
                }
                bsBuffShadow |= 1 << ((32 - bsLiveShadow) - 1);
                bsLiveShadow++;
            }
            while (bsLiveShadow >= 8) {
                outShadow.write(bsBuffShadow >> 24);
                bsBuffShadow <<= 8;
                bsLiveShadow -= 8;
            }
            bsLiveShadow++;
        }
        this.bsBuff = bsBuffShadow;
        this.bsLive = bsLiveShadow;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v2, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v10, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v11, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v12, resolved type: byte} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void sendMTFValues6(int r14, int r15) throws java.io.IOException {
        /*
            r13 = this;
            org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream$Data r0 = r13.data
            byte[][] r0 = r0.sendMTFValues_len
            java.io.OutputStream r1 = r13.out
            int r2 = r13.bsLive
            int r3 = r13.bsBuff
            r4 = 0
        L_0x000b:
            if (r4 >= r14) goto L_0x0079
            r5 = r0[r4]
            r6 = 0
            byte r6 = r5[r6]
            r6 = r6 & 255(0xff, float:3.57E-43)
        L_0x0014:
            r7 = 8
            if (r2 < r7) goto L_0x0022
            int r7 = r3 >> 24
            r1.write(r7)
            int r3 = r3 << 8
            int r2 = r2 + -8
            goto L_0x0014
        L_0x0022:
            int r8 = 32 - r2
            int r8 = r8 + -5
            int r8 = r6 << r8
            r3 = r3 | r8
            int r2 = r2 + 5
            r8 = 0
        L_0x002c:
            if (r8 >= r15) goto L_0x0076
            byte r9 = r5[r8]
            r9 = r9 & 255(0xff, float:3.57E-43)
        L_0x0032:
            r10 = 2
            if (r6 >= r9) goto L_0x004b
        L_0x0035:
            if (r2 < r7) goto L_0x0041
            int r11 = r3 >> 24
            r1.write(r11)
            int r3 = r3 << 8
            int r2 = r2 + -8
            goto L_0x0035
        L_0x0041:
            int r11 = 32 - r2
            int r11 = r11 - r10
            int r10 = r10 << r11
            r3 = r3 | r10
            int r2 = r2 + 2
            int r6 = r6 + 1
            goto L_0x0032
        L_0x004b:
            if (r6 <= r9) goto L_0x0064
        L_0x004d:
            if (r2 < r7) goto L_0x0059
            int r11 = r3 >> 24
            r1.write(r11)
            int r3 = r3 << 8
            int r2 = r2 + -8
            goto L_0x004d
        L_0x0059:
            r11 = 3
            int r12 = 32 - r2
            int r12 = r12 - r10
            int r11 = r11 << r12
            r3 = r3 | r11
            int r2 = r2 + 2
            int r6 = r6 + -1
            goto L_0x004b
        L_0x0064:
            if (r2 < r7) goto L_0x0070
            int r10 = r3 >> 24
            r1.write(r10)
            int r3 = r3 << 8
            int r2 = r2 + -8
            goto L_0x0064
        L_0x0070:
            int r2 = r2 + 1
            int r8 = r8 + 1
            goto L_0x002c
        L_0x0076:
            int r4 = r4 + 1
            goto L_0x000b
        L_0x0079:
            r13.bsBuff = r3
            r13.bsLive = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream.sendMTFValues6(int, int):void");
    }

    private void sendMTFValues7() throws IOException {
        Data dataShadow;
        Data dataShadow2 = this.data;
        byte[][] len = dataShadow2.sendMTFValues_len;
        int[][] code = dataShadow2.sendMTFValues_code;
        OutputStream outShadow = this.out;
        byte[] selector = dataShadow2.selector;
        char[] sfmap = dataShadow2.sfmap;
        int nMTFShadow = this.nMTF;
        int selCtr = 0;
        int bsLiveShadow = this.bsLive;
        int bsBuffShadow = this.bsBuff;
        int gs = 0;
        while (gs < nMTFShadow) {
            int ge = Math.min((gs + 50) - 1, nMTFShadow - 1);
            int selector_selCtr = selector[selCtr] & 255;
            int[] code_selCtr = code[selector_selCtr];
            byte[] len_selCtr = len[selector_selCtr];
            while (gs <= ge) {
                char sfmap_i = sfmap[gs];
                while (true) {
                    dataShadow = dataShadow2;
                    if (bsLiveShadow < 8) {
                        break;
                    }
                    outShadow.write(bsBuffShadow >> 24);
                    bsBuffShadow <<= 8;
                    bsLiveShadow -= 8;
                    dataShadow2 = dataShadow;
                }
                int n = len_selCtr[sfmap_i] & 255;
                bsBuffShadow |= code_selCtr[sfmap_i] << ((32 - bsLiveShadow) - n);
                bsLiveShadow += n;
                gs++;
                dataShadow2 = dataShadow;
            }
            gs = ge + 1;
            selCtr++;
        }
        this.bsBuff = bsBuffShadow;
        this.bsLive = bsLiveShadow;
    }

    private void moveToFrontCodeAndSend() throws IOException {
        bsW(24, this.data.origPtr);
        generateMTFValues();
        sendMTFValues();
    }

    private void blockSort() {
        this.blockSorter.blockSort(this.data, this.last);
    }

    private void generateMTFValues() {
        int wr;
        int lastShadow = this.last;
        Data dataShadow = this.data;
        boolean[] inUse = dataShadow.inUse;
        byte[] block = dataShadow.block;
        int[] fmap = dataShadow.fmap;
        char[] sfmap = dataShadow.sfmap;
        int[] mtfFreq = dataShadow.mtfFreq;
        byte[] unseqToSeq = dataShadow.unseqToSeq;
        byte[] yy = dataShadow.generateMTFValues_yy;
        int nInUseShadow = 0;
        for (int i = 0; i < 256; i++) {
            if (inUse[i]) {
                unseqToSeq[i] = (byte) nInUseShadow;
                nInUseShadow++;
            }
        }
        this.nInUse = nInUseShadow;
        int eob = nInUseShadow + 1;
        for (int i2 = eob; i2 >= 0; i2--) {
            mtfFreq[i2] = 0;
        }
        int i3 = nInUseShadow;
        while (true) {
            i3--;
            if (i3 < 0) {
                break;
            }
            yy[i3] = (byte) i3;
        }
        int wr2 = 0;
        int zPend = 0;
        int i4 = 0;
        while (i4 <= lastShadow) {
            byte ll_i = unseqToSeq[block[fmap[i4]] & 255];
            int j = 0;
            int lastShadow2 = lastShadow;
            byte tmp = yy[0];
            while (ll_i != tmp) {
                j++;
                byte tmp2 = tmp;
                tmp = yy[j];
                yy[j] = tmp2;
            }
            yy[0] = tmp;
            if (j == 0) {
                zPend++;
            } else {
                if (zPend > 0) {
                    int zPend2 = zPend - 1;
                    while (true) {
                        if ((zPend2 & 1) == 0) {
                            sfmap[wr2] = 0;
                            wr2++;
                            mtfFreq[0] = mtfFreq[0] + 1;
                        } else {
                            sfmap[wr2] = 1;
                            wr2++;
                            mtfFreq[1] = mtfFreq[1] + 1;
                        }
                        byte tmp3 = tmp;
                        if (zPend2 < 2) {
                            break;
                        }
                        zPend2 = (zPend2 - 2) >> 1;
                        tmp = tmp3;
                    }
                    zPend = 0;
                }
                sfmap[wr2] = (char) (j + 1);
                wr2++;
                int i5 = j + 1;
                mtfFreq[i5] = mtfFreq[i5] + 1;
            }
            i4++;
            lastShadow = lastShadow2;
        }
        if (zPend > 0) {
            int zPend3 = zPend - 1;
            while (true) {
                if ((zPend3 & 1) == 0) {
                    sfmap[wr2] = 0;
                    wr = wr2 + 1;
                    mtfFreq[0] = mtfFreq[0] + 1;
                } else {
                    sfmap[wr2] = 1;
                    wr = wr2 + 1;
                    mtfFreq[1] = mtfFreq[1] + 1;
                }
                if (zPend3 < 2) {
                    break;
                }
                zPend3 = (zPend3 - 2) >> 1;
            }
        }
        sfmap[wr2] = (char) eob;
        mtfFreq[eob] = mtfFreq[eob] + 1;
        this.nMTF = wr2 + 1;
    }

    static final class Data {
        final byte[] block;
        final int[] fmap;
        final byte[] generateMTFValues_yy = new byte[256];
        final int[] heap = new int[260];
        final boolean[] inUse = new boolean[256];
        final int[] mtfFreq = new int[BZip2Constants.MAX_ALPHA_SIZE];
        int origPtr;
        final int[] parent = new int[516];
        final byte[] selector = new byte[BZip2Constants.MAX_SELECTORS];
        final byte[] selectorMtf = new byte[BZip2Constants.MAX_SELECTORS];
        final byte[] sendMTFValues2_pos = new byte[6];
        final int[][] sendMTFValues_code = ((int[][]) Array.newInstance(int.class, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        final short[] sendMTFValues_cost = new short[6];
        final int[] sendMTFValues_fave = new int[6];
        final byte[][] sendMTFValues_len = ((byte[][]) Array.newInstance(byte.class, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        final int[][] sendMTFValues_rfreq = ((int[][]) Array.newInstance(int.class, new int[]{6, BZip2Constants.MAX_ALPHA_SIZE}));
        final boolean[] sentMTFValues4_inUse16 = new boolean[16];
        final char[] sfmap;
        final byte[] unseqToSeq = new byte[256];
        final int[] weight = new int[516];

        Data(int blockSize100k) {
            int n = BZip2Constants.BASEBLOCKSIZE * blockSize100k;
            this.block = new byte[(n + 1 + 20)];
            this.fmap = new int[n];
            this.sfmap = new char[(n * 2)];
        }
    }
}

package org.apache.commons.compress.compressors.bzip2;

import android.support.v4.media.session.PlaybackStateCompat;
import java.util.BitSet;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

class BlockSort {
    private static final int CLEARMASK = -2097153;
    private static final int DEPTH_THRESH = 10;
    private static final int FALLBACK_QSORT_SMALL_THRESH = 10;
    private static final int FALLBACK_QSORT_STACK_SIZE = 100;
    private static final int[] INCS = {1, 4, 13, 40, 121, 364, 1093, 3280, 9841, 29524, 88573, 265720, 797161, 2391484};
    private static final int QSORT_STACK_SIZE = 1000;
    private static final int SETMASK = 2097152;
    private static final int SMALL_THRESH = 20;
    private static final int STACK_SIZE = 1000;
    private static final int WORK_FACTOR = 30;
    private int[] eclass;
    private boolean firstAttempt;
    private final int[] ftab = new int[65537];
    private final boolean[] mainSort_bigDone = new boolean[256];
    private final int[] mainSort_copy = new int[256];
    private final int[] mainSort_runningOrder = new int[256];
    private final char[] quadrant;
    private final int[] stack_dd = new int[1000];
    private final int[] stack_hh = new int[1000];
    private final int[] stack_ll = new int[1000];
    private int workDone;
    private int workLimit;

    BlockSort(BZip2CompressorOutputStream.Data data) {
        this.quadrant = data.sfmap;
    }

    /* access modifiers changed from: package-private */
    public void blockSort(BZip2CompressorOutputStream.Data data, int last) {
        this.workLimit = last * 30;
        this.workDone = 0;
        this.firstAttempt = true;
        if (last + 1 < 10000) {
            fallbackSort(data, last);
        } else {
            mainSort(data, last);
            if (this.firstAttempt && this.workDone > this.workLimit) {
                fallbackSort(data, last);
            }
        }
        int[] fmap = data.fmap;
        data.origPtr = -1;
        for (int i = 0; i <= last; i++) {
            if (fmap[i] == 0) {
                data.origPtr = i;
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final void fallbackSort(BZip2CompressorOutputStream.Data data, int last) {
        data.block[0] = data.block[last + 1];
        fallbackSort(data.fmap, data.block, last + 1);
        for (int i = 0; i < last + 1; i++) {
            int[] iArr = data.fmap;
            iArr[i] = iArr[i] - 1;
        }
        for (int i2 = 0; i2 < last + 1; i2++) {
            if (data.fmap[i2] == -1) {
                data.fmap[i2] = last;
                return;
            }
        }
    }

    private void fallbackSimpleSort(int[] fmap, int[] eclass2, int lo, int hi) {
        if (lo != hi) {
            if (hi - lo > 3) {
                for (int i = hi - 4; i >= lo; i--) {
                    int tmp = fmap[i];
                    int ec_tmp = eclass2[tmp];
                    int j = i + 4;
                    while (j <= hi && ec_tmp > eclass2[fmap[j]]) {
                        fmap[j - 4] = fmap[j];
                        j += 4;
                    }
                    fmap[j - 4] = tmp;
                }
            }
            for (int i2 = hi - 1; i2 >= lo; i2--) {
                int tmp2 = fmap[i2];
                int ec_tmp2 = eclass2[tmp2];
                int j2 = i2 + 1;
                while (j2 <= hi && ec_tmp2 > eclass2[fmap[j2]]) {
                    fmap[j2 - 1] = fmap[j2];
                    j2++;
                }
                fmap[j2 - 1] = tmp2;
            }
        }
    }

    private void fswap(int[] fmap, int zz1, int zz2) {
        int zztmp = fmap[zz1];
        fmap[zz1] = fmap[zz2];
        fmap[zz2] = zztmp;
    }

    private void fvswap(int[] fmap, int yyp1, int yyp2, int yyn) {
        while (yyn > 0) {
            fswap(fmap, yyp1, yyp2);
            yyp1++;
            yyp2++;
            yyn--;
        }
    }

    private int fmin(int a, int b) {
        return a < b ? a : b;
    }

    private void fpush(int sp, int lz, int hz) {
        this.stack_ll[sp] = lz;
        this.stack_hh[sp] = hz;
    }

    private int[] fpop(int sp) {
        return new int[]{this.stack_ll[sp], this.stack_hh[sp]};
    }

    private void fallbackQSort3(int[] fmap, int[] eclass2, int loSt, int hiSt) {
        long med;
        int unHi;
        int gtHi;
        int unHi2;
        int sp;
        int[] iArr = fmap;
        int[] iArr2 = eclass2;
        long med2 = 0;
        int sp2 = 0 + 1;
        fpush(0, loSt, hiSt);
        while (sp2 > 0) {
            int sp3 = sp2 - 1;
            int[] s = fpop(sp3);
            int lo = s[0];
            int hi = s[1];
            if (hi - lo < 10) {
                fallbackSimpleSort(iArr, iArr2, lo, hi);
                sp2 = sp3;
            } else {
                long r = ((7621 * med2) + 1) % PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID;
                long r3 = r % 3;
                if (r3 == 0) {
                    med = (long) iArr2[iArr[lo]];
                } else if (r3 == 1) {
                    med = (long) iArr2[iArr[(lo + hi) >>> 1]];
                } else {
                    med = (long) iArr2[iArr[hi]];
                }
                int unLo = lo;
                int ltLo = lo;
                int ltLo2 = hi;
                int gtHi2 = hi;
                while (true) {
                    if (unLo > ltLo2) {
                        unHi = ltLo2;
                    } else {
                        unHi = ltLo2;
                        int n = iArr2[iArr[unLo]] - ((int) med);
                        if (n == 0) {
                            fswap(iArr, unLo, ltLo);
                            ltLo++;
                            unLo++;
                            ltLo2 = unHi;
                        } else if (n <= 0) {
                            unLo++;
                            iArr2 = eclass2;
                            int i = loSt;
                            ltLo2 = unHi;
                        }
                    }
                    gtHi = gtHi2;
                    unHi2 = unHi;
                    while (unLo <= unHi2) {
                        int n2 = iArr2[iArr[unHi2]] - ((int) med);
                        if (n2 == 0) {
                            fswap(iArr, unHi2, gtHi);
                            gtHi--;
                            unHi2--;
                            iArr2 = eclass2;
                        } else if (n2 < 0) {
                            break;
                        } else {
                            int i2 = gtHi;
                            unHi2--;
                            iArr2 = eclass2;
                        }
                    }
                    if (unLo > unHi2) {
                        break;
                    }
                    long j = med;
                    int gtHi3 = gtHi;
                    fswap(iArr, unLo, unHi2);
                    unLo++;
                    ltLo2 = unHi2 - 1;
                    iArr2 = eclass2;
                    int i3 = loSt;
                    gtHi2 = gtHi3;
                }
                if (gtHi < ltLo) {
                    iArr2 = eclass2;
                    int gtHi4 = loSt;
                    sp2 = sp3;
                    med2 = r;
                } else {
                    long j2 = med;
                    int n3 = fmin(ltLo - lo, unLo - ltLo);
                    fvswap(iArr, lo, unLo - n3, n3);
                    int m = fmin(hi - gtHi, gtHi - unHi2);
                    int i4 = n3;
                    fvswap(iArr, unHi2 + 1, (hi - m) + 1, m);
                    int n4 = ((lo + unLo) - ltLo) - 1;
                    int m2 = (hi - (gtHi - unHi2)) + 1;
                    int i5 = gtHi;
                    if (n4 - lo > hi - m2) {
                        int sp4 = sp3 + 1;
                        fpush(sp3, lo, n4);
                        sp = sp4 + 1;
                        fpush(sp4, m2, hi);
                    } else {
                        int sp5 = sp3 + 1;
                        fpush(sp3, m2, hi);
                        sp = sp5 + 1;
                        fpush(sp5, lo, n4);
                    }
                    iArr2 = eclass2;
                    int i6 = loSt;
                    sp2 = sp;
                    med2 = r;
                }
            }
        }
    }

    private int[] getEclass() {
        if (this.eclass == null) {
            this.eclass = new int[(this.quadrant.length / 2)];
        }
        return this.eclass;
    }

    /* access modifiers changed from: package-private */
    public final void fallbackSort(int[] fmap, byte[] block, int nblock) {
        int nNotDone;
        int[] iArr = fmap;
        int i = nblock;
        int[] ftab2 = new int[257];
        int[] eclass2 = getEclass();
        for (int i2 = 0; i2 < i; i2++) {
            eclass2[i2] = 0;
        }
        for (int i3 = 0; i3 < i; i3++) {
            byte b = block[i3] & 255;
            ftab2[b] = ftab2[b] + 1;
        }
        for (int i4 = 1; i4 < 257; i4++) {
            ftab2[i4] = ftab2[i4] + ftab2[i4 - 1];
        }
        for (int i5 = 0; i5 < i; i5++) {
            int j = block[i5] & 255;
            int k = ftab2[j] - 1;
            ftab2[j] = k;
            iArr[k] = i5;
        }
        BitSet bhtab = new BitSet(i + 64);
        for (int i6 = 0; i6 < 256; i6++) {
            bhtab.set(ftab2[i6]);
        }
        for (int i7 = 0; i7 < 32; i7++) {
            bhtab.set((i7 * 2) + i);
            bhtab.clear((i7 * 2) + i + 1);
        }
        int H = 1;
        do {
            int j2 = 0;
            for (int i8 = 0; i8 < i; i8++) {
                if (bhtab.get(i8)) {
                    j2 = i8;
                }
                int k2 = iArr[i8] - H;
                if (k2 < 0) {
                    k2 += i;
                }
                eclass2[k2] = j2;
            }
            nNotDone = 0;
            int r = -1;
            while (true) {
                int k3 = bhtab.nextClearBit(r + 1);
                int l = k3 - 1;
                if (l < i && bhtab.nextSetBit(k3 + 1) - 1 < i) {
                    if (r > l) {
                        nNotDone += (r - l) + 1;
                        fallbackQSort3(iArr, eclass2, l, r);
                        int cc = -1;
                        for (int i9 = l; i9 <= r; i9++) {
                            int cc1 = eclass2[iArr[i9]];
                            if (cc != cc1) {
                                bhtab.set(i9);
                                cc = cc1;
                            }
                        }
                    }
                }
            }
            H *= 2;
            if (H > i) {
                return;
            }
        } while (nNotDone != 0);
    }

    private boolean mainSimpleSort(BZip2CompressorOutputStream.Data dataShadow, int lo, int hi, int d, int lastShadow) {
        int bigN;
        BZip2CompressorOutputStream.Data data = dataShadow;
        int i = hi;
        int i2 = 1;
        int bigN2 = (i - lo) + 1;
        if (bigN2 >= 2) {
            int hp = 0;
            while (INCS[hp] < bigN2) {
                hp++;
            }
            int[] fmap = data.fmap;
            char[] quadrant2 = this.quadrant;
            byte[] block = data.block;
            int lastPlus1 = lastShadow + 1;
            boolean firstAttemptShadow = this.firstAttempt;
            int workLimitShadow = this.workLimit;
            int i3 = this.workDone;
            loop1:
            while (true) {
                hp--;
                if (hp < 0) {
                    break;
                }
                int h = INCS[hp];
                int mj = (lo + h) - i2;
                int workDoneShadow = i3;
                int i4 = lo + h;
                while (i4 <= i) {
                    int k = 3;
                    int workDoneShadow2 = workDoneShadow;
                    while (i4 <= i) {
                        k--;
                        if (k < 0) {
                            break;
                        }
                        int v = fmap[i4];
                        int vd = v + d;
                        int j = i4;
                        boolean onceRunned = false;
                        int a = 0;
                        while (true) {
                            if (onceRunned) {
                                fmap[j] = a;
                                int i5 = j - h;
                                j = i5;
                                if (i5 <= mj) {
                                    bigN = bigN2;
                                    break;
                                }
                            } else {
                                onceRunned = true;
                            }
                            a = fmap[j - h];
                            int i1 = a + d;
                            int i22 = vd;
                            bigN = bigN2;
                            if (block[i1 + 1] != block[i22 + 1]) {
                                if ((block[i1 + 1] & 255) <= (block[i22 + 1] & 255)) {
                                    break;
                                }
                            } else if (block[i1 + 2] != block[i22 + 2]) {
                                if ((block[i1 + 2] & 255) <= (block[i22 + 2] & 255)) {
                                    break;
                                }
                            } else if (block[i1 + 3] != block[i22 + 3]) {
                                if ((block[i1 + 3] & 255) <= (block[i22 + 3] & 255)) {
                                    break;
                                }
                            } else if (block[i1 + 4] != block[i22 + 4]) {
                                if ((block[i1 + 4] & 255) <= (block[i22 + 4] & 255)) {
                                    break;
                                }
                            } else if (block[i1 + 5] != block[i22 + 5]) {
                                if ((block[i1 + 5] & 255) <= (block[i22 + 5] & 255)) {
                                    break;
                                }
                            } else {
                                int i12 = i1 + 6;
                                int i23 = i22 + 6;
                                if (block[i12] != block[i23]) {
                                    if ((block[i12] & 255) <= (block[i23] & 255)) {
                                        break;
                                    }
                                } else {
                                    int x = lastShadow;
                                    while (true) {
                                        if (x <= 0) {
                                            break;
                                        }
                                        int x2 = x - 4;
                                        if (block[i12 + 1] != block[i23 + 1]) {
                                            if ((block[i12 + 1] & 255) <= (block[i23 + 1] & 255)) {
                                                break;
                                            }
                                        } else if (quadrant2[i12] != quadrant2[i23]) {
                                            if (quadrant2[i12] <= quadrant2[i23]) {
                                                break;
                                            }
                                        } else if (block[i12 + 2] != block[i23 + 2]) {
                                            if ((block[i12 + 2] & 255) <= (block[i23 + 2] & 255)) {
                                                break;
                                            }
                                        } else if (quadrant2[i12 + 1] != quadrant2[i23 + 1]) {
                                            if (quadrant2[i12 + 1] <= quadrant2[i23 + 1]) {
                                                break;
                                            }
                                        } else if (block[i12 + 3] != block[i23 + 3]) {
                                            if ((block[i12 + 3] & 255) <= (block[i23 + 3] & 255)) {
                                                break;
                                            }
                                        } else if (quadrant2[i12 + 2] != quadrant2[i23 + 2]) {
                                            if (quadrant2[i12 + 2] <= quadrant2[i23 + 2]) {
                                                break;
                                            }
                                        } else if (block[i12 + 4] != block[i23 + 4]) {
                                            if ((block[i12 + 4] & 255) <= (block[i23 + 4] & 255)) {
                                                break;
                                            }
                                        } else if (quadrant2[i12 + 3] != quadrant2[i23 + 3]) {
                                            if (quadrant2[i12 + 3] <= quadrant2[i23 + 3]) {
                                                break;
                                            }
                                        } else {
                                            i12 += 4;
                                            if (i12 >= lastPlus1) {
                                                i12 -= lastPlus1;
                                            }
                                            int i24 = i23 + 4;
                                            if (i24 >= lastPlus1) {
                                                i24 -= lastPlus1;
                                            }
                                            i23 = i24;
                                            workDoneShadow2++;
                                            x = x2;
                                        }
                                    }
                                }
                            }
                            BZip2CompressorOutputStream.Data data2 = dataShadow;
                            bigN2 = bigN;
                        }
                        fmap[j] = v;
                        i4++;
                        BZip2CompressorOutputStream.Data data3 = dataShadow;
                        bigN2 = bigN;
                    }
                    int bigN3 = bigN2;
                    if (firstAttemptShadow && i4 <= i && workDoneShadow2 > workLimitShadow) {
                        i3 = workDoneShadow2;
                        break loop1;
                    }
                    BZip2CompressorOutputStream.Data data4 = dataShadow;
                    workDoneShadow = workDoneShadow2;
                    bigN2 = bigN3;
                }
                BZip2CompressorOutputStream.Data data5 = dataShadow;
                i3 = workDoneShadow;
                i2 = 1;
            }
            this.workDone = i3;
            return firstAttemptShadow && i3 > workLimitShadow;
        } else if (!this.firstAttempt || this.workDone <= this.workLimit) {
            return false;
        } else {
            return true;
        }
    }

    private static void vswap(int[] fmap, int p2, int p22, int n) {
        int n2 = n + p2;
        while (p2 < n2) {
            int t = fmap[p2];
            fmap[p2] = fmap[p22];
            fmap[p22] = t;
            p22++;
            p2++;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        return r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0005, code lost:
        if (r1 < r3) goto L_0x000e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000c, code lost:
        if (r1 > r3) goto L_0x000e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static byte med3(byte r1, byte r2, byte r3) {
        /*
            if (r1 >= r2) goto L_0x0008
            if (r2 >= r3) goto L_0x0005
            goto L_0x000a
        L_0x0005:
            if (r1 >= r3) goto L_0x0010
            goto L_0x000e
        L_0x0008:
            if (r2 <= r3) goto L_0x000c
        L_0x000a:
            r0 = r2
            goto L_0x0011
        L_0x000c:
            if (r1 <= r3) goto L_0x0010
        L_0x000e:
            r0 = r3
            goto L_0x0011
        L_0x0010:
            r0 = r1
        L_0x0011:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.compressors.bzip2.BlockSort.med3(byte, byte, byte):byte");
    }

    private void mainQSort3(BZip2CompressorOutputStream.Data dataShadow, int loSt, int hiSt, int dSt, int last) {
        int unHi;
        int gtHi;
        int gtHi2;
        BZip2CompressorOutputStream.Data data = dataShadow;
        int[] stack_ll2 = this.stack_ll;
        int[] stack_hh2 = this.stack_hh;
        int[] stack_dd2 = this.stack_dd;
        int[] fmap = data.fmap;
        byte[] block = data.block;
        stack_ll2[0] = loSt;
        stack_hh2[0] = hiSt;
        stack_dd2[0] = dSt;
        int sp = 1;
        while (true) {
            int sp2 = sp - 1;
            if (sp2 >= 0) {
                int lo = stack_ll2[sp2];
                int hi = stack_hh2[sp2];
                int d = stack_dd2[sp2];
                if (hi - lo < 20 || d > 10) {
                    int i = d;
                    if (mainSimpleSort(dataShadow, lo, hi, d, last)) {
                        return;
                    }
                } else {
                    int d1 = d + 1;
                    byte med3 = med3(block[fmap[lo] + d1], block[fmap[hi] + d1], block[fmap[(lo + hi) >>> 1] + d1]) & 255;
                    int ltLo = lo;
                    int n = hi;
                    int ltLo2 = lo;
                    int unLo = hi;
                    while (true) {
                        if (ltLo <= n) {
                            unHi = n;
                            int n2 = (block[fmap[ltLo] + d1] & 255) - med3;
                            if (n2 == 0) {
                                int temp = fmap[ltLo];
                                fmap[ltLo] = fmap[ltLo2];
                                fmap[ltLo2] = temp;
                                ltLo2++;
                                ltLo++;
                            } else if (n2 < 0) {
                                ltLo++;
                            }
                            n = unHi;
                        } else {
                            unHi = n;
                        }
                        gtHi = unLo;
                        gtHi2 = unHi;
                        while (ltLo <= gtHi2) {
                            int n3 = (block[fmap[gtHi2] + d1] & 255) - med3;
                            if (n3 != 0) {
                                if (n3 <= 0) {
                                    break;
                                }
                                gtHi2--;
                            } else {
                                int temp2 = fmap[gtHi2];
                                fmap[gtHi2] = fmap[gtHi];
                                fmap[gtHi] = temp2;
                                gtHi--;
                                gtHi2--;
                            }
                            BZip2CompressorOutputStream.Data data2 = dataShadow;
                        }
                        if (ltLo > gtHi2) {
                            break;
                        }
                        int temp3 = fmap[ltLo];
                        fmap[ltLo] = fmap[gtHi2];
                        fmap[gtHi2] = temp3;
                        BZip2CompressorOutputStream.Data data3 = dataShadow;
                        n = gtHi2 - 1;
                        ltLo++;
                        unLo = gtHi;
                    }
                    if (gtHi < ltLo2) {
                        stack_ll2[sp2] = lo;
                        stack_hh2[sp2] = hi;
                        stack_dd2[sp2] = d1;
                        sp2++;
                    } else {
                        byte b = med3;
                        int n4 = ltLo2 - lo < ltLo - ltLo2 ? ltLo2 - lo : ltLo - ltLo2;
                        vswap(fmap, lo, ltLo - n4, n4);
                        int i2 = n4;
                        int m = hi - gtHi < gtHi - gtHi2 ? hi - gtHi : gtHi - gtHi2;
                        vswap(fmap, ltLo, (hi - m) + 1, m);
                        int n5 = ((lo + ltLo) - ltLo2) - 1;
                        int m2 = (hi - (gtHi - gtHi2)) + 1;
                        stack_ll2[sp2] = lo;
                        stack_hh2[sp2] = n5;
                        stack_dd2[sp2] = d;
                        int sp3 = sp2 + 1;
                        stack_ll2[sp3] = n5 + 1;
                        stack_hh2[sp3] = m2 - 1;
                        stack_dd2[sp3] = d1;
                        int sp4 = sp3 + 1;
                        stack_ll2[sp4] = m2;
                        stack_hh2[sp4] = hi;
                        stack_dd2[sp4] = d;
                        sp2 = sp4 + 1;
                    }
                }
                sp = sp2;
                BZip2CompressorOutputStream.Data data4 = dataShadow;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final void mainSort(BZip2CompressorOutputStream.Data dataShadow, int lastShadow) {
        int i;
        int[] runningOrder;
        int j;
        int workLimitShadow;
        int i2;
        int i3;
        int c1;
        int j2;
        BZip2CompressorOutputStream.Data data = dataShadow;
        int i4 = lastShadow;
        int[] runningOrder2 = this.mainSort_runningOrder;
        int[] copy = this.mainSort_copy;
        boolean[] bigDone = this.mainSort_bigDone;
        int[] ftab2 = this.ftab;
        byte[] block = data.block;
        int[] fmap = data.fmap;
        char[] quadrant2 = this.quadrant;
        int workLimitShadow2 = this.workLimit;
        boolean firstAttemptShadow = this.firstAttempt;
        int i5 = 65537;
        while (true) {
            i5--;
            if (i5 < 0) {
                break;
            }
            ftab2[i5] = 0;
        }
        int i6 = 0;
        while (true) {
            i = 1;
            if (i6 >= 20) {
                break;
            }
            block[i4 + i6 + 2] = block[(i6 % (i4 + 1)) + 1];
            i6++;
        }
        int i7 = i4 + 20 + 1;
        while (true) {
            i7--;
            if (i7 < 0) {
                break;
            }
            quadrant2[i7] = 0;
        }
        block[0] = block[i4 + 1];
        boolean firstAttemptShadow2 = firstAttemptShadow;
        int i8 = 255;
        int c12 = block[0] & 255;
        for (int i9 = 0; i9 <= i4; i9++) {
            int c2 = block[i9 + 1] & 255;
            int i10 = (c12 << 8) + c2;
            ftab2[i10] = ftab2[i10] + 1;
            c12 = c2;
        }
        for (int i11 = 1; i11 <= 65536; i11++) {
            ftab2[i11] = ftab2[i11] + ftab2[i11 - 1];
        }
        int c13 = block[1] & 255;
        for (int i12 = 0; i12 < i4; i12++) {
            int c22 = block[i12 + 2] & 255;
            int i13 = (c13 << 8) + c22;
            int i14 = ftab2[i13] - 1;
            ftab2[i13] = i14;
            fmap[i14] = i12;
            c13 = c22;
        }
        int i15 = ((block[i4 + 1] & 255) << 8) + (block[1] & 255);
        int i16 = ftab2[i15] - 1;
        ftab2[i15] = i16;
        fmap[i16] = i4;
        int i17 = 256;
        while (true) {
            i17--;
            if (i17 < 0) {
                break;
            }
            bigDone[i17] = false;
            runningOrder2[i17] = i17;
        }
        int h = 364;
        while (h != i) {
            h /= 3;
            int i18 = h;
            while (i18 <= i8) {
                int vv = runningOrder2[i18];
                int a = ftab2[(vv + 1) << 8] - ftab2[vv << 8];
                int b = h - 1;
                int j3 = i18;
                int ro = runningOrder2[j3 - h];
                while (true) {
                    c1 = c13;
                    if (ftab2[(ro + 1) << 8] - ftab2[ro << 8] <= a) {
                        j2 = j3;
                        break;
                    }
                    runningOrder2[j3] = ro;
                    j2 = j3 - h;
                    if (j2 <= b) {
                        break;
                    }
                    ro = runningOrder2[j2 - h];
                    j3 = j2;
                    c13 = c1;
                }
                runningOrder2[j2] = vv;
                i18++;
                c13 = c1;
                i8 = 255;
            }
            i = 1;
            i8 = 255;
        }
        int i19 = 0;
        while (true) {
            int i20 = 255;
            if (i19 <= 255) {
                int ss = runningOrder2[i19];
                int j4 = 0;
                while (j4 <= i20) {
                    int sb = (ss << 8) + j4;
                    int ftab_sb = ftab2[sb];
                    if ((ftab_sb & 2097152) != 2097152) {
                        int lo = ftab_sb & CLEARMASK;
                        int hi = (ftab2[sb + 1] & CLEARMASK) - 1;
                        if (hi > lo) {
                            i3 = 2097152;
                            j = j4;
                            int i21 = lo;
                            i2 = i19;
                            runningOrder = runningOrder2;
                            workLimitShadow = workLimitShadow2;
                            mainQSort3(dataShadow, lo, hi, 2, lastShadow);
                            if (firstAttemptShadow2 && this.workDone > workLimitShadow) {
                                return;
                            }
                        } else {
                            j = j4;
                            int i22 = lo;
                            i2 = i19;
                            runningOrder = runningOrder2;
                            i3 = 2097152;
                            workLimitShadow = workLimitShadow2;
                        }
                        ftab2[sb] = ftab_sb | i3;
                    } else {
                        j = j4;
                        i2 = i19;
                        runningOrder = runningOrder2;
                        workLimitShadow = workLimitShadow2;
                    }
                    j4 = j + 1;
                    i19 = i2;
                    workLimitShadow2 = workLimitShadow;
                    runningOrder2 = runningOrder;
                    i20 = 255;
                    BZip2CompressorOutputStream.Data data2 = dataShadow;
                }
                int i23 = j4;
                int i24 = i19;
                int[] runningOrder3 = runningOrder2;
                int workLimitShadow3 = workLimitShadow2;
                for (int j5 = 0; j5 <= 255; j5++) {
                    copy[j5] = ftab2[(j5 << 8) + ss] & CLEARMASK;
                }
                int j6 = ftab2[ss << 8] & CLEARMASK;
                int hj = ftab2[(ss + 1) << 8] & CLEARMASK;
                while (j6 < hj) {
                    int fmap_j = fmap[j6];
                    byte b2 = block[fmap_j] & 255;
                    if (!bigDone[b2]) {
                        fmap[copy[b2]] = fmap_j == 0 ? i4 : fmap_j - 1;
                        copy[b2] = copy[b2] + 1;
                    }
                    j6++;
                    byte b3 = b2;
                }
                int j7 = 256;
                while (true) {
                    j7--;
                    if (j7 < 0) {
                        break;
                    }
                    int i25 = (j7 << 8) + ss;
                    ftab2[i25] = ftab2[i25] | 2097152;
                }
                bigDone[ss] = true;
                if (i24 < 255) {
                    int bbStart = ftab2[ss << 8] & CLEARMASK;
                    int bbSize = (CLEARMASK & ftab2[(ss + 1) << 8]) - bbStart;
                    int shifts = 0;
                    while ((bbSize >> shifts) > 65534) {
                        shifts++;
                    }
                    int j8 = 0;
                    while (j8 < bbSize) {
                        int a2update = fmap[bbStart + j8];
                        char qVal = (char) (j8 >> shifts);
                        quadrant2[a2update] = qVal;
                        int bbSize2 = bbSize;
                        if (a2update < 20) {
                            quadrant2[a2update + i4 + 1] = qVal;
                        }
                        j8++;
                        bbSize = bbSize2;
                    }
                }
                i19 = i24 + 1;
                BZip2CompressorOutputStream.Data data3 = dataShadow;
                workLimitShadow2 = workLimitShadow3;
                runningOrder2 = runningOrder3;
            } else {
                int i26 = i19;
                int[] iArr = runningOrder2;
                int i27 = workLimitShadow2;
                return;
            }
        }
    }
}

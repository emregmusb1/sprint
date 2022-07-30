package org.apache.commons.compress.archivers.sevenz;

import java.util.LinkedList;

class Folder {
    BindPair[] bindPairs;
    Coder[] coders;
    long crc;
    boolean hasCrc;
    int numUnpackSubStreams;
    long[] packedStreams;
    long totalInputStreams;
    long totalOutputStreams;
    long[] unpackSizes;

    Folder() {
    }

    /* access modifiers changed from: package-private */
    public Iterable<Coder> getOrderedCoders() {
        LinkedList<Coder> l = new LinkedList<>();
        int current = (int) this.packedStreams[0];
        while (true) {
            int i = -1;
            if (current == -1) {
                return l;
            }
            l.addLast(this.coders[current]);
            int pair = findBindPairForOutStream(current);
            if (pair != -1) {
                i = (int) this.bindPairs[pair].inIndex;
            }
            current = i;
        }
    }

    /* access modifiers changed from: package-private */
    public int findBindPairForInStream(int index) {
        int i = 0;
        while (true) {
            BindPair[] bindPairArr = this.bindPairs;
            if (i >= bindPairArr.length) {
                return -1;
            }
            if (bindPairArr[i].inIndex == ((long) index)) {
                return i;
            }
            i++;
        }
    }

    /* access modifiers changed from: package-private */
    public int findBindPairForOutStream(int index) {
        int i = 0;
        while (true) {
            BindPair[] bindPairArr = this.bindPairs;
            if (i >= bindPairArr.length) {
                return -1;
            }
            if (bindPairArr[i].outIndex == ((long) index)) {
                return i;
            }
            i++;
        }
    }

    /* access modifiers changed from: package-private */
    public long getUnpackSize() {
        long j = this.totalOutputStreams;
        if (j == 0) {
            return 0;
        }
        for (int i = ((int) j) - 1; i >= 0; i--) {
            if (findBindPairForOutStream(i) < 0) {
                return this.unpackSizes[i];
            }
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public long getUnpackSizeForCoder(Coder coder) {
        if (this.coders == null) {
            return 0;
        }
        int i = 0;
        while (true) {
            Coder[] coderArr = this.coders;
            if (i >= coderArr.length) {
                return 0;
            }
            if (coderArr[i] == coder) {
                return this.unpackSizes[i];
            }
            i++;
        }
    }

    public String toString() {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append("Folder with ");
        sb.append(this.coders.length);
        sb.append(" coders, ");
        sb.append(this.totalInputStreams);
        sb.append(" input streams, ");
        sb.append(this.totalOutputStreams);
        sb.append(" output streams, ");
        sb.append(this.bindPairs.length);
        sb.append(" bind pairs, ");
        sb.append(this.packedStreams.length);
        sb.append(" packed streams, ");
        sb.append(this.unpackSizes.length);
        sb.append(" unpack sizes, ");
        if (this.hasCrc) {
            str = "with CRC " + this.crc;
        } else {
            str = "without CRC";
        }
        sb.append(str);
        sb.append(" and ");
        sb.append(this.numUnpackSubStreams);
        sb.append(" unpack streams");
        return sb.toString();
    }
}

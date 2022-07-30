package org.apache.commons.compress.archivers.zip;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.compress.utils.IOUtils;

class BinaryTree {
    private static final int NODE = -2;
    private static final int UNDEFINED = -1;
    private final int[] tree;

    public BinaryTree(int depth) {
        if (depth < 0 || depth > 30) {
            throw new IllegalArgumentException("depth must be bigger than 0 and not bigger than 30 but is " + depth);
        }
        this.tree = new int[((int) ((1 << (depth + 1)) - 1))];
        Arrays.fill(this.tree, -1);
    }

    public void addLeaf(int node, int path, int depth, int value) {
        if (depth == 0) {
            int[] iArr = this.tree;
            if (iArr[node] == -1) {
                iArr[node] = value;
                return;
            }
            throw new IllegalArgumentException("Tree value at index " + node + " has already been assigned (" + this.tree[node] + ")");
        }
        this.tree[node] = -2;
        addLeaf((node * 2) + 1 + (path & 1), path >>> 1, depth - 1, value);
    }

    public int read(BitStream stream) throws IOException {
        int currentIndex = 0;
        while (true) {
            int bit = stream.nextBit();
            if (bit == -1) {
                return -1;
            }
            int childIndex = (currentIndex * 2) + 1 + bit;
            int value = this.tree[childIndex];
            if (value == -2) {
                currentIndex = childIndex;
            } else if (value != -1) {
                return value;
            } else {
                throw new IOException("The child " + bit + " of node at index " + currentIndex + " is not defined");
            }
        }
    }

    static BinaryTree decode(InputStream in, int totalNumberOfValues) throws IOException {
        int read;
        int i = totalNumberOfValues;
        if (i >= 0) {
            int size = in.read() + 1;
            if (size != 0) {
                byte[] encodedTree = new byte[size];
                int read2 = IOUtils.readFully(in, encodedTree);
                if (read2 == size) {
                    int[] originalBitLengths = new int[i];
                    int pos = 0;
                    int length = encodedTree.length;
                    int maxLength = 0;
                    int maxLength2 = 0;
                    while (maxLength2 < length) {
                        byte b = encodedTree[maxLength2];
                        int numberOfValues = ((b & 240) >> 4) + 1;
                        if (pos + numberOfValues <= i) {
                            int bitLength = (b & 15) + 1;
                            int j = 0;
                            while (j < numberOfValues) {
                                originalBitLengths[pos] = bitLength;
                                j++;
                                pos++;
                            }
                            maxLength = Math.max(maxLength, bitLength);
                            maxLength2++;
                        } else {
                            throw new IOException("Number of values exceeds given total number of values");
                        }
                    }
                    int[] permutation = new int[originalBitLengths.length];
                    for (int k = 0; k < permutation.length; k++) {
                        permutation[k] = k;
                    }
                    int c = 0;
                    int[] sortedBitLengths = new int[originalBitLengths.length];
                    for (int k2 = 0; k2 < originalBitLengths.length; k2++) {
                        for (int l = 0; l < originalBitLengths.length; l++) {
                            if (originalBitLengths[l] == k2) {
                                sortedBitLengths[c] = k2;
                                permutation[c] = l;
                                c++;
                            }
                        }
                    }
                    int code = 0;
                    int codeIncrement = 0;
                    int lastBitLength = 0;
                    int[] codes = new int[i];
                    for (int i2 = i - 1; i2 >= 0; i2--) {
                        code += codeIncrement;
                        if (sortedBitLengths[i2] != lastBitLength) {
                            int lastBitLength2 = sortedBitLengths[i2];
                            codeIncrement = 1 << (16 - lastBitLength2);
                            lastBitLength = lastBitLength2;
                        }
                        codes[permutation[i2]] = code;
                    }
                    BinaryTree tree2 = new BinaryTree(maxLength);
                    int i3 = size;
                    int k3 = 0;
                    while (true) {
                        byte[] encodedTree2 = encodedTree;
                        if (k3 < codes.length) {
                            int bitLength2 = originalBitLengths[k3];
                            if (bitLength2 > 0) {
                                read = read2;
                                tree2.addLeaf(0, Integer.reverse(codes[k3] << 16), bitLength2, k3);
                            } else {
                                read = read2;
                            }
                            k3++;
                            InputStream inputStream = in;
                            encodedTree = encodedTree2;
                            read2 = read;
                        } else {
                            return tree2;
                        }
                    }
                } else {
                    byte[] bArr = encodedTree;
                    int i4 = read2;
                    throw new EOFException();
                }
            } else {
                throw new IOException("Cannot read the size of the encoded tree, unexpected end of stream");
            }
        } else {
            throw new IllegalArgumentException("totalNumberOfValues must be bigger than 0, is " + i);
        }
    }
}

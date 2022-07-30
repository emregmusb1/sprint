package org.apache.commons.compress.archivers.zip;

import java.io.Serializable;
import java.math.BigInteger;

public final class ZipEightByteInteger implements Serializable {
    private static final int BYTE_1 = 1;
    private static final int BYTE_1_MASK = 65280;
    private static final int BYTE_1_SHIFT = 8;
    private static final int BYTE_2 = 2;
    private static final int BYTE_2_MASK = 16711680;
    private static final int BYTE_2_SHIFT = 16;
    private static final int BYTE_3 = 3;
    private static final long BYTE_3_MASK = 4278190080L;
    private static final int BYTE_3_SHIFT = 24;
    private static final int BYTE_4 = 4;
    private static final long BYTE_4_MASK = 1095216660480L;
    private static final int BYTE_4_SHIFT = 32;
    private static final int BYTE_5 = 5;
    private static final long BYTE_5_MASK = 280375465082880L;
    private static final int BYTE_5_SHIFT = 40;
    private static final int BYTE_6 = 6;
    private static final long BYTE_6_MASK = 71776119061217280L;
    private static final int BYTE_6_SHIFT = 48;
    private static final int BYTE_7 = 7;
    private static final long BYTE_7_MASK = 9151314442816847872L;
    private static final int BYTE_7_SHIFT = 56;
    private static final byte LEFTMOST_BIT = Byte.MIN_VALUE;
    private static final int LEFTMOST_BIT_SHIFT = 63;
    public static final ZipEightByteInteger ZERO = new ZipEightByteInteger(0);
    private static final long serialVersionUID = 1;
    private final BigInteger value;

    public ZipEightByteInteger(long value2) {
        this(BigInteger.valueOf(value2));
    }

    public ZipEightByteInteger(BigInteger value2) {
        this.value = value2;
    }

    public ZipEightByteInteger(byte[] bytes) {
        this(bytes, 0);
    }

    public ZipEightByteInteger(byte[] bytes, int offset) {
        this.value = getValue(bytes, offset);
    }

    public byte[] getBytes() {
        return getBytes(this.value);
    }

    public long getLongValue() {
        return this.value.longValue();
    }

    public BigInteger getValue() {
        return this.value;
    }

    public static byte[] getBytes(long value2) {
        return getBytes(BigInteger.valueOf(value2));
    }

    public static byte[] getBytes(BigInteger value2) {
        long val = value2.longValue();
        byte[] result = {(byte) ((int) (255 & val)), (byte) ((int) ((65280 & val) >> 8)), (byte) ((int) ((16711680 & val) >> 16)), (byte) ((int) ((BYTE_3_MASK & val) >> 24)), (byte) ((int) ((BYTE_4_MASK & val) >> 32)), (byte) ((int) ((BYTE_5_MASK & val) >> 40)), (byte) ((int) ((BYTE_6_MASK & val) >> 48)), (byte) ((int) ((BYTE_7_MASK & val) >> 56))};
        if (value2.testBit(63)) {
            result[7] = (byte) (result[7] | Byte.MIN_VALUE);
        }
        return result;
    }

    public static long getLongValue(byte[] bytes, int offset) {
        return getValue(bytes, offset).longValue();
    }

    public static BigInteger getValue(byte[] bytes, int offset) {
        BigInteger val = BigInteger.valueOf(((((long) bytes[offset + 7]) << 56) & BYTE_7_MASK) + ((((long) bytes[offset + 6]) << 48) & BYTE_6_MASK) + ((((long) bytes[offset + 5]) << 40) & BYTE_5_MASK) + ((((long) bytes[offset + 4]) << 32) & BYTE_4_MASK) + ((((long) bytes[offset + 3]) << 24) & BYTE_3_MASK) + ((((long) bytes[offset + 2]) << 16) & 16711680) + ((((long) bytes[offset + 1]) << 8) & 65280) + (((long) bytes[offset]) & 255));
        return (bytes[offset + 7] & Byte.MIN_VALUE) == Byte.MIN_VALUE ? val.setBit(63) : val;
    }

    public static long getLongValue(byte[] bytes) {
        return getLongValue(bytes, 0);
    }

    public static BigInteger getValue(byte[] bytes) {
        return getValue(bytes, 0);
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof ZipEightByteInteger)) {
            return false;
        }
        return this.value.equals(((ZipEightByteInteger) o).getValue());
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public String toString() {
        return "ZipEightByteInteger value: " + this.value;
    }
}

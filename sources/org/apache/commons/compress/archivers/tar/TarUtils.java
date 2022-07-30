package org.apache.commons.compress.archivers.tar;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import kotlin.jvm.internal.ByteCompanionObject;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;

public class TarUtils {
    private static final int BYTE_MASK = 255;
    static final ZipEncoding DEFAULT_ENCODING = ZipEncodingHelper.getZipEncoding((String) null);
    static final ZipEncoding FALLBACK_ENCODING = new ZipEncoding() {
        public boolean canEncode(String name) {
            return true;
        }

        public ByteBuffer encode(String name) {
            int length = name.length();
            byte[] buf = new byte[length];
            for (int i = 0; i < length; i++) {
                buf[i] = (byte) name.charAt(i);
            }
            return ByteBuffer.wrap(buf);
        }

        public String decode(byte[] buffer) {
            StringBuilder result = new StringBuilder(buffer.length);
            for (byte b : buffer) {
                if (b == 0) {
                    break;
                }
                result.append((char) (b & 255));
            }
            return result.toString();
        }
    };

    private TarUtils() {
    }

    public static long parseOctal(byte[] buffer, int offset, int length) {
        long result = 0;
        int end = offset + length;
        int start = offset;
        if (length < 2) {
            throw new IllegalArgumentException("Length " + length + " must be at least 2");
        } else if (buffer[start] == 0) {
            return 0;
        } else {
            while (start < end && buffer[start] == 32) {
                start++;
            }
            byte trailer = buffer[end - 1];
            while (start < end && (trailer == 0 || trailer == 32)) {
                end--;
                trailer = buffer[end - 1];
            }
            while (start < end) {
                byte currentByte = buffer[start];
                if (currentByte < 48 || currentByte > 55) {
                    throw new IllegalArgumentException(exceptionMessage(buffer, offset, length, start, currentByte));
                }
                result = (result << 3) + ((long) (currentByte - 48));
                start++;
            }
            return result;
        }
    }

    public static long parseOctalOrBinary(byte[] buffer, int offset, int length) {
        if ((buffer[offset] & ByteCompanionObject.MIN_VALUE) == 0) {
            return parseOctal(buffer, offset, length);
        }
        boolean negative = buffer[offset] == -1;
        if (length < 9) {
            return parseBinaryLong(buffer, offset, length, negative);
        }
        return parseBinaryBigInteger(buffer, offset, length, negative);
    }

    private static long parseBinaryLong(byte[] buffer, int offset, int length, boolean negative) {
        if (length < 9) {
            long val = 0;
            for (int i = 1; i < length; i++) {
                val = (val << 8) + ((long) (buffer[offset + i] & 255));
            }
            if (negative) {
                val = (val - 1) ^ (((long) Math.pow(2.0d, ((double) (length - 1)) * 8.0d)) - 1);
            }
            return negative ? -val : val;
        }
        throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number exceeds maximum signed long value");
    }

    private static long parseBinaryBigInteger(byte[] buffer, int offset, int length, boolean negative) {
        byte[] remainder = new byte[(length - 1)];
        System.arraycopy(buffer, offset + 1, remainder, 0, length - 1);
        BigInteger val = new BigInteger(remainder);
        if (negative) {
            val = val.add(BigInteger.valueOf(-1)).not();
        }
        if (val.bitLength() <= 63) {
            return negative ? -val.longValue() : val.longValue();
        }
        throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number exceeds maximum signed long value");
    }

    public static boolean parseBoolean(byte[] buffer, int offset) {
        return buffer[offset] == 1;
    }

    private static String exceptionMessage(byte[] buffer, int offset, int length, int current, byte currentByte) {
        String string = new String(buffer, offset, length).replaceAll("\u0000", "{NUL}");
        return "Invalid byte " + currentByte + " at offset " + (current - offset) + " in '" + string + "' len=" + length;
    }

    public static String parseName(byte[] buffer, int offset, int length) {
        try {
            return parseName(buffer, offset, length, DEFAULT_ENCODING);
        } catch (IOException e) {
            try {
                return parseName(buffer, offset, length, FALLBACK_ENCODING);
            } catch (IOException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }

    public static String parseName(byte[] buffer, int offset, int length, ZipEncoding encoding) throws IOException {
        int len = 0;
        int i = offset;
        while (len < length && buffer[i] != 0) {
            len++;
            i++;
        }
        if (len <= 0) {
            return "";
        }
        byte[] b = new byte[len];
        System.arraycopy(buffer, offset, b, 0, len);
        return encoding.decode(b);
    }

    public static int formatNameBytes(String name, byte[] buf, int offset, int length) {
        try {
            return formatNameBytes(name, buf, offset, length, DEFAULT_ENCODING);
        } catch (IOException e) {
            try {
                return formatNameBytes(name, buf, offset, length, FALLBACK_ENCODING);
            } catch (IOException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }

    public static int formatNameBytes(String name, byte[] buf, int offset, int length, ZipEncoding encoding) throws IOException {
        int len = name.length();
        ByteBuffer b = encoding.encode(name);
        while (b.limit() > length && len > 0) {
            len--;
            b = encoding.encode(name.substring(0, len));
        }
        int limit = b.limit() - b.position();
        System.arraycopy(b.array(), b.arrayOffset(), buf, offset, limit);
        for (int i = limit; i < length; i++) {
            buf[offset + i] = 0;
        }
        return offset + length;
    }

    public static void formatUnsignedOctalString(long value, byte[] buffer, int offset, int length) {
        int remaining = length - 1;
        if (value == 0) {
            buffer[remaining + offset] = TarConstants.LF_NORMAL;
            remaining--;
        } else {
            long val = value;
            while (remaining >= 0 && val != 0) {
                buffer[offset + remaining] = (byte) (((byte) ((int) (7 & val))) + TarConstants.LF_NORMAL);
                val >>>= 3;
                remaining--;
            }
            if (val != 0) {
                throw new IllegalArgumentException(value + "=" + Long.toOctalString(value) + " will not fit in octal number buffer of length " + length);
            }
        }
        while (remaining >= 0) {
            buffer[offset + remaining] = TarConstants.LF_NORMAL;
            remaining--;
        }
    }

    public static int formatOctalBytes(long value, byte[] buf, int offset, int length) {
        int idx = length - 2;
        formatUnsignedOctalString(value, buf, offset, idx);
        buf[idx + offset] = 32;
        buf[offset + idx + 1] = 0;
        return offset + length;
    }

    public static int formatLongOctalBytes(long value, byte[] buf, int offset, int length) {
        int idx = length - 1;
        formatUnsignedOctalString(value, buf, offset, idx);
        buf[offset + idx] = 32;
        return offset + length;
    }

    public static int formatLongOctalOrBinaryBytes(long value, byte[] buf, int offset, int length) {
        long maxAsOctalChar = length == 8 ? TarConstants.MAXID : TarConstants.MAXSIZE;
        boolean negative = value < 0;
        if (!negative && value <= maxAsOctalChar) {
            return formatLongOctalBytes(value, buf, offset, length);
        }
        if (length < 9) {
            formatLongBinary(value, buf, offset, length, negative);
        } else {
            formatBigIntegerBinary(value, buf, offset, length, negative);
        }
        buf[offset] = (byte) (negative ? 255 : 128);
        return offset + length;
    }

    private static void formatLongBinary(long value, byte[] buf, int offset, int length, boolean negative) {
        int i = offset;
        int i2 = length;
        int bits = (i2 - 1) * 8;
        long max = 1 << bits;
        long val = Math.abs(value);
        if (val < 0 || val >= max) {
            StringBuilder sb = new StringBuilder();
            sb.append("Value ");
            long j = value;
            sb.append(value);
            sb.append(" is too large for ");
            sb.append(i2);
            sb.append(" byte field.");
            throw new IllegalArgumentException(sb.toString());
        }
        if (negative) {
            val = ((val ^ (max - 1)) + 1) | (255 << bits);
        }
        for (int i3 = (i + i2) - 1; i3 >= i; i3--) {
            buf[i3] = (byte) ((int) val);
            val >>= 8;
        }
    }

    private static void formatBigIntegerBinary(long value, byte[] buf, int offset, int length, boolean negative) {
        byte[] b = BigInteger.valueOf(value).toByteArray();
        int len = b.length;
        if (len <= length - 1) {
            int off = (offset + length) - len;
            int i = 0;
            System.arraycopy(b, 0, buf, off, len);
            if (negative) {
                i = 255;
            }
            byte fill = (byte) i;
            for (int i2 = offset + 1; i2 < off; i2++) {
                buf[i2] = fill;
            }
            return;
        }
        throw new IllegalArgumentException("Value " + value + " is too large for " + length + " byte field.");
    }

    public static int formatCheckSumOctalBytes(long value, byte[] buf, int offset, int length) {
        int idx = length - 2;
        formatUnsignedOctalString(value, buf, offset, idx);
        buf[idx + offset] = 0;
        buf[offset + idx + 1] = 32;
        return offset + length;
    }

    public static long computeCheckSum(byte[] buf) {
        long sum = 0;
        for (byte element : buf) {
            sum += (long) (element & 255);
        }
        return sum;
    }

    public static boolean verifyCheckSum(byte[] header) {
        long storedSum = parseOctal(header, TarConstants.CHKSUM_OFFSET, 8);
        long unsignedSum = 0;
        long signedSum = 0;
        for (int i = 0; i < header.length; i++) {
            byte b = header[i];
            if (148 <= i && i < 156) {
                b = 32;
            }
            unsignedSum += (long) (b & 255);
            signedSum += (long) b;
        }
        return storedSum == unsignedSum || storedSum == signedSum;
    }
}

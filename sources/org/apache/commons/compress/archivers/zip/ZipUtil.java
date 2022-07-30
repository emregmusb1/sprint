package org.apache.commons.compress.archivers.zip;

import androidx.core.view.InputDeviceCompat;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;

public abstract class ZipUtil {
    private static final byte[] DOS_TIME_MIN = ZipLong.getBytes(8448);

    public static ZipLong toDosTime(Date time) {
        return new ZipLong(toDosTime(time.getTime()));
    }

    public static byte[] toDosTime(long t) {
        byte[] result = new byte[4];
        toDosTime(t, result, 0);
        return result;
    }

    public static void toDosTime(long t, byte[] buf, int offset) {
        toDosTime(Calendar.getInstance(), t, buf, offset);
    }

    static void toDosTime(Calendar c, long t, byte[] buf, int offset) {
        c.setTimeInMillis(t);
        int year = c.get(1);
        if (year < 1980) {
            copy(DOS_TIME_MIN, buf, offset);
            return;
        }
        ZipLong.putLong((long) ((c.get(13) >> 1) | ((year - 1980) << 25) | ((c.get(2) + 1) << 21) | (c.get(5) << 16) | (c.get(11) << 11) | (c.get(12) << 5)), buf, offset);
    }

    public static long adjustToLong(int i) {
        if (i < 0) {
            return ((long) i) + 4294967296L;
        }
        return (long) i;
    }

    public static byte[] reverse(byte[] array) {
        int z = array.length - 1;
        for (int i = 0; i < array.length / 2; i++) {
            byte x = array[i];
            array[i] = array[z - i];
            array[z - i] = x;
        }
        return array;
    }

    static long bigToLong(BigInteger big) {
        if (big.bitLength() <= 63) {
            return big.longValue();
        }
        throw new NumberFormatException("The BigInteger cannot fit inside a 64 bit java long: [" + big + "]");
    }

    static BigInteger longToBig(long l) {
        if (l >= -2147483648L) {
            if (l < 0 && l >= -2147483648L) {
                l = adjustToLong((int) l);
            }
            return BigInteger.valueOf(l);
        }
        throw new IllegalArgumentException("Negative longs < -2^31 not permitted: [" + l + "]");
    }

    public static int signedByteToUnsignedInt(byte b) {
        if (b >= 0) {
            return b;
        }
        return b + TarConstants.LF_OLDNORM;
    }

    public static byte unsignedIntToSignedByte(int i) {
        if (i > 255 || i < 0) {
            throw new IllegalArgumentException("Can only convert non-negative integers between [0,255] to byte: [" + i + "]");
        } else if (i < 128) {
            return (byte) i;
        } else {
            return (byte) (i + InputDeviceCompat.SOURCE_ANY);
        }
    }

    public static Date fromDosTime(ZipLong zipDosTime) {
        return new Date(dosToJavaTime(zipDosTime.getValue()));
    }

    public static long dosToJavaTime(long dosTime) {
        Calendar cal = Calendar.getInstance();
        cal.set(1, ((int) ((dosTime >> 25) & 127)) + 1980);
        cal.set(2, ((int) ((dosTime >> 21) & 15)) - 1);
        cal.set(5, ((int) (dosTime >> 16)) & 31);
        cal.set(11, ((int) (dosTime >> 11)) & 31);
        cal.set(12, ((int) (dosTime >> 5)) & 63);
        cal.set(13, ((int) (dosTime << 1)) & 62);
        cal.set(14, 0);
        return cal.getTime().getTime();
    }

    /* JADX WARNING: type inference failed for: r4v2, types: [org.apache.commons.compress.archivers.zip.ZipExtraField] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void setNameAndCommentFromExtraFields(org.apache.commons.compress.archivers.zip.ZipArchiveEntry r7, byte[] r8, byte[] r9) {
        /*
            org.apache.commons.compress.archivers.zip.ZipShort r0 = org.apache.commons.compress.archivers.zip.UnicodePathExtraField.UPATH_ID
            org.apache.commons.compress.archivers.zip.ZipExtraField r0 = r7.getExtraField(r0)
            boolean r1 = r0 instanceof org.apache.commons.compress.archivers.zip.UnicodePathExtraField
            r2 = 0
            if (r1 == 0) goto L_0x000f
            r1 = r0
            org.apache.commons.compress.archivers.zip.UnicodePathExtraField r1 = (org.apache.commons.compress.archivers.zip.UnicodePathExtraField) r1
            goto L_0x0010
        L_0x000f:
            r1 = r2
        L_0x0010:
            java.lang.String r3 = getUnicodeStringIfOriginalMatches(r1, r8)
            if (r3 == 0) goto L_0x001e
            r7.setName(r3)
            org.apache.commons.compress.archivers.zip.ZipArchiveEntry$NameSource r4 = org.apache.commons.compress.archivers.zip.ZipArchiveEntry.NameSource.UNICODE_EXTRA_FIELD
            r7.setNameSource(r4)
        L_0x001e:
            if (r9 == 0) goto L_0x003f
            int r4 = r9.length
            if (r4 <= 0) goto L_0x003f
            org.apache.commons.compress.archivers.zip.ZipShort r4 = org.apache.commons.compress.archivers.zip.UnicodeCommentExtraField.UCOM_ID
            org.apache.commons.compress.archivers.zip.ZipExtraField r4 = r7.getExtraField(r4)
            boolean r5 = r4 instanceof org.apache.commons.compress.archivers.zip.UnicodeCommentExtraField
            if (r5 == 0) goto L_0x0030
            r2 = r4
            org.apache.commons.compress.archivers.zip.UnicodeCommentExtraField r2 = (org.apache.commons.compress.archivers.zip.UnicodeCommentExtraField) r2
        L_0x0030:
            java.lang.String r5 = getUnicodeStringIfOriginalMatches(r2, r9)
            if (r5 == 0) goto L_0x003f
            r7.setComment(r5)
            org.apache.commons.compress.archivers.zip.ZipArchiveEntry$CommentSource r6 = org.apache.commons.compress.archivers.zip.ZipArchiveEntry.CommentSource.UNICODE_EXTRA_FIELD
            r7.setCommentSource(r6)
        L_0x003f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.zip.ZipUtil.setNameAndCommentFromExtraFields(org.apache.commons.compress.archivers.zip.ZipArchiveEntry, byte[], byte[]):void");
    }

    private static String getUnicodeStringIfOriginalMatches(AbstractUnicodeExtraField f, byte[] orig) {
        if (f != null) {
            CRC32 crc32 = new CRC32();
            crc32.update(orig);
            if (crc32.getValue() == f.getNameCRC32()) {
                try {
                    return ZipEncodingHelper.UTF8_ZIP_ENCODING.decode(f.getUnicodeName());
                } catch (IOException e) {
                    return null;
                }
            }
        }
        return null;
    }

    static byte[] copy(byte[] from) {
        if (from != null) {
            return Arrays.copyOf(from, from.length);
        }
        return null;
    }

    static void copy(byte[] from, byte[] to, int offset) {
        if (from != null) {
            System.arraycopy(from, 0, to, offset, from.length);
        }
    }

    static boolean canHandleEntryData(ZipArchiveEntry entry) {
        return supportsEncryptionOf(entry) && supportsMethodOf(entry);
    }

    private static boolean supportsEncryptionOf(ZipArchiveEntry entry) {
        return !entry.getGeneralPurposeBit().usesEncryption();
    }

    private static boolean supportsMethodOf(ZipArchiveEntry entry) {
        return entry.getMethod() == 0 || entry.getMethod() == ZipMethod.UNSHRINKING.getCode() || entry.getMethod() == ZipMethod.IMPLODING.getCode() || entry.getMethod() == 8 || entry.getMethod() == ZipMethod.ENHANCED_DEFLATED.getCode() || entry.getMethod() == ZipMethod.BZIP2.getCode();
    }

    static void checkRequestedFeatures(ZipArchiveEntry ze) throws UnsupportedZipFeatureException {
        if (!supportsEncryptionOf(ze)) {
            throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.ENCRYPTION, ze);
        } else if (!supportsMethodOf(ze)) {
            ZipMethod m = ZipMethod.getMethodByCode(ze.getMethod());
            if (m == null) {
                throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.METHOD, ze);
            }
            throw new UnsupportedZipFeatureException(m, ze);
        }
    }
}

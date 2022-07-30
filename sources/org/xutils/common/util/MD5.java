package org.xutils.common.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.compress.utils.CharsetNames;

public final class MD5 {
    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private MD5() {
    }

    public static String toHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(hexDigits[(b >> 4) & 15]);
            hex.append(hexDigits[b & 15]);
        }
        return hex.toString();
    }

    public static String md5(File file) throws IOException {
        FileChannel ch;
        NoSuchAlgorithmException neverHappened;
        FileInputStream in = null;
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            ch = in.getChannel();
            try {
                messagedigest.update(ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length()));
                byte[] encodeBytes = messagedigest.digest();
                IOUtil.closeQuietly((Closeable) in);
                IOUtil.closeQuietly((Closeable) ch);
                return toHexString(encodeBytes);
            } catch (NoSuchAlgorithmException e) {
                neverHappened = e;
                try {
                    throw new RuntimeException(neverHappened);
                } catch (Throwable th) {
                    neverHappened = th;
                    IOUtil.closeQuietly((Closeable) in);
                    IOUtil.closeQuietly((Closeable) ch);
                    throw neverHappened;
                }
            }
        } catch (NoSuchAlgorithmException e2) {
            NoSuchAlgorithmException noSuchAlgorithmException = e2;
            ch = null;
            neverHappened = noSuchAlgorithmException;
            throw new RuntimeException(neverHappened);
        } catch (Throwable th2) {
            NoSuchAlgorithmException noSuchAlgorithmException2 = th2;
            ch = null;
            neverHappened = noSuchAlgorithmException2;
            IOUtil.closeQuietly((Closeable) in);
            IOUtil.closeQuietly((Closeable) ch);
            throw neverHappened;
        }
    }

    public static String md5(String string) {
        try {
            return toHexString(MessageDigest.getInstance("MD5").digest(string.getBytes(CharsetNames.UTF_8)));
        } catch (NoSuchAlgorithmException neverHappened) {
            throw new RuntimeException(neverHappened);
        } catch (UnsupportedEncodingException neverHappened2) {
            throw new RuntimeException(neverHappened2);
        }
    }
}

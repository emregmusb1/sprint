package org.xutils.common.util;

import android.database.Cursor;
import android.text.TextUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import org.apache.commons.compress.utils.CharsetNames;

public class IOUtil {
    private IOUtil() {
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable ex) {
                LogUtil.d(ex.getMessage(), ex);
            }
        }
    }

    public static void closeQuietly(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Throwable ex) {
                LogUtil.d(ex.getMessage(), ex);
            }
        }
    }

    public static byte[] readBytes(InputStream in) throws IOException {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while (true) {
                int read = in.read(buf);
                int len = read;
                if (read == -1) {
                    return out.toByteArray();
                }
                out.write(buf, 0, len);
            }
        } finally {
            closeQuietly((Closeable) out);
        }
    }

    public static byte[] readBytes(InputStream in, long skip, int size) throws IOException {
        if (skip > 0) {
            while (skip > 0) {
                long skip2 = in.skip(skip);
                long skipped = skip2;
                if (skip2 <= 0) {
                    break;
                }
                skip -= skipped;
            }
        }
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) in.read();
        }
        return result;
    }

    public static String readStr(InputStream in) throws IOException {
        return readStr(in, CharsetNames.UTF_8);
    }

    public static String readStr(InputStream in, String charset) throws IOException {
        if (TextUtils.isEmpty(charset)) {
            charset = CharsetNames.UTF_8;
        }
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        Reader reader = new InputStreamReader(in, charset);
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        while (true) {
            int read = reader.read(buf);
            int len = read;
            if (read < 0) {
                return sb.toString();
            }
            sb.append(buf, 0, len);
        }
    }

    public static void writeStr(OutputStream out, String str) throws IOException {
        writeStr(out, str, CharsetNames.UTF_8);
    }

    public static void writeStr(OutputStream out, String str, String charset) throws IOException {
        if (TextUtils.isEmpty(charset)) {
            charset = CharsetNames.UTF_8;
        }
        Writer writer = new OutputStreamWriter(out, charset);
        writer.write(str);
        writer.flush();
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out);
        }
        byte[] buffer = new byte[1024];
        while (true) {
            int read = in.read(buffer);
            int len = read;
            if (read != -1) {
                out.write(buffer, 0, len);
            } else {
                out.flush();
                return;
            }
        }
    }

    public static boolean deleteFileOrDir(File path) {
        return FileUtil.deleteFileOrDir(path);
    }
}

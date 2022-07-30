package org.xutils.common.util;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.xutils.x;

public class FileUtil {
    private FileUtil() {
    }

    public static File getCacheDir(String dirName) {
        File cacheDir;
        File result = null;
        if (isDiskAvailable() && (cacheDir = x.app().getExternalCacheDir()) != null) {
            result = new File(cacheDir, dirName);
        }
        if (result == null) {
            result = new File(x.app().getCacheDir(), dirName);
        }
        if (result.exists() || result.mkdirs()) {
            return result;
        }
        return null;
    }

    public static boolean isDiskAvailable() {
        return getDiskAvailableSize() > 10485760;
    }

    public static long getDiskAvailableSize() {
        long availableBlocks;
        long blockSize;
        if (!existsSdcard().booleanValue()) {
            return 0;
        }
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        if (Build.VERSION.SDK_INT >= 18) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = (long) stat.getBlockSize();
            availableBlocks = (long) stat.getAvailableBlocks();
        }
        return availableBlocks * blockSize;
    }

    public static Boolean existsSdcard() {
        return Boolean.valueOf(Environment.getExternalStorageState().equals("mounted"));
    }

    public static long getFileOrDirSize(File file) {
        if (!file.exists()) {
            return 0;
        }
        if (!file.isDirectory()) {
            return file.length();
        }
        long length = 0;
        File[] list = file.listFiles();
        if (list != null) {
            for (File item : list) {
                length += getFileOrDirSize(item);
            }
        }
        return length;
    }

    public static boolean copy(String fromPath, String toPath) {
        boolean result = false;
        File from = new File(fromPath);
        if (!from.exists()) {
            return false;
        }
        File toFile = new File(toPath);
        IOUtil.deleteFileOrDir(toFile);
        File toDir = toFile.getParentFile();
        if (toDir.exists() || toDir.mkdirs()) {
            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream(from);
                out = new FileOutputStream(toFile);
                IOUtil.copy(in, out);
                result = true;
            } catch (Throwable th) {
                IOUtil.closeQuietly((Closeable) in);
                IOUtil.closeQuietly((Closeable) out);
                throw th;
            }
            IOUtil.closeQuietly((Closeable) in);
            IOUtil.closeQuietly((Closeable) out);
        }
        return result;
    }

    public static boolean deleteFileOrDir(File path) {
        if (path == null || !path.exists()) {
            return true;
        }
        if (path.isFile()) {
            return path.delete();
        }
        File[] files = path.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteFileOrDir(file);
            }
        }
        return path.delete();
    }
}

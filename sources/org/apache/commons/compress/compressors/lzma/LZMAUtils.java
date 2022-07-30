package org.apache.commons.compress.compressors.lzma;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.compress.compressors.FileNameUtil;

public class LZMAUtils {
    private static final byte[] HEADER_MAGIC = {93, 0, 0};
    private static volatile CachedAvailability cachedLZMAAvailability = CachedAvailability.DONT_CACHE;
    private static final FileNameUtil fileNameUtil;

    enum CachedAvailability {
        DONT_CACHE,
        CACHED_AVAILABLE,
        CACHED_UNAVAILABLE
    }

    static {
        Map<String, String> uncompressSuffix = new HashMap<>();
        uncompressSuffix.put(".lzma", "");
        uncompressSuffix.put("-lzma", "");
        fileNameUtil = new FileNameUtil(uncompressSuffix, ".lzma");
        try {
            Class.forName("org.osgi.framework.BundleEvent");
        } catch (Exception e) {
            setCacheLZMAAvailablity(true);
        }
    }

    private LZMAUtils() {
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < HEADER_MAGIC.length) {
            return false;
        }
        int i = 0;
        while (true) {
            byte[] bArr = HEADER_MAGIC;
            if (i >= bArr.length) {
                return true;
            }
            if (signature[i] != bArr[i]) {
                return false;
            }
            i++;
        }
    }

    public static boolean isLZMACompressionAvailable() {
        CachedAvailability cachedResult = cachedLZMAAvailability;
        if (cachedResult != CachedAvailability.DONT_CACHE) {
            return cachedResult == CachedAvailability.CACHED_AVAILABLE;
        }
        return internalIsLZMACompressionAvailable();
    }

    private static boolean internalIsLZMACompressionAvailable() {
        try {
            LZMACompressorInputStream.matches((byte[]) null, 0);
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    public static boolean isCompressedFilename(String fileName) {
        return fileNameUtil.isCompressedFilename(fileName);
    }

    public static String getUncompressedFilename(String fileName) {
        return fileNameUtil.getUncompressedFilename(fileName);
    }

    public static String getCompressedFilename(String fileName) {
        return fileNameUtil.getCompressedFilename(fileName);
    }

    public static void setCacheLZMAAvailablity(boolean doCache) {
        if (!doCache) {
            cachedLZMAAvailability = CachedAvailability.DONT_CACHE;
        } else if (cachedLZMAAvailability == CachedAvailability.DONT_CACHE) {
            cachedLZMAAvailability = internalIsLZMACompressionAvailable() ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE;
        }
    }

    static CachedAvailability getCachedLZMAAvailability() {
        return cachedLZMAAvailability;
    }
}

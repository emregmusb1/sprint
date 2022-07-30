package org.apache.commons.compress.compressors.zstandard;

public class ZstdUtils {
    private static final byte[] SKIPPABLE_FRAME_MAGIC = {42, 77, 24};
    private static final byte[] ZSTANDARD_FRAME_MAGIC = {40, -75, 47, -3};
    private static volatile CachedAvailability cachedZstdAvailability = CachedAvailability.DONT_CACHE;

    enum CachedAvailability {
        DONT_CACHE,
        CACHED_AVAILABLE,
        CACHED_UNAVAILABLE
    }

    static {
        try {
            Class.forName("org.osgi.framework.BundleEvent");
        } catch (Exception e) {
            setCacheZstdAvailablity(true);
        }
    }

    private ZstdUtils() {
    }

    public static boolean isZstdCompressionAvailable() {
        CachedAvailability cachedResult = cachedZstdAvailability;
        if (cachedResult != CachedAvailability.DONT_CACHE) {
            return cachedResult == CachedAvailability.CACHED_AVAILABLE;
        }
        return internalIsZstdCompressionAvailable();
    }

    private static boolean internalIsZstdCompressionAvailable() {
        try {
            Class.forName("com.github.luben.zstd.ZstdInputStream");
            return true;
        } catch (Exception | NoClassDefFoundError e) {
            return false;
        }
    }

    public static void setCacheZstdAvailablity(boolean doCache) {
        if (!doCache) {
            cachedZstdAvailability = CachedAvailability.DONT_CACHE;
        } else if (cachedZstdAvailability == CachedAvailability.DONT_CACHE) {
            cachedZstdAvailability = internalIsZstdCompressionAvailable() ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE;
        }
    }

    public static boolean matches(byte[] signature, int length) {
        if (length < ZSTANDARD_FRAME_MAGIC.length) {
            return false;
        }
        boolean isZstandard = true;
        int i = 0;
        while (true) {
            byte[] bArr = ZSTANDARD_FRAME_MAGIC;
            if (i >= bArr.length) {
                break;
            } else if (signature[i] != bArr[i]) {
                isZstandard = false;
                break;
            } else {
                i++;
            }
        }
        if (isZstandard) {
            return true;
        }
        if (80 != (signature[0] & 240)) {
            return false;
        }
        int i2 = 0;
        while (true) {
            byte[] bArr2 = SKIPPABLE_FRAME_MAGIC;
            if (i2 >= bArr2.length) {
                return true;
            }
            if (signature[i2 + 1] != bArr2[i2]) {
                return false;
            }
            i2++;
        }
    }

    static CachedAvailability getCachedZstdAvailability() {
        return cachedZstdAvailability;
    }
}

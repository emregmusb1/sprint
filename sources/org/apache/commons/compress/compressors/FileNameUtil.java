package org.apache.commons.compress.compressors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FileNameUtil {
    private final Map<String, String> compressSuffix = new HashMap();
    private final String defaultExtension;
    private final int longestCompressedSuffix;
    private final int longestUncompressedSuffix;
    private final int shortestCompressedSuffix;
    private final int shortestUncompressedSuffix;
    private final Map<String, String> uncompressSuffix;

    public FileNameUtil(Map<String, String> uncompressSuffix2, String defaultExtension2) {
        this.uncompressSuffix = Collections.unmodifiableMap(uncompressSuffix2);
        int lc = Integer.MIN_VALUE;
        int sc = Integer.MAX_VALUE;
        int lu = Integer.MIN_VALUE;
        int su = Integer.MAX_VALUE;
        for (Map.Entry<String, String> ent : uncompressSuffix2.entrySet()) {
            int cl = ent.getKey().length();
            lc = cl > lc ? cl : lc;
            sc = cl < sc ? cl : sc;
            String u = ent.getValue();
            int ul = u.length();
            if (ul > 0) {
                if (!this.compressSuffix.containsKey(u)) {
                    this.compressSuffix.put(u, ent.getKey());
                }
                lu = ul > lu ? ul : lu;
                if (ul < su) {
                    su = ul;
                }
            }
        }
        this.longestCompressedSuffix = lc;
        this.longestUncompressedSuffix = lu;
        this.shortestCompressedSuffix = sc;
        this.shortestUncompressedSuffix = su;
        this.defaultExtension = defaultExtension2;
    }

    public boolean isCompressedFilename(String fileName) {
        String lower = fileName.toLowerCase(Locale.ENGLISH);
        int n = lower.length();
        int i = this.shortestCompressedSuffix;
        while (i <= this.longestCompressedSuffix && i < n) {
            if (this.uncompressSuffix.containsKey(lower.substring(n - i))) {
                return true;
            }
            i++;
        }
        return false;
    }

    public String getUncompressedFilename(String fileName) {
        String lower = fileName.toLowerCase(Locale.ENGLISH);
        int n = lower.length();
        int i = this.shortestCompressedSuffix;
        while (i <= this.longestCompressedSuffix && i < n) {
            String suffix = this.uncompressSuffix.get(lower.substring(n - i));
            if (suffix != null) {
                return fileName.substring(0, n - i) + suffix;
            }
            i++;
        }
        return fileName;
    }

    public String getCompressedFilename(String fileName) {
        String lower = fileName.toLowerCase(Locale.ENGLISH);
        int n = lower.length();
        int i = this.shortestUncompressedSuffix;
        while (i <= this.longestUncompressedSuffix && i < n) {
            String suffix = this.compressSuffix.get(lower.substring(n - i));
            if (suffix != null) {
                return fileName.substring(0, n - i) + suffix;
            }
            i++;
        }
        return fileName + this.defaultExtension;
    }
}

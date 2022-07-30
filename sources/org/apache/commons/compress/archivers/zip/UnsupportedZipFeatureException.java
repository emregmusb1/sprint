package org.apache.commons.compress.archivers.zip;

import java.io.Serializable;
import java.util.zip.ZipException;

public class UnsupportedZipFeatureException extends ZipException {
    private static final long serialVersionUID = 20161219;
    private final transient ZipArchiveEntry entry;
    private final Feature reason;

    public UnsupportedZipFeatureException(Feature reason2, ZipArchiveEntry entry2) {
        super("Unsupported feature " + reason2 + " used in entry " + entry2.getName());
        this.reason = reason2;
        this.entry = entry2;
    }

    public UnsupportedZipFeatureException(ZipMethod method, ZipArchiveEntry entry2) {
        super("Unsupported compression method " + entry2.getMethod() + " (" + method.name() + ") used in entry " + entry2.getName());
        this.reason = Feature.METHOD;
        this.entry = entry2;
    }

    public UnsupportedZipFeatureException(Feature reason2) {
        super("Unsupported feature " + reason2 + " used in archive.");
        this.reason = reason2;
        this.entry = null;
    }

    public Feature getFeature() {
        return this.reason;
    }

    public ZipArchiveEntry getEntry() {
        return this.entry;
    }

    public static class Feature implements Serializable {
        public static final Feature DATA_DESCRIPTOR = new Feature("data descriptor");
        public static final Feature ENCRYPTION = new Feature("encryption");
        public static final Feature METHOD = new Feature("compression method");
        public static final Feature SPLITTING = new Feature("splitting");
        public static final Feature UNKNOWN_COMPRESSED_SIZE = new Feature("unknown compressed size");
        private static final long serialVersionUID = 4112582948775420359L;
        private final String name;

        private Feature(String name2) {
            this.name = name2;
        }

        public String toString() {
            return this.name;
        }
    }
}

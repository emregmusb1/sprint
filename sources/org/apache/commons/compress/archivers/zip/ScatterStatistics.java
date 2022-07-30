package org.apache.commons.compress.archivers.zip;

public class ScatterStatistics {
    private final long compressionElapsed;
    private final long mergingElapsed;

    ScatterStatistics(long compressionElapsed2, long mergingElapsed2) {
        this.compressionElapsed = compressionElapsed2;
        this.mergingElapsed = mergingElapsed2;
    }

    public long getCompressionElapsed() {
        return this.compressionElapsed;
    }

    public long getMergingElapsed() {
        return this.mergingElapsed;
    }

    public String toString() {
        return "compressionElapsed=" + this.compressionElapsed + "ms, mergingElapsed=" + this.mergingElapsed + "ms";
    }
}

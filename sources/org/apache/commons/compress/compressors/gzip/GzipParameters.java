package org.apache.commons.compress.compressors.gzip;

public class GzipParameters {
    private String comment;
    private int compressionLevel = -1;
    private String filename;
    private long modificationTime;
    private int operatingSystem = 255;

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public void setCompressionLevel(int compressionLevel2) {
        if (compressionLevel2 < -1 || compressionLevel2 > 9) {
            throw new IllegalArgumentException("Invalid gzip compression level: " + compressionLevel2);
        }
        this.compressionLevel = compressionLevel2;
    }

    public long getModificationTime() {
        return this.modificationTime;
    }

    public void setModificationTime(long modificationTime2) {
        this.modificationTime = modificationTime2;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String fileName) {
        this.filename = fileName;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment2) {
        this.comment = comment2;
    }

    public int getOperatingSystem() {
        return this.operatingSystem;
    }

    public void setOperatingSystem(int operatingSystem2) {
        this.operatingSystem = operatingSystem2;
    }
}

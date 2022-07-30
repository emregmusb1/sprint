package org.apache.commons.compress.archivers;

public class StreamingNotSupportedException extends ArchiveException {
    private static final long serialVersionUID = 1;
    private final String format;

    public StreamingNotSupportedException(String format2) {
        super("The " + format2 + " doesn't support streaming.");
        this.format = format2;
    }

    public String getFormat() {
        return this.format;
    }
}

package org.apache.commons.compress;

import java.io.IOException;

public class MemoryLimitException extends IOException {
    private static final long serialVersionUID = 1;
    private final int memoryLimitInKb;
    private final long memoryNeededInKb;

    public MemoryLimitException(long memoryNeededInKb2, int memoryLimitInKb2) {
        super(buildMessage(memoryNeededInKb2, memoryLimitInKb2));
        this.memoryNeededInKb = memoryNeededInKb2;
        this.memoryLimitInKb = memoryLimitInKb2;
    }

    public MemoryLimitException(long memoryNeededInKb2, int memoryLimitInKb2, Exception e) {
        super(buildMessage(memoryNeededInKb2, memoryLimitInKb2), e);
        this.memoryNeededInKb = memoryNeededInKb2;
        this.memoryLimitInKb = memoryLimitInKb2;
    }

    public long getMemoryNeededInKb() {
        return this.memoryNeededInKb;
    }

    public int getMemoryLimitInKb() {
        return this.memoryLimitInKb;
    }

    private static String buildMessage(long memoryNeededInKb2, int memoryLimitInKb2) {
        return memoryNeededInKb2 + " kb of memory would be needed; limit was " + memoryLimitInKb2 + " kb. If the file is not corrupt, consider increasing the memory limit.";
    }
}

package org.apache.commons.compress.archivers.sevenz;

public class SevenZFileOptions {
    public static final SevenZFileOptions DEFAULT = new SevenZFileOptions(Integer.MAX_VALUE, false);
    private static final boolean DEFAULT_USE_DEFAULTNAME_FOR_UNNAMED_ENTRIES = false;
    private static final int DEFAUL_MEMORY_LIMIT_IN_KB = Integer.MAX_VALUE;
    private final int maxMemoryLimitInKb;
    private final boolean useDefaultNameForUnnamedEntries;

    private SevenZFileOptions(int maxMemoryLimitInKb2, boolean useDefaultNameForUnnamedEntries2) {
        this.maxMemoryLimitInKb = maxMemoryLimitInKb2;
        this.useDefaultNameForUnnamedEntries = useDefaultNameForUnnamedEntries2;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getMaxMemoryLimitInKb() {
        return this.maxMemoryLimitInKb;
    }

    public boolean getUseDefaultNameForUnnamedEntries() {
        return this.useDefaultNameForUnnamedEntries;
    }

    public static class Builder {
        private int maxMemoryLimitInKb = Integer.MAX_VALUE;
        private boolean useDefaultNameForUnnamedEntries = false;

        public Builder withMaxMemoryLimitInKb(int maxMemoryLimitInKb2) {
            this.maxMemoryLimitInKb = maxMemoryLimitInKb2;
            return this;
        }

        public Builder withUseDefaultNameForUnnamedEntries(boolean useDefaultNameForUnnamedEntries2) {
            this.useDefaultNameForUnnamedEntries = useDefaultNameForUnnamedEntries2;
            return this;
        }

        public SevenZFileOptions build() {
            return new SevenZFileOptions(this.maxMemoryLimitInKb, this.useDefaultNameForUnnamedEntries);
        }
    }
}

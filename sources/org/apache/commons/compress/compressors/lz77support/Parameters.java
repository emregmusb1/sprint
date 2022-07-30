package org.apache.commons.compress.compressors.lz77support;

public final class Parameters {
    public static final int TRUE_MIN_BACK_REFERENCE_LENGTH = 3;
    private final boolean lazyMatching;
    private final int lazyThreshold;
    private final int maxBackReferenceLength;
    private final int maxCandidates;
    private final int maxLiteralLength;
    private final int maxOffset;
    private final int minBackReferenceLength;
    private final int niceBackReferenceLength;
    private final int windowSize;

    public static Builder builder(int windowSize2) {
        return new Builder(windowSize2);
    }

    public static class Builder {
        private Boolean lazyMatches;
        private Integer lazyThreshold;
        private int maxBackReferenceLength;
        private Integer maxCandidates;
        private int maxLiteralLength;
        private int maxOffset;
        private int minBackReferenceLength;
        private Integer niceBackReferenceLength;
        private final int windowSize;

        private Builder(int windowSize2) {
            if (windowSize2 < 2 || !Parameters.isPowerOfTwo(windowSize2)) {
                throw new IllegalArgumentException("windowSize must be a power of two");
            }
            this.windowSize = windowSize2;
            this.minBackReferenceLength = 3;
            this.maxBackReferenceLength = windowSize2 - 1;
            this.maxOffset = windowSize2 - 1;
            this.maxLiteralLength = windowSize2;
        }

        public Builder withMinBackReferenceLength(int minBackReferenceLength2) {
            this.minBackReferenceLength = Math.max(3, minBackReferenceLength2);
            int i = this.windowSize;
            int i2 = this.minBackReferenceLength;
            if (i >= i2) {
                if (this.maxBackReferenceLength < i2) {
                    this.maxBackReferenceLength = i2;
                }
                return this;
            }
            throw new IllegalArgumentException("minBackReferenceLength can't be bigger than windowSize");
        }

        public Builder withMaxBackReferenceLength(int maxBackReferenceLength2) {
            int i = this.minBackReferenceLength;
            if (maxBackReferenceLength2 >= i) {
                i = Math.min(maxBackReferenceLength2, this.windowSize - 1);
            }
            this.maxBackReferenceLength = i;
            return this;
        }

        public Builder withMaxOffset(int maxOffset2) {
            this.maxOffset = maxOffset2 < 1 ? this.windowSize - 1 : Math.min(maxOffset2, this.windowSize - 1);
            return this;
        }

        public Builder withMaxLiteralLength(int maxLiteralLength2) {
            int i;
            if (maxLiteralLength2 < 1) {
                i = this.windowSize;
            } else {
                i = Math.min(maxLiteralLength2, this.windowSize);
            }
            this.maxLiteralLength = i;
            return this;
        }

        public Builder withNiceBackReferenceLength(int niceLen) {
            this.niceBackReferenceLength = Integer.valueOf(niceLen);
            return this;
        }

        public Builder withMaxNumberOfCandidates(int maxCandidates2) {
            this.maxCandidates = Integer.valueOf(maxCandidates2);
            return this;
        }

        public Builder withLazyMatching(boolean lazy) {
            this.lazyMatches = Boolean.valueOf(lazy);
            return this;
        }

        public Builder withLazyThreshold(int threshold) {
            this.lazyThreshold = Integer.valueOf(threshold);
            return this;
        }

        public Builder tunedForSpeed() {
            this.niceBackReferenceLength = Integer.valueOf(Math.max(this.minBackReferenceLength, this.maxBackReferenceLength / 8));
            this.maxCandidates = Integer.valueOf(Math.max(32, this.windowSize / 1024));
            this.lazyMatches = false;
            this.lazyThreshold = Integer.valueOf(this.minBackReferenceLength);
            return this;
        }

        public Builder tunedForCompressionRatio() {
            Integer valueOf = Integer.valueOf(this.maxBackReferenceLength);
            this.lazyThreshold = valueOf;
            this.niceBackReferenceLength = valueOf;
            this.maxCandidates = Integer.valueOf(Math.max(32, this.windowSize / 16));
            this.lazyMatches = true;
            return this;
        }

        public Parameters build() {
            int niceLen;
            int threshold;
            int i;
            Integer num = this.niceBackReferenceLength;
            if (num != null) {
                niceLen = num.intValue();
            } else {
                niceLen = Math.max(this.minBackReferenceLength, this.maxBackReferenceLength / 2);
            }
            Integer num2 = this.maxCandidates;
            int candidates = num2 != null ? num2.intValue() : Math.max(256, this.windowSize / 128);
            Boolean bool = this.lazyMatches;
            boolean lazy = bool == null || bool.booleanValue();
            if (lazy) {
                Integer num3 = this.lazyThreshold;
                if (num3 != null) {
                    i = num3.intValue();
                } else {
                    threshold = niceLen;
                    return new Parameters(this.windowSize, this.minBackReferenceLength, this.maxBackReferenceLength, this.maxOffset, this.maxLiteralLength, niceLen, candidates, lazy, threshold);
                }
            } else {
                i = this.minBackReferenceLength;
            }
            threshold = i;
            return new Parameters(this.windowSize, this.minBackReferenceLength, this.maxBackReferenceLength, this.maxOffset, this.maxLiteralLength, niceLen, candidates, lazy, threshold);
        }
    }

    private Parameters(int windowSize2, int minBackReferenceLength2, int maxBackReferenceLength2, int maxOffset2, int maxLiteralLength2, int niceBackReferenceLength2, int maxCandidates2, boolean lazyMatching2, int lazyThreshold2) {
        this.windowSize = windowSize2;
        this.minBackReferenceLength = minBackReferenceLength2;
        this.maxBackReferenceLength = maxBackReferenceLength2;
        this.maxOffset = maxOffset2;
        this.maxLiteralLength = maxLiteralLength2;
        this.niceBackReferenceLength = niceBackReferenceLength2;
        this.maxCandidates = maxCandidates2;
        this.lazyMatching = lazyMatching2;
        this.lazyThreshold = lazyThreshold2;
    }

    public int getWindowSize() {
        return this.windowSize;
    }

    public int getMinBackReferenceLength() {
        return this.minBackReferenceLength;
    }

    public int getMaxBackReferenceLength() {
        return this.maxBackReferenceLength;
    }

    public int getMaxOffset() {
        return this.maxOffset;
    }

    public int getMaxLiteralLength() {
        return this.maxLiteralLength;
    }

    public int getNiceBackReferenceLength() {
        return this.niceBackReferenceLength;
    }

    public int getMaxCandidates() {
        return this.maxCandidates;
    }

    public boolean getLazyMatching() {
        return this.lazyMatching;
    }

    public int getLazyMatchingThreshold() {
        return this.lazyThreshold;
    }

    /* access modifiers changed from: private */
    public static final boolean isPowerOfTwo(int x) {
        return ((x + -1) & x) == 0;
    }
}

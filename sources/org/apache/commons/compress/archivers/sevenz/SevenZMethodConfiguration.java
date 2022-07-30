package org.apache.commons.compress.archivers.sevenz;

import java.util.Objects;

public class SevenZMethodConfiguration {
    private final SevenZMethod method;
    private final Object options;

    public SevenZMethodConfiguration(SevenZMethod method2) {
        this(method2, (Object) null);
    }

    public SevenZMethodConfiguration(SevenZMethod method2, Object options2) {
        this.method = method2;
        this.options = options2;
        if (options2 != null && !Coders.findByMethod(method2).canAcceptOptions(options2)) {
            throw new IllegalArgumentException("The " + method2 + " method doesn't support options of type " + options2.getClass());
        }
    }

    public SevenZMethod getMethod() {
        return this.method;
    }

    public Object getOptions() {
        return this.options;
    }

    public int hashCode() {
        SevenZMethod sevenZMethod = this.method;
        if (sevenZMethod == null) {
            return 0;
        }
        return sevenZMethod.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SevenZMethodConfiguration other = (SevenZMethodConfiguration) obj;
        if (!Objects.equals(this.method, other.method) || !Objects.equals(this.options, other.options)) {
            return false;
        }
        return true;
    }
}

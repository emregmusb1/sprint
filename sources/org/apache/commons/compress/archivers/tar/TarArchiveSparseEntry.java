package org.apache.commons.compress.archivers.tar;

import java.io.IOException;

public class TarArchiveSparseEntry implements TarConstants {
    private final boolean isExtended;

    public TarArchiveSparseEntry(byte[] headerBuf) throws IOException {
        this.isExtended = TarUtils.parseBoolean(headerBuf, 0 + TarConstants.SPARSELEN_GNU_SPARSE);
    }

    public boolean isExtended() {
        return this.isExtended;
    }
}

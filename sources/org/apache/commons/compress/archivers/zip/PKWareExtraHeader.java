package org.apache.commons.compress.archivers.zip;

import androidx.core.internal.view.SupportMenu;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;

public abstract class PKWareExtraHeader implements ZipExtraField {
    private byte[] centralData;
    private final ZipShort headerId;
    private byte[] localData;

    protected PKWareExtraHeader(ZipShort headerId2) {
        this.headerId = headerId2;
    }

    public ZipShort getHeaderId() {
        return this.headerId;
    }

    public void setLocalFileDataData(byte[] data) {
        this.localData = ZipUtil.copy(data);
    }

    public ZipShort getLocalFileDataLength() {
        byte[] bArr = this.localData;
        return new ZipShort(bArr != null ? bArr.length : 0);
    }

    public byte[] getLocalFileDataData() {
        return ZipUtil.copy(this.localData);
    }

    public void setCentralDirectoryData(byte[] data) {
        this.centralData = ZipUtil.copy(data);
    }

    public ZipShort getCentralDirectoryLength() {
        byte[] bArr = this.centralData;
        if (bArr != null) {
            return new ZipShort(bArr.length);
        }
        return getLocalFileDataLength();
    }

    public byte[] getCentralDirectoryData() {
        byte[] bArr = this.centralData;
        if (bArr != null) {
            return ZipUtil.copy(bArr);
        }
        return getLocalFileDataData();
    }

    public void parseFromLocalFileData(byte[] data, int offset, int length) throws ZipException {
        setLocalFileDataData(Arrays.copyOfRange(data, offset, offset + length));
    }

    public void parseFromCentralDirectoryData(byte[] data, int offset, int length) throws ZipException {
        byte[] tmp = Arrays.copyOfRange(data, offset, offset + length);
        setCentralDirectoryData(tmp);
        if (this.localData == null) {
            setLocalFileDataData(tmp);
        }
    }

    /* access modifiers changed from: protected */
    public final void assertMinimalLength(int minimum, int length) throws ZipException {
        if (length < minimum) {
            throw new ZipException(getClass().getName() + " is too short, only " + length + " bytes, expected at least " + minimum);
        }
    }

    public enum EncryptionAlgorithm {
        DES(26113),
        RC2pre52(26114),
        TripleDES168(26115),
        TripleDES192(26121),
        AES128(26126),
        AES192(26127),
        AES256(26128),
        RC2(26370),
        RC4(26625),
        UNKNOWN(SupportMenu.USER_MASK);
        
        private static final Map<Integer, EncryptionAlgorithm> codeToEnum = null;
        private final int code;

        static {
            int i;
            Map<Integer, EncryptionAlgorithm> cte = new HashMap<>();
            for (EncryptionAlgorithm method : values()) {
                cte.put(Integer.valueOf(method.getCode()), method);
            }
            codeToEnum = Collections.unmodifiableMap(cte);
        }

        private EncryptionAlgorithm(int code2) {
            this.code = code2;
        }

        public int getCode() {
            return this.code;
        }

        public static EncryptionAlgorithm getAlgorithmByCode(int code2) {
            return codeToEnum.get(Integer.valueOf(code2));
        }
    }

    public enum HashAlgorithm {
        NONE(0),
        CRC32(1),
        MD5(32771),
        SHA1(32772),
        RIPEND160(32775),
        SHA256(32780),
        SHA384(32781),
        SHA512(32782);
        
        private static final Map<Integer, HashAlgorithm> codeToEnum = null;
        private final int code;

        static {
            int i;
            Map<Integer, HashAlgorithm> cte = new HashMap<>();
            for (HashAlgorithm method : values()) {
                cte.put(Integer.valueOf(method.getCode()), method);
            }
            codeToEnum = Collections.unmodifiableMap(cte);
        }

        private HashAlgorithm(int code2) {
            this.code = code2;
        }

        public int getCode() {
            return this.code;
        }

        public static HashAlgorithm getAlgorithmByCode(int code2) {
            return codeToEnum.get(Integer.valueOf(code2));
        }
    }
}

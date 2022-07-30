package org.apache.commons.compress.archivers.zip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipException;

public class ExtraFieldUtils {
    private static final int WORD = 4;
    private static final Map<ZipShort, Class<?>> implementations = new ConcurrentHashMap();

    static {
        register(AsiExtraField.class);
        register(X5455_ExtendedTimestamp.class);
        register(X7875_NewUnix.class);
        register(JarMarker.class);
        register(UnicodePathExtraField.class);
        register(UnicodeCommentExtraField.class);
        register(Zip64ExtendedInformationExtraField.class);
        register(X000A_NTFS.class);
        register(X0014_X509Certificates.class);
        register(X0015_CertificateIdForFile.class);
        register(X0016_CertificateIdForCentralDirectory.class);
        register(X0017_StrongEncryptionHeader.class);
        register(X0019_EncryptionRecipientCertificateList.class);
        register(ResourceAlignmentExtraField.class);
    }

    public static void register(Class<?> c) {
        try {
            implementations.put(((ZipExtraField) c.newInstance()).getHeaderId(), c);
        } catch (ClassCastException e) {
            throw new RuntimeException(c + " doesn't implement ZipExtraField");
        } catch (InstantiationException e2) {
            throw new RuntimeException(c + " is not a concrete class");
        } catch (IllegalAccessException e3) {
            throw new RuntimeException(c + "'s no-arg constructor is not public");
        }
    }

    public static ZipExtraField createExtraField(ZipShort headerId) throws InstantiationException, IllegalAccessException {
        ZipExtraField field = createExtraFieldNoDefault(headerId);
        if (field != null) {
            return field;
        }
        UnrecognizedExtraField u = new UnrecognizedExtraField();
        u.setHeaderId(headerId);
        return u;
    }

    public static ZipExtraField createExtraFieldNoDefault(ZipShort headerId) throws InstantiationException, IllegalAccessException {
        Class<?> c = implementations.get(headerId);
        if (c != null) {
            return (ZipExtraField) c.newInstance();
        }
        return null;
    }

    public static ZipExtraField[] parse(byte[] data) throws ZipException {
        return parse(data, true, UnparseableExtraField.THROW);
    }

    public static ZipExtraField[] parse(byte[] data, boolean local) throws ZipException {
        return parse(data, local, UnparseableExtraField.THROW);
    }

    public static ZipExtraField[] parse(byte[] data, boolean local, final UnparseableExtraField onUnparseableData) throws ZipException {
        return parse(data, local, (ExtraFieldParsingBehavior) new ExtraFieldParsingBehavior() {
            public ZipExtraField onUnparseableExtraField(byte[] data, int off, int len, boolean local, int claimedLength) throws ZipException {
                return onUnparseableData.onUnparseableExtraField(data, off, len, local, claimedLength);
            }

            public ZipExtraField createExtraField(ZipShort headerId) throws ZipException, InstantiationException, IllegalAccessException {
                return ExtraFieldUtils.createExtraField(headerId);
            }

            public ZipExtraField fill(ZipExtraField field, byte[] data, int off, int len, boolean local) throws ZipException {
                return ExtraFieldUtils.fillExtraField(field, data, off, len, local);
            }
        });
    }

    public static ZipExtraField[] parse(byte[] data, boolean local, ExtraFieldParsingBehavior parsingBehavior) throws ZipException {
        List<ZipExtraField> v = new ArrayList<>();
        int start = 0;
        while (true) {
            if (start > data.length - 4) {
                break;
            }
            ZipShort headerId = new ZipShort(data, start);
            int length = new ZipShort(data, start + 2).getValue();
            if (start + 4 + length > data.length) {
                ZipExtraField field = parsingBehavior.onUnparseableExtraField(data, start, data.length - start, local, length);
                if (field != null) {
                    v.add(field);
                }
            } else {
                try {
                    v.add(Objects.requireNonNull(parsingBehavior.fill((ZipExtraField) Objects.requireNonNull(parsingBehavior.createExtraField(headerId), "createExtraField must not return null"), data, start + 4, length, local), "fill must not return null"));
                    start += length + 4;
                } catch (IllegalAccessException | InstantiationException ie) {
                    throw ((ZipException) new ZipException(ie.getMessage()).initCause(ie));
                }
            }
        }
        return (ZipExtraField[]) v.toArray(new ZipExtraField[v.size()]);
    }

    public static byte[] mergeLocalFileDataData(ZipExtraField[] data) {
        byte[] local;
        boolean lastIsUnparseableHolder = data.length > 0 && (data[data.length - 1] instanceof UnparseableExtraFieldData);
        int regularExtraFieldCount = lastIsUnparseableHolder ? data.length - 1 : data.length;
        int sum = regularExtraFieldCount * 4;
        for (ZipExtraField element : data) {
            sum += element.getLocalFileDataLength().getValue();
        }
        byte[] result = new byte[sum];
        int start = 0;
        for (int i = 0; i < regularExtraFieldCount; i++) {
            System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
            System.arraycopy(data[i].getLocalFileDataLength().getBytes(), 0, result, start + 2, 2);
            start += 4;
            byte[] local2 = data[i].getLocalFileDataData();
            if (local2 != null) {
                System.arraycopy(local2, 0, result, start, local2.length);
                start += local2.length;
            }
        }
        if (lastIsUnparseableHolder && (local = data[data.length - 1].getLocalFileDataData()) != null) {
            System.arraycopy(local, 0, result, start, local.length);
        }
        return result;
    }

    public static byte[] mergeCentralDirectoryData(ZipExtraField[] data) {
        byte[] central;
        boolean lastIsUnparseableHolder = data.length > 0 && (data[data.length - 1] instanceof UnparseableExtraFieldData);
        int regularExtraFieldCount = lastIsUnparseableHolder ? data.length - 1 : data.length;
        int sum = regularExtraFieldCount * 4;
        for (ZipExtraField element : data) {
            sum += element.getCentralDirectoryLength().getValue();
        }
        byte[] result = new byte[sum];
        int start = 0;
        for (int i = 0; i < regularExtraFieldCount; i++) {
            System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
            System.arraycopy(data[i].getCentralDirectoryLength().getBytes(), 0, result, start + 2, 2);
            start += 4;
            byte[] central2 = data[i].getCentralDirectoryData();
            if (central2 != null) {
                System.arraycopy(central2, 0, result, start, central2.length);
                start += central2.length;
            }
        }
        if (lastIsUnparseableHolder && (central = data[data.length - 1].getCentralDirectoryData()) != null) {
            System.arraycopy(central, 0, result, start, central.length);
        }
        return result;
    }

    public static ZipExtraField fillExtraField(ZipExtraField ze, byte[] data, int off, int len, boolean local) throws ZipException {
        if (local) {
            try {
                ze.parseFromLocalFileData(data, off, len);
            } catch (ArrayIndexOutOfBoundsException aiobe) {
                throw ((ZipException) new ZipException("Failed to parse corrupt ZIP extra field of type " + Integer.toHexString(ze.getHeaderId().getValue())).initCause(aiobe));
            }
        } else {
            ze.parseFromCentralDirectoryData(data, off, len);
        }
        return ze;
    }

    public static final class UnparseableExtraField implements UnparseableExtraFieldBehavior {
        public static final UnparseableExtraField READ = new UnparseableExtraField(2);
        public static final int READ_KEY = 2;
        public static final UnparseableExtraField SKIP = new UnparseableExtraField(1);
        public static final int SKIP_KEY = 1;
        public static final UnparseableExtraField THROW = new UnparseableExtraField(0);
        public static final int THROW_KEY = 0;
        private final int key;

        private UnparseableExtraField(int k) {
            this.key = k;
        }

        public int getKey() {
            return this.key;
        }

        public ZipExtraField onUnparseableExtraField(byte[] data, int off, int len, boolean local, int claimedLength) throws ZipException {
            int i = this.key;
            if (i == 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("Bad extra field starting at ");
                sb.append(off);
                sb.append(".  Block length of ");
                sb.append(claimedLength);
                sb.append(" bytes exceeds remaining data of ");
                sb.append(len - 4);
                sb.append(" bytes.");
                throw new ZipException(sb.toString());
            } else if (i == 1) {
                return null;
            } else {
                if (i == 2) {
                    UnparseableExtraFieldData field = new UnparseableExtraFieldData();
                    if (local) {
                        field.parseFromLocalFileData(data, off, len);
                    } else {
                        field.parseFromCentralDirectoryData(data, off, len);
                    }
                    return field;
                }
                throw new ZipException("Unknown UnparseableExtraField key: " + this.key);
            }
        }
    }
}

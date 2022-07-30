package org.apache.commons.compress.archivers.jar;

import java.security.cert.Certificate;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

public class JarArchiveEntry extends ZipArchiveEntry {
    private final Certificate[] certificates = null;
    private final Attributes manifestAttributes = null;

    public JarArchiveEntry(ZipEntry entry) throws ZipException {
        super(entry);
    }

    public JarArchiveEntry(String name) {
        super(name);
    }

    public JarArchiveEntry(ZipArchiveEntry entry) throws ZipException {
        super(entry);
    }

    public JarArchiveEntry(JarEntry entry) throws ZipException {
        super((ZipEntry) entry);
    }

    @Deprecated
    public Attributes getManifestAttributes() {
        return this.manifestAttributes;
    }

    @Deprecated
    public Certificate[] getCertificates() {
        Certificate[] certificateArr = this.certificates;
        if (certificateArr == null) {
            return null;
        }
        Certificate[] certs = new Certificate[certificateArr.length];
        System.arraycopy(certificateArr, 0, certs, 0, certs.length);
        return certs;
    }
}

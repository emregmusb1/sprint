package org.xutils.cache;

import java.util.Date;
import kotlin.jvm.internal.LongCompanionObject;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "disk_cache")
public final class DiskCacheEntity {
    @Column(name = "bytesContent")
    private byte[] bytesContent;
    @Column(name = "etag")
    private String etag;
    @Column(name = "expires")
    private long expires = LongCompanionObject.MAX_VALUE;
    @Column(name = "hits")
    private long hits;
    @Column(isId = true, name = "id")
    private long id;
    @Column(name = "key", property = "UNIQUE")
    private String key;
    @Column(name = "lastAccess")
    private long lastAccess;
    @Column(name = "lastModify")
    private Date lastModify;
    @Column(name = "path")
    private String path;
    @Column(name = "textContent")
    private String textContent;

    public long getId() {
        return this.id;
    }

    public void setId(long id2) {
        this.id = id2;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key2) {
        this.key = key2;
    }

    /* access modifiers changed from: package-private */
    public String getPath() {
        return this.path;
    }

    /* access modifiers changed from: package-private */
    public void setPath(String path2) {
        this.path = path2;
    }

    public String getTextContent() {
        return this.textContent;
    }

    public void setTextContent(String textContent2) {
        this.textContent = textContent2;
    }

    public byte[] getBytesContent() {
        return this.bytesContent;
    }

    public void setBytesContent(byte[] bytesContent2) {
        this.bytesContent = bytesContent2;
    }

    public long getExpires() {
        return this.expires;
    }

    public void setExpires(long expires2) {
        this.expires = expires2;
    }

    public String getEtag() {
        return this.etag;
    }

    public void setEtag(String etag2) {
        this.etag = etag2;
    }

    public long getHits() {
        return this.hits;
    }

    public void setHits(long hits2) {
        this.hits = hits2;
    }

    public Date getLastModify() {
        return this.lastModify;
    }

    public void setLastModify(Date lastModify2) {
        this.lastModify = lastModify2;
    }

    public long getLastAccess() {
        long j = this.lastAccess;
        return j == 0 ? System.currentTimeMillis() : j;
    }

    public void setLastAccess(long lastAccess2) {
        this.lastAccess = lastAccess2;
    }
}

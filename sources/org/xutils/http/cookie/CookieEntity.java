package org.xutils.http.cookie;

import android.text.TextUtils;
import java.net.HttpCookie;
import java.net.URI;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "cookie", onCreated = "CREATE UNIQUE INDEX index_cookie_unique ON cookie(\"name\",\"domain\",\"path\")")
final class CookieEntity {
    private static final long MAX_EXPIRY = (System.currentTimeMillis() + 3110400000000L);
    @Column(name = "comment")
    private String comment;
    @Column(name = "commentURL")
    private String commentURL;
    @Column(name = "discard")
    private boolean discard;
    @Column(name = "domain")
    private String domain;
    @Column(name = "expiry")
    private long expiry = MAX_EXPIRY;
    @Column(isId = true, name = "id")
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "path")
    private String path;
    @Column(name = "portList")
    private String portList;
    @Column(name = "secure")
    private boolean secure;
    @Column(name = "uri")
    private String uri;
    @Column(name = "value")
    private String value;
    @Column(name = "version")
    private int version = 1;

    public CookieEntity() {
    }

    public CookieEntity(URI uri2, HttpCookie cookie) {
        this.uri = uri2 == null ? null : uri2.toString();
        this.name = cookie.getName();
        this.value = cookie.getValue();
        this.comment = cookie.getComment();
        this.commentURL = cookie.getCommentURL();
        this.discard = cookie.getDiscard();
        this.domain = cookie.getDomain();
        long maxAge = cookie.getMaxAge();
        if (maxAge > 0) {
            this.expiry = (1000 * maxAge) + System.currentTimeMillis();
            if (this.expiry < 0) {
                this.expiry = MAX_EXPIRY;
            }
        } else {
            this.expiry = -1;
        }
        this.path = cookie.getPath();
        if (!TextUtils.isEmpty(this.path) && this.path.length() > 1 && this.path.endsWith("/")) {
            String str = this.path;
            this.path = str.substring(0, str.length() - 1);
        }
        this.portList = cookie.getPortlist();
        this.secure = cookie.getSecure();
        this.version = cookie.getVersion();
    }

    public HttpCookie toHttpCookie() {
        HttpCookie cookie = new HttpCookie(this.name, this.value);
        cookie.setComment(this.comment);
        cookie.setCommentURL(this.commentURL);
        cookie.setDiscard(this.discard);
        cookie.setDomain(this.domain);
        long j = this.expiry;
        if (j == -1) {
            cookie.setMaxAge(-1);
        } else {
            cookie.setMaxAge((j - System.currentTimeMillis()) / 1000);
        }
        cookie.setPath(this.path);
        cookie.setPortlist(this.portList);
        cookie.setSecure(this.secure);
        cookie.setVersion(this.version);
        return cookie;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id2) {
        this.id = id2;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri2) {
        this.uri = uri2;
    }

    public boolean isExpired() {
        long j = this.expiry;
        return j != -1 && j < System.currentTimeMillis();
    }
}

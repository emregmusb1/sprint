package org.xutils.http.cookie;

import android.text.TextUtils;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.config.DbConfigs;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.x;

public enum DbCookieStore implements CookieStore {
    INSTANCE;
    
    private static final int LIMIT_COUNT = 5000;
    private static final long TRIM_TIME_SPAN = 1000;
    /* access modifiers changed from: private */
    public DbManager db;
    /* access modifiers changed from: private */
    public long lastTrimTime;
    private final Executor trimExecutor;

    /* access modifiers changed from: private */
    public void tryInit() {
        if (this.db == null) {
            synchronized (this) {
                if (this.db == null) {
                    try {
                        this.db = x.getDb(DbConfigs.COOKIE.getConfig());
                        this.db.delete(CookieEntity.class, WhereBuilder.b("expiry", "=", -1L));
                    } catch (Throwable ex) {
                        LogUtil.e(ex.getMessage(), ex);
                    }
                }
            }
        }
    }

    public void add(URI uri, HttpCookie cookie) {
        if (cookie != null) {
            tryInit();
            try {
                this.db.replace(new CookieEntity(getEffectiveURI(uri), cookie));
            } catch (Throwable ex) {
                LogUtil.e(ex.getMessage(), ex);
            }
            trimSize();
        }
    }

    public List<HttpCookie> get(URI uri) {
        if (uri != null) {
            tryInit();
            URI uri2 = getEffectiveURI(uri);
            List<HttpCookie> rt = new ArrayList<>();
            try {
                Selector<CookieEntity> selector = this.db.selector(CookieEntity.class);
                WhereBuilder where = WhereBuilder.b();
                String host = uri2.getHost();
                if (!TextUtils.isEmpty(host)) {
                    WhereBuilder b = WhereBuilder.b("domain", "=", host);
                    WhereBuilder subWhere = b.or("domain", "=", "." + host);
                    int firstDot = host.indexOf(".");
                    int lastDot = host.lastIndexOf(".");
                    if (firstDot > 0 && lastDot > firstDot) {
                        String domain = host.substring(firstDot, host.length());
                        if (!TextUtils.isEmpty(domain)) {
                            subWhere.or("domain", "=", domain);
                        }
                    }
                    where.and(subWhere);
                }
                String path = uri2.getPath();
                if (!TextUtils.isEmpty(path)) {
                    WhereBuilder subWhere2 = WhereBuilder.b("path", "=", path).or("path", "=", "/").or("path", "=", (Object) null);
                    int lastSplit = path.lastIndexOf("/");
                    while (lastSplit > 0) {
                        path = path.substring(0, lastSplit);
                        subWhere2.or("path", "=", path);
                        lastSplit = path.lastIndexOf("/");
                    }
                    where.and(subWhere2);
                }
                where.or("uri", "=", uri2.toString());
                List<CookieEntity> cookieEntityList = selector.where(where).findAll();
                if (cookieEntityList != null) {
                    for (CookieEntity cookieEntity : cookieEntityList) {
                        if (!cookieEntity.isExpired()) {
                            rt.add(cookieEntity.toHttpCookie());
                        }
                    }
                }
            } catch (Throwable ex) {
                LogUtil.e(ex.getMessage(), ex);
            }
            return rt;
        }
        throw new NullPointerException("uri is null");
    }

    public List<HttpCookie> getCookies() {
        tryInit();
        List<HttpCookie> rt = new ArrayList<>();
        try {
            List<CookieEntity> cookieEntityList = this.db.findAll(CookieEntity.class);
            if (cookieEntityList != null) {
                for (CookieEntity cookieEntity : cookieEntityList) {
                    if (!cookieEntity.isExpired()) {
                        rt.add(cookieEntity.toHttpCookie());
                    }
                }
            }
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
        return rt;
    }

    public List<URI> getURIs() {
        String uri;
        tryInit();
        List<URI> uris = new ArrayList<>();
        try {
            List<DbModel> uriList = this.db.selector(CookieEntity.class).select("uri").findAll();
            if (uriList != null) {
                for (DbModel model : uriList) {
                    uri = model.getString("uri");
                    if (!TextUtils.isEmpty(uri)) {
                        uris.add(new URI(uri));
                    }
                }
            }
        } catch (Throwable throwable) {
            LogUtil.e(throwable.getMessage(), throwable);
        }
        return uris;
    }

    public boolean remove(URI uri, HttpCookie cookie) {
        if (cookie == null) {
            return true;
        }
        tryInit();
        try {
            WhereBuilder where = WhereBuilder.b("name", "=", cookie.getName());
            String domain = cookie.getDomain();
            if (!TextUtils.isEmpty(domain)) {
                where.and("domain", "=", domain);
            }
            String path = cookie.getPath();
            if (!TextUtils.isEmpty(path)) {
                if (path.length() > 1 && path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }
                where.and("path", "=", path);
            }
            this.db.delete(CookieEntity.class, where);
            return true;
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
            return false;
        }
    }

    public boolean removeAll() {
        tryInit();
        try {
            this.db.delete((Class<?>) CookieEntity.class);
            return true;
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
            return true;
        }
    }

    private void trimSize() {
        this.trimExecutor.execute(new Runnable() {
            public void run() {
                List<CookieEntity> rmList;
                DbCookieStore.this.tryInit();
                long current = System.currentTimeMillis();
                if (current - DbCookieStore.this.lastTrimTime >= DbCookieStore.TRIM_TIME_SPAN) {
                    long unused = DbCookieStore.this.lastTrimTime = current;
                    try {
                        DbCookieStore.this.db.delete(CookieEntity.class, WhereBuilder.b("expiry", "<", Long.valueOf(System.currentTimeMillis())).and("expiry", "!=", -1L));
                    } catch (Throwable ex) {
                        LogUtil.e(ex.getMessage(), ex);
                    }
                    try {
                        int count = (int) DbCookieStore.this.db.selector(CookieEntity.class).count();
                        if (count > 5010 && (rmList = DbCookieStore.this.db.selector(CookieEntity.class).where("expiry", "!=", -1L).orderBy("expiry").limit(count - 5000).findAll()) != null) {
                            DbCookieStore.this.db.delete((Object) rmList);
                        }
                    } catch (Throwable ex2) {
                        LogUtil.e(ex2.getMessage(), ex2);
                    }
                }
            }
        });
    }

    private URI getEffectiveURI(URI uri) {
        try {
            return new URI("http", uri.getHost(), uri.getPath(), (String) null, (String) null);
        } catch (Throwable ex) {
            LogUtil.w(ex.getMessage(), ex);
            return uri;
        }
    }
}

package org.xutils.http.request;

import android.text.TextUtils;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import kotlin.jvm.internal.LongCompanionObject;
import org.xutils.cache.DiskCacheEntity;
import org.xutils.cache.LruDiskCache;
import org.xutils.common.util.IOUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;

public class ResRequest extends UriRequest {
    private static long lastModifiedTime = 0;
    protected long contentLength = 0;
    protected InputStream inputStream;

    public ResRequest(RequestParams params, Type loadType) throws Throwable {
        super(params, loadType);
    }

    public void sendRequest() throws Throwable {
    }

    public boolean isLoading() {
        return true;
    }

    public String getCacheKey() {
        return this.queryUrl;
    }

    public Object loadResult() throws Throwable {
        return this.loader.load(this);
    }

    public Object loadResultFromCache() throws Throwable {
        Date lastModifiedDate;
        DiskCacheEntity cacheEntity = LruDiskCache.getDiskCache(this.params.getCacheDirName()).setMaxSize(this.params.getCacheSize()).get(getCacheKey());
        if (cacheEntity == null || (lastModifiedDate = cacheEntity.getLastModify()) == null || lastModifiedDate.getTime() < getLastModified()) {
            return null;
        }
        return this.loader.loadFromCache(cacheEntity);
    }

    public void clearCacheHeader() {
    }

    private int getResId() {
        int resId = 0;
        String resIdStr = this.queryUrl.substring("res:".length()).replace("/", "");
        if (TextUtils.isDigitsOnly(resIdStr)) {
            resId = Integer.parseInt(resIdStr);
        }
        if (resId > 0) {
            return resId;
        }
        throw new IllegalArgumentException("resId not found in url:" + this.queryUrl);
    }

    public InputStream getInputStream() throws IOException {
        if (this.inputStream == null) {
            this.inputStream = this.params.getContext().getResources().openRawResource(getResId());
            this.contentLength = (long) this.inputStream.available();
        }
        return this.inputStream;
    }

    public void close() throws IOException {
        IOUtil.closeQuietly((Closeable) this.inputStream);
        this.inputStream = null;
    }

    public long getContentLength() {
        try {
            getInputStream();
            return this.contentLength;
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
            return -1;
        }
    }

    public int getResponseCode() throws IOException {
        return getInputStream() != null ? 200 : 404;
    }

    public String getResponseMessage() throws IOException {
        return null;
    }

    public long getExpiration() {
        return LongCompanionObject.MAX_VALUE;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003c, code lost:
        if (lastModifiedTime != 0) goto L_0x0052;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003e, code lost:
        lastModifiedTime = java.lang.System.currentTimeMillis();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0029, code lost:
        if (lastModifiedTime == 0) goto L_0x003e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getLastModified() {
        /*
            r7 = this;
            long r0 = lastModifiedTime
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0052
            org.xutils.http.RequestParams r0 = r7.params     // Catch:{ Throwable -> 0x002e }
            android.content.Context r0 = r0.getContext()     // Catch:{ Throwable -> 0x002e }
            android.content.pm.ApplicationInfo r1 = r0.getApplicationInfo()     // Catch:{ Throwable -> 0x002e }
            java.io.File r4 = new java.io.File     // Catch:{ Throwable -> 0x002e }
            java.lang.String r5 = r1.sourceDir     // Catch:{ Throwable -> 0x002e }
            r4.<init>(r5)     // Catch:{ Throwable -> 0x002e }
            boolean r5 = r4.exists()     // Catch:{ Throwable -> 0x002e }
            if (r5 == 0) goto L_0x0025
            long r5 = r4.lastModified()     // Catch:{ Throwable -> 0x002e }
            lastModifiedTime = r5     // Catch:{ Throwable -> 0x002e }
        L_0x0025:
            long r0 = lastModifiedTime
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0052
            goto L_0x003e
        L_0x002c:
            r0 = move-exception
            goto L_0x0045
        L_0x002e:
            r0 = move-exception
            java.lang.String r1 = r0.getMessage()     // Catch:{ all -> 0x002c }
            org.xutils.common.util.LogUtil.w(r1, r0)     // Catch:{ all -> 0x002c }
            lastModifiedTime = r2     // Catch:{ all -> 0x002c }
            long r0 = lastModifiedTime
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0052
        L_0x003e:
            long r0 = java.lang.System.currentTimeMillis()
            lastModifiedTime = r0
            goto L_0x0052
        L_0x0045:
            long r4 = lastModifiedTime
            int r1 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r1 != 0) goto L_0x0051
            long r1 = java.lang.System.currentTimeMillis()
            lastModifiedTime = r1
        L_0x0051:
            throw r0
        L_0x0052:
            long r0 = lastModifiedTime
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xutils.http.request.ResRequest.getLastModified():long");
    }

    public String getETag() {
        return null;
    }

    public String getResponseHeader(String name) {
        return null;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return null;
    }

    public long getHeaderFieldDate(String name, long defaultValue) {
        return defaultValue;
    }
}

package org.xutils.http.loader;

import android.text.TextUtils;
import java.util.Date;
import org.xutils.cache.DiskCacheEntity;
import org.xutils.cache.LruDiskCache;
import org.xutils.http.ProgressHandler;
import org.xutils.http.RequestParams;
import org.xutils.http.request.UriRequest;

public abstract class Loader<T> {
    protected ProgressHandler progressHandler;

    public abstract T load(UriRequest uriRequest) throws Throwable;

    public abstract T loadFromCache(DiskCacheEntity diskCacheEntity) throws Throwable;

    public abstract Loader<T> newInstance();

    public abstract void save2Cache(UriRequest uriRequest);

    public void setParams(RequestParams params) {
    }

    public void setProgressHandler(ProgressHandler callbackHandler) {
        this.progressHandler = callbackHandler;
    }

    /* access modifiers changed from: protected */
    public void saveStringCache(UriRequest request, String resultStr) {
        saveCacheInternal(request, resultStr, (byte[]) null);
    }

    /* access modifiers changed from: protected */
    public void saveByteArrayCache(UriRequest request, byte[] resultData) {
        saveCacheInternal(request, (String) null, resultData);
    }

    private void saveCacheInternal(UriRequest request, String resultStr, byte[] resultData) {
        if (!TextUtils.isEmpty(resultStr) || (resultData != null && resultData.length > 0)) {
            DiskCacheEntity entity = new DiskCacheEntity();
            entity.setKey(request.getCacheKey());
            entity.setLastAccess(System.currentTimeMillis());
            entity.setEtag(request.getETag());
            entity.setExpires(request.getExpiration());
            entity.setLastModify(new Date(request.getLastModified()));
            entity.setTextContent(resultStr);
            entity.setBytesContent(resultData);
            LruDiskCache.getDiskCache(request.getParams().getCacheDirName()).put(entity);
        }
    }
}

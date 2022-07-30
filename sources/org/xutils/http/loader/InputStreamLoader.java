package org.xutils.http.loader;

import java.io.InputStream;
import org.xutils.cache.DiskCacheEntity;
import org.xutils.http.request.UriRequest;

class InputStreamLoader extends Loader<InputStream> {
    InputStreamLoader() {
    }

    public Loader<InputStream> newInstance() {
        return new InputStreamLoader();
    }

    public InputStream load(UriRequest request) throws Throwable {
        request.sendRequest();
        return request.getInputStream();
    }

    public InputStream loadFromCache(DiskCacheEntity cacheEntity) throws Throwable {
        return null;
    }

    public void save2Cache(UriRequest request) {
    }
}

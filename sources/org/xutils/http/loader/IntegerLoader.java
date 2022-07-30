package org.xutils.http.loader;

import org.xutils.cache.DiskCacheEntity;
import org.xutils.http.request.UriRequest;

class IntegerLoader extends Loader<Integer> {
    IntegerLoader() {
    }

    public Loader<Integer> newInstance() {
        return new IntegerLoader();
    }

    public Integer load(UriRequest request) throws Throwable {
        request.sendRequest();
        return Integer.valueOf(request.getResponseCode());
    }

    public Integer loadFromCache(DiskCacheEntity cacheEntity) throws Throwable {
        return null;
    }

    public void save2Cache(UriRequest request) {
    }
}

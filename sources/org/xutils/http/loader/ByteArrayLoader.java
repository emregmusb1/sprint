package org.xutils.http.loader;

import org.xutils.cache.DiskCacheEntity;
import org.xutils.common.util.IOUtil;
import org.xutils.http.request.UriRequest;

class ByteArrayLoader extends Loader<byte[]> {
    private byte[] resultData;

    ByteArrayLoader() {
    }

    public Loader<byte[]> newInstance() {
        return new ByteArrayLoader();
    }

    public byte[] load(UriRequest request) throws Throwable {
        request.sendRequest();
        this.resultData = IOUtil.readBytes(request.getInputStream());
        return this.resultData;
    }

    public byte[] loadFromCache(DiskCacheEntity cacheEntity) throws Throwable {
        byte[] data;
        if (cacheEntity == null || (data = cacheEntity.getBytesContent()) == null || data.length <= 0) {
            return null;
        }
        return data;
    }

    public void save2Cache(UriRequest request) {
        saveByteArrayCache(request, this.resultData);
    }
}

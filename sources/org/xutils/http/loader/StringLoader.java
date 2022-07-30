package org.xutils.http.loader;

import android.text.TextUtils;
import org.apache.commons.compress.utils.CharsetNames;
import org.xutils.cache.DiskCacheEntity;
import org.xutils.common.util.IOUtil;
import org.xutils.http.RequestParams;
import org.xutils.http.request.UriRequest;

class StringLoader extends Loader<String> {
    private String charset = CharsetNames.UTF_8;
    private String resultStr = null;

    StringLoader() {
    }

    public Loader<String> newInstance() {
        return new StringLoader();
    }

    public void setParams(RequestParams params) {
        if (params != null) {
            String charset2 = params.getCharset();
            if (!TextUtils.isEmpty(charset2)) {
                this.charset = charset2;
            }
        }
    }

    public String load(UriRequest request) throws Throwable {
        request.sendRequest();
        this.resultStr = IOUtil.readStr(request.getInputStream(), this.charset);
        return this.resultStr;
    }

    public String loadFromCache(DiskCacheEntity cacheEntity) throws Throwable {
        if (cacheEntity != null) {
            return cacheEntity.getTextContent();
        }
        return null;
    }

    public void save2Cache(UriRequest request) {
        saveStringCache(request, this.resultStr);
    }
}

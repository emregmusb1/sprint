package org.xutils.http.loader;

import android.text.TextUtils;
import org.apache.commons.compress.utils.CharsetNames;
import org.json.JSONObject;
import org.xutils.cache.DiskCacheEntity;
import org.xutils.common.util.IOUtil;
import org.xutils.http.RequestParams;
import org.xutils.http.request.UriRequest;

class JSONObjectLoader extends Loader<JSONObject> {
    private String charset = CharsetNames.UTF_8;
    private String resultStr = null;

    JSONObjectLoader() {
    }

    public Loader<JSONObject> newInstance() {
        return new JSONObjectLoader();
    }

    public void setParams(RequestParams params) {
        if (params != null) {
            String charset2 = params.getCharset();
            if (!TextUtils.isEmpty(charset2)) {
                this.charset = charset2;
            }
        }
    }

    public JSONObject load(UriRequest request) throws Throwable {
        request.sendRequest();
        this.resultStr = IOUtil.readStr(request.getInputStream(), this.charset);
        return new JSONObject(this.resultStr);
    }

    public JSONObject loadFromCache(DiskCacheEntity cacheEntity) throws Throwable {
        if (cacheEntity == null) {
            return null;
        }
        String text = cacheEntity.getTextContent();
        if (!TextUtils.isEmpty(text)) {
            return new JSONObject(text);
        }
        return null;
    }

    public void save2Cache(UriRequest request) {
        saveStringCache(request, this.resultStr);
    }
}

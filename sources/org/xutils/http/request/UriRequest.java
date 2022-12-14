package org.xutils.http.request;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.xutils.common.util.LogUtil;
import org.xutils.http.ProgressHandler;
import org.xutils.http.RequestParams;
import org.xutils.http.app.RequestInterceptListener;
import org.xutils.http.app.ResponseParser;
import org.xutils.http.loader.Loader;
import org.xutils.http.loader.LoaderFactory;
import org.xutils.x;

public abstract class UriRequest implements Closeable {
    protected final Loader<?> loader;
    protected final RequestParams params;
    protected ProgressHandler progressHandler = null;
    protected final String queryUrl;
    protected RequestInterceptListener requestInterceptListener = null;
    protected ResponseParser responseParser = null;

    public abstract void clearCacheHeader();

    public abstract void close() throws IOException;

    public abstract String getCacheKey();

    public abstract long getContentLength();

    public abstract String getETag();

    public abstract long getExpiration();

    public abstract long getHeaderFieldDate(String str, long j);

    public abstract InputStream getInputStream() throws IOException;

    public abstract long getLastModified();

    public abstract int getResponseCode() throws IOException;

    public abstract String getResponseHeader(String str);

    public abstract Map<String, List<String>> getResponseHeaders();

    public abstract String getResponseMessage() throws IOException;

    public abstract boolean isLoading();

    public abstract Object loadResultFromCache() throws Throwable;

    public abstract void sendRequest() throws Throwable;

    public UriRequest(RequestParams params2, Type loadType) throws Throwable {
        this.params = params2;
        this.queryUrl = buildQueryUrl(params2);
        this.loader = LoaderFactory.getLoader(loadType);
        this.loader.setParams(params2);
    }

    /* access modifiers changed from: protected */
    public String buildQueryUrl(RequestParams params2) throws IOException {
        return params2.getUri();
    }

    public void setProgressHandler(ProgressHandler progressHandler2) {
        this.progressHandler = progressHandler2;
        this.loader.setProgressHandler(progressHandler2);
    }

    public void setResponseParser(ResponseParser responseParser2) {
        this.responseParser = responseParser2;
    }

    public void setRequestInterceptListener(RequestInterceptListener requestInterceptListener2) {
        this.requestInterceptListener = requestInterceptListener2;
    }

    public RequestParams getParams() {
        return this.params;
    }

    public String getRequestUri() {
        return this.queryUrl;
    }

    public Object loadResult() throws Throwable {
        return this.loader.load(this);
    }

    public void save2Cache() {
        x.task().run(new Runnable() {
            public void run() {
                try {
                    UriRequest.this.loader.save2Cache(UriRequest.this);
                } catch (Throwable ex) {
                    LogUtil.e(ex.getMessage(), ex);
                }
            }
        });
    }

    public String toString() {
        return getRequestUri();
    }
}

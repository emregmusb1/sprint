package org.xutils.http.request;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import kotlin.jvm.internal.LongCompanionObject;
import org.xutils.cache.DiskCacheEntity;
import org.xutils.cache.LruDiskCache;
import org.xutils.common.util.IOUtil;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.http.BaseParams;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.http.body.ProgressBody;
import org.xutils.http.body.RequestBody;
import org.xutils.http.cookie.DbCookieStore;

public class HttpRequest extends UriRequest {
    private static final CookieManager COOKIE_MANAGER = new CookieManager(DbCookieStore.INSTANCE, CookiePolicy.ACCEPT_ALL);
    private String cacheKey = null;
    private HttpURLConnection connection = null;
    private InputStream inputStream = null;
    private boolean isLoading = false;
    private int responseCode = 0;

    public HttpRequest(RequestParams params, Type loadType) throws Throwable {
        super(params, loadType);
    }

    /* access modifiers changed from: protected */
    public String buildQueryUrl(RequestParams params) throws IOException {
        String uri = params.getUri();
        StringBuilder queryBuilder = new StringBuilder(uri);
        if (!uri.contains("?")) {
            queryBuilder.append("?");
        } else if (!uri.endsWith("?")) {
            queryBuilder.append("&");
        }
        List<KeyValue> queryParams = params.getQueryStringParams();
        if (queryParams != null) {
            for (KeyValue kv : queryParams) {
                String name = kv.key;
                String value = kv.getValueStrOrNull();
                if (!TextUtils.isEmpty(name) && value != null) {
                    queryBuilder.append(URLEncoder.encode(name, params.getCharset()).replaceAll("\\+", "%20"));
                    queryBuilder.append("=");
                    queryBuilder.append(URLEncoder.encode(value, params.getCharset()).replaceAll("\\+", "%20"));
                    queryBuilder.append("&");
                }
            }
        }
        if (queryBuilder.charAt(queryBuilder.length() - 1) == '&') {
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        }
        if (queryBuilder.charAt(queryBuilder.length() - 1) == '?') {
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        }
        return queryBuilder.toString();
    }

    public String getRequestUri() {
        URL url;
        String result = this.queryUrl;
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection == null || (url = httpURLConnection.getURL()) == null) {
            return result;
        }
        return url.toString();
    }

    @TargetApi(19)
    public void sendRequest() throws Throwable {
        RequestBody body;
        this.isLoading = false;
        this.responseCode = 0;
        URL url = new URL(this.queryUrl);
        Proxy proxy = this.params.getProxy();
        if (proxy != null) {
            this.connection = (HttpURLConnection) url.openConnection(proxy);
        } else {
            this.connection = (HttpURLConnection) url.openConnection();
        }
        if (Build.VERSION.SDK_INT < 19) {
            this.connection.setRequestProperty("Connection", "close");
        }
        this.connection.setReadTimeout(this.params.getReadTimeout());
        this.connection.setConnectTimeout(this.params.getConnectTimeout());
        this.connection.setInstanceFollowRedirects(this.params.getRedirectHandler() == null);
        if (this.connection instanceof HttpsURLConnection) {
            SSLSocketFactory sslSocketFactory = this.params.getSslSocketFactory();
            if (sslSocketFactory != null) {
                ((HttpsURLConnection) this.connection).setSSLSocketFactory(sslSocketFactory);
            }
            HostnameVerifier hostnameVerifier = this.params.getHostnameVerifier();
            if (hostnameVerifier != null) {
                ((HttpsURLConnection) this.connection).setHostnameVerifier(hostnameVerifier);
            }
        }
        if (this.params.isUseCookie()) {
            try {
                List<String> cookies = COOKIE_MANAGER.get(url.toURI(), new HashMap(0)).get("Cookie");
                if (cookies != null) {
                    this.connection.setRequestProperty("Cookie", TextUtils.join(";", cookies));
                }
            } catch (Throwable ex) {
                LogUtil.e(ex.getMessage(), ex);
            }
        }
        List<BaseParams.Header> headers = this.params.getHeaders();
        if (headers != null) {
            for (BaseParams.Header header : headers) {
                String name = header.key;
                String value = header.getValueStrOrNull();
                if (!TextUtils.isEmpty(name)) {
                    if (header.setHeader) {
                        this.connection.setRequestProperty(name, value);
                    } else {
                        this.connection.addRequestProperty(name, value);
                    }
                }
            }
        }
        if (this.responseParser != null) {
            this.responseParser.beforeRequest(this);
        }
        if (this.requestInterceptListener != null) {
            this.requestInterceptListener.beforeRequest(this);
        }
        HttpMethod method = this.params.getMethod();
        try {
            this.connection.setRequestMethod(method.toString());
        } catch (ProtocolException ex2) {
            Field methodField = HttpURLConnection.class.getDeclaredField("method");
            methodField.setAccessible(true);
            methodField.set(this.connection, method.toString());
        }
        if (HttpMethod.permitsRequestBody(method) && (body = this.params.getRequestBody()) != null) {
            if (body instanceof ProgressBody) {
                ((ProgressBody) body).setProgressHandler(this.progressHandler);
            }
            String contentType = body.getContentType();
            if (!TextUtils.isEmpty(contentType)) {
                this.connection.setRequestProperty("Content-Type", contentType);
            }
            boolean isChunkedMode = false;
            long contentLength = body.getContentLength();
            if (contentLength < 0) {
                this.connection.setChunkedStreamingMode(262144);
                isChunkedMode = true;
            } else if (contentLength < 2147483647L) {
                this.connection.setFixedLengthStreamingMode((int) contentLength);
            } else if (Build.VERSION.SDK_INT >= 19) {
                this.connection.setFixedLengthStreamingMode(contentLength);
            } else {
                this.connection.setChunkedStreamingMode(262144);
                isChunkedMode = true;
            }
            if (isChunkedMode) {
                this.connection.setRequestProperty("Transfer-Encoding", "chunked");
            } else {
                this.connection.setRequestProperty("Content-Length", String.valueOf(contentLength));
            }
            this.connection.setDoOutput(true);
            body.writeTo(this.connection.getOutputStream());
        }
        if (this.params.isUseCookie()) {
            try {
                Map<String, List<String>> headers2 = this.connection.getHeaderFields();
                if (headers2 != null) {
                    COOKIE_MANAGER.put(url.toURI(), headers2);
                }
            } catch (Throwable ex3) {
                LogUtil.e(ex3.getMessage(), ex3);
            }
        }
        this.responseCode = this.connection.getResponseCode();
        if (this.responseParser != null) {
            this.responseParser.afterRequest(this);
        }
        if (this.requestInterceptListener != null) {
            this.requestInterceptListener.afterRequest(this);
        }
        int i = this.responseCode;
        if (i == 204 || i == 205) {
            throw new HttpException(this.responseCode, getResponseMessage());
        } else if (i < 300) {
            this.isLoading = true;
            return;
        } else {
            HttpException httpException = new HttpException(i, getResponseMessage());
            try {
                httpException.setResult(IOUtil.readStr(getInputStream(), this.params.getCharset()));
            } catch (Throwable ex4) {
                LogUtil.w(ex4.getMessage(), ex4);
            }
            LogUtil.e(httpException.toString() + ", url: " + this.queryUrl);
            throw httpException;
        }
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    public String getCacheKey() {
        if (this.cacheKey == null) {
            this.cacheKey = this.params.getCacheKey();
            if (TextUtils.isEmpty(this.cacheKey)) {
                this.cacheKey = this.params.toString();
            }
        }
        return this.cacheKey;
    }

    public Object loadResult() throws Throwable {
        this.isLoading = true;
        return super.loadResult();
    }

    public Object loadResultFromCache() throws Throwable {
        this.isLoading = true;
        DiskCacheEntity cacheEntity = LruDiskCache.getDiskCache(this.params.getCacheDirName()).setMaxSize(this.params.getCacheSize()).get(getCacheKey());
        if (cacheEntity == null) {
            return null;
        }
        if (HttpMethod.permitsCache(this.params.getMethod())) {
            Date lastModified = cacheEntity.getLastModify();
            if (lastModified.getTime() > 0) {
                this.params.setHeader("If-Modified-Since", toGMTString(lastModified));
            }
            String eTag = cacheEntity.getEtag();
            if (!TextUtils.isEmpty(eTag)) {
                this.params.setHeader("If-None-Match", eTag);
            }
        }
        return this.loader.loadFromCache(cacheEntity);
    }

    public void clearCacheHeader() {
        this.params.setHeader("If-Modified-Since", (String) null);
        this.params.setHeader("If-None-Match", (String) null);
    }

    public InputStream getInputStream() throws IOException {
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection != null && this.inputStream == null) {
            this.inputStream = httpURLConnection.getResponseCode() >= 400 ? this.connection.getErrorStream() : this.connection.getInputStream();
        }
        return this.inputStream;
    }

    public void close() throws IOException {
        InputStream inputStream2 = this.inputStream;
        if (inputStream2 != null) {
            IOUtil.closeQuietly((Closeable) inputStream2);
            this.inputStream = null;
        }
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
    }

    public long getContentLength() {
        long result = -1;
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection != null) {
            try {
                String value = httpURLConnection.getHeaderField("content-length");
                if (value != null) {
                    result = Long.parseLong(value);
                }
            } catch (Throwable ex) {
                LogUtil.e(ex.getMessage(), ex);
            }
        }
        if (result >= 1) {
            return result;
        }
        try {
            return (long) getInputStream().available();
        } catch (Throwable th) {
            return result;
        }
    }

    public int getResponseCode() throws IOException {
        if (this.connection != null) {
            return this.responseCode;
        }
        if (getInputStream() != null) {
            return 200;
        }
        return 404;
    }

    public String getResponseMessage() throws IOException {
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection != null) {
            return URLDecoder.decode(httpURLConnection.getResponseMessage(), this.params.getCharset());
        }
        return null;
    }

    public long getExpiration() {
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection == null) {
            return -1;
        }
        long expiration = -1;
        String cacheControl = httpURLConnection.getHeaderField("Cache-Control");
        if (!TextUtils.isEmpty(cacheControl)) {
            StringTokenizer tok = new StringTokenizer(cacheControl, ",");
            while (true) {
                if (!tok.hasMoreTokens()) {
                    break;
                }
                String token = tok.nextToken().trim().toLowerCase();
                if (token.startsWith("max-age")) {
                    int eqIdx = token.indexOf(61);
                    if (eqIdx > 0) {
                        try {
                            long seconds = Long.parseLong(token.substring(eqIdx + 1).trim());
                            if (seconds > 0) {
                                expiration = System.currentTimeMillis() + (1000 * seconds);
                            }
                        } catch (Throwable ex) {
                            LogUtil.e(ex.getMessage(), ex);
                        }
                    }
                }
            }
        }
        if (expiration <= 0) {
            expiration = this.connection.getExpiration();
        }
        if (expiration <= 0 && this.params.getCacheMaxAge() > 0) {
            expiration = System.currentTimeMillis() + this.params.getCacheMaxAge();
        }
        if (expiration <= 0) {
            return LongCompanionObject.MAX_VALUE;
        }
        return expiration;
    }

    public long getLastModified() {
        return getHeaderFieldDate("Last-Modified", System.currentTimeMillis());
    }

    public String getETag() {
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection == null) {
            return null;
        }
        return httpURLConnection.getHeaderField("ETag");
    }

    public String getResponseHeader(String name) {
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection == null) {
            return null;
        }
        return httpURLConnection.getHeaderField(name);
    }

    public Map<String, List<String>> getResponseHeaders() {
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection == null) {
            return null;
        }
        return httpURLConnection.getHeaderFields();
    }

    public long getHeaderFieldDate(String name, long defaultValue) {
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection == null) {
            return defaultValue;
        }
        return httpURLConnection.getHeaderFieldDate(name, defaultValue);
    }

    private static String toGMTString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM y HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }
}

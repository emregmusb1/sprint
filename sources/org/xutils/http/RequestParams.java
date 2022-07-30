package org.xutils.http;

import android.content.Context;
import android.text.TextUtils;
import java.net.Proxy;
import java.util.concurrent.Executor;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import org.xutils.common.task.Priority;
import org.xutils.http.RequestParamsHelper;
import org.xutils.http.annotation.HttpRequest;
import org.xutils.http.app.DefaultParamsBuilder;
import org.xutils.http.app.DefaultRedirectHandler;
import org.xutils.http.app.HttpRetryHandler;
import org.xutils.http.app.ParamsBuilder;
import org.xutils.http.app.RedirectHandler;
import org.xutils.http.app.RequestTracker;
import org.xutils.x;

public class RequestParams extends BaseParams {
    private static final DefaultRedirectHandler DEFAULT_REDIRECT_HANDLER = new DefaultRedirectHandler();
    public static final int MAX_FILE_LOAD_WORKER = 10;
    private boolean autoRename;
    private boolean autoResume;
    private String buildCacheKey;
    private String buildUri;
    private ParamsBuilder builder;
    private String cacheDirName;
    private final String[] cacheKeys;
    private long cacheMaxAge;
    private long cacheSize;
    private boolean cancelFast;
    private int connectTimeout;
    private Context context;
    private Executor executor;
    private HostnameVerifier hostnameVerifier;
    private HttpRequest httpRequest;
    private HttpRetryHandler httpRetryHandler;
    private boolean invokedGetHttpRequest;
    private int loadingUpdateMaxTimeSpan;
    private int maxRetryCount;
    private Priority priority;
    private Proxy proxy;
    private int readTimeout;
    private RedirectHandler redirectHandler;
    private RequestTracker requestTracker;
    private String saveFilePath;
    private final String[] signs;
    private SSLSocketFactory sslSocketFactory;
    private String uri;
    private boolean useCookie;

    public RequestParams() {
        this((String) null, (ParamsBuilder) null, (String[]) null, (String[]) null);
    }

    public RequestParams(String uri2) {
        this(uri2, (ParamsBuilder) null, (String[]) null, (String[]) null);
    }

    public RequestParams(String uri2, ParamsBuilder builder2, String[] signs2, String[] cacheKeys2) {
        this.useCookie = true;
        this.priority = Priority.DEFAULT;
        this.connectTimeout = 15000;
        this.readTimeout = 15000;
        this.autoResume = true;
        this.autoRename = false;
        this.maxRetryCount = 2;
        this.cancelFast = false;
        this.loadingUpdateMaxTimeSpan = 300;
        this.redirectHandler = DEFAULT_REDIRECT_HANDLER;
        this.invokedGetHttpRequest = false;
        if (uri2 != null && builder2 == null) {
            builder2 = new DefaultParamsBuilder();
        }
        this.uri = uri2;
        this.signs = signs2;
        this.cacheKeys = cacheKeys2;
        this.builder = builder2;
        this.context = x.app();
    }

    /* access modifiers changed from: package-private */
    public void init() throws Throwable {
        if (TextUtils.isEmpty(this.buildUri)) {
            if (!TextUtils.isEmpty(this.uri) || getHttpRequest() != null) {
                initEntityParams();
                this.buildUri = this.uri;
                HttpRequest httpRequest2 = getHttpRequest();
                if (httpRequest2 != null) {
                    this.builder = (ParamsBuilder) httpRequest2.builder().newInstance();
                    this.buildUri = this.builder.buildUri(this, httpRequest2);
                    this.builder.buildParams(this);
                    this.builder.buildSign(this, httpRequest2.signs());
                    if (this.sslSocketFactory == null) {
                        this.sslSocketFactory = this.builder.getSSLSocketFactory();
                        return;
                    }
                    return;
                }
                ParamsBuilder paramsBuilder = this.builder;
                if (paramsBuilder != null) {
                    paramsBuilder.buildParams(this);
                    this.builder.buildSign(this, this.signs);
                    if (this.sslSocketFactory == null) {
                        this.sslSocketFactory = this.builder.getSSLSocketFactory();
                        return;
                    }
                    return;
                }
                return;
            }
            throw new IllegalStateException("uri is empty && @HttpRequest == null");
        }
    }

    public String getUri() {
        return TextUtils.isEmpty(this.buildUri) ? this.uri : this.buildUri;
    }

    public void setUri(String uri2) {
        if (TextUtils.isEmpty(this.buildUri)) {
            this.uri = uri2;
        } else {
            this.buildUri = uri2;
        }
    }

    public String getCacheKey() {
        if (TextUtils.isEmpty(this.buildCacheKey) && this.builder != null) {
            HttpRequest httpRequest2 = getHttpRequest();
            if (httpRequest2 != null) {
                this.buildCacheKey = this.builder.buildCacheKey(this, httpRequest2.cacheKeys());
            } else {
                this.buildCacheKey = this.builder.buildCacheKey(this, this.cacheKeys);
            }
        }
        return this.buildCacheKey;
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory2) {
        this.sslSocketFactory = sslSocketFactory2;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return this.sslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier2) {
        this.hostnameVerifier = hostnameVerifier2;
    }

    public boolean isUseCookie() {
        return this.useCookie;
    }

    public void setUseCookie(boolean useCookie2) {
        this.useCookie = useCookie2;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context2) {
        this.context = context2;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public void setProxy(Proxy proxy2) {
        this.proxy = proxy2;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public void setPriority(Priority priority2) {
        this.priority = priority2;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout2) {
        if (connectTimeout2 > 0) {
            this.connectTimeout = connectTimeout2;
        }
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(int readTimeout2) {
        if (readTimeout2 > 0) {
            this.readTimeout = readTimeout2;
        }
    }

    public String getCacheDirName() {
        return this.cacheDirName;
    }

    public void setCacheDirName(String cacheDirName2) {
        this.cacheDirName = cacheDirName2;
    }

    public long getCacheSize() {
        return this.cacheSize;
    }

    public void setCacheSize(long cacheSize2) {
        this.cacheSize = cacheSize2;
    }

    public long getCacheMaxAge() {
        return this.cacheMaxAge;
    }

    public void setCacheMaxAge(long cacheMaxAge2) {
        this.cacheMaxAge = cacheMaxAge2;
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public void setExecutor(Executor executor2) {
        this.executor = executor2;
    }

    public boolean isAutoResume() {
        return this.autoResume;
    }

    public void setAutoResume(boolean autoResume2) {
        this.autoResume = autoResume2;
    }

    public boolean isAutoRename() {
        return this.autoRename;
    }

    public void setAutoRename(boolean autoRename2) {
        this.autoRename = autoRename2;
    }

    public String getSaveFilePath() {
        return this.saveFilePath;
    }

    public void setSaveFilePath(String saveFilePath2) {
        this.saveFilePath = saveFilePath2;
    }

    public int getMaxRetryCount() {
        return this.maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount2) {
        this.maxRetryCount = maxRetryCount2;
    }

    public boolean isCancelFast() {
        return this.cancelFast;
    }

    public void setCancelFast(boolean cancelFast2) {
        this.cancelFast = cancelFast2;
    }

    public int getLoadingUpdateMaxTimeSpan() {
        return this.loadingUpdateMaxTimeSpan;
    }

    public void setLoadingUpdateMaxTimeSpan(int loadingUpdateMaxTimeSpan2) {
        this.loadingUpdateMaxTimeSpan = loadingUpdateMaxTimeSpan2;
    }

    public HttpRetryHandler getHttpRetryHandler() {
        return this.httpRetryHandler;
    }

    public void setHttpRetryHandler(HttpRetryHandler httpRetryHandler2) {
        this.httpRetryHandler = httpRetryHandler2;
    }

    public RedirectHandler getRedirectHandler() {
        return this.redirectHandler;
    }

    public void setRedirectHandler(RedirectHandler redirectHandler2) {
        this.redirectHandler = redirectHandler2;
    }

    public RequestTracker getRequestTracker() {
        return this.requestTracker;
    }

    public void setRequestTracker(RequestTracker requestTracker2) {
        this.requestTracker = requestTracker2;
    }

    private void initEntityParams() {
        RequestParamsHelper.parseKV(this, getClass(), new RequestParamsHelper.ParseKVListener() {
            public void onParseKV(String name, Object value) {
                RequestParams.this.addParameter(name, value);
            }
        });
    }

    private HttpRequest getHttpRequest() {
        if (this.httpRequest == null && !this.invokedGetHttpRequest) {
            this.invokedGetHttpRequest = true;
            Class<?> thisCls = getClass();
            if (thisCls != RequestParams.class) {
                this.httpRequest = (HttpRequest) thisCls.getAnnotation(HttpRequest.class);
            }
        }
        return this.httpRequest;
    }

    public String toString() {
        String url = getUri();
        String baseParamsStr = super.toString();
        if (TextUtils.isEmpty(url)) {
            return baseParamsStr;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        String str = "?";
        if (url.contains(str)) {
            str = "&";
        }
        sb.append(str);
        sb.append(baseParamsStr);
        return sb.toString();
    }
}

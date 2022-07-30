package org.xutils.http;

import android.text.TextUtils;
import java.io.Closeable;
import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import org.xutils.common.Callback;
import org.xutils.common.task.AbsTask;
import org.xutils.common.task.Priority;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.common.util.IOUtil;
import org.xutils.common.util.ParameterizedTypeUtil;
import org.xutils.ex.HttpException;
import org.xutils.ex.HttpRedirectException;
import org.xutils.http.app.RedirectHandler;
import org.xutils.http.app.RequestInterceptListener;
import org.xutils.http.app.RequestTracker;
import org.xutils.http.request.UriRequest;
import org.xutils.http.request.UriRequestFactory;
import org.xutils.x;

public class HttpTask<ResultType> extends AbsTask<ResultType> implements ProgressHandler {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final PriorityExecutor CACHE_EXECUTOR = new PriorityExecutor(5, true);
    private static final HashMap<String, WeakReference<HttpTask<?>>> DOWNLOAD_TASK = new HashMap<>(1);
    private static final int FLAG_CACHE = 2;
    private static final int FLAG_PROGRESS = 3;
    private static final int FLAG_REQUEST_CREATED = 1;
    private static final PriorityExecutor HTTP_EXECUTOR = new PriorityExecutor(5, true);
    /* access modifiers changed from: private */
    public static final AtomicInteger sCurrFileLoadCount = new AtomicInteger(0);
    private Callback.CacheCallback<ResultType> cacheCallback;
    private final Object cacheLock = new Object();
    private final Callback.CommonCallback<ResultType> callback;
    private final Executor executor;
    private volatile boolean hasException = false;
    private long lastUpdateTime;
    /* access modifiers changed from: private */
    public Type loadType;
    private long loadingUpdateMaxTimeSpan = 300;
    /* access modifiers changed from: private */
    public RequestParams params;
    private Callback.PrepareCallback prepareCallback;
    private Callback.ProgressCallback progressCallback;
    private Object rawResult = null;
    /* access modifiers changed from: private */
    public UriRequest request;
    /* access modifiers changed from: private */
    public RequestInterceptListener requestInterceptListener;
    private RequestTracker tracker;
    private volatile Boolean trustCache = null;

    public HttpTask(RequestParams params2, Callback.Cancelable cancelHandler, Callback.CommonCallback<ResultType> callback2) {
        super(cancelHandler);
        this.params = params2;
        this.callback = callback2;
        if (callback2 instanceof Callback.CacheCallback) {
            this.cacheCallback = (Callback.CacheCallback) callback2;
        }
        if (callback2 instanceof Callback.PrepareCallback) {
            this.prepareCallback = (Callback.PrepareCallback) callback2;
        }
        if (callback2 instanceof Callback.ProgressCallback) {
            this.progressCallback = (Callback.ProgressCallback) callback2;
        }
        if (callback2 instanceof RequestInterceptListener) {
            this.requestInterceptListener = (RequestInterceptListener) callback2;
        }
        RequestTracker customTracker = params2.getRequestTracker();
        if (customTracker == null) {
            if (callback2 instanceof RequestTracker) {
                customTracker = (RequestTracker) callback2;
            } else {
                customTracker = UriRequestFactory.getDefaultTracker();
            }
        }
        if (customTracker != null) {
            this.tracker = new RequestTrackerWrapper(customTracker);
        }
        if (params2.getExecutor() != null) {
            this.executor = params2.getExecutor();
        } else if (this.cacheCallback != null) {
            this.executor = CACHE_EXECUTOR;
        } else {
            this.executor = HTTP_EXECUTOR;
        }
    }

    private void resolveLoadType() {
        Class<?> callBackType = this.callback.getClass();
        Callback.CommonCallback<ResultType> commonCallback = this.callback;
        if (commonCallback instanceof Callback.TypedCallback) {
            this.loadType = ((Callback.TypedCallback) commonCallback).getLoadType();
        } else if (commonCallback instanceof Callback.PrepareCallback) {
            this.loadType = ParameterizedTypeUtil.getParameterizedType(callBackType, Callback.PrepareCallback.class, 0);
        } else {
            this.loadType = ParameterizedTypeUtil.getParameterizedType(callBackType, Callback.CommonCallback.class, 0);
        }
    }

    /* access modifiers changed from: private */
    public UriRequest createNewRequest() throws Throwable {
        this.params.init();
        UriRequest result = UriRequestFactory.getUriRequest(this.params, this.loadType);
        result.setProgressHandler(this);
        this.loadingUpdateMaxTimeSpan = (long) this.params.getLoadingUpdateMaxTimeSpan();
        update(1, result);
        return result;
    }

    private void checkDownloadTask() {
        if (File.class == this.loadType) {
            synchronized (DOWNLOAD_TASK) {
                String downloadTaskKey = this.params.getSaveFilePath();
                if (!TextUtils.isEmpty(downloadTaskKey)) {
                    WeakReference<HttpTask<?>> taskRef = DOWNLOAD_TASK.get(downloadTaskKey);
                    if (taskRef != null) {
                        HttpTask<?> task = (HttpTask) taskRef.get();
                        if (task != null) {
                            task.cancel();
                            task.closeRequestSync();
                        }
                        DOWNLOAD_TASK.remove(downloadTaskKey);
                    }
                    DOWNLOAD_TASK.put(downloadTaskKey, new WeakReference(this));
                }
                if (DOWNLOAD_TASK.size() > 10) {
                    Iterator<Map.Entry<String, WeakReference<HttpTask<?>>>> entryItr = DOWNLOAD_TASK.entrySet().iterator();
                    while (entryItr.hasNext()) {
                        WeakReference<HttpTask<?>> value = entryItr.next().getValue();
                        if (value == null || value.get() == null) {
                            entryItr.remove();
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x017c, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:?, code lost:
        org.xutils.common.util.LogUtil.e("Error while storing the http cache.", r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x01e2, code lost:
        r1 = true;
        org.xutils.common.util.LogUtil.w("Http Redirect:" + r11.params.getUri());
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x01e1 A[ExcHandler: HttpRedirectException (e org.xutils.ex.HttpRedirectException), PHI: r0 
      PHI: (r0v5 'result' ResultType) = (r0v3 'result' ResultType), (r0v3 'result' ResultType), (r0v3 'result' ResultType), (r0v3 'result' ResultType), (r0v3 'result' ResultType), (r0v7 'result' ResultType), (r0v7 'result' ResultType), (r0v7 'result' ResultType), (r0v3 'result' ResultType), (r0v9 'result' ResultType) binds: [B:73:0x0105, B:77:0x0110, B:116:0x0196, B:112:0x0192, B:81:0x013e, B:101:0x0176, B:105:0x017f, B:102:?, B:86:0x0148, B:89:0x0151] A[DONT_GENERATE, DONT_INLINE], Splitter:B:73:0x0105] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ResultType doBackground() throws java.lang.Throwable {
        /*
            r11 = this;
            boolean r0 = r11.isCancelled()
            if (r0 != 0) goto L_0x0218
            r0 = 0
            r11.resolveLoadType()
            org.xutils.http.request.UriRequest r1 = r11.createNewRequest()
            r11.request = r1
            r11.checkDownloadTask()
            r1 = 1
            r2 = 0
            r3 = 0
            org.xutils.http.RequestParams r4 = r11.params
            org.xutils.http.app.HttpRetryHandler r4 = r4.getHttpRetryHandler()
            if (r4 != 0) goto L_0x0024
            org.xutils.http.app.HttpRetryHandler r5 = new org.xutils.http.app.HttpRetryHandler
            r5.<init>()
            r4 = r5
        L_0x0024:
            org.xutils.http.RequestParams r5 = r11.params
            int r5 = r5.getMaxRetryCount()
            r4.setMaxRetryCount(r5)
            boolean r5 = r11.isCancelled()
            if (r5 != 0) goto L_0x0210
            r5 = 0
            org.xutils.common.Callback$CacheCallback<ResultType> r6 = r11.cacheCallback
            r7 = 0
            r8 = 1
            r9 = 0
            if (r6 == 0) goto L_0x00e1
            org.xutils.http.RequestParams r6 = r11.params
            org.xutils.http.HttpMethod r6 = r6.getMethod()
            boolean r6 = org.xutils.http.HttpMethod.permitsCache(r6)
            if (r6 == 0) goto L_0x00e1
            r11.clearRawResult()     // Catch:{ Throwable -> 0x006d }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x006d }
            r6.<init>()     // Catch:{ Throwable -> 0x006d }
            java.lang.String r10 = "load cache: "
            r6.append(r10)     // Catch:{ Throwable -> 0x006d }
            org.xutils.http.request.UriRequest r10 = r11.request     // Catch:{ Throwable -> 0x006d }
            java.lang.String r10 = r10.getRequestUri()     // Catch:{ Throwable -> 0x006d }
            r6.append(r10)     // Catch:{ Throwable -> 0x006d }
            java.lang.String r6 = r6.toString()     // Catch:{ Throwable -> 0x006d }
            org.xutils.common.util.LogUtil.d(r6)     // Catch:{ Throwable -> 0x006d }
            org.xutils.http.request.UriRequest r6 = r11.request     // Catch:{ Throwable -> 0x006d }
            java.lang.Object r6 = r6.loadResultFromCache()     // Catch:{ Throwable -> 0x006d }
            r11.rawResult = r6     // Catch:{ Throwable -> 0x006d }
            goto L_0x0073
        L_0x006d:
            r6 = move-exception
            java.lang.String r10 = "load disk cache error"
            org.xutils.common.util.LogUtil.w(r10, r6)
        L_0x0073:
            boolean r6 = r11.isCancelled()
            if (r6 != 0) goto L_0x00d6
            java.lang.Object r6 = r11.rawResult
            if (r6 == 0) goto L_0x00e1
            org.xutils.common.Callback$PrepareCallback r10 = r11.prepareCallback
            if (r10 == 0) goto L_0x0097
            java.lang.Object r6 = r10.prepare(r6)     // Catch:{ Throwable -> 0x008a }
            r5 = r6
        L_0x0086:
            r11.clearRawResult()
            goto L_0x0099
        L_0x008a:
            r6 = move-exception
            r5 = 0
            java.lang.String r10 = "prepare disk cache error"
            org.xutils.common.util.LogUtil.w(r10, r6)     // Catch:{ all -> 0x0092 }
            goto L_0x0086
        L_0x0092:
            r6 = move-exception
            r11.clearRawResult()
            throw r6
        L_0x0097:
            java.lang.Object r5 = r11.rawResult
        L_0x0099:
            boolean r6 = r11.isCancelled()
            if (r6 != 0) goto L_0x00ce
            if (r5 == 0) goto L_0x00e1
            r6 = 2
            java.lang.Object[] r10 = new java.lang.Object[r8]
            r10[r7] = r5
            r11.update(r6, r10)
            java.lang.Object r6 = r11.cacheLock
            monitor-enter(r6)
        L_0x00ac:
            java.lang.Boolean r10 = r11.trustCache     // Catch:{ all -> 0x00cb }
            if (r10 != 0) goto L_0x00c1
            java.lang.Object r10 = r11.cacheLock     // Catch:{ InterruptedException -> 0x00b8, Throwable -> 0x00b6 }
            r10.wait()     // Catch:{ InterruptedException -> 0x00b8, Throwable -> 0x00b6 }
            goto L_0x00b7
        L_0x00b6:
            r10 = move-exception
        L_0x00b7:
            goto L_0x00ac
        L_0x00b8:
            r7 = move-exception
            org.xutils.common.Callback$CancelledException r8 = new org.xutils.common.Callback$CancelledException     // Catch:{ all -> 0x00cb }
            java.lang.String r9 = "cancelled before request"
            r8.<init>(r9)     // Catch:{ all -> 0x00cb }
            throw r8     // Catch:{ all -> 0x00cb }
        L_0x00c1:
            monitor-exit(r6)     // Catch:{ all -> 0x00cb }
            java.lang.Boolean r6 = r11.trustCache
            boolean r6 = r6.booleanValue()
            if (r6 == 0) goto L_0x00e1
            return r9
        L_0x00cb:
            r7 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x00cb }
            throw r7
        L_0x00ce:
            org.xutils.common.Callback$CancelledException r6 = new org.xutils.common.Callback$CancelledException
            java.lang.String r7 = "cancelled before request"
            r6.<init>(r7)
            throw r6
        L_0x00d6:
            r11.clearRawResult()
            org.xutils.common.Callback$CancelledException r6 = new org.xutils.common.Callback$CancelledException
            java.lang.String r7 = "cancelled before request"
            r6.<init>(r7)
            throw r6
        L_0x00e1:
            java.lang.Boolean r6 = r11.trustCache
            if (r6 != 0) goto L_0x00eb
            java.lang.Boolean r6 = java.lang.Boolean.valueOf(r7)
            r11.trustCache = r6
        L_0x00eb:
            if (r5 != 0) goto L_0x00f2
            org.xutils.http.request.UriRequest r6 = r11.request
            r6.clearCacheHeader()
        L_0x00f2:
            org.xutils.common.Callback$CommonCallback<ResultType> r6 = r11.callback
            boolean r7 = r6 instanceof org.xutils.common.Callback.ProxyCacheCallback
            if (r7 == 0) goto L_0x0101
            org.xutils.common.Callback$ProxyCacheCallback r6 = (org.xutils.common.Callback.ProxyCacheCallback) r6
            boolean r6 = r6.onlyCache()
            if (r6 == 0) goto L_0x0101
            return r9
        L_0x0101:
            r1 = 1
        L_0x0102:
            if (r1 == 0) goto L_0x01ff
            r1 = 0
            boolean r6 = r11.isCancelled()     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            if (r6 != 0) goto L_0x01a9
            org.xutils.http.request.UriRequest r6 = r11.request     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            r6.close()     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            r11.clearRawResult()     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            r6.<init>()     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            java.lang.String r7 = "load: "
            r6.append(r7)     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            org.xutils.http.request.UriRequest r7 = r11.request     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            java.lang.String r7 = r7.getRequestUri()     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            r6.append(r7)     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            java.lang.String r6 = r6.toString()     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            org.xutils.common.util.LogUtil.d(r6)     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            org.xutils.http.HttpTask$RequestWorker r6 = new org.xutils.http.HttpTask$RequestWorker     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            r6.<init>()     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            r6.request()     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            java.lang.Throwable r7 = r6.ex     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            if (r7 != 0) goto L_0x0192
            java.lang.Object r7 = r6.result     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            r11.rawResult = r7     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            org.xutils.common.Callback$PrepareCallback r6 = r11.prepareCallback     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            if (r6 == 0) goto L_0x0163
            boolean r6 = r11.isCancelled()     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            if (r6 != 0) goto L_0x015b
            org.xutils.common.Callback$PrepareCallback r6 = r11.prepareCallback     // Catch:{ all -> 0x0155 }
            java.lang.Object r7 = r11.rawResult     // Catch:{ all -> 0x0155 }
            java.lang.Object r6 = r6.prepare(r7)     // Catch:{ all -> 0x0155 }
            r0 = r6
            r11.clearRawResult()     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            goto L_0x0166
        L_0x0155:
            r6 = move-exception
            r11.clearRawResult()     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            throw r6     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
        L_0x015b:
            org.xutils.common.Callback$CancelledException r6 = new org.xutils.common.Callback$CancelledException     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            java.lang.String r7 = "cancelled before request"
            r6.<init>(r7)     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            throw r6     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
        L_0x0163:
            java.lang.Object r6 = r11.rawResult     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            r0 = r6
        L_0x0166:
            org.xutils.common.Callback$CacheCallback<ResultType> r6 = r11.cacheCallback     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            if (r6 == 0) goto L_0x0182
            org.xutils.http.RequestParams r6 = r11.params     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            org.xutils.http.HttpMethod r6 = r6.getMethod()     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            boolean r6 = org.xutils.http.HttpMethod.permitsCache(r6)     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            if (r6 == 0) goto L_0x0182
            org.xutils.http.request.UriRequest r6 = r11.request     // Catch:{ Throwable -> 0x017c, HttpRedirectException -> 0x01e1 }
            r6.save2Cache()     // Catch:{ Throwable -> 0x017c, HttpRedirectException -> 0x01e1 }
            goto L_0x0182
        L_0x017c:
            r6 = move-exception
            java.lang.String r7 = "Error while storing the http cache."
            org.xutils.common.util.LogUtil.e(r7, r6)     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
        L_0x0182:
            boolean r6 = r11.isCancelled()     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            if (r6 != 0) goto L_0x018a
            goto L_0x01fd
        L_0x018a:
            org.xutils.common.Callback$CancelledException r6 = new org.xutils.common.Callback$CancelledException     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            java.lang.String r7 = "cancelled after request"
            r6.<init>(r7)     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            throw r6     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
        L_0x0192:
            java.lang.Throwable r7 = r6.ex     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
            throw r7     // Catch:{ Throwable -> 0x0195, HttpRedirectException -> 0x01e1 }
        L_0x0195:
            r6 = move-exception
            r11.clearRawResult()     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            boolean r7 = r11.isCancelled()     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            if (r7 == 0) goto L_0x01a7
            org.xutils.common.Callback$CancelledException r7 = new org.xutils.common.Callback$CancelledException     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            java.lang.String r10 = "cancelled during request"
            r7.<init>(r10)     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            throw r7     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
        L_0x01a7:
            throw r6     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
        L_0x01a9:
            org.xutils.common.Callback$CancelledException r6 = new org.xutils.common.Callback$CancelledException     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            java.lang.String r7 = "cancelled before request"
            r6.<init>(r7)     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
            throw r6     // Catch:{ HttpRedirectException -> 0x01e1, Throwable -> 0x01b1 }
        L_0x01b1:
            r6 = move-exception
            org.xutils.http.request.UriRequest r7 = r11.request
            int r7 = r7.getResponseCode()
            r10 = 204(0xcc, float:2.86E-43)
            if (r7 == r10) goto L_0x01e0
            r10 = 205(0xcd, float:2.87E-43)
            if (r7 == r10) goto L_0x01e0
            r10 = 304(0x130, float:4.26E-43)
            if (r7 == r10) goto L_0x01e0
            r3 = r6
            boolean r7 = r11.isCancelled()
            if (r7 == 0) goto L_0x01d7
            boolean r7 = r3 instanceof org.xutils.common.Callback.CancelledException
            if (r7 != 0) goto L_0x01d7
            org.xutils.common.Callback$CancelledException r7 = new org.xutils.common.Callback$CancelledException
            java.lang.String r10 = "canceled by user"
            r7.<init>(r10)
            r3 = r7
        L_0x01d7:
            org.xutils.http.request.UriRequest r7 = r11.request
            int r2 = r2 + 1
            boolean r1 = r4.canRetry(r7, r3, r2)
            goto L_0x01fd
        L_0x01e0:
            return r9
        L_0x01e1:
            r6 = move-exception
            r1 = 1
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r10 = "Http Redirect:"
            r7.append(r10)
            org.xutils.http.RequestParams r10 = r11.params
            java.lang.String r10 = r10.getUri()
            r7.append(r10)
            java.lang.String r7 = r7.toString()
            org.xutils.common.util.LogUtil.w((java.lang.String) r7)
        L_0x01fd:
            goto L_0x0102
        L_0x01ff:
            if (r3 == 0) goto L_0x020f
            if (r0 != 0) goto L_0x020f
            java.lang.Boolean r6 = r11.trustCache
            boolean r6 = r6.booleanValue()
            if (r6 == 0) goto L_0x020c
            goto L_0x020f
        L_0x020c:
            r11.hasException = r8
            throw r3
        L_0x020f:
            return r0
        L_0x0210:
            org.xutils.common.Callback$CancelledException r5 = new org.xutils.common.Callback$CancelledException
            java.lang.String r6 = "cancelled before request"
            r5.<init>(r6)
            throw r5
        L_0x0218:
            org.xutils.common.Callback$CancelledException r0 = new org.xutils.common.Callback$CancelledException
            java.lang.String r1 = "cancelled before request"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xutils.http.HttpTask.doBackground():java.lang.Object");
    }

    /* access modifiers changed from: protected */
    public void onUpdate(int flag, Object... args) {
        Object obj;
        Callback.ProgressCallback progressCallback2;
        if (flag == 1) {
            RequestTracker requestTracker = this.tracker;
            if (requestTracker != null) {
                requestTracker.onRequestCreated(args[0]);
            }
        } else if (flag == 2) {
            synchronized (this.cacheLock) {
                try {
                    ResultType result = args[0];
                    if (this.tracker != null) {
                        this.tracker.onCache(this.request, result);
                    }
                    this.trustCache = Boolean.valueOf(this.cacheCallback.onCache(result));
                    obj = this.cacheLock;
                } catch (Throwable ex) {
                    try {
                        this.trustCache = false;
                        this.callback.onError(ex, true);
                        obj = this.cacheLock;
                    } catch (Throwable th) {
                        this.cacheLock.notifyAll();
                        throw th;
                    }
                }
                obj.notifyAll();
            }
        } else if (flag == 3 && (progressCallback2 = this.progressCallback) != null && args.length == 3) {
            try {
                progressCallback2.onLoading(args[0].longValue(), args[1].longValue(), args[2].booleanValue());
            } catch (Throwable ex2) {
                this.callback.onError(ex2, true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onWaiting() {
        RequestTracker requestTracker = this.tracker;
        if (requestTracker != null) {
            requestTracker.onWaiting(this.params);
        }
        Callback.ProgressCallback progressCallback2 = this.progressCallback;
        if (progressCallback2 != null) {
            progressCallback2.onWaiting();
        }
    }

    /* access modifiers changed from: protected */
    public void onStarted() {
        RequestTracker requestTracker = this.tracker;
        if (requestTracker != null) {
            requestTracker.onStart(this.params);
        }
        Callback.ProgressCallback progressCallback2 = this.progressCallback;
        if (progressCallback2 != null) {
            progressCallback2.onStarted();
        }
    }

    /* access modifiers changed from: protected */
    public void onSuccess(ResultType result) {
        if (!this.hasException) {
            RequestTracker requestTracker = this.tracker;
            if (requestTracker != null) {
                requestTracker.onSuccess(this.request, result);
            }
            this.callback.onSuccess(result);
        }
    }

    /* access modifiers changed from: protected */
    public void onError(Throwable ex, boolean isCallbackError) {
        RequestTracker requestTracker = this.tracker;
        if (requestTracker != null) {
            requestTracker.onError(this.request, ex, isCallbackError);
        }
        this.callback.onError(ex, isCallbackError);
    }

    /* access modifiers changed from: protected */
    public void onCancelled(Callback.CancelledException cex) {
        RequestTracker requestTracker = this.tracker;
        if (requestTracker != null) {
            requestTracker.onCancelled(this.request);
        }
        this.callback.onCancelled(cex);
    }

    /* access modifiers changed from: protected */
    public void onFinished() {
        RequestTracker requestTracker = this.tracker;
        if (requestTracker != null) {
            requestTracker.onFinished(this.request);
        }
        x.task().run(new Runnable() {
            public void run() {
                HttpTask.this.closeRequestSync();
            }
        });
        this.callback.onFinished();
    }

    private void clearRawResult() {
        Object obj = this.rawResult;
        if (obj instanceof Closeable) {
            IOUtil.closeQuietly((Closeable) obj);
        }
        this.rawResult = null;
    }

    /* access modifiers changed from: protected */
    public void cancelWorks() {
        x.task().run(new Runnable() {
            public void run() {
                HttpTask.this.closeRequestSync();
            }
        });
    }

    /* access modifiers changed from: protected */
    public boolean isCancelFast() {
        return this.params.isCancelFast();
    }

    /* access modifiers changed from: private */
    public void closeRequestSync() {
        if (File.class == this.loadType) {
            synchronized (sCurrFileLoadCount) {
                sCurrFileLoadCount.notifyAll();
            }
        }
        clearRawResult();
        IOUtil.closeQuietly((Closeable) this.request);
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public Priority getPriority() {
        return this.params.getPriority();
    }

    public boolean updateProgress(long total, long current, boolean forceUpdateUI) {
        long total2;
        if (isCancelled() || isFinished()) {
            return false;
        }
        if (this.progressCallback == null || this.request == null || current <= 0) {
        } else {
            if (total < 0) {
                total2 = -1;
            } else if (total < current) {
                total2 = current;
            } else {
                total2 = total;
            }
            if (forceUpdateUI) {
                this.lastUpdateTime = System.currentTimeMillis();
                update(3, Long.valueOf(total2), Long.valueOf(current), Boolean.valueOf(this.request.isLoading()));
            } else {
                long currTime = System.currentTimeMillis();
                if (currTime - this.lastUpdateTime >= this.loadingUpdateMaxTimeSpan) {
                    this.lastUpdateTime = currTime;
                    update(3, Long.valueOf(total2), Long.valueOf(current), Boolean.valueOf(this.request.isLoading()));
                }
            }
        }
        if (isCancelled() || isFinished()) {
            return false;
        }
        return true;
    }

    public String toString() {
        return this.params.toString();
    }

    private final class RequestWorker {
        Throwable ex;
        Object result;

        private RequestWorker() {
        }

        public void request() {
            HttpException httpEx;
            int errorCode;
            RedirectHandler redirectHandler;
            boolean interrupted = false;
            try {
                if (File.class == HttpTask.this.loadType) {
                    synchronized (HttpTask.sCurrFileLoadCount) {
                        while (HttpTask.sCurrFileLoadCount.get() >= 10 && !HttpTask.this.isCancelled()) {
                            try {
                                HttpTask.sCurrFileLoadCount.wait(10);
                            } catch (InterruptedException e) {
                                interrupted = true;
                            } catch (Throwable th) {
                            }
                        }
                    }
                    HttpTask.sCurrFileLoadCount.incrementAndGet();
                }
                if (interrupted || HttpTask.this.isCancelled()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("cancelled before request");
                    sb.append(interrupted ? "(interrupted)" : "");
                    throw new Callback.CancelledException(sb.toString());
                }
                try {
                    HttpTask.this.request.setRequestInterceptListener(HttpTask.this.requestInterceptListener);
                    this.result = HttpTask.this.request.loadResult();
                } catch (Throwable ex2) {
                    this.ex = ex2;
                }
                if (this.ex != null) {
                    throw this.ex;
                } else if (File.class == HttpTask.this.loadType) {
                    synchronized (HttpTask.sCurrFileLoadCount) {
                        HttpTask.sCurrFileLoadCount.decrementAndGet();
                        HttpTask.sCurrFileLoadCount.notifyAll();
                    }
                }
            } catch (Throwable ex3) {
                try {
                    this.ex = ex3;
                    if ((ex3 instanceof HttpException) && (((errorCode = httpEx.getCode()) == 301 || errorCode == 302) && (redirectHandler = HttpTask.this.params.getRedirectHandler()) != null)) {
                        try {
                            RequestParams redirectParams = redirectHandler.getRedirectParams(HttpTask.this.request);
                            if (redirectParams != null) {
                                if (redirectParams.getMethod() == null) {
                                    redirectParams.setMethod(HttpTask.this.params.getMethod());
                                }
                                RequestParams unused = HttpTask.this.params = redirectParams;
                                UriRequest unused2 = HttpTask.this.request = HttpTask.this.createNewRequest();
                                this.ex = new HttpRedirectException(errorCode, (httpEx = (HttpException) ex3).getMessage(), httpEx.getResult());
                            }
                        } catch (Throwable th2) {
                            this.ex = ex3;
                        }
                    }
                    if (File.class == HttpTask.this.loadType) {
                        synchronized (HttpTask.sCurrFileLoadCount) {
                            HttpTask.sCurrFileLoadCount.decrementAndGet();
                            HttpTask.sCurrFileLoadCount.notifyAll();
                        }
                    }
                } catch (Throwable th3) {
                    if (File.class == HttpTask.this.loadType) {
                        synchronized (HttpTask.sCurrFileLoadCount) {
                            HttpTask.sCurrFileLoadCount.decrementAndGet();
                            HttpTask.sCurrFileLoadCount.notifyAll();
                        }
                    }
                    throw th3;
                }
            }
        }
    }
}

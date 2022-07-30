package org.xutils.http.request;

import android.text.TextUtils;
import java.lang.reflect.Type;
import java.util.HashMap;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.http.app.RequestTracker;

public final class UriRequestFactory {
    private static final HashMap<String, Class<? extends UriRequest>> SCHEME_CLS_MAP = new HashMap<>();
    private static Class<? extends RequestTracker> defaultTrackerCls;

    private UriRequestFactory() {
    }

    public static UriRequest getUriRequest(RequestParams params, Type loadType) throws Throwable {
        String scheme = null;
        String uri = params.getUri();
        int index = uri.indexOf(":");
        if (uri.startsWith("/")) {
            scheme = "file";
        } else if (index > 0) {
            scheme = uri.substring(0, index);
        }
        if (!TextUtils.isEmpty(scheme)) {
            String scheme2 = scheme.toLowerCase();
            Class<? extends UriRequest> cls = SCHEME_CLS_MAP.get(scheme2);
            if (cls != null) {
                return (UriRequest) cls.getConstructor(new Class[]{RequestParams.class, Type.class}).newInstance(new Object[]{params, loadType});
            } else if (scheme2.startsWith("http")) {
                return new HttpRequest(params, loadType);
            } else {
                if (scheme2.equals("assets")) {
                    return new AssetsRequest(params, loadType);
                }
                if (scheme2.equals("file")) {
                    return new LocalFileRequest(params, loadType);
                }
                if (scheme2.equals("res")) {
                    return new ResRequest(params, loadType);
                }
                throw new IllegalArgumentException("The url not be support: " + uri);
            }
        } else {
            throw new IllegalArgumentException("The url not be support: " + uri);
        }
    }

    public static void registerDefaultTrackerClass(Class<? extends RequestTracker> trackerCls) {
        defaultTrackerCls = trackerCls;
    }

    public static RequestTracker getDefaultTracker() {
        try {
            if (defaultTrackerCls == null) {
                return null;
            }
            return (RequestTracker) defaultTrackerCls.newInstance();
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
            return null;
        }
    }

    public static void registerRequestClass(String scheme, Class<? extends UriRequest> uriRequestCls) {
        SCHEME_CLS_MAP.put(scheme, uriRequestCls);
    }
}

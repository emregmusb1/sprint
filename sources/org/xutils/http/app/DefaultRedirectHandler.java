package org.xutils.http.app;

import android.text.TextUtils;
import android.webkit.URLUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.http.request.HttpRequest;
import org.xutils.http.request.UriRequest;

public class DefaultRedirectHandler implements RedirectHandler {
    public RequestParams getRedirectParams(UriRequest request) throws Throwable {
        if (!(request instanceof HttpRequest)) {
            return null;
        }
        HttpRequest httpRequest = (HttpRequest) request;
        RequestParams params = httpRequest.getParams();
        String location = httpRequest.getResponseHeader("Location");
        if (TextUtils.isEmpty(location)) {
            return null;
        }
        if (!URLUtil.isHttpsUrl(location) && !URLUtil.isHttpUrl(location)) {
            String url = params.getUri();
            if (location.startsWith("/")) {
                int pathIndex = url.indexOf("/", 8);
                if (pathIndex != -1) {
                    url = url.substring(0, pathIndex);
                }
            } else {
                int pathIndex2 = url.lastIndexOf("/");
                if (pathIndex2 >= 8) {
                    url = url.substring(0, pathIndex2 + 1);
                } else {
                    url = url + "/";
                }
            }
            location = url + location;
        }
        params.setUri(location);
        int code = request.getResponseCode();
        if (code == 301 || code == 302 || code == 303) {
            params.clearParams();
            params.setMethod(HttpMethod.GET);
        }
        return params;
    }
}

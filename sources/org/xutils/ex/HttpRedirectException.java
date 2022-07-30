package org.xutils.ex;

public class HttpRedirectException extends HttpException {
    private static final long serialVersionUID = 1;

    public HttpRedirectException(int code, String detailMessage, String result) {
        super(code, detailMessage);
        setResult(result);
    }
}

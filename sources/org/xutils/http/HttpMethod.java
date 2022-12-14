package org.xutils.http;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    HEAD("HEAD"),
    MOVE("MOVE"),
    COPY("COPY"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE"),
    CONNECT("CONNECT");
    
    private final String value;

    private HttpMethod(String value2) {
        this.value = value2;
    }

    public String toString() {
        return this.value;
    }

    public static boolean permitsRetry(HttpMethod method) {
        return method == GET;
    }

    public static boolean permitsCache(HttpMethod method) {
        return method == GET || method == POST;
    }

    public static boolean permitsRequestBody(HttpMethod method) {
        return method == null || method == POST || method == PUT || method == PATCH || method == DELETE;
    }
}

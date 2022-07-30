package org.xutils.http.app;

import java.lang.reflect.Type;

public interface ResponseParser<ResponseDataType> extends RequestInterceptListener {
    Object parse(Type type, Class<?> cls, ResponseDataType responsedatatype) throws Throwable;
}

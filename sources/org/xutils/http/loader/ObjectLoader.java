package org.xutils.http.loader;

import java.lang.reflect.Type;
import org.xutils.cache.DiskCacheEntity;
import org.xutils.http.RequestParams;
import org.xutils.http.app.ResponseParser;
import org.xutils.http.request.UriRequest;

class ObjectLoader extends Loader<Object> {
    private final Loader<?> innerLoader;
    private final Class<?> objectClass;
    private final Type objectType;
    private final ResponseParser parser;

    /* JADX WARNING: type inference failed for: r3v10, types: [java.lang.annotation.Annotation] */
    /* JADX WARNING: type inference failed for: r5v7, types: [java.lang.annotation.Annotation] */
    /* JADX WARNING: type inference failed for: r5v12, types: [java.lang.reflect.Type] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ObjectLoader(java.lang.reflect.Type r7) {
        /*
            r6 = this;
            r6.<init>()
            r6.objectType = r7
            boolean r0 = r7 instanceof java.lang.reflect.ParameterizedType
            java.lang.String r1 = "not support callback type "
            if (r0 == 0) goto L_0x0017
            r0 = r7
            java.lang.reflect.ParameterizedType r0 = (java.lang.reflect.ParameterizedType) r0
            java.lang.reflect.Type r0 = r0.getRawType()
            java.lang.Class r0 = (java.lang.Class) r0
            r6.objectClass = r0
            goto L_0x0020
        L_0x0017:
            boolean r0 = r7 instanceof java.lang.reflect.TypeVariable
            if (r0 != 0) goto L_0x00d1
            r0 = r7
            java.lang.Class r0 = (java.lang.Class) r0
            r6.objectClass = r0
        L_0x0020:
            r0 = 0
            r2 = r7
            java.lang.Class<java.util.List> r3 = java.util.List.class
            java.lang.Class<?> r4 = r6.objectClass
            boolean r3 = r3.equals(r4)
            r4 = 0
            if (r3 == 0) goto L_0x006f
            java.lang.reflect.Type r3 = r6.objectType
            java.lang.Class<java.util.List> r5 = java.util.List.class
            java.lang.reflect.Type r2 = org.xutils.common.util.ParameterizedTypeUtil.getParameterizedType(r3, r5, r4)
            r3 = 0
            boolean r5 = r2 instanceof java.lang.reflect.ParameterizedType
            if (r5 == 0) goto L_0x0045
            r5 = r2
            java.lang.reflect.ParameterizedType r5 = (java.lang.reflect.ParameterizedType) r5
            java.lang.reflect.Type r5 = r5.getRawType()
            r3 = r5
            java.lang.Class r3 = (java.lang.Class) r3
            goto L_0x004c
        L_0x0045:
            boolean r5 = r2 instanceof java.lang.reflect.TypeVariable
            if (r5 != 0) goto L_0x0056
            r3 = r2
            java.lang.Class r3 = (java.lang.Class) r3
        L_0x004c:
            java.lang.Class<org.xutils.http.annotation.HttpResponse> r5 = org.xutils.http.annotation.HttpResponse.class
            java.lang.annotation.Annotation r5 = r3.getAnnotation(r5)
            r0 = r5
            org.xutils.http.annotation.HttpResponse r0 = (org.xutils.http.annotation.HttpResponse) r0
            goto L_0x007a
        L_0x0056:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r1)
            java.lang.String r1 = r2.toString()
            r5.append(r1)
            java.lang.String r1 = r5.toString()
            r4.<init>(r1)
            throw r4
        L_0x006f:
            java.lang.Class<?> r3 = r6.objectClass
            java.lang.Class<org.xutils.http.annotation.HttpResponse> r5 = org.xutils.http.annotation.HttpResponse.class
            java.lang.annotation.Annotation r3 = r3.getAnnotation(r5)
            r0 = r3
            org.xutils.http.annotation.HttpResponse r0 = (org.xutils.http.annotation.HttpResponse) r0
        L_0x007a:
            if (r0 == 0) goto L_0x00ba
            java.lang.Class r3 = r0.parser()     // Catch:{ Throwable -> 0x00b1 }
            java.lang.Object r5 = r3.newInstance()     // Catch:{ Throwable -> 0x00b1 }
            org.xutils.http.app.ResponseParser r5 = (org.xutils.http.app.ResponseParser) r5     // Catch:{ Throwable -> 0x00b1 }
            r6.parser = r5     // Catch:{ Throwable -> 0x00b1 }
            java.lang.Class<org.xutils.http.app.ResponseParser> r5 = org.xutils.http.app.ResponseParser.class
            java.lang.reflect.Type r4 = org.xutils.common.util.ParameterizedTypeUtil.getParameterizedType(r3, r5, r4)     // Catch:{ Throwable -> 0x00b1 }
            org.xutils.http.loader.Loader r4 = org.xutils.http.loader.LoaderFactory.getLoader(r4)     // Catch:{ Throwable -> 0x00b1 }
            r6.innerLoader = r4     // Catch:{ Throwable -> 0x00b1 }
            org.xutils.http.loader.Loader<?> r3 = r6.innerLoader
            boolean r3 = r3 instanceof org.xutils.http.loader.ObjectLoader
            if (r3 != 0) goto L_0x009c
            return
        L_0x009c:
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r1)
            r4.append(r2)
            java.lang.String r1 = r4.toString()
            r3.<init>(r1)
            throw r3
        L_0x00b1:
            r1 = move-exception
            java.lang.RuntimeException r3 = new java.lang.RuntimeException
            java.lang.String r4 = "create parser error"
            r3.<init>(r4, r1)
            throw r3
        L_0x00ba:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "not found @HttpResponse from "
            r3.append(r4)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            r1.<init>(r3)
            throw r1
        L_0x00d1:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r1)
            java.lang.String r1 = r7.toString()
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xutils.http.loader.ObjectLoader.<init>(java.lang.reflect.Type):void");
    }

    public Loader<Object> newInstance() {
        throw new IllegalAccessError("use constructor create ObjectLoader.");
    }

    public void setParams(RequestParams params) {
        this.innerLoader.setParams(params);
    }

    public Object load(UriRequest request) throws Throwable {
        request.setResponseParser(this.parser);
        return this.parser.parse(this.objectType, this.objectClass, this.innerLoader.load(request));
    }

    public Object loadFromCache(DiskCacheEntity cacheEntity) throws Throwable {
        return this.parser.parse(this.objectType, this.objectClass, this.innerLoader.loadFromCache(cacheEntity));
    }

    public void save2Cache(UriRequest request) {
        this.innerLoader.save2Cache(request);
    }
}

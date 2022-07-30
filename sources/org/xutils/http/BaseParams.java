package org.xutils.http;

import android.text.TextUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.compress.utils.CharsetNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.KeyValue;
import org.xutils.http.body.FileBody;
import org.xutils.http.body.InputStreamBody;
import org.xutils.http.body.MultipartBody;
import org.xutils.http.body.RequestBody;
import org.xutils.http.body.StringBody;
import org.xutils.http.body.UrlEncodedBody;

public abstract class BaseParams {
    private boolean asJsonContent = false;
    private String bodyContent;
    private String bodyContentType;
    private final List<KeyValue> bodyParams = new ArrayList();
    private String charset = CharsetNames.UTF_8;
    private final List<Header> headers = new ArrayList();
    private HttpMethod method;
    private boolean multipart = false;
    private final List<KeyValue> queryStringParams = new ArrayList();
    private RequestBody requestBody;

    public void setCharset(String charset2) {
        if (!TextUtils.isEmpty(charset2)) {
            this.charset = charset2;
        }
    }

    public String getCharset() {
        return this.charset;
    }

    public void setMethod(HttpMethod method2) {
        this.method = method2;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public boolean isMultipart() {
        return this.multipart;
    }

    public void setMultipart(boolean multipart2) {
        this.multipart = multipart2;
    }

    public boolean isAsJsonContent() {
        return this.asJsonContent;
    }

    public void setAsJsonContent(boolean asJsonContent2) {
        this.asJsonContent = asJsonContent2;
    }

    public void setHeader(String name, String value) {
        if (!TextUtils.isEmpty(name)) {
            Header header = new Header(name, value, true);
            Iterator<Header> it = this.headers.iterator();
            while (it.hasNext()) {
                if (name.equals(it.next().key)) {
                    it.remove();
                }
            }
            this.headers.add(header);
        }
    }

    public void addHeader(String name, String value) {
        if (!TextUtils.isEmpty(name)) {
            this.headers.add(new Header(name, value, false));
        }
    }

    public void addParameter(String name, Object value) {
        if (HttpMethod.permitsRequestBody(this.method)) {
            addBodyParameter(name, value, (String) null, (String) null);
        } else {
            addQueryStringParameter(name, value);
        }
    }

    public void addQueryStringParameter(String name, Object value) {
        if (!TextUtils.isEmpty(name)) {
            if (value instanceof Iterable) {
                for (Object item : (Iterable) value) {
                    this.queryStringParams.add(new ArrayItem(name, item));
                }
            } else if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    this.queryStringParams.add(new ArrayItem(name, array.opt(i)));
                }
            } else if (value.getClass().isArray()) {
                int len2 = Array.getLength(value);
                for (int i2 = 0; i2 < len2; i2++) {
                    this.queryStringParams.add(new ArrayItem(name, Array.get(value, i2)));
                }
            } else {
                this.queryStringParams.add(new KeyValue(name, value));
            }
        }
    }

    public void addBodyParameter(String name, Object value) {
        addBodyParameter(name, value, (String) null, (String) null);
    }

    public void addBodyParameter(String name, Object value, String contentType) {
        addBodyParameter(name, value, contentType, (String) null);
    }

    public void addBodyParameter(String name, Object value, String contentType, String fileName) {
        if (TextUtils.isEmpty(name) && value == null) {
            return;
        }
        if (!TextUtils.isEmpty(contentType) || !TextUtils.isEmpty(fileName)) {
            this.bodyParams.add(new BodyItemWrapper(name, value, contentType, fileName));
        } else if (value instanceof Iterable) {
            for (Object item : (Iterable) value) {
                this.bodyParams.add(new ArrayItem(name, item));
            }
        } else if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            int len = array.length();
            for (int i = 0; i < len; i++) {
                this.bodyParams.add(new ArrayItem(name, array.opt(i)));
            }
        } else if (value instanceof byte[]) {
            this.bodyParams.add(new KeyValue(name, value));
        } else if (value.getClass().isArray()) {
            int len2 = Array.getLength(value);
            for (int i2 = 0; i2 < len2; i2++) {
                this.bodyParams.add(new ArrayItem(name, Array.get(value, i2)));
            }
        } else {
            this.bodyParams.add(new KeyValue(name, value));
        }
    }

    public void setBodyContent(String content) {
        this.bodyContent = content;
    }

    public String getBodyContent() {
        checkBodyParams();
        return this.bodyContent;
    }

    public void setBodyContentType(String bodyContentType2) {
        this.bodyContentType = bodyContentType2;
    }

    public List<Header> getHeaders() {
        return new ArrayList(this.headers);
    }

    public List<KeyValue> getQueryStringParams() {
        checkBodyParams();
        return new ArrayList(this.queryStringParams);
    }

    public List<KeyValue> getBodyParams() {
        checkBodyParams();
        return new ArrayList(this.bodyParams);
    }

    public List<KeyValue> getParams(String name) {
        List<KeyValue> result = new ArrayList<>();
        for (KeyValue kv : this.queryStringParams) {
            if (name != null && name.equals(kv.key)) {
                result.add(kv);
            }
        }
        for (KeyValue kv2 : this.bodyParams) {
            if (name == null && kv2.key == null) {
                result.add(kv2);
            } else if (name != null && name.equals(kv2.key)) {
                result.add(kv2);
            }
        }
        return result;
    }

    public void clearParams() {
        this.queryStringParams.clear();
        this.bodyParams.clear();
        this.bodyContent = null;
        this.bodyContentType = null;
        this.requestBody = null;
    }

    public void removeParameter(String name) {
        if (TextUtils.isEmpty(name)) {
            this.bodyContent = null;
            this.bodyContentType = null;
        } else {
            Iterator<KeyValue> it = this.queryStringParams.iterator();
            while (it.hasNext()) {
                if (name.equals(it.next().key)) {
                    it.remove();
                }
            }
        }
        Iterator<KeyValue> it2 = this.bodyParams.iterator();
        while (it2.hasNext()) {
            KeyValue kv = it2.next();
            if (name == null && kv.key == null) {
                it2.remove();
            } else if (name != null && name.equals(kv.key)) {
                it2.remove();
            }
        }
    }

    public void setRequestBody(RequestBody requestBody2) {
        this.requestBody = requestBody2;
    }

    public RequestBody getRequestBody() throws IOException {
        checkBodyParams();
        RequestBody requestBody2 = this.requestBody;
        if (requestBody2 != null) {
            return requestBody2;
        }
        if (!TextUtils.isEmpty(this.bodyContent)) {
            RequestBody result = new StringBody(this.bodyContent, this.charset);
            result.setContentType(this.bodyContentType);
            return result;
        } else if (this.multipart) {
            RequestBody result2 = new MultipartBody(this.bodyParams, this.charset);
            result2.setContentType(this.bodyContentType);
            return result2;
        } else if (this.bodyParams.size() == 1) {
            KeyValue kv = this.bodyParams.get(0);
            String name = kv.key;
            Object value = kv.value;
            String contentType = null;
            if (kv instanceof BodyItemWrapper) {
                contentType = ((BodyItemWrapper) kv).contentType;
            }
            if (TextUtils.isEmpty(contentType)) {
                contentType = this.bodyContentType;
            }
            if (value instanceof File) {
                return new FileBody((File) value, contentType);
            }
            if (value instanceof InputStream) {
                return new InputStreamBody((InputStream) value, contentType);
            }
            if (value instanceof byte[]) {
                return new InputStreamBody(new ByteArrayInputStream((byte[]) value), contentType);
            }
            if (TextUtils.isEmpty(name)) {
                RequestBody result3 = new StringBody(kv.getValueStrOrEmpty(), this.charset);
                result3.setContentType(contentType);
                return result3;
            }
            RequestBody result4 = new UrlEncodedBody(this.bodyParams, this.charset);
            result4.setContentType(contentType);
            return result4;
        } else {
            RequestBody result5 = new UrlEncodedBody(this.bodyParams, this.charset);
            result5.setContentType(this.bodyContentType);
            return result5;
        }
    }

    public String toJSONString() throws JSONException {
        JSONObject jsonObject;
        if (!TextUtils.isEmpty(this.bodyContent)) {
            jsonObject = new JSONObject(this.bodyContent);
        } else {
            jsonObject = new JSONObject();
        }
        List<KeyValue> list = new ArrayList<>(this.queryStringParams.size() + this.bodyParams.size());
        list.addAll(this.queryStringParams);
        list.addAll(this.bodyParams);
        params2Json(jsonObject, list);
        return jsonObject.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!this.queryStringParams.isEmpty()) {
            for (KeyValue kv : this.queryStringParams) {
                sb.append(kv.key);
                sb.append("=");
                sb.append(kv.value);
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        if (!TextUtils.isEmpty(this.bodyContent)) {
            sb.append("<");
            sb.append(this.bodyContent);
            sb.append(">");
        } else if (!this.bodyParams.isEmpty()) {
            sb.append("<");
            for (KeyValue kv2 : this.bodyParams) {
                sb.append(kv2.key);
                sb.append("=");
                sb.append(kv2.value);
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(">");
        }
        return sb.toString();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0065, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void checkBodyParams() {
        /*
            r3 = this;
            monitor-enter(r3)
            java.util.List<org.xutils.common.util.KeyValue> r0 = r3.bodyParams     // Catch:{ all -> 0x0074 }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x0074 }
            if (r0 == 0) goto L_0x000b
            monitor-exit(r3)
            return
        L_0x000b:
            org.xutils.http.body.RequestBody r0 = r3.requestBody     // Catch:{ all -> 0x0074 }
            if (r0 != 0) goto L_0x0066
            org.xutils.http.HttpMethod r0 = r3.method     // Catch:{ all -> 0x0074 }
            boolean r0 = org.xutils.http.HttpMethod.permitsRequestBody(r0)     // Catch:{ all -> 0x0074 }
            if (r0 != 0) goto L_0x0018
            goto L_0x0066
        L_0x0018:
            boolean r0 = r3.asJsonContent     // Catch:{ all -> 0x0074 }
            if (r0 == 0) goto L_0x0050
            r0 = 0
            java.lang.String r1 = r3.bodyContent     // Catch:{ JSONException -> 0x0045 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ JSONException -> 0x0045 }
            if (r1 != 0) goto L_0x002e
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0045 }
            java.lang.String r2 = r3.bodyContent     // Catch:{ JSONException -> 0x0045 }
            r1.<init>(r2)     // Catch:{ JSONException -> 0x0045 }
            r0 = r1
            goto L_0x0034
        L_0x002e:
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0045 }
            r1.<init>()     // Catch:{ JSONException -> 0x0045 }
            r0 = r1
        L_0x0034:
            java.util.List<org.xutils.common.util.KeyValue> r1 = r3.bodyParams     // Catch:{ JSONException -> 0x0045 }
            r3.params2Json(r0, r1)     // Catch:{ JSONException -> 0x0045 }
            java.lang.String r1 = r0.toString()     // Catch:{ JSONException -> 0x0045 }
            r3.bodyContent = r1     // Catch:{ JSONException -> 0x0045 }
            java.util.List<org.xutils.common.util.KeyValue> r1 = r3.bodyParams     // Catch:{ JSONException -> 0x0045 }
            r1.clear()     // Catch:{ JSONException -> 0x0045 }
            goto L_0x0064
        L_0x0045:
            r0 = move-exception
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0074 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x0074 }
            r1.<init>(r2, r0)     // Catch:{ all -> 0x0074 }
            throw r1     // Catch:{ all -> 0x0074 }
        L_0x0050:
            java.lang.String r0 = r3.bodyContent     // Catch:{ all -> 0x0074 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x0074 }
            if (r0 != 0) goto L_0x0064
            java.util.List<org.xutils.common.util.KeyValue> r0 = r3.queryStringParams     // Catch:{ all -> 0x0074 }
            java.util.List<org.xutils.common.util.KeyValue> r1 = r3.bodyParams     // Catch:{ all -> 0x0074 }
            r0.addAll(r1)     // Catch:{ all -> 0x0074 }
            java.util.List<org.xutils.common.util.KeyValue> r0 = r3.bodyParams     // Catch:{ all -> 0x0074 }
            r0.clear()     // Catch:{ all -> 0x0074 }
        L_0x0064:
            monitor-exit(r3)
            return
        L_0x0066:
            java.util.List<org.xutils.common.util.KeyValue> r0 = r3.queryStringParams     // Catch:{ all -> 0x0074 }
            java.util.List<org.xutils.common.util.KeyValue> r1 = r3.bodyParams     // Catch:{ all -> 0x0074 }
            r0.addAll(r1)     // Catch:{ all -> 0x0074 }
            java.util.List<org.xutils.common.util.KeyValue> r0 = r3.bodyParams     // Catch:{ all -> 0x0074 }
            r0.clear()     // Catch:{ all -> 0x0074 }
            monitor-exit(r3)
            return
        L_0x0074:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xutils.http.BaseParams.checkBodyParams():void");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: org.json.JSONArray} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void params2Json(org.json.JSONObject r9, java.util.List<org.xutils.common.util.KeyValue> r10) throws org.json.JSONException {
        /*
            r8 = this;
            java.util.HashSet r0 = new java.util.HashSet
            int r1 = r10.size()
            r0.<init>(r1)
            java.util.LinkedHashMap r1 = new java.util.LinkedHashMap
            int r2 = r10.size()
            r1.<init>(r2)
            r2 = 0
        L_0x0013:
            int r3 = r10.size()
            if (r2 >= r3) goto L_0x0053
            java.lang.Object r3 = r10.get(r2)
            org.xutils.common.util.KeyValue r3 = (org.xutils.common.util.KeyValue) r3
            java.lang.String r4 = r3.key
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 == 0) goto L_0x0028
            goto L_0x0050
        L_0x0028:
            r5 = 0
            boolean r6 = r1.containsKey(r4)
            if (r6 == 0) goto L_0x0037
            java.lang.Object r6 = r1.get(r4)
            r5 = r6
            org.json.JSONArray r5 = (org.json.JSONArray) r5
            goto L_0x0040
        L_0x0037:
            org.json.JSONArray r6 = new org.json.JSONArray
            r6.<init>()
            r5 = r6
            r1.put(r4, r5)
        L_0x0040:
            java.lang.Object r6 = r3.value
            java.lang.Object r6 = org.xutils.http.RequestParamsHelper.parseJSONObject(r6)
            r5.put(r6)
            boolean r6 = r3 instanceof org.xutils.http.BaseParams.ArrayItem
            if (r6 == 0) goto L_0x0050
            r0.add(r4)
        L_0x0050:
            int r2 = r2 + 1
            goto L_0x0013
        L_0x0053:
            java.util.Set r2 = r1.entrySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x005b:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x008e
            java.lang.Object r3 = r2.next()
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3
            java.lang.Object r4 = r3.getKey()
            java.lang.String r4 = (java.lang.String) r4
            java.lang.Object r5 = r3.getValue()
            org.json.JSONArray r5 = (org.json.JSONArray) r5
            int r6 = r5.length()
            r7 = 1
            if (r6 > r7) goto L_0x008a
            boolean r6 = r0.contains(r4)
            if (r6 == 0) goto L_0x0081
            goto L_0x008a
        L_0x0081:
            r6 = 0
            java.lang.Object r6 = r5.get(r6)
            r9.put(r4, r6)
            goto L_0x008d
        L_0x008a:
            r9.put(r4, r5)
        L_0x008d:
            goto L_0x005b
        L_0x008e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xutils.http.BaseParams.params2Json(org.json.JSONObject, java.util.List):void");
    }

    public static final class ArrayItem extends KeyValue {
        public ArrayItem(String key, Object value) {
            super(key, value);
        }
    }

    public static final class Header extends KeyValue {
        public final boolean setHeader;

        public Header(String key, String value, boolean setHeader2) {
            super(key, value);
            this.setHeader = setHeader2;
        }
    }

    public final class BodyItemWrapper extends KeyValue {
        public final String contentType;
        public final String fileName;

        public BodyItemWrapper(String key, Object value, String contentType2, String fileName2) {
            super(key, value);
            if (TextUtils.isEmpty(contentType2)) {
                this.contentType = "application/octet-stream";
            } else {
                this.contentType = contentType2;
            }
            this.fileName = fileName2;
        }
    }
}

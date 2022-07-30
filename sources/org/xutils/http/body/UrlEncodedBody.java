package org.xutils.http.body;

import android.text.TextUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import org.apache.commons.compress.utils.CharsetNames;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;

public class UrlEncodedBody implements RequestBody {
    private String charset = CharsetNames.UTF_8;
    private byte[] content;

    public UrlEncodedBody(List<KeyValue> params, String charset2) throws IOException {
        if (!TextUtils.isEmpty(charset2)) {
            this.charset = charset2;
        }
        StringBuilder contentSb = new StringBuilder();
        if (params != null) {
            for (KeyValue kv : params) {
                String name = kv.key;
                String value = kv.getValueStrOrNull();
                if (!TextUtils.isEmpty(name) && value != null) {
                    if (contentSb.length() > 0) {
                        contentSb.append("&");
                    }
                    contentSb.append(URLEncoder.encode(name, this.charset).replaceAll("\\+", "%20"));
                    contentSb.append("=");
                    contentSb.append(URLEncoder.encode(value, this.charset).replaceAll("\\+", "%20"));
                }
            }
        }
        this.content = contentSb.toString().getBytes(this.charset);
    }

    public long getContentLength() {
        return (long) this.content.length;
    }

    public void setContentType(String contentType) {
        if (!TextUtils.isEmpty(contentType)) {
            LogUtil.w("ignored Content-Type: " + contentType);
        }
    }

    public String getContentType() {
        return "application/x-www-form-urlencoded;charset=" + this.charset;
    }

    public void writeTo(OutputStream sink) throws IOException {
        sink.write(this.content);
        sink.flush();
    }
}

package org.xutils.http.body;

import android.text.TextUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.commons.compress.utils.CharsetNames;

public class StringBody implements RequestBody {
    private String charset = CharsetNames.UTF_8;
    private byte[] content;
    private String contentType;

    public StringBody(String str, String charset2) throws UnsupportedEncodingException {
        if (!TextUtils.isEmpty(charset2)) {
            this.charset = charset2;
        }
        this.content = str.getBytes(this.charset);
    }

    public long getContentLength() {
        return (long) this.content.length;
    }

    public void setContentType(String contentType2) {
        this.contentType = contentType2;
    }

    public String getContentType() {
        if (!TextUtils.isEmpty(this.contentType)) {
            return this.contentType;
        }
        return "application/json;charset=" + this.charset;
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(this.content);
        out.flush();
    }
}

package org.xutils.http.body;

import android.text.TextUtils;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.xutils.common.Callback;
import org.xutils.common.util.IOUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.http.ProgressHandler;

public class InputStreamBody implements ProgressBody {
    private ProgressHandler callBackHandler;
    private InputStream content;
    private String contentType;
    private long current;
    private final long total;

    public InputStreamBody(InputStream inputStream) {
        this(inputStream, (String) null);
    }

    public InputStreamBody(InputStream inputStream, String contentType2) {
        this.current = 0;
        this.content = inputStream;
        this.contentType = contentType2;
        this.total = getInputStreamLength(inputStream);
    }

    public void setProgressHandler(ProgressHandler progressHandler) {
        this.callBackHandler = progressHandler;
    }

    public long getContentLength() {
        return this.total;
    }

    public void setContentType(String contentType2) {
        this.contentType = contentType2;
    }

    public String getContentType() {
        return TextUtils.isEmpty(this.contentType) ? "application/octet-stream" : this.contentType;
    }

    public void writeTo(OutputStream out) throws IOException {
        ProgressHandler progressHandler = this.callBackHandler;
        if (progressHandler == null || progressHandler.updateProgress(this.total, this.current, true)) {
            byte[] buffer = new byte[4096];
            while (true) {
                try {
                    int read = this.content.read(buffer);
                    int len = read;
                    if (read != -1) {
                        out.write(buffer, 0, len);
                        this.current += (long) len;
                        if (this.callBackHandler != null) {
                            if (!this.callBackHandler.updateProgress(this.total, this.current, false)) {
                                throw new Callback.CancelledException("upload stopped!");
                            }
                        }
                    } else {
                        out.flush();
                        if (this.callBackHandler != null) {
                            this.callBackHandler.updateProgress(this.total, this.current, true);
                        }
                        return;
                    }
                } finally {
                    IOUtil.closeQuietly((Closeable) this.content);
                }
            }
        } else {
            throw new Callback.CancelledException("upload stopped!");
        }
    }

    public static long getInputStreamLength(InputStream inputStream) {
        try {
            if (!(inputStream instanceof FileInputStream)) {
                if (!(inputStream instanceof ByteArrayInputStream)) {
                    return -1;
                }
            }
            return (long) inputStream.available();
        } catch (Throwable ex) {
            LogUtil.w(ex.getMessage(), ex);
            return -1;
        }
    }
}

package org.xutils.http.body;

import android.text.TextUtils;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.compress.utils.CharsetNames;
import org.xutils.common.Callback;
import org.xutils.common.util.IOUtil;
import org.xutils.common.util.KeyValue;
import org.xutils.http.BaseParams;
import org.xutils.http.ProgressHandler;

public class MultipartBody implements ProgressBody {
    private static byte[] BOUNDARY_PREFIX_BYTES = "--------7da3d81520810".getBytes();
    private static byte[] END_BYTES = "\r\n".getBytes();
    private static byte[] TWO_DASHES_BYTES = "--".getBytes();
    private byte[] boundaryPostfixBytes;
    private ProgressHandler callBackHandler;
    private String charset = CharsetNames.UTF_8;
    private String contentType;
    private long current = 0;
    private List<KeyValue> multipartParams;
    private long total = 0;

    public MultipartBody(List<KeyValue> multipartParams2, String charset2) {
        if (!TextUtils.isEmpty(charset2)) {
            this.charset = charset2;
        }
        this.multipartParams = multipartParams2;
        generateContentType();
        CounterOutputStream counter = new CounterOutputStream();
        try {
            writeTo(counter);
            this.total = counter.total.get();
        } catch (IOException e) {
            this.total = -1;
        }
    }

    public void setProgressHandler(ProgressHandler progressHandler) {
        this.callBackHandler = progressHandler;
    }

    private void generateContentType() {
        String boundaryPostfix = Double.toHexString(Math.random() * 65535.0d);
        this.boundaryPostfixBytes = boundaryPostfix.getBytes();
        this.contentType = "multipart/form-data; boundary=" + new String(BOUNDARY_PREFIX_BYTES) + boundaryPostfix;
    }

    public long getContentLength() {
        return this.total;
    }

    public void setContentType(String subType) {
        if (!TextUtils.isEmpty(subType)) {
            int index = this.contentType.indexOf(";");
            this.contentType = "multipart/" + subType + this.contentType.substring(index);
        }
    }

    public String getContentType() {
        return this.contentType;
    }

    public void writeTo(OutputStream out) throws IOException {
        ProgressHandler progressHandler = this.callBackHandler;
        if (progressHandler == null || progressHandler.updateProgress(this.total, this.current, true)) {
            for (KeyValue entry : this.multipartParams) {
                writeEntry(out, entry);
            }
            byte[] bArr = TWO_DASHES_BYTES;
            writeLine(out, bArr, BOUNDARY_PREFIX_BYTES, this.boundaryPostfixBytes, bArr);
            out.flush();
            ProgressHandler progressHandler2 = this.callBackHandler;
            if (progressHandler2 != null) {
                progressHandler2.updateProgress(this.total, this.current, true);
                return;
            }
            return;
        }
        throw new Callback.CancelledException("upload stopped!");
    }

    private void writeEntry(OutputStream out, KeyValue entry) throws IOException {
        byte[] content;
        String name = entry.key;
        Object value = entry.value;
        if (!TextUtils.isEmpty(name) && value != null) {
            writeLine(out, TWO_DASHES_BYTES, BOUNDARY_PREFIX_BYTES, this.boundaryPostfixBytes);
            String fileName = "";
            String contentType2 = null;
            if (entry instanceof BaseParams.BodyItemWrapper) {
                BaseParams.BodyItemWrapper wrapper = (BaseParams.BodyItemWrapper) entry;
                fileName = wrapper.fileName;
                contentType2 = wrapper.contentType;
            }
            if (value instanceof File) {
                File file = (File) value;
                if (TextUtils.isEmpty(fileName)) {
                    fileName = file.getName();
                }
                if (TextUtils.isEmpty(contentType2)) {
                    contentType2 = FileBody.getFileContentType(file);
                }
                writeLine(out, buildContentDisposition(name, fileName, this.charset));
                writeLine(out, buildContentType(value, contentType2, this.charset));
                writeLine(out, new byte[0][]);
                writeFile(out, file);
                writeLine(out, new byte[0][]);
                return;
            }
            writeLine(out, buildContentDisposition(name, fileName, this.charset));
            writeLine(out, buildContentType(value, contentType2, this.charset));
            writeLine(out, new byte[0][]);
            if (value instanceof InputStream) {
                writeStreamAndCloseIn(out, (InputStream) value);
                writeLine(out, new byte[0][]);
                return;
            }
            if (value instanceof byte[]) {
                content = (byte[]) value;
            } else {
                content = entry.getValueStrOrEmpty().getBytes(this.charset);
            }
            writeLine(out, content);
            this.current += (long) content.length;
            ProgressHandler progressHandler = this.callBackHandler;
            if (progressHandler != null && !progressHandler.updateProgress(this.total, this.current, false)) {
                throw new Callback.CancelledException("upload stopped!");
            }
        }
    }

    private void writeLine(OutputStream out, byte[]... bs) throws IOException {
        if (bs != null) {
            for (byte[] b : bs) {
                out.write(b);
            }
        }
        out.write(END_BYTES);
    }

    private void writeFile(OutputStream out, File file) throws IOException {
        if (out instanceof CounterOutputStream) {
            ((CounterOutputStream) out).addFile(file);
        } else {
            writeStreamAndCloseIn(out, new FileInputStream(file));
        }
    }

    private void writeStreamAndCloseIn(OutputStream out, InputStream in) throws IOException {
        if (out instanceof CounterOutputStream) {
            ((CounterOutputStream) out).addStream(in);
            return;
        }
        try {
            byte[] buf = new byte[4096];
            while (true) {
                int read = in.read(buf);
                int len = read;
                if (read >= 0) {
                    out.write(buf, 0, len);
                    this.current += (long) len;
                    if (this.callBackHandler != null) {
                        if (!this.callBackHandler.updateProgress(this.total, this.current, false)) {
                            throw new Callback.CancelledException("upload stopped!");
                        }
                    }
                } else {
                    return;
                }
            }
        } finally {
            IOUtil.closeQuietly((Closeable) in);
        }
    }

    private static byte[] buildContentDisposition(String name, String fileName, String charset2) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder("Content-Disposition: form-data");
        result.append("; name=\"");
        result.append(name.replace("\"", "\\\""));
        result.append("\"");
        if (!TextUtils.isEmpty(fileName)) {
            result.append("; filename=\"");
            result.append(fileName.replace("\"", "\\\""));
            result.append("\"");
        }
        return result.toString().getBytes(charset2);
    }

    private static byte[] buildContentType(Object value, String contentType2, String charset2) throws UnsupportedEncodingException {
        String contentType3;
        StringBuilder result = new StringBuilder("Content-Type: ");
        if (!TextUtils.isEmpty(contentType2)) {
            contentType3 = contentType2.replaceFirst("\\/jpg$", "/jpeg");
        } else if (value instanceof String) {
            contentType3 = "text/plain; charset=" + charset2;
        } else {
            contentType3 = "application/octet-stream";
        }
        result.append(contentType3);
        return result.toString().getBytes(charset2);
    }

    private class CounterOutputStream extends OutputStream {
        final AtomicLong total = new AtomicLong(0);

        public CounterOutputStream() {
        }

        public void addFile(File file) {
            if (this.total.get() != -1) {
                this.total.addAndGet(file.length());
            }
        }

        public void addStream(InputStream inputStream) {
            if (this.total.get() != -1) {
                long length = InputStreamBody.getInputStreamLength(inputStream);
                if (length > 0) {
                    this.total.addAndGet(length);
                } else {
                    this.total.set(-1);
                }
            }
        }

        public void write(int oneByte) throws IOException {
            if (this.total.get() != -1) {
                this.total.incrementAndGet();
            }
        }

        public void write(byte[] buffer) throws IOException {
            if (this.total.get() != -1) {
                this.total.addAndGet((long) buffer.length);
            }
        }

        public void write(byte[] buffer, int offset, int count) throws IOException {
            if (this.total.get() != -1) {
                this.total.addAndGet((long) count);
            }
        }
    }
}

package org.xutils.http.request;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import org.xutils.http.RequestParams;

public class AssetsRequest extends ResRequest {
    public AssetsRequest(RequestParams params, Type loadType) throws Throwable {
        super(params, loadType);
    }

    public InputStream getInputStream() throws IOException {
        if (this.inputStream == null) {
            Context context = this.params.getContext();
            this.inputStream = context.getResources().getAssets().open(this.queryUrl.replace("assets://", ""));
            this.contentLength = (long) this.inputStream.available();
        }
        return this.inputStream;
    }
}

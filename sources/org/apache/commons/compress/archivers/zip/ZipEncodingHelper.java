package org.apache.commons.compress.archivers.zip;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

public abstract class ZipEncodingHelper {
    static final String UTF8 = "UTF8";
    static final ZipEncoding UTF8_ZIP_ENCODING = getZipEncoding(UTF8);

    public static ZipEncoding getZipEncoding(String name) {
        Charset cs = Charset.defaultCharset();
        if (name != null) {
            try {
                cs = Charset.forName(name);
            } catch (UnsupportedCharsetException e) {
            }
        }
        return new NioZipEncoding(cs, isUTF8(cs.name()));
    }

    static boolean isUTF8(String charsetName) {
        if (charsetName == null) {
            charsetName = Charset.defaultCharset().name();
        }
        if (StandardCharsets.UTF_8.name().equalsIgnoreCase(charsetName)) {
            return true;
        }
        for (String alias : StandardCharsets.UTF_8.aliases()) {
            if (alias.equalsIgnoreCase(charsetName)) {
                return true;
            }
        }
        return false;
    }

    static ByteBuffer growBufferBy(ByteBuffer buffer, int increment) {
        buffer.limit(buffer.position());
        buffer.rewind();
        ByteBuffer on = ByteBuffer.allocate(buffer.capacity() + increment);
        on.put(buffer);
        return on;
    }
}

package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

class NioZipEncoding implements ZipEncoding, CharsetAccessor {
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final char REPLACEMENT = '?';
    private static final byte[] REPLACEMENT_BYTES = {63};
    private static final String REPLACEMENT_STRING = String.valueOf(REPLACEMENT);
    private final Charset charset;
    private final boolean useReplacement;

    NioZipEncoding(Charset charset2, boolean useReplacement2) {
        this.charset = charset2;
        this.useReplacement = useReplacement2;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public boolean canEncode(String name) {
        return newEncoder().canEncode(name);
    }

    public ByteBuffer encode(String name) {
        CharsetEncoder enc = newEncoder();
        CharBuffer cb = CharBuffer.wrap(name);
        CharBuffer tmp = null;
        ByteBuffer out = ByteBuffer.allocate(estimateInitialBufferSize(enc, cb.remaining()));
        while (cb.hasRemaining()) {
            CoderResult res = enc.encode(cb, out, false);
            if (!res.isUnmappable() && !res.isMalformed()) {
                if (!res.isOverflow()) {
                    if (res.isUnderflow() || res.isError()) {
                        break;
                    }
                } else {
                    out = ZipEncodingHelper.growBufferBy(out, estimateIncrementalEncodingSize(enc, cb.remaining()));
                }
            } else {
                if (estimateIncrementalEncodingSize(enc, res.length() * 6) > out.remaining()) {
                    int charCount = 0;
                    for (int i = cb.position(); i < cb.limit(); i++) {
                        charCount += !enc.canEncode(cb.get(i)) ? 6 : 1;
                    }
                    out = ZipEncodingHelper.growBufferBy(out, estimateIncrementalEncodingSize(enc, charCount) - out.remaining());
                }
                if (tmp == null) {
                    tmp = CharBuffer.allocate(6);
                }
                for (int i2 = 0; i2 < res.length(); i2++) {
                    out = encodeFully(enc, encodeSurrogate(tmp, cb.get()), out);
                }
            }
        }
        enc.encode(cb, out, true);
        out.limit(out.position());
        out.rewind();
        return out;
    }

    public String decode(byte[] data) throws IOException {
        return newDecoder().decode(ByteBuffer.wrap(data)).toString();
    }

    private static ByteBuffer encodeFully(CharsetEncoder enc, CharBuffer cb, ByteBuffer out) {
        ByteBuffer o = out;
        while (cb.hasRemaining()) {
            if (enc.encode(cb, o, false).isOverflow()) {
                o = ZipEncodingHelper.growBufferBy(o, estimateIncrementalEncodingSize(enc, cb.remaining()));
            }
        }
        return o;
    }

    private static CharBuffer encodeSurrogate(CharBuffer cb, char c) {
        cb.position(0).limit(6);
        cb.put('%');
        cb.put('U');
        cb.put(HEX_CHARS[(c >> 12) & 15]);
        cb.put(HEX_CHARS[(c >> 8) & 15]);
        cb.put(HEX_CHARS[(c >> 4) & 15]);
        cb.put(HEX_CHARS[c & 15]);
        cb.flip();
        return cb;
    }

    private CharsetEncoder newEncoder() {
        if (this.useReplacement) {
            return this.charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(REPLACEMENT_BYTES);
        }
        return this.charset.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    }

    private CharsetDecoder newDecoder() {
        if (!this.useReplacement) {
            return this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        }
        return this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(REPLACEMENT_STRING);
    }

    private static int estimateInitialBufferSize(CharsetEncoder enc, int charChount) {
        return (int) Math.ceil((double) (enc.maxBytesPerChar() + (((float) (charChount - 1)) * enc.averageBytesPerChar())));
    }

    private static int estimateIncrementalEncodingSize(CharsetEncoder enc, int charCount) {
        return (int) Math.ceil((double) (((float) charCount) * enc.averageBytesPerChar()));
    }
}

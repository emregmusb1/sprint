package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.JSONLexer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import kotlin.text.Typography;
import org.apache.commons.compress.utils.CharsetNames;

public final class SerializeWriter extends Writer {
    public static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    static final char[] DigitOnes = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    static final char[] DigitTens = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'};
    static final char[] ascii_chars = {'0', '0', '0', '1', '0', '2', '0', '3', '0', '4', '0', '5', '0', '6', '0', '7', '0', '8', '0', '9', '0', 'A', '0', 'B', '0', 'C', '0', 'D', '0', 'E', '0', 'F', '1', '0', '1', '1', '1', '2', '1', '3', '1', '4', '1', '5', '1', '6', '1', '7', '1', '8', '1', '9', '1', 'A', '1', 'B', '1', 'C', '1', 'D', '1', 'E', '1', 'F', '2', '0', '2', '1', '2', '2', '2', '3', '2', '4', '2', '5', '2', '6', '2', '7', '2', '8', '2', '9', '2', 'A', '2', 'B', '2', 'C', '2', 'D', '2', 'E', '2', 'F'};
    private static final ThreadLocal<char[]> bufLocal = new ThreadLocal<>();
    static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    static final char[] replaceChars = new char[93];
    static final int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};
    static final byte[] specicalFlags_doubleQuotes = new byte[161];
    static final byte[] specicalFlags_singleQuotes = new byte[161];
    protected char[] buf;
    protected int count;
    protected int features;
    protected final Writer writer;

    static {
        byte[] bArr = specicalFlags_doubleQuotes;
        bArr[0] = 4;
        bArr[1] = 4;
        bArr[2] = 4;
        bArr[3] = 4;
        bArr[4] = 4;
        bArr[5] = 4;
        bArr[6] = 4;
        bArr[7] = 4;
        bArr[8] = 1;
        bArr[9] = 1;
        bArr[10] = 1;
        bArr[11] = 4;
        bArr[12] = 1;
        bArr[13] = 1;
        bArr[34] = 1;
        bArr[92] = 1;
        byte[] bArr2 = specicalFlags_singleQuotes;
        bArr2[0] = 4;
        bArr2[1] = 4;
        bArr2[2] = 4;
        bArr2[3] = 4;
        bArr2[4] = 4;
        bArr2[5] = 4;
        bArr2[6] = 4;
        bArr2[7] = 4;
        bArr2[8] = 1;
        bArr2[9] = 1;
        bArr2[10] = 1;
        bArr2[11] = 4;
        bArr2[12] = 1;
        bArr2[13] = 1;
        bArr2[92] = 1;
        bArr2[39] = 1;
        for (int i = 14; i <= 31; i++) {
            specicalFlags_doubleQuotes[i] = 4;
            specicalFlags_singleQuotes[i] = 4;
        }
        for (int i2 = 127; i2 <= 160; i2++) {
            specicalFlags_doubleQuotes[i2] = 4;
            specicalFlags_singleQuotes[i2] = 4;
        }
        char[] cArr = replaceChars;
        cArr[0] = '0';
        cArr[1] = '1';
        cArr[2] = '2';
        cArr[3] = '3';
        cArr[4] = '4';
        cArr[5] = '5';
        cArr[6] = '6';
        cArr[7] = '7';
        cArr[8] = 'b';
        cArr[9] = 't';
        cArr[10] = 'n';
        cArr[11] = 'v';
        cArr[12] = 'f';
        cArr[13] = 'r';
        cArr[34] = Typography.quote;
        cArr[39] = '\'';
        cArr[47] = '/';
        cArr[92] = '\\';
    }

    public SerializeWriter() {
        this((Writer) null);
    }

    public SerializeWriter(Writer writer2) {
        this.writer = writer2;
        this.features = JSON.DEFAULT_GENERATE_FEATURE;
        this.buf = bufLocal.get();
        ThreadLocal<char[]> threadLocal = bufLocal;
        if (threadLocal != null) {
            threadLocal.set((Object) null);
        }
        if (this.buf == null) {
            this.buf = new char[1024];
        }
    }

    public SerializeWriter(SerializerFeature... features2) {
        this((Writer) null, 0, features2);
    }

    public SerializeWriter(Writer writer2, int featuresValue, SerializerFeature[] features2) {
        this.writer = writer2;
        this.buf = bufLocal.get();
        if (this.buf != null) {
            bufLocal.set((Object) null);
        }
        if (this.buf == null) {
            this.buf = new char[1024];
        }
        for (SerializerFeature feature : features2) {
            featuresValue |= feature.mask;
        }
        this.features = featuresValue;
    }

    public SerializeWriter(int initialSize) {
        this((Writer) null, initialSize);
    }

    public SerializeWriter(Writer writer2, int initialSize) {
        this.writer = writer2;
        if (initialSize > 0) {
            this.buf = new char[initialSize];
            return;
        }
        throw new IllegalArgumentException("Negative initial size: " + initialSize);
    }

    public void config(SerializerFeature feature, boolean state) {
        if (state) {
            this.features |= feature.mask;
        } else {
            this.features &= ~feature.mask;
        }
    }

    public boolean isEnabled(SerializerFeature feature) {
        return (this.features & feature.mask) != 0;
    }

    public void write(int c) {
        int newcount = this.count + 1;
        if (newcount > this.buf.length) {
            if (this.writer == null) {
                expandCapacity(newcount);
            } else {
                flush();
                newcount = 1;
            }
        }
        this.buf[this.count] = (char) c;
        this.count = newcount;
    }

    public void write(char[] c, int off, int len) {
        if (off < 0 || off > c.length || len < 0 || off + len > c.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len != 0) {
            int newcount = this.count + len;
            if (newcount > this.buf.length) {
                if (this.writer == null) {
                    expandCapacity(newcount);
                } else {
                    do {
                        char[] cArr = this.buf;
                        int length = cArr.length;
                        int i = this.count;
                        int rest = length - i;
                        System.arraycopy(c, off, cArr, i, rest);
                        this.count = this.buf.length;
                        flush();
                        len -= rest;
                        off += rest;
                    } while (len > this.buf.length);
                    newcount = len;
                }
            }
            System.arraycopy(c, off, this.buf, this.count, len);
            this.count = newcount;
        }
    }

    /* access modifiers changed from: protected */
    public void expandCapacity(int minimumCapacity) {
        int newCapacity = ((this.buf.length * 3) / 2) + 1;
        if (newCapacity < minimumCapacity) {
            newCapacity = minimumCapacity;
        }
        char[] newValue = new char[newCapacity];
        System.arraycopy(this.buf, 0, newValue, 0, this.count);
        this.buf = newValue;
    }

    public void write(String str, int off, int len) {
        int newcount = this.count + len;
        if (newcount > this.buf.length) {
            if (this.writer == null) {
                expandCapacity(newcount);
            } else {
                do {
                    char[] cArr = this.buf;
                    int length = cArr.length;
                    int i = this.count;
                    int rest = length - i;
                    str.getChars(off, off + rest, cArr, i);
                    this.count = this.buf.length;
                    flush();
                    len -= rest;
                    off += rest;
                } while (len > this.buf.length);
                newcount = len;
            }
        }
        str.getChars(off, off + len, this.buf, this.count);
        this.count = newcount;
    }

    public void writeTo(Writer out) throws IOException {
        if (this.writer == null) {
            out.write(this.buf, 0, this.count);
            return;
        }
        throw new UnsupportedOperationException("writer not null");
    }

    public void writeTo(OutputStream out, String charsetName) throws IOException {
        writeTo(out, Charset.forName(charsetName));
    }

    public void writeTo(OutputStream out, Charset charset) throws IOException {
        if (this.writer == null) {
            out.write(new String(this.buf, 0, this.count).getBytes(charset.name()));
            return;
        }
        throw new UnsupportedOperationException("writer not null");
    }

    public SerializeWriter append(CharSequence csq) {
        String s = csq == null ? "null" : csq.toString();
        write(s, 0, s.length());
        return this;
    }

    public SerializeWriter append(CharSequence csq, int start, int end) {
        String s = (csq == null ? "null" : csq).subSequence(start, end).toString();
        write(s, 0, s.length());
        return this;
    }

    public SerializeWriter append(char c) {
        write((int) c);
        return this;
    }

    public byte[] toBytes(String charsetName) {
        if (this.writer == null) {
            if (charsetName == null) {
                charsetName = CharsetNames.UTF_8;
            }
            try {
                return new String(this.buf, 0, this.count).getBytes(charsetName);
            } catch (UnsupportedEncodingException e) {
                throw new JSONException("toBytes error", e);
            }
        } else {
            throw new UnsupportedOperationException("writer not null");
        }
    }

    public String toString() {
        return new String(this.buf, 0, this.count);
    }

    public void close() {
        if (this.writer != null && this.count > 0) {
            flush();
        }
        char[] cArr = this.buf;
        if (cArr.length <= 8192) {
            bufLocal.set(cArr);
        }
        this.buf = null;
    }

    public void write(String text) {
        if (text == null) {
            writeNull();
        } else {
            write(text, 0, text.length());
        }
    }

    public void writeInt(int i) {
        if (i == Integer.MIN_VALUE) {
            write("-2147483648");
            return;
        }
        int j = 0;
        while ((i < 0 ? -i : i) > sizeTable[j]) {
            j++;
        }
        int size = j + 1;
        if (i < 0) {
            size++;
        }
        int newcount = this.count + size;
        if (newcount > this.buf.length) {
            if (this.writer == null) {
                expandCapacity(newcount);
            } else {
                char[] chars = new char[size];
                getChars((long) i, size, chars);
                write(chars, 0, chars.length);
                return;
            }
        }
        getChars((long) i, newcount, this.buf);
        this.count = newcount;
    }

    public void writeByteArray(byte[] bytes) {
        byte[] bArr = bytes;
        int bytesLen = bArr.length;
        boolean singleQuote = (this.features & SerializerFeature.UseSingleQuotes.mask) != 0;
        char quote = singleQuote ? '\'' : Typography.quote;
        if (bytesLen == 0) {
            write(singleQuote ? "''" : "\"\"");
            return;
        }
        char[] CA = JSONLexer.CA;
        int eLen = (bytesLen / 3) * 3;
        int offset = this.count;
        int newcount = this.count + ((((bytesLen - 1) / 3) + 1) << 2) + 2;
        if (newcount > this.buf.length) {
            if (this.writer != null) {
                write((int) quote);
                int i = 0;
                while (i < eLen) {
                    int s = i + 1;
                    int s2 = s + 1;
                    int i2 = ((bArr[i] & 255) << 16) | ((bArr[s] & 255) << 8) | (bArr[s2] & 255);
                    write((int) CA[(i2 >>> 18) & 63]);
                    write((int) CA[(i2 >>> 12) & 63]);
                    write((int) CA[(i2 >>> 6) & 63]);
                    write((int) CA[i2 & 63]);
                    i = s2 + 1;
                }
                int left = bytesLen - eLen;
                if (left > 0) {
                    int i3 = (left == 2 ? (bArr[bytesLen - 1] & 255) << 2 : 0) | ((bArr[eLen] & 255) << 10);
                    write((int) CA[i3 >> 12]);
                    write((int) CA[(i3 >>> 6) & 63]);
                    write((int) left == 2 ? CA[i3 & 63] : '=');
                    write(61);
                }
                write((int) quote);
                return;
            }
            expandCapacity(newcount);
        }
        this.count = newcount;
        int offset2 = offset + 1;
        this.buf[offset] = quote;
        int s3 = 0;
        int d = offset2;
        while (s3 < eLen) {
            int s4 = s3 + 1;
            int s5 = s4 + 1;
            int i4 = ((bArr[s3] & 255) << 16) | ((bArr[s4] & 255) << 8);
            int s6 = s5 + 1;
            int i5 = (bArr[s5] & 255) | i4;
            char[] cArr = this.buf;
            int d2 = d + 1;
            cArr[d] = CA[(i5 >>> 18) & 63];
            int d3 = d2 + 1;
            cArr[d2] = CA[(i5 >>> 12) & 63];
            int d4 = d3 + 1;
            cArr[d3] = CA[(i5 >>> 6) & 63];
            d = d4 + 1;
            cArr[d4] = CA[i5 & 63];
            s3 = s6;
        }
        int left2 = bytesLen - eLen;
        if (left2 > 0) {
            int i6 = ((bArr[eLen] & 255) << 10) | (left2 == 2 ? (bArr[bytesLen - 1] & 255) << 2 : 0);
            char[] cArr2 = this.buf;
            cArr2[newcount - 5] = CA[i6 >> 12];
            cArr2[newcount - 4] = CA[(i6 >>> 6) & 63];
            cArr2[newcount - 3] = left2 == 2 ? CA[i6 & 63] : '=';
            this.buf[newcount - 2] = '=';
        }
        this.buf[newcount - 1] = quote;
    }

    public void writeLong(long i) {
        if (i == Long.MIN_VALUE) {
            write("-9223372036854775808");
            return;
        }
        long val = i < 0 ? -i : i;
        int size = 0;
        long p = 10;
        int j = 1;
        while (true) {
            if (j >= 19) {
                break;
            } else if (val < p) {
                size = j;
                break;
            } else {
                p *= 10;
                j++;
            }
        }
        if (size == 0) {
            size = 19;
        }
        if (i < 0) {
            size++;
        }
        int newcount = this.count + size;
        if (newcount > this.buf.length) {
            if (this.writer == null) {
                expandCapacity(newcount);
            } else {
                char[] chars = new char[size];
                getChars(i, size, chars);
                write(chars, 0, chars.length);
                return;
            }
        }
        getChars(i, newcount, this.buf);
        this.count = newcount;
    }

    public void writeNull() {
        write("null");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x03c4, code lost:
        if ((com.alibaba.fastjson.serializer.SerializerFeature.WriteSlashAsSpecial.mask & r0.features) != 0) goto L_0x03c6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeStringWithDoubleQuote(java.lang.String r24, char r25, boolean r26) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            r2 = r25
            if (r1 != 0) goto L_0x0011
            r23.writeNull()
            if (r2 == 0) goto L_0x0010
            r0.write((int) r2)
        L_0x0010:
            return
        L_0x0011:
            int r3 = r24.length()
            int r4 = r0.count
            int r4 = r4 + r3
            int r4 = r4 + 2
            if (r2 == 0) goto L_0x001e
            int r4 = r4 + 1
        L_0x001e:
            char[] r5 = r0.buf
            int r5 = r5.length
            r7 = 32
            r9 = 47
            r10 = 117(0x75, float:1.64E-43)
            r11 = 12
            r12 = 8
            r13 = 34
            r14 = 92
            if (r4 <= r5) goto L_0x00fc
            java.io.Writer r5 = r0.writer
            if (r5 == 0) goto L_0x00f9
            r0.write((int) r13)
            r5 = 0
        L_0x0039:
            int r6 = r24.length()
            if (r5 >= r6) goto L_0x00f0
            char r6 = r1.charAt(r5)
            int r15 = r0.features
            com.alibaba.fastjson.serializer.SerializerFeature r8 = com.alibaba.fastjson.serializer.SerializerFeature.BrowserCompatible
            int r8 = r8.mask
            r8 = r8 & r15
            if (r8 == 0) goto L_0x00ca
            if (r6 == r12) goto L_0x00bf
            if (r6 == r11) goto L_0x00bf
            r8 = 10
            if (r6 == r8) goto L_0x00bf
            r8 = 13
            if (r6 == r8) goto L_0x00bf
            r8 = 9
            if (r6 == r8) goto L_0x00bf
            if (r6 == r13) goto L_0x00bf
            if (r6 == r9) goto L_0x00bf
            if (r6 != r14) goto L_0x0063
            goto L_0x00bf
        L_0x0063:
            if (r6 >= r7) goto L_0x008a
            r0.write((int) r14)
            r0.write((int) r10)
            r8 = 48
            r0.write((int) r8)
            r0.write((int) r8)
            char[] r8 = ascii_chars
            int r15 = r6 * 2
            char r8 = r8[r15]
            r0.write((int) r8)
            char[] r8 = ascii_chars
            int r15 = r6 * 2
            r17 = 1
            int r15 = r15 + 1
            char r8 = r8[r15]
            r0.write((int) r8)
            goto L_0x00ec
        L_0x008a:
            r8 = 127(0x7f, float:1.78E-43)
            if (r6 < r8) goto L_0x00e9
            r0.write((int) r14)
            r0.write((int) r10)
            char[] r8 = DIGITS
            int r15 = r6 >>> 12
            r15 = r15 & 15
            char r8 = r8[r15]
            r0.write((int) r8)
            char[] r8 = DIGITS
            int r15 = r6 >>> 8
            r15 = r15 & 15
            char r8 = r8[r15]
            r0.write((int) r8)
            char[] r8 = DIGITS
            int r15 = r6 >>> 4
            r15 = r15 & 15
            char r8 = r8[r15]
            r0.write((int) r8)
            char[] r8 = DIGITS
            r15 = r6 & 15
            char r8 = r8[r15]
            r0.write((int) r8)
            goto L_0x00ec
        L_0x00bf:
            r0.write((int) r14)
            char[] r8 = replaceChars
            char r8 = r8[r6]
            r0.write((int) r8)
            goto L_0x00ec
        L_0x00ca:
            byte[] r8 = specicalFlags_doubleQuotes
            int r15 = r8.length
            if (r6 >= r15) goto L_0x00d3
            byte r8 = r8[r6]
            if (r8 != 0) goto L_0x00de
        L_0x00d3:
            if (r6 != r9) goto L_0x00e9
            int r8 = r0.features
            com.alibaba.fastjson.serializer.SerializerFeature r15 = com.alibaba.fastjson.serializer.SerializerFeature.WriteSlashAsSpecial
            int r15 = r15.mask
            r8 = r8 & r15
            if (r8 == 0) goto L_0x00e9
        L_0x00de:
            r0.write((int) r14)
            char[] r8 = replaceChars
            char r8 = r8[r6]
            r0.write((int) r8)
            goto L_0x00ec
        L_0x00e9:
            r0.write((int) r6)
        L_0x00ec:
            int r5 = r5 + 1
            goto L_0x0039
        L_0x00f0:
            r0.write((int) r13)
            if (r2 == 0) goto L_0x00f8
            r0.write((int) r2)
        L_0x00f8:
            return
        L_0x00f9:
            r0.expandCapacity(r4)
        L_0x00fc:
            int r5 = r0.count
            int r6 = r5 + 1
            int r8 = r6 + r3
            char[] r15 = r0.buf
            r15[r5] = r13
            r5 = 0
            r1.getChars(r5, r3, r15, r6)
            r0.count = r4
            int r5 = r0.features
            com.alibaba.fastjson.serializer.SerializerFeature r15 = com.alibaba.fastjson.serializer.SerializerFeature.BrowserCompatible
            int r15 = r15.mask
            r5 = r5 & r15
            if (r5 == 0) goto L_0x0264
            r5 = -1
            r15 = r6
        L_0x0117:
            if (r15 >= r8) goto L_0x0152
            char[] r10 = r0.buf
            char r10 = r10[r15]
            if (r10 == r13) goto L_0x0147
            if (r10 == r9) goto L_0x0147
            if (r10 != r14) goto L_0x0124
            goto L_0x0147
        L_0x0124:
            if (r10 == r12) goto L_0x0143
            if (r10 == r11) goto L_0x0143
            r14 = 10
            if (r10 == r14) goto L_0x0143
            r14 = 13
            if (r10 == r14) goto L_0x0143
            r14 = 9
            if (r10 != r14) goto L_0x0135
            goto L_0x0143
        L_0x0135:
            if (r10 >= r7) goto L_0x013b
            r5 = r15
            int r4 = r4 + 5
            goto L_0x014b
        L_0x013b:
            r14 = 127(0x7f, float:1.78E-43)
            if (r10 < r14) goto L_0x014b
            r5 = r15
            int r4 = r4 + 5
            goto L_0x014b
        L_0x0143:
            r5 = r15
            int r4 = r4 + 1
            goto L_0x014b
        L_0x0147:
            r5 = r15
            int r4 = r4 + 1
        L_0x014b:
            int r15 = r15 + 1
            r10 = 117(0x75, float:1.64E-43)
            r14 = 92
            goto L_0x0117
        L_0x0152:
            char[] r10 = r0.buf
            int r10 = r10.length
            if (r4 <= r10) goto L_0x015a
            r0.expandCapacity(r4)
        L_0x015a:
            r0.count = r4
            r10 = r5
        L_0x015d:
            if (r10 < r6) goto L_0x024c
            char[] r14 = r0.buf
            char r15 = r14[r10]
            if (r15 == r12) goto L_0x0222
            if (r15 == r11) goto L_0x0222
            r11 = 10
            if (r15 == r11) goto L_0x0222
            r11 = 13
            if (r15 == r11) goto L_0x0222
            r11 = 9
            if (r15 != r11) goto L_0x0175
            goto L_0x0222
        L_0x0175:
            if (r15 == r13) goto L_0x0206
            if (r15 == r9) goto L_0x0206
            r11 = 92
            if (r15 != r11) goto L_0x017f
            goto L_0x0206
        L_0x017f:
            if (r15 >= r7) goto L_0x01be
            int r11 = r10 + 1
            int r12 = r10 + 6
            int r22 = r8 - r10
            r17 = 1
            int r9 = r22 + -1
            java.lang.System.arraycopy(r14, r11, r14, r12, r9)
            char[] r9 = r0.buf
            r11 = 92
            r9[r10] = r11
            int r11 = r10 + 1
            r12 = 117(0x75, float:1.64E-43)
            r9[r11] = r12
            int r11 = r10 + 2
            r12 = 48
            r9[r11] = r12
            int r11 = r10 + 3
            r9[r11] = r12
            int r11 = r10 + 4
            char[] r12 = ascii_chars
            int r14 = r15 * 2
            char r14 = r12[r14]
            r9[r11] = r14
            int r11 = r10 + 5
            int r14 = r15 * 2
            r17 = 1
            int r14 = r14 + 1
            char r12 = r12[r14]
            r9[r11] = r12
            int r8 = r8 + 5
            goto L_0x0242
        L_0x01be:
            r9 = 127(0x7f, float:1.78E-43)
            if (r15 < r9) goto L_0x0242
            int r9 = r10 + 1
            int r11 = r10 + 6
            int r12 = r8 - r10
            r17 = 1
            int r12 = r12 + -1
            java.lang.System.arraycopy(r14, r9, r14, r11, r12)
            char[] r9 = r0.buf
            r11 = 92
            r9[r10] = r11
            int r11 = r10 + 1
            r12 = 117(0x75, float:1.64E-43)
            r9[r11] = r12
            int r11 = r10 + 2
            char[] r12 = DIGITS
            int r14 = r15 >>> 12
            r14 = r14 & 15
            char r14 = r12[r14]
            r9[r11] = r14
            int r11 = r10 + 3
            int r14 = r15 >>> 8
            r14 = r14 & 15
            char r14 = r12[r14]
            r9[r11] = r14
            int r11 = r10 + 4
            int r14 = r15 >>> 4
            r14 = r14 & 15
            char r14 = r12[r14]
            r9[r11] = r14
            int r11 = r10 + 5
            r14 = r15 & 15
            char r12 = r12[r14]
            r9[r11] = r12
            int r8 = r8 + 5
            goto L_0x0242
        L_0x0206:
            char[] r9 = r0.buf
            int r11 = r10 + 1
            int r12 = r10 + 2
            int r14 = r8 - r10
            r17 = 1
            int r14 = r14 + -1
            java.lang.System.arraycopy(r9, r11, r9, r12, r14)
            char[] r9 = r0.buf
            r11 = 92
            r9[r10] = r11
            int r11 = r10 + 1
            r9[r11] = r15
            int r8 = r8 + 1
            goto L_0x0242
        L_0x0222:
            char[] r9 = r0.buf
            int r11 = r10 + 1
            int r12 = r10 + 2
            int r14 = r8 - r10
            r17 = 1
            int r14 = r14 + -1
            java.lang.System.arraycopy(r9, r11, r9, r12, r14)
            char[] r9 = r0.buf
            r11 = 92
            r9[r10] = r11
            int r11 = r10 + 1
            char[] r12 = replaceChars
            char r12 = r12[r15]
            r9[r11] = r12
            int r8 = r8 + 1
        L_0x0242:
            int r10 = r10 + -1
            r9 = 47
            r11 = 12
            r12 = 8
            goto L_0x015d
        L_0x024c:
            if (r2 == 0) goto L_0x025b
            char[] r7 = r0.buf
            int r9 = r0.count
            int r10 = r9 + -2
            r7[r10] = r13
            r10 = 1
            int r9 = r9 - r10
            r7[r9] = r2
            goto L_0x0263
        L_0x025b:
            r10 = 1
            char[] r7 = r0.buf
            int r9 = r0.count
            int r9 = r9 - r10
            r7[r9] = r13
        L_0x0263:
            return
        L_0x0264:
            r5 = 0
            r9 = -1
            r10 = -1
            r11 = 0
            if (r26 == 0) goto L_0x046b
            r12 = r6
        L_0x026b:
            if (r12 >= r8) goto L_0x02e6
            char[] r15 = r0.buf
            char r15 = r15[r12]
            r14 = 8232(0x2028, float:1.1535E-41)
            r13 = -1
            if (r15 != r14) goto L_0x0281
            int r5 = r5 + 1
            r9 = r12
            r11 = r15
            int r4 = r4 + 4
            if (r10 != r13) goto L_0x02df
            r10 = r12
            goto L_0x02df
        L_0x0281:
            r14 = 93
            if (r15 < r14) goto L_0x0297
            r14 = 127(0x7f, float:1.78E-43)
            if (r15 < r14) goto L_0x02df
            r14 = 160(0xa0, float:2.24E-43)
            if (r15 > r14) goto L_0x02df
            if (r10 != r13) goto L_0x0290
            r10 = r12
        L_0x0290:
            int r5 = r5 + 1
            r9 = r12
            r11 = r15
            int r4 = r4 + 4
            goto L_0x02df
        L_0x0297:
            if (r15 != r7) goto L_0x029b
            r14 = 0
            goto L_0x02c4
        L_0x029b:
            r14 = 47
            if (r15 != r14) goto L_0x02aa
            int r14 = r0.features
            com.alibaba.fastjson.serializer.SerializerFeature r7 = com.alibaba.fastjson.serializer.SerializerFeature.WriteSlashAsSpecial
            int r7 = r7.mask
            r7 = r7 & r14
            if (r7 == 0) goto L_0x02aa
            r14 = 1
            goto L_0x02c4
        L_0x02aa:
            r7 = 35
            if (r15 <= r7) goto L_0x02b4
            r7 = 92
            if (r15 == r7) goto L_0x02b4
            r14 = 0
            goto L_0x02c4
        L_0x02b4:
            r7 = 31
            if (r15 <= r7) goto L_0x02c3
            r7 = 92
            if (r15 == r7) goto L_0x02c3
            r7 = 34
            if (r15 != r7) goto L_0x02c1
            goto L_0x02c3
        L_0x02c1:
            r14 = 0
            goto L_0x02c4
        L_0x02c3:
            r14 = 1
        L_0x02c4:
            if (r14 == 0) goto L_0x02df
            int r5 = r5 + 1
            r7 = r12
            r9 = r15
            byte[] r11 = specicalFlags_doubleQuotes
            int r13 = r11.length
            if (r15 >= r13) goto L_0x02d6
            byte r11 = r11[r15]
            r13 = 4
            if (r11 != r13) goto L_0x02d6
            int r4 = r4 + 4
        L_0x02d6:
            r11 = -1
            if (r10 != r11) goto L_0x02dd
            r10 = r12
            r11 = r9
            r9 = r7
            goto L_0x02df
        L_0x02dd:
            r11 = r9
            r9 = r7
        L_0x02df:
            int r12 = r12 + 1
            r7 = 32
            r13 = 34
            goto L_0x026b
        L_0x02e6:
            if (r5 <= 0) goto L_0x0468
            int r4 = r4 + r5
            char[] r7 = r0.buf
            int r7 = r7.length
            if (r4 <= r7) goto L_0x02f1
            r0.expandCapacity(r4)
        L_0x02f1:
            r0.count = r4
            r7 = 1
            if (r5 != r7) goto L_0x039d
            r7 = 8232(0x2028, float:1.1535E-41)
            if (r11 != r7) goto L_0x032c
            int r7 = r9 + 1
            int r12 = r9 + 6
            int r13 = r8 - r9
            r14 = 1
            int r13 = r13 - r14
            char[] r14 = r0.buf
            java.lang.System.arraycopy(r14, r7, r14, r12, r13)
            char[] r14 = r0.buf
            r15 = 92
            r14[r9] = r15
            int r9 = r9 + 1
            r15 = 117(0x75, float:1.64E-43)
            r14[r9] = r15
            r15 = 1
            int r9 = r9 + r15
            r16 = 50
            r14[r9] = r16
            int r9 = r9 + r15
            r16 = 48
            r14[r9] = r16
            int r9 = r9 + r15
            r16 = 50
            r14[r9] = r16
            int r9 = r9 + r15
            r15 = 56
            r14[r9] = r15
            r16 = r3
            goto L_0x046d
        L_0x032c:
            r7 = r11
            byte[] r12 = specicalFlags_doubleQuotes
            int r13 = r12.length
            if (r7 >= r13) goto L_0x037e
            byte r12 = r12[r7]
            r13 = 4
            if (r12 != r13) goto L_0x037e
            int r12 = r9 + 1
            int r13 = r9 + 6
            int r14 = r8 - r9
            r15 = 1
            int r14 = r14 - r15
            char[] r15 = r0.buf
            java.lang.System.arraycopy(r15, r12, r15, r13, r14)
            r15 = r9
            r16 = r3
            char[] r3 = r0.buf
            int r18 = r15 + 1
            r20 = 92
            r3[r15] = r20
            int r15 = r18 + 1
            r19 = 117(0x75, float:1.64E-43)
            r3[r18] = r19
            int r18 = r15 + 1
            char[] r19 = DIGITS
            int r20 = r7 >>> 12
            r20 = r20 & 15
            char r20 = r19[r20]
            r3[r15] = r20
            int r15 = r18 + 1
            int r20 = r7 >>> 8
            r20 = r20 & 15
            char r20 = r19[r20]
            r3[r18] = r20
            int r18 = r15 + 1
            int r20 = r7 >>> 4
            r20 = r20 & 15
            char r20 = r19[r20]
            r3[r15] = r20
            int r15 = r18 + 1
            r20 = r7 & 15
            char r19 = r19[r20]
            r3[r18] = r19
            goto L_0x039b
        L_0x037e:
            r16 = r3
            int r3 = r9 + 1
            int r12 = r9 + 2
            int r13 = r8 - r9
            r14 = 1
            int r13 = r13 - r14
            char[] r14 = r0.buf
            java.lang.System.arraycopy(r14, r3, r14, r12, r13)
            char[] r14 = r0.buf
            r15 = 92
            r14[r9] = r15
            int r9 = r9 + 1
            char[] r15 = replaceChars
            char r15 = r15[r7]
            r14[r9] = r15
        L_0x039b:
            goto L_0x046d
        L_0x039d:
            r16 = r3
            r3 = 1
            if (r5 <= r3) goto L_0x046d
            int r3 = r10 - r6
            r7 = r10
            r12 = r3
        L_0x03a6:
            int r13 = r24.length()
            if (r12 >= r13) goto L_0x046d
            char r13 = r1.charAt(r12)
            byte[] r14 = specicalFlags_doubleQuotes
            int r15 = r14.length
            if (r13 >= r15) goto L_0x03b9
            byte r14 = r14[r13]
            if (r14 != 0) goto L_0x03c6
        L_0x03b9:
            r14 = 47
            if (r13 != r14) goto L_0x0419
            int r15 = r0.features
            com.alibaba.fastjson.serializer.SerializerFeature r14 = com.alibaba.fastjson.serializer.SerializerFeature.WriteSlashAsSpecial
            int r14 = r14.mask
            r14 = r14 & r15
            if (r14 == 0) goto L_0x0419
        L_0x03c6:
            char[] r14 = r0.buf
            int r15 = r7 + 1
            r18 = 92
            r14[r7] = r18
            byte[] r7 = specicalFlags_doubleQuotes
            byte r7 = r7[r13]
            r1 = 4
            if (r7 != r1) goto L_0x040a
            int r7 = r15 + 1
            r18 = 117(0x75, float:1.64E-43)
            r14[r15] = r18
            int r15 = r7 + 1
            char[] r18 = DIGITS
            int r21 = r13 >>> 12
            r21 = r21 & 15
            char r21 = r18[r21]
            r14[r7] = r21
            int r7 = r15 + 1
            int r21 = r13 >>> 8
            r21 = r21 & 15
            char r21 = r18[r21]
            r14[r15] = r21
            int r15 = r7 + 1
            int r21 = r13 >>> 4
            r21 = r21 & 15
            char r21 = r18[r21]
            r14[r7] = r21
            int r7 = r15 + 1
            r21 = r13 & 15
            char r18 = r18[r21]
            r14[r15] = r18
            int r8 = r8 + 5
            r18 = 92
            r19 = 117(0x75, float:1.64E-43)
            goto L_0x0462
        L_0x040a:
            int r7 = r15 + 1
            char[] r18 = replaceChars
            char r18 = r18[r13]
            r14[r15] = r18
            int r8 = r8 + 1
            r18 = 92
            r19 = 117(0x75, float:1.64E-43)
            goto L_0x0462
        L_0x0419:
            r1 = 4
            r14 = 8232(0x2028, float:1.1535E-41)
            if (r13 != r14) goto L_0x0457
            char[] r14 = r0.buf
            int r15 = r7 + 1
            r18 = 92
            r14[r7] = r18
            int r7 = r15 + 1
            r19 = 117(0x75, float:1.64E-43)
            r14[r15] = r19
            int r15 = r7 + 1
            char[] r20 = DIGITS
            int r21 = r13 >>> 12
            r21 = r21 & 15
            char r21 = r20[r21]
            r14[r7] = r21
            int r7 = r15 + 1
            int r21 = r13 >>> 8
            r21 = r21 & 15
            char r21 = r20[r21]
            r14[r15] = r21
            int r15 = r7 + 1
            int r21 = r13 >>> 4
            r21 = r21 & 15
            char r21 = r20[r21]
            r14[r7] = r21
            int r7 = r15 + 1
            r21 = r13 & 15
            char r20 = r20[r21]
            r14[r15] = r20
            int r8 = r8 + 5
            goto L_0x0462
        L_0x0457:
            r18 = 92
            r19 = 117(0x75, float:1.64E-43)
            char[] r14 = r0.buf
            int r15 = r7 + 1
            r14[r7] = r13
            r7 = r15
        L_0x0462:
            int r12 = r12 + 1
            r1 = r24
            goto L_0x03a6
        L_0x0468:
            r16 = r3
            goto L_0x046d
        L_0x046b:
            r16 = r3
        L_0x046d:
            if (r2 == 0) goto L_0x047e
            char[] r1 = r0.buf
            int r3 = r0.count
            int r7 = r3 + -2
            r12 = 34
            r1[r7] = r12
            r7 = 1
            int r3 = r3 - r7
            r1[r3] = r2
            goto L_0x0488
        L_0x047e:
            r7 = 1
            r12 = 34
            char[] r1 = r0.buf
            int r3 = r0.count
            int r3 = r3 - r7
            r1[r3] = r12
        L_0x0488:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.serializer.SerializeWriter.writeStringWithDoubleQuote(java.lang.String, char, boolean):void");
    }

    public void write(boolean value) {
        write(value ? "true" : "false");
    }

    public void writeString(String text) {
        if ((this.features & SerializerFeature.UseSingleQuotes.mask) != 0) {
            writeStringWithSingleQuote(text);
        } else {
            writeStringWithDoubleQuote(text, 0, true);
        }
    }

    /* access modifiers changed from: protected */
    public void writeStringWithSingleQuote(String text) {
        String str = text;
        if (str == null) {
            int newcount = this.count + 4;
            if (newcount > this.buf.length) {
                expandCapacity(newcount);
            }
            "null".getChars(0, 4, this.buf, this.count);
            this.count = newcount;
            return;
        }
        int newcount2 = text.length();
        int newcount3 = this.count + newcount2 + 2;
        char c = '/';
        char c2 = 13;
        char c3 = '\\';
        if (newcount3 > this.buf.length) {
            if (this.writer != null) {
                write(39);
                for (int i = 0; i < text.length(); i++) {
                    char ch = str.charAt(i);
                    if (ch <= 13 || ch == '\\' || ch == '\'' || (ch == '/' && (this.features & SerializerFeature.WriteSlashAsSpecial.mask) != 0)) {
                        write(92);
                        write((int) replaceChars[ch]);
                    } else {
                        write((int) ch);
                    }
                }
                write(39);
                return;
            }
            expandCapacity(newcount3);
        }
        int i2 = this.count;
        int start = i2 + 1;
        int end = start + newcount2;
        char[] cArr = this.buf;
        cArr[i2] = '\'';
        str.getChars(0, newcount2, cArr, start);
        this.count = newcount3;
        int specialCount = 0;
        int lastSpecialIndex = -1;
        char lastSpecial = 0;
        int i3 = start;
        while (i3 < end) {
            char ch2 = this.buf[i3];
            if (!(ch2 <= 13 || ch2 == '\\' || ch2 == '\'')) {
                if (ch2 == c) {
                    if ((SerializerFeature.WriteSlashAsSpecial.mask & this.features) == 0) {
                    }
                }
                i3++;
                c = '/';
            }
            specialCount++;
            lastSpecialIndex = i3;
            lastSpecial = ch2;
            i3++;
            c = '/';
        }
        int newcount4 = newcount3 + specialCount;
        if (newcount4 > this.buf.length) {
            expandCapacity(newcount4);
        }
        this.count = newcount4;
        if (specialCount == 1) {
            char[] cArr2 = this.buf;
            System.arraycopy(cArr2, lastSpecialIndex + 1, cArr2, lastSpecialIndex + 2, (end - lastSpecialIndex) - 1);
            char[] cArr3 = this.buf;
            cArr3[lastSpecialIndex] = '\\';
            cArr3[lastSpecialIndex + 1] = replaceChars[lastSpecial];
        } else if (specialCount > 1) {
            char[] cArr4 = this.buf;
            System.arraycopy(cArr4, lastSpecialIndex + 1, cArr4, lastSpecialIndex + 2, (end - lastSpecialIndex) - 1);
            char[] cArr5 = this.buf;
            cArr5[lastSpecialIndex] = '\\';
            int lastSpecialIndex2 = lastSpecialIndex + 1;
            cArr5[lastSpecialIndex2] = replaceChars[lastSpecial];
            int end2 = end + 1;
            int i4 = lastSpecialIndex2 - 2;
            while (i4 >= start) {
                char ch3 = this.buf[i4];
                if (ch3 > c2 && ch3 != c3 && ch3 != '\'') {
                    if (ch3 == '/') {
                        if ((SerializerFeature.WriteSlashAsSpecial.mask & this.features) == 0) {
                        }
                    }
                    i4--;
                    c2 = 13;
                }
                char[] cArr6 = this.buf;
                System.arraycopy(cArr6, i4 + 1, cArr6, i4 + 2, (end2 - i4) - 1);
                char[] cArr7 = this.buf;
                c3 = '\\';
                cArr7[i4] = '\\';
                cArr7[i4 + 1] = replaceChars[ch3];
                end2++;
                i4--;
                c2 = 13;
            }
        }
        this.buf[this.count - 1] = '\'';
    }

    public void writeFieldName(String key, boolean checkSpecial) {
        if ((this.features & SerializerFeature.UseSingleQuotes.mask) != 0) {
            if ((this.features & SerializerFeature.QuoteFieldNames.mask) != 0) {
                writeStringWithSingleQuote(key);
                write(58);
                return;
            }
            writeKeyWithSingleQuoteIfHasSpecial(key);
        } else if ((this.features & SerializerFeature.QuoteFieldNames.mask) != 0) {
            writeStringWithDoubleQuote(key, ':', checkSpecial);
        } else {
            writeKeyWithDoubleQuoteIfHasSpecial(key);
        }
    }

    private void writeKeyWithDoubleQuoteIfHasSpecial(String text) {
        String str = text;
        int len = text.length();
        int newcount = this.count + len + 1;
        if (newcount > this.buf.length) {
            if (this.writer == null) {
                expandCapacity(newcount);
            } else if (len == 0) {
                write(34);
                write(34);
                write(58);
                return;
            } else {
                boolean hasSpecial = false;
                int i = 0;
                while (true) {
                    if (i >= len) {
                        break;
                    }
                    char ch = str.charAt(i);
                    byte[] bArr = specicalFlags_doubleQuotes;
                    if (ch < bArr.length && bArr[ch] != 0) {
                        hasSpecial = true;
                        break;
                    }
                    i++;
                }
                if (hasSpecial) {
                    write(34);
                }
                for (int i2 = 0; i2 < len; i2++) {
                    char ch2 = str.charAt(i2);
                    byte[] bArr2 = specicalFlags_doubleQuotes;
                    if (ch2 >= bArr2.length || bArr2[ch2] == 0) {
                        write((int) ch2);
                    } else {
                        write(92);
                        write((int) replaceChars[ch2]);
                    }
                }
                if (hasSpecial) {
                    write(34);
                }
                write(58);
                return;
            }
        }
        if (len == 0) {
            int i3 = this.count;
            if (i3 + 3 > this.buf.length) {
                expandCapacity(i3 + 3);
            }
            char[] cArr = this.buf;
            int i4 = this.count;
            this.count = i4 + 1;
            cArr[i4] = Typography.quote;
            int i5 = this.count;
            this.count = i5 + 1;
            cArr[i5] = Typography.quote;
            int i6 = this.count;
            this.count = i6 + 1;
            cArr[i6] = ':';
            return;
        }
        int start = this.count;
        int end = start + len;
        str.getChars(0, len, this.buf, start);
        this.count = newcount;
        boolean hasSpecial2 = false;
        int i7 = start;
        while (i7 < end) {
            char[] cArr2 = this.buf;
            char ch3 = cArr2[i7];
            byte[] bArr3 = specicalFlags_doubleQuotes;
            if (ch3 < bArr3.length && bArr3[ch3] != 0) {
                if (!hasSpecial2) {
                    newcount += 3;
                    if (newcount > cArr2.length) {
                        expandCapacity(newcount);
                    }
                    this.count = newcount;
                    char[] cArr3 = this.buf;
                    System.arraycopy(cArr3, i7 + 1, cArr3, i7 + 3, (end - i7) - 1);
                    char[] cArr4 = this.buf;
                    System.arraycopy(cArr4, 0, cArr4, 1, i7);
                    char[] cArr5 = this.buf;
                    cArr5[start] = Typography.quote;
                    int i8 = i7 + 1;
                    cArr5[i8] = '\\';
                    i7 = i8 + 1;
                    cArr5[i7] = replaceChars[ch3];
                    end += 2;
                    cArr5[this.count - 2] = Typography.quote;
                    hasSpecial2 = true;
                } else {
                    newcount++;
                    if (newcount > cArr2.length) {
                        expandCapacity(newcount);
                    }
                    this.count = newcount;
                    char[] cArr6 = this.buf;
                    System.arraycopy(cArr6, i7 + 1, cArr6, i7 + 2, end - i7);
                    char[] cArr7 = this.buf;
                    cArr7[i7] = '\\';
                    i7++;
                    cArr7[i7] = replaceChars[ch3];
                    end++;
                }
            }
            i7++;
        }
        this.buf[this.count - 1] = ':';
    }

    private void writeKeyWithSingleQuoteIfHasSpecial(String text) {
        String str = text;
        int len = text.length();
        int newcount = this.count + len + 1;
        if (newcount > this.buf.length) {
            if (this.writer == null) {
                expandCapacity(newcount);
            } else if (len == 0) {
                write(39);
                write(39);
                write(58);
                return;
            } else {
                boolean hasSpecial = false;
                int i = 0;
                while (true) {
                    if (i >= len) {
                        break;
                    }
                    char ch = str.charAt(i);
                    byte[] bArr = specicalFlags_singleQuotes;
                    if (ch < bArr.length && bArr[ch] != 0) {
                        hasSpecial = true;
                        break;
                    }
                    i++;
                }
                if (hasSpecial) {
                    write(39);
                }
                for (int i2 = 0; i2 < len; i2++) {
                    char ch2 = str.charAt(i2);
                    byte[] bArr2 = specicalFlags_singleQuotes;
                    if (ch2 >= bArr2.length || bArr2[ch2] == 0) {
                        write((int) ch2);
                    } else {
                        write(92);
                        write((int) replaceChars[ch2]);
                    }
                }
                if (hasSpecial) {
                    write(39);
                }
                write(58);
                return;
            }
        }
        if (len == 0) {
            int i3 = this.count;
            if (i3 + 3 > this.buf.length) {
                expandCapacity(i3 + 3);
            }
            char[] cArr = this.buf;
            int i4 = this.count;
            this.count = i4 + 1;
            cArr[i4] = '\'';
            int i5 = this.count;
            this.count = i5 + 1;
            cArr[i5] = '\'';
            int i6 = this.count;
            this.count = i6 + 1;
            cArr[i6] = ':';
            return;
        }
        int start = this.count;
        int end = start + len;
        str.getChars(0, len, this.buf, start);
        this.count = newcount;
        boolean hasSpecial2 = false;
        int i7 = start;
        while (i7 < end) {
            char[] cArr2 = this.buf;
            char ch3 = cArr2[i7];
            byte[] bArr3 = specicalFlags_singleQuotes;
            if (ch3 < bArr3.length && bArr3[ch3] != 0) {
                if (!hasSpecial2) {
                    newcount += 3;
                    if (newcount > cArr2.length) {
                        expandCapacity(newcount);
                    }
                    this.count = newcount;
                    char[] cArr3 = this.buf;
                    System.arraycopy(cArr3, i7 + 1, cArr3, i7 + 3, (end - i7) - 1);
                    char[] cArr4 = this.buf;
                    System.arraycopy(cArr4, 0, cArr4, 1, i7);
                    char[] cArr5 = this.buf;
                    cArr5[start] = '\'';
                    int i8 = i7 + 1;
                    cArr5[i8] = '\\';
                    i7 = i8 + 1;
                    cArr5[i7] = replaceChars[ch3];
                    end += 2;
                    cArr5[this.count - 2] = '\'';
                    hasSpecial2 = true;
                } else {
                    newcount++;
                    if (newcount > cArr2.length) {
                        expandCapacity(newcount);
                    }
                    this.count = newcount;
                    char[] cArr6 = this.buf;
                    System.arraycopy(cArr6, i7 + 1, cArr6, i7 + 2, end - i7);
                    char[] cArr7 = this.buf;
                    cArr7[i7] = '\\';
                    i7++;
                    cArr7[i7] = replaceChars[ch3];
                    end++;
                }
            }
            i7++;
        }
        this.buf[newcount - 1] = ':';
    }

    public void flush() {
        Writer writer2 = this.writer;
        if (writer2 != null) {
            try {
                writer2.write(this.buf, 0, this.count);
                this.writer.flush();
                this.count = 0;
            } catch (IOException e) {
                throw new JSONException(e.getMessage(), e);
            }
        }
    }

    protected static void getChars(long i, int index, char[] buf2) {
        int charPos = index;
        char sign = 0;
        if (i < 0) {
            sign = '-';
            i = -i;
        }
        while (i > 2147483647L) {
            long q = i / 100;
            int r = (int) (i - (((q << 6) + (q << 5)) + (q << 2)));
            i = q;
            int charPos2 = charPos - 1;
            buf2[charPos2] = DigitOnes[r];
            charPos = charPos2 - 1;
            buf2[charPos] = DigitTens[r];
        }
        int i2 = (int) i;
        while (i2 >= 65536) {
            int q2 = i2 / 100;
            int r2 = i2 - (((q2 << 6) + (q2 << 5)) + (q2 << 2));
            i2 = q2;
            int charPos3 = charPos - 1;
            buf2[charPos3] = DigitOnes[r2];
            charPos = charPos3 - 1;
            buf2[charPos] = DigitTens[r2];
        }
        do {
            int q22 = (52429 * i2) >>> 19;
            charPos--;
            buf2[charPos] = digits[i2 - ((q22 << 3) + (q22 << 1))];
            i2 = q22;
        } while (i2 != 0);
        if (sign != 0) {
            buf2[charPos - 1] = sign;
        }
    }
}

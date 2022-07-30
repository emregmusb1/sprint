package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import kotlin.text.Typography;

public final class JSONLexer {
    public static final char[] CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    public static final int END = 4;
    public static final char EOI = '\u001a';
    static final int[] IA = new int[256];
    public static final int NOT_MATCH = -1;
    public static final int NOT_MATCH_NAME = -2;
    public static final int UNKNOWN = 0;
    private static boolean V6 = false;
    public static final int VALUE = 3;
    protected static final int[] digits = new int[103];
    public static final boolean[] firstIdentifierFlags = new boolean[256];
    public static final boolean[] identifierFlags = new boolean[256];
    private static final ThreadLocal<char[]> sbufLocal = new ThreadLocal<>();
    protected int bp;
    protected Calendar calendar;
    protected char ch;
    public boolean disableCircularReferenceDetect;
    protected int eofPos;
    public int features;
    protected boolean hasSpecial;
    protected final int len;
    public Locale locale;
    public int matchStat;
    protected int np;
    protected int pos;
    protected char[] sbuf;
    protected int sp;
    protected String stringDefaultValue;
    protected final String text;
    public TimeZone timeZone;
    protected int token;

    static {
        int version = -1;
        try {
            version = Class.forName("android.os.Build$VERSION").getField("SDK_INT").getInt((Object) null);
        } catch (Exception e) {
        }
        V6 = version >= 23;
        for (int i = 48; i <= 57; i++) {
            digits[i] = i - 48;
        }
        for (int i2 = 97; i2 <= 102; i2++) {
            digits[i2] = (i2 - 97) + 10;
        }
        for (int i3 = 65; i3 <= 70; i3++) {
            digits[i3] = (i3 - 65) + 10;
        }
        Arrays.fill(IA, -1);
        int iS = CA.length;
        for (int i4 = 0; i4 < iS; i4++) {
            IA[CA[i4]] = i4;
        }
        IA[61] = 0;
        char c = 0;
        while (true) {
            boolean[] zArr = firstIdentifierFlags;
            if (c >= zArr.length) {
                break;
            }
            if (c >= 'A' && c <= 'Z') {
                zArr[c] = true;
            } else if (c >= 'a' && c <= 'z') {
                firstIdentifierFlags[c] = true;
            } else if (c == '_') {
                firstIdentifierFlags[c] = true;
            }
            c = (char) (c + 1);
        }
        char c2 = 0;
        while (true) {
            boolean[] zArr2 = identifierFlags;
            if (c2 < zArr2.length) {
                if (c2 >= 'A' && c2 <= 'Z') {
                    zArr2[c2] = true;
                } else if (c2 >= 'a' && c2 <= 'z') {
                    identifierFlags[c2] = true;
                } else if (c2 == '_') {
                    identifierFlags[c2] = true;
                } else if (c2 >= '0' && c2 <= '9') {
                    identifierFlags[c2] = true;
                }
                c2 = (char) (c2 + 1);
            } else {
                return;
            }
        }
    }

    public JSONLexer(String input) {
        this(input, JSON.DEFAULT_PARSER_FEATURE);
    }

    public JSONLexer(char[] input, int inputLength) {
        this(input, inputLength, JSON.DEFAULT_PARSER_FEATURE);
    }

    public JSONLexer(char[] input, int inputLength, int features2) {
        this(new String(input, 0, inputLength), features2);
    }

    public JSONLexer(String input, int features2) {
        char c;
        this.features = JSON.DEFAULT_PARSER_FEATURE;
        this.timeZone = JSON.defaultTimeZone;
        this.locale = JSON.defaultLocale;
        String str = null;
        this.calendar = null;
        boolean z = false;
        this.matchStat = 0;
        this.sbuf = sbufLocal.get();
        if (this.sbuf == null) {
            this.sbuf = new char[512];
        }
        this.features = features2;
        this.text = input;
        this.len = this.text.length();
        this.bp = -1;
        int index = this.bp + 1;
        this.bp = index;
        if (index >= this.len) {
            c = EOI;
        } else {
            c = this.text.charAt(index);
        }
        this.ch = c;
        if (this.ch == 65279) {
            next();
        }
        this.stringDefaultValue = (Feature.InitStringFieldAsEmpty.mask & features2) != 0 ? "" : str;
        this.disableCircularReferenceDetect = (Feature.DisableCircularReferenceDetect.mask & features2) != 0 ? true : z;
    }

    public final int token() {
        return this.token;
    }

    public void close() {
        char[] cArr = this.sbuf;
        if (cArr.length <= 8196) {
            sbufLocal.set(cArr);
        }
        this.sbuf = null;
    }

    public char next() {
        char c;
        int index = this.bp + 1;
        this.bp = index;
        if (index >= this.len) {
            c = EOI;
        } else {
            c = this.text.charAt(index);
        }
        this.ch = c;
        return c;
    }

    public final void config(Feature feature, boolean state) {
        if (state) {
            this.features |= feature.mask;
        } else {
            this.features &= ~feature.mask;
        }
        if (feature == Feature.InitStringFieldAsEmpty) {
            this.stringDefaultValue = state ? "" : null;
        }
        this.disableCircularReferenceDetect = (this.features & Feature.DisableCircularReferenceDetect.mask) != 0;
    }

    public final boolean isEnabled(Feature feature) {
        return (this.features & feature.mask) != 0;
    }

    public final void nextTokenWithChar(char expect) {
        char c;
        this.sp = 0;
        while (true) {
            char c2 = this.ch;
            if (c2 == expect) {
                int index = this.bp + 1;
                this.bp = index;
                if (index >= this.len) {
                    c = EOI;
                } else {
                    c = this.text.charAt(index);
                }
                this.ch = c;
                nextToken();
                return;
            } else if (c2 == ' ' || c2 == 10 || c2 == 13 || c2 == 9 || c2 == 12 || c2 == 8) {
                next();
            } else {
                throw new JSONException("not match " + expect + " - " + this.ch);
            }
        }
    }

    public final boolean isRef() {
        if (this.sp != 4 || !this.text.startsWith("$ref", this.np + 1)) {
            return false;
        }
        return true;
    }

    public final String numberString() {
        char chLocal = this.text.charAt((this.np + this.sp) - 1);
        int sp2 = this.sp;
        if (chLocal == 'L' || chLocal == 'S' || chLocal == 'B' || chLocal == 'F' || chLocal == 'D') {
            sp2--;
        }
        return subString(this.np, sp2);
    }

    /* access modifiers changed from: protected */
    public char charAt(int index) {
        if (index >= this.len) {
            return EOI;
        }
        return this.text.charAt(index);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0027, code lost:
        scanNumber();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002a, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void nextToken() {
        /*
            r7 = this;
            r0 = 0
            r7.sp = r0
        L_0x0003:
            int r1 = r7.bp
            r7.pos = r1
            char r1 = r7.ch
            r2 = 47
            if (r1 != r2) goto L_0x0011
            r7.skipComment()
            goto L_0x0003
        L_0x0011:
            r2 = 34
            if (r1 != r2) goto L_0x0019
            r7.scanString()
            return
        L_0x0019:
            r2 = 48
            if (r1 < r2) goto L_0x0021
            r2 = 57
            if (r1 <= r2) goto L_0x0027
        L_0x0021:
            char r1 = r7.ch
            r2 = 45
            if (r1 != r2) goto L_0x002b
        L_0x0027:
            r7.scanNumber()
            return
        L_0x002b:
            r2 = 44
            if (r1 != r2) goto L_0x0037
            r7.next()
            r0 = 16
            r7.token = r0
            return
        L_0x0037:
            r2 = 12
            if (r1 == r2) goto L_0x013d
            r3 = 13
            if (r1 == r3) goto L_0x013d
            r4 = 32
            if (r1 == r4) goto L_0x013d
            r4 = 58
            if (r1 == r4) goto L_0x0135
            r4 = 91
            r5 = 26
            r6 = 1
            if (r1 == r4) goto L_0x011e
            r4 = 93
            if (r1 == r4) goto L_0x0116
            r4 = 102(0x66, float:1.43E-43)
            if (r1 == r4) goto L_0x0112
            r4 = 110(0x6e, float:1.54E-43)
            if (r1 == r4) goto L_0x010e
            r4 = 123(0x7b, float:1.72E-43)
            if (r1 == r4) goto L_0x00f9
            r2 = 125(0x7d, float:1.75E-43)
            if (r1 == r2) goto L_0x00e4
            r2 = 83
            if (r1 == r2) goto L_0x00e0
            r2 = 84
            if (r1 == r2) goto L_0x00e0
            r2 = 116(0x74, float:1.63E-43)
            if (r1 == r2) goto L_0x00dc
            r2 = 117(0x75, float:1.64E-43)
            if (r1 == r2) goto L_0x00e0
            switch(r1) {
                case 8: goto L_0x013d;
                case 9: goto L_0x013d;
                case 10: goto L_0x013d;
                default: goto L_0x0075;
            }
        L_0x0075:
            switch(r1) {
                case 39: goto L_0x00c7;
                case 40: goto L_0x00bf;
                case 41: goto L_0x00b7;
                default: goto L_0x0078;
            }
        L_0x0078:
            int r2 = r7.bp
            int r3 = r7.len
            if (r2 == r3) goto L_0x0087
            if (r1 != r5) goto L_0x0085
            int r2 = r2 + 1
            if (r2 != r3) goto L_0x0085
            goto L_0x0087
        L_0x0085:
            r1 = 0
            goto L_0x0088
        L_0x0087:
            r1 = 1
        L_0x0088:
            if (r1 == 0) goto L_0x00a1
            int r0 = r7.token
            r2 = 20
            if (r0 == r2) goto L_0x0099
            r7.token = r2
            int r0 = r7.eofPos
            r7.bp = r0
            r7.pos = r0
            goto L_0x00b1
        L_0x0099:
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException
            java.lang.String r2 = "EOF error"
            r0.<init>(r2)
            throw r0
        L_0x00a1:
            char r2 = r7.ch
            r3 = 31
            if (r2 <= r3) goto L_0x00b2
            r3 = 127(0x7f, float:1.78E-43)
            if (r2 != r3) goto L_0x00ac
            goto L_0x00b2
        L_0x00ac:
            r7.token = r6
            r7.next()
        L_0x00b1:
            return
        L_0x00b2:
            r7.next()
            goto L_0x0141
        L_0x00b7:
            r7.next()
            r0 = 11
            r7.token = r0
            return
        L_0x00bf:
            r7.next()
            r0 = 10
            r7.token = r0
            return
        L_0x00c7:
            int r0 = r7.features
            com.alibaba.fastjson.parser.Feature r1 = com.alibaba.fastjson.parser.Feature.AllowSingleQuotes
            int r1 = r1.mask
            r0 = r0 & r1
            if (r0 == 0) goto L_0x00d4
            r7.scanString()
            return
        L_0x00d4:
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException
            java.lang.String r1 = "Feature.AllowSingleQuotes is false"
            r0.<init>(r1)
            throw r0
        L_0x00dc:
            r7.scanTrue()
            return
        L_0x00e0:
            r7.scanIdent()
            return
        L_0x00e4:
            int r0 = r7.bp
            int r0 = r0 + r6
            r7.bp = r0
            int r1 = r7.len
            if (r0 < r1) goto L_0x00ee
            goto L_0x00f4
        L_0x00ee:
            java.lang.String r1 = r7.text
            char r5 = r1.charAt(r0)
        L_0x00f4:
            r7.ch = r5
            r7.token = r3
            return
        L_0x00f9:
            int r0 = r7.bp
            int r0 = r0 + r6
            r7.bp = r0
            int r1 = r7.len
            if (r0 < r1) goto L_0x0103
            goto L_0x0109
        L_0x0103:
            java.lang.String r1 = r7.text
            char r5 = r1.charAt(r0)
        L_0x0109:
            r7.ch = r5
            r7.token = r2
            return
        L_0x010e:
            r7.scanNullOrNew()
            return
        L_0x0112:
            r7.scanFalse()
            return
        L_0x0116:
            r7.next()
            r0 = 15
            r7.token = r0
            return
        L_0x011e:
            int r0 = r7.bp
            int r0 = r0 + r6
            r7.bp = r0
            int r1 = r7.len
            if (r0 < r1) goto L_0x0128
            goto L_0x012e
        L_0x0128:
            java.lang.String r1 = r7.text
            char r5 = r1.charAt(r0)
        L_0x012e:
            r7.ch = r5
            r0 = 14
            r7.token = r0
            return
        L_0x0135:
            r7.next()
            r0 = 17
            r7.token = r0
            return
        L_0x013d:
            r7.next()
        L_0x0141:
            goto L_0x0003
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.JSONLexer.nextToken():void");
    }

    public final void nextToken(int expect) {
        this.sp = 0;
        while (true) {
            if (expect != 2) {
                char c = EOI;
                if (expect == 4) {
                    char c2 = this.ch;
                    if (c2 == '\"') {
                        this.pos = this.bp;
                        scanString();
                        return;
                    } else if (c2 >= '0' && c2 <= '9') {
                        this.pos = this.bp;
                        scanNumber();
                        return;
                    } else if (this.ch == '{') {
                        this.token = 12;
                        int index = this.bp + 1;
                        this.bp = index;
                        if (index < this.len) {
                            c = this.text.charAt(index);
                        }
                        this.ch = c;
                        return;
                    }
                } else if (expect == 12) {
                    char c3 = this.ch;
                    if (c3 == '{') {
                        this.token = 12;
                        int index2 = this.bp + 1;
                        this.bp = index2;
                        if (index2 < this.len) {
                            c = this.text.charAt(index2);
                        }
                        this.ch = c;
                        return;
                    } else if (c3 == '[') {
                        this.token = 14;
                        int index3 = this.bp + 1;
                        this.bp = index3;
                        if (index3 < this.len) {
                            c = this.text.charAt(index3);
                        }
                        this.ch = c;
                        return;
                    }
                } else if (expect != 18) {
                    if (expect != 20) {
                        switch (expect) {
                            case 14:
                                char c4 = this.ch;
                                if (c4 == '[') {
                                    this.token = 14;
                                    next();
                                    return;
                                } else if (c4 == '{') {
                                    this.token = 12;
                                    next();
                                    return;
                                }
                                break;
                            case 15:
                                if (this.ch == ']') {
                                    this.token = 15;
                                    next();
                                    return;
                                }
                                break;
                            case 16:
                                char c5 = this.ch;
                                if (c5 == ',') {
                                    this.token = 16;
                                    int index4 = this.bp + 1;
                                    this.bp = index4;
                                    if (index4 < this.len) {
                                        c = this.text.charAt(index4);
                                    }
                                    this.ch = c;
                                    return;
                                } else if (c5 == '}') {
                                    this.token = 13;
                                    int index5 = this.bp + 1;
                                    this.bp = index5;
                                    if (index5 < this.len) {
                                        c = this.text.charAt(index5);
                                    }
                                    this.ch = c;
                                    return;
                                } else if (c5 == ']') {
                                    this.token = 15;
                                    int index6 = this.bp + 1;
                                    this.bp = index6;
                                    if (index6 < this.len) {
                                        c = this.text.charAt(index6);
                                    }
                                    this.ch = c;
                                    return;
                                } else if (c5 == 26) {
                                    this.token = 20;
                                    return;
                                }
                                break;
                        }
                    }
                    if (this.ch == 26) {
                        this.token = 20;
                        return;
                    }
                } else {
                    nextIdent();
                    return;
                }
            } else {
                char c6 = this.ch;
                if (c6 < '0' || c6 > '9') {
                    char c7 = this.ch;
                    if (c7 == '\"') {
                        this.pos = this.bp;
                        scanString();
                        return;
                    } else if (c7 == '[') {
                        this.token = 14;
                        next();
                        return;
                    } else if (c7 == '{') {
                        this.token = 12;
                        next();
                        return;
                    }
                } else {
                    this.pos = this.bp;
                    scanNumber();
                    return;
                }
            }
            char c8 = this.ch;
            if (c8 == ' ' || c8 == 10 || c8 == 13 || c8 == 9 || c8 == 12 || c8 == 8) {
                next();
            } else {
                nextToken();
                return;
            }
        }
    }

    public final void nextIdent() {
        while (true) {
            char c = this.ch;
            if (!(c <= ' ' && (c == ' ' || c == 10 || c == 13 || c == 9 || c == 12 || c == 8))) {
                break;
            }
            next();
        }
        char c2 = this.ch;
        if (c2 == '_' || Character.isLetter(c2)) {
            scanIdent();
        } else {
            nextToken();
        }
    }

    public final String tokenName() {
        return JSONToken.name(this.token);
    }

    public final Number integerValue() throws NumberFormatException {
        long limit;
        long result = 0;
        boolean negative = false;
        int i = this.np;
        int max = this.np + this.sp;
        char type = ' ';
        char charAt = charAt(max - 1);
        if (charAt == 'B') {
            max--;
            type = 'B';
        } else if (charAt == 'L') {
            max--;
            type = 'L';
        } else if (charAt == 'S') {
            max--;
            type = 'S';
        }
        if (charAt(this.np) == '-') {
            negative = true;
            limit = Long.MIN_VALUE;
            i++;
        } else {
            limit = -9223372036854775807L;
        }
        if (i < max) {
            result = (long) (-(charAt(i) - 48));
            i++;
        }
        while (i < max) {
            int i2 = i + 1;
            int digit = charAt(i) - 48;
            if (result < -922337203685477580L) {
                return new BigInteger(numberString());
            }
            long result2 = result * 10;
            if (result2 < ((long) digit) + limit) {
                return new BigInteger(numberString());
            }
            result = result2 - ((long) digit);
            i = i2;
        }
        if (!negative) {
            long result3 = -result;
            if (result3 > 2147483647L || type == 'L') {
                return Long.valueOf(result3);
            }
            if (type == 'S') {
                return Short.valueOf((short) ((int) result3));
            }
            if (type == 'B') {
                return Byte.valueOf((byte) ((int) result3));
            }
            return Integer.valueOf((int) result3);
        } else if (i <= this.np + 1) {
            throw new NumberFormatException(numberString());
        } else if (result < -2147483648L || type == 'L') {
            return Long.valueOf(result);
        } else {
            if (type == 'S') {
                return Short.valueOf((short) ((int) result));
            }
            if (type == 'B') {
                return Byte.valueOf((byte) ((int) result));
            }
            return Integer.valueOf((int) result);
        }
    }

    public final String scanSymbol(SymbolTable symbolTable) {
        char c;
        while (true) {
            c = this.ch;
            if (c != ' ' && c != 10 && c != 13 && c != 9 && c != 12 && c != 8) {
                break;
            }
            next();
        }
        if (c == '\"') {
            return scanSymbol(symbolTable, Typography.quote);
        }
        if (c == '\'') {
            if ((this.features & Feature.AllowSingleQuotes.mask) != 0) {
                return scanSymbol(symbolTable, '\'');
            }
            throw new JSONException("syntax error");
        } else if (c == '}') {
            next();
            this.token = 13;
            return null;
        } else if (c == ',') {
            next();
            this.token = 16;
            return null;
        } else if (c == 26) {
            this.token = 20;
            return null;
        } else if ((this.features & Feature.AllowUnQuotedFieldNames.mask) != 0) {
            return scanSymbolUnQuoted(symbolTable);
        } else {
            throw new JSONException("syntax error");
        }
    }

    public String scanSymbol(SymbolTable symbolTable, char quoteChar) {
        String strVal;
        char c;
        char c2 = quoteChar;
        int hash = 0;
        boolean hasSpecial2 = false;
        int startIndex = this.bp + 1;
        int endIndex = this.text.indexOf(c2, startIndex);
        if (endIndex != -1) {
            int chars_len = endIndex - startIndex;
            char[] chars = sub_chars(this.bp + 1, chars_len);
            while (chars_len > 0 && chars[chars_len - 1] == '\\') {
                int slashCount = 1;
                int i = chars_len - 2;
                while (i >= 0 && chars[i] == '\\') {
                    slashCount++;
                    i--;
                }
                if (slashCount % 2 == 0) {
                    break;
                }
                int nextIndex = this.text.indexOf(c2, endIndex + 1);
                int next_chars_len = chars_len + (nextIndex - endIndex);
                if (next_chars_len >= chars.length) {
                    int newLen = (chars.length * 3) / 2;
                    if (newLen < next_chars_len) {
                        newLen = next_chars_len;
                    }
                    char[] newChars = new char[newLen];
                    System.arraycopy(chars, 0, newChars, 0, chars.length);
                    chars = newChars;
                }
                this.text.getChars(endIndex, nextIndex, chars, chars_len);
                chars_len = next_chars_len;
                endIndex = nextIndex;
                hasSpecial2 = true;
            }
            if (!hasSpecial2) {
                for (int i2 = 0; i2 < chars_len; i2++) {
                    char ch2 = chars[i2];
                    hash = (hash * 31) + ch2;
                    if (ch2 == '\\') {
                        hasSpecial2 = true;
                    }
                }
                if (hasSpecial2) {
                    strVal = readString(chars, chars_len);
                    SymbolTable symbolTable2 = symbolTable;
                } else if (chars_len < 20) {
                    strVal = symbolTable.addSymbol(chars, 0, chars_len, hash);
                } else {
                    SymbolTable symbolTable3 = symbolTable;
                    strVal = new String(chars, 0, chars_len);
                }
            } else {
                SymbolTable symbolTable4 = symbolTable;
                strVal = readString(chars, chars_len);
            }
            this.bp = endIndex + 1;
            int index = this.bp;
            if (index >= this.len) {
                c = EOI;
            } else {
                c = this.text.charAt(index);
            }
            this.ch = c;
            return strVal;
        }
        SymbolTable symbolTable5 = symbolTable;
        throw new JSONException("unclosed str, " + info());
    }

    private static String readString(char[] chars, int chars_len) {
        char[] sbuf2 = new char[chars_len];
        int len2 = 0;
        int i = 0;
        while (i < chars_len) {
            char ch2 = chars[i];
            if (ch2 != '\\') {
                sbuf2[len2] = ch2;
                len2++;
            } else {
                i++;
                char ch3 = chars[i];
                if (ch3 == '\"') {
                    sbuf2[len2] = Typography.quote;
                    len2++;
                } else if (ch3 != '\'') {
                    if (ch3 != 'F') {
                        if (ch3 == '\\') {
                            sbuf2[len2] = '\\';
                            len2++;
                        } else if (ch3 == 'b') {
                            sbuf2[len2] = 8;
                            len2++;
                        } else if (ch3 != 'f') {
                            if (ch3 == 'n') {
                                sbuf2[len2] = 10;
                                len2++;
                            } else if (ch3 == 'r') {
                                sbuf2[len2] = 13;
                                len2++;
                            } else if (ch3 != 'x') {
                                switch (ch3) {
                                    case '/':
                                        sbuf2[len2] = '/';
                                        len2++;
                                        break;
                                    case '0':
                                        sbuf2[len2] = 0;
                                        len2++;
                                        break;
                                    case '1':
                                        sbuf2[len2] = 1;
                                        len2++;
                                        break;
                                    case '2':
                                        sbuf2[len2] = 2;
                                        len2++;
                                        break;
                                    case '3':
                                        sbuf2[len2] = 3;
                                        len2++;
                                        break;
                                    case '4':
                                        sbuf2[len2] = 4;
                                        len2++;
                                        break;
                                    case '5':
                                        sbuf2[len2] = 5;
                                        len2++;
                                        break;
                                    case '6':
                                        sbuf2[len2] = 6;
                                        len2++;
                                        break;
                                    case '7':
                                        sbuf2[len2] = 7;
                                        len2++;
                                        break;
                                    default:
                                        switch (ch3) {
                                            case 't':
                                                sbuf2[len2] = 9;
                                                len2++;
                                                break;
                                            case 'u':
                                                int i2 = i + 1;
                                                int i3 = i2 + 1;
                                                int i4 = i3 + 1;
                                                i = i4 + 1;
                                                sbuf2[len2] = (char) Integer.parseInt(new String(new char[]{chars[i2], chars[i3], chars[i4], chars[i]}), 16);
                                                len2++;
                                                break;
                                            case 'v':
                                                sbuf2[len2] = 11;
                                                len2++;
                                                break;
                                            default:
                                                throw new JSONException("unclosed.str.lit");
                                        }
                                }
                            } else {
                                int[] iArr = digits;
                                int i5 = i + 1;
                                i = i5 + 1;
                                sbuf2[len2] = (char) ((iArr[chars[i5]] * 16) + iArr[chars[i]]);
                                len2++;
                            }
                        }
                    }
                    sbuf2[len2] = 12;
                    len2++;
                } else {
                    sbuf2[len2] = '\'';
                    len2++;
                }
            }
            i++;
        }
        return new String(sbuf2, 0, len2);
    }

    public String info() {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append("pos ");
        sb.append(this.bp);
        sb.append(", json : ");
        if (this.text.length() < 65536) {
            str = this.text;
        } else {
            str = this.text.substring(0, 65536);
        }
        sb.append(str);
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public void skipComment() {
        next();
        char c = this.ch;
        if (c == '/') {
            do {
                next();
            } while (this.ch != 10);
            next();
        } else if (c == '*') {
            next();
            while (true) {
                char c2 = this.ch;
                if (c2 == 26) {
                    return;
                }
                if (c2 == '*') {
                    next();
                    if (this.ch == '/') {
                        next();
                        return;
                    }
                } else {
                    next();
                }
            }
        } else {
            throw new JSONException("invalid comment");
        }
    }

    public final String scanSymbolUnQuoted(SymbolTable symbolTable) {
        char first = this.ch;
        char c = this.ch;
        boolean[] zArr = firstIdentifierFlags;
        if (c >= zArr.length || zArr[first]) {
            int hash = first;
            this.np = this.bp;
            this.sp = 1;
            while (true) {
                char ch2 = next();
                boolean[] zArr2 = identifierFlags;
                if (ch2 < zArr2.length && !zArr2[ch2]) {
                    break;
                }
                hash = (hash * 31) + ch2;
                this.sp++;
            }
            this.ch = charAt(this.bp);
            this.token = 18;
            if (this.sp != 4 || !this.text.startsWith("null", this.np)) {
                return symbolTable.addSymbol(this.text, this.np, this.sp, hash);
            }
            return null;
        }
        throw new JSONException("illegal identifier : " + this.ch + ", " + info());
    }

    public final void scanString() {
        char c;
        char quoteChar = this.ch;
        boolean hasSpecial2 = false;
        int startIndex = this.bp + 1;
        int endIndex = this.text.indexOf(quoteChar, startIndex);
        if (endIndex != -1) {
            int chars_len = endIndex - startIndex;
            char[] chars = sub_chars(this.bp + 1, chars_len);
            while (chars_len > 0 && chars[chars_len - 1] == '\\') {
                int slashCount = 1;
                int i = chars_len - 2;
                while (i >= 0 && chars[i] == '\\') {
                    slashCount++;
                    i--;
                }
                if (slashCount % 2 == 0) {
                    break;
                }
                int nextIndex = this.text.indexOf(quoteChar, endIndex + 1);
                int next_chars_len = chars_len + (nextIndex - endIndex);
                if (next_chars_len >= chars.length) {
                    int newLen = (chars.length * 3) / 2;
                    if (newLen < next_chars_len) {
                        newLen = next_chars_len;
                    }
                    char[] newChars = new char[newLen];
                    System.arraycopy(chars, 0, newChars, 0, chars.length);
                    chars = newChars;
                }
                this.text.getChars(endIndex, nextIndex, chars, chars_len);
                chars_len = next_chars_len;
                endIndex = nextIndex;
                hasSpecial2 = true;
            }
            if (!hasSpecial2) {
                for (int i2 = 0; i2 < chars_len; i2++) {
                    if (chars[i2] == '\\') {
                        hasSpecial2 = true;
                    }
                }
            }
            this.sbuf = chars;
            this.sp = chars_len;
            this.np = this.bp;
            this.hasSpecial = hasSpecial2;
            this.bp = endIndex + 1;
            int index = this.bp;
            if (index >= this.len) {
                c = EOI;
            } else {
                c = this.text.charAt(index);
            }
            this.ch = c;
            this.token = 4;
            return;
        }
        throw new JSONException("unclosed str, " + info());
    }

    public String scanStringValue(char quoteChar) {
        String strVal;
        char c;
        int startIndex = this.bp + 1;
        int endIndex = this.text.indexOf(quoteChar, startIndex);
        if (endIndex != -1) {
            if (V6) {
                strVal = this.text.substring(startIndex, endIndex);
            } else {
                int chars_len = endIndex - startIndex;
                strVal = new String(sub_chars(this.bp + 1, chars_len), 0, chars_len);
            }
            if (strVal.indexOf(92) != -1) {
                while (true) {
                    int slashCount = 0;
                    int i = endIndex - 1;
                    while (i >= 0 && this.text.charAt(i) == '\\') {
                        slashCount++;
                        i--;
                    }
                    if (slashCount % 2 == 0) {
                        break;
                    }
                    endIndex = this.text.indexOf(quoteChar, endIndex + 1);
                }
                int chars_len2 = endIndex - startIndex;
                strVal = readString(sub_chars(this.bp + 1, chars_len2), chars_len2);
            }
            this.bp = endIndex + 1;
            int index = this.bp;
            if (index >= this.len) {
                c = EOI;
            } else {
                c = this.text.charAt(index);
            }
            this.ch = c;
            return strVal;
        }
        throw new JSONException("unclosed str, " + info());
    }

    public Calendar getCalendar() {
        return this.calendar;
    }

    public final int intValue() {
        int limit;
        int i;
        int result = 0;
        boolean negative = false;
        int i2 = this.np;
        int i3 = this.np;
        int max = this.sp + i3;
        if (charAt(i3) == '-') {
            negative = true;
            limit = Integer.MIN_VALUE;
            i2++;
        } else {
            limit = -2147483647;
        }
        if (i2 < max) {
            result = -(charAt(i2) - 48);
            i2++;
        }
        while (true) {
            if (i2 >= max) {
                break;
            }
            i = i2 + 1;
            int i4 = charAt(i2);
            if (i4 == 76 || i4 == 83 || i4 == 66) {
                i2 = i;
            } else {
                int digit = i4 - 48;
                if (result >= -214748364) {
                    int result2 = result * 10;
                    if (result2 >= limit + digit) {
                        result = result2 - digit;
                        i2 = i;
                    } else {
                        throw new NumberFormatException(numberString());
                    }
                } else {
                    throw new NumberFormatException(numberString());
                }
            }
        }
        i2 = i;
        if (!negative) {
            return -result;
        }
        if (i2 > this.np + 1) {
            return result;
        }
        throw new NumberFormatException(numberString());
    }

    public byte[] bytesValue() {
        return decodeFast(this.text, this.np + 1, this.sp);
    }

    private void scanTrue() {
        if (this.text.startsWith("true", this.bp)) {
            this.bp += 4;
            this.ch = charAt(this.bp);
            char c = this.ch;
            if (c == ' ' || c == ',' || c == '}' || c == ']' || c == 10 || c == 13 || c == 9 || c == 26 || c == 12 || c == 8 || c == ':') {
                this.token = 6;
                return;
            }
        }
        throw new JSONException("scan true error");
    }

    private void scanNullOrNew() {
        int token2 = 0;
        if (this.text.startsWith("null", this.bp)) {
            this.bp += 4;
            token2 = 8;
        } else if (this.text.startsWith("new", this.bp)) {
            this.bp += 3;
            token2 = 9;
        }
        if (token2 != 0) {
            this.ch = charAt(this.bp);
            char c = this.ch;
            if (c == ' ' || c == ',' || c == '}' || c == ']' || c == 10 || c == 13 || c == 9 || c == 26 || c == 12 || c == 8) {
                this.token = token2;
                return;
            }
        }
        throw new JSONException("scan null/new error");
    }

    private void scanFalse() {
        if (this.text.startsWith("false", this.bp)) {
            this.bp += 5;
            this.ch = charAt(this.bp);
            char c = this.ch;
            if (c == ' ' || c == ',' || c == '}' || c == ']' || c == 10 || c == 13 || c == 9 || c == 26 || c == 12 || c == 8 || c == ':') {
                this.token = 7;
                return;
            }
        }
        throw new JSONException("scan false error");
    }

    private void scanIdent() {
        this.np = this.bp - 1;
        this.hasSpecial = false;
        do {
            this.sp++;
            next();
        } while (Character.isLetterOrDigit(this.ch));
        String ident = stringVal();
        if (ident.equals("null")) {
            this.token = 8;
        } else if (ident.equals("true")) {
            this.token = 6;
        } else if (ident.equals("false")) {
            this.token = 7;
        } else if (ident.equals("new")) {
            this.token = 9;
        } else if (ident.equals("undefined")) {
            this.token = 23;
        } else if (ident.equals("Set")) {
            this.token = 21;
        } else if (ident.equals("TreeSet")) {
            this.token = 22;
        } else {
            this.token = 18;
        }
    }

    public final String stringVal() {
        if (this.hasSpecial) {
            return readString(this.sbuf, this.sp);
        }
        return subString(this.np + 1, this.sp);
    }

    private final String subString(int offset, int count) {
        char[] cArr = this.sbuf;
        if (count < cArr.length) {
            this.text.getChars(offset, offset + count, cArr, 0);
            return new String(this.sbuf, 0, count);
        }
        char[] chars = new char[count];
        this.text.getChars(offset, offset + count, chars, 0);
        return new String(chars);
    }

    /* access modifiers changed from: package-private */
    public final char[] sub_chars(int offset, int count) {
        char[] cArr = this.sbuf;
        if (count < cArr.length) {
            this.text.getChars(offset, offset + count, cArr, 0);
            return this.sbuf;
        }
        char[] chars = new char[count];
        this.sbuf = chars;
        this.text.getChars(offset, offset + count, chars, 0);
        return chars;
    }

    public final boolean isBlankInput() {
        int i = 0;
        while (true) {
            char ch2 = charAt(i);
            boolean whitespace = true;
            if (ch2 == 26) {
                return true;
            }
            if (ch2 > ' ' || !(ch2 == ' ' || ch2 == 10 || ch2 == 13 || ch2 == 9 || ch2 == 12 || ch2 == 8)) {
                whitespace = false;
            }
            if (!whitespace) {
                return false;
            }
            i++;
        }
    }

    /* access modifiers changed from: package-private */
    public final void skipWhitespace() {
        while (true) {
            char c = this.ch;
            if (c > '/') {
                return;
            }
            if (c == ' ' || c == 13 || c == 10 || c == 9 || c == 12 || c == 8) {
                next();
            } else if (c == '/') {
                skipComment();
            } else {
                return;
            }
        }
    }

    public final void scanNumber() {
        char c;
        char c2;
        char c3;
        char c4;
        char c5;
        char c6;
        char c7;
        int i = this.bp;
        this.np = i;
        if (this.ch == '-') {
            this.sp++;
            int index = i + 1;
            this.bp = index;
            if (index >= this.len) {
                c7 = EOI;
            } else {
                c7 = this.text.charAt(index);
            }
            this.ch = c7;
        }
        while (true) {
            int index2 = this.ch;
            if (index2 < 48 || index2 > 57) {
                boolean isDouble = false;
            } else {
                this.sp++;
                int index3 = this.bp + 1;
                this.bp = index3;
                if (index3 >= this.len) {
                    c6 = EOI;
                } else {
                    c6 = this.text.charAt(index3);
                }
                this.ch = c6;
            }
        }
        boolean isDouble2 = false;
        if (this.ch == '.') {
            this.sp++;
            int index4 = this.bp + 1;
            this.bp = index4;
            if (index4 >= this.len) {
                c4 = EOI;
            } else {
                c4 = this.text.charAt(index4);
            }
            this.ch = c4;
            isDouble2 = true;
            while (true) {
                char c8 = this.ch;
                if (c8 < '0' || c8 > '9') {
                    break;
                }
                this.sp++;
                int index5 = this.bp + 1;
                this.bp = index5;
                if (index5 >= this.len) {
                    c5 = EOI;
                } else {
                    c5 = this.text.charAt(index5);
                }
                this.ch = c5;
            }
        }
        char c9 = this.ch;
        if (c9 == 'L') {
            this.sp++;
            next();
        } else if (c9 == 'S') {
            this.sp++;
            next();
        } else if (c9 == 'B') {
            this.sp++;
            next();
        } else if (c9 == 'F') {
            this.sp++;
            next();
            isDouble2 = true;
        } else if (c9 == 'D') {
            this.sp++;
            next();
            isDouble2 = true;
        } else if (c9 == 'e' || c9 == 'E') {
            this.sp++;
            int index6 = this.bp + 1;
            this.bp = index6;
            if (index6 >= this.len) {
                c = EOI;
            } else {
                c = this.text.charAt(index6);
            }
            this.ch = c;
            int index7 = this.ch;
            if (index7 == 43 || index7 == 45) {
                this.sp++;
                int index8 = this.bp + 1;
                this.bp = index8;
                if (index8 >= this.len) {
                    c3 = EOI;
                } else {
                    c3 = this.text.charAt(index8);
                }
                this.ch = c3;
            }
            while (true) {
                int index9 = this.ch;
                if (index9 < 48 || index9 > 57) {
                    char c10 = this.ch;
                } else {
                    this.sp++;
                    int index10 = this.bp + 1;
                    this.bp = index10;
                    if (index10 >= this.len) {
                        c2 = EOI;
                    } else {
                        c2 = this.text.charAt(index10);
                    }
                    this.ch = c2;
                }
            }
            char c102 = this.ch;
            if (c102 == 'D' || c102 == 'F') {
                this.sp++;
                next();
            }
            isDouble2 = true;
        }
        if (isDouble2) {
            this.token = 3;
        } else {
            this.token = 2;
        }
    }

    public boolean scanBoolean() {
        boolean value;
        int offset;
        if (this.text.startsWith("false", this.bp)) {
            offset = 5;
            value = false;
        } else if (this.text.startsWith("true", this.bp)) {
            offset = 4;
            value = true;
        } else {
            int offset2 = this.ch;
            if (offset2 == 49) {
                offset = 1;
                value = true;
            } else if (offset2 == 48) {
                offset = 1;
                value = false;
            } else {
                this.matchStat = -1;
                return false;
            }
        }
        this.bp += offset;
        this.ch = charAt(this.bp);
        return value;
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0078  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Number scanNumberValue() {
        /*
            r19 = this;
            r1 = r19
            int r2 = r1.bp
            r0 = 0
            r3 = 0
            r4 = 0
            r1.np = r4
            char r5 = r1.ch
            r6 = 45
            if (r5 != r6) goto L_0x002e
            r5 = 1
            r8 = -9223372036854775808
            int r10 = r1.np
            int r10 = r10 + 1
            r1.np = r10
            int r10 = r1.bp
            int r10 = r10 + 1
            r1.bp = r10
            int r11 = r1.len
            if (r10 < r11) goto L_0x0025
            r11 = 26
            goto L_0x002b
        L_0x0025:
            java.lang.String r11 = r1.text
            char r11 = r11.charAt(r10)
        L_0x002b:
            r1.ch = r11
            goto L_0x0034
        L_0x002e:
            r5 = 0
            r8 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
        L_0x0034:
            r10 = 0
            r12 = r0
        L_0x0037:
            char r0 = r1.ch
            r13 = 57
            r14 = 48
            if (r0 < r14) goto L_0x0076
            if (r0 > r13) goto L_0x0076
            int r0 = r0 + -48
            r13 = -922337203685477580(0xf333333333333334, double:-8.390303882365713E246)
            int r15 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r15 >= 0) goto L_0x004d
            r12 = 1
        L_0x004d:
            r13 = 10
            long r10 = r10 * r13
            long r13 = (long) r0
            long r13 = r13 + r8
            int r15 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r15 >= 0) goto L_0x0058
            r12 = 1
        L_0x0058:
            long r13 = (long) r0
            long r10 = r10 - r13
            int r0 = r1.np
            int r0 = r0 + 1
            r1.np = r0
            int r0 = r1.bp
            int r0 = r0 + 1
            r1.bp = r0
            int r13 = r1.len
            if (r0 < r13) goto L_0x006d
            r13 = 26
            goto L_0x0073
        L_0x006d:
            java.lang.String r13 = r1.text
            char r13 = r13.charAt(r0)
        L_0x0073:
            r1.ch = r13
            goto L_0x0037
        L_0x0076:
            if (r5 != 0) goto L_0x0079
            long r10 = -r10
        L_0x0079:
            char r0 = r1.ch
            r15 = 76
            r7 = 68
            r4 = 70
            if (r0 != r15) goto L_0x0092
            int r0 = r1.np
            int r0 = r0 + 1
            r1.np = r0
            r19.next()
            java.lang.Long r3 = java.lang.Long.valueOf(r10)
            r15 = r5
            goto L_0x00e1
        L_0x0092:
            r15 = 83
            if (r0 != r15) goto L_0x00a7
            int r0 = r1.np
            int r0 = r0 + 1
            r1.np = r0
            r19.next()
            int r0 = (int) r10
            short r0 = (short) r0
            java.lang.Short r3 = java.lang.Short.valueOf(r0)
            r15 = r5
            goto L_0x00e1
        L_0x00a7:
            r15 = 66
            if (r0 != r15) goto L_0x00bc
            int r0 = r1.np
            int r0 = r0 + 1
            r1.np = r0
            r19.next()
            int r0 = (int) r10
            byte r0 = (byte) r0
            java.lang.Byte r3 = java.lang.Byte.valueOf(r0)
            r15 = r5
            goto L_0x00e1
        L_0x00bc:
            if (r0 != r4) goto L_0x00ce
            int r0 = r1.np
            int r0 = r0 + 1
            r1.np = r0
            r19.next()
            float r0 = (float) r10
            java.lang.Float r3 = java.lang.Float.valueOf(r0)
            r15 = r5
            goto L_0x00e1
        L_0x00ce:
            if (r0 != r7) goto L_0x00e0
            int r0 = r1.np
            int r0 = r0 + 1
            r1.np = r0
            r19.next()
            r15 = r5
            double r4 = (double) r10
            java.lang.Double r3 = java.lang.Double.valueOf(r4)
            goto L_0x00e1
        L_0x00e0:
            r15 = r5
        L_0x00e1:
            r4 = 0
            r5 = 0
            char r0 = r1.ch
            r7 = 46
            if (r0 != r7) goto L_0x0127
            r4 = 1
            int r0 = r1.np
            int r0 = r0 + 1
            r1.np = r0
            int r0 = r1.bp
            int r0 = r0 + 1
            r1.bp = r0
            int r7 = r1.len
            if (r0 < r7) goto L_0x00fd
            r7 = 26
            goto L_0x0103
        L_0x00fd:
            java.lang.String r7 = r1.text
            char r7 = r7.charAt(r0)
        L_0x0103:
            r1.ch = r7
        L_0x0105:
            char r0 = r1.ch
            if (r0 < r14) goto L_0x0127
            if (r0 > r13) goto L_0x0127
            int r0 = r1.np
            int r0 = r0 + 1
            r1.np = r0
            int r0 = r1.bp
            int r0 = r0 + 1
            r1.bp = r0
            int r7 = r1.len
            if (r0 < r7) goto L_0x011e
            r7 = 26
            goto L_0x0124
        L_0x011e:
            java.lang.String r7 = r1.text
            char r7 = r7.charAt(r0)
        L_0x0124:
            r1.ch = r7
            goto L_0x0105
        L_0x0127:
            r7 = 0
            char r0 = r1.ch
            r13 = 101(0x65, float:1.42E-43)
            if (r0 == r13) goto L_0x0132
            r13 = 69
            if (r0 != r13) goto L_0x01ab
        L_0x0132:
            int r0 = r1.np
            int r0 = r0 + 1
            r1.np = r0
            int r0 = r1.bp
            int r0 = r0 + 1
            r1.bp = r0
            int r13 = r1.len
            if (r0 < r13) goto L_0x0145
            r13 = 26
            goto L_0x014b
        L_0x0145:
            java.lang.String r13 = r1.text
            char r13 = r13.charAt(r0)
        L_0x014b:
            r1.ch = r13
            char r0 = r1.ch
            r13 = 43
            if (r0 == r13) goto L_0x0155
            if (r0 != r6) goto L_0x0170
        L_0x0155:
            int r0 = r1.np
            int r0 = r0 + 1
            r1.np = r0
            int r0 = r1.bp
            int r0 = r0 + 1
            r1.bp = r0
            int r6 = r1.len
            if (r0 < r6) goto L_0x0168
            r6 = 26
            goto L_0x016e
        L_0x0168:
            java.lang.String r6 = r1.text
            char r6 = r6.charAt(r0)
        L_0x016e:
            r1.ch = r6
        L_0x0170:
            char r0 = r1.ch
            if (r0 < r14) goto L_0x0194
            r6 = 57
            if (r0 > r6) goto L_0x0194
            int r0 = r1.np
            int r0 = r0 + 1
            r1.np = r0
            int r0 = r1.bp
            int r0 = r0 + 1
            r1.bp = r0
            int r13 = r1.len
            if (r0 < r13) goto L_0x018b
            r13 = 26
            goto L_0x0191
        L_0x018b:
            java.lang.String r13 = r1.text
            char r13 = r13.charAt(r0)
        L_0x0191:
            r1.ch = r13
            goto L_0x0170
        L_0x0194:
            char r0 = r1.ch
            r6 = 68
            if (r0 == r6) goto L_0x019e
            r6 = 70
            if (r0 != r6) goto L_0x01aa
        L_0x019e:
            int r6 = r1.np
            int r6 = r6 + 1
            r1.np = r6
            char r6 = r1.ch
            r19.next()
            r7 = r6
        L_0x01aa:
            r5 = 1
        L_0x01ab:
            if (r4 != 0) goto L_0x01e9
            if (r5 != 0) goto L_0x01e9
            if (r12 == 0) goto L_0x01cc
            int r0 = r1.bp
            int r6 = r0 - r2
            char[] r13 = new char[r6]
            java.lang.String r14 = r1.text
            r16 = r3
            r3 = 0
            r14.getChars(r2, r0, r13, r3)
            java.lang.String r0 = new java.lang.String
            r0.<init>(r13)
            java.math.BigInteger r3 = new java.math.BigInteger
            r3.<init>(r0)
            r16 = r3
            goto L_0x01ce
        L_0x01cc:
            r16 = r3
        L_0x01ce:
            if (r16 != 0) goto L_0x01e8
            r13 = -2147483648(0xffffffff80000000, double:NaN)
            int r0 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x01e4
            r13 = 2147483647(0x7fffffff, double:1.060997895E-314)
            int r0 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r0 >= 0) goto L_0x01e4
            int r0 = (int) r10
            java.lang.Integer r16 = java.lang.Integer.valueOf(r0)
            goto L_0x01e8
        L_0x01e4:
            java.lang.Long r16 = java.lang.Long.valueOf(r10)
        L_0x01e8:
            return r16
        L_0x01e9:
            r16 = r3
            int r3 = r1.bp
            int r3 = r3 - r2
            if (r7 == 0) goto L_0x01f2
            int r3 = r3 + -1
        L_0x01f2:
            char[] r6 = new char[r3]
            java.lang.String r13 = r1.text
            int r14 = r2 + r3
            r0 = 0
            r13.getChars(r2, r14, r6, r0)
            if (r5 != 0) goto L_0x020d
            int r0 = r1.features
            com.alibaba.fastjson.parser.Feature r13 = com.alibaba.fastjson.parser.Feature.UseBigDecimal
            int r13 = r13.mask
            r0 = r0 & r13
            if (r0 == 0) goto L_0x020d
            java.math.BigDecimal r0 = new java.math.BigDecimal
            r0.<init>(r6)
            goto L_0x0225
        L_0x020d:
            java.lang.String r0 = new java.lang.String
            r0.<init>(r6)
            r13 = r0
            r0 = 70
            if (r7 != r0) goto L_0x021c
            java.lang.Float r0 = java.lang.Float.valueOf(r13)     // Catch:{ NumberFormatException -> 0x0226 }
            goto L_0x0224
        L_0x021c:
            double r17 = java.lang.Double.parseDouble(r13)     // Catch:{ NumberFormatException -> 0x0226 }
            java.lang.Double r0 = java.lang.Double.valueOf(r17)     // Catch:{ NumberFormatException -> 0x0226 }
        L_0x0224:
        L_0x0225:
            return r0
        L_0x0226:
            r0 = move-exception
            com.alibaba.fastjson.JSONException r14 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r17 = r2
            java.lang.String r2 = r0.getMessage()
            r1.append(r2)
            java.lang.String r2 = ", "
            r1.append(r2)
            java.lang.String r2 = r19.info()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r14.<init>(r1, r0)
            throw r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.JSONLexer.scanNumberValue():java.lang.Number");
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x00c9  */
    /* JADX WARNING: Removed duplicated region for block: B:35:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final long scanLongValue() {
        /*
            r11 = this;
            r0 = 0
            r11.np = r0
            char r0 = r11.ch
            r1 = 45
            if (r0 != r1) goto L_0x0040
            r0 = 1
            r1 = -9223372036854775808
            int r3 = r11.np
            int r3 = r3 + 1
            r11.np = r3
            int r3 = r11.bp
            int r3 = r3 + 1
            r11.bp = r3
            int r4 = r11.len
            if (r3 >= r4) goto L_0x0025
            java.lang.String r4 = r11.text
            char r4 = r4.charAt(r3)
            r11.ch = r4
            goto L_0x0046
        L_0x0025:
            com.alibaba.fastjson.JSONException r4 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "syntax error, "
            r5.append(r6)
            java.lang.String r6 = r11.info()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r4.<init>(r5)
            throw r4
        L_0x0040:
            r0 = 0
            r1 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
        L_0x0046:
            r3 = 0
        L_0x0048:
            char r5 = r11.ch
            r6 = 48
            if (r5 < r6) goto L_0x00c7
            r6 = 57
            if (r5 > r6) goto L_0x00c7
            int r5 = r5 + -48
            r6 = -922337203685477580(0xf333333333333334, double:-8.390303882365713E246)
            java.lang.String r8 = ", "
            java.lang.String r9 = "error long value, "
            int r10 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r10 < 0) goto L_0x00a8
            r6 = 10
            long r3 = r3 * r6
            long r6 = (long) r5
            long r6 = r6 + r1
            int r10 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r10 < 0) goto L_0x0089
            long r6 = (long) r5
            long r3 = r3 - r6
            int r5 = r11.np
            int r5 = r5 + 1
            r11.np = r5
            int r5 = r11.bp
            int r5 = r5 + 1
            r11.bp = r5
            int r6 = r11.len
            if (r5 < r6) goto L_0x0080
            r6 = 26
            goto L_0x0086
        L_0x0080:
            java.lang.String r6 = r11.text
            char r6 = r6.charAt(r5)
        L_0x0086:
            r11.ch = r6
            goto L_0x0048
        L_0x0089:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r9)
            r7.append(r3)
            r7.append(r8)
            java.lang.String r8 = r11.info()
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r6.<init>(r7)
            throw r6
        L_0x00a8:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r9)
            r7.append(r3)
            r7.append(r8)
            java.lang.String r8 = r11.info()
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r6.<init>(r7)
            throw r6
        L_0x00c7:
            if (r0 != 0) goto L_0x00ca
            long r3 = -r3
        L_0x00ca:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.JSONLexer.scanLongValue():long");
    }

    public final long longValue() throws NumberFormatException {
        long limit;
        int i;
        char c;
        long result = 0;
        boolean negative = false;
        int i2 = this.np;
        int i3 = this.np;
        int max = this.sp + i3;
        if (charAt(i3) == '-') {
            negative = true;
            limit = Long.MIN_VALUE;
            i2++;
        } else {
            limit = -9223372036854775807L;
        }
        if (i2 < max) {
            result = (long) (-(charAt(i2) - 48));
            i2++;
        }
        while (true) {
            if (i2 >= max) {
                break;
            }
            i = i2 + 1;
            if (i2 >= this.len) {
                c = EOI;
            } else {
                c = this.text.charAt(i2);
            }
            char chLocal = c;
            if (chLocal == 'L' || chLocal == 'S' || chLocal == 'B') {
                i2 = i;
            } else {
                int digit = chLocal - '0';
                if (result >= -922337203685477580L) {
                    long result2 = result * 10;
                    if (result2 >= ((long) digit) + limit) {
                        result = result2 - ((long) digit);
                        i2 = i;
                    } else {
                        throw new NumberFormatException(numberString());
                    }
                } else {
                    throw new NumberFormatException(numberString());
                }
            }
        }
        i2 = i;
        if (!negative) {
            return -result;
        }
        if (i2 > this.np + 1) {
            return result;
        }
        throw new NumberFormatException(numberString());
    }

    public final Number decimalValue(boolean decimal) {
        char chLocal = charAt((this.np + this.sp) - 1);
        if (chLocal == 'F') {
            try {
                return Float.valueOf(Float.parseFloat(numberString()));
            } catch (NumberFormatException ex) {
                throw new JSONException(ex.getMessage() + ", " + info());
            }
        } else if (chLocal == 'D') {
            return Double.valueOf(Double.parseDouble(numberString()));
        } else {
            if (decimal) {
                return decimalValue();
            }
            return Double.valueOf(Double.parseDouble(numberString()));
        }
    }

    public final BigDecimal decimalValue() {
        return new BigDecimal(numberString());
    }

    public boolean matchField(char[] fieldName) {
        if (!charArrayCompare(fieldName)) {
            return false;
        }
        this.bp += fieldName.length;
        int i = this.bp;
        if (i < this.len) {
            this.ch = this.text.charAt(i);
            char c = this.ch;
            char c2 = EOI;
            if (c == '{') {
                int index = this.bp + 1;
                this.bp = index;
                if (index < this.len) {
                    c2 = this.text.charAt(index);
                }
                this.ch = c2;
                this.token = 12;
            } else if (c == '[') {
                int index2 = this.bp + 1;
                this.bp = index2;
                if (index2 < this.len) {
                    c2 = this.text.charAt(index2);
                }
                this.ch = c2;
                this.token = 14;
            } else {
                nextToken();
            }
            return true;
        }
        throw new JSONException("unclosed str, " + info());
    }

    private boolean charArrayCompare(char[] chars) {
        int destLen = chars.length;
        if (this.bp + destLen > this.len) {
            return false;
        }
        for (int i = 0; i < destLen; i++) {
            if (chars[i] != this.text.charAt(this.bp + i)) {
                return false;
            }
        }
        return true;
    }

    public int scanFieldInt(char[] fieldName) {
        int offset;
        char chLocal;
        int offset2;
        char c;
        char c2;
        this.matchStat = 0;
        if (!charArrayCompare(fieldName)) {
            this.matchStat = -2;
            return 0;
        }
        int offset3 = fieldName.length;
        int offset4 = offset3 + 1;
        char chLocal2 = charAt(this.bp + offset3);
        boolean quote = false;
        char c3 = EOI;
        if (chLocal2 == '\"') {
            quote = true;
            int offset5 = offset4 + 1;
            int index = this.bp + offset4;
            if (index >= this.len) {
                c2 = EOI;
            } else {
                c2 = this.text.charAt(index);
            }
            chLocal2 = c2;
            offset4 = offset5;
        }
        if (chLocal2 < '0' || chLocal2 > '9') {
            this.matchStat = -1;
            return 0;
        }
        int value = chLocal2 - '0';
        while (true) {
            offset = offset4 + 1;
            chLocal = charAt(this.bp + offset4);
            if (chLocal >= '0' && chLocal <= '9') {
                value = (value * 10) + (chLocal - '0');
                offset4 = offset;
            }
        }
        if (chLocal == '.') {
            this.matchStat = -1;
            return 0;
        }
        if (chLocal != '\"') {
            offset2 = offset;
        } else if (!quote) {
            this.matchStat = -1;
            return 0;
        } else {
            offset2 = offset + 1;
            int index2 = this.bp + offset;
            if (index2 >= this.len) {
                c = EOI;
            } else {
                c = this.text.charAt(index2);
            }
            chLocal = c;
        }
        if (value < 0) {
            this.matchStat = -1;
            return 0;
        } else if (chLocal == ',') {
            this.bp += offset2 - 1;
            int index3 = this.bp + 1;
            this.bp = index3;
            if (index3 < this.len) {
                c3 = this.text.charAt(index3);
            }
            this.ch = c3;
            this.matchStat = 3;
            this.token = 16;
            return value;
        } else if (chLocal == '}') {
            int offset6 = offset2 + 1;
            char chLocal3 = charAt(this.bp + offset2);
            if (chLocal3 == ',') {
                this.token = 16;
                this.bp += offset6 - 1;
                int index4 = this.bp + 1;
                this.bp = index4;
                if (index4 < this.len) {
                    c3 = this.text.charAt(index4);
                }
                this.ch = c3;
            } else if (chLocal3 == ']') {
                this.token = 15;
                this.bp += offset6 - 1;
                int index5 = this.bp + 1;
                this.bp = index5;
                if (index5 < this.len) {
                    c3 = this.text.charAt(index5);
                }
                this.ch = c3;
            } else if (chLocal3 == '}') {
                this.token = 13;
                this.bp += offset6 - 1;
                int index6 = this.bp + 1;
                this.bp = index6;
                if (index6 < this.len) {
                    c3 = this.text.charAt(index6);
                }
                this.ch = c3;
            } else if (chLocal3 == 26) {
                this.token = 20;
                this.bp += offset6 - 1;
                this.ch = EOI;
            } else {
                this.matchStat = -1;
                return 0;
            }
            this.matchStat = 4;
            return value;
        } else {
            this.matchStat = -1;
            return 0;
        }
    }

    public long scanFieldLong(char[] fieldName) {
        char chLocal;
        int offset;
        char c;
        char chLocal2;
        char c2;
        char c3;
        this.matchStat = 0;
        if (!charArrayCompare(fieldName)) {
            this.matchStat = -2;
            return 0;
        }
        int offset2 = fieldName.length;
        int offset3 = offset2 + 1;
        int index = this.bp + offset2;
        int i = this.len;
        char c4 = EOI;
        if (index >= i) {
            chLocal = EOI;
        } else {
            chLocal = this.text.charAt(index);
        }
        boolean quote = false;
        if (chLocal == '\"') {
            quote = true;
            int offset4 = offset3 + 1;
            int index2 = this.bp + offset3;
            if (index2 >= this.len) {
                c3 = EOI;
            } else {
                c3 = this.text.charAt(index2);
            }
            chLocal = c3;
            offset3 = offset4;
        }
        if (chLocal >= '0') {
            char c5 = '9';
            if (chLocal <= '9') {
                long value = (long) (chLocal - '0');
                while (true) {
                    offset = offset3 + 1;
                    int index3 = this.bp + offset3;
                    if (index3 >= this.len) {
                        c = EOI;
                    } else {
                        c = this.text.charAt(index3);
                    }
                    chLocal2 = c;
                    if (chLocal2 < '0' || chLocal2 > c5) {
                        long value2 = value;
                    } else {
                        long j = value;
                        value = (10 * value) + ((long) (chLocal2 - '0'));
                        offset3 = offset;
                        c5 = '9';
                    }
                }
                long value22 = value;
                if (chLocal2 == '.') {
                    this.matchStat = -1;
                    return 0;
                }
                if (chLocal2 == '\"') {
                    if (!quote) {
                        this.matchStat = -1;
                        return 0;
                    }
                    int offset5 = offset + 1;
                    int index4 = this.bp + offset;
                    if (index4 >= this.len) {
                        c2 = EOI;
                    } else {
                        c2 = this.text.charAt(index4);
                    }
                    chLocal2 = c2;
                    offset = offset5;
                }
                if (value22 < 0) {
                    this.matchStat = -1;
                    return 0;
                } else if (chLocal2 == ',') {
                    this.bp += offset - 1;
                    int index5 = this.bp + 1;
                    this.bp = index5;
                    if (index5 < this.len) {
                        c4 = this.text.charAt(index5);
                    }
                    this.ch = c4;
                    this.matchStat = 3;
                    this.token = 16;
                    return value22;
                } else if (chLocal2 == '}') {
                    int offset6 = offset + 1;
                    char chLocal3 = charAt(this.bp + offset);
                    if (chLocal3 == ',') {
                        this.token = 16;
                        this.bp += offset6 - 1;
                        int index6 = this.bp + 1;
                        this.bp = index6;
                        if (index6 < this.len) {
                            c4 = this.text.charAt(index6);
                        }
                        this.ch = c4;
                    } else if (chLocal3 == ']') {
                        this.token = 15;
                        this.bp += offset6 - 1;
                        int index7 = this.bp + 1;
                        this.bp = index7;
                        if (index7 < this.len) {
                            c4 = this.text.charAt(index7);
                        }
                        this.ch = c4;
                    } else if (chLocal3 == '}') {
                        this.token = 13;
                        this.bp += offset6 - 1;
                        int index8 = this.bp + 1;
                        this.bp = index8;
                        if (index8 < this.len) {
                            c4 = this.text.charAt(index8);
                        }
                        this.ch = c4;
                    } else if (chLocal3 == 26) {
                        this.token = 20;
                        this.bp += offset6 - 1;
                        this.ch = EOI;
                    } else {
                        this.matchStat = -1;
                        return 0;
                    }
                    this.matchStat = 4;
                    return value22;
                } else {
                    this.matchStat = -1;
                    return 0;
                }
            }
        }
        this.matchStat = -1;
        return 0;
    }

    public String scanFieldString(char[] fieldName) {
        String strVal;
        char c;
        this.matchStat = 0;
        if (!charArrayCompare(fieldName)) {
            this.matchStat = -2;
            return this.stringDefaultValue;
        }
        int offset = fieldName.length;
        int offset2 = offset + 1;
        int index = this.bp + offset;
        if (index >= this.len) {
            throw new JSONException("unclosed str, " + info());
        } else if (this.text.charAt(index) != '\"') {
            this.matchStat = -1;
            return this.stringDefaultValue;
        } else {
            boolean hasSpecial2 = false;
            int startIndex = this.bp + offset2;
            int endIndex = this.text.indexOf(34, startIndex);
            if (endIndex != -1) {
                if (V6) {
                    strVal = this.text.substring(startIndex, endIndex);
                } else {
                    int chars_len = endIndex - startIndex;
                    strVal = new String(sub_chars(this.bp + offset2, chars_len), 0, chars_len);
                }
                if (strVal.indexOf(92) != -1) {
                    while (true) {
                        int slashCount = 0;
                        int i = endIndex - 1;
                        while (i >= 0 && this.text.charAt(i) == '\\') {
                            hasSpecial2 = true;
                            slashCount++;
                            i--;
                        }
                        if (slashCount % 2 == 0) {
                            break;
                        }
                        endIndex = this.text.indexOf(34, endIndex + 1);
                    }
                    int chars_len2 = endIndex - startIndex;
                    char[] chars = sub_chars(this.bp + offset2, chars_len2);
                    if (hasSpecial2) {
                        strVal = readString(chars, chars_len2);
                    } else {
                        strVal = new String(chars, 0, chars_len2);
                        if (strVal.indexOf(92) != -1) {
                            strVal = readString(chars, chars_len2);
                        }
                    }
                }
                int endIndex2 = endIndex + 1;
                int index2 = endIndex2;
                int i2 = this.len;
                char c2 = EOI;
                if (index2 >= i2) {
                    c = EOI;
                } else {
                    c = this.text.charAt(index2);
                }
                char chLocal = c;
                if (chLocal == ',') {
                    this.bp = endIndex2;
                    int index3 = this.bp + 1;
                    this.bp = index3;
                    if (index3 < this.len) {
                        c2 = this.text.charAt(index3);
                    }
                    this.ch = c2;
                    this.matchStat = 3;
                    this.token = 16;
                    return strVal;
                } else if (chLocal == '}') {
                    int endIndex3 = endIndex2 + 1;
                    char chLocal2 = charAt(endIndex3);
                    if (chLocal2 == ',') {
                        this.token = 16;
                        this.bp = endIndex3;
                        next();
                    } else if (chLocal2 == ']') {
                        this.token = 15;
                        this.bp = endIndex3;
                        next();
                    } else if (chLocal2 == '}') {
                        this.token = 13;
                        this.bp = endIndex3;
                        next();
                    } else if (chLocal2 == 26) {
                        this.token = 20;
                        this.bp = endIndex3;
                        this.ch = EOI;
                    } else {
                        this.matchStat = -1;
                        return this.stringDefaultValue;
                    }
                    this.matchStat = 4;
                    return strVal;
                } else {
                    this.matchStat = -1;
                    return this.stringDefaultValue;
                }
            } else {
                throw new JSONException("unclosed str, " + info());
            }
        }
    }

    public boolean scanFieldBoolean(char[] fieldName) {
        boolean value;
        int offset;
        this.matchStat = 0;
        if (!charArrayCompare(fieldName)) {
            this.matchStat = -2;
            return false;
        }
        int offset2 = fieldName.length;
        if (this.text.startsWith("false", this.bp + offset2)) {
            offset = offset2 + 5;
            value = false;
        } else if (this.text.startsWith("true", this.bp + offset2)) {
            offset = offset2 + 4;
            value = true;
        } else if (this.text.startsWith("\"false\"", this.bp + offset2)) {
            offset = offset2 + 7;
            value = false;
        } else if (this.text.startsWith("\"true\"", this.bp + offset2)) {
            offset = offset2 + 6;
            value = true;
        } else {
            this.matchStat = -1;
            return false;
        }
        int offset3 = offset + 1;
        char chLocal = charAt(this.bp + offset);
        char c = EOI;
        if (chLocal == ',') {
            this.bp += offset3 - 1;
            int index = this.bp + 1;
            this.bp = index;
            if (index < this.len) {
                c = this.text.charAt(index);
            }
            this.ch = c;
            this.matchStat = 3;
            this.token = 16;
            return value;
        } else if (chLocal == '}') {
            int offset4 = offset3 + 1;
            char chLocal2 = charAt(this.bp + offset3);
            if (chLocal2 == ',') {
                this.token = 16;
                this.bp += offset4 - 1;
                int index2 = this.bp + 1;
                this.bp = index2;
                if (index2 < this.len) {
                    c = this.text.charAt(index2);
                }
                this.ch = c;
            } else if (chLocal2 == ']') {
                this.token = 15;
                this.bp += offset4 - 1;
                int index3 = this.bp + 1;
                this.bp = index3;
                if (index3 < this.len) {
                    c = this.text.charAt(index3);
                }
                this.ch = c;
            } else if (chLocal2 == '}') {
                this.token = 13;
                this.bp += offset4 - 1;
                int index4 = this.bp + 1;
                this.bp = index4;
                if (index4 < this.len) {
                    c = this.text.charAt(index4);
                }
                this.ch = c;
            } else if (chLocal2 == 26) {
                this.token = 20;
                this.bp += offset4 - 1;
                this.ch = EOI;
            } else {
                this.matchStat = -1;
                return false;
            }
            this.matchStat = 4;
            return value;
        } else {
            this.matchStat = -1;
            return false;
        }
    }

    public final float scanFieldFloat(char[] fieldName) {
        int offset;
        char chLocal;
        this.matchStat = 0;
        if (!charArrayCompare(fieldName)) {
            this.matchStat = -2;
            return 0.0f;
        }
        int offset2 = fieldName.length;
        int offset3 = offset2 + 1;
        char chLocal2 = charAt(this.bp + offset2);
        if (chLocal2 < '0' || chLocal2 > '9') {
            this.matchStat = -1;
            return 0.0f;
        }
        while (true) {
            offset = offset3 + 1;
            chLocal = charAt(this.bp + offset3);
            if (chLocal >= '0' && chLocal <= '9') {
                offset3 = offset;
            }
        }
        if (chLocal == '.') {
            int offset4 = offset + 1;
            char chLocal3 = charAt(this.bp + offset);
            if (chLocal3 >= '0' && chLocal3 <= '9') {
                while (true) {
                    offset = offset4 + 1;
                    chLocal = charAt(this.bp + offset4);
                    if (chLocal < '0' || chLocal > '9') {
                        break;
                    }
                    offset4 = offset;
                }
            } else {
                this.matchStat = -1;
                return 0.0f;
            }
        }
        int i = this.bp;
        int start = fieldName.length + i;
        float value = Float.parseFloat(subString(start, ((i + offset) - start) - 1));
        if (chLocal == ',') {
            this.bp += offset - 1;
            next();
            this.matchStat = 3;
            this.token = 16;
            return value;
        } else if (chLocal == '}') {
            int offset5 = offset + 1;
            char chLocal4 = charAt(this.bp + offset);
            if (chLocal4 == ',') {
                this.token = 16;
                this.bp += offset5 - 1;
                next();
            } else if (chLocal4 == ']') {
                this.token = 15;
                this.bp += offset5 - 1;
                next();
            } else if (chLocal4 == '}') {
                this.token = 13;
                this.bp += offset5 - 1;
                next();
            } else if (chLocal4 == 26) {
                this.bp += offset5 - 1;
                this.token = 20;
                this.ch = EOI;
            } else {
                this.matchStat = -1;
                return 0.0f;
            }
            this.matchStat = 4;
            return value;
        } else {
            this.matchStat = -1;
            return 0.0f;
        }
    }

    public final double scanFieldDouble(char[] fieldName) {
        int offset;
        char chLocal;
        int offset2;
        char chLocal2;
        this.matchStat = 0;
        if (!charArrayCompare(fieldName)) {
            this.matchStat = -2;
            return 0.0d;
        }
        int offset3 = fieldName.length;
        int offset4 = offset3 + 1;
        char chLocal3 = charAt(this.bp + offset3);
        if (chLocal3 < '0' || chLocal3 > '9') {
            this.matchStat = -1;
            return 0.0d;
        }
        while (true) {
            offset = offset4 + 1;
            chLocal = charAt(this.bp + offset4);
            if (chLocal >= '0' && chLocal <= '9') {
                offset4 = offset;
            }
        }
        if (chLocal == '.') {
            int offset5 = offset + 1;
            char chLocal4 = charAt(this.bp + offset);
            if (chLocal4 >= '0' && chLocal4 <= '9') {
                while (true) {
                    offset = offset5 + 1;
                    chLocal = charAt(this.bp + offset5);
                    if (chLocal < '0' || chLocal > '9') {
                        break;
                    }
                    offset5 = offset;
                }
            } else {
                this.matchStat = -1;
                return 0.0d;
            }
        }
        if (chLocal2 == 'e' || chLocal2 == 'E') {
            int offset6 = offset2 + 1;
            chLocal2 = charAt(this.bp + offset2);
            if (chLocal2 == '+' || chLocal2 == '-') {
                offset2 = offset6 + 1;
                chLocal2 = charAt(this.bp + offset6);
            } else {
                offset2 = offset6;
            }
            while (chLocal2 >= '0' && chLocal2 <= '9') {
                chLocal2 = charAt(this.bp + offset2);
                offset2++;
            }
        }
        int i = this.bp;
        int start = fieldName.length + i;
        double value = Double.parseDouble(subString(start, ((i + offset2) - start) - 1));
        if (chLocal2 == ',') {
            this.bp += offset2 - 1;
            next();
            this.matchStat = 3;
            this.token = 16;
            return value;
        } else if (chLocal2 == '}') {
            int offset7 = offset2 + 1;
            char chLocal5 = charAt(this.bp + offset2);
            if (chLocal5 == ',') {
                this.token = 16;
                this.bp += offset7 - 1;
                next();
            } else if (chLocal5 == ']') {
                this.token = 15;
                this.bp += offset7 - 1;
                next();
            } else if (chLocal5 == '}') {
                this.token = 13;
                this.bp += offset7 - 1;
                next();
            } else if (chLocal5 == 26) {
                this.token = 20;
                this.bp += offset7 - 1;
                this.ch = EOI;
            } else {
                this.matchStat = -1;
                return 0.0d;
            }
            this.matchStat = 4;
            return value;
        } else {
            this.matchStat = -1;
            return 0.0d;
        }
    }

    public String scanFieldSymbol(char[] fieldName, SymbolTable symbolTable) {
        this.matchStat = 0;
        if (!charArrayCompare(fieldName)) {
            this.matchStat = -2;
            return null;
        }
        int offset = fieldName.length;
        int offset2 = offset + 1;
        if (charAt(this.bp + offset) != '\"') {
            this.matchStat = -1;
            return null;
        }
        int hash = 0;
        while (true) {
            int offset3 = offset2 + 1;
            char chLocal = charAt(this.bp + offset2);
            if (chLocal == '\"') {
                int i = this.bp;
                int start = fieldName.length + i + 1;
                String strVal = symbolTable.addSymbol(this.text, start, ((i + offset3) - start) - 1, hash);
                int offset4 = offset3 + 1;
                char chLocal2 = charAt(this.bp + offset3);
                char c = EOI;
                if (chLocal2 == ',') {
                    this.bp += offset4 - 1;
                    int index = this.bp + 1;
                    this.bp = index;
                    if (index < this.len) {
                        c = this.text.charAt(index);
                    }
                    this.ch = c;
                    this.matchStat = 3;
                    return strVal;
                } else if (chLocal2 == '}') {
                    int offset5 = offset4 + 1;
                    char chLocal3 = charAt(this.bp + offset4);
                    if (chLocal3 == ',') {
                        this.token = 16;
                        this.bp += offset5 - 1;
                        next();
                    } else if (chLocal3 == ']') {
                        this.token = 15;
                        this.bp += offset5 - 1;
                        next();
                    } else if (chLocal3 == '}') {
                        this.token = 13;
                        this.bp += offset5 - 1;
                        next();
                    } else if (chLocal3 == 26) {
                        this.token = 20;
                        this.bp += offset5 - 1;
                        this.ch = EOI;
                    } else {
                        this.matchStat = -1;
                        return null;
                    }
                    this.matchStat = 4;
                    return strVal;
                } else {
                    this.matchStat = -1;
                    return null;
                }
            } else {
                hash = (hash * 31) + chLocal;
                if (chLocal == '\\') {
                    this.matchStat = -1;
                    return null;
                }
                offset2 = offset3;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:142:0x0381 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0383  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean scanISO8601DateIfMatch(boolean r39) {
        /*
            r38 = this;
            r9 = r38
            java.lang.String r0 = r9.text
            int r0 = r0.length()
            int r1 = r9.bp
            int r10 = r0 - r1
            r11 = 2
            r12 = 13
            r13 = 57
            r14 = 5
            r15 = 1
            r8 = 48
            r7 = 0
            if (r39 != 0) goto L_0x0079
            if (r10 <= r12) goto L_0x0079
            java.lang.String r0 = r9.text
            java.lang.String r2 = "/Date("
            boolean r0 = r0.startsWith(r2, r1)
            if (r0 == 0) goto L_0x0079
            int r0 = r9.bp
            int r0 = r0 + r10
            int r0 = r0 - r15
            char r0 = r9.charAt(r0)
            r1 = 47
            if (r0 != r1) goto L_0x0079
            int r0 = r9.bp
            int r0 = r0 + r10
            int r0 = r0 - r11
            char r0 = r9.charAt(r0)
            r1 = 41
            if (r0 != r1) goto L_0x0079
            r0 = -1
            r1 = 6
        L_0x003e:
            if (r1 >= r10) goto L_0x0055
            int r2 = r9.bp
            int r2 = r2 + r1
            char r2 = r9.charAt(r2)
            r3 = 43
            if (r2 != r3) goto L_0x004d
            r0 = r1
            goto L_0x0052
        L_0x004d:
            if (r2 < r8) goto L_0x0055
            if (r2 <= r13) goto L_0x0052
            goto L_0x0055
        L_0x0052:
            int r1 = r1 + 1
            goto L_0x003e
        L_0x0055:
            r1 = -1
            if (r0 != r1) goto L_0x0059
            return r7
        L_0x0059:
            int r1 = r9.bp
            int r1 = r1 + 6
            int r2 = r0 - r1
            java.lang.String r2 = r9.subString(r1, r2)
            long r3 = java.lang.Long.parseLong(r2)
            java.util.TimeZone r5 = r9.timeZone
            java.util.Locale r6 = r9.locale
            java.util.Calendar r5 = java.util.Calendar.getInstance(r5, r6)
            r9.calendar = r5
            java.util.Calendar r5 = r9.calendar
            r5.setTimeInMillis(r3)
            r9.token = r14
            return r15
        L_0x0079:
            r6 = 8
            r5 = 12
            r4 = 11
            r3 = 14
            r2 = 10
            if (r10 == r6) goto L_0x03f0
            if (r10 == r3) goto L_0x03f0
            r0 = 17
            if (r10 != r0) goto L_0x0099
            r11 = 48
            r12 = 14
            r13 = 11
            r14 = 13
            r15 = 12
            r16 = 10
            goto L_0x03fc
        L_0x0099:
            if (r10 >= r2) goto L_0x009c
            return r7
        L_0x009c:
            int r0 = r9.bp
            int r0 = r0 + 4
            char r0 = r9.charAt(r0)
            r1 = 45
            if (r0 == r1) goto L_0x00a9
            return r7
        L_0x00a9:
            int r0 = r9.bp
            int r0 = r0 + 7
            char r0 = r9.charAt(r0)
            if (r0 == r1) goto L_0x00b4
            return r7
        L_0x00b4:
            int r0 = r9.bp
            char r24 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + r15
            char r25 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + r11
            char r26 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 3
            char r27 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + r14
            char r28 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 6
            char r29 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + r6
            char r30 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 9
            char r31 = r9.charAt(r0)
            r16 = r24
            r17 = r25
            r18 = r26
            r19 = r27
            r20 = r28
            r21 = r29
            r22 = r30
            r23 = r31
            boolean r0 = checkDate(r16, r17, r18, r19, r20, r21, r22, r23)
            if (r0 != 0) goto L_0x0105
            return r7
        L_0x0105:
            r0 = r38
            r6 = 45
            r1 = r24
            r16 = 10
            r2 = r25
            r11 = 14
            r3 = r26
            r13 = 11
            r4 = r27
            r15 = 12
            r5 = r28
            r6 = r29
            r14 = 0
            r7 = r30
            r8 = r31
            r0.setCalendar(r1, r2, r3, r4, r5, r6, r7, r8)
            int r0 = r9.bp
            int r0 = r0 + 10
            char r0 = r9.charAt(r0)
            r1 = 84
            if (r0 == r1) goto L_0x0167
            r1 = 32
            if (r0 != r1) goto L_0x0138
            if (r39 != 0) goto L_0x0138
            goto L_0x0167
        L_0x0138:
            r1 = 34
            if (r0 == r1) goto L_0x0142
            r1 = 26
            if (r0 != r1) goto L_0x0141
            goto L_0x0142
        L_0x0141:
            return r14
        L_0x0142:
            java.util.Calendar r1 = r9.calendar
            r1.set(r13, r14)
            java.util.Calendar r1 = r9.calendar
            r1.set(r15, r14)
            java.util.Calendar r1 = r9.calendar
            r1.set(r12, r14)
            java.util.Calendar r1 = r9.calendar
            r1.set(r11, r14)
            int r1 = r9.bp
            int r1 = r1 + 10
            r9.bp = r1
            char r1 = r9.charAt(r1)
            r9.ch = r1
            r1 = 5
            r9.token = r1
            r1 = 1
            return r1
        L_0x0167:
            r1 = 19
            if (r10 >= r1) goto L_0x016c
            return r14
        L_0x016c:
            int r1 = r9.bp
            int r1 = r1 + r12
            char r1 = r9.charAt(r1)
            r2 = 58
            if (r1 == r2) goto L_0x0178
            return r14
        L_0x0178:
            int r1 = r9.bp
            int r1 = r1 + 16
            char r1 = r9.charAt(r1)
            r2 = 58
            if (r1 == r2) goto L_0x0185
            return r14
        L_0x0185:
            int r1 = r9.bp
            int r1 = r1 + r13
            char r1 = r9.charAt(r1)
            int r2 = r9.bp
            int r2 = r2 + r15
            char r8 = r9.charAt(r2)
            int r2 = r9.bp
            int r2 = r2 + r11
            char r21 = r9.charAt(r2)
            int r2 = r9.bp
            int r2 = r2 + 15
            char r22 = r9.charAt(r2)
            int r2 = r9.bp
            int r2 = r2 + 17
            char r23 = r9.charAt(r2)
            int r2 = r9.bp
            int r2 = r2 + 18
            char r34 = r9.charAt(r2)
            r2 = r1
            r3 = r8
            r4 = r21
            r5 = r22
            r6 = r23
            r7 = r34
            boolean r2 = checkTime(r2, r3, r4, r5, r6, r7)
            if (r2 != 0) goto L_0x01c3
            return r14
        L_0x01c3:
            int r2 = r1 + -48
            int r2 = r2 * 10
            int r3 = r8 + -48
            int r2 = r2 + r3
            int r3 = r21 + -48
            int r3 = r3 * 10
            int r4 = r22 + -48
            int r3 = r3 + r4
            int r4 = r23 + -48
            int r4 = r4 * 10
            int r5 = r34 + -48
            int r4 = r4 + r5
            java.util.Calendar r5 = r9.calendar
            r5.set(r13, r2)
            java.util.Calendar r5 = r9.calendar
            r5.set(r15, r3)
            java.util.Calendar r5 = r9.calendar
            r5.set(r12, r4)
            int r5 = r9.bp
            int r5 = r5 + 19
            char r5 = r9.charAt(r5)
            r6 = 46
            if (r5 != r6) goto L_0x03b0
            r6 = 23
            if (r10 >= r6) goto L_0x01f8
            return r14
        L_0x01f8:
            int r6 = r9.bp
            int r6 = r6 + 20
            char r6 = r9.charAt(r6)
            r7 = 48
            if (r6 < r7) goto L_0x03aa
            r12 = 57
            if (r6 <= r12) goto L_0x020f
            r32 = r0
            r17 = r1
            r0 = 0
            goto L_0x03af
        L_0x020f:
            int[] r12 = digits
            r12 = r12[r6]
            r13 = 1
            int r15 = r9.bp
            int r15 = r15 + 21
            char r15 = r9.charAt(r15)
            if (r15 < r7) goto L_0x022b
            r14 = 57
            if (r15 > r14) goto L_0x022b
            int r14 = r12 * 10
            int[] r35 = digits
            r35 = r35[r15]
            int r12 = r14 + r35
            r13 = 2
        L_0x022b:
            r14 = 2
            if (r13 != r14) goto L_0x0245
            int r14 = r9.bp
            int r14 = r14 + 22
            char r14 = r9.charAt(r14)
            if (r14 < r7) goto L_0x0245
            r15 = 57
            if (r14 > r15) goto L_0x0245
            int r15 = r12 * 10
            int[] r35 = digits
            r35 = r35[r14]
            int r12 = r15 + r35
            r13 = 3
        L_0x0245:
            java.util.Calendar r14 = r9.calendar
            r14.set(r11, r12)
            r11 = 0
            int r14 = r9.bp
            int r14 = r14 + 20
            int r14 = r14 + r13
            char r14 = r9.charAt(r14)
            r15 = 43
            if (r14 == r15) goto L_0x029a
            r15 = 45
            if (r14 != r15) goto L_0x025f
            r32 = r0
            goto L_0x029e
        L_0x025f:
            r7 = 90
            if (r14 != r7) goto L_0x0292
            r11 = 1
            java.util.Calendar r7 = r9.calendar
            java.util.TimeZone r7 = r7.getTimeZone()
            int r7 = r7.getRawOffset()
            if (r7 == 0) goto L_0x028a
            r7 = 0
            java.lang.String[] r15 = java.util.TimeZone.getAvailableIDs(r7)
            int r7 = r15.length
            if (r7 <= 0) goto L_0x0287
            r7 = 0
            r16 = r15[r7]
            java.util.TimeZone r7 = java.util.TimeZone.getTimeZone(r16)
            r32 = r0
            java.util.Calendar r0 = r9.calendar
            r0.setTimeZone(r7)
            goto L_0x028c
        L_0x0287:
            r32 = r0
            goto L_0x028c
        L_0x028a:
            r32 = r0
        L_0x028c:
            r17 = r1
            r18 = r12
            goto L_0x036f
        L_0x0292:
            r32 = r0
            r17 = r1
            r18 = r12
            goto L_0x036f
        L_0x029a:
            r32 = r0
            r15 = 45
        L_0x029e:
            int r0 = r9.bp
            int r0 = r0 + 20
            int r0 = r0 + r13
            r19 = 1
            int r0 = r0 + 1
            char r0 = r9.charAt(r0)
            if (r0 < r7) goto L_0x03a0
            r15 = 49
            if (r0 <= r15) goto L_0x02bc
            r35 = r0
            r17 = r1
            r37 = r11
            r18 = r12
            r0 = 0
            goto L_0x03a9
        L_0x02bc:
            int r15 = r9.bp
            int r15 = r15 + 20
            int r15 = r15 + r13
            r17 = 2
            int r15 = r15 + 2
            char r15 = r9.charAt(r15)
            if (r15 < r7) goto L_0x0396
            r7 = 57
            if (r15 <= r7) goto L_0x02d9
            r35 = r0
            r17 = r1
            r37 = r11
            r18 = r12
            goto L_0x039e
        L_0x02d9:
            int r7 = r9.bp
            int r7 = r7 + 20
            int r7 = r7 + r13
            int r7 = r7 + 3
            char r7 = r9.charAt(r7)
            r17 = r1
            r1 = 58
            if (r7 != r1) goto L_0x0317
            int r1 = r9.bp
            int r1 = r1 + 20
            int r1 = r1 + r13
            int r1 = r1 + 4
            char r1 = r9.charAt(r1)
            r18 = r12
            r12 = 48
            if (r1 == r12) goto L_0x02fe
            r33 = 0
            return r33
        L_0x02fe:
            r33 = 0
            int r12 = r9.bp
            int r12 = r12 + 20
            int r12 = r12 + r13
            r20 = 5
            int r12 = r12 + 5
            char r12 = r9.charAt(r12)
            r37 = r11
            r11 = 48
            if (r12 == r11) goto L_0x0314
            return r33
        L_0x0314:
            r1 = 6
            r11 = r1
            goto L_0x0333
        L_0x0317:
            r37 = r11
            r18 = r12
            r11 = 48
            if (r7 != r11) goto L_0x0331
            int r1 = r9.bp
            int r1 = r1 + 20
            int r1 = r1 + r13
            int r1 = r1 + 4
            char r1 = r9.charAt(r1)
            if (r1 == r11) goto L_0x032e
            r11 = 0
            return r11
        L_0x032e:
            r1 = 5
            r11 = r1
            goto L_0x0333
        L_0x0331:
            r1 = 3
            r11 = r1
        L_0x0333:
            int[] r1 = digits
            r12 = r1[r0]
            int r12 = r12 * 10
            r1 = r1[r15]
            int r12 = r12 + r1
            int r12 = r12 * 3600
            int r12 = r12 * 1000
            r1 = 45
            if (r14 != r1) goto L_0x0345
            int r12 = -r12
        L_0x0345:
            java.util.Calendar r1 = r9.calendar
            java.util.TimeZone r1 = r1.getTimeZone()
            int r1 = r1.getRawOffset()
            if (r1 == r12) goto L_0x036c
            java.lang.String[] r1 = java.util.TimeZone.getAvailableIDs(r12)
            r35 = r0
            int r0 = r1.length
            if (r0 <= 0) goto L_0x0369
            r0 = 0
            r16 = r1[r0]
            java.util.TimeZone r0 = java.util.TimeZone.getTimeZone(r16)
            r16 = r1
            java.util.Calendar r1 = r9.calendar
            r1.setTimeZone(r0)
            goto L_0x036e
        L_0x0369:
            r16 = r1
            goto L_0x036e
        L_0x036c:
            r35 = r0
        L_0x036e:
        L_0x036f:
            int r0 = r9.bp
            int r1 = r13 + 20
            int r1 = r1 + r11
            int r0 = r0 + r1
            char r0 = r9.charAt(r0)
            r1 = 26
            if (r0 == r1) goto L_0x0383
            r1 = 34
            if (r0 == r1) goto L_0x0383
            r1 = 0
            return r1
        L_0x0383:
            int r1 = r9.bp
            int r7 = r13 + 20
            int r7 = r7 + r11
            int r1 = r1 + r7
            r9.bp = r1
            char r1 = r9.charAt(r1)
            r9.ch = r1
            r1 = 5
            r9.token = r1
            r1 = 1
            return r1
        L_0x0396:
            r35 = r0
            r17 = r1
            r37 = r11
            r18 = r12
        L_0x039e:
            r0 = 0
            return r0
        L_0x03a0:
            r35 = r0
            r17 = r1
            r37 = r11
            r18 = r12
            r0 = 0
        L_0x03a9:
            return r0
        L_0x03aa:
            r32 = r0
            r17 = r1
            r0 = 0
        L_0x03af:
            return r0
        L_0x03b0:
            r32 = r0
            r17 = r1
            r0 = 0
            r12 = 14
            java.util.Calendar r1 = r9.calendar
            r1.set(r12, r0)
            int r0 = r9.bp
            int r0 = r0 + 19
            r9.bp = r0
            char r0 = r9.charAt(r0)
            r9.ch = r0
            r0 = 5
            r9.token = r0
            r0 = 90
            if (r5 != r0) goto L_0x03ee
            java.util.Calendar r0 = r9.calendar
            java.util.TimeZone r0 = r0.getTimeZone()
            int r0 = r0.getRawOffset()
            if (r0 == 0) goto L_0x03ee
            r0 = 0
            java.lang.String[] r1 = java.util.TimeZone.getAvailableIDs(r0)
            int r6 = r1.length
            if (r6 <= 0) goto L_0x03ee
            r0 = r1[r0]
            java.util.TimeZone r0 = java.util.TimeZone.getTimeZone(r0)
            java.util.Calendar r6 = r9.calendar
            r6.setTimeZone(r0)
        L_0x03ee:
            r0 = 1
            return r0
        L_0x03f0:
            r11 = 48
            r12 = 14
            r13 = 11
            r14 = 13
            r15 = 12
            r16 = 10
        L_0x03fc:
            if (r39 == 0) goto L_0x0400
            r0 = 0
            return r0
        L_0x0400:
            int r0 = r9.bp
            char r29 = r9.charAt(r0)
            int r0 = r9.bp
            r1 = 1
            int r0 = r0 + r1
            char r30 = r9.charAt(r0)
            int r0 = r9.bp
            r1 = 2
            int r0 = r0 + r1
            char r17 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 3
            char r31 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 4
            char r32 = r9.charAt(r0)
            int r0 = r9.bp
            r1 = 5
            int r0 = r0 + r1
            char r34 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 6
            char r35 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 7
            char r36 = r9.charAt(r0)
            r21 = r29
            r22 = r30
            r23 = r17
            r24 = r31
            r25 = r32
            r26 = r34
            r27 = r35
            r28 = r36
            boolean r0 = checkDate(r21, r22, r23, r24, r25, r26, r27, r28)
            if (r0 != 0) goto L_0x0456
            r0 = 0
            return r0
        L_0x0456:
            r0 = r38
            r1 = r29
            r2 = r30
            r3 = r17
            r4 = r31
            r5 = r32
            r8 = 8
            r6 = r34
            r7 = r35
            r11 = 8
            r8 = r36
            r0.setCalendar(r1, r2, r3, r4, r5, r6, r7, r8)
            if (r10 == r11) goto L_0x0507
            int r0 = r9.bp
            int r0 = r0 + r11
            char r0 = r9.charAt(r0)
            int r1 = r9.bp
            int r1 = r1 + 9
            char r7 = r9.charAt(r1)
            int r1 = r9.bp
            int r1 = r1 + 10
            char r8 = r9.charAt(r1)
            int r1 = r9.bp
            int r1 = r1 + r13
            char r11 = r9.charAt(r1)
            int r1 = r9.bp
            int r1 = r1 + r15
            char r22 = r9.charAt(r1)
            int r1 = r9.bp
            int r1 = r1 + r14
            char r23 = r9.charAt(r1)
            r1 = r0
            r2 = r7
            r3 = r8
            r4 = r11
            r5 = r22
            r6 = r23
            boolean r1 = checkTime(r1, r2, r3, r4, r5, r6)
            if (r1 != 0) goto L_0x04ad
            r1 = 0
            return r1
        L_0x04ad:
            r1 = 17
            if (r10 != r1) goto L_0x04f0
            int r1 = r9.bp
            int r1 = r1 + r12
            char r1 = r9.charAt(r1)
            int r2 = r9.bp
            int r2 = r2 + 15
            char r2 = r9.charAt(r2)
            int r3 = r9.bp
            int r3 = r3 + 16
            char r3 = r9.charAt(r3)
            r4 = 48
            if (r1 < r4) goto L_0x04ee
            r5 = 57
            if (r1 <= r5) goto L_0x04d2
            r4 = 0
            goto L_0x04ef
        L_0x04d2:
            if (r2 < r4) goto L_0x04ec
            if (r2 <= r5) goto L_0x04d8
            r4 = 0
            goto L_0x04ed
        L_0x04d8:
            if (r3 < r4) goto L_0x04ea
            if (r3 <= r5) goto L_0x04dd
            goto L_0x04ea
        L_0x04dd:
            int r4 = r1 + -48
            int r4 = r4 * 100
            int r5 = r2 + -48
            int r5 = r5 * 10
            int r4 = r4 + r5
            int r5 = r3 + -48
            int r4 = r4 + r5
            goto L_0x04f1
        L_0x04ea:
            r4 = 0
            return r4
        L_0x04ec:
            r4 = 0
        L_0x04ed:
            return r4
        L_0x04ee:
            r4 = 0
        L_0x04ef:
            return r4
        L_0x04f0:
            r4 = 0
        L_0x04f1:
            int r1 = r0 + -48
            int r1 = r1 * 10
            int r2 = r7 + -48
            int r1 = r1 + r2
            int r2 = r8 + -48
            int r2 = r2 * 10
            int r3 = r11 + -48
            int r2 = r2 + r3
            int r3 = r22 + -48
            int r3 = r3 * 10
            int r5 = r23 + -48
            int r3 = r3 + r5
            goto L_0x050c
        L_0x0507:
            r0 = 0
            r4 = r0
            r3 = r0
            r2 = r0
            r1 = r0
        L_0x050c:
            java.util.Calendar r0 = r9.calendar
            r0.set(r13, r1)
            java.util.Calendar r0 = r9.calendar
            r0.set(r15, r2)
            java.util.Calendar r0 = r9.calendar
            r0.set(r14, r3)
            java.util.Calendar r0 = r9.calendar
            r0.set(r12, r4)
            r0 = 5
            r9.token = r0
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.JSONLexer.scanISO8601DateIfMatch(boolean):boolean");
    }

    static boolean checkTime(char h0, char h1, char m0, char m1, char s0, char s1) {
        if (h0 == '0') {
            if (h1 < '0' || h1 > '9') {
                return false;
            }
        } else if (h0 == '1') {
            if (h1 < '0' || h1 > '9') {
                return false;
            }
        } else if (h0 != '2' || h1 < '0' || h1 > '4') {
            return false;
        }
        if (m0 < '0' || m0 > '5') {
            if (!(m0 == '6' && m1 == '0')) {
                return false;
            }
        } else if (m1 < '0' || m1 > '9') {
            return false;
        }
        if (s0 < '0' || s0 > '5') {
            if (s0 == '6' && s1 == '0') {
                return true;
            }
            return false;
        } else if (s1 < '0' || s1 > '9') {
            return false;
        } else {
            return true;
        }
    }

    private void setCalendar(char y0, char y1, char y2, char y3, char M0, char M1, char d0, char d1) {
        this.calendar = Calendar.getInstance(this.timeZone, this.locale);
        this.calendar.set(1, ((y0 - '0') * 1000) + ((y1 - '0') * 100) + ((y2 - '0') * 10) + (y3 - '0'));
        this.calendar.set(2, (((M0 - '0') * 10) + (M1 - '0')) - 1);
        this.calendar.set(5, ((d0 - '0') * 10) + (d1 - '0'));
    }

    static boolean checkDate(char y0, char y1, char y2, char y3, char M0, char M1, int d0, int d1) {
        if ((y0 != '1' && y0 != '2') || y1 < '0' || y1 > '9' || y2 < '0' || y2 > '9' || y3 < '0' || y3 > '9') {
            return false;
        }
        if (M0 == '0') {
            if (M1 < '1' || M1 > '9') {
                return false;
            }
        } else if (M0 != '1') {
            return false;
        } else {
            if (!(M1 == '0' || M1 == '1' || M1 == '2')) {
                return false;
            }
        }
        if (d0 == 48) {
            if (d1 < 49 || d1 > 57) {
                return false;
            }
            return true;
        } else if (d0 == 49 || d0 == 50) {
            if (d1 < 48 || d1 > 57) {
                return false;
            }
            return true;
        } else if (d0 != 51) {
            return false;
        } else {
            if (d1 == 48 || d1 == 49) {
                return true;
            }
            return false;
        }
    }

    public static final byte[] decodeFast(char[] chars, int offset, int charsLen) {
        int sepCnt = 0;
        if (charsLen == 0) {
            return new byte[0];
        }
        int sIx = offset;
        int eIx = (offset + charsLen) - 1;
        while (sIx < eIx && IA[chars[sIx]] < 0) {
            sIx++;
        }
        while (eIx > 0 && IA[chars[eIx]] < 0) {
            eIx--;
        }
        int pad = chars[eIx] == '=' ? chars[eIx + -1] == '=' ? 2 : 1 : 0;
        int cCnt = (eIx - sIx) + 1;
        if (charsLen > 76) {
            if (chars[76] == 13) {
                sepCnt = cCnt / 78;
            }
            sepCnt <<= 1;
        }
        int len2 = (((cCnt - sepCnt) * 6) >> 3) - pad;
        byte[] bytes = new byte[len2];
        int d = 0;
        int cc = 0;
        int eLen = (len2 / 3) * 3;
        while (d < eLen) {
            int[] iArr = IA;
            int sIx2 = sIx + 1;
            int sIx3 = sIx2 + 1;
            int i = (iArr[chars[sIx]] << 18) | (iArr[chars[sIx2]] << 12);
            int sIx4 = sIx3 + 1;
            int i2 = i | (iArr[chars[sIx3]] << 6);
            int sIx5 = sIx4 + 1;
            int i3 = i2 | iArr[chars[sIx4]];
            int d2 = d + 1;
            bytes[d] = (byte) (i3 >> 16);
            int d3 = d2 + 1;
            bytes[d2] = (byte) (i3 >> 8);
            int d4 = d3 + 1;
            bytes[d3] = (byte) i3;
            if (sepCnt > 0 && (cc = cc + 1) == 19) {
                sIx5 += 2;
                cc = 0;
            }
            sIx = sIx5;
            d = d4;
        }
        if (d < len2) {
            int i4 = 0;
            int j = 0;
            while (sIx <= eIx - pad) {
                i4 |= IA[chars[sIx]] << (18 - (j * 6));
                j++;
                sIx++;
            }
            int r = 16;
            while (d < len2) {
                bytes[d] = (byte) (i4 >> r);
                r -= 8;
                d++;
            }
        }
        return bytes;
    }

    public static final byte[] decodeFast(String chars, int offset, int charsLen) {
        int sepCnt = 0;
        if (charsLen == 0) {
            return new byte[0];
        }
        int sIx = offset;
        int eIx = (offset + charsLen) - 1;
        while (sIx < eIx && IA[chars.charAt(sIx)] < 0) {
            sIx++;
        }
        while (eIx > 0 && IA[chars.charAt(eIx)] < 0) {
            eIx--;
        }
        int pad = chars.charAt(eIx) == '=' ? chars.charAt(eIx + -1) == '=' ? 2 : 1 : 0;
        int cCnt = (eIx - sIx) + 1;
        if (charsLen > 76) {
            if (chars.charAt(76) == 13) {
                sepCnt = cCnt / 78;
            }
            sepCnt <<= 1;
        }
        int len2 = (((cCnt - sepCnt) * 6) >> 3) - pad;
        byte[] bytes = new byte[len2];
        int d = 0;
        int cc = 0;
        int eLen = (len2 / 3) * 3;
        while (d < eLen) {
            int sIx2 = sIx + 1;
            int sIx3 = sIx2 + 1;
            int sIx4 = sIx3 + 1;
            int sIx5 = sIx4 + 1;
            int i = (IA[chars.charAt(sIx)] << 18) | (IA[chars.charAt(sIx2)] << 12) | (IA[chars.charAt(sIx3)] << 6) | IA[chars.charAt(sIx4)];
            int d2 = d + 1;
            bytes[d] = (byte) (i >> 16);
            int d3 = d2 + 1;
            bytes[d2] = (byte) (i >> 8);
            int d4 = d3 + 1;
            bytes[d3] = (byte) i;
            if (sepCnt > 0 && (cc = cc + 1) == 19) {
                sIx5 += 2;
                cc = 0;
            }
            sIx = sIx5;
            d = d4;
        }
        if (d < len2) {
            int i2 = 0;
            int j = 0;
            while (sIx <= eIx - pad) {
                i2 |= IA[chars.charAt(sIx)] << (18 - (j * 6));
                j++;
                sIx++;
            }
            int r = 16;
            while (d < len2) {
                bytes[d] = (byte) (i2 >> r);
                r -= 8;
                d++;
            }
        }
        return bytes;
    }

    public static final byte[] decodeFast(String s) {
        int sLen = s.length();
        int sepCnt = 0;
        if (sLen == 0) {
            return new byte[0];
        }
        int sIx = 0;
        int eIx = sLen - 1;
        while (sIx < eIx && IA[s.charAt(sIx) & 255] < 0) {
            sIx++;
        }
        while (eIx > 0 && IA[s.charAt(eIx) & 255] < 0) {
            eIx--;
        }
        int pad = s.charAt(eIx) == '=' ? s.charAt(eIx + -1) == '=' ? 2 : 1 : 0;
        int cCnt = (eIx - sIx) + 1;
        if (sLen > 76) {
            if (s.charAt(76) == 13) {
                sepCnt = cCnt / 78;
            }
            sepCnt <<= 1;
        }
        int len2 = (((cCnt - sepCnt) * 6) >> 3) - pad;
        byte[] dArr = new byte[len2];
        int d = 0;
        int cc = 0;
        int eLen = (len2 / 3) * 3;
        while (d < eLen) {
            int sIx2 = sIx + 1;
            int sIx3 = sIx2 + 1;
            int sIx4 = sIx3 + 1;
            int sIx5 = sIx4 + 1;
            int i = (IA[s.charAt(sIx)] << 18) | (IA[s.charAt(sIx2)] << 12) | (IA[s.charAt(sIx3)] << 6) | IA[s.charAt(sIx4)];
            int d2 = d + 1;
            dArr[d] = (byte) (i >> 16);
            int d3 = d2 + 1;
            dArr[d2] = (byte) (i >> 8);
            int d4 = d3 + 1;
            dArr[d3] = (byte) i;
            if (sepCnt > 0 && (cc = cc + 1) == 19) {
                sIx5 += 2;
                cc = 0;
            }
            sIx = sIx5;
            d = d4;
        }
        if (d < len2) {
            int i2 = 0;
            int j = 0;
            while (sIx <= eIx - pad) {
                i2 |= IA[s.charAt(sIx)] << (18 - (j * 6));
                j++;
                sIx++;
            }
            int r = 16;
            while (d < len2) {
                dArr[d] = (byte) (i2 >> r);
                r -= 8;
                d++;
            }
        }
        return dArr;
    }
}

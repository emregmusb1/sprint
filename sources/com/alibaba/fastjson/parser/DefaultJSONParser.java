package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.parser.deserializer.ExtraTypeProvider;
import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.FieldTypeResolver;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.IntegerCodec;
import com.alibaba.fastjson.serializer.StringCodec;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.Closeable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class DefaultJSONParser implements Closeable {
    public static final int NONE = 0;
    public static final int NeedToResolve = 1;
    public static final int TypeNameRedirect = 2;
    public ParserConfig config;
    protected ParseContext contex;
    private ParseContext[] contextArray;
    private int contextArrayIndex;
    private DateFormat dateFormat;
    private String dateFormatPattern;
    protected List<ExtraProcessor> extraProcessors;
    protected List<ExtraTypeProvider> extraTypeProviders;
    public FieldTypeResolver fieldTypeResolver;
    public final JSONLexer lexer;
    public int resolveStatus;
    private List<ResolveTask> resolveTaskList;
    public final SymbolTable symbolTable;

    public String getDateFomartPattern() {
        return this.dateFormatPattern;
    }

    public DateFormat getDateFormat() {
        if (this.dateFormat == null) {
            this.dateFormat = new SimpleDateFormat(this.dateFormatPattern, this.lexer.locale);
            this.dateFormat.setTimeZone(this.lexer.timeZone);
        }
        return this.dateFormat;
    }

    public void setDateFormat(String dateFormat2) {
        this.dateFormatPattern = dateFormat2;
        this.dateFormat = null;
    }

    public void setDateFomrat(DateFormat dateFormat2) {
        this.dateFormat = dateFormat2;
    }

    public DefaultJSONParser(String input) {
        this(input, ParserConfig.global, JSON.DEFAULT_PARSER_FEATURE);
    }

    public DefaultJSONParser(String input, ParserConfig config2) {
        this(new JSONLexer(input, JSON.DEFAULT_PARSER_FEATURE), config2);
    }

    public DefaultJSONParser(String input, ParserConfig config2, int features) {
        this(new JSONLexer(input, features), config2);
    }

    public DefaultJSONParser(char[] input, int length, ParserConfig config2, int features) {
        this(new JSONLexer(input, length, features), config2);
    }

    public DefaultJSONParser(JSONLexer lexer2) {
        this(lexer2, ParserConfig.global);
    }

    public DefaultJSONParser(JSONLexer lexer2, ParserConfig config2) {
        this.dateFormatPattern = JSON.DEFFAULT_DATE_FORMAT;
        this.contextArrayIndex = 0;
        this.resolveStatus = 0;
        this.extraTypeProviders = null;
        this.extraProcessors = null;
        this.fieldTypeResolver = null;
        this.lexer = lexer2;
        this.config = config2;
        this.symbolTable = config2.symbolTable;
        char c = lexer2.ch;
        char c2 = JSONLexer.EOI;
        if (c == '{') {
            int index = lexer2.bp + 1;
            lexer2.bp = index;
            lexer2.ch = index < lexer2.len ? lexer2.text.charAt(index) : c2;
            lexer2.token = 12;
        } else if (lexer2.ch == '[') {
            int index2 = lexer2.bp + 1;
            lexer2.bp = index2;
            lexer2.ch = index2 < lexer2.len ? lexer2.text.charAt(index2) : c2;
            lexer2.token = 14;
        } else {
            lexer2.nextToken();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:154:0x02a3, code lost:
        r4.nextToken(16);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x02aa, code lost:
        if (r4.token != 13) goto L_0x02fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x02ac, code lost:
        r4.nextToken(16);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:?, code lost:
        r6 = r1.config.getDeserializer(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x02bc, code lost:
        if ((r6 instanceof com.alibaba.fastjson.parser.JavaBeanDeserializer) == false) goto L_0x02c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:161:0x02be, code lost:
        r0 = ((com.alibaba.fastjson.parser.JavaBeanDeserializer) r6).createInstance(r1, (java.lang.reflect.Type) r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x02c6, code lost:
        r0 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x02c8, code lost:
        if (r0 != null) goto L_0x02e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x02ca, code lost:
        r18 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x02ce, code lost:
        if (r13 != java.lang.Cloneable.class) goto L_0x02d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x02d0, code lost:
        r0 = new java.util.HashMap();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x02dc, code lost:
        if ("java.util.Collections$EmptyMap".equals(r12) == false) goto L_0x02e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:169:0x02de, code lost:
        r0 = java.util.Collections.emptyMap();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x02e3, code lost:
        r0 = r13.newInstance();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x02e8, code lost:
        r18 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x02eb, code lost:
        if (r14 != false) goto L_0x02ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x02ed, code lost:
        r1.contex = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x02ef, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x02fb, code lost:
        r19 = r8;
        r1.resolveStatus = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x0302, code lost:
        if (r1.contex == null) goto L_0x030b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x0306, code lost:
        if ((r3 instanceof java.lang.Integer) != false) goto L_0x030b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:183:0x0308, code lost:
        popContext();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x030f, code lost:
        if (r27.size() <= 0) goto L_0x0320;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x0311, code lost:
        r0 = com.alibaba.fastjson.util.TypeUtils.cast((java.lang.Object) r2, r13, r1.config);
        parseObject(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x031b, code lost:
        if (r14 != false) goto L_0x031f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x031d, code lost:
        r1.contex = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x031f, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:?, code lost:
        r6 = r1.config.getDeserializer(r13).deserialze(r1, r13, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x032a, code lost:
        if (r14 != false) goto L_0x032e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x032c, code lost:
        r1.contex = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x032e, code lost:
        return r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x0341, code lost:
        r4.nextToken(4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x0346, code lost:
        if (r4.token != 4) goto L_0x03f7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x0348, code lost:
        r6 = r4.stringVal();
        r4.nextToken(13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0356, code lost:
        if ("@".equals(r6) == false) goto L_0x037c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x035a, code lost:
        if (r1.contex == null) goto L_0x0379;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x035c, code lost:
        r12 = r1.contex;
        r0 = r12.object;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x0364, code lost:
        if ((r0 instanceof java.lang.Object[]) != false) goto L_0x0377;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0368, code lost:
        if ((r0 instanceof java.util.Collection) == false) goto L_0x036b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x036d, code lost:
        if (r12.parent == null) goto L_0x0374;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x036f, code lost:
        r8 = r12.parent.object;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x0374, code lost:
        r8 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x0377, code lost:
        r8 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x0379, code lost:
        r18 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x037c, code lost:
        r18 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x0384, code lost:
        if ("..".equals(r6) == false) goto L_0x039a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x0388, code lost:
        if (r15.object == null) goto L_0x038e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x038a, code lost:
        r8 = r15.object;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x038e, code lost:
        addResolveTask(new com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask(r15, r6));
        r1.resolveStatus = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x03a0, code lost:
        if ("$".equals(r6) == false) goto L_0x03c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x03a2, code lost:
        r0 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x03a5, code lost:
        if (r0.parent == null) goto L_0x03ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x03a7, code lost:
        r0 = r0.parent;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x03ad, code lost:
        if (r0.object == null) goto L_0x03b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x03af, code lost:
        r8 = r0.object;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x03b2, code lost:
        addResolveTask(new com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask(r0, r6));
        r1.resolveStatus = 1;
        r8 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x03c0, code lost:
        addResolveTask(new com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask(r15, r6));
        r1.resolveStatus = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x03cb, code lost:
        r8 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x03d1, code lost:
        if (r4.token != 13) goto L_0x03de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x03d3, code lost:
        r4.nextToken(16);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x03d9, code lost:
        if (r14 != false) goto L_0x03dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x03db, code lost:
        r1.contex = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x03dd, code lost:
        return r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x03f6, code lost:
        throw new com.alibaba.fastjson.JSONException("syntax error, " + r4.info());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x0413, code lost:
        throw new com.alibaba.fastjson.JSONException("illegal ref, " + com.alibaba.fastjson.parser.JSONToken.name(r4.token));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:?, code lost:
        r4.nextToken(16);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x04a8, code lost:
        if (r14 != false) goto L_0x04ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x04aa, code lost:
        r1.contex = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x04ac, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x04ad, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x04ae, code lost:
        r17 = r8;
        r25 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:?, code lost:
        r4.nextToken(16);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x058a, code lost:
        if (r14 != false) goto L_0x058f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x058c, code lost:
        r1.contex = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x058f, code lost:
        if (r14 != false) goto L_0x0593;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x0591, code lost:
        r1.contex = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x0593, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0594, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x0595, code lost:
        r17 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x0635, code lost:
        if (r0 != '}') goto L_0x06ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0637, code lost:
        r6 = r4.bp + 1;
        r4.bp = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x063f, code lost:
        if (r6 < r4.len) goto L_0x0644;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0641, code lost:
        r7 = com.alibaba.fastjson.parser.JSONLexer.EOI;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x0644, code lost:
        r7 = r4.text.charAt(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x064a, code lost:
        r4.ch = r7;
        r0 = r7;
        r4.sp = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x0652, code lost:
        if (r0 != ',') goto L_0x066e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x0654, code lost:
        r6 = r4.bp + 1;
        r4.bp = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x065c, code lost:
        if (r6 < r4.len) goto L_0x0661;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x065e, code lost:
        r7 = com.alibaba.fastjson.parser.JSONLexer.EOI;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0661, code lost:
        r7 = r4.text.charAt(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0667, code lost:
        r4.ch = r7;
        r4.token = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0670, code lost:
        if (r0 != '}') goto L_0x068c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x0672, code lost:
        r6 = r4.bp + 1;
        r4.bp = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x067a, code lost:
        if (r6 < r4.len) goto L_0x067f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x067c, code lost:
        r7 = com.alibaba.fastjson.parser.JSONLexer.EOI;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x067f, code lost:
        r7 = r4.text.charAt(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0685, code lost:
        r4.ch = r7;
        r4.token = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x068e, code lost:
        if (r0 != ']') goto L_0x06aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x0690, code lost:
        r6 = r4.bp + 1;
        r4.bp = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x0698, code lost:
        if (r6 < r4.len) goto L_0x069d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x069a, code lost:
        r7 = com.alibaba.fastjson.parser.JSONLexer.EOI;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x069d, code lost:
        r7 = r4.text.charAt(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x06a3, code lost:
        r4.ch = r7;
        r4.token = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x06aa, code lost:
        r4.nextToken();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x06ad, code lost:
        if (r14 != false) goto L_0x06b5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x06af, code lost:
        setContext(r1.contex, r2, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x06b5, code lost:
        if (r14 != false) goto L_0x06b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x06b7, code lost:
        r1.contex = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x06b9, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x06d2, code lost:
        throw new com.alibaba.fastjson.JSONException("syntax error, " + r4.info());
     */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x0767  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object parseObject(java.util.Map r27, java.lang.Object r28) {
        /*
            r26 = this;
            r1 = r26
            r2 = r27
            r3 = r28
            com.alibaba.fastjson.parser.JSONLexer r4 = r1.lexer
            int r0 = r4.token
            r5 = 0
            r6 = 8
            if (r0 != r6) goto L_0x0013
            r4.nextToken()
            return r5
        L_0x0013:
            r7 = 12
            r8 = 16
            if (r0 == r7) goto L_0x0043
            if (r0 != r8) goto L_0x001c
            goto L_0x0043
        L_0x001c:
            com.alibaba.fastjson.JSONException r5 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "syntax error, expect {, actual "
            r6.append(r7)
            java.lang.String r7 = r4.tokenName()
            r6.append(r7)
            java.lang.String r7 = ", "
            r6.append(r7)
            java.lang.String r7 = r4.info()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.<init>(r6)
            throw r5
        L_0x0043:
            boolean r9 = r2 instanceof com.alibaba.fastjson.JSONObject
            if (r9 == 0) goto L_0x0050
            r9 = r2
            com.alibaba.fastjson.JSONObject r9 = (com.alibaba.fastjson.JSONObject) r9
            java.util.Map r10 = r9.getInnerMap()
            r9 = 1
            goto L_0x0052
        L_0x0050:
            r9 = 0
            r10 = 0
        L_0x0052:
            int r11 = r4.features
            com.alibaba.fastjson.parser.Feature r12 = com.alibaba.fastjson.parser.Feature.AllowISO8601DateFormat
            int r12 = r12.mask
            r11 = r11 & r12
            r12 = 0
            if (r11 == 0) goto L_0x005e
            r11 = 1
            goto L_0x005f
        L_0x005e:
            r11 = 0
        L_0x005f:
            boolean r14 = r4.disableCircularReferenceDetect
            com.alibaba.fastjson.parser.ParseContext r15 = r1.contex
            r16 = r12
            r17 = r0
        L_0x0067:
            char r0 = r4.ch     // Catch:{ all -> 0x0762 }
            r5 = 125(0x7d, float:1.75E-43)
            r6 = 34
            if (r0 == r6) goto L_0x0078
            if (r0 == r5) goto L_0x0078
            r4.skipWhitespace()     // Catch:{ all -> 0x0092 }
            char r7 = r4.ch     // Catch:{ all -> 0x0092 }
            r0 = r7
            goto L_0x0079
        L_0x0078:
            r7 = r0
        L_0x0079:
            r0 = 44
            if (r7 != r0) goto L_0x0097
            int r0 = r4.features     // Catch:{ all -> 0x0092 }
            com.alibaba.fastjson.parser.Feature r8 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas     // Catch:{ all -> 0x0092 }
            int r8 = r8.mask     // Catch:{ all -> 0x0092 }
            r0 = r0 & r8
            if (r0 == 0) goto L_0x0097
            r4.next()     // Catch:{ all -> 0x0092 }
            r4.skipWhitespace()     // Catch:{ all -> 0x0092 }
            char r0 = r4.ch     // Catch:{ all -> 0x0092 }
            r7 = r0
            r8 = 16
            goto L_0x0079
        L_0x0092:
            r0 = move-exception
            r25 = r9
            goto L_0x0765
        L_0x0097:
            r8 = 0
            java.lang.String r0 = "expect ':' at "
            r12 = 58
            java.lang.String r13 = "syntax error, "
            if (r7 != r6) goto L_0x00d4
            com.alibaba.fastjson.parser.SymbolTable r5 = r1.symbolTable     // Catch:{ all -> 0x0092 }
            java.lang.String r5 = r4.scanSymbol(r5, r6)     // Catch:{ all -> 0x0092 }
            char r6 = r4.ch     // Catch:{ all -> 0x0092 }
            r7 = r6
            if (r7 == r12) goto L_0x022f
            r4.skipWhitespace()     // Catch:{ all -> 0x0092 }
            char r6 = r4.ch     // Catch:{ all -> 0x0092 }
            r7 = r6
            if (r7 != r12) goto L_0x00b5
            goto L_0x022f
        L_0x00b5:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0092 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x0092 }
            r12.<init>()     // Catch:{ all -> 0x0092 }
            r12.append(r0)     // Catch:{ all -> 0x0092 }
            int r0 = r4.pos     // Catch:{ all -> 0x0092 }
            r12.append(r0)     // Catch:{ all -> 0x0092 }
            java.lang.String r0 = ", name "
            r12.append(r0)     // Catch:{ all -> 0x0092 }
            r12.append(r5)     // Catch:{ all -> 0x0092 }
            java.lang.String r0 = r12.toString()     // Catch:{ all -> 0x0092 }
            r6.<init>(r0)     // Catch:{ all -> 0x0092 }
            throw r6     // Catch:{ all -> 0x0092 }
        L_0x00d4:
            r5 = 125(0x7d, float:1.75E-43)
            if (r7 != r5) goto L_0x00fb
            int r0 = r4.bp     // Catch:{ all -> 0x0092 }
            r5 = 1
            int r0 = r0 + r5
            r4.bp = r0     // Catch:{ all -> 0x0092 }
            int r5 = r4.len     // Catch:{ all -> 0x0092 }
            if (r0 < r5) goto L_0x00e5
            r5 = 26
            goto L_0x00eb
        L_0x00e5:
            java.lang.String r5 = r4.text     // Catch:{ all -> 0x0092 }
            char r5 = r5.charAt(r0)     // Catch:{ all -> 0x0092 }
        L_0x00eb:
            r4.ch = r5     // Catch:{ all -> 0x0092 }
            r0 = 0
            r4.sp = r0     // Catch:{ all -> 0x0092 }
            r0 = 16
            r4.nextToken(r0)     // Catch:{ all -> 0x0092 }
            if (r14 != 0) goto L_0x00fa
            r1.contex = r15
        L_0x00fa:
            return r2
        L_0x00fb:
            r5 = 39
            if (r7 != r5) goto L_0x014e
            int r5 = r4.features     // Catch:{ all -> 0x0092 }
            com.alibaba.fastjson.parser.Feature r6 = com.alibaba.fastjson.parser.Feature.AllowSingleQuotes     // Catch:{ all -> 0x0092 }
            int r6 = r6.mask     // Catch:{ all -> 0x0092 }
            r5 = r5 & r6
            if (r5 == 0) goto L_0x0135
            com.alibaba.fastjson.parser.SymbolTable r5 = r1.symbolTable     // Catch:{ all -> 0x0092 }
            r6 = 39
            java.lang.String r5 = r4.scanSymbol(r5, r6)     // Catch:{ all -> 0x0092 }
            char r6 = r4.ch     // Catch:{ all -> 0x0092 }
            if (r6 == r12) goto L_0x0117
            r4.skipWhitespace()     // Catch:{ all -> 0x0092 }
        L_0x0117:
            char r6 = r4.ch     // Catch:{ all -> 0x0092 }
            r7 = r6
            if (r7 != r12) goto L_0x011e
            goto L_0x022f
        L_0x011e:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0092 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x0092 }
            r12.<init>()     // Catch:{ all -> 0x0092 }
            r12.append(r0)     // Catch:{ all -> 0x0092 }
            int r0 = r4.pos     // Catch:{ all -> 0x0092 }
            r12.append(r0)     // Catch:{ all -> 0x0092 }
            java.lang.String r0 = r12.toString()     // Catch:{ all -> 0x0092 }
            r6.<init>(r0)     // Catch:{ all -> 0x0092 }
            throw r6     // Catch:{ all -> 0x0092 }
        L_0x0135:
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0092 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0092 }
            r5.<init>()     // Catch:{ all -> 0x0092 }
            r5.append(r13)     // Catch:{ all -> 0x0092 }
            java.lang.String r6 = r4.info()     // Catch:{ all -> 0x0092 }
            r5.append(r6)     // Catch:{ all -> 0x0092 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0092 }
            r0.<init>(r5)     // Catch:{ all -> 0x0092 }
            throw r0     // Catch:{ all -> 0x0092 }
        L_0x014e:
            r5 = 26
            if (r7 == r5) goto L_0x0745
            r6 = 44
            if (r7 == r6) goto L_0x072a
            r6 = 48
            if (r7 < r6) goto L_0x015e
            r6 = 57
            if (r7 <= r6) goto L_0x0162
        L_0x015e:
            r6 = 45
            if (r7 != r6) goto L_0x01bf
        L_0x0162:
            r0 = 0
            r4.sp = r0     // Catch:{ all -> 0x0092 }
            r4.scanNumber()     // Catch:{ all -> 0x0092 }
            int r0 = r4.token     // Catch:{ NumberFormatException -> 0x01a3 }
            r6 = 2
            if (r0 != r6) goto L_0x0172
            java.lang.Number r0 = r4.integerValue()     // Catch:{ NumberFormatException -> 0x01a3 }
            goto L_0x0178
        L_0x0172:
            r0 = 1
            java.lang.Number r6 = r4.decimalValue(r0)     // Catch:{ NumberFormatException -> 0x01a3 }
            r0 = r6
        L_0x0178:
            if (r9 == 0) goto L_0x017f
            java.lang.String r6 = r0.toString()     // Catch:{ NumberFormatException -> 0x01a3 }
            r0 = r6
        L_0x017f:
            char r6 = r4.ch     // Catch:{ all -> 0x0092 }
            r7 = r6
            if (r7 != r12) goto L_0x0188
            r5 = r0
            goto L_0x022f
        L_0x0188:
            com.alibaba.fastjson.JSONException r5 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0092 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0092 }
            r6.<init>()     // Catch:{ all -> 0x0092 }
            java.lang.String r12 = "parse number key error, "
            r6.append(r12)     // Catch:{ all -> 0x0092 }
            java.lang.String r12 = r4.info()     // Catch:{ all -> 0x0092 }
            r6.append(r12)     // Catch:{ all -> 0x0092 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0092 }
            r5.<init>(r6)     // Catch:{ all -> 0x0092 }
            throw r5     // Catch:{ all -> 0x0092 }
        L_0x01a3:
            r0 = move-exception
            com.alibaba.fastjson.JSONException r5 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0092 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0092 }
            r6.<init>()     // Catch:{ all -> 0x0092 }
            java.lang.String r12 = "parse number key error, "
            r6.append(r12)     // Catch:{ all -> 0x0092 }
            java.lang.String r12 = r4.info()     // Catch:{ all -> 0x0092 }
            r6.append(r12)     // Catch:{ all -> 0x0092 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0092 }
            r5.<init>(r6)     // Catch:{ all -> 0x0092 }
            throw r5     // Catch:{ all -> 0x0092 }
        L_0x01bf:
            r6 = 123(0x7b, float:1.72E-43)
            if (r7 == r6) goto L_0x021f
            r6 = 91
            if (r7 != r6) goto L_0x01c8
            goto L_0x021f
        L_0x01c8:
            int r6 = r4.features     // Catch:{ all -> 0x0092 }
            com.alibaba.fastjson.parser.Feature r5 = com.alibaba.fastjson.parser.Feature.AllowUnQuotedFieldNames     // Catch:{ all -> 0x0092 }
            int r5 = r5.mask     // Catch:{ all -> 0x0092 }
            r5 = r5 & r6
            if (r5 == 0) goto L_0x0206
            com.alibaba.fastjson.parser.SymbolTable r5 = r1.symbolTable     // Catch:{ all -> 0x0092 }
            java.lang.String r5 = r4.scanSymbolUnQuoted(r5)     // Catch:{ all -> 0x0092 }
            r4.skipWhitespace()     // Catch:{ all -> 0x0092 }
            char r6 = r4.ch     // Catch:{ all -> 0x0092 }
            r7 = r6
            if (r7 != r12) goto L_0x01e7
            if (r9 == 0) goto L_0x022f
            java.lang.String r0 = r5.toString()     // Catch:{ all -> 0x0092 }
            r5 = r0
            goto L_0x022f
        L_0x01e7:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0092 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x0092 }
            r12.<init>()     // Catch:{ all -> 0x0092 }
            r12.append(r0)     // Catch:{ all -> 0x0092 }
            int r0 = r4.bp     // Catch:{ all -> 0x0092 }
            r12.append(r0)     // Catch:{ all -> 0x0092 }
            java.lang.String r0 = ", actual "
            r12.append(r0)     // Catch:{ all -> 0x0092 }
            r12.append(r7)     // Catch:{ all -> 0x0092 }
            java.lang.String r0 = r12.toString()     // Catch:{ all -> 0x0092 }
            r6.<init>(r0)     // Catch:{ all -> 0x0092 }
            throw r6     // Catch:{ all -> 0x0092 }
        L_0x0206:
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0092 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0092 }
            r5.<init>()     // Catch:{ all -> 0x0092 }
            r5.append(r13)     // Catch:{ all -> 0x0092 }
            java.lang.String r6 = r4.info()     // Catch:{ all -> 0x0092 }
            r5.append(r6)     // Catch:{ all -> 0x0092 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0092 }
            r0.<init>(r5)     // Catch:{ all -> 0x0092 }
            throw r0     // Catch:{ all -> 0x0092 }
        L_0x021f:
            r4.nextToken()     // Catch:{ all -> 0x0762 }
            java.lang.Object r0 = r26.parse()     // Catch:{ all -> 0x0762 }
            r5 = r0
            r8 = 1
            if (r9 == 0) goto L_0x022f
            java.lang.String r0 = r5.toString()     // Catch:{ all -> 0x0092 }
            r5 = r0
        L_0x022f:
            r0 = 13
            if (r8 != 0) goto L_0x0270
            int r6 = r4.bp     // Catch:{ all -> 0x0092 }
            r12 = 1
            int r6 = r6 + r12
            r4.bp = r6     // Catch:{ all -> 0x0092 }
            int r12 = r4.len     // Catch:{ all -> 0x0092 }
            if (r6 < r12) goto L_0x0240
            r12 = 26
            goto L_0x0246
        L_0x0240:
            java.lang.String r12 = r4.text     // Catch:{ all -> 0x0092 }
            char r12 = r12.charAt(r6)     // Catch:{ all -> 0x0092 }
        L_0x0246:
            r4.ch = r12     // Catch:{ all -> 0x0092 }
            r7 = r12
        L_0x0249:
            r6 = 32
            if (r7 > r6) goto L_0x026d
            r6 = 32
            if (r7 == r6) goto L_0x0264
            r6 = 10
            if (r7 == r6) goto L_0x0264
            if (r7 == r0) goto L_0x0264
            r6 = 9
            if (r7 == r6) goto L_0x0264
            r6 = 12
            if (r7 == r6) goto L_0x0264
            r6 = 8
            if (r7 != r6) goto L_0x0275
            goto L_0x0266
        L_0x0264:
            r6 = 8
        L_0x0266:
            r4.next()     // Catch:{ all -> 0x0092 }
            char r12 = r4.ch     // Catch:{ all -> 0x0092 }
            r7 = r12
            goto L_0x0249
        L_0x026d:
            r6 = 8
            goto L_0x0275
        L_0x0270:
            r6 = 8
            char r12 = r4.ch     // Catch:{ all -> 0x0762 }
            r7 = r12
        L_0x0275:
            r12 = 0
            r4.sp = r12     // Catch:{ all -> 0x0762 }
            java.lang.String r12 = "@type"
            if (r5 != r12) goto L_0x0332
            com.alibaba.fastjson.parser.Feature r12 = com.alibaba.fastjson.parser.Feature.DisableSpecialKeyDetect     // Catch:{ all -> 0x0092 }
            boolean r12 = r4.isEnabled(r12)     // Catch:{ all -> 0x0092 }
            if (r12 != 0) goto L_0x032f
            com.alibaba.fastjson.parser.SymbolTable r12 = r1.symbolTable     // Catch:{ all -> 0x0092 }
            r13 = 34
            java.lang.String r12 = r4.scanSymbol(r12, r13)     // Catch:{ all -> 0x0092 }
            com.alibaba.fastjson.parser.ParserConfig r13 = r1.config     // Catch:{ all -> 0x0092 }
            java.lang.ClassLoader r13 = r13.defaultClassLoader     // Catch:{ all -> 0x0092 }
            java.lang.Class r13 = com.alibaba.fastjson.util.TypeUtils.loadClass(r12, r13)     // Catch:{ all -> 0x0092 }
            if (r13 != 0) goto L_0x02a3
            java.lang.String r0 = "@type"
            r2.put(r0, r12)     // Catch:{ all -> 0x0092 }
            r5 = 0
            r7 = 12
            r8 = 16
            r12 = 0
            goto L_0x0067
        L_0x02a3:
            r6 = 16
            r4.nextToken(r6)     // Catch:{ all -> 0x0092 }
            int r6 = r4.token     // Catch:{ all -> 0x0092 }
            if (r6 != r0) goto L_0x02fb
            r0 = 16
            r4.nextToken(r0)     // Catch:{ all -> 0x0092 }
            r0 = 0
            com.alibaba.fastjson.parser.ParserConfig r6 = r1.config     // Catch:{ Exception -> 0x02f0 }
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r6 = r6.getDeserializer(r13)     // Catch:{ Exception -> 0x02f0 }
            r18 = r0
            boolean r0 = r6 instanceof com.alibaba.fastjson.parser.JavaBeanDeserializer     // Catch:{ Exception -> 0x02f0 }
            if (r0 == 0) goto L_0x02c6
            r0 = r6
            com.alibaba.fastjson.parser.JavaBeanDeserializer r0 = (com.alibaba.fastjson.parser.JavaBeanDeserializer) r0     // Catch:{ Exception -> 0x02f0 }
            java.lang.Object r0 = r0.createInstance((com.alibaba.fastjson.parser.DefaultJSONParser) r1, (java.lang.reflect.Type) r13)     // Catch:{ Exception -> 0x02f0 }
            goto L_0x02c8
        L_0x02c6:
            r0 = r18
        L_0x02c8:
            if (r0 != 0) goto L_0x02e8
            r18 = r0
            java.lang.Class<java.lang.Cloneable> r0 = java.lang.Cloneable.class
            if (r13 != r0) goto L_0x02d6
            java.util.HashMap r0 = new java.util.HashMap     // Catch:{ Exception -> 0x02f0 }
            r0.<init>()     // Catch:{ Exception -> 0x02f0 }
            goto L_0x02ea
        L_0x02d6:
            java.lang.String r0 = "java.util.Collections$EmptyMap"
            boolean r0 = r0.equals(r12)     // Catch:{ Exception -> 0x02f0 }
            if (r0 == 0) goto L_0x02e3
            java.util.Map r0 = java.util.Collections.emptyMap()     // Catch:{ Exception -> 0x02f0 }
            goto L_0x02ea
        L_0x02e3:
            java.lang.Object r0 = r13.newInstance()     // Catch:{ Exception -> 0x02f0 }
            goto L_0x02ea
        L_0x02e8:
            r18 = r0
        L_0x02ea:
            if (r14 != 0) goto L_0x02ef
            r1.contex = r15
        L_0x02ef:
            return r0
        L_0x02f0:
            r0 = move-exception
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0092 }
            r19 = r8
            java.lang.String r8 = "create instance error"
            r6.<init>(r8, r0)     // Catch:{ all -> 0x0092 }
            throw r6     // Catch:{ all -> 0x0092 }
        L_0x02fb:
            r19 = r8
            r0 = 2
            r1.resolveStatus = r0     // Catch:{ all -> 0x0092 }
            com.alibaba.fastjson.parser.ParseContext r0 = r1.contex     // Catch:{ all -> 0x0092 }
            if (r0 == 0) goto L_0x030b
            boolean r0 = r3 instanceof java.lang.Integer     // Catch:{ all -> 0x0092 }
            if (r0 != 0) goto L_0x030b
            r26.popContext()     // Catch:{ all -> 0x0092 }
        L_0x030b:
            int r0 = r27.size()     // Catch:{ all -> 0x0092 }
            if (r0 <= 0) goto L_0x0320
            com.alibaba.fastjson.parser.ParserConfig r0 = r1.config     // Catch:{ all -> 0x0092 }
            java.lang.Object r0 = com.alibaba.fastjson.util.TypeUtils.cast((java.lang.Object) r2, r13, (com.alibaba.fastjson.parser.ParserConfig) r0)     // Catch:{ all -> 0x0092 }
            r1.parseObject((java.lang.Object) r0)     // Catch:{ all -> 0x0092 }
            if (r14 != 0) goto L_0x031f
            r1.contex = r15
        L_0x031f:
            return r0
        L_0x0320:
            com.alibaba.fastjson.parser.ParserConfig r0 = r1.config     // Catch:{ all -> 0x0092 }
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r0 = r0.getDeserializer(r13)     // Catch:{ all -> 0x0092 }
            java.lang.Object r6 = r0.deserialze(r1, r13, r3)     // Catch:{ all -> 0x0092 }
            if (r14 != 0) goto L_0x032e
            r1.contex = r15
        L_0x032e:
            return r6
        L_0x032f:
            r19 = r8
            goto L_0x0334
        L_0x0332:
            r19 = r8
        L_0x0334:
            java.lang.String r8 = "$ref"
            r12 = 4
            if (r5 != r8) goto L_0x0414
            com.alibaba.fastjson.parser.Feature r8 = com.alibaba.fastjson.parser.Feature.DisableSpecialKeyDetect     // Catch:{ all -> 0x0092 }
            boolean r8 = r4.isEnabled(r8)     // Catch:{ all -> 0x0092 }
            if (r8 != 0) goto L_0x0414
            r4.nextToken(r12)     // Catch:{ all -> 0x0092 }
            int r6 = r4.token     // Catch:{ all -> 0x0092 }
            if (r6 != r12) goto L_0x03f7
            java.lang.String r6 = r4.stringVal()     // Catch:{ all -> 0x0092 }
            r4.nextToken(r0)     // Catch:{ all -> 0x0092 }
            r8 = 0
            java.lang.String r12 = "@"
            boolean r12 = r12.equals(r6)     // Catch:{ all -> 0x0092 }
            if (r12 == 0) goto L_0x037c
            com.alibaba.fastjson.parser.ParseContext r12 = r1.contex     // Catch:{ all -> 0x0092 }
            if (r12 == 0) goto L_0x0379
            com.alibaba.fastjson.parser.ParseContext r12 = r1.contex     // Catch:{ all -> 0x0092 }
            java.lang.Object r0 = r12.object     // Catch:{ all -> 0x0092 }
            r18 = r8
            boolean r8 = r0 instanceof java.lang.Object[]     // Catch:{ all -> 0x0092 }
            if (r8 != 0) goto L_0x0377
            boolean r8 = r0 instanceof java.util.Collection     // Catch:{ all -> 0x0092 }
            if (r8 == 0) goto L_0x036b
            goto L_0x0377
        L_0x036b:
            com.alibaba.fastjson.parser.ParseContext r8 = r12.parent     // Catch:{ all -> 0x0092 }
            if (r8 == 0) goto L_0x0374
            com.alibaba.fastjson.parser.ParseContext r8 = r12.parent     // Catch:{ all -> 0x0092 }
            java.lang.Object r8 = r8.object     // Catch:{ all -> 0x0092 }
            goto L_0x0378
        L_0x0374:
            r8 = r18
            goto L_0x0378
        L_0x0377:
            r8 = r0
        L_0x0378:
            goto L_0x03cd
        L_0x0379:
            r18 = r8
            goto L_0x03cb
        L_0x037c:
            r18 = r8
            java.lang.String r0 = ".."
            boolean r0 = r0.equals(r6)     // Catch:{ all -> 0x0092 }
            if (r0 == 0) goto L_0x039a
            java.lang.Object r0 = r15.object     // Catch:{ all -> 0x0092 }
            if (r0 == 0) goto L_0x038e
            java.lang.Object r0 = r15.object     // Catch:{ all -> 0x0092 }
            r8 = r0
            goto L_0x03cd
        L_0x038e:
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r0 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask     // Catch:{ all -> 0x0092 }
            r0.<init>(r15, r6)     // Catch:{ all -> 0x0092 }
            r1.addResolveTask(r0)     // Catch:{ all -> 0x0092 }
            r0 = 1
            r1.resolveStatus = r0     // Catch:{ all -> 0x0092 }
            goto L_0x03cb
        L_0x039a:
            java.lang.String r0 = "$"
            boolean r0 = r0.equals(r6)     // Catch:{ all -> 0x0092 }
            if (r0 == 0) goto L_0x03c0
            r0 = r15
        L_0x03a3:
            com.alibaba.fastjson.parser.ParseContext r8 = r0.parent     // Catch:{ all -> 0x0092 }
            if (r8 == 0) goto L_0x03ab
            com.alibaba.fastjson.parser.ParseContext r8 = r0.parent     // Catch:{ all -> 0x0092 }
            r0 = r8
            goto L_0x03a3
        L_0x03ab:
            java.lang.Object r8 = r0.object     // Catch:{ all -> 0x0092 }
            if (r8 == 0) goto L_0x03b2
            java.lang.Object r8 = r0.object     // Catch:{ all -> 0x0092 }
            goto L_0x03bf
        L_0x03b2:
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r8 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask     // Catch:{ all -> 0x0092 }
            r8.<init>(r0, r6)     // Catch:{ all -> 0x0092 }
            r1.addResolveTask(r8)     // Catch:{ all -> 0x0092 }
            r8 = 1
            r1.resolveStatus = r8     // Catch:{ all -> 0x0092 }
            r8 = r18
        L_0x03bf:
            goto L_0x03cd
        L_0x03c0:
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r0 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask     // Catch:{ all -> 0x0092 }
            r0.<init>(r15, r6)     // Catch:{ all -> 0x0092 }
            r1.addResolveTask(r0)     // Catch:{ all -> 0x0092 }
            r0 = 1
            r1.resolveStatus = r0     // Catch:{ all -> 0x0092 }
        L_0x03cb:
            r8 = r18
        L_0x03cd:
            int r0 = r4.token     // Catch:{ all -> 0x0092 }
            r12 = 13
            if (r0 != r12) goto L_0x03de
            r0 = 16
            r4.nextToken(r0)     // Catch:{ all -> 0x0092 }
            if (r14 != 0) goto L_0x03dd
            r1.contex = r15
        L_0x03dd:
            return r8
        L_0x03de:
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0092 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x0092 }
            r12.<init>()     // Catch:{ all -> 0x0092 }
            r12.append(r13)     // Catch:{ all -> 0x0092 }
            java.lang.String r13 = r4.info()     // Catch:{ all -> 0x0092 }
            r12.append(r13)     // Catch:{ all -> 0x0092 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x0092 }
            r0.<init>(r12)     // Catch:{ all -> 0x0092 }
            throw r0     // Catch:{ all -> 0x0092 }
        L_0x03f7:
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0092 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0092 }
            r6.<init>()     // Catch:{ all -> 0x0092 }
            java.lang.String r8 = "illegal ref, "
            r6.append(r8)     // Catch:{ all -> 0x0092 }
            int r8 = r4.token     // Catch:{ all -> 0x0092 }
            java.lang.String r8 = com.alibaba.fastjson.parser.JSONToken.name(r8)     // Catch:{ all -> 0x0092 }
            r6.append(r8)     // Catch:{ all -> 0x0092 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0092 }
            r0.<init>(r6)     // Catch:{ all -> 0x0092 }
            throw r0     // Catch:{ all -> 0x0092 }
        L_0x0414:
            if (r14 != 0) goto L_0x0424
            if (r16 != 0) goto L_0x0424
            com.alibaba.fastjson.parser.ParseContext r0 = r1.contex     // Catch:{ all -> 0x0092 }
            com.alibaba.fastjson.parser.ParseContext r0 = r1.setContext(r0, r2, r3)     // Catch:{ all -> 0x0092 }
            if (r15 != 0) goto L_0x0421
            r15 = r0
        L_0x0421:
            r8 = 1
            r16 = r8
        L_0x0424:
            r0 = 34
            if (r7 != r0) goto L_0x0452
            java.lang.String r0 = r4.scanStringValue(r0)     // Catch:{ all -> 0x0092 }
            r8 = r0
            if (r11 == 0) goto L_0x0445
            com.alibaba.fastjson.parser.JSONLexer r12 = new com.alibaba.fastjson.parser.JSONLexer     // Catch:{ all -> 0x0092 }
            r12.<init>(r0)     // Catch:{ all -> 0x0092 }
            r6 = 1
            boolean r24 = r12.scanISO8601DateIfMatch(r6)     // Catch:{ all -> 0x0092 }
            if (r24 == 0) goto L_0x0442
            java.util.Calendar r6 = r12.calendar     // Catch:{ all -> 0x0092 }
            java.util.Date r6 = r6.getTime()     // Catch:{ all -> 0x0092 }
            r8 = r6
        L_0x0442:
            r12.close()     // Catch:{ all -> 0x0092 }
        L_0x0445:
            if (r10 == 0) goto L_0x044b
            r10.put(r5, r8)     // Catch:{ all -> 0x0092 }
            goto L_0x044e
        L_0x044b:
            r2.put(r5, r8)     // Catch:{ all -> 0x0092 }
        L_0x044e:
            r25 = r9
            goto L_0x0604
        L_0x0452:
            r0 = 48
            if (r7 < r0) goto L_0x045a
            r0 = 57
            if (r7 <= r0) goto L_0x045e
        L_0x045a:
            r0 = 45
            if (r7 != r0) goto L_0x0469
        L_0x045e:
            java.lang.Number r0 = r4.scanNumberValue()     // Catch:{ all -> 0x0092 }
            r2.put(r5, r0)     // Catch:{ all -> 0x0092 }
            r25 = r9
            goto L_0x0604
        L_0x0469:
            r0 = 91
            if (r7 != r0) goto L_0x04df
            r0 = 14
            r4.token = r0     // Catch:{ all -> 0x0092 }
            int r0 = r4.bp     // Catch:{ all -> 0x0092 }
            r6 = 1
            int r0 = r0 + r6
            r4.bp = r0     // Catch:{ all -> 0x0092 }
            int r6 = r4.len     // Catch:{ all -> 0x0092 }
            if (r0 < r6) goto L_0x047e
            r6 = 26
            goto L_0x0484
        L_0x047e:
            java.lang.String r6 = r4.text     // Catch:{ all -> 0x0092 }
            char r6 = r6.charAt(r0)     // Catch:{ all -> 0x0092 }
        L_0x0484:
            r4.ch = r6     // Catch:{ all -> 0x0092 }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x0092 }
            r0.<init>()     // Catch:{ all -> 0x0092 }
            r1.parseArray((java.util.Collection) r0, (java.lang.Object) r5)     // Catch:{ all -> 0x0092 }
            com.alibaba.fastjson.JSONArray r6 = new com.alibaba.fastjson.JSONArray     // Catch:{ all -> 0x0092 }
            r6.<init>((java.util.List<java.lang.Object>) r0)     // Catch:{ all -> 0x0092 }
            if (r10 == 0) goto L_0x0499
            r10.put(r5, r6)     // Catch:{ all -> 0x0092 }
            goto L_0x049c
        L_0x0499:
            r2.put(r5, r6)     // Catch:{ all -> 0x0092 }
        L_0x049c:
            int r8 = r4.token     // Catch:{ all -> 0x0092 }
            r12 = 13
            if (r8 != r12) goto L_0x04b4
            r12 = 16
            r4.nextToken(r12)     // Catch:{ all -> 0x04ad }
            if (r14 != 0) goto L_0x04ac
            r1.contex = r15
        L_0x04ac:
            return r2
        L_0x04ad:
            r0 = move-exception
            r17 = r8
            r25 = r9
            goto L_0x0765
        L_0x04b4:
            r12 = 16
            if (r8 != r12) goto L_0x04c4
            r17 = r8
            r5 = 0
            r6 = 8
            r7 = 12
            r8 = 16
            r12 = 0
            goto L_0x0067
        L_0x04c4:
            com.alibaba.fastjson.JSONException r12 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04ad }
            r21 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ad }
            r0.<init>()     // Catch:{ all -> 0x04ad }
            r0.append(r13)     // Catch:{ all -> 0x04ad }
            java.lang.String r13 = r4.info()     // Catch:{ all -> 0x04ad }
            r0.append(r13)     // Catch:{ all -> 0x04ad }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x04ad }
            r12.<init>(r0)     // Catch:{ all -> 0x04ad }
            throw r12     // Catch:{ all -> 0x04ad }
        L_0x04df:
            r0 = 123(0x7b, float:1.72E-43)
            if (r7 != r0) goto L_0x05c6
            int r0 = r4.bp     // Catch:{ all -> 0x0762 }
            r6 = 1
            int r0 = r0 + r6
            r4.bp = r0     // Catch:{ all -> 0x0762 }
            int r6 = r4.len     // Catch:{ all -> 0x0762 }
            if (r0 < r6) goto L_0x04f0
            r6 = 26
            goto L_0x04f6
        L_0x04f0:
            java.lang.String r6 = r4.text     // Catch:{ all -> 0x0762 }
            char r6 = r6.charAt(r0)     // Catch:{ all -> 0x0762 }
        L_0x04f6:
            r4.ch = r6     // Catch:{ all -> 0x0762 }
            r6 = 12
            r4.token = r6     // Catch:{ all -> 0x0762 }
            boolean r0 = r3 instanceof java.lang.Integer     // Catch:{ all -> 0x0762 }
            int r8 = r4.features     // Catch:{ all -> 0x0762 }
            com.alibaba.fastjson.parser.Feature r12 = com.alibaba.fastjson.parser.Feature.OrderedField     // Catch:{ all -> 0x0762 }
            int r12 = r12.mask     // Catch:{ all -> 0x0762 }
            r8 = r8 & r12
            if (r8 == 0) goto L_0x0512
            com.alibaba.fastjson.JSONObject r8 = new com.alibaba.fastjson.JSONObject     // Catch:{ all -> 0x0092 }
            java.util.LinkedHashMap r12 = new java.util.LinkedHashMap     // Catch:{ all -> 0x0092 }
            r12.<init>()     // Catch:{ all -> 0x0092 }
            r8.<init>((java.util.Map<java.lang.String, java.lang.Object>) r12)     // Catch:{ all -> 0x0092 }
            goto L_0x0517
        L_0x0512:
            com.alibaba.fastjson.JSONObject r8 = new com.alibaba.fastjson.JSONObject     // Catch:{ all -> 0x0762 }
            r8.<init>()     // Catch:{ all -> 0x0762 }
        L_0x0517:
            r12 = 0
            if (r14 != 0) goto L_0x0522
            if (r0 != 0) goto L_0x0522
            com.alibaba.fastjson.parser.ParseContext r20 = r1.setContext(r15, r8, r5)     // Catch:{ all -> 0x0092 }
            r12 = r20
        L_0x0522:
            r20 = 0
            r21 = 0
            com.alibaba.fastjson.parser.deserializer.FieldTypeResolver r6 = r1.fieldTypeResolver     // Catch:{ all -> 0x0762 }
            if (r6 == 0) goto L_0x0550
            if (r5 == 0) goto L_0x0531
            java.lang.String r6 = r5.toString()     // Catch:{ all -> 0x0092 }
            goto L_0x0532
        L_0x0531:
            r6 = 0
        L_0x0532:
            r25 = r9
            com.alibaba.fastjson.parser.deserializer.FieldTypeResolver r9 = r1.fieldTypeResolver     // Catch:{ all -> 0x0760 }
            java.lang.reflect.Type r9 = r9.resolve(r2, r6)     // Catch:{ all -> 0x0760 }
            if (r9 == 0) goto L_0x054d
            r22 = r6
            com.alibaba.fastjson.parser.ParserConfig r6 = r1.config     // Catch:{ all -> 0x0760 }
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r6 = r6.getDeserializer(r9)     // Catch:{ all -> 0x0760 }
            java.lang.Object r23 = r6.deserialze(r1, r9, r5)     // Catch:{ all -> 0x0760 }
            r20 = r23
            r21 = 1
            goto L_0x0552
        L_0x054d:
            r22 = r6
            goto L_0x0552
        L_0x0550:
            r25 = r9
        L_0x0552:
            if (r21 != 0) goto L_0x055b
            java.lang.Object r6 = r1.parseObject((java.util.Map) r8, (java.lang.Object) r5)     // Catch:{ all -> 0x0760 }
            r20 = r6
            goto L_0x055d
        L_0x055b:
            r6 = r20
        L_0x055d:
            if (r12 == 0) goto L_0x0563
            if (r8 == r6) goto L_0x0563
            r12.object = r2     // Catch:{ all -> 0x0760 }
        L_0x0563:
            int r9 = r1.resolveStatus     // Catch:{ all -> 0x0760 }
            r20 = r8
            r8 = 1
            if (r9 != r8) goto L_0x0571
            java.lang.String r8 = r5.toString()     // Catch:{ all -> 0x0760 }
            r1.checkMapResolve(r2, r8)     // Catch:{ all -> 0x0760 }
        L_0x0571:
            if (r10 == 0) goto L_0x0577
            r10.put(r5, r6)     // Catch:{ all -> 0x0760 }
            goto L_0x057a
        L_0x0577:
            r2.put(r5, r6)     // Catch:{ all -> 0x0760 }
        L_0x057a:
            if (r0 == 0) goto L_0x057f
            r1.setContext(r15, r6, r5)     // Catch:{ all -> 0x0760 }
        L_0x057f:
            int r8 = r4.token     // Catch:{ all -> 0x0760 }
            r9 = 13
            if (r8 != r9) goto L_0x0599
            r9 = 16
            r4.nextToken(r9)     // Catch:{ all -> 0x0594 }
            if (r14 != 0) goto L_0x058e
            r1.contex = r15     // Catch:{ all -> 0x0594 }
        L_0x058e:
            if (r14 != 0) goto L_0x0593
            r1.contex = r15
        L_0x0593:
            return r2
        L_0x0594:
            r0 = move-exception
            r17 = r8
            goto L_0x0765
        L_0x0599:
            r9 = 16
            if (r8 != r9) goto L_0x05ab
            r17 = r8
            r9 = r25
            r5 = 0
            r6 = 8
            r7 = 12
            r8 = 16
            r12 = 0
            goto L_0x0067
        L_0x05ab:
            com.alibaba.fastjson.JSONException r9 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0594 }
            r22 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0594 }
            r0.<init>()     // Catch:{ all -> 0x0594 }
            r0.append(r13)     // Catch:{ all -> 0x0594 }
            java.lang.String r13 = r4.info()     // Catch:{ all -> 0x0594 }
            r0.append(r13)     // Catch:{ all -> 0x0594 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0594 }
            r9.<init>(r0)     // Catch:{ all -> 0x0594 }
            throw r9     // Catch:{ all -> 0x0594 }
        L_0x05c6:
            r25 = r9
            r0 = 116(0x74, float:1.63E-43)
            if (r7 != r0) goto L_0x05e7
            java.lang.String r0 = r4.text     // Catch:{ all -> 0x0760 }
            java.lang.String r6 = "true"
            int r8 = r4.bp     // Catch:{ all -> 0x0760 }
            boolean r0 = r0.startsWith(r6, r8)     // Catch:{ all -> 0x0760 }
            if (r0 == 0) goto L_0x0604
            int r0 = r4.bp     // Catch:{ all -> 0x0760 }
            int r0 = r0 + 3
            r4.bp = r0     // Catch:{ all -> 0x0760 }
            r4.next()     // Catch:{ all -> 0x0760 }
            java.lang.Boolean r0 = java.lang.Boolean.TRUE     // Catch:{ all -> 0x0760 }
            r2.put(r5, r0)     // Catch:{ all -> 0x0760 }
            goto L_0x0604
        L_0x05e7:
            r0 = 102(0x66, float:1.43E-43)
            if (r7 != r0) goto L_0x06d3
            java.lang.String r0 = r4.text     // Catch:{ all -> 0x0760 }
            java.lang.String r6 = "false"
            int r8 = r4.bp     // Catch:{ all -> 0x0760 }
            boolean r0 = r0.startsWith(r6, r8)     // Catch:{ all -> 0x0760 }
            if (r0 == 0) goto L_0x0604
            int r0 = r4.bp     // Catch:{ all -> 0x0760 }
            int r0 = r0 + r12
            r4.bp = r0     // Catch:{ all -> 0x0760 }
            r4.next()     // Catch:{ all -> 0x0760 }
            java.lang.Boolean r0 = java.lang.Boolean.FALSE     // Catch:{ all -> 0x0760 }
            r2.put(r5, r0)     // Catch:{ all -> 0x0760 }
        L_0x0604:
            char r0 = r4.ch     // Catch:{ all -> 0x0760 }
            r6 = 44
            if (r0 == r6) goto L_0x0614
            r6 = 125(0x7d, float:1.75E-43)
            if (r0 == r6) goto L_0x0614
            r4.skipWhitespace()     // Catch:{ all -> 0x0760 }
            char r6 = r4.ch     // Catch:{ all -> 0x0760 }
            r0 = r6
        L_0x0614:
            r6 = 44
            if (r0 != r6) goto L_0x0633
            int r6 = r4.bp     // Catch:{ all -> 0x0760 }
            r7 = 1
            int r6 = r6 + r7
            r4.bp = r6     // Catch:{ all -> 0x0760 }
            int r7 = r4.len     // Catch:{ all -> 0x0760 }
            if (r6 < r7) goto L_0x0625
            r7 = 26
            goto L_0x062b
        L_0x0625:
            java.lang.String r7 = r4.text     // Catch:{ all -> 0x0760 }
            char r7 = r7.charAt(r6)     // Catch:{ all -> 0x0760 }
        L_0x062b:
            r4.ch = r7     // Catch:{ all -> 0x0760 }
            r6 = 0
            r8 = 1
            r12 = 16
            goto L_0x0705
        L_0x0633:
            r6 = 125(0x7d, float:1.75E-43)
            if (r0 != r6) goto L_0x06ba
            int r6 = r4.bp     // Catch:{ all -> 0x0760 }
            r7 = 1
            int r6 = r6 + r7
            r4.bp = r6     // Catch:{ all -> 0x0760 }
            int r7 = r4.len     // Catch:{ all -> 0x0760 }
            if (r6 < r7) goto L_0x0644
            r7 = 26
            goto L_0x064a
        L_0x0644:
            java.lang.String r7 = r4.text     // Catch:{ all -> 0x0760 }
            char r7 = r7.charAt(r6)     // Catch:{ all -> 0x0760 }
        L_0x064a:
            r4.ch = r7     // Catch:{ all -> 0x0760 }
            r0 = r7
            r6 = 0
            r4.sp = r6     // Catch:{ all -> 0x0760 }
            r6 = 44
            if (r0 != r6) goto L_0x066e
            int r6 = r4.bp     // Catch:{ all -> 0x0760 }
            r7 = 1
            int r6 = r6 + r7
            r4.bp = r6     // Catch:{ all -> 0x0760 }
            int r7 = r4.len     // Catch:{ all -> 0x0760 }
            if (r6 < r7) goto L_0x0661
            r7 = 26
            goto L_0x0667
        L_0x0661:
            java.lang.String r7 = r4.text     // Catch:{ all -> 0x0760 }
            char r7 = r7.charAt(r6)     // Catch:{ all -> 0x0760 }
        L_0x0667:
            r4.ch = r7     // Catch:{ all -> 0x0760 }
            r7 = 16
            r4.token = r7     // Catch:{ all -> 0x0760 }
            goto L_0x06ad
        L_0x066e:
            r6 = 125(0x7d, float:1.75E-43)
            if (r0 != r6) goto L_0x068c
            int r6 = r4.bp     // Catch:{ all -> 0x0760 }
            r7 = 1
            int r6 = r6 + r7
            r4.bp = r6     // Catch:{ all -> 0x0760 }
            int r7 = r4.len     // Catch:{ all -> 0x0760 }
            if (r6 < r7) goto L_0x067f
            r7 = 26
            goto L_0x0685
        L_0x067f:
            java.lang.String r7 = r4.text     // Catch:{ all -> 0x0760 }
            char r7 = r7.charAt(r6)     // Catch:{ all -> 0x0760 }
        L_0x0685:
            r4.ch = r7     // Catch:{ all -> 0x0760 }
            r7 = 13
            r4.token = r7     // Catch:{ all -> 0x0760 }
            goto L_0x06ad
        L_0x068c:
            r6 = 93
            if (r0 != r6) goto L_0x06aa
            int r6 = r4.bp     // Catch:{ all -> 0x0760 }
            r8 = 1
            int r6 = r6 + r8
            r4.bp = r6     // Catch:{ all -> 0x0760 }
            int r7 = r4.len     // Catch:{ all -> 0x0760 }
            if (r6 < r7) goto L_0x069d
            r7 = 26
            goto L_0x06a3
        L_0x069d:
            java.lang.String r7 = r4.text     // Catch:{ all -> 0x0760 }
            char r7 = r7.charAt(r6)     // Catch:{ all -> 0x0760 }
        L_0x06a3:
            r4.ch = r7     // Catch:{ all -> 0x0760 }
            r7 = 15
            r4.token = r7     // Catch:{ all -> 0x0760 }
            goto L_0x06ad
        L_0x06aa:
            r4.nextToken()     // Catch:{ all -> 0x0760 }
        L_0x06ad:
            if (r14 != 0) goto L_0x06b4
            com.alibaba.fastjson.parser.ParseContext r6 = r1.contex     // Catch:{ all -> 0x0760 }
            r1.setContext(r6, r2, r3)     // Catch:{ all -> 0x0760 }
        L_0x06b4:
            if (r14 != 0) goto L_0x06b9
            r1.contex = r15
        L_0x06b9:
            return r2
        L_0x06ba:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0760 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0760 }
            r7.<init>()     // Catch:{ all -> 0x0760 }
            r7.append(r13)     // Catch:{ all -> 0x0760 }
            java.lang.String r8 = r4.info()     // Catch:{ all -> 0x0760 }
            r7.append(r8)     // Catch:{ all -> 0x0760 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0760 }
            r6.<init>(r7)     // Catch:{ all -> 0x0760 }
            throw r6     // Catch:{ all -> 0x0760 }
        L_0x06d3:
            r6 = 0
            r8 = 1
            r4.nextToken()     // Catch:{ all -> 0x0760 }
            java.lang.Object r0 = r26.parse()     // Catch:{ all -> 0x0760 }
            java.lang.Class r9 = r27.getClass()     // Catch:{ all -> 0x0760 }
            java.lang.Class<com.alibaba.fastjson.JSONObject> r12 = com.alibaba.fastjson.JSONObject.class
            if (r9 != r12) goto L_0x06e9
            java.lang.String r9 = r5.toString()     // Catch:{ all -> 0x0760 }
            goto L_0x06ea
        L_0x06e9:
            r9 = r5
        L_0x06ea:
            r2.put(r9, r0)     // Catch:{ all -> 0x0760 }
            int r9 = r4.token     // Catch:{ all -> 0x0760 }
            r12 = 13
            if (r9 != r12) goto L_0x06fe
            r6 = 16
            r4.nextToken(r6)     // Catch:{ all -> 0x0760 }
            if (r14 != 0) goto L_0x06fd
            r1.contex = r15
        L_0x06fd:
            return r2
        L_0x06fe:
            int r9 = r4.token     // Catch:{ all -> 0x0760 }
            r12 = 16
            if (r9 != r12) goto L_0x0711
        L_0x0705:
            r9 = r25
            r5 = 0
            r6 = 8
            r7 = 12
            r8 = 16
            r12 = 0
            goto L_0x0067
        L_0x0711:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0760 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0760 }
            r8.<init>()     // Catch:{ all -> 0x0760 }
            r8.append(r13)     // Catch:{ all -> 0x0760 }
            java.lang.String r9 = r4.info()     // Catch:{ all -> 0x0760 }
            r8.append(r9)     // Catch:{ all -> 0x0760 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0760 }
            r6.<init>(r8)     // Catch:{ all -> 0x0760 }
            throw r6     // Catch:{ all -> 0x0760 }
        L_0x072a:
            r25 = r9
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0760 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0760 }
            r5.<init>()     // Catch:{ all -> 0x0760 }
            r5.append(r13)     // Catch:{ all -> 0x0760 }
            java.lang.String r6 = r4.info()     // Catch:{ all -> 0x0760 }
            r5.append(r6)     // Catch:{ all -> 0x0760 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0760 }
            r0.<init>(r5)     // Catch:{ all -> 0x0760 }
            throw r0     // Catch:{ all -> 0x0760 }
        L_0x0745:
            r25 = r9
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0760 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0760 }
            r5.<init>()     // Catch:{ all -> 0x0760 }
            r5.append(r13)     // Catch:{ all -> 0x0760 }
            java.lang.String r6 = r4.info()     // Catch:{ all -> 0x0760 }
            r5.append(r6)     // Catch:{ all -> 0x0760 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0760 }
            r0.<init>(r5)     // Catch:{ all -> 0x0760 }
            throw r0     // Catch:{ all -> 0x0760 }
        L_0x0760:
            r0 = move-exception
            goto L_0x0765
        L_0x0762:
            r0 = move-exception
            r25 = r9
        L_0x0765:
            if (r14 != 0) goto L_0x0769
            r1.contex = r15
        L_0x0769:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.DefaultJSONParser.parseObject(java.util.Map, java.lang.Object):java.lang.Object");
    }

    public <T> T parseObject(Class<T> clazz) {
        return parseObject((Type) clazz, (Object) null);
    }

    public <T> T parseObject(Type type) {
        return parseObject(type, (Object) null);
    }

    public <T> T parseObject(Type type, Object fieldName) {
        if (this.lexer.token == 8) {
            this.lexer.nextToken();
            return null;
        }
        if (this.lexer.token == 4) {
            if (type == byte[].class) {
                Object bytesValue = this.lexer.bytesValue();
                this.lexer.nextToken();
                return bytesValue;
            } else if (type == char[].class) {
                String strVal = this.lexer.stringVal();
                this.lexer.nextToken();
                return strVal.toCharArray();
            }
        }
        try {
            return this.config.getDeserializer(type).deserialze(this, type, fieldName);
        } catch (JSONException e) {
            throw e;
        } catch (Exception e2) {
            throw new JSONException(e2.getMessage(), e2);
        }
    }

    public <T> List<T> parseArray(Class<T> clazz) {
        List<T> array = new ArrayList<>();
        parseArray((Class<?>) clazz, (Collection) array);
        return array;
    }

    public void parseArray(Class<?> clazz, Collection array) {
        parseArray((Type) clazz, array);
    }

    public void parseArray(Type type, Collection array) {
        parseArray(type, array, (Object) null);
    }

    /* JADX INFO: finally extract failed */
    public void parseArray(Type type, Collection array, Object fieldName) {
        ObjectDeserializer deserializer;
        Object val;
        Object obj;
        if (this.lexer.token == 21 || this.lexer.token == 22) {
            this.lexer.nextToken();
        }
        if (this.lexer.token == 14) {
            if (Integer.TYPE == type) {
                deserializer = IntegerCodec.instance;
                this.lexer.nextToken(2);
            } else if (String.class == type) {
                deserializer = StringCodec.instance;
                this.lexer.nextToken(4);
            } else {
                deserializer = this.config.getDeserializer(type);
                this.lexer.nextToken(12);
            }
            ParseContext context = this.contex;
            if (!this.lexer.disableCircularReferenceDetect) {
                setContext(this.contex, array, fieldName);
            }
            int i = 0;
            while (true) {
                try {
                    if ((this.lexer.features & Feature.AllowArbitraryCommas.mask) != 0) {
                        while (this.lexer.token == 16) {
                            this.lexer.nextToken();
                        }
                    }
                    if (this.lexer.token == 15) {
                        this.contex = context;
                        this.lexer.nextToken(16);
                        return;
                    }
                    Object obj2 = null;
                    if (Integer.TYPE == type) {
                        array.add(IntegerCodec.instance.deserialze(this, (Type) null, (Object) null));
                    } else if (String.class == type) {
                        if (this.lexer.token == 4) {
                            obj = this.lexer.stringVal();
                            this.lexer.nextToken(16);
                        } else {
                            Object obj3 = parse();
                            if (obj3 != null) {
                                obj2 = obj3.toString();
                            }
                            obj = obj2;
                        }
                        array.add(obj);
                    } else {
                        if (this.lexer.token == 8) {
                            this.lexer.nextToken();
                            val = null;
                        } else {
                            val = deserializer.deserialze(this, type, Integer.valueOf(i));
                        }
                        array.add(val);
                        if (this.resolveStatus == 1) {
                            checkListResolve(array);
                        }
                    }
                    if (this.lexer.token == 16) {
                        this.lexer.nextToken();
                    }
                    i++;
                } catch (Throwable th) {
                    this.contex = context;
                    throw th;
                }
            }
        } else {
            throw new JSONException("exepct '[', but " + JSONToken.name(this.lexer.token) + ", " + this.lexer.info());
        }
    }

    public Object[] parseArray(Type[] types) {
        Object value;
        Type[] typeArr = types;
        int i = 8;
        if (this.lexer.token == 8) {
            this.lexer.nextToken(16);
            return null;
        }
        int i2 = 14;
        if (this.lexer.token == 14) {
            Object[] list = new Object[typeArr.length];
            if (typeArr.length == 0) {
                this.lexer.nextToken(15);
                if (this.lexer.token == 15) {
                    this.lexer.nextToken(16);
                    return new Object[0];
                }
                throw new JSONException("syntax error, " + this.lexer.info());
            }
            this.lexer.nextToken(2);
            int i3 = 0;
            while (i3 < typeArr.length) {
                if (this.lexer.token == i) {
                    this.lexer.nextToken(16);
                    value = null;
                } else {
                    Type type = typeArr[i3];
                    if (type == Integer.TYPE || type == Integer.class) {
                        if (this.lexer.token == 2) {
                            value = Integer.valueOf(this.lexer.intValue());
                            this.lexer.nextToken(16);
                        } else {
                            value = TypeUtils.cast(parse(), type, this.config);
                        }
                    } else if (type != String.class) {
                        boolean isArray = false;
                        Class<?> componentType = null;
                        if (i3 == typeArr.length - 1 && (type instanceof Class)) {
                            Class<?> clazz = (Class) type;
                            isArray = clazz.isArray();
                            componentType = clazz.getComponentType();
                        }
                        if (!isArray || this.lexer.token == i2) {
                            value = this.config.getDeserializer(type).deserialze(this, type, (Object) null);
                        } else {
                            List<Object> varList = new ArrayList<>();
                            ObjectDeserializer derializer = this.config.getDeserializer(componentType);
                            if (this.lexer.token != 15) {
                                while (true) {
                                    varList.add(derializer.deserialze(this, type, (Object) null));
                                    if (this.lexer.token != 16) {
                                        break;
                                    }
                                    this.lexer.nextToken(12);
                                }
                                if (this.lexer.token != 15) {
                                    throw new JSONException("syntax error, " + this.lexer.info());
                                }
                            }
                            value = TypeUtils.cast((Object) varList, type, this.config);
                        }
                    } else if (this.lexer.token == 4) {
                        Object value2 = this.lexer.stringVal();
                        this.lexer.nextToken(16);
                        value = value2;
                    } else {
                        value = TypeUtils.cast(parse(), type, this.config);
                    }
                }
                list[i3] = value;
                if (this.lexer.token == 15) {
                    break;
                } else if (this.lexer.token == 16) {
                    if (i3 == typeArr.length - 1) {
                        this.lexer.nextToken(15);
                    } else {
                        this.lexer.nextToken(2);
                    }
                    i3++;
                    i = 8;
                    i2 = 14;
                } else {
                    throw new JSONException("syntax error, " + this.lexer.info());
                }
            }
            if (this.lexer.token == 15) {
                this.lexer.nextToken(16);
                return list;
            }
            throw new JSONException("syntax error, " + this.lexer.info());
        }
        throw new JSONException("syntax error, " + this.lexer.info());
    }

    /* JADX WARNING: type inference failed for: r2v1, types: [com.alibaba.fastjson.parser.deserializer.ObjectDeserializer] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void parseObject(java.lang.Object r14) {
        /*
            r13 = this;
            java.lang.Class r0 = r14.getClass()
            r1 = 0
            com.alibaba.fastjson.parser.ParserConfig r2 = r13.config
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r2 = r2.getDeserializer(r0)
            boolean r3 = r2 instanceof com.alibaba.fastjson.parser.JavaBeanDeserializer
            if (r3 == 0) goto L_0x0012
            r1 = r2
            com.alibaba.fastjson.parser.JavaBeanDeserializer r1 = (com.alibaba.fastjson.parser.JavaBeanDeserializer) r1
        L_0x0012:
            com.alibaba.fastjson.parser.JSONLexer r3 = r13.lexer
            int r3 = r3.token
            r4 = 12
            r5 = 16
            if (r3 == r4) goto L_0x0040
            com.alibaba.fastjson.parser.JSONLexer r3 = r13.lexer
            int r3 = r3.token
            if (r3 != r5) goto L_0x0023
            goto L_0x0040
        L_0x0023:
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "syntax error, expect {, actual "
            r4.append(r5)
            com.alibaba.fastjson.parser.JSONLexer r5 = r13.lexer
            java.lang.String r5 = r5.tokenName()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        L_0x0040:
            com.alibaba.fastjson.parser.JSONLexer r3 = r13.lexer
            com.alibaba.fastjson.parser.SymbolTable r4 = r13.symbolTable
            java.lang.String r3 = r3.scanSymbol(r4)
            r4 = 13
            if (r3 != 0) goto L_0x006b
            com.alibaba.fastjson.parser.JSONLexer r6 = r13.lexer
            int r6 = r6.token
            if (r6 != r4) goto L_0x0059
            com.alibaba.fastjson.parser.JSONLexer r4 = r13.lexer
            r4.nextToken(r5)
            return
        L_0x0059:
            com.alibaba.fastjson.parser.JSONLexer r6 = r13.lexer
            int r6 = r6.token
            if (r6 != r5) goto L_0x006b
            com.alibaba.fastjson.parser.JSONLexer r6 = r13.lexer
            int r6 = r6.features
            com.alibaba.fastjson.parser.Feature r7 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas
            int r7 = r7.mask
            r6 = r6 & r7
            if (r6 == 0) goto L_0x006b
            goto L_0x0040
        L_0x006b:
            r6 = 0
            if (r1 == 0) goto L_0x0072
            com.alibaba.fastjson.parser.deserializer.FieldDeserializer r6 = r1.getFieldDeserializer(r3)
        L_0x0072:
            r7 = 58
            if (r6 != 0) goto L_0x00b8
            com.alibaba.fastjson.parser.JSONLexer r8 = r13.lexer
            int r8 = r8.features
            com.alibaba.fastjson.parser.Feature r9 = com.alibaba.fastjson.parser.Feature.IgnoreNotMatch
            int r9 = r9.mask
            r8 = r8 & r9
            if (r8 == 0) goto L_0x0095
            com.alibaba.fastjson.parser.JSONLexer r8 = r13.lexer
            r8.nextTokenWithChar(r7)
            r13.parse()
            com.alibaba.fastjson.parser.JSONLexer r7 = r13.lexer
            int r7 = r7.token
            if (r7 != r4) goto L_0x0040
            com.alibaba.fastjson.parser.JSONLexer r4 = r13.lexer
            r4.nextToken()
            return
        L_0x0095:
            com.alibaba.fastjson.JSONException r4 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "setter not found, class "
            r5.append(r7)
            java.lang.String r7 = r0.getName()
            r5.append(r7)
            java.lang.String r7 = ", property "
            r5.append(r7)
            r5.append(r3)
            java.lang.String r5 = r5.toString()
            r4.<init>(r5)
            throw r4
        L_0x00b8:
            com.alibaba.fastjson.util.FieldInfo r8 = r6.fieldInfo
            java.lang.Class<?> r8 = r8.fieldClass
            com.alibaba.fastjson.util.FieldInfo r9 = r6.fieldInfo
            java.lang.reflect.Type r9 = r9.fieldType
            java.lang.Class r10 = java.lang.Integer.TYPE
            r11 = 0
            if (r8 != r10) goto L_0x00d1
            com.alibaba.fastjson.parser.JSONLexer r10 = r13.lexer
            r10.nextTokenWithChar(r7)
            com.alibaba.fastjson.serializer.IntegerCodec r7 = com.alibaba.fastjson.serializer.IntegerCodec.instance
            java.lang.Object r7 = r7.deserialze(r13, r9, r11)
            goto L_0x00fe
        L_0x00d1:
            java.lang.Class<java.lang.String> r10 = java.lang.String.class
            if (r8 != r10) goto L_0x00df
            com.alibaba.fastjson.parser.JSONLexer r10 = r13.lexer
            r10.nextTokenWithChar(r7)
            java.lang.String r7 = r13.parseString()
            goto L_0x00fe
        L_0x00df:
            java.lang.Class r10 = java.lang.Long.TYPE
            if (r8 != r10) goto L_0x00ef
            com.alibaba.fastjson.parser.JSONLexer r10 = r13.lexer
            r10.nextTokenWithChar(r7)
            com.alibaba.fastjson.serializer.IntegerCodec r7 = com.alibaba.fastjson.serializer.IntegerCodec.instance
            java.lang.Object r7 = r7.deserialze(r13, r9, r11)
            goto L_0x00fe
        L_0x00ef:
            com.alibaba.fastjson.parser.ParserConfig r10 = r13.config
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r10 = r10.getDeserializer(r8, r9)
            com.alibaba.fastjson.parser.JSONLexer r12 = r13.lexer
            r12.nextTokenWithChar(r7)
            java.lang.Object r7 = r10.deserialze(r13, r9, r11)
        L_0x00fe:
            r6.setValue((java.lang.Object) r14, (java.lang.Object) r7)
            com.alibaba.fastjson.parser.JSONLexer r7 = r13.lexer
            int r7 = r7.token
            if (r7 != r5) goto L_0x0109
            goto L_0x0040
        L_0x0109:
            com.alibaba.fastjson.parser.JSONLexer r7 = r13.lexer
            int r7 = r7.token
            if (r7 != r4) goto L_0x0115
            com.alibaba.fastjson.parser.JSONLexer r4 = r13.lexer
            r4.nextToken(r5)
            return
        L_0x0115:
            goto L_0x0040
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.DefaultJSONParser.parseObject(java.lang.Object):void");
    }

    public Object parseArrayWithType(Type collectionType) {
        if (this.lexer.token == 8) {
            this.lexer.nextToken();
            return null;
        }
        Type[] actualTypes = ((ParameterizedType) collectionType).getActualTypeArguments();
        if (actualTypes.length == 1) {
            Type actualTypeArgument = actualTypes[0];
            if (actualTypeArgument instanceof Class) {
                List<Object> array = new ArrayList<>();
                parseArray((Class<?>) (Class) actualTypeArgument, (Collection) array);
                return array;
            } else if (actualTypeArgument instanceof WildcardType) {
                WildcardType wildcardType = (WildcardType) actualTypeArgument;
                Type upperBoundType = wildcardType.getUpperBounds()[0];
                if (!Object.class.equals(upperBoundType)) {
                    List<Object> array2 = new ArrayList<>();
                    parseArray((Class<?>) (Class) upperBoundType, (Collection) array2);
                    return array2;
                } else if (wildcardType.getLowerBounds().length == 0) {
                    return parse();
                } else {
                    throw new JSONException("not support type : " + collectionType);
                }
            } else {
                if (actualTypeArgument instanceof TypeVariable) {
                    TypeVariable<?> typeVariable = (TypeVariable) actualTypeArgument;
                    Type[] bounds = typeVariable.getBounds();
                    if (bounds.length == 1) {
                        Type boundType = bounds[0];
                        if (boundType instanceof Class) {
                            List<Object> array3 = new ArrayList<>();
                            parseArray((Class<?>) (Class) boundType, (Collection) array3);
                            return array3;
                        }
                    } else {
                        throw new JSONException("not support : " + typeVariable);
                    }
                }
                if (actualTypeArgument instanceof ParameterizedType) {
                    List<Object> array4 = new ArrayList<>();
                    parseArray((Type) (ParameterizedType) actualTypeArgument, (Collection) array4);
                    return array4;
                }
                throw new JSONException("TODO : " + collectionType);
            }
        } else {
            throw new JSONException("not support type " + collectionType);
        }
    }

    /* access modifiers changed from: protected */
    public void checkListResolve(Collection array) {
        if (array instanceof List) {
            ResolveTask task = getLastResolveTask();
            task.fieldDeserializer = new ResolveFieldDeserializer(this, (List) array, array.size() - 1);
            task.ownerContext = this.contex;
            this.resolveStatus = 0;
            return;
        }
        ResolveTask task2 = getLastResolveTask();
        task2.fieldDeserializer = new ResolveFieldDeserializer(array);
        task2.ownerContext = this.contex;
        this.resolveStatus = 0;
    }

    /* access modifiers changed from: protected */
    public void checkMapResolve(Map object, Object fieldName) {
        ResolveFieldDeserializer fieldResolver = new ResolveFieldDeserializer(object, fieldName);
        ResolveTask task = getLastResolveTask();
        task.fieldDeserializer = fieldResolver;
        task.ownerContext = this.contex;
        this.resolveStatus = 0;
    }

    public Object parseObject(Map object) {
        return parseObject(object, (Object) null);
    }

    public JSONObject parseObject() {
        return (JSONObject) parseObject((Map) (this.lexer.features & Feature.OrderedField.mask) != 0 ? new JSONObject((Map<String, Object>) new LinkedHashMap()) : new JSONObject(), (Object) null);
    }

    public final void parseArray(Collection array) {
        parseArray(array, (Object) null);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: java.lang.Number} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: java.lang.Number} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: java.lang.Number} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v16, resolved type: java.util.Date} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v16, resolved type: java.util.Date} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v17, resolved type: java.util.Date} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: java.util.Date} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: java.lang.Boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v42, resolved type: java.lang.Number} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v43, resolved type: java.lang.Boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v44, resolved type: com.alibaba.fastjson.JSONArray} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void parseArray(java.util.Collection r17, java.lang.Object r18) {
        /*
            r16 = this;
            r1 = r16
            r2 = r17
            com.alibaba.fastjson.parser.JSONLexer r0 = r1.lexer
            int r0 = r0.token
            r3 = 21
            if (r0 == r3) goto L_0x0013
            r3 = 22
            if (r0 != r3) goto L_0x0011
            goto L_0x0013
        L_0x0011:
            r3 = r0
            goto L_0x001d
        L_0x0013:
            com.alibaba.fastjson.parser.JSONLexer r3 = r1.lexer
            r3.nextToken()
            com.alibaba.fastjson.parser.JSONLexer r3 = r1.lexer
            int r0 = r3.token
            r3 = r0
        L_0x001d:
            r0 = 14
            if (r3 != r0) goto L_0x02ce
            com.alibaba.fastjson.parser.JSONLexer r4 = r1.lexer
            boolean r4 = r4.disableCircularReferenceDetect
            com.alibaba.fastjson.parser.ParseContext r5 = r1.contex
            if (r4 != 0) goto L_0x0031
            com.alibaba.fastjson.parser.ParseContext r6 = r1.contex
            r7 = r18
            r1.setContext(r6, r2, r7)
            goto L_0x0033
        L_0x0031:
            r7 = r18
        L_0x0033:
            com.alibaba.fastjson.parser.JSONLexer r6 = r1.lexer     // Catch:{ all -> 0x02c8 }
            char r6 = r6.ch     // Catch:{ all -> 0x02c8 }
            r8 = 123(0x7b, float:1.72E-43)
            r9 = 93
            r11 = 4
            r12 = 12
            r13 = 34
            r14 = 16
            r15 = 1
            if (r6 == r13) goto L_0x0080
            if (r6 != r9) goto L_0x0056
            com.alibaba.fastjson.parser.JSONLexer r0 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r0.next()     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r0 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r0.nextToken(r14)     // Catch:{ all -> 0x02c8 }
            if (r4 != 0) goto L_0x0055
            r1.contex = r5
        L_0x0055:
            return
        L_0x0056:
            if (r6 != r8) goto L_0x0079
            com.alibaba.fastjson.parser.JSONLexer r10 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r8 = r10.bp     // Catch:{ all -> 0x02c8 }
            int r8 = r8 + r15
            r10.bp = r8     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r10 = r1.lexer     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r0 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r0 = r0.len     // Catch:{ all -> 0x02c8 }
            if (r8 < r0) goto L_0x006a
            r0 = 26
            goto L_0x0072
        L_0x006a:
            com.alibaba.fastjson.parser.JSONLexer r0 = r1.lexer     // Catch:{ all -> 0x02c8 }
            java.lang.String r0 = r0.text     // Catch:{ all -> 0x02c8 }
            char r0 = r0.charAt(r8)     // Catch:{ all -> 0x02c8 }
        L_0x0072:
            r10.ch = r0     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r0 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r0.token = r12     // Catch:{ all -> 0x02c8 }
            goto L_0x007e
        L_0x0079:
            com.alibaba.fastjson.parser.JSONLexer r0 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r0.nextToken(r12)     // Catch:{ all -> 0x02c8 }
        L_0x007e:
            r0 = 0
            goto L_0x0093
        L_0x0080:
            com.alibaba.fastjson.parser.JSONLexer r0 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r0 = r0.features     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.Feature r8 = com.alibaba.fastjson.parser.Feature.AllowISO8601DateFormat     // Catch:{ all -> 0x02c8 }
            int r8 = r8.mask     // Catch:{ all -> 0x02c8 }
            r0 = r0 & r8
            if (r0 != 0) goto L_0x008d
            r0 = 1
            goto L_0x0093
        L_0x008d:
            com.alibaba.fastjson.parser.JSONLexer r0 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r0.nextToken(r11)     // Catch:{ all -> 0x02c8 }
            r0 = 0
        L_0x0093:
            r8 = 0
        L_0x0094:
            if (r0 == 0) goto L_0x0119
            com.alibaba.fastjson.parser.JSONLexer r10 = r1.lexer     // Catch:{ all -> 0x02c8 }
            char r10 = r10.ch     // Catch:{ all -> 0x02c8 }
            if (r10 != r13) goto L_0x0119
            com.alibaba.fastjson.parser.JSONLexer r10 = r1.lexer     // Catch:{ all -> 0x02c8 }
            java.lang.String r10 = r10.scanStringValue(r13)     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r12 = r1.lexer     // Catch:{ all -> 0x02c8 }
            char r12 = r12.ch     // Catch:{ all -> 0x02c8 }
            r6 = r12
            r12 = 44
            if (r6 != r12) goto L_0x00de
            com.alibaba.fastjson.parser.JSONLexer r12 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r11 = r12.bp     // Catch:{ all -> 0x02c8 }
            int r11 = r11 + r15
            r12.bp = r11     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r12 = r1.lexer     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r14 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r14 = r14.len     // Catch:{ all -> 0x02c8 }
            if (r11 < r14) goto L_0x00bd
            r14 = 26
            goto L_0x00c5
        L_0x00bd:
            com.alibaba.fastjson.parser.JSONLexer r14 = r1.lexer     // Catch:{ all -> 0x02c8 }
            java.lang.String r14 = r14.text     // Catch:{ all -> 0x02c8 }
            char r14 = r14.charAt(r11)     // Catch:{ all -> 0x02c8 }
        L_0x00c5:
            r12.ch = r14     // Catch:{ all -> 0x02c8 }
            r6 = r14
            r2.add(r10)     // Catch:{ all -> 0x02c8 }
            int r11 = r1.resolveStatus     // Catch:{ all -> 0x02c8 }
            if (r11 != r15) goto L_0x00d2
            r16.checkListResolve(r17)     // Catch:{ all -> 0x02c8 }
        L_0x00d2:
            if (r6 != r13) goto L_0x00d7
            r14 = 4
            goto L_0x02bd
        L_0x00d7:
            r0 = 0
            com.alibaba.fastjson.parser.JSONLexer r11 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r11.nextToken()     // Catch:{ all -> 0x02c8 }
            goto L_0x0119
        L_0x00de:
            if (r6 != r9) goto L_0x0114
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r11 = r9.bp     // Catch:{ all -> 0x02c8 }
            int r11 = r11 + r15
            r9.bp = r11     // Catch:{ all -> 0x02c8 }
            r9 = r11
            com.alibaba.fastjson.parser.JSONLexer r11 = r1.lexer     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r12 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r12 = r12.len     // Catch:{ all -> 0x02c8 }
            if (r9 < r12) goto L_0x00f3
            r12 = 26
            goto L_0x00fb
        L_0x00f3:
            com.alibaba.fastjson.parser.JSONLexer r12 = r1.lexer     // Catch:{ all -> 0x02c8 }
            java.lang.String r12 = r12.text     // Catch:{ all -> 0x02c8 }
            char r12 = r12.charAt(r9)     // Catch:{ all -> 0x02c8 }
        L_0x00fb:
            r11.ch = r12     // Catch:{ all -> 0x02c8 }
            r2.add(r10)     // Catch:{ all -> 0x02c8 }
            int r9 = r1.resolveStatus     // Catch:{ all -> 0x02c8 }
            if (r9 != r15) goto L_0x0107
            r16.checkListResolve(r17)     // Catch:{ all -> 0x02c8 }
        L_0x0107:
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r11 = 16
            r9.nextToken(r11)     // Catch:{ all -> 0x02c8 }
            if (r4 != 0) goto L_0x0113
            r1.contex = r5
        L_0x0113:
            return
        L_0x0114:
            com.alibaba.fastjson.parser.JSONLexer r11 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r11.nextToken()     // Catch:{ all -> 0x02c8 }
        L_0x0119:
            com.alibaba.fastjson.parser.JSONLexer r10 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r10 = r10.token     // Catch:{ all -> 0x02c8 }
            r3 = r10
        L_0x011e:
            r10 = 16
            if (r3 != r10) goto L_0x0138
            com.alibaba.fastjson.parser.JSONLexer r10 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r10 = r10.features     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.Feature r11 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas     // Catch:{ all -> 0x02c8 }
            int r11 = r11.mask     // Catch:{ all -> 0x02c8 }
            r10 = r10 & r11
            if (r10 == 0) goto L_0x0138
            com.alibaba.fastjson.parser.JSONLexer r10 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r10.nextToken()     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r10 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r10 = r10.token     // Catch:{ all -> 0x02c8 }
            r3 = r10
            goto L_0x011e
        L_0x0138:
            r10 = 2
            if (r3 == r10) goto L_0x0242
            r10 = 3
            if (r3 == r10) goto L_0x021c
            r10 = 4
            if (r3 == r10) goto L_0x01e7
            r10 = 6
            if (r3 == r10) goto L_0x01d8
            r10 = 7
            if (r3 == r10) goto L_0x01ca
            r10 = 8
            if (r3 == r10) goto L_0x01bf
            r10 = 12
            if (r3 == r10) goto L_0x0196
            r10 = 20
            if (r3 == r10) goto L_0x018e
            r10 = 23
            if (r3 == r10) goto L_0x0182
            r10 = 14
            if (r3 == r10) goto L_0x0172
            r11 = 15
            if (r3 == r11) goto L_0x0166
            java.lang.Object r11 = r16.parse()     // Catch:{ all -> 0x02c8 }
            r14 = 4
            goto L_0x0254
        L_0x0166:
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r10 = 16
            r9.nextToken(r10)     // Catch:{ all -> 0x02c8 }
            if (r4 != 0) goto L_0x0171
            r1.contex = r5
        L_0x0171:
            return
        L_0x0172:
            com.alibaba.fastjson.JSONArray r11 = new com.alibaba.fastjson.JSONArray     // Catch:{ all -> 0x02c8 }
            r11.<init>()     // Catch:{ all -> 0x02c8 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r8)     // Catch:{ all -> 0x02c8 }
            r1.parseArray((java.util.Collection) r11, (java.lang.Object) r12)     // Catch:{ all -> 0x02c8 }
            r12 = r11
            r14 = 4
            goto L_0x0254
        L_0x0182:
            r10 = 14
            r11 = 0
            com.alibaba.fastjson.parser.JSONLexer r12 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r14 = 4
            r12.nextToken(r14)     // Catch:{ all -> 0x02c8 }
            r14 = 4
            goto L_0x0254
        L_0x018e:
            com.alibaba.fastjson.JSONException r9 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x02c8 }
            java.lang.String r10 = "unclosed jsonArray"
            r9.<init>(r10)     // Catch:{ all -> 0x02c8 }
            throw r9     // Catch:{ all -> 0x02c8 }
        L_0x0196:
            r10 = 14
            com.alibaba.fastjson.parser.JSONLexer r11 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r11 = r11.features     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.Feature r12 = com.alibaba.fastjson.parser.Feature.OrderedField     // Catch:{ all -> 0x02c8 }
            int r12 = r12.mask     // Catch:{ all -> 0x02c8 }
            r11 = r11 & r12
            if (r11 == 0) goto L_0x01ae
            com.alibaba.fastjson.JSONObject r11 = new com.alibaba.fastjson.JSONObject     // Catch:{ all -> 0x02c8 }
            java.util.LinkedHashMap r12 = new java.util.LinkedHashMap     // Catch:{ all -> 0x02c8 }
            r12.<init>()     // Catch:{ all -> 0x02c8 }
            r11.<init>((java.util.Map<java.lang.String, java.lang.Object>) r12)     // Catch:{ all -> 0x02c8 }
            goto L_0x01b3
        L_0x01ae:
            com.alibaba.fastjson.JSONObject r11 = new com.alibaba.fastjson.JSONObject     // Catch:{ all -> 0x02c8 }
            r11.<init>()     // Catch:{ all -> 0x02c8 }
        L_0x01b3:
            java.lang.Integer r12 = java.lang.Integer.valueOf(r8)     // Catch:{ all -> 0x02c8 }
            java.lang.Object r12 = r1.parseObject((java.util.Map) r11, (java.lang.Object) r12)     // Catch:{ all -> 0x02c8 }
            r11 = r12
            r14 = 4
            goto L_0x0254
        L_0x01bf:
            r10 = 14
            r11 = 0
            com.alibaba.fastjson.parser.JSONLexer r12 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r14 = 4
            r12.nextToken(r14)     // Catch:{ all -> 0x02c8 }
            goto L_0x0254
        L_0x01ca:
            r10 = 14
            r14 = 4
            java.lang.Boolean r11 = java.lang.Boolean.FALSE     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r12 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r9 = 16
            r12.nextToken(r9)     // Catch:{ all -> 0x02c8 }
            goto L_0x0254
        L_0x01d8:
            r10 = 14
            r14 = 4
            java.lang.Boolean r9 = java.lang.Boolean.TRUE     // Catch:{ all -> 0x02c8 }
            r11 = r9
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r12 = 16
            r9.nextToken(r12)     // Catch:{ all -> 0x02c8 }
            goto L_0x0254
        L_0x01e7:
            r10 = 14
            r14 = 4
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            java.lang.String r9 = r9.stringVal()     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r11 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r12 = 16
            r11.nextToken(r12)     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r11 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r11 = r11.features     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.Feature r12 = com.alibaba.fastjson.parser.Feature.AllowISO8601DateFormat     // Catch:{ all -> 0x02c8 }
            int r12 = r12.mask     // Catch:{ all -> 0x02c8 }
            r11 = r11 & r12
            if (r11 == 0) goto L_0x021a
            com.alibaba.fastjson.parser.JSONLexer r11 = new com.alibaba.fastjson.parser.JSONLexer     // Catch:{ all -> 0x02c8 }
            r11.<init>(r9)     // Catch:{ all -> 0x02c8 }
            boolean r12 = r11.scanISO8601DateIfMatch(r15)     // Catch:{ all -> 0x02c8 }
            if (r12 == 0) goto L_0x0214
            java.util.Calendar r12 = r11.calendar     // Catch:{ all -> 0x02c8 }
            java.util.Date r12 = r12.getTime()     // Catch:{ all -> 0x02c8 }
            goto L_0x0215
        L_0x0214:
            r12 = r9
        L_0x0215:
            r11.close()     // Catch:{ all -> 0x02c8 }
            r11 = r12
            goto L_0x0254
        L_0x021a:
            r11 = r9
            goto L_0x0254
        L_0x021c:
            r10 = 14
            r14 = 4
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r9 = r9.features     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.Feature r11 = com.alibaba.fastjson.parser.Feature.UseBigDecimal     // Catch:{ all -> 0x02c8 }
            int r11 = r11.mask     // Catch:{ all -> 0x02c8 }
            r9 = r9 & r11
            if (r9 == 0) goto L_0x0232
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            java.lang.Number r9 = r9.decimalValue(r15)     // Catch:{ all -> 0x02c8 }
            r11 = r9
            goto L_0x023a
        L_0x0232:
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r11 = 0
            java.lang.Number r9 = r9.decimalValue(r11)     // Catch:{ all -> 0x02c8 }
            r11 = r9
        L_0x023a:
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r12 = 16
            r9.nextToken(r12)     // Catch:{ all -> 0x02c8 }
            goto L_0x0254
        L_0x0242:
            r10 = 14
            r14 = 4
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            java.lang.Number r9 = r9.integerValue()     // Catch:{ all -> 0x02c8 }
            r11 = r9
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r12 = 16
            r9.nextToken(r12)     // Catch:{ all -> 0x02c8 }
        L_0x0254:
            r2.add(r11)     // Catch:{ all -> 0x02c8 }
            int r9 = r1.resolveStatus     // Catch:{ all -> 0x02c8 }
            if (r9 != r15) goto L_0x025e
            r16.checkListResolve(r17)     // Catch:{ all -> 0x02c8 }
        L_0x025e:
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r9 = r9.token     // Catch:{ all -> 0x02c8 }
            r12 = 16
            if (r9 != r12) goto L_0x02bd
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            char r9 = r9.ch     // Catch:{ all -> 0x02c8 }
            r6 = r9
            if (r6 != r13) goto L_0x027b
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r10 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r10 = r10.bp     // Catch:{ all -> 0x02c8 }
            r9.pos = r10     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r9.scanString()     // Catch:{ all -> 0x02c8 }
            goto L_0x02bd
        L_0x027b:
            r9 = 48
            if (r6 < r9) goto L_0x0291
            r9 = 57
            if (r6 > r9) goto L_0x0291
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r10 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r10 = r10.bp     // Catch:{ all -> 0x02c8 }
            r9.pos = r10     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r9.scanNumber()     // Catch:{ all -> 0x02c8 }
            goto L_0x02bd
        L_0x0291:
            r9 = 123(0x7b, float:1.72E-43)
            if (r6 != r9) goto L_0x02b8
            com.alibaba.fastjson.parser.JSONLexer r10 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r9 = 12
            r10.token = r9     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r10 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r9 = r10.bp     // Catch:{ all -> 0x02c8 }
            int r9 = r9 + r15
            r10.bp = r9     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r10 = r1.lexer     // Catch:{ all -> 0x02c8 }
            com.alibaba.fastjson.parser.JSONLexer r12 = r1.lexer     // Catch:{ all -> 0x02c8 }
            int r12 = r12.len     // Catch:{ all -> 0x02c8 }
            if (r9 < r12) goto L_0x02ad
            r12 = 26
            goto L_0x02b5
        L_0x02ad:
            com.alibaba.fastjson.parser.JSONLexer r12 = r1.lexer     // Catch:{ all -> 0x02c8 }
            java.lang.String r12 = r12.text     // Catch:{ all -> 0x02c8 }
            char r12 = r12.charAt(r9)     // Catch:{ all -> 0x02c8 }
        L_0x02b5:
            r10.ch = r12     // Catch:{ all -> 0x02c8 }
            goto L_0x02bd
        L_0x02b8:
            com.alibaba.fastjson.parser.JSONLexer r9 = r1.lexer     // Catch:{ all -> 0x02c8 }
            r9.nextToken()     // Catch:{ all -> 0x02c8 }
        L_0x02bd:
            int r8 = r8 + 1
            r9 = 93
            r11 = 4
            r12 = 12
            r14 = 16
            goto L_0x0094
        L_0x02c8:
            r0 = move-exception
            if (r4 != 0) goto L_0x02cd
            r1.contex = r5
        L_0x02cd:
            throw r0
        L_0x02ce:
            r7 = r18
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "syntax error, expect [, actual "
            r4.append(r5)
            java.lang.String r5 = com.alibaba.fastjson.parser.JSONToken.name(r3)
            r4.append(r5)
            java.lang.String r5 = ", pos "
            r4.append(r5)
            com.alibaba.fastjson.parser.JSONLexer r5 = r1.lexer
            int r5 = r5.pos
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r0.<init>(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.DefaultJSONParser.parseArray(java.util.Collection, java.lang.Object):void");
    }

    /* access modifiers changed from: protected */
    public void addResolveTask(ResolveTask task) {
        if (this.resolveTaskList == null) {
            this.resolveTaskList = new ArrayList(2);
        }
        this.resolveTaskList.add(task);
    }

    /* access modifiers changed from: protected */
    public ResolveTask getLastResolveTask() {
        List<ResolveTask> list = this.resolveTaskList;
        return list.get(list.size() - 1);
    }

    public List<ExtraProcessor> getExtraProcessors() {
        if (this.extraProcessors == null) {
            this.extraProcessors = new ArrayList(2);
        }
        return this.extraProcessors;
    }

    public List<ExtraTypeProvider> getExtraTypeProviders() {
        if (this.extraTypeProviders == null) {
            this.extraTypeProviders = new ArrayList(2);
        }
        return this.extraTypeProviders;
    }

    public void setContext(ParseContext context) {
        if (!this.lexer.disableCircularReferenceDetect) {
            this.contex = context;
        }
    }

    /* access modifiers changed from: protected */
    public void popContext() {
        this.contex = this.contex.parent;
        ParseContext[] parseContextArr = this.contextArray;
        int i = this.contextArrayIndex;
        parseContextArr[i - 1] = null;
        this.contextArrayIndex = i - 1;
    }

    /* access modifiers changed from: protected */
    public ParseContext setContext(ParseContext parent, Object object, Object fieldName) {
        if (this.lexer.disableCircularReferenceDetect) {
            return null;
        }
        this.contex = new ParseContext(parent, object, fieldName);
        int i = this.contextArrayIndex;
        this.contextArrayIndex = i + 1;
        ParseContext[] parseContextArr = this.contextArray;
        if (parseContextArr == null) {
            this.contextArray = new ParseContext[8];
        } else if (i >= parseContextArr.length) {
            ParseContext[] newArray = new ParseContext[((parseContextArr.length * 3) / 2)];
            System.arraycopy(parseContextArr, 0, newArray, 0, parseContextArr.length);
            this.contextArray = newArray;
        }
        ParseContext[] parseContextArr2 = this.contextArray;
        ParseContext parseContext = this.contex;
        parseContextArr2[i] = parseContext;
        return parseContext;
    }

    public Object parse() {
        return parse((Object) null);
    }

    public Object parse(Object fieldName) {
        int i = this.lexer.token;
        if (i != 2) {
            boolean useBigDecimal = true;
            if (i == 3) {
                if ((this.lexer.features & Feature.UseBigDecimal.mask) == 0) {
                    useBigDecimal = false;
                }
                Object value = this.lexer.decimalValue(useBigDecimal);
                this.lexer.nextToken();
                return value;
            } else if (i == 4) {
                String stringLiteral = this.lexer.stringVal();
                this.lexer.nextToken(16);
                if ((this.lexer.features & Feature.AllowISO8601DateFormat.mask) != 0) {
                    JSONLexer iso8601Lexer = new JSONLexer(stringLiteral);
                    try {
                        if (iso8601Lexer.scanISO8601DateIfMatch(true)) {
                            return iso8601Lexer.calendar.getTime();
                        }
                        iso8601Lexer.close();
                    } finally {
                        iso8601Lexer.close();
                    }
                }
                return stringLiteral;
            } else if (i == 12) {
                return parseObject((Map) (this.lexer.features & Feature.OrderedField.mask) != 0 ? new JSONObject((Map<String, Object>) new LinkedHashMap()) : new JSONObject(), fieldName);
            } else if (i != 14) {
                switch (i) {
                    case 6:
                        this.lexer.nextToken(16);
                        return Boolean.TRUE;
                    case 7:
                        this.lexer.nextToken(16);
                        return Boolean.FALSE;
                    case 8:
                        break;
                    case 9:
                        this.lexer.nextToken(18);
                        if (this.lexer.token == 18) {
                            this.lexer.nextToken(10);
                            accept(10);
                            long time = this.lexer.integerValue().longValue();
                            accept(2);
                            accept(11);
                            return new Date(time);
                        }
                        throw new JSONException("syntax error, " + this.lexer.info());
                    default:
                        switch (i) {
                            case 20:
                                if (this.lexer.isBlankInput()) {
                                    return null;
                                }
                                throw new JSONException("syntax error, " + this.lexer.info());
                            case 21:
                                this.lexer.nextToken();
                                HashSet<Object> set = new HashSet<>();
                                parseArray((Collection) set, fieldName);
                                return set;
                            case 22:
                                this.lexer.nextToken();
                                TreeSet<Object> treeSet = new TreeSet<>();
                                parseArray((Collection) treeSet, fieldName);
                                return treeSet;
                            case 23:
                                break;
                            default:
                                throw new JSONException("syntax error, " + this.lexer.info());
                        }
                }
                this.lexer.nextToken();
                return null;
            } else {
                JSONArray array = new JSONArray();
                parseArray((Collection) array, fieldName);
                return array;
            }
        } else {
            Number intValue = this.lexer.integerValue();
            this.lexer.nextToken();
            return intValue;
        }
    }

    public void config(Feature feature, boolean state) {
        this.lexer.config(feature, state);
    }

    public final void accept(int token) {
        if (this.lexer.token == token) {
            this.lexer.nextToken();
            return;
        }
        throw new JSONException("syntax error, expect " + JSONToken.name(token) + ", actual " + JSONToken.name(this.lexer.token));
    }

    public void close() {
        try {
            if ((this.lexer.features & Feature.AutoCloseSource.mask) != 0) {
                if (this.lexer.token != 20) {
                    throw new JSONException("not close json text, token : " + JSONToken.name(this.lexer.token));
                }
            }
        } finally {
            this.lexer.close();
        }
    }

    public void handleResovleTask(Object value) {
        List<ResolveTask> list = this.resolveTaskList;
        if (list != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                ResolveTask task = this.resolveTaskList.get(i);
                FieldDeserializer fieldDeser = task.fieldDeserializer;
                if (fieldDeser != null) {
                    Object object = null;
                    if (task.ownerContext != null) {
                        object = task.ownerContext.object;
                    }
                    String ref = task.referenceValue;
                    Object refValue = null;
                    if (ref.startsWith("$")) {
                        for (int j = 0; j < this.contextArrayIndex; j++) {
                            if (ref.equals(this.contextArray[j].toString())) {
                                refValue = this.contextArray[j].object;
                            }
                        }
                    } else {
                        refValue = task.context.object;
                    }
                    fieldDeser.setValue(object, refValue);
                }
            }
        }
    }

    public String parseString() {
        int token = this.lexer.token;
        if (token == 4) {
            String val = this.lexer.stringVal();
            char c = this.lexer.ch;
            char c2 = JSONLexer.EOI;
            if (c == ',') {
                JSONLexer jSONLexer = this.lexer;
                int i = jSONLexer.bp + 1;
                jSONLexer.bp = i;
                int index = i;
                JSONLexer jSONLexer2 = this.lexer;
                if (index < jSONLexer2.len) {
                    c2 = this.lexer.text.charAt(index);
                }
                jSONLexer2.ch = c2;
                this.lexer.token = 16;
            } else if (this.lexer.ch == ']') {
                JSONLexer jSONLexer3 = this.lexer;
                int i2 = jSONLexer3.bp + 1;
                jSONLexer3.bp = i2;
                int index2 = i2;
                JSONLexer jSONLexer4 = this.lexer;
                if (index2 < jSONLexer4.len) {
                    c2 = this.lexer.text.charAt(index2);
                }
                jSONLexer4.ch = c2;
                this.lexer.token = 15;
            } else if (this.lexer.ch == '}') {
                JSONLexer jSONLexer5 = this.lexer;
                int i3 = jSONLexer5.bp + 1;
                jSONLexer5.bp = i3;
                int index3 = i3;
                JSONLexer jSONLexer6 = this.lexer;
                if (index3 < jSONLexer6.len) {
                    c2 = this.lexer.text.charAt(index3);
                }
                jSONLexer6.ch = c2;
                this.lexer.token = 13;
            } else {
                this.lexer.nextToken();
            }
            return val;
        } else if (token == 2) {
            String val2 = this.lexer.numberString();
            this.lexer.nextToken(16);
            return val2;
        } else {
            Object value = parse();
            if (value == null) {
                return null;
            }
            return value.toString();
        }
    }

    public static class ResolveTask {
        /* access modifiers changed from: private */
        public final ParseContext context;
        public FieldDeserializer fieldDeserializer;
        public ParseContext ownerContext;
        /* access modifiers changed from: private */
        public final String referenceValue;

        public ResolveTask(ParseContext context2, String referenceValue2) {
            this.context = context2;
            this.referenceValue = referenceValue2;
        }
    }
}

package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class MapDeserializer implements ObjectDeserializer {
    public static MapDeserializer instance = new MapDeserializer();

    MapDeserializer() {
    }

    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        if (type == JSONObject.class && parser.fieldTypeResolver == null) {
            return parser.parseObject();
        }
        JSONLexer lexer = parser.lexer;
        if (lexer.token == 8) {
            lexer.nextToken(16);
            return null;
        }
        Map<?, ?> map = createMap(type);
        ParseContext context = parser.contex;
        try {
            parser.setContext(context, map, fieldName);
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type keyType = parameterizedType.getActualTypeArguments()[0];
                Type valueType = parameterizedType.getActualTypeArguments()[1];
                if (String.class == keyType) {
                    return parseMap(parser, map, valueType, fieldName);
                }
                T parseMap = parseMap(parser, map, keyType, valueType, fieldName);
                parser.setContext(context);
                return parseMap;
            }
            T parseObject = parser.parseObject((Map) map, fieldName);
            parser.setContext(context);
            return parseObject;
        } finally {
            parser.setContext(context);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:51:?, code lost:
        r4 = r11.config.getDeserializer(r5);
        r0.nextToken(16);
        r11.resolveStatus = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0119, code lost:
        if (r1 == null) goto L_0x0122;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x011d, code lost:
        if ((r14 instanceof java.lang.Integer) != false) goto L_0x0122;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x011f, code lost:
        r11.popContext();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0122, code lost:
        r6 = (java.util.Map) r4.deserialze(r11, r5, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0128, code lost:
        r11.setContext(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x012b, code lost:
        return r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Map parseMap(com.alibaba.fastjson.parser.DefaultJSONParser r11, java.util.Map<java.lang.String, java.lang.Object> r12, java.lang.reflect.Type r13, java.lang.Object r14) {
        /*
            com.alibaba.fastjson.parser.JSONLexer r0 = r11.lexer
            int r1 = r0.token
            r2 = 12
            if (r1 != r2) goto L_0x0199
            com.alibaba.fastjson.parser.ParseContext r1 = r11.contex
        L_0x000a:
            r0.skipWhitespace()     // Catch:{ all -> 0x0194 }
            char r2 = r0.ch     // Catch:{ all -> 0x0194 }
            int r3 = r0.features     // Catch:{ all -> 0x0194 }
            com.alibaba.fastjson.parser.Feature r4 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas     // Catch:{ all -> 0x0194 }
            int r4 = r4.mask     // Catch:{ all -> 0x0194 }
            r3 = r3 & r4
            if (r3 == 0) goto L_0x0026
        L_0x0018:
            r3 = 44
            if (r2 != r3) goto L_0x0026
            r0.next()     // Catch:{ all -> 0x0194 }
            r0.skipWhitespace()     // Catch:{ all -> 0x0194 }
            char r3 = r0.ch     // Catch:{ all -> 0x0194 }
            r2 = r3
            goto L_0x0018
        L_0x0026:
            r3 = 0
            java.lang.String r4 = "syntax error, "
            r5 = 58
            r6 = 34
            r7 = 16
            if (r2 != r6) goto L_0x005a
            com.alibaba.fastjson.parser.SymbolTable r8 = r11.symbolTable     // Catch:{ all -> 0x0194 }
            java.lang.String r8 = r0.scanSymbol(r8, r6)     // Catch:{ all -> 0x0194 }
            r0.skipWhitespace()     // Catch:{ all -> 0x0194 }
            char r9 = r0.ch     // Catch:{ all -> 0x0194 }
            r2 = r9
            if (r2 != r5) goto L_0x0041
            goto L_0x00d1
        L_0x0041:
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0194 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0194 }
            r5.<init>()     // Catch:{ all -> 0x0194 }
            r5.append(r4)     // Catch:{ all -> 0x0194 }
            java.lang.String r4 = r0.info()     // Catch:{ all -> 0x0194 }
            r5.append(r4)     // Catch:{ all -> 0x0194 }
            java.lang.String r4 = r5.toString()     // Catch:{ all -> 0x0194 }
            r3.<init>(r4)     // Catch:{ all -> 0x0194 }
            throw r3     // Catch:{ all -> 0x0194 }
        L_0x005a:
            r8 = 125(0x7d, float:1.75E-43)
            if (r2 != r8) goto L_0x006b
            r0.next()     // Catch:{ all -> 0x0194 }
            r0.sp = r3     // Catch:{ all -> 0x0194 }
            r0.nextToken(r7)     // Catch:{ all -> 0x0194 }
            r11.setContext(r1)
            return r12
        L_0x006b:
            r8 = 39
            if (r2 != r8) goto L_0x00b9
            int r9 = r0.features     // Catch:{ all -> 0x0194 }
            com.alibaba.fastjson.parser.Feature r10 = com.alibaba.fastjson.parser.Feature.AllowSingleQuotes     // Catch:{ all -> 0x0194 }
            int r10 = r10.mask     // Catch:{ all -> 0x0194 }
            r9 = r9 & r10
            if (r9 == 0) goto L_0x00a0
            com.alibaba.fastjson.parser.SymbolTable r9 = r11.symbolTable     // Catch:{ all -> 0x0194 }
            java.lang.String r8 = r0.scanSymbol(r9, r8)     // Catch:{ all -> 0x0194 }
            r0.skipWhitespace()     // Catch:{ all -> 0x0194 }
            char r9 = r0.ch     // Catch:{ all -> 0x0194 }
            r2 = r9
            if (r2 != r5) goto L_0x0087
            goto L_0x00d1
        L_0x0087:
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0194 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0194 }
            r5.<init>()     // Catch:{ all -> 0x0194 }
            r5.append(r4)     // Catch:{ all -> 0x0194 }
            java.lang.String r4 = r0.info()     // Catch:{ all -> 0x0194 }
            r5.append(r4)     // Catch:{ all -> 0x0194 }
            java.lang.String r4 = r5.toString()     // Catch:{ all -> 0x0194 }
            r3.<init>(r4)     // Catch:{ all -> 0x0194 }
            throw r3     // Catch:{ all -> 0x0194 }
        L_0x00a0:
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0194 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0194 }
            r5.<init>()     // Catch:{ all -> 0x0194 }
            r5.append(r4)     // Catch:{ all -> 0x0194 }
            java.lang.String r4 = r0.info()     // Catch:{ all -> 0x0194 }
            r5.append(r4)     // Catch:{ all -> 0x0194 }
            java.lang.String r4 = r5.toString()     // Catch:{ all -> 0x0194 }
            r3.<init>(r4)     // Catch:{ all -> 0x0194 }
            throw r3     // Catch:{ all -> 0x0194 }
        L_0x00b9:
            int r4 = r0.features     // Catch:{ all -> 0x0194 }
            com.alibaba.fastjson.parser.Feature r8 = com.alibaba.fastjson.parser.Feature.AllowUnQuotedFieldNames     // Catch:{ all -> 0x0194 }
            int r8 = r8.mask     // Catch:{ all -> 0x0194 }
            r4 = r4 & r8
            if (r4 == 0) goto L_0x018c
            com.alibaba.fastjson.parser.SymbolTable r4 = r11.symbolTable     // Catch:{ all -> 0x0194 }
            java.lang.String r4 = r0.scanSymbolUnQuoted(r4)     // Catch:{ all -> 0x0194 }
            r8 = r4
            r0.skipWhitespace()     // Catch:{ all -> 0x0194 }
            char r4 = r0.ch     // Catch:{ all -> 0x0194 }
            r2 = r4
            if (r2 != r5) goto L_0x016b
        L_0x00d1:
            r0.next()     // Catch:{ all -> 0x0194 }
            r0.skipWhitespace()     // Catch:{ all -> 0x0194 }
            char r4 = r0.ch     // Catch:{ all -> 0x0194 }
            r2 = r4
            r0.sp = r3     // Catch:{ all -> 0x0194 }
            java.lang.String r3 = "@type"
            r4 = 13
            if (r8 != r3) goto L_0x012c
            com.alibaba.fastjson.parser.Feature r3 = com.alibaba.fastjson.parser.Feature.DisableSpecialKeyDetect     // Catch:{ all -> 0x0194 }
            boolean r3 = r0.isEnabled(r3)     // Catch:{ all -> 0x0194 }
            if (r3 != 0) goto L_0x012c
            com.alibaba.fastjson.parser.SymbolTable r3 = r11.symbolTable     // Catch:{ all -> 0x0194 }
            java.lang.String r3 = r0.scanSymbol(r3, r6)     // Catch:{ all -> 0x0194 }
            com.alibaba.fastjson.parser.ParserConfig r5 = r11.config     // Catch:{ all -> 0x0194 }
            java.lang.ClassLoader r5 = r5.defaultClassLoader     // Catch:{ all -> 0x0194 }
            java.lang.Class r5 = com.alibaba.fastjson.util.TypeUtils.loadClass(r3, r5)     // Catch:{ all -> 0x0194 }
            java.lang.Class r6 = r12.getClass()     // Catch:{ all -> 0x0194 }
            if (r5 != r6) goto L_0x010d
            r0.nextToken(r7)     // Catch:{ all -> 0x0194 }
            int r6 = r0.token     // Catch:{ all -> 0x0194 }
            if (r6 != r4) goto L_0x000a
            r0.nextToken(r7)     // Catch:{ all -> 0x0194 }
            r11.setContext(r1)
            return r12
        L_0x010d:
            com.alibaba.fastjson.parser.ParserConfig r4 = r11.config     // Catch:{ all -> 0x0194 }
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r4 = r4.getDeserializer(r5)     // Catch:{ all -> 0x0194 }
            r0.nextToken(r7)     // Catch:{ all -> 0x0194 }
            r6 = 2
            r11.resolveStatus = r6     // Catch:{ all -> 0x0194 }
            if (r1 == 0) goto L_0x0122
            boolean r6 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x0194 }
            if (r6 != 0) goto L_0x0122
            r11.popContext()     // Catch:{ all -> 0x0194 }
        L_0x0122:
            java.lang.Object r6 = r4.deserialze(r11, r5, r14)     // Catch:{ all -> 0x0194 }
            java.util.Map r6 = (java.util.Map) r6     // Catch:{ all -> 0x0194 }
            r11.setContext(r1)
            return r6
        L_0x012c:
            r0.nextToken()     // Catch:{ all -> 0x0194 }
            r11.setContext(r1)     // Catch:{ all -> 0x0194 }
            int r3 = r0.token     // Catch:{ all -> 0x0194 }
            r5 = 8
            if (r3 != r5) goto L_0x013d
            r3 = 0
            r0.nextToken()     // Catch:{ all -> 0x0194 }
            goto L_0x0141
        L_0x013d:
            java.lang.Object r3 = r11.parseObject((java.lang.reflect.Type) r13, (java.lang.Object) r8)     // Catch:{ all -> 0x0194 }
        L_0x0141:
            r12.put(r8, r3)     // Catch:{ all -> 0x0194 }
            int r5 = r11.resolveStatus     // Catch:{ all -> 0x0194 }
            r6 = 1
            if (r5 != r6) goto L_0x014c
            r11.checkMapResolve(r12, r8)     // Catch:{ all -> 0x0194 }
        L_0x014c:
            r11.setContext(r1, r3, r8)     // Catch:{ all -> 0x0194 }
            int r5 = r0.token     // Catch:{ all -> 0x0194 }
            r6 = 20
            if (r5 == r6) goto L_0x0166
            r6 = 15
            if (r5 != r6) goto L_0x015a
            goto L_0x0166
        L_0x015a:
            if (r5 != r4) goto L_0x0164
            r0.nextToken()     // Catch:{ all -> 0x0194 }
            r11.setContext(r1)
            return r12
        L_0x0164:
            goto L_0x000a
        L_0x0166:
            r11.setContext(r1)
            return r12
        L_0x016b:
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0194 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0194 }
            r4.<init>()     // Catch:{ all -> 0x0194 }
            java.lang.String r5 = "expect ':' at "
            r4.append(r5)     // Catch:{ all -> 0x0194 }
            int r5 = r0.pos     // Catch:{ all -> 0x0194 }
            r4.append(r5)     // Catch:{ all -> 0x0194 }
            java.lang.String r5 = ", actual "
            r4.append(r5)     // Catch:{ all -> 0x0194 }
            r4.append(r2)     // Catch:{ all -> 0x0194 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0194 }
            r3.<init>(r4)     // Catch:{ all -> 0x0194 }
            throw r3     // Catch:{ all -> 0x0194 }
        L_0x018c:
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0194 }
            java.lang.String r4 = "syntax error"
            r3.<init>(r4)     // Catch:{ all -> 0x0194 }
            throw r3     // Catch:{ all -> 0x0194 }
        L_0x0194:
            r2 = move-exception
            r11.setContext(r1)
            throw r2
        L_0x0199:
            com.alibaba.fastjson.JSONException r1 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "syntax error, expect {, actual "
            r2.append(r3)
            int r3 = r0.token
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.MapDeserializer.parseMap(com.alibaba.fastjson.parser.DefaultJSONParser, java.util.Map, java.lang.reflect.Type, java.lang.Object):java.util.Map");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x005d, code lost:
        r10 = null;
        r0.nextTokenWithChar(':');
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0063, code lost:
        if (r0.token != 4) goto L_0x00ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0065, code lost:
        r8 = r0.stringVal();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x006f, code lost:
        if ("..".equals(r8) == false) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0071, code lost:
        r10 = r5.parent.object;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x007d, code lost:
        if ("$".equals(r8) == false) goto L_0x008c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x007f, code lost:
        r7 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0082, code lost:
        if (r7.parent == null) goto L_0x0088;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0084, code lost:
        r7 = r7.parent;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0088, code lost:
        r10 = r7.object;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x008c, code lost:
        r11.addResolveTask(new com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask(r5, r8));
        r11.resolveStatus = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0096, code lost:
        r0.nextToken(13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x009c, code lost:
        if (r0.token != 13) goto L_0x00a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x009e, code lost:
        r0.nextToken(16);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00a1, code lost:
        r11.setContext(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00a5, code lost:
        return r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00ad, code lost:
        throw new com.alibaba.fastjson.JSONException("illegal ref");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00c8, code lost:
        throw new com.alibaba.fastjson.JSONException("illegal ref, " + com.alibaba.fastjson.parser.JSONToken.name(r1));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.Object parseMap(com.alibaba.fastjson.parser.DefaultJSONParser r11, java.util.Map<java.lang.Object, java.lang.Object> r12, java.lang.reflect.Type r13, java.lang.reflect.Type r14, java.lang.Object r15) {
        /*
            com.alibaba.fastjson.parser.JSONLexer r0 = r11.lexer
            int r1 = r0.token
            r2 = 16
            r3 = 12
            if (r1 == r3) goto L_0x0028
            if (r1 != r2) goto L_0x000d
            goto L_0x0028
        L_0x000d:
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "syntax error, expect {, actual "
            r3.append(r4)
            java.lang.String r4 = com.alibaba.fastjson.parser.JSONToken.name(r1)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            throw r2
        L_0x0028:
            com.alibaba.fastjson.parser.ParserConfig r3 = r11.config
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r3 = r3.getDeserializer(r13)
            com.alibaba.fastjson.parser.ParserConfig r4 = r11.config
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r4 = r4.getDeserializer(r14)
            r0.nextToken()
            com.alibaba.fastjson.parser.ParseContext r5 = r11.contex
        L_0x0039:
            int r6 = r0.token     // Catch:{ all -> 0x0138 }
            r1 = r6
            r6 = 13
            if (r1 != r6) goto L_0x0049
            r0.nextToken(r2)     // Catch:{ all -> 0x0138 }
            r11.setContext(r5)
            return r12
        L_0x0049:
            r7 = 1
            r8 = 58
            r9 = 4
            if (r1 != r9) goto L_0x00c9
            boolean r10 = r0.isRef()     // Catch:{ all -> 0x0138 }
            if (r10 == 0) goto L_0x00c9
            com.alibaba.fastjson.parser.Feature r10 = com.alibaba.fastjson.parser.Feature.DisableSpecialKeyDetect     // Catch:{ all -> 0x0138 }
            boolean r10 = r0.isEnabled(r10)     // Catch:{ all -> 0x0138 }
            if (r10 != 0) goto L_0x00c9
            r10 = 0
            r0.nextTokenWithChar(r8)     // Catch:{ all -> 0x0138 }
            int r8 = r0.token     // Catch:{ all -> 0x0138 }
            if (r8 != r9) goto L_0x00ae
            java.lang.String r8 = r0.stringVal()     // Catch:{ all -> 0x0138 }
            java.lang.String r9 = ".."
            boolean r9 = r9.equals(r8)     // Catch:{ all -> 0x0138 }
            if (r9 == 0) goto L_0x0077
            com.alibaba.fastjson.parser.ParseContext r7 = r5.parent     // Catch:{ all -> 0x0138 }
            java.lang.Object r9 = r7.object     // Catch:{ all -> 0x0138 }
            r10 = r9
            goto L_0x0096
        L_0x0077:
            java.lang.String r9 = "$"
            boolean r9 = r9.equals(r8)     // Catch:{ all -> 0x0138 }
            if (r9 == 0) goto L_0x008c
            r7 = r5
        L_0x0080:
            com.alibaba.fastjson.parser.ParseContext r9 = r7.parent     // Catch:{ all -> 0x0138 }
            if (r9 == 0) goto L_0x0088
            com.alibaba.fastjson.parser.ParseContext r9 = r7.parent     // Catch:{ all -> 0x0138 }
            r7 = r9
            goto L_0x0080
        L_0x0088:
            java.lang.Object r9 = r7.object     // Catch:{ all -> 0x0138 }
            r10 = r9
            goto L_0x0096
        L_0x008c:
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r9 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask     // Catch:{ all -> 0x0138 }
            r9.<init>(r5, r8)     // Catch:{ all -> 0x0138 }
            r11.addResolveTask(r9)     // Catch:{ all -> 0x0138 }
            r11.resolveStatus = r7     // Catch:{ all -> 0x0138 }
        L_0x0096:
            r0.nextToken(r6)     // Catch:{ all -> 0x0138 }
            int r7 = r0.token     // Catch:{ all -> 0x0138 }
            if (r7 != r6) goto L_0x00a6
            r0.nextToken(r2)     // Catch:{ all -> 0x0138 }
            r11.setContext(r5)
            return r10
        L_0x00a6:
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0138 }
            java.lang.String r6 = "illegal ref"
            r2.<init>(r6)     // Catch:{ all -> 0x0138 }
            throw r2     // Catch:{ all -> 0x0138 }
        L_0x00ae:
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0138 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0138 }
            r6.<init>()     // Catch:{ all -> 0x0138 }
            java.lang.String r7 = "illegal ref, "
            r6.append(r7)     // Catch:{ all -> 0x0138 }
            java.lang.String r7 = com.alibaba.fastjson.parser.JSONToken.name(r1)     // Catch:{ all -> 0x0138 }
            r6.append(r7)     // Catch:{ all -> 0x0138 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0138 }
            r2.<init>(r6)     // Catch:{ all -> 0x0138 }
            throw r2     // Catch:{ all -> 0x0138 }
        L_0x00c9:
            int r10 = r12.size()     // Catch:{ all -> 0x0138 }
            if (r10 != 0) goto L_0x00fa
            if (r1 != r9) goto L_0x00fa
            java.lang.String r9 = "@type"
            java.lang.String r10 = r0.stringVal()     // Catch:{ all -> 0x0138 }
            boolean r9 = r9.equals(r10)     // Catch:{ all -> 0x0138 }
            if (r9 == 0) goto L_0x00fa
            com.alibaba.fastjson.parser.Feature r9 = com.alibaba.fastjson.parser.Feature.DisableSpecialKeyDetect     // Catch:{ all -> 0x0138 }
            boolean r9 = r0.isEnabled(r9)     // Catch:{ all -> 0x0138 }
            if (r9 != 0) goto L_0x00fa
            r0.nextTokenWithChar(r8)     // Catch:{ all -> 0x0138 }
            r0.nextToken(r2)     // Catch:{ all -> 0x0138 }
            int r8 = r0.token     // Catch:{ all -> 0x0138 }
            if (r8 != r6) goto L_0x00f7
            r0.nextToken()     // Catch:{ all -> 0x0138 }
            r11.setContext(r5)
            return r12
        L_0x00f7:
            r0.nextToken()     // Catch:{ all -> 0x0138 }
        L_0x00fa:
            r6 = 0
            java.lang.Object r6 = r3.deserialze(r11, r13, r6)     // Catch:{ all -> 0x0138 }
            int r8 = r0.token     // Catch:{ all -> 0x0138 }
            r9 = 17
            if (r8 != r9) goto L_0x011f
            r0.nextToken()     // Catch:{ all -> 0x0138 }
            java.lang.Object r8 = r4.deserialze(r11, r14, r6)     // Catch:{ all -> 0x0138 }
            int r9 = r11.resolveStatus     // Catch:{ all -> 0x0138 }
            if (r9 != r7) goto L_0x0113
            r11.checkMapResolve(r12, r6)     // Catch:{ all -> 0x0138 }
        L_0x0113:
            r12.put(r6, r8)     // Catch:{ all -> 0x0138 }
            int r7 = r0.token     // Catch:{ all -> 0x0138 }
            if (r7 != r2) goto L_0x011d
            r0.nextToken()     // Catch:{ all -> 0x0138 }
        L_0x011d:
            goto L_0x0039
        L_0x011f:
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0138 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0138 }
            r7.<init>()     // Catch:{ all -> 0x0138 }
            java.lang.String r8 = "syntax error, expect :, actual "
            r7.append(r8)     // Catch:{ all -> 0x0138 }
            int r8 = r0.token     // Catch:{ all -> 0x0138 }
            r7.append(r8)     // Catch:{ all -> 0x0138 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0138 }
            r2.<init>(r7)     // Catch:{ all -> 0x0138 }
            throw r2     // Catch:{ all -> 0x0138 }
        L_0x0138:
            r2 = move-exception
            r11.setContext(r5)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.MapDeserializer.parseMap(com.alibaba.fastjson.parser.DefaultJSONParser, java.util.Map, java.lang.reflect.Type, java.lang.reflect.Type, java.lang.Object):java.lang.Object");
    }

    /* access modifiers changed from: protected */
    public Map<?, ?> createMap(Type type) {
        if (type == Properties.class) {
            return new Properties();
        }
        if (type == Hashtable.class) {
            return new Hashtable();
        }
        if (type == IdentityHashMap.class) {
            return new IdentityHashMap();
        }
        if (type == SortedMap.class || type == TreeMap.class) {
            return new TreeMap();
        }
        if (type == ConcurrentMap.class || type == ConcurrentHashMap.class) {
            return new ConcurrentHashMap();
        }
        if (type == Map.class || type == HashMap.class) {
            return new HashMap();
        }
        if (type == LinkedHashMap.class) {
            return new LinkedHashMap();
        }
        if (type == JSONObject.class) {
            return new JSONObject();
        }
        if (type instanceof ParameterizedType) {
            return createMap(((ParameterizedType) type).getRawType());
        }
        Class<?> clazz = (Class) type;
        if (!clazz.isInterface()) {
            try {
                return (Map) clazz.newInstance();
            } catch (Exception e) {
                throw new JSONException("unsupport type " + type, e);
            }
        } else {
            throw new JSONException("unsupport type " + type);
        }
    }
}

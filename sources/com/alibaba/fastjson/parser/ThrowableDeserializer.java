package com.alibaba.fastjson.parser;

public class ThrowableDeserializer extends JavaBeanDeserializer {
    public ThrowableDeserializer(ParserConfig mapping, Class<?> clazz) {
        super(mapping, clazz, clazz);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v19, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v25, resolved type: java.lang.Throwable} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <T> T deserialze(com.alibaba.fastjson.parser.DefaultJSONParser r18, java.lang.reflect.Type r19, java.lang.Object r20) {
        /*
            r17 = this;
            r1 = r18
            r2 = r19
            com.alibaba.fastjson.parser.JSONLexer r3 = r1.lexer
            int r0 = r3.token
            r4 = 0
            r5 = 8
            if (r0 != r5) goto L_0x0011
            r3.nextToken()
            return r4
        L_0x0011:
            int r0 = r1.resolveStatus
            java.lang.String r6 = "syntax error"
            r7 = 2
            r8 = 0
            if (r0 != r7) goto L_0x001c
            r1.resolveStatus = r8
            goto L_0x0022
        L_0x001c:
            int r0 = r3.token
            r9 = 12
            if (r0 != r9) goto L_0x019d
        L_0x0022:
            r0 = 0
            r9 = 0
            if (r2 == 0) goto L_0x0036
            boolean r10 = r2 instanceof java.lang.Class
            if (r10 == 0) goto L_0x0036
            r10 = r2
            java.lang.Class r10 = (java.lang.Class) r10
            java.lang.Class<java.lang.Throwable> r11 = java.lang.Throwable.class
            boolean r11 = r11.isAssignableFrom(r10)
            if (r11 == 0) goto L_0x0036
            r9 = r10
        L_0x0036:
            r10 = 0
            r11 = 0
            java.util.HashMap r12 = new java.util.HashMap
            r12.<init>()
        L_0x003d:
            com.alibaba.fastjson.parser.SymbolTable r13 = r1.symbolTable
            java.lang.String r13 = r3.scanSymbol(r13)
            r14 = 13
            r15 = 16
            if (r13 != 0) goto L_0x0065
            int r7 = r3.token
            if (r7 != r14) goto L_0x0055
            r3.nextToken(r15)
            r8 = r17
            r4 = r0
            goto L_0x00e9
        L_0x0055:
            int r7 = r3.token
            if (r7 != r15) goto L_0x0065
            int r7 = r3.features
            com.alibaba.fastjson.parser.Feature r8 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas
            int r8 = r8.mask
            r7 = r7 & r8
            if (r7 == 0) goto L_0x0065
            r7 = 2
            r8 = 0
            goto L_0x003d
        L_0x0065:
            r7 = 58
            r3.nextTokenWithChar(r7)
            java.lang.String r7 = "@type"
            boolean r7 = r7.equals(r13)
            r8 = 4
            if (r7 == 0) goto L_0x0091
            int r7 = r3.token
            if (r7 != r8) goto L_0x008b
            java.lang.String r7 = r3.stringVal()
            com.alibaba.fastjson.parser.ParserConfig r8 = r1.config
            java.lang.ClassLoader r8 = r8.defaultClassLoader
            java.lang.Class r7 = com.alibaba.fastjson.util.TypeUtils.loadClass(r7, r8)
            r3.nextToken(r15)
            r8 = r17
            r9 = r7
            goto L_0x00e1
        L_0x008b:
            com.alibaba.fastjson.JSONException r4 = new com.alibaba.fastjson.JSONException
            r4.<init>(r6)
            throw r4
        L_0x0091:
            java.lang.String r7 = "message"
            boolean r7 = r7.equals(r13)
            if (r7 == 0) goto L_0x00b4
            int r7 = r3.token
            if (r7 != r5) goto L_0x009f
            r7 = 0
            goto L_0x00a7
        L_0x009f:
            int r7 = r3.token
            if (r7 != r8) goto L_0x00ae
            java.lang.String r7 = r3.stringVal()
        L_0x00a7:
            r3.nextToken()
            r8 = r17
            r10 = r7
            goto L_0x00e1
        L_0x00ae:
            com.alibaba.fastjson.JSONException r4 = new com.alibaba.fastjson.JSONException
            r4.<init>(r6)
            throw r4
        L_0x00b4:
            java.lang.String r7 = "cause"
            boolean r8 = r7.equals(r13)
            if (r8 == 0) goto L_0x00c6
            r8 = r17
            java.lang.Object r7 = r8.deserialze(r1, r4, r7)
            r0 = r7
            java.lang.Throwable r0 = (java.lang.Throwable) r0
            goto L_0x00e1
        L_0x00c6:
            r8 = r17
            java.lang.String r7 = "stackTrace"
            boolean r7 = r7.equals(r13)
            if (r7 == 0) goto L_0x00da
            java.lang.Class<java.lang.StackTraceElement[]> r7 = java.lang.StackTraceElement[].class
            java.lang.Object r7 = r1.parseObject(r7)
            java.lang.StackTraceElement[] r7 = (java.lang.StackTraceElement[]) r7
            r11 = r7
            goto L_0x00e1
        L_0x00da:
            java.lang.Object r7 = r18.parse()
            r12.put(r13, r7)
        L_0x00e1:
            int r7 = r3.token
            if (r7 != r14) goto L_0x0193
            r3.nextToken(r15)
            r4 = r0
        L_0x00e9:
            r5 = 0
            if (r9 != 0) goto L_0x00f3
            java.lang.Exception r0 = new java.lang.Exception
            r0.<init>(r10, r4)
            goto L_0x0184
        L_0x00f3:
            r0 = 0
            r6 = 0
            r7 = 0
            java.lang.reflect.Constructor[] r13 = r9.getConstructors()     // Catch:{ Exception -> 0x018a }
            int r14 = r13.length     // Catch:{ Exception -> 0x018a }
            r15 = r0
            r0 = 0
        L_0x00fd:
            if (r0 >= r14) goto L_0x014b
            r16 = r13[r0]     // Catch:{ Exception -> 0x018a }
            java.lang.Class[] r1 = r16.getParameterTypes()     // Catch:{ Exception -> 0x018a }
            int r1 = r1.length     // Catch:{ Exception -> 0x018a }
            if (r1 != 0) goto L_0x010c
            r1 = r16
            r15 = r1
            goto L_0x0144
        L_0x010c:
            java.lang.Class[] r1 = r16.getParameterTypes()     // Catch:{ Exception -> 0x018a }
            int r1 = r1.length     // Catch:{ Exception -> 0x018a }
            r2 = 1
            if (r1 != r2) goto L_0x0123
            java.lang.Class[] r1 = r16.getParameterTypes()     // Catch:{ Exception -> 0x018a }
            r2 = 0
            r1 = r1[r2]     // Catch:{ Exception -> 0x018a }
            java.lang.Class<java.lang.String> r2 = java.lang.String.class
            if (r1 != r2) goto L_0x0123
            r1 = r16
            r6 = r1
            goto L_0x0144
        L_0x0123:
            java.lang.Class[] r1 = r16.getParameterTypes()     // Catch:{ Exception -> 0x018a }
            int r1 = r1.length     // Catch:{ Exception -> 0x018a }
            r2 = 2
            if (r1 != r2) goto L_0x0144
            java.lang.Class[] r1 = r16.getParameterTypes()     // Catch:{ Exception -> 0x018a }
            r2 = 0
            r1 = r1[r2]     // Catch:{ Exception -> 0x018a }
            java.lang.Class<java.lang.String> r2 = java.lang.String.class
            if (r1 != r2) goto L_0x0144
            java.lang.Class[] r1 = r16.getParameterTypes()     // Catch:{ Exception -> 0x018a }
            r2 = 1
            r1 = r1[r2]     // Catch:{ Exception -> 0x018a }
            java.lang.Class<java.lang.Throwable> r2 = java.lang.Throwable.class
            if (r1 != r2) goto L_0x0144
            r1 = r16
            r7 = r1
        L_0x0144:
            int r0 = r0 + 1
            r1 = r18
            r2 = r19
            goto L_0x00fd
        L_0x014b:
            if (r7 == 0) goto L_0x015e
            r1 = 2
            java.lang.Object[] r0 = new java.lang.Object[r1]     // Catch:{ Exception -> 0x018a }
            r1 = 0
            r0[r1] = r10     // Catch:{ Exception -> 0x018a }
            r1 = 1
            r0[r1] = r4     // Catch:{ Exception -> 0x018a }
            java.lang.Object r0 = r7.newInstance(r0)     // Catch:{ Exception -> 0x018a }
            java.lang.Throwable r0 = (java.lang.Throwable) r0     // Catch:{ Exception -> 0x018a }
            r5 = r0
            goto L_0x017a
        L_0x015e:
            if (r6 == 0) goto L_0x016e
            r0 = 1
            java.lang.Object[] r0 = new java.lang.Object[r0]     // Catch:{ Exception -> 0x018a }
            r1 = 0
            r0[r1] = r10     // Catch:{ Exception -> 0x018a }
            java.lang.Object r0 = r6.newInstance(r0)     // Catch:{ Exception -> 0x018a }
            java.lang.Throwable r0 = (java.lang.Throwable) r0     // Catch:{ Exception -> 0x018a }
            r5 = r0
            goto L_0x017a
        L_0x016e:
            if (r15 == 0) goto L_0x017a
            r2 = 0
            java.lang.Object[] r0 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x018a }
            java.lang.Object r0 = r15.newInstance(r0)     // Catch:{ Exception -> 0x018a }
            java.lang.Throwable r0 = (java.lang.Throwable) r0     // Catch:{ Exception -> 0x018a }
            r5 = r0
        L_0x017a:
            if (r5 != 0) goto L_0x0182
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x018a }
            r0.<init>(r10, r4)     // Catch:{ Exception -> 0x018a }
            goto L_0x0183
        L_0x0182:
            r0 = r5
        L_0x0183:
        L_0x0184:
            if (r11 == 0) goto L_0x0189
            r0.setStackTrace(r11)
        L_0x0189:
            return r0
        L_0x018a:
            r0 = move-exception
            com.alibaba.fastjson.JSONException r1 = new com.alibaba.fastjson.JSONException
            java.lang.String r2 = "create instance error"
            r1.<init>(r2, r0)
            throw r1
        L_0x0193:
            r1 = 2
            r2 = 0
            r1 = r18
            r2 = r19
            r7 = 2
            r8 = 0
            goto L_0x003d
        L_0x019d:
            r8 = r17
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException
            r0.<init>(r6)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.ThrowableDeserializer.deserialze(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.lang.Object):java.lang.Object");
    }
}

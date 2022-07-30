package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessable;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.parser.deserializer.ExtraTypeProvider;
import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import kotlin.text.Typography;

public class JavaBeanDeserializer implements ObjectDeserializer {
    public final JavaBeanInfo beanInfo;
    private final Class<?> clazz;
    private final FieldDeserializer[] fieldDeserializers;
    private final FieldDeserializer[] sortedFieldDeserializers;

    public JavaBeanDeserializer(ParserConfig config, Class<?> clazz2, Type type) {
        this(config, clazz2, type, JavaBeanInfo.build(clazz2, clazz2.getModifiers(), type, false, true, true, true, config.propertyNamingStrategy));
    }

    public JavaBeanDeserializer(ParserConfig config, Class<?> clazz2, Type type, JavaBeanInfo beanInfo2) {
        this.clazz = clazz2;
        this.beanInfo = beanInfo2;
        this.sortedFieldDeserializers = new FieldDeserializer[beanInfo2.sortedFields.length];
        int size = beanInfo2.sortedFields.length;
        for (int i = 0; i < size; i++) {
            this.sortedFieldDeserializers[i] = config.createFieldDeserializer(config, clazz2, beanInfo2.sortedFields[i]);
        }
        this.fieldDeserializers = new FieldDeserializer[beanInfo2.fields.length];
        int size2 = beanInfo2.fields.length;
        for (int i2 = 0; i2 < size2; i2++) {
            this.fieldDeserializers[i2] = getFieldDeserializer(beanInfo2.fields[i2].name);
        }
    }

    /* access modifiers changed from: protected */
    public Object createInstance(DefaultJSONParser parser, Type type) {
        Object object;
        if ((type instanceof Class) && this.clazz.isInterface()) {
            return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{(Class) type}, new JSONObject((parser.lexer.features & Feature.OrderedField.mask) != 0));
        } else if (this.beanInfo.defaultConstructor == null) {
            return null;
        } else {
            try {
                Constructor<?> constructor = this.beanInfo.defaultConstructor;
                if (this.beanInfo.defaultConstructorParameterSize == 0) {
                    object = constructor.newInstance(new Object[0]);
                } else {
                    object = constructor.newInstance(new Object[]{parser.contex.object});
                }
                if (!(parser == null || (parser.lexer.features & Feature.InitStringFieldAsEmpty.mask) == 0)) {
                    for (FieldInfo fieldInfo : this.beanInfo.fields) {
                        if (fieldInfo.fieldClass == String.class) {
                            fieldInfo.set(object, "");
                        }
                    }
                }
                return object;
            } catch (Exception e) {
                throw new JSONException("create instance error, class " + this.clazz.getName(), e);
            }
        }
    }

    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        return deserialze(parser, type, fieldName, (Object) null);
    }

    private <T> T deserialzeArrayMapping(DefaultJSONParser parser, Type type, Object fieldName, Object object) {
        char c;
        char seperator;
        char c2;
        char c3;
        char c4;
        char c5;
        char c6;
        char c7;
        char c8;
        char c9;
        char c10;
        char c11;
        String strVal;
        char c12;
        char c13;
        char c14;
        char c15;
        char c16;
        JavaBeanDeserializer javaBeanDeserializer = this;
        DefaultJSONParser defaultJSONParser = parser;
        JSONLexer lexer = defaultJSONParser.lexer;
        Object object2 = createInstance(parser, type);
        int size = javaBeanDeserializer.sortedFieldDeserializers.length;
        int i = 0;
        while (i < size) {
            char seperator2 = i == size + -1 ? ']' : ',';
            FieldDeserializer fieldDeser = javaBeanDeserializer.sortedFieldDeserializers[i];
            FieldInfo fieldInfo = fieldDeser.fieldInfo;
            Class<?> fieldClass = fieldInfo.fieldClass;
            try {
                if (fieldClass == Integer.TYPE) {
                    seperator = seperator2;
                    try {
                        int intValue = (int) lexer.scanLongValue();
                        if (fieldInfo.fieldAccess) {
                            fieldInfo.field.setInt(object2, intValue);
                        } else {
                            fieldDeser.setValue(object2, (Object) new Integer(intValue));
                        }
                        if (lexer.ch == ',') {
                            int index = lexer.bp + 1;
                            lexer.bp = index;
                            if (index >= lexer.len) {
                                c16 = JSONLexer.EOI;
                            } else {
                                c16 = lexer.text.charAt(index);
                            }
                            lexer.ch = c16;
                            lexer.token = 16;
                        } else if (lexer.ch == ']') {
                            int index2 = lexer.bp + 1;
                            lexer.bp = index2;
                            if (index2 >= lexer.len) {
                                c15 = JSONLexer.EOI;
                            } else {
                                c15 = lexer.text.charAt(index2);
                            }
                            lexer.ch = c15;
                            lexer.token = 15;
                        } else {
                            lexer.nextToken();
                        }
                        char c17 = seperator;
                    } catch (IllegalAccessException e) {
                        e = e;
                        char c18 = seperator;
                        throw new JSONException("set " + fieldInfo.name + "error", e);
                    }
                } else {
                    seperator = seperator2;
                    if (fieldClass == String.class) {
                        try {
                            if (lexer.ch == '\"') {
                                strVal = lexer.scanStringValue(Typography.quote);
                            } else {
                                if (lexer.ch == 'n') {
                                    if (lexer.text.startsWith("null", lexer.bp)) {
                                        lexer.bp += 4;
                                        int index3 = lexer.bp;
                                        if (lexer.bp >= lexer.len) {
                                            c14 = JSONLexer.EOI;
                                        } else {
                                            c14 = lexer.text.charAt(index3);
                                        }
                                        lexer.ch = c14;
                                        strVal = null;
                                    }
                                }
                                StringBuilder sb = new StringBuilder();
                                sb.append("not match string. feild : ");
                                sb.append(fieldName);
                                throw new JSONException(sb.toString());
                            }
                            if (fieldInfo.fieldAccess) {
                                fieldInfo.field.set(object2, strVal);
                            } else {
                                fieldDeser.setValue(object2, (Object) strVal);
                            }
                            if (lexer.ch == ',') {
                                int index4 = lexer.bp + 1;
                                lexer.bp = index4;
                                if (index4 >= lexer.len) {
                                    c13 = JSONLexer.EOI;
                                } else {
                                    c13 = lexer.text.charAt(index4);
                                }
                                lexer.ch = c13;
                                lexer.token = 16;
                            } else if (lexer.ch == ']') {
                                int index5 = lexer.bp + 1;
                                lexer.bp = index5;
                                if (index5 >= lexer.len) {
                                    c12 = JSONLexer.EOI;
                                } else {
                                    c12 = lexer.text.charAt(index5);
                                }
                                lexer.ch = c12;
                                lexer.token = 15;
                            } else {
                                lexer.nextToken();
                            }
                            char c19 = seperator;
                        } catch (IllegalAccessException e2) {
                            e = e2;
                            Object obj = fieldName;
                            char c182 = seperator;
                            throw new JSONException("set " + fieldInfo.name + "error", e);
                        }
                    } else {
                        Object obj2 = fieldName;
                        try {
                            if (fieldClass == Long.TYPE) {
                                long longValue = lexer.scanLongValue();
                                if (fieldInfo.fieldAccess) {
                                    fieldInfo.field.setLong(object2, longValue);
                                } else {
                                    fieldDeser.setValue(object2, (Object) new Long(longValue));
                                }
                                if (lexer.ch == ',') {
                                    int index6 = lexer.bp + 1;
                                    lexer.bp = index6;
                                    if (index6 >= lexer.len) {
                                        c11 = JSONLexer.EOI;
                                    } else {
                                        c11 = lexer.text.charAt(index6);
                                    }
                                    lexer.ch = c11;
                                    lexer.token = 16;
                                } else if (lexer.ch == ']') {
                                    int index7 = lexer.bp + 1;
                                    lexer.bp = index7;
                                    if (index7 >= lexer.len) {
                                        c10 = JSONLexer.EOI;
                                    } else {
                                        c10 = lexer.text.charAt(index7);
                                    }
                                    lexer.ch = c10;
                                    lexer.token = 15;
                                } else {
                                    lexer.nextToken();
                                }
                                char c20 = seperator;
                            } else if (fieldClass == Boolean.TYPE) {
                                boolean booleanValue = lexer.scanBoolean();
                                if (fieldInfo.fieldAccess) {
                                    fieldInfo.field.setBoolean(object2, booleanValue);
                                } else {
                                    fieldDeser.setValue(object2, (Object) Boolean.valueOf(booleanValue));
                                }
                                if (lexer.ch == ',') {
                                    int index8 = lexer.bp + 1;
                                    lexer.bp = index8;
                                    if (index8 >= lexer.len) {
                                        c9 = JSONLexer.EOI;
                                    } else {
                                        c9 = lexer.text.charAt(index8);
                                    }
                                    lexer.ch = c9;
                                    lexer.token = 16;
                                } else if (lexer.ch == ']') {
                                    int index9 = lexer.bp + 1;
                                    lexer.bp = index9;
                                    if (index9 >= lexer.len) {
                                        c8 = JSONLexer.EOI;
                                    } else {
                                        c8 = lexer.text.charAt(index9);
                                    }
                                    lexer.ch = c8;
                                    lexer.token = 15;
                                } else {
                                    lexer.nextToken();
                                }
                                char c21 = seperator;
                            } else {
                                Object value = null;
                                if (fieldClass.isEnum()) {
                                    char ch = lexer.ch;
                                    if (ch == '\"') {
                                        String enumName = lexer.scanSymbol(defaultJSONParser.symbolTable);
                                        if (enumName != null) {
                                            value = Enum.valueOf(fieldClass, enumName);
                                        }
                                    } else if (ch < '0' || ch > '9') {
                                        throw new JSONException("illegal enum." + lexer.info());
                                    } else {
                                        value = ((EnumDeserializer) ((DefaultFieldDeserializer) fieldDeser).getFieldValueDeserilizer(defaultJSONParser.config)).values[(int) lexer.scanLongValue()];
                                    }
                                    fieldDeser.setValue(object2, value);
                                    if (lexer.ch == ',') {
                                        int index10 = lexer.bp + 1;
                                        lexer.bp = index10;
                                        if (index10 >= lexer.len) {
                                            c7 = JSONLexer.EOI;
                                        } else {
                                            c7 = lexer.text.charAt(index10);
                                        }
                                        lexer.ch = c7;
                                        lexer.token = 16;
                                    } else if (lexer.ch == ']') {
                                        int index11 = lexer.bp + 1;
                                        lexer.bp = index11;
                                        if (index11 >= lexer.len) {
                                            c6 = JSONLexer.EOI;
                                        } else {
                                            c6 = lexer.text.charAt(index11);
                                        }
                                        lexer.ch = c6;
                                        lexer.token = 15;
                                    } else {
                                        lexer.nextToken();
                                    }
                                    char c22 = seperator;
                                } else {
                                    if (fieldClass == Date.class) {
                                        if (lexer.ch == '1') {
                                            fieldDeser.setValue(object2, (Object) new Date(lexer.scanLongValue()));
                                            if (lexer.ch == ',') {
                                                int index12 = lexer.bp + 1;
                                                lexer.bp = index12;
                                                if (index12 >= lexer.len) {
                                                    c5 = JSONLexer.EOI;
                                                } else {
                                                    c5 = lexer.text.charAt(index12);
                                                }
                                                lexer.ch = c5;
                                                lexer.token = 16;
                                            } else if (lexer.ch == ']') {
                                                int index13 = lexer.bp + 1;
                                                lexer.bp = index13;
                                                if (index13 >= lexer.len) {
                                                    c4 = JSONLexer.EOI;
                                                } else {
                                                    c4 = lexer.text.charAt(index13);
                                                }
                                                lexer.ch = c4;
                                                lexer.token = 15;
                                            } else {
                                                lexer.nextToken();
                                            }
                                            char c23 = seperator;
                                        }
                                    }
                                    if (lexer.ch == '[') {
                                        int index14 = lexer.bp + 1;
                                        lexer.bp = index14;
                                        if (index14 >= lexer.len) {
                                            c3 = JSONLexer.EOI;
                                        } else {
                                            c3 = lexer.text.charAt(index14);
                                        }
                                        lexer.ch = c3;
                                        lexer.token = 14;
                                    } else if (lexer.ch == '{') {
                                        int index15 = lexer.bp + 1;
                                        lexer.bp = index15;
                                        if (index15 >= lexer.len) {
                                            c2 = JSONLexer.EOI;
                                        } else {
                                            c2 = lexer.text.charAt(index15);
                                        }
                                        lexer.ch = c2;
                                        lexer.token = 12;
                                    } else {
                                        lexer.nextToken();
                                    }
                                    fieldDeser.parseField(defaultJSONParser, object2, fieldInfo.fieldType, (Map<String, Object>) null);
                                    char seperator3 = seperator;
                                    if (seperator3 == ']') {
                                        try {
                                            if (lexer.token != 15) {
                                                throw new JSONException("syntax error");
                                            }
                                        } catch (IllegalAccessException e3) {
                                            e = e3;
                                            throw new JSONException("set " + fieldInfo.name + "error", e);
                                        }
                                    } else if (seperator3 != ',') {
                                        continue;
                                    } else if (lexer.token != 16) {
                                        throw new JSONException("syntax error");
                                    }
                                }
                            }
                        } catch (IllegalAccessException e4) {
                            e = e4;
                            char c24 = seperator;
                            throw new JSONException("set " + fieldInfo.name + "error", e);
                        }
                    }
                }
                i++;
                javaBeanDeserializer = this;
            } catch (IllegalAccessException e5) {
                e = e5;
                char c25 = seperator2;
                throw new JSONException("set " + fieldInfo.name + "error", e);
            }
        }
        if (lexer.ch == ',') {
            int index16 = lexer.bp + 1;
            lexer.bp = index16;
            if (index16 >= lexer.len) {
                c = JSONLexer.EOI;
            } else {
                c = lexer.text.charAt(index16);
            }
            lexer.ch = c;
            lexer.token = 16;
        } else {
            lexer.nextToken();
        }
        return object2;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v7, resolved type: ?} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v30, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v13, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v14, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v73, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v75, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v76, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v77, resolved type: java.lang.Object} */
    /* JADX WARNING: type inference failed for: r2v1 */
    /* JADX WARNING: type inference failed for: r2v18, types: [com.alibaba.fastjson.parser.deserializer.FieldDeserializer] */
    /* JADX WARNING: type inference failed for: r2v21, types: [com.alibaba.fastjson.parser.deserializer.FieldDeserializer] */
    /* JADX WARNING: type inference failed for: r2v22 */
    /* JADX WARNING: type inference failed for: r2v24, types: [com.alibaba.fastjson.parser.deserializer.FieldDeserializer] */
    /* JADX WARNING: type inference failed for: r2v25 */
    /* JADX WARNING: type inference failed for: r2v27, types: [com.alibaba.fastjson.parser.deserializer.FieldDeserializer] */
    /* JADX WARNING: type inference failed for: r2v28 */
    /* JADX WARNING: type inference failed for: r2v29, types: [com.alibaba.fastjson.parser.deserializer.FieldDeserializer] */
    /* JADX WARNING: type inference failed for: r2v30 */
    /* JADX WARNING: type inference failed for: r2v31, types: [com.alibaba.fastjson.parser.deserializer.FieldDeserializer] */
    /* JADX WARNING: type inference failed for: r2v32 */
    /* JADX WARNING: type inference failed for: r2v33 */
    /* JADX WARNING: type inference failed for: r2v34 */
    /* JADX WARNING: type inference failed for: r2v35 */
    /* JADX WARNING: type inference failed for: r4v29 */
    /* JADX WARNING: type inference failed for: r4v67 */
    /* JADX WARNING: type inference failed for: r4v68 */
    /* JADX WARNING: type inference failed for: r4v69 */
    /* JADX WARNING: type inference failed for: r4v70 */
    /* JADX WARNING: type inference failed for: r4v71 */
    /* JADX WARNING: type inference failed for: r4v72 */
    /* JADX WARNING: type inference failed for: r4v74 */
    /* JADX WARNING: Code restructure failed: missing block: B:178:?, code lost:
        r11.nextToken(16);
        r2 = r0;
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:?, code lost:
        r11.nextTokenWithChar(':');
        r4 = r11.token;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x0359, code lost:
        if (r4 != 4) goto L_0x03e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x035b, code lost:
        r5 = r11.stringVal();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x0365, code lost:
        if ("@".equals(r5) == false) goto L_0x036d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x0367, code lost:
        r1 = r15.object;
        r27 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0373, code lost:
        if ("..".equals(r5) == false) goto L_0x038d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x0375, code lost:
        r6 = r15.parent;
        r27 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x037b, code lost:
        if (r6.object == null) goto L_0x0381;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x037d, code lost:
        r1 = r6.object;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x0381, code lost:
        r8.addResolveTask(new com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask(r6, r5));
        r8.resolveStatus = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x038d, code lost:
        r27 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x0395, code lost:
        if ("$".equals(r5) == false) goto L_0x03b4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x0397, code lost:
        r2 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x039a, code lost:
        if (r2.parent == null) goto L_0x03a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x039c, code lost:
        r2 = r2.parent;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x03a2, code lost:
        if (r2.object == null) goto L_0x03a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x03a4, code lost:
        r1 = r2.object;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x03a8, code lost:
        r8.addResolveTask(new com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask(r2, r5));
        r8.resolveStatus = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x03b4, code lost:
        r8.addResolveTask(new com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask(r15, r5));
        r8.resolveStatus = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x03bf, code lost:
        r11.nextToken(13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x03c7, code lost:
        if (r11.token != 13) goto L_0x03da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x03c9, code lost:
        r11.nextToken(16);
        r8.setContext(r15, r1, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x03d2, code lost:
        if (r3 == null) goto L_0x03d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x03d4, code lost:
        r3.object = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:0x03d6, code lost:
        r8.setContext(r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x03d9, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x03e1, code lost:
        throw new com.alibaba.fastjson.JSONException("illegal ref");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x03e2, code lost:
        r27 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x03fe, code lost:
        throw new com.alibaba.fastjson.JSONException("illegal ref, " + com.alibaba.fastjson.parser.JSONToken.name(r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x03ff, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x0400, code lost:
        r16 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x044b, code lost:
        r17 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:?, code lost:
        r2 = getSeeAlso(r8.config, r7.beanInfo, r5);
        r6 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x0456, code lost:
        if (r2 != null) goto L_0x048b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x0458, code lost:
        r6 = com.alibaba.fastjson.util.TypeUtils.loadClass(r5, r8.config.defaultClassLoader);
        r12 = com.alibaba.fastjson.util.TypeUtils.getClass(r38);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x0465, code lost:
        if (r12 == null) goto L_0x0480;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x0467, code lost:
        if (r6 == null) goto L_0x0474;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x0469, code lost:
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x046d, code lost:
        if (r12.isAssignableFrom(r6) == false) goto L_0x0474;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x046f, code lost:
        r40 = r2;
        r16 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x0474, code lost:
        r40 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x0478, code lost:
        r16 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x047f, code lost:
        throw new com.alibaba.fastjson.JSONException("type not match");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x0480, code lost:
        r40 = r2;
        r16 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x0484, code lost:
        r2 = r8.config.getDeserializer(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x048b, code lost:
        r40 = r2;
        r16 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x048f, code lost:
        r4 = r2.deserialze(r8, r6, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x0493, code lost:
        if (r3 == null) goto L_0x0497;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x0495, code lost:
        r3.object = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x0497, code lost:
        r8.setContext(r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x049a, code lost:
        return r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x07d4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x07d5, code lost:
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x07f3, code lost:
        throw new com.alibaba.fastjson.JSONException("create instance error, " + r7.beanInfo.creatorConstructor.toGenericString(), r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x0805, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x0806, code lost:
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0824, code lost:
        throw new com.alibaba.fastjson.JSONException("create factory method error, " + r7.beanInfo.factoryMethod.toString(), r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0869, code lost:
        throw new com.alibaba.fastjson.JSONException("syntax error, unexpect token " + com.alibaba.fastjson.parser.JSONToken.name(r11.token));
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0068 A[Catch:{ all -> 0x0049 }] */
    /* JADX WARNING: Removed duplicated region for block: B:485:0x0879  */
    /* JADX WARNING: Unknown variable types count: 6 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private <T> T deserialze(com.alibaba.fastjson.parser.DefaultJSONParser r37, java.lang.reflect.Type r38, java.lang.Object r39, java.lang.Object r40) {
        /*
            r36 = this;
            r7 = r36
            r8 = r37
            r9 = r38
            r10 = r39
            r1 = r40
            java.lang.Class<com.alibaba.fastjson.JSON> r0 = com.alibaba.fastjson.JSON.class
            if (r9 == r0) goto L_0x087f
            java.lang.Class<com.alibaba.fastjson.JSONObject> r0 = com.alibaba.fastjson.JSONObject.class
            if (r9 != r0) goto L_0x0014
            goto L_0x087f
        L_0x0014:
            com.alibaba.fastjson.parser.JSONLexer r11 = r8.lexer
            int r2 = r11.token
            r0 = 8
            r12 = 0
            r13 = 16
            if (r2 != r0) goto L_0x0023
            r11.nextToken(r13)
            return r12
        L_0x0023:
            boolean r14 = r11.disableCircularReferenceDetect
            com.alibaba.fastjson.parser.ParseContext r0 = r8.contex
            if (r1 == 0) goto L_0x002f
            if (r0 == 0) goto L_0x002f
            com.alibaba.fastjson.parser.ParseContext r0 = r0.parent
            r15 = r0
            goto L_0x0030
        L_0x002f:
            r15 = r0
        L_0x0030:
            r3 = 0
            r0 = 0
            r6 = 13
            if (r2 != r6) goto L_0x004e
            r11.nextToken(r13)     // Catch:{ all -> 0x0049 }
            if (r1 != 0) goto L_0x0040
            java.lang.Object r4 = r36.createInstance((com.alibaba.fastjson.parser.DefaultJSONParser) r37, (java.lang.reflect.Type) r38)     // Catch:{ all -> 0x0049 }
            r1 = r4
        L_0x0040:
            if (r3 == 0) goto L_0x0045
            r3.object = r1
        L_0x0045:
            r8.setContext(r15)
            return r1
        L_0x0049:
            r0 = move-exception
            r16 = r2
            goto L_0x0877
        L_0x004e:
            r4 = 14
            r5 = 0
            if (r2 != r4) goto L_0x0074
            com.alibaba.fastjson.parser.JavaBeanInfo r4 = r7.beanInfo     // Catch:{ all -> 0x0049 }
            boolean r4 = r4.supportBeanToArray     // Catch:{ all -> 0x0049 }
            if (r4 != 0) goto L_0x0065
            int r4 = r11.features     // Catch:{ all -> 0x0049 }
            com.alibaba.fastjson.parser.Feature r6 = com.alibaba.fastjson.parser.Feature.SupportArrayToBean     // Catch:{ all -> 0x0049 }
            int r6 = r6.mask     // Catch:{ all -> 0x0049 }
            r4 = r4 & r6
            if (r4 == 0) goto L_0x0063
            goto L_0x0065
        L_0x0063:
            r4 = 0
            goto L_0x0066
        L_0x0065:
            r4 = 1
        L_0x0066:
            if (r4 == 0) goto L_0x0074
            java.lang.Object r5 = r36.deserialzeArrayMapping(r37, r38, r39, r40)     // Catch:{ all -> 0x0049 }
            if (r3 == 0) goto L_0x0070
            r3.object = r1
        L_0x0070:
            r8.setContext(r15)
            return r5
        L_0x0074:
            r4 = 12
            r6 = 4
            if (r2 == r4) goto L_0x00c9
            if (r2 == r13) goto L_0x00c9
            boolean r4 = r11.isBlankInput()     // Catch:{ all -> 0x0049 }
            if (r4 == 0) goto L_0x008a
            if (r3 == 0) goto L_0x0086
            r3.object = r1
        L_0x0086:
            r8.setContext(r15)
            return r12
        L_0x008a:
            if (r2 != r6) goto L_0x00a2
            java.lang.String r4 = r11.stringVal()     // Catch:{ all -> 0x0049 }
            int r5 = r4.length()     // Catch:{ all -> 0x0049 }
            if (r5 != 0) goto L_0x00a2
            r11.nextToken()     // Catch:{ all -> 0x0049 }
            if (r3 == 0) goto L_0x009e
            r3.object = r1
        L_0x009e:
            r8.setContext(r15)
            return r12
        L_0x00a2:
            java.lang.StringBuffer r4 = new java.lang.StringBuffer     // Catch:{ all -> 0x0049 }
            r4.<init>()     // Catch:{ all -> 0x0049 }
            java.lang.String r5 = "syntax error, expect {, actual "
            r4.append(r5)     // Catch:{ all -> 0x0049 }
            java.lang.String r5 = r11.info()     // Catch:{ all -> 0x0049 }
            r4.append(r5)     // Catch:{ all -> 0x0049 }
            boolean r5 = r10 instanceof java.lang.String     // Catch:{ all -> 0x0049 }
            if (r5 == 0) goto L_0x00bf
            java.lang.String r5 = ", fieldName "
            r4.append(r5)     // Catch:{ all -> 0x0049 }
            r4.append(r10)     // Catch:{ all -> 0x0049 }
        L_0x00bf:
            com.alibaba.fastjson.JSONException r5 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0049 }
            java.lang.String r6 = r4.toString()     // Catch:{ all -> 0x0049 }
            r5.<init>(r6)     // Catch:{ all -> 0x0049 }
            throw r5     // Catch:{ all -> 0x0049 }
        L_0x00c9:
            int r4 = r8.resolveStatus     // Catch:{ all -> 0x0049 }
            r12 = 2
            if (r4 != r12) goto L_0x00d0
            r8.resolveStatus = r5     // Catch:{ all -> 0x0049 }
        L_0x00d0:
            r4 = 0
            com.alibaba.fastjson.parser.deserializer.FieldDeserializer[] r5 = r7.sortedFieldDeserializers     // Catch:{ all -> 0x0049 }
            int r5 = r5.length     // Catch:{ all -> 0x0049 }
            r12 = r5
            r5 = r4
        L_0x00d6:
            r4 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            if (r5 >= r12) goto L_0x00f8
            com.alibaba.fastjson.parser.deserializer.FieldDeserializer[] r6 = r7.sortedFieldDeserializers     // Catch:{ all -> 0x00f1 }
            r6 = r6[r5]     // Catch:{ all -> 0x00f1 }
            com.alibaba.fastjson.util.FieldInfo r13 = r6.fieldInfo     // Catch:{ all -> 0x00f1 }
            r40 = r2
            java.lang.Class<?> r2 = r13.fieldClass     // Catch:{ all -> 0x02f2 }
            r20 = r2
            r34 = r13
            r13 = r6
            r6 = r34
            goto L_0x0100
        L_0x00f1:
            r0 = move-exception
            r40 = r2
            r16 = r40
            goto L_0x0877
        L_0x00f8:
            r40 = r2
            r13 = r18
            r6 = r19
            r2 = r20
        L_0x0100:
            r18 = 0
            r19 = 0
            r20 = 0
            r22 = 0
            r23 = 0
            r25 = 0
            r26 = 0
            if (r13 == 0) goto L_0x02f7
            r28 = r4
            char[] r4 = r6.name_chars     // Catch:{ all -> 0x02f2 }
            r29 = r5
            java.lang.Class r5 = java.lang.Integer.TYPE     // Catch:{ all -> 0x02f2 }
            r30 = r12
            r12 = -2
            if (r2 == r5) goto L_0x02b0
            java.lang.Class<java.lang.Integer> r5 = java.lang.Integer.class
            if (r2 != r5) goto L_0x0123
            goto L_0x02b0
        L_0x0123:
            java.lang.Class r5 = java.lang.Long.TYPE     // Catch:{ all -> 0x02f2 }
            if (r2 == r5) goto L_0x027c
            java.lang.Class<java.lang.Long> r5 = java.lang.Long.class
            if (r2 != r5) goto L_0x012d
            goto L_0x027c
        L_0x012d:
            java.lang.Class<java.lang.String> r5 = java.lang.String.class
            if (r2 != r5) goto L_0x0165
            java.lang.String r5 = r11.scanFieldString(r4)     // Catch:{ all -> 0x02f2 }
            r20 = r5
            int r5 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            if (r5 <= 0) goto L_0x014f
            r18 = 1
            r19 = 1
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x014f:
            int r5 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            if (r5 != r12) goto L_0x0155
            goto L_0x02d3
        L_0x0155:
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x0165:
            java.lang.Class r5 = java.lang.Boolean.TYPE     // Catch:{ all -> 0x02f2 }
            if (r2 == r5) goto L_0x0243
            java.lang.Class<java.lang.Boolean> r5 = java.lang.Boolean.class
            if (r2 != r5) goto L_0x016f
            goto L_0x0243
        L_0x016f:
            java.lang.Class r5 = java.lang.Float.TYPE     // Catch:{ all -> 0x02f2 }
            if (r2 == r5) goto L_0x020e
            java.lang.Class<java.lang.Float> r5 = java.lang.Float.class
            if (r2 != r5) goto L_0x0179
            goto L_0x020e
        L_0x0179:
            java.lang.Class r5 = java.lang.Double.TYPE     // Catch:{ all -> 0x02f2 }
            if (r2 == r5) goto L_0x01d9
            java.lang.Class<java.lang.Double> r5 = java.lang.Double.class
            if (r2 != r5) goto L_0x0182
            goto L_0x01d9
        L_0x0182:
            boolean r5 = r6.isEnum     // Catch:{ all -> 0x02f2 }
            if (r5 == 0) goto L_0x01c1
            com.alibaba.fastjson.parser.ParserConfig r5 = r8.config     // Catch:{ all -> 0x02f2 }
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r5 = r5.getDeserializer(r2)     // Catch:{ all -> 0x02f2 }
            boolean r5 = r5 instanceof com.alibaba.fastjson.parser.EnumDeserializer     // Catch:{ all -> 0x02f2 }
            if (r5 == 0) goto L_0x01c1
            com.alibaba.fastjson.parser.SymbolTable r5 = r8.symbolTable     // Catch:{ all -> 0x02f2 }
            java.lang.String r5 = r11.scanFieldSymbol(r4, r5)     // Catch:{ all -> 0x02f2 }
            int r12 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            if (r12 <= 0) goto L_0x01a8
            r12 = 1
            r18 = 1
            java.lang.Enum r19 = java.lang.Enum.valueOf(r2, r5)     // Catch:{ all -> 0x02f2 }
            r20 = r19
            r19 = r18
            r18 = r12
            goto L_0x01b1
        L_0x01a8:
            int r12 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            r32 = r5
            r5 = -2
            if (r12 != r5) goto L_0x01b1
            goto L_0x02d3
        L_0x01b1:
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x01c1:
            boolean r5 = r11.matchField(r4)     // Catch:{ all -> 0x02f2 }
            if (r5 == 0) goto L_0x02d3
            r18 = 1
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x01d9:
            double r32 = r11.scanFieldDouble(r4)     // Catch:{ all -> 0x02f2 }
            r26 = r32
            int r5 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            if (r5 <= 0) goto L_0x01f7
            r18 = 1
            r19 = 1
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x01f7:
            int r5 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            r12 = -2
            if (r5 != r12) goto L_0x01fe
            goto L_0x02d3
        L_0x01fe:
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x020e:
            float r5 = r11.scanFieldFloat(r4)     // Catch:{ all -> 0x02f2 }
            r25 = r5
            int r5 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            if (r5 <= 0) goto L_0x022c
            r18 = 1
            r19 = 1
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x022c:
            int r5 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            r12 = -2
            if (r5 != r12) goto L_0x0233
            goto L_0x02d3
        L_0x0233:
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x0243:
            boolean r5 = r11.scanFieldBoolean(r4)     // Catch:{ all -> 0x02f2 }
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)     // Catch:{ all -> 0x02f2 }
            r20 = r5
            int r5 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            if (r5 <= 0) goto L_0x0265
            r18 = 1
            r19 = 1
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x0265:
            int r5 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            r12 = -2
            if (r5 != r12) goto L_0x026c
            goto L_0x02d3
        L_0x026c:
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x027c:
            long r32 = r11.scanFieldLong(r4)     // Catch:{ all -> 0x02f2 }
            r23 = r32
            int r5 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            if (r5 <= 0) goto L_0x029a
            r18 = 1
            r19 = 1
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x029a:
            int r5 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            r12 = -2
            if (r5 != r12) goto L_0x02a0
            goto L_0x02d3
        L_0x02a0:
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x02b0:
            int r5 = r11.scanFieldInt(r4)     // Catch:{ all -> 0x02f2 }
            r22 = r5
            int r5 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            if (r5 <= 0) goto L_0x02cd
            r18 = 1
            r19 = 1
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x02cd:
            int r5 = r11.matchStat     // Catch:{ all -> 0x02f2 }
            r12 = -2
            if (r5 != r12) goto L_0x02e3
        L_0x02d3:
            r16 = r40
            r24 = r0
            r27 = r1
            r22 = r29
            r0 = 13
            r1 = 0
            r2 = 16
            r4 = 1
            goto L_0x083c
        L_0x02e3:
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
            goto L_0x030b
        L_0x02f2:
            r0 = move-exception
            r16 = r40
            goto L_0x0877
        L_0x02f7:
            r28 = r4
            r29 = r5
            r30 = r12
            r12 = r20
            r4 = r25
            r20 = r6
            r5 = r26
            r34 = r23
            r24 = r22
            r22 = r34
        L_0x030b:
            if (r18 != 0) goto L_0x04b5
            r25 = r12
            com.alibaba.fastjson.parser.SymbolTable r12 = r8.symbolTable     // Catch:{ all -> 0x02f2 }
            java.lang.String r12 = r11.scanSymbol(r12)     // Catch:{ all -> 0x02f2 }
            if (r12 != 0) goto L_0x0346
            r26 = r4
            int r4 = r11.token     // Catch:{ all -> 0x02f2 }
            r31 = r5
            r5 = 13
            if (r4 != r5) goto L_0x0329
            r5 = 16
            r11.nextToken(r5)     // Catch:{ all -> 0x03ff }
            r2 = r0
            goto L_0x0790
        L_0x0329:
            r5 = 16
            if (r4 != r5) goto L_0x034c
            int r5 = r11.features     // Catch:{ all -> 0x03ff }
            com.alibaba.fastjson.parser.Feature r6 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas     // Catch:{ all -> 0x03ff }
            int r6 = r6.mask     // Catch:{ all -> 0x03ff }
            r5 = r5 & r6
            if (r5 == 0) goto L_0x034c
            r24 = r0
            r27 = r1
            r16 = r4
            r22 = r29
            r0 = 13
            r1 = 0
            r2 = 16
            r4 = 1
            goto L_0x083c
        L_0x0346:
            r26 = r4
            r31 = r5
            r4 = r40
        L_0x034c:
            java.lang.String r5 = "$ref"
            r6 = 58
            if (r5 != r12) goto L_0x0404
            r11.nextTokenWithChar(r6)     // Catch:{ all -> 0x03ff }
            int r5 = r11.token     // Catch:{ all -> 0x03ff }
            r4 = r5
            r5 = 4
            if (r4 != r5) goto L_0x03e2
            java.lang.String r5 = r11.stringVal()     // Catch:{ all -> 0x03ff }
            java.lang.String r6 = "@"
            boolean r6 = r6.equals(r5)     // Catch:{ all -> 0x03ff }
            if (r6 == 0) goto L_0x036d
            java.lang.Object r6 = r15.object     // Catch:{ all -> 0x03ff }
            r1 = r6
            r27 = r2
            goto L_0x03bf
        L_0x036d:
            java.lang.String r6 = ".."
            boolean r6 = r6.equals(r5)     // Catch:{ all -> 0x03ff }
            if (r6 == 0) goto L_0x038d
            com.alibaba.fastjson.parser.ParseContext r6 = r15.parent     // Catch:{ all -> 0x03ff }
            r27 = r2
            java.lang.Object r2 = r6.object     // Catch:{ all -> 0x03ff }
            if (r2 == 0) goto L_0x0381
            java.lang.Object r2 = r6.object     // Catch:{ all -> 0x03ff }
            r1 = r2
            goto L_0x038c
        L_0x0381:
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r2 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask     // Catch:{ all -> 0x03ff }
            r2.<init>(r6, r5)     // Catch:{ all -> 0x03ff }
            r8.addResolveTask(r2)     // Catch:{ all -> 0x03ff }
            r2 = 1
            r8.resolveStatus = r2     // Catch:{ all -> 0x03ff }
        L_0x038c:
            goto L_0x03bf
        L_0x038d:
            r27 = r2
            java.lang.String r2 = "$"
            boolean r2 = r2.equals(r5)     // Catch:{ all -> 0x03ff }
            if (r2 == 0) goto L_0x03b4
            r2 = r15
        L_0x0398:
            com.alibaba.fastjson.parser.ParseContext r6 = r2.parent     // Catch:{ all -> 0x03ff }
            if (r6 == 0) goto L_0x03a0
            com.alibaba.fastjson.parser.ParseContext r6 = r2.parent     // Catch:{ all -> 0x03ff }
            r2 = r6
            goto L_0x0398
        L_0x03a0:
            java.lang.Object r6 = r2.object     // Catch:{ all -> 0x03ff }
            if (r6 == 0) goto L_0x03a8
            java.lang.Object r6 = r2.object     // Catch:{ all -> 0x03ff }
            r1 = r6
            goto L_0x03b3
        L_0x03a8:
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r6 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask     // Catch:{ all -> 0x03ff }
            r6.<init>(r2, r5)     // Catch:{ all -> 0x03ff }
            r8.addResolveTask(r6)     // Catch:{ all -> 0x03ff }
            r6 = 1
            r8.resolveStatus = r6     // Catch:{ all -> 0x03ff }
        L_0x03b3:
            goto L_0x03bf
        L_0x03b4:
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r2 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask     // Catch:{ all -> 0x03ff }
            r2.<init>(r15, r5)     // Catch:{ all -> 0x03ff }
            r8.addResolveTask(r2)     // Catch:{ all -> 0x03ff }
            r2 = 1
            r8.resolveStatus = r2     // Catch:{ all -> 0x03ff }
        L_0x03bf:
            r2 = 13
            r11.nextToken(r2)     // Catch:{ all -> 0x03ff }
            int r5 = r11.token     // Catch:{ all -> 0x03ff }
            if (r5 != r2) goto L_0x03da
            r2 = 16
            r11.nextToken(r2)     // Catch:{ all -> 0x03ff }
            r8.setContext(r15, r1, r10)     // Catch:{ all -> 0x03ff }
            if (r3 == 0) goto L_0x03d6
            r3.object = r1
        L_0x03d6:
            r8.setContext(r15)
            return r1
        L_0x03da:
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x03ff }
            java.lang.String r5 = "illegal ref"
            r2.<init>(r5)     // Catch:{ all -> 0x03ff }
            throw r2     // Catch:{ all -> 0x03ff }
        L_0x03e2:
            r27 = r2
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x03ff }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ff }
            r5.<init>()     // Catch:{ all -> 0x03ff }
            java.lang.String r6 = "illegal ref, "
            r5.append(r6)     // Catch:{ all -> 0x03ff }
            java.lang.String r6 = com.alibaba.fastjson.parser.JSONToken.name(r4)     // Catch:{ all -> 0x03ff }
            r5.append(r6)     // Catch:{ all -> 0x03ff }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x03ff }
            r2.<init>(r5)     // Catch:{ all -> 0x03ff }
            throw r2     // Catch:{ all -> 0x03ff }
        L_0x03ff:
            r0 = move-exception
            r16 = r4
            goto L_0x0877
        L_0x0404:
            r27 = r2
            r2 = 1
            java.lang.String r5 = "@type"
            if (r5 != r12) goto L_0x04a7
            r11.nextTokenWithChar(r6)     // Catch:{ all -> 0x04b0 }
            int r5 = r11.token     // Catch:{ all -> 0x04b0 }
            r6 = 4
            if (r5 != r6) goto L_0x049b
            java.lang.String r5 = r11.stringVal()     // Catch:{ all -> 0x04b0 }
            r6 = 16
            r11.nextToken(r6)     // Catch:{ all -> 0x04b0 }
            boolean r6 = r9 instanceof java.lang.Class     // Catch:{ all -> 0x04b0 }
            if (r6 == 0) goto L_0x044b
            r6 = r9
            java.lang.Class r6 = (java.lang.Class) r6     // Catch:{ all -> 0x03ff }
            java.lang.String r6 = r6.getName()     // Catch:{ all -> 0x03ff }
            boolean r6 = r5.equals(r6)     // Catch:{ all -> 0x03ff }
            if (r6 == 0) goto L_0x044b
            int r6 = r11.token     // Catch:{ all -> 0x03ff }
            r17 = r12
            r12 = 13
            if (r6 != r12) goto L_0x043b
            r11.nextToken()     // Catch:{ all -> 0x03ff }
            r2 = r0
            goto L_0x0790
        L_0x043b:
            r24 = r0
            r27 = r1
            r16 = r4
            r22 = r29
            r0 = 13
            r1 = 0
            r2 = 16
            r4 = 1
            goto L_0x083c
        L_0x044b:
            r17 = r12
            com.alibaba.fastjson.parser.ParserConfig r2 = r8.config     // Catch:{ all -> 0x04b0 }
            com.alibaba.fastjson.parser.JavaBeanInfo r6 = r7.beanInfo     // Catch:{ all -> 0x04b0 }
            com.alibaba.fastjson.parser.JavaBeanDeserializer r2 = r7.getSeeAlso(r2, r6, r5)     // Catch:{ all -> 0x04b0 }
            r6 = 0
            if (r2 != 0) goto L_0x048b
            com.alibaba.fastjson.parser.ParserConfig r12 = r8.config     // Catch:{ all -> 0x04b0 }
            java.lang.ClassLoader r12 = r12.defaultClassLoader     // Catch:{ all -> 0x04b0 }
            java.lang.Class r12 = com.alibaba.fastjson.util.TypeUtils.loadClass(r5, r12)     // Catch:{ all -> 0x04b0 }
            r6 = r12
            java.lang.Class r12 = com.alibaba.fastjson.util.TypeUtils.getClass(r38)     // Catch:{ all -> 0x04b0 }
            if (r12 == 0) goto L_0x0480
            if (r6 == 0) goto L_0x0474
            boolean r16 = r12.isAssignableFrom(r6)     // Catch:{ all -> 0x03ff }
            if (r16 == 0) goto L_0x0474
            r40 = r2
            r16 = r4
            goto L_0x0484
        L_0x0474:
            r40 = r2
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04b0 }
            r16 = r4
            java.lang.String r4 = "type not match"
            r2.<init>(r4)     // Catch:{ all -> 0x04e7 }
            throw r2     // Catch:{ all -> 0x04e7 }
        L_0x0480:
            r40 = r2
            r16 = r4
        L_0x0484:
            com.alibaba.fastjson.parser.ParserConfig r2 = r8.config     // Catch:{ all -> 0x04e7 }
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r2 = r2.getDeserializer(r6)     // Catch:{ all -> 0x04e7 }
            goto L_0x048f
        L_0x048b:
            r40 = r2
            r16 = r4
        L_0x048f:
            java.lang.Object r4 = r2.deserialze(r8, r6, r10)     // Catch:{ all -> 0x04e7 }
            if (r3 == 0) goto L_0x0497
            r3.object = r1
        L_0x0497:
            r8.setContext(r15)
            return r4
        L_0x049b:
            r16 = r4
            r17 = r12
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04e7 }
            java.lang.String r4 = "syntax error"
            r2.<init>(r4)     // Catch:{ all -> 0x04e7 }
            throw r2     // Catch:{ all -> 0x04e7 }
        L_0x04a7:
            r16 = r4
            r17 = r12
            r12 = 13
            r28 = r17
            goto L_0x04c2
        L_0x04b0:
            r0 = move-exception
            r16 = r4
            goto L_0x0877
        L_0x04b5:
            r27 = r2
            r26 = r4
            r31 = r5
            r25 = r12
            r2 = 1
            r12 = 13
            r16 = r40
        L_0x04c2:
            if (r1 != 0) goto L_0x04ea
            if (r0 != 0) goto L_0x04ea
            java.lang.Object r4 = r36.createInstance((com.alibaba.fastjson.parser.DefaultJSONParser) r37, (java.lang.reflect.Type) r38)     // Catch:{ all -> 0x04e7 }
            r1 = r4
            if (r1 != 0) goto L_0x04d6
            java.util.HashMap r4 = new java.util.HashMap     // Catch:{ all -> 0x04e7 }
            com.alibaba.fastjson.parser.deserializer.FieldDeserializer[] r5 = r7.fieldDeserializers     // Catch:{ all -> 0x04e7 }
            int r5 = r5.length     // Catch:{ all -> 0x04e7 }
            r4.<init>(r5)     // Catch:{ all -> 0x04e7 }
            r0 = r4
        L_0x04d6:
            if (r14 != 0) goto L_0x04e2
            com.alibaba.fastjson.parser.ParseContext r4 = r8.setContext(r15, r1, r10)     // Catch:{ all -> 0x04e7 }
            r3 = r4
            r5 = r0
            r6 = r1
            r17 = r3
            goto L_0x04ee
        L_0x04e2:
            r5 = r0
            r6 = r1
            r17 = r3
            goto L_0x04ee
        L_0x04e7:
            r0 = move-exception
            goto L_0x0877
        L_0x04ea:
            r5 = r0
            r6 = r1
            r17 = r3
        L_0x04ee:
            if (r18 == 0) goto L_0x0732
            if (r19 != 0) goto L_0x0513
            r13.parseField(r8, r6, r9, r5)     // Catch:{ all -> 0x050d }
            r21 = r20
            r9 = r25
            r25 = r26
            r0 = 13
            r20 = r13
            r12 = r22
            r26 = r24
            r23 = r27
            r22 = r29
            r24 = r5
            r27 = r6
            goto L_0x0776
        L_0x050d:
            r0 = move-exception
            r1 = r6
            r3 = r17
            goto L_0x0877
        L_0x0513:
            if (r6 != 0) goto L_0x0597
            java.lang.Class r0 = java.lang.Integer.TYPE     // Catch:{ all -> 0x050d }
            r3 = r27
            if (r3 == r0) goto L_0x0578
            java.lang.Class<java.lang.Integer> r0 = java.lang.Integer.class
            if (r3 != r0) goto L_0x0527
            r40 = r13
            r4 = r26
            r12 = r31
            goto L_0x057e
        L_0x0527:
            java.lang.Class r0 = java.lang.Long.TYPE     // Catch:{ all -> 0x050d }
            if (r3 == r0) goto L_0x056d
            java.lang.Class<java.lang.Long> r0 = java.lang.Long.class
            if (r3 != r0) goto L_0x0536
            r40 = r13
            r4 = r26
            r12 = r31
            goto L_0x0573
        L_0x0536:
            java.lang.Class r0 = java.lang.Float.TYPE     // Catch:{ all -> 0x050d }
            if (r3 == r0) goto L_0x0561
            java.lang.Class<java.lang.Float> r0 = java.lang.Float.class
            if (r3 != r0) goto L_0x0543
            r40 = r13
            r12 = r31
            goto L_0x0565
        L_0x0543:
            java.lang.Class r0 = java.lang.Double.TYPE     // Catch:{ all -> 0x050d }
            if (r3 == r0) goto L_0x0555
            java.lang.Class<java.lang.Double> r0 = java.lang.Double.class
            if (r3 != r0) goto L_0x054c
            goto L_0x0555
        L_0x054c:
            r40 = r13
            r0 = r25
            r4 = r26
            r12 = r31
            goto L_0x0582
        L_0x0555:
            java.lang.Double r0 = new java.lang.Double     // Catch:{ all -> 0x050d }
            r40 = r13
            r12 = r31
            r0.<init>(r12)     // Catch:{ all -> 0x050d }
            r4 = r26
            goto L_0x0582
        L_0x0561:
            r40 = r13
            r12 = r31
        L_0x0565:
            java.lang.Float r0 = new java.lang.Float     // Catch:{ all -> 0x050d }
            r4 = r26
            r0.<init>(r4)     // Catch:{ all -> 0x050d }
            goto L_0x0582
        L_0x056d:
            r40 = r13
            r4 = r26
            r12 = r31
        L_0x0573:
            java.lang.Long r0 = java.lang.Long.valueOf(r22)     // Catch:{ all -> 0x050d }
            goto L_0x0582
        L_0x0578:
            r40 = r13
            r4 = r26
            r12 = r31
        L_0x057e:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r24)     // Catch:{ all -> 0x050d }
        L_0x0582:
            r1 = r20
            java.lang.String r2 = r1.name     // Catch:{ all -> 0x050d }
            r5.put(r2, r0)     // Catch:{ all -> 0x050d }
            r2 = r40
            r26 = r4
            r31 = r12
            r12 = r22
            r23 = r3
            r22 = r5
            goto L_0x0715
        L_0x0597:
            r40 = r13
            r1 = r20
            r4 = r26
            r3 = r27
            r12 = r31
            if (r25 != 0) goto L_0x0703
            java.lang.Class r0 = java.lang.Integer.TYPE     // Catch:{ IllegalAccessException -> 0x06d7 }
            if (r3 == r0) goto L_0x06a5
            java.lang.Class<java.lang.Integer> r0 = java.lang.Integer.class
            if (r3 != r0) goto L_0x05b5
            r2 = r40
            r31 = r12
            r12 = r22
            r9 = r25
            goto L_0x06ad
        L_0x05b5:
            java.lang.Class r0 = java.lang.Long.TYPE     // Catch:{ IllegalAccessException -> 0x0697 }
            if (r3 == r0) goto L_0x065e
            java.lang.Class<java.lang.Long> r0 = java.lang.Long.class
            if (r3 != r0) goto L_0x05c3
            r2 = r40
            r9 = r25
            goto L_0x0662
        L_0x05c3:
            java.lang.Class r0 = java.lang.Float.TYPE     // Catch:{ IllegalAccessException -> 0x064f }
            if (r3 == r0) goto L_0x0619
            java.lang.Class<java.lang.Float> r0 = java.lang.Float.class
            if (r3 != r0) goto L_0x05d0
            r2 = r40
            r9 = r25
            goto L_0x061d
        L_0x05d0:
            java.lang.Class r0 = java.lang.Double.TYPE     // Catch:{ IllegalAccessException -> 0x064f }
            if (r3 == r0) goto L_0x05ee
            java.lang.Class<java.lang.Double> r0 = java.lang.Double.class
            if (r3 != r0) goto L_0x05dd
            r2 = r40
            r9 = r25
            goto L_0x05f2
        L_0x05dd:
            r2 = r40
            r9 = r25
            r2.setValue((java.lang.Object) r6, (java.lang.Object) r9)     // Catch:{ IllegalAccessException -> 0x0644 }
            r31 = r12
            r12 = r22
            r22 = r5
            r5 = r24
            goto L_0x06c8
        L_0x05ee:
            r2 = r40
            r9 = r25
        L_0x05f2:
            boolean r0 = r1.fieldAccess     // Catch:{ IllegalAccessException -> 0x0644 }
            if (r0 == 0) goto L_0x0607
            java.lang.Class r0 = java.lang.Double.TYPE     // Catch:{ IllegalAccessException -> 0x0644 }
            if (r3 != r0) goto L_0x0607
            r2.setValue((java.lang.Object) r6, (double) r12)     // Catch:{ IllegalAccessException -> 0x0644 }
            r31 = r12
            r12 = r22
            r22 = r5
            r5 = r24
            goto L_0x06c8
        L_0x0607:
            java.lang.Double r0 = new java.lang.Double     // Catch:{ IllegalAccessException -> 0x0644 }
            r0.<init>(r12)     // Catch:{ IllegalAccessException -> 0x0644 }
            r2.setValue((java.lang.Object) r6, (java.lang.Object) r0)     // Catch:{ IllegalAccessException -> 0x0644 }
            r31 = r12
            r12 = r22
            r22 = r5
            r5 = r24
            goto L_0x06c8
        L_0x0619:
            r2 = r40
            r9 = r25
        L_0x061d:
            boolean r0 = r1.fieldAccess     // Catch:{ IllegalAccessException -> 0x0644 }
            if (r0 == 0) goto L_0x0632
            java.lang.Class r0 = java.lang.Float.TYPE     // Catch:{ IllegalAccessException -> 0x0644 }
            if (r3 != r0) goto L_0x0632
            r2.setValue((java.lang.Object) r6, (float) r4)     // Catch:{ IllegalAccessException -> 0x0644 }
            r31 = r12
            r12 = r22
            r22 = r5
            r5 = r24
            goto L_0x06c8
        L_0x0632:
            java.lang.Float r0 = new java.lang.Float     // Catch:{ IllegalAccessException -> 0x0644 }
            r0.<init>(r4)     // Catch:{ IllegalAccessException -> 0x0644 }
            r2.setValue((java.lang.Object) r6, (java.lang.Object) r0)     // Catch:{ IllegalAccessException -> 0x0644 }
            r31 = r12
            r12 = r22
            r22 = r5
            r5 = r24
            goto L_0x06c8
        L_0x0644:
            r0 = move-exception
            r31 = r12
            r12 = r22
            r22 = r5
            r5 = r24
            goto L_0x06e4
        L_0x064f:
            r0 = move-exception
            r2 = r40
            r9 = r25
            r31 = r12
            r12 = r22
            r22 = r5
            r5 = r24
            goto L_0x06e4
        L_0x065e:
            r2 = r40
            r9 = r25
        L_0x0662:
            boolean r0 = r1.fieldAccess     // Catch:{ IllegalAccessException -> 0x068d }
            if (r0 == 0) goto L_0x0676
            java.lang.Class r0 = java.lang.Long.TYPE     // Catch:{ IllegalAccessException -> 0x068d }
            if (r3 != r0) goto L_0x0676
            r31 = r12
            r12 = r22
            r2.setValue((java.lang.Object) r6, (long) r12)     // Catch:{ IllegalAccessException -> 0x0686 }
            r22 = r5
            r5 = r24
            goto L_0x06c8
        L_0x0676:
            r31 = r12
            r12 = r22
            java.lang.Long r0 = java.lang.Long.valueOf(r12)     // Catch:{ IllegalAccessException -> 0x0686 }
            r2.setValue((java.lang.Object) r6, (java.lang.Object) r0)     // Catch:{ IllegalAccessException -> 0x0686 }
            r22 = r5
            r5 = r24
            goto L_0x06c8
        L_0x0686:
            r0 = move-exception
            r22 = r5
            r5 = r24
            goto L_0x06e4
        L_0x068d:
            r0 = move-exception
            r31 = r12
            r12 = r22
            r22 = r5
            r5 = r24
            goto L_0x06e4
        L_0x0697:
            r0 = move-exception
            r2 = r40
            r31 = r12
            r12 = r22
            r9 = r25
            r22 = r5
            r5 = r24
            goto L_0x06e4
        L_0x06a5:
            r2 = r40
            r31 = r12
            r12 = r22
            r9 = r25
        L_0x06ad:
            boolean r0 = r1.fieldAccess     // Catch:{ IllegalAccessException -> 0x06d1 }
            if (r0 == 0) goto L_0x06bd
            java.lang.Class r0 = java.lang.Integer.TYPE     // Catch:{ IllegalAccessException -> 0x06d1 }
            if (r3 != r0) goto L_0x06bd
            r22 = r5
            r5 = r24
            r2.setValue((java.lang.Object) r6, (int) r5)     // Catch:{ IllegalAccessException -> 0x06cf }
            goto L_0x06c8
        L_0x06bd:
            r22 = r5
            r5 = r24
            java.lang.Integer r0 = java.lang.Integer.valueOf(r5)     // Catch:{ IllegalAccessException -> 0x06cf }
            r2.setValue((java.lang.Object) r6, (java.lang.Object) r0)     // Catch:{ IllegalAccessException -> 0x06cf }
        L_0x06c8:
            r23 = r3
            r26 = r4
            r24 = r5
            goto L_0x0714
        L_0x06cf:
            r0 = move-exception
            goto L_0x06e4
        L_0x06d1:
            r0 = move-exception
            r22 = r5
            r5 = r24
            goto L_0x06e4
        L_0x06d7:
            r0 = move-exception
            r2 = r40
            r31 = r12
            r12 = r22
            r9 = r25
            r22 = r5
            r5 = r24
        L_0x06e4:
            r23 = r3
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x050d }
            r26 = r4
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x050d }
            r4.<init>()     // Catch:{ all -> 0x050d }
            r24 = r5
            java.lang.String r5 = "set property error, "
            r4.append(r5)     // Catch:{ all -> 0x050d }
            java.lang.String r5 = r1.name     // Catch:{ all -> 0x050d }
            r4.append(r5)     // Catch:{ all -> 0x050d }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x050d }
            r3.<init>(r4, r0)     // Catch:{ all -> 0x050d }
            throw r3     // Catch:{ all -> 0x050d }
        L_0x0703:
            r2 = r40
            r26 = r4
            r31 = r12
            r12 = r22
            r9 = r25
            r23 = r3
            r22 = r5
            r2.setValue((java.lang.Object) r6, (java.lang.Object) r9)     // Catch:{ all -> 0x050d }
        L_0x0714:
            r0 = r9
        L_0x0715:
            int r3 = r11.matchStat     // Catch:{ all -> 0x050d }
            r5 = 4
            if (r3 != r5) goto L_0x0720
            r27 = r6
            r24 = r22
            goto L_0x0788
        L_0x0720:
            r9 = r0
            r21 = r1
            r20 = r2
            r27 = r6
            r25 = r26
            r0 = 13
            r26 = r24
            r24 = r22
            r22 = r29
            goto L_0x0776
        L_0x0732:
            r2 = r13
            r1 = r20
            r12 = r22
            r9 = r25
            r23 = r27
            r22 = r5
            r5 = 4
            r21 = r1
            r1 = r36
            r20 = r2
            r0 = 1
            r2 = r37
            r3 = r28
            r25 = r26
            r4 = r6
            r26 = r24
            r24 = r22
            r22 = r29
            r29 = 4
            r5 = r38
            r27 = r6
            r0 = 13
            r6 = r24
            boolean r1 = r1.parseField(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x0872 }
            if (r1 != 0) goto L_0x0770
            int r2 = r11.token     // Catch:{ all -> 0x0872 }
            if (r2 != r0) goto L_0x076a
            r11.nextToken()     // Catch:{ all -> 0x0872 }
            goto L_0x0788
        L_0x076a:
            r1 = 0
            r2 = 16
            r4 = 1
            goto L_0x083a
        L_0x0770:
            int r2 = r11.token     // Catch:{ all -> 0x0872 }
            r3 = 17
            if (r2 == r3) goto L_0x086a
        L_0x0776:
            int r1 = r11.token     // Catch:{ all -> 0x0872 }
            r2 = 16
            if (r1 != r2) goto L_0x0780
            r1 = 0
            r4 = 1
            goto L_0x083a
        L_0x0780:
            int r1 = r11.token     // Catch:{ all -> 0x0872 }
            if (r1 != r0) goto L_0x082e
            r11.nextToken(r2)     // Catch:{ all -> 0x0872 }
        L_0x0788:
            r4 = r16
            r3 = r17
            r2 = r24
            r1 = r27
        L_0x0790:
            if (r1 != 0) goto L_0x0825
            if (r2 != 0) goto L_0x07a9
            java.lang.Object r0 = r36.createInstance((com.alibaba.fastjson.parser.DefaultJSONParser) r37, (java.lang.reflect.Type) r38)     // Catch:{ all -> 0x03ff }
            r1 = r0
            if (r3 != 0) goto L_0x07a0
            com.alibaba.fastjson.parser.ParseContext r0 = r8.setContext(r15, r1, r10)     // Catch:{ all -> 0x03ff }
            r3 = r0
        L_0x07a0:
            if (r3 == 0) goto L_0x07a5
            r3.object = r1
        L_0x07a5:
            r8.setContext(r15)
            return r1
        L_0x07a9:
            com.alibaba.fastjson.parser.deserializer.FieldDeserializer[] r0 = r7.fieldDeserializers     // Catch:{ all -> 0x03ff }
            int r0 = r0.length     // Catch:{ all -> 0x03ff }
            r5 = r0
            java.lang.Object[] r0 = new java.lang.Object[r5]     // Catch:{ all -> 0x03ff }
            r6 = r0
            r0 = 0
        L_0x07b1:
            if (r0 >= r5) goto L_0x07c4
            com.alibaba.fastjson.parser.deserializer.FieldDeserializer[] r9 = r7.fieldDeserializers     // Catch:{ all -> 0x03ff }
            r9 = r9[r0]     // Catch:{ all -> 0x03ff }
            com.alibaba.fastjson.util.FieldInfo r9 = r9.fieldInfo     // Catch:{ all -> 0x03ff }
            java.lang.String r12 = r9.name     // Catch:{ all -> 0x03ff }
            java.lang.Object r12 = r2.get(r12)     // Catch:{ all -> 0x03ff }
            r6[r0] = r12     // Catch:{ all -> 0x03ff }
            int r0 = r0 + 1
            goto L_0x07b1
        L_0x07c4:
            com.alibaba.fastjson.parser.JavaBeanInfo r0 = r7.beanInfo     // Catch:{ all -> 0x03ff }
            java.lang.reflect.Constructor<?> r0 = r0.creatorConstructor     // Catch:{ all -> 0x03ff }
            if (r0 == 0) goto L_0x07f4
            com.alibaba.fastjson.parser.JavaBeanInfo r0 = r7.beanInfo     // Catch:{ Exception -> 0x07d4 }
            java.lang.reflect.Constructor<?> r0 = r0.creatorConstructor     // Catch:{ Exception -> 0x07d4 }
            java.lang.Object r0 = r0.newInstance(r6)     // Catch:{ Exception -> 0x07d4 }
            r1 = r0
            goto L_0x0825
        L_0x07d4:
            r0 = move-exception
            com.alibaba.fastjson.JSONException r9 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x03ff }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ff }
            r12.<init>()     // Catch:{ all -> 0x03ff }
            java.lang.String r13 = "create instance error, "
            r12.append(r13)     // Catch:{ all -> 0x03ff }
            com.alibaba.fastjson.parser.JavaBeanInfo r13 = r7.beanInfo     // Catch:{ all -> 0x03ff }
            java.lang.reflect.Constructor<?> r13 = r13.creatorConstructor     // Catch:{ all -> 0x03ff }
            java.lang.String r13 = r13.toGenericString()     // Catch:{ all -> 0x03ff }
            r12.append(r13)     // Catch:{ all -> 0x03ff }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x03ff }
            r9.<init>(r12, r0)     // Catch:{ all -> 0x03ff }
            throw r9     // Catch:{ all -> 0x03ff }
        L_0x07f4:
            com.alibaba.fastjson.parser.JavaBeanInfo r0 = r7.beanInfo     // Catch:{ all -> 0x03ff }
            java.lang.reflect.Method r0 = r0.factoryMethod     // Catch:{ all -> 0x03ff }
            if (r0 == 0) goto L_0x0825
            com.alibaba.fastjson.parser.JavaBeanInfo r0 = r7.beanInfo     // Catch:{ Exception -> 0x0805 }
            java.lang.reflect.Method r0 = r0.factoryMethod     // Catch:{ Exception -> 0x0805 }
            r9 = 0
            java.lang.Object r0 = r0.invoke(r9, r6)     // Catch:{ Exception -> 0x0805 }
            r1 = r0
            goto L_0x0825
        L_0x0805:
            r0 = move-exception
            com.alibaba.fastjson.JSONException r9 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x03ff }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ff }
            r12.<init>()     // Catch:{ all -> 0x03ff }
            java.lang.String r13 = "create factory method error, "
            r12.append(r13)     // Catch:{ all -> 0x03ff }
            com.alibaba.fastjson.parser.JavaBeanInfo r13 = r7.beanInfo     // Catch:{ all -> 0x03ff }
            java.lang.reflect.Method r13 = r13.factoryMethod     // Catch:{ all -> 0x03ff }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x03ff }
            r12.append(r13)     // Catch:{ all -> 0x03ff }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x03ff }
            r9.<init>(r12, r0)     // Catch:{ all -> 0x03ff }
            throw r9     // Catch:{ all -> 0x03ff }
        L_0x0825:
            if (r3 == 0) goto L_0x082a
            r3.object = r1
        L_0x082a:
            r8.setContext(r15)
            return r1
        L_0x082e:
            r1 = 0
            int r3 = r11.token     // Catch:{ all -> 0x0872 }
            r4 = 18
            if (r3 == r4) goto L_0x084d
            int r3 = r11.token     // Catch:{ all -> 0x0872 }
            r4 = 1
            if (r3 == r4) goto L_0x084d
        L_0x083a:
            r3 = r17
        L_0x083c:
            int r5 = r22 + 1
            r9 = r38
            r2 = r16
            r0 = r24
            r1 = r27
            r12 = r30
            r6 = 4
            r13 = 16
            goto L_0x00d6
        L_0x084d:
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0872 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0872 }
            r1.<init>()     // Catch:{ all -> 0x0872 }
            java.lang.String r2 = "syntax error, unexpect token "
            r1.append(r2)     // Catch:{ all -> 0x0872 }
            int r2 = r11.token     // Catch:{ all -> 0x0872 }
            java.lang.String r2 = com.alibaba.fastjson.parser.JSONToken.name(r2)     // Catch:{ all -> 0x0872 }
            r1.append(r2)     // Catch:{ all -> 0x0872 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0872 }
            r0.<init>(r1)     // Catch:{ all -> 0x0872 }
            throw r0     // Catch:{ all -> 0x0872 }
        L_0x086a:
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0872 }
            java.lang.String r2 = "syntax error, unexpect token ':'"
            r0.<init>(r2)     // Catch:{ all -> 0x0872 }
            throw r0     // Catch:{ all -> 0x0872 }
        L_0x0872:
            r0 = move-exception
            r3 = r17
            r1 = r27
        L_0x0877:
            if (r3 == 0) goto L_0x087b
            r3.object = r1
        L_0x087b:
            r8.setContext(r15)
            throw r0
        L_0x087f:
            java.lang.Object r0 = r37.parse()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.JavaBeanDeserializer.deserialze(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.lang.Object, java.lang.Object):java.lang.Object");
    }

    /* access modifiers changed from: protected */
    public FieldDeserializer getFieldDeserializer(String key) {
        if (key == null) {
            return null;
        }
        if (this.beanInfo.ordered) {
            int i = 0;
            while (true) {
                FieldDeserializer[] fieldDeserializerArr = this.sortedFieldDeserializers;
                if (i >= fieldDeserializerArr.length) {
                    return null;
                }
                if (fieldDeserializerArr[i].fieldInfo.name.equalsIgnoreCase(key)) {
                    return this.sortedFieldDeserializers[i];
                }
                i++;
            }
        } else {
            int low = 0;
            int high = this.sortedFieldDeserializers.length - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                int cmp = this.sortedFieldDeserializers[mid].fieldInfo.name.compareTo(key);
                if (cmp < 0) {
                    low = mid + 1;
                } else if (cmp <= 0) {
                    return this.sortedFieldDeserializers[mid];
                } else {
                    high = mid - 1;
                }
            }
            return null;
        }
    }

    private boolean parseField(DefaultJSONParser parser, String key, Object object, Type objectType, Map<String, Object> fieldValues) {
        DefaultJSONParser defaultJSONParser = parser;
        String str = key;
        Object obj = object;
        JSONLexer lexer = defaultJSONParser.lexer;
        FieldDeserializer fieldDeserializer = getFieldDeserializer(str);
        if (fieldDeserializer == null) {
            boolean startsWithIs = str.startsWith("is");
            FieldDeserializer[] fieldDeserializerArr = this.sortedFieldDeserializers;
            int length = fieldDeserializerArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                FieldDeserializer fieldDeser = fieldDeserializerArr[i];
                FieldInfo fieldInfo = fieldDeser.fieldInfo;
                Class<?> fieldClass = fieldInfo.fieldClass;
                String fieldName = fieldInfo.name;
                if (!fieldName.equalsIgnoreCase(str)) {
                    if (startsWithIs && ((fieldClass == Boolean.TYPE || fieldClass == Boolean.class) && fieldName.equalsIgnoreCase(str.substring(2)))) {
                        fieldDeserializer = fieldDeser;
                        break;
                    }
                    i++;
                } else {
                    fieldDeserializer = fieldDeser;
                    break;
                }
            }
        }
        if (fieldDeserializer == null) {
            parseExtra(defaultJSONParser, obj, str);
            return false;
        }
        lexer.nextTokenWithChar(':');
        fieldDeserializer.parseField(defaultJSONParser, obj, objectType, fieldValues);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void parseExtra(DefaultJSONParser parser, Object object, String key) {
        Object value;
        JSONLexer lexer = parser.lexer;
        if ((parser.lexer.features & Feature.IgnoreNotMatch.mask) != 0) {
            lexer.nextTokenWithChar(':');
            Type type = null;
            List<ExtraTypeProvider> extraTypeProviders = parser.extraTypeProviders;
            if (extraTypeProviders != null) {
                for (ExtraTypeProvider extraProvider : extraTypeProviders) {
                    type = extraProvider.getExtraType(object, key);
                }
            }
            if (type == null) {
                value = parser.parse();
            } else {
                value = parser.parseObject(type);
            }
            if (object instanceof ExtraProcessable) {
                ((ExtraProcessable) object).processExtra(key, value);
                return;
            }
            List<ExtraProcessor> extraProcessors = parser.extraProcessors;
            if (extraProcessors != null) {
                for (ExtraProcessor process : extraProcessors) {
                    process.processExtra(object, key, value);
                }
                return;
            }
            return;
        }
        throw new JSONException("setter not found, class " + this.clazz.getName() + ", property " + key);
    }

    public Object createInstance(Map<String, Object> map, ParserConfig config) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (this.beanInfo.creatorConstructor == null) {
            Object object = createInstance((DefaultJSONParser) null, (Type) this.clazz);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                FieldDeserializer fieldDeser = getFieldDeserializer(entry.getKey());
                if (fieldDeser != null) {
                    Object value = entry.getValue();
                    Method method = fieldDeser.fieldInfo.method;
                    if (method != null) {
                        method.invoke(object, new Object[]{TypeUtils.cast(value, method.getGenericParameterTypes()[0], config)});
                    } else {
                        fieldDeser.fieldInfo.field.set(object, TypeUtils.cast(value, fieldDeser.fieldInfo.fieldType, config));
                    }
                }
            }
            return object;
        }
        FieldInfo[] fieldInfoList = this.beanInfo.fields;
        int size = fieldInfoList.length;
        Object[] params = new Object[size];
        for (int i = 0; i < size; i++) {
            params[i] = map.get(fieldInfoList[i].name);
        }
        if (this.beanInfo.creatorConstructor == null) {
            return null;
        }
        try {
            return this.beanInfo.creatorConstructor.newInstance(params);
        } catch (Exception e) {
            throw new JSONException("create instance error, " + this.beanInfo.creatorConstructor.toGenericString(), e);
        }
    }

    /* access modifiers changed from: protected */
    public JavaBeanDeserializer getSeeAlso(ParserConfig config, JavaBeanInfo beanInfo2, String typeName) {
        if (beanInfo2.jsonType == null) {
            return null;
        }
        for (Class<?> seeAlsoClass : beanInfo2.jsonType.seeAlso()) {
            ObjectDeserializer seeAlsoDeser = config.getDeserializer(seeAlsoClass);
            if (seeAlsoDeser instanceof JavaBeanDeserializer) {
                JavaBeanDeserializer seeAlsoJavaBeanDeser = (JavaBeanDeserializer) seeAlsoDeser;
                JavaBeanInfo subBeanInfo = seeAlsoJavaBeanDeser.beanInfo;
                if (subBeanInfo.typeName.equals(typeName)) {
                    return seeAlsoJavaBeanDeser;
                }
                JavaBeanDeserializer subSeeAlso = getSeeAlso(config, subBeanInfo, typeName);
                if (subSeeAlso != null) {
                    return subSeeAlso;
                }
            }
        }
        return null;
    }
}

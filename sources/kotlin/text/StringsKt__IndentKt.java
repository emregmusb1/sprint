package kotlin.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u001e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u000b\u001a!\u0010\u0000\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0002H\u0002¢\u0006\u0002\b\u0004\u001a\u0011\u0010\u0005\u001a\u00020\u0006*\u00020\u0002H\u0002¢\u0006\u0002\b\u0007\u001a\u0014\u0010\b\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u001aJ\u0010\t\u001a\u00020\u0002*\b\u0012\u0004\u0012\u00020\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00062\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u00012\u0014\u0010\r\u001a\u0010\u0012\u0004\u0012\u00020\u0002\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u0001H\b¢\u0006\u0002\b\u000e\u001a\u0014\u0010\u000f\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0010\u001a\u00020\u0002\u001a\u001e\u0010\u0011\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0010\u001a\u00020\u00022\b\b\u0002\u0010\u0012\u001a\u00020\u0002\u001a\n\u0010\u0013\u001a\u00020\u0002*\u00020\u0002\u001a\u0014\u0010\u0014\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0012\u001a\u00020\u0002¨\u0006\u0015"}, d2 = {"getIndentFunction", "Lkotlin/Function1;", "", "indent", "getIndentFunction$StringsKt__IndentKt", "indentWidth", "", "indentWidth$StringsKt__IndentKt", "prependIndent", "reindent", "", "resultSizeEstimate", "indentAddFunction", "indentCutFunction", "reindent$StringsKt__IndentKt", "replaceIndent", "newIndent", "replaceIndentByMargin", "marginPrefix", "trimIndent", "trimMargin", "kotlin-stdlib"}, k = 5, mv = {1, 1, 11}, xi = 1, xs = "kotlin/text/StringsKt")
/* compiled from: Indent.kt */
class StringsKt__IndentKt {
    @NotNull
    public static /* bridge */ /* synthetic */ String trimMargin$default(String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "|";
        }
        return StringsKt.trimMargin(str, str2);
    }

    @NotNull
    public static final String trimMargin(@NotNull String $receiver, @NotNull String marginPrefix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(marginPrefix, "marginPrefix");
        return StringsKt.replaceIndentByMargin($receiver, "", marginPrefix);
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceIndentByMargin$default(String str, String str2, String str3, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "";
        }
        if ((i & 2) != 0) {
            str3 = "|";
        }
        return StringsKt.replaceIndentByMargin(str, str2, str3);
    }

    @NotNull
    public static final String replaceIndentByMargin(@NotNull String $receiver, @NotNull String newIndent, @NotNull String marginPrefix) {
        Collection destination$iv$iv$iv;
        String str;
        String invoke;
        String str2 = $receiver;
        String str3 = marginPrefix;
        Intrinsics.checkParameterIsNotNull(str2, "$receiver");
        Intrinsics.checkParameterIsNotNull(newIndent, "newIndent");
        Intrinsics.checkParameterIsNotNull(str3, "marginPrefix");
        if (!StringsKt.isBlank(str3)) {
            List lines = StringsKt.lines(str2);
            int resultSizeEstimate$iv = $receiver.length() + (newIndent.length() * lines.size());
            List<String> $receiver$iv = lines;
            Function1 indentAddFunction$iv = getIndentFunction$StringsKt__IndentKt(newIndent);
            int lastIndex$iv = CollectionsKt.getLastIndex($receiver$iv);
            Iterable destination$iv$iv$iv2 = (Collection) new ArrayList();
            int index$iv$iv$iv$iv = 0;
            boolean z = false;
            boolean z2 = false;
            boolean z3 = false;
            boolean z4 = false;
            for (String value$iv : $receiver$iv) {
                int index$iv$iv$iv$iv2 = index$iv$iv$iv$iv + 1;
                boolean z5 = z;
                int index$iv = index$iv$iv$iv$iv;
                boolean z6 = z2;
                if ((index$iv == 0 || index$iv == lastIndex$iv) && StringsKt.isBlank(value$iv)) {
                    destination$iv$iv$iv = destination$iv$iv$iv2;
                    value$iv = null;
                } else {
                    String line = value$iv;
                    boolean z7 = z4;
                    CharSequence $receiver$iv2 = line;
                    boolean z8 = z3;
                    int length = $receiver$iv2.length();
                    int index$iv2 = 0;
                    while (true) {
                        if (index$iv2 >= length) {
                            index$iv2 = -1;
                            break;
                        } else if ((CharsKt.isWhitespace($receiver$iv2.charAt(index$iv2)) ^ 1) != 0) {
                            break;
                        } else {
                            index$iv2++;
                        }
                    }
                    if (index$iv2 == -1) {
                        int i = index$iv;
                        String str4 = line;
                        destination$iv$iv$iv = destination$iv$iv$iv2;
                        str = null;
                    } else {
                        int i2 = index$iv;
                        String line2 = line;
                        int firstNonWhitespaceIndex = index$iv2;
                        destination$iv$iv$iv = destination$iv$iv$iv2;
                        if (StringsKt.startsWith$default(line, marginPrefix, index$iv2, false, 4, (Object) null)) {
                            int length2 = firstNonWhitespaceIndex + marginPrefix.length();
                            String line3 = line2;
                            if (line3 != null) {
                                str = line3.substring(length2);
                                Intrinsics.checkExpressionValueIsNotNull(str, "(this as java.lang.String).substring(startIndex)");
                            } else {
                                throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
                            }
                        } else {
                            str = null;
                        }
                    }
                    if (str == null || (invoke = indentAddFunction$iv.invoke(str)) == null) {
                        z4 = z7;
                        z3 = z8;
                    } else {
                        value$iv = invoke;
                        z4 = z7;
                        z3 = z8;
                    }
                }
                if (value$iv != null) {
                    destination$iv$iv$iv.add(value$iv);
                }
                destination$iv$iv$iv2 = destination$iv$iv$iv;
                index$iv$iv$iv$iv = index$iv$iv$iv$iv2;
                z = z5;
                z2 = z6;
            }
            String sb = ((StringBuilder) CollectionsKt.joinTo$default((List) destination$iv$iv$iv2, new StringBuilder(resultSizeEstimate$iv), "\n", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 124, (Object) null)).toString();
            Intrinsics.checkExpressionValueIsNotNull(sb, "mapIndexedNotNull { inde…\"\\n\")\n        .toString()");
            return sb;
        }
        throw new IllegalArgumentException("marginPrefix must be non-blank string.".toString());
    }

    @NotNull
    public static final String trimIndent(@NotNull String $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.replaceIndent($receiver, "");
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceIndent$default(String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "";
        }
        return StringsKt.replaceIndent(str, str2);
    }

    @NotNull
    public static final String replaceIndent(@NotNull String $receiver, @NotNull String newIndent) {
        String it$iv$iv$iv;
        String str = $receiver;
        Intrinsics.checkParameterIsNotNull(str, "$receiver");
        Intrinsics.checkParameterIsNotNull(newIndent, "newIndent");
        List $receiver$iv = StringsKt.lines(str);
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $receiver$iv) {
            if (!StringsKt.isBlank((String) element$iv$iv)) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        Iterable<String> $receiver$iv2 = (List) destination$iv$iv;
        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv2, 10));
        for (String p1 : $receiver$iv2) {
            destination$iv$iv2.add(Integer.valueOf(indentWidth$StringsKt__IndentKt(p1)));
        }
        Integer num = (Integer) CollectionsKt.min((List) destination$iv$iv2);
        int minCommonIndent = num != null ? num.intValue() : 0;
        int resultSizeEstimate$iv = $receiver.length() + (newIndent.length() * $receiver$iv.size());
        Function1 indentAddFunction$iv = getIndentFunction$StringsKt__IndentKt(newIndent);
        List<String> $receiver$iv3 = $receiver$iv;
        int lastIndex$iv = CollectionsKt.getLastIndex($receiver$iv3);
        Collection destination$iv$iv$iv = new ArrayList();
        int index$iv$iv$iv$iv = 0;
        for (String value$iv : $receiver$iv3) {
            int index$iv$iv$iv$iv2 = index$iv$iv$iv$iv + 1;
            int index$iv = index$iv$iv$iv$iv;
            if ((index$iv == 0 || index$iv == lastIndex$iv) && StringsKt.isBlank(value$iv)) {
                it$iv$iv$iv = null;
            } else {
                int i = index$iv;
                String line = StringsKt.drop(value$iv, minCommonIndent);
                if (line == null || (it$iv$iv$iv = (String) indentAddFunction$iv.invoke(line)) == null) {
                    it$iv$iv$iv = value$iv;
                }
            }
            if (it$iv$iv$iv != null) {
                destination$iv$iv$iv.add(it$iv$iv$iv);
            }
            String str2 = $receiver;
            index$iv$iv$iv$iv = index$iv$iv$iv$iv2;
        }
        String sb = ((StringBuilder) CollectionsKt.joinTo$default((List) destination$iv$iv$iv, new StringBuilder(resultSizeEstimate$iv), "\n", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 124, (Object) null)).toString();
        Intrinsics.checkExpressionValueIsNotNull(sb, "mapIndexedNotNull { inde…\"\\n\")\n        .toString()");
        return sb;
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String prependIndent$default(String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "    ";
        }
        return StringsKt.prependIndent(str, str2);
    }

    @NotNull
    public static final String prependIndent(@NotNull String $receiver, @NotNull String indent) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(indent, "indent");
        return SequencesKt.joinToString$default(SequencesKt.map(StringsKt.lineSequence($receiver), new StringsKt__IndentKt$prependIndent$1(indent)), "\n", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null);
    }

    private static final int indentWidth$StringsKt__IndentKt(@NotNull String $receiver) {
        CharSequence $receiver$iv = $receiver;
        int index$iv = 0;
        int length = $receiver$iv.length();
        while (true) {
            if (index$iv >= length) {
                index$iv = -1;
                break;
            } else if ((CharsKt.isWhitespace($receiver$iv.charAt(index$iv)) ^ 1) != 0) {
                break;
            } else {
                index$iv++;
            }
        }
        int it = index$iv;
        int i = length;
        return it == -1 ? $receiver.length() : it;
    }

    private static final Function1<String, String> getIndentFunction$StringsKt__IndentKt(String indent) {
        if (indent.length() == 0) {
            return StringsKt__IndentKt$getIndentFunction$1.INSTANCE;
        }
        return new StringsKt__IndentKt$getIndentFunction$2(indent);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x006a, code lost:
        if (r0 != null) goto L_0x0072;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final java.lang.String reindent$StringsKt__IndentKt(@org.jetbrains.annotations.NotNull java.util.List<java.lang.String> r22, int r23, kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.String> r24, kotlin.jvm.functions.Function1<? super java.lang.String, java.lang.String> r25) {
        /*
            r0 = 0
            int r1 = kotlin.collections.CollectionsKt.getLastIndex(r22)
            r2 = r22
            java.lang.Iterable r2 = (java.lang.Iterable) r2
            r3 = 0
            r4 = r3
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.Collection r5 = (java.util.Collection) r5
            r6 = r2
            r7 = r3
            r8 = r6
            r9 = r3
            r10 = 0
            java.util.Iterator r11 = r8.iterator()
            r12 = 0
            r13 = 0
        L_0x0021:
            boolean r14 = r11.hasNext()
            if (r14 == 0) goto L_0x0082
            java.lang.Object r14 = r11.next()
            int r15 = r10 + 1
            r16 = r14
            r17 = r0
            r0 = r16
            java.lang.String r0 = (java.lang.String) r0
            r18 = r10
            r19 = r2
            r2 = r18
            if (r2 == 0) goto L_0x003f
            if (r2 != r1) goto L_0x0052
        L_0x003f:
            r18 = r0
            java.lang.CharSequence r18 = (java.lang.CharSequence) r18
            boolean r18 = kotlin.text.StringsKt.isBlank(r18)
            if (r18 == 0) goto L_0x0052
            r18 = 0
            r0 = r18
            r18 = r1
            r1 = r24
            goto L_0x0071
        L_0x0052:
            r18 = r1
            r1 = r25
            java.lang.Object r20 = r1.invoke(r0)
            r21 = r0
            r0 = r20
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x006d
            r1 = r24
            java.lang.Object r0 = r1.invoke(r0)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x006f
            goto L_0x0071
        L_0x006d:
            r1 = r24
        L_0x006f:
            r0 = r21
        L_0x0071:
            if (r0 == 0) goto L_0x0079
            r2 = r13
            r5.add(r0)
            goto L_0x007a
        L_0x0079:
        L_0x007a:
            r10 = r15
            r0 = r17
            r1 = r18
            r2 = r19
            goto L_0x0021
        L_0x0082:
            r17 = r0
            r18 = r1
            r19 = r2
            r1 = r24
            r0 = r5
            java.util.List r0 = (java.util.List) r0
            r2 = r0
            java.lang.Iterable r2 = (java.lang.Iterable) r2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r12 = r23
            r0.<init>(r12)
            r3 = r0
            java.lang.Appendable r3 = (java.lang.Appendable) r3
            java.lang.String r0 = "\n"
            r4 = r0
            java.lang.CharSequence r4 = (java.lang.CharSequence) r4
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 124(0x7c, float:1.74E-43)
            r11 = 0
            java.lang.Appendable r0 = kotlin.collections.CollectionsKt.joinTo$default(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            java.lang.StringBuilder r0 = (java.lang.StringBuilder) r0
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "mapIndexedNotNull { inde…\"\\n\")\n        .toString()"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.StringsKt__IndentKt.reindent$StringsKt__IndentKt(java.util.List, int, kotlin.jvm.functions.Function1, kotlin.jvm.functions.Function1):java.lang.String");
    }
}

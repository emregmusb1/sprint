package kotlin.io;

import java.io.File;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000$\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\u001a\u0011\u0010\u000b\u001a\u00020\f*\u00020\bH\u0002¢\u0006\u0002\b\r\u001a\u001c\u0010\u000e\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\fH\u0000\u001a\f\u0010\u0011\u001a\u00020\u0012*\u00020\u0002H\u0000\"\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0000\u0010\u0003\"\u0018\u0010\u0004\u001a\u00020\u0002*\u00020\u00028@X\u0004¢\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006\"\u0018\u0010\u0007\u001a\u00020\b*\u00020\u00028@X\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\n¨\u0006\u0013"}, d2 = {"isRooted", "", "Ljava/io/File;", "(Ljava/io/File;)Z", "root", "getRoot", "(Ljava/io/File;)Ljava/io/File;", "rootName", "", "getRootName", "(Ljava/io/File;)Ljava/lang/String;", "getRootLength", "", "getRootLength$FilesKt__FilePathComponentsKt", "subPath", "beginIndex", "endIndex", "toComponents", "Lkotlin/io/FilePathComponents;", "kotlin-stdlib"}, k = 5, mv = {1, 1, 11}, xi = 1, xs = "kotlin/io/FilesKt")
/* compiled from: FilePathComponents.kt */
class FilesKt__FilePathComponentsKt {
    private static final int getRootLength$FilesKt__FilePathComponentsKt(@NotNull String $receiver) {
        int first;
        int first2 = StringsKt.indexOf$default((CharSequence) $receiver, File.separatorChar, 0, false, 4, (Object) null);
        if (first2 == 0) {
            if ($receiver.length() <= 1 || $receiver.charAt(1) != File.separatorChar || (first = StringsKt.indexOf$default((CharSequence) $receiver, File.separatorChar, 2, false, 4, (Object) null)) < 0) {
                return 1;
            }
            int first3 = StringsKt.indexOf$default((CharSequence) $receiver, File.separatorChar, first + 1, false, 4, (Object) null);
            if (first3 >= 0) {
                return first3 + 1;
            }
            return $receiver.length();
        } else if (first2 > 0 && $receiver.charAt(first2 - 1) == ':') {
            return first2 + 1;
        } else {
            if (first2 != -1 || !StringsKt.endsWith$default((CharSequence) $receiver, ':', false, 2, (Object) null)) {
                return 0;
            }
            return $receiver.length();
        }
    }

    @NotNull
    public static final String getRootName(@NotNull File $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        String path = $receiver.getPath();
        Intrinsics.checkExpressionValueIsNotNull(path, "path");
        String path2 = $receiver.getPath();
        Intrinsics.checkExpressionValueIsNotNull(path2, "path");
        int rootLength$FilesKt__FilePathComponentsKt = getRootLength$FilesKt__FilePathComponentsKt(path2);
        if (path != null) {
            String substring = path.substring(0, rootLength$FilesKt__FilePathComponentsKt);
            Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
            return substring;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    @NotNull
    public static final File getRoot(@NotNull File $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new File(FilesKt.getRootName($receiver));
    }

    public static final boolean isRooted(@NotNull File $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        String path = $receiver.getPath();
        Intrinsics.checkExpressionValueIsNotNull(path, "path");
        return getRootLength$FilesKt__FilePathComponentsKt(path) > 0;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: java.util.Collection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: java.util.List} */
    /* JADX WARNING: Multi-variable type inference failed */
    @org.jetbrains.annotations.NotNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final kotlin.io.FilePathComponents toComponents(@org.jetbrains.annotations.NotNull java.io.File r14) {
        /*
            java.lang.String r0 = "$receiver"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r14, r0)
            java.lang.String r0 = r14.getPath()
            java.lang.String r1 = "path"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r1)
            int r1 = getRootLength$FilesKt__FilePathComponentsKt(r0)
            r2 = 0
            java.lang.String r3 = r0.substring(r2, r1)
            java.lang.String r4 = "(this as java.lang.Strin…ing(startIndex, endIndex)"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r3, r4)
            java.lang.String r4 = r0.substring(r1)
            java.lang.String r5 = "(this as java.lang.String).substring(startIndex)"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r4, r5)
            r5 = r4
            java.lang.CharSequence r5 = (java.lang.CharSequence) r5
            int r5 = r5.length()
            r6 = 1
            if (r5 != 0) goto L_0x0031
            r5 = 1
            goto L_0x0032
        L_0x0031:
            r5 = 0
        L_0x0032:
            if (r5 == 0) goto L_0x0039
            java.util.List r2 = kotlin.collections.CollectionsKt.emptyList()
            goto L_0x0079
        L_0x0039:
            r5 = r4
            java.lang.CharSequence r5 = (java.lang.CharSequence) r5
            char[] r6 = new char[r6]
            char r7 = java.io.File.separatorChar
            r6[r2] = r7
            r7 = 0
            r8 = 0
            r9 = 6
            r10 = 0
            java.util.List r5 = kotlin.text.StringsKt.split$default((java.lang.CharSequence) r5, (char[]) r6, (boolean) r7, (int) r8, (int) r9, (java.lang.Object) r10)
            java.lang.Iterable r5 = (java.lang.Iterable) r5
            r6 = r2
            java.util.ArrayList r7 = new java.util.ArrayList
            r8 = 10
            int r8 = kotlin.collections.CollectionsKt.collectionSizeOrDefault(r5, r8)
            r7.<init>(r8)
            java.util.Collection r7 = (java.util.Collection) r7
            r8 = r5
            r9 = r2
            java.util.Iterator r10 = r8.iterator()
        L_0x0060:
            boolean r11 = r10.hasNext()
            if (r11 == 0) goto L_0x0076
            java.lang.Object r11 = r10.next()
            r12 = r11
            java.lang.String r12 = (java.lang.String) r12
            java.io.File r13 = new java.io.File
            r13.<init>(r12)
            r7.add(r13)
            goto L_0x0060
        L_0x0076:
            r2 = r7
            java.util.List r2 = (java.util.List) r2
        L_0x0079:
            kotlin.io.FilePathComponents r5 = new kotlin.io.FilePathComponents
            java.io.File r6 = new java.io.File
            r6.<init>(r3)
            r5.<init>(r6, r2)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FilePathComponentsKt.toComponents(java.io.File):kotlin.io.FilePathComponents");
    }

    @NotNull
    public static final File subPath(@NotNull File $receiver, int beginIndex, int endIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return FilesKt.toComponents($receiver).subPath(beginIndex, endIndex);
    }
}

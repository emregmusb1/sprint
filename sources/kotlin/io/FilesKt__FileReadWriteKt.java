package kotlin.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.internal.InlineOnly;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000z\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004\u001a\u001c\u0010\u0005\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t\u001a!\u0010\n\u001a\u00020\u000b*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\rH\b\u001a!\u0010\u000e\u001a\u00020\u000f*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\rH\b\u001aB\u0010\u0010\u001a\u00020\u0001*\u00020\u000226\u0010\u0011\u001a2\u0012\u0013\u0012\u00110\u0004¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015\u0012\u0013\u0012\u00110\r¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0016\u0012\u0004\u0012\u00020\u00010\u0012\u001aJ\u0010\u0010\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\r26\u0010\u0011\u001a2\u0012\u0013\u0012\u00110\u0004¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015\u0012\u0013\u0012\u00110\r¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0016\u0012\u0004\u0012\u00020\u00010\u0012\u001a7\u0010\u0018\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t2!\u0010\u0011\u001a\u001d\u0012\u0013\u0012\u00110\u0007¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u001a\u0012\u0004\u0012\u00020\u00010\u0019\u001a\r\u0010\u001b\u001a\u00020\u001c*\u00020\u0002H\b\u001a\r\u0010\u001d\u001a\u00020\u001e*\u00020\u0002H\b\u001a\u0017\u0010\u001f\u001a\u00020 *\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\tH\b\u001a\n\u0010!\u001a\u00020\u0004*\u00020\u0002\u001a\u001a\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00070#*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t\u001a\u0014\u0010$\u001a\u00020\u0007*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t\u001a\u0017\u0010%\u001a\u00020&*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\tH\b\u001a?\u0010'\u001a\u0002H(\"\u0004\b\u0000\u0010(*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t2\u0018\u0010)\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070*\u0012\u0004\u0012\u0002H(0\u0019H\bø\u0001\u0000¢\u0006\u0002\u0010,\u001a\u0012\u0010-\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004\u001a\u001c\u0010.\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t\u001a\u0017\u0010/\u001a\u000200*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\tH\b\u0002\b\n\u0006\b\u0011(+0\u0001¨\u00061"}, d2 = {"appendBytes", "", "Ljava/io/File;", "array", "", "appendText", "text", "", "charset", "Ljava/nio/charset/Charset;", "bufferedReader", "Ljava/io/BufferedReader;", "bufferSize", "", "bufferedWriter", "Ljava/io/BufferedWriter;", "forEachBlock", "action", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "buffer", "bytesRead", "blockSize", "forEachLine", "Lkotlin/Function1;", "line", "inputStream", "Ljava/io/FileInputStream;", "outputStream", "Ljava/io/FileOutputStream;", "printWriter", "Ljava/io/PrintWriter;", "readBytes", "readLines", "", "readText", "reader", "Ljava/io/InputStreamReader;", "useLines", "T", "block", "Lkotlin/sequences/Sequence;", "Requires newer compiler version to be inlined correctly.", "(Ljava/io/File;Ljava/nio/charset/Charset;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "writeBytes", "writeText", "writer", "Ljava/io/OutputStreamWriter;", "kotlin-stdlib"}, k = 5, mv = {1, 1, 11}, xi = 1, xs = "kotlin/io/FilesKt")
/* compiled from: FileReadWrite.kt */
class FilesKt__FileReadWriteKt extends FilesKt__FilePathComponentsKt {
    @InlineOnly
    static /* bridge */ /* synthetic */ InputStreamReader reader$default(File $receiver, Charset charset, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        return new InputStreamReader(new FileInputStream($receiver), charset);
    }

    @InlineOnly
    private static final InputStreamReader reader(@NotNull File $receiver, Charset charset) {
        return new InputStreamReader(new FileInputStream($receiver), charset);
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ BufferedReader bufferedReader$default(File $receiver, Charset charset, int bufferSize, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        if ((i & 2) != 0) {
            bufferSize = 8192;
        }
        Reader inputStreamReader = new InputStreamReader(new FileInputStream($receiver), charset);
        return inputStreamReader instanceof BufferedReader ? (BufferedReader) inputStreamReader : new BufferedReader(inputStreamReader, bufferSize);
    }

    @InlineOnly
    private static final BufferedReader bufferedReader(@NotNull File $receiver, Charset charset, int bufferSize) {
        Reader inputStreamReader = new InputStreamReader(new FileInputStream($receiver), charset);
        return inputStreamReader instanceof BufferedReader ? (BufferedReader) inputStreamReader : new BufferedReader(inputStreamReader, bufferSize);
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ OutputStreamWriter writer$default(File $receiver, Charset charset, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        return new OutputStreamWriter(new FileOutputStream($receiver), charset);
    }

    @InlineOnly
    private static final OutputStreamWriter writer(@NotNull File $receiver, Charset charset) {
        return new OutputStreamWriter(new FileOutputStream($receiver), charset);
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ BufferedWriter bufferedWriter$default(File $receiver, Charset charset, int bufferSize, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        if ((i & 2) != 0) {
            bufferSize = 8192;
        }
        Writer outputStreamWriter = new OutputStreamWriter(new FileOutputStream($receiver), charset);
        return outputStreamWriter instanceof BufferedWriter ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, bufferSize);
    }

    @InlineOnly
    private static final BufferedWriter bufferedWriter(@NotNull File $receiver, Charset charset, int bufferSize) {
        Writer outputStreamWriter = new OutputStreamWriter(new FileOutputStream($receiver), charset);
        return outputStreamWriter instanceof BufferedWriter ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, bufferSize);
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ PrintWriter printWriter$default(File $receiver, Charset charset, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        Writer outputStreamWriter = new OutputStreamWriter(new FileOutputStream($receiver), charset);
        return new PrintWriter(outputStreamWriter instanceof BufferedWriter ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, 8192));
    }

    @InlineOnly
    private static final PrintWriter printWriter(@NotNull File $receiver, Charset charset) {
        Writer outputStreamWriter = new OutputStreamWriter(new FileOutputStream($receiver), charset);
        return new PrintWriter(outputStreamWriter instanceof BufferedWriter ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, 8192));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x006d, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0071, code lost:
        kotlin.io.CloseableKt.closeFinally(r0, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0074, code lost:
        throw r2;
     */
    @org.jetbrains.annotations.NotNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final byte[] readBytes(@org.jetbrains.annotations.NotNull java.io.File r13) {
        /*
            java.lang.String r0 = "$receiver"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r13, r0)
            java.io.FileInputStream r0 = new java.io.FileInputStream
            r0.<init>(r13)
            java.io.Closeable r0 = (java.io.Closeable) r0
            r1 = 0
            java.lang.Throwable r1 = (java.lang.Throwable) r1
            r2 = r0
            java.io.FileInputStream r2 = (java.io.FileInputStream) r2     // Catch:{ Throwable -> 0x006f }
            r3 = 0
            r4 = r3
            r5 = 0
            long r6 = r13.length()     // Catch:{ Throwable -> 0x006f }
            r8 = r6
            r10 = 2147483647(0x7fffffff, float:NaN)
            long r10 = (long) r10     // Catch:{ Throwable -> 0x006f }
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 > 0) goto L_0x0047
            int r3 = (int) r6     // Catch:{ Throwable -> 0x006f }
            byte[] r6 = new byte[r3]     // Catch:{ Throwable -> 0x006f }
        L_0x002a:
            if (r3 <= 0) goto L_0x0036
            int r7 = r2.read(r6, r5, r3)     // Catch:{ Throwable -> 0x006f }
            if (r7 >= 0) goto L_0x0033
            goto L_0x0036
        L_0x0033:
            int r3 = r3 - r7
            int r5 = r5 + r7
            goto L_0x002a
        L_0x0036:
            if (r3 != 0) goto L_0x0039
            goto L_0x0043
        L_0x0039:
            byte[] r7 = java.util.Arrays.copyOf(r6, r5)     // Catch:{ Throwable -> 0x006f }
            java.lang.String r8 = "java.util.Arrays.copyOf(this, newSize)"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r7, r8)     // Catch:{ Throwable -> 0x006f }
            r6 = r7
        L_0x0043:
            kotlin.io.CloseableKt.closeFinally(r0, r1)
            return r6
        L_0x0047:
            java.lang.OutOfMemoryError r6 = new java.lang.OutOfMemoryError     // Catch:{ Throwable -> 0x006f }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x006f }
            r7.<init>()     // Catch:{ Throwable -> 0x006f }
            java.lang.String r10 = "File "
            r7.append(r10)     // Catch:{ Throwable -> 0x006f }
            r7.append(r13)     // Catch:{ Throwable -> 0x006f }
            java.lang.String r10 = " is too big ("
            r7.append(r10)     // Catch:{ Throwable -> 0x006f }
            r7.append(r8)     // Catch:{ Throwable -> 0x006f }
            java.lang.String r10 = " bytes) to fit in memory."
            r7.append(r10)     // Catch:{ Throwable -> 0x006f }
            java.lang.String r7 = r7.toString()     // Catch:{ Throwable -> 0x006f }
            r6.<init>(r7)     // Catch:{ Throwable -> 0x006f }
            java.lang.Throwable r6 = (java.lang.Throwable) r6     // Catch:{ Throwable -> 0x006f }
            throw r6     // Catch:{ Throwable -> 0x006f }
        L_0x006d:
            r2 = move-exception
            goto L_0x0071
        L_0x006f:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x006d }
        L_0x0071:
            kotlin.io.CloseableKt.closeFinally(r0, r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.readBytes(java.io.File):byte[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0028, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0021, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0025, code lost:
        kotlin.io.CloseableKt.closeFinally(r0, r1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final void writeBytes(@org.jetbrains.annotations.NotNull java.io.File r4, @org.jetbrains.annotations.NotNull byte[] r5) {
        /*
            java.lang.String r0 = "$receiver"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r4, r0)
            java.lang.String r0 = "array"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r5, r0)
            java.io.FileOutputStream r0 = new java.io.FileOutputStream
            r0.<init>(r4)
            java.io.Closeable r0 = (java.io.Closeable) r0
            r1 = 0
            java.lang.Throwable r1 = (java.lang.Throwable) r1
            r2 = r0
            java.io.FileOutputStream r2 = (java.io.FileOutputStream) r2     // Catch:{ Throwable -> 0x0023 }
            r3 = 0
            r2.write(r5)     // Catch:{ Throwable -> 0x0023 }
            kotlin.Unit r2 = kotlin.Unit.INSTANCE     // Catch:{ Throwable -> 0x0023 }
            kotlin.io.CloseableKt.closeFinally(r0, r1)
            return
        L_0x0021:
            r2 = move-exception
            goto L_0x0025
        L_0x0023:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0021 }
        L_0x0025:
            kotlin.io.CloseableKt.closeFinally(r0, r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.writeBytes(java.io.File, byte[]):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0029, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0022, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0026, code lost:
        kotlin.io.CloseableKt.closeFinally(r0, r1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final void appendBytes(@org.jetbrains.annotations.NotNull java.io.File r4, @org.jetbrains.annotations.NotNull byte[] r5) {
        /*
            java.lang.String r0 = "$receiver"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r4, r0)
            java.lang.String r0 = "array"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r5, r0)
            java.io.FileOutputStream r0 = new java.io.FileOutputStream
            r1 = 1
            r0.<init>(r4, r1)
            java.io.Closeable r0 = (java.io.Closeable) r0
            r1 = 0
            java.lang.Throwable r1 = (java.lang.Throwable) r1
            r2 = r0
            java.io.FileOutputStream r2 = (java.io.FileOutputStream) r2     // Catch:{ Throwable -> 0x0024 }
            r3 = 0
            r2.write(r5)     // Catch:{ Throwable -> 0x0024 }
            kotlin.Unit r2 = kotlin.Unit.INSTANCE     // Catch:{ Throwable -> 0x0024 }
            kotlin.io.CloseableKt.closeFinally(r0, r1)
            return
        L_0x0022:
            r2 = move-exception
            goto L_0x0026
        L_0x0024:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0022 }
        L_0x0026:
            kotlin.io.CloseableKt.closeFinally(r0, r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.appendBytes(java.io.File, byte[]):void");
    }

    @NotNull
    public static final String readText(@NotNull File $receiver, @NotNull Charset charset) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        return new String(FilesKt.readBytes($receiver), charset);
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String readText$default(File file, Charset charset, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        return FilesKt.readText(file, charset);
    }

    public static final void writeText(@NotNull File $receiver, @NotNull String text, @NotNull Charset charset) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(text, "text");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        byte[] bytes = text.getBytes(charset);
        Intrinsics.checkExpressionValueIsNotNull(bytes, "(this as java.lang.String).getBytes(charset)");
        FilesKt.writeBytes($receiver, bytes);
    }

    public static /* bridge */ /* synthetic */ void writeText$default(File file, String str, Charset charset, int i, Object obj) {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        FilesKt.writeText(file, str, charset);
    }

    public static final void appendText(@NotNull File $receiver, @NotNull String text, @NotNull Charset charset) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(text, "text");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        byte[] bytes = text.getBytes(charset);
        Intrinsics.checkExpressionValueIsNotNull(bytes, "(this as java.lang.String).getBytes(charset)");
        FilesKt.appendBytes($receiver, bytes);
    }

    public static /* bridge */ /* synthetic */ void appendText$default(File file, String str, Charset charset, int i, Object obj) {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        FilesKt.appendText(file, str, charset);
    }

    public static final void forEachBlock(@NotNull File $receiver, @NotNull Function2<? super byte[], ? super Integer, Unit> action) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(action, "action");
        FilesKt.forEachBlock($receiver, 4096, action);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0037, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003b, code lost:
        kotlin.io.CloseableKt.closeFinally(r1, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003e, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final void forEachBlock(@org.jetbrains.annotations.NotNull java.io.File r7, int r8, @org.jetbrains.annotations.NotNull kotlin.jvm.functions.Function2<? super byte[], ? super java.lang.Integer, kotlin.Unit> r9) {
        /*
            java.lang.String r0 = "$receiver"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r7, r0)
            java.lang.String r0 = "action"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r9, r0)
            r0 = 512(0x200, float:7.175E-43)
            int r0 = kotlin.ranges.RangesKt.coerceAtLeast((int) r8, (int) r0)
            byte[] r0 = new byte[r0]
            java.io.FileInputStream r1 = new java.io.FileInputStream
            r1.<init>(r7)
            java.io.Closeable r1 = (java.io.Closeable) r1
            r2 = 0
            java.lang.Throwable r2 = (java.lang.Throwable) r2
            r3 = r1
            java.io.FileInputStream r3 = (java.io.FileInputStream) r3     // Catch:{ Throwable -> 0x0039 }
            r4 = 0
        L_0x0020:
            int r5 = r3.read(r0)     // Catch:{ Throwable -> 0x0039 }
            if (r5 > 0) goto L_0x002e
            kotlin.Unit r3 = kotlin.Unit.INSTANCE     // Catch:{ Throwable -> 0x0039 }
            kotlin.io.CloseableKt.closeFinally(r1, r2)
            return
        L_0x002e:
            java.lang.Integer r6 = java.lang.Integer.valueOf(r5)     // Catch:{ Throwable -> 0x0039 }
            r9.invoke(r0, r6)     // Catch:{ Throwable -> 0x0039 }
            goto L_0x0020
        L_0x0037:
            r3 = move-exception
            goto L_0x003b
        L_0x0039:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0037 }
        L_0x003b:
            kotlin.io.CloseableKt.closeFinally(r1, r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.forEachBlock(java.io.File, int, kotlin.jvm.functions.Function2):void");
    }

    public static /* bridge */ /* synthetic */ void forEachLine$default(File file, Charset charset, Function1 function1, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        FilesKt.forEachLine(file, charset, function1);
    }

    public static final void forEachLine(@NotNull File $receiver, @NotNull Charset charset, @NotNull Function1<? super String, Unit> action) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        Intrinsics.checkParameterIsNotNull(action, "action");
        TextStreamsKt.forEachLine(new BufferedReader(new InputStreamReader(new FileInputStream($receiver), charset)), action);
    }

    @InlineOnly
    private static final FileInputStream inputStream(@NotNull File $receiver) {
        return new FileInputStream($receiver);
    }

    @InlineOnly
    private static final FileOutputStream outputStream(@NotNull File $receiver) {
        return new FileOutputStream($receiver);
    }

    @NotNull
    public static /* bridge */ /* synthetic */ List readLines$default(File file, Charset charset, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        return FilesKt.readLines(file, charset);
    }

    @NotNull
    public static final List<String> readLines(@NotNull File $receiver, @NotNull Charset charset) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        ArrayList result = new ArrayList();
        FilesKt.forEachLine($receiver, charset, new FilesKt__FileReadWriteKt$readLines$1(result));
        return result;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x005a, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005e, code lost:
        kotlin.jvm.internal.InlineMarker.finallyStart(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0065, code lost:
        if (kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(1, 1, 0) == false) goto L_0x0067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0067, code lost:
        if (r10 == null) goto L_0x0069;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0069, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0073, code lost:
        kotlin.io.CloseableKt.closeFinally(r0, r10);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* bridge */ /* synthetic */ java.lang.Object useLines$default(java.io.File r6, java.nio.charset.Charset r7, kotlin.jvm.functions.Function1 r8, int r9, java.lang.Object r10) {
        /*
            r10 = r9 & 1
            if (r10 == 0) goto L_0x0007
            java.nio.charset.Charset r7 = kotlin.text.Charsets.UTF_8
        L_0x0007:
            java.lang.String r10 = "$receiver"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r6, r10)
            java.lang.String r10 = "charset"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r7, r10)
            java.lang.String r10 = "block"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r8, r10)
            r10 = 8192(0x2000, float:1.14794E-41)
            java.io.FileInputStream r0 = new java.io.FileInputStream
            r0.<init>(r6)
            java.io.InputStream r0 = (java.io.InputStream) r0
            java.io.InputStreamReader r1 = new java.io.InputStreamReader
            r1.<init>(r0, r7)
            java.io.Reader r1 = (java.io.Reader) r1
            boolean r0 = r1 instanceof java.io.BufferedReader
            if (r0 == 0) goto L_0x002e
            java.io.BufferedReader r1 = (java.io.BufferedReader) r1
            r0 = r1
            goto L_0x0033
        L_0x002e:
            java.io.BufferedReader r0 = new java.io.BufferedReader
            r0.<init>(r1, r10)
        L_0x0033:
            java.io.Closeable r0 = (java.io.Closeable) r0
            r10 = 0
            java.lang.Throwable r10 = (java.lang.Throwable) r10
            r1 = 0
            r2 = 1
            r3 = r0
            java.io.BufferedReader r3 = (java.io.BufferedReader) r3     // Catch:{ Throwable -> 0x005c }
            r4 = r1
            kotlin.sequences.Sequence r5 = kotlin.io.TextStreamsKt.lineSequence(r3)     // Catch:{ Throwable -> 0x005c }
            java.lang.Object r5 = r8.invoke(r5)     // Catch:{ Throwable -> 0x005c }
            kotlin.jvm.internal.InlineMarker.finallyStart(r2)
            boolean r1 = kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(r2, r2, r1)
            if (r1 == 0) goto L_0x0053
            kotlin.io.CloseableKt.closeFinally(r0, r10)
            goto L_0x0056
        L_0x0053:
            r0.close()
        L_0x0056:
            kotlin.jvm.internal.InlineMarker.finallyEnd(r2)
            return r5
        L_0x005a:
            r3 = move-exception
            goto L_0x005e
        L_0x005c:
            r10 = move-exception
            throw r10     // Catch:{ all -> 0x005a }
        L_0x005e:
            kotlin.jvm.internal.InlineMarker.finallyStart(r2)
            boolean r1 = kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(r2, r2, r1)
            if (r1 != 0) goto L_0x0073
            if (r10 != 0) goto L_0x006d
            r0.close()
            goto L_0x0076
        L_0x006d:
            r0.close()     // Catch:{ Throwable -> 0x0071 }
            goto L_0x0076
        L_0x0071:
            r10 = move-exception
            goto L_0x0076
        L_0x0073:
            kotlin.io.CloseableKt.closeFinally(r0, r10)
        L_0x0076:
            kotlin.jvm.internal.InlineMarker.finallyEnd(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.useLines$default(java.io.File, java.nio.charset.Charset, kotlin.jvm.functions.Function1, int, java.lang.Object):java.lang.Object");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0054, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0058, code lost:
        kotlin.jvm.internal.InlineMarker.finallyStart(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x005f, code lost:
        if (kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(1, 1, 0) == false) goto L_0x0061;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0061, code lost:
        if (r2 == null) goto L_0x0063;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0063, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x006d, code lost:
        kotlin.io.CloseableKt.closeFinally(r1, r2);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final <T> T useLines(@org.jetbrains.annotations.NotNull java.io.File r8, @org.jetbrains.annotations.NotNull java.nio.charset.Charset r9, @org.jetbrains.annotations.NotNull kotlin.jvm.functions.Function1<? super kotlin.sequences.Sequence<java.lang.String>, ? extends T> r10) {
        /*
            r0 = 0
            java.lang.String r1 = "$receiver"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r8, r1)
            java.lang.String r1 = "charset"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r9, r1)
            java.lang.String r1 = "block"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r10, r1)
            java.io.FileInputStream r1 = new java.io.FileInputStream
            r1.<init>(r8)
            java.io.InputStream r1 = (java.io.InputStream) r1
            java.io.InputStreamReader r2 = new java.io.InputStreamReader
            r2.<init>(r1, r9)
            java.io.Reader r2 = (java.io.Reader) r2
            boolean r1 = r2 instanceof java.io.BufferedReader
            if (r1 == 0) goto L_0x0026
            java.io.BufferedReader r2 = (java.io.BufferedReader) r2
            r1 = r2
            goto L_0x002d
        L_0x0026:
            java.io.BufferedReader r1 = new java.io.BufferedReader
            r3 = 8192(0x2000, float:1.14794E-41)
            r1.<init>(r2, r3)
        L_0x002d:
            java.io.Closeable r1 = (java.io.Closeable) r1
            r2 = 0
            java.lang.Throwable r2 = (java.lang.Throwable) r2
            r3 = 0
            r4 = 1
            r5 = r1
            java.io.BufferedReader r5 = (java.io.BufferedReader) r5     // Catch:{ Throwable -> 0x0056 }
            r6 = r3
            kotlin.sequences.Sequence r7 = kotlin.io.TextStreamsKt.lineSequence(r5)     // Catch:{ Throwable -> 0x0056 }
            java.lang.Object r7 = r10.invoke(r7)     // Catch:{ Throwable -> 0x0056 }
            kotlin.jvm.internal.InlineMarker.finallyStart(r4)
            boolean r3 = kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(r4, r4, r3)
            if (r3 == 0) goto L_0x004d
            kotlin.io.CloseableKt.closeFinally(r1, r2)
            goto L_0x0050
        L_0x004d:
            r1.close()
        L_0x0050:
            kotlin.jvm.internal.InlineMarker.finallyEnd(r4)
            return r7
        L_0x0054:
            r5 = move-exception
            goto L_0x0058
        L_0x0056:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0054 }
        L_0x0058:
            kotlin.jvm.internal.InlineMarker.finallyStart(r4)
            boolean r3 = kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(r4, r4, r3)
            if (r3 != 0) goto L_0x006d
            if (r2 != 0) goto L_0x0067
            r1.close()
            goto L_0x0070
        L_0x0067:
            r1.close()     // Catch:{ Throwable -> 0x006b }
            goto L_0x0070
        L_0x006b:
            r1 = move-exception
            goto L_0x0070
        L_0x006d:
            kotlin.io.CloseableKt.closeFinally(r1, r2)
        L_0x0070:
            kotlin.jvm.internal.InlineMarker.finallyEnd(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.useLines(java.io.File, java.nio.charset.Charset, kotlin.jvm.functions.Function1):java.lang.Object");
    }
}

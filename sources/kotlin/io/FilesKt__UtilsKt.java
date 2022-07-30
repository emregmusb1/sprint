package kotlin.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000<\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\u001a(\u0010\t\u001a\u00020\u00022\b\b\u0002\u0010\n\u001a\u00020\u00012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0002\u001a(\u0010\r\u001a\u00020\u00022\b\b\u0002\u0010\n\u001a\u00020\u00012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0002\u001a8\u0010\u000e\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00022\b\b\u0002\u0010\u0011\u001a\u00020\u000f2\u001a\b\u0002\u0010\u0012\u001a\u0014\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00150\u0013\u001a&\u0010\u0016\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00022\b\b\u0002\u0010\u0011\u001a\u00020\u000f2\b\b\u0002\u0010\u0017\u001a\u00020\u0018\u001a\n\u0010\u0019\u001a\u00020\u000f*\u00020\u0002\u001a\u0012\u0010\u001a\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u0002\u001a\u0012\u0010\u001a\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u0001\u001a\n\u0010\u001c\u001a\u00020\u0002*\u00020\u0002\u001a\u001d\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00020\u001d*\b\u0012\u0004\u0012\u00020\u00020\u001dH\u0002¢\u0006\u0002\b\u001e\u001a\u0011\u0010\u001c\u001a\u00020\u001f*\u00020\u001fH\u0002¢\u0006\u0002\b\u001e\u001a\u0012\u0010 \u001a\u00020\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002\u001a\u0014\u0010\"\u001a\u0004\u0018\u00010\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002\u001a\u0012\u0010#\u001a\u00020\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002\u001a\u0012\u0010$\u001a\u00020\u0002*\u00020\u00022\u0006\u0010%\u001a\u00020\u0002\u001a\u0012\u0010$\u001a\u00020\u0002*\u00020\u00022\u0006\u0010%\u001a\u00020\u0001\u001a\u0012\u0010&\u001a\u00020\u0002*\u00020\u00022\u0006\u0010%\u001a\u00020\u0002\u001a\u0012\u0010&\u001a\u00020\u0002*\u00020\u00022\u0006\u0010%\u001a\u00020\u0001\u001a\u0012\u0010'\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u0002\u001a\u0012\u0010'\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u0001\u001a\u0012\u0010(\u001a\u00020\u0001*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002\u001a\u001b\u0010)\u001a\u0004\u0018\u00010\u0001*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002H\u0002¢\u0006\u0002\b*\"\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\"\u0015\u0010\u0005\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0004\"\u0015\u0010\u0007\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\b\u0010\u0004¨\u0006+"}, d2 = {"extension", "", "Ljava/io/File;", "getExtension", "(Ljava/io/File;)Ljava/lang/String;", "invariantSeparatorsPath", "getInvariantSeparatorsPath", "nameWithoutExtension", "getNameWithoutExtension", "createTempDir", "prefix", "suffix", "directory", "createTempFile", "copyRecursively", "", "target", "overwrite", "onError", "Lkotlin/Function2;", "Ljava/io/IOException;", "Lkotlin/io/OnErrorAction;", "copyTo", "bufferSize", "", "deleteRecursively", "endsWith", "other", "normalize", "", "normalize$FilesKt__UtilsKt", "Lkotlin/io/FilePathComponents;", "relativeTo", "base", "relativeToOrNull", "relativeToOrSelf", "resolve", "relative", "resolveSibling", "startsWith", "toRelativeString", "toRelativeStringOrNull", "toRelativeStringOrNull$FilesKt__UtilsKt", "kotlin-stdlib"}, k = 5, mv = {1, 1, 11}, xi = 1, xs = "kotlin/io/FilesKt")
/* compiled from: Utils.kt */
class FilesKt__UtilsKt extends FilesKt__FileTreeWalkKt {
    @NotNull
    public static /* bridge */ /* synthetic */ File createTempDir$default(String str, String str2, File file, int i, Object obj) {
        if ((i & 1) != 0) {
            str = "tmp";
        }
        if ((i & 2) != 0) {
            str2 = null;
        }
        if ((i & 4) != 0) {
            file = null;
        }
        return FilesKt.createTempDir(str, str2, file);
    }

    @NotNull
    public static final File createTempDir(@NotNull String prefix, @Nullable String suffix, @Nullable File directory) {
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        File dir = File.createTempFile(prefix, suffix, directory);
        dir.delete();
        if (dir.mkdir()) {
            Intrinsics.checkExpressionValueIsNotNull(dir, "dir");
            return dir;
        }
        throw new IOException("Unable to create temporary directory " + dir + '.');
    }

    @NotNull
    public static /* bridge */ /* synthetic */ File createTempFile$default(String str, String str2, File file, int i, Object obj) {
        if ((i & 1) != 0) {
            str = "tmp";
        }
        if ((i & 2) != 0) {
            str2 = null;
        }
        if ((i & 4) != 0) {
            file = null;
        }
        return FilesKt.createTempFile(str, str2, file);
    }

    @NotNull
    public static final File createTempFile(@NotNull String prefix, @Nullable String suffix, @Nullable File directory) {
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        File createTempFile = File.createTempFile(prefix, suffix, directory);
        Intrinsics.checkExpressionValueIsNotNull(createTempFile, "File.createTempFile(prefix, suffix, directory)");
        return createTempFile;
    }

    @NotNull
    public static final String getExtension(@NotNull File $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        String name = $receiver.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, "name");
        return StringsKt.substringAfterLast(name, '.', "");
    }

    @NotNull
    public static final String getInvariantSeparatorsPath(@NotNull File $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if (File.separatorChar != '/') {
            String path = $receiver.getPath();
            Intrinsics.checkExpressionValueIsNotNull(path, "path");
            return StringsKt.replace$default(path, File.separatorChar, '/', false, 4, (Object) null);
        }
        String path2 = $receiver.getPath();
        Intrinsics.checkExpressionValueIsNotNull(path2, "path");
        return path2;
    }

    @NotNull
    public static final String getNameWithoutExtension(@NotNull File $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        String name = $receiver.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, "name");
        return StringsKt.substringBeforeLast$default(name, ".", (String) null, 2, (Object) null);
    }

    @NotNull
    public static final String toRelativeString(@NotNull File $receiver, @NotNull File base) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(base, "base");
        String relativeStringOrNull$FilesKt__UtilsKt = toRelativeStringOrNull$FilesKt__UtilsKt($receiver, base);
        if (relativeStringOrNull$FilesKt__UtilsKt != null) {
            return relativeStringOrNull$FilesKt__UtilsKt;
        }
        throw new IllegalArgumentException("this and base files have different roots: " + $receiver + " and " + base + '.');
    }

    @NotNull
    public static final File relativeTo(@NotNull File $receiver, @NotNull File base) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(base, "base");
        return new File(FilesKt.toRelativeString($receiver, base));
    }

    @NotNull
    public static final File relativeToOrSelf(@NotNull File $receiver, @NotNull File base) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(base, "base");
        String p1 = toRelativeStringOrNull$FilesKt__UtilsKt($receiver, base);
        return p1 != null ? new File(p1) : $receiver;
    }

    @Nullable
    public static final File relativeToOrNull(@NotNull File $receiver, @NotNull File base) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(base, "base");
        String p1 = toRelativeStringOrNull$FilesKt__UtilsKt($receiver, base);
        if (p1 != null) {
            return new File(p1);
        }
        return null;
    }

    private static final String toRelativeStringOrNull$FilesKt__UtilsKt(@NotNull File $receiver, File base) {
        FilePathComponents thisComponents = normalize$FilesKt__UtilsKt(FilesKt.toComponents($receiver));
        FilePathComponents baseComponents = normalize$FilesKt__UtilsKt(FilesKt.toComponents(base));
        if (!Intrinsics.areEqual((Object) thisComponents.getRoot(), (Object) baseComponents.getRoot())) {
            return null;
        }
        int baseCount = baseComponents.getSize();
        int thisCount = thisComponents.getSize();
        File file = $receiver;
        int i = 0;
        int maxSameCount = Math.min(thisCount, baseCount);
        while (i < maxSameCount && Intrinsics.areEqual((Object) thisComponents.getSegments().get(i), (Object) baseComponents.getSegments().get(i))) {
            i++;
        }
        int sameCount = i;
        StringBuilder res = new StringBuilder();
        int i2 = baseCount - 1;
        if (i2 >= sameCount) {
            while (!Intrinsics.areEqual((Object) baseComponents.getSegments().get(i2).getName(), (Object) "..")) {
                res.append("..");
                if (i2 != sameCount) {
                    res.append(File.separatorChar);
                }
                if (i2 != sameCount) {
                    i2--;
                }
            }
            return null;
        }
        if (sameCount < thisCount) {
            if (sameCount < baseCount) {
                res.append(File.separatorChar);
            }
            String str = File.separator;
            Intrinsics.checkExpressionValueIsNotNull(str, "File.separator");
            CollectionsKt.joinTo$default(CollectionsKt.drop(thisComponents.getSegments(), sameCount), res, str, (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 124, (Object) null);
        }
        return res.toString();
    }

    @NotNull
    public static /* bridge */ /* synthetic */ File copyTo$default(File file, File file2, boolean z, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            z = false;
        }
        if ((i2 & 4) != 0) {
            i = 8192;
        }
        return FilesKt.copyTo(file, file2, z, i);
    }

    @NotNull
    public static final File copyTo(@NotNull File $receiver, @NotNull File target, boolean overwrite, int bufferSize) {
        Closeable fileOutputStream;
        Throwable th;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(target, "target");
        if ($receiver.exists()) {
            if (target.exists()) {
                boolean stillExists = true;
                if (overwrite && target.delete()) {
                    stillExists = false;
                }
                if (stillExists) {
                    throw new FileAlreadyExistsException($receiver, target, "The destination file already exists.");
                }
            }
            if (!$receiver.isDirectory()) {
                File parentFile = target.getParentFile();
                if (parentFile != null) {
                    parentFile.mkdirs();
                }
                Closeable fileInputStream = new FileInputStream($receiver);
                Throwable th2 = null;
                try {
                    FileInputStream input = (FileInputStream) fileInputStream;
                    fileOutputStream = new FileOutputStream(target);
                    Throwable th3 = null;
                    ByteStreamsKt.copyTo(input, (FileOutputStream) fileOutputStream, bufferSize);
                    CloseableKt.closeFinally(fileOutputStream, th3);
                    CloseableKt.closeFinally(fileInputStream, th2);
                } catch (Throwable th4) {
                    Throwable th5 = th4;
                    try {
                        throw th5;
                    } catch (Throwable th6) {
                        CloseableKt.closeFinally(fileInputStream, th5);
                        throw th6;
                    }
                }
            } else if (!target.mkdirs()) {
                throw new FileSystemException($receiver, target, "Failed to create target directory.");
            }
            return target;
        }
        throw new NoSuchFileException($receiver, (File) null, "The source file doesn't exist.", 2, (DefaultConstructorMarker) null);
    }

    public static /* bridge */ /* synthetic */ boolean copyRecursively$default(File file, File file2, boolean z, Function2 function2, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        if ((i & 4) != 0) {
            function2 = FilesKt__UtilsKt$copyRecursively$1.INSTANCE;
        }
        return FilesKt.copyRecursively(file, file2, z, function2);
    }

    public static final boolean copyRecursively(@NotNull File $receiver, @NotNull File target, boolean overwrite, @NotNull Function2<? super File, ? super IOException, ? extends OnErrorAction> onError) {
        boolean stillExists;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(target, "target");
        Intrinsics.checkParameterIsNotNull(onError, "onError");
        if ($receiver.exists()) {
            try {
                Iterator<File> it = FilesKt.walkTopDown($receiver).onFail(new FilesKt__UtilsKt$copyRecursively$2(onError)).iterator();
                while (it.hasNext()) {
                    File src = it.next();
                    if (src.exists()) {
                        File dstFile = new File(target, FilesKt.toRelativeString(src, $receiver));
                        if (dstFile.exists() && (!src.isDirectory() || !dstFile.isDirectory())) {
                            if (!overwrite) {
                                stillExists = true;
                            } else if (dstFile.isDirectory()) {
                                stillExists = !FilesKt.deleteRecursively(dstFile);
                            } else {
                                stillExists = !dstFile.delete();
                            }
                            if (stillExists) {
                                if (((OnErrorAction) onError.invoke(dstFile, new FileAlreadyExistsException(src, dstFile, "The destination file already exists."))) == OnErrorAction.TERMINATE) {
                                    return false;
                                }
                            }
                        }
                        if (src.isDirectory()) {
                            dstFile.mkdirs();
                        } else if (FilesKt.copyTo$default(src, dstFile, overwrite, 0, 4, (Object) null).length() != src.length() && ((OnErrorAction) onError.invoke(src, new IOException("Source file wasn't copied completely, length of destination file differs."))) == OnErrorAction.TERMINATE) {
                            return false;
                        }
                    } else if (((OnErrorAction) onError.invoke(src, new NoSuchFileException(src, (File) null, "The source file doesn't exist.", 2, (DefaultConstructorMarker) null))) == OnErrorAction.TERMINATE) {
                        return false;
                    }
                }
                return true;
            } catch (TerminateException e) {
                return false;
            }
        } else if (((OnErrorAction) onError.invoke($receiver, new NoSuchFileException($receiver, (File) null, "The source file doesn't exist.", 2, (DefaultConstructorMarker) null))) != OnErrorAction.TERMINATE) {
            return true;
        } else {
            return false;
        }
    }

    public static final boolean deleteRecursively(@NotNull File $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        boolean accumulator$iv = true;
        for (File it : FilesKt.walkBottomUp($receiver)) {
            accumulator$iv = (it.delete() || !it.exists()) && accumulator$iv;
        }
        return accumulator$iv;
    }

    public static final boolean startsWith(@NotNull File $receiver, @NotNull File other) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        FilePathComponents components = FilesKt.toComponents($receiver);
        FilePathComponents otherComponents = FilesKt.toComponents(other);
        if (!(!Intrinsics.areEqual((Object) components.getRoot(), (Object) otherComponents.getRoot())) && components.getSize() >= otherComponents.getSize()) {
            return components.getSegments().subList(0, otherComponents.getSize()).equals(otherComponents.getSegments());
        }
        return false;
    }

    public static final boolean startsWith(@NotNull File $receiver, @NotNull String other) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        return FilesKt.startsWith($receiver, new File(other));
    }

    public static final boolean endsWith(@NotNull File $receiver, @NotNull File other) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        FilePathComponents components = FilesKt.toComponents($receiver);
        FilePathComponents otherComponents = FilesKt.toComponents(other);
        if (otherComponents.isRooted()) {
            return Intrinsics.areEqual((Object) $receiver, (Object) other);
        }
        int shift = components.getSize() - otherComponents.getSize();
        if (shift < 0) {
            return false;
        }
        return components.getSegments().subList(shift, components.getSize()).equals(otherComponents.getSegments());
    }

    public static final boolean endsWith(@NotNull File $receiver, @NotNull String other) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        return FilesKt.endsWith($receiver, new File(other));
    }

    @NotNull
    public static final File normalize(@NotNull File $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        FilePathComponents $receiver2 = FilesKt.toComponents($receiver);
        File root = $receiver2.getRoot();
        String str = File.separator;
        Intrinsics.checkExpressionValueIsNotNull(str, "File.separator");
        return FilesKt.resolve(root, CollectionsKt.joinToString$default(normalize$FilesKt__UtilsKt((List<? extends File>) $receiver2.getSegments()), str, (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null));
    }

    private static final FilePathComponents normalize$FilesKt__UtilsKt(@NotNull FilePathComponents $receiver) {
        return new FilePathComponents($receiver.getRoot(), normalize$FilesKt__UtilsKt((List<? extends File>) $receiver.getSegments()));
    }

    private static final List<File> normalize$FilesKt__UtilsKt(@NotNull List<? extends File> $receiver) {
        List list = new ArrayList($receiver.size());
        for (File file : $receiver) {
            String name = file.getName();
            if (name != null) {
                int hashCode = name.hashCode();
                if (hashCode != 46) {
                    if (hashCode == 1472 && name.equals("..")) {
                        if (list.isEmpty() || !(!Intrinsics.areEqual((Object) ((File) CollectionsKt.last(list)).getName(), (Object) ".."))) {
                            list.add(file);
                        } else {
                            list.remove(list.size() - 1);
                        }
                    }
                } else if (name.equals(".")) {
                }
            }
            list.add(file);
        }
        return list;
    }

    @NotNull
    public static final File resolve(@NotNull File $receiver, @NotNull File relative) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(relative, "relative");
        if (FilesKt.isRooted(relative)) {
            return relative;
        }
        String baseName = $receiver.toString();
        Intrinsics.checkExpressionValueIsNotNull(baseName, "baseName");
        if ((baseName.length() == 0) || StringsKt.endsWith$default((CharSequence) baseName, File.separatorChar, false, 2, (Object) null)) {
            return new File(baseName + relative);
        }
        return new File(baseName + File.separatorChar + relative);
    }

    @NotNull
    public static final File resolve(@NotNull File $receiver, @NotNull String relative) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(relative, "relative");
        return FilesKt.resolve($receiver, new File(relative));
    }

    @NotNull
    public static final File resolveSibling(@NotNull File $receiver, @NotNull File relative) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(relative, "relative");
        FilePathComponents components = FilesKt.toComponents($receiver);
        return FilesKt.resolve(FilesKt.resolve(components.getRoot(), components.getSize() == 0 ? new File("..") : components.subPath(0, components.getSize() - 1)), relative);
    }

    @NotNull
    public static final File resolveSibling(@NotNull File $receiver, @NotNull String relative) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(relative, "relative");
        return FilesKt.resolveSibling($receiver, new File(relative));
    }
}

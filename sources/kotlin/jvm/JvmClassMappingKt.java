package kotlin.jvm;

import java.lang.annotation.Annotation;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.TypeCastException;
import kotlin.jvm.internal.ClassBasedDeclarationContainer;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000,\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u001b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0000\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\u0010\u0011\n\u0002\b\u0002\u001a\u001f\u0010\u0018\u001a\u00020\u0019\"\n\b\u0000\u0010\u0002\u0018\u0001*\u00020\r*\u0006\u0012\u0002\b\u00030\u001a¢\u0006\u0002\u0010\u001b\"'\u0010\u0000\u001a\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0001\"\b\b\u0000\u0010\u0002*\u00020\u0003*\u0002H\u00028F¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005\"0\u0010\u0006\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0007\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00018GX\u0004¢\u0006\f\u0012\u0004\b\b\u0010\t\u001a\u0004\b\n\u0010\u000b\"&\u0010\f\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0007\"\b\b\u0000\u0010\u0002*\u00020\r*\u0002H\u00028Æ\u0002¢\u0006\u0006\u001a\u0004\b\n\u0010\u000e\";\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00010\u0007\"\b\b\u0000\u0010\u0002*\u00020\r*\b\u0012\u0004\u0012\u0002H\u00020\u00018Ç\u0002X\u0004¢\u0006\f\u0012\u0004\b\u000f\u0010\t\u001a\u0004\b\u0010\u0010\u000b\"+\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0007\"\b\b\u0000\u0010\u0002*\u00020\r*\b\u0012\u0004\u0012\u0002H\u00020\u00018F¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u000b\"-\u0010\u0013\u001a\n\u0012\u0004\u0012\u0002H\u0002\u0018\u00010\u0007\"\b\b\u0000\u0010\u0002*\u00020\r*\b\u0012\u0004\u0012\u0002H\u00020\u00018F¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u000b\"+\u0010\u0015\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\b\b\u0000\u0010\u0002*\u00020\r*\b\u0012\u0004\u0012\u0002H\u00020\u00078G¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017¨\u0006\u001c"}, d2 = {"annotationClass", "Lkotlin/reflect/KClass;", "T", "", "getAnnotationClass", "(Ljava/lang/annotation/Annotation;)Lkotlin/reflect/KClass;", "java", "Ljava/lang/Class;", "java$annotations", "(Lkotlin/reflect/KClass;)V", "getJavaClass", "(Lkotlin/reflect/KClass;)Ljava/lang/Class;", "javaClass", "", "(Ljava/lang/Object;)Ljava/lang/Class;", "javaClass$annotations", "getRuntimeClassOfKClassInstance", "javaObjectType", "getJavaObjectType", "javaPrimitiveType", "getJavaPrimitiveType", "kotlin", "getKotlinClass", "(Ljava/lang/Class;)Lkotlin/reflect/KClass;", "isArrayOf", "", "", "([Ljava/lang/Object;)Z", "kotlin-stdlib"}, k = 2, mv = {1, 1, 11})
@JvmName(name = "JvmClassMappingKt")
/* compiled from: JvmClassMapping.kt */
public final class JvmClassMappingKt {
    public static /* synthetic */ void java$annotations(KClass kClass) {
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "Use 'java' property to get Java class corresponding to this Kotlin class or cast this instance to Any if you really want to get the runtime Java class of this implementation of KClass.", replaceWith = @ReplaceWith(expression = "(this as Any).javaClass", imports = {}))
    public static /* synthetic */ void javaClass$annotations(KClass kClass) {
    }

    @NotNull
    @JvmName(name = "getJavaClass")
    public static final <T> Class<T> getJavaClass(@NotNull KClass<T> $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Class<?> jClass = ((ClassBasedDeclarationContainer) $receiver).getJClass();
        if (jClass != null) {
            return jClass;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.Class<T>");
    }

    @Nullable
    public static final <T> Class<T> getJavaPrimitiveType(@NotNull KClass<T> $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Class thisJClass = ((ClassBasedDeclarationContainer) $receiver).getJClass();
        if (!thisJClass.isPrimitive()) {
            String name = thisJClass.getName();
            if (name != null) {
                switch (name.hashCode()) {
                    case -2056817302:
                        if (name.equals("java.lang.Integer")) {
                            return Integer.TYPE;
                        }
                        break;
                    case -527879800:
                        if (name.equals("java.lang.Float")) {
                            return Float.TYPE;
                        }
                        break;
                    case -515992664:
                        if (name.equals("java.lang.Short")) {
                            return Short.TYPE;
                        }
                        break;
                    case 155276373:
                        if (name.equals("java.lang.Character")) {
                            return Character.TYPE;
                        }
                        break;
                    case 344809556:
                        if (name.equals("java.lang.Boolean")) {
                            return Boolean.TYPE;
                        }
                        break;
                    case 398507100:
                        if (name.equals("java.lang.Byte")) {
                            return Byte.TYPE;
                        }
                        break;
                    case 398795216:
                        if (name.equals("java.lang.Long")) {
                            return Long.TYPE;
                        }
                        break;
                    case 399092968:
                        if (name.equals("java.lang.Void")) {
                            return Void.TYPE;
                        }
                        break;
                    case 761287205:
                        if (name.equals("java.lang.Double")) {
                            return Double.TYPE;
                        }
                        break;
                }
            }
            return null;
        } else if (thisJClass != null) {
            return thisJClass;
        } else {
            throw new TypeCastException("null cannot be cast to non-null type java.lang.Class<T>");
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    @org.jetbrains.annotations.NotNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final <T> java.lang.Class<T> getJavaObjectType(@org.jetbrains.annotations.NotNull kotlin.reflect.KClass<T> r4) {
        /*
            java.lang.String r0 = "$receiver"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r4, r0)
            r0 = r4
            kotlin.jvm.internal.ClassBasedDeclarationContainer r0 = (kotlin.jvm.internal.ClassBasedDeclarationContainer) r0
            java.lang.Class r0 = r0.getJClass()
            boolean r1 = r0.isPrimitive()
            java.lang.String r2 = "null cannot be cast to non-null type java.lang.Class<T>"
            if (r1 != 0) goto L_0x001d
            if (r0 == 0) goto L_0x0017
            return r0
        L_0x0017:
            kotlin.TypeCastException r1 = new kotlin.TypeCastException
            r1.<init>(r2)
            throw r1
        L_0x001d:
            java.lang.String r1 = r0.getName()
            if (r1 != 0) goto L_0x0024
            goto L_0x002b
        L_0x0024:
            int r3 = r1.hashCode()
            switch(r3) {
                case -1325958191: goto L_0x0085;
                case 104431: goto L_0x007a;
                case 3039496: goto L_0x006f;
                case 3052374: goto L_0x0064;
                case 3327612: goto L_0x0059;
                case 3625364: goto L_0x004e;
                case 64711720: goto L_0x0043;
                case 97526364: goto L_0x0038;
                case 109413500: goto L_0x002d;
                default: goto L_0x002b;
            }
        L_0x002b:
            goto L_0x0090
        L_0x002d:
            java.lang.String r3 = "short"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x002b
            java.lang.Class<java.lang.Short> r1 = java.lang.Short.class
            goto L_0x0091
        L_0x0038:
            java.lang.String r3 = "float"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x002b
            java.lang.Class<java.lang.Float> r1 = java.lang.Float.class
            goto L_0x0091
        L_0x0043:
            java.lang.String r3 = "boolean"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x002b
            java.lang.Class<java.lang.Boolean> r1 = java.lang.Boolean.class
            goto L_0x0091
        L_0x004e:
            java.lang.String r3 = "void"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x002b
            java.lang.Class<java.lang.Void> r1 = java.lang.Void.class
            goto L_0x0091
        L_0x0059:
            java.lang.String r3 = "long"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x002b
            java.lang.Class<java.lang.Long> r1 = java.lang.Long.class
            goto L_0x0091
        L_0x0064:
            java.lang.String r3 = "char"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x002b
            java.lang.Class<java.lang.Character> r1 = java.lang.Character.class
            goto L_0x0091
        L_0x006f:
            java.lang.String r3 = "byte"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x002b
            java.lang.Class<java.lang.Byte> r1 = java.lang.Byte.class
            goto L_0x0091
        L_0x007a:
            java.lang.String r3 = "int"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x002b
            java.lang.Class<java.lang.Integer> r1 = java.lang.Integer.class
            goto L_0x0091
        L_0x0085:
            java.lang.String r3 = "double"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x002b
            java.lang.Class<java.lang.Double> r1 = java.lang.Double.class
            goto L_0x0091
        L_0x0090:
            r1 = r0
        L_0x0091:
            if (r1 == 0) goto L_0x0094
            return r1
        L_0x0094:
            kotlin.TypeCastException r1 = new kotlin.TypeCastException
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.jvm.JvmClassMappingKt.getJavaObjectType(kotlin.reflect.KClass):java.lang.Class");
    }

    @NotNull
    @JvmName(name = "getKotlinClass")
    public static final <T> KClass<T> getKotlinClass(@NotNull Class<T> $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return Reflection.getOrCreateKotlinClass($receiver);
    }

    @NotNull
    public static final <T> Class<T> getJavaClass(@NotNull T $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Class<?> cls = $receiver.getClass();
        if (cls != null) {
            return cls;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.Class<T>");
    }

    @NotNull
    @JvmName(name = "getRuntimeClassOfKClassInstance")
    public static final <T> Class<KClass<T>> getRuntimeClassOfKClassInstance(@NotNull KClass<T> $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Class<?> cls = $receiver.getClass();
        if (cls != null) {
            return cls;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.Class<kotlin.reflect.KClass<T>>");
    }

    private static final <T> boolean isArrayOf(@NotNull Object[] $receiver) {
        Intrinsics.reifiedOperationMarker(4, "T");
        return Object.class.isAssignableFrom($receiver.getClass().getComponentType());
    }

    @NotNull
    public static final <T extends Annotation> KClass<? extends T> getAnnotationClass(@NotNull T $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Class<? extends Annotation> annotationType = $receiver.annotationType();
        Intrinsics.checkExpressionValueIsNotNull(annotationType, "(this as java.lang.annot…otation).annotationType()");
        KClass<? extends T> kotlinClass = getKotlinClass(annotationType);
        if (kotlinClass != null) {
            return kotlinClass;
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.reflect.KClass<out T>");
    }
}

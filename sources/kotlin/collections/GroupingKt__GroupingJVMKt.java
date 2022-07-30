package kotlin.collections;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.PublishedApi;
import kotlin.SinceKotlin;
import kotlin.TypeCastException;
import kotlin.internal.InlineOnly;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.TypeIntrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000&\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010&\n\u0000\u001a0\u0010\u0000\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u00020\u00030\u0001\"\u0004\b\u0000\u0010\u0004\"\u0004\b\u0001\u0010\u0002*\u000e\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u00020\u0005H\u0007\u001aW\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\b0\u0007\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\t\"\u0004\b\u0002\u0010\b*\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\t0\u00072\u001e\u0010\n\u001a\u001a\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\t0\f\u0012\u0004\u0012\u0002H\b0\u000bH\b¨\u0006\r"}, d2 = {"eachCount", "", "K", "", "T", "Lkotlin/collections/Grouping;", "mapValuesInPlace", "", "R", "V", "f", "Lkotlin/Function1;", "", "kotlin-stdlib"}, k = 5, mv = {1, 1, 11}, xi = 1, xs = "kotlin/collections/GroupingKt")
/* compiled from: GroupingJVM.kt */
class GroupingKt__GroupingJVMKt {
    @NotNull
    @SinceKotlin(version = "1.1")
    public static final <T, K> Map<K, Integer> eachCount(@NotNull Grouping<T, ? extends K> $receiver) {
        Object obj;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Map destination$iv = new LinkedHashMap();
        Grouping $receiver$iv = $receiver;
        Grouping $receiver$iv$iv = $receiver$iv;
        Iterator<T> sourceIterator = $receiver$iv$iv.sourceIterator();
        while (sourceIterator.hasNext()) {
            T next = sourceIterator.next();
            Object keyOf = $receiver$iv$iv.keyOf(next);
            Object accumulator$iv$iv = destination$iv.get(keyOf);
            Object obj2 = keyOf;
            Object acc$iv = accumulator$iv$iv;
            T t = next;
            if (accumulator$iv$iv == null && !destination$iv.containsKey(keyOf)) {
                Object obj3 = obj2;
                T t2 = t;
                obj = new Ref.IntRef();
            } else {
                obj = acc$iv;
            }
            Ref.IntRef acc = (Ref.IntRef) obj;
            Object obj4 = obj2;
            T t3 = t;
            acc.element++;
            destination$iv.put(keyOf, acc);
            Grouping<T, ? extends K> grouping = $receiver;
            $receiver$iv = $receiver$iv;
        }
        for (Map.Entry it : destination$iv.entrySet()) {
            if (it != null) {
                TypeIntrinsics.asMutableMapEntry(it).setValue(Integer.valueOf(((Ref.IntRef) it.getValue()).element));
            } else {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.MutableMap.MutableEntry<K, R>");
            }
        }
        return TypeIntrinsics.asMutableMap(destination$iv);
    }

    @PublishedApi
    @InlineOnly
    private static final <K, V, R> Map<K, R> mapValuesInPlace(@NotNull Map<K, V> $receiver, Function1<? super Map.Entry<? extends K, ? extends V>, ? extends R> f) {
        for (Map.Entry it : $receiver.entrySet()) {
            if (it != null) {
                TypeIntrinsics.asMutableMapEntry(it).setValue(f.invoke(it));
            } else {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.MutableMap.MutableEntry<K, R>");
            }
        }
        if ($receiver != null) {
            return TypeIntrinsics.asMutableMap($receiver);
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.MutableMap<K, R>");
    }
}

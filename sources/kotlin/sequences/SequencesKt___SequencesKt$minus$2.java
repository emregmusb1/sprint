package kotlin.sequences;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0011\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010(\n\u0000*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00028\u00000\u0001J\u000f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003H\u0002¨\u0006\u0004"}, d2 = {"kotlin/sequences/SequencesKt___SequencesKt$minus$2", "Lkotlin/sequences/Sequence;", "iterator", "", "kotlin-stdlib"}, k = 1, mv = {1, 1, 11})
/* compiled from: _Sequences.kt */
public final class SequencesKt___SequencesKt$minus$2 implements Sequence<T> {
    final /* synthetic */ Object[] $elements;
    final /* synthetic */ Sequence receiver$0;

    SequencesKt___SequencesKt$minus$2(Sequence<? extends T> $receiver, Object[] $captured_local_variable$1) {
        this.receiver$0 = $receiver;
        this.$elements = $captured_local_variable$1;
    }

    @NotNull
    public Iterator<T> iterator() {
        return SequencesKt.filterNot(this.receiver$0, new SequencesKt___SequencesKt$minus$2$iterator$1(ArraysKt.toHashSet((T[]) this.$elements))).iterator();
    }
}

package kotlin.sequences;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.experimental.Continuation;
import kotlin.coroutines.experimental.SequenceBuilder;
import kotlin.coroutines.experimental.jvm.internal.CoroutineImpl;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003*\b\u0012\u0004\u0012\u0002H\u00030\u0004H@ø\u0001\u0000¢\u0006\u0004\b\u0005\u0010\u0006"}, d2 = {"<anonymous>", "", "T", "R", "Lkotlin/coroutines/experimental/SequenceBuilder;", "invoke", "(Lkotlin/coroutines/experimental/SequenceBuilder;Lkotlin/coroutines/experimental/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 1, 11})
/* compiled from: _Sequences.kt */
final class SequencesKt___SequencesKt$zipWithNext$2 extends CoroutineImpl implements Function2<SequenceBuilder<? super R>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Function2 $transform;
    Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;
    private SequenceBuilder p$;
    final /* synthetic */ Sequence receiver$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SequencesKt___SequencesKt$zipWithNext$2(Sequence sequence, Function2 function2, Continuation continuation) {
        super(2, continuation);
        this.receiver$0 = sequence;
        this.$transform = function2;
    }

    @NotNull
    public final Continuation<Unit> create(@NotNull SequenceBuilder<? super R> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        Intrinsics.checkParameterIsNotNull(sequenceBuilder, "$receiver");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        SequencesKt___SequencesKt$zipWithNext$2 sequencesKt___SequencesKt$zipWithNext$2 = new SequencesKt___SequencesKt$zipWithNext$2(this.receiver$0, this.$transform, continuation);
        sequencesKt___SequencesKt$zipWithNext$2.p$ = sequenceBuilder;
        return sequencesKt___SequencesKt$zipWithNext$2;
    }

    @Nullable
    public final Object invoke(@NotNull SequenceBuilder<? super R> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        return ((SequencesKt___SequencesKt$zipWithNext$2) create(sequenceBuilder, continuation)).doResume(Unit.INSTANCE, (Throwable) null);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: java.util.Iterator} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x004b  */
    @org.jetbrains.annotations.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object doResume(@org.jetbrains.annotations.Nullable java.lang.Object r10, @org.jetbrains.annotations.Nullable java.lang.Throwable r11) {
        /*
            r9 = this;
            java.lang.Object r0 = kotlin.coroutines.experimental.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r1 = r9.label
            r2 = 1
            if (r1 == 0) goto L_0x0028
            if (r1 != r2) goto L_0x0020
            r1 = 0
            r3 = r1
            r4 = r1
            r5 = r9
            java.lang.Object r3 = r5.L$3
            java.lang.Object r4 = r5.L$2
            java.lang.Object r6 = r5.L$1
            r1 = r6
            java.util.Iterator r1 = (java.util.Iterator) r1
            java.lang.Object r6 = r5.L$0
            kotlin.coroutines.experimental.SequenceBuilder r6 = (kotlin.coroutines.experimental.SequenceBuilder) r6
            if (r11 != 0) goto L_0x001f
            goto L_0x0066
        L_0x001f:
            throw r11
        L_0x0020:
            java.lang.IllegalStateException r10 = new java.lang.IllegalStateException
            java.lang.String r11 = "call to 'resume' before 'invoke' with coroutine"
            r10.<init>(r11)
            throw r10
        L_0x0028:
            if (r11 != 0) goto L_0x006b
            r1 = r9
            kotlin.coroutines.experimental.SequenceBuilder r3 = r1.p$
            kotlin.sequences.Sequence r4 = r1.receiver$0
            java.util.Iterator r4 = r4.iterator()
            boolean r5 = r4.hasNext()
            if (r5 != 0) goto L_0x003c
            kotlin.Unit r0 = kotlin.Unit.INSTANCE
            return r0
        L_0x003c:
            java.lang.Object r5 = r4.next()
            r6 = r3
            r8 = r5
            r5 = r1
            r1 = r4
            r4 = r8
        L_0x0045:
            boolean r3 = r1.hasNext()
            if (r3 == 0) goto L_0x0068
            java.lang.Object r3 = r1.next()
            kotlin.jvm.functions.Function2 r7 = r5.$transform
            java.lang.Object r7 = r7.invoke(r4, r3)
            r5.L$0 = r6
            r5.L$1 = r1
            r5.L$2 = r4
            r5.L$3 = r3
            r5.label = r2
            java.lang.Object r7 = r6.yield(r7, r5)
            if (r7 != r0) goto L_0x0066
            return r0
        L_0x0066:
            r4 = r3
            goto L_0x0045
        L_0x0068:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE
            return r0
        L_0x006b:
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.sequences.SequencesKt___SequencesKt$zipWithNext$2.doResume(java.lang.Object, java.lang.Throwable):java.lang.Object");
    }
}

package kotlin.collections;

import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.experimental.Continuation;
import kotlin.coroutines.experimental.SequenceBuilder;
import kotlin.coroutines.experimental.jvm.internal.CoroutineImpl;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00040\u0003H@ø\u0001\u0000¢\u0006\u0004\b\u0005\u0010\u0006"}, d2 = {"<anonymous>", "", "T", "Lkotlin/coroutines/experimental/SequenceBuilder;", "", "invoke", "(Lkotlin/coroutines/experimental/SequenceBuilder;Lkotlin/coroutines/experimental/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 1, 11})
/* compiled from: SlidingWindow.kt */
final class SlidingWindowKt$windowedIterator$1 extends CoroutineImpl implements Function2<SequenceBuilder<? super List<? extends T>>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Iterator $iterator;
    final /* synthetic */ boolean $partialWindows;
    final /* synthetic */ boolean $reuseBuffer;
    final /* synthetic */ int $size;
    final /* synthetic */ int $step;
    int I$0;
    int I$1;
    Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;
    private SequenceBuilder p$;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SlidingWindowKt$windowedIterator$1(int i, int i2, Iterator it, boolean z, boolean z2, Continuation continuation) {
        super(2, continuation);
        this.$step = i;
        this.$size = i2;
        this.$iterator = it;
        this.$reuseBuffer = z;
        this.$partialWindows = z2;
    }

    @NotNull
    public final Continuation<Unit> create(@NotNull SequenceBuilder<? super List<? extends T>> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        Intrinsics.checkParameterIsNotNull(sequenceBuilder, "$receiver");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        SlidingWindowKt$windowedIterator$1 slidingWindowKt$windowedIterator$1 = new SlidingWindowKt$windowedIterator$1(this.$step, this.$size, this.$iterator, this.$reuseBuffer, this.$partialWindows, continuation);
        slidingWindowKt$windowedIterator$1.p$ = sequenceBuilder;
        return slidingWindowKt$windowedIterator$1;
    }

    @Nullable
    public final Object invoke(@NotNull SequenceBuilder<? super List<? extends T>> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        return ((SlidingWindowKt$windowedIterator$1) create(sequenceBuilder, continuation)).doResume(Unit.INSTANCE, (Throwable) null);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v13, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v10, resolved type: kotlin.collections.RingBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v16, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v22, resolved type: kotlin.collections.RingBuffer} */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions count limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x011b  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0149 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x019a  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01a7  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x01ed  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0125 A[SYNTHETIC] */
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object doResume(@org.jetbrains.annotations.Nullable java.lang.Object r19, @org.jetbrains.annotations.Nullable java.lang.Throwable r20) {
        /*
            r18 = this;
            java.lang.Object r0 = kotlin.coroutines.experimental.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            r1 = r18
            int r2 = r1.label
            r3 = 5
            r4 = 4
            r5 = 3
            r6 = 2
            r7 = 1
            if (r2 == 0) goto L_0x00c1
            r8 = 0
            r9 = 0
            if (r2 == r7) goto L_0x0092
            if (r2 == r6) goto L_0x0079
            if (r2 == r5) goto L_0x005b
            if (r2 == r4) goto L_0x003a
            if (r2 != r3) goto L_0x0032
            r0 = r9
            r2 = r8
            r3 = r18
            r4 = r19
            r5 = r20
            java.lang.Object r6 = r3.L$0
            r0 = r6
            kotlin.collections.RingBuffer r0 = (kotlin.collections.RingBuffer) r0
            int r2 = r3.I$0
            if (r5 != 0) goto L_0x0031
            r8 = r4
            r9 = r5
            r4 = r3
            goto L_0x01e8
        L_0x0031:
            throw r5
        L_0x0032:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r2 = "call to 'resume' before 'invoke' with coroutine"
            r0.<init>(r2)
            throw r0
        L_0x003a:
            r2 = r9
            r5 = r8
            r6 = r18
            r8 = r19
            r9 = r20
            java.lang.Object r10 = r6.L$1
            r2 = r10
            kotlin.collections.RingBuffer r2 = (kotlin.collections.RingBuffer) r2
            int r5 = r6.I$0
            java.lang.Object r10 = r6.L$0
            kotlin.coroutines.experimental.SequenceBuilder r10 = (kotlin.coroutines.experimental.SequenceBuilder) r10
            if (r9 != 0) goto L_0x005a
            r11 = r9
            r9 = r6
            r6 = r8
            r16 = r5
            r5 = r0
            r0 = r2
            r2 = r16
            goto L_0x01c8
        L_0x005a:
            throw r9
        L_0x005b:
            r2 = r9
            r6 = r9
            r9 = r18
            r10 = r19
            r11 = r20
            java.lang.Object r12 = r9.L$3
            java.util.Iterator r12 = (java.util.Iterator) r12
            java.lang.Object r2 = r9.L$2
            java.lang.Object r13 = r9.L$1
            r6 = r13
            kotlin.collections.RingBuffer r6 = (kotlin.collections.RingBuffer) r6
            int r8 = r9.I$0
            java.lang.Object r13 = r9.L$0
            kotlin.coroutines.experimental.SequenceBuilder r13 = (kotlin.coroutines.experimental.SequenceBuilder) r13
            if (r11 != 0) goto L_0x0078
            goto L_0x0190
        L_0x0078:
            throw r11
        L_0x0079:
            r0 = r8
            r2 = r9
            r3 = r8
            r4 = r18
            r5 = r19
            r6 = r20
            int r0 = r4.I$1
            java.lang.Object r7 = r4.L$0
            r2 = r7
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            int r3 = r4.I$0
            if (r6 != 0) goto L_0x0091
            r8 = r5
            r9 = r6
            goto L_0x014a
        L_0x0091:
            throw r6
        L_0x0092:
            r2 = r9
            r3 = r8
            r4 = r9
            r5 = r8
            r8 = r18
            r9 = r19
            r10 = r20
            java.lang.Object r11 = r8.L$3
            java.util.Iterator r11 = (java.util.Iterator) r11
            java.lang.Object r2 = r8.L$2
            int r3 = r8.I$1
            java.lang.Object r12 = r8.L$1
            r4 = r12
            java.util.ArrayList r4 = (java.util.ArrayList) r4
            int r5 = r8.I$0
            java.lang.Object r12 = r8.L$0
            kotlin.coroutines.experimental.SequenceBuilder r12 = (kotlin.coroutines.experimental.SequenceBuilder) r12
            if (r10 != 0) goto L_0x00c0
            r16 = r5
            r5 = r0
            r0 = r3
            r3 = r16
            r17 = r10
            r10 = r2
            r2 = r4
            r4 = r8
            r8 = r9
            r9 = r17
            goto L_0x0113
        L_0x00c0:
            throw r10
        L_0x00c1:
            if (r20 != 0) goto L_0x01f3
            r2 = r18
            r8 = r19
            r9 = r20
            kotlin.coroutines.experimental.SequenceBuilder r10 = r2.p$
            int r11 = r2.$step
            int r12 = r2.$size
            int r11 = r11 - r12
            if (r11 < 0) goto L_0x014c
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>(r12)
            r4 = 0
            java.util.Iterator r5 = r2.$iterator
            r12 = r10
            r16 = r5
            r5 = r0
            r0 = r4
            r4 = r2
            r2 = r3
            r3 = r11
            r11 = r16
        L_0x00e4:
            boolean r10 = r11.hasNext()
            if (r10 == 0) goto L_0x0125
            java.lang.Object r10 = r11.next()
            if (r0 <= 0) goto L_0x00f3
            int r0 = r0 + -1
            goto L_0x0124
        L_0x00f3:
            r2.add(r10)
            int r13 = r2.size()
            int r14 = r4.$size
            if (r13 != r14) goto L_0x0124
            r4.L$0 = r12
            r4.I$0 = r3
            r4.L$1 = r2
            r4.I$1 = r0
            r4.L$2 = r10
            r4.L$3 = r11
            r4.label = r7
            java.lang.Object r13 = r12.yield(r2, r4)
            if (r13 != r5) goto L_0x0113
            return r5
        L_0x0113:
            boolean r13 = r4.$reuseBuffer
            if (r13 == 0) goto L_0x011b
            r2.clear()
            goto L_0x0123
        L_0x011b:
            java.util.ArrayList r13 = new java.util.ArrayList
            int r14 = r4.$size
            r13.<init>(r14)
            r2 = r13
        L_0x0123:
            r0 = r3
        L_0x0124:
            goto L_0x00e4
        L_0x0125:
            r10 = r2
            java.util.Collection r10 = (java.util.Collection) r10
            boolean r10 = r10.isEmpty()
            r7 = r7 ^ r10
            if (r7 == 0) goto L_0x01f0
            boolean r7 = r4.$partialWindows
            if (r7 != 0) goto L_0x013b
            int r7 = r2.size()
            int r10 = r4.$size
            if (r7 != r10) goto L_0x01f0
        L_0x013b:
            r4.I$0 = r3
            r4.L$0 = r2
            r4.I$1 = r0
            r4.label = r6
            java.lang.Object r6 = r12.yield(r2, r4)
            if (r6 != r5) goto L_0x014a
            return r5
        L_0x014a:
            goto L_0x01f0
        L_0x014c:
            kotlin.collections.RingBuffer r6 = new kotlin.collections.RingBuffer
            r6.<init>(r12)
            java.util.Iterator r12 = r2.$iterator
            r13 = r10
            r10 = r8
            r8 = r11
            r11 = r9
            r9 = r2
        L_0x0158:
            boolean r2 = r12.hasNext()
            if (r2 == 0) goto L_0x0196
            java.lang.Object r2 = r12.next()
            r6.add(r2)
            boolean r14 = r6.isFull()
            if (r14 == 0) goto L_0x0195
            boolean r14 = r9.$reuseBuffer
            if (r14 == 0) goto L_0x0173
            r14 = r6
            java.util.List r14 = (java.util.List) r14
            goto L_0x017d
        L_0x0173:
            java.util.ArrayList r14 = new java.util.ArrayList
            r15 = r6
            java.util.Collection r15 = (java.util.Collection) r15
            r14.<init>(r15)
            java.util.List r14 = (java.util.List) r14
        L_0x017d:
            r9.L$0 = r13
            r9.I$0 = r8
            r9.L$1 = r6
            r9.L$2 = r2
            r9.L$3 = r12
            r9.label = r5
            java.lang.Object r14 = r13.yield(r14, r9)
            if (r14 != r0) goto L_0x0190
            return r0
        L_0x0190:
            int r14 = r9.$step
            r6.removeFirst(r14)
        L_0x0195:
            goto L_0x0158
        L_0x0196:
            boolean r2 = r9.$partialWindows
            if (r2 == 0) goto L_0x01ed
            r5 = r0
            r0 = r6
            r2 = r8
            r6 = r10
            r10 = r13
        L_0x019f:
            int r8 = r0.size()
            int r12 = r9.$step
            if (r8 <= r12) goto L_0x01ce
            boolean r8 = r9.$reuseBuffer
            if (r8 == 0) goto L_0x01af
            r8 = r0
            java.util.List r8 = (java.util.List) r8
            goto L_0x01b9
        L_0x01af:
            java.util.ArrayList r8 = new java.util.ArrayList
            r12 = r0
            java.util.Collection r12 = (java.util.Collection) r12
            r8.<init>(r12)
            java.util.List r8 = (java.util.List) r8
        L_0x01b9:
            r9.L$0 = r10
            r9.I$0 = r2
            r9.L$1 = r0
            r9.label = r4
            java.lang.Object r8 = r10.yield(r8, r9)
            if (r8 != r5) goto L_0x01c8
            return r5
        L_0x01c8:
            int r8 = r9.$step
            r0.removeFirst(r8)
            goto L_0x019f
        L_0x01ce:
            r4 = r0
            java.util.Collection r4 = (java.util.Collection) r4
            boolean r4 = r4.isEmpty()
            r4 = r4 ^ r7
            if (r4 == 0) goto L_0x01e9
            r9.I$0 = r2
            r9.L$0 = r0
            r9.label = r3
            java.lang.Object r3 = r10.yield(r0, r9)
            if (r3 != r5) goto L_0x01e5
            return r5
        L_0x01e5:
            r8 = r6
            r4 = r9
            r9 = r11
        L_0x01e8:
            goto L_0x01f0
        L_0x01e9:
            r8 = r6
            r4 = r9
            r9 = r11
            goto L_0x01f0
        L_0x01ed:
            r4 = r9
            r8 = r10
            r9 = r11
        L_0x01f0:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE
            return r0
        L_0x01f3:
            throw r20
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.collections.SlidingWindowKt$windowedIterator$1.doResume(java.lang.Object, java.lang.Throwable):java.lang.Object");
    }
}

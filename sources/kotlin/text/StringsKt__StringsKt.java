package kotlin.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ReplaceWith;
import kotlin.TuplesKt;
import kotlin.TypeCastException;
import kotlin.collections.ArraysKt;
import kotlin.collections.CharIterator;
import kotlin.collections.CollectionsKt;
import kotlin.internal.InlineOnly;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntProgression;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\r\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\f\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u001e\n\u0002\b\f\n\u0002\u0010\u0019\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0010\u0011\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u001b\u001a\u001c\u0010\t\u001a\u00020\n*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u00022\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u001c\u0010\u000e\u001a\u00020\n*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u00022\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u001f\u0010\u000f\u001a\u00020\r*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\f\u001a\u00020\rH\u0002\u001a\u001f\u0010\u000f\u001a\u00020\r*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u00022\b\b\u0002\u0010\f\u001a\u00020\rH\u0002\u001a\u0015\u0010\u000f\u001a\u00020\r*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u0013H\n\u001a\u001c\u0010\u0014\u001a\u00020\r*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u001c\u0010\u0014\u001a\u00020\r*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u00022\b\b\u0002\u0010\f\u001a\u00020\r\u001a:\u0010\u0016\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n\u0018\u00010\u0017*\u00020\u00022\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001aE\u0010\u0016\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n\u0018\u00010\u0017*\u00020\u00022\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\u00192\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u001b\u001a\u00020\rH\u0002¢\u0006\u0002\b\u001c\u001a:\u0010\u001d\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n\u0018\u00010\u0017*\u00020\u00022\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u0012\u0010\u001e\u001a\u00020\r*\u00020\u00022\u0006\u0010\u001f\u001a\u00020\u0006\u001a&\u0010 \u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a;\u0010 \u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u001b\u001a\u00020\rH\u0002¢\u0006\u0002\b\"\u001a&\u0010 \u001a\u00020\u0006*\u00020\u00022\u0006\u0010#\u001a\u00020\n2\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a&\u0010$\u001a\u00020\u0006*\u00020\u00022\u0006\u0010%\u001a\u00020&2\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a,\u0010$\u001a\u00020\u0006*\u00020\u00022\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a\r\u0010'\u001a\u00020\r*\u00020\u0002H\b\u001a\r\u0010(\u001a\u00020\r*\u00020\u0002H\b\u001a\r\u0010)\u001a\u00020\r*\u00020\u0002H\b\u001a \u0010*\u001a\u00020\r*\u0004\u0018\u00010\u0002H\b\u0002\u000e\n\f\b\u0000\u0012\u0002\u0018\u0001\u001a\u0004\b\u0003\u0010\u0000\u001a \u0010+\u001a\u00020\r*\u0004\u0018\u00010\u0002H\b\u0002\u000e\n\f\b\u0000\u0012\u0002\u0018\u0001\u001a\u0004\b\u0003\u0010\u0000\u001a\r\u0010,\u001a\u00020-*\u00020\u0002H\u0002\u001a&\u0010.\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a&\u0010.\u001a\u00020\u0006*\u00020\u00022\u0006\u0010#\u001a\u00020\n2\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a&\u0010/\u001a\u00020\u0006*\u00020\u00022\u0006\u0010%\u001a\u00020&2\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a,\u0010/\u001a\u00020\u0006*\u00020\u00022\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u0010\u00100\u001a\b\u0012\u0004\u0012\u00020\n01*\u00020\u0002\u001a\u0010\u00102\u001a\b\u0012\u0004\u0012\u00020\n03*\u00020\u0002\u001a\u0015\u00104\u001a\u00020\r*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u0013H\f\u001a\u000f\u00105\u001a\u00020\n*\u0004\u0018\u00010\nH\b\u001a\u001c\u00106\u001a\u00020\u0002*\u00020\u00022\u0006\u00107\u001a\u00020\u00062\b\b\u0002\u00108\u001a\u00020\u0011\u001a\u001c\u00106\u001a\u00020\n*\u00020\n2\u0006\u00107\u001a\u00020\u00062\b\b\u0002\u00108\u001a\u00020\u0011\u001a\u001c\u00109\u001a\u00020\u0002*\u00020\u00022\u0006\u00107\u001a\u00020\u00062\b\b\u0002\u00108\u001a\u00020\u0011\u001a\u001c\u00109\u001a\u00020\n*\u00020\n2\u0006\u00107\u001a\u00020\u00062\b\b\u0002\u00108\u001a\u00020\u0011\u001aG\u0010:\u001a\b\u0012\u0004\u0012\u00020\u000101*\u00020\u00022\u000e\u0010;\u001a\n\u0012\u0006\b\u0001\u0012\u00020\n0<2\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010=\u001a\u00020\u0006H\u0002¢\u0006\u0004\b>\u0010?\u001a=\u0010:\u001a\b\u0012\u0004\u0012\u00020\u000101*\u00020\u00022\u0006\u0010;\u001a\u00020&2\b\b\u0002\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010=\u001a\u00020\u0006H\u0002¢\u0006\u0002\b>\u001a4\u0010@\u001a\u00020\r*\u00020\u00022\u0006\u0010A\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\u00022\u0006\u0010B\u001a\u00020\u00062\u0006\u00107\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\rH\u0000\u001a\u0012\u0010C\u001a\u00020\u0002*\u00020\u00022\u0006\u0010D\u001a\u00020\u0002\u001a\u0012\u0010C\u001a\u00020\n*\u00020\n2\u0006\u0010D\u001a\u00020\u0002\u001a\u001a\u0010E\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u0006\u001a\u0012\u0010E\u001a\u00020\u0002*\u00020\u00022\u0006\u0010F\u001a\u00020\u0001\u001a\u001d\u0010E\u001a\u00020\n*\u00020\n2\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u0006H\b\u001a\u0015\u0010E\u001a\u00020\n*\u00020\n2\u0006\u0010F\u001a\u00020\u0001H\b\u001a\u0012\u0010G\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0002\u001a\u0012\u0010G\u001a\u00020\n*\u00020\n2\u0006\u0010\u0015\u001a\u00020\u0002\u001a\u0012\u0010H\u001a\u00020\u0002*\u00020\u00022\u0006\u0010I\u001a\u00020\u0002\u001a\u001a\u0010H\u001a\u00020\u0002*\u00020\u00022\u0006\u0010D\u001a\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0002\u001a\u0012\u0010H\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u0002\u001a\u001a\u0010H\u001a\u00020\n*\u00020\n2\u0006\u0010D\u001a\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0002\u001a+\u0010J\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00132\u0014\b\b\u0010K\u001a\u000e\u0012\u0004\u0012\u00020M\u0012\u0004\u0012\u00020\u00020LH\b\u001a\u001d\u0010J\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010N\u001a\u00020\nH\b\u001a$\u0010O\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010O\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010Q\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010Q\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010R\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010R\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010S\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a$\u0010S\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\u0006\u0010N\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001d\u0010T\u001a\u00020\n*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010N\u001a\u00020\nH\b\u001a\"\u0010U\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u00062\u0006\u0010N\u001a\u00020\u0002\u001a\u001a\u0010U\u001a\u00020\u0002*\u00020\u00022\u0006\u0010F\u001a\u00020\u00012\u0006\u0010N\u001a\u00020\u0002\u001a%\u0010U\u001a\u00020\n*\u00020\n2\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u0010!\u001a\u00020\u00062\u0006\u0010N\u001a\u00020\u0002H\b\u001a\u001d\u0010U\u001a\u00020\n*\u00020\n2\u0006\u0010F\u001a\u00020\u00012\u0006\u0010N\u001a\u00020\u0002H\b\u001a=\u0010V\u001a\b\u0012\u0004\u0012\u00020\n03*\u00020\u00022\u0012\u0010;\u001a\n\u0012\u0006\b\u0001\u0012\u00020\n0<\"\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010=\u001a\u00020\u0006¢\u0006\u0002\u0010W\u001a0\u0010V\u001a\b\u0012\u0004\u0012\u00020\n03*\u00020\u00022\n\u0010;\u001a\u00020&\"\u00020\u00112\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010=\u001a\u00020\u0006\u001a/\u0010V\u001a\b\u0012\u0004\u0012\u00020\n03*\u00020\u00022\u0006\u0010I\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010=\u001a\u00020\u0006H\u0002¢\u0006\u0002\bX\u001a%\u0010V\u001a\b\u0012\u0004\u0012\u00020\n03*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010=\u001a\u00020\u0006H\b\u001a=\u0010Y\u001a\b\u0012\u0004\u0012\u00020\n01*\u00020\u00022\u0012\u0010;\u001a\n\u0012\u0006\b\u0001\u0012\u00020\n0<\"\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010=\u001a\u00020\u0006¢\u0006\u0002\u0010Z\u001a0\u0010Y\u001a\b\u0012\u0004\u0012\u00020\n01*\u00020\u00022\n\u0010;\u001a\u00020&\"\u00020\u00112\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010=\u001a\u00020\u0006\u001a\u001c\u0010[\u001a\u00020\r*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u001c\u0010[\u001a\u00020\r*\u00020\u00022\u0006\u0010D\u001a\u00020\u00022\b\b\u0002\u0010\f\u001a\u00020\r\u001a$\u0010[\u001a\u00020\r*\u00020\u00022\u0006\u0010D\u001a\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r\u001a\u0012\u0010\\\u001a\u00020\u0002*\u00020\u00022\u0006\u0010F\u001a\u00020\u0001\u001a\u001d\u0010\\\u001a\u00020\u0002*\u00020\n2\u0006\u0010]\u001a\u00020\u00062\u0006\u0010^\u001a\u00020\u0006H\b\u001a\u001f\u0010_\u001a\u00020\n*\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u00062\b\b\u0002\u0010!\u001a\u00020\u0006H\b\u001a\u0012\u0010_\u001a\u00020\n*\u00020\u00022\u0006\u0010F\u001a\u00020\u0001\u001a\u0012\u0010_\u001a\u00020\n*\u00020\n2\u0006\u0010F\u001a\u00020\u0001\u001a\u001c\u0010`\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010`\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010a\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010a\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010b\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010b\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010c\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\u00112\b\b\u0002\u0010P\u001a\u00020\n\u001a\u001c\u0010c\u001a\u00020\n*\u00020\n2\u0006\u0010I\u001a\u00020\n2\b\b\u0002\u0010P\u001a\u00020\n\u001a\n\u0010d\u001a\u00020\u0002*\u00020\u0002\u001a!\u0010d\u001a\u00020\u0002*\u00020\u00022\u0012\u0010e\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\r0LH\b\u001a\u0016\u0010d\u001a\u00020\u0002*\u00020\u00022\n\u0010%\u001a\u00020&\"\u00020\u0011\u001a\r\u0010d\u001a\u00020\n*\u00020\nH\b\u001a!\u0010d\u001a\u00020\n*\u00020\n2\u0012\u0010e\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\r0LH\b\u001a\u0016\u0010d\u001a\u00020\n*\u00020\n2\n\u0010%\u001a\u00020&\"\u00020\u0011\u001a\n\u0010f\u001a\u00020\u0002*\u00020\u0002\u001a!\u0010f\u001a\u00020\u0002*\u00020\u00022\u0012\u0010e\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\r0LH\b\u001a\u0016\u0010f\u001a\u00020\u0002*\u00020\u00022\n\u0010%\u001a\u00020&\"\u00020\u0011\u001a\r\u0010f\u001a\u00020\n*\u00020\nH\b\u001a!\u0010f\u001a\u00020\n*\u00020\n2\u0012\u0010e\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\r0LH\b\u001a\u0016\u0010f\u001a\u00020\n*\u00020\n2\n\u0010%\u001a\u00020&\"\u00020\u0011\u001a\n\u0010g\u001a\u00020\u0002*\u00020\u0002\u001a!\u0010g\u001a\u00020\u0002*\u00020\u00022\u0012\u0010e\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\r0LH\b\u001a\u0016\u0010g\u001a\u00020\u0002*\u00020\u00022\n\u0010%\u001a\u00020&\"\u00020\u0011\u001a\r\u0010g\u001a\u00020\n*\u00020\nH\b\u001a!\u0010g\u001a\u00020\n*\u00020\n2\u0012\u0010e\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\r0LH\b\u001a\u0016\u0010g\u001a\u00020\n*\u00020\n2\n\u0010%\u001a\u00020&\"\u00020\u0011\"\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\"\u0015\u0010\u0005\u001a\u00020\u0006*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\b¨\u0006h"}, d2 = {"indices", "Lkotlin/ranges/IntRange;", "", "getIndices", "(Ljava/lang/CharSequence;)Lkotlin/ranges/IntRange;", "lastIndex", "", "getLastIndex", "(Ljava/lang/CharSequence;)I", "commonPrefixWith", "", "other", "ignoreCase", "", "commonSuffixWith", "contains", "char", "", "regex", "Lkotlin/text/Regex;", "endsWith", "suffix", "findAnyOf", "Lkotlin/Pair;", "strings", "", "startIndex", "last", "findAnyOf$StringsKt__StringsKt", "findLastAnyOf", "hasSurrogatePairAt", "index", "indexOf", "endIndex", "indexOf$StringsKt__StringsKt", "string", "indexOfAny", "chars", "", "isEmpty", "isNotBlank", "isNotEmpty", "isNullOrBlank", "isNullOrEmpty", "iterator", "Lkotlin/collections/CharIterator;", "lastIndexOf", "lastIndexOfAny", "lineSequence", "Lkotlin/sequences/Sequence;", "lines", "", "matches", "orEmpty", "padEnd", "length", "padChar", "padStart", "rangesDelimitedBy", "delimiters", "", "limit", "rangesDelimitedBy$StringsKt__StringsKt", "(Ljava/lang/CharSequence;[Ljava/lang/String;IZI)Lkotlin/sequences/Sequence;", "regionMatchesImpl", "thisOffset", "otherOffset", "removePrefix", "prefix", "removeRange", "range", "removeSuffix", "removeSurrounding", "delimiter", "replace", "transform", "Lkotlin/Function1;", "Lkotlin/text/MatchResult;", "replacement", "replaceAfter", "missingDelimiterValue", "replaceAfterLast", "replaceBefore", "replaceBeforeLast", "replaceFirst", "replaceRange", "split", "(Ljava/lang/CharSequence;[Ljava/lang/String;ZI)Ljava/util/List;", "split$StringsKt__StringsKt", "splitToSequence", "(Ljava/lang/CharSequence;[Ljava/lang/String;ZI)Lkotlin/sequences/Sequence;", "startsWith", "subSequence", "start", "end", "substring", "substringAfter", "substringAfterLast", "substringBefore", "substringBeforeLast", "trim", "predicate", "trimEnd", "trimStart", "kotlin-stdlib"}, k = 5, mv = {1, 1, 11}, xi = 1, xs = "kotlin/text/StringsKt")
/* compiled from: Strings.kt */
class StringsKt__StringsKt extends StringsKt__StringsJVMKt {
    @NotNull
    public static final CharSequence trim(@NotNull CharSequence $receiver, @NotNull Function1<? super Character, Boolean> predicate) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        int startIndex = 0;
        int endIndex = $receiver.length() - 1;
        boolean startFound = false;
        while (startIndex <= endIndex) {
            boolean match = predicate.invoke(Character.valueOf($receiver.charAt(!startFound ? startIndex : endIndex))).booleanValue();
            if (!startFound) {
                if (!match) {
                    startFound = true;
                } else {
                    startIndex++;
                }
            } else if (!match) {
                break;
            } else {
                endIndex--;
            }
        }
        return $receiver.subSequence(startIndex, endIndex + 1);
    }

    @NotNull
    public static final String trim(@NotNull String $receiver, @NotNull Function1<? super Character, Boolean> predicate) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        CharSequence $receiver$iv = $receiver;
        int startIndex$iv = 0;
        int endIndex$iv = $receiver$iv.length() - 1;
        boolean startFound$iv = false;
        while (startIndex$iv <= endIndex$iv) {
            boolean match$iv = predicate.invoke(Character.valueOf($receiver$iv.charAt(!startFound$iv ? startIndex$iv : endIndex$iv))).booleanValue();
            if (!startFound$iv) {
                if (!match$iv) {
                    startFound$iv = true;
                } else {
                    startIndex$iv++;
                }
            } else if (!match$iv) {
                break;
            } else {
                endIndex$iv--;
            }
        }
        return $receiver$iv.subSequence(startIndex$iv, endIndex$iv + 1).toString();
    }

    @NotNull
    public static final CharSequence trimStart(@NotNull CharSequence $receiver, @NotNull Function1<? super Character, Boolean> predicate) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        int length = $receiver.length();
        for (int index = 0; index < length; index++) {
            if (!predicate.invoke(Character.valueOf($receiver.charAt(index))).booleanValue()) {
                return $receiver.subSequence(index, $receiver.length());
            }
        }
        return "";
    }

    @NotNull
    public static final String trimStart(@NotNull String $receiver, @NotNull Function1<? super Character, Boolean> predicate) {
        CharSequence charSequence;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        CharSequence $receiver$iv = $receiver;
        int index$iv = 0;
        int length = $receiver$iv.length();
        while (true) {
            if (index$iv >= length) {
                charSequence = "";
                break;
            } else if (!predicate.invoke(Character.valueOf($receiver$iv.charAt(index$iv))).booleanValue()) {
                charSequence = $receiver$iv.subSequence(index$iv, $receiver$iv.length());
                break;
            } else {
                index$iv++;
            }
        }
        return charSequence.toString();
    }

    @NotNull
    public static final CharSequence trimEnd(@NotNull CharSequence $receiver, @NotNull Function1<? super Character, Boolean> predicate) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        int index = $receiver.length();
        do {
            index--;
            if (index < 0) {
                return "";
            }
        } while (predicate.invoke(Character.valueOf($receiver.charAt(index))).booleanValue());
        return $receiver.subSequence(0, index + 1);
    }

    @NotNull
    public static final String trimEnd(@NotNull String $receiver, @NotNull Function1<? super Character, Boolean> predicate) {
        CharSequence charSequence;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        CharSequence $receiver$iv = $receiver;
        int index$iv = $receiver$iv.length();
        while (true) {
            index$iv--;
            if (index$iv >= 0) {
                if (!predicate.invoke(Character.valueOf($receiver$iv.charAt(index$iv))).booleanValue()) {
                    charSequence = $receiver$iv.subSequence(0, index$iv + 1);
                    break;
                }
            } else {
                break;
            }
        }
        return charSequence.toString();
    }

    @NotNull
    public static final CharSequence trim(@NotNull CharSequence $receiver, @NotNull char... chars) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        CharSequence $receiver$iv = $receiver;
        int startIndex$iv = 0;
        int endIndex$iv = $receiver$iv.length() - 1;
        boolean startFound$iv = false;
        while (startIndex$iv <= endIndex$iv) {
            boolean match$iv = ArraysKt.contains(chars, $receiver$iv.charAt(!startFound$iv ? startIndex$iv : endIndex$iv));
            if (!startFound$iv) {
                if (!match$iv) {
                    startFound$iv = true;
                } else {
                    startIndex$iv++;
                }
            } else if (!match$iv) {
                break;
            } else {
                endIndex$iv--;
            }
        }
        return $receiver$iv.subSequence(startIndex$iv, endIndex$iv + 1);
    }

    @NotNull
    public static final String trim(@NotNull String $receiver, @NotNull char... chars) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        CharSequence $receiver$iv$iv = $receiver;
        int startIndex$iv$iv = 0;
        int endIndex$iv$iv = $receiver$iv$iv.length() - 1;
        boolean startFound$iv$iv = false;
        while (startIndex$iv$iv <= endIndex$iv$iv) {
            boolean match$iv$iv = ArraysKt.contains(chars, $receiver$iv$iv.charAt(!startFound$iv$iv ? startIndex$iv$iv : endIndex$iv$iv));
            if (!startFound$iv$iv) {
                if (!match$iv$iv) {
                    startFound$iv$iv = true;
                } else {
                    startIndex$iv$iv++;
                }
            } else if (!match$iv$iv) {
                break;
            } else {
                endIndex$iv$iv--;
            }
        }
        return $receiver$iv$iv.subSequence(startIndex$iv$iv, endIndex$iv$iv + 1).toString();
    }

    @NotNull
    public static final CharSequence trimStart(@NotNull CharSequence $receiver, @NotNull char... chars) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        CharSequence $receiver$iv = $receiver;
        int length = $receiver$iv.length();
        for (int index$iv = 0; index$iv < length; index$iv++) {
            if (ArraysKt.contains(chars, $receiver$iv.charAt(index$iv)) == 0) {
                return $receiver$iv.subSequence(index$iv, $receiver$iv.length());
            }
        }
        return "";
    }

    @NotNull
    public static final String trimStart(@NotNull String $receiver, @NotNull char... chars) {
        CharSequence charSequence;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        int index$iv$iv = 0;
        CharSequence $receiver$iv$iv = $receiver;
        int length = $receiver$iv$iv.length();
        while (true) {
            if (index$iv$iv >= length) {
                charSequence = "";
                break;
            } else if (ArraysKt.contains(chars, $receiver$iv$iv.charAt(index$iv$iv)) == 0) {
                charSequence = $receiver$iv$iv.subSequence(index$iv$iv, $receiver$iv$iv.length());
                break;
            } else {
                index$iv$iv++;
            }
        }
        return charSequence.toString();
    }

    @NotNull
    public static final CharSequence trimEnd(@NotNull CharSequence $receiver, @NotNull char... chars) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        CharSequence $receiver$iv = $receiver;
        for (int index$iv = $receiver$iv.length() - 1; index$iv >= 0; index$iv--) {
            if (ArraysKt.contains(chars, $receiver$iv.charAt(index$iv)) == 0) {
                return $receiver$iv.subSequence(0, index$iv + 1);
            }
        }
        return "";
    }

    @NotNull
    public static final String trimEnd(@NotNull String $receiver, @NotNull char... chars) {
        CharSequence charSequence;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        CharSequence $receiver$iv$iv = $receiver;
        int index$iv$iv = $receiver$iv$iv.length() - 1;
        while (true) {
            if (index$iv$iv < 0) {
                break;
            } else if (ArraysKt.contains(chars, $receiver$iv$iv.charAt(index$iv$iv)) == 0) {
                charSequence = $receiver$iv$iv.subSequence(0, index$iv$iv + 1);
                break;
            } else {
                index$iv$iv--;
            }
        }
        return charSequence.toString();
    }

    @NotNull
    public static final CharSequence trim(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        CharSequence $receiver$iv = $receiver;
        int startIndex$iv = 0;
        int endIndex$iv = $receiver$iv.length() - 1;
        boolean startFound$iv = false;
        while (startIndex$iv <= endIndex$iv) {
            char p1 = CharsKt.isWhitespace($receiver$iv.charAt(!startFound$iv ? startIndex$iv : endIndex$iv));
            if (!startFound$iv) {
                if (p1 == 0) {
                    startFound$iv = true;
                } else {
                    startIndex$iv++;
                }
            } else if (p1 == 0) {
                break;
            } else {
                endIndex$iv--;
            }
        }
        return $receiver$iv.subSequence(startIndex$iv, endIndex$iv + 1);
    }

    @InlineOnly
    private static final String trim(@NotNull String $receiver) {
        if ($receiver != null) {
            return StringsKt.trim((CharSequence) $receiver).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static final CharSequence trimStart(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        CharSequence $receiver$iv = $receiver;
        int length = $receiver$iv.length();
        for (int index$iv = 0; index$iv < length; index$iv++) {
            if (CharsKt.isWhitespace($receiver$iv.charAt(index$iv)) == 0) {
                return $receiver$iv.subSequence(index$iv, $receiver$iv.length());
            }
        }
        return "";
    }

    @InlineOnly
    private static final String trimStart(@NotNull String $receiver) {
        if ($receiver != null) {
            return StringsKt.trimStart((CharSequence) $receiver).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static final CharSequence trimEnd(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        CharSequence $receiver$iv = $receiver;
        for (int index$iv = $receiver$iv.length() - 1; index$iv >= 0; index$iv--) {
            if (CharsKt.isWhitespace($receiver$iv.charAt(index$iv)) == 0) {
                return $receiver$iv.subSequence(0, index$iv + 1);
            }
        }
        return "";
    }

    @InlineOnly
    private static final String trimEnd(@NotNull String $receiver) {
        if ($receiver != null) {
            return StringsKt.trimEnd((CharSequence) $receiver).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static /* bridge */ /* synthetic */ CharSequence padStart$default(CharSequence charSequence, int i, char c, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            c = ' ';
        }
        return StringsKt.padStart(charSequence, i, c);
    }

    @NotNull
    public static final CharSequence padStart(@NotNull CharSequence $receiver, int length, char padChar) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if (length < 0) {
            throw new IllegalArgumentException("Desired length " + length + " is less than zero.");
        } else if (length <= $receiver.length()) {
            return $receiver.subSequence(0, $receiver.length());
        } else {
            StringBuilder sb = new StringBuilder(length);
            int length2 = length - $receiver.length();
            int i = 1;
            if (1 <= length2) {
                while (true) {
                    sb.append(padChar);
                    if (i == length2) {
                        break;
                    }
                    i++;
                }
            }
            sb.append($receiver);
            return sb;
        }
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String padStart$default(String str, int i, char c, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            c = ' ';
        }
        return StringsKt.padStart(str, i, c);
    }

    @NotNull
    public static final String padStart(@NotNull String $receiver, int length, char padChar) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.padStart((CharSequence) $receiver, length, padChar).toString();
    }

    @NotNull
    public static /* bridge */ /* synthetic */ CharSequence padEnd$default(CharSequence charSequence, int i, char c, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            c = ' ';
        }
        return StringsKt.padEnd(charSequence, i, c);
    }

    @NotNull
    public static final CharSequence padEnd(@NotNull CharSequence $receiver, int length, char padChar) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if (length < 0) {
            throw new IllegalArgumentException("Desired length " + length + " is less than zero.");
        } else if (length <= $receiver.length()) {
            return $receiver.subSequence(0, $receiver.length());
        } else {
            StringBuilder sb = new StringBuilder(length);
            sb.append($receiver);
            int length2 = length - $receiver.length();
            int i = 1;
            if (1 <= length2) {
                while (true) {
                    sb.append(padChar);
                    if (i == length2) {
                        break;
                    }
                    i++;
                }
            }
            return sb;
        }
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String padEnd$default(String str, int i, char c, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            c = ' ';
        }
        return StringsKt.padEnd(str, i, c);
    }

    @NotNull
    public static final String padEnd(@NotNull String $receiver, int length, char padChar) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.padEnd((CharSequence) $receiver, length, padChar).toString();
    }

    @InlineOnly
    private static final boolean isNullOrEmpty(@Nullable CharSequence $receiver) {
        return $receiver == null || $receiver.length() == 0;
    }

    @InlineOnly
    private static final boolean isEmpty(@NotNull CharSequence $receiver) {
        return $receiver.length() == 0;
    }

    @InlineOnly
    private static final boolean isNotEmpty(@NotNull CharSequence $receiver) {
        return $receiver.length() > 0;
    }

    @InlineOnly
    private static final boolean isNotBlank(@NotNull CharSequence $receiver) {
        return !StringsKt.isBlank($receiver);
    }

    @InlineOnly
    private static final boolean isNullOrBlank(@Nullable CharSequence $receiver) {
        return $receiver == null || StringsKt.isBlank($receiver);
    }

    @NotNull
    public static final CharIterator iterator(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new StringsKt__StringsKt$iterator$1($receiver);
    }

    @InlineOnly
    private static final String orEmpty(@Nullable String $receiver) {
        return $receiver != null ? $receiver : "";
    }

    @NotNull
    public static final IntRange getIndices(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return new IntRange(0, $receiver.length() - 1);
    }

    public static final int getLastIndex(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return $receiver.length() - 1;
    }

    public static final boolean hasSurrogatePairAt(@NotNull CharSequence $receiver, int index) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return index >= 0 && $receiver.length() + -2 >= index && Character.isHighSurrogate($receiver.charAt(index)) && Character.isLowSurrogate($receiver.charAt(index + 1));
    }

    @NotNull
    public static final String substring(@NotNull String $receiver, @NotNull IntRange range) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(range, "range");
        String substring = $receiver.substring(range.getStart().intValue(), range.getEndInclusive().intValue() + 1);
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final CharSequence subSequence(@NotNull CharSequence $receiver, @NotNull IntRange range) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(range, "range");
        return $receiver.subSequence(range.getStart().intValue(), range.getEndInclusive().intValue() + 1);
    }

    @Deprecated(message = "Use parameters named startIndex and endIndex.", replaceWith = @ReplaceWith(expression = "subSequence(startIndex = start, endIndex = end)", imports = {}))
    @InlineOnly
    private static final CharSequence subSequence(@NotNull String $receiver, int start, int end) {
        return $receiver.subSequence(start, end);
    }

    @InlineOnly
    private static final String substring(@NotNull CharSequence $receiver, int startIndex, int endIndex) {
        return $receiver.subSequence(startIndex, endIndex).toString();
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ String substring$default(CharSequence $receiver, int startIndex, int endIndex, int i, Object obj) {
        if ((i & 2) != 0) {
            endIndex = $receiver.length();
        }
        return $receiver.subSequence(startIndex, endIndex).toString();
    }

    @NotNull
    public static final String substring(@NotNull CharSequence $receiver, @NotNull IntRange range) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(range, "range");
        return $receiver.subSequence(range.getStart().intValue(), range.getEndInclusive().intValue() + 1).toString();
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String substringBefore$default(String str, char c, String str2, int i, Object obj) {
        if ((i & 2) != 0) {
            str2 = str;
        }
        return StringsKt.substringBefore(str, c, str2);
    }

    @NotNull
    public static final String substringBefore(@NotNull String $receiver, char delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(0, index);
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String substringBefore$default(String str, String str2, String str3, int i, Object obj) {
        if ((i & 2) != 0) {
            str3 = str;
        }
        return StringsKt.substringBefore(str, str2, str3);
    }

    @NotNull
    public static final String substringBefore(@NotNull String $receiver, @NotNull String delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(0, index);
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String substringAfter$default(String str, char c, String str2, int i, Object obj) {
        if ((i & 2) != 0) {
            str2 = str;
        }
        return StringsKt.substringAfter(str, c, str2);
    }

    @NotNull
    public static final String substringAfter(@NotNull String $receiver, char delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(index + 1, $receiver.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String substringAfter$default(String str, String str2, String str3, int i, Object obj) {
        if ((i & 2) != 0) {
            str3 = str;
        }
        return StringsKt.substringAfter(str, str2, str3);
    }

    @NotNull
    public static final String substringAfter(@NotNull String $receiver, @NotNull String delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(delimiter.length() + index, $receiver.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String substringBeforeLast$default(String str, char c, String str2, int i, Object obj) {
        if ((i & 2) != 0) {
            str2 = str;
        }
        return StringsKt.substringBeforeLast(str, c, str2);
    }

    @NotNull
    public static final String substringBeforeLast(@NotNull String $receiver, char delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(0, index);
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String substringBeforeLast$default(String str, String str2, String str3, int i, Object obj) {
        if ((i & 2) != 0) {
            str3 = str;
        }
        return StringsKt.substringBeforeLast(str, str2, str3);
    }

    @NotNull
    public static final String substringBeforeLast(@NotNull String $receiver, @NotNull String delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(0, index);
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String substringAfterLast$default(String str, char c, String str2, int i, Object obj) {
        if ((i & 2) != 0) {
            str2 = str;
        }
        return StringsKt.substringAfterLast(str, c, str2);
    }

    @NotNull
    public static final String substringAfterLast(@NotNull String $receiver, char delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(index + 1, $receiver.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String substringAfterLast$default(String str, String str2, String str3, int i, Object obj) {
        if ((i & 2) != 0) {
            str3 = str;
        }
        return StringsKt.substringAfterLast(str, str2, str3);
    }

    @NotNull
    public static final String substringAfterLast(@NotNull String $receiver, @NotNull String delimiter, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        String substring = $receiver.substring(delimiter.length() + index, $receiver.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final CharSequence replaceRange(@NotNull CharSequence $receiver, int startIndex, int endIndex, @NotNull CharSequence replacement) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        if (endIndex >= startIndex) {
            StringBuilder sb = new StringBuilder();
            sb.append($receiver, 0, startIndex);
            sb.append(replacement);
            sb.append($receiver, endIndex, $receiver.length());
            return sb;
        }
        throw new IndexOutOfBoundsException("End index (" + endIndex + ") is less than start index (" + startIndex + ").");
    }

    @InlineOnly
    private static final String replaceRange(@NotNull String $receiver, int startIndex, int endIndex, CharSequence replacement) {
        if ($receiver != null) {
            return StringsKt.replaceRange((CharSequence) $receiver, startIndex, endIndex, replacement).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static final CharSequence replaceRange(@NotNull CharSequence $receiver, @NotNull IntRange range, @NotNull CharSequence replacement) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(range, "range");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        return StringsKt.replaceRange($receiver, range.getStart().intValue(), range.getEndInclusive().intValue() + 1, replacement);
    }

    @InlineOnly
    private static final String replaceRange(@NotNull String $receiver, IntRange range, CharSequence replacement) {
        if ($receiver != null) {
            return StringsKt.replaceRange((CharSequence) $receiver, range, replacement).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static final CharSequence removeRange(@NotNull CharSequence $receiver, int startIndex, int endIndex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if (endIndex < startIndex) {
            throw new IndexOutOfBoundsException("End index (" + endIndex + ") is less than start index (" + startIndex + ").");
        } else if (endIndex == startIndex) {
            return $receiver.subSequence(0, $receiver.length());
        } else {
            StringBuilder sb = new StringBuilder($receiver.length() - (endIndex - startIndex));
            sb.append($receiver, 0, startIndex);
            sb.append($receiver, endIndex, $receiver.length());
            return sb;
        }
    }

    @InlineOnly
    private static final String removeRange(@NotNull String $receiver, int startIndex, int endIndex) {
        if ($receiver != null) {
            return StringsKt.removeRange((CharSequence) $receiver, startIndex, endIndex).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static final CharSequence removeRange(@NotNull CharSequence $receiver, @NotNull IntRange range) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(range, "range");
        return StringsKt.removeRange($receiver, range.getStart().intValue(), range.getEndInclusive().intValue() + 1);
    }

    @InlineOnly
    private static final String removeRange(@NotNull String $receiver, IntRange range) {
        if ($receiver != null) {
            return StringsKt.removeRange((CharSequence) $receiver, range).toString();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
    }

    @NotNull
    public static final CharSequence removePrefix(@NotNull CharSequence $receiver, @NotNull CharSequence prefix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        if (StringsKt.startsWith$default($receiver, prefix, false, 2, (Object) null)) {
            return $receiver.subSequence(prefix.length(), $receiver.length());
        }
        return $receiver.subSequence(0, $receiver.length());
    }

    @NotNull
    public static final String removePrefix(@NotNull String $receiver, @NotNull CharSequence prefix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        if (!StringsKt.startsWith$default((CharSequence) $receiver, prefix, false, 2, (Object) null)) {
            return $receiver;
        }
        String substring = $receiver.substring(prefix.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.String).substring(startIndex)");
        return substring;
    }

    @NotNull
    public static final CharSequence removeSuffix(@NotNull CharSequence $receiver, @NotNull CharSequence suffix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(suffix, "suffix");
        if (StringsKt.endsWith$default($receiver, suffix, false, 2, (Object) null)) {
            return $receiver.subSequence(0, $receiver.length() - suffix.length());
        }
        return $receiver.subSequence(0, $receiver.length());
    }

    @NotNull
    public static final String removeSuffix(@NotNull String $receiver, @NotNull CharSequence suffix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(suffix, "suffix");
        if (!StringsKt.endsWith$default((CharSequence) $receiver, suffix, false, 2, (Object) null)) {
            return $receiver;
        }
        String substring = $receiver.substring(0, $receiver.length() - suffix.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final CharSequence removeSurrounding(@NotNull CharSequence $receiver, @NotNull CharSequence prefix, @NotNull CharSequence suffix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        Intrinsics.checkParameterIsNotNull(suffix, "suffix");
        if ($receiver.length() < prefix.length() + suffix.length() || !StringsKt.startsWith$default($receiver, prefix, false, 2, (Object) null) || !StringsKt.endsWith$default($receiver, suffix, false, 2, (Object) null)) {
            return $receiver.subSequence(0, $receiver.length());
        }
        return $receiver.subSequence(prefix.length(), $receiver.length() - suffix.length());
    }

    @NotNull
    public static final String removeSurrounding(@NotNull String $receiver, @NotNull CharSequence prefix, @NotNull CharSequence suffix) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        Intrinsics.checkParameterIsNotNull(suffix, "suffix");
        if ($receiver.length() < prefix.length() + suffix.length() || !StringsKt.startsWith$default((CharSequence) $receiver, prefix, false, 2, (Object) null) || !StringsKt.endsWith$default((CharSequence) $receiver, suffix, false, 2, (Object) null)) {
            return $receiver;
        }
        String substring = $receiver.substring(prefix.length(), $receiver.length() - suffix.length());
        Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
        return substring;
    }

    @NotNull
    public static final CharSequence removeSurrounding(@NotNull CharSequence $receiver, @NotNull CharSequence delimiter) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        return StringsKt.removeSurrounding($receiver, delimiter, delimiter);
    }

    @NotNull
    public static final String removeSurrounding(@NotNull String $receiver, @NotNull CharSequence delimiter) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        return StringsKt.removeSurrounding($receiver, delimiter, delimiter);
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceBefore$default(String str, char c, String str2, String str3, int i, Object obj) {
        if ((i & 4) != 0) {
            str3 = str;
        }
        return StringsKt.replaceBefore(str, c, str2, str3);
    }

    @NotNull
    public static final String replaceBefore(@NotNull String $receiver, char delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        return index == -1 ? missingDelimiterValue : StringsKt.replaceRange((CharSequence) $receiver, 0, index, (CharSequence) replacement).toString();
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceBefore$default(String str, String str2, String str3, String str4, int i, Object obj) {
        if ((i & 4) != 0) {
            str4 = str;
        }
        return StringsKt.replaceBefore(str, str2, str3, str4);
    }

    @NotNull
    public static final String replaceBefore(@NotNull String $receiver, @NotNull String delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        return index == -1 ? missingDelimiterValue : StringsKt.replaceRange((CharSequence) $receiver, 0, index, (CharSequence) replacement).toString();
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceAfter$default(String str, char c, String str2, String str3, int i, Object obj) {
        if ((i & 4) != 0) {
            str3 = str;
        }
        return StringsKt.replaceAfter(str, c, str2, str3);
    }

    @NotNull
    public static final String replaceAfter(@NotNull String $receiver, char delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        return StringsKt.replaceRange((CharSequence) $receiver, index + 1, $receiver.length(), (CharSequence) replacement).toString();
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceAfter$default(String str, String str2, String str3, String str4, int i, Object obj) {
        if ((i & 4) != 0) {
            str4 = str;
        }
        return StringsKt.replaceAfter(str, str2, str3, str4);
    }

    @NotNull
    public static final String replaceAfter(@NotNull String $receiver, @NotNull String delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.indexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        return StringsKt.replaceRange((CharSequence) $receiver, delimiter.length() + index, $receiver.length(), (CharSequence) replacement).toString();
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceAfterLast$default(String str, String str2, String str3, String str4, int i, Object obj) {
        if ((i & 4) != 0) {
            str4 = str;
        }
        return StringsKt.replaceAfterLast(str, str2, str3, str4);
    }

    @NotNull
    public static final String replaceAfterLast(@NotNull String $receiver, @NotNull String delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        return StringsKt.replaceRange((CharSequence) $receiver, delimiter.length() + index, $receiver.length(), (CharSequence) replacement).toString();
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceAfterLast$default(String str, char c, String str2, String str3, int i, Object obj) {
        if ((i & 4) != 0) {
            str3 = str;
        }
        return StringsKt.replaceAfterLast(str, c, str2, str3);
    }

    @NotNull
    public static final String replaceAfterLast(@NotNull String $receiver, char delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        if (index == -1) {
            return missingDelimiterValue;
        }
        return StringsKt.replaceRange((CharSequence) $receiver, index + 1, $receiver.length(), (CharSequence) replacement).toString();
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceBeforeLast$default(String str, char c, String str2, String str3, int i, Object obj) {
        if ((i & 4) != 0) {
            str3 = str;
        }
        return StringsKt.replaceBeforeLast(str, c, str2, str3);
    }

    @NotNull
    public static final String replaceBeforeLast(@NotNull String $receiver, char delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        return index == -1 ? missingDelimiterValue : StringsKt.replaceRange((CharSequence) $receiver, 0, index, (CharSequence) replacement).toString();
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String replaceBeforeLast$default(String str, String str2, String str3, String str4, int i, Object obj) {
        if ((i & 4) != 0) {
            str4 = str;
        }
        return StringsKt.replaceBeforeLast(str, str2, str3, str4);
    }

    @NotNull
    public static final String replaceBeforeLast(@NotNull String $receiver, @NotNull String delimiter, @NotNull String replacement, @NotNull String missingDelimiterValue) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiter, "delimiter");
        Intrinsics.checkParameterIsNotNull(replacement, "replacement");
        Intrinsics.checkParameterIsNotNull(missingDelimiterValue, "missingDelimiterValue");
        int index = StringsKt.lastIndexOf$default((CharSequence) $receiver, delimiter, 0, false, 6, (Object) null);
        return index == -1 ? missingDelimiterValue : StringsKt.replaceRange((CharSequence) $receiver, 0, index, (CharSequence) replacement).toString();
    }

    @InlineOnly
    private static final String replace(@NotNull CharSequence $receiver, Regex regex, String replacement) {
        return regex.replace($receiver, replacement);
    }

    @InlineOnly
    private static final String replace(@NotNull CharSequence $receiver, Regex regex, Function1<? super MatchResult, ? extends CharSequence> transform) {
        return regex.replace($receiver, transform);
    }

    @InlineOnly
    private static final String replaceFirst(@NotNull CharSequence $receiver, Regex regex, String replacement) {
        return regex.replaceFirst($receiver, replacement);
    }

    @InlineOnly
    private static final boolean matches(@NotNull CharSequence $receiver, Regex regex) {
        return regex.matches($receiver);
    }

    public static final boolean regionMatchesImpl(@NotNull CharSequence $receiver, int thisOffset, @NotNull CharSequence other, int otherOffset, int length, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        if (otherOffset < 0 || thisOffset < 0 || thisOffset > $receiver.length() - length || otherOffset > other.length() - length) {
            return false;
        }
        for (int index = 0; index < length; index++) {
            if (!CharsKt.equals($receiver.charAt(thisOffset + index), other.charAt(otherOffset + index), ignoreCase)) {
                return false;
            }
        }
        return true;
    }

    public static /* bridge */ /* synthetic */ boolean startsWith$default(CharSequence charSequence, char c, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.startsWith(charSequence, c, z);
    }

    public static final boolean startsWith(@NotNull CharSequence $receiver, char c, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return $receiver.length() > 0 && CharsKt.equals($receiver.charAt(0), c, ignoreCase);
    }

    public static /* bridge */ /* synthetic */ boolean endsWith$default(CharSequence charSequence, char c, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.endsWith(charSequence, c, z);
    }

    public static final boolean endsWith(@NotNull CharSequence $receiver, char c, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return $receiver.length() > 0 && CharsKt.equals($receiver.charAt(StringsKt.getLastIndex($receiver)), c, ignoreCase);
    }

    public static /* bridge */ /* synthetic */ boolean startsWith$default(CharSequence charSequence, CharSequence charSequence2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.startsWith(charSequence, charSequence2, z);
    }

    public static final boolean startsWith(@NotNull CharSequence $receiver, @NotNull CharSequence prefix, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        if (!ignoreCase && ($receiver instanceof String) && (prefix instanceof String)) {
            return StringsKt.startsWith$default((String) $receiver, (String) prefix, false, 2, (Object) null);
        }
        return StringsKt.regionMatchesImpl($receiver, 0, prefix, 0, prefix.length(), ignoreCase);
    }

    public static /* bridge */ /* synthetic */ boolean startsWith$default(CharSequence charSequence, CharSequence charSequence2, int i, boolean z, int i2, Object obj) {
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.startsWith(charSequence, charSequence2, i, z);
    }

    public static final boolean startsWith(@NotNull CharSequence $receiver, @NotNull CharSequence prefix, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        if (!ignoreCase && ($receiver instanceof String) && (prefix instanceof String)) {
            return StringsKt.startsWith$default((String) $receiver, (String) prefix, startIndex, false, 4, (Object) null);
        }
        return StringsKt.regionMatchesImpl($receiver, startIndex, prefix, 0, prefix.length(), ignoreCase);
    }

    public static /* bridge */ /* synthetic */ boolean endsWith$default(CharSequence charSequence, CharSequence charSequence2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.endsWith(charSequence, charSequence2, z);
    }

    public static final boolean endsWith(@NotNull CharSequence $receiver, @NotNull CharSequence suffix, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(suffix, "suffix");
        if (!ignoreCase && ($receiver instanceof String) && (suffix instanceof String)) {
            return StringsKt.endsWith$default((String) $receiver, (String) suffix, false, 2, (Object) null);
        }
        return StringsKt.regionMatchesImpl($receiver, $receiver.length() - suffix.length(), suffix, 0, suffix.length(), ignoreCase);
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String commonPrefixWith$default(CharSequence charSequence, CharSequence charSequence2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.commonPrefixWith(charSequence, charSequence2, z);
    }

    @NotNull
    public static final String commonPrefixWith(@NotNull CharSequence $receiver, @NotNull CharSequence other, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        int shortestLength = Math.min($receiver.length(), other.length());
        int i = 0;
        while (i < shortestLength && CharsKt.equals($receiver.charAt(i), other.charAt(i), ignoreCase)) {
            i++;
        }
        if (StringsKt.hasSurrogatePairAt($receiver, i - 1) || StringsKt.hasSurrogatePairAt(other, i - 1)) {
            i--;
        }
        return $receiver.subSequence(0, i).toString();
    }

    @NotNull
    public static /* bridge */ /* synthetic */ String commonSuffixWith$default(CharSequence charSequence, CharSequence charSequence2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.commonSuffixWith(charSequence, charSequence2, z);
    }

    @NotNull
    public static final String commonSuffixWith(@NotNull CharSequence $receiver, @NotNull CharSequence other, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        int thisLength = $receiver.length();
        int otherLength = other.length();
        int shortestLength = Math.min(thisLength, otherLength);
        int i = 0;
        while (i < shortestLength && CharsKt.equals($receiver.charAt((thisLength - i) - 1), other.charAt((otherLength - i) - 1), ignoreCase)) {
            i++;
        }
        if (StringsKt.hasSurrogatePairAt($receiver, (thisLength - i) - 1) || StringsKt.hasSurrogatePairAt(other, (otherLength - i) - 1)) {
            i--;
        }
        return $receiver.subSequence(thisLength - i, thisLength).toString();
    }

    public static /* bridge */ /* synthetic */ int indexOfAny$default(CharSequence charSequence, char[] cArr, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.indexOfAny(charSequence, cArr, i, z);
    }

    public static final int indexOfAny(@NotNull CharSequence $receiver, @NotNull char[] chars, int startIndex, boolean ignoreCase) {
        boolean z;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        if (ignoreCase || chars.length != 1 || !($receiver instanceof String)) {
            int index = RangesKt.coerceAtLeast(startIndex, 0);
            int lastIndex = StringsKt.getLastIndex($receiver);
            if (index > lastIndex) {
                return -1;
            }
            boolean z2 = false;
            while (true) {
                char charAtIndex = $receiver.charAt(index);
                char[] $receiver$iv = chars;
                int length = $receiver$iv.length;
                boolean z3 = z2;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        z = false;
                        break;
                    } else if (CharsKt.equals($receiver$iv[i], charAtIndex, ignoreCase) != 0) {
                        z = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (z) {
                    return index;
                }
                if (index == lastIndex) {
                    return -1;
                }
                index++;
                z2 = z3;
            }
        } else {
            return ((String) $receiver).indexOf(ArraysKt.single(chars), startIndex);
        }
    }

    public static /* bridge */ /* synthetic */ int lastIndexOfAny$default(CharSequence charSequence, char[] cArr, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = StringsKt.getLastIndex(charSequence);
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.lastIndexOfAny(charSequence, cArr, i, z);
    }

    public static final int lastIndexOfAny(@NotNull CharSequence $receiver, @NotNull char[] chars, int startIndex, boolean ignoreCase) {
        boolean z;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(chars, "chars");
        if (ignoreCase || chars.length != 1 || !($receiver instanceof String)) {
            int index = RangesKt.coerceAtMost(startIndex, StringsKt.getLastIndex($receiver));
            boolean z2 = false;
            while (index >= 0) {
                char charAtIndex = $receiver.charAt(index);
                char[] $receiver$iv = chars;
                int length = $receiver$iv.length;
                boolean z3 = z2;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        z = false;
                        break;
                    } else if (CharsKt.equals($receiver$iv[i], charAtIndex, ignoreCase) != 0) {
                        z = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (z) {
                    return index;
                }
                index--;
                z2 = z3;
            }
            return -1;
        }
        return ((String) $receiver).lastIndexOf(ArraysKt.single(chars), startIndex);
    }

    static /* bridge */ /* synthetic */ int indexOf$StringsKt__StringsKt$default(CharSequence charSequence, CharSequence charSequence2, int i, int i2, boolean z, boolean z2, int i3, Object obj) {
        return indexOf$StringsKt__StringsKt(charSequence, charSequence2, i, i2, z, (i3 & 16) != 0 ? false : z2);
    }

    private static final int indexOf$StringsKt__StringsKt(@NotNull CharSequence $receiver, CharSequence other, int startIndex, int endIndex, boolean ignoreCase, boolean last) {
        IntProgression intProgression;
        if (!last) {
            intProgression = new IntRange(RangesKt.coerceAtLeast(startIndex, 0), RangesKt.coerceAtMost(endIndex, $receiver.length()));
        } else {
            intProgression = RangesKt.downTo(RangesKt.coerceAtMost(startIndex, StringsKt.getLastIndex($receiver)), RangesKt.coerceAtLeast(endIndex, 0));
        }
        IntProgression indices = intProgression;
        if (!($receiver instanceof String) || !(other instanceof String)) {
            int index = indices.getFirst();
            int last2 = indices.getLast();
            int step = indices.getStep();
            if (step > 0) {
                if (index > last2) {
                    return -1;
                }
            } else if (index < last2) {
                return -1;
            }
            while (true) {
                if (StringsKt.regionMatchesImpl(other, 0, $receiver, index, other.length(), ignoreCase)) {
                    return index;
                }
                if (index == last2) {
                    return -1;
                }
                index += step;
            }
        } else {
            int index2 = indices.getFirst();
            int last3 = indices.getLast();
            int step2 = indices.getStep();
            if (step2 > 0) {
                if (index2 > last3) {
                    return -1;
                }
            } else if (index2 < last3) {
                return -1;
            }
            while (true) {
                if (StringsKt.regionMatches((String) other, 0, (String) $receiver, index2, other.length(), ignoreCase)) {
                    return index2;
                }
                if (index2 == last3) {
                    return -1;
                }
                index2 += step2;
            }
        }
    }

    /* access modifiers changed from: private */
    public static final Pair<Integer, String> findAnyOf$StringsKt__StringsKt(@NotNull CharSequence $receiver, Collection<String> strings, int startIndex, boolean ignoreCase, boolean last) {
        Object element$iv;
        Object element$iv2;
        CharSequence charSequence = $receiver;
        int i = startIndex;
        if (ignoreCase || strings.size() != 1) {
            int index = 0;
            IntProgression indices = !last ? new IntRange(RangesKt.coerceAtLeast(i, 0), $receiver.length()) : RangesKt.downTo(RangesKt.coerceAtMost(i, StringsKt.getLastIndex($receiver)), 0);
            if (charSequence instanceof String) {
                int first = indices.getFirst();
                int last2 = indices.getLast();
                int step = indices.getStep();
                if (step <= 0 ? first >= last2 : first <= last2) {
                    int index2 = first;
                    boolean z = false;
                    while (true) {
                        int i2 = index;
                        Iterator it = strings.iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                element$iv2 = null;
                                break;
                            }
                            element$iv2 = it.next();
                            String it2 = (String) element$iv2;
                            boolean z2 = z;
                            if (StringsKt.regionMatches(it2, 0, (String) charSequence, index2, it2.length(), ignoreCase)) {
                                z = z2;
                                break;
                            }
                            z = z2;
                        }
                        String matchingString = (String) element$iv2;
                        if (matchingString == null) {
                            if (index2 == last2) {
                                break;
                            }
                            index2 += step;
                            index = i2;
                        } else {
                            return TuplesKt.to(Integer.valueOf(index2), matchingString);
                        }
                    }
                }
            } else {
                int first2 = indices.getFirst();
                int last3 = indices.getLast();
                int step2 = indices.getStep();
                if (step2 <= 0 ? first2 >= last3 : first2 <= last3) {
                    int index3 = first2;
                    boolean z3 = false;
                    while (true) {
                        int i3 = index;
                        Iterator it3 = strings.iterator();
                        while (true) {
                            if (!it3.hasNext()) {
                                element$iv = null;
                                break;
                            }
                            element$iv = it3.next();
                            String it4 = (String) element$iv;
                            boolean z4 = z3;
                            if (StringsKt.regionMatchesImpl(it4, 0, $receiver, index3, it4.length(), ignoreCase)) {
                                z3 = z4;
                                break;
                            }
                            z3 = z4;
                        }
                        String matchingString2 = (String) element$iv;
                        if (matchingString2 == null) {
                            if (index3 == last3) {
                                break;
                            }
                            index3 += step2;
                            index = i3;
                        } else {
                            return TuplesKt.to(Integer.valueOf(index3), matchingString2);
                        }
                    }
                }
            }
            return null;
        }
        String string = (String) CollectionsKt.single(strings);
        int index4 = !last ? StringsKt.indexOf$default($receiver, string, startIndex, false, 4, (Object) null) : StringsKt.lastIndexOf$default($receiver, string, startIndex, false, 4, (Object) null);
        if (index4 < 0) {
            return null;
        }
        return TuplesKt.to(Integer.valueOf(index4), string);
    }

    @Nullable
    public static /* bridge */ /* synthetic */ Pair findAnyOf$default(CharSequence charSequence, Collection collection, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.findAnyOf(charSequence, collection, i, z);
    }

    @Nullable
    public static final Pair<Integer, String> findAnyOf(@NotNull CharSequence $receiver, @NotNull Collection<String> strings, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(strings, "strings");
        return findAnyOf$StringsKt__StringsKt($receiver, strings, startIndex, ignoreCase, false);
    }

    @Nullable
    public static /* bridge */ /* synthetic */ Pair findLastAnyOf$default(CharSequence charSequence, Collection collection, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = StringsKt.getLastIndex(charSequence);
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.findLastAnyOf(charSequence, collection, i, z);
    }

    @Nullable
    public static final Pair<Integer, String> findLastAnyOf(@NotNull CharSequence $receiver, @NotNull Collection<String> strings, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(strings, "strings");
        return findAnyOf$StringsKt__StringsKt($receiver, strings, startIndex, ignoreCase, true);
    }

    public static /* bridge */ /* synthetic */ int indexOfAny$default(CharSequence charSequence, Collection collection, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.indexOfAny(charSequence, (Collection<String>) collection, i, z);
    }

    public static final int indexOfAny(@NotNull CharSequence $receiver, @NotNull Collection<String> strings, int startIndex, boolean ignoreCase) {
        Integer first;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(strings, "strings");
        Pair<Integer, String> findAnyOf$StringsKt__StringsKt = findAnyOf$StringsKt__StringsKt($receiver, strings, startIndex, ignoreCase, false);
        if (findAnyOf$StringsKt__StringsKt == null || (first = findAnyOf$StringsKt__StringsKt.getFirst()) == null) {
            return -1;
        }
        return first.intValue();
    }

    public static /* bridge */ /* synthetic */ int lastIndexOfAny$default(CharSequence charSequence, Collection collection, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = StringsKt.getLastIndex(charSequence);
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.lastIndexOfAny(charSequence, (Collection<String>) collection, i, z);
    }

    public static final int lastIndexOfAny(@NotNull CharSequence $receiver, @NotNull Collection<String> strings, int startIndex, boolean ignoreCase) {
        Integer first;
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(strings, "strings");
        Pair<Integer, String> findAnyOf$StringsKt__StringsKt = findAnyOf$StringsKt__StringsKt($receiver, strings, startIndex, ignoreCase, true);
        if (findAnyOf$StringsKt__StringsKt == null || (first = findAnyOf$StringsKt__StringsKt.getFirst()) == null) {
            return -1;
        }
        return first.intValue();
    }

    public static /* bridge */ /* synthetic */ int indexOf$default(CharSequence charSequence, char c, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.indexOf(charSequence, c, i, z);
    }

    public static final int indexOf(@NotNull CharSequence $receiver, char c, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if (!ignoreCase && ($receiver instanceof String)) {
            return ((String) $receiver).indexOf(c, startIndex);
        }
        return StringsKt.indexOfAny($receiver, new char[]{c}, startIndex, ignoreCase);
    }

    public static /* bridge */ /* synthetic */ int indexOf$default(CharSequence charSequence, String str, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.indexOf(charSequence, str, i, z);
    }

    public static final int indexOf(@NotNull CharSequence $receiver, @NotNull String string, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(string, "string");
        if (!ignoreCase && ($receiver instanceof String)) {
            return ((String) $receiver).indexOf(string, startIndex);
        }
        return indexOf$StringsKt__StringsKt$default($receiver, string, startIndex, $receiver.length(), ignoreCase, false, 16, (Object) null);
    }

    public static /* bridge */ /* synthetic */ int lastIndexOf$default(CharSequence charSequence, char c, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = StringsKt.getLastIndex(charSequence);
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.lastIndexOf(charSequence, c, i, z);
    }

    public static final int lastIndexOf(@NotNull CharSequence $receiver, char c, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        if (!ignoreCase && ($receiver instanceof String)) {
            return ((String) $receiver).lastIndexOf(c, startIndex);
        }
        return StringsKt.lastIndexOfAny($receiver, new char[]{c}, startIndex, ignoreCase);
    }

    public static /* bridge */ /* synthetic */ int lastIndexOf$default(CharSequence charSequence, String str, int i, boolean z, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = StringsKt.getLastIndex(charSequence);
        }
        if ((i2 & 4) != 0) {
            z = false;
        }
        return StringsKt.lastIndexOf(charSequence, str, i, z);
    }

    public static final int lastIndexOf(@NotNull CharSequence $receiver, @NotNull String string, int startIndex, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(string, "string");
        if (!ignoreCase && ($receiver instanceof String)) {
            return ((String) $receiver).lastIndexOf(string, startIndex);
        }
        return indexOf$StringsKt__StringsKt($receiver, string, startIndex, 0, ignoreCase, true);
    }

    public static /* bridge */ /* synthetic */ boolean contains$default(CharSequence charSequence, CharSequence charSequence2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.contains(charSequence, charSequence2, z);
    }

    public static final boolean contains(@NotNull CharSequence $receiver, @NotNull CharSequence other, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(other, "other");
        if (other instanceof String) {
            if (StringsKt.indexOf$default($receiver, (String) other, 0, ignoreCase, 2, (Object) null) >= 0) {
                return true;
            }
            return false;
        }
        if (indexOf$StringsKt__StringsKt$default($receiver, other, 0, $receiver.length(), ignoreCase, false, 16, (Object) null) >= 0) {
            return true;
        }
        return false;
    }

    public static /* bridge */ /* synthetic */ boolean contains$default(CharSequence charSequence, char c, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return StringsKt.contains(charSequence, c, z);
    }

    public static final boolean contains(@NotNull CharSequence $receiver, char c, boolean ignoreCase) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.indexOf$default($receiver, c, 0, ignoreCase, 2, (Object) null) >= 0;
    }

    @InlineOnly
    private static final boolean contains(@NotNull CharSequence $receiver, Regex regex) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return regex.containsMatchIn($receiver);
    }

    static /* bridge */ /* synthetic */ Sequence rangesDelimitedBy$StringsKt__StringsKt$default(CharSequence charSequence, char[] cArr, int i, boolean z, int i2, int i3, Object obj) {
        if ((i3 & 2) != 0) {
            i = 0;
        }
        if ((i3 & 4) != 0) {
            z = false;
        }
        if ((i3 & 8) != 0) {
            i2 = 0;
        }
        return rangesDelimitedBy$StringsKt__StringsKt(charSequence, cArr, i, z, i2);
    }

    private static final Sequence<IntRange> rangesDelimitedBy$StringsKt__StringsKt(@NotNull CharSequence $receiver, char[] delimiters, int startIndex, boolean ignoreCase, int limit) {
        if (limit >= 0) {
            return new DelimitedRangesSequence($receiver, startIndex, limit, new StringsKt__StringsKt$rangesDelimitedBy$2(delimiters, ignoreCase));
        }
        throw new IllegalArgumentException(("Limit must be non-negative, but was " + limit + '.').toString());
    }

    static /* bridge */ /* synthetic */ Sequence rangesDelimitedBy$StringsKt__StringsKt$default(CharSequence charSequence, String[] strArr, int i, boolean z, int i2, int i3, Object obj) {
        if ((i3 & 2) != 0) {
            i = 0;
        }
        if ((i3 & 4) != 0) {
            z = false;
        }
        if ((i3 & 8) != 0) {
            i2 = 0;
        }
        return rangesDelimitedBy$StringsKt__StringsKt(charSequence, strArr, i, z, i2);
    }

    private static final Sequence<IntRange> rangesDelimitedBy$StringsKt__StringsKt(@NotNull CharSequence $receiver, String[] delimiters, int startIndex, boolean ignoreCase, int limit) {
        if (limit >= 0) {
            return new DelimitedRangesSequence($receiver, startIndex, limit, new StringsKt__StringsKt$rangesDelimitedBy$4(ArraysKt.asList((T[]) delimiters), ignoreCase));
        }
        throw new IllegalArgumentException(("Limit must be non-negative, but was " + limit + '.').toString());
    }

    @NotNull
    public static /* bridge */ /* synthetic */ Sequence splitToSequence$default(CharSequence charSequence, String[] strArr, boolean z, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            z = false;
        }
        if ((i2 & 4) != 0) {
            i = 0;
        }
        return StringsKt.splitToSequence(charSequence, strArr, z, i);
    }

    @NotNull
    public static final Sequence<String> splitToSequence(@NotNull CharSequence $receiver, @NotNull String[] delimiters, boolean ignoreCase, int limit) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiters, "delimiters");
        return SequencesKt.map(rangesDelimitedBy$StringsKt__StringsKt$default($receiver, delimiters, 0, ignoreCase, limit, 2, (Object) null), new StringsKt__StringsKt$splitToSequence$1($receiver));
    }

    @NotNull
    public static /* bridge */ /* synthetic */ List split$default(CharSequence charSequence, String[] strArr, boolean z, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            z = false;
        }
        if ((i2 & 4) != 0) {
            i = 0;
        }
        return StringsKt.split(charSequence, strArr, z, i);
    }

    @NotNull
    public static final List<String> split(@NotNull CharSequence $receiver, @NotNull String[] delimiters, boolean ignoreCase, int limit) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiters, "delimiters");
        boolean z = true;
        if (delimiters.length == 1) {
            String delimiter = delimiters[0];
            if (delimiter.length() != 0) {
                z = false;
            }
            if (!z) {
                return split$StringsKt__StringsKt($receiver, delimiter, ignoreCase, limit);
            }
        }
        Iterable<IntRange> $receiver$iv = SequencesKt.asIterable(rangesDelimitedBy$StringsKt__StringsKt$default($receiver, delimiters, 0, ignoreCase, limit, 2, (Object) null));
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10));
        for (IntRange it : $receiver$iv) {
            destination$iv$iv.add(StringsKt.substring($receiver, it));
        }
        return (List) destination$iv$iv;
    }

    @NotNull
    public static /* bridge */ /* synthetic */ Sequence splitToSequence$default(CharSequence charSequence, char[] cArr, boolean z, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            z = false;
        }
        if ((i2 & 4) != 0) {
            i = 0;
        }
        return StringsKt.splitToSequence(charSequence, cArr, z, i);
    }

    @NotNull
    public static final Sequence<String> splitToSequence(@NotNull CharSequence $receiver, @NotNull char[] delimiters, boolean ignoreCase, int limit) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiters, "delimiters");
        return SequencesKt.map(rangesDelimitedBy$StringsKt__StringsKt$default($receiver, delimiters, 0, ignoreCase, limit, 2, (Object) null), new StringsKt__StringsKt$splitToSequence$2($receiver));
    }

    @NotNull
    public static /* bridge */ /* synthetic */ List split$default(CharSequence charSequence, char[] cArr, boolean z, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            z = false;
        }
        if ((i2 & 4) != 0) {
            i = 0;
        }
        return StringsKt.split(charSequence, cArr, z, i);
    }

    @NotNull
    public static final List<String> split(@NotNull CharSequence $receiver, @NotNull char[] delimiters, boolean ignoreCase, int limit) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(delimiters, "delimiters");
        if (delimiters.length == 1) {
            return split$StringsKt__StringsKt($receiver, String.valueOf(delimiters[0]), ignoreCase, limit);
        }
        Iterable<IntRange> $receiver$iv = SequencesKt.asIterable(rangesDelimitedBy$StringsKt__StringsKt$default($receiver, delimiters, 0, ignoreCase, limit, 2, (Object) null));
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10));
        for (IntRange it : $receiver$iv) {
            destination$iv$iv.add(StringsKt.substring($receiver, it));
        }
        return (List) destination$iv$iv;
    }

    private static final List<String> split$StringsKt__StringsKt(@NotNull CharSequence $receiver, String delimiter, boolean ignoreCase, int limit) {
        boolean isLimited = false;
        if (limit >= 0) {
            int currentOffset = 0;
            int nextIndex = StringsKt.indexOf($receiver, delimiter, 0, ignoreCase);
            if (nextIndex == -1 || limit == 1) {
                return CollectionsKt.listOf($receiver.toString());
            }
            if (limit > 0) {
                isLimited = true;
            }
            int i = 10;
            if (isLimited) {
                i = RangesKt.coerceAtMost(limit, 10);
            }
            ArrayList result = new ArrayList(i);
            do {
                result.add($receiver.subSequence(currentOffset, nextIndex).toString());
                currentOffset = nextIndex + delimiter.length();
                if ((isLimited && result.size() == limit - 1) || (nextIndex = StringsKt.indexOf($receiver, delimiter, currentOffset, ignoreCase)) == -1) {
                    result.add($receiver.subSequence(currentOffset, $receiver.length()).toString());
                }
                result.add($receiver.subSequence(currentOffset, nextIndex).toString());
                currentOffset = nextIndex + delimiter.length();
                break;
            } while ((nextIndex = StringsKt.indexOf($receiver, delimiter, currentOffset, ignoreCase)) == -1);
            result.add($receiver.subSequence(currentOffset, $receiver.length()).toString());
            return result;
        }
        throw new IllegalArgumentException(("Limit must be non-negative, but was " + limit + '.').toString());
    }

    @InlineOnly
    private static final List<String> split(@NotNull CharSequence $receiver, Regex regex, int limit) {
        return regex.split($receiver, limit);
    }

    @InlineOnly
    static /* bridge */ /* synthetic */ List split$default(CharSequence $receiver, Regex regex, int limit, int i, Object obj) {
        if ((i & 2) != 0) {
            limit = 0;
        }
        return regex.split($receiver, limit);
    }

    @NotNull
    public static final Sequence<String> lineSequence(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return StringsKt.splitToSequence$default($receiver, new String[]{"\r\n", "\n", "\r"}, false, 0, 6, (Object) null);
    }

    @NotNull
    public static final List<String> lines(@NotNull CharSequence $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return SequencesKt.toList(StringsKt.lineSequence($receiver));
    }
}

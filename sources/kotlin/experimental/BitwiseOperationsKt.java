package kotlin.experimental;

import kotlin.Metadata;
import kotlin.SinceKotlin;
import kotlin.internal.InlineOnly;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0005\n\u0000\n\u0002\u0010\n\n\u0002\b\u0004\u001a\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\f\u001a\u0015\u0010\u0000\u001a\u00020\u0003*\u00020\u00032\u0006\u0010\u0002\u001a\u00020\u0003H\f\u001a\r\u0010\u0004\u001a\u00020\u0001*\u00020\u0001H\b\u001a\r\u0010\u0004\u001a\u00020\u0003*\u00020\u0003H\b\u001a\u0015\u0010\u0005\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\f\u001a\u0015\u0010\u0005\u001a\u00020\u0003*\u00020\u00032\u0006\u0010\u0002\u001a\u00020\u0003H\f\u001a\u0015\u0010\u0006\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\f\u001a\u0015\u0010\u0006\u001a\u00020\u0003*\u00020\u00032\u0006\u0010\u0002\u001a\u00020\u0003H\f¨\u0006\u0007"}, d2 = {"and", "", "other", "", "inv", "or", "xor", "kotlin-stdlib"}, k = 2, mv = {1, 1, 11})
/* compiled from: bitwiseOperations.kt */
public final class BitwiseOperationsKt {
    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final byte and(byte $receiver, byte other) {
        return (byte) ($receiver & other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final byte or(byte $receiver, byte other) {
        return (byte) ($receiver | other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final byte xor(byte $receiver, byte other) {
        return (byte) ($receiver ^ other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final byte inv(byte $receiver) {
        return (byte) (~$receiver);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final short and(short $receiver, short other) {
        return (short) ($receiver & other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final short or(short $receiver, short other) {
        return (short) ($receiver | other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final short xor(short $receiver, short other) {
        return (short) ($receiver ^ other);
    }

    @SinceKotlin(version = "1.1")
    @InlineOnly
    private static final short inv(short $receiver) {
        return (short) (~$receiver);
    }
}

package kotlin.text;

import kotlin.Metadata;
import kotlin.PublishedApi;
import kotlin.internal.InlineOnly;
import kotlin.ranges.IntRange;
import org.jetbrains.annotations.NotNull;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000&\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\f\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0011\u001a\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0001\u001a\u0018\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\nH\u0000\u001a\r\u0010\u000e\u001a\u00020\u000f*\u00020\u0002H\b\u001a\r\u0010\u0010\u001a\u00020\u000f*\u00020\u0002H\b\u001a\r\u0010\u0011\u001a\u00020\u000f*\u00020\u0002H\b\u001a\r\u0010\u0012\u001a\u00020\u000f*\u00020\u0002H\b\u001a\r\u0010\u0013\u001a\u00020\u000f*\u00020\u0002H\b\u001a\r\u0010\u0014\u001a\u00020\u000f*\u00020\u0002H\b\u001a\r\u0010\u0015\u001a\u00020\u000f*\u00020\u0002H\b\u001a\r\u0010\u0016\u001a\u00020\u000f*\u00020\u0002H\b\u001a\r\u0010\u0017\u001a\u00020\u000f*\u00020\u0002H\b\u001a\r\u0010\u0018\u001a\u00020\u000f*\u00020\u0002H\b\u001a\r\u0010\u0019\u001a\u00020\u000f*\u00020\u0002H\b\u001a\r\u0010\u001a\u001a\u00020\u000f*\u00020\u0002H\b\u001a\r\u0010\u001b\u001a\u00020\u000f*\u00020\u0002H\b\u001a\n\u0010\u001c\u001a\u00020\u000f*\u00020\u0002\u001a\r\u0010\u001d\u001a\u00020\u0002*\u00020\u0002H\b\u001a\r\u0010\u001e\u001a\u00020\u0002*\u00020\u0002H\b\u001a\r\u0010\u001f\u001a\u00020\u0002*\u00020\u0002H\b\"\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\"\u0015\u0010\u0005\u001a\u00020\u0006*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\b¨\u0006 "}, d2 = {"category", "Lkotlin/text/CharCategory;", "", "getCategory", "(C)Lkotlin/text/CharCategory;", "directionality", "Lkotlin/text/CharDirectionality;", "getDirectionality", "(C)Lkotlin/text/CharDirectionality;", "checkRadix", "", "radix", "digitOf", "char", "isDefined", "", "isDigit", "isHighSurrogate", "isISOControl", "isIdentifierIgnorable", "isJavaIdentifierPart", "isJavaIdentifierStart", "isLetter", "isLetterOrDigit", "isLowSurrogate", "isLowerCase", "isTitleCase", "isUpperCase", "isWhitespace", "toLowerCase", "toTitleCase", "toUpperCase", "kotlin-stdlib"}, k = 5, mv = {1, 1, 11}, xi = 1, xs = "kotlin/text/CharsKt")
/* compiled from: CharJVM.kt */
class CharsKt__CharJVMKt {
    @InlineOnly
    private static final boolean isDefined(char $receiver) {
        return Character.isDefined($receiver);
    }

    @InlineOnly
    private static final boolean isLetter(char $receiver) {
        return Character.isLetter($receiver);
    }

    @InlineOnly
    private static final boolean isLetterOrDigit(char $receiver) {
        return Character.isLetterOrDigit($receiver);
    }

    @InlineOnly
    private static final boolean isDigit(char $receiver) {
        return Character.isDigit($receiver);
    }

    @InlineOnly
    private static final boolean isIdentifierIgnorable(char $receiver) {
        return Character.isIdentifierIgnorable($receiver);
    }

    @InlineOnly
    private static final boolean isISOControl(char $receiver) {
        return Character.isISOControl($receiver);
    }

    @InlineOnly
    private static final boolean isJavaIdentifierPart(char $receiver) {
        return Character.isJavaIdentifierPart($receiver);
    }

    @InlineOnly
    private static final boolean isJavaIdentifierStart(char $receiver) {
        return Character.isJavaIdentifierStart($receiver);
    }

    public static final boolean isWhitespace(char $receiver) {
        return Character.isWhitespace($receiver) || Character.isSpaceChar($receiver);
    }

    @InlineOnly
    private static final boolean isUpperCase(char $receiver) {
        return Character.isUpperCase($receiver);
    }

    @InlineOnly
    private static final boolean isLowerCase(char $receiver) {
        return Character.isLowerCase($receiver);
    }

    @InlineOnly
    private static final char toUpperCase(char $receiver) {
        return Character.toUpperCase($receiver);
    }

    @InlineOnly
    private static final char toLowerCase(char $receiver) {
        return Character.toLowerCase($receiver);
    }

    @InlineOnly
    private static final boolean isTitleCase(char $receiver) {
        return Character.isTitleCase($receiver);
    }

    @InlineOnly
    private static final char toTitleCase(char $receiver) {
        return Character.toTitleCase($receiver);
    }

    @NotNull
    public static final CharCategory getCategory(char $receiver) {
        return CharCategory.Companion.valueOf(Character.getType($receiver));
    }

    @NotNull
    public static final CharDirectionality getDirectionality(char $receiver) {
        return CharDirectionality.Companion.valueOf(Character.getDirectionality($receiver));
    }

    @InlineOnly
    private static final boolean isHighSurrogate(char $receiver) {
        return Character.isHighSurrogate($receiver);
    }

    @InlineOnly
    private static final boolean isLowSurrogate(char $receiver) {
        return Character.isLowSurrogate($receiver);
    }

    public static final int digitOf(char c, int radix) {
        return Character.digit(c, radix);
    }

    @PublishedApi
    public static final int checkRadix(int radix) {
        if (2 <= radix && 36 >= radix) {
            return radix;
        }
        throw new IllegalArgumentException("radix " + radix + " was not in valid range " + new IntRange(2, 36));
    }
}

package com.github.paolorotolo.appintro.model;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.core.view.PointerIconCompat;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\r\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b,\n\u0002\u0010\u000b\n\u0002\b\u0004\b\b\u0018\u00002\u00020\u0001Bs\b\u0007\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0003\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0003\u0010\u0007\u001a\u00020\u0006\u0012\b\b\u0003\u0010\b\u001a\u00020\u0006\u0012\b\b\u0003\u0010\t\u001a\u00020\u0006\u0012\b\b\u0003\u0010\n\u001a\u00020\u0006\u0012\b\b\u0003\u0010\u000b\u001a\u00020\u0006\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\r¢\u0006\u0002\u0010\u000fJ\u000b\u0010.\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010/\u001a\u0004\u0018\u00010\rHÆ\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u00101\u001a\u00020\u0006HÆ\u0003J\t\u00102\u001a\u00020\u0006HÆ\u0003J\t\u00103\u001a\u00020\u0006HÆ\u0003J\t\u00104\u001a\u00020\u0006HÆ\u0003J\t\u00105\u001a\u00020\u0006HÆ\u0003J\t\u00106\u001a\u00020\u0006HÆ\u0003J\u000b\u00107\u001a\u0004\u0018\u00010\rHÆ\u0003Ju\u00108\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0003\u0010\u0005\u001a\u00020\u00062\b\b\u0003\u0010\u0007\u001a\u00020\u00062\b\b\u0003\u0010\b\u001a\u00020\u00062\b\b\u0003\u0010\t\u001a\u00020\u00062\b\b\u0003\u0010\n\u001a\u00020\u00062\b\b\u0003\u0010\u000b\u001a\u00020\u00062\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\rHÆ\u0001J\u0013\u00109\u001a\u00020:2\b\u0010;\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010<\u001a\u00020\u0006HÖ\u0001J\t\u0010=\u001a\u00020\rHÖ\u0001R\u001a\u0010\u0007\u001a\u00020\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\t\u001a\u00020\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0011\"\u0004\b\u0015\u0010\u0013R\u001c\u0010\u000e\u001a\u0004\u0018\u00010\rX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u001a\u0010\u000b\u001a\u00020\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u0011\"\u0004\b\u001b\u0010\u0013R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u0013\u0010 \u001a\u0004\u0018\u00010\r8F¢\u0006\u0006\u001a\u0004\b!\u0010\u0017R\u001a\u0010\u0005\u001a\u00020\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\u0011\"\u0004\b#\u0010\u0013R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b$\u0010\u001d\"\u0004\b%\u0010\u001fR\u001a\u0010\b\u001a\u00020\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b&\u0010\u0011\"\u0004\b'\u0010\u0013R\u0013\u0010(\u001a\u0004\u0018\u00010\r8F¢\u0006\u0006\u001a\u0004\b)\u0010\u0017R\u001c\u0010\f\u001a\u0004\u0018\u00010\rX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b*\u0010\u0017\"\u0004\b+\u0010\u0019R\u001a\u0010\n\u001a\u00020\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b,\u0010\u0011\"\u0004\b-\u0010\u0013¨\u0006>"}, d2 = {"Lcom/github/paolorotolo/appintro/model/SliderPage;", "", "title", "", "description", "imageDrawable", "", "bgColor", "titleColor", "descColor", "titleTypefaceFontRes", "descTypefaceFontRes", "titleTypeface", "", "descTypeface", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;IIIIIILjava/lang/String;Ljava/lang/String;)V", "getBgColor", "()I", "setBgColor", "(I)V", "getDescColor", "setDescColor", "getDescTypeface", "()Ljava/lang/String;", "setDescTypeface", "(Ljava/lang/String;)V", "getDescTypefaceFontRes", "setDescTypefaceFontRes", "getDescription", "()Ljava/lang/CharSequence;", "setDescription", "(Ljava/lang/CharSequence;)V", "descriptionString", "getDescriptionString", "getImageDrawable", "setImageDrawable", "getTitle", "setTitle", "getTitleColor", "setTitleColor", "titleString", "getTitleString", "getTitleTypeface", "setTitleTypeface", "getTitleTypefaceFontRes", "setTitleTypefaceFontRes", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "appintro_release"}, k = 1, mv = {1, 1, 11})
/* compiled from: SliderPage.kt */
public final class SliderPage {
    private int bgColor;
    private int descColor;
    @Nullable
    private String descTypeface;
    private int descTypefaceFontRes;
    @Nullable
    private CharSequence description;
    private int imageDrawable;
    @Nullable
    private CharSequence title;
    private int titleColor;
    @Nullable
    private String titleTypeface;
    private int titleTypefaceFontRes;

    @JvmOverloads
    public SliderPage() {
        this((CharSequence) null, (CharSequence) null, 0, 0, 0, 0, 0, 0, (String) null, (String) null, 1023, (DefaultConstructorMarker) null);
    }

    @JvmOverloads
    public SliderPage(@Nullable CharSequence charSequence) {
        this(charSequence, (CharSequence) null, 0, 0, 0, 0, 0, 0, (String) null, (String) null, 1022, (DefaultConstructorMarker) null);
    }

    @JvmOverloads
    public SliderPage(@Nullable CharSequence charSequence, @Nullable CharSequence charSequence2) {
        this(charSequence, charSequence2, 0, 0, 0, 0, 0, 0, (String) null, (String) null, PointerIconCompat.TYPE_GRAB, (DefaultConstructorMarker) null);
    }

    @JvmOverloads
    public SliderPage(@Nullable CharSequence charSequence, @Nullable CharSequence charSequence2, @DrawableRes int i) {
        this(charSequence, charSequence2, i, 0, 0, 0, 0, 0, (String) null, (String) null, PointerIconCompat.TYPE_TOP_RIGHT_DIAGONAL_DOUBLE_ARROW, (DefaultConstructorMarker) null);
    }

    @JvmOverloads
    public SliderPage(@Nullable CharSequence charSequence, @Nullable CharSequence charSequence2, @DrawableRes int i, @ColorInt int i2) {
        this(charSequence, charSequence2, i, i2, 0, 0, 0, 0, (String) null, (String) null, PointerIconCompat.TYPE_TEXT, (DefaultConstructorMarker) null);
    }

    @JvmOverloads
    public SliderPage(@Nullable CharSequence charSequence, @Nullable CharSequence charSequence2, @DrawableRes int i, @ColorInt int i2, @ColorInt int i3) {
        this(charSequence, charSequence2, i, i2, i3, 0, 0, 0, (String) null, (String) null, 992, (DefaultConstructorMarker) null);
    }

    @JvmOverloads
    public SliderPage(@Nullable CharSequence charSequence, @Nullable CharSequence charSequence2, @DrawableRes int i, @ColorInt int i2, @ColorInt int i3, @ColorInt int i4) {
        this(charSequence, charSequence2, i, i2, i3, i4, 0, 0, (String) null, (String) null, 960, (DefaultConstructorMarker) null);
    }

    @JvmOverloads
    public SliderPage(@Nullable CharSequence charSequence, @Nullable CharSequence charSequence2, @DrawableRes int i, @ColorInt int i2, @ColorInt int i3, @ColorInt int i4, @FontRes int i5) {
        this(charSequence, charSequence2, i, i2, i3, i4, i5, 0, (String) null, (String) null, 896, (DefaultConstructorMarker) null);
    }

    @JvmOverloads
    public SliderPage(@Nullable CharSequence charSequence, @Nullable CharSequence charSequence2, @DrawableRes int i, @ColorInt int i2, @ColorInt int i3, @ColorInt int i4, @FontRes int i5, @FontRes int i6) {
        this(charSequence, charSequence2, i, i2, i3, i4, i5, i6, (String) null, (String) null, 768, (DefaultConstructorMarker) null);
    }

    @JvmOverloads
    public SliderPage(@Nullable CharSequence charSequence, @Nullable CharSequence charSequence2, @DrawableRes int i, @ColorInt int i2, @ColorInt int i3, @ColorInt int i4, @FontRes int i5, @FontRes int i6, @Nullable String str) {
        this(charSequence, charSequence2, i, i2, i3, i4, i5, i6, str, (String) null, 512, (DefaultConstructorMarker) null);
    }

    @NotNull
    public static /* bridge */ /* synthetic */ SliderPage copy$default(SliderPage sliderPage, CharSequence charSequence, CharSequence charSequence2, int i, int i2, int i3, int i4, int i5, int i6, String str, String str2, int i7, Object obj) {
        SliderPage sliderPage2 = sliderPage;
        int i8 = i7;
        return sliderPage.copy((i8 & 1) != 0 ? sliderPage2.title : charSequence, (i8 & 2) != 0 ? sliderPage2.description : charSequence2, (i8 & 4) != 0 ? sliderPage2.imageDrawable : i, (i8 & 8) != 0 ? sliderPage2.bgColor : i2, (i8 & 16) != 0 ? sliderPage2.titleColor : i3, (i8 & 32) != 0 ? sliderPage2.descColor : i4, (i8 & 64) != 0 ? sliderPage2.titleTypefaceFontRes : i5, (i8 & 128) != 0 ? sliderPage2.descTypefaceFontRes : i6, (i8 & 256) != 0 ? sliderPage2.titleTypeface : str, (i8 & 512) != 0 ? sliderPage2.descTypeface : str2);
    }

    @Nullable
    public final CharSequence component1() {
        return this.title;
    }

    @Nullable
    public final String component10() {
        return this.descTypeface;
    }

    @Nullable
    public final CharSequence component2() {
        return this.description;
    }

    public final int component3() {
        return this.imageDrawable;
    }

    public final int component4() {
        return this.bgColor;
    }

    public final int component5() {
        return this.titleColor;
    }

    public final int component6() {
        return this.descColor;
    }

    public final int component7() {
        return this.titleTypefaceFontRes;
    }

    public final int component8() {
        return this.descTypefaceFontRes;
    }

    @Nullable
    public final String component9() {
        return this.titleTypeface;
    }

    @NotNull
    public final SliderPage copy(@Nullable CharSequence charSequence, @Nullable CharSequence charSequence2, @DrawableRes int i, @ColorInt int i2, @ColorInt int i3, @ColorInt int i4, @FontRes int i5, @FontRes int i6, @Nullable String str, @Nullable String str2) {
        return new SliderPage(charSequence, charSequence2, i, i2, i3, i4, i5, i6, str, str2);
    }

    public boolean equals(@Nullable Object obj) {
        if (this != obj) {
            if (obj instanceof SliderPage) {
                SliderPage sliderPage = (SliderPage) obj;
                if (Intrinsics.areEqual((Object) this.title, (Object) sliderPage.title) && Intrinsics.areEqual((Object) this.description, (Object) sliderPage.description)) {
                    if (this.imageDrawable == sliderPage.imageDrawable) {
                        if (this.bgColor == sliderPage.bgColor) {
                            if (this.titleColor == sliderPage.titleColor) {
                                if (this.descColor == sliderPage.descColor) {
                                    if (this.titleTypefaceFontRes == sliderPage.titleTypefaceFontRes) {
                                        if (!(this.descTypefaceFontRes == sliderPage.descTypefaceFontRes) || !Intrinsics.areEqual((Object) this.titleTypeface, (Object) sliderPage.titleTypeface) || !Intrinsics.areEqual((Object) this.descTypeface, (Object) sliderPage.descTypeface)) {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        CharSequence charSequence = this.title;
        int i = 0;
        int hashCode = (charSequence != null ? charSequence.hashCode() : 0) * 31;
        CharSequence charSequence2 = this.description;
        int hashCode2 = (((((((((((((hashCode + (charSequence2 != null ? charSequence2.hashCode() : 0)) * 31) + this.imageDrawable) * 31) + this.bgColor) * 31) + this.titleColor) * 31) + this.descColor) * 31) + this.titleTypefaceFontRes) * 31) + this.descTypefaceFontRes) * 31;
        String str = this.titleTypeface;
        int hashCode3 = (hashCode2 + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.descTypeface;
        if (str2 != null) {
            i = str2.hashCode();
        }
        return hashCode3 + i;
    }

    @NotNull
    public String toString() {
        return "SliderPage(title=" + this.title + ", description=" + this.description + ", imageDrawable=" + this.imageDrawable + ", bgColor=" + this.bgColor + ", titleColor=" + this.titleColor + ", descColor=" + this.descColor + ", titleTypefaceFontRes=" + this.titleTypefaceFontRes + ", descTypefaceFontRes=" + this.descTypefaceFontRes + ", titleTypeface=" + this.titleTypeface + ", descTypeface=" + this.descTypeface + ")";
    }

    @JvmOverloads
    public SliderPage(@Nullable CharSequence title2, @Nullable CharSequence description2, @DrawableRes int imageDrawable2, @ColorInt int bgColor2, @ColorInt int titleColor2, @ColorInt int descColor2, @FontRes int titleTypefaceFontRes2, @FontRes int descTypefaceFontRes2, @Nullable String titleTypeface2, @Nullable String descTypeface2) {
        this.title = title2;
        this.description = description2;
        this.imageDrawable = imageDrawable2;
        this.bgColor = bgColor2;
        this.titleColor = titleColor2;
        this.descColor = descColor2;
        this.titleTypefaceFontRes = titleTypefaceFontRes2;
        this.descTypefaceFontRes = descTypefaceFontRes2;
        this.titleTypeface = titleTypeface2;
        this.descTypeface = descTypeface2;
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    @kotlin.jvm.JvmOverloads
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ SliderPage(java.lang.CharSequence r12, java.lang.CharSequence r13, int r14, int r15, int r16, int r17, int r18, int r19, java.lang.String r20, java.lang.String r21, int r22, kotlin.jvm.internal.DefaultConstructorMarker r23) {
        /*
            r11 = this;
            r0 = r22
            r1 = r0 & 1
            r2 = 0
            if (r1 == 0) goto L_0x000b
            r1 = r2
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            goto L_0x000c
        L_0x000b:
            r1 = r12
        L_0x000c:
            r3 = r0 & 2
            if (r3 == 0) goto L_0x0014
            r3 = r2
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3
            goto L_0x0015
        L_0x0014:
            r3 = r13
        L_0x0015:
            r4 = r0 & 4
            r5 = 0
            if (r4 == 0) goto L_0x001c
            r4 = 0
            goto L_0x001d
        L_0x001c:
            r4 = r14
        L_0x001d:
            r6 = r0 & 8
            if (r6 == 0) goto L_0x0023
            r6 = 0
            goto L_0x0024
        L_0x0023:
            r6 = r15
        L_0x0024:
            r7 = r0 & 16
            if (r7 == 0) goto L_0x002a
            r7 = 0
            goto L_0x002c
        L_0x002a:
            r7 = r16
        L_0x002c:
            r8 = r0 & 32
            if (r8 == 0) goto L_0x0032
            r8 = 0
            goto L_0x0034
        L_0x0032:
            r8 = r17
        L_0x0034:
            r9 = r0 & 64
            if (r9 == 0) goto L_0x003a
            r9 = 0
            goto L_0x003c
        L_0x003a:
            r9 = r18
        L_0x003c:
            r10 = r0 & 128(0x80, float:1.794E-43)
            if (r10 == 0) goto L_0x0041
            goto L_0x0043
        L_0x0041:
            r5 = r19
        L_0x0043:
            r10 = r0 & 256(0x100, float:3.59E-43)
            if (r10 == 0) goto L_0x004b
            r10 = r2
            java.lang.String r10 = (java.lang.String) r10
            goto L_0x004d
        L_0x004b:
            r10 = r20
        L_0x004d:
            r0 = r0 & 512(0x200, float:7.175E-43)
            if (r0 == 0) goto L_0x0055
            r0 = r2
            java.lang.String r0 = (java.lang.String) r0
            goto L_0x0057
        L_0x0055:
            r0 = r21
        L_0x0057:
            r12 = r11
            r13 = r1
            r14 = r3
            r15 = r4
            r16 = r6
            r17 = r7
            r18 = r8
            r19 = r9
            r20 = r5
            r21 = r10
            r22 = r0
            r12.<init>(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.github.paolorotolo.appintro.model.SliderPage.<init>(java.lang.CharSequence, java.lang.CharSequence, int, int, int, int, int, int, java.lang.String, java.lang.String, int, kotlin.jvm.internal.DefaultConstructorMarker):void");
    }

    @Nullable
    public final CharSequence getTitle() {
        return this.title;
    }

    public final void setTitle(@Nullable CharSequence charSequence) {
        this.title = charSequence;
    }

    @Nullable
    public final CharSequence getDescription() {
        return this.description;
    }

    public final void setDescription(@Nullable CharSequence charSequence) {
        this.description = charSequence;
    }

    public final int getImageDrawable() {
        return this.imageDrawable;
    }

    public final void setImageDrawable(int i) {
        this.imageDrawable = i;
    }

    public final int getBgColor() {
        return this.bgColor;
    }

    public final void setBgColor(int i) {
        this.bgColor = i;
    }

    public final int getTitleColor() {
        return this.titleColor;
    }

    public final void setTitleColor(int i) {
        this.titleColor = i;
    }

    public final int getDescColor() {
        return this.descColor;
    }

    public final void setDescColor(int i) {
        this.descColor = i;
    }

    public final int getTitleTypefaceFontRes() {
        return this.titleTypefaceFontRes;
    }

    public final void setTitleTypefaceFontRes(int i) {
        this.titleTypefaceFontRes = i;
    }

    public final int getDescTypefaceFontRes() {
        return this.descTypefaceFontRes;
    }

    public final void setDescTypefaceFontRes(int i) {
        this.descTypefaceFontRes = i;
    }

    @Nullable
    public final String getTitleTypeface() {
        return this.titleTypeface;
    }

    public final void setTitleTypeface(@Nullable String str) {
        this.titleTypeface = str;
    }

    @Nullable
    public final String getDescTypeface() {
        return this.descTypeface;
    }

    public final void setDescTypeface(@Nullable String str) {
        this.descTypeface = str;
    }

    @Nullable
    public final String getTitleString() {
        CharSequence charSequence = this.title;
        if (charSequence != null) {
            return charSequence.toString();
        }
        return null;
    }

    @Nullable
    public final String getDescriptionString() {
        CharSequence charSequence = this.description;
        if (charSequence != null) {
            return charSequence.toString();
        }
        return null;
    }
}

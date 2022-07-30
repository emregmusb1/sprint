package com.github.paolorotolo.appintro.util;

import android.graphics.Typeface;
import android.widget.TextView;
import androidx.annotation.FontRes;
import androidx.core.content.res.ResourcesCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(bv = {1, 0, 2}, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0004\b\b\u0018\u00002\u00020\u0001B\u001b\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0003\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0010\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0005HÆ\u0003J\u001f\u0010\u0015\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0019\u001a\u00020\u0005HÖ\u0001J\t\u0010\u001a\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0004\u001a\u00020\u0005X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e¨\u0006\u001b"}, d2 = {"Lcom/github/paolorotolo/appintro/util/TypefaceContainer;", "", "typeFaceUrl", "", "typeFaceResource", "", "(Ljava/lang/String;I)V", "getTypeFaceResource", "()I", "setTypeFaceResource", "(I)V", "getTypeFaceUrl", "()Ljava/lang/String;", "setTypeFaceUrl", "(Ljava/lang/String;)V", "applyTo", "", "textView", "Landroid/widget/TextView;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "appintro_release"}, k = 1, mv = {1, 1, 11})
/* compiled from: TypefaceContainer.kt */
public final class TypefaceContainer {
    private int typeFaceResource;
    @Nullable
    private String typeFaceUrl;

    public TypefaceContainer() {
        this((String) null, 0, 3, (DefaultConstructorMarker) null);
    }

    @NotNull
    public static /* bridge */ /* synthetic */ TypefaceContainer copy$default(TypefaceContainer typefaceContainer, String str, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = typefaceContainer.typeFaceUrl;
        }
        if ((i2 & 2) != 0) {
            i = typefaceContainer.typeFaceResource;
        }
        return typefaceContainer.copy(str, i);
    }

    @Nullable
    public final String component1() {
        return this.typeFaceUrl;
    }

    public final int component2() {
        return this.typeFaceResource;
    }

    @NotNull
    public final TypefaceContainer copy(@Nullable String str, @FontRes int i) {
        return new TypefaceContainer(str, i);
    }

    public boolean equals(@Nullable Object obj) {
        if (this != obj) {
            if (obj instanceof TypefaceContainer) {
                TypefaceContainer typefaceContainer = (TypefaceContainer) obj;
                if (Intrinsics.areEqual((Object) this.typeFaceUrl, (Object) typefaceContainer.typeFaceUrl)) {
                    if (this.typeFaceResource == typefaceContainer.typeFaceResource) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        String str = this.typeFaceUrl;
        return ((str != null ? str.hashCode() : 0) * 31) + this.typeFaceResource;
    }

    @NotNull
    public String toString() {
        return "TypefaceContainer(typeFaceUrl=" + this.typeFaceUrl + ", typeFaceResource=" + this.typeFaceResource + ")";
    }

    public TypefaceContainer(@Nullable String typeFaceUrl2, @FontRes int typeFaceResource2) {
        this.typeFaceUrl = typeFaceUrl2;
        this.typeFaceResource = typeFaceResource2;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ TypefaceContainer(String str, int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? null : str, (i2 & 2) != 0 ? 0 : i);
    }

    @Nullable
    public final String getTypeFaceUrl() {
        return this.typeFaceUrl;
    }

    public final void setTypeFaceUrl(@Nullable String str) {
        this.typeFaceUrl = str;
    }

    public final int getTypeFaceResource() {
        return this.typeFaceResource;
    }

    public final void setTypeFaceResource(int i) {
        this.typeFaceResource = i;
    }

    public final void applyTo(@Nullable TextView textView) {
        Typeface textTypeface;
        if (textView != null && textView.getContext() != null) {
            if (this.typeFaceUrl != null || this.typeFaceResource != 0) {
                if (this.typeFaceResource != 0) {
                    textTypeface = ResourcesCompat.getFont(textView.getContext(), this.typeFaceResource);
                } else {
                    textTypeface = CustomFontCache.get(this.typeFaceUrl, textView.getContext());
                }
                if (textTypeface != null) {
                    textView.setTypeface(textTypeface);
                }
            }
        }
    }
}

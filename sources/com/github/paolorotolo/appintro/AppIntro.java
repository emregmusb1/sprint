package com.github.paolorotolo.appintro;

import android.graphics.drawable.Drawable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.FontRes;
import androidx.annotation.Nullable;
import com.github.paolorotolo.appintro.util.LogHelper;
import com.github.paolorotolo.appintro.util.TypefaceContainer;

public abstract class AppIntro extends AppIntroBase {
    private static final String TAG = LogHelper.makeLogTag(AppIntro.class);

    /* access modifiers changed from: protected */
    public int getLayoutId() {
        return R.layout.appintro_intro_layout;
    }

    public void setBarColor(@ColorInt int color) {
        ((LinearLayout) findViewById(R.id.bottom)).setBackgroundColor(color);
    }

    public void setNextArrowColor(@ColorInt int color) {
        ((ImageButton) findViewById(R.id.next)).setColorFilter(color);
    }

    public void setSeparatorColor(@ColorInt int color) {
        ((TextView) findViewById(R.id.bottom_separator)).setBackgroundColor(color);
    }

    public void setSkipText(@Nullable CharSequence text) {
        ((TextView) findViewById(R.id.skip)).setText(text);
    }

    public void setSkipTextTypeface(@FontRes int typeface) {
        new TypefaceContainer((String) null, typeface).applyTo((TextView) findViewById(R.id.skip));
    }

    public void setSkipTextTypeface(@Nullable String typeURL) {
        new TypefaceContainer(typeURL, 0).applyTo((TextView) findViewById(R.id.skip));
    }

    public void setDoneText(@Nullable CharSequence text) {
        ((TextView) findViewById(R.id.done)).setText(text);
    }

    public void setDoneTextTypeface(@Nullable String typeURL) {
        new TypefaceContainer(typeURL, 0).applyTo((TextView) findViewById(R.id.done));
    }

    public void setDoneTextTypeface(@FontRes int typeface) {
        new TypefaceContainer((String) null, typeface).applyTo((TextView) findViewById(R.id.done));
    }

    public void setColorDoneText(@ColorInt int colorDoneText) {
        ((TextView) findViewById(R.id.done)).setTextColor(colorDoneText);
    }

    public void setColorSkipButton(@ColorInt int colorSkipButton) {
        ((TextView) findViewById(R.id.skip)).setTextColor(colorSkipButton);
    }

    public void setImageNextButton(Drawable imageNextButton) {
        ((ImageView) findViewById(R.id.next)).setImageDrawable(imageNextButton);
    }

    @Deprecated
    public void showDoneButton(boolean showDone) {
        setProgressButtonEnabled(showDone);
    }

    public void showSeparator(boolean showSeparator) {
        TextView bottomSeparator = (TextView) findViewById(R.id.bottom_separator);
        if (showSeparator) {
            bottomSeparator.setVisibility(0);
        } else {
            bottomSeparator.setVisibility(4);
        }
    }
}

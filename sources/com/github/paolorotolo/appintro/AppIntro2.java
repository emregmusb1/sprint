package com.github.paolorotolo.appintro;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.ColorInt;
import com.github.paolorotolo.appintro.util.LogHelper;

public abstract class AppIntro2 extends AppIntroBase {
    private static final String TAG = LogHelper.makeLogTag(AppIntro2.class);
    protected FrameLayout backgroundFrame;
    protected View customBackgroundView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.backgroundFrame = (FrameLayout) findViewById(R.id.background);
    }

    /* access modifiers changed from: protected */
    public int getLayoutId() {
        return R.layout.appintro_intro_layout2;
    }

    @Deprecated
    public void showDoneButton(boolean showDone) {
        setProgressButtonEnabled(showDone);
    }

    public void setBarColor(@ColorInt int color) {
        ((LinearLayout) findViewById(R.id.bottom)).setBackgroundColor(color);
    }

    public void setImageSkipButton(Drawable imageSkipButton) {
        ((ImageButton) findViewById(R.id.skip)).setImageDrawable(imageSkipButton);
    }

    public void setBackgroundView(View view) {
        this.customBackgroundView = view;
        View view2 = this.customBackgroundView;
        if (view2 != null) {
            this.backgroundFrame.addView(view2);
        }
    }
}

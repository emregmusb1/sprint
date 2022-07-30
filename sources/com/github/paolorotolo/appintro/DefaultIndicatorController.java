package com.github.paolorotolo.appintro;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

class DefaultIndicatorController implements IndicatorController {
    public static final int DEFAULT_COLOR = 1;
    private static final int FIRST_PAGE_NUM = 0;
    private Context mContext;
    int mCurrentPosition;
    private LinearLayout mDotLayout;
    private List<ImageView> mDots;
    private int mSlideCount;
    int selectedDotColor = 1;
    int unselectedDotColor = 1;

    DefaultIndicatorController() {
    }

    public View newInstance(@NonNull Context context) {
        this.mContext = context;
        this.mDotLayout = (LinearLayout) View.inflate(context, R.layout.appintro_default_indicator, (ViewGroup) null);
        return this.mDotLayout;
    }

    public void initialize(int slideCount) {
        this.mDots = new ArrayList();
        this.mSlideCount = slideCount;
        this.selectedDotColor = -1;
        this.unselectedDotColor = -1;
        for (int i = 0; i < slideCount; i++) {
            ImageView dot = new ImageView(this.mContext);
            dot.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.appintro_indicator_dot_grey));
            this.mDotLayout.addView(dot, new LinearLayout.LayoutParams(-2, -2));
            this.mDots.add(dot);
        }
        selectPosition(0);
    }

    public void selectPosition(int index) {
        this.mCurrentPosition = index;
        int i = 0;
        while (i < this.mSlideCount) {
            Drawable drawable = ContextCompat.getDrawable(this.mContext, i == index ? R.drawable.appintro_indicator_dot_white : R.drawable.appintro_indicator_dot_grey);
            if (this.selectedDotColor != 1 && i == index) {
                drawable.mutate().setColorFilter(this.selectedDotColor, PorterDuff.Mode.SRC_IN);
            }
            if (!(this.unselectedDotColor == 1 || i == index)) {
                drawable.mutate().setColorFilter(this.unselectedDotColor, PorterDuff.Mode.SRC_IN);
            }
            this.mDots.get(i).setImageDrawable(drawable);
            i++;
        }
    }

    public void setSelectedIndicatorColor(int color) {
        this.selectedDotColor = color;
        selectPosition(this.mCurrentPosition);
    }

    public void setUnselectedIndicatorColor(int color) {
        this.unselectedDotColor = color;
        selectPosition(this.mCurrentPosition);
    }
}

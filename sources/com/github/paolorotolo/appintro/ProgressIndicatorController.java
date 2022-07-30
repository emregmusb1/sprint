package com.github.paolorotolo.appintro;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;

public class ProgressIndicatorController implements IndicatorController {
    public static final int DEFAULT_COLOR = 1;
    private static final int FIRST_PAGE_NUM = 0;
    private ProgressBar mProgressBar;
    int selectedDotColor = 1;
    int unselectedDotColor = 1;

    public View newInstance(@NonNull Context context) {
        this.mProgressBar = (ProgressBar) View.inflate(context, R.layout.appintro_progress_indicator, (ViewGroup) null);
        if (this.selectedDotColor != 1) {
            this.mProgressBar.getProgressDrawable().setColorFilter(this.selectedDotColor, PorterDuff.Mode.SRC_IN);
        }
        if (this.unselectedDotColor != 1) {
            this.mProgressBar.getIndeterminateDrawable().setColorFilter(this.unselectedDotColor, PorterDuff.Mode.SRC_IN);
        }
        return this.mProgressBar;
    }

    public void initialize(int slideCount) {
        this.mProgressBar.setMax(slideCount);
        selectPosition(0);
    }

    public void selectPosition(int index) {
        this.mProgressBar.setProgress(index + 1);
    }

    public void setSelectedIndicatorColor(int color) {
        this.selectedDotColor = color;
        ProgressBar progressBar = this.mProgressBar;
        if (progressBar != null) {
            progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public void setUnselectedIndicatorColor(int color) {
        this.unselectedDotColor = color;
        ProgressBar progressBar = this.mProgressBar;
        if (progressBar != null) {
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }
}

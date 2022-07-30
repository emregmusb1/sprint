package com.github.paolorotolo.appintro;

import androidx.annotation.ColorInt;

public interface ISlideBackgroundColorHolder {
    @ColorInt
    int getDefaultBackgroundColor();

    void setBackgroundColor(@ColorInt int i);
}

package com.github.paolorotolo.appintro.model;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;

public class SliderPagerBuilder {
    @ColorInt
    private int bgColor;
    @ColorInt
    private int descColor;
    private String descTypeface;
    @FontRes
    private int descTypefaceRes;
    private CharSequence description;
    @DrawableRes
    private int imageDrawable;
    private CharSequence title;
    @ColorInt
    private int titleColor;
    private String titleTypeface;
    @FontRes
    private int titleTypefaceRes;

    public SliderPagerBuilder title(CharSequence title2) {
        this.title = title2;
        return this;
    }

    public SliderPagerBuilder description(CharSequence description2) {
        this.description = description2;
        return this;
    }

    public SliderPagerBuilder imageDrawable(int imageDrawable2) {
        this.imageDrawable = imageDrawable2;
        return this;
    }

    public SliderPagerBuilder bgColor(int bgColor2) {
        this.bgColor = bgColor2;
        return this;
    }

    public SliderPagerBuilder titleColor(int titleColor2) {
        this.titleColor = titleColor2;
        return this;
    }

    public SliderPagerBuilder descColor(int descColor2) {
        this.descColor = descColor2;
        return this;
    }

    public SliderPagerBuilder titleTypeface(String titleTypeface2) {
        this.titleTypeface = titleTypeface2;
        return this;
    }

    public SliderPagerBuilder titleTypefaceRes(@FontRes int titleTypefaceRes2) {
        this.titleTypefaceRes = titleTypefaceRes2;
        return this;
    }

    public SliderPagerBuilder descTypeface(String descTypeface2) {
        this.descTypeface = descTypeface2;
        return this;
    }

    public SliderPagerBuilder descTypefaceRes(@FontRes int descTypefaceRes2) {
        this.descTypefaceRes = descTypefaceRes2;
        return this;
    }

    public SliderPage build() {
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle(this.title);
        sliderPage.setDescription(this.description);
        sliderPage.setImageDrawable(this.imageDrawable);
        sliderPage.setBgColor(this.bgColor);
        sliderPage.setTitleColor(this.titleColor);
        sliderPage.setDescColor(this.descColor);
        sliderPage.setTitleTypeface(this.titleTypeface);
        sliderPage.setTitleTypefaceFontRes(this.titleTypefaceRes);
        sliderPage.setDescTypeface(this.descTypeface);
        sliderPage.setDescTypefaceFontRes(this.descTypefaceRes);
        return sliderPage;
    }
}

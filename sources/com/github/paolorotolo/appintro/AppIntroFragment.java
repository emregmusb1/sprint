package com.github.paolorotolo.appintro;

import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import com.github.paolorotolo.appintro.model.SliderPage;

public final class AppIntroFragment extends AppIntroBaseFragment {
    @Deprecated
    public static AppIntroFragment newInstance(CharSequence title, CharSequence description, @DrawableRes int imageDrawable, @ColorInt int bgColor) {
        return newInstance(title, (String) null, description, (String) null, imageDrawable, bgColor, 0, 0);
    }

    @Deprecated
    public static AppIntroFragment newInstance(CharSequence title, String titleTypeface, CharSequence description, String descTypeface, @DrawableRes int imageDrawable, @ColorInt int bgColor) {
        return newInstance(title, titleTypeface, description, descTypeface, imageDrawable, bgColor, 0, 0);
    }

    @Deprecated
    public static AppIntroFragment newInstance(CharSequence title, @FontRes int titleTypeface, CharSequence description, @FontRes int descTypeface, @DrawableRes int imageDrawable, @ColorInt int bgColor) {
        return newInstance(title, titleTypeface, description, descTypeface, imageDrawable, bgColor, 0, 0);
    }

    public static AppIntroFragment newInstance(CharSequence title, String titleTypefaceUrl, CharSequence description, String descTypefaceUrl, @DrawableRes int imageDrawable, @ColorInt int bgColor, @ColorInt int titleColor, @ColorInt int descColor) {
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle(title);
        sliderPage.setTitleTypeface(titleTypefaceUrl);
        sliderPage.setDescription(description);
        sliderPage.setDescTypeface(descTypefaceUrl);
        sliderPage.setImageDrawable(imageDrawable);
        sliderPage.setBgColor(bgColor);
        sliderPage.setTitleColor(titleColor);
        sliderPage.setDescColor(descColor);
        return newInstance(sliderPage);
    }

    public static AppIntroFragment newInstance(CharSequence title, @FontRes int titleTypefaceRes, CharSequence description, @FontRes int descTypefaceRes, @DrawableRes int imageDrawable, @ColorInt int bgColor, @ColorInt int titleColor, @ColorInt int descColor) {
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle(title);
        sliderPage.setTitleTypefaceFontRes(titleTypefaceRes);
        sliderPage.setDescription(description);
        sliderPage.setDescTypefaceFontRes(descTypefaceRes);
        sliderPage.setImageDrawable(imageDrawable);
        sliderPage.setBgColor(bgColor);
        sliderPage.setTitleColor(titleColor);
        sliderPage.setDescColor(descColor);
        return newInstance(sliderPage);
    }

    public static AppIntroFragment newInstance(@NonNull SliderPage sliderPage) {
        AppIntroFragment slide = new AppIntroFragment();
        Bundle args = new Bundle();
        args.putString("title", sliderPage.getTitleString());
        args.putString("title_typeface", sliderPage.getTitleTypeface());
        args.putInt("title_typeface_res", sliderPage.getTitleTypefaceFontRes());
        args.putInt("title_color", sliderPage.getTitleColor());
        args.putString("desc", sliderPage.getDescriptionString());
        args.putString("desc_typeface", sliderPage.getDescTypeface());
        args.putInt("desc_typeface_res", sliderPage.getDescTypefaceFontRes());
        args.putInt("desc_color", sliderPage.getDescColor());
        args.putInt("drawable", sliderPage.getImageDrawable());
        args.putInt("bg_color", sliderPage.getBgColor());
        slide.setArguments(args);
        return slide;
    }

    /* access modifiers changed from: protected */
    public int getLayoutId() {
        return R.layout.appintro_fragment_intro;
    }
}

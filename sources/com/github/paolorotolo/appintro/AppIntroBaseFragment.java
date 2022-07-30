package com.github.paolorotolo.appintro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.paolorotolo.appintro.util.LogHelper;
import com.github.paolorotolo.appintro.util.TypefaceContainer;

public abstract class AppIntroBaseFragment extends Fragment implements ISlideSelectionListener, ISlideBackgroundColorHolder {
    protected static final String ARG_BG_COLOR = "bg_color";
    protected static final String ARG_DESC = "desc";
    protected static final String ARG_DESC_COLOR = "desc_color";
    protected static final String ARG_DESC_TYPEFACE = "desc_typeface";
    protected static final String ARG_DESC_TYPEFACE_RES = "desc_typeface_res";
    protected static final String ARG_DRAWABLE = "drawable";
    protected static final String ARG_TITLE = "title";
    protected static final String ARG_TITLE_COLOR = "title_color";
    protected static final String ARG_TITLE_TYPEFACE = "title_typeface";
    protected static final String ARG_TITLE_TYPEFACE_RES = "title_typeface_res";
    private static final String TAG = LogHelper.makeLogTag(AppIntroBaseFragment.class);
    private int bgColor;
    private int descColor;
    private TypefaceContainer descTypeface;
    private String description;
    private int drawable;
    private int layoutId;
    private LinearLayout mainLayout;
    private String title;
    private int titleColor;
    private TypefaceContainer titleTypeface;

    /* access modifiers changed from: protected */
    @LayoutRes
    public abstract int getLayoutId();

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null && getArguments().size() != 0) {
            String argsTitleTypeface = getArguments().getString(ARG_TITLE_TYPEFACE);
            String argsDescTypeface = getArguments().getString(ARG_DESC_TYPEFACE);
            int argsTitleTypefaceRes = getArguments().getInt(ARG_TITLE_TYPEFACE_RES);
            int argsDescTypefaceRes = getArguments().getInt(ARG_DESC_TYPEFACE_RES);
            this.drawable = getArguments().getInt(ARG_DRAWABLE);
            this.title = getArguments().getString(ARG_TITLE);
            this.description = getArguments().getString(ARG_DESC);
            this.titleTypeface = new TypefaceContainer(argsTitleTypeface, argsTitleTypefaceRes);
            this.descTypeface = new TypefaceContainer(argsDescTypeface, argsDescTypefaceRes);
            this.bgColor = getArguments().getInt(ARG_BG_COLOR);
            int i = 0;
            this.titleColor = getArguments().containsKey(ARG_TITLE_COLOR) ? getArguments().getInt(ARG_TITLE_COLOR) : 0;
            if (getArguments().containsKey(ARG_DESC_COLOR)) {
                i = getArguments().getInt(ARG_DESC_COLOR);
            }
            this.descColor = i;
        }
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            this.drawable = savedInstanceState.getInt(ARG_DRAWABLE);
            this.title = savedInstanceState.getString(ARG_TITLE);
            this.description = savedInstanceState.getString(ARG_DESC);
            this.titleTypeface = new TypefaceContainer(savedInstanceState.getString(ARG_TITLE_TYPEFACE), savedInstanceState.getInt(ARG_TITLE_TYPEFACE_RES, 0));
            this.descTypeface = new TypefaceContainer(savedInstanceState.getString(ARG_DESC_TYPEFACE), savedInstanceState.getInt(ARG_DESC_TYPEFACE_RES, 0));
            this.bgColor = savedInstanceState.getInt(ARG_BG_COLOR);
            this.titleColor = savedInstanceState.getInt(ARG_TITLE_COLOR);
            this.descColor = savedInstanceState.getInt(ARG_DESC_COLOR);
        }
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        TextView titleText = (TextView) view.findViewById(R.id.title);
        TextView descriptionText = (TextView) view.findViewById(R.id.description);
        ImageView slideImage = (ImageView) view.findViewById(R.id.image);
        this.mainLayout = (LinearLayout) view.findViewById(R.id.main);
        titleText.setText(this.title);
        int i = this.titleColor;
        if (i != 0) {
            titleText.setTextColor(i);
        }
        this.titleTypeface.applyTo(titleText);
        this.titleTypeface.applyTo(descriptionText);
        descriptionText.setText(this.description);
        int i2 = this.descColor;
        if (i2 != 0) {
            descriptionText.setTextColor(i2);
        }
        slideImage.setImageResource(this.drawable);
        this.mainLayout.setBackgroundColor(this.bgColor);
        return view;
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(ARG_DRAWABLE, this.drawable);
        outState.putString(ARG_TITLE, this.title);
        outState.putString(ARG_DESC, this.description);
        outState.putInt(ARG_BG_COLOR, this.bgColor);
        outState.putInt(ARG_TITLE_COLOR, this.titleColor);
        outState.putInt(ARG_DESC_COLOR, this.descColor);
        saveTypefacesInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void saveTypefacesInstanceState(Bundle outState) {
        TypefaceContainer typefaceContainer = this.titleTypeface;
        if (typefaceContainer != null) {
            outState.putString(ARG_TITLE_TYPEFACE, typefaceContainer.getTypeFaceUrl());
            outState.putInt(ARG_TITLE_TYPEFACE_RES, this.titleTypeface.getTypeFaceResource());
        }
        TypefaceContainer typefaceContainer2 = this.descTypeface;
        if (typefaceContainer2 != null) {
            outState.putString(ARG_DESC_TYPEFACE, typefaceContainer2.getTypeFaceUrl());
            outState.putInt(ARG_DESC_TYPEFACE_RES, this.descTypeface.getTypeFaceResource());
        }
    }

    public void onSlideDeselected() {
        LogHelper.d(TAG, String.format("Slide %s has been deselected.", new Object[]{this.title}));
    }

    public void onSlideSelected() {
        LogHelper.d(TAG, String.format("Slide %s has been selected.", new Object[]{this.title}));
    }

    public int getDefaultBackgroundColor() {
        return this.bgColor;
    }

    public void setBackgroundColor(@ColorInt int backgroundColor) {
        this.mainLayout.setBackgroundColor(backgroundColor);
    }
}

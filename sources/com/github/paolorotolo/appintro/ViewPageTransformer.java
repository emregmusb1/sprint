package com.github.paolorotolo.appintro;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

class ViewPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_ALPHA_SLIDE = 0.35f;
    private static final float MIN_ALPHA_ZOOM = 0.5f;
    private static final float MIN_SCALE_DEPTH = 0.75f;
    private static final float MIN_SCALE_ZOOM = 0.85f;
    private static final float SCALE_FACTOR_SLIDE = 0.85f;
    private final TransformType mTransformType;

    enum TransformType {
        FLOW,
        DEPTH,
        ZOOM,
        SLIDE_OVER,
        FADE
    }

    ViewPageTransformer(TransformType transformType) {
        this.mTransformType = transformType;
    }

    /* renamed from: com.github.paolorotolo.appintro.ViewPageTransformer$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$github$paolorotolo$appintro$ViewPageTransformer$TransformType = new int[TransformType.values().length];

        static {
            try {
                $SwitchMap$com$github$paolorotolo$appintro$ViewPageTransformer$TransformType[TransformType.FLOW.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$github$paolorotolo$appintro$ViewPageTransformer$TransformType[TransformType.SLIDE_OVER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$github$paolorotolo$appintro$ViewPageTransformer$TransformType[TransformType.DEPTH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$github$paolorotolo$appintro$ViewPageTransformer$TransformType[TransformType.ZOOM.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$github$paolorotolo$appintro$ViewPageTransformer$TransformType[TransformType.FADE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public void transformPage(@NonNull View page, float position) {
        float translationX;
        float alpha;
        float scale;
        int i = AnonymousClass1.$SwitchMap$com$github$paolorotolo$appintro$ViewPageTransformer$TransformType[this.mTransformType.ordinal()];
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i == 5) {
                            if (position <= -1.0f || position >= 1.0f) {
                                page.setAlpha(0.0f);
                                page.setClickable(false);
                                return;
                            } else if (position == 0.0f) {
                                page.setAlpha(1.0f);
                                page.setClickable(true);
                                return;
                            } else {
                                page.setAlpha(1.0f - Math.abs(position));
                                return;
                            }
                        } else {
                            return;
                        }
                    } else if (position < -1.0f || position > 1.0f) {
                        alpha = 1.0f;
                        scale = 1.0f;
                        translationX = 0.0f;
                    } else {
                        scale = Math.max(0.85f, 1.0f - Math.abs(position));
                        alpha = (((scale - 0.85f) / 0.14999998f) * MIN_ALPHA_ZOOM) + MIN_ALPHA_ZOOM;
                        float vMargin = (((float) page.getHeight()) * (1.0f - scale)) / 2.0f;
                        float hMargin = (((float) page.getWidth()) * (1.0f - scale)) / 2.0f;
                        if (position < 0.0f) {
                            translationX = hMargin - (vMargin / 2.0f);
                        } else {
                            translationX = (vMargin / 2.0f) + (-hMargin);
                        }
                    }
                } else if (position <= 0.0f || position >= 1.0f) {
                    alpha = 1.0f;
                    scale = 1.0f;
                    translationX = 0.0f;
                } else {
                    alpha = 1.0f - position;
                    scale = MIN_SCALE_DEPTH + ((1.0f - Math.abs(position)) * 0.25f);
                    translationX = (-position) * ((float) page.getWidth());
                }
            } else if (position >= 0.0f || position <= -1.0f) {
                alpha = 1.0f;
                scale = 1.0f;
                translationX = 0.0f;
            } else {
                scale = (Math.abs(Math.abs(position) - 1.0f) * 0.14999998f) + 0.85f;
                alpha = Math.max(MIN_ALPHA_SLIDE, 1.0f - Math.abs(position));
                int pageWidth = page.getWidth();
                translationX = ((float) (-pageWidth)) * position;
                if (translationX > ((float) (-pageWidth))) {
                    float f = translationX;
                } else {
                    translationX = 0.0f;
                }
            }
            page.setAlpha(alpha);
            page.setTranslationX(translationX);
            page.setScaleX(scale);
            page.setScaleY(scale);
            return;
        }
        page.setRotationY(-30.0f * position);
    }
}

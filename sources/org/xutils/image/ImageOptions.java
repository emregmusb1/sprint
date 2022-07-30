package org.xutils.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import java.lang.reflect.Field;
import org.xutils.common.util.DensityUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;

public class ImageOptions {
    public static final ImageOptions DEFAULT = new ImageOptions();
    /* access modifiers changed from: private */
    public Animation animation = null;
    /* access modifiers changed from: private */
    public boolean autoRotate = false;
    /* access modifiers changed from: private */
    public boolean circular = false;
    private boolean compress = true;
    /* access modifiers changed from: private */
    public Bitmap.Config config = Bitmap.Config.RGB_565;
    /* access modifiers changed from: private */
    public boolean crop = false;
    /* access modifiers changed from: private */
    public boolean fadeIn = false;
    /* access modifiers changed from: private */
    public Drawable failureDrawable = null;
    /* access modifiers changed from: private */
    public int failureDrawableId = 0;
    /* access modifiers changed from: private */
    public boolean forceLoadingDrawable = true;
    /* access modifiers changed from: private */
    public int height = 0;
    /* access modifiers changed from: private */
    public boolean ignoreGif = true;
    /* access modifiers changed from: private */
    public ImageView.ScaleType imageScaleType = ImageView.ScaleType.CENTER_CROP;
    /* access modifiers changed from: private */
    public Drawable loadingDrawable = null;
    /* access modifiers changed from: private */
    public int loadingDrawableId = 0;
    private int maxHeight = 0;
    private int maxWidth = 0;
    /* access modifiers changed from: private */
    public ParamsBuilder paramsBuilder;
    /* access modifiers changed from: private */
    public ImageView.ScaleType placeholderScaleType = ImageView.ScaleType.CENTER_INSIDE;
    /* access modifiers changed from: private */
    public int radius = 0;
    /* access modifiers changed from: private */
    public boolean square = false;
    /* access modifiers changed from: private */
    public boolean useMemCache = true;
    /* access modifiers changed from: private */
    public int width = 0;

    public interface ParamsBuilder {
        RequestParams buildParams(RequestParams requestParams, ImageOptions imageOptions);
    }

    protected ImageOptions() {
    }

    /* access modifiers changed from: package-private */
    public final void optimizeMaxSize(ImageView view) {
        int i;
        int i2 = this.width;
        if (i2 <= 0 || (i = this.height) <= 0) {
            int screenWidth = DensityUtil.getScreenWidth();
            int screenHeight = DensityUtil.getScreenHeight();
            if (this == DEFAULT) {
                int i3 = (screenWidth * 3) / 2;
                this.width = i3;
                this.maxWidth = i3;
                int i4 = (screenHeight * 3) / 2;
                this.height = i4;
                this.maxHeight = i4;
                return;
            }
            if (this.width < 0) {
                this.maxWidth = (screenWidth * 3) / 2;
                this.compress = false;
            }
            if (this.height < 0) {
                this.maxHeight = (screenHeight * 3) / 2;
                this.compress = false;
            }
            if (view != null || this.maxWidth > 0 || this.maxHeight > 0) {
                int tempWidth = this.maxWidth;
                int tempHeight = this.maxHeight;
                if (view != null) {
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    if (params != null) {
                        if (tempWidth <= 0) {
                            if (params.width > 0) {
                                tempWidth = params.width;
                                if (this.width <= 0) {
                                    this.width = tempWidth;
                                }
                            } else if (params.width != -2) {
                                tempWidth = view.getWidth();
                            }
                        }
                        if (tempHeight <= 0) {
                            if (params.height > 0) {
                                tempHeight = params.height;
                                if (this.height <= 0) {
                                    this.height = tempHeight;
                                }
                            } else if (params.height != -2) {
                                tempHeight = view.getHeight();
                            }
                        }
                    }
                    if (tempWidth <= 0) {
                        tempWidth = getImageViewFieldValue(view, "mMaxWidth");
                    }
                    if (tempHeight <= 0) {
                        tempHeight = getImageViewFieldValue(view, "mMaxHeight");
                    }
                }
                if (tempWidth <= 0) {
                    tempWidth = screenWidth;
                }
                if (tempHeight <= 0) {
                    tempHeight = screenHeight;
                }
                this.maxWidth = tempWidth;
                this.maxHeight = tempHeight;
                return;
            }
            this.maxWidth = screenWidth;
            this.maxHeight = screenHeight;
            return;
        }
        this.maxWidth = i2;
        this.maxHeight = i;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isCrop() {
        return this.crop;
    }

    public int getRadius() {
        return this.radius;
    }

    public boolean isSquare() {
        return this.square;
    }

    public boolean isCircular() {
        return this.circular;
    }

    public boolean isIgnoreGif() {
        return this.ignoreGif;
    }

    public boolean isAutoRotate() {
        return this.autoRotate;
    }

    public boolean isCompress() {
        return this.compress;
    }

    public Bitmap.Config getConfig() {
        return this.config;
    }

    public Drawable getLoadingDrawable(ImageView view) {
        if (this.loadingDrawable == null && this.loadingDrawableId > 0 && view != null) {
            try {
                this.loadingDrawable = view.getResources().getDrawable(this.loadingDrawableId);
            } catch (Throwable ex) {
                LogUtil.e(ex.getMessage(), ex);
            }
        }
        return this.loadingDrawable;
    }

    public Drawable getFailureDrawable(ImageView view) {
        if (this.failureDrawable == null && this.failureDrawableId > 0 && view != null) {
            try {
                this.failureDrawable = view.getResources().getDrawable(this.failureDrawableId);
            } catch (Throwable ex) {
                LogUtil.e(ex.getMessage(), ex);
            }
        }
        return this.failureDrawable;
    }

    public boolean isFadeIn() {
        return this.fadeIn;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public ImageView.ScaleType getPlaceholderScaleType() {
        return this.placeholderScaleType;
    }

    public ImageView.ScaleType getImageScaleType() {
        return this.imageScaleType;
    }

    public boolean isForceLoadingDrawable() {
        return this.forceLoadingDrawable;
    }

    public boolean isUseMemCache() {
        return this.useMemCache;
    }

    public ParamsBuilder getParamsBuilder() {
        return this.paramsBuilder;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImageOptions options = (ImageOptions) o;
        if (this.maxWidth == options.maxWidth && this.maxHeight == options.maxHeight && this.width == options.width && this.height == options.height && this.crop == options.crop && this.radius == options.radius && this.square == options.square && this.circular == options.circular && this.autoRotate == options.autoRotate && this.compress == options.compress && this.config == options.config) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = ((((((((((((((((((this.maxWidth * 31) + this.maxHeight) * 31) + this.width) * 31) + this.height) * 31) + (this.crop ? 1 : 0)) * 31) + this.radius) * 31) + (this.square ? 1 : 0)) * 31) + (this.circular ? 1 : 0)) * 31) + (this.autoRotate ? 1 : 0)) * 31) + (this.compress ? 1 : 0)) * 31;
        Bitmap.Config config2 = this.config;
        return result + (config2 != null ? config2.hashCode() : 0);
    }

    public String toString() {
        return "_" + this.maxWidth + "_" + this.maxHeight + "_" + this.width + "_" + this.height + "_" + this.radius + "_" + this.config + "_" + (this.crop ? 1 : 0) + (this.square ? 1 : 0) + (this.circular ? 1 : 0) + (this.autoRotate ? 1 : 0) + (this.compress ? 1 : 0);
    }

    private static int getImageViewFieldValue(ImageView view, String fieldName) {
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = ((Integer) field.get(view)).intValue();
            if (fieldValue <= 0 || fieldValue >= Integer.MAX_VALUE) {
                return 0;
            }
            return fieldValue;
        } catch (Throwable ex) {
            LogUtil.w(ex.getMessage(), ex);
            return 0;
        }
    }

    public static class Builder {
        protected ImageOptions options;

        public Builder() {
            newImageOptions();
        }

        /* access modifiers changed from: protected */
        public void newImageOptions() {
            this.options = new ImageOptions();
        }

        public ImageOptions build() {
            return this.options;
        }

        public Builder setSize(int width, int height) {
            int unused = this.options.width = width;
            int unused2 = this.options.height = height;
            return this;
        }

        public Builder setCrop(boolean crop) {
            boolean unused = this.options.crop = crop;
            return this;
        }

        public Builder setRadius(int radius) {
            int unused = this.options.radius = radius;
            return this;
        }

        public Builder setSquare(boolean square) {
            boolean unused = this.options.square = square;
            return this;
        }

        public Builder setCircular(boolean circular) {
            boolean unused = this.options.circular = circular;
            return this;
        }

        public Builder setAutoRotate(boolean autoRotate) {
            boolean unused = this.options.autoRotate = autoRotate;
            return this;
        }

        public Builder setConfig(Bitmap.Config config) {
            Bitmap.Config unused = this.options.config = config;
            return this;
        }

        public Builder setIgnoreGif(boolean ignoreGif) {
            boolean unused = this.options.ignoreGif = ignoreGif;
            return this;
        }

        public Builder setLoadingDrawableId(int loadingDrawableId) {
            int unused = this.options.loadingDrawableId = loadingDrawableId;
            return this;
        }

        public Builder setLoadingDrawable(Drawable loadingDrawable) {
            Drawable unused = this.options.loadingDrawable = loadingDrawable;
            return this;
        }

        public Builder setFailureDrawableId(int failureDrawableId) {
            int unused = this.options.failureDrawableId = failureDrawableId;
            return this;
        }

        public Builder setFailureDrawable(Drawable failureDrawable) {
            Drawable unused = this.options.failureDrawable = failureDrawable;
            return this;
        }

        public Builder setFadeIn(boolean fadeIn) {
            boolean unused = this.options.fadeIn = fadeIn;
            return this;
        }

        public Builder setAnimation(Animation animation) {
            Animation unused = this.options.animation = animation;
            return this;
        }

        public Builder setPlaceholderScaleType(ImageView.ScaleType placeholderScaleType) {
            ImageView.ScaleType unused = this.options.placeholderScaleType = placeholderScaleType;
            return this;
        }

        public Builder setImageScaleType(ImageView.ScaleType imageScaleType) {
            ImageView.ScaleType unused = this.options.imageScaleType = imageScaleType;
            return this;
        }

        public Builder setForceLoadingDrawable(boolean forceLoadingDrawable) {
            boolean unused = this.options.forceLoadingDrawable = forceLoadingDrawable;
            return this;
        }

        public Builder setUseMemCache(boolean useMemCache) {
            boolean unused = this.options.useMemCache = useMemCache;
            return this;
        }

        public Builder setParamsBuilder(ParamsBuilder paramsBuilder) {
            ParamsBuilder unused = this.options.paramsBuilder = paramsBuilder;
            return this;
        }
    }
}

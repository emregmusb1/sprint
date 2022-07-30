package org.xutils.image;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import java.lang.ref.WeakReference;

public final class AsyncDrawable extends Drawable {
    private Drawable baseDrawable;
    private final WeakReference<ImageLoader> imageLoaderReference;

    public AsyncDrawable(ImageLoader imageLoader, Drawable drawable) {
        if (imageLoader != null) {
            this.baseDrawable = drawable;
            while (true) {
                Drawable drawable2 = this.baseDrawable;
                if (drawable2 instanceof AsyncDrawable) {
                    this.baseDrawable = ((AsyncDrawable) drawable2).baseDrawable;
                } else {
                    this.imageLoaderReference = new WeakReference<>(imageLoader);
                    return;
                }
            }
        } else {
            throw new IllegalArgumentException("imageLoader may not be null");
        }
    }

    public ImageLoader getImageLoader() {
        return (ImageLoader) this.imageLoaderReference.get();
    }

    public void setBaseDrawable(Drawable baseDrawable2) {
        this.baseDrawable = baseDrawable2;
    }

    public Drawable getBaseDrawable() {
        return this.baseDrawable;
    }

    public void draw(Canvas canvas) {
        Drawable drawable = this.baseDrawable;
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    public void setAlpha(int i) {
        Drawable drawable = this.baseDrawable;
        if (drawable != null) {
            drawable.setAlpha(i);
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        Drawable drawable = this.baseDrawable;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
    }

    public int getOpacity() {
        Drawable drawable = this.baseDrawable;
        if (drawable == null) {
            return -3;
        }
        return drawable.getOpacity();
    }

    public void setBounds(int left, int top, int right, int bottom) {
        Drawable drawable = this.baseDrawable;
        if (drawable != null) {
            drawable.setBounds(left, top, right, bottom);
        }
    }

    public void setBounds(Rect bounds) {
        Drawable drawable = this.baseDrawable;
        if (drawable != null) {
            drawable.setBounds(bounds);
        }
    }

    public void setChangingConfigurations(int configs) {
        Drawable drawable = this.baseDrawable;
        if (drawable != null) {
            drawable.setChangingConfigurations(configs);
        }
    }

    public int getChangingConfigurations() {
        Drawable drawable = this.baseDrawable;
        if (drawable == null) {
            return 0;
        }
        return drawable.getChangingConfigurations();
    }

    public void setDither(boolean dither) {
        Drawable drawable = this.baseDrawable;
        if (drawable != null) {
            drawable.setDither(dither);
        }
    }

    public void setFilterBitmap(boolean filter) {
        Drawable drawable = this.baseDrawable;
        if (drawable != null) {
            drawable.setFilterBitmap(filter);
        }
    }

    public void invalidateSelf() {
        Drawable drawable = this.baseDrawable;
        if (drawable != null) {
            drawable.invalidateSelf();
        }
    }

    public void scheduleSelf(Runnable what, long when) {
        Drawable drawable = this.baseDrawable;
        if (drawable != null) {
            drawable.scheduleSelf(what, when);
        }
    }

    public void unscheduleSelf(Runnable what) {
        Drawable drawable = this.baseDrawable;
        if (drawable != null) {
            drawable.unscheduleSelf(what);
        }
    }

    public void setColorFilter(int color, PorterDuff.Mode mode) {
        Drawable drawable = this.baseDrawable;
        if (drawable != null) {
            drawable.setColorFilter(color, mode);
        }
    }

    public void clearColorFilter() {
        Drawable drawable = this.baseDrawable;
        if (drawable != null) {
            drawable.clearColorFilter();
        }
    }

    public boolean isStateful() {
        Drawable drawable = this.baseDrawable;
        return drawable != null && drawable.isStateful();
    }

    public boolean setState(int[] stateSet) {
        Drawable drawable = this.baseDrawable;
        return drawable != null && drawable.setState(stateSet);
    }

    public int[] getState() {
        Drawable drawable = this.baseDrawable;
        if (drawable == null) {
            return null;
        }
        return drawable.getState();
    }

    public Drawable getCurrent() {
        Drawable drawable = this.baseDrawable;
        if (drawable == null) {
            return null;
        }
        return drawable.getCurrent();
    }

    public boolean setVisible(boolean visible, boolean restart) {
        Drawable drawable = this.baseDrawable;
        return drawable != null && drawable.setVisible(visible, restart);
    }

    public Region getTransparentRegion() {
        Drawable drawable = this.baseDrawable;
        if (drawable == null) {
            return null;
        }
        return drawable.getTransparentRegion();
    }

    public int getIntrinsicWidth() {
        Drawable drawable = this.baseDrawable;
        if (drawable == null) {
            return 0;
        }
        return drawable.getIntrinsicWidth();
    }

    public int getIntrinsicHeight() {
        Drawable drawable = this.baseDrawable;
        if (drawable == null) {
            return 0;
        }
        return drawable.getIntrinsicHeight();
    }

    public int getMinimumWidth() {
        Drawable drawable = this.baseDrawable;
        if (drawable == null) {
            return 0;
        }
        return drawable.getMinimumWidth();
    }

    public int getMinimumHeight() {
        Drawable drawable = this.baseDrawable;
        if (drawable == null) {
            return 0;
        }
        return drawable.getMinimumHeight();
    }

    public boolean getPadding(Rect padding) {
        Drawable drawable = this.baseDrawable;
        return drawable != null && drawable.getPadding(padding);
    }

    public Drawable mutate() {
        Drawable drawable = this.baseDrawable;
        if (drawable == null) {
            return null;
        }
        return drawable.mutate();
    }

    public Drawable.ConstantState getConstantState() {
        Drawable drawable = this.baseDrawable;
        if (drawable == null) {
            return null;
        }
        return drawable.getConstantState();
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
        ImageLoader imageLoader = getImageLoader();
        if (imageLoader != null) {
            imageLoader.cancel();
        }
    }
}

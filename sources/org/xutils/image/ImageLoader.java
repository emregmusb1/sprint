package org.xutils.image;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.widget.ImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.xutils.cache.LruCache;
import org.xutils.cache.LruDiskCache;
import org.xutils.common.Callback;
import org.xutils.common.task.Priority;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.common.util.IOUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.FileLockedException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

final class ImageLoader implements Callback.PrepareCallback<File, Drawable>, Callback.CacheCallback<Drawable>, Callback.ProgressCallback<Drawable>, Callback.TypedCallback<Drawable>, Callback.Cancelable {
    private static final String DISK_CACHE_DIR_NAME = "xUtils_img";
    private static final Executor EXECUTOR = new PriorityExecutor(10, false);
    private static final HashMap<String, FakeImageView> FAKE_IMG_MAP = new HashMap<>();
    private static final LruCache<MemCacheKey, Drawable> MEM_CACHE = new LruCache<MemCacheKey, Drawable>(4194304) {
        private boolean deepClear = false;

        /* access modifiers changed from: protected */
        public int sizeOf(MemCacheKey key, Drawable value) {
            if (value instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) value).getBitmap();
                if (bitmap == null) {
                    return 0;
                }
                return bitmap.getByteCount();
            } else if (value instanceof GifDrawable) {
                return ((GifDrawable) value).getByteCount();
            } else {
                return super.sizeOf(key, value);
            }
        }

        public void trimToSize(int maxSize) {
            if (maxSize < 0) {
                this.deepClear = true;
            }
            super.trimToSize(maxSize);
            this.deepClear = false;
        }

        /* access modifiers changed from: protected */
        public void entryRemoved(boolean evicted, MemCacheKey key, Drawable oldValue, Drawable newValue) {
            super.entryRemoved(evicted, key, oldValue, newValue);
            if (evicted && this.deepClear && (oldValue instanceof ReusableDrawable)) {
                ((ReusableDrawable) oldValue).setMemCacheKey((MemCacheKey) null);
            }
        }
    };
    private static final int MEM_CACHE_MIN_SIZE = 4194304;
    private static final AtomicLong SEQ_SEEK = new AtomicLong(0);
    private static final Type loadType = File.class;
    private Callback.CacheCallback<Drawable> cacheCallback;
    /* access modifiers changed from: private */
    public Callback.CommonCallback<Drawable> callback;
    private volatile boolean cancelled = false;
    private boolean hasCache = false;
    private Callback.Cancelable httpCancelable;
    /* access modifiers changed from: private */
    public MemCacheKey key;
    /* access modifiers changed from: private */
    public ImageOptions options;
    private Callback.PrepareCallback<File, Drawable> prepareCallback;
    private Callback.ProgressCallback<Drawable> progressCallback;
    private final long seq = SEQ_SEEK.incrementAndGet();
    private volatile boolean skipOnFinishedCallback = false;
    private volatile boolean skipOnWaitingCallback = false;
    private volatile boolean stopped = false;
    /* access modifiers changed from: private */
    public WeakReference<ImageView> viewRef;

    static {
        int cacheSize = (1048576 * ((ActivityManager) x.app().getSystemService("activity")).getMemoryClass()) / 8;
        if (cacheSize < 4194304) {
            cacheSize = 4194304;
        }
        MEM_CACHE.resize(cacheSize);
    }

    private ImageLoader() {
    }

    static void clearMemCache() {
        MEM_CACHE.evictAll();
    }

    static void clearCacheFiles() {
        LruDiskCache.getDiskCache(DISK_CACHE_DIR_NAME).clearCacheFiles();
    }

    static Callback.Cancelable doLoadDrawable(String url, ImageOptions options2, Callback.CommonCallback<Drawable> callback2) {
        if (!TextUtils.isEmpty(url)) {
            return doBind(new FakeImageView(), url, options2, callback2);
        }
        postArgsException((ImageView) null, options2, "url is null", callback2);
        return null;
    }

    static Callback.Cancelable doLoadFile(String url, ImageOptions options2, Callback.CacheCallback<File> callback2) {
        if (TextUtils.isEmpty(url)) {
            postArgsException((ImageView) null, options2, "url is null", callback2);
            return null;
        }
        return x.http().get(createRequestParams((Context) null, url, options2), callback2);
    }

    static Callback.Cancelable doBind(ImageView view, String url, ImageOptions options2, Callback.CommonCallback<Drawable> callback2) {
        Bitmap bitmap;
        MemCacheKey oldKey;
        ImageOptions localOptions = options2;
        if (view == null) {
            postArgsException((ImageView) null, localOptions, "view is null", callback2);
            return null;
        } else if (TextUtils.isEmpty(url)) {
            postArgsException(view, localOptions, "url is null", callback2);
            return null;
        } else {
            if (localOptions == null) {
                localOptions = ImageOptions.DEFAULT;
            }
            localOptions.optimizeMaxSize(view);
            MemCacheKey key2 = new MemCacheKey(url, localOptions);
            Drawable oldDrawable = view.getDrawable();
            if (oldDrawable instanceof AsyncDrawable) {
                ImageLoader loader = ((AsyncDrawable) oldDrawable).getImageLoader();
                if (loader != null && !loader.stopped) {
                    if (key2.equals(loader.key)) {
                        return null;
                    }
                    loader.cancel();
                }
            } else if ((oldDrawable instanceof ReusableDrawable) && (oldKey = ((ReusableDrawable) oldDrawable).getMemCacheKey()) != null && oldKey.equals(key2)) {
                MEM_CACHE.put(key2, oldDrawable);
            }
            Drawable memDrawable = null;
            if (localOptions.isUseMemCache()) {
                memDrawable = MEM_CACHE.get(key2);
                if ((memDrawable instanceof BitmapDrawable) && ((bitmap = ((BitmapDrawable) memDrawable).getBitmap()) == null || bitmap.isRecycled())) {
                    memDrawable = null;
                }
            }
            if (memDrawable == null) {
                return new ImageLoader().doLoad(view, url, localOptions, callback2);
            }
            boolean trustMemCache = false;
            try {
                if (callback2 instanceof Callback.ProgressCallback) {
                    ((Callback.ProgressCallback) callback2).onWaiting();
                }
            } catch (Throwable ex) {
                try {
                    LogUtil.e(ex.getMessage(), ex);
                    ImageLoader loader2 = new ImageLoader();
                    loader2.skipOnWaitingCallback = true;
                    Callback.Cancelable doLoad = loader2.doLoad(view, url, localOptions, callback2);
                    if (!(0 == 0 || callback2 == null)) {
                        try {
                            callback2.onFinished();
                        } catch (Throwable ex2) {
                            LogUtil.e(ex2.getMessage(), ex2);
                        }
                    }
                    return doLoad;
                } catch (Throwable ex3) {
                    LogUtil.e(ex3.getMessage(), ex3);
                }
            }
            if (callback2 instanceof Callback.CacheCallback) {
                try {
                    trustMemCache = ((Callback.CacheCallback) callback2).onCache(memDrawable);
                } catch (Throwable ex4) {
                    LogUtil.e(ex4.getMessage(), ex4);
                }
            } else {
                trustMemCache = true;
            }
            if (trustMemCache) {
                view.setScaleType(localOptions.getImageScaleType());
                view.setImageDrawable(memDrawable);
                if (callback2 != null) {
                    try {
                        callback2.onSuccess(memDrawable);
                    } catch (Throwable ex5) {
                        callback2.onError(ex5, true);
                    }
                }
                if (trustMemCache && callback2 != null) {
                    try {
                        callback2.onFinished();
                    } catch (Throwable ex6) {
                        LogUtil.e(ex6.getMessage(), ex6);
                    }
                }
                return null;
            }
            ImageLoader loader3 = new ImageLoader();
            loader3.skipOnWaitingCallback = true;
            Callback.Cancelable doLoad2 = loader3.doLoad(view, url, localOptions, callback2);
            if (trustMemCache && callback2 != null) {
                try {
                    callback2.onFinished();
                } catch (Throwable ex7) {
                    LogUtil.e(ex7.getMessage(), ex7);
                }
            }
            return doLoad2;
        }
        throw th;
    }

    private Callback.Cancelable doLoad(ImageView view, String url, ImageOptions options2, Callback.CommonCallback<Drawable> callback2) {
        this.viewRef = new WeakReference<>(view);
        this.options = options2;
        this.key = new MemCacheKey(url, options2);
        this.callback = callback2;
        if (callback2 instanceof Callback.ProgressCallback) {
            this.progressCallback = (Callback.ProgressCallback) callback2;
        }
        if (callback2 instanceof Callback.PrepareCallback) {
            this.prepareCallback = (Callback.PrepareCallback) callback2;
        }
        if (callback2 instanceof Callback.CacheCallback) {
            this.cacheCallback = (Callback.CacheCallback) callback2;
        }
        Drawable loadingDrawable = view.getDrawable();
        if (loadingDrawable == null || options2.isForceLoadingDrawable()) {
            loadingDrawable = options2.getLoadingDrawable(view);
            view.setScaleType(options2.getPlaceholderScaleType());
        }
        view.setImageDrawable(new AsyncDrawable(this, loadingDrawable));
        RequestParams params = createRequestParams(view.getContext(), url, options2);
        if (view instanceof FakeImageView) {
            synchronized (FAKE_IMG_MAP) {
                HashMap<String, FakeImageView> hashMap = FAKE_IMG_MAP;
                hashMap.put(view.hashCode() + url, (FakeImageView) view);
            }
        }
        Callback.Cancelable cancelable = x.http().get(params, this);
        this.httpCancelable = cancelable;
        return cancelable;
    }

    public void cancel() {
        this.stopped = true;
        this.cancelled = true;
        Callback.Cancelable cancelable = this.httpCancelable;
        if (cancelable != null) {
            cancelable.cancel();
        }
    }

    public boolean isCancelled() {
        return this.cancelled || !validView4Callback(false);
    }

    public void onWaiting() {
        Callback.ProgressCallback<Drawable> progressCallback2;
        if (!this.skipOnWaitingCallback && (progressCallback2 = this.progressCallback) != null) {
            progressCallback2.onWaiting();
        }
    }

    public void onStarted() {
        Callback.ProgressCallback<Drawable> progressCallback2;
        if (validView4Callback(true) && (progressCallback2 = this.progressCallback) != null) {
            progressCallback2.onStarted();
        }
    }

    public void onLoading(long total, long current, boolean isDownloading) {
        Callback.ProgressCallback<Drawable> progressCallback2;
        if (validView4Callback(true) && (progressCallback2 = this.progressCallback) != null) {
            progressCallback2.onLoading(total, current, isDownloading);
        }
    }

    public Type getLoadType() {
        return loadType;
    }

    public Drawable prepare(File rawData) throws Throwable {
        if (!validView4Callback(true)) {
            return null;
        }
        if (rawData.exists()) {
            Drawable result = null;
            try {
                if (this.prepareCallback != null) {
                    result = this.prepareCallback.prepare(rawData);
                }
                if (result == null) {
                    result = ImageDecoder.decodeFileWithLock(rawData, this.options, this);
                }
                if (result != null && (result instanceof ReusableDrawable)) {
                    ((ReusableDrawable) result).setMemCacheKey(this.key);
                    MEM_CACHE.put(this.key, result);
                }
                return result;
            } catch (IOException ex) {
                IOUtil.deleteFileOrDir(rawData);
                throw ex;
            }
        } else {
            throw new FileNotFoundException(rawData.getAbsolutePath());
        }
    }

    public boolean onCache(Drawable result) {
        if (!validView4Callback(true) || result == null) {
            return false;
        }
        this.hasCache = true;
        setSuccessDrawable4Callback(result);
        Callback.CacheCallback<Drawable> cacheCallback2 = this.cacheCallback;
        if (cacheCallback2 != null) {
            return cacheCallback2.onCache(result);
        }
        Callback.CommonCallback<Drawable> commonCallback = this.callback;
        if (commonCallback == null) {
            return true;
        }
        commonCallback.onSuccess(result);
        return true;
    }

    public void onSuccess(Drawable result) {
        if (validView4Callback(!this.hasCache) && result != null) {
            setSuccessDrawable4Callback(result);
            Callback.CommonCallback<Drawable> commonCallback = this.callback;
            if (commonCallback != null) {
                commonCallback.onSuccess(result);
            }
        }
    }

    public void onError(Throwable ex, boolean isOnCallback) {
        this.stopped = true;
        if (validView4Callback(false)) {
            if (ex instanceof FileLockedException) {
                LogUtil.d("ImageFileLocked: " + this.key.url);
                x.task().postDelayed(new Runnable() {
                    public void run() {
                        ImageView imageView = (ImageView) ImageLoader.this.viewRef.get();
                        if (imageView != null) {
                            ImageLoader.doBind(imageView, ImageLoader.this.key.url, ImageLoader.this.options, ImageLoader.this.callback);
                        } else {
                            ImageLoader.this.onFinished();
                        }
                    }
                }, 10);
                this.skipOnFinishedCallback = true;
                return;
            }
            LogUtil.e(this.key.url, ex);
            setErrorDrawable4Callback();
            Callback.CommonCallback<Drawable> commonCallback = this.callback;
            if (commonCallback != null) {
                commonCallback.onError(ex, isOnCallback);
            }
        }
    }

    public void onCancelled(Callback.CancelledException cex) {
        Callback.CommonCallback<Drawable> commonCallback;
        this.stopped = true;
        if (validView4Callback(false) && (commonCallback = this.callback) != null) {
            commonCallback.onCancelled(cex);
        }
    }

    public void onFinished() {
        this.stopped = true;
        if (!this.skipOnFinishedCallback) {
            ImageView view = (ImageView) this.viewRef.get();
            if (view instanceof FakeImageView) {
                synchronized (FAKE_IMG_MAP) {
                    HashMap<String, FakeImageView> hashMap = FAKE_IMG_MAP;
                    hashMap.remove(view.hashCode() + this.key.url);
                }
            }
            Callback.CommonCallback<Drawable> commonCallback = this.callback;
            if (commonCallback != null) {
                commonCallback.onFinished();
            }
        }
    }

    private static RequestParams createRequestParams(Context context, String url, ImageOptions options2) {
        ImageOptions.ParamsBuilder paramsBuilder;
        RequestParams params = new RequestParams(url);
        if (context != null) {
            params.setContext(context);
        }
        params.setCacheDirName(DISK_CACHE_DIR_NAME);
        params.setConnectTimeout(8000);
        params.setPriority(Priority.BG_LOW);
        params.setExecutor(EXECUTOR);
        params.setCancelFast(true);
        params.setUseCookie(false);
        if (options2 == null || (paramsBuilder = options2.getParamsBuilder()) == null) {
            return params;
        }
        return paramsBuilder.buildParams(params, options2);
    }

    private boolean validView4Callback(boolean forceValidAsyncDrawable) {
        ImageView view = (ImageView) this.viewRef.get();
        if (view == null) {
            return false;
        }
        Drawable otherDrawable = view.getDrawable();
        if (otherDrawable instanceof AsyncDrawable) {
            ImageLoader otherLoader = ((AsyncDrawable) otherDrawable).getImageLoader();
            if (otherLoader == null || otherLoader == this) {
                return true;
            }
            if (this.seq > otherLoader.seq) {
                otherLoader.cancel();
                return true;
            }
            cancel();
            return false;
        } else if (forceValidAsyncDrawable) {
            cancel();
            return false;
        }
        return true;
    }

    private void setSuccessDrawable4Callback(Drawable drawable) {
        ImageView view = (ImageView) this.viewRef.get();
        if (view != null) {
            view.setScaleType(this.options.getImageScaleType());
            if (drawable instanceof GifDrawable) {
                if (view.getScaleType() == ImageView.ScaleType.CENTER) {
                    view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
                view.setLayerType(1, (Paint) null);
            }
            if (this.options.getAnimation() != null) {
                ImageAnimationHelper.animationDisplay(view, drawable, this.options.getAnimation());
            } else if (this.options.isFadeIn()) {
                ImageAnimationHelper.fadeInDisplay(view, drawable);
            } else {
                view.setImageDrawable(drawable);
            }
        }
    }

    private void setErrorDrawable4Callback() {
        ImageView view = (ImageView) this.viewRef.get();
        if (view != null) {
            Drawable drawable = this.options.getFailureDrawable(view);
            view.setScaleType(this.options.getPlaceholderScaleType());
            view.setImageDrawable(drawable);
        }
    }

    private static void postArgsException(final ImageView view, final ImageOptions options2, final String exMsg, final Callback.CommonCallback<?> callback2) {
        x.task().autoPost(new Runnable() {
            public void run() {
                try {
                    if (callback2 instanceof Callback.ProgressCallback) {
                        ((Callback.ProgressCallback) callback2).onWaiting();
                    }
                    if (!(view == null || options2 == null)) {
                        view.setScaleType(options2.getPlaceholderScaleType());
                        view.setImageDrawable(options2.getFailureDrawable(view));
                    }
                    if (callback2 != null) {
                        callback2.onError(new IllegalArgumentException(exMsg), false);
                    }
                    Callback.CommonCallback commonCallback = callback2;
                    if (commonCallback != null) {
                        commonCallback.onFinished();
                        return;
                    }
                    return;
                } catch (Throwable throwable) {
                    LogUtil.e(throwable.getMessage(), throwable);
                    return;
                }
                Callback.CommonCallback commonCallback2 = callback2;
                if (commonCallback2 != null) {
                    commonCallback2.onFinished();
                    return;
                }
                return;
                throw th;
            }
        });
    }

    @SuppressLint({"ViewConstructor", "AppCompatCustomView"})
    private static final class FakeImageView extends ImageView {
        private static final AtomicInteger hashCodeSeed = new AtomicInteger(0);
        private Drawable drawable;
        private final int hashCode = hashCodeSeed.incrementAndGet();

        public FakeImageView() {
            super(x.app());
        }

        public int hashCode() {
            return this.hashCode;
        }

        public void setImageDrawable(Drawable drawable2) {
            this.drawable = drawable2;
        }

        public Drawable getDrawable() {
            return this.drawable;
        }

        public void setLayerType(int layerType, Paint paint) {
        }

        public void setScaleType(ImageView.ScaleType scaleType) {
        }

        public void startAnimation(Animation animation) {
        }
    }
}

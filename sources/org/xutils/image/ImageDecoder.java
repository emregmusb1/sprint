package org.xutils.image;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.widget.ImageView;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import org.xutils.cache.DiskCacheEntity;
import org.xutils.cache.DiskCacheFile;
import org.xutils.cache.LruDiskCache;
import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.common.util.IOUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.x;

public final class ImageDecoder {
    private static final int BITMAP_DECODE_MAX_WORKER;
    private static final byte[] GIF_HEADER = {71, 73, 70};
    private static final LruDiskCache THUMB_CACHE = LruDiskCache.getDiskCache("xUtils_img_thumb");
    private static final Executor THUMB_CACHE_EXECUTOR = new PriorityExecutor(1, true);
    private static final Object bitmapDecodeLock = new Object();
    private static final AtomicInteger bitmapDecodeWorker = new AtomicInteger(0);
    private static final Object gifDecodeLock = new Object();

    static {
        int i = 1;
        if (Runtime.getRuntime().availableProcessors() > 4) {
            i = 2;
        }
        BITMAP_DECODE_MAX_WORKER = i;
    }

    private ImageDecoder() {
    }

    static void clearCacheFiles() {
        THUMB_CACHE.clearCacheFiles();
    }

    static Drawable decodeFileWithLock(final File file, final ImageOptions options, Callback.Cancelable cancelable) throws IOException {
        Bitmap finalBitmap;
        Movie movie;
        if (file == null || !file.exists() || file.length() < 1) {
            return null;
        }
        if (cancelable != null && cancelable.isCancelled()) {
            throw new Callback.CancelledException("cancelled during decode image");
        } else if (options.isIgnoreGif() || !isGif(file)) {
            Bitmap bitmap = null;
            try {
                synchronized (bitmapDecodeLock) {
                    while (bitmapDecodeWorker.get() >= BITMAP_DECODE_MAX_WORKER && (cancelable == null || !cancelable.isCancelled())) {
                        try {
                            bitmapDecodeLock.wait();
                        } catch (InterruptedException e) {
                            throw new Callback.CancelledException("cancelled during decode image");
                        } catch (Throwable th) {
                        }
                    }
                }
                if (cancelable != null) {
                    if (cancelable.isCancelled()) {
                        throw new Callback.CancelledException("cancelled during decode image");
                    }
                }
                bitmapDecodeWorker.incrementAndGet();
                if (options.isCompress()) {
                    bitmap = getThumbCache(file, options);
                }
                if (bitmap == null) {
                    Bitmap bitmap2 = decodeBitmap(file, options, cancelable);
                    if (bitmap2 != null && options.isCompress()) {
                        final Bitmap finalBitmap2 = bitmap2;
                        THUMB_CACHE_EXECUTOR.execute(new Runnable() {
                            public void run() {
                                ImageDecoder.saveThumbCache(file, options, finalBitmap2);
                            }
                        });
                    }
                    finalBitmap = bitmap2;
                } else {
                    finalBitmap = bitmap;
                }
                if (1 != 0) {
                    bitmapDecodeWorker.decrementAndGet();
                }
                synchronized (bitmapDecodeLock) {
                    bitmapDecodeLock.notifyAll();
                }
                if (finalBitmap != null) {
                    return new ReusableBitmapDrawable(x.app().getResources(), finalBitmap);
                }
                return null;
            } catch (Throwable th2) {
                if (0 != 0) {
                    bitmapDecodeWorker.decrementAndGet();
                }
                synchronized (bitmapDecodeLock) {
                    bitmapDecodeLock.notifyAll();
                    throw th2;
                }
            }
        } else {
            synchronized (gifDecodeLock) {
                movie = decodeGif(file, options, cancelable);
            }
            if (movie != null) {
                return new GifDrawable(movie, (int) file.length());
            }
            return null;
        }
    }

    /* JADX INFO: finally extract failed */
    public static boolean isGif(File file) {
        try {
            FileInputStream in = new FileInputStream(file);
            boolean equals = Arrays.equals(GIF_HEADER, IOUtil.readBytes(in, 0, 3));
            IOUtil.closeQuietly((Closeable) in);
            return equals;
        } catch (Throwable th) {
            IOUtil.closeQuietly((Closeable) null);
            throw th;
        }
    }

    public static Bitmap decodeBitmap(File file, ImageOptions options, Callback.Cancelable cancelable) throws IOException {
        if (file == null || !file.exists() || file.length() < 1) {
            return null;
        }
        if (options == null) {
            options = ImageOptions.DEFAULT;
        }
        if (options.getMaxWidth() <= 0 || options.getMaxHeight() <= 0) {
            options.optimizeMaxSize((ImageView) null);
        }
        if (cancelable != null) {
            try {
                if (cancelable.isCancelled()) {
                    throw new Callback.CancelledException("cancelled during decode image");
                }
            } catch (Callback.CancelledException ex) {
                throw ex;
            } catch (IOException ex2) {
                throw ex2;
            } catch (Throwable ex3) {
                LogUtil.e(ex3.getMessage(), ex3);
                return null;
            }
        }
        BitmapFactory.Options bitmapOps = new BitmapFactory.Options();
        bitmapOps.inJustDecodeBounds = true;
        bitmapOps.inPurgeable = true;
        bitmapOps.inInputShareable = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOps);
        bitmapOps.inJustDecodeBounds = false;
        bitmapOps.inPreferredConfig = options.getConfig();
        int rotateAngle = 0;
        int rawWidth = bitmapOps.outWidth;
        int rawHeight = bitmapOps.outHeight;
        int optionWith = options.getWidth();
        int optionHeight = options.getHeight();
        if (options.isAutoRotate()) {
            rotateAngle = getRotateAngle(file.getAbsolutePath());
            if ((rotateAngle / 90) % 2 == 1) {
                rawWidth = bitmapOps.outHeight;
                rawHeight = bitmapOps.outWidth;
            }
        }
        if (!options.isCrop() && optionWith > 0 && optionHeight > 0) {
            if ((rotateAngle / 90) % 2 == 1) {
                bitmapOps.outWidth = optionHeight;
                bitmapOps.outHeight = optionWith;
            } else {
                bitmapOps.outWidth = optionWith;
                bitmapOps.outHeight = optionHeight;
            }
        }
        bitmapOps.inSampleSize = calculateSampleSize(rawWidth, rawHeight, options.getMaxWidth(), options.getMaxHeight());
        if (cancelable != null) {
            if (cancelable.isCancelled()) {
                throw new Callback.CancelledException("cancelled during decode image");
            }
        }
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bitmapOps);
        if (bitmap != null) {
            if (cancelable != null) {
                if (cancelable.isCancelled()) {
                    throw new Callback.CancelledException("cancelled during decode image");
                }
            }
            if (rotateAngle != 0) {
                bitmap = rotate(bitmap, rotateAngle, true);
            }
            if (cancelable != null) {
                if (cancelable.isCancelled()) {
                    throw new Callback.CancelledException("cancelled during decode image");
                }
            }
            if (options.isCrop() && optionWith > 0 && optionHeight > 0) {
                bitmap = cut2ScaleSize(bitmap, optionWith, optionHeight, true);
            }
            if (bitmap != null) {
                if (cancelable != null) {
                    if (cancelable.isCancelled()) {
                        throw new Callback.CancelledException("cancelled during decode image");
                    }
                }
                if (options.isCircular()) {
                    bitmap = cut2Circular(bitmap, true);
                } else if (options.getRadius() > 0) {
                    bitmap = cut2RoundCorner(bitmap, options.getRadius(), options.isSquare(), true);
                } else if (options.isSquare()) {
                    bitmap = cut2Square(bitmap, true);
                }
                if (bitmap != null) {
                    return bitmap;
                }
                throw new IOException("decode image error");
            }
            throw new IOException("decode image error");
        }
        throw new IOException("decode image error");
    }

    public static Movie decodeGif(File file, ImageOptions options, Callback.Cancelable cancelable) throws IOException {
        if (file == null || !file.exists() || file.length() < 1) {
            return null;
        }
        if (cancelable != null) {
            try {
                if (cancelable.isCancelled()) {
                    throw new Callback.CancelledException("cancelled during decode image");
                }
            } catch (Callback.CancelledException ex) {
                throw ex;
            } catch (IOException ex2) {
                throw ex2;
            } catch (Throwable ex3) {
                LogUtil.e(ex3.getMessage(), ex3);
                return null;
            }
        }
        Movie movie = Movie.decodeFile(file.getAbsolutePath());
        if (movie != null) {
            return movie;
        }
        throw new IOException("decode image error");
    }

    public static int calculateSampleSize(int rawWidth, int rawHeight, int maxWidth, int maxHeight) {
        int sampleSize = 1;
        if (rawWidth > maxWidth || rawHeight > maxHeight) {
            if (rawWidth > rawHeight) {
                sampleSize = Math.round(((float) rawHeight) / ((float) maxHeight));
            } else {
                sampleSize = Math.round(((float) rawWidth) / ((float) maxWidth));
            }
            if (sampleSize < 1) {
                sampleSize = 1;
            }
            while (((float) (rawWidth * rawHeight)) / ((float) (sampleSize * sampleSize)) > ((float) (maxWidth * maxHeight * 2))) {
                sampleSize++;
            }
        }
        return sampleSize;
    }

    public static Bitmap cut2Square(Bitmap source, boolean recycleSource) {
        int width = source.getWidth();
        int height = source.getHeight();
        if (width == height) {
            return source;
        }
        int squareWith = Math.min(width, height);
        Bitmap result = Bitmap.createBitmap(source, (width - squareWith) / 2, (height - squareWith) / 2, squareWith, squareWith);
        if (result == null) {
            return source;
        }
        if (!recycleSource || result == source) {
            return result;
        }
        source.recycle();
        return result;
    }

    public static Bitmap cut2Circular(Bitmap source, boolean recycleSource) {
        int width = source.getWidth();
        int height = source.getHeight();
        int diameter = Math.min(width, height);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap result = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        if (result == null) {
            return source;
        }
        Canvas canvas = new Canvas(result);
        canvas.drawCircle((float) (diameter / 2), (float) (diameter / 2), (float) (diameter / 2), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, (float) ((diameter - width) / 2), (float) ((diameter - height) / 2), paint);
        if (!recycleSource) {
            return result;
        }
        source.recycle();
        return result;
    }

    public static Bitmap cut2RoundCorner(Bitmap source, int radius, boolean isSquare, boolean recycleSource) {
        if (radius <= 0) {
            return source;
        }
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        int targetWidth = sourceWidth;
        int targetHeight = sourceHeight;
        if (isSquare) {
            int min = Math.min(sourceWidth, sourceHeight);
            targetHeight = min;
            targetWidth = min;
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap result = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        if (result == null) {
            return source;
        }
        Canvas canvas = new Canvas(result);
        canvas.drawRoundRect(new RectF(0.0f, 0.0f, (float) targetWidth, (float) targetHeight), (float) radius, (float) radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, (float) ((targetWidth - sourceWidth) / 2), (float) ((targetHeight - sourceHeight) / 2), paint);
        if (!recycleSource) {
            return result;
        }
        source.recycle();
        return result;
    }

    public static Bitmap cut2ScaleSize(Bitmap source, int dstWidth, int dstHeight, boolean recycleSource) {
        int b;
        int r;
        int t;
        int l;
        Bitmap bitmap = source;
        int i = dstWidth;
        int i2 = dstHeight;
        int width = source.getWidth();
        int height = source.getHeight();
        if (width == i && height == i2) {
            return bitmap;
        }
        Matrix m = new Matrix();
        int i3 = width;
        int i4 = height;
        float sx = ((float) i) / ((float) width);
        float sy = ((float) i2) / ((float) height);
        if (sx > sy) {
            sy = sx;
            l = 0;
            t = (int) ((((float) height) - (((float) i2) / sx)) / 2.0f);
            r = width;
            b = (int) ((((float) height) + (((float) i2) / sx)) / 2.0f);
        } else {
            sx = sy;
            l = (int) ((((float) width) - (((float) i) / sx)) / 2.0f);
            t = 0;
            r = (int) ((((float) width) + (((float) i) / sx)) / 2.0f);
            b = height;
        }
        m.setScale(sx, sy);
        Bitmap result = Bitmap.createBitmap(source, l, t, r - l, b - t, m, true);
        if (result == null) {
            return source;
        }
        if (!recycleSource || result == bitmap) {
            return result;
        }
        source.recycle();
        return result;
    }

    public static Bitmap rotate(Bitmap source, int angle, boolean recycleSource) {
        Bitmap result = null;
        if (angle != 0) {
            Matrix m = new Matrix();
            m.setRotate((float) angle);
            try {
                result = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), m, true);
            } catch (Throwable ex) {
                LogUtil.e(ex.getMessage(), ex);
            }
        }
        if (result == null) {
            return source;
        }
        if (!recycleSource || result == source) {
            return result;
        }
        source.recycle();
        return result;
    }

    @SuppressLint({"ExifInterface"})
    public static int getRotateAngle(String filePath) {
        try {
            int orientation = new ExifInterface(filePath).getAttributeInt("Orientation", 0);
            if (orientation == 3) {
                return 180;
            }
            if (orientation == 6) {
                return 90;
            }
            if (orientation != 8) {
                return 0;
            }
            return 270;
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
            return 0;
        }
    }

    /* access modifiers changed from: private */
    public static void saveThumbCache(File file, ImageOptions options, Bitmap thumbBitmap) {
        DiskCacheEntity entity = new DiskCacheEntity();
        entity.setKey(file.getAbsolutePath() + "@" + file.lastModified() + options.toString());
        DiskCacheFile cacheFile = null;
        OutputStream out = null;
        try {
            cacheFile = THUMB_CACHE.createDiskCacheFile(entity);
            if (cacheFile != null) {
                out = new FileOutputStream(cacheFile);
                thumbBitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
                out.flush();
                cacheFile = cacheFile.commit();
            }
        } catch (Throwable th) {
            IOUtil.closeQuietly((Closeable) null);
            IOUtil.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtil.closeQuietly((Closeable) cacheFile);
        IOUtil.closeQuietly((Closeable) out);
    }

    private static Bitmap getThumbCache(File file, ImageOptions options) {
        DiskCacheFile cacheFile = null;
        try {
            LruDiskCache lruDiskCache = THUMB_CACHE;
            cacheFile = lruDiskCache.getDiskCacheFile(file.getAbsolutePath() + "@" + file.lastModified() + options.toString());
            if (cacheFile != null && cacheFile.exists()) {
                BitmapFactory.Options bitmapOps = new BitmapFactory.Options();
                bitmapOps.inJustDecodeBounds = false;
                bitmapOps.inPurgeable = true;
                bitmapOps.inInputShareable = true;
                bitmapOps.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap decodeFile = BitmapFactory.decodeFile(cacheFile.getAbsolutePath(), bitmapOps);
                IOUtil.closeQuietly((Closeable) cacheFile);
                return decodeFile;
            }
        } catch (Throwable th) {
            IOUtil.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtil.closeQuietly((Closeable) cacheFile);
        return null;
    }
}

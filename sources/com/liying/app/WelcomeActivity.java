package com.liying.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import butterknife.BindView;
import com.ggccc.app.hbsdf23.R;
import com.liying.app.update.AppUpdateManagers;

public class WelcomeActivity extends BaseActivity {
    public static boolean hasAppInit;
    @BindView(2131165336)
    ImageView welcomeView;

    public int getLayoutId() {
        return R.layout.activity_welcome;
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        hasAppInit = true;
        checkMultiHosts();
    }

    private void checkMultiHosts() {
        AppUpdateManagers.init(getApplicationContext());
        this.welcomeView.postDelayed(new Runnable() {
            public void run() {
                WelcomeActivity.this.startActivity(new Intent(WelcomeActivity.this.mContext, MainActivity.class));
                WelcomeActivity.this.overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                WelcomeActivity.this.finish();
            }
        }, 1000);
    }

    public static void scaleImage(final Activity activity, final View view, int drawableResId) {
        Point outSize = new Point();
        activity.getWindow().getWindowManager().getDefaultDisplay().getSize(outSize);
        Bitmap resourceBitmap = BitmapFactory.decodeResource(activity.getResources(), drawableResId);
        if (resourceBitmap != null) {
            final Bitmap scaledBitmap = Bitmap.createScaledBitmap(resourceBitmap, outSize.x, Math.round((((float) (resourceBitmap.getHeight() * outSize.x)) * 1.0f) / ((float) resourceBitmap.getWidth())), false);
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (scaledBitmap.isRecycled()) {
                        return true;
                    }
                    int offset = (scaledBitmap.getHeight() - view.getMeasuredHeight()) / 2;
                    int offset2 = offset < 0 ? 0 : offset;
                    Bitmap bitmap = scaledBitmap;
                    Bitmap finallyBitmap = Bitmap.createBitmap(bitmap, 0, offset2, bitmap.getWidth(), scaledBitmap.getHeight() - (offset2 * 2));
                    if (!finallyBitmap.equals(scaledBitmap)) {
                        scaledBitmap.recycle();
                        System.gc();
                    }
                    view.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), finallyBitmap));
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    public void onBackPressed() {
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        getWindow().setBackgroundDrawable((Drawable) null);
    }
}

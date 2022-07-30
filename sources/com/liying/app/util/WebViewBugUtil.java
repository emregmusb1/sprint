package com.liying.app.util;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class WebViewBugUtil {
    private Activity activity;
    /* access modifiers changed from: private */
    public int contentHeight;
    private FrameLayout.LayoutParams frameLayoutParams;
    /* access modifiers changed from: private */
    public boolean isfirst = true;
    /* access modifiers changed from: private */
    public View mChildOfContent;
    private int statusBarHeight;
    private int usableHeightPrevious;

    public static void assistActivity(Activity activity2) {
        new WebViewBugUtil(activity2);
    }

    private WebViewBugUtil(Activity activity2) {
        this.statusBarHeight = activity2.getResources().getDimensionPixelSize(activity2.getResources().getIdentifier("status_bar_height", "dimen", "android"));
        this.activity = activity2;
        this.mChildOfContent = ((FrameLayout) activity2.findViewById(16908290)).getChildAt(0);
        this.mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (WebViewBugUtil.this.isfirst) {
                    WebViewBugUtil webViewBugUtil = WebViewBugUtil.this;
                    int unused = webViewBugUtil.contentHeight = webViewBugUtil.mChildOfContent.getHeight();
                    boolean unused2 = WebViewBugUtil.this.isfirst = false;
                }
                WebViewBugUtil.this.possiblyResizeChildOfContent();
            }
        });
        this.frameLayoutParams = (FrameLayout.LayoutParams) this.mChildOfContent.getLayoutParams();
    }

    /* access modifiers changed from: private */
    public void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != this.usableHeightPrevious) {
            int usableHeightSansKeyboard = this.mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference <= usableHeightSansKeyboard / 4) {
                this.frameLayoutParams.height = this.contentHeight;
            } else if (Build.VERSION.SDK_INT >= 19) {
                this.frameLayoutParams.height = (usableHeightSansKeyboard - heightDifference) + this.statusBarHeight;
            } else {
                this.frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            }
            this.mChildOfContent.requestLayout();
            this.usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        this.mChildOfContent.getWindowVisibleDisplayFrame(r);
        return r.bottom - r.top;
    }
}

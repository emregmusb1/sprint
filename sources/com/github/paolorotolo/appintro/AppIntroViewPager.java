package com.github.paolorotolo.appintro;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import androidx.viewpager.widget.ViewPager;
import com.github.paolorotolo.appintro.AppIntroBase;
import com.github.paolorotolo.appintro.util.LayoutUtil;
import com.github.paolorotolo.appintro.util.LogHelper;
import java.lang.reflect.Field;

public final class AppIntroViewPager extends ViewPager {
    private static final int ON_ILLEGALLY_REQUESTED_NEXT_PAGE_MAX_INTERVAL = 1000;
    private static final String TAG = LogHelper.makeLogTag(AppIntroViewPager.class);
    private float currentTouchDownX;
    private long illegallyRequestedNextPageLastCalled;
    private int lockPage = 0;
    private ScrollerCustomDuration mScroller = null;
    private OnNextPageRequestedListener nextPageRequestedListener;
    private boolean nextPagingEnabled = true;
    private ViewPager.OnPageChangeListener pageChangeListener;
    private boolean pagingEnabled = true;

    public interface OnNextPageRequestedListener {
        boolean onCanRequestNextPage();

        void onIllegallyRequestedNextPage();
    }

    public AppIntroViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewPagerScroller();
    }

    public void addOnPageChangeListener(AppIntroBase.PagerOnPageChangeListener listener) {
        super.addOnPageChangeListener(listener);
        this.pageChangeListener = listener;
    }

    public void goToNextSlide() {
        if (LayoutUtil.isRtl(getContext())) {
            setCurrentItem(getCurrentItem() - 1);
        } else {
            setCurrentItem(getCurrentItem() + 1);
        }
    }

    public void goToPreviousSlide() {
        try {
            if (LayoutUtil.isRtl(getContext())) {
                setCurrentItem(getCurrentItem() + 1);
            } else {
                setCurrentItem(getCurrentItem() - 1);
            }
        } catch (Exception e) {
            LogHelper.e(TAG, "goToPreviousSlide: An error occurred while switching to the previous slide. Was isFirstSlide checked before the call?");
        }
    }

    public boolean isFirstSlide(int size) {
        if (LayoutUtil.isRtl(getContext())) {
            if ((getCurrentItem() - size) + 1 == 0) {
                return true;
            }
            return false;
        } else if (getCurrentItem() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setCurrentItem(int item) {
        ViewPager.OnPageChangeListener onPageChangeListener;
        boolean invokeMeLater = false;
        if (super.getCurrentItem() == 0 && item == 0) {
            invokeMeLater = true;
        }
        super.setCurrentItem(item);
        if (invokeMeLater && (onPageChangeListener = this.pageChangeListener) != null) {
            onPageChangeListener.onPageSelected(0);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!this.pagingEnabled) {
            return false;
        }
        if (event.getAction() == 0) {
            this.currentTouchDownX = event.getX();
            return super.onInterceptTouchEvent(event);
        } else if (!checkPagingState(event) && !checkCanRequestNextPage()) {
            return super.onInterceptTouchEvent(event);
        } else {
            checkIllegallyRequestedNextPage(event);
            return false;
        }
    }

    public boolean performClick() {
        return super.performClick();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.pagingEnabled) {
            return false;
        }
        if (event.getAction() == 0) {
            this.currentTouchDownX = event.getX();
            return super.onTouchEvent(event);
        } else if (checkPagingState(event) || checkCanRequestNextPage()) {
            checkIllegallyRequestedNextPage(event);
            return false;
        } else {
            performClick();
            return super.onTouchEvent(event);
        }
    }

    private boolean checkPagingState(MotionEvent event) {
        if (this.nextPagingEnabled) {
            return false;
        }
        if (event.getAction() == 0) {
            this.currentTouchDownX = event.getX();
        }
        if (event.getAction() == 2) {
            return detectSwipeToEnd(event);
        }
        return false;
    }

    private boolean checkCanRequestNextPage() {
        OnNextPageRequestedListener onNextPageRequestedListener = this.nextPageRequestedListener;
        return onNextPageRequestedListener != null && !onNextPageRequestedListener.onCanRequestNextPage();
    }

    private void checkIllegallyRequestedNextPage(MotionEvent event) {
        if (event.getAction() == 2 && Math.abs(event.getX() - this.currentTouchDownX) >= ((float) 25) && System.currentTimeMillis() - this.illegallyRequestedNextPageLastCalled >= 1000) {
            this.illegallyRequestedNextPageLastCalled = System.currentTimeMillis();
            OnNextPageRequestedListener onNextPageRequestedListener = this.nextPageRequestedListener;
            if (onNextPageRequestedListener != null) {
                onNextPageRequestedListener.onIllegallyRequestedNextPage();
            }
        }
    }

    private void initViewPagerScroller() {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);
            this.mScroller = new ScrollerCustomDuration(getContext(), (Interpolator) interpolator.get((Object) null));
            scroller.set(this, this.mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean detectSwipeToEnd(MotionEvent event) {
        boolean result = false;
        try {
            float diffX = event.getX() - this.currentTouchDownX;
            if (Math.abs(diffX) > 0.0f && diffX < 0.0f) {
                result = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        if (LayoutUtil.isRtl(getContext())) {
            return !result;
        }
        return result;
    }

    public void setOnNextPageRequestedListener(OnNextPageRequestedListener nextPageRequestedListener2) {
        this.nextPageRequestedListener = nextPageRequestedListener2;
    }

    public void setScrollDurationFactor(double scrollFactor) {
        this.mScroller.setScrollDurationFactor(scrollFactor);
    }

    public boolean isNextPagingEnabled() {
        return this.nextPagingEnabled;
    }

    public void setNextPagingEnabled(boolean nextPagingEnabled2) {
        this.nextPagingEnabled = nextPagingEnabled2;
        if (!nextPagingEnabled2) {
            this.lockPage = getCurrentItem();
        }
    }

    public boolean isPagingEnabled() {
        return this.pagingEnabled;
    }

    public void setPagingEnabled(boolean pagingEnabled2) {
        this.pagingEnabled = pagingEnabled2;
    }

    public int getLockPage() {
        return this.lockPage;
    }

    public void setLockPage(int lockPage2) {
        this.lockPage = lockPage2;
    }
}

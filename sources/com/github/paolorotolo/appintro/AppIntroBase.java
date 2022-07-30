package com.github.paolorotolo.appintro;

import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.github.paolorotolo.appintro.AppIntroViewPager;
import com.github.paolorotolo.appintro.ViewPageTransformer;
import com.github.paolorotolo.appintro.util.LayoutUtil;
import com.github.paolorotolo.appintro.util.LogHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public abstract class AppIntroBase extends AppCompatActivity implements AppIntroViewPager.OnNextPageRequestedListener {
    public static final int DEFAULT_COLOR = 1;
    private static final int DEFAULT_SCROLL_DURATION_FACTOR = 1;
    private static final String INSTANCE_DATA_COLOR_TRANSITIONS_ENABLED = "com.github.paolorotolo.appintro_color_transitions_enabled";
    private static final String INSTANCE_DATA_IMMERSIVE_MODE_ENABLED = "com.github.paolorotolo.appintro_immersive_mode_enabled";
    private static final String INSTANCE_DATA_IMMERSIVE_MODE_STICKY = "com.github.paolorotolo.appintro_immersive_mode_sticky";
    private static final int PERMISSIONS_REQUEST_ALL_PERMISSIONS = 1;
    private static final String TAG = LogHelper.makeLogTag(AppIntroBase.class);
    /* access modifiers changed from: private */
    public boolean areColorTransitionsEnabled = false;
    /* access modifiers changed from: private */
    public final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    protected View backButton;
    protected boolean baseProgressButtonEnabled = true;
    /* access modifiers changed from: private */
    public int currentlySelectedItem = -1;
    protected View doneButton;
    protected final List<Fragment> fragments = new Vector();
    private GestureDetectorCompat gestureDetector;
    protected FrameLayout indicatorContainer;
    private boolean isGoBackLockEnabled = false;
    /* access modifiers changed from: private */
    public boolean isImmersiveModeEnabled = false;
    /* access modifiers changed from: private */
    public boolean isImmersiveModeSticky = false;
    protected boolean isVibrateOn = false;
    protected boolean isWizardMode = false;
    protected IndicatorController mController;
    protected PagerAdapter mPagerAdapter;
    protected Vibrator mVibrator;
    protected View nextButton;
    protected AppIntroViewPager pager;
    protected boolean pagerIndicatorEnabled = true;
    protected ArrayList<PermissionObject> permissionsArray = new ArrayList<>();
    protected boolean progressButtonEnabled = true;
    protected int savedCurrentItem;
    protected int selectedIndicatorColor = 1;
    protected boolean showBackButtonWithDone = false;
    protected View skipButton;
    protected boolean skipButtonEnabled = true;
    protected int slidesNumber;
    protected int unselectedIndicatorColor = 1;
    protected int vibrateIntensity = 20;

    /* access modifiers changed from: protected */
    public abstract int getLayoutId();

    /* access modifiers changed from: protected */
    @SuppressLint({"MissingPermission"})
    public void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        this.gestureDetector = new GestureDetectorCompat(this, new WindowGestureListener());
        this.nextButton = findViewById(R.id.next);
        this.doneButton = findViewById(R.id.done);
        this.skipButton = findViewById(R.id.skip);
        this.backButton = findViewById(R.id.back);
        checkButton(this.nextButton, "next");
        checkButton(this.doneButton, "done");
        checkButton(this.skipButton, "skip");
        checkButton(this.backButton, "back");
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.bottomContainer);
        if (frameLayout != null && isRtl() && Build.VERSION.SDK_INT >= 17) {
            frameLayout.setLayoutDirection(1);
        }
        if (isRtl()) {
            this.nextButton.setScaleX(-1.0f);
            this.backButton.setScaleX(-1.0f);
        }
        this.mVibrator = (Vibrator) getSystemService("vibrator");
        this.mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), this.fragments);
        this.pager = (AppIntroViewPager) findViewById(R.id.view_pager);
        View view = this.doneButton;
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @SuppressLint({"MissingPermission"})
                public void onClick(@NonNull View v) {
                    if (AppIntroBase.this.isVibrateOn) {
                        AppIntroBase.this.mVibrator.vibrate((long) AppIntroBase.this.vibrateIntensity);
                    }
                    if (!AppIntroBase.this.handleBeforeSlideChanged()) {
                        AppIntroBase.this.handleIllegalSlideChangeAttempt();
                    } else if (!AppIntroBase.this.checkAndRequestPermissions()) {
                        AppIntroBase.this.changeSlide(true);
                    }
                }
            });
        }
        View view2 = this.skipButton;
        if (view2 != null) {
            view2.setOnClickListener(new View.OnClickListener() {
                @SuppressLint({"MissingPermission"})
                public void onClick(@NonNull View v) {
                    if (AppIntroBase.this.isVibrateOn) {
                        AppIntroBase.this.mVibrator.vibrate((long) AppIntroBase.this.vibrateIntensity);
                    }
                    AppIntroBase appIntroBase = AppIntroBase.this;
                    appIntroBase.onSkipPressed(appIntroBase.mPagerAdapter.getItem(AppIntroBase.this.pager.getCurrentItem()));
                }
            });
        }
        View view3 = this.nextButton;
        if (view3 != null) {
            view3.setOnClickListener(new NextButtonOnClickListener());
        }
        View view4 = this.backButton;
        if (view4 != null) {
            view4.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    AppIntroBase.this.pager.goToPreviousSlide();
                }
            });
        }
        this.pager.setAdapter(this.mPagerAdapter);
        this.pager.addOnPageChangeListener(new PagerOnPageChangeListener());
        this.pager.setOnNextPageRequestedListener(this);
        setScrollDurationFactor(1);
    }

    private void checkButton(@Nullable View view, @Nullable String viewName) {
        if (view == null) {
            Log.e(TAG, String.format("View not initialized, missing 'R.id.%1$s' in XML!", new Object[]{viewName}));
        }
    }

    /* access modifiers changed from: protected */
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (this.fragments.size() == 0) {
            init((Bundle) null);
        }
        if (isRtl()) {
            this.pager.setCurrentItem(this.fragments.size() - this.savedCurrentItem);
        } else {
            this.pager.setCurrentItem(this.savedCurrentItem);
        }
        this.pager.post(new Runnable() {
            public void run() {
                if (AppIntroBase.this.mPagerAdapter.getItem(AppIntroBase.this.pager.getCurrentItem()) != null) {
                    AppIntroBase appIntroBase = AppIntroBase.this;
                    appIntroBase.handleSlideChanged((Fragment) null, appIntroBase.mPagerAdapter.getItem(AppIntroBase.this.pager.getCurrentItem()));
                    return;
                }
                AppIntroBase.this.finish();
            }
        });
        this.slidesNumber = this.fragments.size();
        setProgressButtonEnabled(this.progressButtonEnabled);
        initController();
    }

    public void onBackPressed() {
        if (this.isGoBackLockEnabled) {
            return;
        }
        if (!this.pager.isFirstSlide(this.fragments.size())) {
            this.pager.goToPreviousSlide();
        } else {
            super.onBackPressed();
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && this.isImmersiveModeEnabled) {
            setImmersiveMode(true, this.isImmersiveModeSticky);
        }
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (this.isImmersiveModeEnabled) {
            this.gestureDetector.onTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("baseProgressButtonEnabled", this.baseProgressButtonEnabled);
        outState.putBoolean("progressButtonEnabled", this.progressButtonEnabled);
        outState.putBoolean("nextEnabled", this.pager.isPagingEnabled());
        outState.putBoolean("nextPagingEnabled", this.pager.isNextPagingEnabled());
        outState.putBoolean("skipButtonEnabled", this.skipButtonEnabled);
        outState.putBoolean("pagerIndicatorEnabled", this.pagerIndicatorEnabled);
        outState.putInt("lockPage", this.pager.getLockPage());
        outState.putInt("currentItem", this.pager.getCurrentItem());
        outState.putBoolean(INSTANCE_DATA_IMMERSIVE_MODE_ENABLED, this.isImmersiveModeEnabled);
        outState.putBoolean(INSTANCE_DATA_IMMERSIVE_MODE_STICKY, this.isImmersiveModeSticky);
        outState.putBoolean(INSTANCE_DATA_COLOR_TRANSITIONS_ENABLED, this.areColorTransitionsEnabled);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.baseProgressButtonEnabled = savedInstanceState.getBoolean("baseProgressButtonEnabled");
        this.progressButtonEnabled = savedInstanceState.getBoolean("progressButtonEnabled");
        this.skipButtonEnabled = savedInstanceState.getBoolean("skipButtonEnabled");
        this.pagerIndicatorEnabled = savedInstanceState.getBoolean("pagerIndicatorEnabled");
        this.savedCurrentItem = savedInstanceState.getInt("currentItem");
        this.pager.setPagingEnabled(savedInstanceState.getBoolean("nextEnabled"));
        this.pager.setNextPagingEnabled(savedInstanceState.getBoolean("nextPagingEnabled"));
        this.pager.setLockPage(savedInstanceState.getInt("lockPage"));
        this.isImmersiveModeEnabled = savedInstanceState.getBoolean(INSTANCE_DATA_IMMERSIVE_MODE_ENABLED);
        this.isImmersiveModeSticky = savedInstanceState.getBoolean(INSTANCE_DATA_IMMERSIVE_MODE_STICKY);
        this.areColorTransitionsEnabled = savedInstanceState.getBoolean(INSTANCE_DATA_COLOR_TRANSITIONS_ENABLED);
    }

    public boolean onCanRequestNextPage() {
        return handleBeforeSlideChanged();
    }

    public void onIllegallyRequestedNextPage() {
        handleIllegalSlideChangeAttempt();
    }

    private void initController() {
        if (this.mController == null) {
            this.mController = new DefaultIndicatorController();
        }
        this.indicatorContainer = (FrameLayout) findViewById(R.id.indicator_container);
        this.indicatorContainer.addView(this.mController.newInstance(this));
        this.mController.initialize(this.slidesNumber);
        int i = this.selectedIndicatorColor;
        if (i != 1) {
            this.mController.setSelectedIndicatorColor(i);
        }
        int i2 = this.unselectedIndicatorColor;
        if (i2 != 1) {
            this.mController.setUnselectedIndicatorColor(i2);
        }
        this.mController.selectPosition(this.currentlySelectedItem);
    }

    /* access modifiers changed from: private */
    public void handleIllegalSlideChangeAttempt() {
        Fragment currentFragment = this.mPagerAdapter.getItem(this.pager.getCurrentItem());
        if (currentFragment instanceof ISlidePolicy) {
            ISlidePolicy slide = (ISlidePolicy) currentFragment;
            if (!slide.isPolicyRespected()) {
                slide.onUserIllegallyRequestedNextPage();
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean handleBeforeSlideChanged() {
        Fragment currentFragment = this.mPagerAdapter.getItem(this.pager.getCurrentItem());
        LogHelper.d(TAG, String.format("User wants to move away from slide: %s. Checking if this should be allowed...", new Object[]{currentFragment}));
        if (currentFragment instanceof ISlidePolicy) {
            LogHelper.d(TAG, "Current fragment implements ISlidePolicy.");
            if (!((ISlidePolicy) currentFragment).isPolicyRespected()) {
                LogHelper.d(TAG, "Slide policy not respected, denying change request.");
                return false;
            }
        }
        LogHelper.d(TAG, "Change request will be allowed.");
        return true;
    }

    /* access modifiers changed from: private */
    public void handleSlideChanged(Fragment oldFragment, Fragment newFragment) {
        if (oldFragment instanceof ISlideSelectionListener) {
            ((ISlideSelectionListener) oldFragment).onSlideDeselected();
        }
        if (newFragment instanceof ISlideSelectionListener) {
            ((ISlideSelectionListener) newFragment).onSlideSelected();
        }
        onSlideChanged(oldFragment, newFragment);
        updatePagerIndicatorState();
    }

    /* access modifiers changed from: protected */
    public void onPageSelected(int position) {
    }

    public void showPagerIndicator(boolean showIndicator) {
        this.pagerIndicatorEnabled = showIndicator;
    }

    public boolean isPagerIndicatorEnabled() {
        return this.pagerIndicatorEnabled;
    }

    public void showSkipButton(boolean showButton) {
        this.skipButtonEnabled = showButton;
        setButtonState(this.skipButton, showButton);
    }

    public boolean isSkipButtonEnabled() {
        return this.skipButtonEnabled;
    }

    public void onSkipPressed(Fragment currentFragment) {
        onSkipPressed();
    }

    /* access modifiers changed from: protected */
    public void setScrollDurationFactor(int factor) {
        this.pager.setScrollDurationFactor((double) factor);
    }

    /* access modifiers changed from: protected */
    public void setButtonState(@Nullable View button, boolean show) {
        if (button != null) {
            if (show) {
                button.setVisibility(0);
            } else {
                button.setVisibility(4);
            }
        }
    }

    public AppIntroViewPager getPager() {
        return this.pager;
    }

    @NonNull
    public List<Fragment> getSlides() {
        return this.mPagerAdapter.getFragments();
    }

    public void addSlide(@NonNull Fragment fragment) {
        if (isRtl()) {
            this.fragments.add(0, fragment);
        } else {
            this.fragments.add(fragment);
        }
        if (this.isWizardMode) {
            setOffScreenPageLimit(this.fragments.size());
        }
        this.mPagerAdapter.notifyDataSetChanged();
    }

    public boolean isProgressButtonEnabled() {
        return this.progressButtonEnabled;
    }

    public void setProgressButtonEnabled(boolean progressButtonEnabled2) {
        this.progressButtonEnabled = progressButtonEnabled2;
        if (!progressButtonEnabled2) {
            setButtonState(this.nextButton, false);
            setButtonState(this.doneButton, false);
            setButtonState(this.backButton, false);
            setButtonState(this.skipButton, false);
        } else if ((isRtl() || this.pager.getCurrentItem() != this.slidesNumber - 1) && (!isRtl() || this.pager.getCurrentItem() != 0)) {
            setButtonState(this.nextButton, true);
            setButtonState(this.doneButton, false);
            if (!this.isWizardMode) {
                setButtonState(this.skipButton, this.skipButtonEnabled);
            } else if ((isRtl() || this.pager.getCurrentItem() != 0) && (!isRtl() || this.pager.getCurrentItem() != this.slidesNumber - 1)) {
                setButtonState(this.backButton, this.isWizardMode);
            } else {
                setButtonState(this.backButton, false);
            }
        } else {
            setButtonState(this.nextButton, false);
            setButtonState(this.doneButton, true);
            if (this.isWizardMode) {
                setButtonState(this.backButton, this.showBackButtonWithDone);
            } else {
                setButtonState(this.skipButton, false);
            }
        }
    }

    public void setOffScreenPageLimit(int limit) {
        this.pager.setOffscreenPageLimit(limit);
    }

    @Deprecated
    public void init(@Nullable Bundle savedInstanceState) {
    }

    @Deprecated
    public void onNextPressed() {
    }

    public void onDonePressed() {
    }

    @Deprecated
    public void onSkipPressed() {
    }

    @Deprecated
    public void onSlideChanged() {
    }

    public void onDonePressed(Fragment currentFragment) {
        onDonePressed();
    }

    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        onSlideChanged();
    }

    public boolean onKeyDown(int code, KeyEvent event) {
        if (code != 66 && code != 96 && code != 23) {
            return super.onKeyDown(code, event);
        }
        ViewPager vp = (ViewPager) findViewById(R.id.view_pager);
        if (vp.getCurrentItem() == vp.getAdapter().getCount() - 1) {
            onDonePressed(this.fragments.get(vp.getCurrentItem()));
            return false;
        }
        vp.setCurrentItem(vp.getCurrentItem() + 1);
        return false;
    }

    public void setNavBarColor(String Color) {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(Color.parseColor(Color));
        }
    }

    public void setNavBarColor(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, color));
        }
    }

    public void showStatusBar(boolean isVisible) {
        if (!isVisible) {
            getWindow().setFlags(1024, 1024);
        } else {
            getWindow().clearFlags(1024);
        }
    }

    public void setVibrate(boolean vibrationEnabled) {
        this.isVibrateOn = vibrationEnabled;
    }

    public boolean getWizardMode() {
        return this.isWizardMode;
    }

    public void setWizardMode(boolean wizardMode) {
        this.isWizardMode = wizardMode;
        this.skipButtonEnabled = false;
        setButtonState(this.skipButton, !wizardMode);
    }

    public boolean getBackButtonVisibilityWithDone() {
        return this.isWizardMode;
    }

    public void setBackButtonVisibilityWithDone(boolean show) {
        this.showBackButtonWithDone = show;
    }

    public void setVibrateIntensity(int intensity) {
        this.vibrateIntensity = intensity;
    }

    public void setProgressIndicator() {
        this.mController = new ProgressIndicatorController();
    }

    public void setCustomIndicator(@NonNull IndicatorController controller) {
        this.mController = controller;
    }

    public void setColorTransitionsEnabled(boolean colorTransitionsEnabled) {
        this.areColorTransitionsEnabled = colorTransitionsEnabled;
    }

    public void setFadeAnimation() {
        this.pager.setPageTransformer(true, new ViewPageTransformer(ViewPageTransformer.TransformType.FADE));
    }

    public void setZoomAnimation() {
        this.pager.setPageTransformer(true, new ViewPageTransformer(ViewPageTransformer.TransformType.ZOOM));
    }

    public void setFlowAnimation() {
        this.pager.setPageTransformer(true, new ViewPageTransformer(ViewPageTransformer.TransformType.FLOW));
    }

    public void setSlideOverAnimation() {
        this.pager.setPageTransformer(true, new ViewPageTransformer(ViewPageTransformer.TransformType.SLIDE_OVER));
    }

    public void setDepthAnimation() {
        this.pager.setPageTransformer(true, new ViewPageTransformer(ViewPageTransformer.TransformType.DEPTH));
    }

    public void setCustomTransformer(@Nullable ViewPager.PageTransformer transformer) {
        this.pager.setPageTransformer(true, transformer);
    }

    public void setIndicatorColor(int selectedIndicatorColor2, int unselectedIndicatorColor2) {
        this.selectedIndicatorColor = selectedIndicatorColor2;
        this.unselectedIndicatorColor = unselectedIndicatorColor2;
        IndicatorController indicatorController = this.mController;
        if (indicatorController != null) {
            if (selectedIndicatorColor2 != 1) {
                indicatorController.setSelectedIndicatorColor(selectedIndicatorColor2);
            }
            if (unselectedIndicatorColor2 != 1) {
                this.mController.setUnselectedIndicatorColor(unselectedIndicatorColor2);
            }
        }
    }

    public void setNextPageSwipeLock(boolean lockEnable) {
        if (lockEnable) {
            this.baseProgressButtonEnabled = this.progressButtonEnabled;
            setProgressButtonEnabled(false);
        } else {
            setProgressButtonEnabled(this.baseProgressButtonEnabled);
        }
        this.pager.setNextPagingEnabled(!lockEnable);
    }

    public void setSwipeLock(boolean lockEnable) {
        if (lockEnable) {
            this.baseProgressButtonEnabled = this.progressButtonEnabled;
        } else {
            setProgressButtonEnabled(this.baseProgressButtonEnabled);
        }
        this.pager.setPagingEnabled(!lockEnable);
    }

    public void setGoBackLock(boolean lockEnabled) {
        this.isGoBackLockEnabled = lockEnabled;
    }

    public void setImmersiveMode(boolean isEnabled) {
        setImmersiveMode(isEnabled, false);
    }

    public void setImmersiveMode(boolean isEnabled, boolean isSticky) {
        int flags;
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        if (!isEnabled && this.isImmersiveModeEnabled) {
            getWindow().getDecorView().setSystemUiVisibility(1792);
            this.isImmersiveModeEnabled = false;
        } else if (isEnabled) {
            if (isSticky) {
                flags = 1798 | 4096;
                this.isImmersiveModeSticky = true;
            } else {
                this.isImmersiveModeSticky = false;
                flags = 1798 | 2048;
            }
            getWindow().getDecorView().setSystemUiVisibility(flags);
            this.isImmersiveModeEnabled = true;
        }
    }

    /* access modifiers changed from: private */
    public void changeSlide(boolean isLastSlide) {
        if (isLastSlide) {
            Fragment currentFragment = this.mPagerAdapter.getItem(this.pager.getCurrentItem());
            handleSlideChanged(currentFragment, (Fragment) null);
            onDonePressed(currentFragment);
            return;
        }
        this.pager.goToNextSlide();
        onNextPressed();
    }

    /* access modifiers changed from: private */
    public boolean checkAndRequestPermissions() {
        if (!this.permissionsArray.isEmpty()) {
            boolean requestPermission = false;
            int permissionPosition = 0;
            if (0 < this.permissionsArray.size()) {
                requestPermission = this.pager.getCurrentItem() + 1 == this.permissionsArray.get(0).getPosition();
                permissionPosition = 0;
            }
            if (requestPermission && Build.VERSION.SDK_INT >= 23) {
                requestPermissions(this.permissionsArray.get(permissionPosition).getPermission(), 1);
                this.permissionsArray.remove(permissionPosition);
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void updatePagerIndicatorState() {
        FrameLayout frameLayout = this.indicatorContainer;
        if (frameLayout == null) {
            return;
        }
        if (this.pagerIndicatorEnabled) {
            frameLayout.setVisibility(0);
        } else {
            frameLayout.setVisibility(4);
        }
    }

    public void askForPermissions(String[] permissions, int slidesNumber2) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        if (slidesNumber2 == 0) {
            Toast.makeText(getBaseContext(), "Invalid Slide Number", 0).show();
            return;
        }
        this.permissionsArray.add(new PermissionObject(permissions, slidesNumber2));
        setSwipeLock(true);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1) {
            LogHelper.e(TAG, "Unexpected request code");
        } else if (this.pager.getCurrentItem() + 1 == this.slidesNumber) {
            changeSlide(true);
        } else {
            changeSlide(false);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isRtl() {
        return LayoutUtil.isRtl(getApplicationContext());
    }

    @SuppressLint({"MissingPermission"})
    private final class NextButtonOnClickListener implements View.OnClickListener {
        private NextButtonOnClickListener() {
        }

        @SuppressLint({"MissingPermission"})
        public void onClick(View v) {
            if (AppIntroBase.this.isVibrateOn) {
                AppIntroBase.this.mVibrator.vibrate((long) AppIntroBase.this.vibrateIntensity);
            }
            if (!AppIntroBase.this.handleBeforeSlideChanged()) {
                AppIntroBase.this.handleIllegalSlideChangeAttempt();
            } else if (!AppIntroBase.this.checkAndRequestPermissions()) {
                AppIntroBase.this.changeSlide(false);
            }
        }
    }

    public class PagerOnPageChangeListener implements ViewPager.OnPageChangeListener {
        public PagerOnPageChangeListener() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (AppIntroBase.this.areColorTransitionsEnabled && position < AppIntroBase.this.mPagerAdapter.getCount() - 1) {
                if (!(AppIntroBase.this.mPagerAdapter.getItem(position) instanceof ISlideBackgroundColorHolder) || !(AppIntroBase.this.mPagerAdapter.getItem(position + 1) instanceof ISlideBackgroundColorHolder)) {
                    throw new IllegalStateException("Color transitions are only available if all slides implement ISlideBackgroundColorHolder.");
                }
                Fragment currentSlide = AppIntroBase.this.mPagerAdapter.getItem(position);
                Fragment nextSlide = AppIntroBase.this.mPagerAdapter.getItem(position + 1);
                ISlideBackgroundColorHolder currentSlideCasted = (ISlideBackgroundColorHolder) currentSlide;
                ISlideBackgroundColorHolder nextSlideCasted = (ISlideBackgroundColorHolder) nextSlide;
                if (currentSlide.isAdded() && nextSlide.isAdded()) {
                    int newColor = ((Integer) AppIntroBase.this.argbEvaluator.evaluate(positionOffset, Integer.valueOf(currentSlideCasted.getDefaultBackgroundColor()), Integer.valueOf(nextSlideCasted.getDefaultBackgroundColor()))).intValue();
                    currentSlideCasted.setBackgroundColor(newColor);
                    nextSlideCasted.setBackgroundColor(newColor);
                }
            }
        }

        public void onPageSelected(int position) {
            if (AppIntroBase.this.slidesNumber > 1) {
                AppIntroBase.this.mController.selectPosition(position);
            }
            if (AppIntroBase.this.pager.isNextPagingEnabled()) {
                AppIntroBase appIntroBase = AppIntroBase.this;
                appIntroBase.setProgressButtonEnabled(appIntroBase.progressButtonEnabled);
            } else if (AppIntroBase.this.pager.getCurrentItem() != AppIntroBase.this.pager.getLockPage()) {
                AppIntroBase appIntroBase2 = AppIntroBase.this;
                appIntroBase2.setProgressButtonEnabled(appIntroBase2.baseProgressButtonEnabled);
                AppIntroBase.this.pager.setNextPagingEnabled(true);
            } else {
                AppIntroBase appIntroBase3 = AppIntroBase.this;
                appIntroBase3.setProgressButtonEnabled(appIntroBase3.progressButtonEnabled);
            }
            AppIntroBase.this.onPageSelected(position);
            if (AppIntroBase.this.slidesNumber > 0) {
                if (AppIntroBase.this.currentlySelectedItem == -1) {
                    AppIntroBase appIntroBase4 = AppIntroBase.this;
                    appIntroBase4.handleSlideChanged((Fragment) null, appIntroBase4.mPagerAdapter.getItem(position));
                } else {
                    AppIntroBase appIntroBase5 = AppIntroBase.this;
                    appIntroBase5.handleSlideChanged(appIntroBase5.mPagerAdapter.getItem(AppIntroBase.this.currentlySelectedItem), AppIntroBase.this.mPagerAdapter.getItem(AppIntroBase.this.pager.getCurrentItem()));
                }
            }
            int unused = AppIntroBase.this.currentlySelectedItem = position;
            AppIntroBase.this.updatePagerIndicatorState();
        }

        public void onPageScrollStateChanged(int state) {
        }
    }

    private final class WindowGestureListener extends GestureDetector.SimpleOnGestureListener {
        private WindowGestureListener() {
        }

        public boolean onSingleTapUp(MotionEvent e) {
            if (AppIntroBase.this.isImmersiveModeEnabled && !AppIntroBase.this.isImmersiveModeSticky) {
                AppIntroBase.this.setImmersiveMode(true, false);
            }
            return false;
        }
    }
}

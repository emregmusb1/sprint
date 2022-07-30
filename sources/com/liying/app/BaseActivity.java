package com.liying.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.ggccc.app.hbsdf23.R;
import com.liying.app.util.StatusBarUtils;

public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = getClass().getName();
    protected Context mContext;
    private Unbinder mUnbinder;

    @LayoutRes
    public abstract int getLayoutId();

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        create(savedInstanceState);
        AtyContainer.getInstance().addActivity(this);
    }

    private void create(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(this.TAG, "ActivityOnCreate");
        this.mContext = this;
        beforeSetContentView();
        setContentView(getLayoutId());
        onBindView();
        onActivityCreate(savedInstanceState);
    }

    public void removeFragmentFromState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.remove("android:support:fragments");
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityMemoryRecycled(Bundle saveInstanceState) {
        Log.e(this.TAG, "<<<<<<<<<<<<<<<onActivityMemoryRecycled>>>>>>>>>>>>>>>>");
        if (!willHandleMemory()) {
            super.onCreate((Bundle) null);
            if (!canShowMainActivity()) {
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(67108864);
                i.addFlags(32768);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.fade_in, 0);
                return;
            }
            finish();
            overridePendingTransition(R.anim.fade_in, 0);
            startActivity(new Intent(this, MainActivity.class));
            return;
        }
        removeFragmentFromState(saveInstanceState);
        create(saveInstanceState);
    }

    private boolean canShowMainActivity() {
        if (getClass() != WelcomeActivity.class) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onActivityCreate(@Nullable Bundle savedInstanceState) {
    }

    /* access modifiers changed from: protected */
    public boolean willHandleMemory() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void beforeSetContentView() {
        StatusBarUtils.setStatusBarTransparent(this);
    }

    /* access modifiers changed from: protected */
    public void onBindView() {
        this.mUnbinder = ButterKnife.bind((Activity) this);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        Unbinder unbinder = this.mUnbinder;
        if (unbinder != null) {
            unbinder.unbind();
        }
        AtyContainer.getInstance().removeActivity(this);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        ((App) getApplicationContext()).setCurrentActivity(this);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (equals(((App) getApplicationContext()).getCurrentActivity())) {
            ((App) getApplicationContext()).setCurrentActivity((Activity) null);
        }
    }
}

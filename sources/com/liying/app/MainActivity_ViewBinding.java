package com.liying.app;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.ggccc.app.hbsdf23.R;

public class MainActivity_ViewBinding implements Unbinder {
    private MainActivity target;

    @UiThread
    public MainActivity_ViewBinding(MainActivity target2) {
        this(target2, target2.getWindow().getDecorView());
    }

    @UiThread
    public MainActivity_ViewBinding(MainActivity target2, View source) {
        this.target = target2;
        target2.statusBar = Utils.findRequiredView(source, R.id.status_bar, "field 'statusBar'");
        target2.progressBar = (ProgressBar) Utils.findRequiredViewAsType(source, R.id.progressBar, "field 'progressBar'", ProgressBar.class);
        target2.llWeb = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.ll_web, "field 'llWeb'", LinearLayout.class);
    }

    @CallSuper
    public void unbind() {
        MainActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.statusBar = null;
            target2.progressBar = null;
            target2.llWeb = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}

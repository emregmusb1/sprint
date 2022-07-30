package com.liying.app;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.ggccc.app.hbsdf23.R;

public class WelcomeActivity_ViewBinding implements Unbinder {
    private WelcomeActivity target;

    @UiThread
    public WelcomeActivity_ViewBinding(WelcomeActivity target2) {
        this(target2, target2.getWindow().getDecorView());
    }

    @UiThread
    public WelcomeActivity_ViewBinding(WelcomeActivity target2, View source) {
        this.target = target2;
        target2.welcomeView = (ImageView) Utils.findRequiredViewAsType(source, R.id.welcome_image, "field 'welcomeView'", ImageView.class);
    }

    @CallSuper
    public void unbind() {
        WelcomeActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.welcomeView = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}

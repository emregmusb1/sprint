package org.xutils.view;

import android.app.Activity;
import android.view.View;

final class ViewFinder {
    private Activity activity;
    private View view;

    public ViewFinder(View view2) {
        this.view = view2;
    }

    public ViewFinder(Activity activity2) {
        this.activity = activity2;
    }

    public View findViewById(int id) {
        View view2 = this.view;
        if (view2 != null) {
            return view2.findViewById(id);
        }
        Activity activity2 = this.activity;
        if (activity2 != null) {
            return activity2.findViewById(id);
        }
        return null;
    }

    public View findViewByInfo(ViewInfo info) {
        return findViewById(info.value, info.parentId);
    }

    public View findViewById(int id, int pid) {
        View pView = null;
        if (pid > 0) {
            pView = findViewById(pid);
        }
        if (pView != null) {
            return pView.findViewById(id);
        }
        return findViewById(id);
    }
}

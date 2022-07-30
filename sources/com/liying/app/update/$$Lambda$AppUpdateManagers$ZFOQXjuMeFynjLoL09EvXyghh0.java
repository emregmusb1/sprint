package com.liying.app.update;

import com.liying.app.App;

/* renamed from: com.liying.app.update.-$$Lambda$AppUpdateManagers$ZFOQXjuMeFynj-LoL09EvXyghh0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AppUpdateManagers$ZFOQXjuMeFynjLoL09EvXyghh0 implements Runnable {
    public static final /* synthetic */ $$Lambda$AppUpdateManagers$ZFOQXjuMeFynjLoL09EvXyghh0 INSTANCE = new $$Lambda$AppUpdateManagers$ZFOQXjuMeFynjLoL09EvXyghh0();

    private /* synthetic */ $$Lambda$AppUpdateManagers$ZFOQXjuMeFynjLoL09EvXyghh0() {
    }

    public final void run() {
        ((App) App.getContext()).getCurrentActivity().startActivity(AppUpdateManagers.buildInstallIntent(App.getContext()));
    }
}

package org.xutils.common.task;

import java.util.concurrent.Executor;
import org.xutils.common.Callback;

public abstract class AbsTask<ResultType> implements Callback.Cancelable {
    private final Callback.Cancelable cancelHandler;
    private volatile boolean isCancelled;
    private ResultType result;
    private volatile State state;
    private TaskProxy taskProxy;

    /* access modifiers changed from: protected */
    public abstract ResultType doBackground() throws Throwable;

    /* access modifiers changed from: protected */
    public abstract void onError(Throwable th, boolean z);

    /* access modifiers changed from: protected */
    public abstract void onSuccess(ResultType resulttype);

    public AbsTask() {
        this((Callback.Cancelable) null);
    }

    public AbsTask(Callback.Cancelable cancelHandler2) {
        this.taskProxy = null;
        this.isCancelled = false;
        this.state = State.IDLE;
        this.cancelHandler = cancelHandler2;
    }

    /* access modifiers changed from: protected */
    public void onWaiting() {
    }

    /* access modifiers changed from: protected */
    public void onStarted() {
    }

    /* access modifiers changed from: protected */
    public void onUpdate(int flag, Object... args) {
    }

    /* access modifiers changed from: protected */
    public void onCancelled(Callback.CancelledException cex) {
    }

    /* access modifiers changed from: protected */
    public void onFinished() {
    }

    public Priority getPriority() {
        return null;
    }

    public Executor getExecutor() {
        return null;
    }

    /* access modifiers changed from: protected */
    public final void update(int flag, Object... args) {
        TaskProxy taskProxy2 = this.taskProxy;
        if (taskProxy2 != null) {
            taskProxy2.onUpdate(flag, args);
        }
    }

    /* access modifiers changed from: protected */
    public void cancelWorks() {
    }

    /* access modifiers changed from: protected */
    public boolean isCancelFast() {
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x005d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void cancel() {
        /*
            r3 = this;
            boolean r0 = r3.isCancelled
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            monitor-enter(r3)
            boolean r0 = r3.isCancelled     // Catch:{ all -> 0x005e }
            if (r0 == 0) goto L_0x000c
            monitor-exit(r3)     // Catch:{ all -> 0x005e }
            return
        L_0x000c:
            r0 = 1
            r3.isCancelled = r0     // Catch:{ all -> 0x005e }
            r3.cancelWorks()     // Catch:{ all -> 0x005e }
            org.xutils.common.Callback$Cancelable r0 = r3.cancelHandler     // Catch:{ all -> 0x005e }
            if (r0 == 0) goto L_0x0023
            org.xutils.common.Callback$Cancelable r0 = r3.cancelHandler     // Catch:{ all -> 0x005e }
            boolean r0 = r0.isCancelled()     // Catch:{ all -> 0x005e }
            if (r0 != 0) goto L_0x0023
            org.xutils.common.Callback$Cancelable r0 = r3.cancelHandler     // Catch:{ all -> 0x005e }
            r0.cancel()     // Catch:{ all -> 0x005e }
        L_0x0023:
            org.xutils.common.task.AbsTask$State r0 = r3.state     // Catch:{ all -> 0x005e }
            org.xutils.common.task.AbsTask$State r1 = org.xutils.common.task.AbsTask.State.WAITING     // Catch:{ all -> 0x005e }
            if (r0 == r1) goto L_0x0035
            org.xutils.common.task.AbsTask$State r0 = r3.state     // Catch:{ all -> 0x005e }
            org.xutils.common.task.AbsTask$State r1 = org.xutils.common.task.AbsTask.State.STARTED     // Catch:{ all -> 0x005e }
            if (r0 != r1) goto L_0x005c
            boolean r0 = r3.isCancelFast()     // Catch:{ all -> 0x005e }
            if (r0 == 0) goto L_0x005c
        L_0x0035:
            org.xutils.common.task.TaskProxy r0 = r3.taskProxy     // Catch:{ all -> 0x005e }
            if (r0 == 0) goto L_0x004b
            org.xutils.common.task.TaskProxy r0 = r3.taskProxy     // Catch:{ all -> 0x005e }
            org.xutils.common.Callback$CancelledException r1 = new org.xutils.common.Callback$CancelledException     // Catch:{ all -> 0x005e }
            java.lang.String r2 = "cancelled by user"
            r1.<init>(r2)     // Catch:{ all -> 0x005e }
            r0.onCancelled(r1)     // Catch:{ all -> 0x005e }
            org.xutils.common.task.TaskProxy r0 = r3.taskProxy     // Catch:{ all -> 0x005e }
            r0.onFinished()     // Catch:{ all -> 0x005e }
            goto L_0x005c
        L_0x004b:
            boolean r0 = r3 instanceof org.xutils.common.task.TaskProxy     // Catch:{ all -> 0x005e }
            if (r0 == 0) goto L_0x005c
            org.xutils.common.Callback$CancelledException r0 = new org.xutils.common.Callback$CancelledException     // Catch:{ all -> 0x005e }
            java.lang.String r1 = "cancelled by user"
            r0.<init>(r1)     // Catch:{ all -> 0x005e }
            r3.onCancelled(r0)     // Catch:{ all -> 0x005e }
            r3.onFinished()     // Catch:{ all -> 0x005e }
        L_0x005c:
            monitor-exit(r3)     // Catch:{ all -> 0x005e }
            return
        L_0x005e:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x005e }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xutils.common.task.AbsTask.cancel():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000a, code lost:
        r0 = r2.cancelHandler;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean isCancelled() {
        /*
            r2 = this;
            boolean r0 = r2.isCancelled
            if (r0 != 0) goto L_0x0017
            org.xutils.common.task.AbsTask$State r0 = r2.state
            org.xutils.common.task.AbsTask$State r1 = org.xutils.common.task.AbsTask.State.CANCELLED
            if (r0 == r1) goto L_0x0017
            org.xutils.common.Callback$Cancelable r0 = r2.cancelHandler
            if (r0 == 0) goto L_0x0015
            boolean r0 = r0.isCancelled()
            if (r0 == 0) goto L_0x0015
            goto L_0x0017
        L_0x0015:
            r0 = 0
            goto L_0x0018
        L_0x0017:
            r0 = 1
        L_0x0018:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xutils.common.task.AbsTask.isCancelled():boolean");
    }

    public final boolean isFinished() {
        return this.state.value() > State.STARTED.value();
    }

    public final State getState() {
        return this.state;
    }

    public final ResultType getResult() {
        return this.result;
    }

    /* access modifiers changed from: package-private */
    public void setState(State state2) {
        this.state = state2;
    }

    /* access modifiers changed from: package-private */
    public final void setTaskProxy(TaskProxy taskProxy2) {
        this.taskProxy = taskProxy2;
    }

    /* access modifiers changed from: package-private */
    public final void setResult(ResultType result2) {
        this.result = result2;
    }

    public enum State {
        IDLE(0),
        WAITING(1),
        STARTED(2),
        SUCCESS(3),
        CANCELLED(4),
        ERROR(5);
        
        private final int value;

        private State(int value2) {
            this.value = value2;
        }

        public int value() {
            return this.value;
        }
    }
}

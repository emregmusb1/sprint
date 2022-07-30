package org.xutils.common.task;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import org.xutils.common.Callback;
import org.xutils.common.task.AbsTask;

class TaskProxy<ResultType> extends AbsTask<ResultType> {
    private static final int MSG_WHAT_BASE = 1000000000;
    private static final int MSG_WHAT_ON_CANCEL = 1000000006;
    private static final int MSG_WHAT_ON_ERROR = 1000000004;
    private static final int MSG_WHAT_ON_FINISHED = 1000000007;
    private static final int MSG_WHAT_ON_START = 1000000002;
    private static final int MSG_WHAT_ON_SUCCESS = 1000000003;
    private static final int MSG_WHAT_ON_UPDATE = 1000000005;
    private static final int MSG_WHAT_ON_WAITING = 1000000001;
    static final PriorityExecutor sDefaultExecutor = new PriorityExecutor(true);
    static final InternalHandler sHandler = new InternalHandler();
    /* access modifiers changed from: private */
    public volatile boolean callOnCanceled = false;
    /* access modifiers changed from: private */
    public volatile boolean callOnFinished = false;
    private final Executor executor;
    /* access modifiers changed from: private */
    public final AbsTask<ResultType> task;

    TaskProxy(AbsTask<ResultType> task2) {
        super(task2);
        this.task = task2;
        this.task.setTaskProxy(this);
        setTaskProxy((TaskProxy) null);
        Executor taskExecutor = task2.getExecutor();
        this.executor = taskExecutor == null ? sDefaultExecutor : taskExecutor;
    }

    /* access modifiers changed from: protected */
    public final ResultType doBackground() throws Throwable {
        onWaiting();
        this.executor.execute(new PriorityRunnable(this.task.getPriority(), new Runnable() {
            public void run() {
                try {
                    if (!TaskProxy.this.callOnCanceled) {
                        if (!TaskProxy.this.isCancelled()) {
                            TaskProxy.this.onStarted();
                            if (!TaskProxy.this.isCancelled()) {
                                TaskProxy.this.task.setResult(TaskProxy.this.task.doBackground());
                                TaskProxy.this.setResult(TaskProxy.this.task.getResult());
                                if (!TaskProxy.this.isCancelled()) {
                                    TaskProxy.this.onSuccess(TaskProxy.this.task.getResult());
                                    TaskProxy.this.onFinished();
                                    return;
                                }
                                throw new Callback.CancelledException("");
                            }
                            throw new Callback.CancelledException("");
                        }
                    }
                    throw new Callback.CancelledException("");
                } catch (Callback.CancelledException cex) {
                    TaskProxy.this.onCancelled(cex);
                } catch (Throwable th) {
                    TaskProxy.this.onFinished();
                    throw th;
                }
            }
        }));
        return null;
    }

    /* access modifiers changed from: protected */
    public void onWaiting() {
        setState(AbsTask.State.WAITING);
        sHandler.obtainMessage(MSG_WHAT_ON_WAITING, this).sendToTarget();
    }

    /* access modifiers changed from: protected */
    public void onStarted() {
        setState(AbsTask.State.STARTED);
        sHandler.obtainMessage(MSG_WHAT_ON_START, this).sendToTarget();
    }

    /* access modifiers changed from: protected */
    public void onSuccess(ResultType resulttype) {
        setState(AbsTask.State.SUCCESS);
        sHandler.obtainMessage(MSG_WHAT_ON_SUCCESS, this).sendToTarget();
    }

    /* access modifiers changed from: protected */
    public void onError(Throwable ex, boolean isCallbackError) {
        setState(AbsTask.State.ERROR);
        sHandler.obtainMessage(MSG_WHAT_ON_ERROR, new ArgsObj(this, ex)).sendToTarget();
    }

    /* access modifiers changed from: protected */
    public void onUpdate(int flag, Object... args) {
        sHandler.obtainMessage(MSG_WHAT_ON_UPDATE, flag, flag, new ArgsObj(this, args)).sendToTarget();
    }

    /* access modifiers changed from: protected */
    public void onCancelled(Callback.CancelledException cex) {
        setState(AbsTask.State.CANCELLED);
        sHandler.obtainMessage(MSG_WHAT_ON_CANCEL, new ArgsObj(this, cex)).sendToTarget();
    }

    /* access modifiers changed from: protected */
    public void onFinished() {
        sHandler.obtainMessage(MSG_WHAT_ON_FINISHED, this).sendToTarget();
    }

    /* access modifiers changed from: package-private */
    public final void setState(AbsTask.State state) {
        super.setState(state);
        this.task.setState(state);
    }

    public final Priority getPriority() {
        return this.task.getPriority();
    }

    public final Executor getExecutor() {
        return this.executor;
    }

    private static class ArgsObj {
        final Object[] args;
        final TaskProxy taskProxy;

        public ArgsObj(TaskProxy taskProxy2, Object... args2) {
            this.taskProxy = taskProxy2;
            this.args = args2;
        }
    }

    static final class InternalHandler extends Handler {
        static final /* synthetic */ boolean $assertionsDisabled = false;

        static {
            Class<TaskProxy> cls = TaskProxy.class;
        }

        private InternalHandler() {
            super(Looper.getMainLooper());
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v10, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.xutils.common.task.TaskProxy} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r7) {
            /*
                r6 = this;
                java.lang.Object r0 = r7.obj
                if (r0 == 0) goto L_0x00b8
                r0 = 0
                r1 = 0
                java.lang.Object r2 = r7.obj
                boolean r2 = r2 instanceof org.xutils.common.task.TaskProxy
                if (r2 == 0) goto L_0x0012
                java.lang.Object r2 = r7.obj
                r0 = r2
                org.xutils.common.task.TaskProxy r0 = (org.xutils.common.task.TaskProxy) r0
                goto L_0x0020
            L_0x0012:
                java.lang.Object r2 = r7.obj
                boolean r2 = r2 instanceof org.xutils.common.task.TaskProxy.ArgsObj
                if (r2 == 0) goto L_0x0020
                java.lang.Object r2 = r7.obj
                org.xutils.common.task.TaskProxy$ArgsObj r2 = (org.xutils.common.task.TaskProxy.ArgsObj) r2
                org.xutils.common.task.TaskProxy r0 = r2.taskProxy
                java.lang.Object[] r1 = r2.args
            L_0x0020:
                if (r0 == 0) goto L_0x00b0
                r2 = 1
                int r3 = r7.what     // Catch:{ Throwable -> 0x008e }
                r4 = 0
                switch(r3) {
                    case 1000000001: goto L_0x0085;
                    case 1000000002: goto L_0x007d;
                    case 1000000003: goto L_0x0071;
                    case 1000000004: goto L_0x005d;
                    case 1000000005: goto L_0x0053;
                    case 1000000006: goto L_0x003c;
                    case 1000000007: goto L_0x002a;
                    default: goto L_0x0029;
                }     // Catch:{ Throwable -> 0x008e }
            L_0x0029:
                goto L_0x008d
            L_0x002a:
                boolean r3 = r0.callOnFinished     // Catch:{ Throwable -> 0x008e }
                if (r3 == 0) goto L_0x0031
                return
            L_0x0031:
                boolean unused = r0.callOnFinished = r2     // Catch:{ Throwable -> 0x008e }
                org.xutils.common.task.AbsTask r3 = r0.task     // Catch:{ Throwable -> 0x008e }
                r3.onFinished()     // Catch:{ Throwable -> 0x008e }
                goto L_0x008d
            L_0x003c:
                boolean r3 = r0.callOnCanceled     // Catch:{ Throwable -> 0x008e }
                if (r3 == 0) goto L_0x0043
                return
            L_0x0043:
                boolean unused = r0.callOnCanceled = r2     // Catch:{ Throwable -> 0x008e }
                org.xutils.common.task.AbsTask r3 = r0.task     // Catch:{ Throwable -> 0x008e }
                r4 = r1[r4]     // Catch:{ Throwable -> 0x008e }
                org.xutils.common.Callback$CancelledException r4 = (org.xutils.common.Callback.CancelledException) r4     // Catch:{ Throwable -> 0x008e }
                r3.onCancelled(r4)     // Catch:{ Throwable -> 0x008e }
                goto L_0x008d
            L_0x0053:
                org.xutils.common.task.AbsTask r3 = r0.task     // Catch:{ Throwable -> 0x008e }
                int r4 = r7.arg1     // Catch:{ Throwable -> 0x008e }
                r3.onUpdate(r4, r1)     // Catch:{ Throwable -> 0x008e }
                goto L_0x008d
            L_0x005d:
                r3 = r1[r4]     // Catch:{ Throwable -> 0x008e }
                java.lang.Throwable r3 = (java.lang.Throwable) r3     // Catch:{ Throwable -> 0x008e }
                java.lang.String r5 = r3.getMessage()     // Catch:{ Throwable -> 0x008e }
                org.xutils.common.util.LogUtil.d(r5, r3)     // Catch:{ Throwable -> 0x008e }
                org.xutils.common.task.AbsTask r5 = r0.task     // Catch:{ Throwable -> 0x008e }
                r5.onError(r3, r4)     // Catch:{ Throwable -> 0x008e }
                goto L_0x008d
            L_0x0071:
                org.xutils.common.task.AbsTask r3 = r0.task     // Catch:{ Throwable -> 0x008e }
                java.lang.Object r4 = r0.getResult()     // Catch:{ Throwable -> 0x008e }
                r3.onSuccess(r4)     // Catch:{ Throwable -> 0x008e }
                goto L_0x008d
            L_0x007d:
                org.xutils.common.task.AbsTask r3 = r0.task     // Catch:{ Throwable -> 0x008e }
                r3.onStarted()     // Catch:{ Throwable -> 0x008e }
                goto L_0x008d
            L_0x0085:
                org.xutils.common.task.AbsTask r3 = r0.task     // Catch:{ Throwable -> 0x008e }
                r3.onWaiting()     // Catch:{ Throwable -> 0x008e }
            L_0x008d:
                goto L_0x00a9
            L_0x008e:
                r3 = move-exception
                org.xutils.common.task.AbsTask$State r4 = org.xutils.common.task.AbsTask.State.ERROR
                r0.setState(r4)
                int r4 = r7.what
                r5 = 1000000004(0x3b9aca04, float:0.004723789)
                if (r4 == r5) goto L_0x00a3
                org.xutils.common.task.AbsTask r4 = r0.task
                r4.onError(r3, r2)
                goto L_0x00a9
            L_0x00a3:
                boolean r2 = org.xutils.x.isDebug()
                if (r2 != 0) goto L_0x00aa
            L_0x00a9:
                return
            L_0x00aa:
                java.lang.RuntimeException r2 = new java.lang.RuntimeException
                r2.<init>(r3)
                throw r2
            L_0x00b0:
                java.lang.RuntimeException r2 = new java.lang.RuntimeException
                java.lang.String r3 = "msg.obj not instanceof TaskProxy"
                r2.<init>(r3)
                throw r2
            L_0x00b8:
                java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
                java.lang.String r1 = "msg must not be null"
                r0.<init>(r1)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.xutils.common.task.TaskProxy.InternalHandler.handleMessage(android.os.Message):void");
        }
    }
}

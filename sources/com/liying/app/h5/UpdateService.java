package com.liying.app.h5;

import android.app.Activity;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.liying.app.App;
import com.liying.app.AtyContainer;
import com.liying.app.language.TipLanguageUtil;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

public class UpdateService extends Service {
    AsyncTask task;
    String url;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        if (this.task == null) {
            try {
                String version = intent.getStringExtra("version");
                String versionUrl = intent.getStringExtra("versionUrl");
                Log.i("UpdateService", String.format("UpdateTask 参数  version:%s url:%s", new Object[]{version, versionUrl}));
                if (versionUrl == null) {
                    return super.onStartCommand(intent, flags, startId);
                }
                this.url = versionUrl;
                Log.i("UpdateService", String.format("开始执行UpdateTask  version:%s url:%s", new Object[]{version, versionUrl}));
                this.task = new UpdateTask(version, new Runnable(version) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        UpdateService.this.lambda$onStartCommand$0$UpdateService(this.f$1);
                    }
                }, new Runnable() {
                    public final void run() {
                        UpdateService.this.lambda$onStartCommand$1$UpdateService();
                    }
                }).execute(new URL[]{new URL(this.url)});
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public /* synthetic */ void lambda$onStartCommand$0$UpdateService(String version) {
        H5Managers.setTempVersion(version);
        Activity currentActivity = ((App) getApplicationContext()).getCurrentActivity();
        if (currentActivity != null) {
            AlertDialog dialog = ((App) getApplicationContext()).getDialog();
            if (dialog == null || !dialog.isShowing()) {
                Log.i("UpdateService", "开始展示界面");
                AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
                builder.setTitle((CharSequence) TipLanguageUtil.getTip("提示")).setMessage((CharSequence) TipLanguageUtil.getTip("更新包已下载完成，请重启完成更新"));
                builder.setPositiveButton((CharSequence) TipLanguageUtil.getTip("升级"), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((App) UpdateService.this.getApplicationContext()).setDialog((AlertDialog) null);
                        AtyContainer.getInstance().finishAllActivity();
                        Process.killProcess(Process.myPid());
                        System.exit(1);
                    }
                });
                builder.setNegativeButton((CharSequence) TipLanguageUtil.getTip("取消"), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((App) UpdateService.this.getApplicationContext()).setDialog((AlertDialog) null);
                    }
                });
                AlertDialog alertDialog = builder.create();
                ((App) getApplicationContext()).setDialog(alertDialog);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                Log.i("UpdateService", "展示界面完成");
                return;
            }
            Log.i("UpdateService", "已有界面展示，结束当前操作");
        }
    }

    public /* synthetic */ void lambda$onStartCommand$1$UpdateService() {
        Activity currentActivity = ((App) getApplicationContext()).getCurrentActivity();
        if (currentActivity != null) {
            Toast.makeText(currentActivity, TipLanguageUtil.getTip("下载失败，请稍候再试"), 0).show();
        }
    }

    class UpdateTask extends AsyncTask<URL, Integer, Boolean> {
        private Runnable callBack;
        HttpURLConnection connection;
        int current = 0;
        private Runnable errorCallBack;
        FileOutputStream fileOutputStream;
        File h5Zip;
        DataInputStream inputStream;
        String version;

        public UpdateTask(String version2, Runnable callBack2, Runnable errorCallBack2) {
            this.version = version2;
            this.callBack = callBack2;
            this.errorCallBack = errorCallBack2;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
            this.h5Zip = new File(UpdateService.this.getFilesDir(), "loadH5");
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x012e, code lost:
            r1 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:63:0x012f, code lost:
            r9 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:67:0x0133, code lost:
            r9 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:68:0x0134, code lost:
            r13 = r9;
            r9 = r1;
            r1 = r13;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Boolean doInBackground(java.net.URL... r15) {
            /*
                r14 = this;
                java.lang.String r0 = "UpdateService"
                r1 = 0
                java.io.File r2 = r14.h5Zip     // Catch:{ IOException -> 0x019e }
                if (r2 != 0) goto L_0x0028
                java.lang.Boolean r0 = java.lang.Boolean.valueOf(r1)     // Catch:{ IOException -> 0x019e }
                java.io.FileOutputStream r1 = r14.fileOutputStream     // Catch:{ IOException -> 0x0023 }
                if (r1 == 0) goto L_0x0019
                java.io.FileOutputStream r1 = r14.fileOutputStream     // Catch:{ IOException -> 0x0023 }
                r1.flush()     // Catch:{ IOException -> 0x0023 }
                java.io.FileOutputStream r1 = r14.fileOutputStream     // Catch:{ IOException -> 0x0023 }
                r1.close()     // Catch:{ IOException -> 0x0023 }
            L_0x0019:
                java.io.DataInputStream r1 = r14.inputStream     // Catch:{ IOException -> 0x0023 }
                if (r1 == 0) goto L_0x0022
                java.io.DataInputStream r1 = r14.inputStream     // Catch:{ IOException -> 0x0023 }
                r1.close()     // Catch:{ IOException -> 0x0023 }
            L_0x0022:
                goto L_0x0027
            L_0x0023:
                r1 = move-exception
                r1.printStackTrace()
            L_0x0027:
                return r0
            L_0x0028:
                java.lang.String r2 = "doInBackground: 开始"
                android.util.Log.i(r0, r2)     // Catch:{ IOException -> 0x019e }
                r2 = r15[r1]     // Catch:{ IOException -> 0x019e }
                java.net.URLConnection r2 = r2.openConnection()     // Catch:{ IOException -> 0x019e }
                java.net.HttpURLConnection r2 = (java.net.HttpURLConnection) r2     // Catch:{ IOException -> 0x019e }
                r14.connection = r2     // Catch:{ IOException -> 0x019e }
                java.net.HttpURLConnection r2 = r14.connection     // Catch:{ IOException -> 0x019e }
                r3 = 5000(0x1388, float:7.006E-42)
                r2.setConnectTimeout(r3)     // Catch:{ IOException -> 0x019e }
                java.net.HttpURLConnection r2 = r14.connection     // Catch:{ IOException -> 0x019e }
                java.lang.String r3 = "GET"
                r2.setRequestMethod(r3)     // Catch:{ IOException -> 0x019e }
                java.net.HttpURLConnection r2 = r14.connection     // Catch:{ IOException -> 0x019e }
                r2.connect()     // Catch:{ IOException -> 0x019e }
                java.net.HttpURLConnection r2 = r14.connection     // Catch:{ IOException -> 0x019e }
                int r2 = r2.getResponseCode()     // Catch:{ IOException -> 0x019e }
                r3 = 200(0xc8, float:2.8E-43)
                if (r2 == r3) goto L_0x0075
                java.lang.Boolean r0 = java.lang.Boolean.valueOf(r1)     // Catch:{ IOException -> 0x019e }
                java.io.FileOutputStream r1 = r14.fileOutputStream     // Catch:{ IOException -> 0x0070 }
                if (r1 == 0) goto L_0x0066
                java.io.FileOutputStream r1 = r14.fileOutputStream     // Catch:{ IOException -> 0x0070 }
                r1.flush()     // Catch:{ IOException -> 0x0070 }
                java.io.FileOutputStream r1 = r14.fileOutputStream     // Catch:{ IOException -> 0x0070 }
                r1.close()     // Catch:{ IOException -> 0x0070 }
            L_0x0066:
                java.io.DataInputStream r1 = r14.inputStream     // Catch:{ IOException -> 0x0070 }
                if (r1 == 0) goto L_0x006f
                java.io.DataInputStream r1 = r14.inputStream     // Catch:{ IOException -> 0x0070 }
                r1.close()     // Catch:{ IOException -> 0x0070 }
            L_0x006f:
                goto L_0x0074
            L_0x0070:
                r1 = move-exception
                r1.printStackTrace()
            L_0x0074:
                return r0
            L_0x0075:
                java.io.DataInputStream r2 = new java.io.DataInputStream     // Catch:{ IOException -> 0x019e }
                java.net.HttpURLConnection r3 = r14.connection     // Catch:{ IOException -> 0x019e }
                java.io.InputStream r3 = r3.getInputStream()     // Catch:{ IOException -> 0x019e }
                r2.<init>(r3)     // Catch:{ IOException -> 0x019e }
                r14.inputStream = r2     // Catch:{ IOException -> 0x019e }
                java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x019e }
                java.io.File r3 = r14.h5Zip     // Catch:{ IOException -> 0x019e }
                r2.<init>(r3, r1)     // Catch:{ IOException -> 0x019e }
                r14.fileOutputStream = r2     // Catch:{ IOException -> 0x019e }
                r2 = 1024(0x400, float:1.435E-42)
                byte[] r2 = new byte[r2]     // Catch:{ IOException -> 0x019e }
            L_0x008f:
                java.io.DataInputStream r3 = r14.inputStream     // Catch:{ IOException -> 0x019e }
                int r3 = r3.read(r2)     // Catch:{ IOException -> 0x019e }
                r4 = r3
                r5 = -1
                if (r3 == r5) goto L_0x009f
                java.io.FileOutputStream r3 = r14.fileOutputStream     // Catch:{ IOException -> 0x019e }
                r3.write(r2, r1, r4)     // Catch:{ IOException -> 0x019e }
                goto L_0x008f
            L_0x009f:
                java.io.FileOutputStream r2 = r14.fileOutputStream     // Catch:{ IOException -> 0x00b7 }
                if (r2 == 0) goto L_0x00ad
                java.io.FileOutputStream r2 = r14.fileOutputStream     // Catch:{ IOException -> 0x00b7 }
                r2.flush()     // Catch:{ IOException -> 0x00b7 }
                java.io.FileOutputStream r2 = r14.fileOutputStream     // Catch:{ IOException -> 0x00b7 }
                r2.close()     // Catch:{ IOException -> 0x00b7 }
            L_0x00ad:
                java.io.DataInputStream r2 = r14.inputStream     // Catch:{ IOException -> 0x00b7 }
                if (r2 == 0) goto L_0x00b6
                java.io.DataInputStream r2 = r14.inputStream     // Catch:{ IOException -> 0x00b7 }
                r2.close()     // Catch:{ IOException -> 0x00b7 }
            L_0x00b6:
                goto L_0x00bc
            L_0x00b7:
                r2 = move-exception
                r2.printStackTrace()
            L_0x00bc:
                java.lang.String r2 = "doInBackground: success"
                android.util.Log.i(r0, r2)
                r2 = 1
                java.util.zip.ZipFile r3 = new java.util.zip.ZipFile     // Catch:{ Exception -> 0x017a }
                java.io.File r4 = r14.h5Zip     // Catch:{ Exception -> 0x017a }
                r3.<init>(r4)     // Catch:{ Exception -> 0x017a }
                java.lang.String r4 = "dist/index.html"
                java.util.zip.ZipEntry r4 = r3.getEntry(r4)     // Catch:{ Exception -> 0x017a }
                java.io.InputStream r5 = r3.getInputStream(r4)     // Catch:{ Exception -> 0x017a }
                r6 = 4096(0x1000, float:5.74E-42)
                r7 = 0
                byte[] r6 = new byte[r6]     // Catch:{ Throwable -> 0x0166 }
                java.io.ByteArrayOutputStream r8 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x0146 }
                r8.<init>()     // Catch:{ Exception -> 0x0146 }
            L_0x00dd:
                int r9 = r5.read(r6)     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                if (r9 <= 0) goto L_0x00e7
                r8.write(r6)     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                goto L_0x00dd
            L_0x00e7:
                java.lang.String r9 = r8.toString()     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                r10.<init>()     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                java.lang.String r11 = "index html:"
                r10.append(r11)     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                r10.append(r9)     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                java.lang.String r10 = r10.toString()     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                android.util.Log.d(r0, r10)     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                java.lang.String r10 = "var APP_VERSION = '%s';"
                r11 = 1
                java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                java.lang.String r12 = r14.version     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                r11[r1] = r12     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                java.lang.String r1 = java.lang.String.format(r10, r11)     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                boolean r1 = r9.contains(r1)     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                if (r1 != 0) goto L_0x012a
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                r1.<init>()     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                java.lang.String r10 = "version not match:"
                r1.append(r10)     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                java.lang.String r10 = r14.version     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                r1.append(r10)     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                java.lang.String r1 = r1.toString()     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                android.util.Log.d(r0, r1)     // Catch:{ Throwable -> 0x0131, all -> 0x012e }
                r1 = 0
                r2 = r1
            L_0x012a:
                r8.close()     // Catch:{ Exception -> 0x0146 }
                goto L_0x015e
            L_0x012e:
                r1 = move-exception
                r9 = r7
                goto L_0x0137
            L_0x0131:
                r1 = move-exception
                throw r1     // Catch:{ all -> 0x0133 }
            L_0x0133:
                r9 = move-exception
                r13 = r9
                r9 = r1
                r1 = r13
            L_0x0137:
                if (r9 == 0) goto L_0x0142
                r8.close()     // Catch:{ Throwable -> 0x013d }
                goto L_0x0145
            L_0x013d:
                r10 = move-exception
                r9.addSuppressed(r10)     // Catch:{ Exception -> 0x0146 }
                goto L_0x0145
            L_0x0142:
                r8.close()     // Catch:{ Exception -> 0x0146 }
            L_0x0145:
                throw r1     // Catch:{ Exception -> 0x0146 }
            L_0x0146:
                r1 = move-exception
                java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0166 }
                r8.<init>()     // Catch:{ Throwable -> 0x0166 }
                java.lang.String r9 = "file not found:"
                r8.append(r9)     // Catch:{ Throwable -> 0x0166 }
                java.lang.String r9 = r14.version     // Catch:{ Throwable -> 0x0166 }
                r8.append(r9)     // Catch:{ Throwable -> 0x0166 }
                java.lang.String r8 = r8.toString()     // Catch:{ Throwable -> 0x0166 }
                android.util.Log.d(r0, r8)     // Catch:{ Throwable -> 0x0166 }
                r2 = 0
            L_0x015e:
                if (r5 == 0) goto L_0x0163
                r5.close()     // Catch:{ Exception -> 0x017a }
            L_0x0163:
                goto L_0x017c
            L_0x0164:
                r1 = move-exception
                goto L_0x0169
            L_0x0166:
                r1 = move-exception
                r7 = r1
                throw r7     // Catch:{ all -> 0x0164 }
            L_0x0169:
                if (r5 == 0) goto L_0x0179
                if (r7 == 0) goto L_0x0176
                r5.close()     // Catch:{ Throwable -> 0x0171 }
                goto L_0x0179
            L_0x0171:
                r6 = move-exception
                r7.addSuppressed(r6)     // Catch:{ Exception -> 0x017a }
                goto L_0x0179
            L_0x0176:
                r5.close()     // Catch:{ Exception -> 0x017a }
            L_0x0179:
                throw r1     // Catch:{ Exception -> 0x017a }
            L_0x017a:
                r1 = move-exception
                r2 = 0
            L_0x017c:
                if (r2 != 0) goto L_0x0183
                java.io.File r1 = r14.h5Zip
                r1.delete()
            L_0x0183:
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r3 = "zipCheck:"
                r1.append(r3)
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                android.util.Log.i(r0, r1)
                java.lang.Boolean r0 = java.lang.Boolean.valueOf(r2)
                return r0
            L_0x019c:
                r0 = move-exception
                goto L_0x01c8
            L_0x019e:
                r2 = move-exception
                r2.printStackTrace()     // Catch:{ all -> 0x019c }
                java.lang.String r3 = "doInBackground: error"
                android.util.Log.e(r0, r3, r2)     // Catch:{ all -> 0x019c }
                java.lang.Boolean r0 = java.lang.Boolean.valueOf(r1)     // Catch:{ all -> 0x019c }
                java.io.FileOutputStream r1 = r14.fileOutputStream     // Catch:{ IOException -> 0x01c3 }
                if (r1 == 0) goto L_0x01b9
                java.io.FileOutputStream r1 = r14.fileOutputStream     // Catch:{ IOException -> 0x01c3 }
                r1.flush()     // Catch:{ IOException -> 0x01c3 }
                java.io.FileOutputStream r1 = r14.fileOutputStream     // Catch:{ IOException -> 0x01c3 }
                r1.close()     // Catch:{ IOException -> 0x01c3 }
            L_0x01b9:
                java.io.DataInputStream r1 = r14.inputStream     // Catch:{ IOException -> 0x01c3 }
                if (r1 == 0) goto L_0x01c2
                java.io.DataInputStream r1 = r14.inputStream     // Catch:{ IOException -> 0x01c3 }
                r1.close()     // Catch:{ IOException -> 0x01c3 }
            L_0x01c2:
                goto L_0x01c7
            L_0x01c3:
                r1 = move-exception
                r1.printStackTrace()
            L_0x01c7:
                return r0
            L_0x01c8:
                java.io.FileOutputStream r1 = r14.fileOutputStream     // Catch:{ IOException -> 0x01e0 }
                if (r1 == 0) goto L_0x01d6
                java.io.FileOutputStream r1 = r14.fileOutputStream     // Catch:{ IOException -> 0x01e0 }
                r1.flush()     // Catch:{ IOException -> 0x01e0 }
                java.io.FileOutputStream r1 = r14.fileOutputStream     // Catch:{ IOException -> 0x01e0 }
                r1.close()     // Catch:{ IOException -> 0x01e0 }
            L_0x01d6:
                java.io.DataInputStream r1 = r14.inputStream     // Catch:{ IOException -> 0x01e0 }
                if (r1 == 0) goto L_0x01df
                java.io.DataInputStream r1 = r14.inputStream     // Catch:{ IOException -> 0x01e0 }
                r1.close()     // Catch:{ IOException -> 0x01e0 }
            L_0x01df:
                goto L_0x01e4
            L_0x01e0:
                r1 = move-exception
                r1.printStackTrace()
            L_0x01e4:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.liying.app.h5.UpdateService.UpdateTask.doInBackground(java.net.URL[]):java.lang.Boolean");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean finished) {
            if (finished.booleanValue()) {
                Runnable runnable = this.callBack;
                if (runnable != null) {
                    runnable.run();
                }
            } else {
                Log.e(ArchiveStreamFactory.ZIP, "zip下载失败");
                Runnable runnable2 = this.errorCallBack;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
            UpdateService.this.stopForeground(false);
            UpdateService.this.stopSelf();
        }
    }
}

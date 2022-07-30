package com.liying.app.update;

import android.app.Activity;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.liying.app.App;
import com.liying.app.AtyContainer;
import com.liying.app.language.TipLanguageUtil;
import com.liying.app.update.AppVersionUpdateService;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppVersionUpdateService extends Service {
    AsyncTask task;
    String url;
    int version;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (this.task == null) {
            try {
                this.url = intent.getStringExtra("url");
                this.version = intent.getIntExtra("version", 0);
                Log.i("AppVersionUpdateService", String.format("UpdateTask 参数 url:%s, version:%s", new Object[]{this.url, Integer.valueOf(this.version)}));
                if (this.version == 0) {
                    return super.onStartCommand(intent, flags, startId);
                }
                Log.i("AppVersionUpdateService", String.format("开始执行UpdateTask url:%s, version:%s", new Object[]{this.url, Integer.valueOf(this.version)}));
                this.task = new UpdateTask(this.version).execute(new URL[]{new URL(this.url)});
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AppVersionUpdateService", "执行UpdateTask失败", e);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class UpdateTask extends AsyncTask<URL, Integer, Boolean> {
        File apk;
        HttpURLConnection connection;
        int current = 0;
        FileOutputStream fileOutputStream;
        DataInputStream inputStream;
        File path;
        int version;

        public UpdateTask(int version2) {
            this.version = version2;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
            this.path = AppVersionUpdateService.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (this.path == null) {
                this.path = AppVersionUpdateService.this.getDir(Environment.DIRECTORY_DOWNLOADS, 0);
            }
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(URL... params) {
            try {
                if (this.path == null) {
                    try {
                        if (this.fileOutputStream != null) {
                            this.fileOutputStream.flush();
                        }
                        if (this.inputStream != null) {
                            this.inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
                Log.i("AppVersionUpdateService", "doInBackground: 开始执行");
                this.connection = (HttpURLConnection) params[0].openConnection();
                this.connection.setConnectTimeout(5000);
                this.connection.setRequestMethod("GET");
                this.connection.connect();
                if (this.connection.getResponseCode() != 200) {
                    try {
                        if (this.fileOutputStream != null) {
                            this.fileOutputStream.flush();
                        }
                        if (this.inputStream != null) {
                            this.inputStream.close();
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    return false;
                }
                this.inputStream = new DataInputStream(this.connection.getInputStream());
                int fileLength = this.connection.getContentLength();
                this.apk = new File(this.path, String.format("download_%s.apk", new Object[]{Integer.valueOf(this.version)}));
                this.fileOutputStream = new FileOutputStream(this.apk, false);
                byte[] buffer = new byte[1024];
                int current2 = 0;
                while (true) {
                    int read = this.inputStream.read(buffer);
                    int length = read;
                    if (read != -1) {
                        this.fileOutputStream.write(buffer, 0, length);
                        current2 += length;
                        publishProgress(new Integer[]{Integer.valueOf((current2 * 100) / fileLength)});
                    } else {
                        try {
                            break;
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                }
                if (this.fileOutputStream != null) {
                    this.fileOutputStream.flush();
                }
                if (this.inputStream != null) {
                    this.inputStream.close();
                }
                Log.i("AppVersionUpdateService", "doInBackground: success");
                return true;
            } catch (IOException e4) {
                e4.printStackTrace();
                Log.e("AppVersionUpdateService", "doInBackground: ", e4);
                try {
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.flush();
                    }
                    if (this.inputStream != null) {
                        this.inputStream.close();
                    }
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
                return false;
            } catch (Throwable th) {
                try {
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.flush();
                    }
                    if (this.inputStream != null) {
                        this.inputStream.close();
                    }
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
                throw th;
            }
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... values) {
            this.current = values[0].intValue();
            super.onProgressUpdate(values);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean finished) {
            if (finished.booleanValue()) {
                Log.i("AppVersionUpdateService", String.format("执行完成 version:%s, apk:%s", new Object[]{Integer.valueOf(this.version), this.apk.getAbsolutePath()}));
                AppUpdateManagers.setCurrentVersion(this.version);
                AppUpdateManagers.setCurrentVersionPath(this.apk.getAbsolutePath());
                if (!AppUpdateManagers.isNeedInstall()) {
                    AppUpdateManagers.setCurrentVersionPath("");
                    return;
                }
                ((App) AppVersionUpdateService.this.getApplicationContext()).addTask("checkApkInstall", new Runnable() {
                    public final void run() {
                        AppVersionUpdateService.UpdateTask.this.lambda$onPostExecute$0$AppVersionUpdateService$UpdateTask();
                    }
                });
            }
            AppVersionUpdateService.this.stopForeground(false);
            AppVersionUpdateService.this.stopSelf();
        }

        public /* synthetic */ void lambda$onPostExecute$0$AppVersionUpdateService$UpdateTask() {
            Activity currentActivity = ((App) AppVersionUpdateService.this.getApplicationContext()).getCurrentActivity();
            if (currentActivity != null) {
                AlertDialog dialog = ((App) AppVersionUpdateService.this.getApplicationContext()).getDialog();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.i("AppVersionUpdateService", "show dialog");
                AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
                builder.setTitle((CharSequence) TipLanguageUtil.getTip("提示")).setMessage((CharSequence) TipLanguageUtil.getTip("检测到新版本，是否更新程序？"));
                builder.setPositiveButton((CharSequence) TipLanguageUtil.getTip("更新"), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((App) AppVersionUpdateService.this.getApplicationContext()).setDialog((AlertDialog) null);
                        AppUpdateManagers.installApk();
                    }
                });
                builder.setNegativeButton((CharSequence) TipLanguageUtil.getTip("退出"), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((App) AppVersionUpdateService.this.getApplicationContext()).setDialog((AlertDialog) null);
                        AtyContainer.getInstance().finishAllActivity();
                        Process.killProcess(Process.myPid());
                        System.exit(1);
                    }
                });
                AlertDialog alertDialog = builder.create();
                ((App) AppVersionUpdateService.this.getApplicationContext()).setDialog(alertDialog);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                Log.i("AppVersionUpdateService", "展示完成");
            }
        }
    }
}

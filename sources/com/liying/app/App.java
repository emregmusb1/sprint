package com.liying.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import androidx.multidex.MultiDex;
import com.liying.app.h5.H5Managers;
import java.util.LinkedHashMap;
import java.util.Map;
import org.xutils.x;

public class App extends Application {
    private static Context mContext;
    private static Map<String, Runnable> taskList = new LinkedHashMap();
    private Activity mCurrentActivity = null;
    private AlertDialog mDialog = null;

    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        x.Ext.init(this);
        MultiDex.install(this);
        H5Managers.init(this);
    }

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    public Activity getCurrentActivity() {
        return this.mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity2) {
        this.mCurrentActivity = mCurrentActivity2;
        if (mCurrentActivity2 != null && !taskList.isEmpty()) {
            for (Runnable runnable : taskList.values()) {
                runnable.run();
            }
        }
        taskList.clear();
    }

    public AlertDialog getDialog() {
        return this.mDialog;
    }

    public void setDialog(AlertDialog dialog) {
        this.mDialog = dialog;
    }

    public void addTask(String key, Runnable task) {
        if (this.mCurrentActivity == null) {
            taskList.put(key, task);
        } else {
            task.run();
        }
    }
}

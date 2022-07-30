package com.liying.app.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import androidx.core.content.FileProvider;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ggccc.app.hbsdf23.R;
import com.liying.app.App;
import com.liying.app.util.Preference;
import java.io.File;
import java.io.UnsupportedEncodingException;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class AppUpdateManagers {
    private static String[] hosts;
    private static Preference preference;

    interface AppUpdateManager {
        public static final String APP_UPDATE_MANAGER = "app_update_manager";
        public static final String CURRENT_VERSION_PATH = "current_version_path";
        public static final String KEY_CURRENT_VERSION = "apk_current_version";
    }

    public static void init(Context context) {
        preference = Preference.obtain(App.getContext(), AppUpdateManager.APP_UPDATE_MANAGER);
        String urlString = context.getString(R.string.webUrl);
        if (urlString != null && !"".equals(urlString)) {
            if (isNeedInstall()) {
                Log.i("UpdateManager", "不需要更新");
                return;
            }
            if (!urlString.startsWith("http")) {
                try {
                    urlString = new String(Base64.decode(urlString, 0), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            hosts = urlString.split(";");
            String[] strArr = hosts;
            if (strArr != null && strArr.length != 0) {
                int i = 0;
                while (true) {
                    String[] strArr2 = hosts;
                    if (i >= strArr2.length) {
                        break;
                    }
                    if (strArr2[i] != null) {
                        strArr2[i] = strArr2[i].trim();
                    }
                    i++;
                }
            }
            requestHostVerify(hosts, 0);
        }
    }

    public static void setCurrentVersion(int version) {
        preference.save(AppUpdateManager.KEY_CURRENT_VERSION, Integer.valueOf(version));
    }

    public static int getCurrentVersion() {
        return ((Integer) preference.get(AppUpdateManager.KEY_CURRENT_VERSION, 0)).intValue();
    }

    public static void setCurrentVersionPath(String path) {
        preference.save(AppUpdateManager.CURRENT_VERSION_PATH, path);
    }

    public static String getCurrentVersionPath() {
        return (String) preference.get(AppUpdateManager.CURRENT_VERSION_PATH, "");
    }

    /* access modifiers changed from: private */
    public static void requestHostVerify(final String[] hosts2, final int index) {
        if (index <= hosts2.length - 1) {
            Log.i("UpdateManager", String.format("更新app接口  host:%s", new Object[]{hosts2[index]}));
            x.http().get(new RequestParams(hosts2[index]), new Callback.CommonCallback<String>() {
                public void onSuccess(String result) {
                    Log.i("UpdateManager", String.format("更新app接口 success  hosts:%s, args:%s", new Object[]{hosts2[index], result}));
                    JSONObject json = JSON.parseObject(result);
                    Log.i("UpdateManager", String.format("json 数据  result:%s, json:%s", new Object[]{result, json}));
                    if (json != null && json.getString("data") != null) {
                        String[] versionAndUrl = json.getString("data").split(";");
                        Log.i("UpdateManager", String.format("versionAndUrl 数据  result:%s", new Object[]{Integer.valueOf(versionAndUrl.length)}));
                        if (versionAndUrl.length == 2) {
                            String version = versionAndUrl[0];
                            String url = versionAndUrl[1];
                            Log.i("UpdateManager", String.format("准备启动服务  version:%s, url:%s", new Object[]{version, url}));
                            if (AppUpdateManagers.getAppVersionCode() < Integer.parseInt(version)) {
                                Log.i("UpdateManager", String.format("启动服务成功  version:%s, url:%s", new Object[]{version, url}));
                                try {
                                    Intent intent = new Intent(App.getContext(), AppVersionUpdateService.class);
                                    intent.putExtra("url", url);
                                    intent.putExtra("version", Integer.parseInt(version));
                                    App.getContext().startService(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("UpdateManager", "启动服务失败", e);
                                }
                            }
                        }
                    }
                }

                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i("UpdateManager", String.format("更新app接口 error  hosts:%s", new Object[]{hosts2[index]}));
                    AppUpdateManagers.requestHostVerify(hosts2, index + 1);
                }

                public void onCancelled(Callback.CancelledException cex) {
                }

                public void onFinished() {
                }
            });
        }
    }

    public static boolean isNeedInstall() {
        return getCurrentVersion() > getAppVersionCode() && checkApk();
    }

    public static void installApk() {
        try {
            ((App) App.getContext()).addTask("installApk", $$Lambda$AppUpdateManagers$ZFOQXjuMeFynjLoL09EvXyghh0.INSTANCE);
        } catch (Exception e) {
            Log.e("UPDATE", "installApk: ", e);
            e.printStackTrace();
        }
    }

    protected static Intent buildInstallIntent(Context context) {
        File apk = new File(getCurrentVersionPath());
        Intent intent = new Intent("android.intent.action.VIEW");
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setFlags(1);
            intent.setDataAndType(FileProvider.getUriForFile(context, App.getContext().getPackageName() + ".fileprovider", apk), "application/vnd.android.package-archive");
        } else {
            intent.setFlags(268435456);
            intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        }
        return intent;
    }

    public static int getAppVersionCode() {
        PackageInfo pInfo = null;
        try {
            pInfo = App.getContext().getPackageManager().getPackageInfo(App.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionCode;
    }

    public static boolean isApk(File apk) {
        if (!apk.exists()) {
            return false;
        }
        try {
            if (App.getContext().getPackageManager().getPackageArchiveInfo(apk.getAbsolutePath(), 1) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkApk() {
        String currentVersionPath = getCurrentVersionPath();
        if (currentVersionPath == null || "".equals(currentVersionPath)) {
            return false;
        }
        File apk = new File(currentVersionPath);
        if (!apk.exists()) {
            return false;
        }
        try {
            PackageManager pm = App.getContext().getPackageManager();
            Log.e("archiveFilePath", apk.getAbsolutePath());
            PackageInfo info = pm.getPackageArchiveInfo(apk.getAbsolutePath(), 1);
            if (info == null) {
                return false;
            }
            int currentVersion = getCurrentVersion();
            int versionCode = info.versionCode;
            Log.i("UpdateManager", String.format("安装包信息  versionCode:%s, packageName:%s, 接口版本:%s, appVersion: %s", new Object[]{Integer.valueOf(versionCode), info.packageName, Integer.valueOf(currentVersion), Integer.valueOf(getAppVersionCode())}));
            if (getAppVersionCode() >= versionCode || versionCode != currentVersion) {
                return false;
            }
            setCurrentVersion(info.versionCode);
            return true;
        } catch (Exception e) {
            setCurrentVersionPath("");
            return false;
        }
    }
}

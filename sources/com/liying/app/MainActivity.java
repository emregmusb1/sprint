package com.liying.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import com.ggccc.app.hbsdf23.R;
import com.liying.app.h5.H5Managers;
import com.liying.app.language.TipLanguageUtil;
import com.liying.app.update.AppUpdateManagers;
import com.liying.app.util.StatusBarUtils;
import com.liying.app.util.ToastUtils;
import com.liying.app.util.WebViewBugUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends BaseActivity {
    /* access modifiers changed from: private */
    public int FILECHOOSER_RESULTCODE = 1;
    private int REQUEST_SELECT_FILE = 100;
    /* access modifiers changed from: private */
    public AlphaAnimation alphaAnimation;
    /* access modifiers changed from: private */
    public boolean enabledGoBack = true;
    private long lastBackTime;
    @BindView(2131165266)
    LinearLayout llWeb;
    private boolean mCacheEnable = true;
    private ValueCallback<Uri> mFilePathCallback4;
    /* access modifiers changed from: private */
    public ValueCallback<Uri[]> mFilePathCallback5;
    private boolean mJavaScriptEnable = true;
    private String mUrl;
    @BindView(2131165280)
    ProgressBar progressBar;
    @BindView(2131165313)
    View statusBar;
    private WebSettings webSettings;
    WebView webkit;
    /* access modifiers changed from: private */
    public boolean webkitIsLoading;

    public int getLayoutId() {
        return R.layout.activity_web_view;
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            public final void run() {
                MainActivity.this.lambda$onCreate$0$MainActivity();
            }
        }, 1000);
        AppUpdateManagers.init(this);
    }

    public /* synthetic */ void lambda$onCreate$0$MainActivity() {
        getWindow().setBackgroundDrawableResource(R.color.main_BG);
        this.llWeb.setVisibility(0);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        ((App) getApplicationContext()).addTask("checkApkInstall", new Runnable() {
            public final void run() {
                MainActivity.this.lambda$onResume$1$MainActivity();
            }
        });
    }

    public /* synthetic */ void lambda$onResume$1$MainActivity() {
        Activity currentActivity;
        if (AppUpdateManagers.isNeedInstall() && (currentActivity = ((App) getApplicationContext()).getCurrentActivity()) != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
            builder.setTitle((CharSequence) TipLanguageUtil.getTip("提示")).setMessage((CharSequence) TipLanguageUtil.getTip("检测到新版本，是否更新程序？"));
            builder.setPositiveButton((CharSequence) TipLanguageUtil.getTip("更新"), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ((App) MainActivity.this.getApplicationContext()).setDialog((AlertDialog) null);
                    AppUpdateManagers.installApk();
                }
            });
            builder.setNegativeButton((CharSequence) TipLanguageUtil.getTip("退出"), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ((App) MainActivity.this.getApplicationContext()).setDialog((AlertDialog) null);
                    AtyContainer.getInstance().finishAllActivity();
                    Process.killProcess(Process.myPid());
                    System.exit(1);
                }
            });
            AlertDialog alertDialog = builder.create();
            ((App) getApplicationContext()).setDialog(alertDialog);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        final WebView.HitTestResult webViewHitTestResult = this.webkit.getHitTestResult();
        if (webViewHitTestResult.getType() == 5 || webViewHitTestResult.getType() == 8) {
            contextMenu.setHeaderTitle(isZh() ? "下载提示" : "Download tips");
            contextMenu.add(0, 1, 0, isZh() ? "保存图片到手机" : "Save picture to phone").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem menuItem) {
                    String DownloadImageURL = webViewHitTestResult.getExtra();
                    if (URLUtil.isValidUrl(DownloadImageURL)) {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadImageURL));
                        request.allowScanningByMediaScanner();
                        request.setMimeType("image/jpeg");
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + "qrcode_" + System.currentTimeMillis() + ".png");
                        ((DownloadManager) MainActivity.this.getSystemService("download")).enqueue(request);
                        Toast.makeText(MainActivity.this, MainActivity.this.isZh() ? "下载成功" : "download successful", 1).show();
                        return false;
                    }
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.createAndSaveFileFromBase64Url(DownloadImageURL, "qrcode_" + System.currentTimeMillis() + ".png");
                    return false;
                }
            });
        }
    }

    public String createAndSaveFileFromBase64Url(String url, String filename) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(path, filename);
        try {
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] decodedBytes = Base64.decode(url.substring(url.indexOf(",") + 1), 0);
            OutputStream os = new FileOutputStream(file);
            os.write(decodedBytes);
            os.close();
            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, (String[]) null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    StringBuilder sb = new StringBuilder();
                    sb.append("-> uri=");
                    sb.append(uri);
                    Log.i("ExternalStorage", sb.toString());
                }
            });
            Toast.makeText(this, isZh() ? "下载成功" : "download successful", 1).show();
        } catch (IOException e) {
            Toast.makeText(this, isZh() ? "下载失败" : "download failed", 1).show();
        }
        return file.toString();
    }

    private void saveImage(Bitmap finalBitmap, String image_name) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityCreate(@Nullable Bundle savedInstanceState) {
        super.onActivityCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        initWebView();
        this.statusBar.setVisibility(Build.VERSION.SDK_INT >= 19 ? 0 : 8);
        if (Build.VERSION.SDK_INT >= 19) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) this.statusBar.getLayoutParams();
            params.height = StatusBarUtils.getStatusBarHeight(this.mContext);
            this.statusBar.setLayoutParams(params);
        }
        getWindow().setFormat(-3);
        this.mUrl = H5Managers.getH5Url();
        this.webSettings = this.webkit.getSettings();
        boolean z = this.mJavaScriptEnable;
        if (z) {
            this.webSettings.setJavaScriptEnabled(z);
            this.webSettings.setAllowFileAccess(true);
            this.webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        }
        this.webSettings.setAllowUniversalAccessFromFileURLs(true);
        if (this.mCacheEnable) {
            this.webSettings.setAppCacheEnabled(true);
            this.webSettings.setAppCachePath(getCacheDir().getPath());
            this.webSettings.setCacheMode(-1);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.webSettings.setMixedContentMode(0);
        }
        this.webkit.addJavascriptInterface(new JSInterface(), "AndroidJS");
        this.webSettings.setSupportZoom(true);
        this.webSettings.setTextZoom(100);
        this.webSettings.setBuiltInZoomControls(true);
        this.webSettings.setDisplayZoomControls(false);
        this.webSettings.setUseWideViewPort(true);
        this.webSettings.setDomStorageEnabled(true);
        this.webSettings.setAllowFileAccess(true);
        this.webSettings.setAllowContentAccess(true);
        this.webSettings.setDatabaseEnabled(true);
        this.webSettings.setAppCacheEnabled(true);
        this.webSettings.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webkit, true);
        }
        this.webkit.setHorizontalScrollBarEnabled(false);
        this.webkit.setOverScrollMode(2);
        this.webkit.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.BROWSABLE");
                intent.setData(Uri.parse(url));
                MainActivity.this.startActivity(intent);
            }
        });
        this.webkit.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.e("MyApplication", consoleMessage.message() + " -- From line " + consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }

            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
            }

            public void onProgressChanged(WebView view, int newProgress) {
                boolean unused = MainActivity.this.webkitIsLoading = true;
                if (MainActivity.this.progressBar != null) {
                    MainActivity.this.progressBar.setProgress(newProgress);
                }
            }

            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                ValueCallback unused = MainActivity.this.mFilePathCallback5 = valueCallback;
                Intent i = new Intent();
                i.addCategory("android.intent.category.OPENABLE");
                i.setType("image/*");
                if (Build.VERSION.SDK_INT < 19) {
                    i.setAction("android.intent.action.GET_CONTENT");
                } else {
                    i.setAction("android.intent.action.OPEN_DOCUMENT");
                }
                MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.this.FILECHOOSER_RESULTCODE);
                return true;
            }
        });
        this.webkit.setWebViewClient(new WebViewClient() {
            private String gameUrl = null;
            private String toOutWebViewUrl = null;

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (MainActivity.this.progressBar != null) {
                    MainActivity.this.progressBar.setVisibility(0);
                }
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                boolean unused = MainActivity.this.webkitIsLoading = false;
                if (MainActivity.this.alphaAnimation != null) {
                    MainActivity.this.alphaAnimation.cancel();
                }
                if (MainActivity.this.alphaAnimation == null) {
                    AlphaAnimation unused2 = MainActivity.this.alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                    MainActivity.this.alphaAnimation.setDuration(1000);
                    MainActivity.this.alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        public void onAnimationStart(Animation animation) {
                        }

                        public void onAnimationEnd(Animation animation) {
                            if (MainActivity.this.progressBar != null) {
                                MainActivity.this.progressBar.setVisibility(8);
                            }
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
                if (MainActivity.this.progressBar != null) {
                    MainActivity.this.progressBar.startAnimation(MainActivity.this.alphaAnimation);
                }
                String str = this.toOutWebViewUrl;
                if (str != null && str.equals(url)) {
                    MainActivity.this.webkit.postDelayed(new Runnable() {
                        public void run() {
                            MainActivity.this.webkit.goBack();
                            MainActivity.this.webkit.postDelayed(new Runnable() {
                                public void run() {
                                    MainActivity.this.webkit.goBack();
                                }
                            }, 300);
                        }
                    }, 200);
                }
            }

            @Nullable
            private WebResourceResponse getWebResourceResponse(WebResourceRequest request) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(request.getUrl().toString()).openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("GET");
                    for (Map.Entry<String, String> entry : request.getRequestHeaders().entrySet()) {
                        connection.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                    return new WebResourceResponse(connection.getContentType(), connection.getHeaderField("encoding"), connection.getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e2) {
                    e2.printStackTrace();
                    return null;
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    this.toOutWebViewUrl = null;
                    if (!url.startsWith("mailto:")) {
                        if (!url.startsWith("sms:") && !url.startsWith("geo:")) {
                            if (!url.startsWith("tel:")) {
                                if (url.startsWith("tm-action")) {
                                    String[] commands = url.split("::");
                                    if (commands != null) {
                                        if (commands.length != 1) {
                                            String str = commands[0];
                                            String operate = commands[1];
                                            String arg1 = null;
                                            if (commands.length > 2) {
                                                arg1 = commands[2];
                                            }
                                            if (commands.length > 3) {
                                                String arg2 = commands[3];
                                            }
                                            if (commands.length > 4) {
                                                String arg3 = commands[4];
                                            }
                                            if ("openUrl".equals(operate) && arg1 != null) {
                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                intent.addCategory("android.intent.category.BROWSABLE");
                                                intent.setData(Uri.parse(arg1));
                                                MainActivity.this.startActivity(intent);
                                            }
                                            return true;
                                        }
                                    }
                                    return false;
                                } else if (!url.startsWith("weixin://") && !url.startsWith("alipays://") && !url.contains("whatsapp.com")) {
                                    return super.shouldOverrideUrlLoading(view, url);
                                } else {
                                    Intent intent2 = new Intent();
                                    intent2.setAction("android.intent.action.VIEW");
                                    intent2.setData(Uri.parse(url));
                                    MainActivity.this.startActivity(intent2);
                                    return true;
                                }
                            }
                        }
                    }
                    MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        });
        Uri.Builder builder = Uri.parse(this.mUrl).buildUpon();
        this.webkit.loadUrl(builder.build().toString());
        Log.e("URL", builder.build().toString());
        WebViewBugUtil.assistActivity(this);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("web_enabledGoBack", this.enabledGoBack);
        super.onSaveInstanceState(outState);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        this.enabledGoBack = savedInstanceState.getBoolean("web_enabledGoBack", true);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == this.FILECHOOSER_RESULTCODE) {
            ValueCallback<Uri[]> valueCallback = this.mFilePathCallback5;
            if (valueCallback != null) {
                valueCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                this.mFilePathCallback5 = null;
            } else {
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initWebView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
        this.webkit = new WebView(this);
        this.webkit.setLayoutParams(params);
        this.llWeb.addView(this.webkit);
        registerForContextMenu(this.webkit);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!this.enabledGoBack) {
            return super.onKeyDown(keyCode, event);
        }
        if (keyCode != 4 || !this.webkit.canGoBack()) {
            return super.onKeyDown(keyCode, event);
        }
        this.webkit.goBack();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        AlphaAnimation alphaAnimation2 = this.alphaAnimation;
        if (alphaAnimation2 != null) {
            alphaAnimation2.cancel();
            this.alphaAnimation = null;
        }
        WebView webView = this.webkit;
        if (webView != null) {
            webView.loadDataWithBaseURL((String) null, "", "text/html", "utf-8", (String) null);
            this.webkit.clearHistory();
            this.webkit.removeAllViews();
            releaseAllWebViewCallback();
            this.webkit.destroy();
            this.webkit = null;
        }
        super.onDestroy();
    }

    private void releaseAllWebViewCallback() {
        if (Build.VERSION.SDK_INT < 16) {
            try {
                Field field = WebView.class.getDeclaredField("mWebViewCore").getType().getDeclaredField("mBrowserFrame").getType().getDeclaredField("sConfigCallback");
                field.setAccessible(true);
                field.set((Object) null, (Object) null);
            } catch (IllegalAccessException | NoSuchFieldException e) {
            }
        } else {
            try {
                Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
                if (sConfigCallback != null) {
                    sConfigCallback.setAccessible(true);
                    sConfigCallback.set((Object) null, (Object) null);
                }
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e2) {
            }
        }
    }

    @JavascriptInterface
    public void alert(String message) {
        new AlertDialog.Builder(this).setMessage((CharSequence) message).setPositiveButton((CharSequence) "确定", (DialogInterface.OnClickListener) null).show();
    }

    @JavascriptInterface
    public void toast(String message) {
        TextUtils.isEmpty(message);
    }

    @JavascriptInterface
    public void invalidate() {
    }

    public void onBackPressed() {
        if (this.enabledGoBack) {
            if (this.webkit.canGoBack()) {
                this.webkit.goBack();
            } else if (System.currentTimeMillis() - this.lastBackTime > 2000) {
                this.lastBackTime = System.currentTimeMillis();
                Locale locale = getResources().getConfiguration().locale;
                String language = locale.getLanguage();
                if (locale.getLanguage().equals(new Locale("zh").getLanguage())) {
                    ToastUtils.showShort(this.mContext, (CharSequence) "再按一次退出程序");
                } else {
                    ToastUtils.showShort(this.mContext, (CharSequence) "Press again to exit the program");
                }
            } else {
                finish();
            }
        }
    }

    public boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (locale.getLanguage().equals(new Locale("zh").getLanguage())) {
            return true;
        }
        return false;
    }

    class JSInterface {
        JSInterface() {
        }

        @JavascriptInterface
        public void enabledBack() {
            boolean unused = MainActivity.this.enabledGoBack = true;
        }

        @JavascriptInterface
        public void disabledBack() {
            boolean unused = MainActivity.this.enabledGoBack = false;
        }

        @JavascriptInterface
        public void toPortrait() {
            enabledBack();
        }

        @JavascriptInterface
        public void toLandscape() {
            disabledBack();
        }

        @JavascriptInterface
        public void saveClientData(String key, String value) {
            MainActivity.this.getSharedPreferences("tm-app_CLIENT_DATA", 0).edit().putString(key, value).apply();
        }

        @JavascriptInterface
        public void updateH5Resource(String version, String url) {
            H5Managers.startLoadH5ZipService(version, url);
        }

        @JavascriptInterface
        public String getClientData(String key) {
            return MainActivity.this.getSharedPreferences("tm-app_CLIENT_DATA", 0).getString(key, "");
        }
    }
}

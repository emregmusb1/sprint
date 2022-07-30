package com.liying.app.h5;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.liying.app.util.Preference;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class H5Managers {
    public static final String LOCAl_H5_URL = "local_h5_url";
    private static Context mContext;
    private static Preference preference;

    interface H5Manager {
        public static final String H5MANAGER = "h5_manager";
        public static final String KEY_CURRENT_VERSION = "key_current_version";
        public static final String KEY_TEMP_VERSION = "key_temp_version";
    }

    public static void init(Context context) {
        mContext = context;
        preference = Preference.obtain(mContext, H5Manager.H5MANAGER);
        initH5Zip();
    }

    static void setCurrentVersion(String version) {
        preference.save(H5Manager.KEY_CURRENT_VERSION, version);
    }

    static String getCurrentVersion() {
        return ((String) preference.get(H5Manager.KEY_CURRENT_VERSION, "0")).trim();
    }

    static void setTempVersion(String version) {
        preference.save(H5Manager.KEY_TEMP_VERSION, version);
    }

    static String getTempVersion() {
        return ((String) preference.get(H5Manager.KEY_TEMP_VERSION, "0")).trim();
    }

    public static void clearPreferences() {
        preference.clearAll();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0086, code lost:
        r12 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0087, code lost:
        r13 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x008b, code lost:
        r13 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x008c, code lost:
        r15 = r13;
        r13 = r12;
        r12 = r15;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void initH5Zip() {
        /*
            java.lang.String r0 = "UpdateService"
            java.io.File r1 = new java.io.File
            android.content.Context r2 = mContext
            java.io.File r2 = r2.getFilesDir()
            java.lang.String r3 = "local_h5_url"
            r1.<init>(r2, r3)
            boolean r2 = r1.exists()
            r4 = 0
            if (r2 != 0) goto L_0x0024
            java.lang.String r0 = "dist.zip"
            android.content.Context r2 = mContext     // Catch:{ IOException -> 0x001e }
            unZip(r4, r3, r0, r2)     // Catch:{ IOException -> 0x001e }
            goto L_0x0022
        L_0x001e:
            r0 = move-exception
            r0.printStackTrace()
        L_0x0022:
            goto L_0x00fa
        L_0x0024:
            java.io.File r2 = new java.io.File
            android.content.Context r5 = mContext
            java.io.File r5 = r5.getFilesDir()
            java.lang.String r6 = "loadH5"
            r2.<init>(r5, r6)
            boolean r5 = r2.exists()
            if (r5 != 0) goto L_0x0038
            return
        L_0x0038:
            r5 = 1
            java.util.zip.ZipFile r6 = new java.util.zip.ZipFile     // Catch:{ Exception -> 0x00c2 }
            r6.<init>(r2)     // Catch:{ Exception -> 0x00c2 }
            java.lang.String r7 = "dist/index.html"
            java.util.zip.ZipEntry r7 = r6.getEntry(r7)     // Catch:{ Exception -> 0x00c2 }
            java.io.InputStream r8 = r6.getInputStream(r7)     // Catch:{ Exception -> 0x00c2 }
            r9 = 4096(0x1000, float:5.74E-42)
            r10 = 0
            byte[] r9 = new byte[r9]     // Catch:{ Throwable -> 0x00ae }
            java.io.ByteArrayOutputStream r11 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x009e }
            r11.<init>()     // Catch:{ Exception -> 0x009e }
        L_0x0052:
            int r12 = r8.read(r9)     // Catch:{ Throwable -> 0x0089, all -> 0x0086 }
            if (r12 <= 0) goto L_0x005c
            r11.write(r9)     // Catch:{ Throwable -> 0x0089, all -> 0x0086 }
            goto L_0x0052
        L_0x005c:
            java.lang.String r12 = r11.toString()     // Catch:{ Throwable -> 0x0089, all -> 0x0086 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0089, all -> 0x0086 }
            r13.<init>()     // Catch:{ Throwable -> 0x0089, all -> 0x0086 }
            java.lang.String r14 = "index html:"
            r13.append(r14)     // Catch:{ Throwable -> 0x0089, all -> 0x0086 }
            r13.append(r12)     // Catch:{ Throwable -> 0x0089, all -> 0x0086 }
            java.lang.String r13 = r13.toString()     // Catch:{ Throwable -> 0x0089, all -> 0x0086 }
            android.util.Log.d(r0, r13)     // Catch:{ Throwable -> 0x0089, all -> 0x0086 }
            java.lang.String r13 = "var APP_VERSION"
            boolean r13 = r12.contains(r13)     // Catch:{ Throwable -> 0x0089, all -> 0x0086 }
            if (r13 != 0) goto L_0x0082
            java.lang.String r13 = "version not match: html"
            android.util.Log.d(r0, r13)     // Catch:{ Throwable -> 0x0089, all -> 0x0086 }
            r5 = 0
        L_0x0082:
            r11.close()     // Catch:{ Exception -> 0x009e }
            goto L_0x00a6
        L_0x0086:
            r12 = move-exception
            r13 = r10
            goto L_0x008f
        L_0x0089:
            r12 = move-exception
            throw r12     // Catch:{ all -> 0x008b }
        L_0x008b:
            r13 = move-exception
            r15 = r13
            r13 = r12
            r12 = r15
        L_0x008f:
            if (r13 == 0) goto L_0x009a
            r11.close()     // Catch:{ Throwable -> 0x0095 }
            goto L_0x009d
        L_0x0095:
            r14 = move-exception
            r13.addSuppressed(r14)     // Catch:{ Exception -> 0x009e }
            goto L_0x009d
        L_0x009a:
            r11.close()     // Catch:{ Exception -> 0x009e }
        L_0x009d:
            throw r12     // Catch:{ Exception -> 0x009e }
        L_0x009e:
            r11 = move-exception
            java.lang.String r12 = "file not found: html"
            android.util.Log.d(r0, r12)     // Catch:{ Throwable -> 0x00ae }
            r0 = 0
            r5 = r0
        L_0x00a6:
            if (r8 == 0) goto L_0x00ab
            r8.close()     // Catch:{ Exception -> 0x00c2 }
        L_0x00ab:
            goto L_0x00c4
        L_0x00ac:
            r0 = move-exception
            goto L_0x00b1
        L_0x00ae:
            r0 = move-exception
            r10 = r0
            throw r10     // Catch:{ all -> 0x00ac }
        L_0x00b1:
            if (r8 == 0) goto L_0x00c1
            if (r10 == 0) goto L_0x00be
            r8.close()     // Catch:{ Throwable -> 0x00b9 }
            goto L_0x00c1
        L_0x00b9:
            r9 = move-exception
            r10.addSuppressed(r9)     // Catch:{ Exception -> 0x00c2 }
            goto L_0x00c1
        L_0x00be:
            r8.close()     // Catch:{ Exception -> 0x00c2 }
        L_0x00c1:
            throw r0     // Catch:{ Exception -> 0x00c2 }
        L_0x00c2:
            r0 = move-exception
            r5 = 0
        L_0x00c4:
            if (r5 != 0) goto L_0x00cc
            r2.delete()     // Catch:{ Exception -> 0x00ca }
            return
        L_0x00ca:
            r0 = move-exception
            goto L_0x00f0
        L_0x00cc:
            r0 = 1
            java.lang.String r6 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x00ca }
            android.content.Context r7 = mContext     // Catch:{ Exception -> 0x00ca }
            unZip(r0, r3, r6, r7)     // Catch:{ Exception -> 0x00ca }
            java.lang.String r0 = getTempVersion()     // Catch:{ Exception -> 0x00ca }
            setCurrentVersion(r0)     // Catch:{ Exception -> 0x00ca }
            r2.delete()     // Catch:{ Exception -> 0x00ca }
            android.content.Context r0 = mContext     // Catch:{ Exception -> 0x00ca }
            java.lang.String r3 = "更新成功"
            java.lang.String r3 = com.liying.app.language.TipLanguageUtil.getTip(r3)     // Catch:{ Exception -> 0x00ca }
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r3, r4)     // Catch:{ Exception -> 0x00ca }
            r0.show()     // Catch:{ Exception -> 0x00ca }
            goto L_0x00fa
        L_0x00f0:
            java.lang.String r3 = "H5"
            java.lang.String r4 = "initH5Zip: error"
            android.util.Log.d(r3, r4, r0)
            r2.delete()
        L_0x00fa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.liying.app.h5.H5Managers.initH5Zip():void");
    }

    public static void startLoadH5ZipService(String version, String url) {
        if (!getCurrentVersion().equals(version)) {
            Intent intent = new Intent(mContext, UpdateService.class);
            intent.putExtra("version", version);
            intent.putExtra("versionUrl", url);
            mContext.startService(intent);
        }
    }

    static void unZip(boolean isReWrite, String outputDirectory, String fileName, Context context) throws IOException {
        InputStream inputStream;
        File parent = context.getFilesDir();
        File file = new File(parent, outputDirectory);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (isReWrite) {
            inputStream = new FileInputStream(new File(fileName));
        } else {
            inputStream = context.getAssets().open(fileName);
        }
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        byte[] buffer = new byte[1048576];
        for (ZipEntry zipEntry = zipInputStream.getNextEntry(); zipEntry != null; zipEntry = zipInputStream.getNextEntry()) {
            if (zipEntry.isDirectory()) {
                File file2 = new File(parent, outputDirectory + File.separator + zipEntry.getName());
                if (isReWrite || !file2.exists()) {
                    file2.mkdir();
                }
            } else {
                File file3 = new File(parent, outputDirectory + File.separator + zipEntry.getName());
                if (isReWrite || !file3.exists()) {
                    file3.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file3);
                    while (true) {
                        int read = zipInputStream.read(buffer);
                        int count = read;
                        if (read <= 0) {
                            break;
                        }
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
        }
        zipInputStream.close();
    }

    public static String getH5Url() {
        return Uri.fromFile(new File(mContext.getFilesDir(), "/local_h5_url/dist/index.html")).toString();
    }
}

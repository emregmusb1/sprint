package org.xutils.http.loader;

import android.text.TextUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import org.xutils.cache.DiskCacheEntity;
import org.xutils.cache.DiskCacheFile;
import org.xutils.cache.LruDiskCache;
import org.xutils.common.Callback;
import org.xutils.common.util.IOUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.common.util.ProcessLock;
import org.xutils.ex.FileLockedException;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.http.request.UriRequest;

public class FileLoader extends Loader<File> {
    private static final int CHECK_SIZE = 512;
    private long contentLength;
    private DiskCacheFile diskCacheFile;
    private boolean isAutoRename;
    private boolean isAutoResume;
    private RequestParams params;
    private String responseFileName;
    private String saveFilePath;
    private String tempSaveFilePath;

    public Loader<File> newInstance() {
        return new FileLoader();
    }

    public void setParams(RequestParams params2) {
        if (params2 != null) {
            this.params = params2;
            this.isAutoResume = params2.isAutoResume();
            this.isAutoRename = params2.isAutoRename();
        }
    }

    /* access modifiers changed from: protected */
    public File load(InputStream in) throws Throwable {
        FileOutputStream fileOutputStream;
        String str;
        FileInputStream fis;
        InputStream inputStream = in;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            File targetFile = new File(this.tempSaveFilePath);
            if (!targetFile.isDirectory()) {
                if (!targetFile.exists()) {
                    File dir = targetFile.getParentFile();
                    if ((!dir.exists() && !dir.mkdirs()) || !dir.isDirectory()) {
                        throw new IOException("could not create the dir: " + dir.getAbsolutePath());
                    }
                }
                long targetFileLen = targetFile.length();
                if (this.isAutoResume && targetFileLen > 0) {
                    fis = null;
                    long filePos = targetFileLen - 512;
                    if (filePos > 0) {
                        fis = new FileInputStream(targetFile);
                        if (Arrays.equals(IOUtil.readBytes(inputStream, 0, 512), IOUtil.readBytes(fis, filePos, 512))) {
                            this.contentLength -= 512;
                            IOUtil.closeQuietly((Closeable) fis);
                        } else {
                            IOUtil.closeQuietly((Closeable) fis);
                            IOUtil.deleteFileOrDir(targetFile);
                            throw new RuntimeException("need retry");
                        }
                    } else {
                        IOUtil.deleteFileOrDir(targetFile);
                        throw new RuntimeException("need retry");
                    }
                }
                long current = 0;
                if (this.isAutoResume) {
                    current = targetFileLen;
                    fileOutputStream = new FileOutputStream(targetFile, true);
                } else {
                    fileOutputStream = new FileOutputStream(targetFile);
                }
                long total = this.contentLength + current;
                bis = new BufferedInputStream(inputStream);
                bos = new BufferedOutputStream(fileOutputStream);
                if (this.progressHandler != null) {
                    FileOutputStream fileOutputStream2 = fileOutputStream;
                    str = "download stopped!";
                    if (!this.progressHandler.updateProgress(total, current, true)) {
                        throw new Callback.CancelledException(str);
                    }
                } else {
                    str = "download stopped!";
                }
                byte[] tmp = new byte[4096];
                while (true) {
                    int read = bis.read(tmp);
                    int len = read;
                    if (read == -1) {
                        bos.flush();
                        if (this.diskCacheFile != null) {
                            targetFile = this.diskCacheFile.commit();
                        }
                        if (this.progressHandler != null) {
                            this.progressHandler.updateProgress(total, current, true);
                        }
                        IOUtil.closeQuietly((Closeable) bis);
                        IOUtil.closeQuietly((Closeable) bos);
                        return autoRename(targetFile);
                    } else if (targetFile.getParentFile().exists()) {
                        bos.write(tmp, 0, len);
                        current += (long) len;
                        if (this.progressHandler != null) {
                            int i = len;
                            if (!this.progressHandler.updateProgress(total, current, false)) {
                                bos.flush();
                                throw new Callback.CancelledException(str);
                            }
                        }
                    } else {
                        targetFile.getParentFile().mkdirs();
                        throw new IOException("parent be deleted!");
                    }
                }
            } else {
                throw new IOException("could not create the file: " + this.tempSaveFilePath);
            }
        } catch (Throwable th) {
            IOUtil.closeQuietly((Closeable) bis);
            IOUtil.closeQuietly((Closeable) bos);
            throw th;
        }
    }

    public File load(UriRequest request) throws Throwable {
        File result;
        File result2;
        ProcessLock processLock = null;
        try {
            this.saveFilePath = this.params.getSaveFilePath();
            this.diskCacheFile = null;
            if (TextUtils.isEmpty(this.saveFilePath)) {
                if (this.progressHandler != null) {
                    if (!this.progressHandler.updateProgress(0, 0, false)) {
                        throw new Callback.CancelledException("download stopped!");
                    }
                }
                initDiskCacheFile(request);
            } else {
                this.tempSaveFilePath = this.saveFilePath + ".tmp";
            }
            if (this.progressHandler != null) {
                if (!this.progressHandler.updateProgress(0, 0, false)) {
                    throw new Callback.CancelledException("download stopped!");
                }
            }
            processLock = ProcessLock.tryLock(this.saveFilePath + "_lock", true);
            if (processLock == null || !processLock.isValid()) {
                throw new FileLockedException("download exists: " + this.saveFilePath);
            }
            this.params = request.getParams();
            long range = 0;
            if (this.isAutoResume) {
                File tempFile = new File(this.tempSaveFilePath);
                long fileLen = tempFile.length();
                if (fileLen <= 512) {
                    IOUtil.deleteFileOrDir(tempFile);
                    range = 0;
                } else {
                    range = fileLen - 512;
                }
            }
            RequestParams requestParams = this.params;
            requestParams.setHeader("Range", "bytes=" + range + "-");
            if (this.progressHandler != null) {
                if (!this.progressHandler.updateProgress(0, 0, false)) {
                    throw new Callback.CancelledException("download stopped!");
                }
            }
            request.sendRequest();
            this.contentLength = request.getContentLength();
            if (this.isAutoRename) {
                this.responseFileName = getResponseFileName(request);
            }
            if (this.isAutoResume) {
                this.isAutoResume = isSupportRange(request);
            }
            if (this.progressHandler != null) {
                if (!this.progressHandler.updateProgress(0, 0, false)) {
                    throw new Callback.CancelledException("download stopped!");
                }
            }
            if (this.diskCacheFile != null) {
                try {
                    DiskCacheEntity entity = this.diskCacheFile.getCacheEntity();
                    entity.setLastAccess(System.currentTimeMillis());
                    entity.setEtag(request.getETag());
                    entity.setExpires(request.getExpiration());
                    entity.setLastModify(new Date(request.getLastModified()));
                } catch (Throwable ex) {
                    LogUtil.e(ex.getMessage(), ex);
                }
            }
            result = load(request.getInputStream());
            IOUtil.closeQuietly((Closeable) processLock);
            IOUtil.closeQuietly((Closeable) this.diskCacheFile);
            return result;
        } catch (HttpException httpException) {
            if (httpException.getCode() == 416) {
                if (this.diskCacheFile != null) {
                    result2 = this.diskCacheFile.commit();
                } else {
                    result2 = new File(this.tempSaveFilePath);
                }
                if (result2 == null || !result2.exists()) {
                    IOUtil.deleteFileOrDir(result2);
                    throw new IllegalStateException("cache file not found" + request.getCacheKey());
                }
                if (this.isAutoRename) {
                    this.responseFileName = getResponseFileName(request);
                }
                result = autoRename(result2);
            } else {
                throw httpException;
            }
        } catch (Throwable th) {
            IOUtil.closeQuietly((Closeable) processLock);
            IOUtil.closeQuietly((Closeable) this.diskCacheFile);
            throw th;
        }
    }

    private void initDiskCacheFile(UriRequest request) throws Throwable {
        DiskCacheEntity entity = new DiskCacheEntity();
        entity.setKey(request.getCacheKey());
        this.diskCacheFile = LruDiskCache.getDiskCache(this.params.getCacheDirName()).createDiskCacheFile(entity);
        DiskCacheFile diskCacheFile2 = this.diskCacheFile;
        if (diskCacheFile2 != null) {
            this.saveFilePath = diskCacheFile2.getAbsolutePath();
            this.tempSaveFilePath = this.saveFilePath;
            this.isAutoRename = false;
            return;
        }
        throw new IOException("create cache file error:" + request.getCacheKey());
    }

    private File autoRename(File loadedFile) {
        if (this.isAutoRename && loadedFile.exists() && !TextUtils.isEmpty(this.responseFileName)) {
            File newFile = new File(loadedFile.getParent(), this.responseFileName);
            while (newFile.exists()) {
                String parent = loadedFile.getParent();
                newFile = new File(parent, System.currentTimeMillis() + this.responseFileName);
            }
            return loadedFile.renameTo(newFile) ? newFile : loadedFile;
        } else if (this.saveFilePath.equals(this.tempSaveFilePath)) {
            return loadedFile;
        } else {
            File newFile2 = new File(this.saveFilePath);
            return loadedFile.renameTo(newFile2) ? newFile2 : loadedFile;
        }
    }

    private static String getResponseFileName(UriRequest request) {
        int startIndex;
        if (request == null) {
            return null;
        }
        String disposition = request.getResponseHeader("Content-Disposition");
        if (!TextUtils.isEmpty(disposition) && (startIndex = disposition.indexOf("filename=")) > 0) {
            int startIndex2 = startIndex + 9;
            int endIndex = disposition.indexOf(";", startIndex2);
            if (endIndex < 0) {
                endIndex = disposition.length();
            }
            if (endIndex > startIndex2) {
                try {
                    String name = URLDecoder.decode(disposition.substring(startIndex2, endIndex), request.getParams().getCharset());
                    if (!name.startsWith("\"") || !name.endsWith("\"")) {
                        return name;
                    }
                    return name.substring(1, name.length() - 1);
                } catch (UnsupportedEncodingException ex) {
                    LogUtil.e(ex.getMessage(), ex);
                }
            }
        }
        return null;
    }

    private static boolean isSupportRange(UriRequest request) {
        if (request == null) {
            return false;
        }
        String ranges = request.getResponseHeader("Accept-Ranges");
        if (ranges != null) {
            return ranges.contains("bytes");
        }
        String ranges2 = request.getResponseHeader("Content-Range");
        if (ranges2 == null || !ranges2.contains("bytes")) {
            return false;
        }
        return true;
    }

    public File loadFromCache(DiskCacheEntity cacheEntity) throws Throwable {
        return LruDiskCache.getDiskCache(this.params.getCacheDirName()).getDiskCacheFile(cacheEntity.getKey());
    }

    public void save2Cache(UriRequest request) {
    }
}

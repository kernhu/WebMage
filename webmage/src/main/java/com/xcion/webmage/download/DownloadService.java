package com.xcion.webmage.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.xcion.webmage.BuildConfig;
import com.xcion.webmage.download.entity.FileInfo;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/23 17:17
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/23 17:17
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class DownloadService extends Service {

    private static final String TAG = "downloader";

    private static final int MSG_INIT = 1000;
    public static final String KEY_TASK = "key_task";
    private DownloadBinder mDownloadBinder = new DownloadBinder();
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    private DownloadTask mDownloadTask = null;

    public class DownloadBinder extends Binder {

        public DownloadService getService() {
            return DownloadService.this;
        }

        public void joinQueue(Intent intent) {
            LinkedList<FileInfo> fileInfos = (LinkedList<FileInfo>) intent.getSerializableExtra(KEY_TASK);
            for (FileInfo info : fileInfos) {
                mExecutorService.execute(new DownloadRunnable(info));
            }
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mDownloadBinder;
    }

    /**
     *
     */
    class DownloadRunnable implements Runnable {

        private FileInfo fileInfo;

        public DownloadRunnable(FileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }

        @Override
        public void run() {

            if (FileInfo.STATE_OK != fileInfo.getState()) { // 如果未准备OK，则重新解析File Info

                HttpURLConnection httpConn = null;
                try {
                    URL url = new URL(fileInfo.getUrl());
                    httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setRequestMethod("GET");
                    httpConn.setRequestProperty("Charset", "UTF-8");
                    httpConn.setConnectTimeout(Downloader.getDownloadOptions().getConnectTimeout());
                    httpConn.setRequestProperty("User-Agent", Downloader.getDownloadOptions().getUserAgentProperty());
                    httpConn.setRequestProperty("Accept", Downloader.getDownloadOptions().getAcceptProperty());
                    httpConn.setReadTimeout(Downloader.getDownloadOptions().getReadTimeout());
                    httpConn.connect();
                    /******************************************************************************************/
                    /******************************************************************************************/
                    /******************************************************************************************/
                    if (BuildConfig.DEBUG) {
                        for (String key : httpConn.getHeaderFields().keySet()) {
                            Log.i("File信息解析", "key=" + key + "<<<>>>value=" + httpConn.getHeaderFields().get(key).toString());
                        }
                    }
                    /******************************************************************************************/
                    /******************************************************************************************/
                    /******************************************************************************************/
                    if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        /******************* file name *******************/
                        String disposition = httpConn.getHeaderField("Content-Disposition");
                        String fileName = "";
                        if (disposition != null && disposition.contains("filename=")) {
                            String dispositionSplit[] = disposition.split("filename=");
                            if (dispositionSplit[1] != null && !TextUtils.isEmpty(dispositionSplit[1])) {
                                fileName = dispositionSplit[1].replace("filename=", "").replace("\"", "").trim();
                            }
                        }
                        if (TextUtils.isEmpty(fileName)) {
                            fileName = url.getFile().substring(url.getFile().lastIndexOf("/") + 1, url.getFile().length()).split("\\?")[0];
                        }
                        /******************* mime type *******************/
                        String mimeType = httpConn.getHeaderField("Content-Type");
                        /******************* file length *******************/
                        long fileLength = Long.parseLong(TextUtils.isEmpty(httpConn.getHeaderField("Content-Length")) ? "0" : httpConn.getHeaderField("Content-Length"));
                        if (fileLength == 0) {
                            fileLength = fileLength >= 0 ? httpConn.getContentLength() : 0;
                        }

                        //文件长度小于等于0时则解析失败，反之成功继续下载
                        if (fileLength <= 0) {
                            fileInfo.setState(FileInfo.STATE_HTTP_ERROR);
                            fileInfo.setResult("could not get file length by the url.");
                        } else {
                            /*****>>>>>>>>>>>>>>>>>>>>>>>>*****/
                            fileInfo.setState(FileInfo.STATE_OK);
                            fileInfo.setResult("");
                        }
                        fileInfo.setFileName(fileName);
                        fileInfo.setMimeType(mimeType);
                        fileInfo.setLength(fileLength);
                    } else {
                        fileInfo.setState(FileInfo.STATE_HTTP_ERROR);
                        fileInfo.setResult(httpConn.getResponseMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fileInfo.setState(FileInfo.STATE_EXCEPTION);
                    fileInfo.setResult(e.getMessage());
                    if (httpConn != null) {
                        httpConn.disconnect();
                    }
                }

            }
            /******************************************************************************************/
            /******************************************************************************************/
            /******************************************************************************************/
            Message message = mHandler.obtainMessage(MSG_INIT, fileInfo);
            message.sendToTarget();
        }
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            final FileInfo mFileInfo = (FileInfo) msg.obj;
            //下载
            mDownloadTask = new DownloadTask(getBaseContext(), mFileInfo);
            mDownloadTask.download();
        }
    };
}

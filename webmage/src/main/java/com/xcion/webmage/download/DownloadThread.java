package com.xcion.webmage.download;

import android.content.Context;
import android.util.Log;


import com.xcion.webmage.download.broadcast.Broadcaster;
import com.xcion.webmage.download.db.DBLeader;
import com.xcion.webmage.download.entity.FileInfo;
import com.xcion.webmage.download.entity.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/28 9:55
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/28 9:55
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class DownloadThread implements Runnable, Controller {

    public static final String TAG = "downloader";

    private DBLeader mDBLeader;
    private WeakReference<Context> reference;
    private FileInfo fileInfo;
    private ThreadInfo threadInfo;

    private String tempPath;
    private State state = State.START;
    private boolean isDownloading;
    private long lastTimeMillis;
    private long rangeLength;

    public enum State {
        START,
        PAUSE,
        CANCEL,
    }

    @Override
    public void startDownload() {
        state = State.START;
    }

    @Override
    public void pauseDownload() {
        state = State.PAUSE;
    }

    @Override
    public void cancelDownload() {
        state = State.CANCEL;
    }

    @Override
    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDBLeader(DBLeader leader) {
        mDBLeader = leader;
    }

    public DownloadThread(Context context, FileInfo fileInfo, ThreadInfo threadInfo) {
        this.reference = new WeakReference<>(context);
        this.fileInfo = fileInfo;
        this.threadInfo = threadInfo;
        tempPath = Downloader.getDownloadOptions().getStoragePath() + "/temp/" + fileInfo.getFileName() + "_" + threadInfo.getThreadId() + ".properties";
    }

    HttpURLConnection httpConn;
    InputStream is;

    @Override
    public void run() {
        try {
            URL url = new URL(fileInfo.getUrl());
            httpConn = (HttpURLConnection) url.openConnection();
            //在头里面请求下载开始位置和结束位置
            httpConn.setRequestProperty("Range", "bytes=" + threadInfo.getStart() + "-" + threadInfo.getEnds());
            httpConn.setRequestMethod("GET");
            httpConn.setRequestProperty("Charset", "UTF-8");
            httpConn.setConnectTimeout(Downloader.getDownloadOptions().getConnectTimeout());
            httpConn.setRequestProperty("User-Agent", Downloader.getDownloadOptions().getUserAgentProperty());
            httpConn.setRequestProperty("Accept", Downloader.getDownloadOptions().getAcceptProperty());
            httpConn.setReadTimeout(Downloader.getDownloadOptions().getReadTimeout());
            is = httpConn.getInputStream();
            //创建可设置位置的文件
            RandomAccessFile file = new RandomAccessFile(fileInfo.getFilePath(), "rwd");
            //设置每条线程写入文件的位置
            file.seek(threadInfo.getStart());
            byte[] buffer = new byte[1024 * 2];
            int len;
            while ((len = is.read(buffer)) != -1) {
                //取消、暂停
                if (state == State.CANCEL) {
                    threadInfo.setState(ThreadInfo.STATE_CANCEL);
                    break;
                }
                if (state == State.PAUSE) {
                    threadInfo.setState(ThreadInfo.STATE_PAUSE);
                    break;
                }

                //把下载数据数据写入文件
                isDownloading = true;
                file.write(buffer, 0, len);
                rangeLength += len;
                fileInfo.setState(FileInfo.STATE_DOWNLOADING);
                fileInfo.setResult("");
                threadInfo.setCurrent(threadInfo.getStart() + rangeLength);
                threadInfo.setState(ThreadInfo.STATE_DOWNLOADING);

                //每隔N秒更新一次数据库并发送一次广告，该过程稍微耗时，所以尽可能控制在1s操作一次
                if (System.currentTimeMillis() - lastTimeMillis >= 1.5 * 1000) {
                    lastTimeMillis = System.currentTimeMillis();
                    synchronized (DownloadThread.this) {
                        //同步进度
                        mDBLeader.updateOrReplaceFile(fileInfo);
                        mDBLeader.updateOrReplaceThread(threadInfo);
                        //发广播
                        if (reference.get() != null) {
                            Broadcaster
                                    .getInstance(reference.get())
                                    .setAction(Downloader.ACTION_DOWNLOADING)
                                    .setFileInfo(mDBLeader.getTaskByUrl(fileInfo.getUrl()))
                                    .setThreadInfo((ArrayList<ThreadInfo>) mDBLeader.getThreadByUrl(fileInfo.getUrl()))
                                    .send();
                        }
                    }
                }
                /**********************************************************************************/
            }
            file.close();
            is.close();


            /*************************************************************************************/
            /*************************************************************************************/
            //取消下载
            if (state == State.CANCEL) {
                synchronized (DownloadThread.this) {
                    mDBLeader.updateOrReplaceThread(threadInfo);
                    List<ThreadInfo> infos = mDBLeader.getThreadByUrl(fileInfo.getUrl());
                    boolean cancel = false;
                    for (ThreadInfo info : infos) {
                        if (info.getState() == ThreadInfo.STATE_CANCEL) {
                            cancel = true;
                        } else {
                            cancel = false;
                        }
                    }
                    if (cancel) {
                        File tempFile = new File(tempPath);
                        File realFile = new File(fileInfo.getFilePath());
                        if (tempFile.exists()) {
                            tempFile.delete();
                        }
                        if (realFile.exists()) {
                            realFile.delete();
                        }
                        isDownloading = false;

                        fileInfo.setState(FileInfo.STATE_CANCELED);
                        fileInfo.setResult("");
                        mDBLeader.updateOrReplaceFile(fileInfo);
                        mDBLeader.updateOrReplaceThread(threadInfo);
                        if (reference.get() != null)
                        Broadcaster.getInstance(reference.get()).setAction(Downloader.ACTION_CANCELLED).setFileInfo(fileInfo).send();
                        Log.e(TAG, "---" + fileInfo.getFileName() + "----" + threadInfo.getThreadId() + "-----取消下载！！！！");
                        System.gc();
                    }
                }
                return;
            }
            /*************************************************************************************/
            /*************************************************************************************/

            /*************************************************************************************/
            /*************************************************************************************/
            //停止状态不需要删除记录文件
            if (state == State.PAUSE) {
                synchronized (DownloadThread.this) {
                    mDBLeader.updateOrReplaceThread(threadInfo);
                    List<ThreadInfo> infos = mDBLeader.getThreadByUrl(fileInfo.getUrl());
                    boolean pause = false;
                    for (ThreadInfo info : infos) {
                        if (info.getState() == ThreadInfo.STATE_COMPLETED) {
                            pause = true;
                        } else {
                            pause = false;
                        }
                    }
                    if (pause) {
                        fileInfo.setState(FileInfo.STATE_PAUSED);
                        fileInfo.setResult("");
                        mDBLeader.updateOrReplaceFile(fileInfo);
                        mDBLeader.updateOrReplaceThread(threadInfo);
                        if (reference.get() != null)
                        Broadcaster.getInstance(reference.get()).setAction(Downloader.ACTION_STOPPED).setFileInfo(fileInfo).send();
                        isDownloading = false;
                        System.gc();
                        Log.e(TAG, "---" + fileInfo.getFileName() + "----" + threadInfo.getThreadId() + "-----暂停下载！！！！");
                    }
                }
                return;
            }
            /*************************************************************************************/
            /*************************************************************************************/
            threadInfo.setState(threadInfo.getCurrent() >= threadInfo.getEnds() ? ThreadInfo.STATE_COMPLETED : ThreadInfo.STATE_DOWNLOADING);
            Log.i(TAG, "线程【" + threadInfo.getThreadId() + "】下载完成=" + threadInfo.toString());
            mDBLeader.updateOrReplaceThread(threadInfo);
            List<ThreadInfo> infos = mDBLeader.getThreadByUrl(fileInfo.getUrl());
            boolean completed = false;
            for (ThreadInfo info : infos) {
                if (info.getState() == ThreadInfo.STATE_COMPLETED) {
                    completed = true;
                } else {
                    completed = false;
                }
            }
            if (completed) {
                //同步下载状态
                fileInfo.setState(FileInfo.STATE_COMPLETED);
                fileInfo.setResult("");
                mDBLeader.updateOrReplaceFile(fileInfo);
                File configFile = new File(tempPath);
                if (configFile.exists()) {
                    configFile.delete();
                }
                //广播通知下载完成
                if (reference.get() != null)
                    Broadcaster.getInstance(reference.get()).setAction(Downloader.ACTION_COMPLETED).setFileInfo(fileInfo).send();

                //下载完成
                isDownloading = false;
                System.gc();
            }
        } catch (Exception e) {
            e.printStackTrace();
            isDownloading = false;
            //广播通知下载完成
            threadInfo.setState(ThreadInfo.STATE_WAIT);
            fileInfo.setState(FileInfo.STATE_EXCEPTION);
            fileInfo.setResult(e.toString());
            mDBLeader.updateOrReplaceFile(fileInfo);
            mDBLeader.updateOrReplaceThread(threadInfo);
            if (reference.get() != null)
                Broadcaster.getInstance(reference.get()).setAction(Downloader.ACTION_FAILURE).setFileInfo(fileInfo).send();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpConn != null) {
                httpConn.disconnect();
                httpConn = null;
            }
        }
        /*****************************************************************************************/
    }
}
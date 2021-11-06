package com.xcion.webmage.download;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.xcion.webmage.BuildConfig;
import com.xcion.webmage.download.broadcast.Broadcaster;
import com.xcion.webmage.download.db.DBLeader;
import com.xcion.webmage.download.db.dao.TaskDaoImpl;
import com.xcion.webmage.download.db.dao.ThreadDaoImpl;
import com.xcion.webmage.download.entity.FileInfo;
import com.xcion.webmage.download.entity.ThreadInfo;
import com.xcion.webmage.utils.FileTool;
import com.xcion.webmage.variate.DownloadOptions;


import java.io.File;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Properties;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/26 18:15
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/26 18:15
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class DownloadTask implements Controller {

    private static final String TAG = "downloader";
    private WeakReference<Context> reference;
    private DownloadOptions mDownloadOptions;
    private FileInfo mFileInfo;
    private DBLeader mDBLeader;

    public DownloadTask(Context context, FileInfo fileInfo) {
        this.reference = new WeakReference<>(context);
        this.mFileInfo = fileInfo;
        mDBLeader = new DBLeader(new TaskDaoImpl(reference.get()), new ThreadDaoImpl(reference.get()));
        FileTool.mkdir(Downloader.getDownloadOptions().getStoragePath());
    }

    @Override
    public void startDownload() {

    }

    @Override
    public void pauseDownload() {

    }

    @Override
    public void cancelDownload() {

    }

    @Override
    public boolean isDownloading() {
        return false;
    }

    public void download() {

        /**************************************** 重复下载 *****************************************/
        //逻辑1.正在下载或者等在下载时，屏蔽掉重复的下载
        FileInfo dbFile = mDBLeader.getTaskByUrl(mFileInfo.getUrl());
        if (dbFile != null && (dbFile.getState() == FileInfo.STATE_DOWNLOADING || dbFile.getState() == FileInfo.STATE_WAITING)) {
            mFileInfo.setState(FileInfo.STATE_REPEATED);
            mFileInfo.setResult("The file is already downloading now");
            Broadcaster.getInstance(reference.get()).setAction(Downloader.ACTION_FAILURE).setFileInfo(mFileInfo).send();
            return;
        }

        /************************************* 解析文件信息失败 ************************************/
        //判断文件信息是否完整，如果完成则开始分割文件，分配线程；反之则广播通知下载失败
        if (mFileInfo.getState() != FileInfo.STATE_OK) {
            //获取文件信息异常，下载失败
            mFileInfo.setState(FileInfo.STATE_HTTP_ERROR);
            mFileInfo.setResult("could not get file info");
            mDBLeader.updateOrReplaceFile(mFileInfo);
            Broadcaster.getInstance(reference.get()).setAction(Downloader.ACTION_FAILURE).setFileInfo(mFileInfo).send();
            return;
        }

        /*************************************** 广播通知状态 **************************************/
        boolean newTask;
        //判断是否存在临时记录文件，如不存在则重新生成；
        File tempFile = new File(Downloader.getDownloadOptions().getStoragePath() + "/temp/" + mFileInfo.getFileName() + ".properties");
        if (!tempFile.exists()) {
            newTask = true;
            FileTool.createFile(tempFile.getPath());
        } else {
            newTask = false;
        }

        /*************************************** 加入下载队列 **************************************/
        //在本地sd卡创建待下载文件
        mFileInfo.setState(FileInfo.STATE_WAITING);
        mFileInfo.setResult("");
        mFileInfo.setFilePath(FileTool.createFile(Downloader.getDownloadOptions().getStoragePath() + mFileInfo.getFileName()).getPath());
        //将文件信息同步到数据库
        mDBLeader.updateOrReplaceFile(mFileInfo);
        Broadcaster.getInstance(reference.get()).setAction(Downloader.ACTION_JOIN_QUEUE).setFileInfo(mFileInfo).send();
        /***************************************************************************************/
        //
        try {
            //创建一个可读可写的随机访问文件
            RandomAccessFile raf = new RandomAccessFile(mFileInfo.getFilePath(), "rwd");
            //设置文件长度
            raf.setLength(mFileInfo.getLength());
            //分配每条线程的下载区间
            Properties pro = FileTool.loadConfig(tempFile);
            int blockSize = (int) (mFileInfo.getLength() / Downloader.getDownloadOptions().getMaxThreadCount());
            SparseArray<Thread> tasks = new SparseArray<>();
            for (int i = 0; i < Downloader.getDownloadOptions().getMaxThreadCount(); i++) {
                //防止分段时丢失精度，
                long startL, endL;
                if (i == Downloader.getDownloadOptions().getMaxThreadCount() - 1) {
                    startL = i * blockSize;
                    endL = mFileInfo.getLength();
                } else {
                    startL = i * blockSize;
                    endL = (i + 1) * blockSize - 1;
                }
                //ThreadInfo同步到数据库
                ThreadInfo threadInfo = new ThreadInfo(i, mFileInfo.getUrl(), startL, endL, 0, ThreadInfo.STATE_WAIT);
                mDBLeader.updateOrReplaceThread(threadInfo);

                Object state = pro.getProperty(mFileInfo.getFileName() + "_state_" + i);
                Log.e(TAG, "state>>>" + state);
                if (state != null && Integer.parseInt(state + "") == 1) {
                    //该线程已经完成
                    continue;
                }

                //分配下载位置
                Object record = pro.getProperty(mFileInfo.getFileName() + "_record_" + i);
                Log.e(TAG, "record>>>" + record);
                if (!newTask && record != null && Long.parseLong(record + "") > 0) {
                    //如果有记录，则恢复下载
                }

                DownloadThread task = new DownloadThread(reference.get(), mFileInfo, threadInfo);
                task.setDBLeader(mDBLeader);
                tasks.put(i, new Thread(task));
            }
            //遍历文件下载线程
            if (BuildConfig.DEBUG) {
                List<ThreadInfo> list = mDBLeader.getThreadByUrl(mFileInfo.getUrl());
                for (ThreadInfo info : list) {
                    Log.i(TAG, "文件下载线程遍历>>>" + info.toString());
                }
            }
            /************************************* 启动下载线程 ************************************/
            //广播通知开始下载
            mFileInfo.setState(FileInfo.STATE_STARTED);
            mFileInfo.setResult("");
            mDBLeader.updateOrReplaceFile(mFileInfo);
            Broadcaster.getInstance(reference.get()).setAction(Downloader.ACTION_STARTED).setFileInfo(mFileInfo).send();
            //启动线程下载
            for (int i = 0, count = tasks.size(); i < count; i++) {
                Thread task = tasks.get(i);
                if (task != null) {
                    task.start();
                }
            }
            /********************************************************************************/
        } catch (Exception e) {
            e.printStackTrace();
            mFileInfo.setState(FileInfo.STATE_EXCEPTION);
            mFileInfo.setResult(e.getMessage());
            mDBLeader.updateOrReplaceFile(mFileInfo);
            Broadcaster.getInstance(reference.get()).setAction(Downloader.ACTION_FAILURE).setFileInfo(mFileInfo).send();
        }
    }

}

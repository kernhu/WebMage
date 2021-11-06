package com.xcion.webmage.download;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.xcion.webmage.download.entity.FileInfo;
import com.xcion.webmage.download.entity.ThreadInfo;
import com.xcion.webmage.download.listener.DownloadClient;
import com.xcion.webmage.utils.PermissionsUtils;
import com.xcion.webmage.variate.DownloadOptions;


import java.util.ArrayList;
import java.util.LinkedList;


/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/10/10 12:54
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/10/10 12:54
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class Downloader {

    public static final String ACTION_JOIN_QUEUE = "action.join.queue";
    public static final String ACTION_STARTED = "action.start";
    public static final String ACTION_DOWNLOADING = "action.downloading";
    public static final String ACTION_STOPPED = "action.stopped";
    public static final String ACTION_CANCELLED = "action.cancelled";
    public static final String ACTION_COMPLETED = "action.completed";
    public static final String ACTION_FAILURE = "action.failure";

    private static final int REQUEST_CODE = 3000;
    private String[] permissions = new String[]
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };

    private DownloadService.DownloadBinder mDownloadBinder;
    private static DownloadOptions mDownloadOptions;

    private Context context;
    private PermissionsUtils permissionsUtils;
    private LinkedList<FileInfo> fileInfos;
    private DownloadClient downloadClient;

    public Downloader(Context context) {
        this.context = context;
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("context must instance of activity");
        }

        //权限管理
        permissionsUtils = new PermissionsUtils(context);

        //启动下载广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Downloader.ACTION_JOIN_QUEUE);
        intentFilter.addAction(Downloader.ACTION_STARTED);
        intentFilter.addAction(Downloader.ACTION_DOWNLOADING);
        intentFilter.addAction(Downloader.ACTION_STOPPED);
        intentFilter.addAction(Downloader.ACTION_CANCELLED);
        intentFilter.addAction(Downloader.ACTION_COMPLETED);
        intentFilter.addAction(Downloader.ACTION_FAILURE);
        context.registerReceiver(mBroadcastReceiver, intentFilter);

        //启动下载服务
        Intent mIntent = new Intent(context, DownloadService.class);
        context.bindService(mIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void setOptions(DownloadOptions options) {
        this.mDownloadOptions = options;
    }

    public static DownloadOptions getDownloadOptions() {
        return mDownloadOptions;
    }

    public Downloader setTask(FileInfo task) {
        LinkedList list = new LinkedList<FileInfo>();
        list.add(task);
        this.setTaskList(list);
        return this;
    }

    public Downloader setTaskList(LinkedList<FileInfo> tasks) {
        this.fileInfos = tasks;
        return this;
    }

    public Downloader setDownloadClient(DownloadClient downloadClient) {
        this.downloadClient = downloadClient;
        return this;
    }

    public void bind() {
        permissionsUtils.requestPermissions(permissions, REQUEST_CODE, new PermissionsUtils.Callback() {
            @Override
            public void onPermission(int requestCode, String[] deniedPermissions) {
                if (deniedPermissions.length == 0) {
                    setJoinQueue();
                } else {
                    //无授权
                    setJoinQueue();
                }
            }
        });
    }

    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     *
     */
    private void setJoinQueue() {
        Intent intent = new Intent();
        intent.putExtra(DownloadService.KEY_TASK, fileInfos);
        if (mDownloadBinder != null) {
            mDownloadBinder.joinQueue(intent);
        }
    }

    /**
     *
     */
    public void unBind() {
        context.unbindService(mServiceConnection);
        context.unregisterReceiver(mBroadcastReceiver);
        permissionsUtils = null;
        context = null;
    }
    /***********************************************************************************************/
    /***********************************************************************************************/
    /***********************************************************************************************/
    /***
     * 下载服务连接
     * */
    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mDownloadBinder = (DownloadService.DownloadBinder) binder;
            DownloadService service = mDownloadBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /***********************************************************************************************/
    /***********************************************************************************************/
    /***********************************************************************************************/
    /**
     * 监听下载状态的广播
     **/
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //
            FileInfo fileInfo = intent.getParcelableExtra(FileInfo.class.getName());
            ArrayList<ThreadInfo> threadInfo = intent.getParcelableArrayListExtra(ThreadInfo.class.getName());
            switch (intent.getAction()) {
                case Downloader.ACTION_JOIN_QUEUE:

                    if (downloadClient != null) {
                        downloadClient.onJoinQueue(fileInfo);
                    }

                    break;
                case Downloader.ACTION_STARTED:

                    if (downloadClient != null) {
                        downloadClient.onStarted(fileInfo);
                    }

                    break;
                case Downloader.ACTION_DOWNLOADING:

                    if (downloadClient != null) {
                        downloadClient.onDownloading(fileInfo, threadInfo);
                    }

                    break;
                case Downloader.ACTION_STOPPED:

                    if (downloadClient != null) {
                        downloadClient.onStopped(fileInfo);
                    }

                    break;
                case Downloader.ACTION_CANCELLED:

                    if (downloadClient != null) {
                        downloadClient.onCancelled(fileInfo);
                    }

                    break;
                case Downloader.ACTION_COMPLETED:

                    if (downloadClient != null) {
                        downloadClient.onCompleted(fileInfo);
                    }

                    break;
                case Downloader.ACTION_FAILURE:

                    if (downloadClient != null) {
                        downloadClient.onFailure(fileInfo);
                    }

                    break;
            }
        }
    };
}

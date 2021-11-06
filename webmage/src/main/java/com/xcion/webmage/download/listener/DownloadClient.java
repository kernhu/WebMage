package com.xcion.webmage.download.listener;

import com.xcion.webmage.download.entity.FileInfo;
import com.xcion.webmage.download.entity.ThreadInfo;

import java.util.ArrayList;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/28 13:54
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/28 13:54
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public abstract class DownloadClient {

    public abstract void onJoinQueue(FileInfo fileInfo);

    public abstract void onStarted(FileInfo fileInfo);

    public abstract void onDownloading(FileInfo fileInfo, ArrayList<ThreadInfo> threadInfo);

    public abstract void onStopped(FileInfo fileInfo);

    public abstract void onCancelled(FileInfo fileInfo);

    public abstract void onCompleted(FileInfo fileInfo);

    public abstract void onFailure(FileInfo fileInfo);
}

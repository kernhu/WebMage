package com.xcion.webmage.download.broadcast;

import android.content.Context;
import android.content.Intent;


import com.xcion.webmage.download.entity.FileInfo;
import com.xcion.webmage.download.entity.ThreadInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * author: Kern Hu
 * email: sky580@126.com
 * data_time: 11/29/20 5:30 PM
 * describe: This is...  下载回调广播发送类
 */

public class Broadcaster {

    private WeakReference<Context> mReference;
    private String action;
    private FileInfo fileInfo;
    private ArrayList<ThreadInfo> threadInfo;

    public Broadcaster(Context context) {
        mReference = new WeakReference<Context>(context);
    }

    public static Broadcaster getInstance(Context context) {
        return new Broadcaster(context);
    }

    public Broadcaster setAction(String action) {
        this.action = action;
        return this;
    }

    public Broadcaster setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
        return this;
    }

    public Broadcaster setThreadInfo(ArrayList<ThreadInfo> threadInfo) {
        this.threadInfo = threadInfo;
        return this;
    }

    public void send() {
        Intent intent = new Intent(action);
        if (fileInfo != null) {
            intent.putExtra(FileInfo.class.getName(), fileInfo);
        }
        if (threadInfo != null && !threadInfo.isEmpty()) {
            intent.putExtra(ThreadInfo.class.getName(), threadInfo);
        }
        if (mReference.get() != null) {
            mReference.get().sendBroadcast(intent);
        }
    }
}

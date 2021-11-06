package com.xcion.webmage.download.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/26 19:25
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/26 19:25
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class ThreadInfo implements Parcelable {

    public static final int STATE_WAIT = 0;
    public static final int STATE_PAUSE = 1;
    public static final int STATE_CANCEL = 2;
    public static final int STATE_DOWNLOADING = 3;
    public static final int STATE_COMPLETED = 4;

    private int threadId;
    private String url;
    private long start = 0;
    private long ends = 0;
    private long current = 0;
    private int state = 0;

    public ThreadInfo() {
    }

    public ThreadInfo(int threadId, String url, long start, long ends, long current, int state) {
        this.threadId = threadId;
        this.url = url;
        this.start = start;
        this.ends = ends;
        this.current = current;
        this.state = state;
    }

    protected ThreadInfo(Parcel in) {
        threadId = in.readInt();
        url = in.readString();
        start = in.readLong();
        ends = in.readLong();
        current = in.readLong();
        state = in.readInt();
    }

    public static final Creator<ThreadInfo> CREATOR = new Creator<ThreadInfo>() {
        @Override
        public ThreadInfo createFromParcel(Parcel in) {
            return new ThreadInfo(in);
        }

        @Override
        public ThreadInfo[] newArray(int size) {
            return new ThreadInfo[size];
        }
    };

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnds() {
        return ends;
    }

    public void setEnds(long ends) {
        this.ends = ends;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "threadId=" + threadId +
                ", url='" + url + '\'' +
                ", start=" + start +
                ", ends=" + ends +
                ", current=" + current +
                ", state=" + state +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(threadId);
        parcel.writeString(url);
        parcel.writeLong(start);
        parcel.writeLong(ends);
        parcel.writeLong(current);
        parcel.writeInt(state);
    }
}

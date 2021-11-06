package com.xcion.webmage.download.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/26 14:17
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/26 14:17
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class FileInfo implements Parcelable {

    public static final int STATE_OK = 1;           //解析完成加入队列；
    public static final int STATE_WAITING = 2;         //等待下载
    public static final int STATE_STARTED = 3;        //开始下载
    public static final int STATE_DOWNLOADING = 4;  //正在下载
    public static final int STATE_CANCELED = 5;       //取消下载
    public static final int STATE_PAUSED = 6;      //暂停下载
    public static final int STATE_COMPLETED = 7;    //完成下载
    public static final int STATE_REPEATED = 101;    //重复下载
    public static final int STATE_HTTP_ERROR = 102; //HTTP请求异常，一般跟网络或者URL有关系
    public static final int STATE_EXCEPTION = 103;  //SDK抛异常

    private String url = "";
    private String fileName = "";
    private String mimeType = "";
    private String filePath = "";
    private long length = 0;
    private int state = 0;
    private String result = "";

    public FileInfo() {
    }

    public FileInfo(String url) {
        this.url = url;
    }

    public FileInfo(String url, String result) {
        this.url = url;
        this.result = result;
    }

    public FileInfo(String url, String fileName, String mimeType, String filePath, long length, int state) {
        this.url = url;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.filePath = filePath;
        this.length = length;
        this.state = state;
    }

    public FileInfo(String url, int state, String result) {
        this.url = url;
        this.state = state;
        this.result = result;
    }

    protected FileInfo(Parcel in) {
        url = in.readString();
        fileName = in.readString();
        mimeType = in.readString();
        filePath = in.readString();
        length = in.readLong();
        state = in.readInt();
        result = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(fileName);
        dest.writeString(mimeType);
        dest.writeString(filePath);
        dest.writeLong(length);
        dest.writeInt(state);
        dest.writeString(result);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel in) {
            return new FileInfo(in);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", filePath='" + filePath + '\'' +
                ", length=" + length +
                ", state=" + state +
                ", result='" + result + '\'' +
                '}';
    }
}

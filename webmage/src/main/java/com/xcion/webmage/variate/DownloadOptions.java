package com.xcion.webmage.variate;

import android.os.Environment;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/12/24 15:19
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/12/24 15:19
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class DownloadOptions {

    public static final String DEFAULT_ACCEPT_PROPERTY = "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*";
    public static final String DEFAULT_USER_AGENT_PROPERTY = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";

    private boolean downloadEnable = true;
    private int connectTimeout = 1000 * 15;
    private int readTimeout = 1000 * 15;
    private int maxConcurrencyCount = 3;
    private int maxThreadCount = 8;
    private boolean showFileInfo = true;
    private String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.xcion.downloader/";
    private String userAgentProperty = DEFAULT_USER_AGENT_PROPERTY;
    private String acceptProperty = DEFAULT_ACCEPT_PROPERTY;

    public DownloadOptions() {
    }

    public boolean isDownloadEnable() {
        return downloadEnable;
    }

    public DownloadOptions setDownloadEnable(boolean downloadEnable) {
        this.downloadEnable = downloadEnable;
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public DownloadOptions setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public DownloadOptions setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public int getMaxConcurrencyCount() {
        return maxConcurrencyCount;
    }

    public DownloadOptions setMaxConcurrencyCount(int maxConcurrencyCount) {
        this.maxConcurrencyCount = maxConcurrencyCount;
        return this;
    }

    public int getMaxThreadCount() {
        return maxThreadCount;
    }

    public DownloadOptions setMaxThreadCount(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
        return this;
    }

    public boolean isShowFileInfo() {
        return showFileInfo;
    }

    public DownloadOptions setShowFileInfo(boolean showFileInfo) {
        this.showFileInfo = showFileInfo;
        return this;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public DownloadOptions setStoragePath(String storagePath) {
        this.storagePath = storagePath;
        return this;
    }

    public String getUserAgentProperty() {
        return userAgentProperty;
    }

    public DownloadOptions setUserAgentProperty(String userAgentProperty) {
        this.userAgentProperty = userAgentProperty;
        return this;
    }

    public String getAcceptProperty() {
        return acceptProperty;
    }

    public DownloadOptions setAcceptProperty(String acceptProperty) {
        this.acceptProperty = acceptProperty;
        return this;
    }

    public DownloadOptions build() {

        return this;
    }
}

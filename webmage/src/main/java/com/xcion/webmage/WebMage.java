package com.xcion.webmage;

import android.util.Log;


import com.xcion.webmage.capture.CaptureStrategy;
import com.xcion.webmage.variate.DownloadOptions;
import com.xcion.webmage.variate.FolderOptions;
import com.xcion.webmage.variate.IndicatorOptions;
import com.xcion.webmage.variate.VideoOptions;
import com.xcion.webmage.variate.WebOptions;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/18 10:00
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/18 10:00
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class WebMage {

    public static final String TAG = "WebMage";

    private long beginInitTime = System.currentTimeMillis();
    private static WebOptions webOptions;
    private static VideoOptions videoOptions;
    private static DownloadOptions downloadOptions;
    private static FolderOptions folderOptions;
    private static IndicatorOptions indicatorOptions;
    private static boolean preInitCore;
    private static String preLoadUrl;
    private static CaptureStrategy captureStrategy = CaptureStrategy.NEVER;

    public static WebOptions getWebOptions() {
        return webOptions;
    }

    public static DownloadOptions getDownloadOptions() {
        return downloadOptions;
    }

    public static FolderOptions getFolderOptions() {
        return folderOptions;
    }

    public static IndicatorOptions getIndicatorOptions() {
        return indicatorOptions;
    }

    public static VideoOptions getVideoOptions() {
        return videoOptions;
    }

    public static boolean isPreInitCore() {
        return preInitCore;
    }

    public static String getPreLoadUrl() {
        return preLoadUrl;
    }

    public static CaptureStrategy getCaptureStrategy() {
        return captureStrategy;
    }

    public WebMage(Builder builder) {
        WebMage.webOptions = builder.getWebOptions() != null ? builder.getWebOptions() : new WebOptions().build();
        WebMage.videoOptions = builder.getVideoOptions() != null ? builder.getVideoOptions() : new VideoOptions().build();
        WebMage.downloadOptions = builder.getDownloadOptions() != null ? builder.getDownloadOptions() : new DownloadOptions().build();
        WebMage.folderOptions = builder.getFolderOptions() != null ? builder.getFolderOptions() : new FolderOptions().build();
        WebMage.indicatorOptions = builder.getIndicatorOptions() != null ? builder.getIndicatorOptions() : new IndicatorOptions().build();
        WebMage.preInitCore = builder.isPreInitCore();
        WebMage.preLoadUrl = builder.getPreLoadUrl();
        WebMage.captureStrategy = builder.getCaptureStrategy();
        Log.i(TAG, "WebMage initialization is complete with " + (System.currentTimeMillis() - beginInitTime) + " milliseconds");
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {

        private WebOptions webOptions;
        private VideoOptions videoOptions;
        private DownloadOptions downloadOptions;
        private FolderOptions folderOptions;
        private IndicatorOptions indicatorOptions;
        private boolean preInitCore = true;
        private String preLoadUrl;
        private CaptureStrategy captureStrategy;

        public WebOptions getWebOptions() {
            return webOptions;
        }

        public Builder setWebOptions(WebOptions webOptions) {
            this.webOptions = webOptions;
            return this;
        }

        public VideoOptions getVideoOptions() {
            return videoOptions;
        }

        public Builder setVideoOptions(VideoOptions videoOptions) {
            this.videoOptions = videoOptions;
            return this;
        }

        public DownloadOptions getDownloadOptions() {
            return downloadOptions;
        }

        public Builder setDownloadOptions(DownloadOptions downloadOptions) {
            this.downloadOptions = downloadOptions;
            return this;
        }

        public FolderOptions getFolderOptions() {
            return folderOptions;
        }

        public Builder setFolderOptions(FolderOptions folderOptions) {
            this.folderOptions = folderOptions;
            return this;
        }

        public IndicatorOptions getIndicatorOptions() {
            return indicatorOptions;
        }

        public Builder setIndicatorOptions(IndicatorOptions indicatorOptions) {
            this.indicatorOptions = indicatorOptions;
            return this;
        }

        public boolean isPreInitCore() {
            return preInitCore;
        }

        public Builder setPreInitCore(boolean preInitCore) {
            this.preInitCore = preInitCore;
            return this;
        }

        public String getPreLoadUrl() {
            return preLoadUrl;
        }

        public Builder setPreLoadUrl(String preLoadUrl) {
            this.preLoadUrl = preLoadUrl;
            return this;
        }

        public CaptureStrategy getCaptureStrategy() {
            return captureStrategy;
        }

        public Builder setCaptureStrategy(CaptureStrategy captureStrategy) {
            this.captureStrategy = captureStrategy;
            return this;
        }

        public WebMage build() {
            return new WebMage(this);
        }

    }
}

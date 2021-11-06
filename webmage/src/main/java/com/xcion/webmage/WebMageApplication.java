package com.xcion.webmage;

import android.app.Application;
import android.graphics.Color;

import com.xcion.webmage.variate.DownloadOptions;
import com.xcion.webmage.variate.FolderOptions;
import com.xcion.webmage.variate.IndicatorOptions;
import com.xcion.webmage.variate.VideoOptions;
import com.xcion.webmage.variate.WebOptions;
import com.xcion.webmage.video.VideoPlayer;

public class WebMageApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        WebMage.with()
                .setWebOptions(new WebOptions()
                        .setTextZoom(100)
                        .setCanZoom(true)
                        .setShieldAds(true)
                        .setBlockImage(false)
                        .setSavePassword(true)
                        .setDampingEnable(true)
                        .setVibratorEnable(true)
                        .setDeepLinkEnable(true)
                        .setDeepLinkTooltip(true)
                        .setCmenuEnable(true)
                        .setHardwareAccelerated(true)
                        .setStoragePath("/WebMage/Cache/")
                        .setGeolocation(Geolocation.ASK_ME)
                        .setDebuggingEnabled(BuildConfig.DEBUG)
                        .setSpeedStrategy(SpeedStrategy.BLOCK_THEN_LOAD)
                        .setSponsorLogo(getResources().getDrawable(R.drawable.ic_webmage_logo, null))
                        .setUserAgent("<package-name:cn.xcion.wmdemo>", "<version-name:v1.0.0>", "<channel-num:official>", "<build-type:release>", "<~hello world~>")
                        .build()
                )
                .setVideoOptions(new VideoOptions()
                        .setHardwareAccelerated(true)
                        .setVideoPlayerRotation(VideoPlayer.Rotation.AUTO_BY_SENSOR)
                        .setVideoPlayerBackground(Color.parseColor("#FF2C2C2C"))
                        .build()
                )
                .setDownloadOptions(new DownloadOptions()
                        .setDownloadEnable(true)
                        .setMaxThreadCount(5)
                        .setShowFileInfo(true)
                        .setReadTimeout(1000 * 15)
                        .setMaxConcurrencyCount(4)
                        .setConnectTimeout(1000 * 15)
                        .setStoragePath("/com.xcion.webgame/")
                        .setAcceptProperty(DownloadOptions.DEFAULT_ACCEPT_PROPERTY)
                        .setUserAgentProperty(DownloadOptions.DEFAULT_USER_AGENT_PROPERTY)
                        .build()
                )
                .setFolderOptions(new FolderOptions()
                        .build()
                )
                .setIndicatorOptions(new IndicatorOptions()
                        .setHeight(2)
                        .setRadius(15)
                        .setMaxProgress(100)
                        .setAnimatorDuration(500)
                        .setProgressColor(Color.parseColor("#FFBB86FC"))
                        .setBackgroundColor(Color.parseColor("#FFF5F5F5"))
                        .build()
                )
                .setPreInitCore(true)
                .setPreLoadUrl("www.baidu.com")
                .build();

    }
}

package com.xcion.webmage.variate;

import android.graphics.Color;

import com.xcion.webmage.video.VideoPlayer;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/12/28 16:30
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/12/28 16:30
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class VideoOptions {

    private boolean hardwareAccelerated = false;
    private int videoPlayerBackground = Color.parseColor("#FF2C2C2C");
    private VideoPlayer.Rotation videoPlayerRotation = VideoPlayer.Rotation.AUTO_BY_SENSOR;

    public VideoOptions() {
    }

    public boolean isHardwareAccelerated() {
        return hardwareAccelerated;
    }

    public VideoOptions setHardwareAccelerated(boolean hardwareAccelerated) {
        this.hardwareAccelerated = hardwareAccelerated;
        return this;
    }

    public int getVideoPlayerBackground() {
        return videoPlayerBackground;
    }

    public VideoOptions setVideoPlayerBackground(int videoPlayerBackground) {
        this.videoPlayerBackground = videoPlayerBackground;
        return this;
    }

    public VideoPlayer.Rotation getVideoPlayerRotation() {
        return videoPlayerRotation;
    }

    public VideoOptions setVideoPlayerRotation(VideoPlayer.Rotation videoPlayerRotation) {
        this.videoPlayerRotation = videoPlayerRotation;
        return this;
    }

    public VideoOptions build() {

        return this;
    }
}

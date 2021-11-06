package com.xcion.webmage.variate;

import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;

import com.xcion.webmage.Geolocation;
import com.xcion.webmage.SpeedStrategy;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/18 17:01
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/18 17:01
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class WebOptions {

    private boolean debuggingEnabled = false;
    private boolean savePassword = true;
    private boolean canZoom = false;
    private int textZoom = 100;
    private boolean shieldAds = false;
    private boolean blockImage = false;
    private Geolocation geolocation = Geolocation.ASK_ME;
    private boolean dampingEnable = true;
    private boolean vibratorEnable = true;
    private SpeedStrategy speedStrategy = SpeedStrategy.BLOCK_THEN_LOAD;
    private String[] userAgent;
    private String storagePath = "/WebMage/Cache/";
    private Drawable sponsorLogo;
    private boolean hardwareAccelerated = true;
    private boolean deepLinkEnable = true;
    private boolean deepLinkTooltip = false;
     private boolean cmenuEnable = true;

    private Map<String, String> interceptor = new HashMap<>();

    public WebOptions() {
    }

    public boolean isDebuggingEnabled() {
        return debuggingEnabled;
    }

    public WebOptions setDebuggingEnabled(boolean debuggingEnabled) {
        this.debuggingEnabled = debuggingEnabled;
        return this;
    }

    public boolean isSavePassword() {
        return savePassword;
    }

    public WebOptions setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
        return this;
    }

    public boolean isCanZoom() {
        return canZoom;
    }

    public WebOptions setCanZoom(boolean canZoom) {
        this.canZoom = canZoom;
        return this;
    }

    public int getTextZoom() {
        return textZoom;
    }

    public WebOptions setTextZoom(int textZoom) {
        this.textZoom = textZoom;
        return this;
    }

    public boolean isShieldAds() {
        return shieldAds;
    }

    public WebOptions setShieldAds(boolean shieldAds) {
        this.shieldAds = shieldAds;
        return this;
    }

    public boolean isBlockImage() {
        return blockImage;
    }

    public WebOptions setBlockImage(boolean blockImage) {
        this.blockImage = blockImage;
        return this;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public WebOptions setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
        return this;
    }

    public boolean isDampingEnable() {
        return dampingEnable;
    }

    public WebOptions setDampingEnable(boolean dampingEnable) {
        this.dampingEnable = dampingEnable;
        return this;
    }

    public boolean isVibratorEnable() {
        return vibratorEnable;
    }

    public WebOptions setVibratorEnable(boolean vibratorEnable) {
        this.vibratorEnable = vibratorEnable;
        return this;
    }

    public SpeedStrategy getSpeedStrategy() {
        return speedStrategy;
    }

    public WebOptions setSpeedStrategy(SpeedStrategy speedStrategy) {
        this.speedStrategy = speedStrategy;
        return this;
    }

    public String[] getUserAgent() {
        return userAgent;
    }

    public WebOptions setUserAgent(String... userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public WebOptions setStoragePath(String storagePath) {
        this.storagePath = storagePath;
        return this;
    }

    public Drawable getSponsorLogo() {
        return sponsorLogo;
    }

    public WebOptions setSponsorLogo(Drawable sponsorLogo) {
        this.sponsorLogo = sponsorLogo;
        return this;
    }

    public boolean isHardwareAccelerated() {
        return hardwareAccelerated;
    }

    public WebOptions setHardwareAccelerated(boolean hardwareAccelerated) {
        this.hardwareAccelerated = hardwareAccelerated;
        return this;
    }

    public boolean isDeepLinkTooltip() {
        return deepLinkTooltip;
    }

    public WebOptions setDeepLinkTooltip(boolean deepLinkTooltip) {
        this.deepLinkTooltip = deepLinkTooltip;
        return this;
    }

    public boolean isDeepLinkEnable() {
        return deepLinkEnable;
    }

    public WebOptions setDeepLinkEnable(boolean deepLinkEnable) {
        this.deepLinkEnable = deepLinkEnable;
        return this;
    }

    public boolean isCmenuEnable() {
        return cmenuEnable;
    }

    public WebOptions setCmenuEnable(boolean cmenuEnable) {
        this.cmenuEnable = cmenuEnable;
        return this;
    }

    public Map<String, String> getInterceptor() {
        return interceptor;
    }

    public WebOptions setInterceptor(String originalUrl, String replacedUrl) {
        this.interceptor.put(originalUrl, replacedUrl);
        return this;
    }



    public WebOptions build() {

        return this;
    }
}

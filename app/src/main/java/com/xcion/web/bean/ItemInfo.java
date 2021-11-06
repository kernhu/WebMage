package com.xcion.web.bean;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/10 14:55
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/10 14:55
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class ItemInfo {

    public static final String FUN_LOAD_H5 = "fun_load_h5";
    public static final String FUN_VIDEO_PLAY = "fun_video_play";
    public static final String FUN_DEEP_LINK = "fun_deep_link";
    public static final String FUN_LOCATION = "fun_location";
    public static final String FUN_DOWNLOAD = "fun_download";
    public static final String FUN_UPLOAD = "fun_upload";
    public static final String FUN_JS_NATIVE = "fun_js_native";
    public static final String FUN_NATIVE_JS = "fun_native_js";
    public static final String FUN_RESOURCE_REPLACE = "fun_resource_replace";
    public static final String FUN_URL_INTERCEPT = "fun_url_intercept";
    public static final String FUN_SNAPSHOOT = "fun_snapshoot";
    public static final String FUN_HYBRID = "fun_HYBRID";
    public static final String FUN_ABOUT_US = "fun_about_us";

    private String title;
    private String desc;
    private String background;
    private String funMold;
    private String url;

    public ItemInfo(String title, String desc, String background, String funMold, String url) {
        this.title = title;
        this.desc = desc;
        this.background = background;
        this.funMold = funMold;
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFunMold() {
        return funMold;
    }

    public void setFunMold(String funMold) {
        this.funMold = funMold;
    }

    @Override
    public String toString() {
        return "ItemInfo{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", background='" + background + '\'' +
                ", url='" + url + '\'' +
                ", funMold='" + funMold + '\'' +
                '}';
    }
}

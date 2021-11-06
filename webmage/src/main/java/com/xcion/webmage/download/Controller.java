package com.xcion.webmage.download;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/28 10:47
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/28 10:47
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public interface Controller {

    void startDownload();

    void pauseDownload();

    void cancelDownload();

    boolean isDownloading();
}

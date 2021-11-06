package com.xcion.webmage.download.db.dao;

import com.xcion.webmage.download.entity.ThreadInfo;

import java.util.List;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/26 19:29
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/26 19:29
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
interface ThreadDao {

    //插入下载信息
    void insertThread(ThreadInfo fileInfo);

    //删除下载
    void deleteThread(String url);

    //更新下载信息
    void updateThread(String url, int threadId, ThreadInfo info);

    //根据url查询下载的信息
    List<ThreadInfo> getThreadByUrl(String url);

    //所有的下载
    List<ThreadInfo> getAllThreads();

    //系统被杀死的时候没有完成的下载
    List<ThreadInfo> getAllThreadsBy(int state);

    //是否存在
    boolean isExists(String url);


    //是否存在
    boolean isExists(String url, int threadId);
}

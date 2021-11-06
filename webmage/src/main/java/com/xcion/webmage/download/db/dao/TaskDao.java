package com.xcion.webmage.download.db.dao;

import com.xcion.webmage.download.entity.FileInfo;

import java.util.List;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/26 16:44
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/26 16:44
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
interface TaskDao {

    //插入下载信息
    void insertTask(FileInfo fileInfo);

    //删除下载
    void deleteTask(String url);

    //更新下载信息
    void updateTask(String url, FileInfo info);

    //根据url查询下载的信息
    FileInfo getTaskByUrl(String url);

    //所有的下载
    List<FileInfo> getAllTasks();

    //系统被杀死的时候没有完成的下载
    List<FileInfo> getAllTasksBy(int state);

    //是否存在
    boolean isExists(String url);

}

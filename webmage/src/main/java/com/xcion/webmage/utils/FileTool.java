package com.xcion.webmage.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/26 15:34
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/26 15:34
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class FileTool {

    private static final String TAG = "FileTool";

    /**
     * @param path
     * @return
     */
    public static boolean mkdir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }


    /**
     * 读取下载配置文件
     */
    public static Properties loadConfig(File file) {
        Properties properties = new Properties();
        FileInputStream fis = null;
        if (!file.exists()) {
            createFile(file.getPath());
        }
        try {
            fis = new FileInputStream(file);
            properties.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    /**
     * 创建文件 当文件不存在的时候就创建一个文件，否则直接返回文件
     */
    public static File createFile(String path) {
        File file = new File(path);

        if (file.getParentFile() != null) {
            if (!file.getParentFile().exists()) {
                if (!createDir(file.getParent())) {
                }
            }
        }
        // 创建目标文件
        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                }
                return file;
            } else {
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 创建目录 当目录不存在的时候创建文件，否则返回false
     */
    public static boolean createDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return false;
            }
            return true;
        }
        return false;
    }

}

package com.xcion.webmage.picture;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/10/7  21:33
 * Intro:  save all the image
 */
public class ImageQueue {

    private static List<String> imageQueue;

    /**
     * @return image's urls
     */
    public static List<String> getQueue() {
        return imageQueue;
    }

    /**
     * add image url
     *
     * @param target image url
     */
    public static void putQueue(String target) {
        if (imageQueue == null) {
            imageQueue = new ArrayList<>();
        }

        if (!TextUtils.isEmpty(target) && !imageQueue.contains(target)) {
            ImageQueue.imageQueue.add(target);
        }
    }

    /**
     * recycle static image queue
     */
    public static void recycle() {
        if (imageQueue != null) {
            imageQueue.clear();
            imageQueue = null;
        }
    }
}

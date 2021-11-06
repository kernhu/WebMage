package com.xcion.webmage.resource;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/10/8  01:23
 * Intro: 网路请求资源拦截器
 */
public class ResourceInterceptor {

    private static final String TAG = "ResourceInterceptor";

    public WebResourceResponse onIntercept(Context context, Uri uri) {
        WebResourceResponse resourceResponse = null;
        String path = uri.getPath();
        if (!TextUtils.isEmpty(path)) {
            path = path.substring(1);
        }
        Log.i(TAG, "shouldInterceptRequest>>>" + path);
        try {
            InputStream input = context.getAssets().open(path);
            if (input != null) {
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(path.substring(path.lastIndexOf(".") + 1));
                HashMap<String, String> header = new HashMap<>();
                header.put("Access-Control-Allow-Origin", "*");
                header.put("Access-Control-Allow-Headers", "Content-Type");
                resourceResponse = new WebResourceResponse(mimeType, "", 200, "ok", header, input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceResponse;
    }

    public void onDestroy() {
    }
}

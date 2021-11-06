package com.xcion.webmage.client;

import android.content.Intent;
import android.view.KeyEvent;

import androidx.annotation.NonNull;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/10/9  00:52
 * Intro:
 */
public interface IClientProxy {

    void onDestroy();

    void onTrimMemory(int level);

    void onLowMemory();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}

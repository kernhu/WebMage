package com.xcion.webmage.permission;

import androidx.annotation.NonNull;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/10/10  21:19
 * Intro:
 */
public interface PermissionCallback {

    /**
     * 通过授权
     * @param permission
     */
    void permissionGranted(@NonNull String[] permission);

    /**
     * 拒绝授权
     * @param permission
     */
    void permissionDenied(@NonNull String[] permission);

}

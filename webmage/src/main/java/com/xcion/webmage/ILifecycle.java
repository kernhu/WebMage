package com.xcion.webmage;

import android.content.Intent;
import android.view.KeyEvent;

import com.xcion.webmage.hybrid.Hybrider;

import androidx.annotation.NonNull;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/18 14:56
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/18 14:56
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public interface ILifecycle {

    void onPause();

    void onResume();

    void onStop();

    void onDestroy();

    void onTrimMemory(int level);

    void onLowMemory();

    boolean onKeyDown(int keyCode, KeyEvent event);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    boolean onBackPressed();

    Hybrider getHybrider();

}

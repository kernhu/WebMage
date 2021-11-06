package com.xcion.webmage.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/27 13:57
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/27 13:57
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class PermissionsUtils {

    private Context context;
    private List<String> deniedPermissions;
    private int requestCode;
    private Callback callback;

    public PermissionsUtils(Context context) {
        this.context = context;
        this.deniedPermissions = new ArrayList<>();
    }

    /**
     * the permission result
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (this.requestCode == requestCode) {
            deniedPermissions.clear();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    String per = permissions[i];
                    deniedPermissions.add(per);
                }
            }
            if (callback != null) {
                callback.onPermission(requestCode, deniedPermissions.toArray(new String[deniedPermissions.size()]));
            }
        }
    }

    /**
     * request permissions
     *
     * @param requestPermissions
     * @param requestCode
     * @param callback
     */
    public void requestPermissions(String[] requestPermissions, int requestCode, Callback callback) {
        this.requestCode = requestCode;
        this.callback = callback;
        //
        deniedPermissions.clear();
        //
        for (String permission : requestPermissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }

        if (deniedPermissions.isEmpty()) {
            if (callback != null) {
                callback.onPermission(requestCode, new String[]{});
            }
        } else {
            ActivityCompat.requestPermissions((Activity) context, deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
        }
    }

    public interface Callback {

        void onPermission(int requestCode, String[] deniedPermissions);

    }
}

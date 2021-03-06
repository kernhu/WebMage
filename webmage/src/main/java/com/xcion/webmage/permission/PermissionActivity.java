package com.xcion.webmage.permission;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

import com.xcion.webmage.R;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/10/10  21:26
 * Intro:
 */
public class PermissionActivity extends AppCompatActivity {

    public static final String EXTRA_PERMISSION = "permission";
    public static final String EXTRA_SHOW_TIP = "show_tip";
    public static final String EXTRA_KEY = "key";
    public static final String EXTRA_TIP = "tip";

    private static final int PERMISSION_REQUEST_CODE = 64;
    private boolean isRequireCheck;
    private String[] permission;
    private String key;
    private boolean showTip;
    private PermissionsApplicant.TipInfo tipInfo;

    private final String defaultTitle = getString(R.string.permission_helping);
    private final String defaultContent = getString(R.string.permission_warning);
    private final String defaultCancel = getString(R.string.permission_cancel);
    private final String defaultEnsure = getString(R.string.permission_setting);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSION)) {
            finish();
            return;
        }

        isRequireCheck = true;
        permission = getIntent().getStringArrayExtra(EXTRA_PERMISSION);
        key = getIntent().getStringExtra(EXTRA_KEY);
        showTip = getIntent().getBooleanExtra(EXTRA_SHOW_TIP, true);
        Serializable ser = getIntent().getSerializableExtra(EXTRA_TIP);

        if (ser == null) {
            tipInfo = new PermissionsApplicant.TipInfo(defaultTitle, defaultContent, defaultCancel, defaultEnsure);
        } else {
            tipInfo = (PermissionsApplicant.TipInfo) ser;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRequireCheck) {
            if (PermissionsApplicant.hasPermission(this, permission)) {
                permissionsGranted();
            } else {
                requestPermissions(permission);
                isRequireCheck = false;
            }
        } else {
            isRequireCheck = true;
        }
    }


    private void requestPermissions(String[] permission) {
        ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
    }


    /**
     * ??????????????????,
     * ??????????????????, ????????????.
     * ??????????????????, ?????????Dialog.
     *
     * @param requestCode  ?????????
     * @param permissions  ??????
     * @param grantResults ??????
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //???????????????????????????????????????????????????????????????????????????????????????PermissionChecker????????????
        if (requestCode == PERMISSION_REQUEST_CODE && PermissionsApplicant.isGranted(grantResults)
                && PermissionsApplicant.hasPermission(this, permissions)) {
            permissionsGranted();
        } else if (showTip) {
            showMissingPermissionDialog();
        } else { //?????????????????????
            permissionsDenied();
        }
    }


    /**
     * ????????????????????????
     */
    private void showMissingPermissionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);

        builder.setTitle(TextUtils.isEmpty(tipInfo.title) ? defaultTitle : tipInfo.title);
        builder.setMessage(TextUtils.isEmpty(tipInfo.content) ? defaultContent : tipInfo.content);

        builder.setNegativeButton(TextUtils.isEmpty(tipInfo.cancel) ? defaultCancel : tipInfo.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionsDenied();
            }
        });

        builder.setPositiveButton(TextUtils.isEmpty(tipInfo.ensure) ? defaultEnsure : tipInfo.ensure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PermissionsApplicant.gotoSetting(PermissionActivity.this);
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    /**
     *
     */
    private void permissionsDenied() {
        PermissionCallback callback = PermissionsApplicant.fetchListener(key);
        if (callback != null) {
            callback.permissionDenied(permission);
        }
        finish();
    }

    /**
     * ????????????????????????
     */
    private void permissionsGranted() {
        PermissionCallback callback = PermissionsApplicant.fetchListener(key);
        if (callback != null) {
            callback.permissionGranted(permission);
        }
        finish();
    }

    protected void onDestroy() {
        PermissionsApplicant.fetchListener(key);
        super.onDestroy();
    }

}

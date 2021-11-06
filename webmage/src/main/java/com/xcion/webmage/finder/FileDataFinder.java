package com.xcion.webmage.finder;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;


import com.xcion.webmage.R;
import com.xcion.webmage.window.SheetMenuWindow;
import com.xcion.webmage.utils.PermissionsUtils;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/12/4 16:54
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/12/4 16:54
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class FileDataFinder {

    enum AcceptType {

        IMAGE("image/"),
        VIDEO("video/*");

        String name;

        AcceptType(String name) {
            this.name = name;
        }
    }

    public interface Callback {

        void onChoose(Uri[] filePath);

        void onCancel();

    }

    private static final int REQUEST_CODE = 30002;
    private String[] permissions = new String[]
            {
                    Manifest.permission.CAMERA
            };

    private WeakReference<Activity> reference;
    private String[] acceptType;
    private ValueCallback<Uri[]> filePathCallback;
    private String title;
    private int requestCode;
    private Callback callback;
    private PermissionsUtils mPermissionsUtils;
    private String mFileName;

    public FileDataFinder(Activity activity) {
        this.reference = new WeakReference<>(activity);

    }

    public FileDataFinder setAcceptType(String[] acceptType) {
        this.acceptType = acceptType;
        return this;
    }

    public FileDataFinder setFilePathCallback(ValueCallback<Uri[]> filePathCallback) {
        this.filePathCallback = filePathCallback;
        return this;
    }

    public FileDataFinder setTitle(String title) {
        this.title = title;
        return this;
    }

    public FileDataFinder setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public FileDataFinder setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }


    public void build() {
        String accept = acceptType[0];
        if (accept.startsWith(AcceptType.IMAGE.name)) {
            showSheetMenuWindow();
        } else {
            takeFile();
        }
    }


    public void onDestroy() {
        mPermissionsUtils = null;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionsUtils != null) {
            mPermissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /****************************************************************************************************/
    /****************************************************************************************************/
    /****************************************************************************************************/

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                try {
                    String selection = MediaStore.Images.Media.DISPLAY_NAME + "=? ";
                    Cursor cursor = reference.get().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, selection, new String[]{mFileName}, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getLong(0));
                            if (callback != null) {
                                callback.onChoose(new Uri[]{uri});
                            }
                        } while (cursor.moveToNext());
                    } else {
                        if (callback != null) {
                            callback.onCancel();
                        }
                        Toast.makeText(reference.get(), reference.get().getResources().getString(R.string.picture_get_fail), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onCancel();
                    }
                }
            } else {
                if (data.getData() != null) {
                    if (callback != null) {
                        callback.onChoose(new Uri[]{data.getData()});
                    }
                } else {
                    if (callback != null) {
                        callback.onChoose(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                    }
                }
            }
        } else {
            if (callback != null) {
                callback.onCancel();
            }
        }
    }

    /****************************************************************************************************/
    /****************************************************************************************************/
    /****************************************************************************************************/
    private void showSheetMenuWindow() {
        SheetMenuWindow.getInstance(reference.get()).setMenuItem(
                reference.get().getResources().getString(R.string.file_finder_gallery),
                reference.get().getResources().getString(R.string.file_finder_camera),
                reference.get().getResources().getString(R.string.file_finder_cancel))
                .setOnItemClickListener(new SheetMenuWindow.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (position == -1 || position == 2) {
                            if (callback != null) {
                                callback.onCancel();
                            }
                        } else if (position == 0) {
                            takeGallery();
                        } else if (position == 1) {
                            if (mPermissionsUtils == null)
                                mPermissionsUtils = new PermissionsUtils(reference.get());
                            mPermissionsUtils.requestPermissions(permissions, REQUEST_CODE, new PermissionsUtils.Callback() {
                                @Override
                                public void onPermission(int requestCode, String[] deniedPermissions) {
                                    if (deniedPermissions.length == 0) {
                                        takePhoto();
                                    } else {
                                        //无授权
                                        Toast.makeText(reference.get(), reference.get().getResources().getString(R.string.permission_camera_fail), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }).show();
    }
    /****************************************************************************************************/
    /****************************************************************************************************/
    /****************************************************************************************************/
    /**
     * select the picture
     */
    private void takeGallery() {
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        innerIntent.setType(acceptType[0]);
        Intent wrapperIntent = Intent.createChooser(innerIntent, title);
        reference.get().startActivityForResult(wrapperIntent, requestCode);
    }

    /**
     * take the file
     */
    private void takeFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(acceptType[0]);
        reference.get().startActivityForResult(Intent.createChooser(intent, title), requestCode);
    }

    /**
     * take photo
     */
    private void takePhoto() {
        // if the device support the camera or not
        if (!reference.get().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(reference.get(), reference.get().getResources().getString(R.string.camera_supports_fail), Toast.LENGTH_SHORT).show();
            return;
        }
        // create the path if not exist
        File fileDir = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        //
        mFileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        String mFilePath = fileDir.getAbsolutePath() + File.separator + mFileName;
        Uri uri = null;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, mFileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        } else {
            contentValues.put(MediaStore.Images.Media.DATA, mFilePath);
        }
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG");
        uri = reference.get().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        reference.get().startActivityForResult(intent, requestCode);
    }
}

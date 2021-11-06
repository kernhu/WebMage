package com.xcion.webmage.capture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.xcion.webmage.utils.DensityUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2021/1/29  00:51
 * Intro:
 */
public class Capturer {

    private Context mContext;
    private DisplayMetrics mDisplayMetrics;

    public Capturer(Context context) {
        this.mContext = context;
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
    }

    /**
     * Capture view screenshot
     *
     * @param view
     * @return return a bitmap from View's parent
     */
    public Bitmap capture(View view) {
//        //

        ViewGroup mViewParent = (ViewGroup) view.getParent();
        int reqWidth = mViewParent.getWidth() == 0 ? mDisplayMetrics.widthPixels : mViewParent.getWidth();
        int reqHeight = mViewParent.getHeight() == 0 ? mDisplayMetrics.heightPixels : mViewParent.getHeight();
//        //
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        options.inPurgeable = true;
//        options.inInputShareable = true;
//        options.inPreferredConfig = Bitmap.Config.RGB_565;
//        options.inJustDecodeBounds = false;
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//        options.inJustDecodeBounds = false;
//
//        Bitmap mCapture = compressImage(Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.RGB_565), 2048, 50);
//        Canvas canvas = new Canvas(mCapture);
//        mViewParent.draw(canvas);


        Bitmap mCapture = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(mCapture);
        mViewParent.draw(canvas);
        return mCapture;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    /**
     * @param image
     * @param size    期望图片的大小，单位为kb
     * @param options 图片压缩的质量，取值1-100，越小表示压缩的越厉害,如输入30，表示压缩70%
     * @return
     */
    private static Bitmap compressImage(Bitmap image, int size, int options) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        while (baos.toByteArray().length / 1024 > size) {
            options -= 10;
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }
}

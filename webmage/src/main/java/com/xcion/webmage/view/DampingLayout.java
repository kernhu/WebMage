package com.xcion.webmage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.xcion.webmage.AbstractFactory;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/17 20:50
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/17 20:50
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */

public class DampingLayout extends AbstractFactory {

    private final int INTERCEPT = 15;
    private final int DEFAULT_POINTER_COUNT = 1;
    private TextView mWebSource;
    private TextView mCoreSupplier;
    private WebView mWebView = null;
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private boolean dampingEnable;
    private int lastY = 0;
    private int mDampingPosition;

    //
    protected float mPointX;
    protected float mPointY;

    public DampingLayout(@NonNull Context context) {
        this(context, null);
    }

    public DampingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DampingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //
        setBackgroundColor(Color.parseColor("#404040"));
        //show website info source
        LinearLayout.LayoutParams mWebSourceLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mWebSourceLp.gravity = Gravity.CENTER_HORIZONTAL;
        mWebSource = new TextView(getContext());
        mWebSource.setTextColor(Color.parseColor("#DDDDDD"));
        mWebSource.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        mWebSource.setCompoundDrawablePadding(15);
        mWebSource.setGravity(Gravity.CENTER_HORIZONTAL);
        //
        LinearLayout.LayoutParams mCoreSupplierLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mCoreSupplierLp.gravity = Gravity.CENTER_HORIZONTAL;
        mCoreSupplier = new TextView(getContext());
        mCoreSupplier.setTextColor(Color.parseColor("#DDDDDD"));
        mCoreSupplier.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        mCoreSupplier.setGravity(Gravity.CENTER);
        mCoreSupplier.setCompoundDrawablePadding(10);
        mCoreSupplier.setGravity(Gravity.CENTER_HORIZONTAL);
        //
        LayoutParams parentLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayout parent = new LinearLayout(getContext());
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.addView(mWebSource, mWebSourceLp);
        parent.addView(mCoreSupplier, mCoreSupplierLp);
        parentLp.topMargin = 60;
        parentLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        this.addView(parent, parentLp);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (dampingEnable) {
            eventControl();
            dampingEnable = false;
        }
    }

    private void eventControl() {
        for (int i = 0; i < this.getChildCount(); i++) {
            if (this.getChildAt(i) instanceof WebView) {
                mWebView = (WebView) this.getChildAt(i);
            }
        }
        if (mWebView != null) {
            mWebView.setOnTouchListener(onTouchListener);
        }
    }


    OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // long press event for popup window's point
            mPointX = (int) event.getRawX();
            mPointY = (int) event.getRawY();
            //if point count doesn't more that 2, scroll enable
            if (event.getPointerCount() == DEFAULT_POINTER_COUNT) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    mVelocityTracker.addMovement(event);
                    lastY = (int) event.getRawY();

                    break;
                case MotionEvent.ACTION_MOVE:

                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000);

                    int currentY = (int) event.getY();
                    boolean isDown = currentY - lastY > 0;
                    if (isTop() && isDown && (currentY - lastY) > INTERCEPT) {
                        mDampingPosition = mDampingPosition + 1;
                        smoothDampingTo(isDown, mDampingPosition);
                    } else if (!isDown && (lastY - currentY) > INTERCEPT) {
                        mDampingPosition = mDampingPosition <= 0 ? 0 : mDampingPosition - 1;
                        smoothDampingTo(isDown, mDampingPosition);
                    }
                    lastY = (int) event.getY();

                    break;
                default:

                    lastY = 0;
                    mDampingPosition = 0;
                    mVelocityTracker.clear();
                    smoothDampingTo(false, mDampingPosition);

                    break;
            }
            return false;
        }
    };

    /**
     * @return the web of h5 is in top
     */
    private boolean isTop() {
        return mWebView == null ? true : (mWebView.getScrollY() == 0);
    }

    private void smoothDampingTo(boolean isDown, int position) {
        if (isDown) {
            mWebView.setTranslationY(position * 8);
        } else {
            mWebView.setTranslationY(position / 6);
        }
    }

    public void setDampingEnable(boolean dampingEnable) {
        this.dampingEnable = dampingEnable;
    }


    /*********************************************************************************************/
    protected void setWebSource(String source) {
        mWebSource.setText(source);
    }

    /*********************************************************************************************/

    protected void setSupplier(String supplier) {
        mCoreSupplier.setText(supplier);
    }

    public void setSupplierIcon(Drawable drawable) {
        if (drawable != null) {
            setSupplierIcon(((BitmapDrawable) drawable).getBitmap());
        }
    }

    protected void setSupplierIcon(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(bitmap);
        mCoreSupplier.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }


    private Drawable createRoundImageWithBorder(Bitmap bitmap) {
        //原图宽度
        int bitmapWidth = bitmap.getWidth();
        //原图高度
        int bitmapHeight = bitmap.getHeight();
        //边框宽度 pixel
        int borderWidthHalf = 0;

        //转换为正方形后的宽高
        int bitmapSquareWidth = Math.min(bitmapWidth, bitmapHeight);

        //最终图像的宽高
        int newBitmapSquareWidth = bitmapSquareWidth + borderWidthHalf;

        Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth, newBitmapSquareWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);
        int x = borderWidthHalf + bitmapSquareWidth - bitmapWidth;
        int y = borderWidthHalf + bitmapSquareWidth - bitmapHeight;

        //裁剪后图像,注意X,Y要除以2 来进行一个中心裁剪
        canvas.drawBitmap(bitmap, x / 2, y / 2, null);
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidthHalf);
        borderPaint.setColor(Color.WHITE);

        //添加边框
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getWidth() / 2, newBitmapSquareWidth / 2, borderPaint);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), roundedBitmap);
        roundedBitmapDrawable.setGravity(Gravity.CENTER);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }
}

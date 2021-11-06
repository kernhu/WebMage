package com.xcion.webmage.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.Transition;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import androidx.core.widget.PopupWindowCompat;

public abstract class AbstractPopupWindow<T extends AbstractPopupWindow> implements PopupWindow.OnDismissListener {

    private static final String TAG = "PopupWindow";
    private static final float DEFAULT_DIM = 0.7f;
    private int width = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int height = ViewGroup.LayoutParams.WRAP_CONTENT;
    private PopupWindow.OnDismissListener dismissListener;
    private PopupWindow popupWindow;
    private Context context;
    private View contentView;
    private int layoutId;
    private boolean focusable = true;
    private boolean outsideTouchable = true;
    private int animationStyle;
    private boolean isBackgroundDim;
    private float dimValue = DEFAULT_DIM;
    @ColorInt
    private int mDimColor = Color.BLACK;
    @NonNull
    private ViewGroup dimView;
    private Transition enterTransition;
    private Transition exitTransition;
    private boolean focusAndOutsideEnable = true;
    private View anchorView;
    @YGravity
    private int yGravity = YGravity.BELOW;
    @XGravity
    private int xGravity = XGravity.LEFT;
    private int offsetX;
    private int offsetY;
    private int inputMethodMode = PopupWindow.INPUT_METHOD_FROM_FOCUSABLE;
    private int softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
    private boolean isNeedReMeasureWH = false;
    private boolean isRealWHAlready = false;
    private boolean isAtAnchorViewMethod = false;
    private OnRealWHAlreadyListener realWHAlreadyListener;

    protected T self() {
        return (T) this;
    }

    public T apply() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow();
        }

        onPopupWindowCreated();

        initContentViewAndWH();

        onPopupWindowViewCreated(contentView);

        if (animationStyle != 0) {
            popupWindow.setAnimationStyle(animationStyle);
        }

        initFocusAndBack();
        popupWindow.setOnDismissListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (enterTransition != null) {
                popupWindow.setEnterTransition(enterTransition);
            }

            if (exitTransition != null) {
                popupWindow.setExitTransition(exitTransition);
            }
        }

        return self();
    }

    private void initContentViewAndWH() {
        if (contentView == null) {
            if (layoutId != 0 && context != null) {
                contentView = LayoutInflater.from(context).inflate(layoutId, null);
            } else {
                throw new IllegalArgumentException("The content view is null,the layoutId=" + layoutId + ",context=" + context);
            }
        }
        popupWindow.setContentView(contentView);

        if (width > 0 || width == ViewGroup.LayoutParams.WRAP_CONTENT || width == ViewGroup.LayoutParams.MATCH_PARENT) {
            popupWindow.setWidth(width);
        } else {
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        if (height > 0 || height == ViewGroup.LayoutParams.WRAP_CONTENT || height == ViewGroup.LayoutParams.MATCH_PARENT) {
            popupWindow.setHeight(height);
        } else {
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        measureContentView();
        registerOnGlobalLayoutListener();

        popupWindow.setInputMethodMode(inputMethodMode);
        popupWindow.setSoftInputMode(softInputMode);
    }

    private void initFocusAndBack() {
        if (!focusAndOutsideEnable) {
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setBackgroundDrawable(null);
            popupWindow.getContentView().setFocusable(true);
            popupWindow.getContentView().setFocusableInTouchMode(true);
            popupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        popupWindow.dismiss();

                        return true;
                    }
                    return false;
                }
            });
            popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int x = (int) event.getX();
                    final int y = (int) event.getY();

                    if ((event.getAction() == MotionEvent.ACTION_DOWN)
                            && ((x < 0) || (x >= width) || (y < 0) || (y >= height))) {
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        return true;
                    }
                    return false;
                }
            });
        } else {
            popupWindow.setFocusable(focusable);
            popupWindow.setOutsideTouchable(outsideTouchable);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    /****自定义生命周期方法****/

    /**
     * PopupWindow对象创建完成
     */
    protected void onPopupWindowCreated() {
        initAttributes();
    }

    protected void onPopupWindowViewCreated(View contentView) {
        initViews(contentView, self());
    }

    protected void onPopupWindowDismiss() {
    }

    /**
     * 可以在此方法中设置PopupWindow需要的属性
     */
    protected abstract void initAttributes();

    /**
     * 初始化view {@see getView()}
     *
     * @param view
     */
    protected abstract void initViews(View view, T popup);

    /**
     * 是否需要测量 contentView的大小
     * 如果需要重新测量并为宽高赋值
     * 注：此方法获取的宽高可能不准确 MATCH_PARENT时无法获取准确的宽高
     */
    private void measureContentView() {
        final View contentView = getContentView();
        if (width <= 0 || height <= 0) {
            contentView.measure(0, View.MeasureSpec.UNSPECIFIED);
            if (width <= 0) {
                width = contentView.getMeasuredWidth();
            }
            if (height <= 0) {
                height = contentView.getMeasuredHeight();
            }
        }
    }

    /**
     * 注册GlobalLayoutListener 获取精准的宽高
     */
    private void registerOnGlobalLayoutListener() {
        getContentView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                width = getContentView().getWidth();
                height = getContentView().getHeight();

                isRealWHAlready = true;
                isNeedReMeasureWH = false;

                if (realWHAlreadyListener != null) {
                    realWHAlreadyListener.onRealWHAlready(AbstractPopupWindow.this, width, height,
                            anchorView == null ? 0 : anchorView.getWidth(), anchorView == null ? 0 : anchorView.getHeight());
                }
                if (isShowing() && isAtAnchorViewMethod) {
                    updateLocation(width, height, anchorView, yGravity, xGravity, offsetX, offsetY);
                }
            }
        });
    }

    /**
     * 更新 PopupWindow 到精准的位置
     *
     * @param width
     * @param height
     * @param anchor
     * @param yGravity
     * @param xGravity
     * @param x
     * @param y
     */
    private void updateLocation(int width, int height, @NonNull View anchor, @YGravity final int yGravity, @XGravity int xGravity, int x, int y) {
        if (popupWindow == null) {
            return;
        }
        x = calculateX(anchor, xGravity, width, x);
        y = calculateY(anchor, yGravity, height, y);
        popupWindow.update(anchor, x, y, width, height);
    }

    /****设置属性方法****/

    public T setContext(Context context) {
        this.context = context;
        return self();
    }

    public T setContentView(View contentView) {
        this.contentView = contentView;
        this.layoutId = 0;
        return self();
    }

    public T setContentView(@LayoutRes int layoutId) {
        this.contentView = null;
        this.layoutId = layoutId;
        return self();
    }

    public T setContentView(Context context, @LayoutRes int layoutId) {
        this.context = context;
        this.contentView = null;
        this.layoutId = layoutId;
        return self();
    }

    public T setContentView(View contentView, int width, int height) {
        this.contentView = contentView;
        this.layoutId = 0;
        this.width = width;
        this.height = height;
        return self();
    }

    public T setContentView(@LayoutRes int layoutId, int width, int height) {
        this.contentView = null;
        this.layoutId = layoutId;
        this.width = width;
        this.height = height;
        return self();
    }

    public T setContentView(Context context, @LayoutRes int layoutId, int width, int height) {
        this.context = context;
        this.contentView = null;
        this.layoutId = layoutId;
        this.width = width;
        this.height = height;
        return self();
    }

    public T setWidth(int width) {
        this.width = width;
        return self();
    }

    public T setHeight(int height) {
        this.height = height;
        return self();
    }

    public T setAnchorView(View view) {
        this.anchorView = view;
        return self();
    }

    public T setYGravity(@YGravity int yGravity) {
        this.yGravity = yGravity;
        return self();
    }

    public T setXGravity(@XGravity int xGravity) {
        this.xGravity = xGravity;
        return self();
    }

    public T setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        return self();
    }

    public T setOffsetY(int offsetY) {
        this.offsetY = offsetY;
        return self();
    }

    public T setAnimationStyle(@StyleRes int animationStyle) {
        this.animationStyle = animationStyle;
        return self();
    }

    public T setFocusable(boolean focusable) {
        this.focusable = focusable;
        return self();
    }

    public T setOutsideTouchable(boolean outsideTouchable) {
        this.outsideTouchable = outsideTouchable;
        return self();
    }

    /**
     * 是否可以点击PopupWindow之外的地方dismiss
     *
     * @param focusAndOutsideEnable
     * @return
     */
    public T setFocusAndOutsideEnable(boolean focusAndOutsideEnable) {
        this.focusAndOutsideEnable = focusAndOutsideEnable;
        return self();
    }

    /**
     * 背景变暗支持api>=18
     *
     * @param isDim
     * @return
     */
    public T setBackgroundDimEnable(boolean isDim) {
        this.isBackgroundDim = isDim;
        return self();
    }

    public T setDimValue(@FloatRange(from = 0.0f, to = 1.0f) float dimValue) {
        this.dimValue = dimValue;
        return self();
    }

    public T setDimColor(@ColorInt int color) {
        this.mDimColor = color;
        return self();
    }

    public T setDimView(@NonNull ViewGroup dimView) {
        this.dimView = dimView;
        return self();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public T setEnterTransition(Transition enterTransition) {
        this.enterTransition = enterTransition;
        return self();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public T setExitTransition(Transition exitTransition) {
        this.exitTransition = exitTransition;
        return self();
    }

    public T setInputMethodMode(int mode) {
        this.inputMethodMode = mode;
        return self();
    }

    public T setSoftInputMode(int mode) {
        this.softInputMode = mode;
        return self();
    }

    /**
     * 是否需要重新获取宽高
     *
     * @param needReMeasureWH
     * @return
     */
    public T setNeedReMeasureWH(boolean needReMeasureWH) {
        this.isNeedReMeasureWH = needReMeasureWH;
        return self();
    }

    /**
     * 检查是否调用了 apply() 方法
     *
     * @param isAtAnchorView 是否是 showAt
     */
    private void checkIsApply(boolean isAtAnchorView) {
        if (this.isAtAnchorViewMethod != isAtAnchorView) {
            this.isAtAnchorViewMethod = isAtAnchorView;
        }
        if (popupWindow == null) {
            apply();
        }
    }

    /**
     * 使用此方法需要在创建的时候调用setAnchorView()等属性设置{@see setAnchorView()}
     */
    public void showAsDropDown() {
        if (anchorView == null) {
            return;
        }
        showAsDropDown(anchorView, offsetX, offsetY);
    }

    /**
     * PopupWindow自带的显示方法
     *
     * @param anchor
     * @param offsetX
     * @param offsetY
     */
    public void showAsDropDown(View anchor, int offsetX, int offsetY) {

        checkIsApply(false);

        handleBackgroundDim();
        anchorView = anchor;
        offsetX = offsetX;
        offsetY = offsetY;
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener();
        }
        popupWindow.showAsDropDown(anchor, offsetX, offsetY);
    }

    public void showAsDropDown(View anchor) {
        checkIsApply(false);
        handleBackgroundDim();
        anchorView = anchor;
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener();
        }
        popupWindow.showAsDropDown(anchor);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void showAsDropDown(View anchor, int offsetX, int offsetY, int gravity) {
        checkIsApply(false);
        handleBackgroundDim();
        anchorView = anchor;
        offsetX = offsetX;
        offsetY = offsetY;
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener();
        }
        PopupWindowCompat.showAsDropDown(popupWindow, anchor, offsetX, offsetY, gravity);
    }

    public void showAtLocation(View parent, int gravity, int offsetX, int offsetY) {
        checkIsApply(false);
        handleBackgroundDim();
        anchorView = parent;
        offsetX = offsetX;
        offsetY = offsetY;
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener();
        }
        popupWindow.showAtLocation(parent, gravity, offsetX, offsetY);
    }

    /**
     * 相对anchor view显示
     * <p>
     * 使用此方法需要在创建的时候调用setAnchorView()等属性设置{@see setAnchorView()}
     * <p>
     * 注意：如果使用 VerticalGravity 和 HorizontalGravity 时，请确保使用之后 PopupWindow 没有超出屏幕边界，
     * 如果超出屏幕边界，VerticalGravity 和 HorizontalGravity 可能无效，从而达不到你想要的效果。
     */
    public void showAtAnchorView() {
        if (anchorView == null) {
            return;
        }
        showAtAnchorView(anchorView, yGravity, xGravity);
    }

    /**
     * 相对anchor view显示，适用 宽高不为match_parent
     * <p>
     * 注意：如果使用 VerticalGravity 和 HorizontalGravity 时，请确保使用之后 PopupWindow 没有超出屏幕边界，
     * 如果超出屏幕边界，VerticalGravity 和 HorizontalGravity 可能无效，从而达不到你想要的效果。     *
     *
     * @param anchor
     * @param vertGravity
     * @param horizGravity
     */
    public void showAtAnchorView(@NonNull View anchor, @YGravity int vertGravity, @XGravity int horizGravity) {
        showAtAnchorView(anchor, vertGravity, horizGravity, 0, 0);
    }

    /**
     * 相对anchor view显示，适用 宽高不为match_parent
     * <p>
     * 注意：如果使用 VerticalGravity 和 HorizontalGravity 时，请确保使用之后 PopupWindow 没有超出屏幕边界，
     * 如果超出屏幕边界，VerticalGravity 和 HorizontalGravity 可能无效，从而达不到你想要的效果。
     *
     * @param anchor
     * @param vertGravity  垂直方向的对齐方式
     * @param horizGravity 水平方向的对齐方式
     * @param x            水平方向的偏移
     * @param y            垂直方向的偏移
     */
    public void showAtAnchorView(@NonNull View anchor, @YGravity final int vertGravity, @XGravity int horizGravity, int x, int y) {
        checkIsApply(true);
        anchorView = anchor;
        offsetX = x;
        offsetY = y;
        yGravity = vertGravity;
        xGravity = horizGravity;
        handleBackgroundDim();
        x = calculateX(anchor, horizGravity, width, offsetX);
        y = calculateY(anchor, vertGravity, height, offsetY);
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener();
        }
        PopupWindowCompat.showAsDropDown(popupWindow, anchor, x, y, Gravity.NO_GRAVITY);

    }

    /**
     * 根据垂直gravity计算y偏移
     *
     * @param anchor
     * @param vertGravity
     * @param measuredH
     * @param y
     * @return
     */
    private int calculateY(View anchor, int vertGravity, int measuredH, int y) {
        switch (vertGravity) {
            case YGravity.ABOVE:
                y -= measuredH + anchor.getHeight();
                break;
            case YGravity.ALIGN_BOTTOM:
                y -= measuredH;
                break;
            case YGravity.CENTER:
                y -= anchor.getHeight() / 2 + measuredH / 2;
                break;
            case YGravity.ALIGN_TOP:
                y -= anchor.getHeight();
                break;
            case YGravity.BELOW:
                break;
        }

        return y;
    }

    /**
     * 根据水平gravity计算x偏移
     *
     * @param anchor
     * @param horizGravity
     * @param measuredW
     * @param x
     * @return
     */
    private int calculateX(View anchor, int horizGravity, int measuredW, int x) {
        switch (horizGravity) {
            case XGravity.LEFT:
                x -= measuredW;
                break;
            case XGravity.ALIGN_RIGHT:
                x -= measuredW - anchor.getWidth();
                break;
            case XGravity.CENTER:
                x += anchor.getWidth() / 2 - measuredW / 2;
                break;
            case XGravity.ALIGN_LEFT:
                break;
            case XGravity.RIGHT:
                x += anchor.getWidth();
                break;
        }

        return x;
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public T setOnDismissListener(PopupWindow.OnDismissListener listener) {
        this.dismissListener = listener;
        return self();
    }

    public T setOnRealWHAlreadyListener(OnRealWHAlreadyListener listener) {
        this.realWHAlreadyListener = listener;
        return self();
    }


    private void handleBackgroundDim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!isBackgroundDim) {
                return;
            }
            if (dimView != null) {
                applyDim(dimView);
            } else {
                if (getContentView() != null && getContentView().getContext() != null &&
                        getContentView().getContext() instanceof Activity) {
                    Activity activity = (Activity) getContentView().getContext();
                    applyDim(activity);
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void applyDim(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        Drawable dimDrawable = new ColorDrawable(mDimColor);
        dimDrawable.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dimDrawable.setAlpha((int) (255 * dimValue));
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dimDrawable);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void applyDim(ViewGroup dimView) {
        Drawable dimDrawable = new ColorDrawable(mDimColor);
        dimDrawable.setBounds(0, 0, dimView.getWidth(), dimView.getHeight());
        dimDrawable.setAlpha((int) (255 * dimValue));
        ViewGroupOverlay overlay = dimView.getOverlay();
        overlay.add(dimDrawable);
    }

    /**
     * 清除背景变暗
     */
    private void clearBackgroundDim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (isBackgroundDim) {
                if (dimView != null) {
                    clearDim(dimView);
                } else {
                    if (getContentView() != null) {
                        Activity activity = (Activity) getContentView().getContext();
                        if (activity != null) {
                            clearDim(activity);
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void clearDim(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void clearDim(ViewGroup dimView) {
        ViewGroupOverlay overlay = dimView.getOverlay();
        overlay.clear();
    }

    /**
     * 获取PopupWindow中加载的view
     *
     * @return
     */
    public View getContentView() {
        if (popupWindow != null) {
            return popupWindow.getContentView();
        } else {
            return null;
        }
    }

    /**
     * 获取PopupWindow对象
     *
     * @return
     */
    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    /**
     * 获取PopupWindow 宽
     *
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * 获取PopupWindow 高
     *
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * 获取纵向Gravity
     *
     * @return
     */
    public int getXGravity() {
        return xGravity;
    }

    /**
     * 获取横向Gravity
     *
     * @return
     */
    public int getYGravity() {
        return yGravity;
    }

    /**
     * 获取x轴方向的偏移
     *
     * @return
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * 获取y轴方向的偏移
     *
     * @return
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * 是否正在显示
     *
     * @return
     */
    public boolean isShowing() {
        return popupWindow != null && popupWindow.isShowing();
    }

    /**
     * 是否精准的宽高获取完成
     *
     * @return
     */
    public boolean isRealWHAlready() {
        return isRealWHAlready;
    }

    /**
     * 获取view
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T findViewById(@IdRes int viewId) {
        View view = null;
        if (getContentView() != null) {
            view = getContentView().findViewById(viewId);
        }
        return (T) view;
    }

    /**
     * 消失
     */
    public void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onDismiss() {
        handleDismiss();
    }

    /**
     * PopupWindow消失后处理一些逻辑
     */
    private void handleDismiss() {
        if (dismissListener != null) {
            dismissListener.onDismiss();
        }
        clearBackgroundDim();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        onPopupWindowDismiss();
    }

    /**
     * PopupWindow是否显示在window中
     * 用于获取准确的PopupWindow宽高，可以重新设置偏移量
     */
    public interface OnRealWHAlreadyListener {

        /**
         * 在 show方法之后 updateLocation之前执行
         *
         * @param popWidth  PopupWindow准确的宽
         * @param popHeight PopupWindow准确的高
         * @param anchorW   锚点View宽
         * @param anchorH   锚点View高
         */
        void onRealWHAlready(AbstractPopupWindow basePopup, int popWidth, int popHeight, int anchorW, int anchorH);
    }

}

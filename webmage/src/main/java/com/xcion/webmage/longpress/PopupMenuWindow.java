package com.xcion.webmage.longpress;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.xcion.webmage.R;
import com.xcion.webmage.picture.ImagePreviewer;
import com.xcion.webmage.popupwindow.MagePopupWindow;
import com.xcion.webmage.share.SystemSharer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/30 14:57
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/30 14:57
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class PopupMenuWindow {

    private WeakReference<Context> contextReference;
    private float pointX;
    private float pointY;
    private WebView webView;
    private LongPressMenuControl menuControl;
    private MagePopupWindow mPopupWindow;
    private DisplayMetrics mDisplayMetrics;
    private ListView mListView;
    private Object mTargetItem;

    public static PopupMenuWindow getInstance(Context context) {
        return new PopupMenuWindow(context);
    }

    public PopupMenuWindow(Context context) {
        this.contextReference = new WeakReference<>(context);
        mDisplayMetrics = context.getResources().getDisplayMetrics();

        mListView = new ListView(context);
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.setBackgroundColor(Color.parseColor("#F5F5F5"));

        mPopupWindow = MagePopupWindow.create(context)
                .setContentView(mListView, mDisplayMetrics.widthPixels / 3, LinearLayout.LayoutParams.WRAP_CONTENT)
                .setFocusAndOutsideEnable(true)
                .setBackgroundDimEnable(true)
                .setDimValue(0.4f)
                .setOnDismissListener(mDismissListener)
                .setDimColor(context.getResources().getColor(R.color.theme_hint_shadow, null));
    }

    public PopupMenuWindow setWebView(WebView webView) {
        this.webView = webView;
        return this;
    }

    public PopupMenuWindow setMenuControl(LongPressMenuControl menuControl) {
        this.menuControl = menuControl;
        return this;
    }

    public PopupMenuWindow setPointX(float pointX) {
        this.pointX = pointX;
        return this;
    }

    public PopupMenuWindow setPointY(float pointY) {
        this.pointY = pointY;
        return this;
    }

    private List<Object> createItems() {
        List<Object> items = new ArrayList<>();
        WebView.HitTestResult result = webView.getHitTestResult();
        int type = result.getType();
        Log.e("sos", "createItems>>>" + type);
        switch (type) {
            case WebView.HitTestResult.PHONE_TYPE:
                /**处理拨号**/
                for (MenuItem.PHONE phone : MenuItem.PHONE.values()) {
                    items.add(phone);
                }
                break;
            case WebView.HitTestResult.EMAIL_TYPE:
                /**处理Email**/
                for (MenuItem.EMAIL email : MenuItem.EMAIL.values()) {
                    items.add(email);
                }
                break;
            case WebView.HitTestResult.GEO_TYPE:
                /**地图类型**/
                for (MenuItem.GEO geo : MenuItem.GEO.values()) {
                    items.add(geo);
                }
                break;
            case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                /**超链接**/
                for (MenuItem.SRC_ANCHOR srcAnchor : MenuItem.SRC_ANCHOR.values()) {
                    items.add(srcAnchor);
                }
                break;
            case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                for (MenuItem.IMAGE_ANCHOR imageAnchor : MenuItem.IMAGE_ANCHOR.values()) {
                    items.add(imageAnchor);
                }
                break;
            case WebView.HitTestResult.IMAGE_TYPE:
                /**处理长按图片的菜单项**/
                for (MenuItem.IMAGE image : MenuItem.IMAGE.values()) {
                    items.add(image);
                }
                // 获取图片的路径
                String saveImgUrl = result.getExtra();
                break;
            default:
                for (MenuItem.TEXT text : MenuItem.TEXT.values()) {
                    items.add(text);
                }
                break;
        }
        return items;
    }

    public void build() {
        List<Object> items = createItems();
        if (items == null || items.size() == 0) {
            return;
        }
        mListView.setAdapter(new MenuItemAdapter<Object>(contextReference.get(), android.R.layout.simple_list_item_1, items));
        int[] location = PopupWindowLocation.calculatePopWindowPos(webView, mListView, (int) pointX, (int) pointY);
        mPopupWindow.showAtLocation(webView, Gravity.TOP | Gravity.START, location[0], location[1]);
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mTargetItem = mListView.getAdapter().getItem(position);
            mPopupWindow.dismiss();
        }
    };

    PopupWindow.OnDismissListener mDismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            doControl();
        }
    };

    private void doControl() {

        String extra = webView.getHitTestResult().getExtra();
        if (mTargetItem instanceof MenuItem.TEXT) {
            MenuItem.TEXT text = (MenuItem.TEXT) mTargetItem;

            if (MenuItem.TEXT.SELECT_TO_COPY == text) {
                //
                //webView

            } else if (MenuItem.TEXT.SELECT_TO_SEARCH == text) {

            } else if (MenuItem.TEXT.CHANGE_FONT_SIZE == text) {

            } else if (MenuItem.TEXT.SAVE_OFFLINE_PAGE == text) {

            } else if (MenuItem.TEXT.COLLECT_WEBSITE == text) {

            } else if (MenuItem.TEXT.SHARE_WEBSITE == text) {
                if (contextReference != null && contextReference.get() != null && webView != null) {
                    SystemSharer.shareText(contextReference.get(), webView.getUrl(), "");
                }
            }
        } else if (mTargetItem instanceof MenuItem.IMAGE) {
            MenuItem.IMAGE image = (MenuItem.IMAGE) mTargetItem;

            if (MenuItem.IMAGE.SAVE_PICTURE == image) {

            } else if (MenuItem.IMAGE.PREVIEW_PICTURE == image) {

                ImagePreviewer
                        .getInstance(contextReference.get())
                        .setParent(webView)
                        .setUrl(extra)
                        .setDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                ImagePreviewer.onDestroy();
                            }
                        })
                        .show();

            }

        } else if (mTargetItem instanceof MenuItem.PHONE) {
            MenuItem.PHONE phone = (MenuItem.PHONE) mTargetItem;

            if (MenuItem.PHONE.CALL_NUMBER == phone) {

            } else if (MenuItem.PHONE.COPY_NUMBER == phone) {

            } else if (MenuItem.PHONE.SEND_MSG == phone) {

            } else if (MenuItem.PHONE.ADD_CONTACT == phone) {

            }

        } else if (mTargetItem instanceof MenuItem.EMAIL) {
            MenuItem.EMAIL email = (MenuItem.EMAIL) mTargetItem;

            if (MenuItem.EMAIL.SEND_EMAIL == email) {

            } else if (MenuItem.EMAIL.COPY_EMAIL_ADDRESS == email) {

            }

        } else if (mTargetItem instanceof MenuItem.GEO) {
            MenuItem.GEO geo = (MenuItem.GEO) mTargetItem;


        } else if (mTargetItem instanceof MenuItem.SRC_ANCHOR) {
            MenuItem.SRC_ANCHOR src_anchor = (MenuItem.SRC_ANCHOR) mTargetItem;

        } else if (mTargetItem instanceof MenuItem.IMAGE_ANCHOR) {
            MenuItem.IMAGE_ANCHOR image_anchor = (MenuItem.IMAGE_ANCHOR) mTargetItem;


        }
    }
}

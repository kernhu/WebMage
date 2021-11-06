package com.xcion.webmage.longpress;

import com.xcion.webmage.R;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/30 15:23
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/30 15:23
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class MenuItem {

    public enum TEXT {

        SELECT_TO_COPY(R.string.menu_item_select_to_copy),
        SELECT_TO_SEARCH(R.string.menu_item_select_to_search),
        CHANGE_FONT_SIZE(R.string.menu_item_change_font_size),
        SAVE_OFFLINE_PAGE(R.string.menu_item_save_offline_page),
        COLLECT_WEBSITE(R.string.menu_item_collect_website),
        SHARE_WEBSITE(R.string.menu_item_share_website);

        public int resId;

        TEXT(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }
    }

    public enum IMAGE {

        SAVE_PICTURE(R.string.menu_item_save_picture),
        PREVIEW_PICTURE(R.string.menu_item_preview_picture),
        SHARE_PICTURE(R.string.menu_item_share_picture);

        public int resId;

        IMAGE(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }
    }

    public enum PHONE {

        CALL_NUMBER(R.string.menu_item_call_number),
        COPY_NUMBER(R.string.menu_item_copy_number),
        SEND_MSG(R.string.menu_item_send_msg),
        ADD_CONTACT(R.string.menu_item_add_contact);

        public int resId;

        PHONE(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }
    }

    public enum EMAIL {

        SEND_EMAIL(R.string.menu_item_send_email),
        COPY_EMAIL_ADDRESS(R.string.menu_item_copy_email_address);

        public int resId;

        EMAIL(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }
    }

    public static class GEO {


        public int resId;

        GEO(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }

    }

    public static class SRC_ANCHOR {

//        public static final String BACKSTAGE_TO_OPEN = "后台打开";
//        public static final String NEW_WINDOW_TO_OPEN = "新窗口打开";
//        public static final String COPY_URL_ADDRESS = "复制链接地址";
//        public static final String SELECT_TO_COPY = "选择复制";


        public int resId;

        SRC_ANCHOR(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }

    }

    public static class IMAGE_ANCHOR {

        public int resId;

        IMAGE_ANCHOR(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }
    }
}

package com.xcion.webmage.longpress;

import com.xcion.webmage.WebMageView;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/30 15:02
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/30 15:02
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public abstract class LongPressMenuControl {

    /******************* TEXT **********************/

    public void onTextControl(WebMageView view, MenuItem.TEXT text, String result) {
    }



    /****************** PHONE ***********************/

    public void onPhoneControl(WebMageView view, MenuItem.PHONE phone, String result) {
    }



    /****************** IMAGE ***********************/

    public void onImageControl(WebMageView view, MenuItem.IMAGE image, String result) {
    }



    /****************** EMAIL ***********************/

    public void onEmailControl(WebMageView view, MenuItem.EMAIL email, String result) {
    }



}

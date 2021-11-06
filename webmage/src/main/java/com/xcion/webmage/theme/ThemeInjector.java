package com.xcion.webmage.theme;

import android.content.Context;
import android.util.Base64;
import android.webkit.WebView;

import java.io.InputStream;

import com.xcion.webmage.R;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/12/3 16:34
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/12/3 16:34
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class ThemeInjector {

    private Context context;
    private String lightCssCode;
    private String darkCssCode;

    public ThemeInjector(Context context) {
        this.context = context;
    }

    public void changeMode(WebView webView, boolean light) {
        if (light) {
            try {
                if (lightCssCode == null) {
                    InputStream is = context.getAssets().open("theme/theme_light.css");//context.getResources().openRawResource(R.raw.day);
                    byte[] buffer = new byte[is.available()];
                    is.read(buffer);
                    is.close();
                    lightCssCode = Base64.encodeToString(buffer, Base64.NO_WRAP);
                }
                webView.loadUrl("javascript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);" + "var style = document.createElement('style');" + "style.type = 'text/css';" + "style.innerHTML = window.atob('" + lightCssCode + "');" + "parent.appendChild(style)" + "})();");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (darkCssCode == null) {
                    InputStream is = context.getAssets().open("theme/theme_dark.css");//context.getResources().openRawResource(R.raw.night);
                    byte[] buffer = new byte[is.available()];
                    is.read(buffer);
                    is.close();
                    darkCssCode = Base64.encodeToString(buffer, Base64.NO_WRAP);
                }
                webView.loadUrl("javascript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);" + "var style = document.createElement('style');" + "style.type = 'text/css';" + "style.innerHTML = window.atob('" + darkCssCode + "');" + "parent.appendChild(style)" + "})();");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

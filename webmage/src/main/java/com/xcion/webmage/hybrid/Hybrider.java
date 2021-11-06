package com.xcion.webmage.hybrid;

import android.webkit.ValueCallback;

public class Hybrider {

    private JSCallback callback;

    public interface HybridCallback {

        void onCallback(String data);

    }

    public Hybrider(JSCallback callback) {
        this.callback = callback;
    }

    /**
     * @param script
     */
    public void loadJavascript(String script) {
        if (callback != null) {
            callback.onCallback(script);
        }
    }

    /**
     * @param script
     * @param resultCallback
     */
    public void evaluateJavascript(String script, ValueCallback<String> resultCallback) {
        if (callback != null) {
            callback.onCallback(script, resultCallback);
        }
    }


    /**
     * @param hybridCallback
     * @param datas
     */
    public void registerInterceptor(HybridCallback hybridCallback, String... datas) {
        if (callback != null) {
            callback.onCallback(hybridCallback, datas);
        }
    }

    /**
     * @param obj
     * @param interfaceName
     */
    public void addJavascriptInterface(Object obj, String interfaceName) {
        if (callback != null) {
            callback.onCallback(obj, interfaceName);
        }
    }

}

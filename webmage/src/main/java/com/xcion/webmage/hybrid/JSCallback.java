package com.xcion.webmage.hybrid;

import android.webkit.ValueCallback;

public interface JSCallback {

    void onCallback(String script);

    void onCallback(String script, ValueCallback<String> resultCallback);

    void onCallback(Hybrider.HybridCallback callback, String... jsTargets);

    void onCallback(Object obj, String interfaceName);

}

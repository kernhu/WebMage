package com.xcion.web;

import android.os.Bundle;

import androidx.annotation.Nullable;

public class HybridActivity extends BaseActivity {

    public static final String KEY_URL = "key_url";
    private String mLoadUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hybrid);
        mLoadUrl = getIntent().getStringExtra(KEY_URL);
    }
}

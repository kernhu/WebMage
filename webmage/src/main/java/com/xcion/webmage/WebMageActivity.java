package com.xcion.webmage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WebMageActivity extends AppCompatActivity {

    private WebMageView mWebMageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebMageView = new WebMageView(this);

    }

    public WebMageView getWebMageView() {
        return mWebMageView;
    }

    @Override
    protected void onPause() {
        mWebMageView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mWebMageView.onResume();
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        mWebMageView.onDestroy();
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if (mWebMageView.onBackPressed()) {

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLowMemory() {
        mWebMageView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        mWebMageView.onTrimMemory(level);
        super.onTrimMemory(level);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mWebMageView.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mWebMageView.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

package com.xcion.webmage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WebMageFragment extends Fragment {

    private WebMageView mWebMageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mWebMageView = new WebMageView(getContext());

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onPause() {
        mWebMageView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mWebMageView.onResume();
        super.onResume();

    }

    @Override
    public void onDestroy() {
        mWebMageView.onDestroy();
        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        mWebMageView.onLowMemory();
        super.onLowMemory();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mWebMageView.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mWebMageView.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

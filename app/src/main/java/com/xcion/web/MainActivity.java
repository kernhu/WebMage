package com.xcion.web;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xcion.lib.SwipeRecyclerView;
import com.xcion.lib.divider.HorizontalDividerItemDecoration;
import com.xcion.web.adapter.RecyclerAdapter;
import com.xcion.web.bean.ItemInfo;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements SwipeRecyclerView.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener
        , RecyclerAdapter.OnItemClickListener {

    private List<ItemInfo> mItemInfos = new ArrayList<>();
    private SwipeRecyclerView mSwipeRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRecyclerView = findViewById(R.id.swipe_recycler_view);
        //
        mItemInfos.add(new ItemInfo("WebMage基础功能", "H5渲染、重定向、刷新、前进后退、错误页、加载进度条、无图模式、夜间模式、选择复制/搜索、字体大小切换、保存离线网页、图片预览保存、UA自定义", "#CDCDCD", ItemInfo.FUN_LOAD_H5,"https://www.baidu.com/"));
        mItemInfos.add(new ItemInfo("视频播放功能", "硬件加速、视频播放、全屏播放、重力感应切换横竖屏", "#CDCDCD", ItemInfo.FUN_VIDEO_PLAY, "https://movie.douban.com/trailer/259258/#content"));
        mItemInfos.add(new ItemInfo("Deeplink功能", "微信/支付宝支付、打电话、发短信、发邮件、调起第三方App", "#CDCDCD", ItemInfo.FUN_DEEP_LINK, "file:///android_asset/web/h5_deeplink.html"));
        mItemInfos.add(new ItemInfo("地图定位功能", "位置定位、地图、适配M/Q", "#CDCDCD", ItemInfo.FUN_LOCATION, "https://map.baidu.com/@12688120,2564403,13z"));
        mItemInfos.add(new ItemInfo("文件下载功能", "Apk下载、音频下载、视频下载、图片下载、压缩包下载等主流文件下载", "#CDCDCD", ItemInfo.FUN_DOWNLOAD, "http://news.sohu.com/"));
        mItemInfos.add(new ItemInfo("文件上传功能", "图片、视频音频、APK/ZIP等文件上传", "#CDCDCD", ItemInfo.FUN_UPLOAD, "file:///android_asset/web/upload/h5_upload.html"));
        mItemInfos.add(new ItemInfo("JS调Android功能", "addJavascriptInterface对象映射、shouldOverrideUrlLoading拦截、onJsAlert/onJsConfirm/onJsPrompt拦截", "#CDCDCD", ItemInfo.FUN_JS_NATIVE, "file:///android_asset/web/js_msg_dialog.html"));
        mItemInfos.add(new ItemInfo("Android调JS功能", "loadUrl、evaluateJavascript注入js", "#CDCDCD", ItemInfo.FUN_NATIVE_JS, "file:///android_asset/web/js_msg_dialog.html"));
        mItemInfos.add(new ItemInfo("资源拦截替换", "拦截网络资源、替换网络资源、JS/CSS/图片", "#CDCDCD", ItemInfo.FUN_RESOURCE_REPLACE, "https://cn.vuejs.org/"));
        mItemInfos.add(new ItemInfo("URL拦截及替换功能", "url拦截及替换", "#CDCDCD", ItemInfo.FUN_URL_INTERCEPT, "file:///android_asset/web/js_msg_dialog.html"));
        mItemInfos.add(new ItemInfo("获取网页快照功能", "获取快照，适用于浏览器多窗口", "#CDCDCD", ItemInfo.FUN_SNAPSHOOT, "https://www.bilibili.com/"));
        mItemInfos.add(new ItemInfo("H5本地化功能", "混合开发、直出秒加载、秒开、本地加载H5", "#CDCDCD", ItemInfo.FUN_HYBRID, "file:///android_asset/template/index.html"));
        mItemInfos.add(new ItemInfo("关于WebMage", "关于我们", "#CDCDCD", ItemInfo.FUN_ABOUT_US, "www.google.com"));

        initView();
    }

    private void initView() {

        mSwipeRecyclerView.getSwipeRefreshLayout()
                .setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSwipeRecyclerView.getRecyclerView().setLayoutManager(mLinearLayoutManager);
        mSwipeRecyclerView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        mSwipeRecyclerView.getRecyclerView().addItemDecoration(new HorizontalDividerItemDecoration
                .Builder(this)
                .colorResId(android.R.color.darker_gray)
                .sizeResId(R.dimen.divider_line_height)
                .margin(10, 10)
                .showLastDivider()
                .build());

        mRecyclerAdapter = new RecyclerAdapter(this, mItemInfos);
        mSwipeRecyclerView.setAdapter(mRecyclerAdapter);
        mSwipeRecyclerView.setOnRefreshListener(this);
        mSwipeRecyclerView.setOnLoadMoreListener(this, 20);
        mRecyclerAdapter.setOnItemClickListener(this);
        mSwipeRecyclerView.setAutoRefresh();
    }

    @Override
    public void onRefresh() {
        mRecyclerAdapter.setUpdate(mItemInfos, true);
    }

    @Override
    public void onLoadMore(int overallItemsCount, int lastVisibleItemPosition) {
        mRecyclerAdapter.setUpdate(mItemInfos, true);
    }


    @Override
    public void onItemClick(View view, int position) {

        ItemInfo itemInfo = mRecyclerAdapter.getCurrentItem(position);
        if (itemInfo != null) {
            if (ItemInfo.FUN_HYBRID.equals(itemInfo.getFunMold())) {
                Intent intent = new Intent(MainActivity.this, HybridActivity.class);
                intent.putExtra(HybridActivity.KEY_URL, itemInfo.getUrl());
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra(WebActivity.KEY_URL, itemInfo.getUrl());
                intent.putExtra(WebActivity.KEY_FUN, itemInfo.getFunMold());
                startActivity(intent);
            }

        }
    }
}
package com.xcion.web.floatview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xcion.web.R;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/7 12:45
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/7 12:45
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class FloatTitleView extends RelativeLayout {

    private ImageView mFavicon;
    private ImageView mReloadIcon;
    private TextView mReceiveTitle;

    private int default_favicon;
    private int reload_icon;
    private int title_color;
    private String default_title;

    public FloatTitleView(@NonNull Context context) {
        this(context, null);
    }

    public FloatTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatTitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FloatTitleView);
        try {
            default_favicon = a.getResourceId(R.styleable.FloatTitleView_ftv_default_favicon, 0);
            reload_icon = a.getResourceId(R.styleable.FloatTitleView_ftv_reload_icon, 0);
            title_color = a.getColor(R.styleable.FloatTitleView_ftv_title_color, 0);
            default_title = a.getString(R.styleable.FloatTitleView_ftv_default_title);
        } finally {
            a.recycle();
        }

        initView();
    }

    private void initView() {

        LayoutParams faviconLP = new LayoutParams(60, 60);
        mFavicon = new ImageView(getContext());
        mFavicon.setId(1 * 100);
        faviconLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        faviconLP.leftMargin = 30;
        faviconLP.rightMargin = 30;
        faviconLP.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        mFavicon.setLayoutParams(faviconLP);
        this.addView(mFavicon);

        LayoutParams reloadLP = new LayoutParams(60, 60);
        mReloadIcon = new ImageView(getContext());
        mReloadIcon.setId(2 * 100);
        reloadLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        reloadLP.rightMargin = 30;
        reloadLP.leftMargin = 30;
        reloadLP.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        mReloadIcon.setLayoutParams(reloadLP);
        this.addView(mReloadIcon);

        LayoutParams titleLP = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mReceiveTitle = new TextView(getContext());
        titleLP.addRule(RelativeLayout.RIGHT_OF, 1 * 100);
        titleLP.addRule(RelativeLayout.LEFT_OF, 2 * 100);
        titleLP.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        mReceiveTitle.setLayoutParams(titleLP);
        this.addView(mReceiveTitle);

        mReceiveTitle.setSingleLine(true);
        mReceiveTitle.setTextColor(title_color);
        mFavicon.setImageDrawable(getResources().getDrawable(default_favicon, null));
        mReloadIcon.setImageDrawable(getResources().getDrawable(reload_icon, null));
        mReceiveTitle.setText(default_title);

    }


    public void setFavicon(Bitmap favicon) {
        mFavicon.setImageBitmap(favicon);
    }

    public void setReceiveTitle(String title) {
        mReceiveTitle.setText(title);
    }

    public void setOnReloadListener(OnClickListener listener) {
        mReloadIcon.setOnClickListener(listener);
    }

}

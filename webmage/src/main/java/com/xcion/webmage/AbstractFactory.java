package com.xcion.webmage;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/11/23 18:03
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/11/23 18:03
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public abstract class AbstractFactory extends FrameLayout {

    public AbstractFactory(@NonNull Context context) {
        this(context, null);
    }

    public AbstractFactory(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractFactory(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AbstractFactory(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected  void initView(){}

    protected   void onBindData(){}

    protected   void onCreateDamping(){}

    protected   void onCreateBrand(){}

    protected   void onCreateIndicator(){}

    protected   void onCreateWebCore(){}

    protected   void onCreateWebSetting(){}

    protected   void onCreateDownloader(){}
}

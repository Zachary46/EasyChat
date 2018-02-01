package com.itheima.leon.qqdemo.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.itheima.leon.qqdemo.presenter.HuanXinPresenter;

import org.litepal.LitePal;

import cn.bmob.v3.Bmob;

/**
 *Author:Zachary
 *Time:2018/1/29 0029 下午 4:28
 *Dec:
 *Call:0592-3278796 
 *Email:developer@baogukeji.com
 *Web:www.baogukeji.com
 */
public class App extends Application {
    public static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化Bomb云数据库
        initBmob();

        //初始化LitePal数据库(本地)
        initLitpal();

        HuanXinPresenter.getInstance().init(this);
    }

    private void initLitpal() {
        LitePal.initialize(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    
    private void initBmob() {
        Bmob.initialize(this, "8c05e1118f8c78f1495fcdda6242683c");
    }
}

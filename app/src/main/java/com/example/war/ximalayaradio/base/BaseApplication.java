package com.example.war.ximalayaradio.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.example.war.ximalayaradio.utils.LogUtil;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;


import java.util.logging.LogRecord;

public class BaseApplication extends Application {
    private static Context sContext = null;
    private static Handler sHandler = null;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
        CommonRequest mXimalaya = CommonRequest.getInstanse();
        if (DTransferConstants.isRelease) {
            String mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af";
            mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8");
            mXimalaya.setPackid("com.app.test.android");
            mXimalaya.init(this, mAppSecret);
        } else {
            String mAppSecret = "0a09d7093bff3d4947a5c4da0125972e";
            mXimalaya.setAppkey("f4d8f65918d9878e1702d49a8cdf0183");
            mXimalaya.setPackid("com.ximalaya.qunfeng");
            mXimalaya.init(this, mAppSecret);
        }

        //初始化播放器
        XmPlayerManager.getInstance(this).init();
        //初始化log
        LogUtil.init(this.getPackageName(), false);

        sHandler = new Handler();

        sContext = getBaseContext();

    }
    public static Handler getsHandler(){
        return sHandler;
    }

    public static Context getAppContext(){
        return sContext;
    }

    public static RefWatcher getRefWatcher(Context context){
        BaseApplication application = (BaseApplication)context.getApplicationContext();
        return application.refWatcher;
    }
}

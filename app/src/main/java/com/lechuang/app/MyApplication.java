package com.lechuang.app;

import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.base.BaseApplication;
import com.squareup.leakcanary.LeakCanary;
import com.taobao.sophix.SophixManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import cn.jpush.android.api.JPushInterface;

/**
 * @author: zhengjr
 * @since: 2018/8/8
 * @describe:
 */

public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
        SophixManager.getInstance().queryAndLoadNewPatch();

        //ARouter配置
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            JPushInterface.setDebugMode(true);
//            LeakCanary.install(this);
        }else {
            UMConfigure.setEncryptEnabled(true);//默认是false，所以要设置
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化

        JPushInterface.init(this);
//        UMConfigure.setLogEnabled(true);
//        UMConfigure.setEncryptEnabled(false);

        UMConfigure.init(this, "5bef6be5b465f57d830003b6", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        PlatformConfig.setWeixin(com.common.BuildConfig.WXAPPID, com.common.BuildConfig.WXAPPSECRET);
        PlatformConfig.setSinaWeibo("106622684", "830ac087c44322903d6b10312bf11d12","http://sns.whalecloud.com");
        PlatformConfig.setQQZone("1107835169",  "mPsRkVyjWNP3DA3A");

    }
}


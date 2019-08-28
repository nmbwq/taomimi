package com.common.app.base;

import android.app.Activity;
import android.app.Application;

import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeInitCallback;
import com.common.app.database.manger.GreenDaoManger;
import com.common.app.utils.ActivityStackManager;
import com.common.app.utils.Logger;
import com.common.app.utils.Utils;
import com.taobao.sophix.SophixManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Author：CHENHAO
 * date：2018/5/3
 * desc：组件开发中我们的application是放在debug包下的，进行集成合并时是需要移除掉的，
 * 所以组件module中不能使用debug包下的application的context，
 * 因此组件中必须通过Utils.getContext()方法来获取全局 Context
 */

public class BaseApplication extends Application {

    private static BaseApplication application;
    //单例模式中获取唯一的MyApplication实例
    private List<Activity> activityList = new LinkedList<Activity>();
    public static boolean mQueryShowHide = true;//默认显示上线隐藏的内容

    @Override
    public void onCreate() {
        super.onCreate();
        ActivityStackManager.getInstance().register(this);
        application = this;
        Utils.init(this);
        GreenDaoManger.getInstance().getDaoSession().getUserInfoBeanDao();

        AlibcTradeSDK.asyncInit(this, new AlibcTradeInitCallback() {
            @Override
            public void onSuccess() {
                Logger.e("---","成功");
            }

            @Override
            public void onFailure(int code, String msg) {
                //初始化失败，可以根据code和msg判断失败原因，详情参见错误说明
                Logger.e("---",code + msg);
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActivityStackManager.getInstance().unRegister(this);
    }

    public static BaseApplication getApplication(){
        return application;
    }


    //添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    //遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }
}

package com.common.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.common.app.base.BaseApplication;
import com.common.app.utils.Logger;
import com.common.app.utils.NotificationUtils;

/**
 * @author: zhengjr
 * @since: 2018/8/27
 * @describe:
 */

public class AppUpdataService extends Service {

    private MyServiceBinder mMyServiceBinder;
    private NotificationUtils mNotificationUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        if (mNotificationUtils == null){
            mNotificationUtils = new NotificationUtils(getBaseContext());
        }
        mMyServiceBinder = new MyServiceBinder(BaseApplication.getApplication(),mNotificationUtils);
        Logger.e("---" ,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.e("---" ,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.e("---" ,"onBind");
        return mMyServiceBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
